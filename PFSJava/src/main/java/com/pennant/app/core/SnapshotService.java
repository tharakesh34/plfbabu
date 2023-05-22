package com.pennant.app.core;

import java.util.Date;

import javax.sql.DataSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.pennanttech.pennapps.core.resource.Literal;

public class SnapshotService {
	private static Logger logger = LogManager.getLogger(SnapshotService.class);

	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	private static final String snapshotQuery_FinPftDetails = "INSERT INTO FinPftDetails_SnapShot  ("
			+ " AppDate, FINREFERENCE , CUSTID , FINBRANCH , FINTYPE , LASTMDFDATE , TOTALPFTSCHD , TOTALPFTCPZ , TOTALPFTPAID ,"
			+ " TOTALPFTBAL , TOTALPFTPAIDINADV , TOTALPRIPAID , TOTALPRIBAL , TDSCHDPFT , TDPFTCPZ , TDSCHDPFTPAID , TDSCHDPFTBAL ,"
			+ " PFTACCRUED , PFTACCRUESUSP , PFTAMZ , PFTAMZSUSP , TDSCHDPRI , TDSCHDPRIPAID , TDSCHDPRIBAL , ACRTILLLBD , AMZTILLLBD ,"
			+ " REPAYFRQ , CUSTCIF , FINCCY , FINPURPOSE , FINCONTRACTDATE , FINAPPROVEDDATE , FINSTARTDATE , MATURITYDATE , FULLPAIDDATE ,"
			+ " FINAMOUNT , DOWNPAYMENT , CURREDUCINGRATE , CURFLATRATE , TOTALPRISCHD , ODPRINCIPAL , ODPROFIT , PENALTYPAID , PENALTYDUE ,"
			+ " PENALTYWAIVED , NSCHDDATE , NSCHDPRI , NSCHDPFT , NSCHDPRIDUE , NSCHDPFTDUE , PFTINSUSP , FINSTATUS , FINSTSREASON ,"
			+ " FINWORSTSTATUS , NOINST , NOPAIDINST , NOODINST , "
			+ " FINCOMMITMENTREF , FINISACTIVE , FIRSTREPAYDATE , FIRSTREPAYAMT ,"
			+ " FINALREPAYAMT , CURODDAYS , FIRSTODDATE , PRVODDATE , CLOSINGSTATUS , FINCATEGORY , PRVRPYSCHDATE , PRVRPYSCHPRI ,"
			+ " PRVRPYSCHPFT , LATESTRPYDATE , LATESTRPYPRI , LATESTRPYPFT , TOTALWRITEOFF , ACCUMULATEDDEPPRI , DEPRECIATEPRI ,"
			+ " TOTALPRIPAIDINADV , PFTAMZNORMAL , PFTAMZPD , AMZTILLLBDNORMAL ,"
			+ " AMZTILLLBDPD , AMZTILLLBDPIS , MAXODDAYS , CALPFTONPD , PFTONPDMETHOD , TOTPFTONPD , TOTPFTONPDPAID , TOTPFTONPDWAIVED ,"
			+ " TOTPFTONPDDUE , ACRSUSPTILLLBD , PRVMTHAMZ , PRVMTHAMZNRM , PRVMTHAMZPD , PRVMTHAMZSUSP , PRVMTHACR , PRVMTHACRSUSP ,"
			+ " FIRSTDISBDATE , LATESTDISBDATE , FUTUREINST , REMAININGTENOR , TOTALTENOR , PFTONPDMRG , EXCESSAMT , EMIINADVANCE ,"
			+ " PAYABLEADVISE , PRODUCTCATEGORY , EXCESSAMTRESV , EMIINADVANCERESV , PAYABLEADVISERESV , TOTCHARGESPAID , LINKEDFINREF ,"
			+ " CLOSEDLINKEDFINREF , UPFRONTFEE , BOUNCEAMTDUE , BOUNCEAMTPAID , BOUNCEAMT , RECEIVABLEADVISE , EXCESSAMTBAL ,"
			+ " EMIINADVANCEBAL , RECEIVABLEADVISEBAL , PAYABLEADVISEBAL , ACTUALODDAYS , LPITILLLBD , GSTLPITILLLBD , LPPTILLLBD ,"
			+ " GSTLPPTILLLBD, AMZMethod, NOAutoIncGrcEnd, CurDPDString) "

			+ " SELECT :AppDate, FINREFERENCE , CUSTID , FINBRANCH , FINTYPE , LASTMDFDATE , TOTALPFTSCHD , TOTALPFTCPZ , TOTALPFTPAID ,"
			+ " TOTALPFTBAL , TOTALPFTPAIDINADV , TOTALPRIPAID , TOTALPRIBAL , TDSCHDPFT , TDPFTCPZ , TDSCHDPFTPAID , TDSCHDPFTBAL ,"
			+ " PFTACCRUED , PFTACCRUESUSP , PFTAMZ , PFTAMZSUSP , TDSCHDPRI , TDSCHDPRIPAID , TDSCHDPRIBAL , ACRTILLLBD , AMZTILLLBD ,"
			+ " REPAYFRQ , CUSTCIF , FINCCY , FINPURPOSE , FINCONTRACTDATE , FINAPPROVEDDATE , FINSTARTDATE , MATURITYDATE , FULLPAIDDATE ,"
			+ " FINAMOUNT , DOWNPAYMENT , CURREDUCINGRATE , CURFLATRATE , TOTALPRISCHD , ODPRINCIPAL , ODPROFIT , PENALTYPAID , PENALTYDUE ,"
			+ " PENALTYWAIVED , NSCHDDATE , NSCHDPRI , NSCHDPFT , NSCHDPRIDUE , NSCHDPFTDUE , PFTINSUSP , FINSTATUS , FINSTSREASON ,"
			+ " FINWORSTSTATUS , NOINST , NOPAIDINST , NOODINST , "
			+ " FINCOMMITMENTREF , FINISACTIVE , FIRSTREPAYDATE , FIRSTREPAYAMT ,"
			+ " FINALREPAYAMT , CURODDAYS , FIRSTODDATE , PRVODDATE , CLOSINGSTATUS , FINCATEGORY , PRVRPYSCHDATE , PRVRPYSCHPRI ,"
			+ " PRVRPYSCHPFT , LATESTRPYDATE , LATESTRPYPRI , LATESTRPYPFT , TOTALWRITEOFF , ACCUMULATEDDEPPRI , DEPRECIATEPRI ,"
			+ " TOTALPRIPAIDINADV , PFTAMZNORMAL , PFTAMZPD , AMZTILLLBDNORMAL ,"
			+ " AMZTILLLBDPD , AMZTILLLBDPIS , MAXODDAYS , CALPFTONPD , PFTONPDMETHOD , TOTPFTONPD , TOTPFTONPDPAID , TOTPFTONPDWAIVED ,"
			+ " TOTPFTONPDDUE , ACRSUSPTILLLBD , PRVMTHAMZ , PRVMTHAMZNRM , PRVMTHAMZPD , PRVMTHAMZSUSP , PRVMTHACR , PRVMTHACRSUSP ,"
			+ " FIRSTDISBDATE , LATESTDISBDATE , FUTUREINST , REMAININGTENOR , TOTALTENOR , PFTONPDMRG , EXCESSAMT , EMIINADVANCE ,"
			+ " PAYABLEADVISE , PRODUCTCATEGORY , EXCESSAMTRESV , EMIINADVANCERESV , PAYABLEADVISERESV , TOTCHARGESPAID , LINKEDFINREF ,"
			+ " CLOSEDLINKEDFINREF , UPFRONTFEE , BOUNCEAMTDUE , BOUNCEAMTPAID , BOUNCEAMT , RECEIVABLEADVISE , EXCESSAMTBAL ,"
			+ " EMIINADVANCEBAL , RECEIVABLEADVISEBAL , PAYABLEADVISEBAL , ACTUALODDAYS , LPITILLLBD , GSTLPITILLLBD , LPPTILLLBD ,"
			+ " GSTLPPTILLLBD, AMZMethod, NOAutoIncGrcEnd, CurDPDString FROM FinPftDetails";

	private static final String snapshotQuery_FinOdDetails = "INSERT INTO FINODDETAILS_SnapShot ("
			+ " AppDate, FINREFERENCE, FINODSCHDDATE, FINODFOR, FINBRANCH, FINTYPE, CUSTID, FINODTILLDATE, FINCURODAMT, "
			+ " FINCURODPRI, FINCURODPFT, FINMAXODAMT, FINMAXODPRI, FINMAXODPFT, GRACEDAYS, INCGRACEDAYS, FINCURODDAYS, TOTPENALTYAMT,"
			+ " TOTWAIVED, TOTPENALTYPAID, TOTPENALTYBAL, FINLMDFDATE, LPIAMT, LPIPAID, LPIBAL, LPIWAIVED, ODINCGRCDAYS, ODCHARGETYPE,"
			+ " ODGRACEDAYS, ODCHARGECALON , ODCHARGEAMTORPERC, ODALLOWWAIVER, ODMAXWAIVERPERC, APPLYODPENALTY, ODMINCAPAMOUNT, ODRULECODE,"
			+ " LpCpz, LpCpzAmount, LpCurCpzBal, LockODRecalCal, PresentmentId, CurOverdraftTxnChrg, MaxOverdraftTxnChrg)"

			+ " SELECT :AppDate, FINREFERENCE, FINODSCHDDATE, FINODFOR, FINBRANCH, FINTYPE, CUSTID, FINODTILLDATE, FINCURODAMT, "
			+ " FINCURODPRI, FINCURODPFT, FINMAXODAMT, FINMAXODPRI, FINMAXODPFT, GRACEDAYS, INCGRACEDAYS, FINCURODDAYS, TOTPENALTYAMT,"
			+ " TOTWAIVED, TOTPENALTYPAID, TOTPENALTYBAL, FINLMDFDATE, LPIAMT, LPIPAID, LPIBAL, LPIWAIVED, ODINCGRCDAYS, ODCHARGETYPE,"
			+ " ODGRACEDAYS, ODCHARGECALON , ODCHARGEAMTORPERC, ODALLOWWAIVER, ODMAXWAIVERPERC, APPLYODPENALTY, ODMINCAPAMOUNT, ODRULECODE,"
			+ " LpCpz, LpCpzAmount, LpCurCpzBal, LockODRecalCal, PresentmentId, CurOverdraftTxnChrg, MaxOverdraftTxnChrg FROM FINODDETAILS";

	private static final String snapshotQuery_FinExcessAmount = "INSERT INTO FINEXCESSAMOUNT_SNAPSHOT ("
			+ " APPDATE, EXCESSID, FINREFERENCE, CUSTID, AMOUNTTYPE, AMOUNT, UTILISEDAMT, RESERVEDAMT, BALANCEAMT)"
			+ " SELECT :AppDate, EXCESSID, fe.FINREFERENCE, CUSTID, AMOUNTTYPE, AMOUNT, UTILISEDAMT, RESERVEDAMT, BALANCEAMT "
			+ " FROM FINEXCESSAMOUNT fe INNER JOIN FINANCEMAIN FM ON FM.FINREFERENCE = FE.FINREFERENCE";

	private static final String snapshotQuery_FinFeeDetail = "INSERT INTO FinFeeDetail_Snapshot("
			+ "AppDate, FeeID, FinReference, CustID, OriginationFee, FinEvent, FeeTypeID, FeeSeq, FeeOrder, CalculatedAmount, ActualAmount, WaivedAmount"
			+ ", PaidAmount, Terms, RemainingFee, PaymentRef, FeeScheduleMethod, CalculationType, RuleCode, FixedAmount, Percentage, CalculateOn"
			+ ", AlwDeviation, MaxWaiverPerc, AlwModifyFee, AlwModifyFeeSchdMthd, Version, Lastmntby, Lastmnton, RecordStatus, RoleCode"
			+ ", NextRoleCode, TaskID, NextTaskID, RecordType, WorkflowID, Status, VasReference, PostDate, PaidAmountOriginal, PaidAmountGst"
			+ ", NetAmountOriginal, NetAmountGst, NetAmount, RemainingFeeOriginal, RemainingFeeGst, TaxApplicable, TaxComponent"
			+ ", ActualAmountOriginal, ActualAmountGst, FeeAmz, TaxPercent, TransactionID, Refundable, InstructionUID, ActPercentage, WaivedGst"
			+ ", ReferenceID, TaxheaderID, PaidTds, RemTds, NetTds)"

			+ " SELECT :AppDate, FeeID, FD.FinReference, FM.CustID, OriginationFee, FinEvent, FeeTypeID, FeeSeq, FeeOrder, CalculatedAmount, ActualAmount"
			+ ", WaivedAmount, PaidAmount, Terms, RemainingFee, PaymentRef, FeeScheduleMethod, CalculationType, RuleCode, FixedAmount"
			+ ", Percentage, CalculateOn, AlwDeviation, MaxWaiverPerc, AlwModifyFee, AlwModifyFeeSchdMthd, FD.Version, FD.Lastmntby, FD.LastMntOn"
			+ ", FD.RecordStatus, FD.RoleCode, FD.NextRoleCode, FD.TaskID, FD.NextTaskID, FD.RecordType, FD.WorkflowID, FD.Status, VasReference, PostDate"
			+ ", PaidAmountOriginal, PaidAmountGst, NetAmountOriginal, NetAmountGst, NetAmount, RemainingFeeOriginal, RemainingFeeGst"
			+ ", TaxApplicable, TaxComponent, ActualAmountOriginal, ActualAmountGst, FeeAmz, TaxPercent, TransactionID, Refundable"
			+ ", InstructionUID, ActPercentage, WaivedGst, ReferenceID, TaxheaderID, PaidTds, RemTds, NetTds FROM FinFeeDetail FD"
			+ " Inner Join FinanceMain FM ON FM.FinReference = FD.FinReference";

	private static final String snapshotQuery_ManualAdvise = "INSERT INTO ManualAdvise_Snapshot("
			+ " AppDate, AdviseID, AdviseType, FinReference, CustID, FeeTypeID, Sequence, AdviseAmount, PaidAmount, WaivedAmount"
			+ ", Remarks, Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId"
			+ ", RecordType, WorkFlowId, BounceID, ReceiptID, ValueDate, PostDate, reservedamt, balanceamt"
			+ ", PaidCGST, PaidSGST, PaidUGST, PaidIGST, WaivedCGST, WaivedSGST, WaivedIGST, WaivedUGST"
			+ ", WaivedCESS, PaidCESS, FinSource, DueCreation, LinkedTranID, TdsPaid, HoldDue, PresentmentId, Status, Reason)"

			+ " SELECT :AppDate,AdviseID, AdviseType, MA.FinReference, FM.CustID, FeeTypeID, Sequence, AdviseAmount, PaidAmount, WaivedAmount"
			+ ", Remarks, MA.Version, MA.LastMntBy, MA.LastMntOn, MA.RecordStatus, MA.RoleCode, MA.NextRoleCode, MA.TaskId, MA.NextTaskId"
			+ ", MA.RecordType, MA.WorkFlowId, BounceID, ReceiptID, ValueDate, PostDate, reservedamt, balanceamt"
			+ ", PaidCGST, PaidSGST, PaidUGST, PaidIGST, WaivedCGST, WaivedSGST, WaivedIGST, WaivedUGST"
			+ ", WaivedCESS, PaidCESS, FinSource, DueCreation, LinkedTranID, TdsPaid, HoldDue, PresentmentId, Status, Reason From ManualAdvise MA"
			+ " Inner Join FinanceMain FM ON FM.FinReference = MA.FinReference";

	public int doSnapshotPreparation(Date date) throws Exception {
		int finPftCount = pftDetailsSnap(date);
		int finODCount = odDetailsSnap(date);
		int finExAmtCount = excessAmountSnap(date);
		int finFeeDetailCount = feeDetailSnap(date);
		int manualAdviseCount = manualAdviseSnap(date);
		return finPftCount + finODCount + finExAmtCount + finFeeDetailCount + manualAdviseCount;
	}

	public int doSnapshotPreparation(Date date, long customerId) throws Exception {
		int finPftCount = pftDetailsSnap(date, customerId);
		int finODCount = odDetailsSnap(date, customerId);
		int finExAmtCount = excessAmountSnap(date, customerId);
		int finFeeDetailCount = feeDetailSnap(date, customerId);
		int manualAdviseCount = manualAdviseSnap(date, customerId);
		return finPftCount + finODCount + finExAmtCount + finFeeDetailCount + manualAdviseCount;
	}

	private int pftDetailsSnap(Date date, long customerId) throws Exception {
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("AppDate", date);
		source.addValue("CustID", customerId);

		String query = snapshotQuery_FinPftDetails + " where CustId = :CustID";
		logger.trace(Literal.SQL + query);

		try {
			return this.namedParameterJdbcTemplate.update(query, source);
		} catch (EmptyResultDataAccessException dae) {
			logger.error(Literal.EXCEPTION, dae);
			throw dae;
		}
	}

	private int pftDetailsSnap(Date date) throws Exception {
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("AppDate", date);

		String query = snapshotQuery_FinPftDetails;
		query = query + " where CustId not in (select CustID from FinPftDetails_SnapShot where AppDate = :AppDate)";

		logger.trace(Literal.SQL + query);

		try {
			return this.namedParameterJdbcTemplate.update(query, source);
		} catch (EmptyResultDataAccessException dae) {
			logger.error(Literal.EXCEPTION, dae);
			throw dae;
		}
	}

	private int odDetailsSnap(Date date, long customerId) throws Exception {
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("AppDate", date);
		source.addValue("CustID", customerId);

		String query = snapshotQuery_FinOdDetails + " where CustId = :CustID";
		logger.trace(Literal.SQL + query);

		try {
			return this.namedParameterJdbcTemplate.update(query, source);
		} catch (EmptyResultDataAccessException dae) {
			logger.error(Literal.EXCEPTION, dae);
			throw dae;
		}

	}

	private int odDetailsSnap(Date date) throws Exception {
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("AppDate", date);

		String query = snapshotQuery_FinOdDetails;
		query = query + " where CustId not in (select CustID from FINODDETAILS_SnapShot where AppDate = :AppDate)";
		//
		logger.trace(Literal.SQL + query);

		try {
			return this.namedParameterJdbcTemplate.update(query, source);
		} catch (EmptyResultDataAccessException dae) {
			logger.error(Literal.EXCEPTION, dae);
			throw dae;
		}

	}

	public int excessAmountSnap(Date date, long customerId) throws Exception {
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("AppDate", date);
		source.addValue("CustID", customerId);

		String query = snapshotQuery_FinExcessAmount + " where fm.CUSTID = :CustID";

		logger.trace(Literal.SQL + query);

		try {
			return this.namedParameterJdbcTemplate.update(query, source);
		} catch (EmptyResultDataAccessException dae) {
			logger.error(Literal.EXCEPTION, dae);
			throw dae;
		}

	}

	private int excessAmountSnap(Date date) throws Exception {
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("AppDate", date);

		String query = snapshotQuery_FinExcessAmount;
		query = query
				+ " where fm.CUSTID not in (SELECT CUSTID FROM FINEXCESSAMOUNT_SNAPSHOT WHERE APPDATE = :AppDate)";

		logger.trace(Literal.SQL + query);

		try {
			return this.namedParameterJdbcTemplate.update(query, source);
		} catch (EmptyResultDataAccessException dae) {
			logger.error(Literal.EXCEPTION, dae);
			throw dae;
		}

	}

	public int feeDetailSnap(Date date, long customerId) throws Exception {
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("AppDate", date);
		source.addValue("CustID", customerId);

		String query = snapshotQuery_FinFeeDetail + " where FM.CUSTID = :CustID";

		logger.trace(Literal.SQL + query);

		try {
			return this.namedParameterJdbcTemplate.update(query, source);
		} catch (EmptyResultDataAccessException dae) {
			logger.error(Literal.EXCEPTION, dae);
			throw dae;
		}

	}

	private int feeDetailSnap(Date date) throws Exception {
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("AppDate", date);

		String query = snapshotQuery_FinFeeDetail;
		query = query + " Where FM.CustID NOT IN (Select CustID From FinFeeDetail_Snapshot Where Appdate = :AppDate)";

		logger.trace(Literal.SQL + query);

		try {
			return this.namedParameterJdbcTemplate.update(query, source);
		} catch (EmptyResultDataAccessException dae) {
			logger.error(Literal.EXCEPTION, dae);
			throw dae;
		}

	}

	public int manualAdviseSnap(Date date, long customerId) throws Exception {
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("AppDate", date);
		source.addValue("CustID", customerId);

		String query = snapshotQuery_ManualAdvise + " where FM.CUSTID = :CustID";

		logger.trace(Literal.SQL + query);

		try {
			return this.namedParameterJdbcTemplate.update(query, source);
		} catch (EmptyResultDataAccessException dae) {
			logger.error(Literal.EXCEPTION, dae);
			throw dae;
		}

	}

	private int manualAdviseSnap(Date date) throws Exception {
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("AppDate", date);

		String query = snapshotQuery_ManualAdvise;
		query = query + " Where FM.CustID NOT IN (Select CustID From ManualAdvise_Snapshot Where Appdate = :AppDate)";

		logger.trace(Literal.SQL + query);

		try {
			return this.namedParameterJdbcTemplate.update(query, source);
		} catch (EmptyResultDataAccessException dae) {
			logger.error(Literal.EXCEPTION, dae);
			throw dae;
		}

	}

	/**
	 * @param dataSource the dataSource to set
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
}