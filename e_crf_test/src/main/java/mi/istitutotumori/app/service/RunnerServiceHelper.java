package mi.istitutotumori.app.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import mi.istitutotumori.e_crf.AbstractInstrument;
import mi.istitutotumori.e_crf.InitInstrument;
import mi.istitutotumori.e_crf.InstrumentFactory;
import mi.istitutotumori.e_crf.InstrumentsProj;
import mi.istitutotumori.e_crf.InstrumentsProjBuilder;
import mi.istitutotumori.e_crf.sanitize.SanitizerApi;

/**
 * Classe di "aiuto" con metodi statici: -
 * {@code InstrumentsProj createInstrumentBundle(List<String> instrumentNames, JsonNode jsonMetadata)}
 * - {@code List<String> extractQoLInstrumentNames(JsonNode responseJson)} -
 * String buildStandardInfoMessage(String token, String recordId, String
 * surveyName, String defaultSurvey, String defaultRecord) - ## metodo per user
 * role ##
 */

public class RunnerServiceHelper implements SanitizerApi {

	/**
	 * Crea una lista di strumenti a partire da una lista di nomi
	 * 
	 * @throws Exception
	 */
	public static InstrumentsProj createInstrumentBundle(List<String> instrumentNames, JsonNode jsonMetadata)
			throws Exception {

		// List<AbstractInstrument> instruments = new ArrayList<>();
		// ora uso direttamente il builder:
		InstrumentsProjBuilder builder = new InstrumentsProjBuilder();
		InitInstrument init = InitInstrument.getInit();
		builder.addInstrument(init);

		for (String name : instrumentNames) {
			JsonNode jsonNode = RunnerServiceUtils.filterByForm(jsonMetadata, name);
			if (jsonNode != null) {
				AbstractInstrument instrument = InstrumentFactory.createInstrument("standard", name, jsonNode);

				builder.addInstrument(instrument);
			}
		}

		return builder.build(); // restituisce InstrumentsProj immutabile
	}

	/**
	 * Estrae i nomi degli strumenti QoL dal JSON della risposta
	 */
	public static List<String> extractQoLInstrumentNames(JsonNode responseJson) {
		List<String> fieldsWithValueOne = SanitizerApi.extractFieldsWithValueOne(responseJson);
		Pattern pattern = Pattern.compile("^(?!which).*?___(.+)$");

		return fieldsWithValueOne.stream().map(field -> {
			Matcher matcher = pattern.matcher(field);
			return matcher.matches() ? matcher.group(1) : null;
		}).filter(name -> name != null).distinct().collect(Collectors.toList());
	}

	/**
	 * Costruisce un messaggio standardizzato con le informazioni comuni
	 */
	public static String buildStandardInfoMessage(String token, String recordId, String surveyName,
			String defaultSurvey, String defaultRecord) {
		String actualSurvey = (surveyName != null && !surveyName.isBlank()) ? surveyName : defaultSurvey;
		String actualRecord = (recordId != null && !recordId.isBlank()) ? recordId : defaultRecord;

		return String.format("""
				TOKEN: %s
				RECORD_ID: %s
				SURVEY_NAME: %s
				""", token, actualRecord, actualSurvey);
	}

	/**
	 * Metodo per il confronto e la differenza tra 2 ArrayNode, mi serve per upload
	 * dei "user_role" mancanti rispetto a quelli presenti nel progetto "tools"
	 * 
	 * @param A
	 * @param B
	 * @return ArrayNode
	 */
	public static ArrayNode diffById(ArrayNode A, ArrayNode B) {
		ArrayNode C = new ObjectMapper().createArrayNode();

		// Set<Integer> ids = new HashSet<>();
		Set<String> labels = new HashSet<>();

		// creo lista role_label presenti nel progetto "reale"
		for (JsonNode b : B) {
			// ids.add(a.get("role_label").asInt());
			labels.add(b.path("role_label").asText());
		}

		// confronto la lista precedente con i role_label presenti in TOOLS
		// se un role_label è assente lo aggiungo al JsonNode da caricare
		for (JsonNode a : A) {
			// int id = b.get("id").asInt();
			String label = a.path("role_label").asText();

			// if (!ids.contains(id)) {
			if (!labels.contains(label)) {
				C.add(a);
			}
		}

		return C;
	}

	/**
	 * Segue e competa il metodo "diffById" restituendo una stringa (già
	 * serializzata) da inviare con le API
	 * 
	 * @param A
	 * @param B
	 * @return String send
	 * @throws JsonProcessingException
	 */
	public static String serializeDiffById(ArrayNode A, ArrayNode B) throws JsonProcessingException {
		ArrayNode c = diffById(A, B);

		JsonNode root = SanitizerApi.sanitizeForRedcap(c);

		// cast
		ArrayNode array = (ArrayNode) root;

		for (JsonNode node : array) {
			ObjectNode obj = (ObjectNode) node;

			obj.put("unique_role_name", "");

			// crea oggetto vuoto
			ObjectNode emptyForms = mapper.createObjectNode();

			// sostituisce completamente "forms"
			obj.set("forms", emptyForms);
			obj.set("forms_export", emptyForms);

		}

		String send = mapper.writeValueAsString(array);
		return send;
	}
}