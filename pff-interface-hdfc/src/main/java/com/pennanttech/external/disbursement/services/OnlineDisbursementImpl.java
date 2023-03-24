package com.pennanttech.external.disbursement.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.pennanttech.dataengine.model.DataEngineStatus;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.disbursement.model.DisbursementRequest;
import com.pennanttech.pff.external.disbursement.OnlineDisbursement;

@Component(value = "onlineDisbursement")
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class OnlineDisbursementImpl implements OnlineDisbursement {
	private static Logger logger = LogManager.getLogger(OnlineDisbursementImpl.class);

	@Override
	public DataEngineStatus processRequest(DisbursementRequest request) {
		logger.debug(Literal.ENTERING);

		DataEngineStatus status = new DataEngineStatus();

		status.setStatus("S");

		logger.debug(Literal.ENTERING);
		return status;
	}
}
