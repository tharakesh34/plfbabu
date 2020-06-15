package com.pennanttech.pff.batch.backend.service.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.batch.backend.dao.BatchProcessStatusDAO;
import com.pennanttech.pff.batch.backend.service.BatchProcessStatusService;
import com.pennanttech.pff.batch.model.BatchProcessStatus;

public class BatchProcessStatusServiceImpl implements BatchProcessStatusService {
	private static Logger logger = LogManager.getLogger(BatchProcessStatusServiceImpl.class);

	private BatchProcessStatusDAO bpsDAO;

	public void setBpsDAO(BatchProcessStatusDAO bpsDAO) {
		this.bpsDAO = bpsDAO;
	}

	@Override
	public BatchProcessStatus getBatchStatus(BatchProcessStatus batchProcessStatus) {
		logger.debug(Literal.ENTERING);
		return bpsDAO.getBatchStatus(batchProcessStatus);
	}

	@Override
	public void saveBatchStatus(BatchProcessStatus batchProcessStatus) {
		logger.debug(Literal.ENTERING);
		bpsDAO.saveBatchStatus(batchProcessStatus);
		logger.debug(Literal.LEAVING);
	}

	@Override
	public void updateBatchStatus(BatchProcessStatus batchProcessStatus) {
		logger.debug(Literal.ENTERING);
		bpsDAO.updateBatchStatus(batchProcessStatus);
		logger.debug(Literal.LEAVING);
	}

}
