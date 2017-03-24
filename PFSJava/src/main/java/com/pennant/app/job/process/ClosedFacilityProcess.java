package com.pennant.app.job.process;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.StatefulJob;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.pennant.Interface.service.CustomerLimitIntefaceService;
import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.limits.ClosedFacilityDetail;

public class ClosedFacilityProcess extends QuartzJobBean implements StatefulJob, Serializable {

	private static final long serialVersionUID = 5973408474522551573L;

	private static final Logger logger = Logger.getLogger(ClosedFacilityProcess.class);

	public ClosedFacilityProcess() {
		super();
	}

	private CustomerLimitIntefaceService custLimitIntefaceService;

	@Override
	protected void executeInternal(JobExecutionContext context)throws JobExecutionException {
		logger.debug("Entering");

		logger.debug("---------------------------------------------------------------");
		logger.debug("PROCESSED CLOSED FACILITY Response Job started at:"+System.currentTimeMillis());
		logger.debug("---------------------------------------------------------------");

		// FetchClosed facility details from INTER.PFF_CLOSED_LIMITS table in AXE_CREDIT schema
		List<ClosedFacilityDetail> clFacilityList = getCustLimitIntefaceService().fetchClosedFacilityDetails();

		// process the closed facility details
		List<ClosedFacilityDetail> proClFacilityList = new ArrayList<ClosedFacilityDetail>();
		for(ClosedFacilityDetail detail:clFacilityList) {
			detail.setProcessed(true);
			detail.setProcessedDate(DateUtility.getAppDate());

			//add processed into new list to save
			proClFacilityList.add(detail);
		}

		// Save the CLosed facility details into PFF
		boolean status = getCustLimitIntefaceService().saveClosedFacilityDetails(proClFacilityList);

		// Update the processed and processedDate flags in AXE_CREDIT schema
		if(status) {
			getCustLimitIntefaceService().updateClosedFacilityStatus(proClFacilityList);
		}
		
		logger.debug("Leaving");

		logger.debug("---------------------------------------------------------------");
		logger.debug("PROCESSED CLOSED FACILITY Response Job ended at:"+System.currentTimeMillis());
		logger.debug("---------------------------------------------------------------");
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public CustomerLimitIntefaceService getCustLimitIntefaceService() {
		return custLimitIntefaceService;
	}

	public void setCustLimitIntefaceService(CustomerLimitIntefaceService custLimitIntefaceService) {
		this.custLimitIntefaceService = custLimitIntefaceService;
	}
}
