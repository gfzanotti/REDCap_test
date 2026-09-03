package mi.istitutotumori.app.controller;

import mi.istitutotumori.app.service.RunnerService;
import mi.istitutotumori.app.service.TabResult;

import java.util.function.BiConsumer;

public class RunnerDispatcher {

	private final RunnerService runnerService;

	public RunnerDispatcher(RunnerService runnerService) {
		this.runnerService = runnerService;
	}

	/**
	 * Dispatch generico: in base al tipo di controller chiama il metodo appropriato
	 * di RunnerService.
	 *
	 * @param controller      controller della tab
	 * @param progressUpdater callback per aggiornare la progress bar
	 * @return TabResult contenente messaggio o errore
	 */
	public TabResult dispatch(Object controller, BiConsumer<Integer, Integer> progressUpdater) {

		if (controller instanceof LabController lab) {
			progressUpdater.accept(20, 100);
			return runnerService.runLab(lab.getParameters());
		}

		if (controller instanceof StandardController std) {
			progressUpdater.accept(10, 100);
			return runnerService.runStandard(std.getParameters());
		}

		if (controller instanceof EotEosController eot) {
			progressUpdater.accept(10, 100);
			return runnerService.runEotEos(eot.getParameters());
		}

		if (controller instanceof QoLController qol) {
			progressUpdater.accept(10, 100);
			return runnerService.runQoL(qol.getParameters());
		}

		TabResult r = new TabResult();
		r.error = "Tipo di controller non riconosciuto";
		return r;
	}
}
