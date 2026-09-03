package mi.istitutotumori.app.service;

import com.fasterxml.jackson.databind.JsonNode;
import mi.istitutotumori.apiredcap.ApiService;
import mi.istitutotumori.app.controller.EotEosController;
import mi.istitutotumori.e_crf.InstrumentsProj;
import mi.istitutotumori.e_crf.InstrumentsProjBuilder;

import java.util.List;

class EotEosRunnerService extends BaseRunnerService {
	private EotEosController.EotEosParameters params;

	EotEosRunnerService(EotEosController.EotEosParameters params) {
		this.params = params;
	}

	@Override
	protected void validateParameters(TabResult result) {
		if (params.token == null || params.token.isEmpty()) {
			result.error = "IL TOKEN è obbligatorio";
		}
	}

	@Override
	protected InstrumentsProj buildInstruments() throws Exception {
		JsonNode jsonMetadataStd = ApiService.getStandardToolsMetadata();

		// Usa InstrumentsProj.builder() (static factory method)
		InstrumentsProjBuilder builder = new InstrumentsProjBuilder();

		// Creare strumenti per end_of_study e end_of_treatment
		for (String instrumentName : List.of("end_of_study", "end_of_treatment")) {
			JsonNode jsonNode = RunnerServiceUtils.filterByForm(jsonMetadataStd, instrumentName);
			if (jsonNode != null) {
				// Usa addInstrument con tipo "standard"
				builder.addInstrument("standard", instrumentName, jsonNode);
			}
		}

		return builder.build();
	}

	@Override
	protected InstrumentsProj processInstrumentMerge(InstrumentsProj instruments) throws Exception {
		RunnerServiceUtils rsu = new RunnerServiceUtils(params.token, instruments);
		return RunnerServiceUtils.getMergedInstList(rsu.existingProjInsts, rsu.addingProjInsts);
	}

	@Override
	protected String getOutputFileName() {
		return "addEoSEoT";
	}

	@Override
	protected String getOperationName() {
		return "EOT/EOS";
	}

	@Override
	protected String getSelectedAction() {
		return params.combo1;
	}

	@Override
	protected String getToken() {
		return params.token;
	}

	@Override
	protected String getAdditionalInfo() {
		return String.format("""

				""");
	}
}