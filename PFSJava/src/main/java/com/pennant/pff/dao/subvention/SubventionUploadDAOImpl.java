package com.pennant.pff.dao.subvention;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.pff.model.subvention.Subvention;
import com.pennant.pff.model.subvention.SubventionHeader;
import com.pennanttech.dataengine.model.DataEngineStatus;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;

public class SubventionUploadDAOImpl extends SequenceDao<Subvention> implements SubventionUploadDAO {
	private static Logger logger = LogManager.getLogger(SubventionUploadDAOImpl.class);

	public SubventionUploadDAOImpl() {
		super();
	}

	@Override
	public long saveSubventionHeader(String batchRef, String entityCode) {
		StringBuilder sql = new StringBuilder("Insert into");
		sql.append(" Subvention_KnockOff_Header");
		sql.append(" (BatchRef, EntityCode, TotalRecords, SucessRecords, FailureRecords)");
		sql.append(" values(?, ?, ?, ?, ?) ");

		logger.trace(Literal.SQL + sql.toString());

		try {
			KeyHolder keyHolder = new GeneratedKeyHolder();

			jdbcOperations.update(new PreparedStatementCreator() {

				@Override
				public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
					PreparedStatement ps = con.prepareStatement(sql.toString(), new String[] { "id" });
					int index = 1;

					ps.setString(index++, batchRef);
					ps.setString(index++, entityCode);
					ps.setInt(index++, 0);
					ps.setInt(index++, 0);
					ps.setInt(index++, 0);

					return ps;
				}
			}, keyHolder);

			return keyHolder.getKey().longValue();
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Literal.EXCEPTION, e);
		}

		return 0;
	}

	public List<Subvention> getSubventionDetails(long batchId) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" Id, BatchId, FINREFERENCE, REFERENCECODE, AMOUNT, FinType, PostDate, ValueDate");
		sql.append(", PartnerBankId, PartnerAccNo");
		sql.append(" FROM SUBVENTION_KNOCKOFF_DETAILS");
		sql.append(" WHERE BATCHID = ?");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), new Object[] { batchId }, (rs, rowNum) -> {
			Subvention sv = new Subvention();

			sv.setId(rs.getLong("Id"));
			sv.setBatchId(rs.getLong("BatchId"));
			sv.setFinReference(rs.getString("FINREFERENCE"));
			sv.setReferenceCode(rs.getString("REFERENCECODE"));
			sv.setAmount(rs.getBigDecimal("AMOUNT"));
			sv.setFinType(rs.getString("FinType"));
			sv.setPostDate(rs.getDate("PostDate"));
			sv.setValueDate(rs.getDate("ValueDate"));
			sv.setPartnerBankId(rs.getLong("PartnerBankId"));
			sv.setPartnerAccNo(rs.getString("PartnerAccNo"));

			return sv;
		});
	}

	@Override
	public List<FinanceMain> getFinanceMain(long batchId) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FM.FinReference, FT.FinType, SD.EntityCode, FM.FinBranch, FM.CustID ");
		sql.append(", FM.FinCcy, SubventionFrom, ManufacturerDealerId, AVD.Code MANUFACTURERDEALERCODE");
		sql.append(", FM.TdsApplicable, FM.FinIsActive");
		sql.append(" From Financemain FM");
		sql.append(" INNER JOIN Customers Cust on FM.CUSTID = Cust.CUSTID");
		sql.append(" INNER JOIN RMTFINANCETYPES FT ON FT.FINTYPE = FM.FINTYPE");
		sql.append(" INNER JOIN SMTDivisiondetail SD On FT.FINDIVISION = SD.DivisionCode");
		sql.append(" LEFT JOIN AMTVEHICLEDEALER AVD ON FM.MANUFACTURERDEALERID = AVD.DEALERID");
		sql.append(" Where FM.FinReference in");
		sql.append(" (select FinReference from SUBVENTION_KNOCKOFF_DETAILS where BatchId = ?)");

		logger.debug(Literal.SQL + sql.toString());

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
			fm.setSubVentionFrom(rs.getString("SubVentionFrom"));
			fm.setManufacturerDealerId(rs.getLong("ManufacturerDealerId"));
			fm.setManufacturerDealerCode(rs.getString("ManufacturerDealerCode"));
			fm.settDSApplicable(rs.getBoolean("TdsApplicable"));
			fm.setFinIsActive(rs.getBoolean("FinIsActive"));
			return fm;
		};
	}

	@Override
	public List<FinFeeDetail> getFinFeeDetails(long batchId, String feeTypeCode) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FeeID, FinReference, FeeTypeID");
		sql.append(", CalculatedAmount, ActualAmount, WaivedAmount, PaidAmount");
		sql.append(", RemainingFee, FixedAmount, Percentage, CalculateOn");
		sql.append(", PaidAmountOriginal, PaidAmountGST, NetAmountOriginal, NetAmountGST");
		sql.append(", NetAmount, RemainingFeeOriginal, RemainingFeeGST");
		sql.append(", TaxApplicable, TaxComponent, ActualAmountOriginal, ActualAmountGST");
		sql.append(", WaivedGST, ReferenceId, TaxHeaderId");
		sql.append(", NetTDS, PaidTDS, RemTDS, ActPercentage");
		sql.append(", FeeTypeCode, TdsReq");
		sql.append(" From FinFeeDetail_AView");
		sql.append(" Where FeeTypeCode = ? and Finevent = 'ADDDBSP' ");
		sql.append(" and FinReference in (select FinReference from SUBVENTION_KNOCKOFF_DETAILS where BatchId = ?)");

		logger.debug(Literal.SQL + sql.toString());

		FinFeeDetailsRowMapper rowMapper = new FinFeeDetailsRowMapper();

		return this.jdbcOperations.query(sql.toString(), new Object[] { feeTypeCode, batchId }, rowMapper);

	}

	private class FinFeeDetailsRowMapper implements RowMapper<FinFeeDetail> {
		private FinFeeDetailsRowMapper() {
			super();
		}

		@Override
		public FinFeeDetail mapRow(ResultSet rs, int rowNum) throws SQLException {
			FinFeeDetail fd = new FinFeeDetail();

			fd.setFeeID(rs.getLong("FeeID"));
			fd.setFinReference(rs.getString("FinReference"));
			fd.setFeeTypeID(rs.getLong("FeeTypeID"));
			fd.setCalculatedAmount(rs.getBigDecimal("CalculatedAmount"));
			fd.setActualAmount(rs.getBigDecimal("ActualAmount"));
			fd.setWaivedAmount(rs.getBigDecimal("WaivedAmount"));
			fd.setPaidAmount(rs.getBigDecimal("PaidAmount"));
			fd.setRemainingFee(rs.getBigDecimal("RemainingFee"));
			fd.setFixedAmount(rs.getBigDecimal("FixedAmount"));
			fd.setPercentage(rs.getBigDecimal("Percentage"));
			fd.setCalculateOn(rs.getString("CalculateOn"));
			fd.setPaidAmountOriginal(rs.getBigDecimal("PaidAmountOriginal"));
			fd.setPaidAmountGST(rs.getBigDecimal("PaidAmountGST"));
			fd.setNetAmountOriginal(rs.getBigDecimal("NetAmountOriginal"));
			fd.setNetAmountGST(rs.getBigDecimal("NetAmountGST"));
			fd.setNetAmount(rs.getBigDecimal("NetAmount"));
			fd.setRemainingFeeOriginal(rs.getBigDecimal("RemainingFeeOriginal"));
			fd.setRemainingFeeGST(rs.getBigDecimal("RemainingFeeGST"));
			fd.setTaxApplicable(rs.getBoolean("TaxApplicable"));
			fd.setTaxComponent(rs.getString("TaxComponent"));
			fd.setActualAmountOriginal(rs.getBigDecimal("ActualAmountOriginal"));
			fd.setActualAmountGST(rs.getBigDecimal("ActualAmountGST"));
			fd.setWaivedGST(rs.getBigDecimal("WaivedGST"));
			fd.setReferenceId(rs.getLong("ReferenceId"));
			fd.setTaxHeaderId(JdbcUtil.getLong(rs.getObject("TaxHeaderId")));
			fd.setNetTDS(rs.getBigDecimal("NetTDS"));
			fd.setPaidTDS(rs.getBigDecimal("PaidTDS"));
			fd.setRemTDS(rs.getBigDecimal("RemTDS"));
			fd.setActPercentage(rs.getBigDecimal("ActPercentage"));
			fd.setFeeTypeCode(rs.getString("FeeTypeCode"));
			fd.setTdsReq(rs.getBoolean("TdsReq"));

			return fd;
		}

	}

	@Override
	public void updateFinFeeDetails(String finReference, FinFeeDetail fee) {
		StringBuilder sql = new StringBuilder("Update FinFeeDetail");
		sql.append(" Set ");
		sql.append("PaidAmount = ?, PaidAmountOriginal = ?, PaidAmountGST = ?, RemainingFee = ?");
		sql.append(", RemainingFeeOriginal = ?, RemainingFeeGST = ?, PaidTDS = ?, RemTDS = ?");
		sql.append(" Where FeeID = ? and Finreference = ? ");

		logger.debug(Literal.SQL + sql.toString());

		try {
			int recordCount = this.jdbcOperations.update(sql.toString(), ps -> {
				int index = 1;
				ps.setBigDecimal(index++, fee.getPaidAmount());
				ps.setBigDecimal(index++, fee.getPaidAmountOriginal());
				ps.setBigDecimal(index++, fee.getPaidAmountGST());
				ps.setBigDecimal(index++, fee.getRemainingFee());
				ps.setBigDecimal(index++, fee.getRemainingFeeOriginal());
				ps.setBigDecimal(index++, fee.getRemainingFeeGST());
				ps.setBigDecimal(index++, fee.getPaidTDS());
				ps.setBigDecimal(index++, fee.getRemTDS());
				ps.setLong(index++, fee.getFeeID());
				ps.setString(index++, finReference);

			});
			if (recordCount <= 0) {
				throw new ConcurrencyException();
			}
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
	}

	@Override
	public void updateSubventionDetails(Subvention subVention) {
		StringBuilder sql = new StringBuilder("Update SUBVENTION_KNOCKOFF_DETAILS");
		sql.append(" Set ");
		sql.append("LINKEDTRANID = ?, REMARKS = ? , STATUS = ?, CGSTAMT = ?, SGSTAMT = ? ");
		sql.append(", UGSTAMT = ?, IGSTAMT = ?, CESSAMT = ?, ProcFeeAmt = ?");
		sql.append(" Where Id = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			this.jdbcOperations.update(sql.toString(), ps -> {
				int index = 1;
				ps.setObject(index++, subVention.getLinkedTranId());
				ps.setString(index++, StringUtils.trimToEmpty(subVention.getRemarks()));
				ps.setString(index++, subVention.getStatus());
				ps.setBigDecimal(index++, subVention.getCgstAmt());
				ps.setBigDecimal(index++, subVention.getSgstAmt());
				ps.setBigDecimal(index++, subVention.getUgstAmt());
				ps.setBigDecimal(index++, subVention.getIgstAmt());
				ps.setBigDecimal(index++, subVention.getCessAmt());
				ps.setObject(index++, subVention.getProcFeeAmt());
				ps.setLong(index++, subVention.getId());

			});
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
	}

	public int getSucessCount(String finRef, String status) {
		String sql = "Select Coalesce(count(FinReference), 0) From SUBVENTION_KNOCKOFF_DETAILS Where FinReference = ? and status = ?";

		logger.debug(Literal.SQL + sql);

		return this.jdbcOperations.queryForObject(sql, new Object[] { finRef, status }, Integer.class);
	}

	@Override
	public boolean isFileExists(String name) {
		String sql = "Select Coalesce(count(ID), 0) From SUBVENTION_KNOCKOFF_HEADER Where BatchRef = ?";

		logger.debug(Literal.SQL + sql);

		return this.jdbcOperations.queryForObject(sql, new Object[] { name }, Integer.class) > 0;
	}

	public int logSubvention(List<ErrorDetail> errDetails, Long id) {
		String sql = "Insert Into Subvention_KnockOff_Log (DetailId, ErrorCode, ErrorDescription) Values(?, ?, ?)";

		logger.debug(Literal.SQL + sql);

		return jdbcOperations.batchUpdate(sql, new BatchPreparedStatementSetter() {

			public void setValues(PreparedStatement ps, int index) throws SQLException {
				int i = 1;
				ErrorDetail err = errDetails.get(index);

				ps.setLong(i++, id);
				ps.setString(i++, err.getCode());
				ps.setString(i++, err.getError());
			}

			public int getBatchSize() {
				return errDetails.size();
			}
		}).length;
	}

	public void updateRemarks(SubventionHeader subventionHeader) {
		String sql = "UPDATE SUBVENTION_KNOCKOFF_HEADER Set TotalRecords = ?, SucessRecords = ?, FailureRecords = ?, Status = ? WHERE Id = ?";

		logger.debug(Literal.SQL + sql);

		try {
			this.jdbcOperations.update(sql, ps -> {
				int index = 1;

				ps.setInt(index++, subventionHeader.getTotalRecords());
				ps.setInt(index++, subventionHeader.getSucessRecords());
				ps.setInt(index++, subventionHeader.getFailureRecords());
				ps.setString(index++, subventionHeader.getStatus());
				ps.setLong(index++, subventionHeader.getId());

			});
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
	}

	@Override
	public int saveSubvention(List<Subvention> subventions, long id) {
		StringBuilder sql = new StringBuilder("Insert into SUBVENTION_KNOCKOFF_DETAILS");
		sql.append(" (BatchId, Finreference, Fintype, Amount, CustomerName, ReferenceCode");
		sql.append(" , PostDate, ValueDate, Transref, PartnerBankId, PartnerAccNo)");
		sql.append(" Values");
		sql.append(" (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL + sql.toString());

		return jdbcOperations.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {

			public void setValues(PreparedStatement ps, int index) throws SQLException {
				int i = 1;
				Subvention subv = subventions.get(index);

				ps.setLong(i++, id);
				ps.setString(i++, subv.getFinReference());
				ps.setString(i++, subv.getFinType());
				ps.setBigDecimal(i++, subv.getAmount());
				ps.setString(i++, subv.getCustomerName());
				ps.setString(i++, subv.getReferenceCode());
				ps.setDate(i++, JdbcUtil.getDate(subv.getPostDate()));
				ps.setDate(i++, JdbcUtil.getDate(subv.getValueDate()));
				ps.setString(i++, subv.getTransref());
				ps.setObject(i++, subv.getPartnerBankId());
				ps.setString(i++, subv.getPartnerAccNo());
			}

			public int getBatchSize() {
				return subventions.size();
			}
		}).length;

	}

	public void updateDeRemarks(SubventionHeader header, DataEngineStatus deStatus) {
		StringBuilder remarks = new StringBuilder(deStatus.getRemarks());
		remarks.append(", Approved: ");
		remarks.append(header.getSucessRecords());
		remarks.append(", Rejected: ");
		remarks.append(header.getFailureRecords());

		deStatus.setRemarks(remarks.toString());

		StringBuffer sql = new StringBuffer();
		sql.append(" UPDATE DATA_ENGINE_STATUS set EndTime = ?, Remarks = ?, Status = ?");
		sql.append(" WHERE Name = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			this.jdbcOperations.update(sql.toString(), ps -> {
				int index = 1;

				ps.setDate(index++, JdbcUtil.getDate(DateUtil.getSysDate()));
				ps.setString(index++, remarks.toString());
				ps.setString(index++, deStatus.getStatus());
				ps.setString(index++, header.getBatchRef());

			});
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
	}

	@Override
	public Subvention getGstDetails(String finReference) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" Sum(CGSTAMT) CGSTAMT, Sum(SGSTAMT) SGSTAMT, Sum(UGSTAMT) UGSTAMT");
		sql.append(", Sum(IGSTAMT) IGSTAMT, SUM(CESSAMT) CESSAMT");
		sql.append(" from SUBVENTION_KNOCKOFF_DETAILS");
		sql.append(" Where finreference = ? and Status = ?");
		sql.append(" Group by FinReference");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), new Object[] { finReference, "S" },
					(rs, rowNum) -> {
						Subvention sv = new Subvention();
						sv.setCgstAmt(rs.getBigDecimal("CGSTAMT"));
						sv.setSgstAmt(rs.getBigDecimal("SGSTAMT"));
						sv.setIgstAmt(rs.getBigDecimal("IGSTAMT"));
						sv.setUgstAmt(rs.getBigDecimal("UGSTAMT"));
						sv.setCessAmt(rs.getBigDecimal("CESSAMT"));
						return sv;
					});
		} catch (EmptyResultDataAccessException e) {
			logger.warn(
					"Record is not found in SUBVENTION_KNOCKOFF_DETAILS table for the specified finreference >> {} and Status >> 'S'",
					finReference);
		}

		return null;
	}
}
