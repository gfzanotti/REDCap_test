package mi.istitutotumori.tdd;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Disabled;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import mi.istitutotumori.apiredcap.ApiService;
import mi.istitutotumori.e_crf.sanitize.SanitizerApi;

/**
 * Uso il paradigma TDD per la risoluzione del malfunzionamento nella creazione
 * dell'instrument <b>PIL</b>. Questo è un esempio di testo errato:
 * 
 * LO SCOPO DELLA VITA (PIL) ? Per ciascuna delle seguenti affermazioni indica
 * con il cursore il numero che le sembra corrispondere il pi? esattamente alla
 * sua situazione.?
 * 
 */
@Disabled("requires REDCap API")
class TddErrorSolvePIL implements SanitizerApi {
	JsonNode export;

	TddErrorSolvePIL() throws Exception {
		// seleziono i campi fields per valutare i singoli errori -> in API Playgrounf
		// questa selezione NON SEMBRA FUNZIONARE
		// restituisce tutti i fields
		this.export = (JsonNode) ApiService.getMetadata("...", "json", "json", "pil_1",
				"pil");
		// System.out.printf("COSTRUTTORE stampa
		// %n%s%n",this.export.get(0).get("field_name").toPrettyString());
		System.out.printf("COSTRUTTORE stampa %n%s%n", this.export.get(0).toPrettyString());
	}

	@Test
	void testSendSerializedJsonOfPil() throws IOException {
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
		String verifica = sanitized.get(0).get("section_header").asText();
		System.out.println(js);

		assertTrue(verifica.contains(
				"<span style=\"font-size: 12pt;\">LO SCOPO DELLA VITA (PIL)</span></p> <p><span style=\"font-weight: normal;\">"));

		String js2 = SanitizerApi.sanitizeAccenti(js);
		String sent = ApiService.setMetadata("...", "json", js2);

		System.out.printf("righe inviate: %n%s%n%n", sent);

		// TEST FALLISCE: problema il JSON contiene caratteri speciali (come è, é, à,
		// etc.) che non sono correttamente codificati in UTF-8
		assertEquals("20", sent);
	}


}
