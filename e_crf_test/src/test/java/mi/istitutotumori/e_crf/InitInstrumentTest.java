package mi.istitutotumori.e_crf;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class InitInstrumentTest {
	@Test
	void testSingletonInstanceCreation() {
		InitInstrument inst1 = InitInstrument.getInit();
		InitInstrument inst2 = InitInstrument.getInit();

		// Verifica che l'istanza non sia null
		assertNotNull(inst1, "L'istanza non dovrebbe essere null");

		// Verifica che sia un singleton
		assertSame(inst1, inst2, "Le due istanze devono essere la stessa (singleton)");
	}

	@Test
	void testFieldsInitialization() {
		InitInstrument inst = InitInstrument.getInit();

		assertNotNull(inst.getFields(), "La lista dei campi non dovrebbe essere null");
		assertFalse(inst.getFields().isEmpty(), "La lista dei campi dovrebbe contenere almeno un elemento");

		// opzionale: verifica che il primo campo sia quello atteso
		Field f = inst.getFields().get(0);
		assertNotNull(f, "Il primo campo non dovrebbe essere null");
	}

	@Test
	void testName() {
		InitInstrument inst = InitInstrument.getInit();
		assertEquals("Record ID", inst.getName(), "Il nome dello strumento non è quello atteso");
	}

	@Test
	void testToString() {
		InitInstrument inst = InitInstrument.getInit();
		String s = inst.toString();
		System.out.print(s);

		assertNotNull(s, "toString() non dovrebbe restituire null");
		assertTrue(s.contains("Record ID"), "toString() dovrebbe contenere il nome dello strumento");
		assertTrue(s.contains("fields"), "toString() dovrebbe contenere la lista dei campi");
	}

}
