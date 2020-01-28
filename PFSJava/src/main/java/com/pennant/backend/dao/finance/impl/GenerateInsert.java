package com.pennant.backend.dao.finance.impl;

import java.lang.reflect.Field;
import java.util.LinkedHashSet;
import java.util.Set;

import com.pennant.backend.model.finance.FinanceScheduleDetail;

public class GenerateInsert {

	private static Set<String> fields = new LinkedHashSet<>();
	private static Object object = new FinanceScheduleDetail();
	private static String tableName = "FinScheduleDetails";
	private static String whereClause = "where Where CustID = ? and FinIsActive = ?";
	private static String varibaleName = "schd";
	private static boolean bulkInsert = true;
	private static String listVaribaleName = "financeScheduleDetail";

	private static String getSelectQuery() {
		StringBuilder insertSql = new StringBuilder();
		insertSql.append("FinReference, SchDate, SchSeq, PftOnSchDate,");
		insertSql.append(" CpzOnSchDate, RepayOnSchDate, RvwOnSchDate, DisbOnSchDate,");
		insertSql.append(" DownpaymentOnSchDate, BalanceForPftCal, BaseRate, SplRate, MrgRate, ActRate, NoOfDays,");
		insertSql
				.append(" CalOnIndRate, DayFactor, ProfitCalc, ProfitSchd, PrincipalSchd, RepayAmount, ProfitBalance,");
		insertSql.append(
				" DisbAmount, DownPaymentAmount, CpzAmount, CpzBalance, OrgPft , OrgPri, OrgEndBal,OrgPlanPft, ");

		insertSql.append(" ClosingBalance, ProfitFraction, PrvRepayAmount, CalculatedRate,FeeChargeAmt,InsuranceAmt,");
		insertSql.append(" FeeSchd , SchdFeePaid , SchdFeeOS ,InsSchd, SchdInsPaid,");
		insertSql.append(" AdvBaseRate , AdvMargin , AdvPftRate , AdvCalRate , AdvProfit , AdvRepayAmount,");
		insertSql.append(" SuplRent , IncrCost ,SuplRentPaid , IncrCostPaid , TDSAmount, TDSPaid, PftDaysBasis, ");
		insertSql.append(" RefundOrWaiver, EarlyPaid, EarlyPaidBal,WriteoffPrincipal, WriteoffProfit, ");
		insertSql.append(
				" WriteoffIns , WriteoffIncrCost, WriteoffSuplRent, WriteoffSchFee, PartialPaidAmt, PresentmentId,TDSApplicable, ");
		insertSql.append(" SchdPriPaid, SchdPftPaid, SchPriPaid, SchPftPaid, Specifier,");
		insertSql.append(" DefSchdDate, SchdMethod, ");
		insertSql.append(" InstNumber, BpiOrHoliday, FrqDate,");
		insertSql.append(" RolloverOnSchDate , RolloverAmount, RolloverAmountPaid, LimitDrop, ODLimit, AvailableLimit");

		return insertSql.toString();
	}

	public static void main(String[] args) throws NoSuchFieldException, SecurityException {
		String sql = getSelectQuery();

		String[] columns = sql.split(",");

		StringBuilder builder = new StringBuilder("\t\tStringBuilder sql = new StringBuilder(\"insert into\");");
		builder.append("\n\t\tsql.append(\" ").append(tableName).append(" \");").append(" // FIXME add table suffix");
		int i = 0;
		int k = 0;
		String temp = "";
		for (String column : columns) {
			if (temp.equals("")) {
				if (k++ == 0) {
					temp = "\n\t\tsql.append(\"(";
				} else {
					temp = "\n\t\tsql.append(\"";
				}

			}
			if (i++ != 0) {
				temp += ", ";
			}
			temp += column.trim();

			if (temp.length() < 95) {
				//
			} else {
				builder.append(temp + "\");");
				temp = "";
			}

			fields.add(column.trim());
		}
		if (!temp.equals("")) {
			builder.append(temp + "\");");
			temp = "";
		}

		builder.append("\n\t\tsql.append(\") values(").append("\");");
		int j = 0;

		for (String column : fields) {
			if (temp.equals("")) {
				temp = "\n\t\tsql.append(\"";
			}
			if (j++ != 0) {
				temp += ", ";
			}
			temp += "?";
			if (temp.length() < 95) {

			} else {
				builder.append(temp + "\");");
				temp = "";
			}
			fields.add(column.trim());
		}
		if (!temp.equals("")) {
			builder.append(temp + "\");");
			temp = "";
		}
		builder.append("\n\t\tsql.append(\"));").append("\");");

		builder.append("\n\n\n");

		if (bulkInsert) {
			builder.append(
					"\t\tjdbcTemplate.getJdbcOperations().batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {");
			builder.append("\n\n\t\t\t@Override");
			builder.append("\n\t\t\tpublic void setValues(PreparedStatement ps, int i) throws SQLException {");
			builder.append("\n\t\t\t\t").append(object.getClass().getSimpleName()).append(" ").append(varibaleName)
					.append(" = ").append(listVaribaleName).append(".get(i);\n\t\t\t\t");
		} else {
			builder.append(
					"\t\tjdbcTemplate.getJdbcOperations().update(sql.toString(), new PreparedStatementSetter() {");
			builder.append("\n\n\t\t\t@Override");
			builder.append("\n\t\t\tpublic void setValues(PreparedStatement ps) throws SQLException {");
		}
		builder.append("\n\t\t\t\t int index = 1;\n");

		int index = 0;
		for (String field : fields) {
			Class<?> type = getType(field);

			if (index > 0) {
				builder.append("\n\t\t\t\t");
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
			} else if ("Long".equals(getType(type))) {
				builder.append(varibaleName);
				builder.append(".get").append(getFieldName).append("());");
			} else {
				builder.append(varibaleName);
				builder.append(".get").append(getFieldName).append("());");
			}
		}

		builder.append("\n\t}");
		if (bulkInsert) {
			builder.append("\n\n\t\t\t@Override");
			builder.append("\n\t\t\tpublic int getBatchSize() {");
			builder.append("\n\t\t\t\treturn " + listVaribaleName + ".size();");
			builder.append("\n\t\t\t}");
		}
		builder.append("\n\t});");

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
			System.out.println();
		}

		return field.getType();
	}

	private static String getType(Class<?> type) {
		String simpleName = type.getSimpleName();
		return simpleName.substring(0, 1).toUpperCase().concat(simpleName.substring(1, simpleName.length()));
	}
}
