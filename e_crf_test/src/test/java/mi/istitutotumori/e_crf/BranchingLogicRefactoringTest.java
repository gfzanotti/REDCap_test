package mi.istitutotumori.e_crf;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Test per verificare che la branching_logic venga correttamente spostata nel
 * field_annotation quando contiene variabili non disponibili nel progetto.
 */
class BranchingLogicRefactoringTest {

	@Test
	void testBranchingLogicWithAvailableVariables() {
		// Creiamo un field con branching logic che referenzia variabili disponibili
		Field field = new Field.FieldBuilder().fieldName("test_field").formName("test_form").fieldType("text")
				.branchingLogic("[lab_test] = '1' and [record_id] = '123'").build();

		// Variabili disponibili nel progetto
		List<String> availableVars = List.of("record_id", "lab_test", "hlt_date");

		// Validiamo la branching logic
		Field validatedField = field.validateBranchingLogic(availableVars);

		// La branching logic dovrebbe rimanere nel campo branchingLogic
		assertEquals("[lab_test] = '1' and [record_id] = '123'", validatedField.getBranchingLogic());
		assertEquals("", validatedField.getFieldAnnotation());
	}

	@Test
	void testBranchingLogicWithMissingVariables() {
		// Creiamo un field con branching logic che referenzia variabili NON disponibili
		Field field = new Field.FieldBuilder().fieldName("test_field").formName("test_form").fieldType("text")
				.branchingLogic("[missing_var1] = '1' and [record_id] = '123'").build();

		// Variabili disponibili nel progetto (missing_var1 NON è disponibile)
		List<String> availableVars = List.of("record_id", "lab_test", "hlt_date");

		// Validiamo la branching logic
		Field validatedField = field.validateBranchingLogic(availableVars);

		// La branching logic dovrebbe essere spostata nel field_annotation
		assertEquals("", validatedField.getBranchingLogic());
		assertTrue(validatedField.getFieldAnnotation().contains("[missing_var1] = '1' and [record_id] = '123'"));
	}

	@Test
	void testBranchingLogicWithMissingVariablesAndExistingAnnotation() {
		// Creiamo un field con branching logic non valida E annotation già presente
		Field field = new Field.FieldBuilder().fieldName("test_field").formName("test_form").fieldType("text")
				.branchingLogic("[missing_var1] = '1'").fieldAnnotation("Existing annotation").build();

		// Variabili disponibili nel progetto
		List<String> availableVars = List.of("record_id", "lab_test");

		// Validiamo la branching logic
		Field validatedField = field.validateBranchingLogic(availableVars);

		// La branching logic dovrebbe essere spostata nel field_annotation e mergiata
		assertEquals("", validatedField.getBranchingLogic());
		assertTrue(validatedField.getFieldAnnotation().contains("Existing annotation"));
		assertTrue(validatedField.getFieldAnnotation().contains("[missing_var1] = '1'"));
		assertTrue(validatedField.getFieldAnnotation().contains("&"));
	}

	@Test
	void testBranchingLogicWithEventNameVariable() {
		// La variabile "event-name" è una variabile speciale che non blocca
		Field field = new Field.FieldBuilder().fieldName("test_field").formName("test_form").fieldType("text")
				.branchingLogic("[event-name] = 'c1_arm_1' and [lab_test] = '1'").build();

		// Variabili disponibili nel progetto (event-name NON è nella lista)
		List<String> availableVars = List.of("record_id", "lab_test", "hlt_date");

		// Validiamo la branching logic
		Field validatedField = field.validateBranchingLogic(availableVars);

		// La branching logic dovrebbe rimanere perché event-name è una variabile non
		// bloccante
		assertEquals("[event-name] = 'c1_arm_1' and [lab_test] = '1'", validatedField.getBranchingLogic());
		assertEquals("", validatedField.getFieldAnnotation());
	}

	@Test
	void testProjectLevelValidation() {
		// Creiamo un progetto con più instruments
		InstrumentsProjBuilder projBuilder = new InstrumentsProjBuilder();

		// Aggiungiamo un instrument con dei field
		StandardInstrument inst = new StandardInstrument("test_instrument");

		Field field1 = new Field.FieldBuilder().fieldName("record_id").formName("form_1").fieldType("text").build();

		Field field2 = new Field.FieldBuilder().fieldName("lab_test").formName("form_1").fieldType("yesno")
				.branchingLogic("[record_id] = '1'") // Valido: record_id esiste
				.build();

		Field field3 = new Field.FieldBuilder().fieldName("alert_field").formName("form_1").fieldType("descriptive")
				.branchingLogic("[missing_field] = '1'") // Non valido: missing_field non esiste
				.build();

		inst.addField(field1);
		inst.addField(field2);
		inst.addField(field3);

		projBuilder.addInstrument(inst);

		// Costruiamo il progetto (dovrebbe validare la branching logic)
		InstrumentsProj project = projBuilder.build();

		// Verifichiamo i risultati
		List<Field> fields = project.getInstruments().get(0).getFields();

		// field2 dovrebbe avere branching_logic valida
		assertEquals("[record_id] = '1'", fields.get(1).getBranchingLogic());

		// field3 dovrebbe avere branching_logic vuota e testo nel field_annotation
		assertEquals("", fields.get(2).getBranchingLogic());
		assertTrue(fields.get(2).getFieldAnnotation().contains("[missing_field] = '1'"));
	}
}
