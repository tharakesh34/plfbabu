package com.pennant.app.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class GenerateAdtChangeSetMain {
	static final String DB_URL = "jdbc:postgresql://192.168.120.26:5432/plf_core_finid_dev";
	static final String USER = "postgres";
	static final String PASS = "Pennant_123";

	public static void main(String[] args) {
		GenerateAdtChangeSet generateChangeSet = new GenerateAdtChangeSet();

		List<String> tables = getTables();

		int id = 2;
		try {
			for (String table : tables) {
				String changeSet = null;

				changeSet = generateChangeSet.getChangeSet(StringUtils.trimToEmpty(table), id);

				if (changeSet != null) {
					System.out.println(changeSet);
					System.out.println();
					id = id + 1;
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private static Connection getConnection() throws SQLException {
		Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
		return conn;
	}

	private static List<String> getTables() {
		List<String> tables = new ArrayList<>();
		try (Connection conn = getConnection();) {
			try (Statement stmt = conn.createStatement()) {
				try (ResultSet rs = stmt.executeQuery(
						"select table_name from INFORMATION_SCHEMA.COLUMNS where column_name = 'finid'")) {
					while (rs.next()) {
						tables.add(rs.getString(1));
					}
				}
			} catch (SQLException e) {
				// e.printStackTrace();
			}
		} catch (SQLException e) {
			// e.printStackTrace();
		}

		return tables;
	}
}
