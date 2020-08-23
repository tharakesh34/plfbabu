package com.pennanttech.pennapps.dms.dao.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowCallbackHandler;

import com.pennanttech.model.dms.DMSModule;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.dms.dao.DMSQueueDAO;
import com.pennanttech.pennapps.dms.model.DMSQueue;
import com.pennanttech.pennapps.dms.service.DMSService;

public class DMSQueueDAOImpl extends SequenceDao<DMSQueue> implements DMSQueueDAO {
	private static Logger logger = Logger.getLogger(DMSQueueDAOImpl.class);

	@Override
	public void log(DMSQueue dMSQueue) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Insert into DMS_QUEUE");
		sql.append(" (DocManagerId, CustId, CustCIF, FinReference, Module, SubModule, Reference");
		sql.append(", DocName, DocCategory, DocType, DocExt, CreatedOn, CreatedBy");
		sql.append(", OfferId, ApplicationNo, AuxiloryFields1)");
		sql.append(" values(?, ?, ?, ?, ?, ?, ?");
		sql.append(", ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL + sql.toString());
		try {
			this.jdbcOperations.update(sql.toString(), new PreparedStatementSetter() {

				@Override
				public void setValues(PreparedStatement ps) throws SQLException {
					ps.setLong(1, JdbcUtil.setLong(dMSQueue.getDocManagerID()));
					ps.setLong(2, JdbcUtil.setLong(dMSQueue.getCustId()));
					ps.setString(3, dMSQueue.getCustCif());
					ps.setString(4, dMSQueue.getFinReference());
					ps.setString(5, dMSQueue.getModule().name());
					ps.setString(6, dMSQueue.getSubModule().name());
					ps.setString(7, dMSQueue.getReference());
					ps.setString(8, dMSQueue.getDocName());
					ps.setString(9, dMSQueue.getDocCategory());
					ps.setString(10, dMSQueue.getDocType());
					ps.setString(11, dMSQueue.getDocExt());
					ps.setDate(12, JdbcUtil.getDate(dMSQueue.getCreatedOn()));
					ps.setLong(13, JdbcUtil.setLong(dMSQueue.getCreatedBy()));
					ps.setString(14, dMSQueue.getOfferId());
					ps.setString(15, dMSQueue.getApplicationNo());
					ps.setString(16, dMSQueue.getAuxiloryFields1());
				}
			});

		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e.getCause());
		}

		logger.debug(Literal.LEAVING);

	}

	@Override
	public void processDMSQueue(DMSService dmsService) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		sql.append("select dq.ID, DocmanagerId, dq.CustId, CustCIF, FinReference, Module, Submodule, Reference");
		sql.append(", DocName, DocCategory, DocType, DocExt, DocImage, OfferId, ApplicationNo,dq.docuri,auxiloryFields1");
		sql.append(" from DMS_QUEUE dq");
		sql.append(" inner join Documentmanager dm on dm.Id = dq.Docmanagerid");
		sql.append(" where ProcessFlag = 0 and AttemptNum <=5");
		logger.trace(Literal.SQL + sql.toString());

		try {

			this.jdbcOperations.query(sql.toString(), new RowCallbackHandler() {

				@Override
				public void processRow(ResultSet rs) throws SQLException {
					DMSQueue dmsQueue = new DMSQueue();
					dmsQueue.setId(rs.getLong(1));
					dmsQueue.setDocManagerID(rs.getLong(2));
					dmsQueue.setCustId(rs.getLong(3));
					dmsQueue.setCustCif(rs.getString(4));
					dmsQueue.setFinReference(rs.getString(5));
					dmsQueue.setModule(DMSModule.getModule(rs.getString(6)));
					dmsQueue.setSubModule(DMSModule.getModule(rs.getString(7)));
					dmsQueue.setReference(rs.getString(8));
					dmsQueue.setDocName(rs.getString(9));
					dmsQueue.setDocCategory(rs.getString(10));
					dmsQueue.setDocType(rs.getString(11));
					dmsQueue.setDocExt(rs.getString(12));
					dmsQueue.setDocImage(rs.getBytes(13));
					dmsQueue.setOfferId(rs.getString(14));
					dmsQueue.setApplicationNo(rs.getString(15));
					dmsQueue.setDocUri(rs.getString(16));
					dmsQueue.setAuxiloryFields1(rs.getString(17));

					if (dmsQueue.getDocImage() != null && dmsQueue.getDocImage().length > 0) {
						dmsService.storeDocInFileSystem(dmsQueue);
					}

				}

			});

		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e.getCause());
		}

	}

	@Override
	public void updateDMSQueue(DMSQueue dMSQueue) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Update DMS_QUEUE");
		sql.append(" set  CustId = ?, CustCIF = ?, FinReference = ?");
		sql.append(", DocURI = ?, ProcessFlag = ?, AttemptNum = ?");
		sql.append(", Errorcode = ?, Errordesc = ?, AuxiloryFields1 = ?");
		sql.append(" where Id = ?");

		logger.debug(Literal.SQL + sql.toString());
		try {
			this.jdbcOperations.update(sql.toString(), new PreparedStatementSetter() {

				@Override
				public void setValues(PreparedStatement ps) throws SQLException {
					ps.setLong(1, JdbcUtil.setLong(dMSQueue.getCustId()));
					ps.setString(2, dMSQueue.getCustCif());
					ps.setString(3, dMSQueue.getFinReference());
					ps.setString(4, dMSQueue.getDocUri());
					ps.setInt(5, dMSQueue.getProcessingFlag());
					ps.setInt(6, dMSQueue.getAttemptNum());
					ps.setString(7, dMSQueue.getErrorCode());
					ps.setString(8, dMSQueue.getErrorDesc());
					ps.setString(9, dMSQueue.getAuxiloryFields1());
					ps.setLong(10, dMSQueue.getId());

				}
			});

		} catch (Exception e) {
			logger.trace(Literal.EXCEPTION, e.getCause());

		}
		logger.debug(Literal.LEAVING);

	}

	@Override
	public byte[] getDocumentByURI(String docURI) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("select DocImage from DMS_QUEUE where docURI = ?");
		DMSQueue dmsQueue = new DMSQueue();

		this.jdbcOperations.query(sql.toString(), new ResultSetExtractor<DMSQueue>() {

			@Override
			public DMSQueue extractData(ResultSet rs) throws SQLException, DataAccessException {
				dmsQueue.setDocImage(rs.getBytes("DocImage"));
				return dmsQueue;
			}
		}, docURI);

		return dmsQueue.getDocImage();
	}
	
	@Override
	public DMSQueue isExistDocuri(String docUri, String reference) {
		logger.debug(Literal.ENTERING);
		
		DMSQueue queue = new DMSQueue();
		StringBuilder sql = new StringBuilder("select dq.id,docmanagerid,dq.custId,custCif,finReference,attemptNum,dq.docUri,errorCode,errorDesc ");
		sql.append(" from dms_queue dq inner join documentmanager dm on dq.docmanagerid = dm.id where dq.docuri = ? and finReference = ?");

		logger.trace(Literal.SQL + sql.toString());
		try {
			return this.jdbcOperations.query(sql.toString(), new PreparedStatementSetter() {

				@Override
				public void setValues(PreparedStatement ps) throws SQLException {
					ps.setString(1, docUri);
					ps.setString(2, reference);
				}
			}, new ResultSetExtractor<DMSQueue>() {
				@Override
				public DMSQueue extractData(ResultSet rs) throws SQLException, DataAccessException {
					
					if (rs.next()) {
						queue.setId(rs.getLong(1));
						queue.setDocManagerID(rs.getLong(2));
						queue.setCustId(rs.getLong(3));
						queue.setCustCif(rs.getString(4));
						queue.setFinReference(rs.getString(5));
						queue.setAttemptNum(rs.getInt(6));
						queue.setDocUri(rs.getString(7));
						queue.setErrorCode(rs.getString(8));
						queue.setErrorDesc(rs.getString(9));
						queue.setProcessingFlag(0);
					}
					return queue;
				}
			});
		} catch (Exception e) {
			logger.warn(Literal.EXCEPTION, e);
		}
		return queue;
	}

}
