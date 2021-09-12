package com.pennant.backend.dao.finance.impl;

import java.lang.reflect.Field;
import java.util.LinkedHashSet;
import java.util.Set;

import com.pennant.backend.model.applicationmaster.CheckListDetail;

public class GenerateRowMapper {

	private static Set<String> fields = new LinkedHashSet<>();
	private static Object object = new CheckListDetail();
	private static String tableName = "LiabilityRequest";
	private static String whereClause = "";
	private static String varibaleName = "cld";
	private static boolean list = false;

	private static String getSelectQuery() {
		StringBuilder sql = new StringBuilder();
		sql.append(" CheckListId, AnsSeqNo, AnsDesc, AnsCond, RemarksAllow, DocRequired, DocType, RemarksMand");
		sql.append(", Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId");
		sql.append(", RecordType, WorkflowId");

		return sql.toString();
	}

	public static void main(String[] args) throws NoSuchFieldException, SecurityException {
		String sql = getSelectQuery();
		String[] tempColms = sql.split(",");
		String[] columns = new String[tempColms.length];

		int t = 0;
		for (String column : tempColms) {
			column = column.trim();
			column = column.substring(0, 1).toUpperCase() + column.substring(1, column.length());
			columns[t++] = column;

		}

		StringBuilder builder = new StringBuilder("logger.debug(Literal.ENTERING); \n ");
		builder.append(" \n StringBuilder sql = new StringBuilder(\"Select\");");

		int i = 0;
		int k = 0;
		String temp = "";
		for (String column : columns) {
			if (temp.equals("")) {
				if (k++ == 0) {
					temp = "\nsql.append(\" ";
				} else {
					temp = "\nsql.append(\"";
				}

			}
			if (i++ != 0) {
				temp += ", ";
			}
			temp += column.trim();

			if (temp.length() < 90) {

			} else {
				builder.append(temp + "\");");
				temp = "";
			}

			fields.add(column.trim());
		}

		if (!"".equals(temp)) {
			builder.append(temp + "\");");
		}

		builder.append("\nsql.append(\" from ").append(tableName).append("\");");
		builder.append("\nsql.append(\" ").append(whereClause).append("\");");
		builder.append("\n");
		builder.append("\n").append("logger.trace(Literal.SQL + sql.toString()); \n");
		builder.append("\n try { \n");
		if (list) {
			builder.append("\treturn this.jdbcOperations.query(sql.toString(), new PreparedStatementSetter() {");
			builder.append("\n\t@Override");
			builder.append("\n\tpublic void setValues(PreparedStatement ps) throws SQLException {");
			builder.append("\n \t int index = 1; ");
			builder.append("\n\t\t// FIXME");
			builder.append("\n\t}");
			builder.append("\n}, new RowMapper<");
		} else {
			builder.append("return this.jdbcOperations.queryForObject(sql.toString(), new Object[] {FIXME}");
			builder.append(", new RowMapper<");
		}

		builder.append(object.getClass().getSimpleName());
		builder.append(">() {");
		builder.append("\n@Override");
		builder.append("\npublic ");
		builder.append(object.getClass().getSimpleName());
		builder.append(" mapRow(ResultSet rs, int rowNum) throws SQLException {");
		builder.append("\n");
		builder.append("\t").append(object.getClass().getSimpleName()).append(" ").append(varibaleName).append(" = ")
				.append("new ").append(object.getClass().getSimpleName()).append("();\n");

		for (String field : fields) {
			Class<?> type = getType(field);
			String method = "";
			if ("Date".equals(type.getSimpleName())) {
				method = method + varibaleName + ".set"
						+ field.substring(0, 1).toUpperCase().concat(field.substring(1, field.length()));
				method = method + "(rs.getTimestamp";
			} else {
				method = method + varibaleName + ".set"
						+ field.substring(0, 1).toUpperCase().concat(field.substring(1, field.length()));
				method = method + "(rs.get" + type.getSimpleName().substring(0, 1).toUpperCase()
						.concat(type.getSimpleName().substring(1, type.getSimpleName().length()));
			}
			method = method + "(\"";
			method = method + field;
			method = method + "\"));\t\t";

			builder.append("\n\t");
			builder.append(method);
		}
		builder.append("\n\n\treturn " + varibaleName).append(";");
		builder.append("\n}});");
		builder.append(" \n } catch(EmptyResultDataAccessException e) { \n");
		builder.append(" logger.error(Literal.EXCEPTION,e);");
		builder.append("\n}");
		builder.append("\n \n logger.debug(Literal.LEAVING);");
		if (list) {

			builder.append("\n return ").append("new ArrayList<>()").append(";");
		} else {

			builder.append("\n return ").append("null").append(";");
		}
		System.out.println(builder.toString());

	}

	private static Class<?> getType(String fieldName) throws NoSuchFieldException {
		String concat = fieldName.substring(0, 1).toLowerCase().concat(fieldName.substring(1, fieldName.length()));

		Field field = null;
		try {
			field = object.getClass().getDeclaredField(concat);
		} catch (Exception e) {
			// TODO: handle exception
		}

		if (field == null) {
			try {
				field = object.getClass().getSuperclass().getDeclaredField(concat);
			} catch (Exception e) {
				// TODO: handle exception
			}

		}

		if (field == null) {
			try {
				field = object.getClass().getSuperclass().getSuperclass().getDeclaredField(concat);
			} catch (Exception e) {
				// TODO: handle exception
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

		if (field == null) {
			System.out.println(fieldName);
		}

		return field.getType();
	}
}
