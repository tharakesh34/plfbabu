/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */

/**
 ********************************************************************************************
 * FILE HEADER *
 ********************************************************************************************
 * * FileName : ALMExtarct.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 11-06-2015 * * Modified Date :
 * 11-06-2015 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 11-06-2015 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennanttech.pennapps.pff.external.alm;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicLong;

import javax.sql.DataSource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.transaction.TransactionStatus;

import com.pennant.app.core.AccrualService;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.ProjectedAccrual;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennanttech.dataengine.DatabaseDataEngine;
import com.pennanttech.dataengine.model.DataEngineStatus;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.external.ALMProcess;
import com.pennanttech.pff.model.external.alm.ALM;

public class ALMExtarct extends DatabaseDataEngine implements ALMProcess {
	public static DataEngineStatus EXTRACT_STATUS = new DataEngineStatus("ALM_REQUEST");

	private Date appDate;
	private MapSqlParameterSource paramMap = null;
	public long totalThreads;
	public AtomicLong completedThreads = null;
	public AtomicLong processedRecords;
	public AtomicLong successRecords;
	private int batchSize = 100000;

	public ALMExtarct(DataSource dataSource, long userId, Date valueDate, Date appDate) {
		super(dataSource, App.DATABASE.name(), userId, true, valueDate, EXTRACT_STATUS);
		this.totalThreads = 0;
		this.completedThreads = new AtomicLong(0L);
		this.processedRecords = new AtomicLong(0L);
		this.successRecords = new AtomicLong(0L);
		this.appDate = appDate;
	}

	@Override
	public void process(Object... objects) {
		try {
			process("ALM_REQUEST");
		} catch (Exception e) {
			throw new InterfaceException("CONTROL_DUMP_REQUEST", e.getMessage());
		}
	}

	@Override
	protected void processData() {
		logger.debug(Literal.ENTERING);

		paramMap = new MapSqlParameterSource();
		paramMap.addValue("FINISACTIVE", 1);
		paramMap.addValue("PAYMENTTYPE", "EMIINADV");
		paramMap.addValue("ACCRUEDON", appDate);
		paramMap.addValue("MATURITYDATE", DateUtil.getMonthStart(appDate));
		paramMap.addValue("ROWNUM", batchSize);

		try {

			// ALM Extraction configuration from SMT parameter
			String fromFinStartDate = SysParamUtil.getValueAsString(SMTParameterConstants.ALMEXTRACT_FROMFINSTARTDATE);
			if ("Y".equals(fromFinStartDate)) {
				deleteAll();
			} else {
				delete();
			}

			loadFinances();

			do {
				extractData();
			} while (totalRecords != processedCount);

			EXTRACT_STATUS.setStatus("S");

		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			EXTRACT_STATUS.setStatus("F");
		}

		logger.debug(Literal.LEAVING);
	}

	private void extractData() throws Exception {
		totalThreads = 0;
		completedThreads.set(0L);

		long threadCount = 5;
		long recordCount = batchSize;

		if (recordCount > totalRecords) {
			recordCount = totalRecords;
		}

		long batchSize = recordCount / threadCount;

		long fromSeq = 1;
		long toSeq = 0;

		for (long i = 1; i <= threadCount; i++) {
			if (i == threadCount) {
				toSeq = recordCount;
			} else {
				toSeq = toSeq + batchSize;
			}

			Thread thread = new Thread(new AccrualProcessThread(fromSeq, toSeq));
			thread.start();
			Thread.sleep(2000);
			totalThreads++;

			fromSeq = fromSeq + batchSize;
		}

		while (true) {
			if (totalThreads == completedThreads.get()) {
				processedCount = this.processedRecords.get();
				successCount = this.successRecords.get();
				executionStatus.setProcessedRecords(processedCount);
				executionStatus.setSuccessRecords(successCount);
				break;
			}
		}
	}

	/**
	 * Consider only active loans for alm extract
	 */
	private void loadFinances() throws SQLException {
		StringBuilder sql = new StringBuilder();

		jdbcTemplate.update("TRUNCATE TABLE ALM_EXTRACT");

		sql.append(" INSERT INTO ALM_EXTRACT");
		sql.append(" SELECT ROWNUM, FM.FINREFERENCE, FM.FINTYPE, FINSTARTDATE, MATURITYDATE,");
		sql.append(" CLOSINGSTATUS, CCYMINORCCYUNITS, CCYEDITFIELD, EMIADV,DD.ENTITYCODE");
		sql.append(" FROM FINANCEMAIN FM");
		sql.append(" INNER JOIN RMTCURRENCIES CCY ON CCY.CCYCODE = FM.FINCCY");
		sql.append(" INNER JOIN RMTFINANCETYPES FT ON FT.FINTYPE = FM.FINTYPE");
		sql.append(" INNER JOIN SMTDIVISIONDETAIL DD ON DD.DIVISIONCODE = FT.FINDIVISION");
		sql.append(" LEFT JOIN");
		sql.append(" (SELECT FINREFERENCE, COUNT(RH.REFERENCE) EMIADV FROM FINRECEIPTDETAIL RD");
		sql.append(" INNER JOIN FINRECEIPTHEADER RH ON RH.RECEIPTID=RH.RECEIPTID");
		sql.append(" INNER JOIN FINANCEMAIN L ON RH.REFERENCE = L.FINREFERENCE");
		sql.append(" WHERE PAYMENTTYPE=:PAYMENTTYPE AND FINISACTIVE=:FINISACTIVE");
		sql.append(" GROUP BY FINREFERENCE) T ON T.FINREFERENCE = FM.FINREFERENCE");
		sql.append(" WHERE FM.FINISACTIVE = :FINISACTIVE");
		sql.append(" AND MATURITYDATE >= :MATURITYDATE");

		totalRecords = parameterJdbcTemplate.update(sql.toString(), paramMap);
		executionStatus.setTotalRecords(totalRecords);
	}

	/**
	 * TRUNCATE the records from ALM table based on SMTParameter flag
	 */
	private void deleteAll() {
		jdbcTemplate.update("TRUNCATE TABLE ALM");
	}

	/**
	 * Delete the records from ALM table with accrued on greater than or equal to execution date to handle retry case
	 */
	private void delete() {
		delete(paramMap, "ALM", destinationJdbcTemplate, " WHERE ACCRUEDON >= :ACCRUEDON");
	}

	@SuppressWarnings("unused")
	private BigDecimal getAmount(BigDecimal amount, BigDecimal minorCcyUnits, int editField) {
		if (amount == null) {
			amount = BigDecimal.ZERO;
		}

		amount = amount.divide(minorCcyUnits, 0, RoundingMode.HALF_DOWN);

		return amount;
	}

	private void save(List<ALM> list) throws Exception {
		StringBuilder query = new StringBuilder();
		query.append(" INSERT INTO ALM VALUES(");
		query.append(" :AgreementId,");
		query.append(" :AgreementNo,");
		query.append(" :ProductFlag,");
		query.append(" :NpaStageId,");
		query.append(" :Installment,");
		query.append(" :PrinComp,");
		query.append(" :IntComp,");
		query.append(" :DueDate,");
		query.append(" :AccruedAmt,");
		query.append(" :AccruedOn,");
		query.append(" :CumulativeAccrualAmt,");
		query.append(" :AdvFlag,");
		query.append(" :EntityCode)");

		destinationJdbcTemplate.batchUpdate(query.toString(), SqlParameterSourceUtils.createBatch(list.toArray()));
	}

	@Override
	protected MapSqlParameterSource mapData(ResultSet rs) throws Exception {
		return null;
	}

	/**
	 * Return Map<String, ALM> Load the schedule details for all the loans between fromSeq and toSeq of ALM_EXTRACT
	 * table
	 * 
	 * @param fromSeq
	 * @param toSeq
	 * @return
	 */
	public Map<String, ALM> getFinSchdDetailsForALM(long fromSeq, long toSeq) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource();

		StringBuilder sql = new StringBuilder();
		sql.append(" Select FM.FINID, FM.FinReference, SchDate, SchSeq, PftOnSchDate, CpzOnSchDate, RepayOnSchDate,");
		sql.append(" RvwOnSchDate, BalanceForPftCal, CalculatedRate, NoOfDays, ProfitCalc, ProfitSchd, PrincipalSchd");
		sql.append(", RepayAmount, DisbAmount, DownPaymentAmount, CpzAmount, FSD.FeeChargeAmt, ");
		sql.append(" SchdPriPaid, SchdPftPaid, SchPftPaid, SchPriPaid, Specifier, AE.FinStartDate,");
		sql.append(" AE.MaturityDate, CcyMinorCcyUnits, CcyEditField, AE.EmiAdv, AE.ClosingStatus, AE.FinType,");
		sql.append(" CalRoundingMode,  RoundingTarget, AE.EntityCode");
		sql.append(" From ALM_EXTRACT AE");
		sql.append(" INNER JOIN FINANCEMAIN FM ON FM.FinReference = AE.FinReference");
		sql.append(" LEFT JOIN FinScheduleDetails FSD ON FSD.FinReference = AE.FinReference");
		sql.append(" where AE.SEQID between :FromSeq and :ToSeq order by SchDate asc");

		paramMap.addValue("FromSeq", fromSeq);
		paramMap.addValue("ToSeq", toSeq);

		Map<String, ALM> map = new HashMap<>();
		parameterJdbcTemplate.query(sql.toString(), paramMap, new RowCallbackHandler() {
			@Override
			public void processRow(ResultSet rs) throws SQLException {
				String finReference = rs.getString("FinReference");

				ALM alm = map.get(finReference);

				try {
					if (alm == null) {
						alm = new ALM();
						map.put(finReference, alm);

						alm.setAgreementNo(finReference);
						String agreementId = null;
						agreementId = StringUtils.substring(finReference, finReference.length() - 7,
								finReference.length());
						alm.setAgreementId(Long.parseLong(agreementId));

						alm.setProductFlag(rs.getString("FinType"));
						alm.setNpaStageId(rs.getString("ClosingStatus"));

						if ("W".equals(alm.getNpaStageId())) {
							alm.setNpaStageId("WRITEOFF");
						} else {
							alm.setNpaStageId("REGULAR");
						}

						Integer advanceEmi = rs.getInt("EmiAdv");

						if (advanceEmi != null && advanceEmi.intValue() > 0) {
							alm.setAdvFlag("Y");
						} else {
							alm.setAdvFlag("N");
						}
					}

					alm.setFinStartDate(rs.getDate("FinStartDate"));
					alm.setMaturityDate(rs.getDate("MaturityDate"));
					alm.setCcyMinorCcyUnits(rs.getBigDecimal("CcyMinorCcyUnits"));
					alm.setCcyEditField(rs.getInt("CcyEditField"));
					alm.setEntityCode(rs.getString("EntityCode"));
					// To handle Overdraft loans, since Overdraft loans may not have schedules
					if (rs.getDate("SchDate") != null) {
						ProjectedAccrual schedule = new ProjectedAccrual();
						schedule.setFinID(rs.getLong("FinID"));
						schedule.setFinReference(finReference);
						schedule.setSchdDate(rs.getDate("SchDate"));
						schedule.setSchSeq(rs.getInt("SchSeq"));
						schedule.setPftOnSchDate(rs.getBoolean("PftOnSchDate"));
						schedule.setCpzOnSchDate(rs.getBoolean("CpzOnSchDate"));
						schedule.setRepayOnSchDate(rs.getBoolean("RepayOnSchDate"));
						schedule.setRvwOnSchDate(rs.getBoolean("RvwOnSchDate"));
						schedule.setBalanceForPftCal(rs.getBigDecimal("BalanceForPftCal"));
						schedule.setCalculatedRate(rs.getBigDecimal("CalculatedRate"));
						schedule.setNoOfDays(rs.getInt("NoOfDays"));
						schedule.setProfitCalc(rs.getBigDecimal("ProfitCalc"));
						schedule.setProfitSchd(rs.getBigDecimal("ProfitSchd"));
						schedule.setPrincipalSchd(rs.getBigDecimal("PrincipalSchd"));
						schedule.setSchdTot(rs.getBigDecimal("RepayAmount"));
						schedule.setDisbAmount(rs.getBigDecimal("DisbAmount"));
						schedule.setDownPaymentAmount(rs.getBigDecimal("DownPaymentAmount"));
						schedule.setCpzAmount(rs.getBigDecimal("CpzAmount"));
						schedule.setFeeChargeAmt(rs.getBigDecimal("FeeChargeAmt"));
						schedule.setSchdPriPaid(rs.getBigDecimal("SchdPriPaid"));
						schedule.setSchdPftPaid(rs.getBigDecimal("SchdPftPaid"));
						schedule.setSchPftPaid(rs.getBoolean("SchPftPaid"));
						schedule.setSchPriPaid(rs.getBoolean("SchPriPaid"));
						schedule.setSpecifier(rs.getString("Specifier"));
						schedule.setFinStartDate(rs.getDate("FinStartDate"));
						schedule.setMaturityDate(rs.getDate("MaturityDate"));
						schedule.setCcyMinorCcyUnits(rs.getBigDecimal("CcyMinorCcyUnits"));
						schedule.setCcyEditField((rs.getInt("CcyEditField")));

						alm.getAccrualList().add(schedule);
					}

					alm.setCalRoundingMode(rs.getString("CalRoundingMode"));
					alm.setRoundingTarget(rs.getInt("RoundingTarget"));
				} catch (Exception e) {
					saveBatchLog(finReference, "F", e.getMessage());
					executionStatus.setFailedRecords(failedCount++);
				}
			}
		});
		return map;
	}

	public class AccrualProcessThread implements Runnable {
		long fromSeq = 1;
		long toSeq = 0;

		public AccrualProcessThread(long fromSeq, long toSeq) {
			this.fromSeq = fromSeq;
			this.toSeq = toSeq;
		}

		@Override
		public void run() {
			logger.debug("Entering thread :");

			// ALM Extraction configuration from SMT parameter
			String fromFinStartDate = SysParamUtil.getValueAsString(SMTParameterConstants.ALMEXTRACT_FROMFINSTARTDATE);

			// extract Schedule details
			Map<String, ALM> map = getFinSchdDetailsForALM(fromSeq, toSeq);

			TransactionStatus txnStatus = null;

			for (Entry<String, ALM> key : map.entrySet()) {
				ALM alm = key.getValue();
				List<ProjectedAccrual> accrualList = null;
				executionStatus.setProcessedRecords(processedRecords.getAndIncrement());
				try {

					if (!alm.getAccrualList().isEmpty()) {
						accrualList = calculateAccrualsOnMonthEnd(alm, appDate, fromFinStartDate);
					}

					// Due to invalid FinReference Number product flag will not set
					if (CollectionUtils.isNotEmpty(accrualList) && alm.getProductFlag() != null) {
						txnStatus = transManager.getTransaction(transDef);
						saveAccruals(alm, accrualList);
						transManager.commit(txnStatus);
					}

				} catch (Exception e) {
					if (txnStatus != null && !txnStatus.isCompleted()) {
						transManager.rollback(txnStatus);
					}

					executionStatus.setFailedRecords(failedCount++);
					String keyId = alm.getAgreementNo();

					if (StringUtils.trimToNull(keyId) == null) {
						keyId = String.valueOf(processedCount);
					}

					saveBatchLog(keyId, "F", e.getMessage());

				}
				executionStatus.setSuccessRecords(successRecords.getAndIncrement());
			}
			completedThreads.getAndIncrement();
		}

		private void saveAccruals(ALM object, List<ProjectedAccrual> accrualList) throws Exception {
			ALM alm = null;
			List<ALM> list = null;
			for (ProjectedAccrual item : accrualList) {
				alm = new ALM();
				list = new ArrayList<>();

				alm.setAgreementNo(object.getAgreementNo());
				alm.setAgreementId(object.getAgreementId());
				alm.setProductFlag(object.getProductFlag());
				alm.setNpaStageId(object.getNpaStageId());
				alm.setAdvFlag(object.getAdvFlag());
				alm.setFinStartDate(object.getFinStartDate());
				alm.setMaturityDate(object.getMaturityDate());

				alm.setInstallment(item.getSchdTot().divide(object.getCcyMinorCcyUnits(), object.getCcyEditField(),
						RoundingMode.HALF_DOWN));
				alm.setPrinComp(item.getSchdPri().divide(object.getCcyMinorCcyUnits(), object.getCcyEditField(),
						RoundingMode.HALF_DOWN));
				alm.setIntComp(item.getSchdPft().divide(object.getCcyMinorCcyUnits(), object.getCcyEditField(),
						RoundingMode.HALF_DOWN));
				alm.setAccruedAmt(item.getPftAccrued().divide(object.getCcyMinorCcyUnits(), object.getCcyEditField(),
						RoundingMode.HALF_DOWN));
				alm.setCumulativeAccrualAmt(item.getCumulativeAccrued().divide(object.getCcyMinorCcyUnits(),
						object.getCcyEditField(), RoundingMode.HALF_DOWN));

				alm.setDueDate(item.getSchdDate());
				alm.setAccruedOn(item.getAccruedOn());
				alm.setEntityCode(object.getEntityCode());
				list.add(alm);

				if (!list.isEmpty()) {
					try {
						save(list);
					} catch (Exception e) {
						logger.error(Literal.EXCEPTION, e);
						throw e;
					}

					list = null;
				}
			}
		}
	}

	private List<ProjectedAccrual> calculateAccrualsOnMonthEnd(ALM alm, Date appDate, String fromFinStartDate)
			throws Exception {
		List<FinanceScheduleDetail> schList = new ArrayList<FinanceScheduleDetail>(alm.getAccrualList().size());

		FinanceMain finMain = new FinanceMain();
		finMain.setFinReference(alm.getAgreementNo());
		finMain.setFinStartDate(alm.getFinStartDate());
		finMain.setMaturityDate(alm.getMaturityDate());
		finMain.setRoundingTarget(alm.getRoundingTarget());
		finMain.setCalRoundingMode(alm.getCalRoundingMode());

		for (ProjectedAccrual item : alm.getAccrualList()) {
			FinanceScheduleDetail schedule = new FinanceScheduleDetail();

			schedule.setFinID(item.getFinID());
			schedule.setFinReference(item.getFinReference());
			schedule.setSchDate(item.getSchdDate());
			schedule.setSchSeq(item.getSchSeq());
			schedule.setPftOnSchDate(item.isPftOnSchDate());
			schedule.setCpzOnSchDate(item.isCpzOnSchDate());
			schedule.setRepayOnSchDate(item.isRepayOnSchDate());
			schedule.setRvwOnSchDate(item.isRvwOnSchDate());
			schedule.setBalanceForPftCal(item.getBalanceForPftCal());
			schedule.setCalculatedRate(item.getCalculatedRate());
			schedule.setNoOfDays(item.getNoOfDays());
			schedule.setProfitCalc(item.getProfitCalc());
			schedule.setProfitSchd(item.getProfitSchd());
			schedule.setPrincipalSchd(item.getPrincipalSchd());
			schedule.setRepayAmount(item.getSchdTot());
			schedule.setDisbAmount(item.getDisbAmount());
			schedule.setDownPaymentAmount(item.getDownPaymentAmount());
			schedule.setCpzAmount(item.getCpzAmount());
			schedule.setFeeChargeAmt(item.getFeeChargeAmt());
			schedule.setSchdPriPaid(item.getSchdPriPaid());
			schedule.setSchdPftPaid(item.getSchdPftPaid());
			schedule.setSchPftPaid(item.isSchPftPaid());
			schedule.setSchPriPaid(item.isSchPriPaid());
			schedule.setSpecifier(item.getSpecifier());
			schList.add(schedule);

		}

		return AccrualService.getAccrualsFromCurMonthEnd(finMain, schList, appDate, fromFinStartDate);
	}
}
