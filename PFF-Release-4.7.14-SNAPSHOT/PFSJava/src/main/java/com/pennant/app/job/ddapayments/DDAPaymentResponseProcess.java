package com.pennant.app.job.ddapayments;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.StatefulJob;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.pennant.backend.dao.ddapayments.DDAPaymentResponseDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.model.ddapayments.DDAPayments;

public class DDAPaymentResponseProcess extends QuartzJobBean implements StatefulJob, Serializable {

	private static final long serialVersionUID = 5973408474522551573L;

	private static final Logger logger = Logger.getLogger(DDAPaymentResponseProcess.class);

	public DDAPaymentResponseProcess() {
		super();
	}

	private FinanceMainDAO financeMainDAO;
	private DDAPaymentResponseDAO ddaPaymentResponseDAO;

	@Override
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
		logger.debug("Entering");

		logger.debug("---------------------------------------------------------------");
		logger.debug("DDA Payment Response Job started at:"+System.currentTimeMillis());
		logger.debug("---------------------------------------------------------------");

		/**
		 * At scheduled time PFF will take the Backup from DDS_PFF_DD503 table <br>
		 * 
		 * And clear the DDS_T24_DD503 table
		 * 
		 */

		// fetch DDA Payment Response details from DDS_PFF_DD503 table
		List<DDAPayments> ddaPaymentResList = getDdaPaymentResponseDAO().getDDAPaymentResDetails();

		if(ddaPaymentResList != null && !ddaPaymentResList.isEmpty()) {
			for(DDAPayments ddaPayments: ddaPaymentResList) {

				String[] t24Data = null;
				if(!StringUtils.isBlank(ddaPayments.getT24Data())) {
					t24Data = ddaPayments.getT24Data().split(";");
				}

				// set DDA Payment Response details into object
				ddaPayments.setFinReference(t24Data[0]);
				ddaPayments.setRepayAccountId(t24Data[1]);
				//ddaPayments.setCBSuspenseAccNo(t24Data[2]);
				ddaPayments.setFinRepaymentAmount(new BigDecimal(t24Data[3]));
				ddaPayments.setStatus(t24Data[5]);
				ddaPayments.setReason(t24Data[6]);
			}
			// Save the DDA payments' responses for which status is "PAY" into log table
			getDdaPaymentResponseDAO().logDDAPaymentResDetails(ddaPaymentResList);

		}

		// Clear the DDA Payment details from table
		getDdaPaymentResponseDAO().deleteDDAPaymentResDetails();

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

	public DDAPaymentResponseDAO getDdaPaymentResponseDAO() {
		return ddaPaymentResponseDAO;
	}

	public void setDdaPaymentResponseDAO(DDAPaymentResponseDAO ddaPaymentResponseDAO) {
		this.ddaPaymentResponseDAO = ddaPaymentResponseDAO;
	}

}

