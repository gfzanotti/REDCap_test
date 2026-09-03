package mi.istitutotumori.app.controller;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import mi.istitutotumori.app.service.TabResult;

public class StandardController implements ResultReceiver {

	// ATTENZIONE questi attributi devono essere uguali ai valori "fx:id" nei
	// rispettivi file *.fxml
	@FXML
	private ComboBox<String> combo1Std;
	@FXML
	private ComboBox<String> comboSceltaProgetto;
	@FXML
	private TextField tokenStd;
	@FXML
	private TextField recordStdId;
	@FXML
	private CheckBox concomitant_medications;
	@FXML
	private CheckBox laboratory_data;
	@FXML
	private CheckBox adverse_events;
	@FXML
	private CheckBox sae;
	@FXML
	private CheckBox monitor;
	@FXML
	private CheckBox medical_history;
	@FXML
	private CheckBox userRole;
	@FXML
	private CheckBox flag8;
	// @FXML private TextArea outputArea;

	private List<CheckBox> allCheckBoxes;
	// aggiunto dopo inserimento FLAG per 2userRole"
	private static final Set<String> INSTRUMENTS = Set.of("concomitant_medications", "laboratory_data",
			"adverse_events", "sae", "monitor", "medical_history");

	public StandardParameters getParameters() {
		// raccoglie solo le checkbox selezionate → usa fx:id come nome strumento
		/*
		 * List<String> selectedInstruments = allCheckBoxes.stream()
		 * .filter(CheckBox::isSelected) .map(cb -> cb.getId()) // usa il fx:id
		 * .collect(Collectors.toList());
		 */

		List<String> selectedInstruments = allCheckBoxes.stream().filter(cb -> INSTRUMENTS.contains(cb.getId()))
				.filter(CheckBox::isSelected).map(CheckBox::getId).collect(Collectors.toList());

		String userRoleId = userRole.isSelected() ? userRole.getId() : null;
		return new StandardParameters(combo1Std.getValue(), tokenStd.getText(), recordStdId.getText(),
				selectedInstruments, comboSceltaProgetto.getValue(), userRoleId);
	}

	public static class StandardParameters {
		public final String combo1, token, recordId, comboSceltaProgetto, userRole;
		public final List<String> instruments;

		private StandardParameters(String combo1, String token, String recordId, List<String> instruments,
				String comboSceltaProgetto, String userRole) {
			this.combo1 = combo1;
			this.token = token;
			this.recordId = recordId;
			this.instruments = instruments;
			this.comboSceltaProgetto = comboSceltaProgetto;
			this.userRole = userRole;
		}

		@Override
		public String toString() {
			return "combo1=" + combo1 + ", token=" + token + ", recordId=" + recordId + ", comboSceltaProgetto="
					+ comboSceltaProgetto;
		}
	}

	@FXML
	private void initialize() {

		combo1Std.getItems().addAll("Salva File .csv", "Importa dati con API");
		comboSceltaProgetto.getItems().addAll("crea nuovo progetto", "accoda a progetto esistente");

		// inizializzo la lista delle checkbox
		allCheckBoxes = List.of(medical_history, laboratory_data, concomitant_medications, adverse_events, sae, monitor,
				userRole,

				flag8);

		// inizializza le chekBox inattive
		userRole.setDisable(false); // incominciata ad usare 25.04.2026
		flag8.setDisable(true);
	}

	@Override
	public void showResult(TabResult result) {
		// outputArea.setText(result.error != null ? result.error : result.message);
	}

}
