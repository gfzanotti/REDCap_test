package mi.istitutotumori.app.controller;

import java.io.File;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import mi.istitutotumori.app.service.TabResult;

public class LabController implements ResultReceiver {

	// ATTENZIONE questi attributi devono essere uguali ai valori "fx:id" nei
	// rispettivi file *.fxml
	@FXML
	private ComboBox<String> combo1;
	@FXML
	private ComboBox<String> comboSceltaProgetto;
	@FXML
	private TextField tokenLab;
	@FXML
	private TextField surveyLabName;
	@FXML
	private TextField RecordLabId;
	@FXML
	private TextArea outputArea;

	@FXML
	private Label selectedPath;

	public LabParameters getParameters() {
		return new LabParameters(tokenLab.getText(), surveyLabName.getText(), RecordLabId.getText(), combo1.getValue(),
				comboSceltaProgetto.getValue());
	}

	public static class LabParameters {
		public final String token;
		public final String surveyName;
		public final String recordId;
		public final String combo1;
		public final String comboSceltaProgetto;

		private LabParameters(String t, String s, String r, String c1, String comboSceltaProgetto) {
			token = t;
			surveyName = s;
			recordId = r;
			combo1 = c1;
			this.comboSceltaProgetto = comboSceltaProgetto;
		}

		@Override
		public String toString() {
			return "token=" + token + ", survey=" + surveyName + ", recordId=" + recordId + ", combo1=" + combo1
					+ ", comboSceltaProgetto=" + comboSceltaProgetto;
		}
	}

	// Metodo richiamato da FXML
	@FXML
	public void selectFile() {
		FileChooser fileChooser = new FileChooser();
		File file = fileChooser.showOpenDialog(null); // o passare stage corretto
		if (file != null) {
			selectedPath.setText(file.getAbsolutePath());
		}
	}

	@FXML
	private void initialize() {
		combo1.getItems().addAll("Salva File .csv", "Importa dati con API");
		comboSceltaProgetto.getItems().addAll("crea nuovo progetto", "accoda a progetto esistente",
				"UPDATE (draft project)");
	}

	@Override
	public void showResult(TabResult result) {
		outputArea.setText(result.error != null ? result.error : result.message);
	}
}
