package com.pennant.mq.process.impl;

import org.apache.log4j.Logger;
import org.jaxen.JaxenException;

import com.pennant.coreinterface.model.handlinginstructions.HandlingInstruction;
import com.pennant.coreinterface.process.FinanceMaintenanceProcess;
import com.pennant.exception.PFFInterfaceException;
import com.pennant.mq.processutil.HandlingInstructionProcess;
import com.pennant.mq.util.InterfaceMasterConfigUtil;

public class FinanceMaintenanceProcessImpl implements FinanceMaintenanceProcess {

	private final static Logger logger = Logger.getLogger(FinanceMaintenanceProcessImpl.class);

	public FinanceMaintenanceProcessImpl() {
		super();
	}

	private HandlingInstructionProcess handlingInstructionProcess;

	/**
	 * Method for sending FinanceMaintenance handling instruction to ICCS interface
	 * 
	 * @param handlingInstruction
	 * @return HandlingInstruction
	 * @throws PFFInterfaceException
	 */
	@Override
	public HandlingInstruction sendHandlingInstruction(HandlingInstruction handlingInstruction)
			throws PFFInterfaceException {
		logger.debug("Entering");

		HandlingInstruction handlInstResponse = null;
		try {
			handlInstResponse = getHandlingInstructionProcess().sendHandlingInstruction(handlingInstruction, 
					InterfaceMasterConfigUtil.FINANCE_MAINTENANCE);
		} catch (JaxenException jax) {
			logger.error("Exception: ", jax);
			throw new PFFInterfaceException("PTI7001", jax.getMessage());
		}

		logger.debug("Leaving");
		return handlInstResponse;
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public HandlingInstructionProcess getHandlingInstructionProcess() {
		return handlingInstructionProcess;
	}

	public void setHandlingInstructionProcess(
			HandlingInstructionProcess handlingInstructionProcess) {
		this.handlingInstructionProcess = handlingInstructionProcess;
	}

}
