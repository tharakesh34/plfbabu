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
		sql.append(" FinReference, GraceTerms,  NumberOfTerms, GrcPeriodEndDate, AllowGrcPeriod,");
		sql.append(" GraceBaseRate, GraceSpecialRate, GrcPftRate, GrcPftFrq,NextGrcPftDate, AllowGrcPftRvw,");
		sql.append(" GrcPftRvwFrq, NextGrcPftRvwDate, AllowGrcCpz, GrcCpzFrq, NextGrcCpzDate,RepayBaseRate,");
		sql.append(" RepaySpecialRate, RepayProfitRate, RepayFrq, NextRepayDate, RepayPftFrq, NextRepayPftDate,");
		sql.append(" AllowRepayRvw,RepayRvwFrq, NextRepayRvwDate, AllowRepayCpz, RepayCpzFrq, NextRepayCpzDate,");
		sql.append(" MaturityDate, CpzAtGraceEnd,DownPayment, DownPayBank, DownPaySupl, ReqRepayAmount, TotalProfit,");
		sql.append(" TotalCpz,TotalGrossPft,TotalGracePft, TotalGraceCpz,TotalGrossGrcPft, TotalRepayAmt,");
		sql.append("  GrcRateBasis, RepayRateBasis,FinType,FinRemarks, FinCcy, ScheduleMethod,FinContractDate,");
		sql.append(" ProfitDaysBasis, ReqMaturity, CalTerms, CalMaturity, FirstRepay, LastRepay,");
		sql.append(" FinStartDate, FinAmount, FinRepaymentAmount, CustID, Defferments,PlanDeferCount,");
		sql.append(" FinBranch, FinSourceID, AllowedDefRpyChange, AvailedDefRpyChange, AllowedDefFrqChange,");
		sql.append(" AvailedDefFrqChange, RecalType, FinIsActive,FinAssetValue, disbAccountId, repayAccountId, ");
		sql.append(" LastRepayDate, LastRepayPftDate, LastRepayRvwDate, LastRepayCpzDate, AllowGrcRepay, GrcSchdMthd,");
		sql.append(" GrcMargin, RepayMargin, FinCommitmentRef, FinLimitRef, DepreciationFrq, FinCurrAssetValue,");
		sql.append(" NextDepDate, LastDepDate, FinAccount, FinCustPftAccount, ClosingStatus, FinApprovedDate, ");
		sql.append(" DedupFound,SkipDedup,Blacklisted,");
		sql.append(" GrcProfitDaysBasis, StepFinance , StepPolicy, AlwManualSteps, NoOfSteps, StepType, ");
		sql.append(" AnualizedPercRate , EffectiveRateOfReturn , FinRepayPftOnFrq, ");
		sql.append(" LinkedFinRef, ");
		sql.append(" GrcMinRate, GrcMaxRate ,GrcMaxAmount, RpyMinRate, RpyMaxRate,  ");
		sql.append(
				" ManualSchedule , TakeOverFinance, GrcAdvBaseRate ,GrcAdvMargin ,GrcAdvPftRate ,RpyAdvBaseRate ,RpyAdvMargin ,RpyAdvPftRate ,");
		sql.append(
				" SupplementRent, IncreasedCost , feeAccountId, MinDownPayPerc,TDSApplicable,InsuranceAmt, AlwBPI , BpiTreatment , PlanEMIHAlw ,");
		sql.append(
				" PlanEMIHMethod , PlanEMIHMaxPerYear , PlanEMIHMax , PlanEMIHLockPeriod , PlanEMICpz , CalRoundingMode ,RoundingTarget, AlwMultiDisb,FinRepayMethod, ");
		sql.append(
				" FeeChargeAmt, BpiAmount, DeductFeeDisb, RvwRateApplFor, SchCalOnRvw,PastduePftCalMthd,DroppingMethod,RateChgAnyDay,PastduePftMargin,");
		sql.append(" FinCategory, ProductCategory, AdvanceEMI, BpiPftDaysBasis, FixedTenorRate,FixedRateTenor,");
		sql.append(" BusinessVertical ");
		sql.append(
				", GrcAdvType, GrcAdvTerms, AdvType, AdvTerms, AdvStage, AllowDrawingPower, AllowRevolving, appliedLoanAmt, FinIsRateRvwAtGrcEnd ");

		//	if (!wif) {
		sql.append(", InvestmentRef, MigratedFinance, ScheduleMaintained, ScheduleRegenerated,CustDSR,");
		sql.append(
				" LimitValid, OverrideLimit,FinPurpose,FinStatus, FinStsReason, InitiateUser, BankName, Iban, AccountType, DdaReferenceNo, DeviationApproval,FinPreApprovedRef,MandateID,");
		sql.append(" JointAccount,JointCustId,DownPayAccount, SecurityDeposit, RcdMaintainSts,FinCancelAc, ");
		sql.append(
				" NextUserId, Priority,RolloverFrq, NextRolloverDate,ShariaStatus, InitiateDate,MMAId, AccountsOfficer , ApplicationNo,");
		sql.append(
				" DsaCode, DroplineFrq,FirstDroplineDate,PftServicingODLimit, ReferralId, EmployeeName, DmaCode, SalesDepartment, QuickDisb, WifReference,");
		sql.append(
				" UnPlanEMIHLockPeriod , UnPlanEMICpz, ReAgeCpz, MaxUnplannedEmi, MaxReAgeHolidays, AvailedUnPlanEmi, AvailedReAgeH, ReAgeBucket, DueBucket, EligibilityMethod,samplingRequired,legalRequired,connector,ProcessAttributes ");
		sql.append(", PromotionCode, TdsPercentage, TdsStartDate, TdsEndDate, TdsLimitAmt , VanReq, VanCode");
		sql.append(", SanBsdSchdle, PromotionSeqId, SvAmount, CbAmount, PlanEMIHAlwInGrace");

		// HL
		sql.append(", FinOcrRequired, ReqLoanAmt, ReqLoanTenor, OfferProduct, OfferAmount");
		sql.append(", CustSegmentation, BaseProduct, ProcessType, BureauTimeSeries");
		sql.append(", CampaignName, ExistingLanRefNo, LeadSource, PoSource , Rsa, Verification");
		sql.append(", SourcingBranch, SourChannelCategory, AsmName, OfferId");
		sql.append(", Pmay, AlwGrcAdj, EndGrcPeriodAftrFullDisb, AutoIncGrcEndDate");
		sql.append(", parentRef, loanSplitted, AlwLoanSplit, InstBasedSchd");

		//}
		sql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId,");
		sql.append(" NextTaskId, RecordType, WorkflowId");

		return sql.toString();
	}

	private static String getSelectSecondQuery() {

		StringBuilder sql = new StringBuilder("");
		sql.append("FinReference, GraceTerms, NumberOfTerms, GrcPeriodEndDate, AllowGrcPeriod, GraceBaseRate");
		sql.append(", GraceSpecialRate, GrcPftRate, GrcPftFrq, NextGrcPftDate, AllowGrcPftRvw, GrcPftRvwFrq");
		sql.append(", NextGrcPftRvwDate, AllowGrcCpz, GrcCpzFrq, NextGrcCpzDate, RepayBaseRate, RepaySpecialRate");
		sql.append(", RepayProfitRate, RepayFrq, NextRepayDate, RepayPftFrq, NextRepayPftDate, AllowRepayRvw");
		sql.append(", RepayRvwFrq, NextRepayRvwDate, AllowRepayCpz, RepayCpzFrq, NextRepayCpzDate, MaturityDate");
		sql.append(", CpzAtGraceEnd, DownPayment, DownPayBank, DownPaySupl, ReqRepayAmount, TotalProfit");
		sql.append(", TotalCpz, TotalGrossPft, TotalGracePft, TotalGraceCpz, TotalGrossGrcPft, TotalRepayAmt");
		sql.append(", GrcRateBasis, RepayRateBasis, FinType, FinRemarks, FinCcy, ScheduleMethod, FinContractDate");
		sql.append(", ProfitDaysBasis, ReqMaturity, CalTerms, CalMaturity, FirstRepay, LastRepay, FinStartDate");
		sql.append(", FinAmount, FinRepaymentAmount, CustID, Defferments, PlanDeferCount, FinBranch, FinSourceID");
		sql.append(", AllowedDefRpyChange, AvailedDefRpyChange, AllowedDefFrqChange, AvailedDefFrqChange");
		sql.append(", RecalType, FinIsActive, FinAssetValue, disbAccountId, repayAccountId, LastRepayDate");
		sql.append(", LastRepayPftDate, LastRepayRvwDate, LastRepayCpzDate, AllowGrcRepay, GrcSchdMthd");
		sql.append(", GrcMargin, RepayMargin, FinCommitmentRef, FinLimitRef, DepreciationFrq, FinCurrAssetValue");
		sql.append(", NextDepDate, LastDepDate, FinAccount, FinCustPftAccount, ClosingStatus, FinApprovedDate");
		sql.append(", DedupFound, SkipDedup, Blacklisted, GrcProfitDaysBasis, StepFinance, StepPolicy");
		sql.append(", AlwManualSteps, NoOfSteps, StepType, AnualizedPercRate, EffectiveRateOfReturn, FinRepayPftOnFrq");
		sql.append(", LinkedFinRef, GrcMinRate, GrcMaxRate, GrcMaxAmount, RpyMinRate, RpyMaxRate, ManualSchedule");
		sql.append(", TakeOverFinance, GrcAdvBaseRate, GrcAdvMargin, GrcAdvPftRate, RpyAdvBaseRate, RpyAdvMargin");
		sql.append(", RpyAdvPftRate, SupplementRent, IncreasedCost, feeAccountId, MinDownPayPerc, TDSApplicable");
		sql.append(", InsuranceAmt, AlwBPI, BpiTreatment, PlanEMIHAlw, PlanEMIHMethod, PlanEMIHMaxPerYear");
		sql.append(", PlanEMIHMax, PlanEMIHLockPeriod, PlanEMICpz, CalRoundingMode, RoundingTarget, AlwMultiDisb");
		sql.append(", FinRepayMethod, FeeChargeAmt, BpiAmount, DeductFeeDisb, RvwRateApplFor, SchCalOnRvw");
		sql.append(", PastduePftCalMthd, DroppingMethod, RateChgAnyDay, PastduePftMargin, FinCategory");
		sql.append(", ProductCategory, AdvanceEMI, BpiPftDaysBasis, FixedTenorRate, FixedRateTenor, BusinessVertical");
		sql.append(", GrcAdvType, GrcAdvTerms, AdvType, AdvTerms, AdvStage, AllowDrawingPower, AllowRevolving");
		sql.append(", appliedLoanAmt, FinIsRateRvwAtGrcEnd");
		//if (!wif) {
		sql.append(", InvestmentRef, MigratedFinance, ScheduleMaintained, ScheduleRegenerated");
		sql.append(", CustDSR, LimitValid, OverrideLimit, FinPurpose, FinStatus, FinStsReason");
		sql.append(", InitiateUser, BankName, Iban, AccountType, DdaReferenceNo, DeviationApproval");
		sql.append(", FinPreApprovedRef, MandateID, JointAccount, JointCustId, DownPayAccount, SecurityDeposit");
		sql.append(", RcdMaintainSts, FinCancelAc, NextUserId, Priority, RolloverFrq, NextRolloverDate");
		sql.append(", ShariaStatus, InitiateDate, MMAId, AccountsOfficer, ApplicationNo, DsaCode, DroplineFrq");
		sql.append(", FirstDroplineDate, PftServicingODLimit, ReferralId, EmployeeName, DmaCode, SalesDepartment");
		sql.append(", QuickDisb, WifReference, UnPlanEMIHLockPeriod, UnPlanEMICpz, ReAgeCpz, MaxUnplannedEmi");
		sql.append(", MaxReAgeHolidays, AvailedUnPlanEmi, AvailedReAgeH, ReAgeBucket, DueBucket");
		sql.append(", EligibilityMethod, samplingRequired, legalRequired, connector, ProcessAttributes");
		sql.append(", PromotionCode, TdsPercentage, TdsStartDate, TdsEndDate, TdsLimitAmt");
		sql.append(", VanReq, VanCode, SanBsdSchdle, PromotionSeqId, SvAmount, CbAmount");
		sql.append(", AlwGrcAdj, EndGrcPeriodAftrFullDisb, AutoIncGrcEndDate, PlanEMIHAlwInGrace");
		// HL
		sql.append(", FinOcrRequired, ReqLoanAmt, ReqLoanTenor, OfferProduct, OfferAmount");
		sql.append(", CustSegmentation, BaseProduct, ProcessType, BureauTimeSeries");
		sql.append(", CampaignName, ExistingLanRefNo, LeadSource, PoSource , Rsa, Verification");
		sql.append(", SourcingBranch, SourChannelCategory, AsmName, OfferId");
		sql.append(", Pmay, AlwGrcAdj, EndGrcPeriodAftrFullDisb, AutoIncGrcEndDate");
		sql.append(", parentRef, loanSplitted, AlwLoanSplit, InstBasedSchd");

		//}
		sql.append(", Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId");
		sql.append(", RecordType, WorkflowId");
		return sql.toString();
	}
}
