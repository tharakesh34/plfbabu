package com.pennant;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

/**
 * A dirty simple program that reads an Excel file.
 * 
 * @author Kesava.j
 * 
 */
public class RightsPreparation {

	public static void main(String[] args) throws IOException {
		boolean firstRow = true;
		String group = "";
		String role = "";
		String excelFilePath = "/home/veeraprasad.d/Downloads/Caste_Rights.xls";
		
		FileInputStream inputStream = new FileInputStream(new File(excelFilePath));
		Workbook workbook = new HSSFWorkbook(inputStream);
		Sheet firstSheet = workbook.getSheetAt(0);
		Iterator<Row> iterator = firstSheet.iterator();

		System.out.println("Update SeqSecGroups Set SeqNo = (Select MAX(GrpID) + 1 from SecGroups);");
		System.out.println("Update SeqSecRights Set SeqNo = (Select MAX(RightID) + 1 from SecRights);");
		System.out.println("Update SeqSecGroupRights Set SeqNo = (Select MAX(GrpRightID) + 1 from SecGroupRights);");
		System.out.println("Update SeqSecRoleGroups Set SeqNo = (Select MAX(RoleGrpID) + 1 from SecRoleGroups);");
		System.out.println();
		
		while (iterator.hasNext()) {
			Row nextRow = iterator.next();
			Iterator<Cell> cellIterator = nextRow.cellIterator();

			String rightType, rightName, page, newRight, newGroup, groupDesc;
			String script;
			Cell cell = cellIterator.next();

			if (firstRow) {
				firstRow = false;
				continue;
			}

			rightType = getValue(cell);
			rightName = getValue(cellIterator.next());
			page = getValue(cellIterator.next());
			group = getValue(cellIterator.next());
			newRight = getValue(cellIterator.next());
			newGroup = getValue(cellIterator.next());

			if ("Y".equals(newGroup)) {
				groupDesc = getValue(cellIterator.next());
				role = getValue(cellIterator.next());
				System.out.println("Delete from SecGroupRights where GrpID In (Select GrpID from SecGroups where GrpCode = '" + group + "');");
				System.out.println("Delete from SecRoleGroups where GrpID IN (Select GrpId from SecGroups WHERE GrpCode = '" + group + "')"
						+ " And RoleID IN (Select RoleID from SecRoles WHERE RoleCd = '" + role + "');");
				System.out.println("Delete from SecGroups where GrpCode = '" + group + "';");
				System.out.println("INSERT INTO SecGroups Values ((Select MAX(GrpID)+1 From SecGroups), '" + group
						+ "'," + "'" + groupDesc + "', 1, 1000, CURRENT_TIMESTAMP, NULL, NULL, NULL, NULL, NULL, NULL,0);");
				System.out.println();
			}

			if ("Y".equals(newRight)) {
				script = "Delete from SecGroupRights where RightID In (Select RightID from SecRights where RightName = '"
						+ rightName + "');";
				System.out.println(script);

				script = "Delete from SecRights where RightName = '" + rightName + "';";
				System.out.println(script);
				
				script = "Insert into SecRights Values((Select max(RightID) + 1 from SecRights), " + rightType + ", '"
						+ rightName + "', '" + page + "', 1, 1000, CURRENT_TIMESTAMP, null, null, null, null, null, null, 0 );";

				System.out.println(script);
			}

			script = "Insert into secGroupRights Values((Select max(GrpRightID) + 1 from SecGroupRights), "
					+ "(Select GrpID from SecGroups where GrpCode = '" + group + "'), "
					+ "(Select RightID from SecRights where RightName = '" + rightName
					+ "'), 1, 1, 1000, CURRENT_TIMESTAMP, null, null, null, null, null, null, 0 );";
			System.out.println(script);
			
			script = "INSERT INTO SecRoleGroups values ((SELECT MAX(RoleGrpID) + 1 from SecRoleGroups), "
					+ "(Select MAX(GrpId) from SecGroups WHERE GrpCode = '" + group + "'), "
					+ "(Select MAX(RoleID) from SecRoles WHERE RoleCd = 'MSTGRP1_MAKER'), 0, 1000, CURRENT_TIMESTAMP, NULL, NULL, NULL, NULL, NULL, NULL, 0);";
			
			if ("Y".equals(newGroup)) {
				System.out.println("INSERT INTO SecRoleGroups values ((SELECT MAX(RoleGrpID) + 1 from SecRoleGroups), "
						+ "(Select MAX(GrpId) from SecGroups WHERE GrpCode = '" + group +"'), "
								+ "(Select MAX(RoleID) from SecRoles WHERE RoleCd = '" + role + "'), 0, 1000, CURRENT_TIMESTAMP, NULL, NULL, NULL, NULL, NULL, NULL, 0);");
			}

			System.out.println();
		}
		// workbook.close();
		inputStream.close();
		System.out.println("Update SeqSecGroups Set SeqNo = (Select MAX(GrpID) + 1 from SecGroups);");
		System.out.println("Update SeqSecRights Set SeqNo = (Select MAX(RightID) + 1 from SecRights);");
		System.out.println("Update SeqSecGroupRights Set SeqNo = (Select MAX(GrpRightID) + 1 from SecGroupRights);");
		System.out.println("Update SeqSecRoleGroups Set SeqNo = (Select MAX(RoleGrpID) + 1 from SecRoleGroups);");
	}

	private static String getValue(Cell cell) {
		String value = "";

		switch (cell.getCellType()) {
		case Cell.CELL_TYPE_STRING:
			value = cell.getStringCellValue();
			break;
		case Cell.CELL_TYPE_BOOLEAN:
			System.out.print(cell.getBooleanCellValue());
			break;
		case Cell.CELL_TYPE_NUMERIC:
			value = String.valueOf(cell.getNumericCellValue()).substring(0, 1);
			break;
		}

		return value;
	}
}