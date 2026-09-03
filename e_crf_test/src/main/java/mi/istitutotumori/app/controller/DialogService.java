package mi.istitutotumori.app.controller;

import javafx.scene.control.Alert;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.ButtonType;

public class DialogService {

	public void showError(String msg) {
		Alert a = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
		a.showAndWait();
	}

	public void showInfo(String title, String header, String msg) {
		Alert a = new Alert(Alert.AlertType.INFORMATION);
		a.setTitle(title);
		a.setHeaderText(header);
		a.setContentText(msg);
		a.showAndWait();
	}

	public void showScrollableInfo(String title, String header, String msg) {

		TextArea area = new TextArea(msg);
		area.setEditable(false);
		area.setWrapText(true);

		ScrollPane scroll = new ScrollPane(area);
		scroll.setFitToWidth(true);
		scroll.setFitToHeight(true);

		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle(title);
		alert.setHeaderText(header);
		alert.getDialogPane().setContent(scroll);

		alert.getDialogPane().setPrefWidth(500);
		alert.getDialogPane().setPrefHeight(400);
		alert.setResizable(true);

		alert.showAndWait();
	}
}
