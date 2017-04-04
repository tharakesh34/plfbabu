package com.pennant.eod;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.jdbc.datasource.DataSourceUtils;

import com.pennant.app.core.RepayQueueService;
import com.pennant.app.core.ServiceUtil;
import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.FinRepayQueue.FinRepayQueue;
import com.pennant.eod.beans.PaymentRecoveryHeader;

public class EodPaymentRequest {

	private static Logger			logger	= Logger.getLogger(EodPaymentRequest.class);

	private DataSource				dataSource;
	private ServiceUtil				serviceUtil;
	private PaymentRecoveryService	paymentRecoveryService;
	private RepayQueueService		repayQueueService;

	public static final String customeFinance = " SELECT F.FinReference, F.FinBranch Branch, F.FinType ,F.CustID CustomerID ,F.LinkedFinRef,  S.SchDate RpyDate,"
			+ "  S.PrincipalSchd, S.SchdPriPaid, (S.ProfitSchd - S.SchdPftPaid) As SchdPftBal,  S.ProfitSchd, S.SchdPftpaid, (S.PrincipalSchd - S.SchdPriPaid) As SchdPriBal,"
			+ " S.SuplRent SchdSuplRent, S.SuplRentPaid SchdSuplRentPaid, (S.SuplRent -  S.SuplRentPaid) SchdSuplRentBal,"
			+ " S.IncrCost SchdIncrCost, S.IncrCostPaid SchdIncrCostPaid, (S.IncrCost - S.IncrCostPaid) SchdIncrCostBal,"
			+ " S.FeeSchd SchdFee , S.SchdFeePaid , (S.FeeSchd - S.SchdFeePaid) SchdFeeBal, "
			+ " S.InsSchd SchdIns, S.SchdInsPaid SchdInsPaid, (S.InsSchd - S.SchdInsPaid) SchdInsBal,"
			+ " S.AdvCalRate, S.AdvProfit, S.CalculatedRate "
			+ " FROM FinanceMain F , FinScheduleDetails S WHERE F.FinReference = S.FinReference  AND S.SchDate <= ? "
			//AND  (S.RepayOnSchDate = 1 OR (S.PftOnSchDate = 1 AND RepayAmount > 0))
			+ " AND F.FinIsActive = 1 "
			+ " AND (S.PrincipalSchd <> S.SchdPriPaid OR S.ProfitSchd <> S.SchdPftPaid "
			+ "   OR S.SuplRent <> S.SuplRentPaid OR  S.IncrCost <> S.IncrCostPaid "
			+ " OR S.FeeSchd <> S.SchdFeePaid OR S.InsSchd <>  S.SchdInsPaid ) "
			+ " AND CustID=?  order by F.LinkedFinRef asc";
	
	public EodPaymentRequest() {
		super();
	}
	
	
	/**
	 * @param custId
	 * @param connection
	 * @throws Exception
	 */
	public void prepareCustomerFinancesRequest() throws Exception {
		logger.debug(" Entering ");
		long start = System.currentTimeMillis();
		System.out.println("Start Time " + start);
		Date date = DateUtility.getAppDate();

		ResultSet resultSet = null;
		PreparedStatement sqlStatement = null;
		try {

			Connection connection = DataSourceUtils.doGetConnection(getDataSource());

			//payments
			PreparedStatement sqlStatement1 = connection
					.prepareStatement("SELECT  distinct CustID FROM FinanceMain where FinIsActive = 1");
			ResultSet resultSetnew = sqlStatement1.executeQuery();

			PaymentRecoveryHeader header = new PaymentRecoveryHeader();
			header.setBatchRefNumber(BatchFileUtil.getBatchReference());
			header.setBatchType(BatchFileUtil.BATCH_CODE);
			header.setFileName(BatchFileUtil.getAutoPayReqFileName());
			header.setFileCreationDate(DateUtility.getSysDate());
			header.setNumberofRecords(0);
			getPaymentRecoveryService().save(header);

			while (resultSetnew.next()) {
				//payments
				sqlStatement = connection.prepareStatement(customeFinance);
				sqlStatement.setDate(1, DateUtility.getDBDate(date.toString()));
				sqlStatement.setLong(2, resultSetnew.getLong("CustID"));
				resultSet = sqlStatement.executeQuery();

				while (resultSet.next()) {
					FinRepayQueue finRepayQueue = getRepayQueueService().doWriteDataToBean(resultSet);
					getServiceUtil().processRepayRequest(date, finRepayQueue, header);
				}

				resultSet.close();
				sqlStatement.close();
			}
			resultSetnew.close();
			sqlStatement1.close();

		} catch (Exception e) {
			logger.error(e);
			throw e;
		} finally {
			if (resultSet != null) {
				resultSet.close();
			}
			if (sqlStatement != null) {
				sqlStatement.close();
			}
		}

		System.out.println("End Time " + System.currentTimeMillis());
		System.out.println("Duration " + (System.currentTimeMillis() - start));
	}

	public ServiceUtil getServiceUtil() {
		return serviceUtil;
	}

	public void setServiceUtil(ServiceUtil serviceUtil) {
		this.serviceUtil = serviceUtil;
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public PaymentRecoveryService getPaymentRecoveryService() {
		return paymentRecoveryService;
	}

	public void setPaymentRecoveryService(PaymentRecoveryService paymentRecoveryService) {
		this.paymentRecoveryService = paymentRecoveryService;
	}

	public RepayQueueService getRepayQueueService() {
		return repayQueueService;
	}

	public void setRepayQueueService(RepayQueueService repayQueueService) {
		this.repayQueueService = repayQueueService;
	}
}
