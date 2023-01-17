package com.pennant.backend.endofday.tasklet;

import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.backend.dao.limit.LimitStructureDAO;
import com.pennant.backend.util.BatchUtil;
import com.pennant.eod.dao.CustomerGroupQueuingDAO;
import com.pennant.pff.batch.job.dao.BatchJobQueueDAO;
import com.pennanttech.pff.eod.EODUtil;
import com.pennanttech.pff.eod.step.StepUtil;

public class LimitsUpdate implements Tasklet {
	private Logger logger = LogManager.getLogger(LimitsUpdate.class);

	private LimitStructureDAO limitStructureDAO;
	private BatchJobQueueDAO eodCustomerQueueDAO;
	private CustomerGroupQueuingDAO customerGroupQueuingDAO;

	public LimitsUpdate() {
		super();
	}

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext context) throws Exception {
		Date valueDate = EODUtil.getDate("APP_VALUEDATE", context);

		logger.info("START Update Limits On  {}", valueDate);

		BatchUtil.setExecutionStatus(context, StepUtil.CUSTOMER_LIMITS_UPDATE);

		StepUtil.CUSTOMER_LIMITS_UPDATE.setTotalRecords(1);

		/* Update the Rebuild flag as true in LimitStructure */
		this.limitStructureDAO.updateReBuildField("", "", false, "");

		/* Insert the CustomerQueuing table data into CustomerQueuing_Log table */
		this.eodCustomerQueueDAO.logQueue();

		/* Delete the CustomerQueuing Data */
		this.eodCustomerQueueDAO.clearQueue();

		/* Move the CustomerGroupQueing to log */
		this.customerGroupQueuingDAO.logCustomerGroupQueuing();

		/* Delete the CustomerGroupQueuing data */
		this.customerGroupQueuingDAO.delete();

		logger.info("COMPLETE Update Limits On  {}", valueDate);

		StepUtil.CUSTOMER_LIMITS_UPDATE.setProcessedRecords(1);

		return RepeatStatus.FINISHED;
	}

	@Autowired
	public void setLimitStructureDAO(LimitStructureDAO limitStructureDAO) {
		this.limitStructureDAO = limitStructureDAO;
	}

	@Autowired
	public void setEodCustomerQueueDAO(BatchJobQueueDAO eodCustomerQueueDAO) {
		this.eodCustomerQueueDAO = eodCustomerQueueDAO;
	}

	@Autowired
	public void setCustomerGroupQueuingDAO(CustomerGroupQueuingDAO customerGroupQueuingDAO) {
		this.customerGroupQueuingDAO = customerGroupQueuingDAO;
	}

}
