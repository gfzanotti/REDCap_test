package mi.istitutotumori.tdd;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Disabled;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import mi.istitutotumori.apiredcap.ApiService;
import mi.istitutotumori.e_crf.sanitize.SanitizerApi;

/**
 */

@Disabled("requires REDCap API")
class TddUserRights {

	private static final String TOKEN_upload = "..."; // "newTestGian PID 418"

	private static JsonNode o_r;
	private static JsonNode actualProjData;
	private static ObjectMapper mapper = new ObjectMapper();

	private static ArrayNode diffById(ArrayNode A, ArrayNode B) {
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

	private static String serializeDiffById(ArrayNode A, ArrayNode B) throws JsonProcessingException {
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


	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		o_r = (JsonNode) ApiService.getUserRights("userRole", "");
		actualProjData = (JsonNode) ApiService.getUserRights("userRole", TOKEN_upload);

		System.out.println("\n");
		for (JsonNode role : o_r) {
			String name = role.get("unique_role_name").asText();
			String forms = role.get("forms").asText();
			String forms_export = role.get("forms_export").asText();

			System.out.println(name + " - " + forms + " - criteria: " + forms_export);

			// System.out.printf("# value ## %s\n", o_r.get(0).get("unique_role_name"));
		}
		System.out.println("\n");
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

	@Test
	void test_3() throws Exception {
		ArrayNode a = (ArrayNode) o_r;
		ArrayNode b = (ArrayNode) actualProjData;

		System.out.println("## ELEMENTI MANTENUTI ##");
		System.out.println("  se non visualizzo nulla vuol dire che non ne è stato mantenuto alcuno  ");
		for (JsonNode element : b) {
			String value_1 = element.path("unique_role_name").asText();
			String value_2 = element.path("role_label").asText();
			System.out.println(value_1 + " : " + value_2);
		}

		String send = serializeDiffById(a, b);
		assertNotNull(send, "La metadata response non deve essere nulla");

		String response = ApiService.setUserRole(TOKEN_upload, "userRole", "json", send, "json");
		System.out.println("##  RESPONSE  ##########################################################");
		System.out.println("##   " + response + "   ##");
		System.out.println("##########################################################\n");
		assertNotNull(response, "La risposta REDCap non deve essere nulla");

	}
}
