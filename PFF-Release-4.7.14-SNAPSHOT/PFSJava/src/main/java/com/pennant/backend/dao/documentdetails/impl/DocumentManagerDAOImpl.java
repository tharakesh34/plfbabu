package com.pennant.backend.dao.documentdetails.impl;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.documentdetails.DocumentManagerDAO;
import com.pennant.backend.dao.impl.BasisNextidDaoImpl;
import com.pennant.backend.model.documentdetails.DocumentManager;

public class DocumentManagerDAOImpl extends BasisNextidDaoImpl<DocumentManager> implements DocumentManagerDAO {


	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	private static Logger logger = Logger.getLogger(DocumentManagerDAOImpl.class);

	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	@Override
	public long save(DocumentManager documentManager) {
		logger.debug("Entering"+", documentManager="+documentManager);
		
		if (documentManager.getId() == Long.MIN_VALUE) {
			documentManager.setId(getNextidviewDAO().getNextId(
					"SeqDocumentManager"));

			StringBuilder insertSql = new StringBuilder(
					"Insert Into DocumentManager (Id, Docimage) Values (:Id, :DocImage)");

			logger.debug("insertSql: " + insertSql.toString());

			SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(
					documentManager);
			this.namedParameterJdbcTemplate.update(insertSql.toString(),
					beanParameters);
		}
		logger.debug("Leaving");
		return documentManager.getId();
	}

	@Override
	public DocumentManager getById(long id) {
		logger.debug("Entering");
		DocumentManager documentManager = new DocumentManager();
		documentManager.setId(id);

		StringBuilder selectSql = new StringBuilder("Select Id, DocImage From DocumentManager Where Id =:Id");
		logger.debug("selectSql: " + selectSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(documentManager);
		RowMapper<DocumentManager> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(DocumentManager.class);

		try {
			documentManager = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			documentManager = null;
		}
		
		logger.debug("Leaving");
		return documentManager;
	}
}