package com.pennant.backend.dao.dms.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;
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
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.documentdetails.DocumentManager;
import com.pennant.backend.util.DmsDocumentConstants;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;

public class DMSIdentificationDAOImpl extends SequenceDao<DocumentDetails> implements DMSIdentificationDAO {
	private static Logger logger = LogManager.getLogger(DMSIdentificationDAOImpl.class);

	private static String insertQuery = insertQuery("dmsdocprocess");
	private static String insertLogQuery = insertLogQuery("dmsdocprocesslog");
	private static String updateCustDocQuery = updateCustDocQuery();
	private static String updateCustDocTempQuery = updateCustDocTempQuery();
	private static String updateDocDetailQuery = updateDocDetailsQuery();
	private static String updateDocTempDetailQuery = updateDocTempDetailsQuery();
	private static String updateDmsProcessLogQuery = updateDmsProcessLogQuery();
	private static String dmsProcessUpdateSql = dmsProcessUpdateSql();

	private DataSourceTransactionManager transManager;
	private DefaultTransactionDefinition transDef;

	private enum Field {
		Id(1),
		FinReference(2),
		DocModule(3),
		DocRefId(4),
		State(5),
		Status(6),
		ReferenceId(7),
		RetryCount(8),
		CustomerCif(9),
		DocId(10),
		DocCategory(11),
		DocDesc(12),
		DocExt(13),
		LastMntOn(14),
		CreatedOn(15),
		ErrorDesc(16);

		private int index;

		private Field(int index) {
			this.index = index;
		}

	}

	@Override
	public void saveDMSDocumentReferences(List<DocumentDetails> dmsDocumentDetailList) {
		logger.debug(Literal.ENTERING);

		if (CollectionUtils.isEmpty(dmsDocumentDetailList)) {
			return;
		}

		try {
			//dmsDocumentDetailList.stream().forEach(details -> details.setId(getNextValue("SeqDmsIdentification")));

			SqlParameterSource[] params = SqlParameterSourceUtils.createBatch(dmsDocumentDetailList.toArray());
			jdbcTemplate.batchUpdate(insertQuery, params);

		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
	}

	private static String dmsProcessUpdateSql() {
		StringBuilder sql = new StringBuilder();
		sql.append("update DmsDocProcess set");
		sql.append(" RetryCount = :RetryCount");
		sql.append(", State = :State");
		sql.append(" where Id = :Id");

		return sql.toString();
	}

	private static String updateDmsProcessLogQuery() {
		StringBuilder sql = new StringBuilder();
		sql.append("update DmsDocProcessLog set");
		sql.append(" RetryCount=:RetryCount");
		sql.append(", Status = '" + DmsDocumentConstants.DMS_DOCUMENT_STATUS_NONPROCESSABLE + "'");
		sql.append(" where Id=:Id");

		return sql.toString();
	}

	private static String updateDocDetailsQuery() {

		StringBuilder sql = new StringBuilder();
		sql.append("update DocumentDetails set");
		sql.append(" DocRefId = :DocRefId");
		sql.append(", DocUri = :DocUri");
		sql.append(" where DocId = :DocId");
		sql.append(" and DocCategory=:DocCategory");

		return sql.toString();
	}

	private static String updateDocTempDetailsQuery() {

		StringBuilder sql = new StringBuilder();
		sql.append("update DocumentDetails_temp set");
		sql.append(" DocRefId = :DocRefId");
		sql.append(", DocUri = :DocUri");
		sql.append(" where DocId = :DocId");
		sql.append(" and DocCategory=:DocCategory");

		return sql.toString();
	}

	private static String updateCustDocQuery() {
		StringBuilder sql = new StringBuilder();
		sql.append("update CustomerDocuments set");
		sql.append(" DocRefId = :DocRefId");
		sql.append(", DocUri= :DocUri");
		sql.append(" where CustId=:DocId");
		sql.append(" and CustDocCategory=:DocCategory");

		return sql.toString();
	}

	private static String updateCustDocTempQuery() {
		StringBuilder sql = new StringBuilder();
		sql.append("update CustomerDocuments_Temp set");
		sql.append(" DocRefId = :DocRefId");
		sql.append(", DocUri= :DocUri");
		sql.append(" where CustId=:DocId");
		sql.append(" and CustDocCategory=:DocCategory");

		return sql.toString();
	}

	private static String insertQuery(String tableName) {
		StringBuilder sql = new StringBuilder();
		StringBuilder columns = new StringBuilder();

		sql.append("insert into ");
		sql.append(tableName);
		sql.append("(");

		for (Field field : Field.values()) {

			if (tableName.equals("dmsdocprocess") && field == Field.ErrorDesc) {
				continue;
			}
			if (field == Field.Id) {
				continue;
			}

			if (columns.length() > 0) {
				columns.append(",");
			}

			columns.append(field);
		}

		sql.append(columns.toString());
		sql.append(")");
		sql.append(" values (");
		columns = new StringBuilder();
		for (Field field : Field.values()) {

			if (tableName.equals("dmsdocprocess") && field == Field.ErrorDesc) {
				continue;
			}
			if (field == Field.Id) {
				continue;
			}
			if (columns.length() > 0) {
				columns.append(",");
			}

			columns.append(":");
			columns.append(field);
		}
		sql.append(columns.toString());
		sql.append(")");

		insertQuery = sql.toString();

		return insertQuery;
	}

	private static String insertLogQuery(String tableName) {
		StringBuilder sql = new StringBuilder();
		StringBuilder columns = new StringBuilder();

		sql.append("insert into ");
		sql.append(tableName);
		sql.append("(");

		for (Field field : Field.values()) {

			if (tableName.equals("dmsdocprocess") && field == Field.ErrorDesc) {
				continue;
			}

			if (columns.length() > 0) {
				columns.append(",");
			}

			columns.append(field);
		}

		sql.append(columns.toString());
		sql.append(")");
		sql.append(" values (");
		columns = new StringBuilder();
		for (Field field : Field.values()) {
			if (columns.length() > 0) {
				columns.append(",");
			}

			columns.append(":");
			columns.append(field);
		}
		sql.append(columns.toString());
		sql.append(")");

		insertLogQuery = sql.toString();

		return insertLogQuery;
	}

	@Override
	public List<DocumentDetails> retrieveDMSDocumentReference() {
		try {
			return this.jdbcTemplate.query("select * from DmsDocProcess", new RowMapper<DocumentDetails>() {
				@Override
				public DocumentDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
					DocumentDetails documentDetail = new DocumentDetails();

					documentDetail.setId(rs.getLong(Field.Id.index));
					documentDetail.setFinReference(rs.getString(Field.FinReference.index));
					documentDetail.setDocModule(rs.getString(Field.DocModule.index));
					documentDetail.setDocRefId(rs.getLong(Field.DocRefId.index));
					documentDetail.setState(rs.getString(Field.State.index));
					documentDetail.setStatus(rs.getString(Field.Status.index));
					//	documentDetail.setLastMntOn(rs.getTimestamp(Field.LastMntOn.index));
					//	documentDetail.setCreatedOn(rs.getTimestamp(Field.CreatedOn.index));
					documentDetail.setCustomerCif(rs.getString(Field.CustomerCif.index));
					documentDetail.setReferenceId(rs.getString(Field.ReferenceId.index));
					documentDetail.setDocId(rs.getLong(Field.DocId.index));
					documentDetail.setDocCategory(rs.getString(Field.DocCategory.index));
					documentDetail.setDocDesc(rs.getString(Field.DocDesc.index));
					documentDetail.setDocExt(rs.getString(Field.DocExt.index));

					return documentDetail;
				}
			});
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
		return new ArrayList<>();
	}

	@Override
	public void processSuccessResponse(DocumentDetails docDetails) {
		TransactionStatus txnStatus = transManager.getTransaction(transDef);

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(docDetails);
		jdbcTemplate.update("delete from DocumentManager where id = :DocRefId", beanParameters);

		beanParameters = new BeanPropertySqlParameterSource(docDetails);
		jdbcTemplate.update("delete from DmsDocProcessLog where DocRefId = :DocRefId", beanParameters);

		beanParameters = new BeanPropertySqlParameterSource(docDetails);
		jdbcTemplate.update("delete from DmsDocProcess where DocRefId = :DocRefId", beanParameters);
		try {
			if (StringUtils.equals(docDetails.getDocModule(), "CUSTOMER")) {
				docDetails.setDocRefId(null);
				beanParameters = new BeanPropertySqlParameterSource(docDetails);
				jdbcTemplate.update(updateCustDocQuery, beanParameters);
				jdbcTemplate.update(updateCustDocTempQuery, beanParameters);

			} else {
				docDetails.setDocRefId(null);
				beanParameters = new BeanPropertySqlParameterSource(docDetails);
				jdbcTemplate.update(updateDocDetailQuery, beanParameters);
				jdbcTemplate.update(updateDocTempDetailQuery, beanParameters);
			}

			docDetails.setStatus(DmsDocumentConstants.DMS_DOCUMENT_STATUS_SUCCESS);

			beanParameters = new BeanPropertySqlParameterSource(docDetails);
			jdbcTemplate.update(insertLogQuery, beanParameters);

			transManager.commit(txnStatus);
		} catch (Exception e) {
			transManager.rollback(txnStatus);
			logger.error(Literal.EXCEPTION, e);
		}
		logger.debug("Leaving");
	}

	@Override
	public void processFailure(DocumentDetails dmsDocumentDetails, int configRetryCount) {
		TransactionStatus txnStatus = null;
		try {
			txnStatus = transManager.getTransaction(transDef);
			if (dmsDocumentDetails.getRetryCount() >= configRetryCount) {
				BeanPropertySqlParameterSource beanParameters = new BeanPropertySqlParameterSource(dmsDocumentDetails);
				jdbcTemplate.update("delete from DmsDocProcess where Id = :DocRefId", beanParameters);

				beanParameters = new BeanPropertySqlParameterSource(dmsDocumentDetails);
				jdbcTemplate.update(insertLogQuery, beanParameters);

				beanParameters = new BeanPropertySqlParameterSource(dmsDocumentDetails);
				jdbcTemplate.update(updateDmsProcessLogQuery, beanParameters);
			} else {

				BeanPropertySqlParameterSource beanParameters = new BeanPropertySqlParameterSource(dmsDocumentDetails);
				jdbcTemplate.update(insertLogQuery, beanParameters);

				beanParameters = new BeanPropertySqlParameterSource(dmsDocumentDetails);
				jdbcTemplate.update(dmsProcessUpdateSql, beanParameters);
			}
			transManager.commit(txnStatus);
		} catch (Exception e) {
			transManager.rollback(txnStatus);
			logger.error(Literal.EXCEPTION, e);
		}
	}

	@Override
	public List<DocumentDetails> retrieveDMSDocumentLogs(long dmsId) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		paramMap.addValue("dmsId", dmsId);
		try {
			return this.jdbcTemplate.query("select * from DmsDocProcessLog  where id = :dmsId", paramMap,
					new RowMapper<DocumentDetails>() {
						@Override
						public DocumentDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
							DocumentDetails documentDetail = new DocumentDetails();

							documentDetail.setId(rs.getLong(Field.Id.index));
							documentDetail.setFinReference(rs.getString(Field.FinReference.index));
							documentDetail.setDocModule(rs.getString(Field.DocModule.index));
							documentDetail.setDocRefId(rs.getLong(rs.getString(Field.DocRefId.index)));
							documentDetail.setState(rs.getString(Field.State.index));
							documentDetail.setStatus(rs.getString(Field.Status.index));
							//documentDetail.setLastMntOn(rs.getTimestamp(Field.LastMntOn.index));
							//documentDetail.setCreatedOn(rs.getTimestamp(Field.CreatedOn.index));
							documentDetail.setCustomerCif(rs.getString(Field.CustomerCif.index));
							documentDetail.setReferenceId(rs.getString(Field.ReferenceId.index));
							documentDetail.setDocId(rs.getLong(rs.getString(Field.DocId.index)));
							documentDetail.setDocCategory(rs.getString(Field.DocCategory.index));
							documentDetail.setDocDesc(rs.getString(Field.DocDesc.index));
							documentDetail.setDocExt(rs.getString(Field.DocExt.index));
							documentDetail.setRetryCount(rs.getInt(Field.RetryCount.index));
							documentDetail.setErrorDesc(rs.getString(Field.ErrorDesc.index));

							return documentDetail;
						}
					});
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
		return new ArrayList<>();

	}

	public DocumentManager retrieveDocumentManagerDocImage(long docRefId) {
		logger.debug("Entering");
		DocumentManager documentManager = new DocumentManager();
		documentManager.setId(docRefId);

		StringBuilder selectSql = new StringBuilder("Select Id, DocImage From DocumentManager Where Id =:Id");
		logger.debug("selectSql: " + selectSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(documentManager);
		RowMapper<DocumentManager> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(DocumentManager.class);

		try {
			documentManager = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			documentManager = null;
		}

		logger.debug("Leaving");
		return documentManager;
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
