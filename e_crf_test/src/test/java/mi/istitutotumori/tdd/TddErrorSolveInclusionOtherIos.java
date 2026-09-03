package mi.istitutotumori.tdd;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Disabled;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import mi.istitutotumori.apiredcap.ApiService;
import mi.istitutotumori.e_crf.AbstractInstrument;
import mi.istitutotumori.e_crf.InstrumentFactory;
import mi.istitutotumori.e_crf.StandardInstrument;
import mi.istitutotumori.e_crf.sanitize.SanitizerApi;
import mi.istitutotumori.e_crf.utils.SerializeInst2CsvString;
import mi.istitutotumori.e_crf.utils.SerializeInst2JsonString;

/**
 * ERRORE da verificare: presenza di un carattere <b>"?"</b> al posto di uno
 * <b>spazio</b>, nella deserializzazione, in questa sottostringa del field
 * <i>"field_label"</i> -> <b><\p style="text-align: center;"> <\/p></b>
 */
@Disabled("requires REDCap API")
class TddErrorSolveInclusionOtherIos implements SanitizerApi {
	JsonNode export;

	TddErrorSolveInclusionOtherIos() throws Exception {
		this.export = (JsonNode) ApiService.getMetadata("...", "json", "json", null,
				"inclusion_of_others_ios");
		System.out.printf("COSTRUTTORE stampa %n%s%n", this.export.toPrettyString());
	}

	@Test
	void testSendSerializedJsonOfIos() throws IOException {
		// serializzo il JsonNode
		JsonNode sanitized = SanitizerApi.sanitizeForRedcap(this.export);
		String js = new ObjectMapper().writeValueAsString(sanitized);
		System.out.printf("JS is %n%s", js);

		// ATTENZIONE js è un array Json, prima devo selezionare l'indice
		// stampa diagnostica caratteri unicode
		// System.out.println(js.codePoints().mapToObj(cp -> String.format("U+%04X",
		// cp)).toList());

		String verifica = sanitized.get(0).get("field_label").asText();
		// System.out.println(verifica);

		assertTrue(verifica.contains("<p style=\"text-align: center;\"> </p>"));

		String sent = ApiService.setMetadata("...", "json", js);
		// System.out.printf("righe inviate: %n%s%n%n", sent);

		assertEquals("1", sent);
	}

	@Test
	void passoDaStandardInstSerializzoJson() throws Exception {
		// StandardInstrument std = new StandardInstrument("IOS", this.export);
		StandardInstrument std = (StandardInstrument) InstrumentFactory.createInstrument("std", "IOS", this.export);

		System.out.printf("passoDaStandardInstSerializzoJson 1 stampa %n%s%n%n", std.getField("ios").getFieldLabel());

		List<AbstractInstrument> list = List.of(std);
		String jsSend = SerializeInst2JsonString.serializeMetadata(list);
		System.out.printf("passoDaStandardInstSerializzoJson 2 stampa %n%s%n%n", jsSend);

		String sent2 = ApiService.setMetadata("...", "json", jsSend);
		System.out.printf("passoDaStandardInstSerializzoJson righe inviate: %n%s%n%n", sent2);
		// NON FUNZIONA IL PASSAGGIO A STRING AVVIENE SENZA SANITIZED

	}

	@Test
	void passoDaStandardInstSerializzoCsv() throws Exception {
		// StandardInstrument std = new StandardInstrument("IOS", this.export);
		StandardInstrument std = (StandardInstrument) InstrumentFactory.createInstrument("std", "IOS", this.export);

		System.out.printf("passoDaStandardInstSerializzoCsv 1 stampa %n%s%n%n", std.getField("ios").getFieldLabel());

		SerializeInst2CsvString newObj = new SerializeInst2CsvString();

		System.out.printf("passoDaStandardInstSerializzoCsv 2 stampa %n%s%n%n", newObj.toString());

		String sent2 = ApiService.setMetadata("...", "csv", newObj.toCsvString(null));
		System.out.printf("passoDaStandardInstSerializzoCsv righe inviate: %n%s%n%n", sent2);
		// NON FUNZIONA IL PASSAGGIO A STRING AVVIENE SENZA SANITIZED

	}

}
