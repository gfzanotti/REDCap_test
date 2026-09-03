package mi.istitutotumori.e_crf.sanitize;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

import mi.istitutotumori.tracing.ExcludeTrace;

@ExcludeTrace(reason = "NON utile")
public interface SanitizerApi {

	static final ObjectMapper mapper = new ObjectMapper();

	static String cleanString(String input) {
		if (input == null || input.isBlank())
			return null;

		// 1. sostituisci NBSP con spazio normale
		input = input.replace("\u00A0", " ");

		// 2. rimuovi caratteri di controllo non validi
		input = input.replaceAll("\\p{C}", "");

		// 3. normalizza UTF-8 (NFC è la più compatibile con PHP)
		input = Normalizer.normalize(input, Normalizer.Form.NFC);

		return input;
	}

	public static String sanitizeAccenti(String json) {
		if (json == null)
			return null;

		// Sostituisce i caratteri problematici con entità HTML
		json = json.replace("è", "&egrave;").replace("é", "&eacute;").replace("à", "&agrave;").replace("ì", "&igrave;")
				.replace("ò", "&ograve;").replace("ù", "&ugrave;").replace("È", "&Egrave;").replace("É", "&Eacute;")
				.replace("À", "&Agrave;").replace("Ì", "&Igrave;").replace("Ò", "&Ograve;").replace("Ù", "&Ugrave;");

		return json;
	}

	public static JsonNode sanitizeForRedcap(JsonNode node) {
		if (node == null || node.isNull()) {
			return NullNode.getInstance();
		}

		if (node.isObject()) {
			ObjectNode cleanObj = mapper.createObjectNode();
			node.fields().forEachRemaining(entry -> cleanObj.set(entry.getKey(), sanitizeForRedcap(entry.getValue())));
			return cleanObj;
		}

		if (node.isArray()) {
			ArrayNode cleanArr = mapper.createArrayNode();
			node.forEach(elem -> cleanArr.add(sanitizeForRedcap(elem)));
			return cleanArr;
		}

		if (node.isTextual()) {
			return new TextNode(cleanString(node.asText()));
		}

		return node; // numeri, boolean, ecc. non richiedono pulizia
	}

	/*
	 * Metodo che estrae i campi con valore "1" da un oggetto JsonNode
	 */
	@ExcludeTrace(reason = "NON utile")
	public static List<String> extractFieldsWithValueOne(JsonNode recordJson) {
		List<String> result = new ArrayList<>();

		recordJson.fields().forEachRemaining(entry -> {
			JsonNode valueNode = entry.getValue();
			if (valueNode != null && "1".equals(valueNode.asText())) {
				result.add(entry.getKey());
			}
		});

		return result;
	}

}