package mi.istitutotumori.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import mi.istitutotumori.app.controller.RootController;

/**
 * Applicazione per la costruzione di trial clinici di ricerca che mira a
 * ottimizzare le procedure per la creazione dei progetti attraverso
 * l'applicativo open-source REDCap
 * {@see https://...}. L'APP è composta da diversi
 * "tab" che corrispondono a diverse funzioni Questa classe crea il layout e
 * carica i vari "tab".
 */
public class MainApp extends Application {

	@Override
	public void start(Stage primaryStage) {
		try {
			// 1) Carico il RootLayout
			FXMLLoader rootLoader = new FXMLLoader(getClass().getResource("/app/view/RootLayout.fxml"));
			BorderPane rootLayout = rootLoader.load();
			RootController rootController = rootLoader.getController();

			// 2) Creo la Scene
			Scene scene = new Scene(rootLayout);
			scene.getStylesheets().add(getClass().getResource("/app/css/pastelAcquaMarin.css").toExternalForm());

			primaryStage.setScene(scene);
			primaryStage.setTitle("REDCap vers " + VersionUtil.getVersion());
			primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/icons/RedCap.jpg")));

			// 3) Passo la scene al RootController (serve per cambiare tema)
			rootController.setScene(scene);

			// 4) Carico i tab dinamicamente
			loadTab("/app/view/Lab.fxml", "Lab", rootController);
			loadTab("/app/view/Standard.fxml", "Standard", rootController);
			loadTab("/app/view/EotEos.fxml", "EotEos", rootController);
			loadTab("/app/view/QoL.fxml", "QoL", rootController);

			// 5) Mostro la finestra
			primaryStage.show();

		} catch (Exception e) {
			e.printStackTrace();
		}
	} // end start method

	/**
	 * Metodo che carica un FXML (richiamato prima), crea un Tab, gli assegna il
	 * controller e lo aggiunge al TabPane gestito dal RootController
	 */
	private void loadTab(String fxmlPath, String tabName, RootController rootController) throws Exception {
		FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
		Node content = loader.load();

		Object controller = loader.getController();

		// *** FONDAMENTALE ***
		// assegno il controller al content, così RootController.onRun() può leggerlo
		content.setUserData(controller);

		Tab tab = new Tab(tabName);
		tab.setContent(content);

		// rootController.tabPane.getTabs().add(tab);
		rootController.addTab(tab);
	}

	public static void main(String[] args) {
		launch(args);
	}
} // end class
