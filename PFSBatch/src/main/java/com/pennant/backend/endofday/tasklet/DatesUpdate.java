package com.pennant.backend.endofday.tasklet;

import java.util.Date;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import com.pennant.app.core.DateService;
import com.pennant.app.util.DateUtility;
import com.pennant.eod.constants.EodConstants;
import com.pennant.eod.dao.CustomerQueuingDAO;

public class DatesUpdate implements Tasklet {
	private Logger					logger	= Logger.getLogger(DatesUpdate.class);

	@SuppressWarnings("unused")
	private DataSource			dataSource;
	private DateService			dateService;

	private CustomerQueuingDAO	customerQueuingDAO;


	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext context) throws Exception {
		Date valueDate = DateUtility.getAppValueDate();
		logger.debug("START: Update Dates On : " + valueDate);

		//stepExecution.getExecutionContext().put(stepExecution.getId().toString(), valueDate);
		// Log the Customer queuing data and threads status
		customerQueuingDAO.logCustomerQueuing(EodConstants.PROGRESS_SUCCESS);
		//check extended month end and update the dates.
		dateService.doUpdateAftereod(true);

		logger.debug("COMPLETE:  Update Dates On :" + valueDate);
		return RepeatStatus.FINISHED;
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setDateService(DateService dateService) {
		this.dateService = dateService;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public void setCustomerQueuingDAO(CustomerQueuingDAO customerQueuingDAO) {
		this.customerQueuingDAO = customerQueuingDAO;
	}


}
