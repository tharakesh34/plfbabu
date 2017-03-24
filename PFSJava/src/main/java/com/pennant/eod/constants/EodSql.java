package com.pennant.eod.constants;

public class EodSql {

	private EodSql() {
		super();
	}

	public static final String customeFinance = " SELECT F.FinReference, F.FinBranch Branch, F.FinType ,F.CustID CustomerID ,F.LinkedFinRef,  S.SchDate RpyDate,"
			+ "  S.PrincipalSchd, S.SchdPriPaid, (S.ProfitSchd - S.SchdPftPaid) As SchdPftBal,  S.ProfitSchd, S.SchdPftpaid, (S.PrincipalSchd - S.SchdPriPaid) As SchdPriBal,"
			+ " S.SuplRent SchdSuplRent, S.SuplRentPaid SchdSuplRentPaid, (S.SuplRent -  S.SuplRentPaid) SchdSuplRentBal,"
			+ " S.IncrCost SchdIncrCost, S.IncrCostPaid SchdIncrCostPaid, (S.IncrCost - S.IncrCostPaid) SchdIncrCostBal,"
			+ " S.FeeSchd SchdFee , S.SchdFeePaid , (S.FeeSchd - S.SchdFeePaid) SchdFeeBal, "
			+ " S.InsSchd SchdIns, S.SchdInsPaid SchdInsPaid, (S.InsSchd - S.SchdInsPaid) SchdInsBal,"
			+ " S.AdvCalRate, S.AdvProfit, S.CalculatedRate "
			+ " FROM FinanceMain F , FinScheduleDetails S WHERE F.FinReference = S.FinReference  AND S.SchDate <= ? "
			+ " AND  (S.RepayOnSchDate = 1 OR (S.PftOnSchDate = 1 AND RepayAmount > 0))  AND F.FinIsActive = 1 "
			+ " AND (S.PrincipalSchd <> S.SchdPriPaid OR S.ProfitSchd <> S.SchdPftPaid "
			+ "   OR S.SuplRent <> S.SuplRentPaid OR  S.IncrCost <> S.IncrCostPaid "
			+ " OR S.FeeSchd <> S.SchdFeePaid OR S.InsSchd <>  S.SchdInsPaid ) "
			+ " AND CustID=?  order by F.LinkedFinRef asc";

	public static final String customerRepayQueue = "	SELECT RQ.FinReference, RQ.FinType, RQ.RpyDate, RQ.FinPriority, RQ.Branch,RQ.LinkedFinRef,"
			+ "RQ.CustomerID, RQ.FinRpyFor, RQ.SchdPft, RQ.SchdPri, RQ.SchdPftPaid, RQ.SchdPriPaid, RQ.SchdPftBal, RQ.SchdPriBal, RQ.SchdIsPftPaid, RQ.SchdIsPriPaid,"
			+ "(RQ.SchdPftBal+ RQ.SchdPriBal)  RepayQueueBal, PD.AcrTillLBD, PD.TdPftAmortizedSusp, PD.AmzTillLBD, "
			+ "RQ.SchdFee, RQ.SchdFeePaid, RQ.SchdFeeBal, RQ.SchdIns, RQ.SchdInsPaid, RQ.SchdInsBal, "
			+ "RQ.SchdSuplRent, RQ.SchdSuplRentPaid, RQ.SchdSuplRentBal, "
			+ "RQ.SchdIncrCost, RQ.SchdIncrCostPaid, RQ.SchdIncrCostBal,RQ.AdvProfit,RQ.SchdRate,RQ.Rebate, "
			+ "FM.ProfitDaysBasis, RQ.PenaltyPayNow, RQ.LatePayPftPayNow "
			+ " FROM FinRpyQueue RQ  INNER JOIN FinPftDetails PD ON PD.FinReference = RQ.FinReference "
			+ " INNER JOIN FinanceMain FM ON FM.FinReference = RQ.FinReference "
			+ " WHERE RQ.CustomerID=? "
			+ " ORDER BY RQ.RpyDate, RQ.FinPriority, RQ.FinReference, RQ.FinRpyFor , RQ.LinkedFinRef ASC ";

	public static final String accrual = "SELECT F.FinReference, P.AcrTillLBD, P.TdPftAmortizedSusp,P.AmzTillLBD, P.FirstODDate, P.LastODDate, P.CRBFirstODDate, P.CRBLastODDate FROM "
			+ "FinanceMain F  INNER JOIN FinPftDetails P ON F.FinReference = P.FinReference WHERE P.FinIsActive = 1  AND F.FinStartDate <=? And F.CustID=? ";

	public static final String rateReview = " SELECT fm.FinReference  FinReference FROM FinanceMain  fm  WHERE fm.FinIsActive = 1  AND AllowGrcPftRvw = 1 AND "
			+ "LastRepayRvwDate < GrcPeriodEndDate AND NextGrcPftRvwDate = ? AND GraceBaseRate IS NOT NULL AND GraceBaseRate <> '' and fm.CustID=? UNION"
			+ " SELECT fm.FinReference  FinReference FROM FinanceMain fm WHERE fm.FinIsActive = 1  AND AllowRepayRvw = 1 AND "
			+ "LastRepayRvwDate < MaturityDate AND NextRepayRvwDate = ? AND RepayBaseRate IS NOT NULL AND RepayBaseRate <> '' and fm.CustID=?";

	public static final String snapshotQuery = " INSERT INTO FinPftDetails_SnapShot "
			+ " SELECT ?, FinReference, CustId, FinBranch, FinType, LastMdfDate, TotalPftSchd, TotalPftCpz"
			+ " TotalPftPaid, TotalPftBal, TotalPftPaidInAdv, TotalPriPaid, TotalPriBal, TdSchdPft, TdPftCpz, TdSchdPftPaid,  "
			+ " TdSchdPftBal, TdPftAccrued, TdPftAccrueSusp, TdPftAmortized, TdPftAmortizedSusp, TdSchdPri, TdSchdPriPaid,  "
			+ " TdSchdPriBal, AcrTillNBD, AcrTillLBD, AcrTodayToNBD, AmzTillNBD, AmzTillLBD, AmzTodayToNBD, RepayFrq,  "
			+ " CustCIF, FinCcy, FinPurpose, FinContractDate, FinApprovedDate, FinStartDate, MaturityDate, FullPaidDate,  "
			+ " FinAmount, DownPayment, CurReducingRate, curFlatRate, TotalpriSchd, EarlyPaidAmt, ODPrincipal, ODProfit,  "
			+ " PenaltyPaid, PenaltyDue, PenaltyWaived, NSchdDate, NSchdPri, NSchdPft, NSchdPriDue, NSchdPftDue, AccruePft,  "
			+ " EarnedPft, Unearned, PftInSusp, SuspPft, PftAccrueTsfd, FinStatus, FinStsReason, FinWorstStatus,  "
			+ " InsPaidAmt, AdminPaidAmt, InsCal, NOInst, NOPaidInst, NOODInst, FinAccount, FinAcType,  "
			+ " DisbAccountId, DisbActCcy, RepayAccountId, FinCustPftAccount, IncomeAccount, UEIncomeSuspAccount,  "
			+ " FinCommitmentRef, FinIsActive, NORepayments, FirstRepayDate, FirstRepayAmt, LastRepayAmt, ODDays,  "
			+ " FirstODDate, LastODDate, ClosingStatus, FinCategory, LastRpySchDate, NextRpySchDate, LastRpySchPri,  "
			+ " LastRpySchPft, LatestRpyDate, LatestRpyPri, LatestRpyPft, LatestWriteOffDate, TotalWriteoff, PrvPftAccrueTsfd,  "
			+ " SuspPftAccrueTsfd, AccumulatedDepPri, DepreciatePri, AcrTsfdInSusp, CRBFirstODDate, CRBLastODDate,  "
			+ " CRBODPrincipal, CRBODProfit, CRBODDays, CRBODInst FROM FinPftDetails";

	public static final String collateralDemark = " SELECT T1.FinReference, T1.Reference, T1.Value, T1.Remarks FROM FinCollaterals T1 "
			+ " INNER JOIN FinanceMain T2 ON T1.FinReference = T2.FinReference "
			+ " INNER JOIN CollateralMarkLog T3 ON T1.FinReference = T3.FinReference"
			+ " INNER JOIN FinPftDetails T4 ON T1.FinReference = T4.FinReference"
			+ " WHERE T2.FinIsActive = ? AND T2.ClosingStatus = ? AND T3.Status = ? "
			+ " AND T3.Status <> ? AND T4.FullPaidDate = ? AND T2.CustID = ?";

	public static final String duedatepostings = " SELECT fm.FinReference, fm.FinType,fm.FinBranch, fsd.SchDate, SchdPftPaid, SchdPriPaid FROM "
			+ " (SELECT FinReference, FinSchdDate FROM FinRepayDetails WHERE FinPostDate < FinSchdDate and FinSchdDate=? "
			+ " GROUP BY FinReference, FinSchdDate) t  INNER JOIN FinScheduleDetails fsd on t.FinReference=fsd.FinReference and t.FinSchdDate=fsd.SchDate"
			+ " INNER JOIN FinanceMain fm on t.FinReference=fm.FinReference WHERE fm.CustID= ? ";

	public static final String insurancePostings = " SELECT fm.FinReference, fm.FinType, fm.FinBranch, fsd.SchDate, fsd.InsSchd "
			+ " FROM FinScheduleDetails fsd INNER JOIN FinanceMain fm on fm.FinReference=fsd.FinReference "
			+ " WHERE fsd.SchDate >= ? and fsd.SchDate<= ? AND (fsd.InsSchd >0 )";

	public static final String financeDocuments = " SELECT DD.Doctype, DD.DocCategory, DD.DocName, DM.DocImage FROM DocumentDetails DD INNER JOIN "
			+ " DocumentManager DM ON DD.DocRefId = DM.Id WHERE ReferenceId = ? ";

	public static final String customerDocuments = " SELECT CustDocCategory, CustDocType, CustDocName, CustDocImage FROM CustomerDocuments WHERE CustID = ? ";

	public static final String approvedFinances = " SELECT FinReference, custid FROM FinanceMain WHERE FinApprovedDate = ?  ";
	
	public static void main(String[] args) {
		System.out.println(customerRepayQueue);
	}
	
	
}
