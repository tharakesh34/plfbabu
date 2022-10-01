package com.pennant.backend.dao.documentdetails.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.PreparedStatementSetter;

import com.pennant.backend.dao.documentdetails.DocumentManagerDAO;
import com.pennant.backend.model.documentdetails.DocumentManager;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;

public class DocumentManagerDAOImpl extends SequenceDao<DocumentManager> implements DocumentManagerDAO {
	private static Logger logger = LogManager.getLogger(DocumentManagerDAOImpl.class);

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
					ps.setObject(3, docManager.getCustId());
					ps.setString(4, docManager.getDocURI());

				}
			});
		}
		logger.debug(Literal.LEAVING);
		return docManager.getId();
	}

	@Override
	public DocumentManager getById(long id) {
		StringBuilder sql = new StringBuilder("Select Id, DocImage, DocURI From DocumentManager Where Id = ?");
		logger.trace(Literal.SQL + sql.toString());
		try {
			return jdbcOperations.queryForObject(sql.toString(), (ResultSet rs, int rowNum) -> {
				DocumentManager documentManager = new DocumentManager();
				documentManager.setId(id);
				documentManager.setDocImage(rs.getBytes(2));
				documentManager.setDocURI(rs.getString(3));
				return documentManager;
			}, id);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Document details not found in DocumentManager for the specified id >> {}", id);
		}
		return null;
	}

	@Override
	public void update(long id, Long custId, String uri) {
		StringBuilder sql = new StringBuilder("Update DocumentManager");
		sql.append(" set DocURI = ? ");
		sql.append(" Where id = ?");

		this.jdbcOperations.update(sql.toString(), new PreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				int index = 1;
				ps.setString(index++, uri);
				ps.setLong(index, id);
			}
		});
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