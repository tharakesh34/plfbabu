package com.pennant.app.core;

import java.util.Date;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.pennanttech.pennapps.core.resource.Literal;

public class SnapshotService {
	private static Logger logger = Logger.getLogger(SnapshotService.class);

	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	private static final String snapshotQuery_FinPftDetails = "INSERT INTO FinPftDetails_SnapShot  ("
			+ " AppDate, FINREFERENCE , CUSTID , FINBRANCH , FINTYPE , LASTMDFDATE , TOTALPFTSCHD , TOTALPFTCPZ , TOTALPFTPAID ,"
			+ " TOTALPFTBAL , TOTALPFTPAIDINADV , TOTALPRIPAID , TOTALPRIBAL , TDSCHDPFT , TDPFTCPZ , TDSCHDPFTPAID , TDSCHDPFTBAL ,"
			+ " PFTACCRUED , PFTACCRUESUSP , PFTAMZ , PFTAMZSUSP , TDSCHDPRI , TDSCHDPRIPAID , TDSCHDPRIBAL , ACRTILLLBD , AMZTILLLBD ,"
			+ " REPAYFRQ , CUSTCIF , FINCCY , FINPURPOSE , FINCONTRACTDATE , FINAPPROVEDDATE , FINSTARTDATE , MATURITYDATE , FULLPAIDDATE ,"
			+ " FINAMOUNT , DOWNPAYMENT , CURREDUCINGRATE , CURFLATRATE , TOTALPRISCHD , ODPRINCIPAL , ODPROFIT , PENALTYPAID , PENALTYDUE ,"
			+ " PENALTYWAIVED , NSCHDDATE , NSCHDPRI , NSCHDPFT , NSCHDPRIDUE , NSCHDPFTDUE , PFTINSUSP , FINSTATUS , FINSTSREASON ,"
			+ " FINWORSTSTATUS , NOINST , NOPAIDINST , NOODINST , DISBACCOUNTID , DISBACTCCY , REPAYACCOUNTID ,"
			+ " FINCUSTPFTACCOUNT , INCOMEACCOUNT , UEINCOMESUSPACCOUNT , FINCOMMITMENTREF , FINISACTIVE , FIRSTREPAYDATE , FIRSTREPAYAMT ,"
			+ " FINALREPAYAMT , CURODDAYS , FIRSTODDATE , PRVODDATE , CLOSINGSTATUS , FINCATEGORY , PRVRPYSCHDATE , PRVRPYSCHPRI ,"
			+ " PRVRPYSCHPFT , LATESTRPYDATE , LATESTRPYPRI , LATESTRPYPFT , TOTALWRITEOFF , ACCUMULATEDDEPPRI , DEPRECIATEPRI ,"
			+ " TOTALPRIPAIDINADV , PFTAMZNORMAL , PFTAMZPD , AMZTILLLBDNORMAL ,"
			+ " AMZTILLLBDPD , AMZTILLLBDPIS , MAXODDAYS , CALPFTONPD , PFTONPDMETHOD , TOTPFTONPD , TOTPFTONPDPAID , TOTPFTONPDWAIVED ,"
			+ " TOTPFTONPDDUE , ACRSUSPTILLLBD , PRVMTHAMZ , PRVMTHAMZNRM , PRVMTHAMZPD , PRVMTHAMZSUSP , PRVMTHACR , PRVMTHACRSUSP ,"
			+ " FIRSTDISBDATE , LATESTDISBDATE , FUTUREINST , REMAININGTENOR , TOTALTENOR , PFTONPDMRG , EXCESSAMT , EMIINADVANCE ,"
			+ " PAYABLEADVISE , PRODUCTCATEGORY , EXCESSAMTRESV , EMIINADVANCERESV , PAYABLEADVISERESV , TOTCHARGESPAID , LINKEDFINREF ,"
			+ " CLOSEDLINKEDFINREF , UPFRONTFEE , BOUNCEAMTDUE , BOUNCEAMTPAID , BOUNCEAMT , RECEIVABLEADVISE , EXCESSAMTBAL ,"
			+ " EMIINADVANCEBAL , RECEIVABLEADVISEBAL , PAYABLEADVISEBAL , ACTUALODDAYS , LPITILLLBD , GSTLPITILLLBD , LPPTILLLBD ,"
			+ " GSTLPPTILLLBD, AMZMethod ) "

			+ " SELECT :AppDate, FINREFERENCE , CUSTID , FINBRANCH , FINTYPE , LASTMDFDATE , TOTALPFTSCHD , TOTALPFTCPZ , TOTALPFTPAID ,"
			+ " TOTALPFTBAL , TOTALPFTPAIDINADV , TOTALPRIPAID , TOTALPRIBAL , TDSCHDPFT , TDPFTCPZ , TDSCHDPFTPAID , TDSCHDPFTBAL ,"
			+ " PFTACCRUED , PFTACCRUESUSP , PFTAMZ , PFTAMZSUSP , TDSCHDPRI , TDSCHDPRIPAID , TDSCHDPRIBAL , ACRTILLLBD , AMZTILLLBD ,"
			+ " REPAYFRQ , CUSTCIF , FINCCY , FINPURPOSE , FINCONTRACTDATE , FINAPPROVEDDATE , FINSTARTDATE , MATURITYDATE , FULLPAIDDATE ,"
			+ " FINAMOUNT , DOWNPAYMENT , CURREDUCINGRATE , CURFLATRATE , TOTALPRISCHD , ODPRINCIPAL , ODPROFIT , PENALTYPAID , PENALTYDUE ,"
			+ " PENALTYWAIVED , NSCHDDATE , NSCHDPRI , NSCHDPFT , NSCHDPRIDUE , NSCHDPFTDUE , PFTINSUSP , FINSTATUS , FINSTSREASON ,"
			+ " FINWORSTSTATUS , NOINST , NOPAIDINST , NOODINST , DISBACCOUNTID , DISBACTCCY , REPAYACCOUNTID ,"
			+ " FINCUSTPFTACCOUNT , INCOMEACCOUNT , UEINCOMESUSPACCOUNT , FINCOMMITMENTREF , FINISACTIVE , FIRSTREPAYDATE , FIRSTREPAYAMT ,"
			+ " FINALREPAYAMT , CURODDAYS , FIRSTODDATE , PRVODDATE , CLOSINGSTATUS , FINCATEGORY , PRVRPYSCHDATE , PRVRPYSCHPRI ,"
			+ " PRVRPYSCHPFT , LATESTRPYDATE , LATESTRPYPRI , LATESTRPYPFT , TOTALWRITEOFF , ACCUMULATEDDEPPRI , DEPRECIATEPRI ,"
			+ " TOTALPRIPAIDINADV , PFTAMZNORMAL , PFTAMZPD , AMZTILLLBDNORMAL ,"
			+ " AMZTILLLBDPD , AMZTILLLBDPIS , MAXODDAYS , CALPFTONPD , PFTONPDMETHOD , TOTPFTONPD , TOTPFTONPDPAID , TOTPFTONPDWAIVED ,"
			+ " TOTPFTONPDDUE , ACRSUSPTILLLBD , PRVMTHAMZ , PRVMTHAMZNRM , PRVMTHAMZPD , PRVMTHAMZSUSP , PRVMTHACR , PRVMTHACRSUSP ,"
			+ " FIRSTDISBDATE , LATESTDISBDATE , FUTUREINST , REMAININGTENOR , TOTALTENOR , PFTONPDMRG , EXCESSAMT , EMIINADVANCE ,"
			+ " PAYABLEADVISE , PRODUCTCATEGORY , EXCESSAMTRESV , EMIINADVANCERESV , PAYABLEADVISERESV , TOTCHARGESPAID , LINKEDFINREF ,"
			+ " CLOSEDLINKEDFINREF , UPFRONTFEE , BOUNCEAMTDUE , BOUNCEAMTPAID , BOUNCEAMT , RECEIVABLEADVISE , EXCESSAMTBAL ,"
			+ " EMIINADVANCEBAL , RECEIVABLEADVISEBAL , PAYABLEADVISEBAL , ACTUALODDAYS , LPITILLLBD , GSTLPITILLLBD , LPPTILLLBD ,"
			+ " GSTLPPTILLLBD, AMZMethod FROM FinPftDetails";

	private static final String snapshotQuery_FinOdDetails = "INSERT INTO FINODDETAILS_SnapShot ("
			+ " AppDate, FINREFERENCE, FINODSCHDDATE, FINODFOR, FINBRANCH, FINTYPE, CUSTID, FINODTILLDATE, FINCURODAMT, "
			+ " FINCURODPRI, FINCURODPFT, FINMAXODAMT, FINMAXODPRI, FINMAXODPFT, GRACEDAYS, INCGRACEDAYS, FINCURODDAYS, TOTPENALTYAMT,"
			+ " TOTWAIVED, TOTPENALTYPAID, TOTPENALTYBAL, FINLMDFDATE, LPIAMT, LPIPAID, LPIBAL, LPIWAIVED, ODINCGRCDAYS, ODCHARGETYPE,"
			+ " ODGRACEDAYS, ODCHARGECALON , ODCHARGEAMTORPERC, ODALLOWWAIVER, ODMAXWAIVERPERC, APPLYODPENALTY, ODMINCAPAMOUNT, ODRULECODE,"
			+ " LpCpz, LpCpzAmount, LpCurCpzBal, LockODRecalCal)"

			+ " SELECT :AppDate, FINREFERENCE, FINODSCHDDATE, FINODFOR, FINBRANCH, FINTYPE, CUSTID, FINODTILLDATE, FINCURODAMT, "
			+ " FINCURODPRI, FINCURODPFT, FINMAXODAMT, FINMAXODPRI, FINMAXODPFT, GRACEDAYS, INCGRACEDAYS, FINCURODDAYS, TOTPENALTYAMT,"
			+ " TOTWAIVED, TOTPENALTYPAID, TOTPENALTYBAL, FINLMDFDATE, LPIAMT, LPIPAID, LPIBAL, LPIWAIVED, ODINCGRCDAYS, ODCHARGETYPE,"
			+ " ODGRACEDAYS, ODCHARGECALON , ODCHARGEAMTORPERC, ODALLOWWAIVER, ODMAXWAIVERPERC, APPLYODPENALTY, ODMINCAPAMOUNT, ODRULECODE,"
			+ " LpCpz, LpCpzAmount, LpCurCpzBal, LockODRecalCal FROM FINODDETAILS";

	private static final String snapshotQuery_FinExcessAmount = "INSERT INTO FINEXCESSAMOUNT_SNAPSHOT ("
			+ " APPDATE, EXCESSID, FINREFERENCE, CUSTID, AMOUNTTYPE, AMOUNT, UTILISEDAMT, RESERVEDAMT, BALANCEAMT)"
			+ " SELECT :AppDate, EXCESSID, fe.FINREFERENCE, CUSTID, AMOUNTTYPE, AMOUNT, UTILISEDAMT, RESERVEDAMT, BALANCEAMT "
			+ " FROM FINEXCESSAMOUNT fe INNER JOIN FINANCEMAIN FM ON FM.FINREFERENCE = FE.FINREFERENCE";

	public int doSnapshotPreparation(Date date) throws Exception {
		int finPftCount = pftDetailsSnap(date);
		int finODCount = odDetailsSnap(date);
		int finExAmtCount = excessAmountSnap(date);
		return finPftCount + finODCount + finExAmtCount;
	}

	public int doSnapshotPreparation(Date date, long customerId) throws Exception {
		int finPftCount = pftDetailsSnap(date, customerId);
		int finODCount = odDetailsSnap(date, customerId);
		int finExAmtCount = excessAmountSnap(date, customerId);
		return finPftCount + finODCount + finExAmtCount;
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

	/**
	 * @param dataSource
	 *            the dataSource to set
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
}