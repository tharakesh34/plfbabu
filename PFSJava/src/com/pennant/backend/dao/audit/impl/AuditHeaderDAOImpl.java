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

import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.impl.BasisNextidDaoImpl;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;


public class AuditHeaderDAOImpl extends BasisNextidDaoImpl<AuditHeader> implements AuditHeaderDAO{
	
	private static Logger logger = Logger.getLogger(AuditHeaderDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	private DataSourceTransactionManager auditTransactionManager;
	
	public AuditHeader getNewAuditHeader(){
		return new AuditHeader();
	}

	
	/*public long addAudit(AuditHeader auditHeader) {
		logger.debug("addAudit");
		long id = Long.MIN_VALUE;
		auditHeader.setAuditDate(new Timestamp(System.currentTimeMillis()));
		TransactionDefinition td = new DefaultTransactionDefinition();
		TransactionStatus tranStatus = auditTransactionManager.getTransaction(td);
		try{
			id = addAuditHeader(auditHeader);
			auditHeader.setId(id);
			if(auditHeader.getAuditDetails()!=null && auditHeader.getAuditDetails().size()>0){
				createAuditDetails(auditHeader);
			}else{
				addAuditDetails(auditHeader);	
			}
			
			if(auditHeader.getAuditDetails()!=null && auditHeader.getAuditDetails().size()>0){
				createAuditDetails(auditHeader);
			}
			
			auditTransactionManager.commit(tranStatus);
		} catch (DataAccessException e) {
			e.printStackTrace();
			auditTransactionManager.rollback(tranStatus);
			throw e;
		}
		
		return id;
	}
*/
	public long addAudit(AuditHeader auditHeader) {
		logger.debug("Entering");
		long id = Long.MIN_VALUE;
		auditHeader.setAuditDate(new Timestamp(System.currentTimeMillis()));
		//TransactionDefinition td = new DefaultTransactionDefinition();
		//TransactionStatus tranStatus = auditTransactionManager.getTransaction(td);
		try{
			id = addAuditHeader(auditHeader);
			auditHeader.setId(id);
			
			if(auditHeader!=null){
				
				if(auditHeader.getAuditDetail()!=null){
					createAuditDetails(auditHeader,auditHeader.getAuditDetail());	
				}
				
				if(auditHeader.getAuditDetails()!=null && auditHeader.getAuditDetails().size()>0){
					createAuditDetails(auditHeader);
				}
			}
			
			
			//auditTransactionManager.commit(tranStatus);
		} catch (DataAccessException e) {
			logger.error(e);
			//e.printStackTrace();
			//auditTransactionManager.rollback(tranStatus);
			throw e;
		}
		logger.debug("Leaving");
		return id;
	}

	
	private long addAuditHeader(AuditHeader auditHeader){
		logger.debug("addAuditHeader");
		auditHeader.setId(getNextidviewDAO().getNextId("Seq"+PennantJavaUtil.getTabelMap("AuditHeader")));
		String insertSql = 	"insert into AuditHeader (AuditId,AuditDate,AuditUsrId,AuditModule,AuditBranchCode,AuditDeptCode,AuditTranType," +
				"AuditCustNo,AuditAccNo,AuditLoanNo,AuditReference,AuditSystemIP,AuditSessionID," +
				"AuditInfo,Overide,AuditOveride," +
				"AuditPrinted,AuditRecovered,AuditErrorForRecocvery) " +
				"values(:AuditId,:AuditDate,:AuditUsrId,:AuditModule,:AuditBranchCode,:AuditDeptCode,:AuditTranType," +
				":AuditCustNo,:AuditAccNo,:AuditLoanNo,:AuditReference,:AuditSystemIP,:AuditSessionID," +
				":AuditInfo, :Overide,:AuditOveride," +
				":AuditPrinted,:AuditRecovered,:AuditErrorForRecocvery)";
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(auditHeader);
		this.namedParameterJdbcTemplate.update(insertSql, beanParameters);
		return auditHeader.getId();
	}
	
	
	private void addAuditDetails(Object modelData,String insertString){
		logger.debug("Entering");
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(modelData);
		logger.debug("SQL Qry"+insertString);
		this.namedParameterJdbcTemplate.update(insertString, beanParameters);
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
				addAuditDetails(auditDetail.getBefImage(), getInsertQry(auditDetail.getBefImage(),PennantConstants.TRAN_BEF_IMG,auditHeader,auditDetail.getAuditField(),auditDetail.getAuditValue(),auditDetail.getAuditSeq()));
			}
			
			if (after){
				addAuditDetails(auditDetail.getModelData(), getInsertQry(auditDetail.getModelData(),PennantConstants.TRAN_AFT_IMG,auditHeader,auditDetail.getAuditField(),auditDetail.getAuditValue(),auditDetail.getAuditSeq()));
			}
			
			if (workFlow){
				addAuditDetails(auditDetail.getModelData(), getInsertQry(auditDetail.getModelData(),PennantConstants.TRAN_WF_IMG,auditHeader,auditDetail.getAuditField(),auditDetail.getAuditValue(),auditDetail.getAuditSeq()));
			}
		}
		logger.debug("Leaving");
	}

	/*public void addAuditDetails(AuditHeader auditHeader){
		
		boolean after=false;
		boolean before=false;
		boolean workFlow=false;
		
		Object object = auditHeader.getModelData();
		
		if  (object!=null) {
			StringBuffer fields= new StringBuffer();
			StringBuffer values=new StringBuffer();
			
			ArrayList<String> arrayFields = getFieldList(object);
			for (int j = 0; j < arrayFields.size(); j++) {
				
				fields.append(arrayFields.get(j));
				values.append(":"+arrayFields.get(j));
				
				if (j<arrayFields.size()-1){
					fields.append(" , ");
					values.append(" , ");
				}
			}
			
			Object beforeObject=null; 
			
			
			
			try {
				beforeObject =  object.getClass().getMethod("getBefImage",null).invoke(object,null);
			} catch (Exception e) {
			}
			
			if (StringUtils.trimToEmpty(auditHeader.getAuditTranType()).equalsIgnoreCase(PennantConstants.TRAN_ADD)){
				after = true;
			}else if (StringUtils.trimToEmpty(auditHeader.getAuditTranType()).equalsIgnoreCase(PennantConstants.TRAN_UPD)){
				after =true;
				before=true;
			}else if (StringUtils.trimToEmpty(auditHeader.getAuditTranType()).equalsIgnoreCase(PennantConstants.TRAN_DEL)){
				before=true;
			}else if (StringUtils.trimToEmpty(auditHeader.getAuditTranType()).equalsIgnoreCase(PennantConstants.TRAN_WF)){
				workFlow =true;
			}
			
			if (before && beforeObject!=null){
				addAuditDetails(beforeObject, getInsertQry(object,PennantConstants.TRAN_BEF_IMG,auditHeader,fields.toString(),values.toString(),1));
			}
			
			if (after){
				addAuditDetails(object, getInsertQry(object,PennantConstants.TRAN_AFT_IMG,auditHeader,fields.toString(),values.toString(),1));
			}
			
			if (workFlow){
				addAuditDetails(object, getInsertQry(object,PennantConstants.TRAN_WF_IMG,auditHeader,fields.toString(),values.toString(),1));
			}
		}
	}
*/	

	private String getInsertQry(Object dataObject,String ImageType,AuditHeader auditHeader,String fields,String values,int SeqNo){
		
		StringBuffer sqlString = new StringBuffer();
		sqlString.append("insert into Adt" +PennantJavaUtil.getTabelMap(dataObject.getClass().getSimpleName())+" (");
		sqlString.append(" AuditId, AuditDate,AuditSeq,AuditImage ,"+fields+")");
		sqlString.append (" VALUES (");
		sqlString.append(String.valueOf(auditHeader.getId())+",");
		sqlString.append("'"+PennantJavaUtil.dbTimeStamp(auditHeader.getAuditDate(), PennantConstants.DBDateTimeFormat)+"',");
		sqlString.append(SeqNo+",'"+ImageType+"',");
		sqlString.append(values+")");
		return sqlString.toString();
	}
	
	
	/*private ArrayList<String> getFieldList(Object detailObject){
		
		String excludeFields="serialVersionUID,newRecord,lovValue,befImage,userDetails,userAction,loginAppCode,loginUsrId,loginGrpCode,loginRoleCd,";
		
		Field[] fields =   detailObject.getClass().getDeclaredFields();
		ArrayList<String> arrayFields = new ArrayList<String>(); 
		
		for (int i = 0; i <fields.length; i++) {
			if (!excludeFields.contains(fields[i].getName()+",") && !fields[i].getName().startsWith("lovDesc")){
				arrayFields.add(fields[i].getName());
			}
		}
		
		return arrayFields;
	}
*/

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
