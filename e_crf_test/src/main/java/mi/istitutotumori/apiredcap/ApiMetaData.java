package mi.istitutotumori.apiredcap;

/**
 * L'estrazione con l'API "Export Metadata (Data Dictionary)" richiede al
 * massimo 7 parametri: - token: ... - content:
 * metadata (questo valore è fisso trattandosi della classe ApiMetadata) -
 * format: json (default anche nella superclasse) - returnFormat: json (default
 * anche nella superclasse) - fields: @param nome di un field - forms: @param
 * nome di un instrument - data: [{stringa json}] o array di righe .csv (serve
 * per l'import)
 *
 * Faccio più costruttori usando "constructor chaining"
 */
class ApiMetaData extends AbstractApiRedcap {

	private static final String TOKEN_SURVEY = ""; // "Survey eCRF"
	private static final String STANDARD_TOOLS = ""; // "eCRF tools"

	/**
	 * <b>Costruttore generale</b> con il numero di parametri massimi previsto per
	 * l'<i>estrazione dei "metadata"</i> eredita e sovrasccarica quello della
	 * classe astratta
	 * 
	 * @param token
	 * @param action
	 * @param format
	 * @param returnFormat
	 * @param fields
	 * @param forms
	 * @param data
	 */
	private ApiMetaData(String token, String action, String format, String returnFormat, String fields, String forms,
			String data) {
		// per gli altri parametri che non servono, puoi passare `null`
		super(token, "metadata", // content,
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

	/**
	 * Costruttore con 5 parametri per gli export [token, format, returnFormat,
	 * fields, forms], applico "constructor chaining" e richiamo il costruttore con
	 * 7 parametri. Mi serve per estrarre metadata di singoli "form" (Instrument),
	 * se non specifico i parametri <b>"fields" e "forms"</b> usando null ->
	 * estraggo tutto il data dictionary
	 */
	ApiMetaData(String token, String format, String returnFormat, String fields, String forms) {
		// per gli altri parametri che non servono, puoi passare `null`
		this(token, action, format, returnFormat, fields, forms, null);
	}

	/**
	 * Costruttore con 3 parametri per gli import [token, format, data], applico
	 * "constructor chaining" e richiamo il costruttore con 7 parametri. Il
	 * parametro "String data" serve per gli import
	 * 
	 * @param token
	 * @param format
	 * @param data
	 */
	ApiMetaData(String token, String format, String data) {
		// per gli altri parametri che non servono, puoi passare `null`
		this(token, "import", format, returnFormat, fields, forms, data);
	}

	// costruttore con 2 parametri [token, forms]
	// applico "constructor chaining" e richiamo il costruttore con 5 parametri
	private ApiMetaData(String token, String forms) {
		// per gli altri parametri che non servono, puoi passare `null`
		this(token, format, returnFormat, fields, forms);
	}

	/**
	 * Metodo statico per estrarre solo i "metadata" delle survey (punto di partenza
	 * per la costruzione di nuove eCRF)
	 * 
	 * @param form
	 * @return oggetto ApiMetadata
	 */
	static ApiMetaData surveyMetaData(String form) {
		return new ApiMetaData(TOKEN_SURVEY, form);
	}

	/**
	 * Metodo statico per estrarre solo i "metadata" del Progetto Standard "eCRF
	 * tools" (punto di partenza per la costruzione di nuove eCRF)
	 * 
	 * @return
	 */
	static ApiMetaData standardToolsMetaData() {
		return new ApiMetaData(STANDARD_TOOLS, null);
	}
} // end class
