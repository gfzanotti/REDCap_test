package mi.istitutotumori.app.controller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.net.URISyntaxException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType0Font;

import javafx.fxml.FXML;
import javafx.print.PrinterJob;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.scene.control.ButtonType;

/**
 * 16.07.2026 al momento ho i metodi statici richiamati dalla classe
 * RootController che servono per la gestione del 'tab' help con la
 * documentazione javadoc
 */
public class ControllerUtilsMethod {

	private enum State {
		OUTSIDE, INSIDE
	}

	@FXML
	protected static void showDocs() {
		try {
			// Cartella dove salvare la documentazione generata
			// File docsFolder = new File(System.getProperty("user.home") + "/AppDocs");
			File docsFolder = new File(System.getProperty("user.dir") + "/AppDocs");

			if (!docsFolder.exists()) {
				docsFolder.mkdirs();
			}

			File outputTxt = new File(docsFolder, "javadoc.txt");

			// Se NON esiste, lo generiamo automaticamente
			if (!outputTxt.exists()) {
				File srcFolder = new File("src");
				if (!srcFolder.exists()) {
					System.out.println("ATTENZIONE: la cartella 'src' non è stata trovata!");
					return;
				}
				collectJavaDocs(srcFolder, outputTxt);
			}

			// ----- Leggo il contenuto del file -----
			String content = java.nio.file.Files.readString(outputTxt.toPath());

			// --- AREA TESTO ---
			TextArea textArea = new TextArea(content);
			textArea.setEditable(false);
			textArea.setStyle("-fx-font-family: Consolas; -fx-font-size: 1.4em; -fx-padding: 10;");

			ScrollPane scrollPane = new ScrollPane(textArea);
			scrollPane.setFitToWidth(true);
			scrollPane.setFitToHeight(true);

			// --- TOOLBAR ---
			TextField searchField = new TextField();
			searchField.setPromptText("Cerca…");

			Button searchBtn = new Button("Trova");
			searchBtn.setOnAction(e -> {
				String text = textArea.getText().toLowerCase();
				String query = searchField.getText().toLowerCase();
				if (query.isEmpty() || !text.contains(query)) {
					return;
				}

				int idx = text.indexOf(query, textArea.getCaretPosition());
				if (idx == -1) {
					idx = text.indexOf(query);
				}

				textArea.selectRange(idx, idx + query.length());
			});

			Button zoomIn = new Button("Zoom +");
			Button zoomOut = new Button("Zoom -");

			zoomIn.setOnAction(e -> textArea
					.setStyle("-fx-font-family: Consolas; -fx-font-size: " + (getFontSize(textArea) + 2) + "px;"));
			zoomOut.setOnAction(e -> textArea
					.setStyle("-fx-font-family: Consolas; -fx-font-size: " + (getFontSize(textArea) - 2) + "px;"));

			Button darkModeBtn = new Button("Dark Mode");
			darkModeBtn.setOnAction(e -> {
				boolean dark = darkModeBtn.getText().equals("Dark Mode");

				if (dark) {
					textArea.setStyle("""
							    -fx-font-family: Consolas;
							    -fx-font-size: 1.4em;
							    -fx-control-inner-background: #1e1e1e;
							    -fx-text-fill: #e0e0e0;
							    -fx-padding: 10;
							""");
					darkModeBtn.setText("Light Mode");
				} else {
					textArea.setStyle("""
							    -fx-font-family: Consolas;
							    -fx-font-size: 1.4em;
							    -fx-control-inner-background: white;
							    -fx-text-fill: black;
							    -fx-padding: 10;
							""");
					darkModeBtn.setText("Dark Mode");
				}
			});

			Button printBtn = new Button("Stampa");
			printBtn.setOnAction(e -> printTextArea(textArea));

			Button exportPdfBtn = new Button("Esporta PDF");
			exportPdfBtn.setOnAction(e -> {
				try {
					exportPdf(textArea);
				} catch (URISyntaxException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			});

			// ---- BAR ----
			ToolBar toolbar = new ToolBar(new Label("Trova:"), searchField, searchBtn, new Separator(), zoomIn, zoomOut,
					new Separator(), darkModeBtn, new Separator(), printBtn, exportPdfBtn);

			BorderPane root = new BorderPane();
			root.setTop(toolbar);
			root.setCenter(scrollPane);

			Stage stage = new Stage();
			stage.setTitle("Documentazione");
			stage.setScene(new Scene(root, 1000, 700));
			stage.show();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void collectJavaDocs(File file, File outputTxt) throws Exception {
		StringBuilder sb = new StringBuilder();

		scanFolderForDocs(file, sb);

		try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputTxt))) {
			writer.write(sb.toString());
		}
	}

	private static void scanFolderForDocs(File file, StringBuilder sb) throws Exception {
		if (file.isDirectory()) {
			// ESCLUDI DIRECTORY (es: "uml")
			if (file.getName().equalsIgnoreCase("uml")) {
				System.out.printf("file escluso : %s", file.getName());
				return;
			}

			for (File f : file.listFiles()) {
				scanFolderForDocs(f, sb);
			}
			return;
		}

		if (!file.getName().endsWith(".java")) {
			return;
		}

		sb.append("──────────────────────────────────────────\n");
		sb.append(" FILE: ").append(file.getName()).append("\n");
		sb.append("──────────────────────────────────────────\n\n");

		State state = State.OUTSIDE;

		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			String line;

			while ((line = br.readLine()) != null) {
				String trim = line.trim();

				switch (state) {

				case OUTSIDE:
					if (trim.startsWith("/**")) {
						state = State.INSIDE;
					}
					break;

				case INSIDE:
					if (trim.endsWith("*/")) {
						state = State.OUTSIDE;
						sb.append("\n");
					} else {
						if (trim.startsWith("*")) {
							trim = trim.substring(1).trim();
						}
						sb.append(trim).append("\n");
					}
					break;
				}
			}
		}

		sb.append("\n\n");
	}

	private static void printTextArea(TextArea textArea) {
		PrinterJob job = PrinterJob.createPrinterJob();
		if (job == null) {
			Alert alert = new Alert(Alert.AlertType.ERROR, "Nessuna stampante disponibile!", ButtonType.OK);
			alert.showAndWait();
			return;
		}

		boolean proceed = job.showPrintDialog(null);
		if (proceed) {

			boolean wrap = textArea.isWrapText();
			textArea.setWrapText(false);

			boolean success = job.printPage(textArea);

			textArea.setWrapText(wrap);

			if (success) {
				job.endJob();
				Alert alert = new Alert(Alert.AlertType.INFORMATION, "Stampa completata", ButtonType.OK);
				alert.showAndWait();
			} else {
				Alert alert = new Alert(Alert.AlertType.ERROR, "Errore durante la stampa", ButtonType.OK);
				alert.showAndWait();
			}
		}
	}

	@SuppressWarnings("resource")
	private static void exportPdf(TextArea textArea) throws URISyntaxException {
		String jarDir = new File(RootController.class.getProtectionDomain().getCodeSource().getLocation().toURI())
				.getParent();

		File pdfFile = new File(jarDir + File.separator + "AppDocs" + File.separator + "documentazione.pdf");
		// File pdfFile = new File(System.getProperty("user.dir") +
		// "/target/AppDocs/documentazione.pdf");

		// crea cartella se non esiste
		File parent = pdfFile.getParentFile();
		if (!parent.exists()) {
			parent.mkdirs();
		}

		try (PDDocument doc = new PDDocument()) {

			// 🔹 font Unicode
			PDType0Font font = PDType0Font.load(doc, new File("C:/Windows/Fonts/arial.ttf"));

			float fontSize = 12;
			float leading = 1.5f * fontSize;

			float margin = 50;

			PDPage page = new PDPage();
			doc.addPage(page);

			PDPageContentStream content = new PDPageContentStream(doc, page);
			content.setFont(font, fontSize);
			content.beginText();

			float yStart = page.getMediaBox().getHeight() - margin;
			float yPosition = yStart;

			float width = page.getMediaBox().getWidth() - 2 * margin;

			content.newLineAtOffset(margin, yPosition);

			String text = textArea.getText();

			for (String line : text.split("\n")) {

				line = line.replace("\t", "    ");
				line = line.replace("\r", "");

				String[] words = line.split(" ");
				StringBuilder currentLine = new StringBuilder();

				for (String word : words) {

					String testLine = currentLine + word + " ";
					float size = font.getStringWidth(testLine) / 1000 * fontSize;

					if (size > width) {

						// 🔹 controllo fine pagina
						if (yPosition <= margin) {
							content.endText();
							content.close();

							page = new PDPage();
							doc.addPage(page);

							content = new PDPageContentStream(doc, page);
							content.setFont(font, fontSize);
							content.beginText();

							yPosition = page.getMediaBox().getHeight() - margin;
							content.newLineAtOffset(margin, yPosition);
						}

						content.showText(currentLine.toString());
						content.newLineAtOffset(0, -leading);
						yPosition -= leading;

						currentLine = new StringBuilder(word + " ");

					} else {
						currentLine.append(word).append(" ");
					}
				}

				// 🔹 ultima parte della riga
				if (yPosition <= margin) {
					content.endText();
					content.close();

					page = new PDPage();
					doc.addPage(page);

					content = new PDPageContentStream(doc, page);
					content.setFont(font, fontSize);
					content.beginText();

					yPosition = page.getMediaBox().getHeight() - margin;
					content.newLineAtOffset(margin, yPosition);
				}

				content.showText(currentLine.toString());
				content.newLineAtOffset(0, -leading);
				yPosition -= leading;
			}

			content.endText();
			content.close();

			doc.save(pdfFile);

			new Alert(Alert.AlertType.INFORMATION,
					"PDF generato correttamente.\nLo trovi in: " + pdfFile.getAbsolutePath(), ButtonType.OK)
					.showAndWait();

		} catch (Exception ex) {
			ex.printStackTrace();
			new Alert(Alert.AlertType.ERROR, "Errore imprevisto nella generazione del PDF", ButtonType.OK)
					.showAndWait();
		}
	}

	private static int getFontSize(TextArea area) {
		String style = area.getStyle();
		int i = style.indexOf("font-size:");
		if (i == -1) {
			return 14;
		}
		String sub = style.substring(i + 10).trim();
		return Integer.parseInt(sub.substring(0, sub.indexOf("px")).trim());
	}

}
