package mi.istitutotumori.e_crf;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import mi.istitutotumori.e_crf.labutils.ConstData;
import mi.istitutotumori.e_crf.labutils.WorkingLists;

/**
 * Classe di costruzione dello strumento di laboratorio, di complessità maggiore
 * rispetto agli strumenti standard. Si appoggia alle classi di utilità per la
 * costruzione dei metadati dei vari campi.
 */
public class LabInstrument implements AbstractInstrument {

	private String name; // ad esempio "Hematology" o "Lab data"
	// 30.06.2026 meglio: "laboratory_data", "configuration_of_lab_data",
	// "dati_laboratorio"
	// NOME dell'INSTRUMENT corrisponde al formName dei Fields
	private final List<Field> fields = new ArrayList<>();

	/**
	 * COSTRUTTORE: 1. itera sulle istanze "firstRows" del file dictFirstRows.json
	 * 2. richiama metodo fromJsonFileValues(ConstData constants, String rowValue)
	 * della classe Field 3. aggiunge la riga (field) della tabella innestata degli
	 * esami, metodo field4HtmlNestedTable(wl.htmlNestedTable) 4. itero sull'array
	 * esteso dei parametri per i campi dei singoli esami, metodo
	 * otherLabItems(item, wl.sqlRout, sql)
	 *
	 * ne faccio 2 versioni con il method chaining. la prima con 2 parametri e la
	 * seconda senza parametri
	 * 
	 * @param recordId
	 * @param form          (nome della survey)
	 * @param name_formName
	 * @throws Exception
	 */
	public LabInstrument(String recordId, String form, String name_formName) throws Exception {
		this.name = name_formName;
		WorkingLists wl = new WorkingLists(recordId, form);
		ConstData constants = new ConstData();

		buildInitialFields(constants);
		addHtmlNestedTable(wl);
		buildLabItems(wl);
	} // END constructor

	/* Costruttore senza parametri (constructor chaining) */
	public LabInstrument() throws Exception {
		this("1", "survey_lab_data", "Hematology Instrument"); // default
	} // END constructor 2

	private void buildInitialFields(ConstData constants) {
		// List<Field> fields = new ArrayList<>();
		// Trovo il numero di righe
		Iterator<String> columnNames = constants.getSection("firstRows").fieldNames();
		String firstColumn = columnNames.next();
		// determino il numero di fields da costruire
		int rowCount = constants.getSection("firstRows").get(firstColumn).size();

		// for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
		// *** salto il field "record_id"
		for (int rowIndex = 1; rowIndex < rowCount; rowIndex++) {
			this.addField(Field.fromConstData(String.valueOf(rowIndex), this.name));
			// Field f =Field.fromJsonFileValues(constants, String.valueOf(rowIndex));
			// Aggiungi il field all'instrument
			// fields.add(f);
		} // end method buildInitialFields
	}

	/**
	 * Metodo usato dal metodo statico fieldLabBuilder() per aggiungere la 4 riga
	 * (field)
	 * 
	 * @param wl
	 */
	private void addHtmlNestedTable(WorkingLists wl) {
		// implementazione di più campi per i sottoinsiemi di esami
		wl.htmlNestedTable.forEach((key, value) -> {
			fields.add(Field.htmlNestedTable(key, value, this.name));
		});
		// fields.add(Field.htmlNestedTable(wl.htmlNestedTable.keySet().iterator().next()));
	} // END method addHtmlNestedTable

	// faccio la lista dei field di tutti gli item degli esami
	// prende in ingresso array Esteso
	/**
	 * Metodo usato dal metodo statico fieldLabBuilder() per le righe (fields) di
	 * tutte le istanze dei singoli esami. Viene applicato iterando sull'array
	 * esteso (item) e crea i fields aggiungendo le stringhe SQL
	 * 
	 * @param wl
	 */
	private void buildLabItems(WorkingLists wl) {
		for (List<String> item : wl.arrayEsteso) {
			String sql = wl.getSqlFieldMinMaxUnit(item.get(0));
			fields.add(Field.fromLabItem(item, wl.sqlRout, sql, this.name));
		}
	} // END method buildLabItems

	@Override
	public String getName() {
		return name;
	}

	@Override
	public List<Field> getFields() {
		return this.fields;
	}

	@Override
	public String toString() {
		return "Instrument :   ***  %s  ***;%n %s".formatted(getName(), fields);
	}
}// END class