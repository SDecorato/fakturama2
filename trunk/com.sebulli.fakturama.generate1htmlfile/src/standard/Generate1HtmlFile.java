/* 
 * Fakturama - Free Invoicing Software - http://fakturama.sebulli.com
 * 
 * Copyright (C) 2010 Gerd Bartelt
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Gerd Bartelt - initial API and implementation
 */

package standard;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Convert the Fakturama Help to one single HTML file, so that it can
 * be opened by OpenOffice Writer
 * 
 * @author Gerd Bartelt
 *
 */
public class Generate1HtmlFile {

	private static String path = "../com.sebulli.fakturama.help/";
	
	/**
	 * Collect all HTML files of the help system and convert them.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance(); 
	    DocumentBuilder builder;
		try {
			
			// Add the header
			addHtmlFile( "templates/header.html", "templates/",  false, true, false);
			
			// Parse the toc.xml file
			builder = factory.newDocumentBuilder();
		    Document document = builder.parse( new File(path + "toc.xml") );
		    NodeList nodeList = document.getElementsByTagName("topic");
		    
		    // Get all the topic entries
		    for (int i = 0; i < nodeList.getLength(); i++) {
		    	NodeList  childNodes = nodeList.item(i).getChildNodes();
			    for (int ii = 0; ii < childNodes.getLength(); ii++) {
			    	Node childNode = childNodes.item(ii);
			    	if (childNode.getNodeName().equals("anchor")) {
			    		
			    		// Add the HTML file of each toc file
				    	String htmlFileName = path + nodeList.item(i).getAttributes().getNamedItem("href").getNodeValue();
				    	addHtmlFile( htmlFileName, getPath(htmlFileName), true, false, false);

			    		parseTocFile(path + "toc"+childNode.getAttributes().getNamedItem("id").getNodeValue()+".xml");
			    	}
			    }
		    }
		    
		    // Add the footer
			addHtmlFile( "templates/footer.html", "templates/", false, false, false);

		
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Parse a toc file and add all the HTML files
	 * 
	 * @param fileName
	 * 			Name of the Toc file to parse
	 */
	private static void parseTocFile (String fileName) {
	    System.out.println("Parsing: " + fileName); 
	    
	    // Load the XML file
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance(); 
	    DocumentBuilder builder;
		try {
			builder = factory.newDocumentBuilder();
		    Document document = builder.parse( new File(path + fileName) );
		    NodeList nodeList = document.getElementsByTagName("topic");
		    
		    // Get all "topic" entries
		    for (int i = 0; i < nodeList.getLength(); i++) {
		    	
		    	// Add the HTML file of the topic entry
		    	String htmlFileName = path + nodeList.item(i).getAttributes().getNamedItem("href").getNodeValue();
		    	addHtmlFile( htmlFileName, getPath(htmlFileName), true, false, true);
		    }
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Extract the path of a complex file name
	 * 
	 * @param s
	 * 		Path and filename
	 * 
	 * @return
	 * 		Only the path
	 */
	private static String getPath (String s) {
		
		String p ="";
		int i;
		i = s.lastIndexOf("/"); 
		if (i>0){
			p = s.substring(0,i+1);
		}
		else {
			i = s.lastIndexOf("\\"); 
			if (i>0)
				p = s.substring(0,i+1);
		}
		return p;
	}
	
	/**
	 * Add a new line to the output file
	 * 
	 * @param line
	 * 		The line to add
	 */
	@SuppressWarnings("unused")
	private static void addLine(String line) {
		try {
			File outputFile = new File("output/index.html");
			BufferedWriter outputWriter = new BufferedWriter(new FileWriter(outputFile, true));
			outputWriter.write(line + "\r\n");
			outputWriter.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Add a HTML file to the output file
	 * 
	 * @param fileName
	 * 			The HTML file to add
	 * @param p
	 * 			The path to the HTML file (without filename)
	 * @param onlyBody
	 * 			True, if only the body of the HTML file should be added
	 * @param newFile
	 * 			True, if a new output file should be created
	 * @param isNotH1
	 * 			True, of all H1 headings should be converted to H2 headings
	 */
	private static void addHtmlFile (String fileName, String p, boolean onlyBody, boolean newFile, boolean isNotH1) {
		
		if (fileName.endsWith("nohelp.html"))
			return;
		
		try {
			// Create a folder "output", if it does not exist yet.
			File directory = new File("output");
			if (!directory.exists())
				directory.mkdirs();
			
			// Create a new output file - delete the old one
			File outputFile = new File("output/index.html");
			if (newFile) {
				if (outputFile.exists())
					outputFile.delete();
				outputFile.createNewFile();
			}
			
			BufferedWriter outputWriter = new BufferedWriter(new FileWriter(outputFile, true));
			
			// Create a new input File object
			File htmlFile = new File(fileName);

			// If the log file exists read the content
			if (htmlFile.exists()) {

			    System.out.println("  Adding: " + fileName); 

			    // Open the existing file
				BufferedReader in = new BufferedReader(new FileReader(fileName));
				String line = "";

				boolean body = false;
				while ((line = in.readLine()) != null) {
					
					// Find the body of the HTML file
					if (line.startsWith("<body>") && onlyBody) {
						body = true;
					}
					else if (line.startsWith("</body>") && onlyBody) {
							body = false;
					}
					else if (body || !onlyBody) {
						// Convert the headings
						line = line.replace("<h3>", "<h4>");
						line = line.replace("</h3>", "</h4>");
						line = line.replace("<h2>", "<h3>");
						line = line.replace("</h2>", "</h3>");
						if (isNotH1) {
							line = line.replace("<h1>", "<h2>");
							line = line.replace("</h1>", "</h2>");
						}
						line = line.replace("src=\"", "src=\"" +"../"+ p);
						
						outputWriter.write(line + "\r\n");
					}
				}
				in.close();
			}
			outputWriter.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

}
