package mi.istitutotumori.e_crf;

import java.util.ArrayList;
import java.util.List;

import mi.istitutotumori.tracing.ExcludeTrace;

/**
 * MODIFICHE release 0.1.7 (25.03.2026)
 * 
 * CLASSE per costruire un oggetto "Instrument" -> InitInstrument che all'inizio
 * di ogni progetto inserisce L'Instrument "Form 1" con il Field "Record ID". La
 * classe viene usata per costruire l'oggetto in: - LabRunnerService.java (riga
 * 33) - StandardRunnerService.java (riga 57) - RunnerServiceUtils.java (riga
 * 62) - RunnerServiceHelper.java (riga {11, 28})
 */
public class InitInstrument implements AbstractInstrument {

	private static InitInstrument instance;
	private final List<Field> fields = new ArrayList<>();
	private String name = "Record ID";

	private InitInstrument() {
		fields.add(Field.fromConstData("0", "form_1"));

	}

	@ExcludeTrace(reason = "NON utile")
	public static InitInstrument getInit() {
		if (instance == null) {
			instance = new InitInstrument();
		}
		return instance;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return this.name;
	}

	@Override
	public List<Field> getFields() {
		// TODO Auto-generated method stub
		return this.fields;
	}

	@Override
	public String toString() {
		return "InitInstrument{" + "name='" + name + '\'' + ", fields=" + fields + '}';
	}
}
