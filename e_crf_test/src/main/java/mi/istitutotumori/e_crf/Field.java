package mi.istitutotumori.e_crf;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;

import mi.istitutotumori.e_crf.labutils.ConstData;
import mi.istitutotumori.e_crf.sanitize.SanitizerApi;
import mi.istitutotumori.e_crf.utils.BranchingLogicValidator;
import mi.istitutotumori.tracing.ExcludeTrace;

/**
 * Classe "Field" che copia la costruzione logica dei "field" di REDCap è
 * presente la mappatura tra i nomi usati da REDCap e quelli usati in questo
 * codice
 */
public final class Field implements SanitizerApi {
	@JsonProperty("field_name")
	private final String fieldName;

	@JsonProperty("form_name")
	private final String formName;

	@JsonProperty("section_header")
	private final String sectionHeader;

	@JsonProperty("field_type")
	private final String fieldType;

	@JsonProperty("field_label")
	private final String fieldLabel;

	@JsonProperty("select_choices_or_calculations")
	private final String selectChoicesOrCalculations;

	@JsonProperty("field_note")
	private final String fieldNote;

	@JsonProperty("text_validation_type_or_show_slider_number")
	private final String textValidationEG;

	@JsonProperty("text_validation_min")
	private final String textValidationMin;

	@JsonProperty("text_validation_max")
	private final String textValidationMax;

	@JsonProperty("identifier")
	private final String identifier;

	@JsonProperty("branching_logic")
	private final String branchingLogic;

	@JsonProperty("required_field")
	private final String requiredField;

	@JsonProperty("custom_alignment")
	private final String customAlignment;

	@JsonProperty("question_number")
	private final String questionNumber;

	@JsonProperty("matrix_group_name")
	private final String matrixGroupName;

	@JsonProperty("matrix_ranking")
	private final String matrixRanking;

	@JsonProperty("field_annotation")
	private final String fieldAnnotation;

	private Field(FieldBuilder b) {
		this.fieldName = b.fieldName;
		this.formName = b.formName;
		this.sectionHeader = sanitize(b.sectionHeader);
		// this.sectionHeader = b.sectionHeader;
		this.fieldType = b.fieldType;
		this.fieldLabel = sanitize(b.fieldLabel);
		this.selectChoicesOrCalculations = sanitize(b.selectChoicesOrCalculations);
		// this.fieldLabel = b.fieldLabel;
		// this.selectChoicesOrCalculations = b.selectChoicesOrCalculations;
		this.fieldNote = b.fieldNote;
		this.textValidationEG = b.textValidationEG;
		this.textValidationMin = b.textValidationMin;
		this.textValidationMax = b.textValidationMax;
		this.identifier = b.identifier;
		this.branchingLogic = b.branchingLogic;
		this.requiredField = b.requiredField;
		this.customAlignment = b.customAlignment;
		this.questionNumber = b.questionNumber;
		this.matrixGroupName = b.matrixGroupName;
		this.matrixRanking = b.matrixRanking;
		this.fieldAnnotation = b.fieldAnnotation;
	}

	// ### VERSIONE LabInstrument ###
	@ExcludeTrace(reason = "NON utile")
	static Field htmlNestedTable(String fieldName, String html, String overrideFormName) {
		String formName = overrideFormName != null ? overrideFormName : "laboratory_data";
		return new FieldBuilder().fieldName(fieldName).formName(formName).fieldType("descriptive").fieldLabel(html)
				.branchingLogic("[lab_test]='1'").build();
	}

	@ExcludeTrace(reason = "NON utile")
	static Field fromLabItem(List<String> item, String sqlRout, String sqlItem, String overrideFormName) {
		String formName = overrideFormName != null ? overrideFormName : "laboratory_data";

		FieldBuilder b = new FieldBuilder().fieldName(item.get(0)).formName(formName).fieldLabel(item.get(1))
				.customAlignment("RH");

		if (item.get(0).contains("_result")) {
			b.fieldType("text");
		} else if (item.get(0).contains("rout")) {
			b.fieldType("sql").selectChoices(sqlRout);
		} else {
			b.fieldType("sql").selectChoices(sqlItem);
		}

		return b.build();
	}

	// ### VERSIONE ConfLabDataInstrument ###
	@ExcludeTrace(reason = "NON utile")
	static Field htmlNestedTableNonInt(String fieldName, String html) {
		return new FieldBuilder().fieldName(fieldName + "_de").formName("configuration_of_lab_data")
				.fieldType("descriptive").fieldLabel(html).branchingLogic("[lab_test]='1'").build();
	}

	@ExcludeTrace(reason = "NON utile")
	static Field fromLabItemNonInt(List<String> item, String sqlRout, String sqlItem) {

		FieldBuilder b = new FieldBuilder().fieldName(item.get(0)).formName("configuration_of_lab_data")
				.fieldLabel(item.get(1)).customAlignment("RH").fieldType("text");

		/*
		 * if (item.get(0).contains("_result")) { b.fieldType("text"); } else if
		 * (item.get(0).contains("rout")) { b.fieldType("sql").selectChoices(sqlRout); }
		 * else { b.fieldType("sql").selectChoices(sqlItem); }
		 */

		return b.build();
	}

	// Getters e setters
	@ExcludeTrace(reason = "NON utile")
	public String getFieldName() {
		return fieldName;
	}
	// public void setFieldName(String fieldName) {this.fieldName = fieldName; }

	@ExcludeTrace(reason = "NON utile")
	public String getFormName() {
		return formName;
	}
	// public void setFormName(String formName) {this.formName = formName; }

	@ExcludeTrace(reason = "NON utile")
	public String getSectionHeader() {
		return sectionHeader;
	}
	// public void setSectionHeader(String sectionHeader) {this.sectionHeader =
	// sanitizeString(sectionHeader); }

	@ExcludeTrace(reason = "NON utile")
	public String getFieldType() {
		return fieldType;
	}
	// public void setFieldType(String fieldType) {this.fieldType = fieldType; }

	@ExcludeTrace(reason = "NON utile")
	public String getFieldLabel() {
		return fieldLabel;
	}
	// public void setFieldLabel(String fieldLabel) {this.fieldLabel =
	// sanitizeString(fieldLabel); }

	@ExcludeTrace(reason = "NON utile")
	public String getSelectChoicesOrCalculations() {
		return selectChoicesOrCalculations;
	}
	/*
	 * public void setSelectChoicesOrCalculations(String
	 * selectChoicesOrCalculations) { this.selectChoicesOrCalculations =
	 * sanitizeString(selectChoicesOrCalculations); }
	 */

	@ExcludeTrace(reason = "NON utile")
	public String getFieldNote() {
		return fieldNote;
	}
	// public void setFieldNote(String fieldNote) {this.fieldNote = fieldNote; }

	@ExcludeTrace(reason = "NON utile")
	public String getTextValidationEG() {
		return textValidationEG;
	}
	// public void setTextValidationEG(String textValidationEG)
	// {this.textValidationEG = textValidationEG; }

	@ExcludeTrace(reason = "NON utile")
	public String getTextValidationMin() {
		return textValidationMin;
	}
	// public void setTextValidationMin(String textValidationMin)
	// {this.textValidationMin = textValidationMin; }

	@ExcludeTrace(reason = "NON utile")
	public String getTextValidationMax() {
		return textValidationMax;
	}
	// public void setTextValidationMax(String textValidationMax)
	// {this.textValidationMax = textValidationMax; }

	@ExcludeTrace(reason = "NON utile")
	public String getIdentifier() {
		return identifier;
	}
	// public void setIdentifier(String identifier) {this.identifier = identifier; }

	@ExcludeTrace(reason = "NON utile")
	public String getBranchingLogic() {
		return branchingLogic;
	}
	// public void setBranchingLogic(String branchingLogic) {this.branchingLogic =
	// branchingLogic;}

	@ExcludeTrace(reason = "NON utile")
	public String getRequiredField() {
		return requiredField;
	}
	// public void setRequiredField(String requiredField) {this.requiredField =
	// requiredField; }

	@ExcludeTrace(reason = "NON utile")
	public String getCustomAlignment() {
		return customAlignment;
	}
	// public void setCustomAlignment(String customAlignment) {this.customAlignment
	// = customAlignment; }

	@ExcludeTrace(reason = "NON utile")
	public String getQuestionNumber() {
		return questionNumber;
	}
	// public void setQuestionNumber(String questionNumber) {this.questionNumber =
	// questionNumber; }

	@ExcludeTrace(reason = "NON utile")
	public String getMatrixGroupName() {
		return matrixGroupName;
	}
	// public void setMatrixGroupName(String matrixGroupName) {this.matrixGroupName
	// = matrixGroupName; }

	@ExcludeTrace(reason = "NON utile")
	public String getMatrixRanking() {
		return matrixRanking;
	}
	// public void setMatrixRanking(String matrixRanking) {this.matrixRanking =
	// matrixRanking; }

	@ExcludeTrace(reason = "NON utile")
	public String getFieldAnnotation() {
		return fieldAnnotation;
	}
	// public void setFieldAnnotation(String fieldAnnotation) {this.fieldAnnotation
	// = fieldAnnotation; }

	/**
	 * Questo metodo mi serve per mantenere la formattazione voluta da REDCap nei
	 * valori dei campi deserializzati soprattutto nei campi particolari come
	 * "selectChoicesOrCalculations" e "fieldLabel"
	 * 
	 * @param value
	 * @return
	 */
	private static String sanitize(String value) {
		if (value == null || value.isBlank()) {
			return value;
		}

		// 1. Normalizzo i caratteri UTF-8
		// value = Normalizer.normalize(value, Normalizer.Form.NFKC);

		// 2. sostituisco NBSP reale con entità HTML
		value = value.replace("\u00A0", "&nbsp;");

		// 3. Sostituisco caratteri accentati con entità HTML
		value = SanitizerApi.sanitizeAccenti(value);

		// 4. rimuovo caratteri di controllo invisibili
		// value = value.replaceAll("\\p{C}", "") // questo l'ho tolto per il ritorno a
		// capo dell'instrumetn Nffq

		// 5. pulizia virgolette nei codici REDCap
		value = value.replaceAll("\"([^\"]+)\"\\s*,", "$1,") // "1", Yes
				.replaceAll("\\|\\s*\"([^\"]+)\"\\s*,", "| $1,") // | "0", No
				.trim();

		return value;

	}

	@Override
	public String toString() {
		// versione ridotta
		return "\t\t\t\t\t  *** NewField *** [\nfieldName =" + fieldName + "\n, formName =" + formName
				+ "\n, fieldType =" + fieldType + "\n, fieldLabel =" + fieldLabel + "\n, selectChoicesOrCalculations ="
				+ selectChoicesOrCalculations + "\n, fieldNote =" + fieldNote + "\n, textValidationEG ="
				+ textValidationEG + "\n, textValidationMin =" + textValidationMin + "\n, branchingLogic ="
				+ branchingLogic + "]\n";

		/*
		 * // versione completa return "RedcapField [fieldName =" + fieldName +
		 * ", formName =" + formName + ", sectionHeader =" + sectionHeader +
		 * ", fieldType =" + fieldType + ", fieldLabel =" + fieldLabel +
		 * ", selectChoicesOrCalculations =" + selectChoicesOrCalculations +
		 * ", fieldNote =" + fieldNote + ", textValidationEG =" + textValidationEG +
		 * ", textValidationMin =" + textValidationMin + ", textValidationMax =" +
		 * textValidationMax + ", identifier =" + identifier + ", branchingLogic =" +
		 * branchingLogic + ", requiredField =" + requiredField + ", customAlignment ="
		 * + customAlignment + ", questionNumber =" + questionNumber +
		 * ", matrixGroupName =" + matrixGroupName + ", matrixRanking =" + matrixRanking
		 * + ", fieldAnnotation =" + fieldAnnotation + "]";
		 */
	}

	/**
	 * Metodo factory che restituisce una copia del Field con la branching_logic
	 * validata rispetto alle variabili disponibili. Se la branching_logic contiene
	 * variabili non disponibili, viene spostata nel field_annotation.
	 */
	public Field validateBranchingLogic(List<String> availableVariables) {
		if (availableVariables == null || branchingLogic.isBlank()) {
			return this;
		}

		BranchingLogicValidator validator = new BranchingLogicValidator();
		var result = validator.validate(availableVariables, branchingLogic);

		// Se la branching_logic è già valida, non fare nulla
		if (!result.logic().isEmpty() && result.logic().equals(branchingLogic)) {
			return this;
		}

		// La branching_logic non è valida, crea una copia con i campi corretti
		return new FieldBuilder().fieldName(this.fieldName).formName(this.formName).sectionHeader(this.sectionHeader)
				.fieldType(this.fieldType).fieldLabel(this.fieldLabel).selectChoices(this.selectChoicesOrCalculations)
				.fieldNote(this.fieldNote).textValidationEG(this.textValidationEG)
				.textValidationMin(this.textValidationMin).textValidationMax(this.textValidationMax)
				.identifier(this.identifier).branchingLogic(result.logic()) // branching logic validata (può essere
																			// vuota)
				.requiredField(this.requiredField).customAlignment(this.customAlignment)
				.questionNumber(this.questionNumber).matrixGroupName(this.matrixGroupName)
				.matrixRanking(this.matrixRanking)
				.fieldAnnotation(mergeAnnotation(this.fieldAnnotation, result.annotationExtra())).build();
	}

	/**
	 * Utility per mergere due annotazioni
	 */
	private static String mergeAnnotation(String base, String extra) {
		if (extra == null || extra.isBlank())
			return base;
		if (base == null || base.isBlank())
			return extra;
		return base + " & " + extra;
	}

	/**
	 * Se la branchingLogic è vuota ma l'annotation contiene la logica originale
	 * (es. è stata precedentemente spostata lì), questo metodo restituisce una
	 * copia del Field con la branchingLogic ripristinata prendendo l'ultima
	 * porzione dell'annotation che contiene espressioni REDCap (contiene '['). Se
	 * non c'è nulla da ripristinare, ritorna this.
	 */
	public Field restoreBranchingFromAnnotation() {
		if (branchingLogic != null && !branchingLogic.isBlank())
			return this;
		if (fieldAnnotation == null || fieldAnnotation.isBlank())
			return this;
		String ann = fieldAnnotation.trim();

		// If the entire annotation looks like a branching logic, restore it entirely.
		if (isLikelyBranchingLogic(ann)) {
			return new FieldBuilder().fieldName(this.fieldName).formName(this.formName)
					.sectionHeader(this.sectionHeader).fieldType(this.fieldType).fieldLabel(this.fieldLabel)
					.selectChoices(this.selectChoicesOrCalculations).fieldNote(this.fieldNote)
					.textValidationEG(this.textValidationEG).textValidationMin(this.textValidationMin)
					.textValidationMax(this.textValidationMax).identifier(this.identifier).branchingLogic(ann)
					.requiredField(this.requiredField).customAlignment(this.customAlignment)
					.questionNumber(this.questionNumber).matrixGroupName(this.matrixGroupName)
					.matrixRanking(this.matrixRanking).fieldAnnotation("").build();
		}

		// split by delimiter used in mergeAnnotation and try to extract last candidate
		// containing '['
		String[] parts = ann.split("\\s*&\\s*");
		int idxCandidate = -1;
		for (int i = parts.length - 1; i >= 0; i--) {
			if (parts[i].contains("[")) {
				idxCandidate = i;
				break;
			}
		}

		if (idxCandidate == -1)
			return this;

		String candidate = parts[idxCandidate].trim();

		// rebuild annotation without the candidate part
		StringBuilder cleaned = new StringBuilder();
		for (int i = 0; i < parts.length; i++) {
			if (i == idxCandidate)
				continue;
			if (cleaned.length() > 0)
				cleaned.append(" & ");
			cleaned.append(parts[i].trim());
		}

		String newAnnotation = cleaned.length() == 0 ? "" : cleaned.toString();

		return new FieldBuilder().fieldName(this.fieldName).formName(this.formName).sectionHeader(this.sectionHeader)
				.fieldType(this.fieldType).fieldLabel(this.fieldLabel).selectChoices(this.selectChoicesOrCalculations)
				.fieldNote(this.fieldNote).textValidationEG(this.textValidationEG)
				.textValidationMin(this.textValidationMin).textValidationMax(this.textValidationMax)
				.identifier(this.identifier).branchingLogic(candidate).requiredField(this.requiredField)
				.customAlignment(this.customAlignment).questionNumber(this.questionNumber)
				.matrixGroupName(this.matrixGroupName).matrixRanking(this.matrixRanking).fieldAnnotation(newAnnotation)
				.build();
	}

	private static boolean isLikelyBranchingLogic(String s) {
		if (s == null || s.isBlank())
			return false;
		// must contain at least one variable reference
		if (!s.contains("[") || !s.contains("]"))
			return false;
		String lower = s.toLowerCase();
		// common operators in branching logic
		if (lower.contains("=") || lower.contains(">") || lower.contains("<") || lower.contains("!="))
			return true;
		if (lower.contains(" and ") || lower.contains(" or ") || lower.contains("not "))
			return true;
		// fallback: presence of bracketed vars is a good hint
		return true;
	}

	@ExcludeTrace(reason = "NON utile")
	static class FieldBuilder {
		// default sicuri
		private String fieldName = "";
		private String formName = "";
		private String sectionHeader = "";
		private String fieldType = "";
		private String fieldLabel = "";
		private String selectChoicesOrCalculations = "";
		private String fieldNote = "";
		private String textValidationEG = "";
		private String textValidationMin = "";
		private String textValidationMax = "";
		private String identifier = "";
		private String branchingLogic = "";
		private String requiredField = "";
		private String customAlignment = "";
		private String questionNumber = "";
		private String matrixGroupName = "";
		private String matrixRanking = "";
		private String fieldAnnotation = "";

		// Per la validazione della branching logic
		private List<String> availableVariables = null;

		FieldBuilder fieldName(String s) {
			this.fieldName = s;
			return this;
		}

		FieldBuilder formName(String s) {
			this.formName = s;
			return this;
		}

		FieldBuilder sectionHeader(String s) {
			this.sectionHeader = s;
			return this;
		}

		FieldBuilder fieldType(String s) {
			this.fieldType = s;
			return this;
		}

		FieldBuilder fieldLabel(String s) {
			this.fieldLabel = s;
			return this;
		}

		FieldBuilder selectChoices(String s) {
			this.selectChoicesOrCalculations = s;
			return this;
		}

		FieldBuilder fieldNote(String s) {
			this.fieldNote = s;
			return this;
		}

		FieldBuilder textValidationEG(String s) {
			this.textValidationEG = s;
			return this;
		}

		FieldBuilder textValidationMin(String s) {
			this.textValidationMin = s;
			return this;
		}

		FieldBuilder textValidationMax(String s) {
			this.textValidationMax = s;
			return this;
		}

		FieldBuilder identifier(String s) {
			this.identifier = s;
			return this;
		}

		FieldBuilder branchingLogic(String s) {
			this.branchingLogic = s;
			return this;
		}

		FieldBuilder requiredField(String s) {
			this.requiredField = s;
			return this;
		}

		FieldBuilder customAlignment(String s) {
			this.customAlignment = s;
			return this;
		}

		FieldBuilder questionNumber(String s) {
			this.questionNumber = s;
			return this;
		}

		FieldBuilder matrixGroupName(String s) {
			this.matrixGroupName = s;
			return this;
		}

		FieldBuilder matrixRanking(String s) {
			this.matrixRanking = s;
			return this;
		}

		FieldBuilder fieldAnnotation(String s) {
			this.fieldAnnotation = s;
			return this;
		}

		/**
		 * Imposta le variabili disponibili per la validazione della branching logic. Se
		 * impostate, la branching logic verrà validata durante il build(). Se contiene
		 * variabili non disponibili, verrà spostata nel field_annotation.
		 */
		FieldBuilder withAvailableVariables(List<String> variables) {
			this.availableVariables = variables;
			return this;
		}

		Field build() {
			if (fieldName == null || fieldName.isBlank()) {
				throw new IllegalStateException("fieldName is mandatory");
			}

			// Valida la branching logic se le variabili disponibili sono state impostate
			if (availableVariables != null && !branchingLogic.isBlank()) {
				BranchingLogicValidator validator = new BranchingLogicValidator();
				var result = validator.validate(availableVariables, branchingLogic);

				// Se la branching logic non è valida, spostala nel field_annotation
				if (!result.logic().isEmpty()) {
					// La branching logic è valida, mantienila
					branchingLogic = result.logic();
				} else {
					// La branching logic non è valida, spostala nel field_annotation
					if (!result.annotationExtra().isBlank()) {
						// Merge con annotation esistente
						if (fieldAnnotation != null && !fieldAnnotation.isBlank()) {
							fieldAnnotation = fieldAnnotation + " & " + result.annotationExtra();
						} else {
							fieldAnnotation = result.annotationExtra();
						}
						branchingLogic = "";
					}
				}
			}

			return new Field(this);
		}
	} // end builder

	/**
	 * Metodo factory per la costruzione dei field da oggetto ConstData, inserisce i
	 * valori dei fields iniziali (utile nel caso dell'Instrument di laboratorio)
	 * 
	 * @param row
	 * @param overrideFormName
	 * @return dati di un "field"
	 */
	@ExcludeTrace(reason = "NON utile")
	static Field fromConstData(String row, String overrideFormName) {
		ConstData c = new ConstData();

		String formName = overrideFormName != null ? overrideFormName : c.firstRows.get("form_name").get(row).asText();
		return new FieldBuilder().fieldName(c.firstRows.get("field_name").get(row).asText())
				// .formName(c.firstRows.get("form_name").get(row).asText())
				.formName(formName).sectionHeader(c.firstRows.get("section_header").get(row).asText())
				.fieldType(c.firstRows.get("field_type").get(row).asText())
				.fieldLabel(c.firstRows.get("field_label").get(row).asText())
				.selectChoices(c.firstRows.get("select_choices_or_calculations").get(row).asText())
				.textValidationEG(c.firstRows.get("text_validation_type_or_show_slider_number").get(row).asText())
				.branchingLogic(c.firstRows.get("branching_logic").get(row).asText())
				.customAlignment(c.firstRows.get("custom_alignment").get(row).asText()).build();
	}

}