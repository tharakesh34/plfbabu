package com.pennant.backend.dao.finance.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.pennant.backend.dao.finance.PaymentMethodUploadDAO;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.pff.model.paymentmethodupload.PaymentMethodUpload;
import com.pennant.pff.model.paymentmethodupload.PaymentMethodUploadHeader;
import com.pennanttech.dataengine.model.DataEngineStatus;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;

public class PaymentMethodUploadDAOImpl extends SequenceDao<PaymentMethodUpload> implements PaymentMethodUploadDAO {

	@Override
	public boolean isFileExists(String name) {
		String sql = "Select count(ID) From PaymentMethod_Upload_Header Where FileName = ?";

		logger.debug(Literal.SQL + sql);

		try {
			return this.jdbcOperations.queryForObject(sql, Integer.class, name) > 0;
		} catch (EmptyResultDataAccessException e) {
			//
		}

		return false;
	}

	@Override
	public long saveHeader(String fileName) {
		String sql = "Insert Into PaymentMethod_Upload_Header (FileName, TotalRecords, SucessRecords, FailureRecords) Values(?, ?, ?, ?)";

		logger.debug(Literal.SQL + sql);

		KeyHolder keyHolder = new GeneratedKeyHolder();

		try {
			jdbcOperations.update(new PreparedStatementCreator() {

				@Override
				public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
					PreparedStatement ps = con.prepareStatement(sql, new String[] { "id" });
					int index = 1;

					ps.setString(index++, fileName);
					ps.setInt(index++, 0);
					ps.setInt(index++, 0);
					ps.setInt(index++, 0);

					return ps;
				}
			}, keyHolder);

			return keyHolder.getKey().longValue();
		} catch (Exception e) {
			//
		}

		return 0;
	}

	@Override
	public void updateRemarks(PaymentMethodUploadHeader header) {
		String sql = "Update PaymentMethod_Upload_Header Set TotalRecords = ?, SucessRecords = ?, FailureRecords = ?, Status = ? Where Id = ?";

		logger.debug(Literal.SQL + sql);

		try {
			this.jdbcOperations.update(sql, ps -> {
				int index = 1;

				ps.setInt(index++, header.getTotalRecords());
				ps.setInt(index++, header.getSucessRecords());
				ps.setInt(index++, header.getFailureRecords());
				ps.setString(index++, header.getStatus());

				ps.setObject(index++, header.getId());

			});
		} catch (Exception e) {
			//
		}
	}

	@Override
	public List<PaymentMethodUpload> getChangePaymentUploadDetails(long batchId) {
		String sql = "Select Id, BatchId, FinID, FinReference, MandateId, FinRepayMethod, UploadStatusRemarks From PaymentMethod_Upload_Detail Where BatchId = ?";

		logger.debug(Literal.SQL + sql);

		List<PaymentMethodUpload> list = this.jdbcOperations.query(sql, ps -> {
			int index = 1;

			ps.setLong(index++, batchId);
		}, (rs, rowNum) -> {
			PaymentMethodUpload pmu = new PaymentMethodUpload();

			pmu.setId(JdbcUtil.getLong(rs.getObject("Id")));
			pmu.setBatchId(JdbcUtil.getLong(rs.getObject("BatchId")));
			pmu.setFinID(JdbcUtil.getLong(rs.getObject("FinID")));
			pmu.setFinReference(rs.getString("FinReference"));
			pmu.setMandateId(JdbcUtil.getLong(rs.getObject("MandateId")));
			pmu.setFinRepayMethod(rs.getString("FinRepayMethod"));
			pmu.setUploadStatusRemarks(rs.getString("UploadStatusRemarks"));

			return pmu;
		});

		return list.stream().sorted((l1, l2) -> Long.compare(l1.getId(), l2.getId())).collect(Collectors.toList());
	}

	@Override
	public int logRcUpload(List<ErrorDetail> errDetail, Long id) {
		String sql = "Insert Into PaymentMethod_Upload_Log (DetailId, ErrorCode, ErrorDescription) Values( ?, ?, ?)";

		logger.debug(Literal.SQL + sql);

		return jdbcOperations.batchUpdate(sql, new BatchPreparedStatementSetter() {

			public void setValues(PreparedStatement ps, int i) throws SQLException {
				ErrorDetail err = errDetail.get(i);
				int index = 1;

				ps.setObject(index++, id);
				ps.setString(index++, err.getCode());
				ps.setString(index++, err.getError());
			}

			public int getBatchSize() {
				return errDetail.size();
			}
		}).length;
	}

	@Override
	public void updateDeRemarks(DataEngineStatus deStatus) {
		String sql = "Update Data_Engine_Status Set EndTime = ?, Remarks = ?, Status = ?, SuccessRecords = ?, FailedRecords = ? Where Id = ?";

		logger.debug(Literal.SQL + sql);

		this.jdbcOperations.update(sql, ps -> {
			int index = 1;

			ps.setDate(index++, JdbcUtil.getDate(DateUtil.getSysDate()));
			ps.setString(index++, deStatus.getRemarks());
			ps.setString(index++, deStatus.getStatus());
			ps.setLong(index++, deStatus.getSuccessRecords());
			ps.setLong(index++, deStatus.getFailedRecords());
			ps.setLong(index++, deStatus.getId());
		});

	}

	@Override
	public List<FinanceMain> getFinanceMain(long batchId) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" fm.FinID, fm.FinReference, FT.FinType, SD.EntityCode, fm.FinBranch, fm.CustID");
		sql.append(", Cust.CustCIF LovDescCustCIF, fm.FinCcy, fm.FinIsActive");
		sql.append(" From Financemain fm");
		sql.append(" Inner Join Customers Cust on fm.CustId = Cust.CustId");
		sql.append(" Inner Join RMTFinanceTypes FT on FT.FinType = fm.FinType");
		sql.append(" Inner Join SMTDivisiondetail SD On FT.FinDivision = SD.DivisionCode");
		sql.append(" Where fm.FinID in ");
		sql.append("(Select FinID From PaymentMethod_Upload_Detail Where BatchId = ?)");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, batchId);
		}, (rs, rowNum) -> {
			FinanceMain fm = new FinanceMain();

			fm.setFinID(rs.getLong("FinID"));
			fm.setFinReference(rs.getString("FinReference"));
			fm.setFinType(rs.getString("FinType"));
			fm.setEntityCode(rs.getString("EntityCode"));
			fm.setFinBranch(rs.getString("FinBranch"));
			fm.setCustID(rs.getLong("CustID"));
			fm.setLovDescCustCIF(rs.getString("LovDescCustCIF"));
			fm.setFinCcy(rs.getString("FinCcy"));
			fm.setFinIsActive(rs.getBoolean("FinIsActive"));

			return fm;
		});

	}

	@Override
	public void updateChangePaymentDetails(PaymentMethodUpload paymentUpload) {
		String sql = "Update PaymentMethod_Upload_Detail Set UploadStatusRemarks = ?, Status = ? Where Id = ?";

		logger.debug(Literal.SQL + sql);

		try {
			this.jdbcOperations.update(sql, ps -> {
				int index = 1;

				ps.setString(index++, StringUtils.trimToEmpty(paymentUpload.getUploadStatusRemarks()));
				ps.setString(index++, paymentUpload.getStatus());
				ps.setObject(index++, paymentUpload.getId());

			});
		} catch (Exception e) {
			//
		}
	}

	@Override
	public void updateFinRepaymethod(PaymentMethodUpload changePayment) {
		String sql = "Update FinanceMain Set FinRepayMethod = ?, MandateID = ? Where FinID = ?";

		logger.debug(Literal.SQL + sql);

		try {
			this.jdbcOperations.update(sql, ps -> {
				int index = 1;

				ps.setString(index++, changePayment.getFinRepayMethod());
				ps.setObject(index++, changePayment.getMandateId());
				ps.setObject(index++, changePayment.getFinID());

			});
		} catch (Exception e) {
			//
		}
	}

	@Override
	public boolean isMandateIdExists(long mandateId) {
		String sql = "Select count(FinID) From FinanceMain Where MandateId = ?";

		logger.debug(Literal.SQL + sql);

		try {
			return this.jdbcOperations.queryForObject(sql, Integer.class, mandateId) > 0;
		} catch (EmptyResultDataAccessException dae) {
			//
		}

		return false;
	}

}
