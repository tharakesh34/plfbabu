package com.pennant.backend.service.batchProcessStatusImpl;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.backend.dao.batchProcessStatus.BatchProcessStatusDAO;
import com.pennant.backend.service.batchProcessStatus.BatchProcessStatusService;

public class BatchProcessStatusServiceImpl implements BatchProcessStatusService {

	@Autowired
	private BatchProcessStatusDAO batchProcessStatusDAO;

	@Override
	public String getBatchStatus(String batchName) {
		return batchProcessStatusDAO.getBatchStatus(batchName);

	}

	@Override
	public void saveBatchStatus(String batchName, Date startTime, String Status) {
		batchProcessStatusDAO.saveBatchStatus(batchName, startTime, Status);

	}

	@Override
	public void updateBatchStatus(String batchName, Date endTime, String Status) {
		batchProcessStatusDAO.updateBatchStatus(batchName, endTime, Status);

	}

}
