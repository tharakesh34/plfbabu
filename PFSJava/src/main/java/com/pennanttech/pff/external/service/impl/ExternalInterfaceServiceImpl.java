package com.pennanttech.pff.external.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.pennant.app.constants.ImplementationConstants;
import com.pennanttech.dataengine.model.DataEngineStatus;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.external.MandateProcesses;
import com.pennanttech.pff.external.mandate.dao.MandateProcessDAO;
import com.pennanttech.pff.external.service.ExternalInterfaceService;
import com.pennanttech.pff.model.mandate.MandateData;

public class ExternalInterfaceServiceImpl implements ExternalInterfaceService {
	private static final Logger logger = LogManager.getLogger(ExternalInterfaceServiceImpl.class);
	private MandateProcesses mandateProcesses;
	private MandateProcesses defaultMandateProcess;
	private MandateProcessDAO mandateProcessdao;

	@Override
	public synchronized void processMandateRequest(MandateData mandateData) {
		logger.debug(Literal.ENTERING);

		List<Long> selectedMandateIds = mandateData.getMandateIdList();

		Long process_Id = mandateProcessdao.saveMandateRequests(selectedMandateIds);

		if (process_Id != null) {
			mandateData.setProcess_Id(process_Id);
			DataEngineStatus status = getMandateProcess().sendReqest(mandateData);

			if (status != null) {
				if (status.getStatus().equals("F")) {
					mandateProcessdao.deleteMandateRequests(selectedMandateIds);
					mandateProcessdao.deleteMandateStatus(selectedMandateIds);
				}
			}
		}

		logger.debug(Literal.LEAVING);
	}
	
	@Override
	public synchronized void processAutoMandateRequest() {
		logger.info(Literal.ENTERING);
		
		List<Long> mandates = new ArrayList<>();
		List<String> entityCodes = mandateProcessdao.getEntityCodes();
		
		for (String entityCode : entityCodes) {
			if (ImplementationConstants.MANDATE_PTNRBNK_IN_DWNLD) {
				List<String> partnerBankCodes = mandateProcessdao.getPartnerBankCodeByEntity(entityCode);
				for (String partnerBankCode : partnerBankCodes) {
					mandates = mandateProcessdao.getMandateList(entityCode, partnerBankCode);
					doMandateProcess(entityCode, mandates);
				}
			} else {
				mandates = mandateProcessdao.getMandateList(entityCode);
				doMandateProcess(entityCode, mandates);
			}
		}
		
		logger.debug(Literal.LEAVING);
	}

	private void doMandateProcess(String entityCode, List<Long> mandates) {
		logger.debug(Literal.ENTERING);
		MandateData md = new MandateData();
		md.setEntity(entityCode);
		md.setMandateIdList(mandates);
		processMandateRequest(md);
		logger.debug(Literal.LEAVING);
	}

	private MandateProcesses getMandateProcess() {
		return mandateProcesses == null ? defaultMandateProcess : mandateProcesses;
	}

	@Autowired(required = false)
	@Qualifier(value = "mandateProcesses")
	public void setMandateProces(MandateProcesses mandateProcesses) {
		this.mandateProcesses = mandateProcesses;
	}

	@Autowired
	public void setDefaultMandateProcess(MandateProcesses defaultMandateProcess) {
		this.defaultMandateProcess = defaultMandateProcess;
	}

	@Autowired
	public void setMandateProcessdao(MandateProcessDAO mandateProcessdao) {
		this.mandateProcessdao = mandateProcessdao;
	}

	public MandateProcessDAO getMandateProcessdao() {
		return mandateProcessdao;
	}

	public MandateProcesses getMandateProcesses() {
		return mandateProcesses;
	}

	public void setMandateProcesses(MandateProcesses mandateProcesses) {
		this.mandateProcesses = mandateProcesses;
	}
}
