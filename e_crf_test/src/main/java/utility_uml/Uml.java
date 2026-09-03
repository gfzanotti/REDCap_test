//package com.example.uml;
package utility_uml;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.github.javaparser.resolution.types.ResolvedReferenceType;
import com.github.javaparser.resolution.types.ResolvedType;
// symbol solver
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.javaparsermodel.JavaParserFacade;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;

public class Uml {

	private static final Map<String, ClassOrInterfaceDeclaration> classes = new HashMap<>();
	private static final Set<String> relations = new LinkedHashSet<>();
	private static final Map<String, Set<String>> inheritanceHierarchy = new HashMap<>();

	private static JavaParserFacade javaTypeSolver;

	public static void main(String[] args) throws Exception {

		if (args.length < 2) {
			System.out.println("USO: java GenerateUMLAdvanced <directory-sorgente> <output.puml>");
			return;
		}

		Path srcDir = Paths.get(args[0]);

		Path output = Paths.get(args[1]);

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
	// Scansiona i file .java
	// ----------------------------------------------------
	private static void scanAllJavaFiles(Path root) throws IOException {
		Files.walk(root).filter(f -> f.toString().endsWith(".java")).forEach(Uml::parseClass);
	}

	private static void parseClass(Path file) {
		try {
			CompilationUnit cu = StaticJavaParser.parse(file);
			cu.findAll(ClassOrInterfaceDeclaration.class).forEach(c -> classes.put(c.getNameAsString(), c));
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
								if (classes.containsKey(simpleName)) {
									targetTypes.add(simpleName);
								}
							}
						}
					} else {
						// Tipo non generico
						String typeName = refType.getQualifiedName();
						String simpleName = extractSimpleName(typeName);
						if (classes.containsKey(simpleName)) {
							targetTypes.add(simpleName);
						}
					}
				}

				// Aggiungi relazioni per ogni tipo target trovato
				for (String target : targetTypes) {
					if (target.equals(className)) {
						continue; // Skip self-reference
					}

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

							if (classes.containsKey(innerType) && !innerType.equals(className)) {
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
					} else if (classes.containsKey(typeStr) && !typeStr.equals(className)) {
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

			c.findAll(MethodCallExpr.class).forEach(call -> {
				try {
					ResolvedMethodDeclaration decl = javaTypeSolver.solve(call).getCorrespondingDeclaration();
					String targetClass = decl.declaringType().getClassName();

					if (classes.containsKey(targetClass) && !targetClass.equals(caller)) {
						relations.add(caller + " ..> " + targetClass + " : calls");
					}
				} catch (Exception ignored) {
				}
			});
		}
	}

	// ----------------------------------------------------
	// OUTPUT FINALE .PUML
	// ----------------------------------------------------
	private static void writePlantUML(Path output) throws IOException {
		try (BufferedWriter w = Files.newBufferedWriter(output)) {

			w.write("@startuml\n");
			w.write("skinparam classAttributeIconSize 0\n");
			w.write("skinparam linetype ortho\n");
			w.write("skinparam nodesep 50\n");
			w.write("skinparam ranksep 50\n\n");

			// CLASSI
			for (var entry : classes.entrySet()) {
				ClassOrInterfaceDeclaration c = entry.getValue();
				String name = c.getNameAsString();

				if (c.isInterface()) {
					w.write("interface " + name + " {\n");
				} else if (c.isAbstract()) {
					w.write("abstract class " + name + " {\n");
				} else {
					w.write("class " + name + " {\n");
				}

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

				// EREDITARIETÀ
				for (ClassOrInterfaceType ext : c.getExtendedTypes()) {
					w.write(ext.getNameAsString() + " <|-- " + name + "\n");
				}

				// IMPLEMENTAZIONE INTERFACCE
				for (ClassOrInterfaceType impl : c.getImplementedTypes()) {
					w.write(impl.getNameAsString() + " <|.. " + name + "\n");
				}
			}

			// RELAZIONI (associazioni, composizioni, aggregazioni)
			w.write("\n' RELAZIONI TRA CLASSI\n");
			for (String r : relations) {
				w.write(r + "\n");
			}

			w.write("\n@enduml\n");
		}
	}

	private static String getVisibility(FieldDeclaration f) {
		if (f.isPublic()) {
			return "+";
		}
		if (f.isProtected()) {
			return "#";
		}
		if (f.isPrivate()) {
			return "-";
		}
		return "~"; // default (package-private)
	}

	private static String getMethodVisibility(MethodDeclaration m) {
		if (m.isPublic()) {
			return "+";
		}
		if (m.isProtected()) {
			return "#";
		}
		if (m.isPrivate()) {
			return "-";
		}
		return "~"; // default (package-private)
	}
}