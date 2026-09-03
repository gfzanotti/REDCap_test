package mi.istitutotumori.apiredcap;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * La Classe ApiService è una classe con metodi statici che costruisce le
 * chiamate API a REDCap, gestisce le response e utilizza il caching per evitare
 * chiamate ripetute. Restituisce oggetti JsonNode con i dati estratti o una
 * response sulla quantità dei "fields" importati
 */
public final class ApiService {

	private static final ObjectMapper mapper = new ObjectMapper();
	private final static Map<String, JsonNode> recordCache = new HashMap<>();
	private final static Map<String, JsonNode> metaCache = new HashMap<>();

	/**
	 * Il metodo "getSurveyRecord" usa il metodo (FACTORY) statico della classe
	 * ApiRecord per costruire un oggetto ApiRecord che uso per estrarre i dati
	 * delle survey
	 * 
	 * @param recordId
	 * @param form
	 * @return JsonNode con i dati REDCap
	 * @throws IOException
	 */
	public static JsonNode getSurveyRecord(String recordId, String form) throws IOException {
		String key = recordId + ":" + form;

		if (recordCache.containsKey(key)) {
			return recordCache.get(key);
		}

		// chiamo il FACTORY method della classe ApiRecord
		ApiRecord surveyRecord = ApiRecord.surveyRecord(recordId, form);
		String response = surveyRecord.doPost();

		if (response == null || response.isEmpty()) {
			throw new IOException("Empty response from API");
		}

		JsonNode result = mapper.readTree(response).get(0);
		recordCache.put(key, result);
		return result;
	}

	/**
	 * Il metodo "getSurveyMetadata" usa il metodo statico (FACTORY) della classe
	 * ApiMetadata per costruire un oggetto ApiMetadata che uso per estrarre i
	 * meta-dati delle survey
	 * 
	 * @param form
	 * @return JsonNode con i dati REDCap
	 * @throws Exception
	 */
	public static JsonNode getSurveyMetadata(String form) throws Exception {
		if (metaCache.containsKey(form)) {
			return metaCache.get(form);
		}
		ApiMetaData metaSurvey = ApiMetaData.surveyMetaData(form);
		String response = metaSurvey.doPost();

		if (response == null || response.isEmpty()) {
			throw new IOException("Empty response from API");
		}

		JsonNode result = mapper.readTree(response);
		metaCache.put(form, result);
		return result;

	}

	/**
	 * Il metodo "getStandardToolsMetadata" usa il metodo statico (FACTORY) della
	 * classe ApiMetadata per costruire un oggetto ApiMetadata che uso per estrarre
	 * i meta-dati del <i>progetto Standard Tools</i>
	 * 
	 * @return JsonNode Data Dictionary completo del progetto eCRF tools
	 * @throws Exception
	 */
	public static JsonNode getStandardToolsMetadata() throws Exception {
		/*
		 * if (metaCache.containsKey(form)) { return metaCache.get(form); }
		 */
		ApiMetaData standardToolsMetaData = ApiMetaData.standardToolsMetaData();
		String response = standardToolsMetaData.doPost();

		if (response == null || response.isEmpty()) {
			throw new IOException("Empty response from API");
		}

		return mapper.readTree(response);
	}

	/** ... */
	public static JsonNode getUserRights(String content, String token) throws Exception {
		/*
		 * if (metaCache.containsKey(form)) { return metaCache.get(form); }
		 */
		ApiUserRights userRights = ApiUserRights.userRights(content, token);
		String response = userRights.doPost();

		if (response == null || response.isEmpty()) {
			throw new IOException("Empty response from API");
		}

		return mapper.readTree(response);
	}

	/**
	 * Metodo generico getMetadata con 5 parametri che restituisce un Object
	 * 
	 * @param token
	 * @param format
	 * @param returnFormat
	 * @param fields
	 * @param forms
	 * @return Object
	 * @throws IOException
	 */
	public static Object getMetadata(String token, String format, String returnFormat, String fields, String forms)
			throws IOException {
		String key = token + ":" + format + ":" + returnFormat + ":" + forms;

		if (metaCache.containsKey(key)) {
			return metaCache.get(key);
		}

		ApiMetaData meta = new ApiMetaData(token, format, returnFormat, fields, forms);
		String response = meta.doPost();

		if (response == null || response.isEmpty()) {
			throw new IOException("Empty response from API");
		}

		switch (format) {
		case "csv":
			System.out.println(response);
			return response;

		case "json":
			return mapper.readTree(response);

		default:
			throw new IllegalArgumentException("Unsupported format: " + format);
		} // end switch
	} // end method getMetadata

	/**
	 * Metodo per <b>import</b> dei dati di un <i>Data Dictionary (metadata)</i>
	 * usando il metodo doPost() della classe astratta
	 * 
	 * @param token
	 * @param format
	 * @param data
	 * @return response -> Stringa con il numero dei <b>fields caricati</b>
	 * @throws IOException
	 */
	public static String setMetadata(String token, String format, String data) throws IOException {

		ApiMetaData meta = new ApiMetaData(token, format, data);
		String response = meta.doPost();

		if (response == null) {
			throw new IOException("No response from API");
		}

		return response;
	}

	/**
	 * 
	 */
	public static String setUserRole(String token, String content, String format, String data, String returnFormat)
			throws IOException {

		ApiUserRights userRole = new ApiUserRights(token, content, format, data, returnFormat);
		String response = userRole.doPost();

		if (response == null) {
			throw new IOException("No response from API");
		}

		return response;
	}
} // END class
