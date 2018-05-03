package com.pennant.app.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import org.apache.log4j.Logger;

import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.finance.FinCollaterals;
import com.pennant.backend.service.collateral.CollateralMarkProcess;
import com.pennant.eod.constants.EodConstants;
import com.pennanttech.pennapps.core.InterfaceException;

public class CollateralService extends ServiceHelper {

	private static final long		serialVersionUID	= -3371115026576113554L;

	private static Logger			logger				= Logger.getLogger(CollateralService.class);

	private CollateralMarkProcess	collateralMarkProcess;

	public static final String		collateralDemark	= " SELECT T1.FinReference, T1.Reference, T1.Value, T1.Remarks FROM FinCollaterals T1 "
																+ " INNER JOIN FinanceMain T2 ON T1.FinReference = T2.FinReference "
																+ " INNER JOIN CollateralMarkLog T3 ON T1.FinReference = T3.FinReference"
																+ " INNER JOIN FinPftDetails T4 ON T1.FinReference = T4.FinReference"
																+ " WHERE T2.FinIsActive = ? AND T2.ClosingStatus = ? AND T3.Status = ? "
																+ " AND T3.Status <> ? AND T4.FullPaidDate = ? AND T2.CustID = ?";

	/**
	 * Process Collateral request and do below Action<br>
	 * --> Collateral Mark<br>
	 * --> Collateral DeMark
	 * 
	 * @param custId
	 * @param connection
	 * @param resultSet
	 * @param sqlStatement
	 * @param date
	 * @throws Exception
	 */
	public void processCollaterals(Connection connection, long custId, Date date) throws Exception {
		ResultSet resultSet = null;
		PreparedStatement sqlStatement = null;
		try {
			sqlStatement = connection.prepareStatement(collateralDemark);
			sqlStatement.setBoolean(1, false);
			sqlStatement.setString(2, EodConstants.FIN_CLOSESTS);
			sqlStatement.setString(3, EodConstants.COLLT_MARKSTS);
			sqlStatement.setString(4, EodConstants.COLLT_DEMARKSTS);
			sqlStatement.setDate(5, DateUtility.getDBDate(String.valueOf(DateUtility.getAppDate())));
			sqlStatement.setLong(6, custId);
			resultSet = sqlStatement.executeQuery();
			while (resultSet.next()) {
				try {
					doCollateralDemarking(resultSet);
				} catch (InterfaceException e) {
					logger.error("Exception: ", e);
					continue;
				}
			}
		} catch (Exception e) {
			logger.error("Exception :", e);
			throw e;
		} finally {
			if (resultSet != null) {
				resultSet.close();
			}
			if (sqlStatement != null) {
				sqlStatement.close();
			}
		}
	}

	/**
	 * Method for validate the collateral Demark request and send to middleware services
	 * 
	 * @param resultSet
	 * @throws InterfaceException
	 * @throws SQLException
	 */
	public void doCollateralDemarking(ResultSet resultSet) throws InterfaceException, SQLException {
		logger.debug("Entering");

		FinCollaterals finCollaterals = new FinCollaterals();
		finCollaterals.setFinReference(resultSet.getString("FinReference"));
		finCollaterals.setValue(resultSet.getBigDecimal("value"));// Deposit Amount
		finCollaterals.setReference(resultSet.getString("Reference"));//Deposit ID
		finCollaterals.setRemarks(resultSet.getString("Remarks"));

		// Send Collateral De-Mark request to interface through online request
		collateralMarkProcess.deMarkCollateral(finCollaterals);

		logger.debug("Leaving");
	}

	public void setCollateralMarkProcess(CollateralMarkProcess collateralMarkProcess) {
		this.collateralMarkProcess = collateralMarkProcess;
	}

}
