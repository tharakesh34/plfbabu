package com.pennant.backend.dao.finance.impl;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;

import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.finance.FinanceWriteoffDAO;
import com.pennant.backend.model.finance.FinWriteoffPayment;
import com.pennant.backend.model.finance.FinanceWriteoff;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;

public class FinanceWriteoffDAOImpl extends BasicDao<FinanceWriteoff> implements FinanceWriteoffDAO {
	private static Logger logger = LogManager.getLogger(FinanceWriteoffDAOImpl.class);

	public FinanceWriteoffDAOImpl() {
		super();
	}

	@Override
	public FinanceWriteoff getFinanceWriteoffById(long finID, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FinID, FinReference, WriteoffDate, SeqNo, WrittenoffPri, WrittenoffPft, CurODPri");
		sql.append(", CurODPft, UnPaidSchdPri, UnPaidSchdPft, PenaltyAmount, ProvisionedAmount, WriteoffPrincipal");
		sql.append(", WriteoffProfit, AdjAmount, Remarks, WrittenoffSchFee, UnpaidSchFee, WriteoffSchFee");
		sql.append(" From FinWriteoffDetail");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, num) -> {
				FinanceWriteoff fwo = new FinanceWriteoff();

				fwo.setFinID(rs.getLong("FinID"));
				fwo.setFinReference(rs.getString("FinReference"));
				fwo.setWriteoffDate(rs.getTimestamp("WriteoffDate"));
				fwo.setSeqNo(rs.getInt("SeqNo"));
				fwo.setWrittenoffPri(rs.getBigDecimal("WrittenoffPri"));
				fwo.setWrittenoffPft(rs.getBigDecimal("WrittenoffPft"));
				fwo.setCurODPri(rs.getBigDecimal("CurODPri"));
				fwo.setCurODPft(rs.getBigDecimal("CurODPft"));
				fwo.setUnPaidSchdPri(rs.getBigDecimal("UnPaidSchdPri"));
				fwo.setUnPaidSchdPft(rs.getBigDecimal("UnPaidSchdPft"));
				fwo.setPenaltyAmount(rs.getBigDecimal("PenaltyAmount"));
				fwo.setProvisionedAmount(rs.getBigDecimal("ProvisionedAmount"));
				fwo.setWriteoffPrincipal(rs.getBigDecimal("WriteoffPrincipal"));
				fwo.setWriteoffProfit(rs.getBigDecimal("WriteoffProfit"));
				fwo.setAdjAmount(rs.getBigDecimal("AdjAmount"));
				fwo.setRemarks(rs.getString("Remarks"));
				fwo.setWrittenoffSchFee(rs.getBigDecimal("WrittenoffSchFee"));
				fwo.setUnpaidSchFee(rs.getBigDecimal("UnpaidSchFee"));
				fwo.setWriteoffSchFee(rs.getBigDecimal("WriteoffSchFee"));

				return fwo;
			}, finID);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public int getMaxFinanceWriteoffSeq(long finID, Date writeoffDate, String type) {
		StringBuilder sql = new StringBuilder("Select Coalesce(max(SeqNo), 0)");
		sql.append(" From FinWriteoffDetail");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinID = ? and WriteoffDate = ?");

		logger.debug(Literal.SQL + sql.toString());
		return this.jdbcOperations.queryForObject(sql.toString(), Integer.class, finID, writeoffDate);
	}

	@Override
	public void delete(long finID, String type) {
		StringBuilder sql = new StringBuilder("Delete From FinWriteoffDetail");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			int recordCount = this.jdbcOperations.update(sql.toString(), ps -> {
				int index = 1;

				ps.setLong(index, finID);
			});

			if (recordCount <= 0) {
				throw new ConcurrencyException();
			}

		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
	}

	@Override
	public String save(FinanceWriteoff fwo, String type) {
		StringBuilder sql = new StringBuilder("Insert Into FinWriteOffDetail");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" (FinID, FinReference, WriteoffDate, SeqNo, WrittenoffPri, WrittenoffPft, CurODPri, CurODPft");
		sql.append(", UnPaidSchdPri, UnPaidSchdPft, PenaltyAmount, ProvisionedAmount, WriteoffPrincipal");
		sql.append(", WriteoffProfit, AdjAmount, Remarks, WrittenoffSchFee, UnpaidSchFee, WriteoffSchFee, ReceiptID)");
		sql.append(" Values( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 0;

			ps.setLong(++index, fwo.getFinID());
			ps.setString(++index, fwo.getFinReference());
			ps.setDate(++index, JdbcUtil.getDate(fwo.getWriteoffDate()));
			ps.setInt(++index, fwo.getSeqNo());
			ps.setBigDecimal(++index, fwo.getWrittenoffPri());
			ps.setBigDecimal(++index, fwo.getWrittenoffPft());
			ps.setBigDecimal(++index, fwo.getCurODPri());
			ps.setBigDecimal(++index, fwo.getCurODPft());
			ps.setBigDecimal(++index, fwo.getUnPaidSchdPri());
			ps.setBigDecimal(++index, fwo.getUnPaidSchdPft());
			ps.setBigDecimal(++index, fwo.getPenaltyAmount());
			ps.setBigDecimal(++index, fwo.getProvisionedAmount());
			ps.setBigDecimal(++index, fwo.getWriteoffPrincipal());
			ps.setBigDecimal(++index, fwo.getWriteoffProfit());
			ps.setBigDecimal(++index, fwo.getAdjAmount());
			ps.setString(++index, fwo.getRemarks());
			ps.setBigDecimal(++index, fwo.getWrittenoffSchFee());
			ps.setBigDecimal(++index, fwo.getUnpaidSchFee());
			ps.setBigDecimal(++index, fwo.getWriteoffSchFee());
			ps.setObject(++index, fwo.getReceiptID());
		});

		return fwo.getFinReference();
	}

	@Override
	public void update(FinanceWriteoff fwo, String type) {
		StringBuilder sql = new StringBuilder("Update FinWriteoffDetail");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Set WriteoffDate = ?, SeqNo = ?, WrittenoffPri = ?, WrittenoffPft = ?, CurODPri = ?");
		sql.append(", CurODPft = ?, UnPaidSchdPri = ?, UnPaidSchdPft = ?, PenaltyAmount = ?, ProvisionedAmount = ?");
		sql.append(", WriteoffPrincipal = ?, WriteoffProfit = ?, AdjAmount = ?, Remarks = ?, WrittenoffSchFee = ?");
		sql.append(", UnpaidSchFee = ?, WriteoffSchFee = ?");
		sql.append(" Where FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		int recordCount = this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setDate(index++, JdbcUtil.getDate(fwo.getWriteoffDate()));
			ps.setInt(index++, fwo.getSeqNo());
			ps.setBigDecimal(index++, fwo.getWrittenoffPri());
			ps.setBigDecimal(index++, fwo.getWrittenoffPft());
			ps.setBigDecimal(index++, fwo.getCurODPri());
			ps.setBigDecimal(index++, fwo.getCurODPft());
			ps.setBigDecimal(index++, fwo.getUnPaidSchdPri());
			ps.setBigDecimal(index++, fwo.getUnPaidSchdPft());
			ps.setBigDecimal(index++, fwo.getPenaltyAmount());
			ps.setBigDecimal(index++, fwo.getProvisionedAmount());
			ps.setBigDecimal(index++, fwo.getWriteoffPrincipal());
			ps.setBigDecimal(index++, fwo.getWriteoffProfit());
			ps.setBigDecimal(index++, fwo.getAdjAmount());
			ps.setString(index++, fwo.getRemarks());
			ps.setBigDecimal(index++, fwo.getWrittenoffSchFee());
			ps.setBigDecimal(index++, fwo.getUnpaidSchFee());
			ps.setBigDecimal(index++, fwo.getWriteoffSchFee());

			ps.setLong(index, fwo.getFinID());
		});

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
	}

	@Override
	public FinWriteoffPayment getFinWriteoffPaymentById(long finID, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FinID, FinReference, WriteoffPayAmount, WriteoffPayAccount, LinkedTranId, SeqNo");
		sql.append(", Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode");
		sql.append(", TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" From FinWriteoffPayment");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, num) -> {
				FinWriteoffPayment payment = new FinWriteoffPayment();

				payment.setFinID(rs.getLong("FinID"));
				payment.setFinReference(rs.getString("FinReference"));
				payment.setWriteoffPayAmount(rs.getBigDecimal("WriteoffPayAmount"));
				payment.setWriteoffPayAccount(rs.getString("WriteoffPayAccount"));
				payment.setLinkedTranId(rs.getLong("LinkedTranId"));
				payment.setSeqNo(rs.getInt("SeqNo"));
				payment.setVersion(rs.getInt("Version"));
				payment.setLastMntBy(rs.getLong("LastMntBy"));
				payment.setLastMntOn(rs.getTimestamp("LastMntOn"));
				payment.setRecordStatus(rs.getString("RecordStatus"));
				payment.setRoleCode(rs.getString("RoleCode"));
				payment.setNextRoleCode(rs.getString("NextRoleCode"));
				payment.setTaskId(rs.getString("TaskId"));
				payment.setNextTaskId(rs.getString("NextTaskId"));
				payment.setRecordType(rs.getString("RecordType"));
				payment.setWorkflowId(rs.getLong("WorkflowId"));

				return payment;
			});
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public void deletefinWriteoffPayment(long finID, long seqNo, String type) {
		StringBuilder sql = new StringBuilder("Delete From FinWriteoffPayment");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinID = ? and SeqNo = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			int recordCount = this.jdbcOperations.update(sql.toString(), ps -> {
				int index = 1;

				ps.setLong(index++, finID);
				ps.setLong(index, seqNo);
			});

			if (recordCount <= 0) {
				throw new ConcurrencyException();
			}

		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
	}

	@Override
	public String saveFinWriteoffPayment(FinWriteoffPayment payment, String type) {
		StringBuilder sql = new StringBuilder("Insert Into FinWriteoffPayment");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" (FinID, FinReference, WriteoffPayAmount, WriteoffPayAccount, LinkedTranId, SeqNo");
		sql.append(", Version, LastMntBy, LastMntOn, RecordStatus, RoleCode");
		sql.append(", NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" Values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, payment.getFinID());
			ps.setString(index++, payment.getFinReference());
			ps.setBigDecimal(index++, payment.getWriteoffPayAmount());
			ps.setString(index++, payment.getWriteoffPayAccount());
			ps.setLong(index++, payment.getLinkedTranId());
			ps.setLong(index++, payment.getSeqNo());
			ps.setInt(index++, payment.getVersion());
			ps.setLong(index++, payment.getLastMntBy());
			ps.setTimestamp(index++, payment.getLastMntOn());
			ps.setString(index++, payment.getRecordStatus());
			ps.setString(index++, payment.getRoleCode());
			ps.setString(index++, payment.getNextRoleCode());
			ps.setString(index++, payment.getTaskId());
			ps.setString(index++, payment.getNextTaskId());
			ps.setString(index++, payment.getRecordType());
			ps.setLong(index, payment.getWorkflowId());
		});

		return payment.getFinReference();
	}

	@Override
	public void updateFinWriteoffPayment(FinWriteoffPayment Payment, String type) {
		StringBuilder sql = new StringBuilder("Update FinWriteoffPayment");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Set WriteoffPayAmount = ?, WriteoffPayAccount = ?, LinkedTranId = ?, SeqNo = ?");
		sql.append(", Version = ?, LastMntBy = ?, LastMntOn = ?, RecordStatus = ?, RoleCode = ?");
		sql.append(", NextRoleCode = ?, TaskId = ?, NextTaskId = ?, RecordType = ?, WorkflowId = ?");
		sql.append(" Where FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		int recordCount = this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setBigDecimal(index++, Payment.getWriteoffPayAmount());
			ps.setString(index++, Payment.getWriteoffPayAccount());
			ps.setLong(index++, Payment.getLinkedTranId());
			ps.setLong(index++, Payment.getSeqNo());
			ps.setInt(index++, Payment.getVersion());
			ps.setLong(index++, Payment.getLastMntBy());
			ps.setTimestamp(index++, Payment.getLastMntOn());
			ps.setString(index++, Payment.getRecordStatus());
			ps.setString(index++, Payment.getRoleCode());
			ps.setString(index++, Payment.getNextRoleCode());
			ps.setString(index++, Payment.getTaskId());
			ps.setString(index++, Payment.getNextTaskId());
			ps.setString(index++, Payment.getRecordType());
			ps.setLong(index++, Payment.getWorkflowId());
			ps.setLong(index, Payment.getFinID());
		});

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
	}

	public BigDecimal getTotalFinWriteoffDetailAmt(long finID) {
		String sql = "Select sum(WriteoffPrincipal) + sum(WriteoffProfit) + sum(WriteoffSchFee) From FinWriteoffDetail Where FinID = ?";

		logger.debug(Literal.SQL + sql);
		return this.jdbcOperations.queryForObject(sql, BigDecimal.class, finID);
	}

	public BigDecimal getTotalWriteoffPaymentAmount(long finID) {
		String sql = "Select sum(WriteoffPayAmount) From FinWriteoffPayment Where FinID = ?";

		logger.debug(Literal.SQL + sql);
		return this.jdbcOperations.queryForObject(sql, BigDecimal.class, finID);
	}

	@Override
	public Date getFinWriteoffDate(long finID) {
		String sql = "Select WriteoffDate From FinWriteoffDetail Where FinID = ?";

		logger.debug(Literal.SQL + sql);

		try {
			return this.jdbcOperations.queryForObject(sql, Date.class, finID);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return SysParamUtil.getAppDate();
		}
	}

	public long getfinWriteoffPaySeqNo(long finID, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" Coalesce(max(SeqNo), 0) From FinWriteoffPayment");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinID = ?");

		logger.debug(Literal.SQL + sql.toString());
		return this.jdbcOperations.queryForObject(sql.toString(), Long.class, finID);
	}

	@Override
	public boolean isWriteoffLoan(long finID, String type) {
		StringBuilder sql = new StringBuilder("Select Count(FinID)");
		sql.append(" From FinWriteoffDetail");
		sql.append(type);
		sql.append(" Where FinID = ?");

		return this.jdbcOperations.queryForObject(sql.toString(), Integer.class, finID) > 0;
	}
}
