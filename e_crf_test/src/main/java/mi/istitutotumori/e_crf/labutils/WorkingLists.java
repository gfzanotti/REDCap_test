package mi.istitutotumori.e_crf.labutils;

import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.databind.JsonNode;

import mi.istitutotumori.apiredcap.ApiService;
import mi.istitutotumori.tracing.ExcludeTrace;

@ExcludeTrace(reason = "NON utile")
public class WorkingLists {
	// JsonNode jsonRecord, JsonNode jsonMetadata, String sqlTableProject, String
	// sqlTableDictionary, String fieldName

	private static final Logger dataStructLog = LogManager.getLogger("dataStructWL_Logger");

	// builder viene creato dal costruttore
	private WorkingDataBuilder builder;
	private HtmlNestedTableBuilder htmlBuilder;

	// constant
	private static final String sqlTableProject = "[data-table]";
	private static final String sqlTableDictionary = "redcap_data";
	private static final String fieldName = "ciccio";
	// private static final String token = new
	// ConstData().getSection("tokenList").findValue("survey").toString();

	// attributi ritornati
	private List<List<String>> arrayAppoggio;
	public List<List<String>> arrayEsteso;
	private List<List<String>> nestedTableData;
	public List<List<String>> arrayEstesoNonInt;
	private List<List<String>> nestedTableDataNonInt;
	public String sqlRout;
	public Map<String, String> htmlNestedTable;
	public Map<String, String> htmlNestedTableNonInt;

	/**
	 * Per la Stringa "sqlFieldMinMaxUnit" che iterando su arrayEsteso di volta in
	 * volta mi crea l'sql dei singoli field restituisco un metodo statico che
	 * ingloba i parametri costanti di sqlTableProject e sqlTableDictionary ma che
	 * richiede un campo Stringa sull'iterazione
	 */

	/**
	 * COSTRUTTORE
	 * 
	 * @param recordId
	 * @param form
	 * @throws Exception
	 */
	public WorkingLists(String recordId, String form) throws Exception {

		dataStructLog.info("Inizio costruzione WorkingLists \n");

		JsonNode jsonRecord = ApiService.getSurveyRecord(recordId, form);
		dataStructLog.info("Record ottenuto: \n{} \n", jsonRecord);

		JsonNode jsonMetadata = ApiService.getSurveyMetadata(form);
		dataStructLog.info("Metadata ottenuto, {} campi \n", jsonMetadata.size());

		this.builder = new WorkingDataBuilder(jsonRecord, jsonMetadata, sqlTableProject, sqlTableDictionary, fieldName);
		this.htmlBuilder = new HtmlNestedTableBuilder();

		this.arrayAppoggio = builder.getArrayAppoggio();
		dataStructLog.info("Array appoggio creato con {} elementi \n {} \n", this.arrayAppoggio.size(),
				this.arrayAppoggio);

		this.arrayEsteso = builder.getArrayEsteso();
		dataStructLog.info("Array esteso creato con {} elementi \n {} \n", this.arrayEsteso.size(), this.arrayEsteso);

		this.nestedTableData = builder.getNestedTableData();
		dataStructLog.info("nestedTableData creato \n {} \n", this.nestedTableData);

		this.arrayEstesoNonInt = builder.getArrayEstesoNonInt();
		dataStructLog.info("Array estesoNonInt creato con {} elementi \n {} \n", this.arrayEstesoNonInt.size(),
				this.arrayEstesoNonInt);

		this.nestedTableDataNonInt = builder.getNestedTableDataNonInt();
		dataStructLog.info("nestedTableDataNonInt creato \n {} \n", this.nestedTableDataNonInt);

		this.sqlRout = builder.getSqlRout();
		dataStructLog.info("sqlRout creato, \n {} \n", this.sqlRout);

		/*
		 * if (jsonRecord.get("esami_non_int").asText().equals("1")) {
		 * System.out.println("CICCIOOO htmlBuilder"); this.htmlNestedTable =
		 * htmlBuilder.buildHtmlNonInt(this.nestedTableData); } else {
		 * System.out.println("NOON CICCIOOO htmlBuilder"); this.htmlNestedTable =
		 * htmlBuilder.buildHtml(this.nestedTableData); }
		 */

		this.htmlNestedTable = htmlBuilder.buildHtml(this.nestedTableData);
		dataStructLog.info("htmlNestedTable creato \n {} \n", this.htmlNestedTable);

		this.htmlNestedTableNonInt = htmlBuilder.buildHtmlNonInt(this.nestedTableDataNonInt);
		;
		dataStructLog.info("htmlNestedTableNonInt creato \n {} \n", this.htmlNestedTableNonInt);
	}

	public WorkingLists() throws Exception {
		this("1", "survey_lab_data"); // valori di default
	}

	public String getSqlFieldMinMaxUnit(String fieldName) {
		return builder.setSqlFieldMinMaxUnit(fieldName, sqlTableProject, sqlTableDictionary);
	}

}
