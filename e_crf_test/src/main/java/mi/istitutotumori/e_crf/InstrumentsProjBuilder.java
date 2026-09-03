package mi.istitutotumori.e_crf;

import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Builder fluente per InstrumentsProj.
 */
public class InstrumentsProjBuilder {

	private final InstrumentsProj project;

	public InstrumentsProjBuilder() {
		this.project = new InstrumentsProj();
	}

	/**
	 * Aggiunge un AbstractInstrument già costruito
	 */
	public InstrumentsProjBuilder addInstrument(AbstractInstrument instrument) {
		project.addInstrument(instrument);
		return this;
	}

	/**
	 * Crea e aggiunge uno strumento tramite la factory
	 * 
	 * @param type   tipo di instrument ("lab", "standard", ecc.)
	 * @param name   nome dello strumento
	 * @param params parametri opzionali per il costruttore
	 */
	public InstrumentsProjBuilder addInstrument(String type, String name, Object... params) throws Exception {
		AbstractInstrument instrument = InstrumentFactory.createInstrument(type, name, params);
		return addInstrument(instrument);
	}

	/**
	 * Aggiunge una lista di instruments già esistenti
	 */
	public InstrumentsProjBuilder addInstruments(List<AbstractInstrument> instruments) {
		instruments.forEach(project::addInstrument);
		return this;
	}

	/**
	 * Costruisce il bundle standard da nomi, recordId e JsonNode Funziona con
	 * LabInstrument e StandardInstrument
	 */
	public InstrumentsProjBuilder buildStandardBundle(Set<String> names, String recordId, JsonNode json)
			throws Exception {
		for (String name : names) {
			if ("laboratory_data".equals(name) && recordId != null) {
				addInstrument("lab", name, recordId, name);
				continue;
			}

			// altri instruments (standard)
			for (JsonNode node : json) {
				if (name.equals(node.path("form_name").asText())) {
					addInstrument("standard", name, node);
					break; // aggiungo solo una volta per form_name
				}
			}
		}
		return this;
	}

	/**
	 * Costruisce l'oggetto finale InstrumentsProj
	 */
	public InstrumentsProj build() {
		// Valida la branching_logic di tutti i field del progetto
		project.validateAllBranchingLogic();
		return project;
	}

	/**
	 * Reset del builder
	 */
	public InstrumentsProjBuilder reset() {
		project.reset();
		return this;
	}
}
