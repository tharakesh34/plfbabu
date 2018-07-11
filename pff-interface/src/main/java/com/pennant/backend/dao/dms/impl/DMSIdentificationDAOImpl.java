package com.pennant.backend.dao.dms.impl;

import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.pennant.backend.dao.dms.DMSIdentificationDAO;
import com.pennant.backend.util.DmsDocumentConstants;
import com.pennanttech.model.dms.DMSDocumentDetails;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;

public class DMSIdentificationDAOImpl extends SequenceDao<DMSDocumentDetails> implements DMSIdentificationDAO {
	private static Logger logger = Logger.getLogger(DMSIdentificationDAOImpl.class);
	private DataSourceTransactionManager transManager;
	private DefaultTransactionDefinition transDef;

	@Override
	public void saveDMSDocumentReferences(List<DMSDocumentDetails> dmsDocumentDetailList) {
		logger.debug("Entering");
		if (CollectionUtils.isNotEmpty(dmsDocumentDetailList)) {
			try {
				String sql = "insert into dmsdocprocess "
						+ "(id,finreference,docmodule,docrefid,state,status,lastMntOn,createdOn,referenceid,customercif,docid,docCategory,docDesc,docExt) "
						+ "values (:id,:finReference,:docModule,:docRefId,:state,:status,:lastMntOn,:createdOn,:referenceId,:customerCif,:docId,:docCategory,:docDesc,:docExt)";

				dmsDocumentDetailList.stream().forEach(details -> details.setId(getNextValue("seqdmsidentification")));

				SqlParameterSource[] params = SqlParameterSourceUtils.createBatch(dmsDocumentDetailList.toArray());
				jdbcTemplate.batchUpdate(sql, params);
			} catch (Exception e) {
				logger.error("Exception", e);
			}
		}
		logger.debug("Leaving");
	}

	@Override
	public List<DMSDocumentDetails> retrieveDMSDocumentReference() {
		logger.debug("Entering");
		List<DMSDocumentDetails> dmsDocumentDetails = null;
		try {
			String sql = "select * from dmsdocprocess";
			RowMapper<DMSDocumentDetails> typeRowMapper = ParameterizedBeanPropertyRowMapper
					.newInstance(DMSDocumentDetails.class);
			logger.debug(Literal.LEAVING);
			dmsDocumentDetails = this.jdbcTemplate.query(sql, typeRowMapper);
		} catch (Exception e) {
			logger.error("Exception", e);
		}
		logger.debug("Leaving");
		return dmsDocumentDetails;
	}

	@Override
	public void processSuccessResponse(DMSDocumentDetails dmsDocumentDetails,
			DMSDocumentDetails responseDmsDocumentDetails) {
		logger.debug("Entering");
		TransactionStatus txnStatus = null;
		try {
			txnStatus = transManager.getTransaction(transDef);
			if (StringUtils.equals(responseDmsDocumentDetails.getDocModule(), "CUSTOMER")) {
				String customerSql = "update customerdocuments set docrefid = :docRefId, docuri= :docUri"
						+ " where custid=:docId and custdoccategory=:docCategory";
				SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(responseDmsDocumentDetails);
				jdbcTemplate.update(customerSql, beanParameters);
			} else {
				String customerSql = "update documentdetails set docrefid = :docRefId, docuri= :docUri"
						+ " where docId=:docId and docCategory=:docCategory";
				SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(responseDmsDocumentDetails);
				jdbcTemplate.update(customerSql, beanParameters);
			}

			String documentManagerSql = "delete from documentmanager where id = :docRefId";
			SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(dmsDocumentDetails);
			jdbcTemplate.update(documentManagerSql, beanParameters);

			String dmsErrorDelete = "delete from dmsdocprocesslog where id=:id";
			beanParameters = new BeanPropertySqlParameterSource(dmsDocumentDetails);
			jdbcTemplate.update(dmsErrorDelete, beanParameters);

			dmsDocumentDetails.setStatus(DmsDocumentConstants.DMS_DOCUMENT_STATUS_SUCCESS);

			String sql = "insert into dmsdocprocesslog "
					+ "(id,finreference,docmodule,docrefid,state,status,lastMntOn,createdOn,referenceid,customercif,docid,docCategory,retrycount) "
					+ "values (:id,:finReference,:docModule,:docRefId,:state,:status,:lastMntOn,:createdOn,:referenceId,:customerCif,:docId,:docCategory,:retryCount)";
			beanParameters = new BeanPropertySqlParameterSource(dmsDocumentDetails);
			jdbcTemplate.update(sql, beanParameters);

			String dmsDelete = "delete from dmsdocprocess where id = :id";
			beanParameters = new BeanPropertySqlParameterSource(dmsDocumentDetails);
			jdbcTemplate.update(dmsDelete, beanParameters);
			transManager.commit(txnStatus);
		} catch (Exception e) {
			transManager.rollback(txnStatus);
			logger.error("Exception", e);
		}
		logger.debug("Leaving");
	}

	@Override
	public void processFailure(DMSDocumentDetails dmsDocumentDetails, int configRetryCount) {
		logger.debug("Entering");
		TransactionStatus txnStatus = null;
		try {
			txnStatus = transManager.getTransaction(transDef);
			if (dmsDocumentDetails.getRetryCount() >= configRetryCount) {
				String dmsDelete = "delete from dmsdocprocess where id = :id";
				BeanPropertySqlParameterSource beanParameters = new BeanPropertySqlParameterSource(dmsDocumentDetails);
				jdbcTemplate.update(dmsDelete, beanParameters);

				String sql = "insert into dmsdocprocesslog "
						+ "(id,finreference,docmodule,docrefid,state,status,lastMntOn,createdOn,referenceid,customercif,docid,docCategory,retrycount,errordesc) "
						+ "values (:id,:finReference,:docModule,:docRefId,:state,:status,:lastMntOn,:createdOn,:referenceId,:customerCif,:docId,:docCategory,:retryCount,:errorDesc)";
				beanParameters = new BeanPropertySqlParameterSource(dmsDocumentDetails);
				jdbcTemplate.update(sql, beanParameters);

				String customerSql = "update dmsdocprocesslog set retrycount=:retryCount,status = '"
						+ DmsDocumentConstants.DMS_DOCUMENT_STATUS_NONPROCESSABLE + "' where id=:id";
				beanParameters = new BeanPropertySqlParameterSource(dmsDocumentDetails);
				jdbcTemplate.update(customerSql, beanParameters);
				logger.info("DMSDocument " + dmsDocumentDetails.getId() + " reference "
						+ dmsDocumentDetails.getReferenceId() + " have reached maximum retry count "
						+ dmsDocumentDetails.getRetryCount() + " for loan " + dmsDocumentDetails.getFinReference());
			} else {
				String sql = "insert into dmsdocprocesslog "
						+ "(id,finreference,docmodule,docrefid,state,status,lastMntOn,createdOn,referenceid,customercif,docid,docCategory,retrycount,errordesc) "
						+ "values (:id,:finReference,:docModule,:docRefId,:state,:status,:lastMntOn,:createdOn,:referenceId,:customerCif,:docId,:docCategory,:retryCount,:errorDesc)";
				BeanPropertySqlParameterSource beanParameters = new BeanPropertySqlParameterSource(dmsDocumentDetails);
				jdbcTemplate.update(sql, beanParameters);

				String dmsProcessUpdateSql = "update dmsdocprocess set retrycount=:retryCount,state=:state,status=:status where id=:id";
				beanParameters = new BeanPropertySqlParameterSource(dmsDocumentDetails);
				jdbcTemplate.update(dmsProcessUpdateSql, beanParameters);
			}
			transManager.commit(txnStatus);
		} catch (Exception e) {
			transManager.rollback(txnStatus);
			logger.error("Exception", e);
		}
		logger.debug("Leaving");
	}

	@Override
	public List<DMSDocumentDetails> retrieveDMSDocumentLogs(long dmsId) {
		logger.debug("Entering");
		List<DMSDocumentDetails> dmsDocumentDetails = null;
		try {
			String sql = "select * from dmsdocprocesslog where id = :dmsId";
			RowMapper<DMSDocumentDetails> typeRowMapper = ParameterizedBeanPropertyRowMapper
					.newInstance(DMSDocumentDetails.class);
			MapSqlParameterSource paramMap = new MapSqlParameterSource();
			paramMap.addValue("dmsId", dmsId);
			dmsDocumentDetails = this.jdbcTemplate.query(sql, paramMap, typeRowMapper);
		} catch (Exception e) {
			logger.error("Exception", e);
		}
		logger.debug(Literal.LEAVING);
		return dmsDocumentDetails;
	}
	
	@Override
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
		this.transManager = new DataSourceTransactionManager(dataSource);
		this.transDef = new DefaultTransactionDefinition();
		this.transDef.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
		this.transDef.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
		this.transDef.setTimeout(60);
	}
}
