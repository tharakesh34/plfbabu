package com.pennant.app.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class GenerateChangeSet {
	static final String DB_URL = "jdbc:postgresql://192.168.120.26:5432/plf_core_finid_dev";
	static final String USER = "postgres";
	static final String PASS = "Pennant_123";

	public String getChangeSet(String tableName, int id) throws Exception {
		StringBuilder builder = new StringBuilder();

		boolean finIdExists = isFinIDExist(tableName);
		boolean tempExists = isTempExist(tableName + "_temp");
		boolean tempFinIdExists = false;

		if (finIdExists && tempExists) {
			tempFinIdExists = isFinIDExist(tableName + "_temp");
		}

		if (finIdExists && tempExists) {
			if (!tempFinIdExists) {
				System.out.println(tableName);
				throw new Exception("FinID not exists in Temp table.");
			} else {
				return null;
			}
		}

		if (finIdExists) {
			return null;
		}

		builder.append("<changeSet id=").append("\"").append(id).append("\" ").append("author=\"murthy.y\">");
		builder.append("\n\t<addColumn tableName=\"" + tableName + "\">");
		builder.append("\n\t\t<column name=\"FinID\" type=\"bigint\" />");
		builder.append("\n\t\t</addColumn>");
		builder.append("</changeSet>");

		if (tempExists) {
			builder.append("<changeSet id=").append("\"").append(id).append(".1\" ").append("author=\"murthy.y\">");
			builder.append("\n\t<addColumn tableName=\"" + tableName + "_temp\">");
			builder.append("\n\t\t<column name=\"FinID\" type=\"bigint\" />");
			builder.append("\n\t\t</addColumn>");
			builder.append("</changeSet>");
		}

		builder.append("<changeSet id=").append("\"").append(id).append(".2\" ").append("author=\"murthy.y\">");
		builder.append("\n\t<sql>");
		builder.append("\n\t\t\t<![CDATA[");
		builder.append("\n\t\t\t\tUpdate ").append(tableName)
				.append(" set FinID = (select m.FinID from FINID_MAPPING m where ").append(tableName)
				.append(".FinReference = m.FinReference);");
		if (tempExists) {
			builder.append("\n\t\t\t\tUpdate ").append(tableName)
					.append("_temp set FinID = (select m.FinID from FINID_MAPPING m where ").append(tableName)
					.append("_temp.FinReference = m.FinReference);");
		}

		builder.append("\n\t\t\t]]>");
		builder.append("\n\t</sql>");
		builder.append("</changeSet>");

		builder.append("<changeSet id=").append("\"").append(id).append(".3\" ").append("author=\"murthy.y\">");
		builder.append(String.format(
				"<addForeignKeyConstraint constraintName=\"fk_%s_finid\" referencedTableName=\"FinanceMain\" baseColumnNames=\"FinID\" baseTableName=\"%s\" referencedColumnNames=\"FinID\" />",
				tableName, tableName));
		builder.append(
				String.format("<createIndex tableName=\"%s\" indexName=\"idx_%s_finid\">", tableName, tableName));
		builder.append("\n\t<column name=\"FinID\" type=\"bigint\" />");
		builder.append("</createIndex>");

		if (tempExists) {
			builder.append(String.format("<createIndex tableName=\"%s\" indexName=\"idx_%s_finid\">",
					tableName + "_temp", tableName + "_t"));
			builder.append("\n\t<column name=\"FinID\" type=\"bigint\" />");
			builder.append("</createIndex>");
		}
		builder.append("</changeSet>");
		return builder.toString();
	}

	private Connection getConnection() throws SQLException {
		Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
		return conn;
	}

	private boolean isFinIDExist(String tableName) {
		try (Connection conn = getConnection();) {
			try (Statement stmt = conn.createStatement()) {
				try (ResultSet rs = stmt.executeQuery("select count(FinID) from " + tableName + " limit 1")) {
					return rs.next();
				}
			} catch (SQLException e) {
				// e.printStackTrace();
			}
		} catch (SQLException e) {
			// e.printStackTrace();
		}

		return false;
	}

	private boolean isTempExist(String tableName) {
		try (Connection conn = getConnection();) {
			try (Statement stmt = conn.createStatement()) {
				try (ResultSet rs = stmt.executeQuery("select count(*) from " + tableName + " limit 1")) {
					return rs.next();
				}
			} catch (SQLException e) {
				// e.printStackTrace();
			}
		} catch (SQLException e) {
			// e.printStackTrace();
		}

		return false;
	}

}
