package com.pennant.backend.dao.finance.impl;

import java.lang.reflect.Field;
import java.util.LinkedHashSet;
import java.util.Set;

import com.pennant.backend.model.finance.FinanceScheduleDetail;

public class GenerateRowMapper {

	private static Set<String> fields = new LinkedHashSet<>();
	private static Object object = new FinanceScheduleDetail();
	private static String tableName = "FinScheduleDetails";
	private static String whereClause = "where Where CustID = ? and FinIsActive = ?";
	private static String varibaleName = "schd";

	private static String getSelectQuery() {
		StringBuilder selectSql = new StringBuilder(
				" SchDate, SchSeq, PftOnSchDate, CpzOnSchDate, RepayOnSchDate, RvwOnSchDate, BalanceForPftCal,ClosingBalance, ");
		selectSql.append(
				" CalculatedRate, NoOfDays, ProfitCalc, ProfitSchd, PrincipalSchd, DisbAmount, DownPaymentAmount, CpzAmount, CpzBalance, FeeChargeAmt, ");
		selectSql.append(" SchdPriPaid, SchdPftPaid, SchPftPaid, SchPriPaid, Specifier, SchdPftWaiver");
		
		

		return selectSql.toString();
	}

	public static void main(String[] args) throws NoSuchFieldException, SecurityException {
		String sql = getSelectQuery();

		String[] columns = sql.split(",");

		StringBuilder builder = new StringBuilder("StringBuilder sql = new StringBuilder(\"select\");");
		int i = 0;
		int k=0;
		String temp = "";
		for (String column : columns) {
			if (temp.equals("")) {
				if(k++ == 0) {
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
				System.out.println("");
			} else {
				builder.append(temp + "\");");
				temp = "";
			}

			fields.add(column.trim());
		}
		
		
		if(!"".equals(temp)) {
			builder.append(temp + "\");");
		}
		
		builder.append("\nsql.append(\" from ").append(tableName).append("\");");
		builder.append("\nsql.append(\" ").append(whereClause).append("\");");
		builder.append("\n");

		builder.append(
				"\nreturn this.jdbcTemplate.getJdbcOperations().query(sql.toString(), new PreparedStatementSetter() {");
		builder.append("\n\t@Override");
		builder.append("\n\tpublic void setValues(PreparedStatement ps) throws SQLException {");
		builder.append("\n\t\t// FIXME");
		builder.append("\n\t}");
		builder.append("\n}, new RowMapper<");
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
			method = method + varibaleName + ".set"
					+ field.substring(0, 1).toUpperCase().concat(field.substring(1, field.length()));
			method = method + "(rs.get" + type.getSimpleName().substring(0, 1).toUpperCase()
					.concat(type.getSimpleName().substring(1, type.getSimpleName().length()));
			method = method + "(\"";
			method = method + field;
			method = method + "\"));\t\t";

			builder.append("\n\t");
			builder.append(method);
		}
		builder.append("\n\n\treturn " + varibaleName).append(";");
		builder.append("\n}});");

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
		
		/*Handling the variable name start with upper case*/
		if (field == null) {
			try {
				concat = fieldName.substring(0, 1).toUpperCase().concat(fieldName.substring(1, fieldName.length()));

				field = object.getClass().getDeclaredField(concat);
			} catch (Exception e) {
			}
		}
		
		if (field == null) {
			System.out.println();
		}

		return field.getType();
	}
}
