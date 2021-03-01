package com.pennant.backend.endofday.tasklet;

import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.backend.util.BatchUtil;
import com.pennant.eod.dao.CustomerGroupQueuingDAO;
import com.pennanttech.pff.eod.EODUtil;
import com.pennanttech.pff.eod.step.StepUtil;

public class PrepareCustomerGroupQueue implements Tasklet {
	private Logger logger = LogManager.getLogger(PrepareCustomerGroupQueue.class);

	private CustomerGroupQueuingDAO customerGroupQueuingDAO;

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext context) throws Exception {
		Date valueDate = EODUtil.getDate("APP_VALUEDATE", context);

		logger.info("START Prepare Customer Groups On {}", valueDate);

		BatchUtil.setExecutionStatus(context, StepUtil.PREPARE_CUSTOMER_GROUP_QUEUE);

		/* Delete the CustomerGroupQueuing data */
		this.customerGroupQueuingDAO.delete();

		/* Insert the CustomerGroupQueuing data */
		int totalRecords = this.customerGroupQueuingDAO.prepareCustomerGroupQueue();

		BatchUtil.setExecutionStatus(context, StepUtil.PREPARE_CUSTOMER_GROUP_QUEUE);
		StepUtil.PREPARE_CUSTOMER_GROUP_QUEUE.setTotalRecords(totalRecords);
		StepUtil.PREPARE_CUSTOMER_GROUP_QUEUE.setProcessedRecords(totalRecords);

		logger.info("COMPLETE Prepare Customer Groups On {}", valueDate);

		return RepeatStatus.FINISHED;
	}

	@Autowired
	public void setCustomerGroupQueuingDAO(CustomerGroupQueuingDAO customerGroupQueuingDAO) {
		this.customerGroupQueuingDAO = customerGroupQueuingDAO;
	}
}
