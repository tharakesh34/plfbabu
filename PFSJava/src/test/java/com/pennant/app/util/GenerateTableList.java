package com.pennant.app.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

public class GenerateTableList {
	static final String DB_URL = "jdbc:postgresql://192.168.120.26:5432/plf_core_finid_dev";
	static final String USER = "postgres";
	static final String PASS = "Pennant_123";

	public static void main(String[] args) {
		System.out.println("Getting Table names from DB...");

		List<String> tables = getTables();

		System.out.println("Writing Table names into file...");
		try {

			File f = new File("D:/FinID/Tablenames.txt");

			if (f.exists()) {
				f.delete();
			}

			f.createNewFile();

			FileUtils.writeLines(f, tables, true);
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("Preparing FinanceMain Data from All tables...");

		try {
			getFinIdData(tables);
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("Process Completed...");

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
				e.printStackTrace();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		List<String> names = new ArrayList<>();
		for (String table : tables) {
			if (!StringUtils.containsAnyIgnoreCase(table, "View")) {
				names.add(table);
			}
		}

		return names;
	}

	@SuppressWarnings("resource")
	private static void getFinIdData(List<String> tableNames) throws Exception {
		FileOutputStream outputStream = null;
		BufferedOutputStream bos = null;
		Workbook workbook = null;
		Sheet sheet = null;
		Statement stmt = null;
		Connection conn = null;

		try {

			File f1 = new File("D:/FinID/FinReferenceNotExist.txt");

			if (f1.exists()) {
				f1.delete();
			}

			f1.createNewFile();

			File f = new File("D:/FinID/FinanceMain.xls");

			if (f.exists()) {
				f.delete();
			}

			f.createNewFile();

			workbook = new HSSFWorkbook();
			sheet = workbook.createSheet("Loan-Data");
			int rowCount = 0;
			createHeaders(sheet, rowCount);

			try {
				conn = getConnection();
				stmt = conn.createStatement();
			} catch (Exception e) {
				e.printStackTrace();
			}

			for (String tableName : tableNames) {
				String sql = "Select distinct FinId, FinReference from " + tableName
						+ " Where FinReference = '1500AGR0009364'";

				System.out.println(sql);
				ResultSet executeQuery = null;

				try {
					executeQuery = stmt.executeQuery(sql);
				} catch (Exception e) {
					FileUtils.write(f1, sql, true);
				}

				if (executeQuery == null) {
					continue;
				}

				try (ResultSet rs = executeQuery) {
					while (rs.next()) {
						Row row = sheet.createRow(++rowCount);

						Cell cell = row.createCell(0);
						cell.setCellType(CellType.STRING);
						cell.setCellValue(tableName);

						cell = row.createCell(1);
						cell.setCellType(CellType.STRING);
						cell.setCellValue(String.valueOf(rs.getObject(1)));

						cell = row.createCell(2);
						cell.setCellType(CellType.STRING);
						cell.setCellValue(rs.getString(2));
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}

			}

			outputStream = new FileOutputStream(f);
			bos = new BufferedOutputStream(outputStream);
			workbook.write(bos);

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (bos != null) {
				bos.close();
				bos.flush();
				bos = null;
			}
			if (outputStream != null) {
				outputStream.flush();
				outputStream.close();
				outputStream = null;
			}
			if (workbook != null) {
				workbook = null;
			}
			sheet = null;

			stmt.close();
			conn.close();
		}
	}

	private static void createHeaders(Sheet sheet, int rowCount) {
		Row row = sheet.createRow(rowCount);

		Cell cell = row.createCell(0);
		cell.setCellType(CellType.STRING);
		cell.setCellValue("TABLE NAME");

		cell = row.createCell(1);
		cell.setCellType(CellType.STRING);
		cell.setCellValue("FIN ID");

		cell = row.createCell(2);
		cell.setCellType(CellType.STRING);
		cell.setCellValue("FIN REFERENCE");
	}
}
