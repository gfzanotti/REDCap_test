package mi.istitutotumori.e_crf.utils;

import java.util.*;
import java.util.regex.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import mi.istitutotumori.e_crf.AbstractInstrument;
import mi.istitutotumori.e_crf.Field;

public class BranchingLogicValidator {

	private static final Logger log = LogManager.getLogger("bl_Logger");

	// Variabili che non bloccano la validazione della branching logic
	private static final Set<String> NON_BLOCKING = Set.of("event-name");
	private static final Pattern VAR_PATTERN = Pattern.compile("\\[(.*?)\\]");

	public record BranchingResult(String logic, String annotationExtra, Set<String> removedVars) {
	}

	public BranchingResult validate(List<String> instrumentVars, String logic) {

		if (logic == null || logic.isBlank()) {
			return new BranchingResult("", "", Set.of());
		}

		log.info("Validazione branching logic: {}", logic);

		Matcher matcher = VAR_PATTERN.matcher(logic);
		Set<String> varsFound = new HashSet<>();

		while (matcher.find()) {
			varsFound.add(matcher.group(1));
		}

		Set<String> removed = new HashSet<>();

		for (String var : varsFound) {
			if (NON_BLOCKING.contains(var)) {
				log.debug("Variabile non bloccante ignorata: {}", var);
				continue;
			}
			if (!instrumentVars.contains(var)) {
				removed.add(var);
				// log.warn("Variabile NON presente nell'Instrument: {} | BL: {}", var, logic);
				log.warn("Variabile NON presente nell'Instrument: {}", var);
			}
		}

		if (!removed.isEmpty()) {
			log.info("Branching logic BLOCCANTE rilevata: {}", logic);
			return new BranchingResult("", logic, removed);
		}

		return new BranchingResult(logic, "", Set.of());
	}

	/**
	 * Valida l'intero progetto REDCap rappresentato da una lista di
	 * AbstractInstrument. Per ogni Field di ogni Instrument esegue la validazione
	 * della branching_logic usando la lista globale delle variabili del progetto.
	 * Se un Field viene modificato dalla validazione (branching logic spostata in
	 * annotation), viene sostituito nella lista dei fields dell'instrument.
	 *
	 * Restituisce una mappa (instrumentName -> set di variabili rimosse) utile per
	 * report/log.
	 */
	public Map<String, Set<String>> validateProject(List<AbstractInstrument> instruments) {
		return validateProject(instruments, true);
	}

	/**
	 * Valida il progetto. Se restoreFromAnnotation è true, prima ripristina
	 * eventuali branching logic salvate in field_annotation e poi riesegue la
	 * validazione su tutto il progetto con la lista completa delle variabili.
	 */
	public Map<String, Set<String>> validateProject(List<AbstractInstrument> instruments,
			boolean restoreFromAnnotation) {
		if (instruments == null || instruments.isEmpty()) {
			return Collections.emptyMap();
		}
		// Optionally restore branching logic from annotations first
		if (restoreFromAnnotation) {
			for (AbstractInstrument inst : instruments) {
				List<Field> fields = inst.getFields();
				for (int idx = 0; idx < fields.size(); idx++) {
					Field f = fields.get(idx);
					Field restored = f.restoreBranchingFromAnnotation();
					if (restored != f) {
						fields.set(idx, restored);
						log.info("Restored branching logic from annotation for field {} of instrument {}",
								f.getFieldName(), inst.getName());
					}
				}
			}
		}

		// costruisci la lista globale di variabili disponibili nel progetto
		List<String> globalVars = instruments.stream().flatMap(i -> i.getVariableNameSet().stream()).distinct()
				.collect(java.util.stream.Collectors.toList());

		Map<String, Set<String>> removedPerInstrument = new HashMap<>();

		for (AbstractInstrument inst : instruments) {
			String instName = inst.getName();
			List<Field> fields = inst.getFields();
			for (int idx = 0; idx < fields.size(); idx++) {
				Field original = fields.get(idx);
				Field validated = original.validateBranchingLogic(globalVars);
				if (validated != original) {
					// sostituisci il field modificato
					fields.set(idx, validated);

					// ricalcola i removedVars tramite validate per ottenere il dettaglio
					BranchingResult res = this.validate(globalVars, original.getBranchingLogic());
					if (!res.removedVars().isEmpty()) {
						removedPerInstrument.computeIfAbsent(instName, k -> new HashSet<>()).addAll(res.removedVars());
						log.info("Instrument {}: removedVars={}", instName, res.removedVars());
					}
				}
			}
		}

		return removedPerInstrument;
	}
}
