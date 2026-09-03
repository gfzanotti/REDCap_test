package mi.istitutotumori.e_crf;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import mi.istitutotumori.e_crf.utils.BranchingLogicValidator;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Rappresenta un progetto REDCap composto da più Instruments. Ora costruibile
 * tramite pattern Builder.
 */
public class InstrumentsProj {

	private final List<AbstractInstrument> instruments;

	// Costruttore privato → uso obbligatorio del Builder
	InstrumentsProj() {
		this.instruments = new ArrayList<>();
	}

	// Factory per ottenere il builder
//    public static InstrumentsProjBuilder builder() {
//        return new InstrumentsProjBuilder();
//    }

	// Aggiunge uno strumento al progetto
	void addInstrument(AbstractInstrument inst) {
		instruments.add(inst);
	}

	// Rimuove uno strumento dal progetto
	public boolean removeInstrumentByName(String name) {
		return instruments.removeIf(i -> i.getName().equals(name));
	}

	// Lista immutabile di instruments
	public List<AbstractInstrument> getInstruments() {
		return Collections.unmodifiableList(instruments);
	}

	// Restituisce la LISTA degli instruments "name"
	public List<String> getListNameInstruments() {
		return instruments.stream().map(i -> i.getName()).toList();
	}

	// Reset interno
	void reset() {
		instruments.clear();
	}

	/**
	 * Valida la branching_logic di tutti i field di tutti gli instruments del
	 * progetto. Se la branching_logic contiene variabili non disponibili nel
	 * progetto, viene spostata nel field_annotation.
	 * 
	 * Questo metodo dovrebbe essere chiamato dopo che tutti gli instruments sono
	 * stati aggiunti al progetto.
	 */
	public Map<String, Set<String>> validateAllBranchingLogic() {
		Logger log = LogManager.getLogger("bl_Logger");
		BranchingLogicValidator validator = new BranchingLogicValidator();
		Map<String, Set<String>> report = validator.validateProject(this.instruments, true);
		if (report != null && !report.isEmpty()) {
			// Log summary using bl_Logger
			report.forEach((inst, vars) -> {
				if (vars != null && !vars.isEmpty()) {
					log.info("[BranchingLogicValidator] Instrument={} removedVars={}", inst, vars);
				}
			});
		}
		return report;
	}

	@Override
	public String toString() {
		return instruments.stream().map(AbstractInstrument::toString).collect(Collectors.joining("\n"));
	}
}
