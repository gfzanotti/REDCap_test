package mi.istitutotumori.app.service;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Disabled;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import mi.istitutotumori.apiredcap.ApiService;
import mi.istitutotumori.e_crf.InstrumentsProjBuilder;
import mi.istitutotumori.e_crf.labutils.ConstData;
import mi.istitutotumori.e_crf.utils.SerializeInst2CsvString;

/**
 * La classe FullProcessTest testa il processo di formazione dei dati degli
 * oggetti REDCap nei vari TAB bypassando la GUI
 * 
 * @param <AbstractInstrument>
 */
@Disabled("integration tests requiring REDCap API and local resources")
class FullProcessTest {

	String outputDir = System.getProperty("user.home") + "/Desktop/OutPutREDCap";
	String dataOggi = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
	ConstData valConst = new ConstData();

	// -------------------------------
	// Utility: filtra metadata per form
	// -------------------------------
	public JsonNode filterByForm(JsonNode metadata, String formName) {
		ObjectMapper mapper = new ObjectMapper();
		ArrayNode result = mapper.createArrayNode();

		for (JsonNode field : metadata) {
			if (field.has("form_name") && formName.equals(field.get("form_name").asText())) {
				result.add(field);
			}
		}
		return result;
	}

	// -------------------------------
	// Test token
	// -------------------------------
	@Test
	void testTokenRetrieval() {
		String token = valConst.getToken("eCRF tools");
		assertNotNull(token);
		assertEquals("...", token);

		String token2 = valConst.getToken("testGian_1");
		assertNotNull(token2);
	}

	// -------------------------------
	// Test principale: costruzione strumenti + CSV
	// -------------------------------
	@Test
	void testRunStandard() throws Exception {

		List<String> newInstName = List.of("concomitant_medications", "laboratory_data", "adverse_events", "sae",
				"monitor", "medical_history");

		InstrumentsProjBuilder builder = new InstrumentsProjBuilder();

		// Recupero metadata standard
		JsonNode metadata = ApiService.getStandardToolsMetadata();
		assertNotNull(metadata);
		assertTrue(metadata.isArray());

		// Costruisco strumenti
		for (String formName : newInstName) {
			JsonNode jsonNode = filterByForm(metadata, formName);
			assertNotNull(jsonNode);
			assertTrue(jsonNode.isArray());
			assertTrue(jsonNode.size() > 0, "Il form " + formName + " non contiene campi");

			builder.addInstrument("std", formName, jsonNode);
		}

		// Verifica numero strumenti creati
		var instruments = builder.build().getInstruments();
		assertEquals(newInstName.size(), instruments.size());

		// Serializzazione CSV
		SerializeInst2CsvString facade = new SerializeInst2CsvString();
		String csv = facade.toCsvString(instruments);

		assertNotNull(csv);
		assertTrue(csv.contains("field_name"), "CSV deve contenere intestazioni");
		assertTrue(csv.contains("form_name"));

		// Verifica formato CSV (virgolette + separatore)
		assertTrue(csv.startsWith("\"field_name\",\"form_name\""));

		// Verifica numero righe (428 campi attesi)
		long numLines = csv.lines().count() - 1; // tolgo intestazione
		assertEquals(428, numLines);

		// Invio metadata a REDCap
		String esito = ApiService.setMetadata(valConst.getToken("testGian_1"), "csv", csv);

		assertNotNull(esito);
		assertEquals("428", esito);
	}

}
