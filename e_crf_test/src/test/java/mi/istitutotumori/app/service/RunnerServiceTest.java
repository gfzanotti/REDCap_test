package mi.istitutotumori.app.service;

import static org.junit.jupiter.api.Assertions.fail;

import java.util.Set;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import mi.istitutotumori.apiredcap.ApiService;

@Disabled("integration tests")
class RunnerServiceTest {

	public JsonNode filterByForms(JsonNode metadata, Set<String> formsDaTenere) {
		ObjectMapper mapper = new ObjectMapper();
		ArrayNode result = mapper.createArrayNode();

		for (JsonNode field : metadata) {
			JsonNode formNode = field.get("form_name");
			if (formNode != null && formsDaTenere.contains(formNode.asText())) {
				result.add(field);
			}
		}

		return result;
	}

	@Test
	@Disabled("Not yet implemented")
	void testRunLab() {

	}

	@Test
	@Disabled("Not yet implemented")
	void testRunStandard() {

	}

	@Test
	void testRunEotEos() throws Exception {
		JsonNode jsonMetadataStd = (JsonNode) ApiService.getMetadata("7...", "json", "json",
				null, null);
		// System.out.println(jsonMetadataStd.get(null).toPrettyString());
		Set<String> inst2create = Set.of("end_of_study", "end_of_treatment");
		JsonNode filtrati = filterByForms(jsonMetadataStd, inst2create);

		System.out.println(filtrati.toPrettyString());

		// InstrumentList addInst = InstrumentList.buildStandardBundle(inst2create,
		// null, filtrati);

	}

	@Test
	@Disabled
	void testRunQoL() {
		fail("Not yet implemented");
	}

}
