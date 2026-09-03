package mi.istitutotumori.e_crf.labutils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import mi.istitutotumori.tracing.ExcludeTrace;

@ExcludeTrace(reason = "escludo intera classe HtmlNestedTableBuilder")
class HtmlNestedTableBuilder {

	Map<String, String> buildHtml(List<List<String>> nestedData) {
		// raggruppo gli esami in sottoinsiemi prendendo il valore del primo parametro
		// della lista
		// es: "Lab test"
		Map<String, List<List<String>>> grouped = new LinkedHashMap<>();
		for (List<String> row : nestedData) {
			grouped.computeIfAbsent(row.get(0), k -> new ArrayList<>()).add(row.subList(1, row.size()));
		}

		Map<String, String> result = new LinkedHashMap<>();

		for (var entry : grouped.entrySet()) {
			StringBuilder html = new StringBuilder();
			html.append(String.format("<div class=\"rich-text-field-label\">"
					+ "<table style=\"border-collapse:collapse; width:100%%; height:auto;\" border=\"1\">"
					+ "<tbody><tr style=\"height:22px;\">"
					+ "<td style=\"width:16.6%%; border-color:#ced4d9; border-style:dotted; height:22px;\">%s</td>"
					+ "<td style=\"width:16.6%%; border-color:#ced4d9; border-style:dotted; height:22px;\">Result</td>"
					+ "<td style=\"width:16.6%%; border-color:#ced4d9; border-style:dotted; height:22px;\">Unit</td>"
					+ "<td style=\"width:16.6%%; border-color:#ced4d9; border-style:dotted; height:22px;\">Range min</td>"
					+ "<td style=\"width:16.6%%; border-color:#ced4d9; border-style:dotted; height:22px;\">Range max</td>"
					+ "<td style=\"width:16.6%%; border-color:#ced4d9; border-style:dotted; height:22px;\">Out of range</td></tr>",
					entry.getKey()));

			for (List<String> row : entry.getValue()) {
				html.append(String.format("<tr style=\"height:22px;\">"
						+ "<td style=\"width:16.6%%; border-color:#ced4d9; border-style:dotted; height:22px;\">%s</td>"
						+ "<td style=\"width:16.6%%; border-color:#ced4d9; border-style:dotted; height:22px;\">{%s}</td>"
						+ "<td style=\"width:16.6%%; border-color:#ced4d9; border-style:dotted; height:22px;\">{%s}</td>"
						+ "<td style=\"width:16.6%%; border-color:#ced4d9; border-style:dotted; height:22px;\">{%s}</td>"
						+ "<td style=\"width:16.6%%; border-color:#ced4d9; border-style:dotted; height:22px;\">{%s}</td>"
						+ "<td style=\"width:16.6%%; border-color:#ced4d9; border-style:dotted; height:22px;\">{%s}</td></tr>",
						row.get(0), row.get(1), row.get(2), row.get(3), row.get(4), row.get(5)));
			} // OK
				// Fine HTML della tabella parziale
			html.append("</tbody></table></div><br>");

			// aggiunto il 17.04.2026 per la creazini di field separati
			result.put(entry.getKey(), html.toString());
		}
		return result;
	}

	// versione NonInt
	Map<String, String> buildHtmlNonInt(List<List<String>> nestedData) {
		Map<String, List<List<String>>> grouped = new LinkedHashMap<>();
		for (List<String> row : nestedData) {
			grouped.computeIfAbsent(row.get(0), k -> new ArrayList<>()).add(row.subList(1, row.size()));
		}

		Map<String, String> resultNonInt = new LinkedHashMap<>();

		for (var entry : grouped.entrySet()) {
			StringBuilder html = new StringBuilder();
			html.append(String.format("<div class=\"rich-text-field-label\">"
					+ "<table style=\"border-collapse:collapse; width:100%%; height:auto;\" border=\"1\">"
					+ "<tbody><tr style=\"height:22px;\">"
					+ "<td style=\"width:16.6%%; border-color:#ced4d9; border-style:dotted; height:22px;\">%s</td>"
					// + "<td style=\"width:16.6%%; border-color:#ced4d9; border-style:dotted;
					// height:22px;\">Result</td>"
					+ "<td style=\"width:16.6%%; border-color:#ced4d9; border-style:dotted; height:22px;\">Unit</td>"
					+ "<td style=\"width:16.6%%; border-color:#ced4d9; border-style:dotted; height:22px;\">Range min</td>"
					+ "<td style=\"width:16.6%%; border-color:#ced4d9; border-style:dotted; height:22px;\">Range max</td>",
					// + "<td style=\"width:16.6%%; border-color:#ced4d9; border-style:dotted;
					// height:22px;\">Out of range</td></tr>",
					entry.getKey()));

			for (List<String> row : entry.getValue()) {
				html.append(String.format("<tr style=\"height:22px;\">"
						+ "<td style=\"width:16.6%%; border-color:#ced4d9; border-style:dotted; height:22px;\">%s</td>"
						// +"<td style=\"width:16.6%%; border-color:#ced4d9; border-style:dotted;
						// height:22px;\">{%s}</td>"
						+ "<td style=\"width:16.6%%; border-color:#ced4d9; border-style:dotted; height:22px;\">{%s}</td>"
						+ "<td style=\"width:16.6%%; border-color:#ced4d9; border-style:dotted; height:22px;\">{%s}</td>"
						+ "<td style=\"width:16.6%%; border-color:#ced4d9; border-style:dotted; height:22px;\">{%s}</td></tr>",
						// +"<td style=\"width:16.6%%; border-color:#ced4d9; border-style:dotted;
						// height:22px;\">{%s}</td></tr>",
						// row.get(0), row.get(2), row.get(3), row.get(4)));
						row.get(0), row.get(1), row.get(2), row.get(3)));
			} // OK
				// Fine HTML della tabella parziale
			html.append("</tbody></table></div><br>");

			// aggiunto il 17.04.2026 per la creazini di field separati
			resultNonInt.put(entry.getKey(), html.toString());
		}
		return resultNonInt;
	}

}
