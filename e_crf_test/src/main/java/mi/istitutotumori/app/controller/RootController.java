package mi.istitutotumori.app.controller;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;
import mi.istitutotumori.app.service.RunnerService;
import mi.istitutotumori.app.service.TabResult;

/**
 * La classe RootController gestisce gli eventi relativi agli oggetti FXML
 * dell'applicazione JavaFx, è suddivisa in diversi tab corrispondenti a
 * processi diversi e contiene una barra degli strumenti minimale per uscire
 * dall'applicazione o mostrare un help. FUNZIONI MINORI sono: - la stampa
 * dell'help - salvataggio in PDF del help - cambio del tema - funzioni di zoom
 * I "CONTROLLER nominali" dei tab gestiscono solo il passaggio dei parametri e
 * le impostazioni si inizializzazione.
 */
public class RootController {

	@FXML
	private TabPane tabPane;
	@FXML
	private Button runButton;
	@FXML
	private Button themeButton;

	private boolean darkMode = false;
	private Scene scene;

	private final RunnerDispatcher runnerDispatcher;
	private final DialogService dialogService;
	private final ProgressDialogFactory progressDialogFactory;

	public RootController() {
		RunnerService runnerService = new RunnerService();
		this.runnerDispatcher = new RunnerDispatcher(runnerService);
		this.dialogService = new DialogService();
		this.progressDialogFactory = new ProgressDialogFactory();
	}

	public void setScene(Scene scene) {
		this.scene = scene;
	}

	// ---------------------------------------------------------
	// TEMA
	// ---------------------------------------------------------
	@FXML
	private void toggleTheme() {
		if (scene == null)
			return;

		scene.getStylesheets().clear();
		String css = darkMode ? "/app/css/pastelAcquaMarin.css" : "/app/css/pastelAcquaMarinDark.css";

		scene.getStylesheets().add(getClass().getResource(css).toExternalForm());
		themeButton.setText(darkMode ? "Tema: Light" : "Tema: Dark");
		darkMode = !darkMode;
	}

	// ---------------------------------------------------------
	// RUN
	// ---------------------------------------------------------
	@FXML
	private void onRun() {

		Stage progressDialog = progressDialogFactory.createProgressDialog();
		ProgressBar progressBar = progressDialogFactory.getProgressBar(progressDialog);

		Task<TabResult> task = createRunTask();

		progressBar.progressProperty().bind(task.progressProperty());

		task.setOnSucceeded(e -> {
			progressDialog.close();

			/*
			 * RIMUOVE IL POPUP QUELLO VECCHIO CHE DOPO LA MODIFICA NON VIENE PIU SCRITTO
			 * TabResult result = task.getValue(); // <-- CORRETTO
			 * 
			 * String msg = (result.error != null) ? result.error : result.message;
			 * 
			 * /* RIMUOVE IL POPUP QUELLO VECCHIO CHE DOPO LA MODIFICA NON VIENE PIU SCRITTO
			 * dialogService.showScrollableInfo( "Risultato", "Operazione completata", msg
			 * );
			 */
		});

		task.setOnFailed(e -> {
			progressDialog.close();
			dialogService.showError("Errore durante l’esecuzione");
		});

		Thread t = new Thread(task);
		t.setDaemon(true);
		t.start();

		progressDialog.showAndWait();
	}

	private Task<TabResult> createRunTask() {

		return new Task<>() {

			@Override
			protected TabResult call() throws Exception {

				updateProgress(0, 100);

				Tab tab = tabPane.getSelectionModel().getSelectedItem();
				if (tab == null) {
					TabResult r = new TabResult();
					r.error = "Nessuna tab selezionata";
					return r;
				}

				Object controller = tab.getContent().getUserData();

				TabResult result = runnerDispatcher.dispatch(controller, this::updateProgress);

				updateProgress(100, 100);
				return result;
			}

			@Override
			protected void succeeded() {
				TabResult result = getValue();

				// 1) Mostra popup scrollabile
				Platform.runLater(() -> {
					dialogService.showScrollableInfo("Risultato", "Operazione completata",
							result.error != null ? result.error : result.message);
				});

				// 2) Aggiorna il tab se implementa ResultReceiver
				Object controller = tabPane.getSelectionModel().getSelectedItem().getContent().getUserData();

				if (controller instanceof ResultReceiver receiver) {
					Platform.runLater(() -> receiver.showResult(result));
				}
			}

			@Override
			protected void failed() {
				Platform.runLater(() -> {
					dialogService.showError("Errore durante l’esecuzione");
				});
			}
		};
	}

	@FXML
	private void onExit() {
		System.exit(0);
	}

	@FXML
	private void showDocs() {
		ControllerUtilsMethod.showDocs();
	}

	public void addTab(Tab tab) {
		tabPane.getTabs().add(tab);
	}
}
