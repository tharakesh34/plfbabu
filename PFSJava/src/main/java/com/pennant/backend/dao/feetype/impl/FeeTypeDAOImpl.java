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
 * * FileName : FeeTypeDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 03-01-2017 * * Modified Date
 * : 03-01-2017 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 03-01-2017 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.backend.dao.feetype.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;

import com.pennant.backend.dao.feetype.FeeTypeDAO;
import com.pennant.backend.model.finance.FeeType;
import com.pennant.pff.accounting.SingleFee;
import com.pennant.pff.fee.AdviseType;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

public class FeeTypeDAOImpl extends SequenceDao<FeeType> implements FeeTypeDAO {

	public FeeTypeDAOImpl() {
		super();
	}

	@Override
	public String save(FeeType ft, TableType tableType) {
		StringBuilder sql = new StringBuilder("Insert into FeeTypes");
		sql.append(tableType.getSuffix());
		sql.append(" (FeeTypeID, FeeTypeCode, FeeTypeDesc, ManualAdvice, AdviseType, AccountSetId, Active");
		sql.append(", TaxComponent, TaxApplicable, Refundable, IncomeOrExpenseAcType, WaiverOrRefundAcType");
		sql.append(", HostFeeTypeCode, AmortzReq, DueAccReq, DueAccSet, TdsReq, PayableLinkTo, RecvFeeTypeId");
		sql.append(", AllowAutoRefund");
		sql.append(", Version, LastMntBy, LastMntOn, RecordStatus, RoleCode");
		sql.append(", NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(")");
		sql.append(" Values(");
		sql.append(" ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
		sql.append(")");

		if (ft.getId() == Long.MIN_VALUE) {
			ft.setId(getNextValue("SeqFeeTypes"));
		}

		logger.debug(Literal.SQL.concat(sql.toString()));

		try {
			jdbcOperations.update(sql.toString(), ps -> {
				int index = 1;

				ps.setLong(index++, ft.getFeeTypeID());
				ps.setString(index++, ft.getFeeTypeCode());
				ps.setString(index++, ft.getFeeTypeDesc());
				ps.setBoolean(index++, ft.isManualAdvice());
				ps.setInt(index++, ft.getAdviseType());
				ps.setObject(index++, ft.getAccountSetId());
				ps.setBoolean(index++, ft.isActive());
				ps.setString(index++, ft.getTaxComponent());
				ps.setBoolean(index++, ft.isTaxApplicable());
				ps.setBoolean(index++, ft.isRefundable());
				ps.setString(index++, ft.getIncomeOrExpenseAcType());
				ps.setString(index++, ft.getWaiverOrRefundAcType());
				ps.setString(index++, ft.getHostFeeTypeCode());
				ps.setBoolean(index++, ft.isAmortzReq());
				ps.setBoolean(index++, ft.isDueAccReq());
				ps.setObject(index++, ft.getDueAccSet());
				ps.setBoolean(index++, ft.isTdsReq());
				ps.setString(index++, ft.getPayableLinkTo());
				ps.setObject(index++, ft.getRecvFeeTypeId());
				ps.setBoolean(index++, ft.isAllowAutoRefund());
				ps.setInt(index++, ft.getVersion());
				ps.setLong(index++, ft.getLastMntBy());
				ps.setTimestamp(index++, ft.getLastMntOn());
				ps.setString(index++, ft.getRecordStatus());
				ps.setString(index++, ft.getRoleCode());
				ps.setString(index++, ft.getNextRoleCode());
				ps.setString(index++, ft.getTaskId());
				ps.setString(index++, ft.getNextTaskId());
				ps.setString(index++, ft.getRecordType());
				ps.setLong(index, ft.getWorkflowId());
			});
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		return String.valueOf(ft.getFeeTypeID());
	}

	@Override
	public void update(FeeType ft, TableType tableType) {
		StringBuilder sql = new StringBuilder("Update FeeTypes");
		sql.append(tableType.getSuffix());
		sql.append(" Set FeeTypeCode = ?, FeeTypeDesc = ?, ManualAdvice = ?, AdviseType = ?, AccountSetId = ?");
		sql.append(", Active = ?, TaxComponent = ?, TaxApplicable = ?, Refundable = ?");
		sql.append(", IncomeOrExpenseAcType = ?, WaiverOrRefundAcType = ?, HostFeeTypeCode = ?, AmortzReq = ?");
		sql.append(", DueAccReq = ? , DueAccSet = ?, TdsReq = ?, PayableLinkTo = ?, RecvFeeTypeId = ?");
		sql.append(", AllowAutoRefund= ?");
		sql.append(", Version = ?, LastMntBy = ?, LastMntOn = ?, RecordStatus = ?, RoleCode = ?");
		sql.append(", NextRoleCode = ?, TaskId = ?, NextTaskId = ?, RecordType = ?, WorkflowId = ?");
		sql.append(" Where FeeTypeId = ?");
		sql.append(QueryUtil.getConcurrencyClause(tableType));

		logger.debug(Literal.SQL.concat(sql.toString()));

		int recordCount = jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setString(index++, ft.getFeeTypeCode());
			ps.setString(index++, ft.getFeeTypeDesc());
			ps.setBoolean(index++, ft.isManualAdvice());
			ps.setInt(index++, ft.getAdviseType());
			ps.setObject(index++, ft.getAccountSetId());
			ps.setBoolean(index++, ft.isActive());
			ps.setString(index++, ft.getTaxComponent());
			ps.setBoolean(index++, ft.isTaxApplicable());
			ps.setBoolean(index++, ft.isRefundable());
			ps.setString(index++, ft.getIncomeOrExpenseAcType());
			ps.setString(index++, ft.getWaiverOrRefundAcType());
			ps.setString(index++, ft.getHostFeeTypeCode());
			ps.setBoolean(index++, ft.isAmortzReq());
			ps.setBoolean(index++, ft.isDueAccReq());
			ps.setObject(index++, ft.getDueAccSet());
			ps.setBoolean(index++, ft.isTdsReq());
			ps.setString(index++, ft.getPayableLinkTo());
			ps.setObject(index++, ft.getRecvFeeTypeId());
			ps.setBoolean(index++, ft.isAllowAutoRefund());
			ps.setInt(index++, ft.getVersion());
			ps.setLong(index++, ft.getLastMntBy());
			ps.setTimestamp(index++, ft.getLastMntOn());
			ps.setString(index++, ft.getRecordStatus());
			ps.setString(index++, ft.getRoleCode());
			ps.setString(index++, ft.getNextRoleCode());
			ps.setString(index++, ft.getTaskId());
			ps.setString(index++, ft.getNextTaskId());
			ps.setString(index++, ft.getRecordType());
			ps.setLong(index++, ft.getWorkflowId());

			ps.setLong(index++, ft.getFeeTypeID());
			if (tableType == TableType.TEMP_TAB) {
				ps.setTimestamp(index, ft.getPrevMntOn());
			} else {
				ps.setInt(index, ft.getVersion() - 1);
			}
		});

		if (recordCount == 0) {
			throw new ConcurrencyException();
		}
	}

	@Override
	public void delete(FeeType ft, TableType tableType) {
		StringBuilder sql = new StringBuilder("Delete From FeeTypes");
		sql.append(tableType.getSuffix());
		sql.append(" Where FeeTypeID = ?");
		sql.append(QueryUtil.getConcurrencyClause(tableType));

		logger.debug(Literal.SQL.concat(sql.toString()));

		try {
			int recordCount = jdbcOperations.update(sql.toString(), ps -> {
				ps.setLong(1, ft.getFeeTypeID());
				if (tableType == TableType.TEMP_TAB) {
					ps.setTimestamp(2, ft.getPrevMntOn());
				} else {
					ps.setInt(2, ft.getVersion() - 1);
				}
			});

			if (recordCount == 0) {
				throw new ConcurrencyException();
			}

		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
	}

	@Override
	public boolean isDuplicateKey(long feeTypeID, String feeTypeCode, TableType tableType) {
		String sql;
		String whereClause = "FeeTypeCode = ? and FeeTypeID != ?";
		Object[] obj = new Object[] { feeTypeCode, feeTypeID };

		switch (tableType) {
		case MAIN_TAB:
			sql = QueryUtil.getCountQuery("FeeTypes", whereClause);
			break;
		case TEMP_TAB:
			sql = QueryUtil.getCountQuery("FeeTypes_Temp", whereClause);
			break;
		default:
			sql = QueryUtil.getCountQuery(new String[] { "FeeTypes_Temp", "FeeTypes" }, whereClause);
			obj = new Object[] { feeTypeCode, feeTypeID, feeTypeCode, feeTypeID };
			break;
		}

		logger.debug(Literal.SQL.concat(sql));

		return jdbcOperations.queryForObject(sql, Integer.class, obj) > 0;
	}

	@Override
	public FeeType getFeeTypeById(final long feeTypeId, String type) {
		StringBuilder sql = getFeeTypeQuery(type);
		sql.append(" Where FeeTypeID = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), new FeeTypeRM(type), feeTypeId);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public List<FeeType> getFeeTypeListByIds(List<Long> feeTypeIds) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" ft.FeeTypeCode, ft.FeeTypeDesc, ft.ManualAdvice, ft.AdviseType, ft.TaxApplicable, ft.TdsReq");
		sql.append(", ft.IncomeOrExpenseAcType, ieat.AcTypeDesc IncomeOrExpenseAcTypeDesc");
		sql.append(", ft.WaiverOrRefundAcType, wrat.AcTypeDesc WaiverOrRefundAcTypeDesc");
		sql.append(", cgstat.AcType CgstAcType, cgstat.AcTypeDesc CgstAcTypeDesc");
		sql.append(", sgstat.AcType SgstAcType, sgstat.AcTypeDesc SgstAcTypeDesc");
		sql.append(", igstat.AcType IgstAcType, Igstat.AcTypeDesc IgstAcTypeDesc");
		sql.append(", ugstat.AcType UgstAcType, ugstat.AcTypeDesc UgstAcTypeDesc");
		sql.append(", cessat.AcType CessAcType, cessat.AcTypeDesc CessAcTypeDesc");
		sql.append(", tdsat.AcType TdsAcType, tdsat.AcTypeDesc TdsAcTypeDesc");
		sql.append(" From FeeTypes ft");
		sql.append(" Left Join RMTAccountTypes ieat On ieat.AcType = ft.IncomeOrExpenseAcType");
		sql.append(" Left Join RMTAccountTypes wrat On wrat.AcType = ft.WaiverOrRefundAcType");
		sql.append(" Left Join RMTAccountTypes cgstat On cgstat.AcType = ?");
		sql.append(" Left Join RMTAccountTypes sgstat On sgstat.AcType = ?");
		sql.append(" Left Join RMTAccountTypes igstat On igstat.AcType = ?");
		sql.append(" Left Join RMTAccountTypes ugstat On ugstat.AcType = ?");
		sql.append(" Left Join RMTAccountTypes cessat On cessat.AcType = ?");
		sql.append(" Left Join RMTAccountTypes tdsat On tdsat.AcType = ?");
		sql.append(" Where FeeTypeID IN (");
		sql.append(JdbcUtil.getInCondition(feeTypeIds));
		sql.append(") and Active = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 0;

			ps.setString(++index, SingleFee.AT_CGST);
			ps.setString(++index, SingleFee.AT_SGST);
			ps.setString(++index, SingleFee.AT_IGST);
			ps.setString(++index, SingleFee.AT_UGST);
			ps.setString(++index, SingleFee.AT_CESS);
			ps.setString(++index, SingleFee.AT_TDS);

			for (Long id : feeTypeIds) {
				ps.setLong(++index, id);
			}

			ps.setInt(++index, 1);
		}, (rs, rowNum) -> {
			FeeType ft = new FeeType();
			ft.setFeeTypeCode(rs.getString("FeeTypeCode"));
			ft.setFeeTypeDesc(rs.getString("FeeTypeDesc"));
			ft.setManualAdvice(rs.getBoolean("ManualAdvice"));
			ft.setAdviseType(rs.getInt("AdviseType"));
			ft.setTaxApplicable(rs.getBoolean("TaxApplicable"));
			ft.setTdsReq(rs.getBoolean("TdsReq"));

			ft.setIncomeOrExpenseAcType(rs.getString("IncomeOrExpenseAcType"));
			ft.setIncomeOrExpenseAcTypeDesc(rs.getString("IncomeOrExpenseAcTypeDesc"));

			ft.setWaiverOrRefundAcType(rs.getString("WaiverOrRefundAcType"));
			ft.setIncomeOrExpenseAcTypeDesc(rs.getString("IncomeOrExpenseAcTypeDesc"));

			ft.setCgstAcType(rs.getString("CgstAcType"));
			ft.setCgstAcTypeDesc(rs.getString("CgstAcTypeDesc"));

			ft.setSgstAcType(rs.getString("SgstAcType"));
			ft.setSgstAcTypeDesc(rs.getString("SgstAcTypeDesc"));

			ft.setIgstAcType(rs.getString("IgstAcType"));
			ft.setIgstAcTypeDesc(rs.getString("IgstAcTypeDesc"));

			ft.setUgstAcType(rs.getString("UgstAcType"));
			ft.setUgstAcTypeDesc(rs.getString("UgstAcTypeDesc"));

			ft.setCessAcType(rs.getString("CessAcType"));
			ft.setCessAcTypeDesc(rs.getString("CessAcTypeDesc"));

			ft.setTdsAcType(rs.getString("TdsAcType"));
			ft.setTdsAcTypeDesc(rs.getString("TdsAcTypeDesc"));

			return ft;
		});
	}

	@Override
	public List<Long> getFeeTypeIDs(List<String> feeTypeCodes) {
		StringBuilder sql = new StringBuilder("Select FeeTypeID From FeeTypes");
		sql.append(" Where FeeTypeCode IN (");
		sql.append(JdbcUtil.getInCondition(feeTypeCodes));
		sql.append(") and Active = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		List<Long> list = new ArrayList<>();

		List<FeeType> feeTypes = this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;
			for (String feeTypeCode : feeTypeCodes) {
				ps.setString(index++, feeTypeCode);
			}
			ps.setInt(index, 1);
		}, (rs, rowNum) -> {
			FeeType ft = new FeeType();

			ft.setFeeTypeID(rs.getLong(1));

			return ft;
		});

		for (FeeType feeType : feeTypes) {
			list.add(feeType.getFeeTypeID());
		}

		return list;
	}

	@Override
	public List<FeeType> getManualAdviseFeeType(int adviceType, String type) {
		StringBuilder sql = getManualAdviseFeeTypeQuery(type);
		sql.append(" Where AdviseType = ? and ManualAdvice = ? and Active = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.query(sql.toString(), new ManualAdviseFeeTypeRM(), adviceType, 1, 1);
	}

	@Override
	public List<FeeType> getAMZReqFeeTypes() {
		StringBuilder sql = getManualAdviseFeeTypeQuery("");
		sql.append(" Where AmortzReq = ? and Active = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.query(sql.toString(), new ManualAdviseFeeTypeRM(), true, true);
	}

	@Override
	public FeeType getApprovedFeeTypeByFeeCode(String feeTypeCd) {
		StringBuilder sql = new StringBuilder("Select FeeTypeID, FeeTypeCode, FeeTypeDesc");
		sql.append(", Active, ManualAdvice, AdviseType, AccountSetId, HostFeeTypeCode, AmortzReq");
		sql.append(", TaxApplicable, TaxComponent, Refundable, DueAccReq");
		sql.append(", DueAccSet, TdsReq, IncomeOrExpenseAcType, WaiverOrRefundAcType, PayableLinkTo, RecvFeeTypeId");
		sql.append(" from FeeTypes");
		sql.append(" Where FeetypeCode = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				FeeType fee = new FeeType();

				fee.setFeeTypeID(rs.getLong("FeeTypeID"));
				fee.setFeeTypeCode(rs.getString("FeeTypeCode"));
				fee.setFeeTypeDesc(rs.getString("FeeTypeDesc"));
				fee.setActive(rs.getBoolean("Active"));
				fee.setManualAdvice(rs.getBoolean("ManualAdvice"));
				fee.setAdviseType(rs.getInt("AdviseType"));
				fee.setAccountSetId(JdbcUtil.getLong(rs.getObject("AccountSetId")));
				fee.setHostFeeTypeCode(rs.getString("HostFeeTypeCode"));
				fee.setAmortzReq(rs.getBoolean("AmortzReq"));
				fee.setTaxApplicable(rs.getBoolean("TaxApplicable"));
				fee.setTaxComponent(rs.getString("TaxComponent"));
				fee.setRefundable(rs.getBoolean("Refundable"));
				fee.setDueAccReq(rs.getBoolean("DueAccReq"));
				fee.setDueAccSet(JdbcUtil.getLong(rs.getObject("DueAccSet")));
				fee.setTdsReq(rs.getBoolean("TdsReq"));
				fee.setIncomeOrExpenseAcType(rs.getString("IncomeOrExpenseAcType"));
				fee.setWaiverOrRefundAcType(rs.getString("WaiverOrRefundAcType"));
				fee.setPayableLinkTo(rs.getString("PayableLinkTo"));
				fee.setRecvFeeTypeId(rs.getLong("RecvFeeTypeId"));

				return fee;
			}, feeTypeCd);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public FeeType getTaxDetailByCode(final String feeTypeCode) {
		StringBuilder sql = new StringBuilder();
		sql.append("Select FeeTypeID, Active, ManualAdvice, AdviseType");
		sql.append(", HostFeeTypeCode, DueAccReq, DueAccSet");
		sql.append(", TaxComponent, TaxApplicable, AmortzReq, AccountSetId");
		sql.append(", FeeTypeCode, FeeTypeDesc, Refundable, TdsReq");
		sql.append(" From FeeTypes Where FeeTypeCode = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		try {
			return jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				FeeType f = new FeeType();

				f.setFeeTypeID(rs.getLong("FeeTypeID"));
				f.setActive(rs.getBoolean("Active"));
				f.setManualAdvice(rs.getBoolean("ManualAdvice"));
				f.setAdviseType(rs.getInt("AdviseType"));
				f.setHostFeeTypeCode(rs.getString("HostFeeTypeCode"));
				f.setDueAccReq(rs.getBoolean("DueAccReq"));
				f.setDueAccSet(JdbcUtil.getLong(rs.getObject("DueAccSet")));
				f.setTaxComponent(rs.getString("TaxComponent"));
				f.setTaxApplicable(rs.getBoolean("TaxApplicable"));
				f.setAmortzReq(rs.getBoolean("AmortzReq"));
				f.setAccountSetId(JdbcUtil.getLong(rs.getObject("AccountSetId")));
				f.setFeeTypeCode(rs.getString("FeeTypeCode"));
				f.setFeeTypeDesc(rs.getString("FeeTypeDesc"));
				f.setRefundable(rs.getBoolean("Refundable"));
				f.setTdsReq(rs.getBoolean("TdsReq"));

				return f;
			}, feeTypeCode);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public int getAccountingSetIdCount(long accountSetId, String type) {
		StringBuilder sql = new StringBuilder("Select Count(AccountSetId) From FeeTypes");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where AccountSetId = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.queryForObject(sql.toString(), Integer.class, accountSetId);
	}

	@Override
	public Long getFinFeeTypeIdByFeeType(String feeTypeCode, String type) {
		StringBuilder sql = new StringBuilder("Select FeeTypeID From FeeTypes");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FeeTypeCode = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		try {
			return jdbcOperations.queryForObject(sql.toString(), Long.class, feeTypeCode);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public String getTaxCompByCode(String feeTypeCode) {
		String sql = "Select TaxComponent From FeeTypes Where FeeTypeCode = ?";

		logger.debug(Literal.SQL.concat(sql));

		try {
			return this.jdbcOperations.queryForObject(sql, String.class, feeTypeCode);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public Long getFeeTypeId(String feeTypeCode) {
		String sql = "Select FeeTypeID From FeeTypes Where FeeTypeCode = ?";

		logger.debug(Literal.SQL.concat(sql));

		try {
			return this.jdbcOperations.queryForObject(sql, Long.class, feeTypeCode);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public boolean isFeeTypeAmortzReq(String feeTypeCode) {
		String sql = "Select AmortzReq From FeeTypes Where FeeTypeCode = ?";

		logger.debug(Literal.SQL.concat(sql));

		try {
			return this.jdbcOperations.queryForObject(sql, Boolean.class, feeTypeCode);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return false;
		}
	}

	@Override
	public String getTaxComponent(String feeTypeCode) {
		String sql = "Select TaxComponent from FeeTypes Where FeeTypeCode = ?";

		logger.debug(Literal.SQL.concat(sql));

		try {
			return jdbcOperations.queryForObject(sql, String.class, feeTypeCode);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public long getManualAdviseFeeTypeById(long id) {
		String sql = "Select FeeTypeID From FeeTypes_AView Where AdviseType = ? and ManualAdvice = ? and Active = ? and FeeTypeID = ?";

		logger.debug(Literal.SQL.concat(sql));

		try {
			return jdbcOperations.queryForObject(sql, Long.class, 1, 1, 1, id);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return Long.MIN_VALUE;
		}
	}

	@Override
	public FeeType getFeeTypeByRecvFeeTypeId(long feeTypeID) {
		String sql = "Select FeeTypeID, FeeTypeCode, FeeTypeDesc From FeeTypes Where FeeTypeID = ?";

		logger.debug(Literal.SQL.concat(sql));

		try {
			return this.jdbcOperations.queryForObject(sql, (rs, rowNum) -> {
				FeeType ft = new FeeType();

				ft.setFeeTypeID(rs.getLong("FeeTypeID"));
				ft.setFeeTypeCode(rs.getString("FeeTypeCode"));
				ft.setFeeTypeDesc(rs.getString("FeeTypeDesc"));

				return ft;
			}, feeTypeID);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public boolean isValidFee(String feeTypeCode, int adviseType) {
		StringBuilder sql = new StringBuilder("Select count(AdviseID) ");
		sql.append(" From ManualAdvise ma");
		sql.append(" Inner Join FeeTypes ft on ft.FeeTypeID = ma.FeeTypeID");
		sql.append(" Where ft.FeeTypeCode = ? and ma.AdviseType = ?");
		sql.append(" and ma.ManualAdvice = ? and ma.Active = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return jdbcOperations.queryForObject(sql.toString(), Integer.class, feeTypeCode, adviseType, 1, 1) > 0;
	}

	private StringBuilder getManualAdviseFeeTypeQuery(String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FeeTypeID, FeeTypeCode, FeeTypeDesc, Active, ManualAdvice");
		sql.append(", AdviseType, AccountSetId, HostFeeTypeCode, AmortzReq, TaxApplicable");
		sql.append(", TaxComponent, Refundable, DueAccReq, DueAccSet, TdsReq");
		sql.append(" From FeeTypes");
		sql.append(type);

		return sql;
	}

	private class ManualAdviseFeeTypeRM implements RowMapper<FeeType> {

		@Override
		public FeeType mapRow(ResultSet rs, int rowNum) throws SQLException {
			FeeType ft = new FeeType();

			ft.setFeeTypeID(rs.getLong("FeeTypeID"));
			ft.setFeeTypeCode(rs.getString("FeeTypeCode"));
			ft.setFeeTypeDesc(rs.getString("FeeTypeDesc"));
			ft.setActive(rs.getBoolean("Active"));
			ft.setManualAdvice(rs.getBoolean("ManualAdvice"));
			ft.setAdviseType(rs.getInt("AdviseType"));
			ft.setAccountSetId(JdbcUtil.getLong(rs.getObject("AccountSetId")));
			ft.setHostFeeTypeCode(rs.getString("HostFeeTypeCode"));
			ft.setAmortzReq(rs.getBoolean("AmortzReq"));
			ft.setTaxApplicable(rs.getBoolean("TaxApplicable"));
			ft.setTaxComponent(rs.getString("TaxComponent"));
			ft.setRefundable(rs.getBoolean("Refundable"));
			ft.setDueAccReq(rs.getBoolean("DueAccReq"));
			ft.setDueAccSet(JdbcUtil.getLong(rs.getObject("DueAccSet")));
			ft.setTdsReq(rs.getBoolean("TdsReq"));

			return ft;
		}
	}

	private StringBuilder getFeeTypeQuery(String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FeeTypeID, FeeTypeCode, FeeTypeDesc, Active, ManualAdvice, Refundable");
		sql.append(", AdviseType, AccountSetId, TaxComponent, TaxApplicable, IncomeOrExpenseAcType");
		sql.append(", WaiverOrRefundAcType, HostFeeTypeCode, AmortzReq, DueAccReq, DueAccSet, TdsReq");
		sql.append(", AllowAutoRefund");

		if (type.contains("View")) {
			sql.append(", AccountSetCode, AccountSetCodeName, DueAcctSetCode, DueAcctSetCodeName, AcType, AcTypeDesc");
		}

		sql.append(", Version, LastMntOn, LastMntBy, RecordStatus, RoleCode, NextRoleCode");
		sql.append(", TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(", PayableLinkTo, RecvFeeTypeId");
		sql.append(" From FeeTypes");
		sql.append(StringUtils.trimToEmpty(type));

		return sql;
	}

	private class FeeTypeRM implements RowMapper<FeeType> {
		private String type;

		public FeeTypeRM(String type) {
			this.type = type;
		}

		@Override
		public FeeType mapRow(ResultSet rs, int rowNum) throws SQLException {
			FeeType ft = new FeeType();

			ft.setFeeTypeID(rs.getLong("FeeTypeID"));
			ft.setFeeTypeCode(rs.getString("FeeTypeCode"));
			ft.setFeeTypeDesc(rs.getString("FeeTypeDesc"));
			ft.setActive(rs.getBoolean("Active"));
			ft.setManualAdvice(rs.getBoolean("ManualAdvice"));
			ft.setRefundable(rs.getBoolean("Refundable"));
			ft.setAdviseType(rs.getInt("AdviseType"));
			ft.setAccountSetId(JdbcUtil.getLong(rs.getObject("AccountSetId")));
			ft.setTaxComponent(rs.getString("TaxComponent"));
			ft.setTaxApplicable(rs.getBoolean("TaxApplicable"));
			ft.setIncomeOrExpenseAcType(rs.getString("IncomeOrExpenseAcType"));
			ft.setWaiverOrRefundAcType(rs.getString("WaiverOrRefundAcType"));
			ft.setHostFeeTypeCode(rs.getString("HostFeeTypeCode"));
			ft.setAmortzReq(rs.getBoolean("AmortzReq"));
			ft.setDueAccReq(rs.getBoolean("DueAccReq"));
			ft.setDueAccSet(JdbcUtil.getLong(rs.getObject("DueAccSet")));
			ft.setTdsReq(rs.getBoolean("TdsReq"));
			ft.setAllowAutoRefund(rs.getBoolean("AllowAutoRefund"));

			if (type.contains("View")) {
				ft.setAccountSetCode(rs.getString("AccountSetCode"));
				ft.setAccountSetCodeName(rs.getString("AccountSetCodeName"));
				ft.setDueAcctSetCode(rs.getString("DueAcctSetCode"));
				ft.setDueAcctSetCodeName(rs.getString("DueAcctSetCodeName"));
				ft.setAcType(rs.getString("AcType"));
				ft.setAcTypeDesc(rs.getString("AcTypeDesc"));
			}

			ft.setVersion(rs.getInt("Version"));
			ft.setLastMntBy(rs.getLong("LastMntBy"));
			ft.setLastMntOn(rs.getTimestamp("LastMntOn"));
			ft.setRecordStatus(rs.getString("RecordStatus"));
			ft.setRoleCode(rs.getString("RoleCode"));
			ft.setNextRoleCode(rs.getString("NextRoleCode"));
			ft.setTaskId(rs.getString("TaskId"));
			ft.setNextTaskId(rs.getString("NextTaskId"));
			ft.setRecordType(rs.getString("RecordType"));
			ft.setWorkflowId(rs.getLong("WorkflowId"));
			ft.setPayableLinkTo(rs.getString("PayableLinkTo"));
			ft.setRecvFeeTypeId(rs.getLong("RecvFeeTypeId"));

			return ft;
		}
	}

	@Override
	public String getFeeTypeCode(String feeTypeCode, String payableLinkTo) {
		StringBuilder sql = new StringBuilder();
		sql.append(" Select Min(FeeTypeCode) From (");
		sql.append(" Select FeeTypeCode From FeeTypes_Temp Where PayableLinkTo  = ? and FeeTypeCode != ?");
		sql.append(" Union all");
		sql.append(" Select FeeTypeCode From  FeeTypes Where PayableLinkTo  = ? and FeeTypeCode != ?");
		sql.append(") T");

		logger.debug(Literal.SQL.concat(sql.toString()));

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), String.class, payableLinkTo, feeTypeCode,
					payableLinkTo, feeTypeCode);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public String getOtrRecFeeTypeCode(String feeTypeCode, String payableLinkTo, long recvFeeTypeId) {
		StringBuilder sql = new StringBuilder();
		sql.append(" Select Min(FeeTypeCode) From (");
		sql.append(" Select FeeTypeCode From FeeTypes_Temp Where PayableLinkTo  = ?");
		sql.append(" and FeeTypeCode != ? and RecvFeeTypeId = ?");
		sql.append(" Union all");
		sql.append(" Select FeeTypeCode From  FeeTypes Where PayableLinkTo  = ?");
		sql.append(" and FeeTypeCode != ? and RecvFeeTypeId = ?");
		sql.append(") T");

		logger.debug(Literal.SQL.concat(sql.toString()));

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), String.class, payableLinkTo, feeTypeCode,
					recvFeeTypeId, payableLinkTo, feeTypeCode, recvFeeTypeId);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public long getRecvFeeTypeId(String feeTypeCode, String payableLinkTo, long recvFeeTypeId) {
		StringBuilder sql = new StringBuilder();
		sql.append("Select coalesce(Min(RecvFeeTypeId), 0) From (");
		sql.append(" Select RecvFeeTypeId From FeeTypes_Temp Where PayableLinkTo  = ?");
		sql.append(" and FeeTypeCode != ? and RecvFeeTypeId = ?");
		sql.append(" Union all");
		sql.append(" Select RecvFeeTypeId From  FeeTypes Where PayableLinkTo  = ?");
		sql.append(" and FeeTypeCode != ? and RecvFeeTypeId = ?");
		sql.append(") T");

		logger.debug(Literal.SQL.concat(sql.toString()));

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), long.class, payableLinkTo, feeTypeCode,
					recvFeeTypeId, payableLinkTo, feeTypeCode, recvFeeTypeId);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return 0;
		}
	}

	@Override
	public FeeType getPayableFeeType(String feeTypeCode) {
		StringBuilder sql = new StringBuilder();
		sql.append("Select fe.FeeTypeCode, fe.FeeTypeDesc, fe.Refundable");
		sql.append(", fe.PayableLinkTo, fe.AdviseType, fe.FeeTypeID, fe.RecvFeeTypeId");
		sql.append(" From FeeTypes fee");
		sql.append(" Inner join (select case when PayableLinkTo = ?");
		sql.append(" then (Select FeeTypeCode From FeeTypes where FeeTypeId = f.RecvFeeTypeId)");
		sql.append(" else PayableLinkTo end Type, FeeTypeCode, FeeTypeDesc, Refundable");
		sql.append(", PayableLinkTo, AdviseType, FeeTypeID, RecvFeeTypeId From FeeTypes f) fe");
		sql.append(" on fee.FeeTypeCode = fe.Type and fee.FeeTypeCode = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				FeeType ft = new FeeType();

				ft.setFeeTypeID(rs.getLong("FeeTypeID"));
				ft.setRecvFeeTypeCode(rs.getString("FeeTypeCode"));
				ft.setRecvFeeTypeDesc(rs.getString("FeeTypeDesc"));
				ft.setRefundable(rs.getBoolean("Refundable"));
				ft.setPayableLinkTo(rs.getString("PayableLinkTo"));
				ft.setAdviseType(rs.getInt("AdviseType"));
				ft.setRecvFeeTypeId(JdbcUtil.getLong(rs.getObject("RecvFeeTypeId")));

				return ft;
			}, "MANADV", feeTypeCode);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public boolean isValidFeeType(String feeTypeCode) {
		StringBuilder sql = new StringBuilder();
		sql.append("select count(FeeTypeCode) from feetypes where feetypeCode=? and Active=?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.queryForObject(sql.toString(), Integer.class, feeTypeCode, 1) > 0;
	}

	@Override
	public List<String> getReceivableFeeTypes() {
		String sql = "Select FeeTypeCode From FeeTypes Where AdviseType = ? and Active = ?";

		logger.debug(Literal.SQL.concat(sql));

		return this.jdbcOperations.query(sql, (rs, rownum) -> rs.getString(1), AdviseType.RECEIVABLE.id(), 1);
	}

	@Override
	public Long getPayableFeeTypeID(String code) {
		String sql = "Select FeeTypeID From FeeTypes Where FeeTypeCode = ? and AdviseType = ? and Active = ?";

		logger.debug(Literal.SQL.concat(sql));

		try {
			return this.jdbcOperations.queryForObject(sql, Long.class, code, AdviseType.PAYABLE.id(), 1);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}
}