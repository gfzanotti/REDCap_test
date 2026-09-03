package mi.istitutotumori.tdd;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Disabled;

import mi.istitutotumori.e_crf.InitInstrument;
import mi.istitutotumori.e_crf.InstrumentsProjBuilder;
import mi.istitutotumori.e_crf.LabInstrument;

@Disabled("placeholder test")
class TestLabInstrumentNew {
	@BeforeEach
	void setUp() throws Exception {
		// Crea direttamente InstrumentsProj usando il builder statico
		InstrumentsProjBuilder builder = new InstrumentsProjBuilder();

		// creo il primo instrument INIT
		InitInstrument init = InitInstrument.getInit();
		builder.addInstrument(init);

		// Determino quale LabInstrument creare
		LabInstrument labInstrument;

		// 20.06.2026 ho cambiato il Record ID : 1_TEST_gian (anzichè 1)
		labInstrument = new LabInstrument("1_TEST_gian", "survey_lab_data", "lab exam");

		// Aggiungi lo strumento
		builder.addInstrument(labInstrument);

		builder.build();

		// System.out.printf(init.toString());
		System.out.println("INIZIO STAMPA DATI");
		System.out.print(labInstrument);
		System.out.println("###   FINE STAMPA DATI   ###");

	}

	@Test
	void test() {
		fail("Not yet implemented");
	}

}
