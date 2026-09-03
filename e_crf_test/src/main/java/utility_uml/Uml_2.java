//package com.example.uml;
package utility_uml;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.github.javaparser.resolution.types.ResolvedType;
import com.github.javaparser.resolution.types.ResolvedReferenceType;

// symbol solver
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.javaparsermodel.JavaParserFacade;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;

public class Uml_2 {

	// Classe per rappresentare una classe con il suo package
	private static class ClassInfo {
		String name;
		ClassOrInterfaceDeclaration declaration;
		String packageName;

		ClassInfo(String name, ClassOrInterfaceDeclaration declaration, String packageName) {
			this.name = name;
			this.declaration = declaration;
			this.packageName = packageName;
		}
	}

	// Mappa: nome classe -> ClassInfo
	private static final Map<String, ClassInfo> classInfoMap = new HashMap<>();
	private static final Set<String> relations = new LinkedHashSet<>();
	private static final Map<String, Set<String>> inheritanceHierarchy = new HashMap<>();

	// Lista dei package da escludere
	private static final Set<String> excludedPackages = new HashSet<>(Arrays.asList("com.example.uml",
			"mi.istitutotumori.app.controller", "mi.istitutotumori.app.model", "mi.istitutotumori.app"));

	// Mappa per raggruppare classi per package
	private static final Map<String, Set<String>> packageToClasses = new HashMap<>();

	private static JavaParserFacade javaTypeSolver;

	public static void main(String[] args) throws Exception {

		if (args.length < 2) {
			System.out.println("USO: java UmlWithPackages <directory-sorgente> <output.puml>");
			System.out.println("Package esclusi automaticamente:");
			for (String pkg : excludedPackages) {
				System.out.println("  - " + pkg);
			}
			return;
		}

		Path srcDir = Paths.get(args[0]);
		Path output = Paths.get(args[1]);

		System.out.println("Analisi in corso...");
		System.out.println("Package esclusi dall'analisi:");
		for (String pkg : excludedPackages) {
			System.out.println("  - " + pkg);
		}

		setupSymbolSolver(srcDir);
		scanAllJavaFiles(srcDir);

		// Costruisci la mappa package -> classi
		buildPackageMap();

		System.out.println("\nPackage trovati:");
		for (String pkg : packageToClasses.keySet()) {
			System.out.println("  - " + pkg + " (" + packageToClasses.get(pkg).size() + " classi)");
		}

		buildInheritanceHierarchy();
		analyzeInheritedRelationships();
		analyzeDirectRelationships();
		analyzeMethodCalls();

		writePlantUMLWithPackages(output);

		System.out.println("\nDiagramma UML generato: " + output.toAbsolutePath());
		System.out.println("Classi analizzate: " + classInfoMap.size());
		System.out.println("Relazioni trovate: " + relations.size());
	}

	// ----------------------------------------------------
	// SYMBOL SOLVER
	// ----------------------------------------------------
	private static void setupSymbolSolver(Path srcDir) {
		CombinedTypeSolver typeSolver = new CombinedTypeSolver(new ReflectionTypeSolver(),
				new JavaParserTypeSolver(srcDir.toFile()));

		ParserConfiguration config = new ParserConfiguration().setSymbolResolver(new JavaSymbolSolver(typeSolver))
				.setLanguageLevel(ParserConfiguration.LanguageLevel.JAVA_17);

		StaticJavaParser.setConfiguration(config);

		javaTypeSolver = JavaParserFacade.get(typeSolver);
	}

	// ----------------------------------------------------
	// SCANSIONE FILE .java
	// ----------------------------------------------------
	private static void scanAllJavaFiles(Path root) throws IOException {
		Files.walk(root).filter(f -> f.toString().endsWith(".java")).filter(f -> !isInExcludedPackage(f))
				.forEach(Uml_2::parseClass);
	}

	private static boolean isInExcludedPackage(Path javaFile) {
		try {
			String packageName = getPackageNameFromFile(javaFile);
			if (packageName == null) {
				return false;
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
			return null; // File senza package
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
			String packageName = cu.getPackageDeclaration().map(pkg -> pkg.getNameAsString()).orElse("default");

			// Verifica se il package è escluso
			for (String excludedPkg : excludedPackages) {
				if (packageName.equals(excludedPkg) || packageName.startsWith(excludedPkg + ".")) {
					return; // Salta questo file
				}
			}

			// Aggiungi tutte le classi/interfacce del file
			cu.findAll(ClassOrInterfaceDeclaration.class).forEach(c -> {
				String className = c.getNameAsString();
				classInfoMap.put(className, new ClassInfo(className, c, packageName));
			});

		} catch (Exception e) {
			System.err.println("Errore parsing: " + file + " -> " + e.getMessage());
		}
	}

	// ----------------------------------------------------
	// COSTRUISCI MAPPA PACKAGE -> CLASSI
	// ----------------------------------------------------
	private static void buildPackageMap() {
		for (ClassInfo classInfo : classInfoMap.values()) {
			String packageName = classInfo.packageName;
			packageToClasses.computeIfAbsent(packageName, k -> new HashSet<>()).add(classInfo.name);
		}
	}

	// ----------------------------------------------------
	// GERARCHIA DI EREDITARIETÀ
	// ----------------------------------------------------
	private static void buildInheritanceHierarchy() {
		for (ClassInfo classInfo : classInfoMap.values()) {
			String className = classInfo.name;

			inheritanceHierarchy.put(className, new HashSet<>());
			ClassOrInterfaceDeclaration c = classInfo.declaration;

			// Aggiungi superclassi dirette
			for (ClassOrInterfaceType ext : c.getExtendedTypes()) {
				String superClass = ext.getNameAsString();
				inheritanceHierarchy.get(className).add(superClass);
				propagateInheritance(className, superClass);
			}
		}
	}

	private static void propagateInheritance(String child, String parent) {
		if (inheritanceHierarchy.containsKey(parent)) {
			for (String grandParent : inheritanceHierarchy.get(parent)) {
				inheritanceHierarchy.get(child).add(grandParent);
				propagateInheritance(child, grandParent);
			}
		}
	}

	// ----------------------------------------------------
	// ANALIZZA RELAZIONI
	// ----------------------------------------------------
	private static void analyzeInheritedRelationships() {
		for (ClassInfo classInfo : classInfoMap.values()) {
			String className = classInfo.name;
			ClassOrInterfaceDeclaration c = classInfo.declaration;

			analyzeFieldsForRelationships(className, c.getFields());

			if (inheritanceHierarchy.containsKey(className)) {
				for (String superClass : inheritanceHierarchy.get(className)) {
					ClassInfo parentInfo = classInfoMap.get(superClass);
					if (parentInfo != null) {
						analyzeFieldsForRelationships(className, parentInfo.declaration.getFields());
					}
				}
			}
		}
	}

	private static void analyzeDirectRelationships() {
		for (ClassInfo classInfo : classInfoMap.values()) {
			analyzeFieldsForRelationships(classInfo.name, classInfo.declaration.getFields());
		}
	}

	private static void analyzeFieldsForRelationships(String className, List<FieldDeclaration> fields) {
		for (FieldDeclaration f : fields) {
			try {
				ResolvedType resolvedType = javaTypeSolver.getType(f.getElementType());
				List<String> targetTypes = new ArrayList<>();

				if (resolvedType.isReferenceType()) {
					ResolvedReferenceType refType = resolvedType.asReferenceType();

					if (refType.typeParametersValues().size() > 0) {
						for (ResolvedType typeParam : refType.typeParametersValues()) {
							if (typeParam.isReferenceType()) {
								String typeName = typeParam.asReferenceType().getQualifiedName();
								String simpleName = extractSimpleName(typeName);

								if (classInfoMap.containsKey(simpleName)) {
									targetTypes.add(simpleName);
								}
							}
						}
					} else {
						String typeName = refType.getQualifiedName();
						String simpleName = extractSimpleName(typeName);

						if (classInfoMap.containsKey(simpleName)) {
							targetTypes.add(simpleName);
						}
					}
				}

				for (String target : targetTypes) {
					if (target.equals(className))
						continue;

					boolean isCollection = isCollectionType(f.getElementType());
					boolean isFinal = f.isFinal();
					boolean isPrivate = f.isPrivate();
					String fieldName = getFieldName(f);

					String inheritedNote = "";
					ClassInfo classInfo = classInfoMap.get(className);
					if (classInfo != null && !classInfo.declaration.getFields().contains(f)) {
						inheritedNote = " (inherited)";
					}

					if (isCollection && isFinal) {
						relations.add(className + " *-- \"*\" " + target + " : " + fieldName + inheritedNote);
					} else if (isCollection) {
						relations.add(className + " \"1\" --> \"*\" " + target + " : " + fieldName + inheritedNote);
					} else if (isFinal && isPrivate) {
						relations.add(className + " *-- " + target + " : " + fieldName + inheritedNote);
					} else {
						relations.add(className + " --> " + target + " : " + fieldName + inheritedNote);
					}
				}

			} catch (Exception e) {
				// Fallback
				try {
					String typeStr = f.getElementType().toString();
					if (typeStr.contains("<") && typeStr.contains(">")) {
						int start = typeStr.indexOf('<') + 1;
						int end = typeStr.lastIndexOf('>');
						if (start < end) {
							String innerType = typeStr.substring(start, end).trim();
							if (innerType.contains("<"))
								innerType = innerType.substring(0, innerType.indexOf('<'));
							if (innerType.contains(","))
								innerType = innerType.substring(0, innerType.indexOf(','));
							innerType = innerType.trim();

							if (classInfoMap.containsKey(innerType) && !innerType.equals(className)) {
								boolean isCollection = isCollectionType(f.getElementType());
								boolean isFinal = f.isFinal();
								String fieldName = getFieldName(f);

								if (isCollection && isFinal) {
									relations.add(className + " *-- \"*\" " + innerType + " : " + fieldName);
								} else if (isCollection) {
									relations.add(className + " \"1\" --> \"*\" " + innerType + " : " + fieldName);
								} else {
									relations.add(className + " --> " + innerType + " : " + fieldName);
								}
							}
						}
					} else if (classInfoMap.containsKey(typeStr) && !typeStr.equals(className)) {
						boolean isFinal = f.isFinal();
						boolean isPrivate = f.isPrivate();
						String fieldName = getFieldName(f);

						if (isFinal && isPrivate) {
							relations.add(className + " *-- " + typeStr + " : " + fieldName);
						} else {
							relations.add(className + " --> " + typeStr + " : " + fieldName);
						}
					}
				} catch (Exception e2) {
					// Ignora
				}
			}
		}
	}

	private static String extractSimpleName(String qualifiedName) {
		if (qualifiedName.contains(".")) {
			return qualifiedName.substring(qualifiedName.lastIndexOf('.') + 1);
		}
		return qualifiedName;
	}

	private static boolean isCollectionType(Type type) {
		String typeStr = type.toString();
		return typeStr.contains("List<") || typeStr.contains("Set<") || typeStr.contains("Collection<")
				|| typeStr.contains("Map<") || typeStr.contains("ArrayList<") || typeStr.contains("HashSet<")
				|| typeStr.contains("LinkedList<") || typeStr.contains("Vector<");
	}

	private static String getFieldName(FieldDeclaration f) {
		if (f.getVariables().size() > 0) {
			return f.getVariables().get(0).getNameAsString();
		}
		return "";
	}

	// ----------------------------------------------------
	// CHIAMATE DI METODO
	// ----------------------------------------------------
	private static void analyzeMethodCalls() {
		for (ClassInfo classInfo : classInfoMap.values()) {
			String caller = classInfo.name;

			classInfo.declaration.findAll(MethodCallExpr.class).forEach(call -> {
				try {
					ResolvedMethodDeclaration decl = javaTypeSolver.solve(call).getCorrespondingDeclaration();
					String targetClass = decl.declaringType().getClassName();

					if (classInfoMap.containsKey(targetClass) && !targetClass.equals(caller)) {
						relations.add(caller + " ..> " + targetClass + " : calls");
					}
				} catch (Exception ignored) {
				}
			});
		}
	}

	// ----------------------------------------------------
	// OUTPUT PLANTUML CON PACKAGE
	// ----------------------------------------------------
	private static void writePlantUMLWithPackages(Path output) throws IOException {
		try (BufferedWriter w = Files.newBufferedWriter(output)) {

			w.write("@startuml\n");
			w.write("skinparam classAttributeIconSize 0\n");
			w.write("skinparam linetype ortho\n");
			w.write("skinparam nodesep 50\n");
			w.write("skinparam ranksep 50\n");
			w.write("skinparam packageBackgroundColor #E6F3FF\n");
			w.write("skinparam packageBorderColor #3399FF\n");
			w.write("skinparam packageFontColor #003366\n\n");

			// 1. DEFINISCI I PACKAGE
			w.write("' ====================================\n");
			w.write("' PACKAGE\n");
			w.write("' ====================================\n\n");

			// Crea una mappa per ordinare i package per profondità
			Map<String, Integer> packageDepth = new HashMap<>();
			for (String pkg : packageToClasses.keySet()) {
				packageDepth.put(pkg, pkg.split("\\.").length);
			}

			// Ordina i package dalla radice alle foglie
			List<String> sortedPackages = new ArrayList<>(packageToClasses.keySet());
			sortedPackages.sort((p1, p2) -> {
				int depthCompare = Integer.compare(packageDepth.get(p1), packageDepth.get(p2));
				if (depthCompare != 0)
					return depthCompare;
				return p1.compareTo(p2);
			});

			// Scrivi i package
			for (String pkg : sortedPackages) {
				String packageDisplayName = pkg.equals("default") ? "Default Package" : pkg;
				w.write("package \"" + packageDisplayName + "\" {\n");

				// Scrivi le classi di questo package
				for (String className : packageToClasses.get(pkg)) {
					ClassInfo classInfo = classInfoMap.get(className);
					if (classInfo != null) {
						writeClassInPackage(w, classInfo);
					}
				}

				w.write("}\n\n");
			}

			// 2. EREDITARIETÀ E IMPLEMENTAZIONI (fuori dai package per visibilità)
			w.write("' ====================================\n");
			w.write("' EREDITARIETÀ E IMPLEMENTAZIONI\n");
			w.write("' ====================================\n\n");

			for (ClassInfo classInfo : classInfoMap.values()) {
				String className = classInfo.name;
				ClassOrInterfaceDeclaration c = classInfo.declaration;

				for (ClassOrInterfaceType ext : c.getExtendedTypes()) {
					String superClass = ext.getNameAsString();
					if (classInfoMap.containsKey(superClass)) {
						w.write(superClass + " <|-- " + className + "\n");
					}
				}

				for (ClassOrInterfaceType impl : c.getImplementedTypes()) {
					String interfaceName = impl.getNameAsString();
					if (classInfoMap.containsKey(interfaceName)) {
						w.write(interfaceName + " <|.. " + className + "\n");
					}
				}
			}

			// 3. RELAZIONI TRA CLASSI
			w.write("\n' ====================================\n");
			w.write("' RELAZIONI TRA CLASSI\n");
			w.write("' ====================================\n\n");

			for (String r : relations) {
				w.write(r + "\n");
			}

			// 4. LEGENDA E NOTE
			w.write("\n' ====================================\n");
			w.write("' LEGENDA\n");
			w.write("' ====================================\n");
			w.write("legend right\n");
			w.write("  | Colore - Simbolo | Significato |\n");
			w.write("  |<#E6F3FF>| Package |\n");
			w.write("  |--|Associazione|\n");
			w.write("  |*--|Composizione|\n");
			w.write("  |o--|Aggregazione|\n");
			w.write("  |<--|Ereditarietà|\n");
			w.write("  |<..|Implementazione|\n");
			w.write("  |..>|Dipendenza (chiamata)|\n");
			w.write("endlegend\n\n");

			w.write("@enduml\n");
		}
	}

	private static void writeClassInPackage(BufferedWriter w, ClassInfo classInfo) throws IOException {
		ClassOrInterfaceDeclaration c = classInfo.declaration;
		String name = classInfo.name;

		if (c.isInterface()) {
			w.write("  interface " + name + " <<interface>> {\n");
		} else if (c.isAbstract()) {
			w.write("  abstract class " + name + " <<abstract>> {\n");
		} else {
			w.write("  class " + name + " {\n");
		}

		// CAMPI (solo i primi 3 per leggibilità)
		int fieldCount = 0;
		for (FieldDeclaration f : c.getFields()) {
			if (fieldCount < 3) {
				String fieldType = f.getElementType().toString();
				String fieldName = f.getVariables().get(0).getNameAsString();
				String visibility = getVisibility(f);
				w.write("    " + visibility + fieldType + " " + fieldName + "\n");
				fieldCount++;
			} else {
				w.write("    ...\n");
				break;
			}
		}

		// METODI (solo i primi 3 per leggibilità)
		int methodCount = 0;
		for (MethodDeclaration m : c.getMethods()) {
			if (methodCount < 3) {
				String visibility = getMethodVisibility(m);
				w.write("    " + visibility + m.getNameAsString() + "()\n");
				methodCount++;
			} else {
				w.write("    ...\n");
				break;
			}
		}

		w.write("  }\n");
	}

	// Versione alternativa con meno dettagli nelle classi (più compatta)
	@SuppressWarnings("unused")
	private static void writePlantUMLCompact(Path output) throws IOException {
		try (BufferedWriter w = Files.newBufferedWriter(output)) {

			w.write("@startuml\n");
			w.write("skinparam classAttributeIconSize 0\n");
			w.write("skinparam linetype ortho\n");
			w.write("skinparam nodesep 30\n");
			w.write("skinparam ranksep 30\n");
			w.write("hide empty members\n");
			w.write("hide circle\n\n");

			// PACKAGE
			for (String pkg : packageToClasses.keySet()) {
				String packageDisplayName = pkg.equals("default") ? "Default Package" : pkg;
				w.write("package \"" + packageDisplayName + "\" {\n");

				for (String className : packageToClasses.get(pkg)) {
					ClassInfo classInfo = classInfoMap.get(className);
					if (classInfo != null) {
						ClassOrInterfaceDeclaration c = classInfo.declaration;

						if (c.isInterface()) {
							w.write("  interface " + className + "\n");
						} else if (c.isAbstract()) {
							w.write("  abstract class " + className + "\n");
						} else {
							w.write("  class " + className + "\n");
						}
					}
				}

				w.write("}\n\n");
			}

			// RELAZIONI
			for (String r : relations) {
				w.write(r + "\n");
			}

			// EREDITARIETÀ
			for (ClassInfo classInfo : classInfoMap.values()) {
				String className = classInfo.name;
				ClassOrInterfaceDeclaration c = classInfo.declaration;

				for (ClassOrInterfaceType ext : c.getExtendedTypes()) {
					String superClass = ext.getNameAsString();
					if (classInfoMap.containsKey(superClass)) {
						w.write(superClass + " <|-- " + className + "\n");
					}
				}
			}

			w.write("\n@enduml\n");
		}
	}

	private static String getVisibility(FieldDeclaration f) {
		if (f.isPublic())
			return "+";
		if (f.isProtected())
			return "#";
		if (f.isPrivate())
			return "-";
		return "~";
	}

	private static String getMethodVisibility(MethodDeclaration m) {
		if (m.isPublic())
			return "+";
		if (m.isProtected())
			return "#";
		if (m.isPrivate())
			return "-";
		return "~";
	}
}