package com.pennanttech.pff.external.presentment;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.RepayConstants;
import com.pennanttech.dataengine.DataEngineExport;
import com.pennanttech.model.presentment.Presentment;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.InterfaceConstants;
import com.pennanttech.pff.external.AbstractInterface;
import com.pennanttech.pff.external.PresentmentRequest;

public class DefaultPresentmentRequest extends AbstractInterface implements PresentmentRequest {
	private static final Logger logger = Logger.getLogger(DefaultPresentmentRequest.class);

	private static final String STATUS = "STATUS";

	@Override
	public void sendReqest(List<Long> idList, List<Long> idExcludeEmiList, long presentmentId, boolean isError,
			boolean isPDC, String presentmentRef, String bankAccNo) throws Exception {
		logger.debug(Literal.ENTERING);

		boolean isBatchFail = false;
		StringBuilder sql = null;

		if (idList != null && !idList.isEmpty()) {

			if (isPDC) {
				sql = getPDCSqlQuery();
			} else {
				sql = getSqlQuery();
			}

			MapSqlParameterSource paramMap = new MapSqlParameterSource();
			paramMap.addValue("PRESENTMENTID", presentmentId);
			paramMap.addValue("EXCLUDEREASON", RepayConstants.PEXC_EMIINCLUDE);
			paramMap.addValue(STATUS, RepayConstants.PEXC_APPROV);

			List<Presentment> presements = namedJdbcTemplate.query(sql.toString(), paramMap,
					new PresentmentRowMapper());

			// Begin Transaction
			namedJdbcTemplate.update("TRUNCATE TABLE PRESENTMENT_REQ_DETAILS_TEMP", new MapSqlParameterSource());
			int successCount = 0;
			int processedCount = 0;
			try {
				for (Presentment presement : presements) {
					processedCount++;
					save(presement, "PRESENTMENT_REQ_DETAILS_TEMP");
					successCount++;
				}
				// Commit Transaction
			} catch (Exception e) {
				logger.debug(Literal.EXCEPTION, e);
				isBatchFail = true;

				// Roolback
			} finally {
				if (isBatchFail) {
					clearTables();
				} else {
					copyDataFromTempToMainTables(presentmentId, successCount);
					if (isError) {
						updatePresentmentHeader(presentmentId, 3, presentmentId, processedCount);
					} else {
						updatePresentmentHeader(presentmentId, 4, presentmentId, processedCount);
					}
					updatePresentmentDetails(idList, "A", RepayConstants.PEXC_EMIINCLUDE);

					String alwPresentmentDwnld = SysParamUtil
							.getValueAsString(InterfaceConstants.ALLOW_PRESENTMENT_DOWNLOAD);
					if ("Y".equalsIgnoreCase(alwPresentmentDwnld)) {
						prepareRequestFile(presentmentId, presentmentRef, bankAccNo);
					}
				}
			}
		}

		if (!isBatchFail && idExcludeEmiList != null && !idExcludeEmiList.isEmpty()) {
			updatePresentmentDetails(idExcludeEmiList, "A", RepayConstants.PEXC_EMIINADVANCE);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for creating PRESENTMENT_REQUEST file.
	 * 
	 * @param presentmentId2
	 * 
	 * @throws Exception
	 */
	public void prepareRequestFile(long presentmentId, String presentmentRef, String bankAccNo) throws Exception {
		logger.debug(Literal.ENTERING);

		try {
			String paymentMode = getPaymenyMode(presentmentId);
			String paymentModeConfigName = "PRESENTMENT_REQUEST_";
			paymentModeConfigName = paymentModeConfigName.concat(paymentMode);
			String smtPaymentModeConfig = SysParamUtil.getValueAsString(paymentModeConfigName);

			DataEngineExport dataEngine = null;
			dataEngine = new DataEngineExport(dataSource, 1000, App.DATABASE.name(), true,
					SysParamUtil.getAppValueDate());
			Map<String, Object> filterMap = new HashMap<>();
			filterMap.put("JOB_ID", presentmentId);
			dataEngine.setFilterMap(filterMap);
			Map<String, Object> parameterMap = new HashMap<>();
			parameterMap.put("ddMMyy", DateUtil.getSysDate("ddMMyy"));
			parameterMap.put("DepositeDate", DateUtil.format(getScheduleDate(presentmentId), "dd-MMM-yy"));
			parameterMap.put("despositslipid", presentmentRef);
			parameterMap.put("clientCode", "VEHCLIX162");
			parameterMap.put("AccountNo", bankAccNo);
			// for new Presentment only total count needs
			if (smtPaymentModeConfig != null && smtPaymentModeConfig.equals("PRESENTMENT_REQUEST_PDC")) {
				parameterMap.put("ChequeamountSum", getSumOfChequeAmt());
			}
			dataEngine.setParameterMap(parameterMap);

			if (smtPaymentModeConfig != null) {
				dataEngine.exportData(smtPaymentModeConfig);
			} else {
				dataEngine.exportData("PRESENTMENT_REQUEST");
			}
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			throw new AppException(e.getMessage());
		}
		logger.debug(Literal.LEAVING);
	}

	private StringBuilder getSqlQuery() {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT  T2.FINBRANCH, T1.FINREFERENCE, T4.MICR, T3.ACCTYPE, T1.SCHDATE, ");
		sql.append(" T3.ACCNUMBER, T5.CUSTSHRTNAME,T5.CUSTCOREBANK,T3.ACCHOLDERNAME, T6.BANKCODE, ");
		sql.append(" T6.BANKNAME, T1.PRESENTMENTID, T1.PRESENTMENTAMT,");
		sql.append(" T0.PRESENTMENTDATE, T3.MANDATEREF, T4.IFSC, ");
		sql.append(" T7.PARTNERBANKCODE, T7.UTILITYCODE, T3.STARTDATE, T3.EXPIRYDATE, T3.MANDATETYPE, ");
		sql.append(" T2.FINTYPE, T2.CUSTID , T1.EMINO, T4.BRANCHDESC, T4.BRANCHCODE, T1.ID, T1.PresentmentRef, ");
		sql.append(" T8.BRANCHSWIFTBRNCDE, T11.ENTITYCODE, T10.CCYMINORCCYUNITS, ");
		sql.append(" T7.PARTNERBANKNAME, NULL CHEQUESERIALNO, NULL CHEQUEDATE ");
		sql.append(" FROM PRESENTMENTHEADER T0 ");
		sql.append(" INNER JOIN PRESENTMENTDETAILS T1 ON T0.ID = T1.PRESENTMENTID ");
		sql.append(" INNER JOIN FINANCEMAIN T2 ON T1.FINREFERENCE = T2.FINREFERENCE ");
		sql.append(" INNER JOIN CuSTOMERS T5 ON T5.CUSTID = T2.CUSTID ");
		sql.append(" INNER JOIN MANDATES T3 ON T2.MANDATEID = T3.MANDATEID ");
		sql.append(" INNER JOIN BANKBRANCHES T4 ON T3.BANKBRANCHID = T4.BANKBRANCHID ");
		sql.append(" INNER JOIN BMTBANKDETAIL T6 ON T4.BANKCODE = T6.BANKCODE ");
		sql.append(" INNER JOIN PARTNERBANKS T7 ON T7.PARTNERBANKID = T0.PARTNERBANKID ");
		sql.append(" INNER JOIN RMTBRANCHES T8 ON T8.BRANCHCODE = T2.FINBRANCH ");
		sql.append(" INNER JOIN RMTFINANCETYPES T9 ON T9.FINTYPE = T2.FINTYPE");
		sql.append(" INNER JOIN RMTCURRENCIES T10 ON T10.CCYCODE = T2.FINCCY");
		sql.append(" INNER JOIN SMTDIVISIONDETAIL T11 ON T11.DIVISIONCODE=T9.FINDIVISION");
		sql.append(" WHERE T1.PRESENTMENTID = :PRESENTMENTID");
		sql.append(" AND T1.EXCLUDEREASON = :EXCLUDEREASON AND T1.STATUS <> :STATUS ");
		return sql;
	}

	private StringBuilder getPDCSqlQuery() {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT  T2.FINBRANCH, T1.FINREFERENCE, T4.MICR, T1.SCHDATE, ");
		sql.append(" T3.ACCOUNTTYPE ACCTYPE, T3.ACCOUNTNO ACCNUMBER, T3.ACCHOLDERNAME, T3.CHEQUESERIALNO MANDATEREF,");
		sql.append(" 'PDC' MANDATETYPE, T5.CUSTSHRTNAME,T5.CUSTCOREBANK, T6.BANKCODE, ");
		sql.append(" T6.BANKNAME, T1.PRESENTMENTID, T1.PRESENTMENTAMT,");
		sql.append(" T0.PRESENTMENTDATE, T4.IFSC, ");
		sql.append(" T7.PARTNERBANKCODE, NULL PARTNERBANKNAME, T7.UTILITYCODE, ");
		sql.append(" T2.FINTYPE, T2.CUSTID , T1.EMINO, T4.BRANCHDESC, T4.BRANCHCODE, T1.ID, T1.PresentmentRef, ");
		sql.append(" T8.BRANCHSWIFTBRNCDE, T11.ENTITYCODE, T10.CCYMINORCCYUNITS, ");
		sql.append(" T3.ChequeSerialNo, T3.ChequeDate ");
		sql.append(" FROM PRESENTMENTHEADER T0 ");
		sql.append(" INNER JOIN PRESENTMENTDETAILS T1 ON T0.ID = T1.PRESENTMENTID ");
		sql.append(" INNER JOIN FINANCEMAIN T2 ON T1.FINREFERENCE = T2.FINREFERENCE ");
		sql.append(" INNER JOIN CuSTOMERS T5 ON T5.CUSTID = T2.CUSTID ");
		sql.append(" INNER JOIN CHEQUEDETAIL T3 ON T3.CHEQUEDETAILSID = T1.MANDATEID ");
		sql.append(" INNER JOIN BANKBRANCHES T4 ON T3.BANKBRANCHID = T4.BANKBRANCHID ");
		sql.append(" INNER JOIN BMTBANKDETAIL T6 ON T4.BANKCODE = T6.BANKCODE ");
		sql.append(" INNER JOIN PARTNERBANKS T7 ON T7.PARTNERBANKID = T0.PARTNERBANKID ");
		sql.append(" INNER JOIN RMTBRANCHES T8 ON T8.BRANCHCODE = T2.FINBRANCH ");
		sql.append(" INNER JOIN RMTFINANCETYPES T9 ON T9.FINTYPE = T2.FINTYPE");
		sql.append(" INNER JOIN RMTCURRENCIES T10 ON T10.CCYCODE = T2.FINCCY");
		sql.append(" INNER JOIN SMTDIVISIONDETAIL T11 ON T11.DIVISIONCODE=T9.FINDIVISION");
		sql.append(
				" WHERE T1.PRESENTMENTID = :PRESENTMENTID AND T1.EXCLUDEREASON = :EXCLUDEREASON AND T1.STATUS <> :STATUS ");
		return sql;
	}

	public class PresentmentRowMapper implements RowMapper<Presentment> {
		List<Presentment> presements = new ArrayList<>();

		@Override
		public Presentment mapRow(ResultSet rs, int rowNum) throws SQLException {
			Presentment presement = new Presentment();

			// Payment Mode Data
			presement.setAccType(Long.valueOf(rs.getString("ACCTYPE")));
			presement.setAccountNo(rs.getString("ACCNUMBER"));
			presement.setUmrnNo(rs.getString("MANDATEREF"));

			presement.setBrCode(rs.getString("BRANCHSWIFTBRNCDE"));
			presement.setAgreementNo(rs.getString("FINREFERENCE"));
			presement.setMicrCode(rs.getString("MICR"));
			presement.setDestAccHolder(rs.getString("CUSTSHRTNAME"));
			presement.setBankName(rs.getString("BANKNAME"));

			String entity = rs.getString("ENTITYCODE");
			if (StringUtils.isNumeric(entity)) {
				presement.setEntityCode(Long.valueOf(entity));
			}

			if (StringUtils.equals(null, StringUtils.trimToNull(rs.getString("BRANCHDESC")))) {
				presement.setBankAddress(rs.getString("BANKNAME"));
			} else {
				presement.setBankAddress(rs.getString("BRANCHDESC"));
			}
			presement.setEmiNo(rs.getLong("EMINO"));
			presement.setBatchId(rs.getString("PresentmentRef"));

			// Presentment amount convertion using currency minor units..
			BigDecimal presentAmt = rs.getBigDecimal("PRESENTMENTAMT");
			int ccyMinorUnits = rs.getInt("CCYMINORCCYUNITS");
			BigDecimal checqueAmt = presentAmt.divide(new BigDecimal(ccyMinorUnits));

			presement.setChequeAmount(checqueAmt);
			presement.setPresentationDate(rs.getDate("PRESENTMENTDATE"));

			String mandateType = rs.getString("MANDATETYPE");
			if (StringUtils.equals(mandateType, "ECS")) {
				presement.setInstrumentMode("E");
			} else if (StringUtils.equals(mandateType, "DDM")) {
				presement.setInstrumentMode("A");
			} else if (StringUtils.equals(mandateType, "NACH")) {
				presement.setInstrumentMode("Z");
			} else if (StringUtils.equals(mandateType, "PDC")) {
				presement.setInstrumentMode("P");
			}

			presement.setProductCode(rs.getString("FINTYPE"));

			presement.setDataGenDate(new Timestamp(System.currentTimeMillis()));
			presement.setUserID(String.valueOf(1000));
			presement.setJobId(rs.getLong("PRESENTMENTID"));

			String micr = rs.getString("MICR");
			if (StringUtils.trimToNull(micr) != null && micr.length() >= 6) {
				String bankCode = micr.substring(3, 6);
				presement.setBankCode(bankCode);
			}
			presement.setTxnReference(rs.getLong("ID"));

			String string = rs.getString("CUSTCOREBANK");
			if (StringUtils.isNotBlank(string) && StringUtils.isNumeric(string)) {
				presement.setCustomerId(Long.valueOf(string));
			}
			presement.setCycleDate(rs.getDate("SCHDATE"));
			presement.setPartnerBankName(rs.getString("PARTNERBANKNAME"));
			presement.setIFSC(rs.getString("IFSC"));
			presement.setChequeSerialNo(rs.getString("CHEQUESERIALNO"));
			if (rs.getString("CHEQUEDATE") != null) {
				presement.setChequeDate(rs.getDate("CHEQUEDATE"));
			}

			return presement;
		}
	}

	private void save(Presentment presentment, String tableName) {
		StringBuilder sql = new StringBuilder("insert into ");
		sql.append(tableName);
		sql.append(" (TXN_REF, Entity_Code, CYCLE_TYPE, INSTRUMENT_MODE,PRESENTATIONDATE,BANK_CODE,PRODUCT_CODE,");
		sql.append(" CustomerId, AGREEMENTNO, CHEQUEAMOUNT, EMI_NO, TXN_TYPE_CODE, SOURCE_CODE, BR_CODE,");
		sql.append(" UMRN_NO , BANK_NAME, MICR_CODE, AccountNo, DEST_ACC_HOLDER, ACC_TYPE, BANK_ADDRESS, RESUB_FLAG,");
		sql.append(
				" ORGIN_SYSTEM, DATA_GEN_DATE ,USERID, BATCHID,job_Id ,PICKUP_BATCHID, CycleDate,PARTNER_BANK,IFSC,");
		sql.append(" ChequeSerialNo, ChequeDate) ");
		sql.append(" values( :TxnReference,");

		if (presentment.getEntityCode() == 0) {
			sql.append(" null,");
		} else {
			sql.append(" :EntityCode,");
		}

		sql.append(" :CycleType, :InstrumentMode, :PresentationDate, :BankCode, :ProductCode,");
		sql.append(" :CustomerId, :AgreementNo, :ChequeAmount, :EmiNo, :TxnTypeCode, :SourceCode, :BrCode,");
		sql.append(" :UmrnNo , :BankName, :MicrCode, :AccountNo, :DestAccHolder, :AccType, :BankAddress, :ResubFlag,");
		sql.append(
				" :OrginSystem, :DataGenDate , :UserID, :BatchId, :JobId , :PickupBatchId, :CycleDate, :partnerBankName, :IFSC,");
		sql.append(" :ChequeSerialNo, :ChequeDate)");

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(presentment);
		try {
			namedJdbcTemplate.update(sql.toString(), paramSource);
		} catch (DuplicateKeyException e) {
			logger.error(Literal.EXCEPTION, e);
			throw new ConcurrencyException(e);
		}
	}

	private void clearTables() {
		logger.debug(Literal.ENTERING);

		namedJdbcTemplate.update("TRUNCATE TABLE PRESENTMENT_REQ_DETAILS_TEMP", new MapSqlParameterSource());

		logger.debug(Literal.LEAVING);
	}

	private void copyDataFromTempToMainTables(long presentmentId, int successCount) {
		logger.debug(Literal.ENTERING);

		saveHeader(presentmentId, successCount);

		namedJdbcTemplate.update("INSERT INTO PRESENTMENT_REQ_DETAILS SELECT * FROM PRESENTMENT_REQ_DETAILS_TEMP",
				new MapSqlParameterSource());

		updateHeader(presentmentId, successCount);

		logger.debug(Literal.LEAVING);
	}

	private void updateHeader(long presentmentId, int successCount) {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource parmMap = null;

		parmMap = new MapSqlParameterSource();
		parmMap.addValue("Job_Id", presentmentId);
		parmMap.addValue("Data_trnsfr_status", "C");
		parmMap.addValue("Total_Cnt", successCount);

		// Prepare the SQL, ensure primary key will not be updated.
		StringBuilder sql = new StringBuilder("update PRESENTMENT_REQ_HEADER");
		sql.append(" set Total_Cnt = :Total_Cnt ,Data_trnsfr_status = :Data_trnsfr_status ");
		sql.append(" where Job_Id = :Job_Id");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		int recordCount = namedJdbcTemplate.update(sql.toString(), parmMap);

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);

	}

	private void saveHeader(long presentmentId, int successCount) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		MapSqlParameterSource parmMap;

		parmMap = new MapSqlParameterSource();
		parmMap.addValue("ID", presentmentId);

		sql.append(" SELECT  ID, SCHDATE, MANDATETYPE ");
		sql.append(" FROM PRESENTMENTHEADER  ");
		sql.append(" WHERE ID = :ID");

		namedJdbcTemplate.query(sql.toString(), parmMap, new ResultSetExtractor<Long>() {
			@Override
			public Long extractData(ResultSet rs) throws SQLException {
				logger.debug("Entering");
				while (rs.next()) {
					try {
						Presentment response = mapControlTableDate(rs, successCount);
						saveToControlTable(response, "PRESENTMENT_REQ_HEADER");
					} catch (Exception e) {
						logger.error(Literal.EXCEPTION, e);
						throw e;
					}
				}
				return presentmentId;
			}
		});
		logger.debug(Literal.LEAVING);
	}

	private void saveToControlTable(Presentment presentmentResponse, String tableName) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("INSERT INTO ");
		sql.append(tableName);
		sql.append(" (ENTITY, Instrument_Type, Job_Id, Cycle_Date,Data_gen_Status,Bank_Report_Cnt,Total_Cnt,");
		sql.append(" Data_trnsfr_status, Data_trnsfr_jobid, Start_Date, End_Date, ERROR_MSG)");
		sql.append(
				" values( :EntityCode, :InstrumentMode, :JobId, :CycleDate, :DataGenStatus, :bankReportCnt, :stagingTableCnt,");
		sql.append(" :dataTrnsfrStatus, :dataTrnsfrJobid, :startDate, :endDate,:errorMsg)");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(presentmentResponse);
		try {
			namedJdbcTemplate.update(sql.toString(), paramSource);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}
		logger.debug("Leaving");
	}

	private Presentment mapControlTableDate(ResultSet rs, int successCount) throws SQLException {
		Presentment response = new Presentment();

		String mnadteType = rs.getString("MANDATETYPE");

		if (StringUtils.equals(mnadteType, "ECS")) {
			response.setInstrumentMode("E");
		} else if (StringUtils.equals(mnadteType, "DDM")) {
			response.setInstrumentMode("A");
		} else if (StringUtils.equals(mnadteType, "NACH")) {
			response.setInstrumentMode("Z");
		}

		response.setJobId(rs.getLong("ID"));
		response.setCycleDate(rs.getDate("schdate"));
		response.setBankReportCnt(successCount);
		response.setStagingTableCnt(0);
		response.setDataTrnsfrStatus("P");

		return response;
	}

	private void updatePresentmentDetails(List<Long> idList, String status, int excludeReason) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = null;

		sql = new StringBuilder();

		sql.append(
				" UPDATE PRESENTMENTDETAILS Set STATUS = :STATUS,  ErrorDesc = :ErrorDesc Where ID =:ID AND EXCLUDEREASON = :EXCLUDEREASON ");
		logger.trace(Literal.SQL + sql.toString());

		//Fix related Bulk-Processing in case of list having huge records IN operator is not working.
		//So instead on IN Operator using BatchUpdate.
		List<MapSqlParameterSource> sources = new ArrayList<>();
		for (Long id : idList) {
			MapSqlParameterSource batchValues = new MapSqlParameterSource();
			batchValues.addValue(STATUS, status);
			batchValues.addValue("ErrorDesc", null);
			batchValues.addValue("EXCLUDEREASON", excludeReason);
			batchValues.addValue("ID", id);
			sources.add(batchValues);
			SqlParameterSource[] batchArgs = sources.toArray(new SqlParameterSource[sources.size()]);
			if (sources.size() == PennantConstants.BULKPROCESSING_SIZE) {
				this.namedJdbcTemplate.batchUpdate(sql.toString(), batchArgs);
				sources.clear();
			}
		}
		if (!sources.isEmpty()) {
			SqlParameterSource[] batchArgs = sources.toArray(new SqlParameterSource[sources.size()]);
			this.namedJdbcTemplate.batchUpdate(sql.toString(), batchArgs);
			sources.clear();
		}
		logger.debug(Literal.LEAVING);
	}

	private void updatePresentmentHeader(long presentmentId, int manualEcclude, long dBStatusId, long totalRecords) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = null;
		MapSqlParameterSource source = null;

		sql = new StringBuilder();
		sql.append(
				" UPDATE PRESENTMENTHEADER Set STATUS = :STATUS, DBSTATUSID = :DBSTATUSID, TOTALRECORDS = TOTALRECORDS+:TOTALRECORDS  Where ID = :ID ");
		logger.trace(Literal.SQL + sql.toString());

		source = new MapSqlParameterSource();
		source.addValue(STATUS, manualEcclude);
		source.addValue("DBSTATUSID", dBStatusId);
		source.addValue("ID", presentmentId);
		source.addValue("TOTALRECORDS", totalRecords);

		try {
			this.namedJdbcTemplate.update(sql.toString(), source);
		} catch (Exception e) {
			logger.error("Exception :", e);
			throw e;
		}
		logger.debug(Literal.LEAVING);
	}

	private String getPaymenyMode(long presentmentId) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		MapSqlParameterSource parmMap;

		parmMap = new MapSqlParameterSource();
		parmMap.addValue("ID", presentmentId);

		sql.append(" SELECT MANDATETYPE ");
		sql.append(" FROM PRESENTMENTHEADER  ");
		sql.append(" WHERE ID = :ID");
		logger.debug("selectSql: " + sql.toString());

		try {
			return this.namedJdbcTemplate.queryForObject(sql.toString(), parmMap, String.class);
		} catch (EmptyResultDataAccessException e) {
			logger.info(e);
		}
		logger.debug(Literal.LEAVING);
		return null;
	}

	// For Presentment PDC total amount
	private String getSumOfChequeAmt() {
		BigDecimal amount = BigDecimal.ZERO;
		StringBuilder sql = new StringBuilder();
		sql.append(" Select coalesce(sum(Chequeamount), 0)");
		sql.append(" from PRESENTMENT_REQ_DETAILS_VIEW");

		try {
			amount = this.jdbcTemplate.queryForObject(sql.toString(), BigDecimal.class);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Literal.EXCEPTION, e);
			return amount.toString();
		}

		return amount.toString();
	}

	//For Presentment schedule date populated in PDC Download
	private Date getScheduleDate(long presentmentId) {
		Date schDate = null;
		StringBuilder sql = new StringBuilder();
		sql.append(" Select Distinct SchDate from PRESENTMENTDETAILS Where PresentmentId = :presentmentId");
		MapSqlParameterSource parmMap;

		parmMap = new MapSqlParameterSource();
		parmMap.addValue("presentmentId", presentmentId);
		try {
			schDate = this.namedJdbcTemplate.queryForObject(sql.toString(), parmMap, Date.class);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Literal.EXCEPTION, e);
			return schDate;
		}

		return DateUtil.getSqlDate(schDate);
	}

}
