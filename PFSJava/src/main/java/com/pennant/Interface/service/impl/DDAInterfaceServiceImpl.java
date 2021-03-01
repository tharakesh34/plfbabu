package com.pennant.Interface.service.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.Interface.service.DDAInterfaceService;
import com.pennant.backend.model.finance.DDAProcessData;
import com.pennant.coreinterface.model.dda.DDAAmendment;
import com.pennant.coreinterface.model.dda.DDACancellation;
import com.pennant.coreinterface.model.dda.DDARegistration;
import com.pennant.coreinterface.model.dda.DDAUpdate;
import com.pennant.coreinterface.process.DDAProcess;
import com.pennanttech.pennapps.core.InterfaceException;

public class DDAInterfaceServiceImpl implements DDAInterfaceService {

	private static final Logger logger = LogManager.getLogger(DDAInterfaceServiceImpl.class);

	public DDAInterfaceServiceImpl() {

	}

	private DDAProcess ddaProcess;

	/**
	 * Method for send DDA Registration Request to Interface
	 * 
	 * @param ddaRegistration
	 * @return DDARegistration
	 */
	@Override
	public DDAProcessData sendDDARegistrationReq(DDAProcessData ddaProcessRequest) throws InterfaceException {
		logger.debug("Entering");

		DDARegistration ddaRegistration = new DDARegistration();
		BeanUtils.copyProperties(ddaProcessRequest, ddaRegistration);

		// Send DDA Registration request to middleware
		if (ddaProcess != null) {
			ddaRegistration = ddaProcess.sendDDARequest(ddaRegistration);

			BeanUtils.copyProperties(ddaRegistration, ddaProcessRequest);

			logger.debug("Leaving");
			return ddaProcessRequest;
		}
		return null;
	}

	/**
	 * Method for send DDA Amendment Request to Interface
	 * 
	 * @param ddaAmendment
	 * @return DDAAmendment
	 * @throws InterfaceException
	 */
	@Override
	public DDAAmendment sendDDAAmendmentReq(DDAAmendment ddaAmendment) throws InterfaceException {
		logger.debug("Entering");
		logger.debug("Leaving");
		if (ddaProcess != null) {
			return ddaProcess.sendDDAAmendment(ddaAmendment);
		}
		return null;
	}

	/**
	 * Method for send Update DDA Reference Request to Interface
	 * 
	 * @param ddaUpdate
	 * @return DDAUpdate
	 * @throws InterfaceException
	 */
	@Override
	public DDAUpdate sendDDAUpdateReq(DDAUpdate ddaUpdate) throws InterfaceException {
		logger.debug("Entering");
		logger.debug("Leaving");
		if (ddaProcess != null) {
			return ddaProcess.sendDDAUpdate(ddaUpdate);
		}
		return null;
	}

	/**
	 * Method for send Cancel DDA Registration request to Interface
	 * 
	 * @param ddaUpdate
	 * @return DDAUpdate
	 * @throws InterfaceException
	 */
	@Override
	public DDAProcessData cancelDDARegistration(DDAProcessData ddaProcessData) throws InterfaceException {
		logger.debug("Entering");

		DDACancellation ddaCancellation = new DDACancellation();
		ddaCancellation.setIsNumber(ddaProcessData.getFinRefence());
		ddaCancellation.setDdaReferenceNo(ddaProcessData.getDdaReference());
		ddaCancellation.setDdaRegFormName(ddaProcessData.getDdaRegFormName());
		ddaCancellation.setDdaRegFormData(ddaProcessData.getDdaRegFormData());

		// Send DDA.CANCELLATION Request to interface
		if (ddaProcess != null) {
			ddaCancellation = ddaProcess.cancelDDARegistration(ddaCancellation);

			if (ddaCancellation != null) {
				ddaProcessData.setReferenceNum(ddaCancellation.getReferenceNum());
				ddaProcessData.setReturnCode(ddaCancellation.getReturnCode());
				ddaProcessData.setReturnText(ddaCancellation.getReturnText());
			}
			logger.debug("Leaving");

			return ddaProcessData;
		}
		return null;
	}
	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	/*
	 * public DDAProcess getDdaProcess() { return ddaProcess; }
	 */
	@Autowired(required = false)
	public void setDdaProcess(DDAProcess ddaProcess) {
		this.ddaProcess = ddaProcess;
	}
}
