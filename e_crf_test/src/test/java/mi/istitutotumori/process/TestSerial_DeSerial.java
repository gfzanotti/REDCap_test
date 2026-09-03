package mi.istitutotumori.process;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Disabled;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import mi.istitutotumori.apiredcap.ApiService;
import mi.istitutotumori.e_crf.InstrumentsProj;
import mi.istitutotumori.e_crf.InstrumentsProjBuilder;
import mi.istitutotumori.e_crf.labutils.ConstData;
import mi.istitutotumori.e_crf.utils.SerializeInst2CsvString;
import mi.istitutotumori.e_crf.utils.SerializeInst2JsonString;

@Disabled("integration test requires external REDCap")
class TestSerial_DeSerial {
	final Logger otherInfo = LogManager.getLogger("optional_log");

	String outputDir = System.getProperty("user.home") + "/Desktop/OutPutREDCap/test";
	String dataOggi = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
	static ConstData valConst = new ConstData();
	private static String xxx = "...";
	private static JsonNode jnIn;

	// METODI DI SERVIZIO
	public JsonNode filterByForm(JsonNode metadata, String formName) {
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

	@BeforeAll
	static void init() throws Exception {
		System.out.println("Inizializzazione globale");
		/* JsonNode estratto da REDCap [DE-Serializzazione] */
		jnIn = (JsonNode) ApiService.getMetadata(valConst.getToken("testGian_Produzione"), "json", "json", null, null);
		// jnIn = (JsonNode) ApiService.getMetadata(LIMEFAP_TOKEN_PROJ_DOWNLOAD, "json",
		// "json", null, null);
		/* stampo l'oggetto JsonNode */
		System.out.printf("JsonNode stampa %n%s%n", jnIn.toPrettyString());
	}

	/**
	 * Applico SERIALIZZAZIONE - DESERIALIZZAZIONE all'andata; verifico il buon
	 * esito di DE-SERIALIZZAZIONE - SERIALIZZAZIONE al ritorno
	 * 
	 * @throws Exception
	 */
	@Test /* 1° test */
	void serDeSer() throws Exception {

		/*
		 * RI-serializzio il JsonNode con "ObjectMapper" e lo importo in nuovo progetto
		 * REDCap
		 */
		ObjectMapper mapper = new ObjectMapper();
		String metadataJson = mapper.writeValueAsString(jnIn);
		String esito = ApiService.setMetadata(valConst.getToken("testGian_1"), "json", metadataJson);
		assertNotNull(esito);
		/* stampa il numero di righe/record inviati */
		System.out.println(esito);
		assertEquals("364", esito);
	}

	/**
	 * ORA INSERISCO la costruzione di un PROGETTO con tutti gli INSTRUMENTS e poi
	 * SERIALIZZO ED INVIO *
	 * 
	 * @throws Exception
	 */
	@Test
	// @Disabled("Test disabilitato temporaneamente")
	void projSer_Send() throws Exception {

		/*
		 * uso la classe RunnerServiceUtils per creare il progetto con INTRUMENTS Estrae
		 * i nomi degli strumenti presenti in un JsonNode
		 */

		Set<String> listInstExistingProject = new LinkedHashSet<>();
		jnIn.forEach(n -> {
			if (n.has("form_name")) {
				listInstExistingProject.add(n.get("form_name").asText());
			}
		});

		/* Usa InstrumentsProjBuilder per costruire gli strumenti esistenti */
		InstrumentsProjBuilder builder = new InstrumentsProjBuilder();

		for (String s : listInstExistingProject) {
			JsonNode singleNode = filterByForm(jnIn, s);
			if (singleNode != null && singleNode.size() > 0) {
				/* Usa la Factory per creare lo strumento */
				builder.addInstrument("standard", s, singleNode);
			}
		}

		/* Creo i record immutabili */
		InstrumentsProj existingProjInsts = builder.build();

		// SerializeInst2CsvString des = new
		// SerializeInst2CsvString(existingProjInsts.getInstruments(),
		// outputDir+"/XXMedas.csv");

		/* Istanzio il façade */
		SerializeInst2CsvString facade = new SerializeInst2CsvString();

		/* 1. Ottengo la stringa JSON */
		String jsonSerial = SerializeInst2JsonString.serializeMetadata(existingProjInsts.getInstruments());
		// System.out.println(csv);

		// 2. Scrivo il CSV su file */
		facade.toCsvFile(existingProjInsts.getInstruments(), outputDir + "/XXMedas.csv");
		otherInfo.info("***  STRINGA des  ***");
		otherInfo.info(jsonSerial);

		String esitoTest2 = ApiService.setMetadata(valConst.getToken("testGian_2"), "json", jsonSerial);

		assertNotNull(esitoTest2);
		/* stampa il numero di righe/record inviati */
		System.out.println(esitoTest2);
		assertEquals("364", esitoTest2);
	}
}
