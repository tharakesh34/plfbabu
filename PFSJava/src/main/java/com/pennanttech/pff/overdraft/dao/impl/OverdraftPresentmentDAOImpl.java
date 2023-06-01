package com.pennanttech.pff.overdraft.dao.impl;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import com.pennant.backend.model.finance.FinODDetails;
import com.pennant.backend.model.finance.ManualAdvise;
import com.pennant.backend.util.RepayConstants;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.overdraft.dao.OverdraftPresentmentDAO;
import com.pennanttech.pff.presentment.model.PresentmentCharge;
import com.pennanttech.pff.presentment.model.PresentmentDetail;

public class OverdraftPresentmentDAOImpl extends SequenceDao<AbstractWorkflowEntity>
		implements OverdraftPresentmentDAO {

	@Override
	public void updateCharges(long presentmentId, BigDecimal charges) {
		String sql = "Update PresentmentDetails Set PresentmentAmt = PresentmentAmt + ?, Charges = ? Where Id = ?";

		logger.debug(Literal.SQL + sql);

		int recordCount = this.jdbcOperations.update(sql, ps -> {
			int index = 1;

			ps.setBigDecimal(index++, charges);
			ps.setBigDecimal(index++, charges);

			ps.setLong(index, presentmentId);
		});

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
	}

	@Override
	public void update(List<FinODDetails> odDetails, String type) {
		String sql = "";
		if (RepayConstants.FEE_TYPE_LPP.equals(type)) {
			sql = "Update FinODDetails Set presentmentID = ? Where FinReference = ? And presentmentID = ? And TotPenaltyBal > ?";
		} else if (RepayConstants.FEE_TYPE_LPI.equals(type)) {
			sql = "Update FinODDetails Set presentmentID = ? Where FinReference = ? And presentmentID = ? And LPIBal > ?";
		}

		logger.debug(Literal.SQL + sql);

		this.jdbcOperations.batchUpdate(sql, new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				FinODDetails od = odDetails.get(i);
				int index = 1;

				ps.setLong(index++, od.getPresentmentID());
				ps.setString(index++, od.getFinReference());
				ps.setInt(index++, 0);
				ps.setInt(index, 0);

			}

			@Override
			public int getBatchSize() {
				return odDetails.size();
			}
		});
	}

	@Override
	public void update(List<ManualAdvise> maList) {
		String sql = "Update ManualAdvise Set PresentmentID = ? Where AdviseID = ?";

		logger.debug(Literal.SQL + sql);

		this.jdbcOperations.batchUpdate(sql, new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				ManualAdvise ma = maList.get(i);
				int index = 1;
				ps.setObject(index++, ma.getPresentmentID());
				ps.setLong(index, ma.getAdviseID());

			}

			@Override
			public int getBatchSize() {
				return maList.size();
			}
		});

	}

	@Override
	public void cancelManualAdvise(long presentmentID) {
		String sql = "Update ManualAdvise Set PresentmentID = ? Where PresentmentID = ?";

		logger.debug(Literal.SQL + sql);

		this.jdbcOperations.update(sql, ps -> {
			int index = 1;

			ps.setLong(index++, 0);
			ps.setLong(index, presentmentID);
		});

	}

	@Override
	public void cancelODDetails(long presentmentID) {
		String sql = "Update FinODDetails Set PresentmentID = ? Where PresentmentID = ?";

		logger.debug(Literal.SQL + sql);

		this.jdbcOperations.update(sql, ps -> {
			int index = 1;
			ps.setLong(index++, 0);
			ps.setLong(index, presentmentID);
		});
	}

	@Override
	public void cancelPresentmentCharges(long presentmentID) {
		String sql = "Delete From Presentment_Charges Where PresentmentID = ?";

		logger.debug(Literal.SQL + sql);

		try {
			jdbcOperations.update(sql, ps -> ps.setLong(1, presentmentID));
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
	}

	@Override
	public void savePresentmentCharge(List<PresentmentCharge> pcList, TableType tableType) {
		StringBuilder sql = new StringBuilder("Insert into Presentment_Charges");
		sql.append(tableType.getSuffix());
		sql.append(" (Id, PresentmentId, SeqNo, FeeType, ActualFeeAmount, CgstAmount, SgstAmount");
		sql.append(", IgstAmount, UgstAmount, CessAmount, FeeAmount, AdviseId)");
		sql.append(" Values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL + sql.toString());

		try {
			this.jdbcOperations.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {

				@Override
				public void setValues(PreparedStatement ps, int i) throws SQLException {
					PresentmentCharge pc = pcList.get(i);

					int index = 1;

					if (pc.getId() == 0 || pc.getId() <= Long.MIN_VALUE) {
						pc.setId(getNextValue("SEQ_PRESENTMENT_CHARGES"));
					}

					ps.setLong(index++, pc.getId());
					ps.setLong(index++, pc.getPresenmentID());
					ps.setInt(index++, pc.getSeqNo());
					ps.setString(index++, pc.getFeeType());
					ps.setBigDecimal(index++, pc.getActualFeeAmount());
					ps.setBigDecimal(index++, pc.getCgstAmount());
					ps.setBigDecimal(index++, pc.getSgstAmount());
					ps.setBigDecimal(index++, pc.getIgstAmount());
					ps.setBigDecimal(index++, pc.getUgstAmount());
					ps.setBigDecimal(index++, pc.getCessAmount());
					ps.setBigDecimal(index++, pc.getFeeAmount());
					ps.setLong(index, pc.getAdviseId());
				}

				@Override
				public int getBatchSize() {
					return pcList.size();
				}
			});
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}
	}

	@Override
	public List<PresentmentCharge> getPresentmentCharges(long presenmentID, String type) {
		StringBuilder sql = new StringBuilder("Select Id, PresenmentID, SeqNo, FeeType, ActualFeeAmount");
		sql.append(", CgstAmount, SgstAmount, IgstAmount, UgstAmount, CessAmount, FeeAmount, AdviseId");
		sql.append(" From PRESENTMENT_CHARGES");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where PresenmentID = ?");

		logger.debug(Literal.SQL + sql.toString());

		List<PresentmentCharge> pcList = this.jdbcOperations.query(sql.toString(), (rs, rowNum) -> {
			PresentmentCharge pc = new PresentmentCharge();

			pc.setId(rs.getLong("Id"));
			pc.setPresenmentID(rs.getLong("PresenmentID"));
			pc.setSeqNo(rs.getInt("SeqNo"));
			pc.setFeeType(rs.getString("FeeType"));
			pc.setActualFeeAmount(rs.getBigDecimal("ActualFeeAmount"));
			pc.setCgstAmount(rs.getBigDecimal("CgstAmount"));
			pc.setSgstAmount(rs.getBigDecimal("SgstAmount"));
			pc.setIgstAmount(rs.getBigDecimal("IgstAmount"));
			pc.setUgstAmount(rs.getBigDecimal("UgstAmount"));
			pc.setCessAmount(rs.getBigDecimal("CessAmount"));
			pc.setFeeAmount(rs.getBigDecimal("FeeAmount"));
			pc.setAdviseId(rs.getLong("AdviseId"));

			return pc;
		}, presenmentID);

		return pcList.stream().sorted((l1, l2) -> Integer.compare(l1.getSeqNo(), l2.getSeqNo()))
				.collect(Collectors.toList());
	}

	@Override
	public List<PresentmentDetail> getPreviousPresentmentBatches(PresentmentDetail dtls) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" Id, PresentmentId, PresentmentRef, FinReference, MandateId, PresentmentAmt");
		sql.append(" From PresentmentDetails");
		sql.append(" Where FinReference = ? and SchDate= ? and PresentmentId < ?");

		if (StringUtils.isNotBlank(dtls.getStatus())) {
			sql.append(" and Status = ?");
		}

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;
			ps.setString(index++, dtls.getFinReference());
			ps.setDate(index++, JdbcUtil.getDate(dtls.getSchDate()));
			ps.setLong(index++, dtls.getHeaderId());

			if (StringUtils.isNotBlank(dtls.getStatus())) {
				ps.setString(index, dtls.getStatus());
			}
		}, (rs, rowNum) -> {
			PresentmentDetail pd = new PresentmentDetail();

			pd.setId(rs.getLong("Id"));
			pd.setHeaderId(rs.getLong("PresentmentId"));
			pd.setPresentmentRef(rs.getString("PresentmentRef"));
			pd.setFinReference(rs.getString("FinReference"));
			pd.setMandateId(rs.getLong("MandateId"));
			pd.setPresentmentAmt(rs.getBigDecimal("PresentmentAmt"));

			return pd;
		});

	}

	@Override
	public void updateBatchApprovedDate(long presentmentId, Date approvedDate) {
		String sql = "Update PresentmentHeader Set ApprovedDate = ? Where Id = ?";

		logger.debug(Literal.SQL + sql);

		int recordCount = jdbcOperations.update(sql, ps -> {
			ps.setDate(1, JdbcUtil.getDate(approvedDate));
			ps.setLong(2, presentmentId);
		});

		if (recordCount == 0) {
			throw new ConcurrencyException();
		}
	}

}
