package com.pennant.backend.dao.audit.impl;

import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.util.ArrayList;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.impl.BasisNextidDaoImpl;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

public class AuditHeaderCustomerDAOImpl extends BasisNextidDaoImpl<AuditHeader> implements AuditHeaderDAO{
	private static Logger logger = Logger.getLogger(AuditHeaderDAOImpl.class);
	
	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	private DataSourceTransactionManager auditTransactionManager;


	@Override
	public AuditHeader getNewAuditHeader() {
		return new AuditHeader();
	}
	

	@Override
	public long addAudit(AuditHeader auditHeader) {
		logger.debug("addAudit");
		long id = Long.MIN_VALUE;
		auditHeader.setAuditDate(new Timestamp(System.currentTimeMillis()));
		TransactionDefinition td = new DefaultTransactionDefinition();
		TransactionStatus tranStatus = auditTransactionManager.getTransaction(td);
		try{
			id = addAuditHeader(auditHeader);
			auditHeader.setId(id);
			addAuditDetails(auditHeader);
			auditTransactionManager.commit(tranStatus);
		} catch (DataAccessException e) {
			e.printStackTrace();
			auditTransactionManager.rollback(tranStatus);
			throw e;
		}
		
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
		logger.debug("addAuditDetails");
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(modelData);
		this.namedParameterJdbcTemplate.update(insertString, beanParameters);
		
	}
	
	public void addAuditDetails(AuditHeader auditHeader){
		
		CustomerDetails customerDetails = (CustomerDetails) auditHeader.getModelData();
		CustomerDetails befCustomerDetails = customerDetails.getBefImage();
		
		addAuditDetail(customerDetails.getCustomer(), befCustomerDetails.getCustomer(), auditHeader.getAuditTranType(), auditHeader);
	}
		
		
	public void addAuditDetail(Object currentImage,Object befImage,String tranType,AuditHeader auditHeader){
		
		boolean after=false;
		boolean before=false;
		boolean workFlow=false;
		
		if  (currentImage!=null) {
			StringBuffer fields= new StringBuffer();
			StringBuffer values=new StringBuffer();
			
			ArrayList<String> arrayFields = getFieldList(currentImage);
			for (int j = 0; j < arrayFields.size(); j++) {
				
				fields.append(arrayFields.get(j));
				values.append(":"+arrayFields.get(j));
				
				if (j<arrayFields.size()-1){
					fields.append(" , ");
					values.append(" , ");
				}
			}
			
			if (StringUtils.trimToEmpty(tranType).equalsIgnoreCase("A")){
				after = true;
			}else if (StringUtils.trimToEmpty(tranType).equalsIgnoreCase("M")){
				after =true;
				before=true;
			}else if (StringUtils.trimToEmpty(tranType).equalsIgnoreCase("D")){
				before=true;
			}else if (StringUtils.trimToEmpty(tranType).equalsIgnoreCase("W")){
				workFlow =true;
			}
			
			if (before && befImage!=null){
				addAuditDetails(befImage, getInsertQry(currentImage,"B",auditHeader,fields.toString(),values.toString()));
			}
			
			if (after){
				addAuditDetails(currentImage, getInsertQry(currentImage,"A",auditHeader,fields.toString(),values.toString()));
			}
			
			if (workFlow){
				addAuditDetails(currentImage, getInsertQry(currentImage,"W",auditHeader,fields.toString(),values.toString()));
			}
		}
		}
	

	private String getInsertQry(Object dataObject,String imageType,AuditHeader auditHeader,String fields,String values){
		
		StringBuffer sqlString = new StringBuffer();
		sqlString.append("insert into Adt" +PennantJavaUtil.getTabelMap(dataObject.getClass().getSimpleName())+" (");
		sqlString.append(" AuditId, AuditDate, AuditImage ,"+fields+")");
		sqlString.append (" VALUES (");
		sqlString.append(String.valueOf(auditHeader.getId())+",");
		sqlString.append("'"+PennantJavaUtil.dbTimeStamp(auditHeader.getAuditDate(), PennantConstants.DBDateTimeFormat)+"','"+imageType+"',");
		sqlString.append(values+")");
		return sqlString.toString();
	}
	
	
	private ArrayList<String> getFieldList(Object detailObject){
		
		String excludeFields="serialVersionUID,newRecord,lovValue,befImage,userDetails,userAction,";
		
		Field[] fields =   detailObject.getClass().getDeclaredFields();
		ArrayList<String> arrayFields = new ArrayList<String>(); 
		
		for (int i = 0; i < fields.length; i++) {
			if (!excludeFields.contains(fields[i].getName()+",") && !fields[i].getName().startsWith("lovDesc")){
				arrayFields.add(fields[i].getName());
			}
		}
		
		return arrayFields;
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
