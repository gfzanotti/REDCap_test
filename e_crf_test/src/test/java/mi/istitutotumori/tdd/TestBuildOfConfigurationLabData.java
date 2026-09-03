package mi.istitutotumori.tdd;

import static org.junit.jupiter.api.Assertions.*;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Disabled;
import com.fasterxml.jackson.databind.JsonNode;
import mi.istitutotumori.apiredcap.ApiService;

@Disabled("requires REDCap API")
class TestBuildOfConfigurationLabData {
	// esporto la survey
	JsonNode exportRecord;

	/**
	 * @throws IOException
	 *
	 */
	public TestBuildOfConfigurationLabData() throws IOException {
		this.exportRecord = (JsonNode) ApiService.getSurveyRecord("6", "survey_lab_data");
	}

	@Test
	void test() {
		System.out.printf("RECORD estratto %n%s%n", this.exportRecord.toPrettyString());
		String value = this.exportRecord.get("esami_non_int").asText();

		assertTrue(value.contains("1"));
	}

	@Test
	void test2() {
		String value = this.exportRecord.get("esami_non_int").asText();

		assertEquals("1", value);
	}

}
