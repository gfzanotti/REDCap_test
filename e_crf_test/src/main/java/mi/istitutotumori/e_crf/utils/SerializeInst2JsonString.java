package mi.istitutotumori.e_crf.utils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import mi.istitutotumori.e_crf.AbstractInstrument;
import mi.istitutotumori.e_crf.Field;
import mi.istitutotumori.e_crf.sanitize.SanitizerApi;
import mi.istitutotumori.tracing.ExcludeTrace;;

/**
 * Classe con metodi statici per la conversione delle strutture dati - da un
 * arrray di liste dell'instrument.getField ad una stringa con oggetti Json - da
 * un oggetto JsonNode ad un file .csv
 */
@ExcludeTrace(reason = "NON utile")
public class SerializeInst2JsonString implements SanitizerApi {

	/*
	 * Colonne richieste da REDCap private static final List<String> COLUMNS =
	 * Arrays.asList( "field_name", "form_name", "section_header", "field_type",
	 * "field_label", "select_choices_or_calculations", "field_note",
	 * "text_validation_type_or_show_slider_number", "text_validation_min",
	 * "text_validation_max", "identifier", "branching_logic", "required_field",
	 * "custom_alignment", "question_number", "matrix_group_name", "matrix_ranking",
	 * "field_annotation" );
	 */

	/**
	 * 
	 * @param instruments
	 * @return
	 * @throws Exception
	 */
	public static String serializeMetadata(List<AbstractInstrument> instruments) throws Exception {

		List<Map<String, String>> metadata = new ArrayList<>();

		for (AbstractInstrument instrument : instruments) {
			for (Field f : instrument.getFields()) {

				Map<String, String> row = new LinkedHashMap<>();
				row.put("field_name", safeClean(f.getFieldName()));
				row.put("form_name", safeClean(f.getFormName()));
				row.put("section_header", safeClean(f.getSectionHeader()));
				row.put("field_type", safeClean(f.getFieldType()));
				row.put("field_label", safeClean(f.getFieldLabel()));
				row.put("select_choices_or_calculations", safeClean(f.getSelectChoicesOrCalculations()));
				row.put("field_note", safeClean(f.getFieldNote()));
				row.put("text_validation_type_or_show_slider_number", safeClean(f.getTextValidationEG()));
				row.put("text_validation_min", safeClean(f.getTextValidationMin()));
				row.put("text_validation_max", safeClean(f.getTextValidationMax()));
				row.put("identifier", safeClean(f.getIdentifier()));
				row.put("branching_logic", safeClean(f.getBranchingLogic()));
				row.put("required_field", safeClean(f.getRequiredField()));
				row.put("custom_alignment", safeClean(f.getCustomAlignment()));
				row.put("question_number", safeClean(f.getQuestionNumber()));
				row.put("matrix_group_name", safeClean(f.getMatrixGroupName()));
				row.put("matrix_ranking", safeClean(f.getMatrixRanking()));
				row.put("field_annotation", safeClean(f.getFieldAnnotation()));

				metadata.add(row);
			}
		}

		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(SerializationFeature.INDENT_OUTPUT);

		return mapper.writeValueAsString(metadata);
	}
	/* FINE convertToJson */

	/**
	 * Applica la sanitizzazione: - converte null in "" - converte NBSP in &nbsp;
	 * (se già lo fai in cleanString)
	 */
	private static String safeClean(String value) {
		/*
		 * if (value == null) { return ""; } String cleaned =
		 * SanitizerApi.cleanString(value); return (cleaned == null) ? "" : cleaned;
		 */
		return value;
	}

}
