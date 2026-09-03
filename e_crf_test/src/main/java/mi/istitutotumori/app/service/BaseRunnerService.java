package mi.istitutotumori.app.service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import mi.istitutotumori.apiredcap.ApiService;
import mi.istitutotumori.e_crf.InstrumentsProj;
import mi.istitutotumori.e_crf.utils.SerializeInst2CsvString;
import mi.istitutotumori.e_crf.utils.SerializeInst2JsonString;

/**
 * Questa classe astratta applica la logica comune del "controller" (nei vari
 * TAB della GUI)
 */
abstract class BaseRunnerService {

	protected String outputDir = System.getProperty("user.home") + "/Desktop/OutPutREDCap";
	protected String dataOggi = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));

	private static final Logger exceptionLog = LogManager.getLogger("exceptionOutputLogger");
	private static final Logger returnApi = LogManager.getLogger("returnApiLogger");

	/**
	 * Template method per eseguire l'operazione
	 */
	public TabResult execute() {
		TabResult result = new TabResult();

		try {
			validateParameters(result);
			if (result.error != null)
				return result;

			InstrumentsProj instrumentList = buildInstruments();
			instrumentList = processInstrumentMerge(instrumentList);

			result = handleOutput(instrumentList, result);

		} catch (IOException e) {
			result.error = "Errore di comunicazione con l'API: \n" + e.getMessage();
			exceptionLog.error("API communication error", e);
		} catch (IllegalArgumentException e) {
			result.error = "Parametri non validi: \n" + e.getMessage();
			exceptionLog.error("Invalid Parameters \n", e);
		} catch (Exception e) {
			result.error = """
					Errore interno imprevisto.
					Dettaglio:
					%s

					Possibili cause:
					• Controlla i valori dei parametri.
					• Verifica che l'ID del record esista.

					""".formatted(e.getMessage());
			exceptionLog.error("Unexpected error \n", e);
		}

		return result;
	}

	// Metodi astratti che le sottoclassi devono implementare
	protected abstract void validateParameters(TabResult result);

	protected abstract InstrumentsProj buildInstruments() throws Exception;

	protected abstract String getOutputFileName();

	protected abstract String getOperationName();

	// Hook method - può essere sovrascritto se necessario
	protected InstrumentsProj processInstrumentMerge(InstrumentsProj instruments) throws Exception {
		return instruments;
	}

	// Metodo concreto per gestire l'output (CSV o API)
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

	protected TabResult handleCsvOutput(InstrumentsProj instruments, String fileName, TabResult result)
			throws Exception {
		SerializeInst2CsvString serializerFacade = new SerializeInst2CsvString();
		serializerFacade.toCsvFile(instruments.getInstruments(), fileName);

		logMissingVariables(serializerFacade);

		result.message = String.format("""
				File CSV generato correttamente.

				Output: %s
				%s
				""", fileName.split("/")[fileName.split("/").length - 1], getAdditionalInfo());

		return result;
	}

	protected TabResult handleApiOutput(InstrumentsProj instruments, String fileName, TabResult result)
			throws Exception {
		SerializeInst2CsvString serializerFacade2 = new SerializeInst2CsvString();
		serializerFacade2.toCsvFile(instruments.getInstruments(), fileName); // mi salva il file CSV anche quando uso le
																				// API per importazione

		logMissingVariables(serializerFacade2);

		String token = getToken();
		String jsonSerial = SerializeInst2JsonString.serializeMetadata(instruments.getInstruments());

		returnApi.info("CSV inviato da %s %n".formatted(getOperationName()));
		returnApi.info(jsonSerial);

		String esito = ApiService.setMetadata(token, "json", jsonSerial);
		logApiResponse(esito);

		result.message = String.format("""
				Dati %s elaborati e importati con API.

				Token : %s
				Risposta API: %s (Fields caricati)
				%s

				""", getOperationName(), token, esito, getAdditionalInfo());

		return result;
	}

	// Metodi di supporto
	protected abstract String getSelectedAction();

	protected abstract String getToken();

	protected abstract String getAdditionalInfo();

	private void logMissingVariables(SerializeInst2CsvString serializer) {
		returnApi.info("***  VARIABILI ASSENTI (field eliminato)  ***");
		if (serializer.getSetEliminato() != null && returnApi.isInfoEnabled()) {
			returnApi.info("%s%n".formatted(serializer.getSetEliminato()));
		}
	}

	private void logApiResponse(String response) {
		returnApi.info("###  RESPONSE API  ###");
		if (response != null && returnApi.isInfoEnabled()) {
			returnApi.info("%s%n".formatted(response));
		}
	}
}