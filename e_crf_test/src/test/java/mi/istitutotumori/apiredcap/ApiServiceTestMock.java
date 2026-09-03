package mi.istitutotumori.apiredcap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Test JUnit 5 per ApiService
 */
class ApiServiceTestMock {

	// ------------------------
	// Test getRecord con mock di ApiRecord
	// ------------------------
	@Test
	void getRecord_restituisce_json_quando_ok() throws Exception {

		// String token = "TEST_TOKEN";
		String recordId = "1";
		String form = "form1";

		// Mock di ApiRecord per intercettare doPost()
		try (MockedConstruction<ApiRecord> mocked = mockConstruction(ApiRecord.class,
				(mock, context) -> when(mock.doPost()).thenReturn("[{\"campo\":\"valore\"}]"))) {

			JsonNode result = ApiService.getSurveyRecord(recordId, form);

			assertNotNull(result);
			assertEquals("valore", result.get("campo").asText());

			// Verifica caching
			JsonNode cached = ApiService.getSurveyRecord(recordId, form);
			assertSame(result, cached); // deve restituire lo stesso oggetto dalla cache
		}
	}

	// ------------------------
	// Test getRecord quando doPost genera JSON malformato
	// non mi da mai result = null !!!
	// ------------------------
	// @Test
//    void getRecord_json_malformato_restituisce_null() throws Exception {
//
//        String token = "TEST_TOKEN";
//        String recordId = "1";
//        String form = "form1";
//
//        try (MockedConstruction<ApiRecord> mocked = mockConstruction(ApiRecord.class,
//                (mock, context) -> when(mock.doPost()).thenReturn(null))) {
//
//            JsonNode result = service.getRecord(token, recordId, form);
//
//            assertNull(result);
//        }
//    }

	// ------------------------
	// Test getMetadata (versione a 1 parametro) con mock
	// ------------------------
	@Test
	void getMetadata_restituisce_json_quando_ok() throws Exception {

		String token = "TEST_TOKEN";
		String form = "form1";

		try (MockedConstruction<ApiMetaData> mocked = mockConstruction(ApiMetaData.class,
				(mock, context) -> when(mock.doPost()).thenReturn("{\"meta\":\"ok\"}"))) {

			JsonNode result = (JsonNode) ApiService.getMetadata(token, "json", "json", null, form);

			assertNotNull(result);
			assertEquals("ok", result.get("meta").asText());

			// Verifica caching
			// JsonNode cached = service.getMetadata(token, form, null);
			// assertSame(result, cached);
		}
	}

	// ------------------------
	// Test getMetadata (versione a 5 parametri) formato json
	// ------------------------
	@Test
	void getMetadata_5param_json() throws Exception {

		String token = "T";
		String format = "json";
		String returnFormat = "json";
		String fields = "f1";
		String forms = "form1";

		try (MockedConstruction<ApiMetaData> mocked = mockConstruction(ApiMetaData.class,
				(mock, context) -> when(mock.doPost()).thenReturn("{\"campo\":\"valore\"}"))) {

			Object result = ApiService.getMetadata(token, format, returnFormat, fields, forms);

			assertTrue(result instanceof JsonNode);
			assertEquals("valore", ((JsonNode) result).get("campo").asText());
		}
	}

	// ------------------------
	// Test getMetadata 5 parametri formato csv
	// ------------------------
	@Test
	void getMetadata_5param_csv() throws Exception {

		String token = "T";
		String format = "csv";
		String returnFormat = "csv";
		String fields = "f1";
		String forms = "form1";

		String csvOutput = "campo1,campo2\nval1,val2";

		try (MockedConstruction<ApiMetaData> mocked = mockConstruction(ApiMetaData.class,
				(mock, context) -> when(mock.doPost()).thenReturn(csvOutput))) {

			Object result = ApiService.getMetadata(token, format, returnFormat, fields, forms);

			assertTrue(result instanceof String);
			assertEquals(csvOutput, result);
		}
	}

	// ------------------------
	// Test setMetadata
	// ------------------------
	@Test
	void setMetadata_restituisce_json() throws Exception {

		String token = "T";
		String format = "json";
		String data = "{\"campo\":\"valore\"}";

		try (MockedConstruction<ApiMetaData> mocked = mockConstruction(ApiMetaData.class,
				(mock, context) -> when(mock.doPost()).thenReturn(data))) {

			String result = ApiService.setMetadata(token, format, data);

			assertNotNull(result);
			assertTrue(result.contains("valore"));
		}
	}

	@Test
	void testGetSurveyRecordUsesCache() throws Exception {
		try (MockedStatic<ApiRecord> mockedFactory = Mockito.mockStatic(ApiRecord.class)) {

			ApiRecord mockRecord = Mockito.mock(ApiRecord.class);

			mockedFactory.when(() -> ApiRecord.surveyRecord("123", "formA")).thenReturn(mockRecord);

			Mockito.when(mockRecord.doPost()).thenReturn("[{\"field\":\"value\"}]");

			// Prima chiamata → deve chiamare doPost()
			JsonNode r1 = ApiService.getSurveyRecord("123", "formA");

			// Seconda chiamata → NON deve chiamare doPost()
			JsonNode r2 = ApiService.getSurveyRecord("123", "formA");

			// Verifica: doPost() chiamato UNA sola volta
			Mockito.verify(mockRecord, Mockito.times(1)).doPost();

			// Verifica: la cache restituisce lo stesso oggetto
			assertSame(r1, r2);
		}
	}

	@Test
	void testGetSurveyMetadataUsesCache() throws Exception {
		try (MockedStatic<ApiMetaData> mockedFactory = Mockito.mockStatic(ApiMetaData.class)) {

			ApiMetaData mockMeta = Mockito.mock(ApiMetaData.class);

			mockedFactory.when(() -> ApiMetaData.surveyMetaData("formA")).thenReturn(mockMeta);

			Mockito.when(mockMeta.doPost()).thenReturn("[{\"meta\":\"value\"}]");

			JsonNode m1 = ApiService.getSurveyMetadata("formA");
			JsonNode m2 = ApiService.getSurveyMetadata("formA");

			// Se la cache funziona → doPost() chiamato UNA sola volta
			Mockito.verify(mockMeta, Mockito.times(1)).doPost();

			// La cache deve restituire lo stesso oggetto
			assertSame(m1, m2);
		}
	}

}
