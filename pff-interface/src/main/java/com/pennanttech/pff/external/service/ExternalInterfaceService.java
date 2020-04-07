package com.pennanttech.pff.external.service;

import com.pennanttech.pff.model.mandate.MandateData;

public interface ExternalInterfaceService {
	void processMandateRequest(MandateData mandateData);

	void processAutoMandateRequest();
}
