package com.pennanttech.external.sihold.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pennanttech.external.sihold.model.SIHoldDetails;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;

@SuppressWarnings("rawtypes")
public class ExternalSIHoldMarkingDAOImpl extends SequenceDao implements ExternalSIHoldMarkingDAO {
	private static final Logger logger = LogManager.getLogger(ExternalSIHoldMarkingDAOImpl.class);

	@Override
	public void insertHoldData(SIHoldDetails details) {

	}

	@Override
	public List<SIHoldDetails> getHoldRecords(int fileStatus) {
		String queryStr;

		List<SIHoldDetails> unprocessedHoldDetailsList = new ArrayList<SIHoldDetails>();

		queryStr = fetchUnprocessedHoldMarkQuery();
		jdbcOperations.query(queryStr, ps -> {
			ps.setInt(1, fileStatus);
		}, rs -> {
			SIHoldDetails holdDetails = new SIHoldDetails();
			holdDetails.setAccount(rs.getString("Account"));
			holdDetails.setFileStatus(rs.getInt("FileStatus"));
			holdDetails.setHoldAmt(rs.getBigDecimal("HoldAmt"));
			holdDetails.setLoanRef(rs.getString("LoanRef"));
			holdDetails.setSchDate(rs.getDate("SchDate"));
			unprocessedHoldDetailsList.add(holdDetails);
		});
		logger.debug(Literal.LEAVING);
		return unprocessedHoldDetailsList;
	}

	private String fetchUnprocessedHoldMarkQuery() {
		StringBuilder query = new StringBuilder();
		query.append(" Select * from LOAN_SIHOLD Where FileStatus = ?");
		return query.toString();
	}

	@Override
	public void updateHoldRecordFileStatus(String accNumber, String loanRef, Date schDate, int fileStatus) {
		logger.debug(Literal.ENTERING);
		String queryStr = "UPDATE LOAN_SIHOLD SET FileStatus = ? WHERE Account= ? AND LoanRef = ? AND SchDate=?";
		logger.debug(Literal.SQL + queryStr);
		jdbcOperations.update(queryStr.toString(), ps -> {
			int index = 1;
			ps.setInt(index++, fileStatus);
			ps.setString(index++, accNumber);
			ps.setString(index++, loanRef);
			ps.setDate(index, JdbcUtil.getDate(schDate));
		});
		logger.debug(Literal.LEAVING);

	}

	@Override
	public long getSeqNumber(String tableName) {
		return getNextValue(tableName);
	}
}
