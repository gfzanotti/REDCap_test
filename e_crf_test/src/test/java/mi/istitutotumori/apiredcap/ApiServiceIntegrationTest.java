package mi.istitutotumori.apiredcap;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Test di integrazione reale con REDCap ATTENZIONE: serve un token valido e
 * accesso al server REDCap
 */
class ApiServiceIntegrationTest {

	private static final String TOKEN_NEW_PROJECT = "..."; 
	private static final String TOKEN_TEMPLATE = "..."; 
	private static final String RECORD_ID = "1"; // record valido nel tuo progetto
	private static final String FORM = "survey_questionnaires"; // form valido
	private static final String CSV_STRING = "field_name,form_name,section_header,field_type,field_label,select_choices_or_calculations,field_note,text_validation_type_or_show_slider_number,text_validation_min,text_validation_max,identifier,branching_logic,required_field,custom_alignment,question_number,matrix_group_name,matrix_ranking,field_annotation\r\n"
			+ "record_id,end_of_study,,text,Record ID,,,,,,,,,,,,,\r\n"
			+ "date_reason_off_study,end_of_study,,text,Date,,,date_dmy,2024-01-01,,,,,RH,,,,\r\n"
			+ "reason_off_study,end_of_study,,radio,Reason Off Study:,\"1, Study completed | 2, Lost to follow-up | 3, Withdrawn consent | 4, Death | 5, Sponsor decision | 6, Other\",,,,,,,,,,,,\r\n"
			+ "other_reason_off_study,end_of_study,,text,Please specify below,,,,,,,[reason_off_study] = '6',,,,,,\r\n"
			+ "death_reason,end_of_study,,radio,Please specify main cause of death,\"1, Adverse event | 2, Clinical disease progression | 3, Other\",,,,,,[reason_off_study] = '4',,,,,,\r\n"
			+ "death_specify,end_of_study,,text,Specify,,,,,,,[reason_off_study] = '4',,,,,,\r\n"
			+ "death_date,end_of_study,,text,Date of death:,,,date_dmy,2024-01-01,,,[reason_off_study] = '4',,RH,,,,\r\n"
			+ "death_date_incompl,end_of_study,,text,Date of death incomplete,,,,,,,[reason_off_study] = '4',,RH,,,,\r\n"
			+ "autopsy_performed,end_of_study,,yesno,Autopsy performed:,,,,,,,[reason_off_study] = '4',,RH,,,,\r\n"
			+ "alert_eos1,end_of_study,,descriptive,\"<div class=\"\"rich-text-field-label\"\"><h3 style=\"\"text-align: center;\"\"><span style=\"\"font-weight: normal; color: #e03e2d;\"\">Attention! There is an empty field!</span></h3></div>\",,,,,,,([date_reason_off_study]='' or [reason_off_study]='' or [other_reason_off_study]='' or [death_reason]='' or [death_specify]='' or [death_date]='' or [death_date_incompl]='' or [autopsy_performed]=''),,,,,,\r\n"
			+ "subject_discontinued,end_of_treatment,,yesno,Was the subject discontinued from the study?,,,,,,,,,RH,,,,\r\n"
			+ "discontinuation_date,end_of_treatment,,text,Discontinuation Date,,,date_dmy,,,,[subject_discontinued] = '1',,RH,,,,\r\n"
			+ "primary_reason,end_of_treatment,,radio,Primary reason for discontinuation of treatment period:,\"1, Non compliance | 2, Clinical Progression Disease | 3, Radiological Progression Disease | 4, Investigator decision | 5, Adverse event | 6, Consent withdrawal | 7, Death | 9, Other\",,,,,,[subject_discontinued] = '1',,,,,,\r\n"
			+ "other_reason_discont,end_of_treatment,,text,Please specify below,,,,,,,[primary_reason] = '7' or [primary_reason] = '9',,,,,,\r\n"
			+ "alert_eot1,end_of_treatment,,descriptive,\"<div class=\"\"rich-text-field-label\"\"><h3 style=\"\"text-align: center;\"\"><span style=\"\"font-weight: normal; color: #e03e2d;\"\">Attention! There is an empty field!</span></h3></div>\",,,,,,,([subject_discontinued]='' or [discontinuation_date]='' or [primary_reason]='' or [other_reason_discont]=''),,,,,,\r\n"
			+ ""; // esempio CSV

	@Test
	void testGetRecord_reale() throws IOException {
		JsonNode result = ApiService.getSurveyRecord(RECORD_ID, FORM);
		assertNotNull(result, "La risposta non deve essere nulla");
		System.out.println("Record JSON: " + result.toPrettyString());
	}

	@Test
	void testGetMetadata_reale_5par() throws Exception {
		JsonNode meta = (JsonNode) ApiService.getMetadata(TOKEN_TEMPLATE, "json", "json", null,
				"configuration_of_lab_data");
		assertNotNull(meta, "La metadata response non deve essere nulla");
		System.out.println("Metadata JSON: " + meta.toPrettyString());
	}

	@Test
	void testGetSurveyMetadata_reale() throws Exception {
		JsonNode meta = (JsonNode) ApiService.getSurveyMetadata("configuration_of_lab_data");
		assertNotNull(meta, "La metadata response non deve essere nulla");
		System.out.println("Metadata JSON: " + meta.toPrettyString());
	}

	@Test
	void testGetStandardToolsMetadata_reale() throws Exception {
		JsonNode meta = (JsonNode) ApiService.getStandardToolsMetadata();
		assertNotNull(meta, "La metadata response non deve essere nulla");
		System.out.println("Metadata JSON: " + meta.toPrettyString());
	}

	@Test
	void testImportMetaData_reale() throws IOException {
		// ATTENZIONE: questa chiamata potrebbe modificare i dati su REDCap
		String response = ApiService.setMetadata(TOKEN_NEW_PROJECT, "csv", CSV_STRING);
		assertNotNull(response, "La risposta REDCap non deve essere nulla");
		System.out.println("Import response: " + response + "fields");
	}

	@Test
	void testSetMetadata_reale() throws Exception {
		String dataJson = "[{" + "\"field_name\":\"record_id\"," + "\"form_name\":\"end_of_study\","
				+ "\"section_header\":\"\"," + "\"field_type\":\"text\"," + "\"field_label\":\"Record ID\","
				+ "\"select_choices_or_calculations\":\"\"," + "\"field_note\":\"\","
				+ "\"text_validation_type_or_show_slider_number\":\"\"," + "\"text_validation_min\":\"\","
				+ "\"text_validation_max\":\"\"," + "\"identifier\":\"\"," + "\"branching_logic\":\"\","
				+ "\"required_field\":\"\"," + "\"custom_alignment\":\"\"," + "\"question_number\":\"\","
				+ "\"matrix_group_name\":\"\"," + "\"matrix_ranking\":\"\"," + "\"field_annotation\":\"\"" + "}]";
		String response = ApiService.setMetadata(TOKEN_NEW_PROJECT, "json", dataJson);
		assertNotNull(response, "La risposta REDCap non deve essere nulla");
		System.out.println("SetMetadata response: " + response);
	}
}
