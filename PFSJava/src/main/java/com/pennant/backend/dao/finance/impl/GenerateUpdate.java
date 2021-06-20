package com.pennant.backend.dao.finance.impl;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;

import com.amazonaws.util.StringUtils;
import com.pennant.backend.model.finance.FinanceDisbursement;

public class GenerateUpdate {

	private static List<String> columns = new LinkedList<>();
	private static List<String> fields = new LinkedList<>();
	private static Object object = new FinanceDisbursement();
	private static String tableName = "FinDisbursementDetails";
	private static String whereClause = "Id= :Id";
	private static String varibaleName = "fd";
	private static boolean bulkUpdate = false;
	private static String listVaribaleName = "prvList";

	private static String getSelectQuery() {
		StringBuilder updateSql = new StringBuilder();
		updateSql.append(
				" LinkedTranId=:LinkedTranId , DisbDate =:DisbDate , DisbSeq =:DisbSeq");
		return updateSql.toString();
	}

	public static void main(String[] args) throws NoSuchFieldException, SecurityException {
		String sql = getSelectQuery();
		String temp = "";
		for (String colum : sql.split(",")) {
			colum = StringUtils.trim(colum);

			columns.add(StringUtils.trim(colum.split("=")[0]));
			fields.add(StringUtils.trim(colum.split("=")[1]).replace(":", ""));

		}

		StringBuilder builder = new StringBuilder("\t\tStringBuilder sql = new StringBuilder();");
		builder.append("\n\t\tsql.append(\"Update ").append(tableName).append(" set").append("\");");

		int i = 0;
		for (String column : columns) {
			if (temp.equals("")) {
				temp = "\n\t\tsql.append(\"";
			}
			if (i++ == 0) {
				temp += column + " = ?";
			} else {
				temp += ", " + column + " = ?";
			}
			if (temp.length() < 90) {
			} else {
				builder.append(temp + "\");");
				temp = "";
			}
		}

		if (bulkUpdate) {
			builder.append("\t\tjdbcOperations.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {");
			builder.append("\n\n\t\t\t@Override");
			builder.append("\n\t\t\tpublic void setValues(PreparedStatement ps, int i) throws SQLException {");
			builder.append("\n\t\t\t\t").append(object.getClass().getSimpleName()).append(" ").append(varibaleName)
					.append(" = ").append(listVaribaleName).append(".get(i);\n\n\t\t\t\t");
			builder.append("\n\t\t\t\t int index = 1;\n");
		} else {
			builder.append("\n\t\t // FIXME Please append where condition.");
			builder.append(
					"\n\n\t\tjdbcTemplate.getJdbcOperations().update(sql.toString(), new PreparedStatementSetter() {");
			builder.append("\n\n\t\t\t @Override");
			builder.append("\n\t\t\tpublic void setValues(PreparedStatement ps) throws SQLException {");
			builder.append("\n\t\t\t\t int index = 1;\n");
		}

		int index = 0;
		for (String field : fields) {
			Class<?> type = getType(field);

			if (index > 0) {
				builder.append("\n\t\t");
			}
			builder.append("\n\t\tps.set" + getType(type)).append("(").append("index++").append(", ");
			String getFieldName = field.substring(0, 1).toUpperCase().concat(field.substring(1, field.length()));

			if ("Boolean".equals(getType(type))) {
				builder.append(varibaleName);
				builder.append(".is").append(getFieldName).append("());");
			} else if ("Date".equals(getType(type))) {
				builder.append("JdbcUtil.getDate(");
				builder.append(varibaleName);
				builder.append(".get").append(getFieldName).append("()));");
			} else {
				builder.append(varibaleName);
				builder.append(".get").append(getFieldName).append("());");
			}
		}

		builder.append("\n\t\t // FIXME Please append fields of where condition.");
		builder.append("\n\t}});");

		System.out.println(builder.toString());

	}

	private static Class<?> getType(String fieldName) throws NoSuchFieldException {
		String concat = fieldName.substring(0, 1).toLowerCase().concat(fieldName.substring(1, fieldName.length()));

		Field field = null;
		try {
			field = object.getClass().getDeclaredField(concat);
		} catch (Exception e) {
		}

		if (field == null) {
			try {
				field = object.getClass().getSuperclass().getDeclaredField(concat);
			} catch (Exception e) {
			}

		}

		if (field == null) {
			try {
				field = object.getClass().getSuperclass().getSuperclass().getDeclaredField(concat);
			} catch (Exception e) {
			}

		}

		/* Handling the variable name start with upper case */
		if (field == null) {
			try {
				concat = fieldName.substring(0, 1).toUpperCase().concat(fieldName.substring(1, fieldName.length()));

				field = object.getClass().getDeclaredField(concat);
			} catch (Exception e) {
			}

		}

		return field.getType();
	}

	private static String getType(Class<?> type) {
		String simpleName = type.getSimpleName();
		return simpleName.substring(0, 1).toUpperCase().concat(simpleName.substring(1, simpleName.length()));
	}
}
