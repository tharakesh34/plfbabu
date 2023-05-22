package com.pennant.pff.dao.subvention;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DuplicateKeyException;
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
import com.pennanttech.pennapps.core.resource.Message;
import com.pennanttech.pennapps.core.util.DateUtil;

public class SubventionUploadDAOImpl extends SequenceDao<Subvention> implements SubventionUploadDAO {
	private static Logger logger = LogManager.getLogger(SubventionUploadDAOImpl.class);

	public SubventionUploadDAOImpl() {
		super();
	}

	@Override
	public long saveSubventionHeader(String batchRef, String entityCode) {
		StringBuilder sql = new StringBuilder("Insert Into");
		sql.append(" Subvention_KnockOff_Header");
		sql.append(" (BatchRef, EntityCode, TotalRecords, SucessRecords, FailureRecords)");
		sql.append(" values(?, ?, ?, ?, ?) ");

		logger.debug(Literal.SQL + sql.toString());

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
					ps.setInt(index, 0);

					return ps;
				}
			}, keyHolder);

			return keyHolder.getKey().longValue();
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}
	}

	public List<Subvention> getSubventionDetails(long batchId) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" Id, BatchId, FinID, FinReference, ReferenceCode, Amount, FinType, PostDate, ValueDate");
		sql.append(", PartnerBankId, PartnerAccNo");
		sql.append(" From Subvention_KnockOff_Details");
		sql.append(" Where BatchId = ?");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index, batchId);
		}, (rs, rowNum) -> {
			Subvention sv = new Subvention();

			sv.setId(JdbcUtil.getLong(rs.getObject("Id")));
			sv.setBatchId(JdbcUtil.getLong(rs.getObject("BatchId")));
			sv.setFinID(rs.getLong("FinID"));
			sv.setFinReference(rs.getString("FinReference"));
			sv.setReferenceCode(rs.getString("ReferenceCode"));
			sv.setAmount(rs.getBigDecimal("Amount"));
			sv.setFinType(rs.getString("FinType"));
			sv.setPostDate(rs.getDate("PostDate"));
			sv.setValueDate(rs.getDate("ValueDate"));
			sv.setPartnerBankId(JdbcUtil.getLong(rs.getObject("PartnerBankId")));
			sv.setPartnerAccNo(rs.getString("PartnerAccNo"));

			return sv;
		});
	}

	@Override
	public List<FinanceMain> getFinanceMain(long batchId) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FM.FinID, FM.FinReference, FT.FinType, SD.EntityCode, FM.FinBranch, FM.CustID");
		sql.append(", FM.FinCcy, SubventionFrom, ManufacturerDealerId, AVD.Code ManufacturerDealerCode");
		sql.append(", FM.TdsApplicable, FM.FinIsActive");
		sql.append(" From Financemain FM");
		sql.append(" Inner Join Customers Cust on FM.CustID = Cust.CustId");
		sql.append(" Inner Join RMTFinanceTypes FT on FT.FinType = FM.FinType");
		sql.append(" Inner Join SMTDivisiondetail SD on FT.FinDivision = SD.DivisionCode");
		sql.append(" Left Join AMTVehicleDealer AVD on FM.ManufacturerDealerId = AVD.DealerId");
		sql.append(" Where FM.FinID in");
		sql.append(" (Select FinID From Subvention_Knockoff_Details Where BatchId = ?)");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index, batchId);
		}, (rs, rowNum) -> {
			FinanceMain fm = new FinanceMain();

			fm.setFinID(rs.getLong("FinID"));
			fm.setFinReference(rs.getString("FinReference"));
			fm.setFinType(rs.getString("FinType"));
			fm.setEntityCode(rs.getString("EntityCode"));
			fm.setFinBranch(rs.getString("FinBranch"));
			fm.setCustID(rs.getLong("CustID"));
			fm.setFinCcy(rs.getString("FinCcy"));
			fm.setSubVentionFrom(rs.getString("SubVentionFrom"));
			fm.setManufacturerDealerId(JdbcUtil.getLong(rs.getObject("ManufacturerDealerId")));
			fm.setManufacturerDealerCode(rs.getString("ManufacturerDealerCode"));
			fm.setTDSApplicable(rs.getBoolean("TdsApplicable"));
			fm.setFinIsActive(rs.getBoolean("FinIsActive"));
			return fm;
		});

	}

	@Override
	public List<FinFeeDetail> getFinFeeDetails(long batchId, String feeTypeCode) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FeeID, FinID, FinReference, FeeTypeID");
		sql.append(", CalculatedAmount, ActualAmount, WaivedAmount, PaidAmount");
		sql.append(", RemainingFee, FixedAmount, Percentage, CalculateOn");
		sql.append(", PaidAmountOriginal, PaidAmountGST, NetAmountOriginal, NetAmountGST");
		sql.append(", NetAmount, RemainingFeeOriginal, RemainingFeeGST");
		sql.append(", TaxApplicable, TaxComponent, ActualAmountOriginal, ActualAmountGST");
		sql.append(", WaivedGST, ReferenceId, TaxHeaderId");
		sql.append(", NetTDS, PaidTDS, RemTDS, ActPercentage");
		sql.append(", FeeTypeCode, TdsReq");
		sql.append(" From FinFeeDetail_AView");
		sql.append(" Where FeeTypeCode = ? and Finevent = ?");
		sql.append(" and FinID in (Select FinID From Subvention_Knockoff_Details Where BatchId = ?)");

		logger.debug(Literal.SQL + sql.toString());

		FinFeeDetailsRowMapper rowMapper = new FinFeeDetailsRowMapper();

		return this.jdbcOperations.query(sql.toString(), rowMapper, new Object[] { feeTypeCode, "ADDDBSP", batchId });

	}

	private class FinFeeDetailsRowMapper implements RowMapper<FinFeeDetail> {
		private FinFeeDetailsRowMapper() {
			super();
		}

		@Override
		public FinFeeDetail mapRow(ResultSet rs, int rowNum) throws SQLException {
			FinFeeDetail fd = new FinFeeDetail();

			fd.setFeeID(rs.getLong("FeeID"));
			fd.setFinID(rs.getLong("FinID"));
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
	public void updateFinFeeDetails(long finID, FinFeeDetail fee) {
		StringBuilder sql = new StringBuilder("Update FinFeeDetail");
		sql.append(" Set PaidAmount = ?, PaidAmountOriginal = ?, PaidAmountGST = ?, RemainingFee = ?");
		sql.append(", RemainingFeeOriginal = ?, RemainingFeeGST = ?, PaidTDS = ?, RemTDS = ?");
		sql.append(" Where FeeID = ? and FinID = ? ");

		logger.debug(Literal.SQL + sql.toString());

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
			ps.setLong(index, finID);

		});

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
	}

	@Override
	public void updateSubventionDetails(Subvention subVention) {
		StringBuilder sql = new StringBuilder("Update Subvention_KnockOff_Details");
		sql.append(" Set LinkedTranId = ?, Remarks = ? , Status = ?, CGSTAmt = ?, SGSTAmt = ? ");
		sql.append(", UGSTAmt = ?, IGSTAmt = ?, CESSAmt = ?, ProcFeeAmt = ?");
		sql.append(" Where Id = ?");

		logger.debug(Literal.SQL + sql.toString());

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

			ps.setObject(index, subVention.getId());

		});
	}

	public int getSucessCount(long finID, String status) {
		String sql = "Select Coalesce(count(FinID), 0) From Subvention_KnockOff_Details Where FinID = ? and Status = ?";

		logger.debug(Literal.SQL + sql);

		return this.jdbcOperations.queryForObject(sql, Integer.class, finID, status);
	}

	@Override
	public boolean isFileExists(String name) {
		String sql = "Select Coalesce(count(ID), 0) From Subvention_KnockOff_Header Where BatchRef = ?";

		logger.debug(Literal.SQL + sql);

		return this.jdbcOperations.queryForObject(sql, Integer.class, name) > 0;
	}

	public int logSubvention(List<ErrorDetail> errDetails, Long id) {
		String sql = "Insert Into Subvention_KnockOff_Log (DetailId, ErrorCode, ErrorDescription) Values(?, ?, ?)";

		logger.debug(Literal.SQL + sql);

		return jdbcOperations.batchUpdate(sql, new BatchPreparedStatementSetter() {

			public void setValues(PreparedStatement ps, int index) throws SQLException {
				int i = 1;
				ErrorDetail err = errDetails.get(index);

				ps.setObject(i++, id);
				ps.setString(i++, err.getCode());
				ps.setString(i, err.getError());
			}

			public int getBatchSize() {
				return errDetails.size();
			}
		}).length;
	}

	public void updateRemarks(SubventionHeader sh) {
		String sql = "Update Subvention_knockOff_Header Set TotalRecords = ?, SucessRecords = ?, FailureRecords = ?, Status = ? Where Id = ?";

		logger.debug(Literal.SQL + sql);

		this.jdbcOperations.update(sql, ps -> {
			int index = 1;

			ps.setInt(index++, sh.getTotalRecords());
			ps.setInt(index++, sh.getSucessRecords());
			ps.setInt(index++, sh.getFailureRecords());
			ps.setString(index++, sh.getStatus());
			ps.setObject(index, sh.getId());

		});
	}

	@Override
	public int saveSubvention(List<Subvention> subventions, long id) {
		StringBuilder sql = new StringBuilder("Insert Into Subvention_Knockoff_Details");
		sql.append(" (BatchId, FinID, Finreference, Fintype, Amount, CustomerName, ReferenceCode");
		sql.append(", PostDate, ValueDate, Transref, PartnerBankId, PartnerAccNo)");
		sql.append(" Values");
		sql.append(" (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL + sql.toString());

		return jdbcOperations.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {

			public void setValues(PreparedStatement ps, int index) throws SQLException {
				int i = 1;
				Subvention subv = subventions.get(index);

				ps.setLong(i++, id);
				ps.setLong(i++, subv.getFinID());
				ps.setString(i++, subv.getFinReference());
				ps.setString(i++, subv.getFinType());
				ps.setBigDecimal(i++, subv.getAmount());
				ps.setString(i++, subv.getCustomerName());
				ps.setString(i++, subv.getReferenceCode());
				ps.setDate(i++, JdbcUtil.getDate(subv.getPostDate()));
				ps.setDate(i++, JdbcUtil.getDate(subv.getValueDate()));
				ps.setString(i++, subv.getTransref());
				ps.setObject(i++, subv.getPartnerBankId());
				ps.setString(i, subv.getPartnerAccNo());
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

		String sql = "Update Data_Engine_Status Set EndTime = ?, Remarks = ?, Status = ? Where Name = ?";

		logger.debug(Literal.SQL + sql);

		this.jdbcOperations.update(sql, ps -> {
			int index = 1;

			ps.setDate(index++, JdbcUtil.getDate(DateUtil.getSysDate()));
			ps.setString(index++, remarks.toString());
			ps.setString(index++, deStatus.getStatus());
			ps.setString(index, header.getBatchRef());

		});
	}

	@Override
	public Subvention getGstDetails(long finID) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" sum(CGSTAmt) CGSTAmt, sum(SGSTAmt) SGSTAmt, sum(UGSTAmt) UGSTAmt");
		sql.append(", sum(IGSTAmt) IGSTAmt, sum(CESSAmt) CESSAmt");
		sql.append(" From Subvention_KnockOff_Details");
		sql.append(" Where FinID = ? and Status = ?");
		sql.append(" Group by FinID");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				Subvention sv = new Subvention();

				sv.setCgstAmt(rs.getBigDecimal("CGSTAmt"));
				sv.setSgstAmt(rs.getBigDecimal("SGSTAmt"));
				sv.setUgstAmt(rs.getBigDecimal("UGSTAmt"));
				sv.setIgstAmt(rs.getBigDecimal("IGSTAmt"));
				sv.setCessAmt(rs.getBigDecimal("CESSAmt"));

				return sv;
			}, finID, "S");
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}
}
