package mi.istitutotumori.tdd;

//import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Disabled;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import mi.istitutotumori.apiredcap.ApiService;
import mi.istitutotumori.e_crf.ConfLabDataInstrument;
import mi.istitutotumori.e_crf.InstrumentFactory;
import mi.istitutotumori.e_crf.InstrumentsProj;
import mi.istitutotumori.e_crf.InstrumentsProjBuilder;
import mi.istitutotumori.e_crf.LabInstrument;
import mi.istitutotumori.e_crf.utils.SerializeInst2CsvString;
import mi.istitutotumori.app.service.RunnerServiceUtils;

@Disabled("requires REDCap API")
class TddUpdateLab {
	@BeforeEach
	void setUp() throws Exception {
		final Logger returnApi = LogManager.getLogger("returnApiLogger");
		final Logger otherInfo = LogManager.getLogger("optional_log");

		// String surveyVal = "6";
		String surveyVal = "1_TEST_gian";

		String outputDir = System.getProperty("user.home") + "/Desktop/OutPutREDCap";
		String dataOggi = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
		String fileNameSurveyUpdated = outputDir + "/" + "SurveyUpdated" + "_" + dataOggi + ".csv";
		String fileNameExistingProj = outputDir + "/" + "ExistingProj" + "_" + dataOggi + ".csv";
		String fileNameUpdateProj = outputDir + "/" + "UPDATEProj" + "_" + dataOggi + ".csv";

		// esporto la survey per l'update
		JsonNode stdJson = ApiService.getSurveyRecord(surveyVal, null);
		String tipoStudio = stdJson.get("esami_non_int").asText();
		String lingua = stdJson.get("lingua").asText();

		List<String> updateProjList = new ArrayList<>();
		if ("2".equals(lingua)) { // selezione italiano
			updateProjList.add("dati_laboratorio");
			updateProjList.add("configurazione_dati_laboratorio");
		} else {
			updateProjList.add("laboratory_data");
			updateProjList.add("configuration_of_lab_data");
		}

		// Crea direttamente InstrumentsProj usando il builder statico
		InstrumentsProjBuilder builder = new InstrumentsProjBuilder();

		// creo il primo instrument INIT
		// InitInstrument init = InitInstrument.getInit();
		// builder.addInstrument(init);
		builder.addInstrument(InstrumentFactory.createInstrument("init", "init"));

		// Determino quale LabInstrument creare
		LabInstrument labInstrument;
		ConfLabDataInstrument confLab;

		// 20.06.2026 ho cambiato il Record ID : 1_TEST_gian (anzichè 1) sul progetto
		// PID 377
		labInstrument = new LabInstrument(surveyVal, "survey_lab_data", updateProjList.get(0));
		// Aggiungi lo strumento
		builder.addInstrument(labInstrument);

		if ("1".equals(tipoStudio)) {
			confLab = new ConfLabDataInstrument(surveyVal, "survey_lab_data", updateProjList.get(1));
			// Aggiungi lo strumento
			builder.addInstrument(confLab);
		}

		InstrumentsProj updateProj = builder.build();

		//////////////////////////////////////////////

		//////////////////////////////////////////////
		/*
		 * List<String> updateProjList = updateProj.getInstruments().stream()
		 * .map(instrument -> instrument.getFields().get(0).getFormName()) .toList();
		 * proj.getInstruments().forEach(instrument -> { String formName =
		 * instrument.getFields() .get(0) .getFormName();
		 * 
		 */
		////////////////////////////////////////////////

		// la "loggo" direttamente poichè le collezioni java implementano toString()
		otherInfo.info("Instrument UPDATE: {}", updateProjList);


	}

	static JsonNode filterByForm(JsonNode metadata, String formName) {
		ObjectMapper mapper = new ObjectMapper();
		ArrayNode result = mapper.createArrayNode();

		for (JsonNode field : metadata) {
			JsonNode formNode = field.get("form_name");
			if (formNode != null && formName.equals(formNode.asText())) {
				result.add(field);
			}
		}
		return result;
	}

	@Test
	void test() {
		System.out.println("FINE");
		;
	}

}