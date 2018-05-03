package com.pennant.app.core;

import java.util.Date;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

public class SnapshotService {

	private static Logger				logger			= Logger.getLogger(SnapshotService.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate	namedParameterJdbcTemplate;

	private static final String			snapshotQuery_FinPftDetails	= "INSERT INTO FinPftDetails_SnapShot  "
			+ "SELECT :AppDate,FINREFERENCE,CUSTID,FINBRANCH,FINTYPE,"
			+ "LASTMDFDATE,TOTALPFTSCHD,TOTALPFTCPZ,TOTALPFTPAID,TOTALPFTBAL,TOTALPFTPAIDINADV,TOTALPRIPAID,"
			+ "TOTALPRIBAL,TDSCHDPFT,TDPFTCPZ,TDSCHDPFTPAID,TDSCHDPFTBAL,PFTACCRUED,PFTACCRUESUSP,PFTAMZ,"
			+ "PFTAMZSUSP,TDSCHDPRI,TDSCHDPRIPAID,TDSCHDPRIBAL,ACRTILLLBD,AMZTILLLBD,REPAYFRQ,CUSTCIF,"
			+ "FINCCY,FINPURPOSE,FINCONTRACTDATE,FINAPPROVEDDATE,FINSTARTDATE,MATURITYDATE,FULLPAIDDATE,"
			+ "FINAMOUNT,DOWNPAYMENT,CURREDUCINGRATE,CURFLATRATE,TOTALPRISCHD,ODPRINCIPAL,ODPROFIT,PENALTYPAID,"
			+ "PENALTYDUE,PENALTYWAIVED,NSCHDDATE,NSCHDPRI,NSCHDPFT,NSCHDPRIDUE,NSCHDPFTDUE,PFTINSUSP,FINSTATUS,"
			+ "FINSTSREASON,FINWORSTSTATUS,NOINST,NOPAIDINST,NOODINST,FINACCOUNT,FINACTYPE,DISBACCOUNTID,"
			+ "DISBACTCCY,REPAYACCOUNTID,FINCUSTPFTACCOUNT,INCOMEACCOUNT,UEINCOMESUSPACCOUNT,FINCOMMITMENTREF,"
			+ "FINISACTIVE,FIRSTREPAYDATE,FIRSTREPAYAMT,FINALREPAYAMT,CURODDAYS,FIRSTODDATE,PRVODDATE,"
			+ "CLOSINGSTATUS,FINCATEGORY,PRVRPYSCHDATE,PRVRPYSCHPRI,PRVRPYSCHPFT,LATESTRPYDATE,LATESTRPYPRI,"
			+ "LATESTRPYPFT,TOTALWRITEOFF,ACCUMULATEDDEPPRI,DEPRECIATEPRI,TOTALADVPFTSCHD,TOTALRBTSCHD,"
			+ "TOTALPRIPAIDINADV,TDSCHDADVPFT,TDSCHDRBT,PFTAMZNORMAL,PFTAMZPD,AMZTILLLBDNORMAL,AMZTILLLBDPD,"
			+ "AMZTILLLBDPIS,MAXODDAYS,CALPFTONPD,PFTONPDMETHOD,TOTPFTONPD,TOTPFTONPDPAID,TOTPFTONPDWAIVED,"
			+ "TOTPFTONPDDUE,ACRSUSPTILLLBD,PRVMTHAMZ,PRVMTHAMZNRM,PRVMTHAMZPD,PRVMTHAMZSUSP,PRVMTHACR,"
			+ "PRVMTHACRSUSP,FIRSTDISBDATE,LATESTDISBDATE,FUTUREINST,REMAININGTENOR,TOTALTENOR,PFTONPDMRG,"
			+ "EXCESSAMT,EMIINADVANCE,PAYABLEADVISE,PRODUCTCATEGORY,EXCESSAMTRESV,EMIINADVANCERESV,"
			+ "PAYABLEADVISERESV,TOTCHARGESPAID,LINKEDFINREF,CLOSEDLINKEDFINREF,UPFRONTFEE,BOUNCEAMTDUE,"
			+ "BOUNCEAMTPAID,BOUNCEAMT,RECEIVABLEADVISE,EXCESSAMTBAL,EMIINADVANCEBAL,RECEIVABLEADVISEBAL,PAYABLEADVISEBAL from FinPftDetails";
	
	private static final String			snapshotQuery_FinOdDetails	= "INSERT INTO FINODDETAILS_SnapShot  "
			+ "SELECT :AppDate,FINREFERENCE,FINODSCHDDATE,FINODFOR,FINBRANCH,FINTYPE,"
			+ "CUSTID,FINODTILLDATE,FINCURODAMT,FINCURODPRI,FINCURODPFT,FINMAXODAMT,FINMAXODPRI,FINMAXODPFT,GRACEDAYS,INCGRACEDAYS,"
			+ "FINCURODDAYS,TOTPENALTYAMT,TOTWAIVED,TOTPENALTYPAID,TOTPENALTYBAL,FINLMDFDATE,LPIAMT,LPIPAID,LPIBAL,LPIWAIVED,ODCHARGETYPE,"
			+ "ODGRACEDAYS,ODCHARGECALON,ODCHARGEAMTORPERC,ODALLOWWAIVER,ODMAXWAIVERPERC,APPLYODPENALTY,ODINCGRCDAYS from FINODDETAILS";

	/**
	 * Method for prepare Snapshot details
	 * 
	 * @param date
	 * @return
	 */
	public int doSnapshotPreparation(Date date) throws Exception {

		int finPftCount = snapshotPreparationForFinPftDetails(date);
		int finODCount = snapshotPreparationForFinOdDetails(date);

		return finPftCount + finODCount;
	}

	/**
	 * 
	 * @param date
	 * @return
	 * @throws Exception
	 */
	private int snapshotPreparationForFinPftDetails(Date date) throws Exception {

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("AppDate", date);
		
		logger.debug("selectSql: " + snapshotQuery_FinPftDetails);
		
		try {
			return this.namedParameterJdbcTemplate.update(snapshotQuery_FinPftDetails, source);
		} catch (EmptyResultDataAccessException dae) {
			logger.error("Exception: ", dae);
			throw dae;
		}

	}

	/**
	 * 
	 * @param date
	 * @return
	 * @throws Exception
	 */
	private int snapshotPreparationForFinOdDetails(Date date) throws Exception {

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("AppDate", date);
		
		logger.debug("selectSql: " + snapshotQuery_FinOdDetails);
		
		try {
			return this.namedParameterJdbcTemplate.update(snapshotQuery_FinOdDetails, source);
		} catch (EmptyResultDataAccessException dae) {
			logger.error("Exception: ", dae);
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
