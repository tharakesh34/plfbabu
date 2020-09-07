package com.pennant.backend.dao.finance.impl;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;

import com.amazonaws.util.StringUtils;
import com.pennant.backend.model.finance.FinanceProfitDetail;

public class GenerateUpdate {

	private static List<String> columns = new LinkedList<>();
	private static List<String> fields = new LinkedList<>();
	private static Object object = new FinanceProfitDetail();
	private static String tableName = "FinPftDetails";
	private static String whereClause = "FinReference = :FinReference";
	private static String varibaleName = "fm";
	private static boolean bulkUpdate = true;
	private static String listVaribaleName = "financeMailList";

	private static String getSelectQuery() {
		StringBuilder updateSql = new StringBuilder();
		updateSql.append(" PftAccrued = :PftAccrued, PftAccrueSusp = :PftAccrueSusp, PftAmz = :PftAmz,");
		updateSql.append(" PftAmzSusp = :PftAmzSusp, PftAmzNormal = :PftAmzNormal, PftAmzPD = :PftAmzPD,");
		updateSql.append(" PftInSusp = :PftInSusp, CurFlatRate = :CurFlatRate, CurReducingRate = :CurReducingRate,");
		updateSql.append(" TotalPftSchd = :TotalPftSchd, TotalPftCpz = :TotalPftCpz, TotalPftPaid = :TotalPftPaid,");
		updateSql.append(" TotalPftBal = :TotalPftBal, TdSchdPft = :TdSchdPft, TdPftCpz = :TdPftCpz,");
		updateSql.append(" TdSchdPftPaid = :TdSchdPftPaid, TdSchdPftBal = :TdSchdPftBal,");
		updateSql.append(" TotalpriSchd = :TotalpriSchd,TotalPriPaid = :TotalPriPaid, TotalPriBal = :TotalPriBal,");
		updateSql.append(" TdSchdPri = :TdSchdPri, TdSchdPriPaid = :TdSchdPriPaid, TdSchdPriBal = :TdSchdPriBal,");
		updateSql.append(" CalPftOnPD = :CalPftOnPD, PftOnPDMethod = :PftOnPDMethod, PftOnPDMrg = :PftOnPDMrg,");
		updateSql.append(" TotPftOnPD = :TotPftOnPD,TotPftOnPDPaid = :TotPftOnPDPaid,");
		updateSql.append(" TotPftOnPDWaived = :TotPftOnPDWaived, TotPftOnPDDue = :TotPftOnPDDue,");
		updateSql.append(" NOInst = :NOInst, NOPaidInst = :NOPaidInst, NOODInst = :NOODInst,");
		updateSql.append(" FutureInst = :FutureInst, RemainingTenor = :RemainingTenor, TotalTenor = :TotalTenor,");
		updateSql.append(
				" ODPrincipal = :ODPrincipal, ODProfit = :ODProfit, CurODDays = :CurODDays, ActualODDays = :ActualODDays,");
		updateSql.append(" MaxODDays = :MaxODDays, FirstODDate = :FirstODDate, PrvODDate = :PrvODDate,");
		updateSql.append(" PenaltyPaid = :PenaltyPaid, PenaltyDue = :PenaltyDue, PenaltyWaived = :PenaltyWaived,");
		updateSql.append(" FirstRepayDate = :FirstRepayDate, FirstRepayAmt = :FirstRepayAmt,");
		updateSql.append(" FinalRepayAmt = :FinalRepayAmt, FirstDisbDate = :FirstDisbDate,");
		updateSql.append(" LatestDisbDate = :LatestDisbDate, FullPaidDate = :FullPaidDate,");
		updateSql.append(" PrvRpySchDate = :PrvRpySchDate, PrvRpySchPri = :PrvRpySchPri,");
		updateSql.append(" PrvRpySchPft = :PrvRpySchPft,");
		updateSql.append(" NSchdDate = :NSchdDate, NSchdPri = :NSchdPri, NSchdPft = :NSchdPft, ");
		updateSql.append(" NSchdPriDue = :NSchdPriDue, NSchdPftDue = :NSchdPftDue,");
		updateSql.append(" AccumulatedDepPri = :AccumulatedDepPri, DepreciatePri = :DepreciatePri,");
		updateSql.append(" TotalPriPaidInAdv = :TotalPriPaidInAdv,");
		updateSql.append(" FinStatus = :FinStatus, FinStsReason = :FinStsReason, FinWorstStatus = :FinWorstStatus, ");
		updateSql.append(
				" BaseRateCode =:BaseRateCode,SplRateCode=:SplRateCode, BaseRate=:BaseRate ,SplRate=:SplRate ,MrgRate=:MrgRate , TdTdsAmount=:TdTdsAmount ,TdTdsPaid =:TdTdsPaid, TdTdsBal=:TdTdsBal ,TdsAccrued=:TdsAccrued,NoInstEarlyStl=:NOInst,");

		updateSql.append(
				" TotalPftPaidInAdv = :TotalPftPaidInAdv, LastMdfDate = :LastMdfDate, MaxRpyAmount = :MaxRpyAmount, ");
		updateSql.append(" CpzPosted = :CpzPosted, CurCpzBalance=:CurCpzBalance, GlPaymentSts = :GlPaymentSts, ");
		updateSql.append(" NOAutoIncGrcEnd = :NOAutoIncGrcEnd ");

		if (true) {
			updateSql.append(
					",AmzTillLBD = :AmzTillLBD, LpiTillLBD=:LpiTillLBD, LppTillLBD = :LppTillLBD, GstLpiTillLBD = :GstLpiTillLBD, GstLppTillLBD = :GstLppTillLBD, AmzTillLBDNormal = :AmzTillLBDNormal, ");
			updateSql.append(" AmzTillLBDPD = :AmzTillLBDPD, AmzTillLBDPIS = :AmzTillLBDPIS,");
			updateSql.append(" AcrTillLBD = :AcrTillLBD, AcrSuspTillLBD = :AcrSuspTillLBD ");
		}

		if (true) {
			updateSql.append(" ,PrvMthAmz = :PrvMthAmz, PrvMthAmzNrm = :PrvMthAmzNrm, ");
			updateSql.append(" PrvMthAmzPD = :PrvMthAmzPD, PrvMthAmzSusp = :PrvMthAmzSusp,");
			updateSql.append(" PrvMthAcr = :PrvMthAcr, PrvMthAcrSusp = :PrvMthAcrSusp, AMZMethod = :AMZMethod");
		}
		updateSql.append(" , SvnAcrTillLBD = :SvnAcrTillLBD, SvnAcrCalReq = :SvnAcrCalReq");

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
			builder.append(
					"\t\tjdbcTemplate.getJdbcOperations().batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {");
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
				builder.append("JdbcUtil.getSqlDate(");
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
