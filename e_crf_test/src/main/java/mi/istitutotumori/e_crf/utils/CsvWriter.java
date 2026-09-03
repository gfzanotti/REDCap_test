package mi.istitutotumori.e_crf.utils;

import java.io.*;
import java.nio.file.*;

public class CsvWriter {

	public void write(String csv, String filename) throws IOException {
		Path path = Paths.get(filename);
		Files.createDirectories(path.getParent());
		Files.writeString(path, csv);
	}
}
