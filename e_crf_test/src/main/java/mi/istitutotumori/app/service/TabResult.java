package mi.istitutotumori.app.service;

public class TabResult {
	public String message; // messaggio informativo finale
	public String error; // eventuale errore

	public boolean hasError() {
		return error != null && !error.isEmpty();
	}

}
