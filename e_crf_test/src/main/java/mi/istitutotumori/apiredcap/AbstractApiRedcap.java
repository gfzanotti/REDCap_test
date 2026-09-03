package mi.istitutotumori.apiredcap;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
// mi servono nell'ultimo for per elencare la lista degli esami
// import java.util.ArrayList;
// import java.util.List;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

/**
 * Classe astratta "base" che include tutti i parametri possibili che si usano
 * nelle APi REDCap
 *
 * @author Gianfranco Zanotti
 */

abstract class AbstractApiRedcap {
	/**
	 * Definisco gli attributi che mi servono per la costruzione degli oggetti, per
	 * la chiamata delle API (Request [Post e Client], Response) e per la gestione
	 * del "character-input stream". Il tipo parametrico "params" lo uso per la
	 * gestione "aggregata" dei parametri di connessione e richiesta. Ho usato la
	 * logica delle API PLAYGROUND di REDCap e della documentazione relativa.
	 */

	private final List<NameValuePair> params;
	private final HttpPost post;
	private HttpResponse resp;
	private final HttpClient client;
	@SuppressWarnings("unused")
	private int respCode;
	private BufferedReader reader;
	private final StringBuffer result;
	private String line;
	// private ArrayList<String> array;

	// definisco i valori COSTANTI dei parametri da passare al costruttore
	protected static String token = "";
	protected static String content = "record";
	protected static String action = "export";
	protected static String format = "json";
	protected static String data = "";
	protected static String type = "flat";
	protected static String csvDelimiter = "";
	protected static String records = "";
	protected static String fields = null;
	protected static String forms = null;
	protected static String rawOrLabel = "raw";
	protected static String exportCheckboxLabel = "false";
	protected static String rawOrLabelHeaders = "raw";
	protected static String exportSurveyFields = "false";
	protected static String exportDataAccessGroups = "false";
	protected static String returnFormat = "json";
	// protected static String events = "";
	// protected static String filterLogic = "";
	// protected static String delete_logging = "";

	// Costruttore con tutti i parametri (potresti anche usare builder qui)
	protected AbstractApiRedcap(String token, String content, String action, String format, String data, String type,
			String csvDelimiter, String records, String fields, String forms, String rawOrLabel,
			String exportCheckboxLabel, String rawOrLabelHeaders, String exportSurveyFields,
			String exportDataAccessGroups, String returnFormat) {

		params = new ArrayList<>();
		if (token != null) {
			params.add(new BasicNameValuePair("token", token));
		}
		if (content != null) {
			params.add(new BasicNameValuePair("content", content));
		}
		if (action != null) {
			params.add(new BasicNameValuePair("action", action));
		}
		if (format != null) {
			params.add(new BasicNameValuePair("format", format));
		}
		if (data != null) {
			params.add(new BasicNameValuePair("data", data));
		}
		if (type != null) {
			params.add(new BasicNameValuePair("type", type));
		}
		if (csvDelimiter != null) {
			params.add(new BasicNameValuePair("csvDelimiter", csvDelimiter));
		}
		if (records != null) {
			params.add(new BasicNameValuePair("records[0]", records));
		}
		// per selezionare un sottoinsieme di "Form Name"
		if (fields != null) {
			params.add(new BasicNameValuePair("fields[0]", fields));
		}
		if (forms != null) {
			params.add(new BasicNameValuePair("forms[0]", forms));
		}
		if (rawOrLabel != null) {
			params.add(new BasicNameValuePair("rawOrLabel", rawOrLabel));
		}
		if (exportCheckboxLabel != null) {
			params.add(new BasicNameValuePair("exportCheckboxLabel", exportCheckboxLabel));
		}
		if (rawOrLabelHeaders != null) {
			params.add(new BasicNameValuePair("rawOrLabelHeaders", rawOrLabelHeaders));
		}
		if (exportSurveyFields != null) {
			params.add(new BasicNameValuePair("exportSurveyFields", exportSurveyFields));
		}
		if (exportDataAccessGroups != null) {
			params.add(new BasicNameValuePair("exportDataAccessGroups", exportDataAccessGroups));
		}
		if (returnFormat != null) {
			params.add(new BasicNameValuePair("returnFormat", returnFormat));
			// return params;
		}

		post = new HttpPost("http://.../api/");
		post.setHeader("Content-Type", "application/x-www-form-urlencoded");

		try {
			// post.setEntity(new UrlEncodedFormEntity(params));
			post.setEntity(new UrlEncodedFormEntity(params, StandardCharsets.UTF_8));
		} catch (final Exception e) {
			e.printStackTrace();
		}

		result = new StringBuffer();
		client = HttpClientBuilder.create().build();
		respCode = -1;
		reader = null;
		line = null;
	}

	/**
	 * METODO doPost() che effettua una connessione HTTP request-response per
	 * esportare o importare dati
	 * 
	 * @return result il metodo doPost() ritorna una String che rappresenta un array
	 *         di oggetti Json
	 */
	String doPost() {
		resp = null;

		try {
			System.out.printf("###   COSA INVIO ?  %s \n", EntityUtils.toString(post.getEntity()));
			resp = client.execute(post);
			System.out.printf("###   RESP IS    :  %s \n", resp.toString());
		} catch (final Exception e) {
			e.printStackTrace();
		}

		if (resp != null) {
			respCode = resp.getStatusLine().getStatusCode();

			try {
				reader = new BufferedReader(new InputStreamReader(resp.getEntity().getContent()));
			} catch (final Exception e) {
				e.printStackTrace();
			}
		}

		if (reader != null) {
			try {
				while ((line = reader.readLine()) != null) {
					result.append(line);
				}
			} catch (final Exception e) {
				e.printStackTrace();
			}
		}

		return result.toString();
	} // end doPost()
} // end Class