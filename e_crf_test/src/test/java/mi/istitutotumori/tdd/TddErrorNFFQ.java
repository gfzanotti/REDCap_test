package mi.istitutotumori.tdd;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.io.IOException;
import java.util.List;
import java.util.stream.StreamSupport;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Disabled;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import mi.istitutotumori.apiredcap.ApiService;
import mi.istitutotumori.e_crf.AbstractInstrument;
import mi.istitutotumori.e_crf.InstrumentFactory;
import mi.istitutotumori.e_crf.sanitize.SanitizerApi;
import mi.istitutotumori.e_crf.utils.SerializeInst2CsvString;
import mi.istitutotumori.e_crf.utils.SerializeInst2JsonString;

/**
 * ERRORE da verificare: non rispetta i ritorni a capo
 */
@Disabled("requires REDCap API")
class TddErrorNFFQ implements SanitizerApi {
	JsonNode export;
	JsonNode sel;
	AbstractInstrument prova;

	TddErrorNFFQ() throws Exception {
		this.export = (JsonNode) ApiService.getMetadata("...", "json", "json", "nffq_f_1 ",
				"Nffq");
		// this.prova = new StandardInstrument("NFFQ", this.export);
		this.prova = InstrumentFactory.createInstrument("std", "NFFQ", this.export);
		this.sel = StreamSupport.stream(this.export.spliterator(), false)
				.filter(n -> "nffq_f_1".equals(n.path("field_name").asText())).findFirst().orElse(null);

		// System.out.printf("COSTRUTTORE stampa %n%s%n",this.sel.toPrettyString());
		System.out.printf("stampo dati FIELD %n%s", this.prova.getField("nffq_f_1"));
	}

	@Test
	void testSendSerializedJsonOfNFFQ() throws IOException {
		// serializzo il JsonNode
		JsonNode sanitized = SanitizerApi.sanitizeForRedcap(this.export);
		sanitized = this.export;
		// JsonNode sanitized = this.sel;
		// System.out.printf("COSTRUTTORE stampa2 %n%s%n",sanitized.toPrettyString());
		String js = new ObjectMapper().writeValueAsString(sanitized);
		// System.out.printf("Stringa da inviare %n%s%n",js);

		// ATTENZIONE js è un array Json, prima devo selezionare l'indice
		// stampa diagnostica caratteri unicode
		// System.out.println(js.codePoints().mapToObj(cp -> String.format("U+%04X",
		// cp)).toList());

		// String verifica = sanitized.get(1).get("field_label").asText();
		// System.out.printf("verifica is %n%s", verifica);

		// assertTrue(verifica.contains("(fresh, chopped, blended,
		// frozen)\nFREQUENCY\nIndicate"));

		String sent = ApiService.setMetadata("...", "json", js);
		System.out.printf("%nrighe inviate: %n%s%n%n", sent);

		assertNotEquals("1", sent);
	}

	@Test
	void passoDaStandardInstSerializzoJson() throws Exception {
		// StandardInstrument std = new StandardInstrument("IOS", this.export);
		System.out.printf("%npassoDaStandardInstSerializzoJson 1 stampa %n%s%n%n",
				this.prova.getField("nffq_f_1").getFieldLabel());

		List<AbstractInstrument> list = List.of(prova);
		String jsSend = SerializeInst2JsonString.serializeMetadata(list);
		// System.out.printf("passoDaStandardInstSerializzoJson 2 stampa
		// %n%s%n%n",jsSend);

		String sent2 = ApiService.setMetadata("...", "json", jsSend);
		System.out.printf("%npassoDaStandardInstSerializzoJson righe inviate: %n%s%n%n", sent2);
		// NON FUNZIONA IL PASSAGGIO A STRING AVVIENE SENZA SANITIZED

	}

	@Test
	void passoDaStandardInstSerializzoCsv() throws IOException {
		// StandardInstrument std = new StandardInstrument("IOS", this.export);
		System.out.printf("%npassoDaStandardInstSerializzoCsv 1 stampa %n%s%n%n",
				this.prova.getField("nffq_f_1").getFieldLabel());

		SerializeInst2CsvString newObj = new SerializeInst2CsvString();

		System.out.printf("%npassoDaStandardInstSerializzoCsv 2 stampa %n%s%n%n", newObj.toString());

		String sent2 = ApiService.setMetadata("....", "csv", newObj.toCsvString(null));
		System.out.printf("%npassoDaStandardInstSerializzoCsv righe inviate: %n%s%n%n", sent2);
		// NON FUNZIONA IL PASSAGGIO A STRING AVVIENE SENZA SANITIZED
	}
}
