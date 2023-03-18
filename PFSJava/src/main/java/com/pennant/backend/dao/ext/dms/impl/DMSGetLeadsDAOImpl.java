package com.pennant.backend.dao.ext.dms.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Calendar;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import com.pennant.app.util.DateUtility;
import com.pennant.backend.dao.ext.dms.DMSGetLeadsDAO;
import com.pennanttech.model.dms.DMSLeadDetails;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.resource.Literal;

public class DMSGetLeadsDAOImpl extends BasicDao<DMSLeadDetails> implements DMSGetLeadsDAO {

	@Override
	public String processLeadsForDMSRetrieval(DMSLeadDetails dmsLeadDetails) {
		logger.debug(Literal.ENTERING);
		int[] result;

		String insertSql = "insert into DMS_RetreievProcess_Leads (LeadId, ProcessedFlag, InsertedOn) values (?, ?, ?)";

		logger.debug(Literal.SQL + insertSql);

		try {
			result = jdbcOperations.batchUpdate(insertSql, new BatchPreparedStatementSetter() {
				@Override
				public void setValues(PreparedStatement ps, int i) throws SQLException {
					ps.setString(1, dmsLeadDetails.getLeadIds().get(i));
					ps.setString(2, "N");
					ps.setDate(3, DateUtility
							.getDBDate(String.valueOf(DateUtility.getTimestamp(Calendar.getInstance().getTime()))));

				}

				@Override
				public int getBatchSize() {
					return dmsLeadDetails.getLeadIds().size();
				}

			});
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION);
			throw new InterfaceException("8903", "Error saving LeadIds for DMS Retrieval process", e);
		}
		logger.debug(Literal.LEAVING);
		return result.length > 0 ? "Lead Ids pushed Successfully" : "Lead Ids push Failed";
	}

}
