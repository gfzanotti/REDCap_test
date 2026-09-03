package mi.istitutotumori.e_crf;

import java.util.List;

/*
 * Interfaccia per definire gli Strumenti (Instruments)
 * Provo ad utilizzare i metodi di default previsti da Java 8 in poi
 */
public interface AbstractInstrument {

	String getName();

	List<Field> getFields();

	default void addField(Field f) {
		getFields().add(f);
	}

	default void addListField(List<Field> fs) {
		getFields().addAll(fs);
	}

	default Field getField(String fieldName) {
		return getFields().stream().filter(f -> f.getFieldName().equals(fieldName)).findFirst().orElse(null);
	}

	default List<String> getVariableNameSet() {
		return getFields().stream().map(Field::getFieldName).toList();
	}

	/**
	 * Valida la branching_logic di tutti i field dell'instrument rispetto alle
	 * variabili disponibili. Se la branching_logic contiene variabili non
	 * disponibili, viene spostata nel field_annotation.
	 * 
	 * @param availableVariables lista di nomi di variabili disponibili nel progetto
	 */
	/*
	 * default void validateAllBranchingLogic(List<String> availableVariables) { if
	 * (availableVariables == null || availableVariables.isEmpty()) { return; }
	 * 
	 * List<Field> fields = getFields(); for (int i = 0; i < fields.size(); i++) {
	 * Field original = fields.get(i); Field validated =
	 * original.validateBranchingLogic(availableVariables);
	 * 
	 * // Se il field è stato modificato, sostituiscilo nella lista if (validated !=
	 * original) { fields.set(i, validated); } } }
	 */
}
// end class