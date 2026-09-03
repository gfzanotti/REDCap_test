package mi.istitutotumori.app.controller;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import mi.istitutotumori.app.service.TabResult;

public class EotEosController implements ResultReceiver {

	// ATTENZIONE questi attributi devono essere uguali ai valori "fx:id" nei
	// rispettivi file *.fxml
	@FXML
	private ComboBox<String> combo1;
	@FXML
	private TextField token;
	@FXML
	private TextArea infoTextArea;
	// @FXML private TextArea outputArea;

	public EotEosParameters getParameters() {
		return new EotEosParameters(combo1.getValue(), token.getText());
	}

	public static class EotEosParameters {
		public final String combo1;
		public final String token;

		private EotEosParameters(String c1, String t1) {
			combo1 = c1;
			token = t1;
		}

		@Override
		public String toString() {
			return "combo1=" + combo1 + ", token=" + token;
		}
	}

	@FXML
	private void initialize() {
		combo1.getItems().addAll("Salva File .csv", "Importa dati con API");

		String testoIstruzioni = "Aggiungi gli instrument di End of Treatment ed End of Study al resto del progetto.\n\n"
				+ "Parametro obbligatorio è il token del progetto su cui si vuole agire.\n\n"
				+ "Puoi salvare i dati in formato .csv o importarli direttamente nel progetto tramite API.";

		infoTextArea.setText(testoIstruzioni);
	}

	@Override
	public void showResult(TabResult result) {
		// outputArea.setText(result.error != null ? result.error : result.message);
	}
}
