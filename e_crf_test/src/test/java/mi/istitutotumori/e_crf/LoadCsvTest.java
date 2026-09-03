package mi.istitutotumori.e_crf;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Disabled;

import com.opencsv.CSVReader;

import mi.istitutotumori.apiredcap.ApiService;

@Disabled("requires local file path hardcoded")
class LoadCsvTest {

	private static final Logger testLog = LogManager.getLogger("testOutputLogger");
	ApiService api = new ApiService();

	private static List<List<String>> loadCsv() throws Exception {
		String file = "C:\\Users\\zanottigianfranco\\Desktop\\OutPutREDCap\\dataTest\\SurveyECRF_DataDictionary_2025-12-21.csv";
		List<List<String>> risultato = new ArrayList<>();

		try (CSVReader reader = new CSVReader(new FileReader(file))) {
			testLog.info("Input Stream ottenuto: \n{} \n", reader);
			String[] riga;

			while ((riga = reader.readNext()) != null) {
				List<String> row = new ArrayList<>();
				for (String campo : riga) {
					row.add(campo);
				}
				risultato.add(row);
			}
		}
		return risultato;
	}

	@Test
	void testCsv() throws Exception {
		testLog.info("Inizio memorizzazione strutture dati metodo ##testCsv##");
		List<List<String>> risultato = loadCsv();

		testLog.info("RISULTATO ottenuto: \n");
		for (List<String> r : risultato) {
			testLog.info(String.join(", ", r));
		}
		testLog.info("struttura CSV (List<List<String>>): \n{} \n", risultato);

		// Esempio di asserzione
		assertEquals("Variable / Field Name", risultato.get(0).get(0).replace("\uFEFF", ""));
		assertEquals("Form Name", risultato.get(0).get(1));
		assertEquals("survey_introduzione", risultato.get(1).get(1));
		assertEquals("record_id", risultato.get(1).get(0));
		assertEquals("survey_introduzione", risultato.get(2).get(1));
		assertEquals("Basophils", risultato.get(29).get(4));
	}


}
