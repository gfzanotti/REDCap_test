package utility_uml;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class CallGraphPuml {

	public static void main(String[] args) throws Exception {

		// 📌 Cartella sorgente
		String sourceDir = "src/main/java";

		// 📌 File output
		String outputFile = "target/callgraph.puml";

		Map<String, Set<String>> callGraph = new HashMap<>();

		// 🔍 Analizza tutti i file .java
		List<File> javaFiles = listJavaFiles(new File(sourceDir));

		for (File file : javaFiles) {
			try {
				CompilationUnit cu = StaticJavaParser.parse(file);

				cu.findAll(MethodDeclaration.class).forEach(method -> {

					@SuppressWarnings("unchecked")
					String className = method.findAncestor(ClassOrInterfaceDeclaration.class)
							.map(c -> c.getNameAsString()).orElse("UnknownClass");

					String caller = className + "." + method.getNameAsString();

					callGraph.putIfAbsent(caller, new HashSet<>());

					method.findAll(MethodCallExpr.class).forEach(call -> {
						String callee = call.getNameAsString();
						callGraph.get(caller).add(callee);
					});
				});

			} catch (Exception e) {
				System.err.println("Errore parsing file: " + file.getName());
			}
		}

		// 📝 Scrivi file PlantUML
		writePlantUML(callGraph, outputFile);

		System.out.println("✔ File generato: " + outputFile);
	}

	// 🔹 Lista ricorsiva file .java
	private static List<File> listJavaFiles(File dir) {
		List<File> files = new ArrayList<>();

		File[] list = dir.listFiles();
		if (list == null)
			return files;

		for (File f : list) {
			if (f.isDirectory()) {
				files.addAll(listJavaFiles(f));
			} else if (f.getName().endsWith(".java")) {
				files.add(f);
			}
		}
		return files;
	}

	// 🔹 Scrittura PlantUML
	private static void writePlantUML(Map<String, Set<String>> callGraph, String outputFile) throws IOException {

		try (FileWriter writer = new FileWriter(outputFile)) {

			writer.write("@startuml\n");
			writer.write("title Call Graph\n\n");

			for (String caller : callGraph.keySet()) {
				for (String callee : callGraph.get(caller)) {
					writer.write("\"" + caller + "\" --> \"" + callee + "\"\n");
				}
			}

			writer.write("\n@enduml\n");
		}
	}
}