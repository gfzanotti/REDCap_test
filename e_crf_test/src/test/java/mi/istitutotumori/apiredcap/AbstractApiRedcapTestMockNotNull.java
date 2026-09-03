package mi.istitutotumori.apiredcap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.entity.StringEntity;
import org.junit.jupiter.api.Test;

class AbstractApiRedcapTestMockNotNull extends AbstractApiRedcap {

	private AbstractApiRedcapTestMockNotNull() {
		super("TEST_TOKEN", "record", "export", "json", "", "flat", "", "", "", "", "raw", "false", "raw", "false",
				"false", "json");
	}

	@Test
	void costruttore_inizializza_correttamente_i_parametri() throws Exception {
		AbstractApiRedcap api = new AbstractApiRedcapTestMockNotNull();

		Field paramsField = AbstractApiRedcap.class.getDeclaredField("params");
		paramsField.setAccessible(true);

		@SuppressWarnings("unchecked")
		List<NameValuePair> params = (List<NameValuePair>) paramsField.get(api);

		assertFalse(params.isEmpty());
		assertTrue(params.stream().anyMatch(p -> p.getName().equals("token")));
	} // end costruttore_inizializza_correttamente_i_parametri

	@Test
	void doPost_ritorna_il_corpo_della_response() throws Exception {
		AbstractApiRedcap api = new AbstractApiRedcapTestMockNotNull();

		// mock HttpClient
		HttpClient clientMock = mock(HttpClient.class);
		HttpResponse responseMock = mock(HttpResponse.class);
		StatusLine statusLineMock = mock(StatusLine.class);

		when(statusLineMock.getStatusCode()).thenReturn(200);
		when(responseMock.getStatusLine()).thenReturn(statusLineMock);
		when(responseMock.getEntity()).thenReturn(new StringEntity("{\"ok\":true}"));

		when(clientMock.execute(any())).thenReturn(responseMock);

		// iniettiamo il mock via reflection
		Field clientField = AbstractApiRedcap.class.getDeclaredField("client");
		clientField.setAccessible(true);
		clientField.set(api, clientMock);

		String result = api.doPost();

		assertEquals("{\"ok\":true}", result);
	}

}
