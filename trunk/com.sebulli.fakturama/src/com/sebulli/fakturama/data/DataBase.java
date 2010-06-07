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

public class DataBase {
	private Connection con = null;

	private String getCreateSqlTableString(UniDataSet uds) {
		String s = "";
		UniData ud;
		List<String> list = new ArrayList<String>();

		list.addAll(uds.getHashMap().keySet());
		Collections.sort(list);

		try {
			s = "id INT IDENTITY PRIMARY KEY";

			for (String key : list) {
				if (!key.equalsIgnoreCase("id")) {
					ud = uds.hashMap.get(key);
					s += ", " + key + " ";
					switch (ud.getUniDataType()) {
					case ID:
						s += "INTEGER";
						break;
					case INT:
						s += "INTEGER";
						break;
					case BOOLEAN:
						s += "BOOLEAN";
						break;
					case DOUBLE:
						s += "DOUBLE";
						break;
					case STRING:
						s += "VARCHAR";
						break;
					case PRICE:
						s += "DOUBLE";
						break;
					case PERCENT:
						s += "DOUBLE";
						break;
					case QUANTITY:
						s += "DOUBLE";
						break;
					case DATE:
						s += "VARCHAR";
						break;
					default:
						Logger.logError("Unknown UniDataType");
					}
				}
			}
		} catch (Exception e) {
			Logger.logError(e, "Error creating SQL String from dataset.");
		}
		return uds.sqlTabeName + "(" + s + ")";
	}

	private String getInsertSqlColumnsString(UniDataSet uds) {
		String s = "";
		List<String> list = new ArrayList<String>();

		list.addAll(uds.getHashMap().keySet());
		Collections.sort(list);

		try {
			s = "id";

			for (String key : list) {
				if (!key.equalsIgnoreCase("id")) {
					s += ", " + key;
				}
			}
		} catch (Exception e) {
			Logger.logError(e, "Error creating SQL columns string from dataset.");
		}
		return "(" + s + ")";
	}

	private void setSqlParameters(PreparedStatement prepStmt, UniDataSet uds, boolean useId) {
		List<String> list = new ArrayList<String>();
		int i;
		list.addAll(uds.getHashMap().keySet());
		Collections.sort(list);

		try {
			i = 1;
			if (useId) {
				prepStmt.setInt(i, uds.getIntValueByKey("id"));
				i++;
			}

			for (String key : list) {
				if (!key.equalsIgnoreCase("id")) {
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

	private String getInsertSqlColumnsStringWithPlaceholders(UniDataSet uds) {
		String s = "";
		List<String> list = new ArrayList<String>();

		list.addAll(uds.getHashMap().keySet());
		Collections.sort(list);

		try {
			s = "?";

			for (String key : list) {
				if (!key.equalsIgnoreCase("id")) {
					s += ", " + "?";
				}
			}
		} catch (Exception e) {
			Logger.logError(e, "Error creating SQL columns string from dataset.");
		}
		return "(" + s + ")";
	}

	private String getUpdateSqlValuesStringWithPlaceholders(UniDataSet uds) {
		String s = "";
		List<String> list = new ArrayList<String>();

		list.addAll(uds.getHashMap().keySet());
		Collections.sort(list);

		try {
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

	public void insertUniDataSet(UniDataSet uds) {
		String s;
		ResultSet rs;
		Statement stmt;
		PreparedStatement prepStmt;
		try {
			stmt = con.createStatement();

			s = "SELECT * FROM " + uds.sqlTabeName + " WHERE ID=" + uds.getStringValueByKey("id");
			rs = stmt.executeQuery(s);
			if (rs.next()) {
				Logger.logError("Dataset with this id is already in database" + uds.getStringValueByKey("name"));
				// updateUniDataSet (uds);
			} else {
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

	public void updateUniDataSet(UniDataSet uds) {
		String s;
		PreparedStatement prepStmt;

		try {
			s = "UPDATE " + uds.sqlTabeName + " SET " + getUpdateSqlValuesStringWithPlaceholders(uds) + " WHERE ID=" + uds.getStringValueByKey("id");
			prepStmt = con.prepareStatement(s);
			setSqlParameters(prepStmt, uds, false);
			prepStmt.executeUpdate();
			prepStmt.close();

		} catch (SQLException e) {
			Logger.logError(e, "Error saving dataset " + uds.getStringValueByKey("name"));
		}
	}

	@SuppressWarnings("unchecked")
	public void getTable(ArrayList uniDataList, UniDataSet udsTemplate) {
		String s;
		String columnName;
		ResultSet rs;
		Statement stmt;
		UniDataSet uds = null;

		try {
			stmt = con.createStatement();
			s = "SELECT * FROM " + udsTemplate.sqlTabeName;
			rs = stmt.executeQuery(s);
			ResultSetMetaData meta = rs.getMetaData();
			while (rs.next()) {
				uds = null;
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

				for (int i = 1; i <= meta.getColumnCount(); i++) {
					columnName = meta.getColumnName(i).toLowerCase();
					s = rs.getString(i);
					uds.setStringValueByKey(columnName, s);
				}
				uniDataList.add(uds);
			}

			rs.close();
			stmt.close();
		} catch (Exception e) {
			Logger.logError(e, "Error reading database table " + udsTemplate.sqlTabeName);
		}

	}

	public boolean connect(String workingDirectory) {
		String dataBaseName;
		ResultSet rs;

		try {
			Class.forName("org.hsqldb.jdbcDriver");
		} catch (ClassNotFoundException e) {
			Logger.logError("Class org.hsqldb.jdbcDriver not found");
			return false;
		}

		String path = workingDirectory + "/Database/";
		File directory = new File(path);
		if (!directory.exists())
			directory.mkdirs();

		dataBaseName = path + "Database";

		try {
			con = DriverManager.getConnection("jdbc:hsqldb:file:" + dataBaseName + ";shutdown=true", "sa", "");
			Statement stmt = con.createStatement();

			try {
				rs = stmt.executeQuery("SELECT * FROM Properties");
				rs.close();
			} catch (SQLException e) {
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
		} finally { /*
					 * if ( con != null ) try { con.close(); } catch (
					 * SQLException e ) { Logger.logError(e,
					 * "Error closing the Database:" + dataBaseName); }
					 */
		}
		return false;
	}

	public boolean isConnected() {
		return (con != null);
	}

	public void close() {
		if (con != null)
			try {
				con.close();
			} catch (SQLException e) {
				Logger.logError(e, "Error closing the Database");
			}
	}

}
