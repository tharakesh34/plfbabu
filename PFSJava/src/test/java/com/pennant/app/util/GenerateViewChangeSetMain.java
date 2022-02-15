package com.pennant.app.util;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pennanttech.pennapps.core.resource.Literal;

public class GenerateViewChangeSetMain {
	private static final Logger logger = LogManager.getLogger(GenerateViewChangeSetMain.class);

	static final String DB_URL = "jdbc:postgresql://192.168.120.26:5432/plf_core_finid_dev";
	static final String USER = "postgres";
	static final String PASS = "Pennant_123";

	public static void main(String[] args) {
		GenerateViesChangeSet generateChangeSet = new GenerateViesChangeSet();

		Map<String, String> finalViews = new HashMap<>();

		List<String> tables = getTables();
		Map<String, String> views = getViews();

		int tablecount = tables.size();
		for (String table : tables) {
			for (Entry<String, String> viewSet : views.entrySet()) {

				if (finalViews.containsKey(viewSet.getKey())) {
					continue;
				}

				String viewDef = viewSet.getValue();
				viewDef = viewDef.toLowerCase();
				if (viewDef.contains(table) && viewDef.contains("finreference")) {

					viewDef = viewDef.replaceAll(" as ", " ");
					viewDef = viewDef.replaceAll(" AS ", " ");
					viewDef = viewDef.replaceAll(" As ", " ");
					viewDef = viewDef.replaceAll(" aS ", " ");
					// viewDef = viewDef.replaceAll("\\(", "");
					// viewDef = viewDef.replaceAll("\\)", "");
					viewDef = viewDef.replaceAll("::text", "");
					viewDef = viewDef.replaceAll("::character varying500::bpchar", "");
					viewDef = viewDef.replaceAll("::character varying", "");
					viewDef = viewDef.replaceAll("::bigint", "");
					viewDef = viewDef.replaceAll("::numeric18,3", "");
					viewDef = viewDef.replaceAll("::numeric", "");
					viewDef = viewDef.replaceAll(";", "");
					viewDef = viewDef.replaceAll("operator(pg_catalog.=)", " = ");

					finalViews.put(viewSet.getKey(), viewDef);

				}
			}

			System.out.println(tablecount--);
		}

		int i = 1;
		for (Entry<String, String> viewSet : finalViews.entrySet()) {
			String changeSet;
			try {
				changeSet = generateChangeSet.getChangeSet(viewSet.getKey(), i++, viewSet.getValue());

				FileUtils.write(new File("D:/change-log-finid_views.xml"), changeSet, true);
				FileUtils.write(new File("D:/change-log-finid_views.xml"), "\n", true);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error(Literal.EXCEPTION, e);
			}

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

	private static Map<String, String> getViews() {
		Map<String, String> views = new HashMap<>();
		try (Connection conn = getConnection();) {
			try (Statement stmt = conn.createStatement()) {
				try (ResultSet rs = stmt.executeQuery(
						"select table_Name, view_definition from INFORMATION_SCHEMA.views where table_schema='plf'")) {
					while (rs.next()) {
						views.put(rs.getString(1), rs.getString(2));
					}
				}
			} catch (SQLException e) {
				// e.printStackTrace();
			}
		} catch (SQLException e) {
			// e.printStackTrace();
		}

		return views;
	}
}
