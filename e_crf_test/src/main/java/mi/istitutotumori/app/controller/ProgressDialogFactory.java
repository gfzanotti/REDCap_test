package mi.istitutotumori.app.controller;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ProgressDialogFactory {

	public Stage createProgressDialog() {
		Stage dialog = new Stage();
		dialog.initModality(Modality.APPLICATION_MODAL);
		dialog.setTitle("Elaborazione in corso...");

		ProgressBar bar = new ProgressBar(0);
		bar.setPrefWidth(400);

		GridPane grid = new GridPane();
		grid.setPadding(new Insets(10));
		grid.setHgap(10);
		grid.setVgap(10);
		grid.add(new Label("Elaborazione in corso..."), 0, 0);
		grid.add(bar, 0, 1);

		dialog.setScene(new Scene(grid, 500, 100));
		return dialog;
	}

	public ProgressBar getProgressBar(Stage dialog) {
		GridPane grid = (GridPane) dialog.getScene().getRoot();
		return (ProgressBar) grid.getChildren().get(1);
	}
}
