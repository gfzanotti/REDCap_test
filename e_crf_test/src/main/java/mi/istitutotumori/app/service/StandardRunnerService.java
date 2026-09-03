package mi.istitutotumori.app.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import mi.istitutotumori.apiredcap.ApiService;
import mi.istitutotumori.app.controller.StandardController;
import mi.istitutotumori.e_crf.InstrumentFactory;
import mi.istitutotumori.e_crf.InstrumentsProj;
import mi.istitutotumori.e_crf.InstrumentsProjBuilder;

class StandardRunnerService extends BaseRunnerService {

	private static final Logger returnApi = LogManager.getLogger("returnApiLogger");
	private StandardController.StandardParameters params;
	List<String> listUserRole = new ArrayList<>();

	public StandardRunnerService(StandardController.StandardParameters params) {
		this.params = params;

		/**
		 * 25.04.2026 aggiungo il codice per inserire i ruoli utente cerco di evitare di
		 * andarli a ricopiare in un progetto "sensibile" construendo una lista di token
		 * da evitare
		 */
		Set<String> valoriProibiti = Set.of("");
		if (params.userRole != null && !valoriProibiti.contains(params.token)) {
			// ObjectMapper objectMapper = new ObjectMapper();
			JsonNode dataFromTools = null;
			JsonNode actualProjData = null;
			try {
				// se passo token (secondo parametro) == null o "" -> scarico da eCRF tools
				dataFromTools = (JsonNode) ApiService.getUserRights("userRole", "");
				actualProjData = (JsonNode) ApiService.getUserRights("userRole", params.token);

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			try {
				String send = RunnerServiceHelper.serializeDiffById((ArrayNode) dataFromTools,
						(ArrayNode) actualProjData);
				// String response = ApiService.setUserRole(params.token, "userRole", "json",
				// send, "json");
				ApiService.setUserRole(params.token, "userRole", "json", send, "json");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// creo la lista degli "user_role" presenti nel progetto
			for (JsonNode element : actualProjData) {
				String value_1 = element.path("unique_role_name").asText();
				String value_2 = element.path("role_label").asText();

				listUserRole.add(value_1 + " : " + value_2);
			}

		}
	}

	@Override
	protected void validateParameters(TabResult result) {
		if (params.recordId == null || params.recordId.isEmpty()) {
			result.error = "IL RECORD_ID è obbligatorio";
			return;
		}

		if (params.comboSceltaProgetto == null || params.comboSceltaProgetto.isBlank()) {
			result.error = "DEVI scegliere cosa vuoi fare!";
			return;
		}
	}

	@Override
	protected InstrumentsProj buildInstruments() throws Exception {
		// GET tipo_studio da API
		JsonNode stdJson = ApiService.getSurveyRecord(params.recordId, null);
		String tipoStudio = stdJson.get("tipo_studio").asText();

		if (!"1".equals(tipoStudio)) {
			throw new IllegalArgumentException("Il tipo di studio deve essere 1 (Trial interventistico no profit)");
		}

		// Ottenere metadata degli strumenti standard
		JsonNode jsonMetadataStd = ApiService.getStandardToolsMetadata();

		returnApi.info("***  JsonNode Tools Standard  ***");
		if (jsonMetadataStd != null && returnApi.isInfoEnabled()) {
			returnApi.info("%s%n".formatted(jsonMetadataStd.toPrettyString()));
		}

		// Usare il Builder di InstrumentsProj
		InstrumentsProjBuilder builder = new InstrumentsProjBuilder();

		// creo il primo instrument INIT
		// InitInstrument init = InitInstrument.getInit();
		// builder.addInstrument(init);
		builder.addInstrument(InstrumentFactory.createInstrument("init", "init"));

		for (String instrumentName : params.instruments) {
			JsonNode jsonNode = RunnerServiceUtils.filterByForm(jsonMetadataStd, instrumentName);
			if (jsonNode != null) {
				// Usa il metodo addInstrument del builder
				builder.addInstrument("standard", instrumentName, jsonNode);
			}
		}

		// Costruire InstrumentsProj
		return builder.build();
	}

	@Override
	protected InstrumentsProj processInstrumentMerge(InstrumentsProj instruments) throws Exception {
		String token = getToken();

		// Gestione caso "accoda a progetto esistente"
		if ("accoda a progetto esistente".equals(params.comboSceltaProgetto)) {
			RunnerServiceUtils rsu = new RunnerServiceUtils(token, instruments);
			return RunnerServiceUtils.getMergedInstList(rsu.existingProjInsts, rsu.addingProjInsts);
		}

		return instruments;
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
			result.error = "Errore: valore non riconosciuto per la scelta CSV/API.";
			return result;
		}
	}

	@Override
	protected String getOutputFileName() {
		return "StandardBundle";
	}

	@Override
	protected String getOperationName() {
		return "STANDARD";
	}

	@Override
	protected String getSelectedAction() {
		return params.combo1;
	}

	@Override
	protected String getToken() {
		return params.token.isEmpty() ? "" : params.token;
	}

	@Override
	protected String getAdditionalInfo() {
		return String.format("""

				RECORD_ID: %s
				SCELTA PROGETTO: %s

				NOTA: se il token non è stato inserito,
				i dati sono stati importati nel progetto di default.

				INFO: LISTA ELEMENTI MANTENUTI nel caso di import "user_role".
				      SE non visualizzo nulla allora non ne è stato mantenuto alcuno

				      %s

				""", params.recordId, params.comboSceltaProgetto, String.join("\n      ", listUserRole));
	}
}