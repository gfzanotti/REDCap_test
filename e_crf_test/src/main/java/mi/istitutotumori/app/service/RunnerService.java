package mi.istitutotumori.app.service;

import mi.istitutotumori.app.controller.EotEosController;
import mi.istitutotumori.app.controller.LabController;
import mi.istitutotumori.app.controller.QoLController;
import mi.istitutotumori.app.controller.StandardController;

/**
 * Questa classe applica tutta la logica "controller" del pattern MVC: -
 * interpreta i parametri passati nei vari TAB/form - delega l'esecuzione a
 * servizi specifici
 */
public class RunnerService {

	public TabResult runLab(LabController.LabParameters params) {
		BaseRunnerService service = new LabRunnerService(params);
		return service.execute();
	}

	public TabResult runStandard(StandardController.StandardParameters params) {
		BaseRunnerService service = new StandardRunnerService(params);
		return service.execute();
	}

	public TabResult runEotEos(EotEosController.EotEosParameters params) {
		BaseRunnerService service = new EotEosRunnerService(params);
		return service.execute();
	}

	public TabResult runQoL(QoLController.QoLParameters params) {
		BaseRunnerService service = new QoLRunnerService(params);
		return service.execute();
	}
}
