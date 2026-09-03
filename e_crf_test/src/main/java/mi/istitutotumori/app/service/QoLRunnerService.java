package mi.istitutotumori.app.service;

import com.fasterxml.jackson.databind.JsonNode;
import mi.istitutotumori.apiredcap.ApiService;
import mi.istitutotumori.app.controller.QoLController;
import mi.istitutotumori.e_crf.InstrumentFactory;
import mi.istitutotumori.e_crf.InstrumentsProj;
import mi.istitutotumori.e_crf.InstrumentsProjBuilder;
import mi.istitutotumori.e_crf.sanitize.SanitizerApi;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

class QoLRunnerService extends BaseRunnerService implements SanitizerApi {
	private QoLController.QoLParameters params;

	public QoLRunnerService(QoLController.QoLParameters params) {
		this.params = params;
	}

	@Override
	protected void validateParameters(TabResult result) {
		// Validazioni di base per QoL
		if (params.recordId == null || params.recordId.isEmpty()) {
			result.error = "IL RECORD_ID è obbligatorio";
		}
		if (params.token == null || params.token.isEmpty()) {
			result.error = "IL TOKEN è obbligatorio";
		}
	}

	@Override
	protected InstrumentsProj buildInstruments() throws Exception {

		// Ottenere i dati del survey
		JsonNode res = ApiService.getSurveyRecord(params.recordId, params.surveyName);

		// Estrai i nomi degli strumenti QoL
		List<String> instrumentNames = extractQoLInstruments(res);

		// Ottenere metadata degli strumenti standard
		JsonNode jsonMetadataStd = ApiService.getStandardToolsMetadata();

		// Usa InstrumentsProj.builder() invece del costruttore vecchio
		InstrumentsProjBuilder builder = new InstrumentsProjBuilder();

		// Itera e aggiungi gli strumenti
		for (String instrumentName : instrumentNames) {
			JsonNode jsonNode = RunnerServiceUtils.filterByForm(jsonMetadataStd, instrumentName);

			if (jsonNode != null) {
				// Crea lo strumento usando StandardInstrument.Builder
				/*
				 * AbstractInstrument instrument = InstrumentFactory.createInstrument(
				 * "standard", instrumentName, jsonNode );
				 */

				// Aggiungi al progetto
				// builder.addInstrument(instrument);
				builder.addInstrument(InstrumentFactory.createInstrument("standard", instrumentName, jsonNode));
			}
		}

		// Restituisci il progetto
		return builder.build();
	}

	@Override
	protected InstrumentsProj processInstrumentMerge(InstrumentsProj instruments) throws Exception {
		RunnerServiceUtils rsu = new RunnerServiceUtils(params.token, instruments);
		return RunnerServiceUtils.getMergedInstList(rsu.existingProjInsts, rsu.addingProjInsts);
	}

	@Override
	protected TabResult handleOutput(InstrumentsProj instruments, TabResult result) throws Exception {
		String action = getSelectedAction();
		String fileName = outputDir + "/" + getOutputFileName() + "_" + dataOggi + ".csv";

		if (action == null || action.equals("Salva File .csv")) {
			return handleCsvOutput(instruments, fileName, result);
		} else if (action.equals("Importa dati con API")) {
			return handleApiOutput(instruments, fileName, result);
		} else {
			result.error = "Valore non riconosciuto nella selezione CSV/API.";
			return result;
		}
	}

	private List<String> extractQoLInstruments(JsonNode res) {
		List<String> resList = SanitizerApi.extractFieldsWithValueOne(res);
		Pattern p = Pattern.compile("^(?!which).*?___(.+)$");

		return resList.stream().map(s -> {
			Matcher m = p.matcher(s);
			return m.matches() ? m.group(1) : null;
		}).filter(s -> s != null).collect(Collectors.toList());
	}

	@Override
	protected String getOutputFileName() {
		return "addQoL";
	}

	@Override
	protected String getOperationName() {
		return "QoL";
	}

	@Override
	protected String getSelectedAction() {
		return params.combo1QoL;
	}

	@Override
	protected String getToken() {
		return params.token;
	}

	@Override
	protected String getAdditionalInfo() {
		return String.format("""

				SURVEY_NAME: %s
				RECORD_ID: %s
				""", params.surveyName != null ? params.surveyName : "N/A", params.recordId);
	}
}