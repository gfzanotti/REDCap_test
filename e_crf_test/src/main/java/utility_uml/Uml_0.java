//package com.example.uml;
package utility_uml;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.github.javaparser.resolution.types.ResolvedType;
// symbol solver
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.javaparsermodel.JavaParserFacade;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;

public class Uml_0 {

	private static final Map<String, ClassOrInterfaceDeclaration> classes = new HashMap<>();
	private static final Set<String> relations = new LinkedHashSet<>();
	private static final Map<String, Set<String>> inheritanceHierarchy = new HashMap<>();

	// Lista dei package da escludere (può essere passata come argomento o
	// hardcoded)
	private static final Set<String> excludedPackages = new HashSet<>();

	// Mappa per memorizzare il package di ogni classe
	private static final Map<String, String> classToPackageMap = new HashMap<>();

	private static JavaParserFacade javaTypeSolver;

	// ----------------------------------------------------
	// MAIN
	// ----------------------------------------------------
	public static void main(String[] args) throws Exception {

		if (args.length < 2) {
			System.out.println("USO: java Uml_0 <src-dir> <output.puml> [package1,package2,...]");
			System.out.println("\nEsempi:");
			System.out.println("  java Uml_0 src output.puml");
			System.out.println("  java Uml_0 src output.puml com.example.test,com.example.utils");
			return;
		}

		Path srcDir = Paths.get(args[0]);
		Path output = Paths.get(args[1]);

		// Gestisci i package da escludere se specificati
		if (args.length > 2) {
			String[] packages = args[2].split(",");
			excludedPackages.addAll(Arrays.asList(packages));
			System.out.println("Package esclusi: " + excludedPackages);
		} else {
			// Puoi anche definire qui dei package hardcoded da escludere
			excludedPackages.addAll(
					Arrays.asList("com.example.uml", "mi.istitutotumori.app.controller", "mi.istitutotumori.app.model",
							// "mi.istitutotumori.app",
							"utility"));
		}

		setupSymbolSolver(srcDir);
		scanAllJavaFiles(srcDir);

		// Rimuovi le classi che appartengono ai package esclusi
		removeExcludedClasses();

		buildInheritanceHierarchy();
		analyzeInheritedRelationships();
		analyzeDirectRelationships();
		analyzeMethodCalls();

		writePlantUML(output);

		System.out.println("Diagramma UML generato: " + output.toAbsolutePath());
		System.out.println("Classi analizzate: " + classes.size());
		System.out.println("Relazioni trovate: " + relations.size());
	}

	// ----------------------------------------------------
	// SYMBOL SOLVER
	// ----------------------------------------------------
	private static void setupSymbolSolver(Path srcDir) {
		CombinedTypeSolver solver = new CombinedTypeSolver(new ReflectionTypeSolver(),
				new JavaParserTypeSolver(srcDir.toFile()));

		ParserConfiguration config = new ParserConfiguration().setSymbolResolver(new JavaSymbolSolver(solver))
				.setLanguageLevel(ParserConfiguration.LanguageLevel.JAVA_17);

		StaticJavaParser.setConfiguration(config);
		javaTypeSolver = JavaParserFacade.get(solver);
	}

	// ----------------------------------------------------
	// PARSING CON CONTROLLO PACKAGE
	// ----------------------------------------------------
	private static void scanAllJavaFiles(Path root) throws IOException {
		Files.walk(root).filter(p -> p.toString().endsWith(".java")).filter(p -> !isFileInExcludedPackage(p))
				.forEach(Uml_0::parseClass);
	}

	private static boolean isFileInExcludedPackage(Path javaFile) {
		try {
			String packageName = getPackageNameFromFile(javaFile);
			if (packageName == null) {
				return false; // File senza package
			}

			// Controlla se il package è nella lista degli esclusi
			for (String excludedPkg : excludedPackages) {
				if (packageName.equals(excludedPkg) || packageName.startsWith(excludedPkg + ".")) {
					System.out.println("File escluso: " + javaFile + " (package: " + packageName + ")");
					return true;
				}
			}
			return false;

		} catch (IOException e) {
			System.err.println("Errore lettura file: " + javaFile);
			return false;
		}
	}

	private static String getPackageNameFromFile(Path javaFile) throws IOException {
		String content = Files.readString(javaFile);
		int packageIndex = content.indexOf("package ");
		if (packageIndex == -1) {
			return null;
		}

		int endOfLine = content.indexOf(";", packageIndex);
		if (endOfLine == -1) {
			return null;
		}

		return content.substring(packageIndex + 8, endOfLine).trim();
	}

	private static void parseClass(Path file) {
		try {
			CompilationUnit cu = StaticJavaParser.parse(file);

			// Ottieni il package del file
			String packageName = cu.getPackageDeclaration().map(p -> p.getNameAsString()).orElse("(default)");

			// Aggiungi tutte le classi/interfacce del file
			cu.findAll(ClassOrInterfaceDeclaration.class).forEach(c -> {
				String className = c.getNameAsString();
				classes.put(className, c);
				classToPackageMap.put(className, packageName);
			});

		} catch (Exception e) {
			System.err.println("Errore parsing: " + file);
		}
	}

	// Rimuovi le classi che appartengono ai package esclusi
	private static void removeExcludedClasses() {
		Set<String> classesToRemove = new HashSet<>();

		for (Map.Entry<String, String> entry : classToPackageMap.entrySet()) {
			String className = entry.getKey();
			String packageName = entry.getValue();

			for (String excludedPkg : excludedPackages) {
				if (packageName.equals(excludedPkg) || packageName.startsWith(excludedPkg + ".")) {
					classesToRemove.add(className);
					break;
				}
			}
		}

		// Rimuovi le classi dalla mappa principale
		for (String className : classesToRemove) {
			classes.remove(className);
			classToPackageMap.remove(className);
		}

		if (!classesToRemove.isEmpty()) {
			System.out.println("Classi rimosse (package esclusi): " + classesToRemove.size());
		}
	}

	// ----------------------------------------------------
	// PACKAGE UTILS
	// ----------------------------------------------------
	private static String getPackageName(ClassOrInterfaceDeclaration c) {
		String className = c.getNameAsString();
		return classToPackageMap.getOrDefault(className, "(default)");
	}

	private static String detectLayer(String pkg) {
		if (pkg.contains(".domain") || pkg.endsWith(".domain"))
			return "domain";
		// if (pkg.contains(".service") || pkg.endsWith(".service")) return "service";
		if (pkg.contains(".infra") || pkg.contains(".infrastructure"))
			return "infrastructure";
		if (pkg.contains(".controller") || pkg.endsWith(".controller"))
			return "controller";
		if (pkg.contains(".repository") || pkg.endsWith(".repository"))
			return "repository";
		if (pkg.contains(".model") || pkg.endsWith(".model"))
			return "model";
		if (pkg.contains(".dto") || pkg.endsWith(".dto"))
			return "dto";
		if (pkg.contains(".config") || pkg.endsWith(".config"))
			return "config";
		return "other";
	}

	private static String layerColor(String layer) {
		return switch (layer) {
		case "domain" -> "#FFF2CC"; // Giallo chiaro
		// case "service" -> "#E3F2FD"; // Blu chiaro
		case "controller" -> "#FFEBEE"; // Rosso chiaro
		case "repository" -> "#F3E5F5"; // Viola chiaro
		case "model" -> "#E8F5E9"; // Verde chiaro
		case "dto" -> "#FFF8E1"; // Ambra chiaro
		case "config" -> "#ECEFF1"; // Grigio bluastro chiaro
		case "infrastructure" -> "#E0F2F1"; // Ciano chiaro
		default -> "#FAFAFA"; // Bianco sporco
		};
	}

	// ----------------------------------------------------
	// INHERITANCE
	// ----------------------------------------------------
	private static void buildInheritanceHierarchy() {
		for (ClassOrInterfaceDeclaration c : classes.values()) {
			inheritanceHierarchy.put(c.getNameAsString(), new HashSet<>());
			for (ClassOrInterfaceType ext : c.getExtendedTypes()) {
				inheritanceHierarchy.get(c.getNameAsString()).add(ext.getNameAsString());
			}
		}
	}

	// ----------------------------------------------------
	// RELATIONSHIPS (FILTRATE PER PACKAGE ESCLUSI)
	// ----------------------------------------------------
	private static void analyzeInheritedRelationships() {
		for (ClassOrInterfaceDeclaration c : classes.values()) {
			analyzeFields(c.getNameAsString(), c.getFields());
		}
	}

	private static void analyzeDirectRelationships() {
		analyzeInheritedRelationships();
	}

	private static void analyzeFields(String source, List<FieldDeclaration> fields) {
		for (FieldDeclaration f : fields) {
			try {
				ResolvedType t = javaTypeSolver.getType(f.getElementType());
				if (t.isReferenceType()) {
					String target = extractSimpleName(t.describe());

					// Controlla se la classe target esiste e non è in un package escluso
					if (classes.containsKey(target) && !target.equals(source) && !isClassInExcludedPackage(target)) {
						relations.add(source + " --> " + target);
					}
				}
			} catch (Exception ignored) {
			}
		}
	}

	private static void analyzeMethodCalls() {
		for (ClassOrInterfaceDeclaration c : classes.values()) {
			String caller = c.getNameAsString();

			// Salta se il chiamante è in un package escluso
			if (isClassInExcludedPackage(caller)) {
				continue;
			}

			c.findAll(MethodCallExpr.class).forEach(call -> {
				try {
					ResolvedMethodDeclaration decl = javaTypeSolver.solve(call).getCorrespondingDeclaration();
					String target = decl.declaringType().getClassName();

					// Controlla se la classe target esiste, non è la stessa e non è in package
					// escluso
					if (classes.containsKey(target) && !target.equals(caller) && !isClassInExcludedPackage(target)) {
						relations.add(caller + " ..> " + target + " : calls");
					}
				} catch (Exception ignored) {
				}
			});
		}
	}

	// Controlla se una classe è in un package escluso
	private static boolean isClassInExcludedPackage(String className) {
		String packageName = classToPackageMap.get(className);
		if (packageName == null) {
			return false;
		}

		for (String excludedPkg : excludedPackages) {
			if (packageName.equals(excludedPkg) || packageName.startsWith(excludedPkg + ".")) {
				return true;
			}
		}
		return false;
	}

	private static String extractSimpleName(String q) {
		return q.contains(".") ? q.substring(q.lastIndexOf('.') + 1) : q;
	}

	// ----------------------------------------------------
	// OUTPUT PLANTUML
	// ----------------------------------------------------
	private static void writePlantUML(Path output) throws IOException {

		// Raggruppa le classi per layer e poi per package
		Map<String, Map<String, List<ClassOrInterfaceDeclaration>>> byLayer = new TreeMap<>();

		for (ClassOrInterfaceDeclaration c : classes.values()) {
			// Salta le classi in package esclusi (doppio controllo)
			if (isClassInExcludedPackage(c.getNameAsString())) {
				continue;
			}

			String pkg = getPackageName(c);
			String layer = detectLayer(pkg);

			byLayer.computeIfAbsent(layer, k -> new TreeMap<>()).computeIfAbsent(pkg, k -> new ArrayList<>()).add(c);
		}

		try (BufferedWriter w = Files.newBufferedWriter(output)) {

			w.write("@startuml\n");
			w.write("skinparam classAttributeIconSize 0\n");
			w.write("skinparam packageStyle rectangle\n");
			w.write("skinparam packageBorderColor #333333\n");
			w.write("skinparam packageFontSize 14\n");
			w.write("skinparam classFontSize 12\n");
			w.write("skinparam classBorderColor #666666\n");
			w.write("left to right direction\n\n");

			// Scrivi una legenda
			w.write("' LEGENDA\n");
			w.write("legend right\n");
			w.write("  |Layer|Colore|\n");
			w.write("  |Domain|<#FFF2CC>|\n");
			// w.write(" |Service|<#E3F2FD>|\n");
			w.write("  |Controller|<#FFEBEE>|\n");
			w.write("  |Repository|<#F3E5F5>|\n");
			w.write("  |Model|<#E8F5E9>|\n");
			w.write("endlegend\n\n");

			for (var layerEntry : byLayer.entrySet()) {
				String layer = layerEntry.getKey();
				String color = layerColor(layer);

				w.write("package \"" + layer.toUpperCase() + "\" " + color + " {\n");

				for (var pkgEntry : layerEntry.getValue().entrySet()) {
					String pkg = pkgEntry.getKey();

					// Formatta il nome del package per visualizzazione
					String displayPkg = pkg.length() > 40 ? "..." + pkg.substring(pkg.length() - 37) : pkg;

					w.write("  package \"" + displayPkg + "\" {\n");

					pkgEntry.getValue().stream()
							.sorted(Comparator.comparing(ClassOrInterfaceDeclaration::getNameAsString)).forEach(c -> {
								try {
									String className = c.getNameAsString();
									if (c.isInterface()) {
										w.write("    interface " + className + " <<interface>>\n");
									} else if (c.isAbstract()) {
										w.write("    abstract class " + className + " <<abstract>>\n");
									} else {
										w.write("    class " + className + "\n");
									}
								} catch (IOException e) {
									e.printStackTrace();
								}
							});

					w.write("  }\n");
				}
				w.write("}\n\n");
			}

			// Scrivi le relazioni (filtrando quelle che coinvolgono classi escluse)
			w.write("' RELAZIONI\n");
			for (String r : relations) {
				// Estrai le classi dalla relazione
				String[] parts = r.split(" ");
				if (parts.length >= 3) {
					String sourceClass = parts[0];
					String targetClass = parts[2].replace(":", "").trim();

					// Includi solo se entrambe le classi non sono escluse
					if (!isClassInExcludedPackage(sourceClass) && !isClassInExcludedPackage(targetClass)) {
						w.write(r + "\n");
					}
				}
			}

			w.write("\n@enduml\n");
		}
	}
}