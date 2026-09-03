package mi.istitutotumori.e_crf.utils;

import java.util.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import mi.istitutotumori.e_crf.*;

public class InstrumentCsvBuilder {

	private static final Logger log = LogManager.getLogger("bl_Logger");

	private static final List<String> COLUMNS = List.of("field_name", "form_name", "section_header", "field_type",
			"field_label", "select_choices_or_calculations", "field_note", "text_validation_type_or_show_slider_number",
			"text_validation_min", "text_validation_max", "identifier", "branching_logic", "required_field",
			"custom_alignment", "question_number", "matrix_group_name", "matrix_ranking", "field_annotation");

	private final Set<String> setEliminato = new HashSet<>();

	public Set<String> getSetEliminato() {
		return Collections.unmodifiableSet(setEliminato);
	}

	public List<List<String>> build(List<AbstractInstrument> instruments) {

		log.info("Inizio tracciamento Branching Logic BLOCCANTI \n (file 'InstrumentCsvBuilder.java')");

		List<List<String>> table = new ArrayList<>();
		table.add(COLUMNS);

		// reset accumulated eliminated variables for this build run
		setEliminato.clear();

		// build a global list of all variable names across the entire project (kept for
		// compatibility)
		List<String> globalVars = instruments.stream().flatMap(i -> i.getVariableNameSet().stream()).distinct()
				.collect(java.util.stream.Collectors.toList());

		for (AbstractInstrument inst : instruments) {
			log.info("Instrument: {}", inst.getName());
			table.addAll(buildInstrumentRows(inst));
		}
		return table;
	}

	private List<List<String>> buildInstrumentRows(AbstractInstrument inst) {

		List<List<String>> rows = new ArrayList<>();

		for (Field f : inst.getFields()) {

			log.debug("Field: {}", f.getFieldName());

			// Assume branching logic already validated at project-level. Use current field
			// values.
			List<String> row = List.of(f.getFieldName(), f.getFormName(), f.getSectionHeader(), f.getFieldType(),
					f.getFieldLabel(), f.getSelectChoicesOrCalculations(), f.getFieldNote(), f.getTextValidationEG(),
					f.getTextValidationMin(), f.getTextValidationMax(), f.getIdentifier(), f.getBranchingLogic(),
					f.getRequiredField(), f.getCustomAlignment(), f.getQuestionNumber(), f.getMatrixGroupName(),
					f.getMatrixRanking(), f.getFieldAnnotation());

			rows.add(row);
		}

		return rows;
	}

	private String mergeAnnotation(String base, String extra) {
		if (extra == null || extra.isBlank())
			return base;
		if (base == null || base.isBlank())
			return extra;
		return base + " & " + extra;
	}

	/**
	 * Imposta il set delle variabili eliminate (report) generato esternamente dalla
	 * validazione del progetto. Viene usato da facades come SerializeInst2CsvString
	 * per esporre il report.
	 */
	public void setRemovedPerInstrument(Map<String, Set<String>> removedPerInstrument) {
		setEliminato.clear();
		if (removedPerInstrument == null || removedPerInstrument.isEmpty())
			return;
		removedPerInstrument.values().forEach(s -> setEliminato.addAll(s));
	}
}
