package com.pennant.backend.dao.finance.impl;

import java.util.HashMap;
import java.util.Map;

public class GenerateQueryDifference {

	public static void main(String[] args) {
		StringBuilder QueryDiffOne = new StringBuilder();
		StringBuilder QueryDiffTwo = new StringBuilder();
		String firstSqlQuery = getSelectFirstQuery();
		String secondSqlQuery = getSelectSecondQuery();
		String[] sqlQueryOne = firstSqlQuery.split(",");
		String[] sqlQueryTwo = secondSqlQuery.split(",");
		Map<String, Integer> firstQueryNotMatches = getQueryNotMatches(sqlQueryOne, sqlQueryTwo);
		for (String key : firstQueryNotMatches.keySet()) {
			if (firstQueryNotMatches.get(key) == 0) {
				QueryDiffOne.append(key);
				QueryDiffOne.append(" , ");
			}
		}

		System.out.println("Query 1 Exceed Columns : " + QueryDiffOne);
		Map<String, Integer> secondQueryNotMatches = getQueryNotMatches(sqlQueryTwo, sqlQueryOne);
		for (String key : secondQueryNotMatches.keySet()) {
			if (secondQueryNotMatches.get(key) == 0) {
				QueryDiffTwo.append(key);
				QueryDiffTwo.append(" , ");
			}
		}
		System.out.println("Query 2 Exceed Columns : " + QueryDiffTwo);
		// System.out.println("Qeury Size : "+sqlQueryOne.length);
	}

	private static Map<String, Integer> getQueryNotMatches(String[] sqlQueryOne, String[] sqlQueryTwo) {
		Map<String, Integer> defaultQueryColumn = new HashMap<String, Integer>();
		int k = 0;
		while (k < sqlQueryOne.length) {

			defaultQueryColumn.put(sqlQueryOne[k], 0);
			k++;
		}
		for (int i = 0; i < sqlQueryTwo.length; i++) {
			for (int j = 0; j < sqlQueryOne.length; j++) {
				if (sqlQueryTwo[i].trim().equalsIgnoreCase(sqlQueryOne[j].trim())) {
					defaultQueryColumn.put(sqlQueryOne[j], 1);

				}
			}
		}
		return defaultQueryColumn;
	}

	private static String getSelectFirstQuery() {
		StringBuilder sql = new StringBuilder();
		sql.append(" StepFinance,  AccountsOfficer, PlanEMIHMax");
		sql.append(", RepayCpzFrq, WorkflowId, UnPlanEMIHLockPeriod");
		sql.append(", NextRoleCode, GrcPftRate, AllowGrcRepay, GrcCpzFrq");
		sql.append(", LastRepayPftDate, NextGrcPftDate, RecalType, AllowedDefRpyChange");
		sql.append(", MinDownPayPerc, CustDSR, RvwRateApplFor, RateChgAnyDay");
		sql.append(", RcdMaintainSts, MaxReAgeHolidays");
		sql.append(", CustID, CalTerms, FinAmount, UnPlanEMICpz");
		sql.append(", PlanDeferCount, PlanEMIHAlw, PlanEMIHAlwInGrace, CalMaturity");
		sql.append(", DsaCode, Defferments, BpiAmount, ApplicationNo, LegalRequired");
		sql.append(", RoleCode, FinPurpose, FinCurrAssetValue, FinStsReason, NextTaskId");
		sql.append(", GrcRateBasis, LastRepayRvwDate, RecordStatus, TotalGraceCpz");
		sql.append(", RpyMaxRate, RpyMinRate, AlwMultiDisb, NextRepayCpzDate, FinRepaymentAmount");
		sql.append(", DmaCode, ReqRepayAmount, MaxUnplannedEmi");
		sql.append(", ScheduleRegenerated, FixedRateTenor, GrcProfitDaysBasis");
		sql.append(", DownPayBank, DroplineFrq, FinStatus, NumberOfTerms");
		sql.append(", OverrideLimit, Version, ScheduleMethod, FirstRepay");
		sql.append(", RepayProfitRate, FinIsActive, GrcPeriodEndDate");
		sql.append(", RepayRvwFrq, FinLimitRef, ProductCategory");
		sql.append(", FinRepayPftOnFrq, EmployeeName, TDSApplicable");
		sql.append(", Connector, AvailedDefRpyChange, ScheduleMaintained, LastRepayDate");
		sql.append(", LastRepay, ReqMaturity, PastduePftMargin, RepayFrq, TotalCpz");
		sql.append(", TotalGrossPft, FinRepayMethod, AllowRepayRvw");
		sql.append(", TotalProfit, AvailedUnPlanEmi, FinCategory, GrcPftRvwFrq, FinCommitmentRef");
		sql.append(", TotalGrossGrcPft, DownPaySupl, AllowGrcPeriod, SalesDepartment");
		sql.append(", PlanEMICpz, GrcMaxRate, FirstDroplineDate, MandateID");
		sql.append(", AvailedDefFrqChange, CpzAtGraceEnd, StepPolicy, AllowGrcCpz");
		sql.append(", NextRepayPftDate, AllowGrcPftRvw, NextGrcPftRvwDate, FinContractDate");
		sql.append(", InvestmentRef, PromotionCode, FinPreApprovedRef, LimitValid");
		sql.append(", NoOfSteps, RoundingTarget");
		sql.append(", LastRepayCpzDate, NextGrcCpzDate, PlanEMIHMethod, AdvanceEMI");
		sql.append(", AnualizedPercRate, FinCcy, TaskId, BpiPftDaysBasis, StepType");
		sql.append(", WifReference, InitiateDate, FinApprovedDate");
		sql.append(", BpiTreatment, NextRepayDate, DroppingMethod, RepayBaseRate");
		sql.append(", SchCalOnRvw, AvailedReAgeH, FeeChargeAmt, MigratedFinance");
		sql.append(", FixedTenorRate, LastMntOn, GrcMaxAmount, PlanEMIHLockPeriod");
		sql.append(", InitiateUser, RepayPftFrq, ProfitDaysBasis, JointAccount, FinBranch");
		sql.append(", GrcPftFrq, FinSourceID, EligibilityMethod, CalRoundingMode");
		sql.append(", DeductFeeDisb, FinType, EffectiveRateOfReturn, GraceTerms");
		sql.append(", DownPayment, FinAssetValue, FinRemarks");
		sql.append(", Priority, AllowedDefFrqChange, DeviationApproval, ManualSchedule");
		sql.append(", GraceBaseRate, ReferralId, SamplingRequired, AlwBPI, AlwManualSteps");
		sql.append(", GrcMargin, RepayMargin, RepayRateBasis, LinkedFinRef, TotalRepayAmt");
		sql.append(", JointCustId, TotalGracePft, RepaySpecialRate");
		sql.append(", RecordType, ReAgeCpz, GraceSpecialRate, AllowRepayCpz");
		sql.append(", NextRepayRvwDate, PlanEMIHMaxPerYear, GrcSchdMthd, LastMntBy");
		sql.append(", NextUserId, FinStartDate, MaturityDate, ClosingStatus");
		sql.append(", PftServicingODLimit, QuickDisb, GrcMinRate, PastduePftCalMthd");
		sql.append(", ReAgeBucket, TdsType, WriteoffLoan, ASCD");
		return sql.toString();
	}

	private static String getSelectSecondQuery() {
		StringBuilder sql = new StringBuilder();

		sql.append(" StepFinance,  AccountsOfficer, PlanEMIHMax");
		sql.append(", RepayCpzFrq, WorkflowId, UnPlanEMIHLockPeriod");
		sql.append(", NextRoleCode, GrcPftRate, AllowGrcRepay, GrcCpzFrq");
		sql.append(", LastRepayPftDate, NextGrcPftDate, RecalType, AllowedDefRpyChange");
		sql.append(", MinDownPayPerc, CustDSR, RvwRateApplFor, RateChgAnyDay");
		sql.append(", RcdMaintainSts, MaxReAgeHolidays");
		sql.append(", CustID, CalTerms, FinAmount, UnPlanEMICpz");
		sql.append(", PlanDeferCount, PlanEMIHAlw, PlanEMIHAlwInGrace, CalMaturity");
		sql.append(", DsaCode, Defferments, BpiAmount, ApplicationNo, LegalRequired");
		sql.append(", RoleCode, FinPurpose, FinCurrAssetValue, FinStsReason, NextTaskId");
		sql.append(", GrcRateBasis, LastRepayRvwDate, RecordStatus, TotalGraceCpz");
		sql.append(", RpyMaxRate, RpyMinRate, AlwMultiDisb, NextRepayCpzDate, FinRepaymentAmount");
		sql.append(", DmaCode, ReqRepayAmount, MaxUnplannedEmi");
		sql.append(", ScheduleRegenerated, FixedRateTenor, GrcProfitDaysBasis");
		sql.append(", DownPayBank, DroplineFrq, FinStatus, NumberOfTerms");
		sql.append(", OverrideLimit, Version, ScheduleMethod, FirstRepay");
		sql.append(", RepayProfitRate, FinIsActive, GrcPeriodEndDate");
		sql.append(", RepayRvwFrq, FinLimitRef, ProductCategory");
		sql.append(", FinRepayPftOnFrq, EmployeeName, TDSApplicable");
		sql.append(", Connector, AvailedDefRpyChange, ScheduleMaintained, LastRepayDate");
		sql.append(", LastRepay, ReqMaturity, PastduePftMargin, RepayFrq, TotalCpz");
		sql.append(", TotalGrossPft, FinRepayMethod, AllowRepayRvw");
		sql.append(", TotalProfit, AvailedUnPlanEmi, FinCategory, GrcPftRvwFrq, FinCommitmentRef");
		sql.append(", TotalGrossGrcPft, DownPaySupl, AllowGrcPeriod, SalesDepartment");
		sql.append(", PlanEMICpz, GrcMaxRate, FirstDroplineDate, MandateID");
		sql.append(", AvailedDefFrqChange, CpzAtGraceEnd, StepPolicy, AllowGrcCpz");
		sql.append(", NextRepayPftDate, AllowGrcPftRvw, NextGrcPftRvwDate, FinContractDate");
		sql.append(", InvestmentRef, PromotionCode, FinPreApprovedRef, LimitValid");
		sql.append(", NoOfSteps, RoundingTarget");
		sql.append(", LastRepayCpzDate, NextGrcCpzDate, PlanEMIHMethod, AdvanceEMI");
		sql.append(", AnualizedPercRate, FinCcy, TaskId, BpiPftDaysBasis, StepType");
		sql.append(", WifReference, InitiateDate, FinApprovedDate");
		sql.append(", BpiTreatment, NextRepayDate, DroppingMethod, RepayBaseRate");
		sql.append(", SchCalOnRvw, AvailedReAgeH, FeeChargeAmt, MigratedFinance");
		sql.append(", FixedTenorRate, LastMntOn, GrcMaxAmount, PlanEMIHLockPeriod");
		sql.append(", InitiateUser, RepayPftFrq, ProfitDaysBasis, JointAccount, FinBranch");
		sql.append(", GrcPftFrq, FinSourceID, EligibilityMethod, CalRoundingMode");
		sql.append(", DeductFeeDisb, FinType, EffectiveRateOfReturn, GraceTerms");
		sql.append(", DownPayment, FinAssetValue, FinRemarks");
		sql.append(", Priority, AllowedDefFrqChange, DeviationApproval, ManualSchedule");
		sql.append(", GraceBaseRate, ReferralId, SamplingRequired, AlwBPI, AlwManualSteps");
		sql.append(", GrcMargin, RepayMargin, RepayRateBasis, LinkedFinRef, TotalRepayAmt");
		sql.append(", JointCustId, TotalGracePft, RepaySpecialRate");
		sql.append(", RecordType, ReAgeCpz, GraceSpecialRate, AllowRepayCpz");
		sql.append(", NextRepayRvwDate, PlanEMIHMaxPerYear, GrcSchdMthd, LastMntBy");
		sql.append(", NextUserId, FinStartDate, MaturityDate, ClosingStatus");
		sql.append(", PftServicingODLimit, QuickDisb, GrcMinRate, PastduePftCalMthd");
		sql.append(", ReAgeBucket, TdsType, WriteoffLoan, ASC");
		return sql.toString();
	}
}
