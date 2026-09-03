package mi.istitutotumori.app.service;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import mi.istitutotumori.apiredcap.ApiService;
import mi.istitutotumori.app.controller.LabController;
import mi.istitutotumori.e_crf.InstrumentFactory;
import mi.istitutotumori.e_crf.InstrumentsProj;
import mi.istitutotumori.e_crf.InstrumentsProjBuilder;

class LabRunnerService extends BaseRunnerService {
	private LabController.LabParameters params;

	public LabRunnerService(LabController.LabParameters params) {
		this.params = params;
	}

	@Override
	protected void validateParameters(TabResult result) {
		// Validazione specifica per Lab
		if (!((params.surveyName == null || params.surveyName.isEmpty())
				&& (params.recordId == null || params.recordId.isEmpty()))
				&& !(!params.surveyName.isEmpty() && !params.recordId.isEmpty())) {
			result.error = "Errore: inserire sia RECORD ID che SURVEY NAME,\noppure lasciarli entrambi vuoti per il default.";
		}
	}

	@Override
	protected InstrumentsProj buildInstruments() throws Exception {

		// GET tipo_studio da API con indicazione [esami_non_int]==1
		JsonNode stdJson = ApiService.getSurveyRecord(params.recordId, null);
		String tipoStudio = stdJson.get("esami_non_int").asText();

		// AGGIUNTO IL 30.06.2026 per l'opzione di update dell'instrument di laboratorio
		String lingua = stdJson.get("lingua").asText();
		List<String> updateProjList = new ArrayList<>();
		if ("2".equals(lingua)) { // selezione italiano
			updateProjList.add("dati_laboratorio");
			updateProjList.add("configurazione_dati_laboratorio");
		} else {
			updateProjList.add("laboratory_data");
			updateProjList.add("configuration_of_lab_data");
		}

		// Crea direttamente InstrumentsProj usando il builder statico
		InstrumentsProjBuilder builder = new InstrumentsProjBuilder();

		// creo il primo instrument INIT
		// InitInstrument init = InitInstrument.getInit();
		// builder.addInstrument(init);
		builder.addInstrument(InstrumentFactory.createInstrument("init", "init"));

		// Determino quale LabInstrument creare
		// LabInstrument labInstrument;

		if ((params.surveyName == null || params.surveyName.isEmpty())
				&& (params.recordId == null || params.recordId.isEmpty())) {
			// Costruttore senza parametri
			// labInstrument = new LabInstrument();
			builder.addInstrument(InstrumentFactory.createInstrument("lab", updateProjList.get(0)));
			if ("1".equals(tipoStudio)) {
				builder.addInstrument(InstrumentFactory.createInstrument("configlabdata", updateProjList.get(1)));
			}

		} else {
			// Costruttore con parametri
			// labInstrument = new LabInstrument(params.recordId, params.surveyName,
			// "Hematology Instrument");
			builder.addInstrument(InstrumentFactory.createInstrument("lab", updateProjList.get(0), params.recordId,
					params.surveyName, "Hematology Instrument"));
			if ("1".equals(tipoStudio)) {
				builder.addInstrument(InstrumentFactory.createInstrument("configlabdata", updateProjList.get(1),
						params.recordId, params.surveyName, "Hematology Instrument"));
			}

		}

		// Aggiungi lo strumento
		// builder.addInstrument(labInstrument); // non serve e uso il factory

		return builder.build();
	}

	/* aggiunta dopo richiesta di "ACCODAMENTO" dell'Instrument di laboratorio */
	@Override
	protected InstrumentsProj processInstrumentMerge(InstrumentsProj instruments) throws Exception {
		String token = getToken();

		// Gestione caso "accoda a progetto esistente"
		if ("accoda a progetto esistente".equals(params.comboSceltaProgetto)) {
			RunnerServiceUtils rsu = new RunnerServiceUtils(token, instruments);
			return RunnerServiceUtils.getMergedInstList(rsu.existingProjInsts, rsu.addingProjInsts);
		}

		// Gestione caso "UPDATE progetto esistente"
		if ("UPDATE (draft project)".equals(params.comboSceltaProgetto)) {
			RunnerServiceUtils rsu = new RunnerServiceUtils(token, instruments);
			return RunnerServiceUtils.getUpdateProj(rsu.existingProjInsts, rsu.addingProjInsts);
		}

		return instruments;
	}

	@Override
	protected String getOutputFileName() {
		return "LAB";
	}

	@Override
	protected String getOperationName() {
		return "LAB";
	}

	@Override
	protected String getSelectedAction() {
		return params.combo1;
	}

	@Override
	protected String getToken() {
		return params.token.isEmpty() ? "..." : params.token;
	}

	@Override
	protected String getAdditionalInfo() {
		return String.format("""

				SURVEY_NAME: %s
				RECORD_ID: %s
				SCELTA PROGETTO: %s

				Se faccio l'UPDATE di un progetto in PRODUZIONE
				mi salva solo il file 'CSV'
				""",
				(params.surveyName != null && !params.surveyName.isBlank()) ? params.surveyName : "survey_lab_data",
				(params.recordId != null && !params.recordId.isBlank()) ? params.recordId : "1",
				params.comboSceltaProgetto);
	}
}
