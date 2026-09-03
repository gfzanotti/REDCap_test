package mi.istitutotumori.apiredcap;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Field;
import java.util.List;

import org.apache.http.NameValuePair;
import org.junit.jupiter.api.Test;

class AbstractApiRedcapTestMockNull extends AbstractApiRedcap {
	private AbstractApiRedcapTestMockNull() {
		super(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
	}

	@Test
	void testo_condizioni_null() throws Exception {
		AbstractApiRedcapTestMockNull test = new AbstractApiRedcapTestMockNull();

		Field paramsField = AbstractApiRedcap.class.getDeclaredField("params");
		paramsField.setAccessible(true);

		@SuppressWarnings("unchecked")
		List<NameValuePair> params = (List<NameValuePair>) paramsField.get(test);

		assertTrue(params.isEmpty());

	} // end costruttore_inizializza_correttamente_i_parametri

}
