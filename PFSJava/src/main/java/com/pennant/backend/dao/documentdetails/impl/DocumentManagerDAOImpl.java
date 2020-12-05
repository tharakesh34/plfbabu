package com.pennant.backend.dao.documentdetails.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;

import com.pennant.backend.dao.documentdetails.DocumentManagerDAO;
import com.pennant.backend.model.documentdetails.DocumentManager;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;

public class DocumentManagerDAOImpl extends SequenceDao<DocumentManager> implements DocumentManagerDAO {
	private static Logger logger = Logger.getLogger(DocumentManagerDAOImpl.class);

	public DocumentManagerDAOImpl() {
		super();
	}

	@Override
	public long save(DocumentManager docManager) {
		logger.debug(Literal.ENTERING);

		if (docManager.getId() == Long.MIN_VALUE) {
			docManager.setId(getNextValue("SeqDocumentManager"));

			StringBuilder sql = new StringBuilder(
					"Insert Into DocumentManager (Id, Docimage, CustID, DocURI) Values (?, ?, ?, ?)");

			logger.trace(Literal.SQL + sql.toString());

			this.jdbcOperations.update(sql.toString(), new PreparedStatementSetter() {
				@Override
				public void setValues(PreparedStatement ps) throws SQLException {
					ps.setLong(1, docManager.getId());
					ps.setBytes(2, docManager.getDocImage());
					ps.setLong(3, JdbcUtil.setLong(docManager.getCustId()));
					ps.setString(4, docManager.getDocURI());

				}
			});
		}
		logger.debug(Literal.LEAVING);
		return docManager.getId();
	}

	@Override
	public DocumentManager getById(long id) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Select Id, DocImage, DocURI From DocumentManager Where Id = ?");
		logger.trace(Literal.SQL + sql.toString());
		try {
			return jdbcOperations.queryForObject(sql.toString(), new Object[] { id }, new RowMapper<DocumentManager>() {

				@Override
				public DocumentManager mapRow(ResultSet rs, int rowNum) throws SQLException {
					DocumentManager documentManager = new DocumentManager();
					documentManager.setId(id);
					documentManager.setDocImage(rs.getBytes(2));
					documentManager.setDocURI(rs.getString(3));
					return documentManager;
				}
			});
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);
		return null;
	}

	@Override
	public void update(long id, Long custId, String uri) {
		logger.debug(Literal.ENTERING);
		StringBuilder sql = new StringBuilder("update DocumentManager set DocURI = ? where id = ?");

		try {
			this.jdbcOperations.update(sql.toString(), new PreparedStatementSetter() {

				@Override
				public void setValues(PreparedStatement ps) throws SQLException {
					ps.setString(1, uri);
					ps.setLong(2, id);
				}
			});

		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);
	}

	@Override
	public void update(long docRefID, byte[] docImage) {
		logger.debug(Literal.ENTERING);

		DocumentManager documentManager = new DocumentManager();
		documentManager.setDocImage(docImage);
		documentManager.setId(docRefID);

		StringBuilder sql = new StringBuilder("update DocumentManager set DocImage = ? where Id = ?");

		logger.trace(Literal.SQL + sql.toString());
		jdbcOperations.update(sql.toString(), new Object[] { docImage, docRefID });
		logger.debug(Literal.LEAVING);
	}
}