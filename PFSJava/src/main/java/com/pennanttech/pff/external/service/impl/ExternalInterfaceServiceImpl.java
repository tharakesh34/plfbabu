package com.pennanttech.pff.external.service.impl;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.external.MandateProcesses;
import com.pennanttech.pff.external.mandate.dao.MandateProcessDAO;
import com.pennanttech.pff.external.service.ExternalInterfaceService;
import com.pennanttech.pff.model.mandate.MandateData;

public class ExternalInterfaceServiceImpl implements ExternalInterfaceService {
	private static final Logger logger = Logger.getLogger(ExternalInterfaceServiceImpl.class);
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
			getMandateProcess().sendReqest(mandateData);
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public synchronized void processAutoMandateRequest() {
		logger.debug(Literal.ENTERING);
		List<String> entityCodes = mandateProcessdao.getEntityCodes();
		for (String entityCode : entityCodes) {
			List<Long> mandateList = mandateProcessdao.getMandateList(entityCode);
			if (CollectionUtils.isNotEmpty(mandateList)) {
				MandateData mandateData = new MandateData();
				mandateData.setMandateIdList(mandateList);
				mandateData.setEntity(entityCode);
				processMandateRequest(mandateData);
			}
		}
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
