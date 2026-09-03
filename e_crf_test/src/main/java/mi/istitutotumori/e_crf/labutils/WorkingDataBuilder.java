package mi.istitutotumori.e_crf.labutils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.StreamSupport;

import com.fasterxml.jackson.databind.JsonNode;

import mi.istitutotumori.e_crf.sanitize.SanitizerApi;
import mi.istitutotumori.tracing.ExcludeTrace;

/**
 * Nella classe WorkingDataBuilder gestisco/creo queste strutture utili nella
 * formazione dei fields dell'Istrument "laboratorio": - un array List iniziale
 * di appoggio: [[wbc_01 (chiave), wbc ( ), WBC (descrizione), Lab test
 * (sottoinsieme esami)],...] - un array List "esteso": [[wbc_01 (chiave), wbc (
 * ), WBC (descrizione), Lab test (sottoinsieme esami)],...] - un array per
 * creare il field della nested table di REDCap - una String per il codice SQL
 * dei valori Rout degli esami - una String per il codice Html della tabella
 * annidata Imposto la classe FINAL perchè uso i setter per costruire gli
 * attributi. Faccio così poiche questi attributi devono essere costruiti
 * prendondo le inforamzioni dalle API di REDCap ed in sostanza non si prevedono
 * modifiche -> uso la logica dei setter come se fossero dei builder
 */
@ExcludeTrace(reason = "escludo intera classe WorkingDataBuilder")
final class WorkingDataBuilder implements SanitizerApi {

	private final List<List<String>> arrayAppoggio;
	private final List<List<String>> arrayEsteso;
	private final List<List<String>> nestedTableData;
	private final List<List<String>> arrayEstesoNonInt;
	private final List<List<String>> nestedTableDataNonInt;
	private final String sqlRout;
	private final String sqlFieldMinMaxUnit;

	public WorkingDataBuilder(JsonNode jsonRecord, JsonNode jsonMetadata, String sqlTableProject,
			String sqlTableDictionary, String fieldName) {
		this.arrayAppoggio = setArrayAppoggio(jsonRecord, jsonMetadata);

		/*
		 * if (jsonRecord.get("esami_non_int").asText().equals("1")) {
		 * System.out.println("CICCIOOO"); this.arrayEsteso =
		 * setArrayEstesoNonInt(this.arrayAppoggio); this.nestedTableData =
		 * setNestedTableDataNonInt(this.arrayAppoggio); } else {
		 * System.out.println("NOON CICCIOOO"); this.arrayEsteso =
		 * setArrayEsteso(this.arrayAppoggio); this.nestedTableData =
		 * setNestedTableData(this.arrayAppoggio); }
		 */
		this.arrayEsteso = setArrayEsteso(this.arrayAppoggio);
		this.nestedTableData = setNestedTableData(this.arrayAppoggio);
		this.arrayEstesoNonInt = setArrayEstesoNonInt(this.arrayAppoggio);
		this.nestedTableDataNonInt = setNestedTableDataNonInt(this.arrayAppoggio);
		this.sqlRout = setSqlRout(arrayAppoggio, sqlTableProject);
		this.sqlFieldMinMaxUnit = setSqlFieldMinMaxUnit(fieldName, sqlTableProject, sqlTableDictionary);
	}

	/* metodi getter */

	public List<List<String>> getArrayAppoggio() {
		return arrayAppoggio;
	}

	public List<List<String>> getArrayEsteso() {
		return arrayEsteso;
	}

	public List<List<String>> getNestedTableData() {
		return nestedTableData;
	}

	public List<List<String>> getArrayEstesoNonInt() {
		return arrayEstesoNonInt;
	}

	public List<List<String>> getNestedTableDataNonInt() {
		return nestedTableDataNonInt;
	}

	public String getSqlRout() {
		return sqlRout;
	}

	public String getSqlFieldMinMaxUnit() {
		return sqlFieldMinMaxUnit;
	}

	/**
	 * METODI BUILDER (setter) prendo i dati della selezione esami dalla API REDCap,
	 * devo fornire il # di record a cui ci si riferisce e il Forms poi estraggo con
	 * API i dati "metadata dictionary", fornisco solo il Form
	 * 
	 * @param jsonRecord
	 * @param jsonMetadata
	 * @return
	 */
	@ExcludeTrace(reason = "NON utile")
	private List<List<String>> setArrayAppoggio(JsonNode jsonRecord, JsonNode jsonMetadata) {
		List<List<String>> arrayAppoggio = new ArrayList<>();
		// Iterator<String> it = jsonRecord.fieldNames();
		List<String> selectedKeys = new ArrayList<>();

		/**
		 * creo la lista delle scelte = "1"
		 */
		selectedKeys = SanitizerApi.extractFieldsWithValueOne(jsonRecord);
		// while (it.hasNext()) {
		// String key = it.next();
		// if ("1".equals(jsonRecord.get(key).asText())) {
		// selectedKeys.add(key);
		// }
		// }
		/**
		 * VERIFICO che la chiave dei valori = "1" nelle ultime 3 posizioni abbia una
		 * stringa del tipo "_XY" dove X e Y sono carattere qualsiasi dversi da "_" e
		 * nella quartultima posizione non abbia "_", poichè "__XY" potrebbe essere un
		 * nome di variabile creata in automatico da REDCap
		 */
		for (String item : selectedKeys) {
			if (item != null && item.length() >= 3 && item.charAt(item.length() - 3) == '_'
					&& item.charAt(item.length() - 2) != '_' && item.charAt(item.length() - 1) != '_'
					&& item.charAt(item.length() - 4) != '_') {
				String desc = StreamSupport.stream(jsonMetadata.spliterator(), false)
						.filter(n -> item.equals(n.path("field_name").asText()))
						.map(n -> n.path("field_label").asText()) // tolto il null che deprecava il metodo
						.findFirst().orElse(null);

				ConstData constants = new ConstData();
				String setLab = constants.getSection("examSet").findValue(item.substring(item.length() - 3)).asText();

				arrayAppoggio.add(List.of(item, item.substring(0, item.length() - 3), desc, setLab));

			}
			continue;

		}
		return arrayAppoggio;
	} // END method buildArrayAppoggio

	private List<List<String>> setArrayEsteso(List<List<String>> arrayAppoggio) {
		List<List<String>> arrayEsteso = new ArrayList<>();
		for (List<String> row : arrayAppoggio) {
			arrayEsteso.add(List.of(row.get(1) + "_result", row.get(2) + " result", row.get(3)));
			arrayEsteso.add(List.of(row.get(1) + "_unit", row.get(2) + " Unit", row.get(3)));
			arrayEsteso.add(List.of(row.get(1) + "_rmin", row.get(2) + " range min", row.get(3)));
			arrayEsteso.add(List.of(row.get(1) + "_rmax", row.get(2) + " range max", row.get(3)));
			arrayEsteso.add(List.of(row.get(1) + "_rout", row.get(2) + " range out", row.get(3)));
		}
		return arrayEsteso;
	} // END method buildArrayEsteso

	private List<List<String>> setNestedTableData(List<List<String>> arrayAppoggio) {
		List<List<String>> nested = new ArrayList<>();
		for (List<String> row : arrayAppoggio) {
			nested.add(List.of(row.get(3), row.get(2), row.get(1) + "_result:icons", row.get(1) + "_unit:icons",
					row.get(1) + "_rmin:icons", row.get(1) + "_rmax:icons", row.get(1) + "_rout:icons"));
		}

		return nested;
	} // END method buildNestedTableData

	private String setSqlRout(List<List<String>> arrayAppoggio, String sqlTableProject) {
		StringBuilder sql = new StringBuilder();
		for (List<String> row : arrayAppoggio) {
			sql.append("select distinct value from ").append(sqlTableProject)
					.append(" where project_id=[project-id] and field_name='").append(row.get(1))
					.append("_rout' union ");
		}
		if (sql.length() > 7) {
			sql.setLength(sql.length() - 7); // remove final " union"
		}
		return sql.toString();
	} // END method buildSqlRout

	String setSqlFieldMinMaxUnit(String fieldName, String sqlTableProject, String sqlTableDictionary) {
		StringBuilder sql = new StringBuilder();

		// aggiunta il 15.04.2026
		sql.append("select distinct value from ").append(sqlTableProject)
				.append(" where project_id=[project-id] and field_name='").append(fieldName + "_de").append("' ");

		sql.append("union ");

		// prima iniziava QUI
		sql.append("select distinct value from ").append(sqlTableProject)
				.append(" where project_id=[project-id] and field_name='").append(fieldName).append("' ");

		sql.append("union ");

		sql.append("select distinct value from ").append(sqlTableDictionary)
				.append(" where project_id=177 and field_name='").append(fieldName).append("_de' ");

		sql.append("order by 1");

		return sql.toString();
	} // END method buildSqlFieldMinMaxUnit

	private List<List<String>> setArrayEstesoNonInt(List<List<String>> arrayAppoggio) {
		List<List<String>> arrayEsteso = new ArrayList<>();
		for (List<String> row : arrayAppoggio) {
			// arrayEsteso.add(List.of(row.get(1) + "_result", row.get(2) + " result",
			// row.get(3)));
			arrayEsteso.add(List.of(row.get(1) + "_unit_de", row.get(2) + " Unit", row.get(3)));
			arrayEsteso.add(List.of(row.get(1) + "_rmin_de", row.get(2) + " range min", row.get(3)));
			arrayEsteso.add(List.of(row.get(1) + "_rmax_de", row.get(2) + " range max", row.get(3)));
			// arrayEsteso.add(List.of(row.get(1) + "_rout", row.get(2) + " range out",
			// row.get(3)));
		}
		return arrayEsteso;
	} // END method buildArrayEstesoNonInt

	private List<List<String>> setNestedTableDataNonInt(List<List<String>> arrayAppoggio) {
		List<List<String>> nested = new ArrayList<>();
		for (List<String> row : arrayAppoggio) {
			nested.add(List.of(
					// row.get(3)+ "_de",
					row.get(3), row.get(2),
					// row.get(1) + "_result:icons",
					row.get(1) + "_unit_de:icons", row.get(1) + "_rmin_de:icons", row.get(1) + "_rmax_de:icons"
			// row.get(1) + "_rout:icons"
			));
		}

		return nested;
	} // END method buildNestedTableDataNonInt

} // end class
