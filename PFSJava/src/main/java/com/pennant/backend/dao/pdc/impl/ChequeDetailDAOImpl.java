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
 * * FileName : ChequeDetailDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 27-11-2017 * * Modified
 * Date : 27-11-2017 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 27-11-2017 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.pdc.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;

import com.pennant.backend.dao.pdc.ChequeDetailDAO;
import com.pennant.backend.model.finance.ChequeDetail;
import com.pennant.backend.model.finance.PaymentInstruction;
import com.pennant.backend.model.mandate.Mandate;
import com.pennant.backend.util.DisbursementConstants;
import com.pennant.pff.mandate.ChequeSatus;
import com.pennant.pff.mandate.InstrumentType;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;
import com.pennanttech.pff.presentment.model.PresentmentDetail;

/**
 * Data access layer implementation for <code>ChequeDetail</code> with set of CRUD operations.
 */
public class ChequeDetailDAOImpl extends SequenceDao<Mandate> implements ChequeDetailDAO {

	public ChequeDetailDAOImpl() {
		super();
	}

	@Override
	public ChequeDetail getChequeDetail(long chequeDetailsID, String type) {
		StringBuilder sql = getSqlQuery(type);
		sql.append(" Where ChequeDetailsID = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		try {
			return jdbcOperations.queryForObject(sql.toString(), new ChequeDetailRM(type), chequeDetailsID);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public List<ChequeDetail> getChequeDetailList(long headerID, String type) {
		StringBuilder sql = getSqlQuery(type);
		sql.append(" Where HeaderID = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		List<ChequeDetail> list = jdbcOperations.query(sql.toString(), ps -> ps.setLong(1, headerID),
				new ChequeDetailRM(type));

		return list.stream().sorted((l1, l2) -> Long.compare(l1.getChequeDetailsID(), l2.getChequeDetailsID()))
				.collect(Collectors.toList());
	}

	@Override
	public boolean isDuplicateKey(long chequeID, long branchID, String accountNo, String chequeSerial, TableType type) {
		String serialNo = String.valueOf(chequeSerial);

		String sql;

		String whereClause = "BankBranchID = ? and AccountNo = ? and ChequeSerialNo = ? and ChequeDetailsID != ?";

		Object[] obj = new Object[] { branchID, accountNo, serialNo, chequeID };

		switch (type) {
		case MAIN_TAB:
			sql = QueryUtil.getCountQuery("ChequeDetail", whereClause);
			break;
		case TEMP_TAB:
			sql = QueryUtil.getCountQuery("ChequeDetail_Temp", whereClause);
			break;
		default:
			sql = QueryUtil.getCountQuery(new String[] { "ChequeDetail_Temp", "ChequeDetail" }, whereClause);
			obj = new Object[] { branchID, accountNo, serialNo, chequeID, branchID, accountNo, serialNo, chequeID };
			break;
		}

		logger.debug(Literal.SQL.concat(sql));

		return jdbcOperations.queryForObject(sql, Integer.class, obj) > 0;
	}

	@Override
	public String save(ChequeDetail cheque, TableType type) {
		StringBuilder sql = new StringBuilder("Insert into ChequeDetail");
		sql.append(type.getSuffix());
		sql.append("(ChequeDetailsID, HeaderID, BankBranchID, AccountNo, ChequeSerialNo, ChequeDate");
		sql.append(", EMIRefNo, Amount, ChequeCcy, Status, Active, DocumentName, DocumentRef, ChequeType");
		sql.append(", ChequeStatus, AccountType, AccHolderName, Version, LastMntBy, LastMntOn");
		sql.append(", RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" Values(");
		sql.append(" ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
		sql.append(", ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		if (cheque.getId() == Long.MIN_VALUE || cheque.getId() == 0) {
			cheque.setId(getNextValue("SeqChequeDetail"));
		}

		logger.debug(Literal.SQL.concat(sql.toString()));

		try {
			jdbcOperations.update(sql.toString(), ps -> {
				int index = 1;

				ps.setLong(index++, cheque.getChequeDetailsID());
				ps.setLong(index++, cheque.getHeaderID());
				ps.setLong(index++, cheque.getBankBranchID());
				ps.setString(index++, cheque.getAccountNo());
				ps.setString(index++, cheque.getChequeSerialNumber());
				ps.setDate(index++, JdbcUtil.getDate(cheque.getChequeDate()));
				ps.setInt(index++, cheque.geteMIRefNo());
				ps.setBigDecimal(index++, cheque.getAmount());
				ps.setString(index++, cheque.getChequeCcy());
				ps.setString(index++, cheque.getStatus());
				ps.setBoolean(index++, cheque.isActive());
				ps.setString(index++, cheque.getDocumentName());
				ps.setObject(index++, cheque.getDocumentRef());
				ps.setString(index++, cheque.getChequeType());
				ps.setString(index++, cheque.getChequeStatus());
				ps.setString(index++, cheque.getAccountType());
				ps.setString(index++, cheque.getAccHolderName());
				ps.setInt(index++, cheque.getVersion());
				ps.setLong(index++, cheque.getLastMntBy());
				ps.setTimestamp(index++, cheque.getLastMntOn());
				ps.setString(index++, cheque.getRecordStatus());
				ps.setString(index++, cheque.getRoleCode());
				ps.setString(index++, cheque.getNextRoleCode());
				ps.setString(index++, cheque.getTaskId());
				ps.setString(index++, cheque.getNextTaskId());
				ps.setString(index++, cheque.getRecordType());
				ps.setLong(index++, cheque.getWorkflowId());
			});
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		return String.valueOf(cheque.getChequeDetailsID());
	}

	@Override
	public void update(ChequeDetail cheque, TableType type) {
		StringBuilder sql = new StringBuilder("Update ChequeDetail");
		sql.append(type.getSuffix());
		sql.append(" Set HeaderID = ?, BankBranchID = ?, AccountNo = ?, ChequeSerialNo = ?");
		sql.append(", ChequeDate = ?, EMIRefNo = ?, Amount = ?, ChequeCcy = ?, Status = ?");
		sql.append(", Active = ?, DocumentName = ?, DocumentRef = ?, ChequeType = ?");
		sql.append(", ChequeStatus = ?, AccountType = ?, AccHolderName = ?");
		sql.append(", Version = ?, LastMntBy = ?, LastMntOn = ?, RecordStatus = ?, RoleCode = ?");
		sql.append(", NextRoleCode = ?, TaskId = ?, NextTaskId = ?, RecordType = ?, WorkflowId = ?");
		sql.append(" Where chequeDetailsID = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		int recordCount = jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, cheque.getHeaderID());
			ps.setLong(index++, cheque.getBankBranchID());
			ps.setString(index++, cheque.getAccountNo());
			ps.setString(index++, cheque.getChequeSerialNumber());
			ps.setDate(index++, JdbcUtil.getDate(cheque.getChequeDate()));
			ps.setInt(index++, cheque.geteMIRefNo());
			ps.setBigDecimal(index++, cheque.getAmount());
			ps.setString(index++, cheque.getChequeCcy());
			ps.setString(index++, cheque.getStatus());
			ps.setBoolean(index++, cheque.isActive());
			ps.setString(index++, cheque.getDocumentName());
			ps.setObject(index++, cheque.getDocumentRef());
			ps.setString(index++, cheque.getChequeType());
			ps.setString(index++, cheque.getChequeStatus());
			ps.setString(index++, cheque.getAccountType());
			ps.setString(index++, cheque.getAccHolderName());
			ps.setInt(index++, cheque.getVersion());
			ps.setLong(index++, cheque.getLastMntBy());
			ps.setTimestamp(index++, cheque.getLastMntOn());
			ps.setString(index++, cheque.getRecordStatus());
			ps.setString(index++, cheque.getRoleCode());
			ps.setString(index++, cheque.getNextRoleCode());
			ps.setString(index++, cheque.getTaskId());
			ps.setString(index++, cheque.getNextTaskId());
			ps.setString(index++, cheque.getRecordType());
			ps.setLong(index++, cheque.getWorkflowId());

			ps.setLong(index++, cheque.getChequeDetailsID());
		});

		if (recordCount == 0) {
			throw new ConcurrencyException();
		}
	}

	@Override
	public void delete(ChequeDetail cheque, TableType type) {
		StringBuilder sql = new StringBuilder("Delete from ChequeDetail");
		sql.append(type.getSuffix());
		sql.append(" Where ChequeDetailsID = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		try {
			jdbcOperations.update(sql.toString(), ps -> ps.setLong(1, cheque.getChequeDetailsID()));
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
	}

	@Override
	public void batchUpdateChequeStatus(List<Long> detailIDs, String status) {
		String sql = "Update ChequeDetail Set Chequestatus = ? where ChequeDetailsId = ?";

		logger.debug(Literal.SQL.concat(sql));

		jdbcOperations.batchUpdate(sql, new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				int index = 1;
				long id = detailIDs.get(i);

				ps.setString(index++, status);

				ps.setLong(index, id);
			}

			@Override
			public int getBatchSize() {
				return detailIDs.size();
			}
		});
	}

	@Override
	public int updateChequeStatus(List<PresentmentDetail> presentments) {
		List<Long> detailIDs = new ArrayList<>();
		presentments.forEach(pd -> detailIDs.add(pd.getMandateId()));

		batchUpdateChequeStatus(detailIDs, ChequeSatus.PRESENT);

		return detailIDs.size();
	}

	@Override
	public void updateChequeStatus(long detailID, String status) {
		List<Long> detailIDs = new ArrayList<>();
		detailIDs.add(detailID);

		batchUpdateChequeStatus(detailIDs, status);
	}

	@Override
	public boolean isChequeExists(long headerID, Date chequeDate) {
		String sql = "Select count(ChequeDetailsID) From ChequeDetail_View Where HeaderID = ? and ChequeDate = ?";

		logger.debug(Literal.SQL.concat(sql));

		return jdbcOperations.queryForObject(sql, Integer.class, headerID, JdbcUtil.getDate(chequeDate)) > 0;
	}

	@Override
	public boolean isRelisedAllCheques(long finId) {
		StringBuilder sql = new StringBuilder("Select count(ChequeDetailsId)");
		sql.append(" From ChequeHeader ch");
		sql.append(" Inner Join ChequeDetail cd on cd.ChequeDetailsId = ch.HeaderId");
		sql.append(" Where ch.finID = ? and cd.ChequeStatus <> ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return jdbcOperations.queryForObject(sql.toString(), Integer.class, finId, ChequeSatus.REALISED) > 0;
	}

	private StringBuilder getSqlQuery(String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" ChequeDetailsID, HeaderID, BankBranchID, AccountNo, ChequeSerialNo, ChequeDate");
		sql.append(", EMIRefNo, Amount, ChequeCcy, Status, Active, DocumentName, DocumentRef, ChequeType");
		sql.append(", ChequeStatus, AccountType, AccHolderName, Version, LastMntOn, LastMntBy");
		sql.append(", RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");

		if (type.equals("_View")) {
			sql.append(", BankCode, BranchCode, BranchDesc, Micr, Ifsc, City, BankName");
		}
		if (type.equals("_AView")) {
			sql.append(", BankCode, BranchCode, BranchDesc, Micr, Ifsc, City, BankName");
		}

		sql.append(" From ChequeDetail");
		sql.append(type);

		return sql;
	}

	private class ChequeDetailRM implements RowMapper<ChequeDetail> {
		private String type;

		public ChequeDetailRM(String type) {
			this.type = type;
		}

		@Override
		public ChequeDetail mapRow(ResultSet rs, int rowNum) throws SQLException {
			ChequeDetail cheque = new ChequeDetail();

			cheque.setChequeDetailsID(rs.getLong("ChequeDetailsID"));
			cheque.setHeaderID(rs.getLong("HeaderID"));
			cheque.setBankBranchID(rs.getLong("BankBranchID"));
			cheque.setAccountNo(rs.getString("AccountNo"));
			cheque.setChequeSerialNumber(rs.getString("ChequeSerialNo"));
			cheque.setChequeDate(JdbcUtil.getDate(rs.getDate("ChequeDate")));
			cheque.seteMIRefNo(rs.getInt("EMIRefNo"));
			cheque.setAmount(rs.getBigDecimal("Amount"));
			cheque.setChequeCcy(rs.getString("ChequeCcy"));
			cheque.setStatus(rs.getString("Status"));
			cheque.setActive(rs.getBoolean("Active"));
			cheque.setDocumentName(rs.getString("DocumentName"));
			cheque.setDocumentRef(JdbcUtil.getLong(rs.getObject("DocumentRef")));
			cheque.setChequeType(rs.getString("ChequeType"));
			cheque.setChequeStatus(rs.getString("ChequeStatus"));
			cheque.setAccountType(rs.getString("AccountType"));
			cheque.setAccHolderName(rs.getString("AccHolderName"));
			cheque.setVersion(rs.getInt("Version"));
			cheque.setLastMntBy(rs.getLong("LastMntBy"));
			cheque.setLastMntOn(rs.getTimestamp("LastMntOn"));
			cheque.setRecordStatus(rs.getString("RecordStatus"));
			cheque.setRoleCode(rs.getString("RoleCode"));
			cheque.setNextRoleCode(rs.getString("NextRoleCode"));
			cheque.setTaskId(rs.getString("TaskId"));
			cheque.setNextTaskId(rs.getString("NextTaskId"));
			cheque.setRecordType(rs.getString("RecordType"));
			cheque.setWorkflowId(rs.getLong("WorkflowId"));
			cheque.setChequeSerialNo(rs.getInt("ChequeSerialNo"));

			if (type.equals("_View")) {
				cheque.setBankCode(rs.getString("BankCode"));
				cheque.setBranchCode(rs.getString("BranchCode"));
				cheque.setBranchDesc(rs.getString("BranchDesc"));
				cheque.setMicr(rs.getString("Micr"));
				cheque.setIfsc(rs.getString("Ifsc"));
				cheque.setCity(rs.getString("City"));
				cheque.setBankName(rs.getString("BankName"));
			}

			if (type.equals("_AView")) {
				cheque.setBankCode(rs.getString("BankCode"));
				cheque.setBranchCode(rs.getString("BranchCode"));
				cheque.setBranchDesc(rs.getString("BranchDesc"));
				cheque.setMicr(rs.getString("Micr"));
				cheque.setIfsc(rs.getString("Ifsc"));
				cheque.setCity(rs.getString("City"));
				cheque.setBankName(rs.getString("BankName"));
			}

			return cheque;
		}
	}

	@Override
	public Long getChequeDetailID(long finID) {
		String sql = "Select cd.ChequeDetailsID From ChequeHeader ch Inner Join ChequeDetail cd on cd.HeaderID = ch.HeaderID Where ch.FinID = ? and cd.ChequeType = ? and cd.ChequeStatus != ?";

		logger.debug(Literal.SQL.concat(sql));

		List<ChequeDetail> list = this.jdbcOperations.query(sql, ps -> {
			int index = 0;
			ps.setLong(++index, finID);
			ps.setString(++index, InstrumentType.PDC.name());
			ps.setString(++index, ChequeSatus.CANCELLED);
		}, (rs, rowNum) -> {
			ChequeDetail cd = new ChequeDetail();

			cd.setChequeDetailsID(rs.getLong(1));

			return cd;
		});

		list = list.stream().sorted((l1, l2) -> Long.compare(l2.getId(), l1.getId())).collect(Collectors.toList());

		if (CollectionUtils.isEmpty(list)) {
			return null;
		}

		return list.get(0).getChequeDetailsID();
	}

	@Override
	public Long getChequeDetailIDByAppDate(long finID, Date appDate) {
		String sql = "Select cd.ChequeDetailsID From ChequeHeader ch Inner Join ChequeDetail cd on cd.HeaderID = ch.HeaderID Where ch.FinID = ? and cd.ChequeType = ? and ChequeDate > ?";

		logger.debug(Literal.SQL.concat(sql));

		List<ChequeDetail> list = jdbcOperations.query(sql, ps -> {
			ps.setLong(1, finID);
			ps.setString(2, InstrumentType.PDC.name());
			ps.setDate(3, JdbcUtil.getDate(appDate));
		}, (rs, rowNum) -> {
			ChequeDetail cd = new ChequeDetail();

			cd.setChequeDetailsID(rs.getLong(1));

			return cd;
		});

		list = list.stream().sorted((l1, l2) -> Long.compare(l2.getId(), l1.getId())).collect(Collectors.toList());

		if (CollectionUtils.isEmpty(list)) {
			return null;
		}

		return list.get(0).getChequeDetailsID();
	}

	@Override
	public PaymentInstruction getBeneficiary(long id) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" bb.BankBranchID, bb.BankCode, bb.BranchDesc, bd.BankName, bb.IFSC");
		sql.append(", pvc.PCCityName, cd.AccountNo, cd.AccHolderName, b.DefChequeDDPrintLoc, fm.FinType, fm.FinBranch");
		sql.append(" From ChequeDetail cd");
		sql.append(" Inner Join ChequeHeader ch on ch.HeaderID = cd.HeaderID");
		sql.append(" Inner Join FinanceMain fm on fm.FinID = ch.FinID");
		sql.append(" Inner Join BankBranches bb on bb.BankBranchID = cd.BankBranchID");
		sql.append(" Inner Join BMTBankDetail bd on bd.BankCode = bb.BankCode");
		sql.append(" Inner join RMTBranches b on b.BranchCode = fm.FinBranch");
		sql.append(" Inner Join RMTProvincevsCity pvc on pvc.PCCity = bb.City");
		sql.append(" Where ChequeDetailsID = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		try {
			return jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				PaymentInstruction pi = new PaymentInstruction();

				pi.setBankBranchId(rs.getLong("BankBranchID"));
				pi.setBankBranchCode(rs.getString("BankCode"));
				pi.setBranchDesc(rs.getString("BranchDesc"));
				pi.setBankName(rs.getString("BankName"));
				pi.setBankBranchIFSC(rs.getString("IFSC"));
				pi.setpCCityName(rs.getString("PCCityName"));
				pi.setAccountNo(rs.getString("AccountNo"));
				pi.setAcctHolderName(rs.getString("AccHolderName"));
				pi.setIssuingBank(rs.getString("BankCode"));
				pi.setIssuingBankName(rs.getString("BranchDesc"));
				pi.setPartnerBankAcType(rs.getString("BranchDesc"));
				pi.setPartnerBankAc(rs.getString("AccountNo"));

				pi.setPaymentType(DisbursementConstants.PAYMENT_TYPE_CHEQUE);
				pi.setFavourName(rs.getString("AccHolderName"));
				pi.setPrintingLoc(rs.getString("DefChequeDDPrintLoc"));
				pi.setFinType(rs.getString("FinType"));
				pi.setFinBranch(rs.getString("FinBranch"));

				return pi;
			}, id);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
		}
		return null;
	}

	@Override
	public void deleteCheques(ChequeDetail cheque) {
		String sql = "Delete from ChequeDetail Where ChequeSerialNo = ? and AccountNo = ?";

		logger.debug(Literal.SQL.concat(sql));

		this.jdbcOperations.update(sql, ps -> {
			ps.setString(1, cheque.getChequeSerialNumber());
			ps.setString(2, cheque.getAccountNo());
		});
	}

	@Override
	public String getChequeStatus(String chequeSerial, String accountNo) {
		String sql = "Select ChequeStatus From ChequeDetail  Where ChequeSerialNo = ? and AccountNo = ?";

		logger.debug(Literal.SQL.concat(sql));

		try {
			return jdbcOperations.queryForObject(sql, String.class, chequeSerial, accountNo);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public boolean isDuplicateKeyPresent(String accountNo, String chequeSerial, TableType type) {
		String serialNo = String.valueOf(chequeSerial);

		String sql;

		String whereClause = "AccountNo = ? and ChequeSerialNo = ? ";

		Object[] obj = new Object[] { accountNo, serialNo };

		switch (type) {
		case MAIN_TAB:
			sql = QueryUtil.getCountQuery("ChequeDetail", whereClause);
			break;
		case TEMP_TAB:
			sql = QueryUtil.getCountQuery("ChequeDetail_Temp", whereClause);
			break;
		default:
			sql = QueryUtil.getCountQuery(new String[] { "ChequeDetail_Temp", "ChequeDetail" }, whereClause);
			obj = new Object[] { accountNo, serialNo, accountNo, serialNo };
			break;
		}

		logger.debug(Literal.SQL.concat(sql));

		return jdbcOperations.queryForObject(sql, Integer.class, obj) > 0;
	}

}
