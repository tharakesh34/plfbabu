package com.pennant.backend.endofday.tasklet;

import java.util.Date;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import com.pennant.app.util.DateUtility;
import com.pennant.backend.dao.limit.LimitStructureDAO;
import com.pennant.eod.dao.CustomerGroupQueuingDAO;
import com.pennant.eod.dao.CustomerQueuingDAO;

public class LimitsUpdate implements Tasklet {
	private Logger					logger	= Logger.getLogger(LimitsUpdate.class);

	@SuppressWarnings("unused")
	private DataSource			dataSource;
	private LimitStructureDAO limitStructureDAO;
	private CustomerQueuingDAO	customerQueuingDAO;
	private CustomerGroupQueuingDAO	customerGroupQueuingDAO;
	
	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext context) throws Exception {
		Date valueDate = DateUtility.getAppValueDate();
		logger.debug("START: Update Limits On : " + valueDate);
		
		//update the Rebuild flag as true in LimitStructure
		this.limitStructureDAO.updateReBuildField("", "", false, "");
		
		//insert the CustomerQueuing table data into CustomerQueuing_Log table 
		this.customerQueuingDAO.logCustomerQueuing();
		
		//delete the CustomerQueuing Data
		this.customerQueuingDAO.delete();
		
		// move the CustomerGroupQueing to log
		this.customerGroupQueuingDAO.logCustomerGroupQueuing();

		// delete the CustomerGroupQueuing data
		this.customerGroupQueuingDAO.delete();
		
		logger.debug("COMPLETE:  Update Limits On :" + valueDate);
		
		return RepeatStatus.FINISHED;
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public void setLimitStructureDAO(LimitStructureDAO limitStructureDAO) {
		this.limitStructureDAO = limitStructureDAO;
	}
	
	public void setCustomerQueuingDAO(CustomerQueuingDAO customerQueuingDAO) {
		this.customerQueuingDAO = customerQueuingDAO;
	}

	public void setCustomerGroupQueuingDAO(CustomerGroupQueuingDAO customerGroupQueuingDAO) {
		this.customerGroupQueuingDAO = customerGroupQueuingDAO;
	}
}
