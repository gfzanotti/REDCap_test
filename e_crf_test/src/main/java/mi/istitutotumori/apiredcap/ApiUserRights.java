package mi.istitutotumori.apiredcap;

public class ApiUserRights extends AbstractApiRedcap {
	private static final String STANDARD_TOOLS = "..."; // "eCRF tools"

	private ApiUserRights(String token, String content, String action, String format, String returnFormat,
			String fields, String forms, String data) {
		// per gli altri parametri che non servono, puoi passare `null`
		super(token, content, // content,
				action, // action
				format, data, // data
				null, // type
				null, // csvDelimiter
				null, // records
				fields, // fields
				forms, // forms "survey_lab_data" "survey_introduzione"
				null, // rawOrLabel
				null, // export CheckboxLabel
				null, // rawOrLabelHeaders
				null, // exportSurveyFields
				null, // exportDataAccessGroups
				returnFormat);
	}

	ApiUserRights(String token, String content, String format, String data, String returnFormat) {
		// per gli altri parametri che non servono, puoi passare `null`
		this(token, content, null, "json", "json", null, null, data);
	}

	/**
	 * Metodo statico per estrarre solo i "user rights" del Progetto Standard "eCRF
	 * tools" (punto di partenza per la costruzione di nuove eCRF)
	 * 
	 * @return
	 */
	static ApiUserRights userRights(String content, String token) {
		if (token == null || token == "") {
			return new ApiUserRights(STANDARD_TOOLS, content, null, "json", "json", null, null, null);
		} else {
			return new ApiUserRights(token, content, null, "json", "json", null, null, null);
		}

	}

}
