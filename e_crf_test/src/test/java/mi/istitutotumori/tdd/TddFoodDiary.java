package mi.istitutotumori.tdd;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Disabled;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import mi.istitutotumori.apiredcap.ApiService;
import mi.istitutotumori.e_crf.sanitize.SanitizerApi;

/**
 * Uso il paradigma TDD per la risoluzione del malfunzionamento nella creazione
 * dell'instrument <b>Ermedas</b>. Questo è un esempio di testo errato:
 * 
 */
@Disabled("requires REDCap API")
class TddFoodDiary implements SanitizerApi {
	JsonNode export;

	TddFoodDiary() throws Exception {
		// seleziono i campi fields per valutare i singoli errori -> in API Playgrounf
		// questa selezione NON SEMBRA FUNZIONARE
		// restituisce tutti i fields
		this.export = (JsonNode) ApiService.getMetadata("...", "json", "json", null, null);
		// System.out.printf("COSTRUTTORE stampa
		// %n%s%n",this.export.get(0).get("field_name").toPrettyString());
		// System.out.printf("COSTRUTTORE stampa
		// %n%s%n",this.export.get(0).toPrettyString());
		System.out.printf("COSTRUTTORE stampa %n%s%n", this.export.toPrettyString());
	}

	@Test
	void testSendSerializedJsonOfFood() throws IOException {
		// serializzo il JsonNode -> pggetto "sent"
		JsonNode sanitized = SanitizerApi.sanitizeForRedcap(this.export);
		String js = new ObjectMapper().writeValueAsString(sanitized);

		// ATTENZIONE js è un array Json, prima devo selezionare l'indice
		// stampa diagnostica caratteri unicode
		// System.out.println(js.codePoints().mapToObj(cp -> String.format("U+%04X",
		// cp)).toList());

		// per correggere il failure ho applicato il metodo sanitizeString() in
		// setFieldLabel(String fieldLabel)
		// nella classe Field.java
		// String verifica =
		// sanitized.get("field_name"=="oth_food2_emb").get("branching_logic").asText();
		String verifica = null;
		for (JsonNode node : sanitized) {
			if ("oth_food2_emb".equals(node.get("field_name").asText())) {
				verifica = node.get("branching_logic").asText();
				System.out.println(verifica);
				break;
			}
		}
		System.out.println(js);

		assertTrue(verifica.contains("[oth_food1]"));

		String js2 = SanitizerApi.sanitizeAccenti(js);
		String sent = ApiService.setMetadata("...", "json", js2);

		System.out.printf("righe inviate: \t = \t%s%n", sent);

		// TEST FALLISCE: problema il JSON contiene caratteri speciali (come è, é, à,
		// etc.) che non sono correttamente codificati in UTF-8
		assertEquals("109", sent);
	}
}