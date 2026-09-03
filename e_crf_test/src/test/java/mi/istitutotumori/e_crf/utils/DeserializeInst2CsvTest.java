package mi.istitutotumori.e_crf.utils;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;

import mi.istitutotumori.apiredcap.ApiService;
import mi.istitutotumori.e_crf.AbstractInstrument;
import mi.istitutotumori.e_crf.InstrumentFactory;
import mi.istitutotumori.e_crf.InstrumentsProj;
import mi.istitutotumori.e_crf.InstrumentsProjBuilder;

@Disabled("integration tests requiring REDCap API")
class DeserializeInst2CsvTest {

	private static final Logger testLog = LogManager.getLogger("testOutputLogger");

	// Lista strumenti del progetto Standard Tools
	private static final List<String> NAME_INST_PROJECT_TOOLS = List.of("criteria", "medical_history", "visit",
			"laboratory_data", "study_treatment", "pharmacokinetic", "radiological_exams", "recist_11", "irrecist",
			"qlq_ov28", "eq_5d_5l", "adl", "iadl", "hads", "end_of_treatment", "end_of_study",
			"concomitant_medications", "adverse_events", "sae", "monitor", "qlq_c30", "qlq_cr29", "protocol_deviations",
			"sdm9", "eortc_qlqinfo25", "eortc_qlqbil21", "factm", "live_status", "tns", "cipnrods", "cipn20",
			"factgogntx", "proctcae", "mauq", "distress_thermometer", "esas", "famcarep13", "famcarecaregiver",
			"healthcare_costs", "social_status", "modified_msas", "expectation_of_cinvs", "diary", "symptoms", "stai",
			"items", "msas", "flie", "final_assessment_cinvs_cses", "pais", "pil", "inclusion_of_others_ios", "nffq",
			"medilite");

	// ------------------------------------------------------------
	// Utility: recupera un singolo Instrument da REDCap
	// ------------------------------------------------------------
	private AbstractInstrument loadInstrument(String name) throws Exception {
		JsonNode json = (JsonNode) ApiService.getMetadata("...", "json", "json", null,
				name);

		assertNotNull(json, "Metadata JSON nullo per " + name);
		assertTrue(json.isArray(), "Metadata non è un array per " + name);
		assertTrue(json.size() > 0, "Metadata vuoto per " + name);

		return InstrumentFactory.createInstrument("std", name, json);
	}

	// ------------------------------------------------------------
	// Test singolo Instrument → CSV
	// ------------------------------------------------------------
	@Test
	void testSingleInstrumentToCsv() throws Exception {

		String instName = "medical_history";

		AbstractInstrument inst = loadInstrument(instName);

		List<AbstractInstrument> list = List.of(inst);

		SerializeInst2CsvString facade = new SerializeInst2CsvString();

		String csv = facade.toCsvString(list);

		assertNotNull(csv);
		assertTrue(csv.contains("field_name"));
		assertTrue(csv.contains(instName));

		testLog.info("CSV generato:\n{}", csv);
	}

}
