package com.pennant.backend.dao.insurance.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.pennant.backend.dao.insurance.InsuranceDetailDAO;
import com.pennant.backend.model.insurance.InsuranceDetails;
import com.pennant.backend.model.insurance.InsurancePaymentInstructions;
import com.pennant.backend.util.InsuranceConstants;
import com.pennant.backend.util.VASConsatnts;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

public class InsuranceDetailDAOImpl extends SequenceDao<InsuranceDetails> implements InsuranceDetailDAO {
	private static Logger logger = LogManager.getLogger(InsuranceDetailDAOImpl.class);

	public InsuranceDetailDAOImpl() {
		super();
	}

	@Override
	public InsuranceDetails getInsurenceDetailsByRef(String reference, String tableType) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();

		sql.append(
				"Select Id ,Reference ,FinReference ,StartDate ,EndDate ,Term ,CoverageAmount ,PolicyNumber ,IssuanceDate ,IssuanceStatus ,PartnerPremium ,");
		sql.append(
				"PartnerReceivedDate ,AWBN`o1 ,AWBNo2 ,AWBNo3 ,DispatchStatus1 ,DispatchStatus2 ,DispatchStatus3 ,ReasonOfRTO1 ,ReasonOfRTO2 ,ReasonOfRTO3");
		sql.append(
				",DispatchDateAttempt1 ,DispatchDateAttempt2 ,DispatchDateAttempt3 ,MedicalStatus ,PendencyReasonCategory ,PendencyReason ,InsPendencyResReq");
		sql.append(
				",FPR ,PolicyStatus ,FormHandoverDate ,NomineeName ,NomineeRelation ,VASProviderId, ReconStatus, TolaranceAmount, ManualReconRemarks, ManualReconResCategory, LinkedTranId ");
		if (tableType.contains("View")) {
			sql.append(",FinType, PostingAgainst, InsurancePremium, FreeLockPeriod ");
		}
		sql.append(" ,Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId,");
		sql.append(" RecordType, WorkflowId From InsuranceDetails");
		sql.append(StringUtils.trimToEmpty(tableType));
		sql.append(" Where Reference = :Reference");
		logger.debug("selectSql: " + sql.toString());

		RowMapper<InsuranceDetails> typeRowMapper = BeanPropertyRowMapper.newInstance(InsuranceDetails.class);

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("Reference", reference);

		try {
			return this.jdbcTemplate.queryForObject(sql.toString(), source, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);
		return null;
	}

	@Override
	public InsuranceDetails getInsurenceDetailsById(long id, String tableType) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();

		sql.append(
				"Select Id ,Reference ,FinReference ,StartDate ,EndDate ,Term ,CoverageAmount ,PolicyNumber ,IssuanceDate ,IssuanceStatus ,PartnerPremium ,");
		sql.append(
				"PartnerReceivedDate ,AWBNo1 ,AWBNo2 ,AWBNo3 ,DispatchStatus1 ,DispatchStatus2 ,DispatchStatus3 ,ReasonOfRTO1 ,ReasonOfRTO2 ,ReasonOfRTO3");
		sql.append(
				",DispatchDateAttempt1 ,DispatchDateAttempt2 ,DispatchDateAttempt3 ,MedicalStatus ,PendencyReasonCategory ,PendencyReason ,InsPendencyResReq");
		sql.append(
				",FPR ,PolicyStatus ,FormHandoverDate ,NomineeName ,NomineeRelation ,VASProviderId, ReconStatus, TolaranceAmount, ManualReconRemarks, ManualReconResCategory,LinkedTranId ");
		if (tableType.contains("View")) {
			sql.append(",FinType, PostingAgainst, InsurancePremium, FreeLockPeriod ");
		}
		sql.append(" ,Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId,");
		sql.append(" RecordType, WorkflowId From InsuranceDetails");
		sql.append(StringUtils.trimToEmpty(tableType));
		sql.append(" Where Id = :Id");
		logger.debug("selectSql: " + sql.toString());

		RowMapper<InsuranceDetails> typeRowMapper = BeanPropertyRowMapper.newInstance(InsuranceDetails.class);

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("Id", id);

		try {
			return this.jdbcTemplate.queryForObject(sql.toString(), source, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);
		return null;
	}

	@Override
	public long saveInsuranceDetails(InsuranceDetails insuranceDetail, String tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("insert into InsuranceDetails");
		sql.append(tableType);
		sql.append(
				" (Id ,Reference ,FinReference ,StartDate ,EndDate ,Term ,CoverageAmount ,PolicyNumber ,IssuanceDate ,IssuanceStatus ,PartnerPremium ,");
		sql.append(
				"PartnerReceivedDate ,AWBNo1 ,AWBNo2 ,AWBNo3 ,DispatchStatus1 ,DispatchStatus2 ,DispatchStatus3 ,ReasonOfRTO1 ,ReasonOfRTO2 ,ReasonOfRTO3");
		sql.append(
				",DispatchDateAttempt1 ,DispatchDateAttempt2 ,DispatchDateAttempt3 ,MedicalStatus ,PendencyReasonCategory ,PendencyReason ,InsPendencyResReq, TolaranceAmount");
		sql.append(
				",FPR ,PolicyStatus ,FormHandoverDate ,NomineeName ,NomineeRelation ,VASProviderId ,ReconStatus ,Version ,LastMntBy ,LastMntOn, ManualReconRemarks, ManualReconResCategory, LinkedTranId");
		sql.append(",RecordStatus ,RoleCode ,NextRoleCode ,TaskId ,NextTaskId ,RecordType ,WorkflowId)");

		sql.append(
				" values (:Id ,:Reference ,:FinReference ,:StartDate ,:EndDate ,:Term ,:CoverageAmount ,:PolicyNumber ,:IssuanceDate ,:IssuanceStatus ,:PartnerPremium ");
		sql.append(
				",:PartnerReceivedDate ,:AWBNo1 ,:AWBNo2 ,:AWBNo3 ,:DispatchStatus1 ,:DispatchStatus2 ,:DispatchStatus3 ,:ReasonOfRTO1 ,:ReasonOfRTO2 ,:ReasonOfRTO3");
		sql.append(
				",:DispatchDateAttempt1 ,:DispatchDateAttempt2 ,:DispatchDateAttempt3 ,:MedicalStatus ,:PendencyReasonCategory ,:PendencyReason ,:InsPendencyResReq, :TolaranceAmount");
		sql.append(
				",:FPR ,:PolicyStatus ,:FormHandoverDate ,:NomineeName ,:NomineeRelation ,:VASProviderId ,:ReconStatus ,:Version ,:LastMntBy ,:LastMntOn, :ManualReconRemarks, :ManualReconResCategory, :LinkedTranId");
		sql.append(",:RecordStatus ,:RoleCode ,:NextRoleCode ,:TaskId ,:NextTaskId ,:RecordType ,:WorkflowId)");

		// Get the identity sequence number.
		if (insuranceDetail.getId() <= 0) {
			insuranceDetail.setId(getNextValue("SeqInsuranceDetails"));
		}

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(insuranceDetail);

		try {
			jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}
		logger.debug(Literal.LEAVING);
		return insuranceDetail.getId();
	}

	@Override
	public void updateInsuranceDetails(InsuranceDetails insuranceDetail, String tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL, ensure primary key will not be updated.
		StringBuilder sql = new StringBuilder("update InsuranceDetails");
		sql.append(tableType);
		sql.append(
				" set Id = :Id ,Reference = :Reference ,FinReference = :FinReference ,StartDate = :StartDate ,EndDate = :EndDate ,Term = :Term");
		sql.append(
				",CoverageAmount = :CoverageAmount ,PolicyNumber = :PolicyNumber ,IssuanceDate = :IssuanceDate ,IssuanceStatus = :IssuanceStatus");
		sql.append(
				",PartnerPremium = :PartnerPremium ,PartnerReceivedDate = :PartnerReceivedDate ,AWBNo1 = :AWBNo1 ,AWBNo2 = :AWBNo2 ,AWBNo3 = :AWBNo3");
		sql.append(
				",DispatchStatus1 = :DispatchStatus1 ,DispatchStatus2 = :DispatchStatus2 ,DispatchStatus3 = :DispatchStatus3");
		sql.append(
				",ReasonOfRTO1 = :ReasonOfRTO1 ,ReasonOfRTO2 = :ReasonOfRTO2 ,ReasonOfRTO3 = :ReasonOfRTO3 ,DispatchDateAttempt1 = :DispatchDateAttempt1");
		sql.append(
				",DispatchDateAttempt2 = :DispatchDateAttempt2 ,DispatchDateAttempt3 = :DispatchDateAttempt3 ,MedicalStatus = :MedicalStatus");
		sql.append(
				",PendencyReasonCategory = :PendencyReasonCategory ,PendencyReason = :PendencyReason ,InsPendencyResReq = :InsPendencyResReq, TolaranceAmount = :TolaranceAmount");
		sql.append(
				",FPR = :FPR ,PolicyStatus = :PolicyStatus ,FormHandoverDate = :FormHandoverDate ,NomineeName = :NomineeName, ManualReconRemarks = :ManualReconRemarks, ManualReconResCategory = :ManualReconResCategory");
		sql.append(
				",LinkedTranId= :LinkedTranId ,NomineeRelation = :NomineeRelation ,VASProviderId = :VASProviderId ,ReconStatus = :ReconStatus ,Version = :Version");
		sql.append(
				",LastMntBy = :LastMntBy ,LastMntOn = :LastMntOn ,RecordStatus = :RecordStatus ,RoleCode = :RoleCode ,NextRoleCode = :NextRoleCode");
		sql.append(",TaskId = :TaskId ,NextTaskId = :NextTaskId ,RecordType = :RecordType ,WorkflowId = :WorkflowId");
		sql.append(" Where Reference = :Reference");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(insuranceDetail);
		int recordCount = jdbcTemplate.update(sql.toString(), paramSource);

		if (recordCount == 0) {
			throw new ConcurrencyException();
		}
		logger.debug(Literal.LEAVING);
	}

	@Override
	public boolean isDuplicateKey(long id, String reference, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		String sql;
		String whereClause = "Reference = :Reference and id != :id";

		switch (tableType) {
		case MAIN_TAB:
			sql = QueryUtil.getCountQuery("InsuranceDetails", whereClause);
			break;
		case TEMP_TAB:
			sql = QueryUtil.getCountQuery("InsuranceDetailss_Temp", whereClause);
			break;
		default:
			sql = QueryUtil.getCountQuery(new String[] { "InsuranceDetails_Temp", "InsuranceDetails" }, whereClause);
			break;
		}

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql);
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("id", id);
		paramSource.addValue("Reference", reference);

		Integer count = jdbcTemplate.queryForObject(sql, paramSource, Integer.class);

		boolean exists = false;
		if (count > 0) {
			exists = true;
		}

		logger.debug(Literal.LEAVING);
		return exists;
	}

	@Override
	public void delete(InsuranceDetails insuranceDetails, String tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("delete from InsuranceDetails");
		sql.append(tableType);
		sql.append(" where ID = :id");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(insuranceDetails);
		int recordCount = 0;

		try {
			recordCount = jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}
		logger.debug(Literal.LEAVING);
	}

	@Override
	public long saveInsurancePayments(InsurancePaymentInstructions paymentInstructions, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("insert into InsurancePaymentInstructions");
		sql.append(tableType.getSuffix());
		sql.append(
				"(Id ,EntityCode ,ProviderId ,PaymentAmount ,PayableAmount ,ReceivableAmount ,LinkedTranId ,DataEngineStatusId ,NoOfInsurances");
		sql.append(
				", NoOfPayments ,NoOfReceivables ,AdjustedReceivable ,PartnerBankId ,PaymentDate ,PaymentType ,TransactionRef ,RejectReason");
		sql.append(" ,Remarks ,RealizationDate ,RespDate ,ApprovedDate ,PaymentCCy ,Status ");
		sql.append(" ,Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId,");
		sql.append(" RecordType, WorkflowId)");

		sql.append(
				" values (:Id ,:EntityCode ,:ProviderId ,:PaymentAmount ,:PayableAmount ,:ReceivableAmount ,:LinkedTranId ,:DataEngineStatusId ,:NoOfInsurances");
		sql.append(
				" ,:NoOfPayments ,:NoOfReceivables ,:AdjustedReceivable ,:PartnerBankId ,:PaymentDate ,:PaymentType ,:TransactionRef ,:RejectReason");
		sql.append(" ,:Remarks ,:RealizationDate ,:RespDate ,:ApprovedDate ,:PaymentCCy ,:Status ");
		sql.append(" ,:Version,:LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId,");
		sql.append(" :RecordType, :WorkflowId)");
		// Get the identity sequence number.
		if (paymentInstructions.getId() <= 0) {
			paymentInstructions.setId(getSeqNumber());
		}

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(paymentInstructions);

		try {
			jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}
		logger.debug(Literal.LEAVING);
		return paymentInstructions.getId();
	}

	@Override
	public long getSeqNumber() {
		return getNextValue("SeqAdvpayment");
	}

	/**
	 * 
	 */
	@Override
	public int updatePaymentStatus(InsurancePaymentInstructions instruction) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		StringBuilder sql = new StringBuilder();
		sql.append(" Update INSURANCEPAYMENTINSTRUCTIONS");
		sql.append(" Set STATUS = :STATUS, RESPDATE = :RESPDATE, TRANSACTIONREF = :TRANSACTIONREF,");
		sql.append(" REALIZATIONDATE= :REALIZATIONDATE, REJECTREASON = :REJECTREASON Where ID = :ID");

		paramMap.addValue("STATUS", instruction.getStatus());
		paramMap.addValue("RESPDATE", instruction.getRespDate());
		paramMap.addValue("TRANSACTIONREF", instruction.getTransactionRef());
		paramMap.addValue("REJECTREASON", instruction.getRejectReason());
		paramMap.addValue("REALIZATIONDATE", instruction.getRealizationDate());
		paramMap.addValue("ID", instruction.getId());

		logger.trace(Literal.SQL + sql.toString());

		return this.jdbcTemplate.update(sql.toString(), paramMap);

	}

	@Override
	public void updateInsuranceDetails(String reference, String status) {
		logger.debug(Literal.ENTERING);

		StringBuilder mainSql = new StringBuilder();
		mainSql.append(" Update INSURANCEDETAILS set POLICYSTATUS = :POLICYSTATUS ");
		mainSql.append(" Where Reference = :Reference");

		StringBuilder tempSql = new StringBuilder();
		tempSql.append(" Update INSURANCEDETAILS_TEMP set POLICYSTATUS = :POLICYSTATUS");
		tempSql.append(" Where Reference = :Reference");

		MapSqlParameterSource source = new MapSqlParameterSource();

		source.addValue("Reference", reference);
		if (VASConsatnts.STATUS_CANCEL.equals(status)) {
			source.addValue("POLICYSTATUS", InsuranceConstants.CANCEL);
		} else if (VASConsatnts.STATUS_SURRENDER.equals(status)) {
			source.addValue("POLICYSTATUS", InsuranceConstants.SURRENDER);
		}

		try {
			jdbcTemplate.update(mainSql.toString(), source);
			jdbcTemplate.update(tempSql.toString(), source);
		} catch (Exception e) {
			logger.debug(Literal.EXCEPTION, e);
			throw e;
		} finally {
			source = null;
			tempSql = null;
			mainSql = null;
		}
		logger.debug(Literal.LEAVING);
	}

	@Override
	public void updatePaymentLinkedTranId(String reference, long linkedTranId) {

		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		sql.append(" Update INSURANCEDETAILS set PaymentLinkedTranId = :PaymentLinkedTranId ");
		sql.append(" Where Reference = :Reference");

		MapSqlParameterSource source = new MapSqlParameterSource();

		source.addValue("Reference", reference);
		source.addValue("PaymentLinkedTranId", linkedTranId);

		try {
			jdbcTemplate.update(sql.toString(), source);
		} catch (Exception e) {
			logger.debug(Literal.EXCEPTION, e);
			throw e;
		} finally {
			source = null;
			sql = null;
		}
		logger.debug(Literal.LEAVING);

	}

	@Override
	public void updateLinkTranId(long id, long linkedTranId) {

		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		sql.append(" Update INSURANCEPAYMENTINSTRUCTIONS set LinkedTranId = :linkedTranId ");
		sql.append(" Where id = :id");

		MapSqlParameterSource source = new MapSqlParameterSource();

		source.addValue("linkedTranId", linkedTranId);
		source.addValue("id", id);

		try {
			jdbcTemplate.update(sql.toString(), source);
		} catch (Exception e) {
			logger.debug(Literal.EXCEPTION, e);
			throw e;
		} finally {
			source = null;
			sql = null;
		}
		logger.debug(Literal.LEAVING);

	}

	@Override
	public InsurancePaymentInstructions getInsurancePaymentInstructionStatus(long id) {
		StringBuilder sql = new StringBuilder();
		sql.append("Select Status from INSURANCEPAYMENTINSTRUCTIONS");
		sql.append(" Where id = ?");
		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), new Object[] { id }, (rs, rowNum) -> {
				InsurancePaymentInstructions ipi = new InsurancePaymentInstructions();
				ipi.setStatus(rs.getString(1));
				return ipi;
			});
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Record not found in INSURANCEPAYMENTINSTRUCTIONS table/view for the specified ID >> {} ", id);
		}

		return null;
	}

	@Override
	public InsurancePaymentInstructions getInsurancePaymentInstructionById(long id) {

		StringBuilder sql = new StringBuilder();
		sql.append("SELECT LINKEDTRANID, ID, PAYMENTAMOUNT, PAYMENTTYPE, PROVIDERID");
		sql.append(", ENTITYCODE, PAYMENTCCY FROM INSURANCEPAYMENTINSTRUCTIONS");
		sql.append("  WHERE ID = ?");

		logger.debug(Literal.SQL + sql.toString());
		try {
			return this.jdbcOperations.queryForObject(sql.toString(), new Object[] { id }, (rs, rowNum) -> {
				InsurancePaymentInstructions ipi = new InsurancePaymentInstructions();
				ipi.setLinkedTranId(rs.getLong("LINKEDTRANID"));
				ipi.setId(rs.getLong("ID"));
				ipi.setPaymentAmount(rs.getBigDecimal("PAYMENTAMOUNT"));
				ipi.setPaymentType(rs.getString("PAYMENTTYPE"));
				ipi.setProviderId(rs.getLong("PROVIDERID"));
				ipi.setEntityCode(rs.getString("ENTITYCODE"));
				ipi.setEntityCode(rs.getString("PAYMENTCCY"));
				return ipi;
			});
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Record not found in INSURANCEPAYMENTINSTRUCTIONS table/view for the specified ID >> {} ", id);
		}
		return null;
	}

}
