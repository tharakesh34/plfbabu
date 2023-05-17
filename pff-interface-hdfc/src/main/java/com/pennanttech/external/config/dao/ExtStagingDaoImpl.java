package com.pennanttech.external.config.dao;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.pennanttech.external.extractions.model.AlmExtract;
import com.pennanttech.external.extractions.model.BaselOne;
import com.pennanttech.external.extractions.model.BaselTwoExtract;
import com.pennanttech.external.extractions.model.RPMSExtract;
import com.pennanttech.external.presentment.model.ExtPresentmentFile;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.resource.Literal;

public class ExtStagingDaoImpl implements ExtStagingDao {
	private static final Logger logger = LogManager.getLogger(ExtStagingDaoImpl.class);

	private NamedParameterJdbcTemplate extNamedJdbcTemplate;

	@Override
	public List<ExtPresentmentFile> getStagingPresentment(String flag) {
		logger.debug(Literal.ENTERING);
		String queryStr;

		List<ExtPresentmentFile> presentmentFilesList = new ArrayList<ExtPresentmentFile>();

		StringBuilder query = new StringBuilder();
		query.append(" SELECT FINNONE_BATCHID,AGREEMENTID,CHEQUESNO,");
		query.append(" CHEQUEDATE,CHEQUESTATUS,BOUNCE_REASON ");
		query.append(" FROM PDC_BATCH_D_STG ");
		query.append(" WHERE  PICK_FINNONE = ?");
		queryStr = query.toString();

		extNamedJdbcTemplate.getJdbcOperations().query(queryStr, ps -> {
			ps.setString(1, flag);// FIXME
		}, rs -> {
			ExtPresentmentFile details = new ExtPresentmentFile();
			details.setClusterId(StringUtils.trimToEmpty(rs.getString("FINNONE_BATCHID")));
			details.setFinReference(rs.getString("AGREEMENTID"));
			details.setChequeSerialNo(rs.getString("CHEQUESNO"));
			details.setChequeDate(rs.getDate("CHEQUEDATE"));
			details.setStatus(rs.getString("CHEQUESTATUS"));
			details.setBounceReason(rs.getString("BOUNCE_REASON"));
			presentmentFilesList.add(details);
		});

		logger.debug(Literal.LEAVING);
		return presentmentFilesList;
	}

	@Override
	public void updatePickupStatus(String pickFlag, long agreementId, String chequeSno) {
		Timestamp curTimeStamp = new Timestamp(System.currentTimeMillis());
		StringBuilder sql = new StringBuilder("UPDATE PDC_BATCH_D_STG");
		sql.append(" SET PICK_FINNONE = ?, FINNONE_PICK_DATE = ? WHERE AGREEMENTID= ? AND CHEQUESNO=?");

		logger.debug(Literal.SQL + sql.toString());

		extNamedJdbcTemplate.getJdbcOperations().update(sql.toString(), ps -> {
			int index = 1;
			ps.setString(index++, pickFlag);
			ps.setTimestamp(index++, curTimeStamp);
			ps.setLong(index++, agreementId);
			ps.setString(index, chequeSno);
		});
	}

	@Override
	public void updateErrorDetails(long agreementId, String chequeSno, String errorFlag, String errorDesc) {
		StringBuilder sql = new StringBuilder("UPDATE PDC_BATCH_D_STG");
		sql.append(" SET EXCEPTION_FLAG = ?, EXCEPTION_DESC = ? WHERE AGREEMENTID= ? AND CHEQUESNO=?");

		logger.debug(Literal.SQL + sql.toString());

		extNamedJdbcTemplate.getJdbcOperations().update(sql.toString(), ps -> {
			int index = 1;
			ps.setString(index++, errorFlag);
			ps.setString(index++, errorDesc);
			ps.setLong(index++, agreementId);
			ps.setString(index, chequeSno);
		});
	}

	@Override
	public void saveBaselOneExtractionDataToTable(BaselOne baselOne) {
		String sql = "INSERT INTO EXT_BASEL_ONE "
				+ " ( AGREEMENTID,CUSTOMERID,REG_EMI,EXCESS_MONEY,ACCRUED_INT,UN_EARNED_INT,"
				+ "GROSS_RCVBL,SUS_INT,ASSET_CLASSIFTN_ID,SECURITIZED,EXACT_NPA_DATE,"
				+ "ANNUAL_TURNOVER,EXPTYPE,TENURE )  VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

		logger.debug(Literal.SQL + sql);

		extNamedJdbcTemplate.getJdbcOperations().update(sql.toString(), ps -> {
			int index = 1;

			ps.setString(index++, baselOne.getAgreementId());
			ps.setString(index++, baselOne.getCustomerId());
			ps.setBigDecimal(index++, baselOne.getRegEmi());
			ps.setBigDecimal(index++, baselOne.getExcessmoney());
			ps.setBigDecimal(index++, baselOne.getAccruedInterest());
			ps.setBigDecimal(index++, baselOne.getUnEarnedInterest());
			ps.setBigDecimal(index++, baselOne.getGrossReceivable());
			ps.setBigDecimal(index++, baselOne.getSuspenseInterest());
			ps.setString(index++, baselOne.getAssetClassficationId());
			ps.setString(index++, baselOne.getSecuritized());
			ps.setDate(index++, (Date) baselOne.getExactNpaDate());
			ps.setBigDecimal(index++, baselOne.getAnnualTurnover());
			ps.setString(index++, baselOne.getExptype());
			ps.setInt(index, baselOne.getTenure());

		});
	}

	@Override
	public void saveAlmExtractionDataToTable(AlmExtract almExtract) {
		String sql = "INSERT INTO ALM_REPORT "
				+ " ( ALM_REPORT_TYPE,ALM_REPORT_DATE,ACCOUNT_NUM,ACCRUALBASIS,ACCRUED_INTEREST,"
				+ "BANKNUMBER ,BRANCH,COMP_FREQ,COMP_FREQ_INCR,CURRENCY_CODE,CURRENT_BALANCE,"
				+ "DUE_DATE,INTT_PAYMT_FREQ,INTT_RATE,LIFE_CEILING,LIFE_FLOOR,LOAN_TYPE,MATURITY,"
				+ "ORIG_BALANCE,ORIG_TERM,ORIGINATION_DATE,INSTALMENT,PYMT_FREQ,PYMT_TYPE,PCT_OWNED,"
				+ "RATE_FLAG,REPRICE_INDEX,DPD,TOTAL_INTEREST,BILLED_INTEREST,BILLED_NOT_RECEIVED_INTEREST,"
				+ "BILLED_NOT_RECEIVED_PRINCIPAL,CUSTOMERNAME,PRETAXIRR,SCHEMEID,PROFESSIONCODE,BROKERID,"
				+ "PSLCTGID,NPA_STAGEID,WEAKER_SECTION_DESC ) "
				+ " VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

		logger.debug(Literal.SQL + sql);

		extNamedJdbcTemplate.getJdbcOperations().update(sql.toString(), ps -> {
			int index = 1;
			ps.setString(index++, almExtract.getAlmReportType());
			ps.setDate(index++, (Date) almExtract.getAlmReportDate());
			ps.setString(index++, almExtract.getAccountNumber());
			ps.setString(index++, almExtract.getAccrualBasis());
			ps.setBigDecimal(index++, almExtract.getAccruedInterest());

			ps.setString(index++, almExtract.getBankNumber());
			ps.setString(index++, almExtract.getBranch());
			ps.setLong(index++, almExtract.getCompFreq());
			ps.setLong(index++, almExtract.getCompFreqIncr());
			ps.setString(index++, almExtract.getCurrencyCode());
			ps.setBigDecimal(index++, almExtract.getCurrentBalance());

			ps.setDate(index++, (Date) JdbcUtil.getDate(almExtract.getDueDate()));
			ps.setLong(index++, almExtract.getInitPaymentFreq());
			ps.setBigDecimal(index++, almExtract.getInitRate());
			ps.setBigDecimal(index++, almExtract.getLifeCeiling());
			ps.setBigDecimal(index++, almExtract.getLifeFloor());
			ps.setString(index++, almExtract.getLoanType());
			ps.setDate(index++, (Date) JdbcUtil.getDate(almExtract.getMaturity()));

			ps.setBigDecimal(index++, almExtract.getOriginalBalance());
			ps.setInt(index++, almExtract.getOriginalTerm());
			ps.setDate(index++, (Date) almExtract.getOriginationDate());
			ps.setBigDecimal(index++, almExtract.getInstalment());
			ps.setString(index++, almExtract.getPaymentFreq());
			ps.setLong(index++, almExtract.getPaymentType());
			ps.setLong(index++, almExtract.getPctOwned());

			ps.setString(index++, almExtract.getRateFlag());
			ps.setString(index++, almExtract.getRePriceIndex());
			ps.setLong(index++, almExtract.getDpd());
			ps.setBigDecimal(index++, almExtract.getTotalInterest());
			ps.setBigDecimal(index++, almExtract.getBilledInterest());
			ps.setBigDecimal(index++, almExtract.getBilledNotReceivedInterest());

			ps.setBigDecimal(index++, almExtract.getBilledNotReceivedPrincipal());
			ps.setString(index++, almExtract.getCustomerName());
			ps.setBigDecimal(index++, almExtract.getPretaxirr());
			ps.setLong(index++, almExtract.getSchemeId());
			ps.setString(index++, almExtract.getProfessionCode());
			ps.setLong(index++, almExtract.getBrokerId());

			ps.setLong(index++, almExtract.getPslctgid());
			ps.setString(index++, almExtract.getNpaStageId());
			ps.setString(index, almExtract.getWeakerSectionDesc());

		});

	}

	@Override
	public void saveBaselTwoExtractionDataToTable(BaselTwoExtract baselTwo) {
		String sql = "INSERT INTO BASELTWO " + " ( AGREEMENTID,CUSTOMERID,ICN_STATUS,ICN_ACQUIRED,LOAN_APPL_DT,"
				+ "TOTAL_EMIS,DND,APPLIED_BEFORE,OLD_AGMTNO,MICR_LOCATION,FEES,"
				+ "FIXED_FLOAT,DEALER_COMM,MANF_DISC,PROMOTION_CODE,RC_AVAIL_STATUS,"
				+ "RC_ACQUIRED,EMI_TYPE,REST,CUST_ACCOUNT,NO_BOUNCES,CHARGES_PAID,"
				+ "PREV_NPA,SECURT_FLAG,SECURT_DT,DPD_STRING,TOT_AMT_PAID_MNTH_CHQ,"
				+ "TOT_AMT_PAID_MNTH_CASH,RESCHEDULED,RESCHEDULE_EFF_DT,CLOSURE_TYPE,"
				+ "PRIN_LOSS_CLOSURE,IBPC_START,IBPC_END,MONTHS_IN_PREV_JOB,MONTHS_IN_CURR_JOB,"
				+ "MONTHS_IN_CURR_RESIDENCE,RENT_PM,DEVIATION,VEHICLE_AGE,APP_SCORE,RIC_SCORE,"
				+ "INCOME_PROOF_RECEIVED,TOTAL_EXPERIENCE,EMPLOYER_CATEGORY,MONTHS_IN_CITY,"
				+ "ELIGIBILITY,OBLIGATIONS,OBLIGATIONS_N) "
				+ " VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,"
				+ "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

		logger.debug(Literal.SQL + sql);

		extNamedJdbcTemplate.getJdbcOperations().update(sql.toString(), ps -> {
			int index = 1;
			ps.setLong(index++, baselTwo.getAgreementId());
			ps.setLong(index++, baselTwo.getCustomerId());
			ps.setString(index++, baselTwo.getIcnStatus());
			ps.setDate(index++, (Date) baselTwo.getIcnAcquired());
			ps.setDate(index++, (Date) baselTwo.getLoanApplDate());
			ps.setInt(index++, baselTwo.getTotalEmis());
			ps.setString(index++, baselTwo.getDnd());
			ps.setString(index++, baselTwo.getAppliedBefore());
			ps.setString(index++, baselTwo.getOldAgmtNo());
			ps.setString(index++, baselTwo.getMicrLocation());
			ps.setBigDecimal(index++, baselTwo.getFees());
			ps.setString(index++, baselTwo.getFixedFloat());
			ps.setBigDecimal(index++, baselTwo.getDealerComm());
			ps.setBigDecimal(index++, baselTwo.getManfDisc());
			ps.setString(index++, baselTwo.getPromotionCode());
			ps.setString(index++, baselTwo.getRcAvailStatus());
			ps.setDate(index++, (Date) baselTwo.getRcAcquired());
			ps.setString(index++, baselTwo.getEmiType());
			ps.setString(index++, baselTwo.getRest());
			ps.setString(index++, baselTwo.getCustAccount());
			ps.setInt(index++, baselTwo.getNoBounces());
			ps.setBigDecimal(index++, baselTwo.getChargesPaid());
			ps.setDate(index++, (Date) baselTwo.getPrevNPA());
			ps.setString(index++, baselTwo.getSecurtFlag());
			ps.setDate(index++, (Date) baselTwo.getSecurtDt());
			ps.setString(index++, baselTwo.getDpdString());
			ps.setBigDecimal(index++, baselTwo.getTotAmtPaidMnthChq());
			ps.setBigDecimal(index++, baselTwo.getTotAmtPaidMnthCash());
			ps.setString(index++, baselTwo.getRescheduled());
			ps.setDate(index++, (Date) baselTwo.getRescheduleEffDt());
			ps.setString(index++, baselTwo.getClosureType());
			ps.setBigDecimal(index++, baselTwo.getPrinLossClosure());
			ps.setDate(index++, (Date) baselTwo.getIbpcStart());
			ps.setDate(index++, (Date) baselTwo.getIbpcEnd());
			ps.setInt(index++, baselTwo.getMonthsInPrevJob());
			ps.setInt(index++, baselTwo.getMonthsIncurrJob());
			ps.setInt(index++, baselTwo.getMonthsInCurrResidence());
			ps.setBigDecimal(index++, baselTwo.getRentPM());
			ps.setString(index++, baselTwo.getDeviation());
			ps.setInt(index++, baselTwo.getVehicleAge());
			ps.setInt(index++, baselTwo.getAppScore());
			ps.setInt(index++, baselTwo.getRicScore());
			ps.setString(index++, baselTwo.getIncomeProofReceived());
			ps.setBigDecimal(index++, baselTwo.getTotalExperience());
			ps.setString(index++, baselTwo.getEmployerCategory());
			ps.setInt(index++, baselTwo.getMonthsInCity());
			ps.setString(index++, baselTwo.getEligibility());
			ps.setBigDecimal(index++, baselTwo.getObligations());
			ps.setBigDecimal(index, baselTwo.getObligationsn());

		});

	}

	@Override
	public void saveRPMSExtractExtractionDataToTable(RPMSExtract rpmsExtract) {

		String sql = "INSERT INTO RPMSEXTRACT "
				+ " ( AGREEMENTID,CUSTOMERID,STATUS,MULTI_LINK_LOAN_FLAG,PARENT_LOAN_NO,"
				+ "CUSTOMER_SEGMENT,GROUPID,GROUPDESC,GROUPCODE,REPO_SETTLED_FLAG,"
				+ "CLOSURE_REASON,CLOSURE_DATE,POS_ON_CLOSURE,PDC_FLAG,TOT_PRIN_WAIVE_OFF,"
				+ "TOT_INT_WAIVE_OFF,WOFF_CHQBOUNCE_CHARGES,WOFF_OVERDUE_CHARGE,WOFF_OTHERS,"
				+ "RAM_ID,TURNOVER_IN_YEAR_ONE,TURNOVER_AMT_YEAR_ONE,TURNOVER_IN_YEAR_TWO,"
				+ "TURNOVER_AMT_YEAR_TWO,TURNOVER_IN_YEAR_THREE,TURNOVER_AMT_YEAR_THREE ) "
				+ " VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

		logger.debug(Literal.SQL + sql);

		extNamedJdbcTemplate.getJdbcOperations().update(sql.toString(), ps -> {
			int index = 1;
			ps.setLong(index++, rpmsExtract.getAgreementId());
			ps.setLong(index++, rpmsExtract.getCustomerId());
			ps.setString(index++, rpmsExtract.getStatus());
			ps.setString(index++, rpmsExtract.getMultiLinkLoanFlag());
			ps.setLong(index++, rpmsExtract.getParentLoanNo());
			ps.setString(index++, rpmsExtract.getCustomerSegment());
			ps.setLong(index++, rpmsExtract.getGroupId());
			ps.setString(index++, rpmsExtract.getGroupDesc());
			ps.setString(index++, rpmsExtract.getGroupCode());
			ps.setString(index++, rpmsExtract.getRepoSettledFlag());
			ps.setString(index++, rpmsExtract.getClosureReason());
			ps.setDate(index++, (Date) rpmsExtract.getClosureDate());
			ps.setBigDecimal(index++, rpmsExtract.getPosOnClosure());
			ps.setString(index++, rpmsExtract.getPdcFlag());
			ps.setBigDecimal(index++, rpmsExtract.getTotPrinWaiveOff());
			ps.setBigDecimal(index++, rpmsExtract.getTotIntWaiveOff());
			ps.setBigDecimal(index++, rpmsExtract.getWoffChqBounceCharges());
			ps.setBigDecimal(index++, rpmsExtract.getWoffOverDueCharge());
			ps.setBigDecimal(index++, rpmsExtract.getWoffOthers());
			ps.setString(index++, rpmsExtract.getRamId());
			ps.setString(index++, rpmsExtract.getTurnOverInYearOne());
			ps.setBigDecimal(index++, rpmsExtract.getTurnOverAmtYearOne());
			ps.setString(index++, rpmsExtract.getTurnOverInYearTwo());
			ps.setBigDecimal(index++, rpmsExtract.getTurnOverAmtYearTwo());
			ps.setString(index++, rpmsExtract.getTurnOverInYearThree());
			ps.setBigDecimal(index++, rpmsExtract.getTurnOverAmtYearThree());

		});

	}

	@Override
	public void truncateTable(String tableName) {
		extNamedJdbcTemplate.getJdbcOperations().update("TRUNCATE TABLE " + tableName);
	}

	public void setStagingDataSource(DataSource stagingDataSource) {
		this.extNamedJdbcTemplate = new NamedParameterJdbcTemplate(stagingDataSource);
	}

}
