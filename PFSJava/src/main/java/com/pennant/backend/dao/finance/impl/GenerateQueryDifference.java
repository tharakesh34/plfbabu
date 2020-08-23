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
		//	System.out.println("Qeury Size : "+sqlQueryOne.length);
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

		StringBuilder sql = new StringBuilder("");
		sql.append("  FinReference, NumberOfTerms, GrcPeriodEndDate, AllowGrcPeriod, GraceBaseRate");
		sql.append(", GraceSpecialRate, GrcPftRate, GrcPftFrq, NextGrcPftDate, AllowGrcPftRvw, GrcPftRvwFrq");
		sql.append(", NextGrcPftRvwDate, AllowGrcCpz, GrcCpzFrq, NextGrcCpzDate, RepayBaseRate, RepaySpecialRate");
		sql.append(", RepayProfitRate, RepayFrq, NextRepayDate, RepayPftFrq, NextRepayPftDate, AllowRepayRvw");
		sql.append(", RepayRvwFrq, NextRepayRvwDate, AllowRepayCpz, RepayCpzFrq, NextRepayCpzDate");
		sql.append(", MaturityDate, CpzAtGraceEnd, DownPayment, GraceFlatAmount, ReqRepayAmount, TotalProfit");
		sql.append(", TotalCpz, TotalGrossPft, TotalGrossGrcPft, TotalGracePft, TotalGraceCpz, GrcRateBasis");
		sql.append(", RepayRateBasis, FinType, FinRemarks, FinCcy, ScheduleMethod, ProfitDaysBasis");
		sql.append(", ReqMaturity, CalTerms, CalMaturity, FirstRepay, LastRepay, FinStartDate, FinAmount");
		sql.append(", FinRepaymentAmount, CustID, Defferments, PlanDeferCount, FinBranch, FinSourceID");
		sql.append(", AllowedDefRpyChange, AvailedDefRpyChange, AllowedDefFrqChange, AvailedDefFrqChange");
		sql.append(", RecalType, FinAssetValue, DisbAccountId, RepayAccountId, FinIsActive, Version");
		sql.append(", LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId");
		sql.append(", RecordType, WorkflowId, MinDownPayPerc, LastRepayDate, LastRepayPftDate, LastRepayRvwDate");
		sql.append(", LastRepayCpzDate, AllowGrcRepay, GrcSchdMthd, GrcMargin, RepayMargin, FinCurrAssetValue");
		sql.append(", FinCommitmentRef, DepreciationFrq, FinContractDate, NextDepDate, LastDepDate");
		sql.append(", FinAccount, FinCustPftAccount, TotalRepayAmt, FinApprovedDate, FeeChargeAmt");
		sql.append(", FinRepayPftOnFrq, AnualizedPercRate, EffectiveRateOfReturn, DownPayBank, DownPaySupl");
		sql.append(", GraceTerms, GrcProfitDaysBasis, StepFinance, StepType, StepPolicy, AlwManualSteps");
		sql.append(", NoOfSteps, LinkedFinRef, GrcMinRate, GrcMaxRate, GrcMaxAmount, RpyMinRate, RpyMaxRate");
		sql.append(", ManualSchedule, TakeOverFinance, GrcAdvBaseRate, GrcAdvMargin, GrcAdvPftRate");
		sql.append(", RpyAdvBaseRate, RpyAdvMargin, RpyAdvPftRate, SupplementRent, IncreasedCost, FeeAccountId");
		sql.append(
				", TDSApplicable, InsuranceAmt, DeductInsDisb, AlwBPI, BpiTreatment, PlanEMIHAlw, PlanEMIHAlwInGrace");
		sql.append(", PlanEMIHMethod, PlanEMIHMaxPerYear, PlanEMIHMax, PlanEMIHLockPeriod, PlanEMICpz");
		sql.append(", CalRoundingMode, RoundingTarget, AlwMultiDisb, BpiAmount, DeductFeeDisb, RvwRateApplFor");
		sql.append(", SchCalOnRvw, PastduePftCalMthd, DroppingMethod, RateChgAnyDay, PastduePftMargin");
		sql.append(", FinCategory, ProductCategory, AdvanceEMI, BpiPftDaysBasis, FixedTenorRate, FixedRateTenor");
		sql.append(", GrcAdvType, GrcAdvTerms, AdvType, AdvTerms, AdvStage, AllowDrawingPower, AllowRevolving");
		sql.append(", SanBsdSchdle, PromotionSeqId, SvAmount, CbAmount, AppliedLoanAmt");
		sql.append(", FinIsRateRvwAtGrcEnd, ClosingStatus");

	//	if (!wif) {
			sql.append(", DmaCode, TdsPercentage, FinStsReason, Connector, samplingRequired, LimitApproved");
			sql.append(", NextUserId, VanCode, FinLimitRef, ShariaStatus, RolloverFrq, DdaReferenceNo");
			sql.append(", legalRequired, CreditInsAmt, Blacklisted, FinRepayMethod, FirstDroplineDate");
			sql.append(", CustDSR, BankName, DownPayAccount, AccountsOfficer, QuickDisb, UnPlanEMICpz");
			sql.append(", ReAgeCpz, AvailedReAgeH, iban, SalesDepartment, DroplineFrq, NextRolloverDate");
			sql.append(", SecurityDeposit, PromotionCode, TdsLimitAmt, MigratedFinance, MaxReAgeHolidays");
			sql.append(", WifReference, UnPlanEMIHLockPeriod, TdsEndDate, Priority, Discrepancy, DeviationApproval");
			sql.append(", ScheduleMaintained, FinPurpose, ScheduleRegenerated, SecurityCollateral, RcdMaintainSts");
			sql.append(", MaxUnplannedEmi, DsaCode, ReferralId, MMAId, InitiateDate, ProcessAttributes");
			sql.append(", VanReq, InvestmentRef, FinPreApprovedRef, EmployeeName, OverrideLimit, TdsStartDate");
			sql.append(", MandateID, LimitValid, FinCancelAc, ApplicationNo, EligibilityMethod, PftServicingODLimit");
			sql.append(", BusinessVertical, ReAgeBucket, JointCustId, InitiateUser, AccountType, Approved");
			sql.append(", JointAccount, FinStatus, AvailedUnPlanEmi");

			// HL
			sql.append(", ReqLoanAmt, ReqLoanTenor, FinOcrRequired, OfferProduct, OfferAmount, CustSegmentation");
			sql.append(", BaseProduct, ProcessType, BureauTimeSeries, CampaignName, ExistingLanRefNo, OfferId");
			sql.append(", LeadSource, PoSource, Rsa, Verification, SourcingBranch, SourChannelCategory, AsmName");
			sql.append(", AlwGrcAdj, EndGrcPeriodAftrFullDisb, AutoIncGrcEndDate,instBasedSchd, parentRef");
		//}

	//	if (StringUtils.trimToEmpty(type).contains("View")) {
			sql.append(", LovDescFinTypeName, LovDescFinMaxAmt, LovDescFinMinAmount, LovDescFinBranchName");

		//	if (!wif) {
				sql.append(", LovDescFinScheduleOn, LovDescAccruedTillLBD, CustStsDescription");
				sql.append(", LovDescSourceCity, LovDescFinDivision, FinBranchProvinceCode, LovDescStepPolicyName");
				sql.append(", LovDescAccountsOfficer, DsaCodeDesc, ReferralIdDesc, EmployeeNameDesc, DmaCodeDesc");
				sql.append(", SalesDepartmentDesc, LovDescEntityCode, LovEligibilityMethod");
				sql.append(", LovDescEligibilityMethod, LovDescFinPurposeName, ConnectorCode");
				sql.append(", ConnectorDesc, BusinessVerticalCode, BusinessVerticalDesc, LovDescSourcingBranch");
		//	}
	//	}

		return sql.toString();
	}

	private static String getSelectSecondQuery() {

		StringBuilder sql = new StringBuilder("");
		sql.append("  FinReference, NumberOfTerms, GrcPeriodEndDate, AllowGrcPeriod, GraceBaseRate");
		sql.append(", GraceSpecialRate, GrcPftRate, GrcPftFrq, NextGrcPftDate, AllowGrcPftRvw, GrcPftRvwFrq");
		sql.append(", NextGrcPftRvwDate, AllowGrcCpz, GrcCpzFrq, NextGrcCpzDate, RepayBaseRate, RepaySpecialRate");
		sql.append(", RepayProfitRate, RepayFrq, NextRepayDate, RepayPftFrq, NextRepayPftDate, AllowRepayRvw");
		sql.append(", RepayRvwFrq, NextRepayRvwDate, AllowRepayCpz, RepayCpzFrq, NextRepayCpzDate");
		sql.append(", MaturityDate, CpzAtGraceEnd, DownPayment, GraceFlatAmount, ReqRepayAmount, TotalProfit");
		sql.append(", TotalCpz, TotalGrossPft, TotalGrossGrcPft, TotalGracePft, TotalGraceCpz, GrcRateBasis");
		sql.append(", RepayRateBasis, FinType, FinRemarks, FinCcy, ScheduleMethod, ProfitDaysBasis");
		sql.append(", ReqMaturity, CalTerms, CalMaturity, FirstRepay, LastRepay, FinStartDate, FinAmount");
		sql.append(", FinRepaymentAmount, CustID, Defferments, PlanDeferCount, FinBranch, FinSourceID");
		sql.append(", AllowedDefRpyChange, AvailedDefRpyChange, AllowedDefFrqChange, AvailedDefFrqChange");
		sql.append(", RecalType, FinAssetValue, DisbAccountId, RepayAccountId, FinIsActive, Version");
		sql.append(", LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId");
		sql.append(", RecordType, WorkflowId, MinDownPayPerc, LastRepayDate, LastRepayPftDate, LastRepayRvwDate");
		sql.append(", LastRepayCpzDate, AllowGrcRepay, GrcSchdMthd, GrcMargin, RepayMargin, FinCurrAssetValue");
		sql.append(", FinCommitmentRef, DepreciationFrq, FinContractDate, NextDepDate, LastDepDate");
		sql.append(", FinAccount, FinCustPftAccount, TotalRepayAmt, FinApprovedDate, FeeChargeAmt");
		sql.append(", FinRepayPftOnFrq, AnualizedPercRate, EffectiveRateOfReturn, DownPayBank, DownPaySupl");
		sql.append(", GraceTerms, GrcProfitDaysBasis, StepFinance, StepType, StepPolicy, AlwManualSteps");
		sql.append(", NoOfSteps, LinkedFinRef, GrcMinRate, GrcMaxRate, GrcMaxAmount, RpyMinRate, RpyMaxRate");
		sql.append(", ManualSchedule, TakeOverFinance, GrcAdvBaseRate, GrcAdvMargin, GrcAdvPftRate");
		sql.append(", RpyAdvBaseRate, RpyAdvMargin, RpyAdvPftRate, SupplementRent, IncreasedCost, FeeAccountId");
		sql.append(
				", TDSApplicable, InsuranceAmt, DeductInsDisb, AlwBPI, BpiTreatment, PlanEMIHAlw,planEMIHAlwInGrace");
		sql.append(", PlanEMIHMethod, PlanEMIHMaxPerYear, PlanEMIHMax, PlanEMIHLockPeriod, PlanEMICpz");
		sql.append(", CalRoundingMode, RoundingTarget, AlwMultiDisb, BpiAmount, DeductFeeDisb, RvwRateApplFor");
		sql.append(", SchCalOnRvw, PastduePftCalMthd, DroppingMethod, RateChgAnyDay, PastduePftMargin");
		sql.append(", FinCategory, ProductCategory, AdvanceEMI, BpiPftDaysBasis, FixedTenorRate, FixedRateTenor");
		sql.append(", GrcAdvType, GrcAdvTerms, AdvType, AdvTerms, AdvStage, AllowDrawingPower, AllowRevolving");
		sql.append(", SanBsdSchdle, PromotionSeqId, SvAmount, CbAmount, AppliedLoanAmt");
		sql.append(", FinIsRateRvwAtGrcEnd, ClosingStatus");

	//	if (!wif) {
			sql.append(", DmaCode, TdsPercentage, FinStsReason, Connector, samplingRequired, LimitApproved");
			sql.append(", NextUserId, VanCode, FinLimitRef, ShariaStatus, RolloverFrq, DdaReferenceNo");
			sql.append(", legalRequired, CreditInsAmt, Blacklisted, FinRepayMethod, FirstDroplineDate");
			sql.append(", CustDSR, BankName, DownPayAccount, AccountsOfficer, QuickDisb, UnPlanEMICpz");
			sql.append(", ReAgeCpz, AvailedReAgeH, iban, SalesDepartment, DroplineFrq, NextRolloverDate");
			sql.append(", SecurityDeposit, PromotionCode, TdsLimitAmt, MigratedFinance, MaxReAgeHolidays");
			sql.append(", WifReference, UnPlanEMIHLockPeriod, TdsEndDate, Priority, Discrepancy, DeviationApproval");
			sql.append(", ScheduleMaintained, FinPurpose, ScheduleRegenerated, SecurityCollateral, RcdMaintainSts");
			sql.append(", MaxUnplannedEmi, DsaCode, ReferralId, MMAId, InitiateDate, ProcessAttributes");
			sql.append(", VanReq, InvestmentRef, FinPreApprovedRef, EmployeeName, OverrideLimit, TdsStartDate");
			sql.append(", MandateID, LimitValid, FinCancelAc, ApplicationNo, EligibilityMethod, PftServicingODLimit");
			sql.append(", BusinessVertical, ReAgeBucket, JointCustId, InitiateUser, AccountType, Approved");
			sql.append(", JointAccount, FinStatus, AvailedUnPlanEmi");
	//	}

		//if (StringUtils.trimToEmpty(type).contains("View")) {
			sql.append(", LovDescFinTypeName, LovDescFinMaxAmt, LovDescFinMinAmount, LovDescFinBranchName");

		//	if (!wif) {
				sql.append(", LovDescFinScheduleOn, LovDescAccruedTillLBD, CustStsDescription");
				sql.append(", LovDescSourceCity, LovDescFinDivision, FinBranchProvinceCode, LovDescStepPolicyName");
				sql.append(", LovDescAccountsOfficer, DsaCodeDesc, ReferralIdDesc, EmployeeNameDesc, DmaCodeDesc");
				sql.append(", SalesDepartmentDesc, LovDescEntityCode, LovEligibilityMethod");
				sql.append(", LovDescEligibilityMethod, LovDescFinPurposeName, ConnectorCode");
				sql.append(", ConnectorDesc, BusinessVerticalCode, BusinessVerticalDesc");
		

		return sql.toString();
	}
}
