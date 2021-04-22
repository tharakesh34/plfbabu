package com.pennant.backend.service.handlinstruction;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.util.resource.Labels;

import com.pennant.backend.dao.lmtmasters.FinanceReferenceDetailDAO;
import com.pennant.backend.model.finance.FinCollaterals;
import com.pennant.backend.util.PennantConstants;
import com.pennant.constants.InterfaceConstants;
import com.pennant.coreinterface.model.handlinginstructions.HandlingInstruction;
import com.pennant.coreinterface.process.FinanceMaintenanceProcess;
import com.pennanttech.pennapps.core.InterfaceException;

public class HandlingInstructionService {

	private static final Logger logger = LogManager.getLogger(HandlingInstructionService.class);

	private FinanceMaintenanceProcess financeMaintenanceProcess;
	private FinanceReferenceDetailDAO financeReferenceDetailDAO;

	/**
	 * Method for send Handling Instruction to ICCS interface and save the request and response details
	 * 
	 * @param handlingInstruction
	 * @throws InterfaceException
	 */
	public void sendFinanceMaintenanceRequest(HandlingInstruction handlingInstruction) throws InterfaceException {
		logger.debug("Entering");

		// Validate Security cheque is taken as collateral or not
		String finReference = handlingInstruction.getFinanceRef();
		String collateralType = PennantConstants.SECURITY_CHEQUE;

		FinCollaterals finCollaterals = getFinanceReferenceDetailDAO().getFinCollaterals(finReference, collateralType);

		if (finCollaterals != null) {

			// Send Handling instruction request to ICCS interface
			if (financeMaintenanceProcess != null) {
				HandlingInstruction handlInstResponse = financeMaintenanceProcess
						.sendHandlingInstruction(handlingInstruction);

				if (handlInstResponse == null) {
					throw new InterfaceException("PTI7001", Labels.getLabel("FAILED_HANDLINST"));
				}
				if (StringUtils.equals(handlInstResponse.getReturnCode(), InterfaceConstants.SUCCESS_CODE)) {

					handlingInstruction.setReferenceNum(handlInstResponse.getReferenceNum());
					handlingInstruction.setReturnCode(handlInstResponse.getReturnCode());
					handlingInstruction.setReturnText(handlInstResponse.getReturnText());

					// Save Request and Response details
					saveHandlingInstructionLogDetails(handlingInstruction);
				} else {
					throw new InterfaceException(handlInstResponse.getReturnCode(),
							handlingInstruction.getReturnText());
				}

			}

			logger.debug("Leaving");
		}
	}

	/**
	 * Method to save the handling Instruction log details
	 * 
	 * @param handlingInstruction
	 */
	private void saveHandlingInstructionLogDetails(HandlingInstruction handlingInstruction) {
		logger.debug("Entering");

		getFinanceReferenceDetailDAO().saveHandlInstructionDetails(handlingInstruction);

		logger.debug("Leaving");
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	/*
	 * public FinanceMaintenanceProcess getFinanceMaintenanceProcess() { return financeMaintenanceProcess; }
	 */
	@Autowired(required = false)
	public void setFinanceMaintenanceProcess(FinanceMaintenanceProcess financeMaintenanceProcess) {
		this.financeMaintenanceProcess = financeMaintenanceProcess;
	}

	public FinanceReferenceDetailDAO getFinanceReferenceDetailDAO() {
		return financeReferenceDetailDAO;
	}

	public void setFinanceReferenceDetailDAO(FinanceReferenceDetailDAO financeReferenceDetailDAO) {
		this.financeReferenceDetailDAO = financeReferenceDetailDAO;
	}
}
