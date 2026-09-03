package mi.istitutotumori.e_crf;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Disabled;

@Disabled("depends on external resources")
class LabInstrumentTest {

	AbstractInstrument lab;

	LabInstrumentTest() throws Exception {
		lab = new LabInstrument();
	}

	@Test
	void testLabInstrumentStringStringString() throws Exception {
		lab = new LabInstrument("3", "survey_lab_data", "lab exam");
		Field f = lab.getField("wbc_result");
		// System.out.println(f.toString());
		assertEquals("WBC result", f.getFieldLabel());
	}

	@Test
	void testLabInstrument() {
		Field f = lab.getField("lab_test");
		// System.out.println(f.toString());
		assertEquals("laboratory_data", f.getFormName());
	}

	@Test
	void testGetName() {
		// System.out.println(lab.getName());
		assertEquals("Hematology Instrument", lab.getName());
	}

	@Test
	void testGetFields() {
		List<Field> fs = lab.getFields();
		// System.out.println(fs.size());
		assertEquals(23, fs.size());
	}

	@Test
	void testToString() {
		assertNotNull(lab.getField("lab_test").toString());
	}

}
