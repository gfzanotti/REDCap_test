package mi.istitutotumori.e_crf.utils;

import java.util.List;

import mi.istitutotumori.tracing.ExcludeTrace;

@ExcludeTrace(reason = "NON utile")
public class CsvSerializer {

	public String serialize(List<List<String>> table) {
		StringBuilder sb = new StringBuilder();

		for (List<String> row : table) {
			sb.append(serializeRow(row)).append("\n");
		}

		return sb.toString();
	}

	private String serializeRow(List<String> row) {
		return row.stream().map(this::escape)
				// .reduce((a, b) -> a + ";" + b) // determina il separatore
				// .reduce((a, b) -> a + "\t" + b) // determina il separatore
				.reduce((a, b) -> a + "," + b) // determina il separatore
				.orElse("");
	}

	private String escape(String value) {
		if (value == null)
			return "\"\"";
		String escaped = value.replace("\"", "\"\"");
		return "\"" + escaped + "\"";
	}
}
