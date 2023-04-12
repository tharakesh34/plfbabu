package com.pennant.pff.presentment.dao.impl;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.payment.PaymentHeader;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.RepayConstants;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennant.eod.constants.EodConstants;
import com.pennant.pff.batch.job.model.BatchJobQueue;
import com.pennant.pff.mandate.InstrumentType;
import com.pennant.pff.mandate.MandateStatus;
import com.pennant.pff.presentment.ExcludeReasonCode;
import com.pennant.pff.presentment.dao.PresentmentDAO;
import com.pennant.pff.presentment.model.PresentmentExcludeCode;
import com.pennant.pff.presentment.service.PresentmentEngine;
import com.pennanttech.model.presentment.Presentment;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;
import com.pennanttech.pff.presentment.model.PresentmentDetail;
import com.pennanttech.pff.presentment.model.PresentmentHeader;

public class PresentmentDAOImpl extends SequenceDao<PaymentHeader> implements PresentmentDAO {

	public PresentmentDAOImpl() {
		super();
	}

	@Override
	public long createBatch(String batchType, int totalRecords) {
		String sql = "Insert into PRMNT_BATCH_JOBS (Batch_Type, Start_Time, Total_Records) values (?, ?, ?)";

		logger.debug(Literal.SQL.concat(sql));

		KeyHolder keyHolder = new GeneratedKeyHolder();

		jdbcOperations.update(new PreparedStatementCreator() {

			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement ps = con.prepareStatement(sql, new String[] { "id" });

				ps.setString(1, batchType);
				ps.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
				ps.setInt(3, totalRecords);

				return ps;
			}
		}, keyHolder);

		Number key = keyHolder.getKey();

		if (key == null) {
			return 0;
		}

		return key.longValue();
	}

	@Override
	public void deleteBatch(long batchID) {
		String sql = "Delete From PRMNT_BATCH_JOBS Where ID = ?";

		logger.debug(Literal.SQL.concat(sql));

		this.jdbcOperations.update(sql, batchID);
	}

	@Override
	public BatchJobQueue getBatch(BatchJobQueue jobQueue) {
		StringBuilder sql = new StringBuilder();
		sql.append("Select Batch_Type, Total_Records, Process_Records, Success_Records, Failed_Records, Remarks");
		sql.append(" From PRMNT_BATCH_JOBS");
		sql.append(" Where ID = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
			BatchJobQueue bjq = new BatchJobQueue();

			bjq.setBatchType(rs.getString("Batch_Type"));
			bjq.setTotalRecords(rs.getInt("Total_Records"));
			bjq.setProcessRecords(rs.getInt("Process_Records"));
			bjq.setSuccessRecords(rs.getInt("Success_Records"));
			bjq.setFailedRecords(rs.getInt("Failed_Records"));
			bjq.setRemarks(rs.getString("Remarks"));

			return bjq;
		}, jobQueue.getBatchId());

	}

	@Override
	public void updateTotalRecords(int count, long batchID) {
		String sql = "Update PRMNT_BATCH_JOBS Set Total_Records = ? Where ID = ?";

		logger.debug(Literal.SQL.concat(sql));

		this.jdbcOperations.update(sql, ps -> {
			ps.setInt(1, count);
			ps.setLong(2, batchID);
		});

	}

	@Override
	public void updateBatch(BatchJobQueue jobQueue) {
		int process = jobQueue.getProgress();

		String sql = null;

		if (process == EodConstants.PROGRESS_IN_PROCESS) {
			sql = "Update PRMNT_BATCH_JOBS Set Process_Records = ? Where ID = ?";

			this.jdbcOperations.update(sql, ps -> {
				ps.setInt(1, jobQueue.getProcessedRecords());
				ps.setLong(2, jobQueue.getBatchId());
			});
		}
		if (process == EodConstants.PROGRESS_SUCCESS) {
			sql = "Update PRMNT_BATCH_JOBS Set Success_Records = ? Where ID = ?";

			this.jdbcOperations.update(sql, ps -> {
				ps.setInt(1, jobQueue.getSuccessRecords());
				ps.setLong(2, jobQueue.getBatchId());
			});
		}

		if (process == EodConstants.PROGRESS_FAILED) {
			sql = "Update PRMNT_BATCH_JOBS Set Failed_Records = ? Where ID = ?";

			this.jdbcOperations.update(sql, ps -> {
				ps.setInt(1, jobQueue.getFailedRecords());
				ps.setLong(2, jobQueue.getBatchId());
			});
		}

		logger.debug(Literal.SQL.concat(sql));

	}

	@Override
	public void updateRemarks(BatchJobQueue jobQueue) {
		String sql = "Update PRMNT_BATCH_JOBS Set Remarks = ? Where ID = ?";

		logger.debug(Literal.SQL.concat(sql));

		this.jdbcOperations.update(sql, ps -> {
			ps.setString(1, jobQueue.getRemarks());
			ps.setLong(2, jobQueue.getBatchId());
		});
	}

	@Override
	public void updateFailureError(BatchJobQueue jobQueue) {
		String sql = "Update PRMNT_BATCH_JOBS Set Failed_Step = ?, Error = ? Where ID = ?";

		logger.debug(Literal.SQL.concat(sql));

		this.jdbcOperations.update(sql, ps -> {
			ps.setString(1, jobQueue.getFailedStep());
			ps.setString(2, jobQueue.getError().substring(1999));
			ps.setLong(3, jobQueue.getBatchId());
		});
	}

	@Override
	public void updateEndTimeStatus(BatchJobQueue jobQueue) {
		String sql = "Update PRMNT_BATCH_JOBS Set End_Time = ?, Status = ? Where ID = ?";

		logger.debug(Literal.SQL.concat(sql));

		this.jdbcOperations.update(sql, ps -> {
			ps.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
			ps.setString(2, jobQueue.getBatchStatus());
			ps.setLong(3, jobQueue.getBatchId());
		});
	}

	@Override
	public int extarct(long batchID, Date dueDate) {
		StringBuilder sql = new StringBuilder(getExtractQuery("NACH+ECS+EMANDATE+SI+SII"));
		sql.append(" Where (SchDate = ? or DefSchddate = ?)");

		logger.debug(Literal.SQL.concat(sql.toString()));

		int recordCount = this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, batchID);
			ps.setInt(index++, 1);
			ps.setDate(index++, JdbcUtil.getDate(dueDate));
			ps.setDate(index++, JdbcUtil.getDate(dueDate));
		});

		sql = new StringBuilder(getExtractQuery(InstrumentType.PDC.name()));
		sql.append(" Where (SchDate = ? or DefSchddate = ?) and ChequeType = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		recordCount = recordCount + this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, batchID);
			ps.setInt(index++, 1);
			ps.setDate(index++, JdbcUtil.getDate(dueDate));
			ps.setDate(index++, JdbcUtil.getDate(dueDate));
			ps.setString(index++, InstrumentType.PDC.name());
		});

		sql = new StringBuilder(getExtractQuery(InstrumentType.DAS.name()));
		sql.append(" Where (SchDate = ? or DefSchddate = ?) and MandateType = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		recordCount = recordCount + this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, batchID);
			ps.setInt(index++, 1);
			ps.setDate(index++, JdbcUtil.getDate(dueDate));
			ps.setDate(index++, JdbcUtil.getDate(dueDate));
			ps.setString(index++, InstrumentType.DAS.name());
		});

		return recordCount;
	}

	@Override
	public int extarct(long batchID, Date fromDate, Date toDate) {
		StringBuilder sql = new StringBuilder(getExtractQuery("NACH+ECS+EMANDATE+SI+SII"));
		sql.append(" Where (SchDate >= ? and SchDate <= ?) or (DefSchddate >= ? and DefSchddate <= ?)");

		logger.debug(Literal.SQL.concat(sql.toString()));

		int recordCount = this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, batchID);
			ps.setInt(index++, 1);
			ps.setDate(index++, JdbcUtil.getDate(fromDate));
			ps.setDate(index++, JdbcUtil.getDate(toDate));
			ps.setDate(index++, JdbcUtil.getDate(fromDate));
			ps.setDate(index++, JdbcUtil.getDate(toDate));
		});

		sql = new StringBuilder(getExtractQuery(InstrumentType.PDC.name()));
		sql.append(
				" Where ((SchDate >= ? and SchDate <= ?) or (DefSchddate >= ? and DefSchddate <= ?)) and ChequeType = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		recordCount = recordCount + this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, batchID);
			ps.setInt(index++, 1);
			ps.setDate(index++, JdbcUtil.getDate(fromDate));
			ps.setDate(index++, JdbcUtil.getDate(toDate));
			ps.setDate(index++, JdbcUtil.getDate(fromDate));
			ps.setDate(index++, JdbcUtil.getDate(toDate));
			ps.setString(index++, InstrumentType.PDC.name());
		});

		sql = new StringBuilder(getExtractQuery(InstrumentType.DAS.name()));
		sql.append(
				" Where ((SchDate >= ? and SchDate <= ?) or (DefSchddate >= ? and DefSchddate <= ?)) and MandateType = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		recordCount = recordCount + this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, batchID);
			ps.setInt(index++, 1);
			ps.setDate(index++, JdbcUtil.getDate(fromDate));
			ps.setDate(index++, JdbcUtil.getDate(toDate));
			ps.setDate(index++, JdbcUtil.getDate(fromDate));
			ps.setDate(index++, JdbcUtil.getDate(toDate));
			ps.setString(index++, InstrumentType.DAS.name());
		});

		return recordCount;
	}

	@Override
	public int extarct(long batchID, String instrumentType, Date fromDate, Date toDate) {
		StringBuilder sql = new StringBuilder(getExtractQuery(instrumentType));
		sql.append(" Where (SchDate >= ? and SchDate <= ?) or (DefSchddate >= ? and DefSchddate <= ?)");

		logger.debug(Literal.SQL.concat(sql.toString()));

		String bankCode = SysParamUtil.getValueAsString(SMTParameterConstants.BANK_CODE);

		return this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, batchID);
			ps.setObject(index++, null);
			ps.setInt(index++, 1);
			if (InstrumentType.isPDC(instrumentType) || InstrumentType.isIPDC(instrumentType)) {
				ps.setString(index++, "PDC");
				ps.setString(index++, bankCode);
			} else {
				ps.setString(index++, instrumentType);
			}
			ps.setDate(index++, JdbcUtil.getDate(fromDate));
			ps.setDate(index++, JdbcUtil.getDate(toDate));
			ps.setDate(index++, JdbcUtil.getDate(fromDate));
			ps.setDate(index, JdbcUtil.getDate(toDate));
		});
	}

	@Override
	public int clearByNoDues(long batchID) {
		String sql = "Delete From PRMNT_EXTRACTION_STAGE Where BatchID = ? and (ProfitSchd + PrincipalSchd + FeeSchd + TdsAmount) - (SchdPftPaid + SchdPriPaid + SchdFeePaid + TdsPaid) <= ? and ProductCategory != ?";

		logger.debug(Literal.SQL.concat(sql));

		return this.jdbcOperations.update(sql, ps -> {
			ps.setLong(1, batchID);
			ps.setBigDecimal(2, BigDecimal.ZERO);
			ps.setString(3, FinanceConstants.PRODUCT_ODFACILITY);
		});
	}

	@Override
	public int clearByInstrumentType(long batchID, String instrumentType) {
		String sql = "Delete From PRMNT_EXTRACTION_STAGE Where BatchID = ? and MandateType != ? or ChequeType != ?";

		logger.debug(Literal.SQL.concat(sql));

		return this.jdbcOperations.update(sql, batchID, instrumentType, instrumentType);
	}

	@Override
	public int clearByInstrumentType(long batchID, String instrumentType, String emnadateSource) {
		String sql = "Delete From PRMNT_EXTRACTION_STAGE Where BatchID = ? and MandateType != ? and EmandateSource != ?";

		logger.debug(Literal.SQL.concat(sql));

		return this.jdbcOperations.update(sql, batchID, instrumentType, emnadateSource);
	}

	@Override
	public int clearByLoanType(long batchID, String loanType) {
		String sql = "Delete From PRMNT_EXTRACTION_STAGE Where BatchID = ? and FinType != ?";

		logger.debug(Literal.SQL.concat(sql));

		return this.jdbcOperations.update(sql, batchID, loanType);
	}

	@Override
	public int clearByLoanBranch(long batchID, String loanBranch) {
		String sql = "Delete From PRMNT_EXTRACTION_STAGE Where BatchID = ? and FinBranch != ?";

		logger.debug(Literal.SQL.concat(sql));

		return this.jdbcOperations.update(sql, batchID, loanBranch);
	}

	@Override
	public int clearByEntityCode(long batchID, String entityCode) {
		String sql = "Delete From PRMNT_EXTRACTION_STAGE Where BatchID = ? and EntityCode != ?";

		logger.debug(Literal.SQL.concat(sql));

		return this.jdbcOperations.update(sql, batchID, entityCode);
	}

	@Override
	public int clearSecurityCheque(long batchID) {
		String sql = "Delete From PRMNT_EXTRACTION_STAGE Where BatchID = ? and ChequeType = ?";

		logger.debug(Literal.SQL.concat(sql));

		return this.jdbcOperations.update(sql, batchID, InstrumentType.SPDC.name());
	}

	private List<PresentmentDetail> getSecurityMandates(long batchID) {
		StringBuilder sql = new StringBuilder();
		sql.append("Select pes.ID, sm.MandateId, sm.MandateType");
		sql.append(" From PRMNT_EXTRACTION_STAGE pes");
		sql.append(" Inner Join Mandates m on m.MandateId = pes.MandateId and m.status in (?, ?, ?)");
		sql.append(" Inner Join FinanceMain fm on fm.FinID = pes.FinID");
		sql.append(" Inner Join Mandates sm on sm.MandateId = fm.SecurityMandateId and sm.Status = ?");
		sql.append(" And sm.SecurityMandate = ?");
		sql.append(" Where BatchID = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;

			ps.setString(index++, MandateStatus.NEW);
			ps.setString(index++, MandateStatus.AWAITCON);
			ps.setString(index++, MandateStatus.REJECTED);
			ps.setString(index++, MandateStatus.APPROVED);
			ps.setInt(index++, 1);

			ps.setLong(index++, batchID);

		}, (rs, rowNum) -> {
			PresentmentDetail pd = new PresentmentDetail();

			pd.setId(rs.getLong("ID"));
			pd.setMandateId(rs.getLong("MandateId"));
			pd.setMandateType(rs.getString("MandateType"));

			return pd;
		});
	}

	@Override
	public int updateToSecurityMandate(long batchID) {
		List<PresentmentDetail> securityMandates = getSecurityMandates(batchID);

		if (CollectionUtils.isEmpty(securityMandates)) {
			return 0;
		}

		String sql = "Update PRMNT_EXTRACTION_STAGE Set MandateId = ?, MandateStatus = ?, MandateType = ?, InstrumentType = ? Where Id = ?";

		logger.debug(Literal.SQL.concat(sql));
		return jdbcOperations.batchUpdate(sql, new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				PresentmentDetail pd = securityMandates.get(i);

				int index = 1;

				ps.setLong(index++, pd.getMandateId());
				ps.setString(index++, MandateStatus.APPROVED);
				ps.setString(index++, pd.getMandateType());
				ps.setString(index++, pd.getMandateType());

				ps.setLong(index, pd.getId());

			}

			@Override
			public int getBatchSize() {
				return securityMandates.size();
			}
		}).length;
	}

	@Override
	public void updateIPDC(long batchID) {
		String sql = "Update PRMNT_EXTRACTION_STAGE set InstrumentType = ?, MandateType = ?, ChequeType = ? Where BatchID = ? and InstrumentType = ? and BankCode = ?";

		String bankcode = SysParamUtil.getValueAsString(SMTParameterConstants.BANK_CODE);

		logger.debug(Literal.SQL.concat(sql));

		this.jdbcOperations.update(sql, InstrumentType.IPDC.name(), InstrumentType.IPDC.name(),
				InstrumentType.IPDC.name(), batchID, InstrumentType.PDC.name(), bankcode);
	}

	@Override
	public int clearByExistingRecord(long batchID) {
		StringBuilder sql = new StringBuilder();
		sql.append(" Delete From PRMNT_EXTRACTION_STAGE");
		sql.append(" Where BatchID = ? and ID in (Select ps.ID");
		sql.append(" From PRMNT_EXTRACTION_STAGE ps");
		sql.append(" Inner Join PresentmentDetails pd on pd.FinID = ps.FinID and pd.SchDate = ps.SchDate");
		sql.append(" Where pd.ExcludeReason in (?, ?, ?, ?)");
		sql.append(" )");

		logger.debug(Literal.SQL.concat(sql.toString()));

		int count = this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, batchID);
			ps.setInt(index++, ExcludeReasonCode.EMI_INCLUDE.id());
			ps.setInt(index++, ExcludeReasonCode.EMI_IN_ADVANCE.id());
			ps.setInt(index++, ExcludeReasonCode.INT_ADV.id());
			ps.setInt(index++, ExcludeReasonCode.EMI_ADV.id());
		});

		sql = new StringBuilder();
		sql.append(" Delete From PRMNT_EXTRACTION_STAGE");
		sql.append(" Where BatchID = ? and ID in (Select ps.ID from PRMNT_EXTRACTION_STAGE ps");
		sql.append(" Inner Join PresentmentDetails pd on pd.FinID = ps.FinID and pd.SchDate = ps.SchDate");
		sql.append(" Inner Join PresentmentHeader ph on ph.ID = pd.PresentmentID");
		sql.append(" Inner Join Mandates m on m.MandateID = ps.MandateID");
		sql.append(" Where pd.ExcludeReason = ? and ph.Status in (?, ?, ?) and m.StartDate <= ps.SchDate");
		sql.append(" )");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return count + this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, batchID);
			ps.setInt(index++, ExcludeReasonCode.MANUAL_EXCLUDE.id());
			ps.setInt(index++, RepayConstants.PEXC_EXTRACT);
			ps.setInt(index++, RepayConstants.PEXC_BATCH_CREATED);
			ps.setInt(index, RepayConstants.PEXC_AWAITING_CONF);
		});
	}

	@Override
	public int clearByRepresentment(long batchID) {
		StringBuilder sql = new StringBuilder();
		sql.append(" Delete From PRMNT_EXTRACTION_STAGE");
		sql.append(" Where BatchID = ? and ID not in (Select ps.ID From PRMNT_EXTRACTION_STAGE ps");
		sql.append(" Inner Join PresentmentDetails pd on pd.FinID = ps.FinID and pd.SchDate = ps.SchDate");
		sql.append(" Where pd.Status in (?, ?)");
		sql.append(" )");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, batchID);
			ps.setString(index++, "B");
			ps.setString(index, "F");
		});
	}

	@Override
	public void updatePartnerBankID(long batchID) {
		StringBuilder sql = new StringBuilder();
		sql.append("Update PRMNT_EXTRACTION_STAGE set PartnerBankId = ?");
		sql.append(" Where BatchID = ? and PartnerBankId is null");

		logger.debug(Literal.SQL.concat(sql.toString()));

		this.jdbcOperations.update(sql.toString(), -1, batchID);
	}

	@Override
	public int clearByManualExclude(long batchID) {
		StringBuilder sql = new StringBuilder();
		sql.append(" Delete From PRMNT_EXTRACTION_STAGE");
		sql.append(" Where BatchID = ? and ID in (Select ps.ID from PRMNT_EXTRACTION_STAGE ps");
		sql.append(" Inner Join PresentmentDetails pd on pd.FinID = ps.FinID and pd.SchDate = ps.SchDate");
		sql.append(" Inner Join PresentmentHeader ph on ph.ID = pd.PresentmentID");
		sql.append(" Inner Join Mandates m On m.MandateID = ps.MandateID");
		sql.append(" Where pd.ExcludeReason = ? and ph.status in (1, 2, 3) and m.StartDate <= ps.SchDate");
		sql.append(" )");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, batchID);
			ps.setInt(index++, ExcludeReasonCode.MANUAL_EXCLUDE.id());
			ps.setInt(index++, ExcludeReasonCode.EMI_IN_ADVANCE.id());
			ps.setInt(index++, ExcludeReasonCode.EMI_HOLD.id());
			ps.setInt(index, ExcludeReasonCode.MANDATE_HOLD.id());
		});

	}

	private StringBuilder getExtractQuery(String instrumentType) {
		StringBuilder sql = new StringBuilder();
		sql.append("Insert Into PRMNT_EXTRACTION_STAGE (BatchID");
		sql.append(", FinId, FinReference, FinType, ProductCategory, FinBranch, EntityCode");
		sql.append(", BpiTreatment, GrcPeriodEndDate, GrcAdvType, AdvType, AdvStage, SchdVersion");
		sql.append(", SchDate, DefSchdDate, SchSeq, BpiOrHoliday");
		sql.append(", ProfitSchd, PrincipalSchd, FeeSchd, TdsAmount");
		sql.append(", SchdPftPaid, SchdPriPaid, SchdFeePaid, TdsPaid, RePresentUploadID");
		sql.append(", InstrumentType, ChequeId, ChequeType, ChequeStatus, ChequeDate");
		sql.append(", MandateId, MandateType, EmandateSource, MandateStatus, MandateExpiryDate");
		sql.append(", PartnerBankId, BranchCode, BankCode");
		sql.append(", EmployeeNo, EmPloyerId, EmployerName, ChequeAmount, InstNumber");
		sql.append(") Select");
		sql.append(" ?, fm.FinId, fm.FinReference, fm.FinType, fm.ProductCategory, fm.FinBranch, sdd.EntityCode");
		sql.append(", fm.BpiTreatment, fm.GrcPeriodEndDate, fm.GrcAdvType, fm.AdvType, fm.AdvStage, fm.SchdVersion");
		sql.append(", fsd.SchDate, fsd.DefSchdDate, fsd.SchSeq, fsd.BpiOrHoliday");
		sql.append(", fsd.ProfitSchd, fsd.PrincipalSchd, fsd.FeeSchd, fsd.TdsAmount");
		sql.append(", fsd.SchdPftPaid, fsd.SchdPriPaid, fsd.SchdFeePaid, fsd.TdsPaid, ?");

		if (InstrumentType.isPDC(instrumentType) || InstrumentType.isIPDC(instrumentType)) {
			sql.append(", cd.ChequeType, cd.ChequeDetailsId, cd.ChequeType, cd.ChequeStatus, cd.ChequeDate");
			sql.append(", cd.ChequeDetailsId, cd.ChequeType, null, null, null");
			sql.append(", null, b.BranchCode, bb.BankCode");
			sql.append(", null, null, null, cd.Amount ChequeAmount, cd.EmiRefNo");
			sql.append(" From FinScheduleDetails fsd");
			sql.append(" Inner Join FinanceMain fm On fm.FinID = fsd.FinID and fm.FinIsActive = ?");
			sql.append(" Inner Join RmtFinanceTypes ft on ft.FinType = fm.FinType");
			sql.append(" Inner Join ChequeHeader ch on ch.FinId = fm.FinId");
			sql.append(" Inner Join ChequeDetail cd on cd.HeaderId = ch.HeaderId");
			sql.append(" and cd.EmiRefNo = fsd.InstNumber and cd.ChequeType = ?");
			sql.append(" Inner Join RmtBranches b on b.BranchCode = fm.FinBranch");
			sql.append(" Inner Join BankBranches bb on bb.BankBranchId = cd.BankBranchId");
			if (InstrumentType.isIPDC(instrumentType)) {
				sql.append(" and bb.BankCode = ?");
			} else {
				sql.append(" and bb.BankCode != ?");
			}
		} else if (InstrumentType.isDAS(instrumentType)) {
			sql.append(", m.MandateType, null, null, null, null");
			sql.append(", fm.MandateId, m.MandateType, m.EmandateSource, m.Status, m.ExpiryDate");
			sql.append(", m.PartnerBankId, b.BranchCode, null");
			sql.append(", m.EmployeeNo, m.EmPloyerId, e.EmpName EmPloyerName, 0 ChequeAmount, fsd.InstNumber");
			sql.append(" From FinScheduleDetails fsd");
			sql.append(" Inner Join FinanceMain fm On fm.FinID = fsd.FinID and fm.FinIsActive = ?");
			sql.append(" Inner Join RmtFinanceTypes ft On ft.FinType = fm.FinType");
			sql.append(" Inner Join Mandates m on m.MandateId = fm.MandateId and MandateType = ?");
			sql.append(" Inner Join RmtBranches b On b.BranchCode = fm.FinBranch");
			sql.append(" Inner Join EmployerDetail e On e.EmPloyerId = m.EmPloyerId");
		} else {
			sql.append(", m.MandateType, null, null, null, null");
			sql.append(", fm.MandateId, m.MandateType, m.EmandateSource, m.Status, m.ExpiryDate");
			sql.append(", m.PartnerBankId, b.BranchCode, bb.BankCode");
			sql.append(", null, null, null, 0 ChequeAmount, fsd.InstNumber");
			sql.append(" From FinScheduleDetails fsd");
			sql.append(" Inner Join FinanceMain fm On fm.FinID = fsd.FinID and fm.FinIsActive = ?");
			sql.append(" Inner Join RmtFinanceTypes ft On ft.FinType = fm.FinType");
			sql.append(" Inner Join Mandates m on m.MandateId = fm.MandateId and MandateType = ?");
			sql.append(" Inner Join RmtBranches b On b.BranchCode = fm.FinBranch");
			sql.append(" Inner Join BankBranches bb On bb.BankBranchId = m.BankBranchId");
		}

		sql.append(" Inner Join SmtDivisionDetail sdd On sdd.DivisionCode = ft.FinDivision");

		return sql;
	}

	@Override
	public List<PresentmentDetail> getPresentmentDetails(long batchID) {
		StringBuilder sql = new StringBuilder();
		sql.append("Select BatchID, FinId, FinReference, FinType, ProductCategory, FinBranch, EntityCode");
		sql.append(", BpiTreatment, GrcPeriodEndDate, GrcAdvType, AdvType, AdvStage, SchdVersion");
		sql.append(", SchDate, DefSchdDate, SchSeq, InstNumber, BpiOrHoliday");
		sql.append(", ProfitSchd, PrincipalSchd, FeeSchd, TdsAmount");
		sql.append(", SchdPftPaid, SchdPriPaid, SchdFeePaid, TdsPaid");
		sql.append(", MandateId, MandateType, EmandateSource, MandateStatus, MandateExpiryDate");
		sql.append(", ChequeId, ChequeType, ChequeStatus, ChequeDate");
		sql.append(", PartnerBankId, BranchCode, BankCode");
		sql.append(" From  PRMNT_EXTRACTION_STAGE Where BatchID = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.query(sql.toString(), (rs, rowNum) -> {
			PresentmentDetail pd = new PresentmentDetail();

			pd.setFinID(rs.getLong("BatchID"));
			pd.setFinID(rs.getLong("FinId"));
			pd.setFinReference(rs.getString("FinReference"));
			pd.setFinType(rs.getString("FinType"));
			pd.setProductCategory(rs.getString("ProductCategory"));
			pd.setFinBranch(rs.getString("FinBranch"));
			pd.setEntityCode(rs.getString("EntityCode"));
			pd.setBpiTreatment(rs.getString("BpiTreatment"));
			pd.setGrcPeriodEndDate(rs.getDate("GrcPeriodEndDate"));
			pd.setGrcAdvType(rs.getString("GrcAdvType"));
			pd.setAdvType(rs.getString("AdvType"));
			pd.setAdvStage(rs.getString("AdvStage"));
			pd.setSchdVersion(rs.getInt("SchdVersion"));
			pd.setSchDate(rs.getDate("SchDate"));
			pd.setDefSchdDate(rs.getDate("DefSchdDate"));
			pd.setSchSeq(rs.getInt("SchSeq"));
			pd.setInstNumber(rs.getInt("InstNumber"));
			pd.setBpiOrHoliday(rs.getString("BpiOrHoliday"));
			pd.setProfitSchd(rs.getBigDecimal("ProfitSchd"));
			pd.setPrincipalSchd(rs.getBigDecimal("PrincipalSchd"));
			pd.setFeeSchd(rs.getBigDecimal("FeeSchd"));
			pd.settDSAmount(rs.getBigDecimal("TdsAmount"));
			pd.setSchdPftPaid(rs.getBigDecimal("SchdPftPaid"));
			pd.setSchdPriPaid(rs.getBigDecimal("SchdPriPaid"));
			pd.setSchdFeePaid(rs.getBigDecimal("SchdFeePaid"));
			pd.setTdsPaid(rs.getBigDecimal("TdsPaid"));
			pd.setMandateId(JdbcUtil.getLong(rs.getObject("MandateId")));
			pd.setMandateType(rs.getString("MandateType"));
			pd.setEmandateSource(rs.getString("EmandateSource"));
			pd.setMandateStatus(rs.getString("MandateStatus"));
			pd.setMandateExpiryDate(rs.getDate("MandateExpiryDate"));
			pd.setChequeId(JdbcUtil.getLong(rs.getObject("ChequeId")));
			pd.setChequeType(rs.getString("ChequeType"));
			pd.setChequeStatus(rs.getString("ChequeStatus"));
			pd.setChequeDate(rs.getDate("ChequeDate"));
			pd.setPartnerBankId(JdbcUtil.getLong(rs.getObject("PartnerBankId")));
			pd.setBranchCode(rs.getString("BranchCode"));
			pd.setBankCode(rs.getString("BankCode"));

			return pd;
		}, batchID);
	}

	@Override
	public List<PresentmentDetail> getGroupByDefault(long batchID) {
		String sql = "Select DefSchdDate, EntityCode, InstrumentType From PRMNT_EXTRACTION_STAGE Where BatchID = ? Group By DefSchdDate, EntityCode, InstrumentType";

		logger.debug(Literal.SQL.concat(sql));

		return jdbcOperations.query(sql, (rs, rowNum) -> {
			PresentmentDetail pd = new PresentmentDetail();

			pd.setDefSchdDate(JdbcUtil.getDate(rs.getDate("DefSchdDate")));
			pd.setEntityCode(rs.getString("EntityCode"));
			pd.setInstrumentType(rs.getString("InstrumentType"));

			return pd;
		}, batchID);
	}

	@Override
	public List<PresentmentDetail> getGroupByPartnerBankAndBank(long batchID) {
		String sql = "Select DefSchdDate, BankCode, EntityCode, PartnerBankId, InstrumentType From PRMNT_EXTRACTION_STAGE Where BatchID = ? Group By DefSchdDate, BankCode, EntityCode, PartnerBankId, InstrumentType";

		logger.debug(Literal.SQL.concat(sql));

		return jdbcOperations.query(sql, (rs, rowNum) -> {
			PresentmentDetail pd = new PresentmentDetail();

			pd.setDefSchdDate(JdbcUtil.getDate(rs.getDate("DefSchdDate")));
			pd.setBankCode(rs.getString("BankCode"));
			pd.setEntityCode(rs.getString("EntityCode"));
			pd.setPartnerBankId(JdbcUtil.getLong(rs.getObject("PartnerBankId")));
			pd.setInstrumentType(rs.getString("InstrumentType"));

			return pd;
		}, batchID);
	}

	@Override
	public List<PresentmentDetail> getGroupByBank(long batchID) {
		String sql = "Select DefSchdDate, BankCode, EntityCode, InstrumentType From PRMNT_EXTRACTION_STAGE Where BatchID = ? Group By DefSchdDate, BankCode, EntityCode, InstrumentType";

		logger.debug(Literal.SQL.concat(sql));

		return jdbcOperations.query(sql, (rs, rowNum) -> {
			PresentmentDetail pd = new PresentmentDetail();

			pd.setDefSchdDate(JdbcUtil.getDate(rs.getDate("DefSchdDate")));
			pd.setBankCode(rs.getString("BankCode"));
			pd.setEntityCode(rs.getString("EntityCode"));
			pd.setInstrumentType(rs.getString("InstrumentType"));

			return pd;
		}, batchID);
	}

	@Override
	public List<PresentmentDetail> getGroupByPartnerBank(long batchID) {
		String sql = "Select DefSchdDate, EntityCode, PartnerBankId, InstrumentType From PRMNT_EXTRACTION_STAGE Where BatchID = ? Group By DefSchdDate, EntityCode, PartnerBankId, InstrumentType";

		logger.debug(Literal.SQL.concat(sql));

		return jdbcOperations.query(sql, (rs, rowNum) -> {
			PresentmentDetail pd = new PresentmentDetail();

			pd.setDefSchdDate(JdbcUtil.getDate(rs.getDate("DefSchdDate")));
			pd.setEntityCode(rs.getString("EntityCode"));
			pd.setPartnerBankId(JdbcUtil.getLong(rs.getObject("PartnerBankId")));
			pd.setInstrumentType(rs.getString("InstrumentType"));

			return pd;
		}, batchID);
	}

	@Override
	public void updateHeader(List<PresentmentDetail> list) {
		String sql = "Update PRMNT_EXTRACTION_STAGE set HeaderID = ?, DueDate = ? Where ID = ?";

		logger.debug(Literal.SQL.concat(sql));

		jdbcOperations.batchUpdate(sql, new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				int index = 0;
				PresentmentDetail pd = list.get(i);

				ps.setLong(++index, pd.getHeaderId());
				ps.setDate(++index, JdbcUtil.getDate(pd.getDueDate()));

				ps.setLong(++index, pd.getId());
			}

			@Override
			public int getBatchSize() {
				return list.size();
			}
		});
	}

	@Override
	public void updateHeaderIdByDefault(long batchID, List<PresentmentDetail> list) {
		String sql = "Update PRMNT_EXTRACTION_STAGE set HeaderID = ?, DueDate = ? Where BatchID = ? and DefSchdDate = ? and EntityCode = ? and InstrumentType = ?";

		logger.debug(Literal.SQL.concat(sql));

		jdbcOperations.batchUpdate(sql, new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				int index = 1;
				PresentmentDetail pd = list.get(i);

				ps.setLong(index++, pd.getHeaderId());
				ps.setDate(index++, JdbcUtil.getDate(pd.getDueDate()));

				ps.setLong(index++, batchID);
				ps.setDate(index++, JdbcUtil.getDate(pd.getDefSchdDate()));
				ps.setString(index++, pd.getEntityCode());
				ps.setString(index, pd.getInstrumentType());

			}

			@Override
			public int getBatchSize() {
				return list.size();
			}
		});
	}

	@Override
	public void updateHeaderIdByPartnerBankAndBank(long batchID, List<PresentmentDetail> list) {
		StringBuilder sql = new StringBuilder("Update PRMNT_EXTRACTION_STAGE");
		sql.append(" Set HeaderID = ?, DueDate = ?");
		sql.append(" Where BatchID = ?");
		sql.append(" and DefSchdDate = ? and BankCode = ? and EntityCode = ?");
		sql.append(" and PartnerBankId = ? and InstrumentType = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		jdbcOperations.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				int index = 1;
				PresentmentDetail pd = list.get(i);

				ps.setLong(index++, pd.getHeaderId());
				ps.setDate(index++, JdbcUtil.getDate(pd.getDueDate()));

				ps.setLong(index++, batchID);
				ps.setDate(index++, JdbcUtil.getDate(pd.getDefSchdDate()));
				ps.setString(index++, pd.getBankCode());
				ps.setString(index++, pd.getEntityCode());
				ps.setObject(index++, pd.getPartnerBankId());
				ps.setString(index, pd.getInstrumentType());
			}

			@Override
			public int getBatchSize() {
				return list.size();
			}
		});
	}

	@Override
	public void updateHeaderIdByBank(long batchID, List<PresentmentDetail> list) {
		StringBuilder sql = new StringBuilder("Update PRMNT_EXTRACTION_STAGE set HeaderID = ?, DueDate = ?");
		sql.append(" Where BatchID = ?");
		sql.append(" and DefSchdDate = ? and BankCode = ? and EntityCode = ? and InstrumentType = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		jdbcOperations.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				int index = 1;
				PresentmentDetail pd = list.get(i);

				ps.setLong(index++, pd.getHeaderId());
				ps.setDate(index++, JdbcUtil.getDate(pd.getDueDate()));

				ps.setLong(index++, batchID);
				ps.setDate(index++, JdbcUtil.getDate(pd.getDefSchdDate()));
				ps.setString(index++, pd.getBankCode());
				ps.setString(index++, pd.getEntityCode());
				ps.setString(index, pd.getInstrumentType());
			}

			@Override
			public int getBatchSize() {
				return list.size();
			}
		});
	}

	@Override
	public void updateHeaderIdByPartnerBank(long batchID, List<PresentmentDetail> list) {
		StringBuilder sql = new StringBuilder("Update PRMNT_EXTRACTION_STAGE set HeaderID = ?, DueDate = ?");
		sql.append(" Where BatchID = ?");
		sql.append(" and DefSchdDate = ? and EntityCode = ? and PartnerBankId = ? and InstrumentType = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		jdbcOperations.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				int index = 1;
				PresentmentDetail pd = list.get(i);

				ps.setLong(index++, pd.getHeaderId());
				ps.setDate(index++, JdbcUtil.getDate(pd.getDueDate()));

				ps.setLong(index++, batchID);
				ps.setDate(index++, JdbcUtil.getDate(pd.getDefSchdDate()));
				ps.setString(index++, pd.getEntityCode());
				ps.setObject(index++, pd.getPartnerBankId());
				ps.setString(index, pd.getInstrumentType());
			}

			@Override
			public int getBatchSize() {
				return list.size();
			}
		});
	}

	@Override
	public long save(PresentmentDetail pd) {
		StringBuilder sql = new StringBuilder("Insert into PresentmentDetails");
		sql.append("(Id, PresentmentId, PresentmentRef, FinID, FinReference, SchDate, MandateId, SchdVersion");
		sql.append(", SchAmtDue, SchPriDue, SchPftDue, SchFeeDue, SchInsDue, SchPenaltyDue, AdvanceAmt, ExcessID");
		sql.append(", AdviseAmt, PresentmentAmt, ExcludeReason, BounceID, EmiNo, TDSAmount, Status, ReceiptID");
		sql.append(", EmployeeNo, EmployerId, EmployerName");
		sql.append(", Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode");
		sql.append(", TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
		sql.append(", ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL.concat(sql.toString()));

		jdbcOperations.update(sql.toString(), ps -> {
			int i = 1;

			ps.setLong(i++, pd.getId());
			ps.setLong(i++, pd.getHeaderId());
			ps.setString(i++, pd.getPresentmentRef());
			ps.setLong(i++, pd.getFinID());
			ps.setString(i++, pd.getFinReference());
			ps.setDate(i++, JdbcUtil.getDate(pd.getSchDate()));
			ps.setObject(i++, pd.getMandateId());
			ps.setInt(i++, pd.getSchdVersion());
			ps.setBigDecimal(i++, pd.getSchAmtDue());
			ps.setBigDecimal(i++, pd.getSchPriDue());
			ps.setBigDecimal(i++, pd.getSchPftDue());
			ps.setBigDecimal(i++, pd.getSchFeeDue());
			ps.setBigDecimal(i++, pd.getSchInsDue());
			ps.setBigDecimal(i++, pd.getSchPenaltyDue());
			ps.setBigDecimal(i++, pd.getAdvanceAmt());
			ps.setLong(i++, pd.getExcessID());
			ps.setBigDecimal(i++, pd.getAdviseAmt());
			ps.setBigDecimal(i++, pd.getPresentmentAmt());
			ps.setLong(i++, pd.getExcludeReason());
			ps.setLong(i++, pd.getBounceID());
			ps.setInt(i++, pd.getEmiNo());
			ps.setBigDecimal(i++, pd.gettDSAmount());
			ps.setString(i++, pd.getStatus());
			ps.setLong(i++, pd.getReceiptID());
			ps.setString(i++, pd.getEmployeeNo());
			ps.setObject(i++, pd.getEmployerId());
			ps.setString(i++, pd.getEmployerName());
			ps.setInt(i++, pd.getVersion());
			ps.setLong(i++, pd.getLastMntBy());
			ps.setTimestamp(i++, pd.getLastMntOn());
			ps.setString(i++, pd.getRecordStatus());
			ps.setString(i++, pd.getRoleCode());
			ps.setString(i++, pd.getNextRoleCode());
			ps.setString(i++, pd.getTaskId());
			ps.setString(i++, pd.getNextTaskId());
			ps.setString(i++, pd.getRecordType());
			ps.setLong(i, pd.getWorkflowId());
		});

		return pd.getId();
	}

	@Override
	public long savePresentmentHeader(PresentmentHeader ph) {
		StringBuilder sql = new StringBuilder("Insert into");
		sql.append(" PresentmentHeader");
		sql.append("(Id, BatchID, Reference, PresentmentDate, PartnerBankId, FromDate, ToDate, PresentmentType");
		sql.append(", Status, MandateType, EmandateSource, FinBranch, Schdate, LoanType, ImportStatusId");
		sql.append(", TotalRecords, ProcessedRecords, SuccessRecords, FailedRecords, Version, LastMntBy");
		sql.append(", LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType");
		sql.append(", WorkflowId, dBStatusId, bankCode, EntityCode");
		sql.append(") values(");
		sql.append("?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
		sql.append(", ?, ?, ?, ?");
		sql.append(")");

		logger.debug(Literal.SQL.concat(sql.toString()));

		jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, ph.getId());
			ps.setLong(index++, ph.getBatchID());
			ps.setString(index++, ph.getReference());
			ps.setDate(index++, JdbcUtil.getDate(ph.getPresentmentDate()));
			ps.setObject(index++, ph.getPartnerBankId());
			ps.setDate(index++, JdbcUtil.getDate(ph.getFromDate()));
			ps.setDate(index++, JdbcUtil.getDate(ph.getToDate()));
			ps.setString(index++, ph.getPresentmentType());
			ps.setInt(index++, ph.getStatus());
			ps.setString(index++, ph.getMandateType());
			ps.setString(index++, ph.getEmandateSource());
			ps.setString(index++, ph.getFinBranch());
			ps.setDate(index++, JdbcUtil.getDate(ph.getSchdate()));
			ps.setString(index++, ph.getLoanType());
			ps.setLong(index++, ph.getImportStatusId());
			ps.setInt(index++, ph.getTotalRecords());
			ps.setInt(index++, ph.getProcessedRecords());
			ps.setInt(index++, ph.getSuccessRecords());
			ps.setInt(index++, ph.getFailedRecords());
			ps.setInt(index++, ph.getVersion());
			ps.setLong(index++, ph.getLastMntBy());
			ps.setTimestamp(index++, ph.getLastMntOn());
			ps.setString(index++, ph.getRecordStatus());
			ps.setString(index++, ph.getRoleCode());
			ps.setString(index++, ph.getNextRoleCode());
			ps.setString(index++, ph.getTaskId());
			ps.setString(index++, ph.getNextTaskId());
			ps.setString(index++, ph.getRecordType());
			ps.setLong(index++, ph.getWorkflowId());
			ps.setLong(index++, ph.getdBStatusId());
			ps.setString(index++, ph.getBankCode());
			ps.setString(index, ph.getEntityCode());
		});

		return ph.getId();
	}

	@Override
	public int updateSchdWithPresentmentId(List<PresentmentDetail> presenetments) {
		String sql = "Update FinScheduleDetails Set PresentmentId = ? Where FinID = ? and SchDate = ? and  SchSeq = ?";

		logger.debug(Literal.SQL.concat(sql));

		return jdbcOperations.batchUpdate(sql, new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int index) throws SQLException {
				PresentmentDetail pd = presenetments.get(index);
				ps.setLong(1, pd.getId());
				ps.setLong(2, pd.getFinID());
				ps.setDate(3, JdbcUtil.getDate(pd.getSchDate()));
				ps.setInt(4, pd.getSchSeq());
			}

			@Override
			public int getBatchSize() {
				return presenetments.size();
			}
		}).length;
	}

	@Override
	public void clearQueue(long batchId) {
		jdbcOperations.update("Delete From PRMNT_EXTRACTION_STAGE Where BatchID = ?", batchId);
	}

	@Override
	public List<PresentmentHeader> getPresentmentHeaders(long batchID) {
		StringBuilder sql = new StringBuilder();
		sql.append("Select Id, Reference, EntityCode, Schdate, bd.BankCode, bd.BankName, pb.PartnerBankName");
		sql.append(", FromDate, ToDate, PresentmentDate, Status, MandateType, ph.RecordStatus, ph.RecordType");
		sql.append(", LoanType, pb.ACCOUNTNO PartnerAcctNumber, ph.PartnerBankId");
		sql.append(" From  PresentmentHeader ph");
		sql.append(" Left Join PartnerBanks pb on pb.PartnerBankId = ph.PartnerBankId");
		sql.append(" Left Join BmtBankDetail bd on bd.BankCode = ph.BankCode");
		sql.append(" Where BatchID = ? and Status = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.query(sql.toString(), ps -> {
			ps.setLong(1, batchID);
			ps.setInt(2, RepayConstants.PEXC_EXTRACT);
		}, (rs, rowNum) -> {
			PresentmentHeader ph = new PresentmentHeader();

			ph.setId(rs.getLong("Id"));
			ph.setReference(rs.getString("Reference"));
			ph.setEntityCode(rs.getString("EntityCode"));
			ph.setSchdate(rs.getDate("Schdate"));
			ph.setBankCode(rs.getString("BankCode"));
			ph.setBankName(rs.getString("BankName"));
			ph.setPartnerBankName(rs.getString("PartnerBankName"));
			ph.setFromDate(rs.getDate("FromDate"));
			ph.setToDate(rs.getDate("ToDate"));
			ph.setPresentmentDate(rs.getDate("PresentmentDate"));
			ph.setStatus(rs.getInt("Status"));
			ph.setMandateType(rs.getString("MandateType"));
			ph.setRecordStatus(rs.getString("RecordStatus"));
			ph.setRecordType(rs.getString("RecordType"));
			ph.setLoanType(rs.getString("LoanType"));
			ph.setPartnerAcctNumber(rs.getString("PartnerAcctNumber"));
			ph.setPartnerBankId(rs.getLong("PartnerBankId"));

			return ph;
		});
	}

	@Override
	public List<Long> getExcludeList(long id) {
		String sql = "Select ID From PresentmentDetails Where PresentmentId = ? and ExcludeReason != ?";

		logger.debug(Literal.SQL.concat(sql));

		return jdbcOperations.queryForList(sql, Long.class, id, RepayConstants.PEXC_EMIINCLUDE);

	}

	@Override
	public List<Long> getIncludeList(long id) {
		String sql = "Select ID From PresentmentDetails Where PresentmentId = ? and ExcludeReason = ?";

		logger.debug(Literal.SQL.concat(sql));

		return jdbcOperations.queryForList(sql, Long.class, id, RepayConstants.PEXC_EMIINCLUDE);
	}

	@Override
	public boolean searchIncludeList(long presentmentId, int excludereason) {
		String sql = "Select Count(Id) From PresentmentDetails Where PresentmentId = ? and ExcludeReason = ?";

		logger.debug(Literal.SQL.concat(sql));

		return jdbcOperations.queryForObject(sql, Integer.class, presentmentId, excludereason) > 0;
	}

	@Override
	public int approveExludes(long presentmentId) {
		String sql = "Update PresentmentDetails Set Status = ? Where Id = ? and Excludereason in (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

		logger.debug(Literal.SQL.concat(sql));

		Object[] args = new Object[] { RepayConstants.PEXC_APPROV, presentmentId, RepayConstants.PEXC_EMIHOLD,
				RepayConstants.PEXC_MANDATE_HOLD, RepayConstants.PEXC_MANDATE_NOTAPPROV,
				RepayConstants.PEXC_MANDATE_EXPIRY, RepayConstants.PEXC_MANUAL_EXCLUDE,
				RepayConstants.PEXC_MANDATE_REJECTED, RepayConstants.CHEQUESTATUS_PRESENT,
				RepayConstants.CHEQUESTATUS_BOUNCE, RepayConstants.CHEQUESTATUS_REALISE,
				RepayConstants.CHEQUESTATUS_REALISED };

		return this.jdbcOperations.update(sql, args);
	}

	private Map<Integer, Integer> getTotalPresentments(long presentmentId) {
		Map<Integer, Integer> totals = new HashMap<>();

		String sql = "Select ExcludeReason, Count(PresentmentID) from PresentmentDetails Where PresentmentID = ? group by ExcludeReason";

		logger.debug(Literal.SQL.concat(sql));

		return this.jdbcOperations.query(sql, (ResultSet rs) -> {
			while (rs.next()) {
				totals.put(rs.getInt(1), rs.getInt(2));

			}
			return totals;
		}, presentmentId);
	}

	@Override
	public int updateHeader(long presentmentId) {
		Map<Integer, Integer> presentments = getTotalPresentments(presentmentId);

		int total = 0;

		if (MapUtils.isEmpty(presentments)) {
			return 0;
		}

		for (Integer count : presentments.values()) {
			total = total + count;
		}

		int success = 0;

		if (presentments.containsKey(RepayConstants.PEXC_EMIINCLUDE)) {
			success = presentments.get(RepayConstants.PEXC_EMIINCLUDE);
		}

		if (presentments.containsKey(RepayConstants.PEXC_EMIINADVANCE)) {
			success = success + presentments.get(RepayConstants.PEXC_EMIINADVANCE);
		}

		int failure = total - success;

		int status = RepayConstants.PEXC_SEND_PRESENTMENT;

		String sql = "Update PresentmentHeader Set Status = ?, TotalRecords = ?, SuccessRecords = ?, FailedRecords = ? Where Id = ?";

		logger.debug(Literal.SQL.concat(sql));

		return this.jdbcOperations.update(sql, status, total, success, failure, presentmentId);
	}

	public Presentment getPartnerBankId(String finType, String mandateType) {
		String sql = "Select PartnerBankID, AccountNo From PresentmentPartnerBank Where FinType = ?";

		logger.debug(Literal.SQL.concat(sql));

		try {
			return jdbcOperations.queryForObject(sql, (rs, i) -> {
				Presentment p = new Presentment();
				p.setPartnerBankId(rs.getLong("PartnerBankID"));
				p.setAccountNo(rs.getString("AccountNo"));
				return p;
			}, finType);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND, e);
			return null;
		}
	}

	@Override
	public void updatePartnerBankID(long id, long partnerBankId) {
		String sql = "Update PresentmentHeader set PartnerBankId = ? Where ID = ?";

		logger.debug(Literal.SQL.concat(sql));

		this.jdbcOperations.update(sql, partnerBankId, id);
	}

	@Override
	public PresentmentDetail getPresentmentDetail(long extrationID) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" Id, HeaderID, DueDate, FinId, SchdVersion, FinReference, FinType, ProductCategory, FinBranch");
		sql.append(", EntityCode, BpiTreatment, GrcPeriodEndDate, GrcAdvType, AdvType, AdvStage");
		sql.append(", SchDate, DefSchdDate, SchSeq, InstNumber, BpiOrHoliday");
		sql.append(", ProfitSchd, PrincipalSchd, FeeSchd, TdsAmount");
		sql.append(", SchdPftPaid, SchdPriPaid, SchdFeePaid, TdsPaid, RePresentUploadID");
		sql.append(", MandateId, MandateType, EmandateSource, MandateStatus, MandateExpiryDate");
		sql.append(", ChequeId, ChequeType, ChequeStatus, ChequeDate");
		sql.append(", PartnerBankId, BranchCode, BankCode, InstrumentType");
		sql.append(", EmployeeNo, EmployerId, EmployerName, ChequeAmount");
		sql.append(" From PRMNT_EXTRACTION_STAGE");
		sql.append(" Where Id = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
			PresentmentDetail pd = new PresentmentDetail();

			pd.setId(rs.getLong("Id"));
			pd.setHeaderId(JdbcUtil.getLong(rs.getObject("HeaderID")));
			pd.setDueDate(rs.getDate("DueDate"));
			pd.setFinID(rs.getLong("FinId"));
			pd.setSchdVersion(rs.getInt("SchdVersion"));
			pd.setFinReference(rs.getString("FinReference"));
			pd.setFinType(rs.getString("FinType"));
			pd.setProductCategory(rs.getString("ProductCategory"));
			pd.setFinBranch(rs.getString("FinBranch"));
			pd.setEntityCode(rs.getString("EntityCode"));
			pd.setBpiTreatment(rs.getString("BpiTreatment"));
			pd.setGrcPeriodEndDate(rs.getDate("GrcPeriodEndDate"));
			pd.setGrcAdvType(rs.getString("GrcAdvType"));
			pd.setAdvType(rs.getString("AdvType"));
			pd.setAdvStage(rs.getString("AdvStage"));
			pd.setSchDate(rs.getDate("SchDate"));
			pd.setDefSchdDate(rs.getDate("DefSchdDate"));
			pd.setSchSeq(rs.getInt("SchSeq"));
			pd.setInstNumber(rs.getInt("InstNumber"));
			pd.setEmiNo(rs.getInt("InstNumber"));
			pd.setBpiOrHoliday(rs.getString("BpiOrHoliday"));
			pd.setProfitSchd(rs.getBigDecimal("ProfitSchd"));
			pd.setPrincipalSchd(rs.getBigDecimal("PrincipalSchd"));
			pd.setFeeSchd(rs.getBigDecimal("FeeSchd"));
			pd.settDSAmount(rs.getBigDecimal("TdsAmount"));
			pd.setSchdPftPaid(rs.getBigDecimal("SchdPftPaid"));
			pd.setSchdPriPaid(rs.getBigDecimal("SchdPriPaid"));
			pd.setSchdFeePaid(rs.getBigDecimal("SchdFeePaid"));
			pd.setTdsPaid(rs.getBigDecimal("TdsPaid"));
			pd.setRePresentUploadID(JdbcUtil.getLong(rs.getObject("RePresentUploadID")));
			pd.setMandateId(JdbcUtil.getLong(rs.getObject("MandateId")));
			pd.setMandateType(rs.getString("MandateType"));
			pd.setEmandateSource(rs.getString("EmandateSource"));
			pd.setMandateStatus(rs.getString("MandateStatus"));
			pd.setMandateExpiryDate(rs.getDate("MandateExpiryDate"));
			pd.setChequeId(JdbcUtil.getLong(rs.getObject("ChequeId")));
			pd.setChequeType(rs.getString("ChequeType"));
			pd.setChequeStatus(rs.getString("ChequeStatus"));
			pd.setChequeDate(rs.getDate("ChequeDate"));
			pd.setChequeAmount(rs.getBigDecimal("ChequeAmount"));
			pd.setPartnerBankId(JdbcUtil.getLong(rs.getObject("PartnerBankId")));
			pd.setBranchCode(rs.getString("BranchCode"));
			pd.setBankCode(rs.getString("BankCode"));
			pd.setInstrumentType(rs.getString("InstrumentType"));
			pd.setEmployeeNo(rs.getString("EmployeeNo"));
			pd.setEmployerId(JdbcUtil.getLong(rs.getObject("EmployerId")));
			pd.setEmployerName(rs.getString("EmployerName"));

			return pd;

		}, extrationID);
	}

	@Override
	public PresentmentDetail getPresentmenToPost(long presentmentId) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" fm.CustId, fm.FinBranch, fm.FinType, pd.Id, pd.PresentmentId");
		sql.append(", fm.FinID, pd.FinReference, pd.SchDate, pd.MandateId, pd.AdvanceAmt, pd.ExcessID");
		sql.append(", pd.PresentmentAmt, pd.ExcludeReason, pd.BounceID, ph.MandateType, pb.AccountNo, pb.AcType");
		sql.append(", pb.PartnerBankId, fm.finIsActive");
		sql.append(" From PresentmentDetails pd ");
		sql.append(" Inner join PresentmentHeader ph on ph.Id = pd.PresentmentId");
		sql.append(" Left join PartnerBanks pb on pb.PartnerBankId = ph.PartnerBankId");
		sql.append(" Inner join Financemain fm on pd.FinID = fm.FinID");
		sql.append(" Where pd.Id = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
			PresentmentDetail pd = new PresentmentDetail();

			pd.setFinType(rs.getString("FinType"));
			pd.setId(rs.getLong("Id"));
			pd.setHeaderId(rs.getLong("PresentmentId"));
			pd.setFinID(rs.getLong("FinID"));
			pd.setFinReference(rs.getString("FinReference"));
			pd.setSchDate(rs.getTimestamp("SchDate"));
			pd.setMandateId(rs.getLong("MandateId"));
			pd.setAdvanceAmt(rs.getBigDecimal("AdvanceAmt"));
			pd.setExcessID(rs.getLong("ExcessID"));
			pd.setPresentmentAmt(rs.getBigDecimal("PresentmentAmt"));
			pd.setExcludeReason(rs.getInt("ExcludeReason"));
			pd.setBounceID(rs.getLong("BounceID"));
			pd.setInstrumentType(rs.getString("MandateType"));
			pd.setAccountNo(rs.getString("AccountNo"));
			pd.setAcType(rs.getString("AcType"));
			pd.setFinisActive(rs.getBoolean("finIsActive"));

			return pd;
		}, presentmentId);
	}

	@Override
	public List<PresentmentDetail> getSendToPresentmentDetails(long presentmentId) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" pd.Id, pd.PresentmentId, pd.FinID, pd.FinReference");
		sql.append(", pd.SchDate, pd.MandateId, pd.SchAmtDue, pd.SchPriDue");
		sql.append(", pd.SchPftDue, pd.SchFeeDue, pd.SchInsDue, pd.SchPenaltyDue, pd.AdvanceAmt, pd.ExcessID");
		sql.append(", pd.AdviseAmt, pd.PresentmentAmt, pd.EmiNo, pd.Status, pd.PresentmentRef, pd.EcsReturn");
		sql.append(", pd.ReceiptID, pd.ExcludeReason, pd.Version, pd.LastMntOn, pd.LastMntBy, pd.RecordStatus");
		sql.append(", pd.RoleCode, pd.NextRoleCode, pd.TaskId, pd.NextTaskId, pd.RecordType, pd.WorkflowId");
		sql.append(", pb.AccountNo, pb.AcType");
		sql.append(" From PresentmentDetails pd");
		sql.append(" inner join PresentmentHeader ph on ph.ID = pd.PresentmentID");
		sql.append(" inner join PartnerBanks pb on pb.PartnerBankID = ph.PartnerBankID ");
		sql.append(" Where pd.PresentmentId = ?");
		sql.append(" and pd.ExcludeReason = ? and pd.Status <> ?");
		sql.append(" and pd.Receiptid = ? ");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;
			ps.setLong(index++, presentmentId);
			ps.setInt(index++, RepayConstants.PEXC_EMIINCLUDE);
			ps.setString(index++, RepayConstants.PEXC_APPROV);
			ps.setInt(index, 0);
		}, (rs, rowNum) -> {
			PresentmentDetail pd = new PresentmentDetail();

			pd.setId(rs.getLong("Id"));
			pd.setHeaderId(rs.getLong("PresentmentId"));
			pd.setFinID(rs.getLong("FinID"));
			pd.setFinReference(rs.getString("FinReference"));
			pd.setSchDate(rs.getTimestamp("SchDate"));
			pd.setMandateId(rs.getLong("MandateId"));
			pd.setSchAmtDue(rs.getBigDecimal("SchAmtDue"));
			pd.setSchPriDue(rs.getBigDecimal("SchPriDue"));
			pd.setSchPftDue(rs.getBigDecimal("SchPftDue"));
			pd.setSchFeeDue(rs.getBigDecimal("SchFeeDue"));
			pd.setSchInsDue(rs.getBigDecimal("SchInsDue"));
			pd.setSchPenaltyDue(rs.getBigDecimal("SchPenaltyDue"));
			pd.setAdvanceAmt(rs.getBigDecimal("AdvanceAmt"));
			pd.setExcessID(rs.getLong("ExcessID"));
			pd.setAdviseAmt(rs.getBigDecimal("AdviseAmt"));
			pd.setPresentmentAmt(rs.getBigDecimal("PresentmentAmt"));
			pd.setEmiNo(rs.getInt("EmiNo"));
			pd.setStatus(rs.getString("Status"));
			pd.setPresentmentRef(rs.getString("PresentmentRef"));
			pd.setEcsReturn(rs.getString("EcsReturn"));
			pd.setReceiptID(rs.getLong("ReceiptID"));
			pd.setExcludeReason(rs.getInt("ExcludeReason"));
			pd.setVersion(rs.getInt("Version"));
			pd.setLastMntOn(rs.getTimestamp("LastMntOn"));
			pd.setLastMntBy(rs.getLong("LastMntBy"));
			pd.setRecordStatus(rs.getString("RecordStatus"));
			pd.setRoleCode(rs.getString("RoleCode"));
			pd.setNextRoleCode(rs.getString("NextRoleCode"));
			pd.setTaskId(rs.getString("TaskId"));
			pd.setNextTaskId(rs.getString("NextTaskId"));
			pd.setRecordType(rs.getString("RecordType"));
			pd.setWorkflowId(rs.getLong("WorkflowId"));
			pd.setAccountNo(rs.getString("AccountNo"));
			pd.setAcType(rs.getString("AcType"));

			return pd;
		});

	}

	@Override
	public void updatePresentmentIdAsZero(long presentmentId) {
		String sql = "Update FinScheduleDetails Set PresentmentId = ? Where PresentmentId = ?";

		this.jdbcOperations.update(sql, ps -> {
			ps.setLong(1, 0);
			ps.setLong(2, presentmentId);
		});
	}

	@Override
	public void updateExcludeReason(long presentmentId, int manualExclude) {
		String sql = "Update presentmentdetails Set ExcludeReason = ? Where ID = ?";

		logger.debug(Literal.SQL.concat(sql));

		try {
			this.jdbcOperations.update(sql, ps -> {
				ps.setInt(1, manualExclude);
				ps.setLong(2, presentmentId);
			});
		} catch (Exception e) {
			throw new ConcurrencyException();
		}
	}

	public int extractPDC(long batchID, long finID, Date dueDate, Long rePresentUploadID, String instrumentType) {
		StringBuilder sql = new StringBuilder(getExtractQuery(instrumentType));
		sql.append(" Where fm.FinId = ? and (SchDate = ? or DefSchddate = ?)");

		logger.debug(Literal.SQL.concat(sql.toString()));

		String bankCode = SysParamUtil.getValueAsString(SMTParameterConstants.BANK_CODE);

		return this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, batchID);
			ps.setLong(index++, rePresentUploadID);
			ps.setInt(index++, 1);
			ps.setString(index++, InstrumentType.PDC.name());
			ps.setString(index++, bankCode);
			ps.setLong(index++, finID);
			ps.setDate(index++, JdbcUtil.getDate(dueDate));
			ps.setDate(index++, JdbcUtil.getDate(dueDate));

		});
	}

	public int extractDAS(long batchID, long finID, Date dueDate, Long rePresentUploadID) {
		StringBuilder sql = new StringBuilder(getExtractQuery(InstrumentType.DAS.name()));
		sql.append(" Where fm.FinId = ? and (SchDate = ? or DefSchddate = ?) and MandateType = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, batchID);
			ps.setLong(index++, rePresentUploadID);
			ps.setInt(index++, 1);
			ps.setString(index++, InstrumentType.DAS.name());
			ps.setLong(index++, finID);
			ps.setDate(index++, JdbcUtil.getDate(dueDate));
			ps.setDate(index++, JdbcUtil.getDate(dueDate));
			ps.setString(index++, InstrumentType.DAS.name());
		});
	}

	@Override
	public int extract(long batchID, PresentmentHeader ph) {
		long finID = ph.getFinID();
		Date dueDate = ph.getDueDate();
		String instrumentType = ph.getMandateType();
		Long rePresentUploadID = ph.getRePresentUploadID();
		String partnerBankCode = ph.getPartnerBankCode();
		String bankCode = SysParamUtil.getValueAsString(SMTParameterConstants.BANK_CODE);

		InstrumentType type = InstrumentType.getType(instrumentType);

		if (type == InstrumentType.PDC && partnerBankCode != null && bankCode != null
				&& partnerBankCode.equals(bankCode)) {
			instrumentType = InstrumentType.IPDC.name();
			ph.setMandateType(InstrumentType.IPDC.name());
		}

		if (type == InstrumentType.PDC) {
			return extractPDC(batchID, finID, dueDate, rePresentUploadID, instrumentType);
		}

		if (type == InstrumentType.DAS) {
			return extractDAS(batchID, finID, dueDate, rePresentUploadID);
		}

		StringBuilder sql = new StringBuilder(getExtractQuery(instrumentType));
		sql.append(" Where fm.FinId = ? and (SchDate = ? or DefSchddate = ?)");

		logger.debug(Literal.SQL.concat(sql.toString()));

		final String instType = instrumentType;

		return this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, batchID);
			ps.setLong(index++, rePresentUploadID);
			ps.setInt(index++, 1);
			ps.setString(index++, instType);
			ps.setLong(index++, finID);
			ps.setDate(index++, JdbcUtil.getDate(dueDate));
			ps.setDate(index++, JdbcUtil.getDate(dueDate));
		});
	}

	@Override
	public List<PresentmentHeader> getpresentmentHeaderList(List<Long> headerId) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" fm.FinID, rpd.DueDate, fm.FinRepayMethod, rpd.Id, bb.BankCode");
		sql.append(" From FILE_UPLOAD_HEADER rph");
		sql.append(" Inner Join REPRESENT_UPLOADS rpd on rph.Id = rpd.HeaderId");
		sql.append(" Inner Join FinanceMain fm on fm.FinID = rpd.FinID");
		sql.append(" Left Join ChequeHeader ch on ch.FinId = fm.FinId");
		sql.append(" Left Join ChequeDetail cd on cd.HeaderId = ch.HeaderId and cd.Chequedate = rpd.DueDate");
		sql.append(" Left Join BankBranches bb on bb.BankBranchId = cd.BankBranchId");
		sql.append(" Where rph.Id in (");
		sql.append(JdbcUtil.getInCondition(headerId));
		sql.append(") and rpd.Progress = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 0;

			for (Long id : headerId) {
				ps.setLong(++index, id);
			}

			ps.setInt(++index, EodConstants.PROGRESS_SUCCESS);
		}, (rs, rowNum) -> {
			PresentmentHeader ph = new PresentmentHeader();

			ph.setFinID(rs.getLong("FinID"));
			ph.setDueDate(rs.getDate("DueDate"));
			ph.setMandateType(rs.getString("FinRepayMethod"));
			ph.setRePresentUploadID(JdbcUtil.getLong(rs.getObject("Id")));
			ph.setPartnerBankCode(rs.getString("BankCode"));

			return ph;
		});
	}

	@Override
	public void updateRepresentWithPresentmentId(List<PresentmentDetail> presenetments) {
		String sql = "Update REPRESENT_UPLOADS Set PresentmentID = ? Where ID = ?";

		logger.debug(Literal.SQL.concat(sql));

		this.jdbcOperations.batchUpdate(sql, new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int index) throws SQLException {
				PresentmentDetail pd = presenetments.get(index);

				ps.setLong(1, pd.getId());
				ps.setLong(2, pd.getRePresentUploadID());
			}

			@Override
			public int getBatchSize() {
				return presenetments.size();
			}
		});
	}

	@Override
	public Long getPreviousMandateID(long finID, Date schDate) {
		String sql = "Select Id, MandateID from PresentmentDetails Where FinID = ? and SchDate = ? order by Id desc";

		List<Long> list = this.jdbcOperations.query(sql, ps -> {
			ps.setLong(1, finID);
			ps.setDate(2, JdbcUtil.getDate(schDate));
		}, (rs, rowNum) -> {
			return JdbcUtil.getLong(rs.getObject("MandateID"));
		});

		if (CollectionUtils.isEmpty(list)) {
			return null;
		}

		return list.get(0);
	}

	@Override
	public Map<String, String> getUpfrontBounceCodes() {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" pec.Code, pec.InstrumentType, br.ReturnCode");
		sql.append(" From Presentment_Exclude_Codes pec");
		sql.append(" Inner Join BounceReasons br on br.BounceID = pec.BounceID");

		logger.debug(Literal.SQL.concat(sql.toString()));

		Map<String, String> map = new HashMap<>();

		List<PresentmentExcludeCode> list = this.jdbcOperations.query(sql.toString(), (rs, rowNum) -> {
			PresentmentExcludeCode pec = new PresentmentExcludeCode();

			pec.setCode(rs.getString("Code"));
			pec.setInstrumentType(rs.getString("InstrumentType"));
			pec.setReturnCode(rs.getString("ReturnCode"));

			return pec;
		});

		for (PresentmentExcludeCode pec : list) {
			map.put(pec.getCode().concat("$").concat(pec.getInstrumentType()), pec.getReturnCode());
		}

		return map;
	}

	@Override
	public Map<String, Integer> batchSizeByInstrumentType() {
		Map<String, Integer> batchSizeMap = new HashMap<>();

		String sql = "SELECT CODE, BATCHSIZE FROM INSTRUMENT_TYPES";

		logger.debug(Literal.SQL.concat(sql));

		return this.jdbcOperations.query(sql, (ResultSet rs) -> {
			while (rs.next()) {
				batchSizeMap.put(rs.getString(1), rs.getInt(2));
			}
			return batchSizeMap;
		});
	}

	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED)
	public int getRecordsByWaiting(String clearingStatus) {
		StringBuilder sql = new StringBuilder();
		sql.append("Select count(prd.ID) From PRESENTMENT_RESP_HEADER prh");
		sql.append(" Inner Join PRESENTMENT_RESP_DTLS prd on prd.Header_ID  = prh.ID");
		sql.append(" Where prh.Progress = ? and Event = ? and prd.PROCESS_FLAG = ? and CLEARING_STATUS = ? ");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.queryForObject(sql.toString(), Integer.class, 1, "IMPORT", 0, clearingStatus);
	}

	@Override
	public PresentmentDetail getPresentmenForResponse(Long responseID) {
		StringBuilder sql = new StringBuilder("SELECT");
		sql.append(" PRD.HEADER_ID, PRD.BRANCH_CODE, FM.FINID, FM.FINREFERENCE, PRD.HOST_REFERENCE, PRD.INSTALMENT_NO");
		sql.append(", PRD.AMOUNT_CLEARED, PRD.CLEARING_DATE, PRD.CLEARING_STATUS, PRD.BOUNCE_CODE, BOUNCE_REMARKS");
		sql.append(", PRD.ID RESPONSEID, PD.ID, PD.PRESENTMENTID, PD.MANDATEID, PH.MANDATETYPE");
		sql.append(", PD.SCHDATE, PD.SCHAMTDUE, PD.SCHPRIDUE, PD.SCHPFTDUE");
		sql.append(", PD.SCHFEEDUE, PD.SCHINSDUE, PD.SCHPENALTYDUE");
		sql.append(", PD.ADVANCEAMT, PD.EXCESSID, PD.ADVISEAMT, PD.PRESENTMENTAMT");
		sql.append(", PD.TDSAMOUNT, PD.EXCLUDEREASON, PD.EMINO, PD.STATUS, PD.PRESENTMENTREF");
		sql.append(", PD.ECSRETURN, PD.RECEIPTID, PD.ERRORCODE, PD.ERRORDESC, PD.MANUALADVISEID");
		sql.append(", FM.FINISACTIVE, FM.FINTYPE, PRD.ACCOUNT_NUMBER, PRD.UTR_Number, PRD.FateCorrection");
		sql.append(", PB.ACTYPE");
		sql.append(" FROM PRESENTMENT_RESP_DTLS PRD");
		sql.append(" INNER JOIN PRESENTMENTDETAILS PD ON PD.PRESENTMENTREF = PRD.PRESENTMENT_REFERENCE");
		sql.append(" INNER JOIN PRESENTMENTHEADER PH ON PH.ID = PD.PRESENTMENTID");
		sql.append(" INNER JOIN FINANCEMAIN FM ON FM.FINID = PD.FINID");
		sql.append(" INNER JOIN PARTNERBANKS PB ON PB.PARTNERBANKID = PH.PARTNERBANKID");
		sql.append(" where PRD.ID = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
			PresentmentDetail pd = new PresentmentDetail();

			pd.setId(rs.getLong("ID"));
			pd.setResponseId(rs.getLong("RESPONSEID"));
			pd.setHeaderId(rs.getLong("HEADER_ID"));
			pd.setFinID(rs.getLong("FINID"));
			pd.setFinReference(rs.getString("FINREFERENCE"));
			pd.setHostReference(rs.getString("HOST_REFERENCE"));
			pd.setFinType(rs.getString("FINTYPE"));
			pd.setFinisActive(rs.getBoolean("FINISACTIVE"));
			pd.setSchDate(rs.getDate("SCHDATE"));
			pd.setMandateId(rs.getLong("MANDATEID"));
			pd.setMandateType(rs.getString("MANDATETYPE"));
			pd.setSchAmtDue(rs.getBigDecimal("SCHAMTDUE"));
			pd.setSchPriDue(rs.getBigDecimal("SCHPRIDUE"));
			pd.setSchPftDue(rs.getBigDecimal("SCHPFTDUE"));
			pd.setSchFeeDue(rs.getBigDecimal("SCHFEEDUE"));
			pd.setSchInsDue(rs.getBigDecimal("SCHINSDUE"));
			pd.setSchPenaltyDue(rs.getBigDecimal("SCHPENALTYDUE"));
			pd.setAdvanceAmt(rs.getBigDecimal("ADVANCEAMT"));
			pd.setExcessID(rs.getLong("EXCESSID"));
			pd.setAdviseAmt(rs.getBigDecimal("ADVISEAMT"));
			pd.setPresentmentAmt(rs.getBigDecimal("PRESENTMENTAMT"));
			pd.settDSAmount(rs.getBigDecimal("TDSAMOUNT"));
			pd.setExcludeReason(rs.getInt("EXCLUDEREASON"));
			pd.setEmiNo(rs.getInt("EMINO"));
			pd.setStatus(rs.getString("STATUS"));
			pd.setBounceCode(rs.getString("BOUNCE_CODE"));
			pd.setBounceRemarks(rs.getString("BOUNCE_REMARKS"));
			pd.setClearingStatus(rs.getString("CLEARING_STATUS"));
			pd.setPresentmentRef(rs.getString("PRESENTMENTREF"));
			pd.setEcsReturn(rs.getString("ECSRETURN"));
			pd.setReceiptID(rs.getLong("RECEIPTID"));
			pd.setErrorCode(rs.getString("ERRORCODE"));
			pd.setErrorDesc(rs.getString("ERRORDESC"));
			pd.setManualAdviseId(JdbcUtil.getLong(rs.getObject("MANUALADVISEID")));
			pd.setAccountNo(rs.getString("ACCOUNT_NUMBER"));
			pd.setUtrNumber(rs.getString("UTR_Number"));
			pd.setFateCorrection(rs.getString("FateCorrection"));
			pd.setAcType(rs.getString("ACTYPE"));

			return pd;
		}, responseID);
	}

	@Override
	public List<Long> getResponseHeadersByBatch(long batchID, String responseType) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT DISTINCT HEADER_ID FROM PRESENTMENT_RESP_DTLS PRD");

		if ("S".equals(responseType)) {
			sql.append(" INNER JOIN PRMNT_RESP_SUCCESS_QUEUE PRSQ ON PRSQ.REFERENCEID = PRD.ID");
		} else {
			sql.append(" INNER JOIN PRMNT_RESP_BOUNCE_QUEUE PRSQ ON PRSQ.REFERENCEID = PRD.ID");
		}

		sql.append(" WHERE PRSQ.BATCHID = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return jdbcOperations.queryForList(sql.toString(), Long.class, batchID);
	}

	@Override
	public List<Long> getPresentmentIdListByRespBatch(long headerId) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT DISTINCT PD.ID FROM PRESENTMENT_RESP_DTLS PRD");
		sql.append(" INNER JOIN PRESENTMENTDETAILS PD ON PD.PRESENTMENTREF = PRD.PRESENTMENT_REFERENCE");
		sql.append(" INNER JOIN PRESENTMENTHEADER PH ON PH.ID = PD.PRESENTMENTID");
		sql.append(" WHERE PRD.HEADER_ID = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return jdbcOperations.queryForList(sql.toString(), Long.class, headerId);
	}

	@Override
	public List<String> getStatusByPresentmentDetail(Long id) {
		String sql = "SELECT STATUS FROM PRESENTMENTDETAILS WHERE ID = ? AND EXCLUDEREASON = ?";

		logger.debug(Literal.SQL.concat(sql));

		return jdbcOperations.queryForList(sql, String.class, id, 0);
	}

	@Override
	public List<String> getStatusByPresentmentHeader(Long presentmentId) {
		String sql = "SELECT STATUS FROM PRESENTMENTDETAILS WHERE PRESENTMENTID = ? AND EXCLUDEREASON = ?";

		logger.debug(Literal.SQL.concat(sql));

		return jdbcOperations.queryForList(sql, String.class, presentmentId, 0);
	}

	@Override
	public long getPresentmentDetailPresenmentId(Long id) {
		String sql = "SELECT PRESENTMENTID FROM PRESENTMENTDETAILS WHERE ID = ?";

		logger.debug(Literal.SQL.concat(sql));

		return jdbcOperations.queryForObject(sql, long.class, id);
	}

	@Override
	public void updateHeaderCounts(Long id, int successCount, int failedCount) {
		String sql = "UPDATE PRESENTMENTHEADER SET Resp_Success = ?, Resp_Failed = ? WHERE ID = ?";

		logger.debug(Literal.SQL.concat(sql));

		jdbcOperations.update(sql, ps -> {
			ps.setInt(1, successCount);
			ps.setInt(2, failedCount);
			ps.setLong(3, id);
		});

	}

	@Override
	public void updateHeaderStatus(Long id, int status) {
		String sql = "UPDATE PresentmentHeader SET STATUS = ? WHERE ID = ?";

		logger.debug(Literal.SQL.concat(sql));

		this.jdbcOperations.update(sql, ps -> {
			ps.setInt(1, status);
			ps.setLong(2, id);
		});
	}

	@Override
	public void updateResponseHeader(long headerId, int totalRecords, int successRecords, int failedRecords,
			String status, String remarks) {

		Timestamp curTimeStamp = new Timestamp(System.currentTimeMillis());

		StringBuilder sql = new StringBuilder();
		sql.append("UPDATE PRESENTMENT_RESP_HEADER SET");
		sql.append(" TOTAL_RECORDS = ?, SUCESS_RECORDS = ?, FAILURE_RECORDS = ?,");
		sql.append(" STATUS = ?, REMARKS = ?, END_TIME = ? WHERE ID = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		jdbcOperations.update(sql.toString(), ps -> {
			int index = 0;

			ps.setInt(++index, totalRecords);
			ps.setInt(++index, successRecords);
			ps.setInt(++index, failedRecords);
			ps.setString(++index, status);
			ps.setString(++index, remarks);
			ps.setTimestamp(++index, curTimeStamp);
			ps.setLong(++index, headerId);
		});
	}

	@Override
	public void updateResposeStatus(long responseID, String pexcFailure, String errorMessage, int processFlag) {
		String sql = "UPDATE PRESENTMENT_RESP_DTLS SET ERROR_CODE = ?, ERROR_DESCRIPTION = ?, PROCESS_FLAG = ?  Where ID = ?";

		logger.debug(Literal.SQL.concat(sql));

		jdbcOperations.update(sql, ps -> {
			ps.setString(1, pexcFailure);
			ps.setString(2, errorMessage);
			ps.setInt(3, processFlag);
			ps.setLong(4, responseID);
		});
	}

	@Override
	public List<String> getInstrumentTypes(long batchID) {
		String sql = "Select distinct MandateType From PresentmentHeader Where BatchID = ?";

		logger.debug(Literal.SQL.concat(sql));

		return jdbcOperations.queryForList(sql.toString(), String.class, batchID);
	}

	@Override
	public void groupByInclude(long batchID, String instrumentType, PresentmentEngine presentmentEngine,
			Map<Long, Integer> headerMap, Integer batchSize, List<PresentmentDetail> list) {
		StringBuilder sql = new StringBuilder();
		sql.append("Select pd.ID, ph.Id HeaderId, ph.MandateType");
		sql.append(" From PresentmentHeader ph");
		sql.append(" Inner Join PresentmentDetails pd on pd.PresentmentID = ph.ID");
		sql.append(" Where ph.BatchID = ? and ph.MandateType = ? and ExcludeReason = ?");
		sql.append(" order by ph.Id");

		jdbcOperations.query(sql.toString(), ps -> {
			ps.setLong(1, batchID);
			ps.setString(2, instrumentType);
			ps.setInt(3, RepayConstants.PEXC_EMIINCLUDE);
		}, (rs, rowNum) -> {
			PresentmentDetail pd = new PresentmentDetail();

			pd.setId(rs.getLong("ID"));
			pd.setHeaderId(rs.getLong("HeaderId"));
			pd.setMandateType(rs.getString("MandateType"));

			presentmentEngine.groupByInclude(pd, headerMap, batchSize, list);

			return pd;
		});
	}

	@Override
	public void updateHeaderByInclude(List<PresentmentDetail> list) {
		String sql = "Update PresentmentDetails set PresentmentID = ? Where ID = ?";

		logger.debug(Literal.SQL.concat(sql));

		jdbcOperations.batchUpdate(sql, new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				PresentmentDetail pd = list.get(i);

				ps.setLong(1, pd.getHeaderId());
				ps.setLong(2, pd.getId());
			}

			@Override
			public int getBatchSize() {
				return list.size();
			}
		});
	}

	@Override
	public long getNextValue() {
		return getNextValue("SeqPresentmentDetails");
	}

	@Override
	public long getSeqNumber(String tableName) {
		return getNextValue(tableName);
	}

	@Override
	public void updateBatch(Long batchId, String remarks) {
		logger.debug(Literal.ENTERING);

		String sql = "Update PRMNT_BATCH_JOBS Set End_Time = ?, Status = ?, Remarks = ? Where ID = ?";

		this.jdbcOperations.update(sql, ps -> {
			ps.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
			ps.setString(2, "FAILED");
			ps.setString(3, remarks);
			ps.setLong(4, batchId);

		});

		logger.debug(Literal.LEAVING);
	}

	@Override
	public int updateRespProcessFlag(long batchID, int processFlag, String responseType) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("UPDATE PRESENTMENT_RESP_DTLS Set PROCESS_FLAG = ? Where ID IN (");
		if ("S".equals(responseType)) {
			sql.append("SELECT REFERENCEID FROM PRMNT_RESP_SUCCESS_QUEUE WHERE BATCHID = ?");
		} else {
			sql.append("SELECT REFERENCEID FROM PRMNT_RESP_BOUNCE_QUEUE WHERE BATCHID = ?");
		}
		sql.append(")");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.update(sql.toString(), processFlag, batchID);

	}

}
