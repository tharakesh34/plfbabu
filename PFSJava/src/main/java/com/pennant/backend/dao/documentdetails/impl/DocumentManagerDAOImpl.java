package com.pennant.backend.dao.documentdetails.impl;

import org.apache.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.documentdetails.DocumentManagerDAO;
import com.pennant.backend.model.documentdetails.DocumentManager;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;

public class DocumentManagerDAOImpl extends SequenceDao<DocumentManager> implements DocumentManagerDAO {
	private static Logger logger = Logger.getLogger(DocumentManagerDAOImpl.class);

	
	@Override
	public long save(DocumentManager documentManager) {
		logger.debug("Entering"+", documentManager="+documentManager);
		
		if (documentManager.getId() == Long.MIN_VALUE) {
			documentManager.setId(getNextValue(
					"SeqDocumentManager"));

			StringBuilder insertSql = new StringBuilder(
					"Insert Into DocumentManager (Id, Docimage) Values (:Id, :DocImage)");

			logger.debug("insertSql: " + insertSql.toString());

			SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(
					documentManager);
			this.jdbcTemplate.update(insertSql.toString(),
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
			documentManager = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			documentManager = null;
		}
		
		logger.debug("Leaving");
		return documentManager;
	}
}