package mi.istitutotumori.e_crf.utils;

import java.util.List;
import java.util.Set;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import mi.istitutotumori.e_crf.AbstractInstrument;

/**
 * PATTERN Facade
 */
public class SerializeInst2CsvString {

	private static final Logger log = LogManager.getLogger("bl_Logger");

	private final InstrumentCsvBuilder builder = new InstrumentCsvBuilder();
	private final CsvSerializer serializer = new CsvSerializer();
	private final BranchingLogicValidator validator = new BranchingLogicValidator();

	public String toCsvString(List<AbstractInstrument> instruments) {
		// Central validation: validate project once and pass report to builder
		Map<String, Set<String>> report = validator.validateProject(instruments, true);
		builder.setRemovedPerInstrument(report);
		var table = builder.build(instruments);
		return serializer.serialize(table);
	}

	public void toCsvFile(List<AbstractInstrument> instruments, String filename) throws Exception {
		String csv = toCsvString(instruments);
		new CsvWriter().write(csv, filename);
	}

	/* NUOVO: accesso al set eliminato */
	public Set<String> getSetEliminato() {
		log.info("Variabili eliminate: {}", builder.getSetEliminato());
		return builder.getSetEliminato();
	}
}
