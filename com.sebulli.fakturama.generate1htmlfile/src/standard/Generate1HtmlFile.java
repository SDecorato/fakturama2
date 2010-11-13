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

public class Generate1HtmlFile {

	private static String path = "../com.sebulli.fakturama.help/";
	
	/**
	 * Collect all HTML files of the help system into one single
	 * file to convert it with OpenOffice Writer into a PDF manual.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance(); 
	    DocumentBuilder builder;
		try {
			builder = factory.newDocumentBuilder();
		    Document document = builder.parse( new File(path + "toc.xml") );
		    NodeList nodeList = document.getElementsByTagName("topic");
		    for (int i = 0; i < nodeList.getLength(); i++) {
		    	NodeList  childNodes = nodeList.item(i).getChildNodes();
			    for (int ii = 0; ii < childNodes.getLength(); ii++) {
			    	Node childNode = childNodes.item(ii);
			    	if (childNode.getNodeName().equals("anchor")) {
			    		parseTocFile(path + "toc"+childNode.getAttributes().getNamedItem("id").getNodeValue()+".xml");
			    	}
			    }
		    }
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @param fileName
	 */
	private static void parseTocFile (String fileName) {
	    System.out.println("Parsing: " + fileName); 
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance(); 
	    DocumentBuilder builder;
		try {
			builder = factory.newDocumentBuilder();
		    Document document = builder.parse( new File(path + fileName) );
		    NodeList nodeList = document.getElementsByTagName("topic");
		    for (int i = 0; i < nodeList.getLength(); i++) {
		    	addHtmlFile(nodeList.item(i).getAttributes().getNamedItem("href").getNodeValue());
		    }
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * 
	 * @param fileName
	 */
	private static void addHtmlFile (String fileName) {
		fileName = path + fileName;
	    System.out.println("  Adding: " +  fileName); 
		try {
			// Create a folder "output", if it does not exist yet.
			File directory = new File("output");
			if (!directory.exists())
				directory.mkdirs();
			
			File outputFile = new File("output/index.html");
			BufferedWriter outputWriter = new BufferedWriter(new FileWriter(outputFile, true));

			// Create a new input File object
			File htmlFile = new File(fileName);

			// If the log file exists read the content
			if (htmlFile.exists()) {

				// Open the existing file
				BufferedReader in = new BufferedReader(new FileReader(fileName));
				String line = "";

				boolean body = false;
				while ((line = in.readLine()) != null) {
					if (line.startsWith("<body>")) {
						body = true;
					}
					else if (line.startsWith("</body>")) {
							body = false;
					}
					else if (body) {
//						System.out.println("    Write:" + line);
						outputWriter.write(line);
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
