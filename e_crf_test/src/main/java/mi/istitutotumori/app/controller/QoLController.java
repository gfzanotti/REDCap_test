package mi.istitutotumori.app.controller;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import mi.istitutotumori.app.service.TabResult;

public class QoLController implements ResultReceiver {

	// ATTENZIONE questi attributi devono essere uguali ai valori "fx:id" nei
	// rispettivi file *.fxml
	@FXML
	private TextField tokenQoL;
	@FXML
	private TextField surveyName;
	@FXML
	private TextField recordId;
	@FXML
	private ComboBox<String> combo1QoL;
	// @FXML private TextArea outputArea;

	public QoLParameters getParameters() {
		return new QoLParameters(tokenQoL.getText(), surveyName.getText(), recordId.getText(), combo1QoL.getValue());
	}

	public static class QoLParameters {
		public final String token;
		public final String surveyName;
		public final String recordId;
		public final String combo1QoL;

		private QoLParameters(String t, String s, String r, String c) {
			token = t;
			surveyName = s;
			recordId = r;
			combo1QoL = c;
		}

		@Override
		public String toString() {
			return "token=" + token + ", survey=" + surveyName + ", recordId=" + recordId + ", combo1QoL=" + combo1QoL;
		}
	}

	@FXML
	private void initialize() {

		combo1QoL.getItems().addAll("Salva File .csv", "Importa dati con API");

	}

	@Override
	public void showResult(TabResult result) {
		// outputArea.setText(result.error != null ? result.error : result.message);
	}
}