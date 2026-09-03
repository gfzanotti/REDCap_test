package mi.istitutotumori.e_crf.utils;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.node.*;

public class JsonUtils {

	public static JsonNode convert(JsonNode node) {
		if (node.isObject()) {
			ObjectNode obj = (ObjectNode) node;
			obj.fieldNames().forEachRemaining(field -> {
				obj.set(field, convert(obj.get(field)));
			});
			return obj;

		} else if (node.isArray()) {
			ArrayNode arr = (ArrayNode) node;
			for (int i = 0; i < arr.size(); i++) {
				arr.set(i, convert(arr.get(i)));
			}
			return arr;

		} else if (node.isNumber() || node.isBoolean()) {
			return new TextNode(node.asText());

		} else {
			return node;
		}
	}
}