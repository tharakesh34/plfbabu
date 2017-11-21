/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. 
 * All components/modules/functions/classes/logic in this software, unless 
 * otherwise stated, the property of Pennant Technologies. 
 * 
 * Copyright and other intellectual property laws protect these materials. 
 * Reproduction or retransmission of the materials, in whole or in part, in any manner, 
 * without the prior written consent of the copyright holder, is a violation of 
 * copyright law.
 */

/**
 ********************************************************************************************
 *                                 FILE HEADER                                              *
 ********************************************************************************************
 *
 * FileName    		:  AuditHeaderDAOImpl.java												*                           
 *                                                                    
 * Author      		:  PENNANT TECHONOLOGIES												*
 *                                                                  
 * Creation Date    :  26-04-2011															*
 *                                                                  
 * Modified Date    :  26-04-2011															*
 *                                                                  
 * Description 		:												 						*                                 
 *                                                                                          
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 26-04-2011       Pennant	                 0.1                                            * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
*/
package com.pennant.backend.dao.audit.impl;

import java.sql.Timestamp;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import com.pennant.app.util.DateUtility;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.impl.BasisNextidDaoImpl;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.extendedfields.ExtendedFieldRender;
import com.pennant.backend.util.PennantConstants;
import com.pennanttech.pennapps.core.feature.ModuleUtil;
import com.pennanttech.pff.core.App;
import com.pennanttech.pff.core.App.Database;

public class AuditHeaderDAOImpl extends BasisNextidDaoImpl<AuditHeader> implements AuditHeaderDAO{
	private static Logger logger = Logger.getLogger(AuditHeaderDAOImpl.class);

	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	private DataSourceTransactionManager auditTransactionManager;
	
	public AuditHeaderDAOImpl() {
		super();
	}
	
	public AuditHeader getNewAuditHeader(){
		return new AuditHeader();
	}
	
	public long addAudit(AuditHeader auditHeader) {
		logger.debug("Entering");

		long id = Long.MIN_VALUE;
		auditHeader.setAuditDate(new Timestamp(System.currentTimeMillis()));

		try {
			id = addAuditHeader(auditHeader);
			auditHeader.setId(id);

			if (auditHeader.getAuditDetail() != null) {
				createAuditDetails(auditHeader, auditHeader.getAuditDetail());
			}

			if (auditHeader.getAuditDetails() != null && auditHeader.getAuditDetails().size() > 0) {
				createAuditDetails(auditHeader);
			}
		} catch (DataAccessException e) {
			logger.error("Exception: ", e);
			throw e;
		}

		logger.debug("Leaving");
		return id;
	}
	
	private long addAuditHeader(AuditHeader auditHeader){
		logger.debug("addAuditHeader");
		auditHeader.setId(getNextidviewDAO().getNextId("SeqAuditHeader"));
		StringBuilder insertSql = new StringBuilder();
		
		insertSql.append("insert into AuditHeader (AuditId,AuditDate,AuditUsrId,AuditModule,AuditBranchCode,AuditDeptCode,AuditTranType,");
		insertSql.append("AuditCustNo,AuditAccNo,AuditLoanNo,AuditReference,AuditSystemIP,AuditSessionID,");
		insertSql.append("AuditInfo,Overide,AuditOveride," );
		insertSql.append("AuditPrinted,AuditRecovered,AuditErrorForRecocvery) ");
		insertSql.append("values(:AuditId,:AuditDate,:AuditUsrId,:AuditModule,:AuditBranchCode,:AuditDeptCode,:AuditTranType,"); 
		insertSql.append(":AuditCustNo,:AuditAccNo,:AuditLoanNo,:AuditReference,:AuditSystemIP,:AuditSessionID," );
		insertSql.append(":AuditInfo, :Overide,:AuditOveride," );
		insertSql.append(":AuditPrinted,:AuditRecovered,:AuditErrorForRecocvery)");
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(auditHeader);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		return auditHeader.getId();
	}
	
	private void addAuditDetails(Object modelData,String insertString, boolean isExtendedModule){
		logger.debug("Entering");
		
		logger.debug("SQL Qry"+insertString);
		if(isExtendedModule){
			ExtendedFieldRender fieldRender = (ExtendedFieldRender) modelData;
			this.namedParameterJdbcTemplate.update(insertString, fieldRender.getAuditMapValues());
		}else{
			SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(modelData);
			this.namedParameterJdbcTemplate.update(insertString, beanParameters);
		}
		logger.debug("Leaving");
	}
	
	public void createAuditDetails(AuditHeader auditHeader){
		List<AuditDetail> auditDetails = auditHeader.getAuditDetails();
		
		if(auditDetails!=null && auditDetails.size()>0){
			for (int i = 0; i < auditDetails.size(); i++) {
				createAuditDetails(auditHeader,auditDetails.get(i));
			}
		}
	}

	private void createAuditDetails(AuditHeader auditHeader,AuditDetail auditDetail){
		logger.debug("Entering");
		boolean after=false;
		boolean before=false;
		boolean workFlow=false;
		
		if(auditDetail.getModelData()!=null){
			
			if (StringUtils.trimToEmpty(auditDetail.getAuditTranType()).equalsIgnoreCase(PennantConstants.TRAN_ADD)){
				after = true;
			}else if (StringUtils.trimToEmpty(auditDetail.getAuditTranType()).equalsIgnoreCase(PennantConstants.TRAN_UPD)){
				after =true;
				before=true;
			}else if (StringUtils.trimToEmpty(auditDetail.getAuditTranType()).equalsIgnoreCase(PennantConstants.TRAN_DEL)){
				before=true;
			}else if (StringUtils.trimToEmpty(auditDetail.getAuditTranType()).equalsIgnoreCase(PennantConstants.TRAN_WF)){
				workFlow =true;
			}

			if (before && auditDetail.getBefImage()!=null){
				addAuditDetails(auditDetail.getBefImage(), getInsertQry(auditDetail.getBefImage(),PennantConstants.TRAN_BEF_IMG,auditHeader,
						auditDetail), auditDetail.isExtended());
			}
			
			if (after){
				addAuditDetails(auditDetail.getModelData(), getInsertQry(auditDetail.getModelData(),PennantConstants.TRAN_AFT_IMG,auditHeader,
						auditDetail), auditDetail.isExtended());
			}
			
			if (workFlow){
				addAuditDetails(auditDetail.getModelData(), getInsertQry(auditDetail.getModelData(),PennantConstants.TRAN_WF_IMG,auditHeader,
						auditDetail), auditDetail.isExtended());
			}
		}
		logger.debug("Leaving");
	}

	private String getInsertQry(Object dataObject, String imageType, AuditHeader auditHeader,AuditDetail auditDetail) {
		StringBuilder sql = new StringBuilder();
		sql.append("insert into Adt");
		if(auditDetail.isExtended()){
			sql.append(((ExtendedFieldRender)dataObject).getTableName());
		}else{
			sql.append(ModuleUtil.getTableName(dataObject.getClass().getSimpleName()));
		}
		sql.append(" (");
		sql.append(" AuditId, AuditDate, AuditSeq, AuditImage, ");
		sql.append(auditDetail.getAuditField());
		sql.append(" ) VALUES (");
		sql.append(String.valueOf(auditHeader.getId()));
		sql.append(", '");
		if(App.DATABASE == Database.SQL_SERVER){
			sql.append(auditHeader.getAuditDate());
		}else{
			sql.append(DateUtility.format(auditHeader.getAuditDate(), PennantConstants.DBDateTimeFormat));
		}
		sql.append("',");
		sql.append(auditDetail.getAuditSeq());
		sql.append(",'");
		sql.append(imageType);
		sql.append("',");
		sql.append(auditDetail.getAuditValue() );
		sql.append(")");
		return sql.toString();
	}

	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}


	public DataSourceTransactionManager getAuditTransactionManager() {
		return auditTransactionManager;
	}

	public void setAuditTransactionManager(
			DataSourceTransactionManager auditTransactionManager) {
		this.auditTransactionManager = auditTransactionManager;
	}	
}
