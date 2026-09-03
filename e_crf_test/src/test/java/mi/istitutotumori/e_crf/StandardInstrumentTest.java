package mi.istitutotumori.e_crf;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;

import mi.istitutotumori.apiredcap.ApiService;

@Disabled("integration tests requiring REDCap API")
class StandardInstrumentTest {
	private static final Logger testLog = LogManager.getLogger("testOutputLogger");

	StandardInstrument inst;

	StandardInstrumentTest() {
		inst = new StandardInstrument("provaTest");
		testLog.info("OK");
	}

	@Test
	void testGetName() {
		assertEquals("provaTest", inst.getName());
	}

	@Test
	void testStandardInstrumentString() {
		assertEquals("provaTest", "provaTest"); // da implementare
	}

	@Test
	@Disabled("Not yet implemented")
	void testStandardInstrumentStringJsonNode() {

	}

	@Test
	@Disabled("Not yet implemented")
	void testStandardInstrumentStringString() {

	}


	@Test
	void testInstTypeObject() throws IOException {
		String value = "visit";
		JsonNode jsonNode = (JsonNode) ApiService.getMetadata("...", "json", "json", null,
				value);
		inst = new StandardInstrument(value, jsonNode);
		System.out.println(inst.getField("phys_exam_performed").getClass().getName());

		assertTrue(inst.getField("phys_exam_performed") instanceof Field);
		assertTrue(inst.getFields() instanceof List<Field>);
	}

}
