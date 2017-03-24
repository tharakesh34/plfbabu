package com.pennant.app.job.ddapayments;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.StatefulJob;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.pennant.app.util.DateUtility;
import com.pennant.backend.dao.ddapayments.DDAPaymentInitiateDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.FinanceMainExtDAO;
import com.pennant.backend.model.ddapayments.DDAPayments;
import com.pennant.backend.util.FinanceConstants;

public class DDAPaymentInitiationProcess  extends QuartzJobBean implements StatefulJob, Serializable {

	private static final long serialVersionUID = 809983809253088428L;
	private static final Logger logger = Logger.getLogger(DDAPaymentInitiationProcess.class);

	public DDAPaymentInitiationProcess() {
		super();
	}

	private FinanceMainDAO financeMainDAO;
	private FinanceMainExtDAO financeMainExtDAO;
	private DDAPaymentInitiateDAO ddaPaymentInitiateDAO;

	public static final String dateFormat2 = "yyyymmdd";
	public static final String dateFormat3 = "dd-MM-yyyy";

	@Override
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
		logger.debug("Entering");

		logger.debug("-----------------------------------------------------------------");
		logger.debug("DDA Payment Initiation Job started at:"+System.currentTimeMillis());
		logger.debug("-----------------------------------------------------------------");

		Date appDate = DateUtility.getAppDate();

		/**
		 * At scheduled time PFF will take the Backup from DDS_PFF_DD500 table <br>
		 * 
		 * And inserts the DDRs that need to be collected for that day
		 * 
		 */

		// Fetch Previous day DDA payment initiation details 
		List<DDAPayments> backUpDDAPaymentList = getDdaPaymentInitiateDAO().fetchDDAInitDetails();

		if(backUpDDAPaymentList != null && !backUpDDAPaymentList.isEmpty()) {

			// Copy the DDS_PFF_DD500 data into Log Table
			getDdaPaymentInitiateDAO().logDDAPaymentInitDetails(backUpDDAPaymentList);
		}

		// Clear the DDA Payment details from table
		getDdaPaymentInitiateDAO().deleteDDAPaymentInitDetails();

		// fetch DDA Payment Initiation details from PFF by appdate
		List<DDAPayments> finDDAPaymentList = getFinanceMainDAO().getDDAPaymentsList(FinanceConstants.REPAYMTH_AUTODDA, appDate);

		if(finDDAPaymentList != null && !finDDAPaymentList.isEmpty()) {
			for(DDAPayments ddaPayments: finDDAPaymentList) {
				
				// Fetch Repayment Account IBAN number
				String repayIBAN = getFinanceMainExtDAO().getRepayIBAN(ddaPayments.getFinReference());
				
				ddaPayments.setdDARefNo(ddaPayments.getdDAReferenceNo()+"-"+DateUtility.format(appDate, dateFormat2));
				ddaPayments.setpFFData(ddaPayments.getCustCIF()
						+";"+ ddaPayments.getFinReference() 
						+";"+ ddaPayments.getFinReference()
						+";"+ ddaPayments.getFinRepaymentAmount()
						+";"+ DateUtility.format(ddaPayments.getSchDate(), dateFormat3)
						+";"+ ddaPayments.getdDAReferenceNo() 
						+";"+ repayIBAN);

				// Save DDA Payment Details into DDS_PFF_DD500 table
				try {
					getDdaPaymentInitiateDAO().saveDDAPaymentInitDetails(ddaPayments);
				} catch(Exception e) {
					logger.error("Exception: ", e); 
				}
			}
		}

		logger.debug("Leaving");
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public FinanceMainDAO getFinanceMainDAO() {
		return financeMainDAO;
	}

	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	public DDAPaymentInitiateDAO getDdaPaymentInitiateDAO() {
		return ddaPaymentInitiateDAO;
	}

	public void setDdaPaymentInitiateDAO(DDAPaymentInitiateDAO ddaPaymentInitiateDAO) {
		this.ddaPaymentInitiateDAO = ddaPaymentInitiateDAO;
	}
	public FinanceMainExtDAO getFinanceMainExtDAO() {
		return financeMainExtDAO;
	}

	public void setFinanceMainExtDAO(FinanceMainExtDAO financeMainExtDAO) {
		this.financeMainExtDAO = financeMainExtDAO;
	}

}
