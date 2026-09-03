package mi.istitutotumori.e_crf;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import mi.istitutotumori.e_crf.Field.FieldBuilder;

class InstrumentListTest {

	private InstrumentsProj instrumentList = new InstrumentsProj();

	@Test
	void testAddInstrument() {

		AbstractInstrument mockInstrument = mock(AbstractInstrument.class);

		instrumentList.addInstrument(mockInstrument);

		assertEquals(1, instrumentList.getInstruments().size());
		assertTrue(instrumentList.getInstruments().contains(mockInstrument));

	}

	@Test
	void testAddInstruments() {

		AbstractInstrument i1 = mock(StandardInstrument.class);
		AbstractInstrument i2 = mock(StandardInstrument.class);

		instrumentList.addInstrument(i1);
		instrumentList.addInstrument(i2);

		assertEquals(2, instrumentList.getInstruments().size());

	}
	/*
	 * @Test void testGetInstruments() { fail("Not yet implemented"); }
	 */

	@Test
	void testGetAllFields_choiceTrue() {
		Field f1 = new FieldBuilder().fieldName("field_name").formName("form_name").sectionHeader("section_header")
				.fieldType("field_type").fieldLabel("field_label").selectChoices("select_choices_or_calculations")
				.branchingLogic("branching_logic").build();

		AbstractInstrument inst = mock(StandardInstrument.class);
		when(inst.getFields()).thenReturn(List.of(f1));

		List<Field> fields = inst.getFields();
		System.out.println(fields);

		assertEquals(1, fields.size());
		assertEquals("field_name", fields.get(0).getFieldName());
		assertEquals("form_name", fields.get(0).getFormName());
	}

	@Test
	@Disabled
	void testBuildStandardBundle() {
		// complicato da fare col mock

	}

	@Test
	void testToString() {
		Field f1 = new FieldBuilder().fieldName("field_name").formName("form_name").sectionHeader("section_header")
				.fieldType("field_type").fieldLabel("field_label").selectChoices("select_choices_or_calculations")
				.branchingLogic("branching_logic").build();

		AbstractInstrument inst = mock(StandardInstrument.class);
		when(inst.getFields()).thenReturn(List.of(f1));

		System.out.println(inst.toString());

		assertTrue(inst.toString().contains("for StandardInstrument"));

	}

}
