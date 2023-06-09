package com.pennanttech.external.extractions.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.sql.DataSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.core.CallableStatementCreator;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.pennanttech.external.extractions.model.AlmExtract;
import com.pennanttech.external.extractions.model.BaselOne;
import com.pennanttech.external.extractions.model.BaselTwoExtract;
import com.pennanttech.external.extractions.model.RPMSExtract;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.resource.Literal;

public class ExtExtractionDaoImpl implements ExtExtractionDao {
	private static final Logger logger = LogManager.getLogger(ExtExtractionDaoImpl.class);

	private NamedParameterJdbcTemplate mainNamedJdbcTemplate;
	private NamedParameterJdbcTemplate extNamedJdbcTemplate;
	private NamedParameterJdbcTemplate stageNamedJdbcTemplate;

	public ExtExtractionDaoImpl() {
		super();
	}

	@Override
	public long getSeqNumber(String tableName) {
		StringBuilder sql = new StringBuilder("select ").append(tableName).append(".NEXTVAL from DUAL");

		return this.extNamedJdbcTemplate.queryForObject(sql.toString(), new MapSqlParameterSource(), Long.class);
	}

	public String executeSp(String spName, String fileName) {
		logger.info("File writing SP Execution Started");
		String status = "FAIL";
		try {
			this.mainNamedJdbcTemplate.getJdbcOperations().call(new CallableStatementCreator() {

				@Override
				public CallableStatement createCallableStatement(Connection connection) throws SQLException {

					CallableStatement callableStatement = connection.prepareCall(spName);
					callableStatement.setString(1, fileName);
					return callableStatement;

				}
			}, new ArrayList<>());

			status = "SUCCESS";
		} catch (Exception e) {
			logger.error("Exception: Error while executing the Procedure ", e);
			status = "Error In Executing Procedure";
		}
		logger.info("File writing SP Execution Completed");
		return status;
	}

	@Override
	public void truncateTable(String tableName) {
		mainNamedJdbcTemplate.getJdbcOperations().update("TRUNCATE TABLE " + tableName);
	}

	@Override
	public void saveBaselOneExtractionDataToTable(BaselOne baselOne) {
		String sql = "INSERT INTO EXT_BASEL_ONE "
				+ " ( AGREEMENTID,CUSTOMERID,REG_EMI,EXCESS_MONEY,ACCRUED_INT,UN_EARNED_INT,"
				+ "GROSS_RCVBL,SUS_INT,ASSET_CLASSIFTN_ID,SECURITIZED,EXACT_NPA_DATE,"
				+ "ANNUAL_TURNOVER,EXPTYPE,TENURE )  VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

		logger.debug(Literal.SQL + sql);

		stageNamedJdbcTemplate.getJdbcOperations().update(sql, ps -> {
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

		stageNamedJdbcTemplate.getJdbcOperations().update(sql, ps -> {
			int index = 1;
			ps.setString(index++, almExtract.getAlmReportType());
			ps.setDate(index++, JdbcUtil.getDate(almExtract.getAlmReportDate()));
			ps.setString(index++, almExtract.getAccountNumber());
			ps.setString(index++, almExtract.getAccrualBasis());
			ps.setBigDecimal(index++, almExtract.getAccruedInterest());

			ps.setString(index++, almExtract.getBankNumber());
			ps.setString(index++, almExtract.getBranch());
			ps.setLong(index++, almExtract.getCompFreq());
			ps.setLong(index++, almExtract.getCompFreqIncr());
			ps.setString(index++, almExtract.getCurrencyCode());
			ps.setBigDecimal(index++, almExtract.getCurrentBalance());

			ps.setDate(index++, JdbcUtil.getDate(almExtract.getDueDate()));
			ps.setLong(index++, almExtract.getInitPaymentFreq());
			ps.setBigDecimal(index++, almExtract.getInitRate());
			ps.setBigDecimal(index++, almExtract.getLifeCeiling());
			ps.setBigDecimal(index++, almExtract.getLifeFloor());
			ps.setString(index++, almExtract.getLoanType());
			ps.setDate(index++, JdbcUtil.getDate(almExtract.getMaturity()));

			ps.setBigDecimal(index++, almExtract.getOriginalBalance());
			ps.setInt(index++, almExtract.getOriginalTerm());
			ps.setDate(index++, JdbcUtil.getDate(almExtract.getOriginationDate()));
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

		stageNamedJdbcTemplate.getJdbcOperations().update(sql, ps -> {
			int index = 1;
			ps.setLong(index++, baselTwo.getAgreementId());
			ps.setLong(index++, baselTwo.getCustomerId());
			ps.setString(index++, baselTwo.getIcnStatus());
			ps.setDate(index++, JdbcUtil.getDate(baselTwo.getIcnAcquired()));
			ps.setDate(index++, JdbcUtil.getDate(baselTwo.getLoanApplDate()));
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
			ps.setDate(index++, JdbcUtil.getDate(baselTwo.getRcAcquired()));
			ps.setString(index++, baselTwo.getEmiType());
			ps.setString(index++, baselTwo.getRest());
			ps.setString(index++, baselTwo.getCustAccount());
			ps.setInt(index++, baselTwo.getNoBounces());
			ps.setBigDecimal(index++, baselTwo.getChargesPaid());
			ps.setDate(index++, JdbcUtil.getDate(baselTwo.getPrevNPA()));
			ps.setString(index++, baselTwo.getSecurtFlag());
			ps.setDate(index++, JdbcUtil.getDate(baselTwo.getSecurtDt()));
			ps.setString(index++, baselTwo.getDpdString());
			ps.setBigDecimal(index++, baselTwo.getTotAmtPaidMnthChq());
			ps.setBigDecimal(index++, baselTwo.getTotAmtPaidMnthCash());
			ps.setString(index++, baselTwo.getRescheduled());
			ps.setDate(index++, JdbcUtil.getDate(baselTwo.getRescheduleEffDt()));
			ps.setString(index++, baselTwo.getClosureType());
			ps.setBigDecimal(index++, baselTwo.getPrinLossClosure());
			ps.setDate(index++, JdbcUtil.getDate(baselTwo.getIbpcStart()));
			ps.setDate(index++, JdbcUtil.getDate(baselTwo.getIbpcEnd()));
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

		stageNamedJdbcTemplate.getJdbcOperations().update(sql, ps -> {
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
	public void truncateStageTable(String tableName) {
		stageNamedJdbcTemplate.getJdbcOperations().update("TRUNCATE TABLE " + tableName);
	}

	public void setExtDataSource(DataSource extDataSource) {
		this.extNamedJdbcTemplate = new NamedParameterJdbcTemplate(extDataSource);
	}

	public void setMainDataSource(DataSource mainDataSource) {
		this.mainNamedJdbcTemplate = new NamedParameterJdbcTemplate(mainDataSource);
	}

	public void setStagingDataSource(DataSource stagingDataSource) {
		this.stageNamedJdbcTemplate = new NamedParameterJdbcTemplate(stagingDataSource);
	}

}