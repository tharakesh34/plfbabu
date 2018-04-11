package com.pennant.backend.endofday.tasklet;

import java.util.Date;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import com.pennant.app.util.DateUtility;
import com.pennant.eod.dao.CustomerGroupQueuingDAO;

public class PrepareCustomerGroupQueue implements Tasklet {
	private Logger					logger	= Logger.getLogger(PrepareCustomerGroupQueue.class);

	@SuppressWarnings("unused")
	private DataSource			dataSource;
	private CustomerGroupQueuingDAO	customerGroupQueuingDAO;

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext context) throws Exception {
		Date valueDate = DateUtility.getAppValueDate();
		logger.debug("START: Prepare Customer Groups On : " + valueDate);
		
		// delete the CustomerGroupQueuing data
		this.customerGroupQueuingDAO.delete();

		// insert the CustomerGroupQueuing data
		this.customerGroupQueuingDAO.prepareCustomerGroupQueue();

		logger.debug("COMPLETE: Prepare Customer Groups On :" + valueDate);

		return RepeatStatus.FINISHED;
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public void setCustomerGroupQueuingDAO(CustomerGroupQueuingDAO customerGroupQueuingDAO) {
		this.customerGroupQueuingDAO = customerGroupQueuingDAO;
	}
}
