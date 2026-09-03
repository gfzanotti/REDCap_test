package mi.istitutotumori.e_crf;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import mi.istitutotumori.e_crf.Field;
import mi.istitutotumori.e_crf.StandardInstrument;
import mi.istitutotumori.e_crf.utils.BranchingLogicValidator;

public class BranchingLogicValidatorProjectTest {

	@Test
	public void testValidateProject_movesBlockingBranchingLogicToAnnotation() {
		StandardInstrument inst = new StandardInstrument();

		// Field that exists (will be part of available vars)
		Field f1 = new Field.FieldBuilder().fieldName("exist_var").formName("f").fieldType("text").fieldLabel("lbl")
				.build();
		inst.addField(f1);

		// Field with branching logic referencing a non-existent variable 'missing_var'
		String bl = "[missing_var] = '1'";
		Field f2 = new Field.FieldBuilder().fieldName("f_with_bl").formName("f").fieldType("text").fieldLabel("lbl2")
				.branchingLogic(bl).build();
		inst.addField(f2);

		BranchingLogicValidator validator = new BranchingLogicValidator();
		Map<String, java.util.Set<String>> report = validator.validateProject(List.of(inst));

		// After validation the field f_with_bl should have empty branchingLogic and
		// fieldAnnotation containing the original BL
		Field updated = inst.getField("f_with_bl");
		assertNotNull(updated);
		assertTrue(updated.getBranchingLogic() == null || updated.getBranchingLogic().isBlank(),
				"branchingLogic should be empty after validation");
		assertNotNull(updated.getFieldAnnotation());
		assertTrue(
				updated.getFieldAnnotation().contains("missing_var")
						|| updated.getFieldAnnotation().contains("missing_var"),
				"annotation must contain original logic");

		// Report should contain the instrument and the removed var
		assertNotNull(report);
		assertTrue(report.containsKey(inst.getName()));
		assertTrue(report.get(inst.getName()).contains("missing_var"));
	}
}
