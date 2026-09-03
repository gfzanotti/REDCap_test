package mi.istitutotumori.app.service;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Disabled;

import com.fasterxml.jackson.databind.JsonNode;

import mi.istitutotumori.apiredcap.ApiService;
import mi.istitutotumori.e_crf.InstrumentsProj;
import mi.istitutotumori.e_crf.InstrumentsProjBuilder;

@Disabled("integration test requiring REDCap API")
class RunnerServiceUtilsTest {
	private static final Logger testLog = LogManager.getLogger("testOutputLogger");
	RunnerServiceUtils testObj1;

	/**
	 * @param existingProjInsts
	 * @param addingProjInsts
	 * @throws Exception
	 */
	public RunnerServiceUtilsTest() throws Exception {

		String token = "...";
		JsonNode jsonMetadataStd = (JsonNode) ApiService.getMetadata(token, "json", "json", null, null);
		InstrumentsProj newInstList = new InstrumentsProjBuilder()
				.buildStandardBundle(Set.of("end_of_study", "end_of_treatment"), null, jsonMetadataStd).build();

		this.testObj1 = new RunnerServiceUtils(token, newInstList);
	}

	@Test
	void testGetMergedInstList() {

		/*
		 * InstrumentsProjBuilder pb = new InstrumentsProjBuilder();
		 * pb.merge(testObj1.existingProjInsts, testObj1.addingProjInsts);
		 * assertTrue(pb.build() instanceof InstrumentsProj);
		 */

		assertTrue(RunnerServiceUtils.getMergedInstList(testObj1.existingProjInsts,
				testObj1.addingProjInsts) instanceof InstrumentsProj);

	}

	// evoluzione della classe test provo a generare i tipi JsonNode ed importarli
	// in redcap
	@Test
	void importJson() {
		/*
		 * InstrumentsProj inst = new InstrumentsProjBuilder()
		 * .merge(testObj1.existingProjInsts, testObj1.addingProjInsts) .build();
		 */

		InstrumentsProj inst = RunnerServiceUtils.getMergedInstList(testObj1.existingProjInsts,
				testObj1.addingProjInsts);

		testLog.info("Nuovo InstrumentList 'mergiato'\n");
		testLog.info(inst.toString());
	}

}
