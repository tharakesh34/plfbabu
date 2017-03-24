package com.pennant.eod;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.jdbc.datasource.DataSourceUtils;

import com.pennant.app.constants.AccountEventConstants;
import com.pennant.app.core.ServiceHelper;
import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.rulefactory.DataSet;
import com.pennant.backend.model.rulefactory.DataSetFiller;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.eod.constants.EodSql;

public class ThirdPartyPostingService extends ServiceHelper {

	private static final long serialVersionUID = -8918958779146455681L;

	private static Logger logger = Logger.getLogger(ThirdPartyPostingService.class);

	/**
	 * Method for process third party postings<br>
	 * Third party postings performed on monthly basis(i.e once per month)
	 * 
	 * @param dateValueDate
	 * 				used to prepare the month start and End date
	 * @throws Exception
	 */
	public void processThirdPartyPostings(Date dateValueDate) throws Exception {
		logger.debug("Entering");

		Connection connection = null;
		PreparedStatement sqlStatement = null;
		ResultSet resultSet = null;

		Date monthStartDate = DateUtility.getMonthEndDate(dateValueDate);
		Date monthEndDate = DateUtility.getMonthEndDate(dateValueDate);
		
		try{
			connection = DataSourceUtils.getConnection(getDataSource());
			sqlStatement = connection.prepareStatement(EodSql.insurancePostings);//third party can be any type
			sqlStatement.setDate(1, DateUtility.getDBDate(monthStartDate.toString()));
			sqlStatement.setDate(2, DateUtility.getDBDate(monthEndDate.toString()));
			resultSet = sqlStatement.executeQuery();

			while(resultSet.next()) {
				doThirdPartyPostings(resultSet);
			}
		} catch(Exception e) {
			logger.error("Exception: ", e);
			throw e;
		} finally {
			if (resultSet != null) {
				resultSet.close();
			}
			if (sqlStatement != null) {
				sqlStatement.close();
			}
			DataSourceUtils.releaseConnection(connection, getDataSource());
		}

		logger.debug("Leaving");
	}

	/**
	 * Method for preparing the accounting details and Execute Third party postings based on event. 
	 * 
	 * @param resultSet
	 * @throws Exception
	 */
	public void doThirdPartyPostings(ResultSet resultSet) throws Exception {
		logger.debug("Entering");

		// DataSet Object preparation for AccountingSet Execution
		DataSet dataSet = new DataSet();
		dataSet.setFinReference(resultSet.getString("FinReference"));
		dataSet.setFinBranch(resultSet.getString("FinBranch"));
		dataSet.setFinType(resultSet.getString("FinType"));
		dataSet.setFinEvent(AccountEventConstants.THIRDPARTY_TRANSFER);
		dataSet.setNewRecord(false);

		// Postings Process
		DataSetFiller filler = new DataSetFiller();

		List<ReturnDataSet> list = processAccountingByEvent(dataSet, filler, AccountEventConstants.THIRDPARTY_TRANSFER);
		saveAccounting(list);

		logger.debug("Leaving");
	}
}
