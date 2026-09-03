package mi.istitutotumori.e_crf.labutils;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import mi.istitutotumori.tracing.ExcludeTrace;

/**
 * Classe che gestisce i dati costanti memorizzati nel file .json. Restituisce
 * un oggetto "constants" con 3 attributi: - firstRows - neutralRow - tokenList
 * i primi 2 si riferiscono a fomrattazioni di "field" il terzo ai sottoinsiemi
 * degli esami di laboratorio
 *
 * @author Gianfranco Zanotti
 * @version 1.0
 * @since 2025-10-15
 */
@ExcludeTrace(reason = "NON utile")
public final class ConstData {
	private JsonNode constants;
	public JsonNode firstRows;
	private JsonNode neutralRow;
	// private JsonNode examSet;
	private JsonNode tokenList;
	// protected List<String> colNameFields = new ArrayList<>();

	public ConstData() {
		try (InputStream is = ConstData.class.getResourceAsStream("/dictConstants.json")) {
			ObjectMapper mapper = new ObjectMapper();
			constants = mapper.readTree(is);
		} catch (Exception e) {
			throw new RuntimeException("Errore nel caricamento di constants.json", e);
		}
		this.firstRows = constants.get("firstRows");
		this.neutralRow = constants.get("neutralRow");
		// this.examSet = constants.get("examSet");
		this.tokenList = constants.get("tokenList");
	}

	// ritorna tutto il file Json
	public JsonNode getConstants() {
		return constants;
	}

	// ritorna solo 1 oggetto del file es:
	// {"Variable / Field Name":{"2":"hlt_date","1":"lab_test","0":"record_id"},...
	public JsonNode getSection(String name) {
		return constants.get(name);
	}

	public List<String> getColNameFields() {
		List<String> keys = new ArrayList<>();
		this.neutralRow.fieldNames().forEachRemaining(keys::add);
		return keys;
	}

	public String getToken(String nameToken) {
		// return this.tokenList.get(nameToken).get("token").toString(); // SBAGLIATO
		// NON restituisce la stringa contenuta nel nodo, ma l’intero JSON serializzato.
		return this.tokenList.get(nameToken).get("token").asText();
	}

	/**
	 * questo main è di TEST
	 * 
	 * @param args
	 */

//	public static void main(String[] args) { 
//		  ConstData prova = new ConstData();
//	  
//		  System.out.println(prova.getConstants().toPrettyString()); //TUTTO il file
//		  System.out.println(prova.getSection("tokenList").findValue("survey")); //
//		  System.out.println(prova.neutralRow); //
//		  prova.neutralRow.fieldNames().forEachRemaining(System.out::println); // System.out.println(prova.neutralRow.keySet());
//		  
//		  System.out.println(prova.getColNameFields());
//		  
//		  // controllo il tipo di nodo // JsonNode node =
//		  prova.constants.get("tokenList"); JsonNode node
//		  =prova.getSection("tokenList").get("Survey eCRF");
//		  
//		  if (node.isObject()) { System.out.println("object"); } if (node.isArray()) {
//		  System.out.println("array"); } if (node.isTextual())
//		  {System.out.println("text"); } if (node.isInt()) { System.out.println("int");
//		  } if (node.isBoolean()) {System.out.println("bool"); }
//		  
//		  List<String> rigaZero = new ArrayList<>(); Iterator<String> columns = prova.getSection("firstRows").fieldNames();
//		  System.out.println(prova.getSection("firstRows").fieldNames()); while
//		  (columns.hasNext()) { String columnName = columns.next(); 
//		  JsonNode columnValues = prova.getSection("firstRows").get(columnName); // prendi l'elemento "0" // String value = columnValues.get("0").asText("");
//		  //deprecato String value = columnValues.has("0") ? columnValues.get("0").asText() : ""; rigaZero.add(value); 
//		  }
//		  System.out.println(rigaZero);
//		  
//		  int rowCount = prova.getSection("firstRows").size();
//		  System.out.println(rowCount);
//		  System.out.println(prova.getSection("firstRows").fieldNames());
//		  
//		  Iterator<String> columnNames = prova.getSection("firstRows").fieldNames();
//		  String firstColumn = columnNames.next(); int rowCount2 =
//		  prova.getSection("firstRows").get(firstColumn).size();
//		  System.out.println(String.valueOf(rowCount2));
//		  
//		  //System.out.println(prova.examSet.toPrettyString());
//		  System.out.println(prova.tokenList.toPrettyString());
//		  System.out.println("vediamo cosa tira fuori ");
//		  System.out.println(prova.tokenList.get("NuovaCRF_Bozza_base").toPrettyString(
//		  ));
//		  System.out.println(prova.tokenList.get("NuovaCRF_Bozza_base").get("token"));
//		  System.out.println(prova.getToken("NuovaCRF_Bozza_base"));
//	  
//	  }
	// end main
}
