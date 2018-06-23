package com.pennant.backend.dao.dms.impl;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.dms.DMSIdentificationDAO;
import com.pennanttech.model.dms.DMSDocumentDetails;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;

public class DMSIdentificationDAOImpl extends SequenceDao<DMSDocumentDetails> implements DMSIdentificationDAO {
	private static Logger logger = Logger.getLogger(DMSIdentificationDAOImpl.class);

	@Override
	public void saveDMSDocumentReferences(List<DMSDocumentDetails> dmsDocumentDetailList) {
		logger.debug("Entering");
		if (CollectionUtils.isNotEmpty(dmsDocumentDetailList)) {
			try{
				String sql = "insert into dmsdocprocess "
						+ "(id,finreference,docmodule,docrefid,state,status,lastMntOn,createdOn,referenceid,customercif,docid,docCategory,docDesc,docExt) "
						+ "values (:id,:finReference,:docModule,:docRefId,:state,:status,:lastMntOn,:createdOn,:referenceId,:customerCif,:docId,:docCategory,:docDesc,:docExt)";
				
				dmsDocumentDetailList.stream().forEach(details-> details.setId(getNextValue("seqdmsidentification")));

				SqlParameterSource[] params = SqlParameterSourceUtils.createBatch(dmsDocumentDetailList.toArray());
				jdbcTemplate.batchUpdate(sql, params);
			}catch(Exception e){
				logger.error("Exception", e);
			}			
		}
		logger.debug("Leaving");
	}

	@Override
	public List<DMSDocumentDetails> retrieveDMSDocumentReference(){
		logger.debug("Entering");
		try {
			String sql = "select * from dmsdocprocess";
			RowMapper<DMSDocumentDetails> typeRowMapper = ParameterizedBeanPropertyRowMapper
					.newInstance(DMSDocumentDetails.class);
			logger.debug(Literal.LEAVING);
			return this.jdbcTemplate.query(sql, typeRowMapper);

		} catch (Exception e) {
			logger.error("Exception", e);
		}
		logger.debug("Leaving");
		return null;
	}
	
	@Override
	public void processSuccessResponse(DMSDocumentDetails dmsDocumentDetails,
			DMSDocumentDetails responseDmsDocumentDetails) {
		logger.debug("Entering");
		try{
			if(StringUtils.equals(responseDmsDocumentDetails.getDocModule(), "CUSTOMER")){
				String customerSql= "update customerdocuments set docrefid = :docRefId, docuri= :docUri"
						+" where custid=:docId and custdoccategory=:docCategory";
				SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(responseDmsDocumentDetails);
				jdbcTemplate.update(customerSql, beanParameters);
			}else{
				String customerSql= "update documentdetails set docrefid = :docRefId, docuri= :docUri"
						+" where docId=:docId and docCategory=:docCategory";
				SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(responseDmsDocumentDetails);
				jdbcTemplate.update(customerSql, beanParameters);
			}
			
			String documentManagerSql = "delete from documentmanager where id = :docRefId";
			SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(dmsDocumentDetails);
			jdbcTemplate.update(documentManagerSql, beanParameters);
			
			String dmsErrorDelete = "delete from dmsdocprocesslog where id=:id";
			beanParameters = new BeanPropertySqlParameterSource(dmsDocumentDetails);
			jdbcTemplate.update(dmsErrorDelete, beanParameters);
			
			dmsDocumentDetails.setStatus("Success");
			
			String sql = "insert into dmsdocprocesslog "
					+ "(id,finreference,docmodule,docrefid,state,status,lastMntOn,createdOn,referenceid,customercif,docid,docCategory,retrycount) "
					+ "values (:id,:finReference,:docModule,:docRefId,:state,:status,:lastMntOn,:createdOn,:referenceId,:customerCif,:docId,:docCategory,:retryCount)";
			beanParameters = new BeanPropertySqlParameterSource(dmsDocumentDetails);
			jdbcTemplate.update(sql, beanParameters);
			
			String dmsDelete = "delete from dmsdocprocess where id = :id";
			beanParameters = new BeanPropertySqlParameterSource(dmsDocumentDetails);
			jdbcTemplate.update(dmsDelete, beanParameters);
		}catch(Exception e){
			logger.error("Exception", e);
		}
		logger.debug("Leaving");
	}

	@Override
	public void processFailure(DMSDocumentDetails dmsDocumentDetails, int configRetryCount) {
		logger.debug("Entering");
		try{
			String sql = "insert into dmsdocprocesslog "
					+ "(id,finreference,docmodule,docrefid,state,status,lastMntOn,createdOn,referenceid,customercif,docid,docCategory,retrycount,errordesc) "
					+ "values (:id,:finReference,:docModule,:docRefId,:state,:status,:lastMntOn,:createdOn,:referenceId,:customerCif,:docId,:docCategory,:retryCount,:errorDesc)";
			BeanPropertySqlParameterSource beanParameters = new BeanPropertySqlParameterSource(dmsDocumentDetails);
			jdbcTemplate.update(sql, beanParameters);
			
			if(dmsDocumentDetails.getRetryCount()>=configRetryCount){
				String dmsDelete = "delete from dmsdocprocess where id = :id";
				beanParameters = new BeanPropertySqlParameterSource(dmsDocumentDetails);
				jdbcTemplate.update(dmsDelete, beanParameters);
				
				String customerSql= "update dmsdocprocesslog set status = 'NonProcessing' where id=:id";
				beanParameters = new BeanPropertySqlParameterSource(dmsDocumentDetails);
				jdbcTemplate.update(customerSql, beanParameters);
				logger.debug("DMSDocument "+dmsDocumentDetails.getId()+" : "+dmsDocumentDetails.getReferenceId()+" have reached maximum retry count "+dmsDocumentDetails.getRetryCount()+" for loan "+dmsDocumentDetails.getFinReference());
			}
		}catch(Exception e){
			logger.error("Exception", e);
		}
		logger.debug("Leaving");
	}
}
