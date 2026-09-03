package mi.istitutotumori.tdd;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.charset.StandardCharsets;
import java.net.URLEncoder;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Disabled;

import com.fasterxml.jackson.databind.JsonNode;

import mi.istitutotumori.apiredcap.ApiService;
import mi.istitutotumori.e_crf.AbstractInstrument;
import mi.istitutotumori.e_crf.InstrumentFactory;
import mi.istitutotumori.e_crf.utils.SerializeInst2CsvString;

/**
 * Test per verificare la corretta gestione del carattere "°" nel campo
 * "indication_ae_specify" dell'Instrument "concomitant_medications".
 */
@Disabled("requires REDCap API")
class TestConcomitantMedChar {

	private JsonNode loadMetadata() throws Exception {
		JsonNode json = (JsonNode) ApiService.getMetadata("...", "json", "json", null,
				"concomitant_medications");

		assertNotNull(json, "Metadata JSON nullo");
		assertTrue(json.isArray(), "Metadata non è un array");
		assertTrue(json.size() > 0, "Metadata vuoto");

		return json;
	}

	private AbstractInstrument loadInstrument(JsonNode json) throws Exception {
		AbstractInstrument inst = InstrumentFactory.createInstrument("std", "concomitant_medications", json);

		assertNotNull(inst);
		assertNotNull(inst.getFields());
		assertTrue(inst.getFields().size() > 0);

		return inst;
	}

	@Test
	void testCharacterDegreeSymbolPreservedInCsv() throws Exception {

		JsonNode json = loadMetadata();
		AbstractInstrument inst = loadInstrument(json);

		// Recupero il campo problematico
		var field = inst.getField("indication_ae_specify");
		assertNotNull(field, "Campo indication_ae_specify non trovato");

		String label = field.getFieldLabel();
		assertNotNull(label);

		// Verifica che il carattere ° sia presente nel label originale
		assertTrue(label.contains("°"), "Il label originale NON contiene il carattere °");

		// Serializzazione CSV con façade refactorizzato
		SerializeInst2CsvString facade = new SerializeInst2CsvString();
		String csv = facade.toCsvString(List.of(inst));

		assertNotNull(csv);
		assertTrue(csv.contains("field_name"), "CSV deve contenere intestazioni");

		// Verifica che il carattere ° sia presente anche nel CSV
		assertTrue(csv.contains("°"), "Il CSV NON contiene il carattere ° → problema di encoding");

		// Verifica che il campo specifico sia presente nel CSV
		assertTrue(csv.contains("indication_ae_specify"), "Il CSV non contiene il campo indication_ae_specify");

		// Verifica che il label sia correttamente serializzato con virgolette RFC 4180
		String escapedLabel = "\"" + label.replace("\"", "\"\"") + "\"";
		assertTrue(csv.contains(escapedLabel), "Il CSV non contiene il label correttamente serializzato");

		// Debug utile (non necessario per il test)
		System.out.println("Label originale: " + label);
		System.out.println("Label escaped: " + escapedLabel);
		System.out.println("CSV generato:\n" + csv);

		// Verifica UTF‑8
		String encoded = URLEncoder.encode(label, StandardCharsets.UTF_8);
		assertNotNull(encoded);
		System.out.println("UTF‑8 encoded: " + encoded);
	}
}
