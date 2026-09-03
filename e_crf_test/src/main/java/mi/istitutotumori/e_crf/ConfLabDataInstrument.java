package mi.istitutotumori.e_crf;

import java.util.ArrayList;
//import java.util.Iterator;
import java.util.List;

//import mi.istitutotumori.e_crf.labutils.ConstData;
import mi.istitutotumori.e_crf.labutils.WorkingLists;

public class ConfLabDataInstrument implements AbstractInstrument {

	private String name; // ad esempio "Hematology" o "Configuration of Lab data"
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
	 * @param form     (nome della survey)
	 * @param name
	 * @throws Exception
	 */
	public ConfLabDataInstrument(String recordId, String form, String name) throws Exception {
		this.name = name;
		WorkingLists wl = new WorkingLists(recordId, form);
		// ConstData constants = new ConstData();

		// buildInitialFields(constants);
		addHtmlNestedTable(wl);
		buildLabItems(wl);
	} // END constructor

	/* Costruttore senza parametri (constructor chaining) */
	public ConfLabDataInstrument() throws Exception {
		this("1", "survey_lab_data", "Configuration of Lab Data"); // default
	} // END constructor 2

	/*
	 * private void buildInitialFields(ConstData constants) { //List<Field> fields =
	 * new ArrayList<>(); // Trovo il numero di righe Iterator<String> columnNames =
	 * constants.getSection("firstRows").fieldNames(); String firstColumn =
	 * columnNames.next(); // determino il numero di fields da costruire int
	 * rowCount = constants.getSection("firstRows").get(firstColumn).size();
	 * 
	 * // for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) { // *** salto il
	 * field "record_id" for (int rowIndex = 1; rowIndex < rowCount; rowIndex++) {
	 * this.addField(Field.fromConstData(String.valueOf(rowIndex))); //Field f
	 * =Field.fromJsonFileValues(constants, String.valueOf(rowIndex)); // Aggiungi
	 * il field all'instrument //fields.add(f); } // end method buildInitialFields }
	 */

	/**
	 * Metodo usato dal metodo statico fieldLabBuilder() per aggiungere la 4 riga
	 * (field)
	 * 
	 * @param wl
	 */
	private void addHtmlNestedTable(WorkingLists wl) {
		// implementazione di più campi per i sottoinsiemi di esami
		wl.htmlNestedTableNonInt.forEach((key, value) -> {
			fields.add(Field.htmlNestedTableNonInt(key, value));
		});
		// fields.add(Field.htmlNestedTableNonInt(wl.htmlNestedTableNonInt));
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
		for (List<String> item : wl.arrayEstesoNonInt) {
			String sql = wl.getSqlFieldMinMaxUnit(item.get(0));
			fields.add(Field.fromLabItemNonInt(item, wl.sqlRout, sql));
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
}
