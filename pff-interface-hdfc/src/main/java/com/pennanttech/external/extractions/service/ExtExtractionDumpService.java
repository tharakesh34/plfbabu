package com.pennanttech.external.extractions.service;

import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pennant.backend.model.finance.CustEODEvent;
import com.pennanttech.external.MicroEodExternalProcessHook;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;

public class ExtExtractionDumpService implements MicroEodExternalProcessHook {
	private static final Logger logger = LogManager.getLogger(ExtExtractionDumpService.class);

	private BaselOneDumpService baselOneDumpService;
	private ALMDumpService almDumpService;

	@Override
	public void saveExtractionData(CustEODEvent custEODEvent, Date appdate) {
		logger.debug(Literal.ENTERING);

		boolean isEOM = false;

		if (appdate.compareTo(DateUtil.getMonthEnd(appdate)) == 0) {
			isEOM = true;
		}

		if (isEOM) {

			if (baselOneDumpService != null) {
				baselOneDumpService.processBaselOne(custEODEvent);
			}

			if (almDumpService != null) {
				almDumpService.processALM(custEODEvent);
			}

		}

		logger.debug(Literal.LEAVING);
	}

	public void setBaselOneDumpService(BaselOneDumpService baselOneDumpService) {
		this.baselOneDumpService = baselOneDumpService;
	}

	public void setAlmDumpService(ALMDumpService almDumpService) {
		this.almDumpService = almDumpService;
	}

}
