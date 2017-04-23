/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */

/**
 ******************************************************************************************** 
 * FILE HEADER *
 ******************************************************************************************** 
 * 
 * FileName : LatePayMarkingService.java *
 * 
 * Author : PENNANT TECHONOLOGIES *
 * 
 * Creation Date : 26-04-2011 *
 * 
 * Modified Date : 30-07-2011 *
 * 
 * Description : *
 * 
 ******************************************************************************************** 
 * Date Author Version Comments *
 ******************************************************************************************** 
 * 26-04-2011 Pennant 0.1 * * * * * * * * *
 ******************************************************************************************** 
 */
package com.pennant.app.core;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.app.util.DateUtility;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.customermasters.CustomerDAO;
import com.pennant.backend.dao.finance.FinODDetailsDAO;
import com.pennant.backend.dao.finance.FinStatusDetailDAO;
import com.pennant.backend.model.FinRepayQueue.FinRepayQueue;
import com.pennant.backend.model.applicationmaster.DPDBucketConfiguration;
import com.pennant.backend.model.finance.FinODDetails;
import com.pennant.backend.model.finance.FinStatusDetail;
import com.pennant.eod.util.EODProperties;

public class LatePayMarkingService extends ServiceHelper {

	private static final long	serialVersionUID	= 6161809223570900644L;
	private static Logger		logger				= Logger.getLogger(LatePayMarkingService.class);

	public static final String	PDCALCULATION		= "SELECT FinReference, RpyDate, FinRpyFor, Branch, FinType, CustomerID, SchdPriBal, SchdPftBal "
															+ " FROM FinRpyQueue WHERE CustomerID=? AND FINRPYFOR = ?";

	public static final String	DPDBUCKETING		= "SELECT FP.FinReference,FP.ODDAYS,FP.TDSCHDPFT,FP.TDSCHDPFTPAID,FP.TDSCHDPRI,FP.TDSCHDPRIPAID, "
															+ " FP.ExcessAmt,FP.EmiInAdvance,FP.PayableAdvise,FM.FinStatus,FP.FinCategory "
															+ " FROM FINPFTDETAILS FP INNER JOIN Financemain FM ON FP.FINREFERENCE = FM.FINREFERENCE WHERE FP.CURODDAYS > 0 and FP.CustID=?";

	public static final String	CUSTOMERSTATUS		= "SELECT FM.FinReference,FM.FinStatus,FP.FinCategory FROM FINANCEMAIN FM INNER JOIN FINPFTDETAILS FP ON  FM.FINREFERENCE = FP.FINREFERENCE"
															+ " and CustID = ?";
	private FinODDetailsDAO		finODDetailsDAO;
	private FinStatusDetailDAO	finStatusDetailDAO;
	private CustomerDAO			customerDAO;

	/**
	 * Default constructor
	 */
	public LatePayMarkingService() {
		super();
	}

	/**
	 * @param connection
	 * @param custId
	 * @param date
	 * @throws Exception
	 */
	public void processLatePayMarking(Connection connection, long custId, Date date) throws Exception {
		ResultSet resultSet = null;
		PreparedStatement sqlStatement = null;
		String finreference = "";

		try {
			//payments
			sqlStatement = connection.prepareStatement(PDCALCULATION);
			sqlStatement.setLong(1, custId);
			sqlStatement.setString(2, "S");
			resultSet = sqlStatement.executeQuery();

			while (resultSet.next()) {
				finreference = resultSet.getString("FinReference");
				FinRepayQueue finRepayQueue = new FinRepayQueue();
				finRepayQueue.setFinReference(finreference);
				finRepayQueue.setRpyDate(resultSet.getDate("RpyDate"));
				finRepayQueue.setFinRpyFor(resultSet.getString("FinRpyFor"));
				finRepayQueue.setBranch(resultSet.getString("Branch"));
				finRepayQueue.setFinType(resultSet.getString("FinType"));
				finRepayQueue.setCustomerID(resultSet.getLong("CustomerID"));
				finRepayQueue.setSchdPriBal(getDecimal(resultSet, "SchdPriBal"));
				finRepayQueue.setSchdPftBal(getDecimal(resultSet, "SchdPftBal"));
				latePayMarking(finRepayQueue, date);
			}

		} catch (Exception e) {
			logger.error("Exception: Finreference :" + finreference, e);
			throw new Exception("Exception: Finreference : " + finreference, e);
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
	 * @param connection
	 * @param custId
	 * @param date
	 * @throws Exception
	 */
	public void processDPDBuketing(Connection connection, long custId, Date date) throws Exception {
		ResultSet resultSet = null;
		PreparedStatement sqlStatement = null;
		String finreference = "";

		try {
			//payments
			sqlStatement = connection.prepareStatement(DPDBUCKETING);
			sqlStatement.setLong(1, custId);
			resultSet = sqlStatement.executeQuery();

			while (resultSet.next()) {
				finreference = resultSet.getString("FinReference");
				int dueDays = resultSet.getInt("ODDAYS");
				BigDecimal tdSchdPri = getDecimal(resultSet, "tdSchdPri");
				BigDecimal tdSchdPriPaid = getDecimal(resultSet, "tdSchdPriPaid");
				BigDecimal tdSchdPft = getDecimal(resultSet, "tdSchdPft");
				BigDecimal tdSchdPftPaid = getDecimal(resultSet, "tdSchdPftPaid");
				BigDecimal excessAmt = getDecimal(resultSet, "excessAmt");
				BigDecimal emiInAdvance = getDecimal(resultSet, "EmiInAdvance");
				BigDecimal payableAdvise = getDecimal(resultSet, "PayableAdvise");

				String finStatus = resultSet.getString("FinStatus");

				BigDecimal numerator = tdSchdPri.add(tdSchdPft).subtract(tdSchdPriPaid).subtract(tdSchdPftPaid)
						.subtract(excessAmt).subtract(emiInAdvance).subtract(payableAdvise);

				BigDecimal duepercentgae = (numerator.divide(tdSchdPri.add(tdSchdPft), 0, RoundingMode.HALF_DOWN))
						.multiply(new BigDecimal(100));

				BigDecimal minDuePerc = (BigDecimal) SysParamUtil.getValue("MIN_DUE_PERCENTAGE_FOR_BUCKETS");

				String productCode = resultSet.getString("FinCategory");
				long bucketID = 0;

				if (duepercentgae.compareTo(minDuePerc) > 0) {
					List<DPDBucketConfiguration> list = EODProperties.getBucketConfigurations(productCode);
					sortBucketConfig(list);
					for (DPDBucketConfiguration dpdBucketConfiguration : list) {
						if (dpdBucketConfiguration.getDueDays() >= dueDays) {
							bucketID = dpdBucketConfiguration.getBucketID();
							break;
						}
					}
				}

				String bucketCode = "";
				if (bucketID != 0) {
					bucketCode = EODProperties.getBucket(bucketID);
				}

				boolean isStsChanged = false;
				if (!StringUtils.equals(finStatus, bucketCode)) {
					isStsChanged = true;
				}

				if (isStsChanged) {
					FinStatusDetail statusDetail = new FinStatusDetail();
					statusDetail.setFinReference(finreference);
					statusDetail.setValueDate(date);
					statusDetail.setCustId(custId);
					statusDetail.setFinStatus(bucketCode);
					statusDetail.setODDays(dueDays);
					finStatusDetailDAO.saveOrUpdateFinStatus(statusDetail);
				}

			}

		} catch (Exception e) {
			logger.error("Exception: Finreference :" + finreference, e);
			throw new Exception("Exception: Finreference : " + finreference, e);
		} finally {
			if (resultSet != null) {
				resultSet.close();
			}
			if (sqlStatement != null) {
				sqlStatement.close();
			}
		}
	}

	public void processCustomerStatus(Connection connection, long custId, Date date) throws Exception {
		ResultSet resultSet = null;
		PreparedStatement sqlStatement = null;
		String finreference = "";

		try {
			//payments
			sqlStatement = connection.prepareStatement(CUSTOMERSTATUS);
			sqlStatement.setLong(1, custId);
			resultSet = sqlStatement.executeQuery();

			String custStatus = "";
			int maxDueDays = 0;

			while (resultSet.next()) {
				finreference = resultSet.getString("FinReference");
				String finStatus = resultSet.getString("FinStatus");
				String productCode = resultSet.getString("FinCategory");
				if (StringUtils.isNotBlank(finStatus)) {
					long bucketId = EODProperties.getBucketID(finStatus);
					List<DPDBucketConfiguration> list = EODProperties.getBucketConfigurations(productCode);
					for (DPDBucketConfiguration configuration : list) {
						if (configuration.getBucketID() == bucketId && configuration.getDueDays() > maxDueDays) {
							maxDueDays = configuration.getDueDays();
							custStatus = EODProperties.getBucket(configuration.getBucketID());
							break;
						}
					}
				}
			}

			customerDAO.updateCustStatus(custStatus, date, custId);

		} catch (Exception e) {
			logger.error("Exception: Finreference :" + finreference, e);
			throw new Exception("Exception: Finreference : " + finreference, e);
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
	 * Method for Preparation or Update of OverDue Details data
	 * 
	 * @param financeMain
	 * @param finRepayQueue
	 * @param dateValueDate
	 * @return
	 * @throws Exception
	 */
	private void latePayMarking(FinRepayQueue finRepayQueue, Date dateValueDate) throws Exception {
		logger.debug("Entering");

		boolean isODExist = finODDetailsDAO.isODExist(finRepayQueue.getFinReference(), finRepayQueue.getRpyDate());

		// Finance Overdue Details Save or Updation
		if (isODExist) {
			updateODDetails(finRepayQueue, dateValueDate);
		} else {
			createODDetails(finRepayQueue, dateValueDate);
		}

		logger.debug("Leaving");
	}

	/**
	 * Method for Preparing OverDue Details
	 * 
	 * @param finRepayQueue
	 * @param valueDate
	 * @return
	 */
	private void createODDetails(FinRepayQueue finRepayQueue, Date valueDate) {
		logger.debug(" Entering ");

		if (finRepayQueue.getSchdPftBal().compareTo(BigDecimal.ZERO) <= 0
				&& finRepayQueue.getSchdPftBal().compareTo(BigDecimal.ZERO) <= 0) {
			return;
		}

		FinODDetails finODDetails = new FinODDetails();
		finODDetails.setFinReference(finRepayQueue.getFinReference());
		finODDetails.setFinODSchdDate(finRepayQueue.getRpyDate());
		finODDetails.setFinODFor(finRepayQueue.getFinRpyFor());
		finODDetails.setFinBranch(finRepayQueue.getBranch());
		finODDetails.setFinType(finRepayQueue.getFinType());
		finODDetails.setCustID(finRepayQueue.getCustomerID());
		finODDetails.setFinODTillDate(valueDate);
		finODDetails.setFinCurODAmt(finRepayQueue.getSchdPftBal().add(finRepayQueue.getSchdPftBal()));
		finODDetails.setFinCurODPri(finRepayQueue.getSchdPriBal());
		finODDetails.setFinCurODPft(finRepayQueue.getSchdPftBal());
		finODDetails.setFinMaxODAmt(finODDetails.getFinCurODPri());
		finODDetails.setFinMaxODPri(finODDetails.getFinCurODPri());
		finODDetails.setFinMaxODPri(finODDetails.getFinCurODPft());
		finODDetails.setFinCurODDays(DateUtility.getDaysBetween(finODDetails.getFinODSchdDate(), valueDate));
		finODDetails.setFinLMdfDate(valueDate);

		finODDetailsDAO.save(finODDetails);
	}

	/**
	 * Method for Preparing OverDue Details
	 * 
	 * @param details
	 * @param finRepayQueue
	 * @param valueDate
	 * @param increment
	 * @return
	 */
	private void updateODDetails(FinRepayQueue finRepayQueue, Date valueDate) {
		FinODDetails finODDetails = new FinODDetails();
		finODDetails.setFinCurODAmt(finRepayQueue.getSchdPftBal().add(finRepayQueue.getSchdPftBal()));
		finODDetails.setFinCurODPri(finRepayQueue.getSchdPriBal());
		finODDetails.setFinCurODPft(finRepayQueue.getSchdPftBal());
		finODDetails.setFinODTillDate(valueDate);
		finODDetails.setFinCurODDays(DateUtility.getDaysBetween(finODDetails.getFinODSchdDate(), valueDate));
		finODDetails.setFinLMdfDate(valueDate);
		finODDetailsDAO.updateBatch(finODDetails);

	}

	private void sortBucketConfig(List<DPDBucketConfiguration> list) {

		if (list != null && !list.isEmpty()) {
			Collections.sort(list, new Comparator<DPDBucketConfiguration>() {
				@Override
				public int compare(DPDBucketConfiguration detail1, DPDBucketConfiguration detail2) {
					return detail1.getDueDays() - detail2.getDueDays();
				}
			});
		}

	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public void setFinODDetailsDAO(FinODDetailsDAO finODDetailsDAO) {
		this.finODDetailsDAO = finODDetailsDAO;
	}

	public void setCustomerDAO(CustomerDAO customerDAO) {
		this.customerDAO = customerDAO;
	}

	public void setFinStatusDetailDAO(FinStatusDetailDAO finStatusDetailDAO) {
		this.finStatusDetailDAO = finStatusDetailDAO;
	}
}
