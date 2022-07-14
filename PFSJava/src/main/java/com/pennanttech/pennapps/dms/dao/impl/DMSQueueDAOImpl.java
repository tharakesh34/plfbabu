package com.pennanttech.pennapps.dms.dao.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;

import com.pennanttech.model.dms.DMSModule;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.dms.dao.DMSQueueDAO;
import com.pennanttech.pennapps.dms.model.DMSQueue;

public class DMSQueueDAOImpl extends SequenceDao<DMSQueue> implements DMSQueueDAO {
	private static Logger logger = LogManager.getLogger(DMSQueueDAOImpl.class);

	@Override
	public void log(DMSQueue dMSQueue) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Insert into DMS_QUEUE");
		sql.append(" (DocManagerId, CustId, CustCIF, FinReference, Module, SubModule, Reference");
		sql.append(", DocName, DocCategory, DocType, DocExt, CreatedOn, CreatedBy");
		sql.append(", OfferId, ApplicationNo, AuxiloryFields1, DocUri )");
		sql.append(" values(?, ?, ?, ?, ?, ?, ?");
		sql.append(", ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL + sql.toString());
		try {
			this.jdbcOperations.update(sql.toString(), new PreparedStatementSetter() {
				@Override
				public void setValues(PreparedStatement ps) throws SQLException {
					ps.setLong(1, dMSQueue.getDocManagerID());
					ps.setObject(2, dMSQueue.getCustId());
					ps.setString(3, dMSQueue.getCustCif());
					ps.setString(4, dMSQueue.getFinReference());
					ps.setString(5, dMSQueue.getModule().name());
					ps.setString(6, dMSQueue.getSubModule().name());
					ps.setString(7, dMSQueue.getReference());
					ps.setString(8, dMSQueue.getDocName());
					ps.setString(9, dMSQueue.getDocCategory());
					ps.setString(10, dMSQueue.getDocType());
					ps.setString(11, dMSQueue.getDocExt());
					ps.setTimestamp(12, dMSQueue.getCreatedOn());
					ps.setLong(13, dMSQueue.getCreatedBy());
					ps.setString(14, dMSQueue.getOfferId());
					ps.setString(15, dMSQueue.getApplicationNo());
					ps.setString(16, dMSQueue.getAuxiloryFields1());
					ps.setString(17, dMSQueue.getDocUri());
				}
			});
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		logger.debug(Literal.LEAVING);
	}

	public void insertDMSQueueLog(DMSQueue dMSQueue) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Insert into DMS_QUEUE_LOG");
		sql.append(" (Id, DocManagerId, CustId, CustCIF, FinReference, Module, SubModule, Reference");
		sql.append(", DocName, DocCategory, DocType, DocExt, Docuri, CreatedOn, CreatedBy, processflag");
		sql.append(", attemptnum, errorcode, errordesc)");
		sql.append(" values(?, ?, ?, ?, ?, ?, ?, ?");
		sql.append(", ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL + sql.toString());
		try {
			this.jdbcOperations.update(sql.toString(), new PreparedStatementSetter() {
				@Override
				public void setValues(PreparedStatement ps) throws SQLException {
					ps.setLong(1, dMSQueue.getId());
					ps.setLong(2, dMSQueue.getDocManagerID());
					ps.setObject(3, dMSQueue.getCustId());
					ps.setString(4, dMSQueue.getCustCif());
					ps.setString(5, dMSQueue.getFinReference());
					ps.setString(6, dMSQueue.getModule().name());
					ps.setString(7, dMSQueue.getSubModule().name());
					ps.setString(8, dMSQueue.getReference());
					ps.setString(9, dMSQueue.getDocName());
					ps.setString(10, dMSQueue.getDocCategory());
					ps.setString(11, dMSQueue.getDocType());
					ps.setString(12, dMSQueue.getDocExt());
					ps.setString(13, dMSQueue.getDocUri());
					ps.setDate(14, JdbcUtil.getDate(dMSQueue.getCreatedOn()));
					ps.setLong(15, dMSQueue.getCreatedBy());
					ps.setInt(16, dMSQueue.getProcessingFlag());
					ps.setInt(17, dMSQueue.getAttemptNum());
					ps.setString(18, dMSQueue.getErrorCode());
					ps.setString(19, dMSQueue.getErrorDesc());

				}
			});
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public List<Long> processDMSQueue() {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		sql.append("select ID from DMS_QUEUE");
		sql.append(" where ProcessFlag = 0 and AttemptNum <=5");
		logger.trace(Literal.SQL + sql.toString());

		return this.jdbcOperations.queryForList(sql.toString(), Long.class);

	}

	public DMSQueue getDMSQueue(long queueID) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		sql.append("select dq.ID, DocmanagerId, dq.CustId, cs.CustCIF, FinReference, Module, Submodule, Reference");
		sql.append(", DocName, DocCategory, DocType, DocExt, DocImage, cs.CustShrtName, auxiloryFields1");
		sql.append(", dq.processflag, dq.attemptnum, OfferId, ApplicationNo, dq.docuri");
		sql.append(" from DMS_QUEUE dq");
		sql.append(" inner join Documentmanager dm on dm.Id = dq.Docmanagerid");
		sql.append(" inner join Customers cs  on cs.CustId = dq.CustId");
		sql.append(" where dq.ID = ?");
		logger.trace(Literal.SQL + sql.toString());

		return this.jdbcOperations.queryForObject(sql.toString(), new RowMapper<DMSQueue>() {

			@Override
			public DMSQueue mapRow(ResultSet rs, int rowNum) throws SQLException {
				DMSQueue dmsQueue = new DMSQueue();
				dmsQueue.setId(rs.getLong(1));
				dmsQueue.setDocManagerID(rs.getLong(2));
				dmsQueue.setCustId(JdbcUtil.getLong(rs.getObject(3)));
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
				dmsQueue.setAuxiloryFields1(rs.getString(15));
				dmsQueue.setProcessingFlag(rs.getInt(16));
				dmsQueue.setAttemptNum(rs.getInt(17));
				dmsQueue.setOfferId(rs.getString(18));
				dmsQueue.setApplicationNo(rs.getString(19));
				dmsQueue.setDocUri(rs.getString(20));

				return dmsQueue;
			}

		}, queueID);

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

		this.jdbcOperations.update(sql.toString(), new PreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setObject(1, dMSQueue.getCustId());
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

	public byte[] getDocImage(long docMgrId) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("select DocImage from Documentmanager where id = ?");
		DMSQueue dmsQueue = new DMSQueue();

		this.jdbcOperations.query(sql.toString(), new ResultSetExtractor<DMSQueue>() {

			@Override
			public DMSQueue extractData(ResultSet rs) throws SQLException, DataAccessException {
				while (rs.next()) {
					dmsQueue.setDocImage(rs.getBytes("DocImage"));
				}
				return dmsQueue;
			}
		}, docMgrId);
		logger.debug(Literal.LEAVING);
		return dmsQueue.getDocImage();
	}

	@Override
	public int delete(long queueId) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("delete from DMS_QUEUE where Id = ?");
		try {
			return this.jdbcOperations.update(sql.toString(), queueId);
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
	}

	@Override
	public void update(DMSQueue dMSQueue) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Update DMS_QUEUE");
		sql.append(" set  AuxiloryFields1 = ?, createdon= ?");
		sql.append(" where docManagerID = ? and processflag=0");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), new PreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setString(1, dMSQueue.getAuxiloryFields1());
				ps.setTimestamp(2, dMSQueue.getCreatedOn());
				ps.setLong(3, dMSQueue.getDocManagerID());
			}
		});

		logger.debug(Literal.LEAVING);
	}
}
