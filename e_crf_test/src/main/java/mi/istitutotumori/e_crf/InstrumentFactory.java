package mi.istitutotumori.e_crf;

import java.io.IOException;

import com.fasterxml.jackson.databind.JsonNode;
import com.opencsv.exceptions.CsvValidationException;

/**
 * Factory per creare diversi tipi di AbstractInstrument
 */
public class InstrumentFactory {

	/**
	 * Crea un AbstractInstrument in base al tipo
	 * 
	 * @param type   "lab" / "standard" / altri alias
	 * @param name   nome dello strumento
	 * @param params parametri opzionali per il costruttore
	 * @return AbstractInstrument pronto
	 * @throws Exception
	 */
	public static AbstractInstrument createInstrument(String type, String name, Object... params) throws Exception {
		return switch (type.toLowerCase()) {
		case "lab", "laboratory" -> createLabInstrument(name, params);
		case "configlabdata" -> createConfLabDataInstrument(name, params);
		case "standard", "std" -> createStandardInstrument(name, params);
		case "init" -> InitInstrument.getInit();

		default -> throw new IllegalArgumentException("Tipo di instrument non supportato: " + type);
		};
	}

	// ---------------- LabInstrument ----------------
	private static AbstractInstrument createLabInstrument(String name, Object... params) throws Exception {
		if (params.length == 0) {
			return new LabInstrument(); // costruttore default
		}
		if (params.length == 3 && params[0] instanceof String recordId && params[1] instanceof String form) {
			return new LabInstrument(recordId, form, name);
		}
		throw new IllegalArgumentException("Parametri non validi per LabInstrument");
	}

	// ---------------- Configuration of LabInstrument ----------------
	private static AbstractInstrument createConfLabDataInstrument(String name, Object... params) throws Exception {
		if (params.length == 0) {
			return new ConfLabDataInstrument(); // costruttore default
		}
		if (params.length == 3 && params[0] instanceof String recordId && params[1] instanceof String form) {
			return new ConfLabDataInstrument(recordId, form, name);
		}
		throw new IllegalArgumentException("Parametri non validi per ConfigLabInstrument");
	}

	// ---------------- StandardInstrument ----------------
	private static AbstractInstrument createStandardInstrument(String name, Object... params)
			throws CsvValidationException, IOException {

		if (params.length == 0) {
			return new StandardInstrument(name); // costruttore default
		}

		if (params.length == 1) {
			Object arg = params[0];

			if (arg instanceof String csv) {
				return new StandardInstrument(name, csv); // CSV come stringa
			}

			if (arg instanceof JsonNode jsonNode) {
				return new StandardInstrument(name, jsonNode); // JsonNode
			}
		}

		throw new IllegalArgumentException("Parametri non validi per StandardInstrument");
	}
}
