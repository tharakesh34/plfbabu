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
 * * FileName : CustomerDocumentDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 06-05-2011 * *
 * Modified Date : 06-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 06-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.customermasters.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;

import com.pennant.backend.dao.customermasters.CustomerDocumentDAO;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerDocument;
import com.pennant.backend.model.customermasters.ExternalDocument;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

/**
 * DAO methods implementation for the <b>CustomerDocument model</b> class.<br>
 * 
 */
public class CustomerDocumentDAOImpl extends SequenceDao<CustomerDocument> implements CustomerDocumentDAO {
	private static Logger logger = LogManager.getLogger(CustomerDocumentDAOImpl.class);

	public CustomerDocumentDAOImpl() {
		super();
	}

	@Override
	public CustomerDocument getCustomerDocumentById(final long id, String docCategory, String type) {
		StringBuilder sql = getSqlQuery(type);
		sql.append(" Where CustID = ? and CustDocCategory = ?");

		logger.debug(Literal.SQL + sql.toString());

		CustomerDocumentRM rowMapper = new CustomerDocumentRM(type);

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), rowMapper, id, docCategory);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public List<CustomerDocument> getCustomerDocumentByCustomer(long custId, String type) {
		StringBuilder sql = getSqlQuery(type);
		sql.append(" Where CustID = ?");

		logger.debug(Literal.SQL + sql.toString());

		CustomerDocumentRM rowMapper = new CustomerDocumentRM(type);

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;
			ps.setLong(index, custId);
		}, rowMapper);

	}

	public List<CustomerDocument> getCustomerDocumentByCustomerId(final long custId) {
		return getCustomerDocumentByCustomer(custId, "_AVIEW");
	}

	@Override
	public void delete(CustomerDocument cd, String type) {
		StringBuilder sql = new StringBuilder("Delete From CustomerDocuments");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where Id = ?");

		try {
			int recordCount = this.jdbcOperations.update(sql.toString(), ps -> {
				ps.setLong(1, cd.getID());
			});

			if (recordCount <= 0) {
				throw new ConcurrencyException();
			}
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
	}

	public void deleteByCustomer(long customerId, String type) {
		StringBuilder sql = new StringBuilder("Delete From CustomerDocuments");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where CustID = ?");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> {
			ps.setLong(1, customerId);
		});

	}

	@Override
	public long save(CustomerDocument cd, String type) {
		StringBuilder sql = new StringBuilder("Insert Into CustomerDocuments");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" (ID, CustID, CustDocType, CustDocTitle, CustDocSysName, CustDocRcvdOn, CustDocExpDate");
		sql.append(", CustDocIssuedOn, CustDocIssuedCountry, CustDocIsVerified, CustDocVerifiedBy, CustDocIsAcrive");
		sql.append(", CustDocCategory, CustDocName, DocRefId, DocPurpose, DocUri, PdfPassWord, Remarks");
		sql.append(", Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId");
		sql.append(", RecordType, WorkflowId");
		sql.append(") values(");
		sql.append(" ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL + sql.toString());

		if (cd.getID() == Long.MIN_VALUE) {
			cd.setID(getNextValue("SeqDocumentDetails"));
		}

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, cd.getID());
			ps.setLong(index++, cd.getCustID());
			ps.setString(index++, cd.getCustDocType());
			ps.setString(index++, cd.getCustDocTitle());
			ps.setString(index++, cd.getCustDocSysName());
			ps.setTimestamp(index++, cd.getCustDocRcvdOn());
			ps.setDate(index++, JdbcUtil.getDate(cd.getCustDocExpDate()));
			ps.setDate(index++, JdbcUtil.getDate(cd.getCustDocIssuedOn()));
			ps.setString(index++, cd.getCustDocIssuedCountry());
			ps.setBoolean(index++, cd.isCustDocIsVerified());
			ps.setLong(index++, cd.getCustDocVerifiedBy());
			ps.setBoolean(index++, cd.isCustDocIsAcrive());
			ps.setString(index++, cd.getCustDocCategory());
			ps.setString(index++, cd.getCustDocName());
			ps.setObject(index++, cd.getDocRefId());
			ps.setString(index++, cd.getDocPurpose());
			ps.setString(index++, cd.getDocUri());
			ps.setString(index++, cd.getPdfPassWord());
			ps.setString(index++, cd.getRemarks());
			ps.setInt(index++, cd.getVersion());
			ps.setLong(index++, cd.getLastMntBy());
			ps.setTimestamp(index++, cd.getLastMntOn());
			ps.setString(index++, cd.getRecordStatus());
			ps.setString(index++, cd.getRoleCode());
			ps.setString(index++, cd.getNextRoleCode());
			ps.setString(index++, cd.getTaskId());
			ps.setString(index++, cd.getNextTaskId());
			ps.setString(index++, cd.getRecordType());
			ps.setLong(index, cd.getWorkflowId());
		});

		return cd.getCustID();
	}

	@Override
	public long save(ExternalDocument ed, String type) {
		if (ed.getId() == Long.MIN_VALUE || ed.getId() == 0) {
			ed.setId(getNextValue("SeqDocumentDetails"));
		}

		StringBuilder sql = new StringBuilder("Insert Into ExternalDocuments");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append("( Id, CustId, FinID, FinReference, BankId, DocName, DocType");
		sql.append(", FromDate, ToDate, Passwordprotected, Password, DocRefId, DocUri)");
		sql.append(" Values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? , ?)");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, ed.getId());
			ps.setLong(index++, ed.getCustId());
			ps.setLong(index++, ed.getFinID());
			ps.setString(index++, ed.getFinReference());
			ps.setLong(index++, ed.getBankId());
			ps.setString(index++, ed.getDocName());
			ps.setString(index++, ed.getDocType());
			ps.setDate(index++, JdbcUtil.getDate(ed.getFromDate()));
			ps.setDate(index++, JdbcUtil.getDate(ed.getToDate()));
			ps.setString(index++, ed.getPasswordProtected());
			ps.setString(index++, ed.getPassword());
			ps.setLong(index++, ed.getDocRefId());
			ps.setString(index, ed.getDocUri());
		});

		return ed.getId();
	}

	@Override
	public void update(CustomerDocument cd, String type) {
		StringBuilder sql = new StringBuilder("Update CustomerDocuments");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Set");
		sql.append(" CustDocType = ?, CustDocTitle = ?, CustDocSysName = ?, CustDocRcvdOn = ?");
		sql.append(", CustDocExpDate = ?, CustDocIssuedOn = ?, CustDocName = ?, Remarks = ?");
		sql.append(", CustDocIssuedCountry = ?, CustDocIsVerified = ?, CustDocVerifiedBy = ?");
		sql.append(", CustDocIsAcrive = ?, DocRefId = ?, DocPurpose = ?, DocUri = ?, PdfPassWord = ?");
		sql.append(", Version = ? , LastMntBy = ?, LastMntOn = ?, RecordStatus= ?, RoleCode = ?");
		sql.append(", NextRoleCode = ?, TaskId = ?, NextTaskId = ?, RecordType = ?, WorkflowId = ?");
		sql.append(" Where ID = ?");

		logger.debug(Literal.SQL + sql.toString());

		int recordCount = this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setString(index++, cd.getCustDocType());
			ps.setString(index++, cd.getCustDocTitle());
			ps.setString(index++, cd.getCustDocSysName());
			ps.setTimestamp(index++, cd.getCustDocRcvdOn());
			ps.setDate(index++, JdbcUtil.getDate(cd.getCustDocExpDate()));
			ps.setDate(index++, JdbcUtil.getDate(cd.getCustDocIssuedOn()));
			ps.setString(index++, cd.getCustDocName());
			ps.setString(index++, cd.getRemarks());
			ps.setString(index++, cd.getCustDocIssuedCountry());
			ps.setBoolean(index++, cd.isCustDocIsVerified());
			ps.setLong(index++, cd.getCustDocVerifiedBy());
			ps.setBoolean(index++, cd.isCustDocIsAcrive());
			ps.setObject(index++, JdbcUtil.getLong(cd.getDocRefId()));
			ps.setString(index++, cd.getDocPurpose());
			ps.setString(index++, cd.getDocUri());
			ps.setString(index++, cd.getPdfPassWord());
			ps.setInt(index++, cd.getVersion());
			ps.setLong(index++, cd.getLastMntBy());
			ps.setTimestamp(index++, cd.getLastMntOn());
			ps.setString(index++, cd.getRecordStatus());
			ps.setString(index++, cd.getRoleCode());
			ps.setString(index++, cd.getNextRoleCode());
			ps.setString(index++, cd.getTaskId());
			ps.setString(index++, cd.getNextTaskId());
			ps.setString(index++, cd.getRecordType());
			ps.setLong(index++, cd.getWorkflowId());

			ps.setLong(index, cd.getID());
		});

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
	}

	public DocumentDetails getCustDocByCustAndDocType(final long custId, String docType, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" ID, CustID, CustDocCategory, CustDocType, CustDocName, DocRefId, DocPurpose, DocUri");

		if (type.contains("View")) {
			sql.append(", LovDescCustDocCategory, CustDocTitle, CustDocSysName, CustDocRcvdOn, CustDocExpDate");
			sql.append(", CustDocIssuedOn, CustDocIssuedCountry, LovDescCustDocIssuedCountry, CustDocIsVerified");
			sql.append(", CustDocVerifiedBy, CustDocIsAcrive, DocIsPdfExtRequired, DocIsPasswordProtected");
			sql.append(", PdfMappingRef, PdfPassWord, DocExpDateIsMand, DocIssueDateMand, DocIdNumMand");
		}

		sql.append(", Version, LastMntOn, LastMntBy, RecordStatus, RoleCode, NextRoleCode");
		sql.append(", TaskId, NextTaskId, RecordType, WorkflowId, Remarks");
		sql.append(" From CustomerDocuments");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where CustID = ? and CustDocCategory = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				DocumentDetails dd = new DocumentDetails();

				dd.setDocId(rs.getLong("ID"));
				dd.setCustId(rs.getLong("CustID"));
				dd.setDocCategory(rs.getString("CustDocCategory"));
				dd.setCategoryCode("CUSTOMER");
				dd.setDoctype(rs.getString("CustDocType"));
				dd.setDocName(rs.getString("CustDocName"));
				dd.setDocRefId(JdbcUtil.getLong(rs.getObject("DocRefId")));
				dd.setDocPurpose(rs.getString("DocPurpose"));
				dd.setDocUri(rs.getString("DocUri"));

				if (type.contains("View")) {
					dd.setLovDescDocCategoryName(rs.getString("LovDescCustDocCategory"));
					dd.setCustDocTitle(rs.getString("CustDocTitle"));
					dd.setCustDocSysName(rs.getString("CustDocSysName"));
					dd.setCustDocRcvdOn(rs.getTimestamp("CustDocRcvdOn"));
					dd.setCustDocExpDate(rs.getTimestamp("CustDocExpDate"));
					dd.setCustDocIssuedOn(rs.getTimestamp("CustDocIssuedOn"));
					dd.setCustDocIssuedCountry(rs.getString("CustDocIssuedCountry"));
					dd.setLovDescCustDocIssuedCountry(rs.getString("LovDescCustDocIssuedCountry"));
					dd.setCustDocIsVerified(rs.getBoolean("CustDocIsVerified"));
					dd.setCustDocVerifiedBy(rs.getLong("CustDocVerifiedBy"));
					dd.setCustDocIsAcrive(rs.getBoolean("CustDocIsAcrive"));
					dd.setDocIsPdfExtRequired(rs.getBoolean("DocIsPdfExtRequired"));
					dd.setDocIsPasswordProtected(rs.getBoolean("DocIsPasswordProtected"));
					dd.setPdfMappingRef(JdbcUtil.getLong(rs.getObject("PdfMappingRef")));
					dd.setPdfPassWord(rs.getString("PdfPassWord"));

					// dd.setDocExpDateIsMand(rs.getBoolean("DocExpDateIsMand"));
					// dd.setDocIssueDateMand(rs.getBoolean("DocIssueDateMand"));
					// dd.setDocIdNumMand(rs.getBoolean("DocIdNumMand"));
				}

				dd.setVersion(rs.getInt("Version"));
				dd.setLastMntOn(rs.getTimestamp("LastMntOn"));
				dd.setLastMntBy(rs.getLong("LastMntBy"));
				dd.setRecordStatus(rs.getString("RecordStatus"));
				dd.setRoleCode(rs.getString("RoleCode"));
				dd.setNextRoleCode(rs.getString("NextRoleCode"));
				dd.setTaskId(rs.getString("TaskId"));
				dd.setNextTaskId(rs.getString("NextTaskId"));
				dd.setRecordType(rs.getString("RecordType"));
				dd.setWorkflowId(rs.getLong("WorkflowId"));
				dd.setRemarks(rs.getString("Remarks"));

				return dd;
			}, custId, docType);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	public DocumentDetails getCustDocByCustAndDocType(final long docId, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" Id, CustID, CustDocCategory, CustDocType, CustDocName, DocRefId, DocPurpose, DocUri");

		if (type.contains("View")) {
			sql.append(", LovDescCustDocCategory, CustDocTitle, CustDocSysName");
			sql.append(", CustDocRcvdOn, CustDocExpDate, CustDocIssuedOn, CustDocIssuedCountry");
			sql.append(", LovDescCustDocIssuedCountry, CustDocIsVerified, CustDocVerifiedBy, CustDocIsAcrive");
			sql.append(", DocExpDateIsMand, DocIssueDateMand, DocIdNumMand");
			sql.append(", DocIsPdfExtRequired, DocIsPasswordProtected, PdfMappingRef, PdfPassWord");
		}

		sql.append(", Version, LastMntOn, LastMntBy, RecordStatus, RoleCode, NextRoleCode");
		sql.append(", TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" From CustomerDocuments");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where Id = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				DocumentDetails dd = new DocumentDetails();

				dd.setId(rs.getLong("Id"));
				dd.setCustId(JdbcUtil.getLong(rs.getObject("CustID")));
				dd.setDocCategory(rs.getString("CustDocCategory"));
				dd.setCategoryCode("CUSTOMER");
				dd.setDoctype(rs.getString("CustDocType"));
				dd.setDocName(rs.getString("CustDocName"));
				dd.setDocRefId(JdbcUtil.getLong(rs.getObject("DocRefId")));
				dd.setDocPurpose(rs.getString("DocPurpose"));
				dd.setDocUri(rs.getString("DocUri"));

				if (type.contains("View")) {
					dd.setLovDescDocCategoryName(rs.getString("LovDescCustDocCategory"));
					dd.setCustDocTitle(rs.getString("CustDocTitle"));
					dd.setCustDocSysName(rs.getString("CustDocSysName"));
					dd.setCustDocRcvdOn(rs.getTimestamp("CustDocRcvdOn"));
					dd.setCustDocExpDate(rs.getDate("CustDocExpDate"));
					dd.setCustDocIssuedOn(rs.getDate("CustDocIssuedOn"));
					dd.setCustDocIssuedCountry(rs.getString("CustDocIssuedCountry"));
					dd.setLovDescCustDocIssuedCountry(rs.getString("LovDescCustDocIssuedCountry"));
					dd.setCustDocIsVerified(rs.getBoolean("CustDocIsVerified"));
					dd.setCustDocVerifiedBy(rs.getLong("CustDocVerifiedBy"));
					dd.setCustDocIsAcrive(rs.getBoolean("CustDocIsAcrive"));
					// dd.setDocExpDateIsMand(rs.getString("DocExpDateIsMand"));
					// dd.setDocIssueDateMand(rs.getString("DocIssueDateMand"));
					// dd.setDocIdNumMand(rs.getString("DocIdNumMand"));
					dd.setDocIsPdfExtRequired(rs.getBoolean("DocIsPdfExtRequired"));
					dd.setDocIsPasswordProtected(rs.getBoolean("DocIsPasswordProtected"));
					dd.setPdfMappingRef(JdbcUtil.getLong(rs.getObject("PdfMappingRef")));
					dd.setPdfPassWord(rs.getString("PdfPassWord"));
				}

				dd.setVersion(rs.getInt("Version"));
				dd.setLastMntBy(rs.getLong("LastMntBy"));
				dd.setLastMntOn(rs.getTimestamp("LastMntOn"));
				dd.setRecordStatus(rs.getString("RecordStatus"));
				dd.setRoleCode(rs.getString("RoleCode"));
				dd.setNextRoleCode(rs.getString("NextRoleCode"));
				dd.setTaskId(rs.getString("TaskId"));
				dd.setNextTaskId(rs.getString("NextTaskId"));
				dd.setRecordType(rs.getString("RecordType"));
				dd.setWorkflowId(rs.getLong("WorkflowId"));

				return dd;
			}, docId);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public List<DocumentDetails> getCustDocListByDocTypes(final long custId, List<String> docTypeList, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" ID, CustID, CustDocCategory, CustDocType, DocPurpose, DocUri");
		sql.append(", CustDocName, RecordStatus, RecordType, WorkflowId, Remarks");
		sql.append(" From CustomerDocuments");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where CustID = ? and CustDocCategory in (");

		int i = 0;

		while (i < docTypeList.size()) {
			sql.append(" ?,");
			i++;
		}

		sql.deleteCharAt(sql.length() - 1);
		sql.append(")");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;
			ps.setLong(index++, custId);

			for (String docType : docTypeList) {
				ps.setString(index++, docType);
			}

		}, (rs, rowNum) -> {
			DocumentDetails dd = new DocumentDetails();

			dd.setDocId(rs.getLong("ID"));
			dd.setCustId(JdbcUtil.getLong(rs.getObject("CustID")));
			dd.setDocCategory(rs.getString("CustDocCategory"));
			dd.setCategoryCode("CUSTOMER");
			dd.setDoctype(rs.getString("CustDocType"));
			dd.setDocPurpose(rs.getString("DocPurpose"));
			dd.setDocUri(rs.getString("DocUri"));
			dd.setDocName(rs.getString("CustDocName"));
			dd.setRecordStatus(rs.getString("RecordStatus"));
			dd.setRecordType(rs.getString("RecordType"));
			dd.setWorkflowId(rs.getLong("WorkflowId"));
			dd.setRemarks(rs.getString("Remarks"));

			return dd;
		});
	}

	public List<DocumentDetails> getCustDocByCustId(final long custId, String type) {
		CustomerDocument customerDocument = new CustomerDocument();
		customerDocument.setCustID(custId);

		StringBuilder sql = new StringBuilder("Select");
		sql.append(" CustID, CustDocCategory");
		sql.append(", CustDocType, CustDocName, DocRefId, DocPurpose, DocUri");
		sql.append(", CustDocTitle, CustDocSysName, CustDocIssuedCountry");
		if (type.contains("View")) {
			sql.append(", LovDescCustDocCategory");
			sql.append(", CustDocRcvdOn, CustDocExpDate, CustDocIssuedOn, LovDescCustCIF, LovDescCustShrtName");
			sql.append(", LovDescCustDocIssuedCountry, CustDocIsVerified, CustDocVerifiedBy, CustDocIsAcrive");
			sql.append(", DocExpDateIsMand, DocIssueDateMand, DocIdNumMand");
		}
		sql.append(", Version, LastMntOn, LastMntBy, RecordStatus, RoleCode, NextRoleCode");
		sql.append(", TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" From CustomerDocuments");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where CustID = ?");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> {
			ps.setLong(1, custId);
		}, (rs, rowNum) -> {
			DocumentDetails dd = new DocumentDetails();

			dd.setCustId(JdbcUtil.getLong(rs.getObject("CustID")));
			dd.setDocCategory(rs.getString("CustDocCategory"));
			dd.setCategoryCode("CUSTOMER");
			dd.setDoctype(rs.getString("CustDocType"));
			dd.setDocName(rs.getString("CustDocName"));
			dd.setDocRefId(JdbcUtil.getLong(rs.getObject("DocRefId")));
			dd.setDocPurpose(rs.getString("DocPurpose"));
			dd.setDocUri(rs.getString("DocUri"));
			dd.setCustDocTitle(rs.getString("CustDocTitle"));
			dd.setCustDocSysName(rs.getString("CustDocSysName"));
			dd.setCustDocIssuedCountry(rs.getString("CustDocIssuedCountry"));

			if (type.contains("View")) {
				dd.setLovDescDocCategoryName(rs.getString("LovDescCustDocCategory"));
				dd.setCustDocRcvdOn(rs.getTimestamp("CustDocRcvdOn"));
				dd.setCustDocExpDate(rs.getTimestamp("CustDocExpDate"));
				dd.setCustDocIssuedOn(rs.getTimestamp("CustDocIssuedOn"));
				dd.setLovDescCustCIF(rs.getString("LovDescCustCIF"));
				dd.setLovDescCustShrtName(rs.getString("LovDescCustShrtName"));
				dd.setLovDescCustDocIssuedCountry(rs.getString("LovDescCustDocIssuedCountry"));
				dd.setCustDocIsVerified(rs.getBoolean("CustDocIsVerified"));
				dd.setCustDocVerifiedBy(rs.getLong("CustDocVerifiedBy"));
				dd.setCustDocIsAcrive(rs.getBoolean("CustDocIsAcrive"));

				// dd.setDocExpDateIsMand(rs.getBoolean("DocExpDateIsMand"));
				// dd.setDocIssueDateMand(rs.getBoolean("DocIssueDateMand"));
				// dd.setDocIdNumMand(rs.getBoolean("DocIdNumMand"));
			}

			dd.setVersion(rs.getInt("Version"));
			dd.setLastMntOn(rs.getTimestamp("LastMntOn"));
			dd.setLastMntBy(rs.getLong("LastMntBy"));
			dd.setRecordStatus(rs.getString("RecordStatus"));
			dd.setRoleCode(rs.getString("RoleCode"));
			dd.setNextRoleCode(rs.getString("NextRoleCode"));
			dd.setTaskId(rs.getString("TaskId"));
			dd.setNextTaskId(rs.getString("NextTaskId"));
			dd.setRecordType(rs.getString("RecordType"));
			dd.setWorkflowId(rs.getLong("WorkflowId"));

			return dd;
		});
	}

	@Override
	public boolean isDuplicateTitle(long custId, String custDocCategory, String custDocTitle) {
		String sql = QueryUtil.getCountQuery(new String[] { "CustomerDocuments_Temp", "CustomerDocuments" },
				"CustID != ? and CustDocCategory = ? and CustDocTitle = ?");

		logger.debug(Literal.SQL + sql);

		Object[] args = new Object[] { custId, custDocCategory, custDocTitle, custId, custDocCategory, custDocTitle };

		return jdbcOperations.queryForObject(sql, Integer.class, args) > 0;
	}

	/* FIXME :: move to document types DAO */
	@Override
	public int getDocTypeCount(String docType) {
		String sql = "Select count(DocTypeCode) From BMTDocumentTypes Where DocTypeCode = ?";

		logger.debug(Literal.SQL + sql);
		return this.jdbcOperations.queryForObject(sql, Integer.class, docType);
	}

	@Override
	public int getVersion(long custId, String docType) {
		String sql = "Select Version From CustomerDocuments Where CustID = ? and CustDocCategory = ?";

		logger.debug(Literal.SQL + sql);

		try {
			return this.jdbcOperations.queryForObject(sql, Integer.class, custId, docType);
		} catch (EmptyResultDataAccessException dae) {
			logger.warn(Message.NO_RECORD_FOUND);
			return 0;
		}
	}

	/* FIXME :: move to country DAO */
	@Override
	public int getCustCountryCount(String countryCode) {
		String sql = "Select count(CountryCode) From BmtCountries Where CountryCode = ?";

		logger.debug(Literal.SQL + sql);
		return this.jdbcOperations.queryForObject(sql, Integer.class, countryCode);
	}

	@Override
	public List<String> getDuplicateDocByTitle(String docCategory, String docNumber) {
		StringBuilder sql = new StringBuilder("Select c.CustCIF");
		sql.append(" From Customers c");
		sql.append(" Inner Join CustomerDocuments_View cd on cd.CustID = c.CustID");
		sql.append(" Where cd.CustDocCategory = ? and cd.CustDocTitle = ?");

		logger.debug(Literal.SQL + sql.toString());

		return jdbcOperations.query(sql.toString(), ps -> {
			ps.setString(1, docCategory);
			ps.setString(2, docNumber);
		}, (rs, rowNum) -> {
			return rs.getString(1);
		});

	}

	@Override
	public int updateDocURI(String docURI, long docrefid, TableType tableType) {
		StringBuilder sql = new StringBuilder("Update CustomerDocuments");
		sql.append(tableType.getSuffix());
		sql.append(" Set DocURI = ? Where DocRefId = ?");

		return this.jdbcOperations.update(sql.toString(), ps -> {
			ps.setString(1, docURI);
			ps.setLong(2, docrefid);
		});
	}

	@Override
	public List<ExternalDocument> getExternalDocuments(long bankId, String type) {
		ExternalDocument externalDocument = new ExternalDocument();
		externalDocument.setBankId(bankId);

		StringBuilder sql = new StringBuilder("Select");
		sql.append(" Id, CustId, FinID, FinReference, BankId, DocName, DocType");
		sql.append(", FromDate, ToDate, PasswordProtected, DocRefId, DocUri");
		sql.append(" From ExternalDocuments");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where BankId = ?");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> {
			ps.setLong(1, bankId);
		}, (rs, i) -> {
			ExternalDocument ed = new ExternalDocument();

			ed.setId(rs.getLong("Id"));
			ed.setCustId(rs.getLong("CustId"));
			ed.setFinID(rs.getLong("FinID"));
			ed.setFinReference(rs.getString("FinReference"));
			ed.setBankId(rs.getLong("BankId"));
			ed.setDocName(rs.getString("DocName"));
			ed.setDocType(rs.getString("DocType"));
			ed.setFromDate(rs.getTimestamp("FromDate"));
			ed.setToDate(rs.getTimestamp("ToDate"));
			ed.setPasswordProtected(rs.getString("PasswordProtected"));
			ed.setDocRefId(rs.getLong("DocRefId"));
			ed.setDocUri(rs.getString("DocUri"));

			return ed;
		});
	}

	private StringBuilder getSqlQuery(String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" ID, CustID, CustDocType, CustDocTitle, CustDocSysName, CustDocRcvdOn, CustDocCategory");
		sql.append(", CustDocName, DocRefId, CustDocExpDate, CustDocIssuedOn, CustDocIssuedCountry");
		sql.append(", CustDocIsVerified, CustDocVerifiedBy, CustDocIsAcrive, DocPurpose, DocUri, Remarks");
		if (type.contains("View")) {
			sql.append(", LovDescCustDocCategory, LovDescCustDocIssuedCountry, DocIssueDateMand, DocIdNumMand");
			sql.append(", DocExpDateIsMand, DocIssuedAuthorityMand, DocIsPdfExtRequired");
			sql.append(", DocIsPasswordProtected, PdfMappingRef, PdfPassWord");
		}
		sql.append(", Version, LastMntOn, LastMntBy, RecordStatus, RoleCode, NextRoleCode");
		sql.append(", TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" From CustomerDocuments");
		sql.append(StringUtils.trimToEmpty(type));
		return sql;
	}

	private class CustomerDocumentRM implements RowMapper<CustomerDocument> {
		String type;

		public CustomerDocumentRM(String type) {
			this.type = type;
		}

		@Override
		public CustomerDocument mapRow(ResultSet rs, int rowNum) throws SQLException {
			CustomerDocument cd = new CustomerDocument();

			cd.setID(rs.getLong("ID"));
			cd.setCustID(rs.getLong("CustID"));
			cd.setCustDocType(rs.getString("CustDocType"));
			cd.setCustDocTitle(rs.getString("CustDocTitle"));
			cd.setCustDocSysName(rs.getString("CustDocSysName"));
			cd.setCustDocRcvdOn(rs.getTimestamp("CustDocRcvdOn"));
			cd.setCustDocCategory(rs.getString("CustDocCategory"));
			cd.setCustDocName(rs.getString("CustDocName"));
			cd.setDocRefId(JdbcUtil.getLong(rs.getObject("DocRefId")));
			cd.setCustDocExpDate(rs.getTimestamp("CustDocExpDate"));
			cd.setCustDocIssuedOn(rs.getTimestamp("CustDocIssuedOn"));
			cd.setCustDocIssuedCountry(rs.getString("CustDocIssuedCountry"));
			cd.setCustDocIsVerified(rs.getBoolean("CustDocIsVerified"));
			cd.setCustDocVerifiedBy(rs.getLong("CustDocVerifiedBy"));
			cd.setCustDocIsAcrive(rs.getBoolean("CustDocIsAcrive"));
			cd.setDocPurpose(rs.getString("DocPurpose"));
			cd.setDocUri(rs.getString("DocUri"));
			cd.setRemarks(rs.getString("Remarks"));

			if (type.contains("View")) {
				cd.setLovDescCustDocCategory(rs.getString("LovDescCustDocCategory"));
				cd.setLovDescCustDocIssuedCountry(rs.getString("LovDescCustDocIssuedCountry"));
				cd.setDocIssueDateMand(rs.getBoolean("DocIssueDateMand"));
				cd.setDocIdNumMand(rs.getBoolean("DocIdNumMand"));
				cd.setLovDescdocExpDateIsMand(rs.getBoolean("DocExpDateIsMand"));
				cd.setDocIssuedAuthorityMand(rs.getBoolean("DocIssuedAuthorityMand"));
				cd.setDocIsPdfExtRequired(rs.getBoolean("DocIsPdfExtRequired"));
				cd.setDocIsPasswordProtected(rs.getBoolean("DocIsPasswordProtected"));
				cd.setPdfMappingRef(JdbcUtil.getLong(rs.getObject("PdfMappingRef")));
				cd.setPdfPassWord(rs.getString("PdfPassWord"));
			}

			cd.setVersion(rs.getInt("Version"));
			cd.setLastMntOn(rs.getTimestamp("LastMntOn"));
			cd.setLastMntBy(rs.getLong("LastMntBy"));
			cd.setRecordStatus(rs.getString("RecordStatus"));
			cd.setRoleCode(rs.getString("RoleCode"));
			cd.setNextRoleCode(rs.getString("NextRoleCode"));
			cd.setTaskId(rs.getString("TaskId"));
			cd.setNextTaskId(rs.getString("NextTaskId"));
			cd.setRecordType(rs.getString("RecordType"));
			cd.setWorkflowId(rs.getLong("WorkflowId"));

			return cd;
		}

	}

	@Override
	public boolean getCustomerDocExists(long custId, String docType) {
		String sql = "Select count(CustID) From CustomerDocuments Where CustID = ? and CustDocCategory = ?";

		logger.debug(Literal.SQL + sql);

		return jdbcOperations.queryForObject(sql, Integer.class, custId, docType) > 0;
	}

	@Override
	public List<Customer> getCustIdByDocTitle(String custDocTitle) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" C.CustID, C.CustCIF, C.CustShrtName");
		sql.append(" From Customers C");
		sql.append(" inner join CustomerDocuments_View CD on CD.CustID = C.CustID");
		sql.append(" Where CustDocTitle = ? and CustDocCategory = ?");

		return this.jdbcOperations.query(sql.toString(), ps -> {
			ps.setString(1, custDocTitle);
			ps.setString(2, "03");
		}, (rs, rowNum) -> {
			Customer customer = new Customer();

			customer.setCustID(rs.getLong("CustID"));
			customer.setCustCIF(rs.getString("CustCIF"));
			customer.setCustShrtName(rs.getString("CustShrtName"));

			return customer;
		});
	}
}