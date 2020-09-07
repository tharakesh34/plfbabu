package com.pennant.backend.dao.finance.impl;

import java.lang.reflect.Field;
import java.util.LinkedHashSet;
import java.util.Set;

import com.pennant.backend.model.finance.FinanceMain;

public class GenerateInsert {

	private static Set<String> fields = new LinkedHashSet<>();
	private static Object object = new FinanceMain();
	private static String tableName = "FinanceMain";
	private static String varibaleName = "fm";
	private static boolean bulkInsert = false;
	private static String listVaribaleName = "finaneMain";

	private static String getSelectQuery() {
		StringBuilder sql = new StringBuilder();
		sql.append(" FinReference, GraceTerms, NumberOfTerms, GrcPeriodEndDate, AllowGrcPeriod");
		sql.append(", GraceBaseRate, GraceSpecialRate, GrcPftRate, GrcPftFrq, NextGrcPftDate, AllowGrcPftRvw");
		sql.append(", GrcPftRvwFrq, NextGrcPftRvwDate, AllowGrcCpz, GrcCpzFrq, NextGrcCpzDate, RepayBaseRate");
		sql.append(", RepaySpecialRate, RepayProfitRate, RepayFrq, NextRepayDate, RepayPftFrq, NextRepayPftDate");
		sql.append(", AllowRepayRvw, RepayRvwFrq, NextRepayRvwDate, AllowRepayCpz, RepayCpzFrq, NextRepayCpzDate");
		sql.append(", MaturityDate, CpzAtGraceEnd, DownPayment, DownPayBank, DownPaySupl, ReqRepayAmount");
		sql.append(", TotalProfit, TotalCpz, TotalGrossPft, TotalGracePft, TotalGraceCpz, TotalGrossGrcPft");
		sql.append(", TotalRepayAmt, GrcRateBasis, RepayRateBasis, FinType, FinRemarks, FinCcy, ScheduleMethod");
		sql.append(", FinContractDate, ProfitDaysBasis, ReqMaturity, CalTerms, CalMaturity, FirstRepay");
		sql.append(", LastRepay, FinStartDate, FinAmount, FinRepaymentAmount, CustID, Defferments");
		sql.append(", PlanDeferCount, FinBranch, FinSourceID, AllowedDefRpyChange, AvailedDefRpyChange");
		sql.append(", AllowedDefFrqChange, AvailedDefFrqChange, RecalType, FinIsActive, FinAssetValue");
		sql.append(", disbAccountId, repayAccountId, LastRepayDate, LastRepayPftDate, LastRepayRvwDate");
		sql.append(", LastRepayCpzDate, AllowGrcRepay, GrcSchdMthd, GrcMargin, RepayMargin, FinCommitmentRef");
		sql.append(", FinLimitRef, DepreciationFrq, FinCurrAssetValue, NextDepDate, LastDepDate");
		sql.append(", FinCustPftAccount, ClosingStatus, FinApprovedDate, DedupFound, SkipDedup, Blacklisted");
		sql.append(", GrcProfitDaysBasis, StepFinance, StepPolicy, AlwManualSteps, NoOfSteps, StepType");
		sql.append(", AnualizedPercRate, EffectiveRateOfReturn, FinRepayPftOnFrq, LinkedFinRef, GrcMinRate");
		sql.append(", GrcMaxRate, GrcMaxAmount, RpyMinRate, RpyMaxRate, ManualSchedule, TakeOverFinance");
		sql.append(", feeAccountId, MinDownPayPerc, TDSApplicable, InsuranceAmt");
		sql.append(", AlwBPI, BpiTreatment, PlanEMIHAlw, PlanEMIHMethod, PlanEMIHMaxPerYear, PlanEMIHMax");
		sql.append(", PlanEMIHLockPeriod, PlanEMICpz, CalRoundingMode, RoundingTarget, AlwMultiDisb");
		sql.append(", FinRepayMethod, FeeChargeAmt, BpiAmount, DeductFeeDisb, RvwRateApplFor, SchCalOnRvw");
		sql.append(", PastduePftCalMthd, DroppingMethod, RateChgAnyDay, PastduePftMargin, FinCategory");
		sql.append(", ProductCategory, AdvanceEMI, BpiPftDaysBasis, FixedTenorRate, FixedRateTenor");
		sql.append(", BusinessVertical, GrcAdvType, GrcAdvTerms, AdvType, AdvTerms, AdvStage, AllowDrawingPower");
		sql.append(", AllowRevolving, appliedLoanAmt, FinIsRateRvwAtGrcEnd, Version, LastMntBy, LastMntOn");
		sql.append(", RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");

		sql.append(", MigratedFinance, ScheduleMaintained, ScheduleRegenerated");
		sql.append(", CustDSR, LimitValid, OverrideLimit, FinPurpose, FinStatus, FinStsReason, InitiateUser");
		sql.append(", BankName, Iban, AccountType, DdaReferenceNo, DeviationApproval, FinPreApprovedRef");
		sql.append(", MandateID, JointAccount, JointCustId, DownPayAccount, SecurityDeposit, RcdMaintainSts");
		sql.append(", FinCancelAc, NextUserId, Priority");
		sql.append(", InitiateDate, AccountsOfficer, ApplicationNo, DsaCode, DroplineFrq, FirstDroplineDate");
		sql.append(", PftServicingODLimit, ReferralId, EmployeeName, DmaCode, SalesDepartment, QuickDisb");
		sql.append(", WifReference, UnPlanEMIHLockPeriod, UnPlanEMICpz, ReAgeCpz, MaxUnplannedEmi");
		sql.append(", MaxReAgeHolidays, AvailedUnPlanEmi, AvailedReAgeH, ReAgeBucket, DueBucket, EligibilityMethod");
		sql.append(", SamplingRequired, LegalRequired, Connector, ProcessAttributes, PromotionCode");
		sql.append(", TdsPercentage, TdsStartDate, TdsEndDate, TdsLimitAmt, VanReq, VanCode, SanBsdSchdle");
		sql.append(", PromotionSeqId, SvAmount, CbAmount");

		return sql.toString();
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
				builder.append("JdbcUtil.setLong(");
				builder.append(varibaleName);
				builder.append(".get").append(getFieldName).append("()));");
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
