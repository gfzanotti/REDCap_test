package mi.istitutotumori.apiredcap;

/**
 * L'estrazione con l'API "Export Records" richiede al massimo 15 parametri: -
 * token: ... - content: record (questo valore è
 * fisso trattandosi della classe ApiRecord) - format: json (default anche nella
 * superclasse) - returnFormat: json (default anche nella superclasse) -
 * fields: @param nome di un field o anche null (meglio!) - forms: @param nome
 * di un instrument - ...
 *
 * Faccio 1 solo costruttore che prende 2 parametri [records, forms], entrambe
 * forniti con 1 solo valore. IPOTESI: il token è quello di default del progetto
 * "Survey eCRF"
 */

class ApiRecord extends AbstractApiRedcap {

	private static final String TOKEN_SURVEY = ""; // "Survey eCRF"

	/**
	 * <b>Costruttore generale</b> con il numero di parametri massimi previsto per
	 * l'<i>estrazione dei record</i> eredita e sovrasccarica quello della classe
	 * astratta
	 * 
	 * @param token
	 * @param records
	 * @param forms
	 * @param format
	 */
	private ApiRecord(String token, String records, String forms, String format) {
		// per gli altri parametri che non servono, puoi passare `null`
		super(token, content, // valore di default è "record"
				action, // valore di default è "export"
				format, // valore di default è "json"
				null, // data
				type, // valore di default è "flat"
				csvDelimiter, // valore di default è ""
				records, fields, // valore di default è null
				forms, // valore di default è null
				rawOrLabel, // valore di default è null
				exportCheckboxLabel, // valore di default è "false"
				rawOrLabelHeaders, // valore di default è "raw"
				exportSurveyFields, // valore di default è "false"
				exportDataAccessGroups, // valore di default è "false"
				returnFormat // valore di default è "json"
		);
	}

	/**
	 * Costruttore
	 * 
	 * @param token
	 * @param records
	 * @param forms
	 */
	private ApiRecord(String token, String records, String forms) {
		// per gli altri parametri che non servono, puoi passare `null`
		this(token, records, forms, format);
	}

	/**
	 * Metodo statico <b>Factory</b> per estrarre solo i record delle survey (punto
	 * di partenza per la costruzione di nuove eCRF)
	 * 
	 * @param recordId
	 * @param form
	 * @return oggetto ApiRecord
	 */
	static ApiRecord surveyRecord(String recordId, String form) {
		return new ApiRecord(TOKEN_SURVEY, recordId, form);
	}
}
