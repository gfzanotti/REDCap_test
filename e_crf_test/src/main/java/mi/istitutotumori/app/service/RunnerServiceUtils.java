package mi.istitutotumori.app.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import mi.istitutotumori.apiredcap.ApiService;
import mi.istitutotumori.e_crf.InstrumentsProj;
import mi.istitutotumori.e_crf.InstrumentsProjBuilder;
import mi.istitutotumori.e_crf.utils.SerializeInst2CsvString;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Utility class per creare e gestire strumenti (Instruments) da progetti
 * esistenti e per filtrare i data dictionary JSON.
 */
public final class RunnerServiceUtils {

	final InstrumentsProj existingProjInsts;
	final InstrumentsProj addingProjInsts;

	/**
	 * Costruttore. Estrae gli strumenti esistenti da un progetto tramite token e
	 * crea due progetti immutabili: 1. gli strumenti esistenti 2. gli strumenti da
	 * aggiungere
	 *
	 * @param token       Token per estrarre gli strumenti esistenti
	 * @param newInstList Lista di strumenti da aggiungere (già costruiti)
	 * @throws Exception
	 */
	public RunnerServiceUtils(String token, InstrumentsProj newInstList) throws Exception {

		// Export metadata dal progetto esistente
		JsonNode out = (JsonNode) ApiService.getMetadata(token, "json", "json", null, null);

		// Estrae i nomi degli strumenti presenti
		Set<String> listInstExistingProject = new LinkedHashSet<>();
		out.forEach(n -> {
			if (n.has("form_name")) {
				listInstExistingProject.add(n.get("form_name").asText());
			}
		});

		// Usa InstrumentsProjBuilder per costruire gli strumenti esistenti
		InstrumentsProjBuilder builder = new InstrumentsProjBuilder();

		for (String s : listInstExistingProject) {
			JsonNode singleNode = filterByForm(out, s);
			if (singleNode != null && singleNode.size() > 0) {
				// Usa la Factory per creare lo strumento
				builder.addInstrument("standard", s, singleNode);
			}
		}

		// Creo i record immutabili
		this.existingProjInsts = builder.build();
		this.addingProjInsts = newInstList;

		// 🔥 Rimuovo InitInstrument se presente
		this.addingProjInsts.removeInstrumentByName("Record ID");
	}

	/**
	 * Costruttore alternativo con InstrumentsProjBuilder per newInstList
	 */
	private RunnerServiceUtils(String token, InstrumentsProjBuilder newInstBuilder) throws Exception {
		// Export metadata dal progetto esistente
		JsonNode out = (JsonNode) ApiService.getMetadata(token, "json", "json", null, null);

		// Estrae i nomi degli strumenti presenti
		Set<String> listInstExistingProject = new LinkedHashSet<>();
		out.forEach(n -> {
			if (n.has("form_name")) {
				listInstExistingProject.add(n.get("form_name").asText());
			}
		});

		// Usa InstrumentsProjBuilder per costruire gli strumenti esistenti
		InstrumentsProjBuilder existingBuilder = new InstrumentsProjBuilder();

		for (String s : listInstExistingProject) {
			JsonNode singleNode = filterByForm(out, s);
			if (singleNode != null && singleNode.size() > 0) {
				existingBuilder.addInstrument("standard", s, singleNode);
			}
		}

		// Creo i record immutabili
		this.existingProjInsts = existingBuilder.build();
		this.addingProjInsts = newInstBuilder.build();
	}

	/**
	 * Merge tra due progetti immutabili, restituisce un nuovo InstrumentsProj
	 * immutabile
	 */
	public static InstrumentsProj getMergedInstList(InstrumentsProj existingProj, InstrumentsProj addingProj) {
		// Usa il builder per fare il merge
		return new InstrumentsProjBuilder().addInstruments(existingProj.getInstruments())
				.addInstruments(addingProj.getInstruments()).build();
	}

	/**
	 * UPDATE di alcuni instrument di un progetto creato da una survey (di
	 * laboratorio), su un progetto già esistente ed in produzione (messo in stato
	 * "DRAFT")
	 * 
	 * @throws Exception
	 */
	public static InstrumentsProj getUpdateProj(InstrumentsProj existingProj, InstrumentsProj updateProj)
			throws Exception {
		final String outputDir = System.getProperty("user.home") + "/Desktop/OutPutREDCap";
		final String dataOggi = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
		String fileNameSurveyUpdated = outputDir + "/" + "SurveyUpdated" + "_" + dataOggi + ".csv";
		String fileNameExistingProj = outputDir + "/" + "ExistingProj" + "_" + dataOggi + ".csv";
		final Logger otherInfo = LogManager.getLogger("optional_log");

		// creo file CSV della survey update
		SerializeInst2CsvString serializer = new SerializeInst2CsvString();
		serializer.toCsvFile(updateProj.getInstruments(), fileNameSurveyUpdated);
		otherInfo.info("***  Lista Instruments AGGIORNATI  ***");
		if (serializer.getSetEliminato() != null && otherInfo.isInfoEnabled()) {
			otherInfo.info("%s%n".formatted(serializer.getSetEliminato()));
		}

		// creo file CSV del progetto esistente
		SerializeInst2CsvString serializer_2 = new SerializeInst2CsvString();
		serializer_2.toCsvFile(existingProj.getInstruments(), fileNameExistingProj);
		otherInfo.info("***  Lista Instruments AGGIORNATI  ***");
		if (serializer_2.getSetEliminato() != null && otherInfo.isInfoEnabled()) {
			otherInfo.info("%s%n".formatted(serializer.getSetEliminato()));
		}

		// creo la lista di strumenti del progetto "update"
		// List<String> updateProjList = updateProj.getInstruments().stream()
		// .map(instrument -> instrument.getFields().get(0).getFormName())
		// .toList();

		// lista alternativa fatta a mano su quali INSTRUMENT devo rimuovere dal
		// progetto ESISTENTE
		List<String> lista = List.of("dati_laboratorio", "configurazione_dati_laboratorio", "laboratory_data",
				"configuration_of_lab_data");

		// ripulisco il progetto esistente dagli instrument presenti nell'update
		// existingProj.getInstruments().forEach(instrument ->
		// instrument.getFields().removeIf(field ->
		// updateProjList.contains(field.getFormName())));
		existingProj.getInstruments()
				.forEach(instrument -> instrument.getFields().removeIf(field -> lista.contains(field.getFormName())));

		// Usa il builder per fare il merge
		return new InstrumentsProjBuilder().addInstruments(existingProj.getInstruments())
				.addInstruments(updateProj.getInstruments()).build();
	}

	// ... altri metodi invariati ...

	/**
	 * Factory method per creare un RunnerServiceUtils
	 */
	public static RunnerServiceUtils create(String token, InstrumentsProj newInstList) throws Exception {
		return new RunnerServiceUtils(token, newInstList);
	}

	/**
	 * Factory method con builder per gli strumenti da aggiungere
	 */
	public static RunnerServiceUtils createWithBuilder(String token, InstrumentsProjBuilder newInstBuilder)
			throws Exception {
		return new RunnerServiceUtils(token, newInstBuilder);
	}

	static JsonNode filterByForm(JsonNode metadata, String formName) {
		ObjectMapper mapper = new ObjectMapper();
		ArrayNode result = mapper.createArrayNode();

		for (JsonNode field : metadata) {
			JsonNode formNode = field.get("form_name");
			if (formNode != null && formName.equals(formNode.asText())) {
				result.add(field);
			}
		}
		return result;
	}
}