/*
 * 
 *	Fakturama - Free Invoicing Software 
 *  Copyright (C) 2010  Gerd Bartelt
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *   
 */

package com.sebulli.fakturama.data;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.sebulli.fakturama.logger.Logger;

/**
 * HSQLDB data base manager
 * 
 * @author Gerd Bartelt
 */
public class DataBase {
	private Connection con = null;

	/**
	 * Convert a UniDataType to the Types for the data base
	 * 
	 * @param udt
	 * @return
	 */
	private String getDataBaseTypeByUniDataType (UniDataType udt) {
		// Depending on the UniDataSet type, add an data base type
		switch (udt) {
		case ID:
		case INT:
			return "INTEGER";

		case BOOLEAN:
			return "BOOLEAN";

		case DOUBLE:
		case QUANTITY:
		case PRICE:
		case PERCENT:
			return "DOUBLE";

		case STRING:
		case DATE:
			return "VARCHAR";

		default:
			Logger.logError("Unknown UniDataType");
		}
		return "VARCHAR";
	}
	
	/**
	 * Generate the SQL string to create a new table in the data base  
	 * 
	 * @param uds UniDataSet as template for the new table
	 * @return
	 */
	private String getCreateSqlTableString(UniDataSet uds) {
		String s = "";

		// Generate a list with all keys in the template UniDataSet
		// and sort it alphabetically
		List<String> list = new ArrayList<String>();
		list.addAll(uds.getHashMap().keySet());
		Collections.sort(list);

		try {
			s = "id INT IDENTITY PRIMARY KEY";

			// Add all keys to the SQL string.
			// They are the columns of the new table
			// But do not add a column for "id" because the ID is the 
			// id of the data base entry
			for (String key : list) {
				
				if (!key.equalsIgnoreCase("id")) {

					// Separate the columns by an ","	
					s += ", " + key + " ";
					s += getDataBaseTypeByUniDataType (uds.hashMap.get(key).getUniDataType());
				}
			}
		} catch (Exception e) {
			Logger.logError(e, "Error creating SQL String from dataset.");
		}
		
		// return the SQL string in brackets. 
		return uds.sqlTabeName + "(" + s + ")";
	}

	/**
	 * Generate the SQL string with all columns to insert a new row in the data base  
	 * 
	 * @param uds
	 * @return
	 */
	private String getInsertSqlColumnsString(UniDataSet uds) {
		String s = "";
		
		// Generate a list with all keys in the template UniDataSet
		// and sort it alphabetically
		List<String> list = new ArrayList<String>();
		list.addAll(uds.getHashMap().keySet());
		Collections.sort(list);

		try {
			s = "id";

			// Get all UniDataSet keys and use them as columns headers
			for (String key : list) {
				if (!key.equalsIgnoreCase("id")) {
					s += ", " + key;
				}
			}
		} catch (Exception e) {
			Logger.logError(e, "Error creating SQL columns string from dataset.");
		}
		
		// return the SQL string in brackets. 
		return "(" + s + ")";
	}
	
	/**
	 * Generate the SQL string to insert a new column in the data base  
	 * It is a SQL string with placeholders
	 * 
	 * @param uds
	 * @return
	 */
	private String getInsertSqlColumnsStringWithPlaceholders(UniDataSet uds) {
		String s = "";

		// Generate a list with all keys in the template UniDataSet
		// and sort it alphabetically
		List<String> list = new ArrayList<String>();
		list.addAll(uds.getHashMap().keySet());
		Collections.sort(list);

		try {
			s = "?";

			// Get all UniDataSet keys 
			for (String key : list) {
				if (!key.equalsIgnoreCase("id")) {
					s += ", " + "?";
				}
			}
		} catch (Exception e) {
			Logger.logError(e, "Error creating SQL columns string from dataset.");
		}

		// return the SQL string in brackets. 
		return "(" + s + ")";
	}
	
	/**
	 * Generate the SQL string to update data in the data base  
	 * It is a SQL string with placeholders
	 * 
	 * @param uds
	 * @return
	 */
	private String getUpdateSqlValuesStringWithPlaceholders(UniDataSet uds) {
		String s = "";

		// Generate a list with all keys in the template UniDataSet
		// and sort it alphabetically
		List<String> list = new ArrayList<String>();
		list.addAll(uds.getHashMap().keySet());
		Collections.sort(list);

		try {
			// Get all UniDataSet keys 
			for (String key : list) {
				if (!key.equalsIgnoreCase("id")) {
					s += ", " + key + "=?";
				}
			}
		} catch (Exception e) {
			Logger.logError(e, "Error creating SQL values string from dataset.");
		}
		
		// remove first ", "
		return s.substring(2);
	}
	
	/**
	 * Set an SQL parameter 
	 * 
	 * @param prepStmt Prepared Statement
	 * @param uds UniDataSet
	 * @param useId True, if the id is used
	 */
	private void setSqlParameters(PreparedStatement prepStmt, UniDataSet uds, boolean useId) {
		int i;

		// Generate a list with all keys in the template UniDataSet
		// and sort it alphabetically
		List<String> list = new ArrayList<String>();
		list.addAll(uds.getHashMap().keySet());
		Collections.sort(list);

		try {
			i = 1;
			
			// Set also the ID
			if (useId) {
				prepStmt.setInt(i, uds.getIntValueByKey("id"));
				i++;
			}

			// Set all other columns, depending on the data type
			for (String key : list) {
				if ( !key.equalsIgnoreCase("id") ) {
					UniDataType udt = uds.getUniDataTypeByKey(key);
					switch (udt) {
					case ID:
					case INT:
						prepStmt.setInt(i, uds.getIntValueByKey(key));
						break;
					case BOOLEAN:
						prepStmt.setBoolean(i, uds.getBooleanValueByKey(key));
						break;
					case PRICE:
					case PERCENT:
					case QUANTITY:
					case DOUBLE:
						prepStmt.setDouble(i, uds.getDoubleValueByKey(key));
						break;

					case DATE:
					case STRING:
						prepStmt.setString(i, uds.getStringValueByKey(key));
						break;
					default:
						Logger.logError("Unspecified Data");
						break;
					}
					i++;
				}
			}
		} catch (Exception e) {
			Logger.logError(e, "Error creating SQL values string from dataset.");
		}
	}

	/**
	 * Insert a UniDataSet object in the data base
	 * 
	 * @param uds UniDataSet to insert
	 */
	public void insertUniDataSet(UniDataSet uds) {
		String s;
		ResultSet rs;
		Statement stmt;
		PreparedStatement prepStmt;
		try {
			stmt = con.createStatement();

			s = "SELECT * FROM " + uds.sqlTabeName + " WHERE ID=" + uds.getStringValueByKey("id");
			rs = stmt.executeQuery(s);

			// test, if there is not an existing object with the same ID
			if (rs.next()) {
				Logger.logError("Dataset with this id is already in database" + uds.getStringValueByKey("name"));
			} else {
				
				// Generate the statement to insert a value and execute it
				s = "INSERT INTO " + uds.sqlTabeName + " " + getInsertSqlColumnsString(uds) + " VALUES" + getInsertSqlColumnsStringWithPlaceholders(uds);
				prepStmt = con.prepareStatement(s);
				setSqlParameters(prepStmt, uds, true);
				prepStmt.executeUpdate();
				prepStmt.close();

			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			Logger.logError(e, "Error saving dataset " + uds.getStringValueByKey("name"));
		}

	}
	
	/**
	 * Update a UniDataSet object in the database
	 * 
	 * @param uds UniDataSet object to update
	 */
	public void updateUniDataSet(UniDataSet uds) {
		String s;
		PreparedStatement prepStmt;

		try {
			// Create the SQL statement to update the data and execute it.
			s = "UPDATE " + uds.sqlTabeName + " SET " + getUpdateSqlValuesStringWithPlaceholders(uds) + " WHERE ID=" + uds.getStringValueByKey("id");
			prepStmt = con.prepareStatement(s);
			setSqlParameters(prepStmt, uds, false);
			prepStmt.executeUpdate();
			prepStmt.close();
		} catch (SQLException e) {
			Logger.logError(e, "Error saving dataset " + uds.getStringValueByKey("name"));
		}
	}

	/**
	 * Copy the data base table into a UniDataSet ArrayList
	 * 
	 * @param uniDataList Copy the table to this ArrayList
	 * @param udsTemplate Use this as template
	 */
	@SuppressWarnings("unchecked")
	public void getTable(@SuppressWarnings("rawtypes") ArrayList uniDataList, UniDataSet udsTemplate) {
		String s;
		String columnName;
		ResultSet rs;
		Statement stmt;
		UniDataSet uds = null;

		try {
			
			// read the data base table
			stmt = con.createStatement();
			s = "SELECT * FROM " + udsTemplate.sqlTabeName;
			rs = stmt.executeQuery(s);
			ResultSetMetaData meta = rs.getMetaData();
			while (rs.next()) {
				uds = null;
			
				// Create a new temporary UniDataSet to store the data
				if (udsTemplate instanceof DataSetProduct)
					uds = new DataSetProduct();
				if (udsTemplate instanceof DataSetContact)
					uds = new DataSetContact();
				if (udsTemplate instanceof DataSetItem)
					uds = new DataSetItem();
				if (udsTemplate instanceof DataSetVAT)
					uds = new DataSetVAT();
				if (udsTemplate instanceof DataSetProperty)
					uds = new DataSetProperty();
				if (udsTemplate instanceof DataSetShipping)
					uds = new DataSetShipping();
				if (udsTemplate instanceof DataSetPayment)
					uds = new DataSetPayment();
				if (udsTemplate instanceof DataSetText)
					uds = new DataSetText();
				if (udsTemplate instanceof DataSetDocument)
					uds = new DataSetDocument();

				if (uds == null)
					Logger.logError("Error: unknown UniDataSet Type");

				// Copy the table to the new UniDataSet
				for (int i = 1; i <= meta.getColumnCount(); i++) {
					columnName = meta.getColumnName(i).toLowerCase();
					s = rs.getString(i);
					uds.setStringValueByKey(columnName, s);
				}
				
				// Add the new UniDataSet to the Array List
				uniDataList.add(uds);
			}

			rs.close();
			stmt.close();
		} catch (Exception e) {
			Logger.logError(e, "Error reading database table " + udsTemplate.sqlTabeName);
		}

	}

	/**
	 * Check the table in the data base, if there is a column for each UniDataSet property.
	 * If not, create a new column.
	 * 
	 * @param uds The UniDataSet to check. Defines also the table.
	 */
	private void checkTableAndInsertNewColumns (UniDataSet uds) {
		ResultSet rs;
		Statement stmt;
		ResultSetMetaData rsmd;
		int columns = 0;
		
		try {
			
			// Get the columns of the table, specified by the UniDataSet uds.
			stmt = con.createStatement();
			rs = stmt.executeQuery("SELECT * FROM "+ uds.sqlTabeName);
			rsmd = rs.getMetaData();
			columns = rsmd.getColumnCount();
			
			// Generate a list with all keys in the template UniDataSet
			// and sort it alphabetically
			List<String> list = new ArrayList<String>();
			list.addAll(uds.getHashMap().keySet());
			Collections.sort(list);

			// Get all UniDataSet keys and test, if all columns exist
			for (String key : list) {
				
				// Do not test the ID column.
				if (!key.equalsIgnoreCase("id")) {

					String columnname = key;
					Boolean columnExists = false;
					
					// Search all column for the key.
					for(int i = 1; i <= columns; i++) {
						if ( rsmd.getColumnName(i).equalsIgnoreCase(columnname) ) {
							columnExists = true;
						}
					}
					
					// Create a new column, if it does not exist yet.
					if (!columnExists) {
						String dType = getDataBaseTypeByUniDataType (uds.hashMap.get(key).getUniDataType());
						stmt.executeUpdate("ALTER TABLE " + uds.sqlTabeName + " ADD " + columnname + " "+  dType);
						Logger.logInfo("New column " + columnname + " added in table " + uds.sqlTabeName + " - Data type: " + dType);
					}
				}
			}
			
			rs.close();
			
		} catch (SQLException e) {
			Logger.logError(e, "Error inserting a new table column.");
		}		
		
	}
	
	/**
	 * Connect to the data base
	 * 
	 * @param workingDirectory Working directory
	 * @return True, if the data base is connected
	 */
	public boolean connect(String workingDirectory) {
		String dataBaseName;
		ResultSet rs;

		// Get the JDBC driver
		try {
			Class.forName("org.hsqldb.jdbcDriver");
		} catch (ClassNotFoundException e) {
			Logger.logError("Class org.hsqldb.jdbcDriver not found");
			return false;
		}

		// The data base is in the /Database/ directory
		// If this folder doesn't exist - create it !
		String path = workingDirectory + "/Database/";
		File directory = new File(path);
		if (!directory.exists())
			directory.mkdirs();

		dataBaseName = path + "Database";

		try {
			// connect to the database
			con = DriverManager.getConnection("jdbc:hsqldb:file:" + dataBaseName + ";shutdown=true", "sa", "");
			Statement stmt = con.createStatement();

			// Read the "Properties" table, to see, if it exists.
			// If not - it is a new data base.
			try {
				rs = stmt.executeQuery("SELECT * FROM Properties");
				rs.close();
				
				// Check all tables, if there is a column for each
				// UniDataSet property.
				checkTableAndInsertNewColumns(new DataSetProduct());
				checkTableAndInsertNewColumns(new DataSetContact());
				checkTableAndInsertNewColumns(new DataSetItem());
				checkTableAndInsertNewColumns(new DataSetVAT());
				checkTableAndInsertNewColumns(new DataSetShipping());
				checkTableAndInsertNewColumns(new DataSetPayment());
				checkTableAndInsertNewColumns(new DataSetText());
				checkTableAndInsertNewColumns(new DataSetDocument());
				
			} catch (SQLException e) {
				// In a new data base: create all the tables
				try {
					stmt.executeQuery("CREATE TABLE Properties(Id INT IDENTITY PRIMARY KEY, Name VARCHAR, Value VARCHAR)");
					stmt.executeUpdate("INSERT INTO Properties VALUES(0,'version','1')");
					stmt.executeQuery("CREATE TABLE " + getCreateSqlTableString(new DataSetProduct()));
					stmt.executeQuery("CREATE TABLE " + getCreateSqlTableString(new DataSetContact()));
					stmt.executeQuery("CREATE TABLE " + getCreateSqlTableString(new DataSetItem()));
					stmt.executeQuery("CREATE TABLE " + getCreateSqlTableString(new DataSetVAT()));
					stmt.executeQuery("CREATE TABLE " + getCreateSqlTableString(new DataSetShipping()));
					stmt.executeQuery("CREATE TABLE " + getCreateSqlTableString(new DataSetPayment()));
					stmt.executeQuery("CREATE TABLE " + getCreateSqlTableString(new DataSetText()));
					stmt.executeQuery("CREATE TABLE " + getCreateSqlTableString(new DataSetDocument()));
					stmt.close();
					return true;

				} catch (SQLException e2) {
					Logger.logError(e2, "Error creating new tables in database.");
				}
			}

			stmt.close();
		} catch (SQLException e) {
			Logger.logError(e, "Error connecting the Database:" + dataBaseName);
		}
		return false;
	}

	/**
	 * Test, if the data base is connected
	 * 	
	 * @return True, if connected
	 */
	public boolean isConnected() {
		return (con != null);
	}

	/**
	 * Close the data base
	 */
	public void close() {
		if (con != null)
			try {
				con.close();
			} catch (SQLException e) {
				Logger.logError(e, "Error closing the Database");
			}
	}

}
