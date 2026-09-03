package mi.istitutotumori.e_crf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.io.StringReader;

import com.fasterxml.jackson.databind.JsonNode;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;

/**
 * Classe per la costruzione degli "Instrument Standard".
 */
public class StandardInstrument implements AbstractInstrument {
	// definisco le variabili di "servizio"
	// per il logger che mi traccia le strutture dati
	// private static final Logger dataStructLog =
	// LogManager.getLogger("dataStructWL_Logger");
	private String name;
	private final List<Field> fields = new ArrayList<>();

	/* Costruttori */
	public StandardInstrument() {
	}

	StandardInstrument(String name) {
		this.name = name;
	}

	StandardInstrument(String name, JsonNode jsonNode) {
		this.name = name;
		/*
		 * ObjectMapper mapper = new ObjectMapper(); String json =
		 * mapper.writeValueAsString(jsonNode); List<Field> fields =
		 * mapper.readValue(json, new TypeReference<List<Field>>() {});
		 */

//				fields.forEach(f -> {
//					if (f.getBranchingLogic() != null) {
//						f.setBranchingLogic(f.getBranchingLogic().replace("\n", " ").replace("\r", " ").trim());
//					}
//					if (f.getFieldLabel() != null) {
//						f.setFieldLabel(f.getFieldLabel().replace("\n", " ").replace("\r", " ").trim());
//					}
//					if (f.getSelectChoicesOrCalculations() != null) {
//						f.setSelectChoicesOrCalculations(f.getSelectChoicesOrCalculations().replace("\n", " ").replace("\r", " ").trim());
//					}
//					// ... ripeti per gli altri campi stringa se serve
//				});

		// this.addListField(fields);

//List<Field> fieldList = new ArrayList<>();

		if (jsonNode != null && jsonNode.isArray()) {
			for (JsonNode fieldNode : jsonNode) {
				Field f = new Field.FieldBuilder().fieldName(getNodeText(fieldNode, "field_name"))
						.formName(getNodeText(fieldNode, "form_name"))
						.sectionHeader(getNodeText(fieldNode, "section_header"))
						.fieldType(getNodeText(fieldNode, "field_type"))
						.fieldLabel(getNodeText(fieldNode, "field_label"))
						.selectChoices(getNodeText(fieldNode, "select_choices_or_calculations"))
						.fieldNote(getNodeText(fieldNode, "field_note"))
						.textValidationEG(getNodeText(fieldNode, "text_validation_type_or_show_slider_number"))
						.textValidationMin(getNodeText(fieldNode, "text_validation_min"))
						.textValidationMax(getNodeText(fieldNode, "text_validation_max"))
						.identifier(getNodeText(fieldNode, "identifier"))
						.branchingLogic(getNodeText(fieldNode, "branching_logic"))
						.requiredField(getNodeText(fieldNode, "required_field"))
						.customAlignment(getNodeText(fieldNode, "custom_alignment"))
						.questionNumber(getNodeText(fieldNode, "question_number"))
						.matrixGroupName(getNodeText(fieldNode, "matrix_group_name"))
						.matrixRanking(getNodeText(fieldNode, "matrix_ranking"))
						.fieldAnnotation(getNodeText(fieldNode, "field_annotation")).build();

				// fieldList.add(field);
				this.fields.add(f);
				// this.addListField(fields);
			}
		}

	}

	private String getNodeText(JsonNode node, String fieldName) {
		if (node == null || !node.has(fieldName) || node.get(fieldName).isNull()) {
			return "";
		}
		return node.get(fieldName).asText();
	}

	StandardInstrument(String name, String csv) throws IOException, CsvValidationException {

		this.name = name;

		// List<Field> fields = new ArrayList<>();
		String normalizedCsv = csv.replace("\r\n", "\n").replace("\r", "\n");

		try (CSVReader reader = new CSVReaderBuilder(new StringReader(normalizedCsv)).build()) {

			String[] header = reader.readNext();

			if (header.length > 0 && header[0].startsWith("\uFEFF")) {
				header[0] = header[0].replace("\uFEFF", "");
			}

			String[] riga;
			while ((riga = reader.readNext()) != null) {
				Field.FieldBuilder b = new Field.FieldBuilder();

				for (int i = 0; i < riga.length; i++) {
					String valore = riga[i];
					switch (header[i]) {
					case "field_name" -> b.fieldName(valore);
					case "form_name" -> b.formName(valore);
					case "section_header" -> b.sectionHeader(valore);
					case "field_type" -> b.fieldType(valore);
					case "field_label" -> b.fieldLabel(valore);
					case "select_choices_or_calculations" -> b.selectChoices(valore);
					case "field_note" -> b.fieldNote(valore);
					case "text_validation_type_or_show_slider_number" -> b.textValidationEG(valore);
					case "text_validation_min" -> b.textValidationMin(valore);
					case "text_validation_max" -> b.textValidationMax(valore);
					case "identifier" -> b.identifier(valore);
					case "branching_logic" -> b.branchingLogic(valore);
					case "required_field" -> b.requiredField(valore);
					case "custom_alignment" -> b.customAlignment(valore);
					case "question_number" -> b.questionNumber(valore);
					case "matrix_group_name" -> b.matrixGroupName(valore);
					case "matrix_ranking" -> b.matrixRanking(valore);
					case "field_annotation" -> b.fieldAnnotation(valore);
					default -> {
					}
					}
				}

				this.fields.add(b.build());
			}
		}

		// this.addListField(fields);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public List<Field> getFields() {
		return this.fields;
	}

	@Override
	public String toString() {
		return "Instrument :   ***  %s  ***;%n %s".formatted(getName(), fields);
	}

} // end class