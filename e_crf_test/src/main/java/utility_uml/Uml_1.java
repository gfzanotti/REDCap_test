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

public class Uml_1 {

	private static final Map<String, ClassOrInterfaceDeclaration> classes = new HashMap<>();
	private static final Set<String> relations = new LinkedHashSet<>();
	private static final Map<String, Set<String>> inheritanceHierarchy = new HashMap<>();

	// Lista dei package da escludere (hardcoded come richiesto)
	private static final Set<String> excludedPackages = new HashSet<>(Arrays.asList("com.example.uml",
			"mi.istitutotumori.app.controller", "mi.istitutotumori.app.model", "mi.istitutotumori.app"));

	private static JavaParserFacade javaTypeSolver;

	public static void main(String[] args) throws Exception {

		if (args.length < 2) {
			System.out.println("USO: java Uml_1 <directory-sorgente> <output.puml>");
			System.out.println("Package esclusi automaticamente: com.example.uml, istitutotumori.app.controller");
			return;
		}

		Path srcDir = Paths.get(args[0]);
		Path output = Paths.get(args[1]);

		System.out.println("Package esclusi dall'analisi:");
		for (String pkg : excludedPackages) {
			System.out.println("  - " + pkg);
		}

		setupSymbolSolver(srcDir);
		scanAllJavaFiles(srcDir);

		buildInheritanceHierarchy();
		analyzeInheritedRelationships();
		analyzeDirectRelationships(); // campi, composizioni, aggregazioni
		analyzeMethodCalls(); // chiamate tra classi

		writePlantUML(output);

		System.out.println("Diagramma UML generato: " + output.toAbsolutePath());
	}

	// ----------------------------------------------------
	// SYMBOL SOLVER (necessario per risolvere tipi reali)
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
	// Scansiona i file .java ESCLUDENDO I PACKAGE SPECIFICATI
	// ----------------------------------------------------
	private static void scanAllJavaFiles(Path root) throws IOException {
		Files.walk(root).filter(f -> f.toString().endsWith(".java")).filter(f -> !isInExcludedPackage(f))
				.forEach(Uml_1::parseClass);
	}

	private static boolean isInExcludedPackage(Path javaFile) {
		try {
			if (excludedPackages.isEmpty()) {
				return false;
			}

			// Leggi il package dal file
			String content = Files.readString(javaFile);
			int packageIndex = content.indexOf("package ");
			if (packageIndex == -1) {
				return false; // File senza package
			}

			int endOfLine = content.indexOf(";", packageIndex);
			if (endOfLine == -1) {
				return false;
			}

			String packageName = content.substring(packageIndex + 8, endOfLine).trim();

			// Controlla se il package è nella lista degli esclusi
			for (String excludedPkg : excludedPackages) {
				if (packageName.equals(excludedPkg) || packageName.startsWith(excludedPkg + ".")) {
					System.out.println("File escluso: " + javaFile + " (package: " + packageName + ")");
					return true;
				}
			}

			return false;

		} catch (IOException e) {
			System.err.println("Errore lettura file per controllo package: " + javaFile);
			return false;
		}
	}

	private static void parseClass(Path file) {
		try {
			CompilationUnit cu = StaticJavaParser.parse(file);

			// Verifica se il package è escluso
			cu.getPackageDeclaration().ifPresent(pkgDecl -> {
				String packageName = pkgDecl.getNameAsString();

				for (String excludedPkg : excludedPackages) {
					if (packageName.equals(excludedPkg) || packageName.startsWith(excludedPkg + ".")) {
						return; // Salta questo file, non aggiungere le sue classi
					}
				}

				// Aggiungi tutte le classi/interfacce del file
				cu.findAll(ClassOrInterfaceDeclaration.class).forEach(c -> classes.put(c.getNameAsString(), c));
			});

			// Se non c'è package declaration, aggiungi comunque le classi
			if (!cu.getPackageDeclaration().isPresent()) {
				cu.findAll(ClassOrInterfaceDeclaration.class).forEach(c -> classes.put(c.getNameAsString(), c));
			}

		} catch (Exception e) {
			System.err.println("Errore parsing: " + file + " -> " + e.getMessage());
		}
	}

	// ----------------------------------------------------
	// COSTRUISCI GERARCHIA DI EREDITARIETÀ
	// ----------------------------------------------------
	private static void buildInheritanceHierarchy() {
		for (ClassOrInterfaceDeclaration c : classes.values()) {
			String className = c.getNameAsString();

			// Salta se la classe è in un package escluso
			if (isClassFromExcludedPackage(className)) {
				continue;
			}

			inheritanceHierarchy.put(className, new HashSet<>());

			// Aggiungi superclassi dirette
			for (ClassOrInterfaceType ext : c.getExtendedTypes()) {
				String superClass = ext.getNameAsString();
				inheritanceHierarchy.get(className).add(superClass);

				// Propaga ereditarietà transitiva
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
	// ANALIZZA RELAZIONI INCLUSE QUELLE EREDITATE
	// ----------------------------------------------------
	private static void analyzeInheritedRelationships() {
		for (ClassOrInterfaceDeclaration c : classes.values()) {
			String className = c.getNameAsString();

			// Salta se la classe è in un package escluso
			if (isClassFromExcludedPackage(className)) {
				continue;
			}

			// Analizza i campi di questa classe
			analyzeFieldsForRelationships(className, c.getFields());

			// Analizza i campi ereditati da tutte le superclassi
			if (inheritanceHierarchy.containsKey(className)) {
				for (String superClass : inheritanceHierarchy.get(className)) {
					ClassOrInterfaceDeclaration parentClass = classes.get(superClass);
					if (parentClass != null) {
						analyzeFieldsForRelationships(className, parentClass.getFields());
					}
				}
			}
		}
	}

	private static void analyzeDirectRelationships() {
		for (ClassOrInterfaceDeclaration c : classes.values()) {
			String className = c.getNameAsString();

			// Salta se la classe è in un package escluso
			if (isClassFromExcludedPackage(className)) {
				continue;
			}

			analyzeFieldsForRelationships(className, c.getFields());
		}
	}

	private static void analyzeFieldsForRelationships(String className, List<FieldDeclaration> fields) {
		for (FieldDeclaration f : fields) {
			try {
				// Ottieni il tipo risolto del campo
				ResolvedType resolvedType = javaTypeSolver.getType(f.getElementType());

				// Gestione di tipi generici come List<Field>, Set<Field>, etc.
				List<String> targetTypes = new ArrayList<>();

				if (resolvedType.isReferenceType()) {
					ResolvedReferenceType refType = resolvedType.asReferenceType();

					// Se è un tipo generico con parametri (es: List<Field>)
					if (refType.typeParametersValues().size() > 0) {
						for (ResolvedType typeParam : refType.typeParametersValues()) {
							if (typeParam.isReferenceType()) {
								String typeName = typeParam.asReferenceType().getQualifiedName();
								String simpleName = extractSimpleName(typeName);

								// Verifica se la classe target è nella mappa e non è in package escluso
								if (classes.containsKey(simpleName) && !isClassFromExcludedPackage(simpleName)) {
									targetTypes.add(simpleName);
								}
							}
						}
					} else {
						// Tipo non generico
						String typeName = refType.getQualifiedName();
						String simpleName = extractSimpleName(typeName);

						// Verifica se la classe target è nella mappa e non è in package escluso
						if (classes.containsKey(simpleName) && !isClassFromExcludedPackage(simpleName)) {
							targetTypes.add(simpleName);
						}
					}
				}

				// Aggiungi relazioni per ogni tipo target trovato
				for (String target : targetTypes) {
					if (target.equals(className))
						continue; // Skip self-reference

					boolean isCollection = isCollectionType(f.getElementType());
					boolean isFinal = f.isFinal();
					boolean isPrivate = f.isPrivate();
					String fieldName = getFieldName(f);

					// Aggiungi nota se è ereditato
					String inheritedNote = "";
					ClassOrInterfaceDeclaration fieldClass = classes.get(className);
					if (fieldClass != null && !fieldClass.getFields().contains(f)) {
						inheritedNote = " (inherited)";
					}

					if (isCollection && isFinal) {
						// Composizione "molteplice" (es. List<Field>)
						relations.add(className + " *-- \"*\" " + target + " : " + fieldName + inheritedNote);
					} else if (isCollection) {
						// Aggregazione "molteplice"
						relations.add(className + " \"1\" --> \"*\" " + target + " : " + fieldName + inheritedNote);
					} else if (isFinal && isPrivate) {
						// Composizione singola
						relations.add(className + " *-- " + target + " : " + fieldName + inheritedNote);
					} else {
						// Associazione normale
						relations.add(className + " --> " + target + " : " + fieldName + inheritedNote);
					}
				}

			} catch (Exception e) {
				// Fallback: prova con l'analisi semplice del tipo
				try {
					String typeStr = f.getElementType().toString();
					if (typeStr.contains("<") && typeStr.contains(">")) {
						// Estrai il tipo parametrico (es: Field da List<Field>)
						int start = typeStr.indexOf('<') + 1;
						int end = typeStr.lastIndexOf('>');
						if (start < end) {
							String innerType = typeStr.substring(start, end);
							// Rimuovi eventuali spazi e parametri aggiuntivi
							innerType = innerType.trim();
							if (innerType.contains("<")) {
								innerType = innerType.substring(0, innerType.indexOf('<'));
							}
							if (innerType.contains(",")) {
								innerType = innerType.substring(0, innerType.indexOf(','));
							}
							innerType = innerType.trim();

							// Verifica se la classe target non è in package escluso
							if (classes.containsKey(innerType) && !innerType.equals(className)
									&& !isClassFromExcludedPackage(innerType)) {
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
					} else if (classes.containsKey(typeStr) && !typeStr.equals(className)
							&& !isClassFromExcludedPackage(typeStr)) {
						// Tipo semplice
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
					// Ignora errori minori
				}
			}
		}
	}

	// Metodo per verificare se una classe è in un package escluso
	@SuppressWarnings("unchecked")
	private static boolean isClassFromExcludedPackage(String className) {
		// Cerca la classe nella mappa
		ClassOrInterfaceDeclaration classDecl = classes.get(className);
		if (classDecl != null) {
			// Ottieni il CompilationUnit padre per determinare il package
			Optional<CompilationUnit> cuOpt = classDecl.findAncestor(CompilationUnit.class);
			if (cuOpt.isPresent()) {
				Optional<com.github.javaparser.ast.PackageDeclaration> pkgOpt = cuOpt.get().getPackageDeclaration();
				if (pkgOpt.isPresent()) {
					String packageName = pkgOpt.get().getNameAsString();

					// Controlla se il package è escluso
					for (String excludedPkg : excludedPackages) {
						if (packageName.equals(excludedPkg) || packageName.startsWith(excludedPkg + ".")) {
							return true;
						}
					}
				}
			}
		}
		return false;
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
	// RELAZIONI VIA CHIAMATE DI METODO
	// ----------------------------------------------------
	private static void analyzeMethodCalls() {
		for (ClassOrInterfaceDeclaration c : classes.values()) {
			String caller = c.getNameAsString();

			// Salta se il chiamante è in un package escluso
			if (isClassFromExcludedPackage(caller)) {
				continue;
			}

			c.findAll(MethodCallExpr.class).forEach(call -> {
				try {
					ResolvedMethodDeclaration decl = javaTypeSolver.solve(call).getCorrespondingDeclaration();
					String targetClass = decl.declaringType().getClassName();

					// Controlla se la classe target è nella mappa, non è la stessa e non è in
					// package escluso
					if (classes.containsKey(targetClass) && !targetClass.equals(caller)
							&& !isClassFromExcludedPackage(targetClass)) {
						relations.add(caller + " ..> " + targetClass + " : calls");
					}
				} catch (Exception ignored) {
				}
			});
		}
	}

	// ----------------------------------------------------
	// OUTPUT FINALE .PUML (escludendo classi nei package esclusi)
	// ----------------------------------------------------
	private static void writePlantUML(Path output) throws IOException {
		try (BufferedWriter w = Files.newBufferedWriter(output)) {

			w.write("@startuml\n");
			w.write("skinparam classAttributeIconSize 0\n");
			w.write("skinparam linetype ortho\n");
			w.write("skinparam nodesep 50\n");
			w.write("skinparam ranksep 50\n\n");

			// CLASSI (escludi quelle nei package esclusi)
			for (var entry : classes.entrySet()) {
				String className = entry.getKey();

				// Salta se la classe è in un package escluso
				if (isClassFromExcludedPackage(className)) {
					continue;
				}

				ClassOrInterfaceDeclaration c = entry.getValue();
				String name = c.getNameAsString();

				if (c.isInterface())
					w.write("interface " + name + " {\n");
				else if (c.isAbstract())
					w.write("abstract class " + name + " {\n");
				else
					w.write("class " + name + " {\n");

				// CAMPI
				c.getFields().forEach(f -> {
					try {
						String fieldType = f.getElementType().toString();
						String fieldName = f.getVariables().get(0).getNameAsString();
						String visibility = getVisibility(f);
						w.write("  " + visibility + fieldType + " " + fieldName + "\n");
					} catch (IOException e) {
						e.printStackTrace();
					}
				});

				// METODI (solo i nomi per brevità)
				c.getMethods().forEach(m -> {
					try {
						String visibility = getMethodVisibility(m);
						w.write("  " + visibility + m.getNameAsString() + "()\n");
					} catch (IOException e) {
						e.printStackTrace();
					}
				});

				w.write("}\n\n");

				// EREDITARIETÀ (solo se entrambe le classi non sono escluse)
				for (ClassOrInterfaceType ext : c.getExtendedTypes()) {
					String superClass = ext.getNameAsString();
					if (!isClassFromExcludedPackage(superClass)) {
						w.write(superClass + " <|-- " + name + "\n");
					}
				}

				// IMPLEMENTAZIONE INTERFACCE (solo se entrambe non sono escluse)
				for (ClassOrInterfaceType impl : c.getImplementedTypes()) {
					String interfaceName = impl.getNameAsString();
					if (!isClassFromExcludedPackage(interfaceName)) {
						w.write(interfaceName + " <|.. " + name + "\n");
					}
				}
			}

			// RELAZIONI (associazioni, composizioni, aggregazioni)
			// Filtra quelle che coinvolgono classi nei package esclusi
			w.write("\n' RELAZIONI TRA CLASSI\n");
			for (String r : relations) {
				// Estrai le classi coinvolte nella relazione
				String[] parts = r.split(" ");
				if (parts.length >= 3) {
					String sourceClass = parts[0];
					String targetClass = parts[2];

					// Rimuovi eventuali quote e annotazioni
					targetClass = targetClass.replaceAll("\"", "").replaceAll("\\*", "");

					// Estrai solo il nome della classe (prima dei due punti se presenti)
					if (targetClass.contains(":")) {
						targetClass = targetClass.substring(0, targetClass.indexOf(":")).trim();
					}

					// Includi solo se entrambe le classi non sono nei package esclusi
					if (!isClassFromExcludedPackage(sourceClass) && !isClassFromExcludedPackage(targetClass)) {
						w.write(r + "\n");
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
		return "~"; // default (package-private)
	}

	private static String getMethodVisibility(MethodDeclaration m) {
		if (m.isPublic())
			return "+";
		if (m.isProtected())
			return "#";
		if (m.isPrivate())
			return "-";
		return "~"; // default (package-private)
	}
}