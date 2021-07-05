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
import org.springframework.jdbc.core.RowMapper;
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
		String sql = "Select count(ID) from PAYMENTMETHOD_UPLOAD_HEADER Where FileName = ?";

		logger.debug(Literal.SQL + sql);

		return this.jdbcOperations.queryForObject(sql, new Object[] { name }, Integer.class) > 0 ? true : false;
	}

	@Override
	public long saveHeader(String fileName) {
		String sql = "Insert into PAYMENTMETHOD_UPLOAD_HEADER (FileName, TotalRecords, SucessRecords, FailureRecords) Values(?, ?, ?, ?)";

		logger.debug(Literal.SQL + sql);

		try {
			KeyHolder keyHolder = new GeneratedKeyHolder();

			jdbcOperations.update(new PreparedStatementCreator() {

				@Override
				public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
					PreparedStatement ps = con.prepareStatement(sql, new String[] { "id" });

					ps.setString(1, fileName);
					ps.setInt(2, 0);
					ps.setInt(3, 0);
					ps.setInt(4, 0);

					return ps;
				}
			}, keyHolder);

			return keyHolder.getKey().longValue();
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		return 0;
	}

	@Override
	public void updateRemarks(PaymentMethodUploadHeader header) {
		String sql = "Update PAYMENTMETHOD_UPLOAD_HEADER Set TotalRecords = ?, SucessRecords = ?, FailureRecords = ?, Status = ? Where Id = ?";

		logger.debug(Literal.SQL + sql);

		try {
			this.jdbcOperations.update(sql, ps -> {
				ps.setInt(1, header.getTotalRecords());
				ps.setInt(2, header.getSucessRecords());
				ps.setInt(3, header.getFailureRecords());
				ps.setString(4, header.getStatus());
				ps.setLong(5, header.getId());

			});
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
	}

	@Override
	public List<PaymentMethodUpload> getChangePaymentUploadDetails(long batchId) {
		String sql = "SELECT Id, BatchId, FINREFERENCE, MandateId, FinRepayMethod , UploadStatusRemarks FROM PAYMENTMETHOD_UPLOAD_DETAIL WHERE BATCHID = ?";

		logger.debug(Literal.SQL + sql);

		List<PaymentMethodUpload> list = this.jdbcOperations.query(sql, new Object[] { batchId }, (rs, rowNum) -> {
			PaymentMethodUpload pmu = new PaymentMethodUpload();

			pmu.setId(rs.getLong("Id"));
			pmu.setBatchId(rs.getLong("BatchId"));
			pmu.setFinReference(rs.getString("FINREFERENCE"));
			pmu.setMandateId(rs.getLong("MandateId"));
			pmu.setFinRepayMethod(rs.getString("FinRepayMethod"));
			pmu.setUploadStatusRemarks(rs.getString("UploadStatusRemarks"));

			return pmu;
		});

		return list.stream().sorted((l1, l2) -> Long.compare(l1.getId(), l2.getId())).collect(Collectors.toList());
	}

	@Override
	public int logRcUpload(List<ErrorDetail> errDetail, Long id) {
		String sql = "Insert Into PAYMENTMETHOD_UPLOAD_LOG (DetailId, ErrorCode, ErrorDescription) Values( ? , ?, ?)";

		logger.debug(Literal.SQL + sql);

		return jdbcOperations.batchUpdate(sql, new BatchPreparedStatementSetter() {

			public void setValues(PreparedStatement ps, int index) throws SQLException {
				ErrorDetail err = errDetail.get(index);

				ps.setLong(1, id);
				ps.setString(2, err.getCode());
				ps.setString(3, err.getError());
			}

			public int getBatchSize() {
				return errDetail.size();
			}
		}).length;
	}

	@Override
	public void updateDeRemarks(DataEngineStatus deStatus) {
		String sql = "Update DATA_ENGINE_STATUS set EndTime = ?, Remarks = ?, Status = ?, SuccessRecords = ?, FailedRecords = ? WHERE Id = ?";

		logger.debug(Literal.SQL + sql);

		this.jdbcOperations.update(sql, ps -> {
			ps.setDate(1, JdbcUtil.getDate(DateUtil.getSysDate()));
			ps.setString(2, deStatus.getRemarks());
			ps.setString(3, deStatus.getStatus());
			ps.setLong(4, deStatus.getSuccessRecords());
			ps.setLong(5, deStatus.getFailedRecords());
			ps.setLong(6, deStatus.getId());
		});

	}

	@Override
	public List<FinanceMain> getFinanceMain(long batchId) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FM.FinReference, FT.FinType, SD.EntityCode, FM.FinBranch, FM.CustID, ");
		sql.append(" Cust.CustCIF LovDescCustCIF, FM.FinCcy, FM.FinIsActive");
		sql.append(" From Financemain FM");
		sql.append(" INNER JOIN Customers Cust on FM.CUSTID = Cust.CUSTID");
		sql.append(" INNER JOIN RMTFINANCETYPES FT ON FT.FINTYPE = FM.FINTYPE");
		sql.append(" INNER JOIN SMTDivisiondetail SD On FT.FINDIVISION = SD.DivisionCode");
		sql.append(" Where FM.FinReference in ");
		sql.append("(select FinReference from PAYMENTMETHOD_UPLOAD_DETAIL where BatchId = ?)");

		logger.trace(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), new Object[] { batchId }, FinanceMainRowMapper());

	}

	private RowMapper<FinanceMain> FinanceMainRowMapper() {
		return (rs, rowNum) -> {
			FinanceMain fm = new FinanceMain();

			fm.setFinReference(rs.getString("FinReference"));
			fm.setFinType(rs.getString("FinType"));
			fm.setEntityCode(rs.getString("EntityCode"));
			fm.setFinBranch(rs.getString("FinBranch"));
			fm.setCustID(rs.getLong("CustID"));
			fm.setFinCcy(rs.getString("FinCcy"));
			fm.setFinIsActive(rs.getBoolean("FinIsActive"));
			fm.setLovDescCustCIF(rs.getString("LovDescCustCIF"));

			return fm;
		};
	}

	@Override
	public void updateChangePaymentDetails(PaymentMethodUpload paymentUpload) {
		String sql = "Update PAYMENTMETHOD_UPLOAD_DETAIL Set UploadStatusRemarks = ?, STATUS = ? Where Id = ?";

		logger.debug(Literal.SQL + sql);

		try {
			this.jdbcOperations.update(sql, ps -> {
				ps.setString(1, StringUtils.trimToEmpty(paymentUpload.getUploadStatusRemarks()));
				ps.setString(2, paymentUpload.getStatus());
				ps.setLong(3, paymentUpload.getId());

			});
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
	}

	@Override
	public void updateFinRepaymethod(PaymentMethodUpload changePayment) {
		String sql = "Update FinanceMain Set FinRepayMethod = ?, MandateID = ? Where FinReference = ?";

		logger.debug(Literal.SQL + sql);

		try {
			this.jdbcOperations.update(sql, ps -> {
				ps.setString(1, changePayment.getFinRepayMethod());
				ps.setObject(2, changePayment.getMandateId());
				ps.setString(3, changePayment.getFinReference());

			});
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
	}

	/**
	 * Method for fetch existing mandate id by reference.
	 * 
	 * @param finReference
	 * @param type
	 * @return mandateId
	 */
	@Override
	public boolean isMandateIdExists(long mandateId) {
		String sql = "Select count(*) From FinanceMain Where MandateId = ?";

		logger.debug(Literal.SQL + sql);

		try {
			return this.jdbcOperations.queryForObject(sql, new Object[] { mandateId }, Integer.class) > 0;
		} catch (EmptyResultDataAccessException dae) {
			logger.warn("No records are available in FinanceMain for thi MandateId >> {}", mandateId);
		}

		return false;
	}

}
