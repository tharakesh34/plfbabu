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
 * * FileName : TransactionEntryDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 14-12-2011 * *
 * Modified Date : 14-12-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 14-12-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.backend.dao.rmtmasters.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.pennant.backend.dao.rmtmasters.TransactionEntryDAO;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.rmtmasters.TransactionEntry;
import com.pennant.backend.model.rulefactory.Rule;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;

/**
 * DAO methods implementation for the <b>TransactionEntry model</b> class.<br>
 */
public class TransactionEntryDAOImpl extends BasicDao<TransactionEntry> implements TransactionEntryDAO {
	private static Logger logger = LogManager.getLogger(TransactionEntryDAOImpl.class);

	public TransactionEntryDAOImpl() {
		super();
	}

	/**
	 * This method set the Work Flow id based on the module name and return the new TransactionEntry
	 * 
	 * @return TransactionEntry
	 */
	@Override
	public TransactionEntry getTransactionEntry() {
		logger.debug("Entering");
		WorkFlowDetails workFlowDetails = WorkFlowUtil.getWorkFlowDetails("TransactionEntry");
		TransactionEntry transactionEntry = new TransactionEntry();
		if (workFlowDetails != null) {
			transactionEntry.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		logger.debug("Leaving");
		return transactionEntry;
	}

	/**
	 * This method get the module from method getTransactionEntry() and set the new record flag as true and return
	 * TransactionEntry()
	 * 
	 * @return TransactionEntry
	 */
	@Override
	public TransactionEntry getNewTransactionEntry() {
		logger.debug("Entering");
		TransactionEntry transactionEntry = getTransactionEntry();
		transactionEntry.setNewRecord(true);
		logger.debug("Leaving");
		return transactionEntry;
	}

	/**
	 * Fetch the Record Transaction Entry details by key field
	 * 
	 * @param id   (int)
	 * @param type (String) ""/_Temp/_View
	 * @return TransactionEntry
	 */
	@Override
	public TransactionEntry getTransactionEntryById(final long id, int transOrder, String type) {
		logger.debug("Entering");
		TransactionEntry transactionEntry = new TransactionEntry();

		transactionEntry.setId(id);
		transactionEntry.setTransOrder(transOrder);

		StringBuilder selectSql = new StringBuilder("Select AccountSetid, TransOrder, TransDesc,");
		selectSql.append(" Debitcredit, ShadowPosting, Account, AccountType,AccountBranch ,AccountSubHeadRule,");
		selectSql.append(
				" TranscationCode, RvsTransactionCode, AmountRule,FeeCode,ChargeType,EntryByInvestment,OpenNewFinAc,");
		selectSql.append(" PostToSys, DerivedTranOrder, FeeRepeat, ReceivableOrPayable, AssignmentEntry, Bulking, ");
		if (StringUtils.trimToEmpty(type).contains("View")) {
			selectSql.append(" lovDescAccountTypeName,lovDescAccountSubHeadRuleName,");
			selectSql.append(" lovDescTranscationCodeName,lovDescRvsTransactionCodeName, ");
			selectSql.append(" lovDescEventCodeName,lovDescAccSetCodeName,lovDescAccSetCodeDesc,");
			selectSql.append(" lovDescAccountBranchName,lovDescSysInAcTypeName,");
		}
		selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode,");
		selectSql.append(" TaskId, NextTaskId, RecordType, WorkflowId ");
		selectSql.append(" From RMTTransactionEntry");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where AccountSetid =:AccountSetid and TransOrder=:TransOrder");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(transactionEntry);
		RowMapper<TransactionEntry> typeRowMapper = BeanPropertyRowMapper.newInstance(TransactionEntry.class);

		try {
			return this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	/**
	 * Fetch the Record Transaction Entry details by key field
	 * 
	 * @param id   (int)
	 * @param type (String) ""/_Temp/_View
	 * @return TransactionEntry
	 */
	@Override
	public List<TransactionEntry> getListTransactionEntryById(final long id, String type, boolean postingsProcess) {
		logger.debug("Entering");

		TransactionEntry transactionEntry = new TransactionEntry();
		transactionEntry.setId(id);

		StringBuilder selectSql = new StringBuilder("Select AccountSetid, TransOrder, TransDesc,");
		selectSql.append(" Debitcredit, ShadowPosting, Account, AccountType,AccountBranch,");
		selectSql.append(" AccountSubHeadRule, TranscationCode, RvsTransactionCode, AmountRule,ChargeType,");
		selectSql.append(" PostToSys, DerivedTranOrder, FeeRepeat, ReceivableOrPayable, AssignmentEntry, Bulking");
		selectSql.append(", FeeCode , EntryByInvestment ,OpenNewFinAc");
		if (StringUtils.trimToEmpty(type).contains("View")) {
			selectSql.append(" , lovDescEventCodeName, lovDescEventCodeDesc ");
			if (!postingsProcess) {
				selectSql.append(" ,lovDescAccountTypeName,lovDescAccountSubHeadRuleName,");
				selectSql.append(" lovDescTranscationCodeName,lovDescRvsTransactionCodeName ,");
				selectSql.append(" lovDescAccSetCodeName,lovDescAccSetCodeDesc,");
				selectSql.append(" lovDescAccountBranchName,lovDescSysInAcTypeName, ");
			}
		}
		if (!postingsProcess) {
			selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode,");
			selectSql.append(" TaskId, NextTaskId, RecordType, WorkflowId");
		}
		selectSql.append(" From RMTTransactionEntry");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where AccountSetid =:AccountSetid");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(transactionEntry);
		RowMapper<TransactionEntry> typeRowMapper = BeanPropertyRowMapper.newInstance(TransactionEntry.class);
		logger.debug("Leaving");
		return this.jdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
	}

	/**
	 * get the list of AccountSetid's
	 * 
	 * @return List
	 */
	@Override
	public List<Long> getAccountSetIds() {
		logger.debug("Entering");

		StringBuilder selectSql = new StringBuilder("select distinct AccountSetid");
		selectSql.append(" From RMTTransactionEntry");

		logger.debug("selectSql: " + selectSql.toString());

		List<Long> accountSetIDs = this.jdbcTemplate.queryForList(selectSql.toString(),
				new BeanPropertySqlParameterSource(""), Long.class);
		logger.debug("Leaving");
		return accountSetIDs;
	}

	public static List<TransactionEntry> sortTxnEntries(List<TransactionEntry> transactionEntries) {

		Collections.sort(transactionEntries, new Comparator<TransactionEntry>() {
			@Override
			public int compare(TransactionEntry detail1, TransactionEntry detail2) {
				return (Integer.valueOf(detail1.getTransOrder()).compareTo(Integer.valueOf(detail2.getTransOrder())));
			}
		});

		return transactionEntries;
	}

	/**
	 * Fetch the Record Transaction Entry details by key field
	 * 
	 * @param id   (int)
	 * @param type (String) ""/_Temp/_View
	 * @return TransactionEntry
	 */
	@Override
	public List<TransactionEntry> getListTranEntryForBatch(final long id, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" te.AccountSetid, te.TransOrder, te.TransDesc, te.Debitcredit, te.ShadowPosting");
		sql.append(", te.Account, te.AccountType, te.AccountBranch, te.AccountSubHeadRule, te.TranscationCode");
		sql.append(", te.RvsTransactionCode, te.AmountRule, te.ChargeType, te.FeeCode, te.OpenNewFinAc");
		sql.append(", te.PostToSys, te.DerivedTranOrder, te.FeeRepeat, te.ReceivableOrPayable");
		sql.append(", te.AssignmentEntry, te.Bulking, am.HostAccount GlCode");
		sql.append(" From RMTTransactionEntry");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" te Left join AccountMapping am on am.Account = te.Account");
		sql.append(" Where AccountSetid = ?");

		logger.trace(Literal.SQL + sql.toString());

		List<TransactionEntry> list = this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;
			ps.setLong(index, id);

		}, (rs, rowNum) -> {
			TransactionEntry te = new TransactionEntry();

			te.setAccountSetid(rs.getLong("AccountSetid"));
			te.setTransOrder(rs.getInt("TransOrder"));
			te.setTransDesc(rs.getString("TransDesc"));
			te.setDebitcredit(rs.getString("Debitcredit"));
			te.setShadowPosting(rs.getBoolean("ShadowPosting"));
			te.setAccount(rs.getString("Account"));
			te.setAccountType(rs.getString("AccountType"));
			te.setAccountBranch(rs.getString("AccountBranch"));
			te.setAccountSubHeadRule(rs.getString("AccountSubHeadRule"));
			te.setTranscationCode(rs.getString("TranscationCode"));
			te.setRvsTransactionCode(rs.getString("RvsTransactionCode"));
			te.setAmountRule(rs.getString("AmountRule"));
			te.setChargeType(rs.getString("ChargeType"));
			te.setFeeCode(rs.getString("FeeCode"));
			te.setOpenNewFinAc(rs.getBoolean("OpenNewFinAc"));
			te.setPostToSys(rs.getString("PostToSys"));
			te.setDerivedTranOrder(rs.getInt("DerivedTranOrder"));
			te.setFeeRepeat(rs.getBoolean("FeeRepeat"));
			te.setReceivableOrPayable(rs.getInt("ReceivableOrPayable"));
			te.setAssignmentEntry(rs.getBoolean("AssignmentEntry"));
			te.setBulking(rs.getBoolean("Bulking"));
			te.setGlCode(rs.getString("GlCode"));
			return te;

		});

		return sortTxnEntries(list);
	}

	/**
	 * Fetch the Record Transaction Entry details by key field and RefType
	 * 
	 * @param id   (int)
	 * @param type (String) ""/_Temp/_View
	 * @return TransactionEntry
	 */
	@Override
	public List<TransactionEntry> getListTransactionEntryByRefType(String finType, String finEvent, int refType,
			String roleCode, String type, boolean postingsProcess) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Select");
		sql.append(" te.AccountSetid, te.TransOrder, te.TransDesc, te.Debitcredit, te.ShadowPosting");
		sql.append(", te.Account, te.AccountType, te.AccountBranch, te.AccountSubHeadRule");
		sql.append(", te.TranscationCode, te.RvsTransactionCode, te.AmountRule, te.ChargeType, te.FeeCode");
		sql.append(", te.EntryByInvestment, te.OpenNewFinAc, te.PostToSys, te.DerivedTranOrder");
		sql.append(", te.FeeRepeat, te.ReceivableOrPayable, te.AssignmentEntry, te.Bulking, am.HostAccount GlCode");
		if (StringUtils.trimToEmpty(type).contains("View")) {
			sql.append(", te.LovDescEventCodeName, te.LovDescAccSetCodeName");

			if (!postingsProcess) {
				sql.append(", te.LovDescAccountTypeName, te.LovDescAccountSubHeadRuleName"); // LovDescAccountTypeName
																								// column
				// is not availble in table and
				// AEView
				sql.append(", te.LovDescTranscationCodeName, te.LovDescRvsTransactionCodeName");
				sql.append(", te.LovDescAccountBranchName, te.LovDescSysInAcTypeName, te.LovDescAccSetCodeDesc");
			}
		}

		if (!postingsProcess) {
			sql.append(", Version, LastMntBy, LastMntOn, RecordStatus, RoleCode");
			sql.append(", NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		}

		sql.append(" from RMTTransactionEntry");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" te Left join AccountMapping am on am.Account = te.Account");
		sql.append("  Where AccountSetid in (");
		sql.append(" Select FinRefId from LMTFinRefDetail");
		sql.append(" Where Fintype = ? and FinEvent = ?");
		sql.append(" and FinRefType = ? and MandInputInStage like ?)");
		sql.append("  order by AccountSetid, TransOrder");

		logger.trace(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), new PreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				int index = 1;
				ps.setString(index++, finType);
				ps.setString(index++, finEvent);
				ps.setInt(index++, refType);
				ps.setString(index, "%" + roleCode + "%");
			}
		}, new RowMapper<TransactionEntry>() {
			@Override
			public TransactionEntry mapRow(ResultSet rs, int rowNum) throws SQLException {
				TransactionEntry te = new TransactionEntry();

				te.setAccountSetid(rs.getLong("AccountSetid"));
				te.setTransOrder(rs.getInt("TransOrder"));
				te.setTransDesc(rs.getString("TransDesc"));
				te.setDebitcredit(rs.getString("Debitcredit"));
				te.setShadowPosting(rs.getBoolean("ShadowPosting"));
				te.setAccount(rs.getString("Account"));
				te.setAccountType(rs.getString("AccountType"));
				te.setAccountBranch(rs.getString("AccountBranch"));
				te.setAccountSubHeadRule(rs.getString("AccountSubHeadRule"));
				te.setTranscationCode(rs.getString("TranscationCode"));
				te.setRvsTransactionCode(rs.getString("RvsTransactionCode"));
				te.setAmountRule(rs.getString("AmountRule"));
				te.setChargeType(rs.getString("ChargeType"));
				te.setFeeCode(rs.getString("FeeCode"));
				te.setEntryByInvestment(rs.getBoolean("EntryByInvestment"));
				te.setOpenNewFinAc(rs.getBoolean("OpenNewFinAc"));
				te.setPostToSys(rs.getString("PostToSys"));
				te.setDerivedTranOrder(rs.getInt("DerivedTranOrder"));
				te.setFeeRepeat(rs.getBoolean("FeeRepeat"));
				te.setReceivableOrPayable(rs.getInt("ReceivableOrPayable"));
				te.setAssignmentEntry(rs.getBoolean("AssignmentEntry"));
				te.setBulking(rs.getBoolean("Bulking"));
				te.setGlCode(rs.getString("GlCode"));

				if (StringUtils.trimToEmpty(type).contains("View")) {
					te.setLovDescEventCodeName(rs.getString("LovDescEventCodeName"));
					te.setLovDescAccSetCodeName(rs.getString("LovDescAccSetCodeName"));

					if (!postingsProcess) {
						te.setLovDescAccountTypeName(rs.getString("LovDescAccountTypeName"));
						te.setLovDescAccountSubHeadRuleName(rs.getString("LovDescAccountSubHeadRuleName"));
						te.setLovDescTranscationCodeName(rs.getString("LovDescTranscationCodeName"));
						te.setLovDescRvsTransactionCodeName(rs.getString("LovDescRvsTransactionCodeName"));
						te.setLovDescAccountBranchName(rs.getString("LovDescAccountBranchName"));
						te.setLovDescSysInAcTypeName(rs.getString("LovDescSysInAcTypeName"));
						te.setLovDescAccSetCodeDesc(rs.getString("LovDescAccSetCodeDesc"));
					}
				}

				if (!postingsProcess) {
					te.setVersion(rs.getInt("Version"));
					te.setLastMntBy(rs.getLong("LastMntBy"));
					te.setLastMntOn(rs.getTimestamp("LastMntOn"));
					te.setRecordStatus(rs.getString("RecordStatus"));
					te.setRoleCode(rs.getString("RoleCode"));
					te.setNextRoleCode(rs.getString("NextRoleCode"));
					te.setTaskId(rs.getString("TaskId"));
					te.setNextTaskId(rs.getString("NextTaskId"));
					te.setRecordType(rs.getString("RecordType"));
					te.setWorkflowId(rs.getLong("WorkflowId"));
				}

				return te;
			}
		});
	}

	/**
	 * Fetch the Record Rule Details by key field
	 * 
	 * @param id   (int)
	 * @param type (String) ""/_Temp/_View
	 * @return Rules
	 */
	@Override
	public List<Rule> getListFeeChargeRules(List<Long> accSetIdList, String ruleEvent, String type, int seqOrder) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("AccountSetId", accSetIdList);

		StringBuilder selectSql = new StringBuilder(" Select DISTINCT FeeCode from RMTTransactionEntry ");
		selectSql.append(" Where AccountSetid IN (:AccountSetId) AND  COALESCE(Feecode, ' ') <> ' ' ");
		logger.debug("selectSql: " + selectSql.toString());
		List<String> feeCodeList = this.jdbcTemplate.queryForList(selectSql.toString(), source, String.class);

		if (!feeCodeList.isEmpty()) {

			// Adding Fee Codes to List For Filter Search
			List<String> feeCodes = new ArrayList<String>();
			for (String feeCodeValue : feeCodeList) {

				String[] list = null;
				if (feeCodeValue.contains(",")) {
					list = feeCodeValue.split(",");
				}

				if (list != null && list.length > 0) {
					for (int j = 0; j < list.length; j++) {
						if (!feeCodes.contains(list[j])) {
							feeCodes.add(list[j]);
						}
					}
				} else {
					feeCodes.add(feeCodeValue);
				}
			}

			source = new MapSqlParameterSource();
			source.addValue("FeeCodes", feeCodes);
			source.addValue("RuleEvent", ruleEvent);
			source.addValue("SeqOrder", seqOrder);

			selectSql = new StringBuilder(" SELECT RuleCode, RuleCodeDesc,SQLRule, CalFeeModify,FeeToFinance, ");
			selectSql.append(" WaiverDecider , Waiver,WaiverPerc, SeqOrder ");
			selectSql.append(" From Rules");
			selectSql.append(StringUtils.trimToEmpty(type));
			selectSql.append(" where RuleModule='FEES' AND RuleCode IN (:FeeCodes) AND RuleEvent =:RuleEvent ");
			if (seqOrder != 0) {
				selectSql.append(" AND SeqOrder > :SeqOrder ");
			}
			selectSql.append(" Order BY SeqOrder ");

			logger.debug("selectSql: " + selectSql.toString());
			RowMapper<Rule> typeRowMapper = BeanPropertyRowMapper.newInstance(Rule.class);
			logger.debug("Leaving");
			return this.jdbcTemplate.query(selectSql.toString(), source, typeRowMapper);

		} else {
			logger.debug("Leaving");
			return new ArrayList<Rule>();
		}
	}

	/**
	 * Fetch the accounting fee codes list by accountingids
	 * 
	 * @param accountSetId (int)
	 * @param type         (String) ""/_Temp/_View
	 * @return Rules
	 */
	@Override
	public List<String> getFeeCodeList(List<Long> accountSetId) {
		logger.debug("Entering");

		MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
		mapSqlParameterSource.addValue("AccountSetid", accountSetId);

		StringBuilder selectSql = new StringBuilder(" Select DISTINCT FeeCode from RMTTransactionEntry");
		selectSql.append(" Where AccountSetid IN (:AccountSetid) AND  COALESCE(Feecode,' ') <> ' ' ");

		logger.debug("selectSql: " + selectSql.toString());

		return this.jdbcTemplate.queryForList(selectSql.toString(), mapSqlParameterSource, String.class);
	}

	/**
	 * Fetch the accounting fee codes list by accountingids
	 * 
	 * @param accountSetId (int)
	 * @param type         (String) ""/_Temp/_View
	 * @return Rules
	 */
	@Override
	public Map<String, String> getAccountingFeeCodes(List<Long> accountSetId) {
		logger.debug("Entering");

		MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
		mapSqlParameterSource.addValue("AccountSetid", accountSetId);

		StringBuilder selectSql = new StringBuilder(
				"SELECT DISTINCT T2.EventCode,T1.FeeCode FROM RMTTransactionEntry T1 INNER JOIN ");
		selectSql.append(" RmtAccountingSet T2 On T1.Accountsetid = T2.Accountsetid ");
		selectSql.append(" WHERE T1.AccountSetid IN (:AccountSetid) AND  COALESCE(T1.Feecode,' ') <> ' ' ");

		logger.debug("selectSql: " + selectSql.toString());

		SqlRowSet rowSet = this.jdbcTemplate.queryForRowSet(selectSql.toString(), mapSqlParameterSource);

		Map<String, String> feeCodesMap = new HashMap<String, String>();

		while (rowSet.next()) {
			if (feeCodesMap.containsKey(rowSet.getString(1))) {
				String feeCodes = feeCodesMap.get(rowSet.getString(1));
				String newFee = rowSet.getString(2);

				if (feeCodes.contains(",")) {
					for (String feeCode : feeCodes.split(",")) {
						if (!valueExist(feeCode, newFee)) {
							feeCodes = feeCodes + "," + newFee;
						}
					}
				} else {
					if (!valueExist(feeCodes, newFee)) {
						feeCodes = feeCodes + "," + newFee;
					}
				}

				feeCodesMap.put(rowSet.getString(1), feeCodes);
			} else {
				feeCodesMap.put(rowSet.getString(1), rowSet.getString(2));
			}
		}

		return feeCodesMap;
	}

	private boolean valueExist(String source, String dest) {
		if (dest.contains(",")) {
			String[] feeCodeArr = dest.split(",");
			boolean feeExist = false;
			for (String feeArr : feeCodeArr) {
				if (StringUtils.equals(feeArr, source)) {
					feeExist = true;
				}
			}
			if (feeExist) {
				return true;
			}
		} else {
			if (StringUtils.equals(dest, source)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * This method Deletes the Record from the RMTTransactionEntry or RMTTransactionEntry_Temp. if Record not deleted
	 * then throws DataAccessException with error 41003. delete Transaction Entry by key AccountSetid
	 * 
	 * @param Transaction Entry (transactionEntry)
	 * @param type        (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void delete(TransactionEntry transactionEntry, String type) {
		logger.debug("Entering");
		int recordCount = 0;

		StringBuilder deleteSql = new StringBuilder("Delete From RMTTransactionEntry");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where AccountSetid =:AccountSetid AND TransOrder =:TransOrder");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(transactionEntry);
		try {
			recordCount = this.jdbcTemplate.update(deleteSql.toString(), beanParameters);
			if (recordCount <= 0) {
				throw new ConcurrencyException();
			}
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * This method Deletes the Records from the RMTTransactionEntry or RMTTransactionEntry_Temp. delete Transaction
	 * Entry(s) by key AccountSetid
	 * 
	 * @param accountingSetId (long)
	 * @param type            (String) ""/_Temp/_View
	 * @return
	 */
	public void deleteByAccountingSetId(final long accountingSetId, String type) {
		logger.debug("Entering");
		TransactionEntry transactionEntry = new TransactionEntry();
		transactionEntry.setId(accountingSetId);

		StringBuilder deleteSql = new StringBuilder("Delete From RMTTransactionEntry");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where AccountSetid =:AccountSetid");

		logger.debug("deleteSql: " + deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(transactionEntry);
		this.jdbcTemplate.update(deleteSql.toString(), beanParameters);
		logger.debug("Leaving");
	}

	/**
	 * This method insert new Records into RMTTransactionEntry or RMTTransactionEntry_Temp. it fetches the available
	 * Sequence form SeqRMTTransactionEntry by using getNextidviewDAO().getNextId() method.
	 * 
	 * save Transaction Entry
	 * 
	 * @param Transaction Entry (transactionEntry)
	 * @param type        (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public long save(TransactionEntry transactionEntry, String type) {
		logger.debug("Entering");

		StringBuilder insertSql = new StringBuilder("Insert Into RMTTransactionEntry");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (AccountSetid, TransOrder, TransDesc, Debitcredit, ShadowPosting,");
		insertSql.append(" Account, AccountType, AccountBranch, AccountSubHeadRule, TranscationCode,");
		insertSql.append(" RvsTransactionCode, AmountRule,FeeCode,ChargeType, EntryByInvestment ,OpenNewFinAc,");
		insertSql.append(" PostToSys, DerivedTranOrder , FeeRepeat, ReceivableOrPayable, AssignmentEntry, Bulking, ");
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode,");
		insertSql.append(" TaskId, NextTaskId, RecordType, WorkflowId) ");
		insertSql.append(" Values(:AccountSetid, :TransOrder, :TransDesc, :Debitcredit, :ShadowPosting,");
		insertSql.append(" :Account, :AccountType,:AccountBranch, :AccountSubHeadRule, :TranscationCode,");
		insertSql.append(" :RvsTransactionCode, :AmountRule,:FeeCode,:ChargeType, :EntryByInvestment ,:OpenNewFinAc,");
		insertSql.append(
				" :PostToSys, :DerivedTranOrder, :FeeRepeat, :ReceivableOrPayable, :AssignmentEntry, :Bulking,");
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode,");
		insertSql.append(" :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		logger.debug("insertSql: " + insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(transactionEntry);
		this.jdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
		return transactionEntry.getId();
	}

	/**
	 * This method updates the Record RMTTransactionEntry or RMTTransactionEntry_Temp. if Record not updated then throws
	 * DataAccessException with error 41004. update Transaction Entry by key AccountSetid and Version
	 * 
	 * @param Transaction Entry (transactionEntry)
	 * @param type        (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void update(TransactionEntry transactionEntry, String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder updateSql = new StringBuilder("Update RMTTransactionEntry");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set TransDesc = :TransDesc, Debitcredit = :Debitcredit,");
		updateSql.append(" ShadowPosting = :ShadowPosting, Account = :Account, AccountType = :AccountType,");
		updateSql.append(" AccountBranch=:AccountBranch,AccountSubHeadRule = :AccountSubHeadRule,");
		updateSql.append(" TranscationCode = :TranscationCode, RvsTransactionCode = :RvsTransactionCode,");
		updateSql.append(" AmountRule = :AmountRule,FeeCode=:FeeCode, ");
		updateSql.append(" PostToSys =:PostToSys, DerivedTranOrder=:DerivedTranOrder, FeeRepeat=:FeeRepeat, ");
		updateSql.append(
				" ReceivableOrPayable = :ReceivableOrPayable, AssignmentEntry =:AssignmentEntry, Bulking =:Bulking, ");
		updateSql
				.append(" ChargeType =:ChargeType, EntryByInvestment =:EntryByInvestment ,OpenNewFinAc=:OpenNewFinAc,");
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn,");
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode,");
		updateSql.append(
				" TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where AccountSetid =:AccountSetid and TransOrder = :TransOrder");

		if (!type.endsWith("_Temp")) {
			updateSql.append("  AND Version= :Version-1");
		}

		logger.debug("updateSql: " + updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(transactionEntry);
		recordCount = this.jdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}

	/**
	 * Fetch the Record Transaction Entry details by key field
	 * 
	 * @param id   (int)
	 * @param type (String) ""/_Temp/_View
	 * @return TransactionEntry
	 */
	@Override
	public List<TransactionEntry> getListFeeTransEntryById(final long id, String type) {
		logger.debug("Entering");

		TransactionEntry transactionEntry = new TransactionEntry();
		transactionEntry.setId(id);

		StringBuilder selectSql = new StringBuilder("Select AccountSetid, TransOrder, TransDesc,");
		selectSql.append(" Debitcredit, ShadowPosting, Account, AccountType,AccountBranch,");
		selectSql.append(" AccountSubHeadRule, TranscationCode, RvsTransactionCode, AmountRule,ChargeType,");
		selectSql.append(" PostToSys, DerivedTranOrder, FeeRepeat, ReceivableOrPayable, AssignmentEntry, Bulking");
		selectSql.append(", FeeCode,EntryByInvestment,OpenNewFinAc,");
		if (StringUtils.trimToEmpty(type).contains("View")) {
			selectSql.append(" lovDescAccountTypeName,lovDescAccountSubHeadRuleName,");
			selectSql.append(" lovDescTranscationCodeName,lovDescRvsTransactionCodeName ,");
			selectSql.append(" lovDescEventCodeName,lovDescAccSetCodeName,lovDescAccSetCodeDesc,");
			selectSql.append(" lovDescAccountBranchName,lovDescSysInAcTypeName, ");
		}
		selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode,");
		selectSql.append(" TaskId, NextTaskId, RecordType, WorkflowId");
		selectSql.append(" From RMTTransactionEntry");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where AccountSetid =:AccountSetid ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(transactionEntry);
		RowMapper<TransactionEntry> typeRowMapper = BeanPropertyRowMapper.newInstance(TransactionEntry.class);
		logger.debug("Leaving");
		return this.jdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
	}

	/**
	 * Method for Getting List of Transaction entries For LATEPAY event
	 */
	@Override
	public List<TransactionEntry> getODTransactionEntries() {
		logger.debug("Entering");

		StringBuilder selectSql = new StringBuilder("Select  TransOrder, TransDesc,");
		selectSql.append(" Debitcredit, ShadowPosting, Account, AccountType, AmountRule ");
		selectSql.append(" , FeeRepeat, ReceivableOrPayable, AssignmentEntry, Bulking");
		selectSql.append(" From RMTODTransactionEntry");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(new TransactionEntry());
		RowMapper<TransactionEntry> typeRowMapper = BeanPropertyRowMapper.newInstance(TransactionEntry.class);
		logger.debug("Leaving");
		return this.jdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
	}

	/**
	 * Method for Getting List of Transaction entries for Updation depends on OverDueRule
	 */
	@Override
	public List<TransactionEntry> getTransactionEntryList(String oDRuleCode) {
		logger.debug("Entering");

		StringBuilder selectSql = new StringBuilder(" SELECT TE.AccountSetid, TE.TransOrder , ODT.AccountType ");
		selectSql.append(" FROM RMTTransactionEntry TE, RMTODTransactionEntry ODT ");
		selectSql.append(" WHERE AccountSetid = (select AccountSetid from RMTAccountingSet ");
		selectSql.append(" WHERE EventCode='LATEPAY' AND AccountSetCode ='");
		selectSql.append(oDRuleCode);
		selectSql.append("') ");
		selectSql.append(" AND TE.TransOrder = ODT.TransOrder AND ODT.AccountType != ''");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(new TransactionEntry());
		RowMapper<TransactionEntry> typeRowMapper = BeanPropertyRowMapper.newInstance(TransactionEntry.class);
		logger.debug("Leaving");
		return this.jdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
	}

	/**
	 * Method for Update List of Transaction entries for Updation depends on OverDueRule
	 */
	@Override
	public void updateTransactionEntryList(List<TransactionEntry> entries) {
		logger.debug("Entering");

		StringBuilder updateSql = new StringBuilder(" UPDATE RMTTransactionEntry ");
		updateSql.append(" SET AccountType = :AccountType, AccountSubHeadRule = :AccountSubHeadRule ");
		updateSql.append(" WHERE AccountSetid = :AccountSetid AND TransOrder = :TransOrder");

		logger.debug("updateSql: " + updateSql.toString());
		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(entries.toArray());
		this.jdbcTemplate.batchUpdate(updateSql.toString(), beanParameters);

		logger.debug("Leaving");
	}

	@Override
	public List<TransactionEntry> getTransactionEntriesbyFinType(String fintype, String type) {
		logger.debug("Entering");

		MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
		mapSqlParameterSource.addValue("FinType", fintype);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" select Distinct AccountType, AccountSubHeadRule ");
		selectSql.append(" from RMTTransactionEntry");
		selectSql.append(type);
		selectSql.append(
				"  where AccountSetID IN (select AccountSetID from FintypeAccounting where FinType = :FinType)");

		logger.debug("selectSql: " + selectSql.toString());
		RowMapper<TransactionEntry> typeRowMapper = BeanPropertyRowMapper.newInstance(TransactionEntry.class);

		logger.debug("Leaving");

		return this.jdbcTemplate.query(selectSql.toString(), mapSqlParameterSource, typeRowMapper);
	}

	@Override
	public List<Rule> getSubheadRules(List<String> subHeadRules, String type) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		List<Rule> ruleList = new ArrayList<Rule>();

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" select RuleCode, SqlRule, Fields ");
		selectSql.append(" from Rules");
		selectSql.append(type);
		selectSql.append("  where RuleCode IN  (:RuleCode) ");

		source.addValue("RuleCode", subHeadRules);
		logger.debug("selectSql: " + selectSql.toString());

		RowMapper<Rule> typeRowMapper = BeanPropertyRowMapper.newInstance(Rule.class);
		ruleList = this.jdbcTemplate.query(selectSql.toString(), source, typeRowMapper);

		logger.debug("Leaving");

		return ruleList;
	}

	@Override
	public int getTransactionEntryByRuleCode(String ruleCode, String type) {
		logger.debug("Entering");
		TransactionEntry transactionEntry = new TransactionEntry();
		transactionEntry.setAccountSubHeadRule(ruleCode);
		StringBuilder selectSql = new StringBuilder("SELECT COUNT(*)");
		selectSql.append(" From RMTTransactionEntry");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where AccountSubHeadRule =:AccountSubHeadRule");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(transactionEntry);

		logger.debug("Leaving");
		return this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, Integer.class);
	}

}