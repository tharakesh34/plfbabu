package com.pennanttech.external;

import com.pennanttech.pff.presentment.model.PresentmentHeader;

public interface ExternalPresentmentHook {

	public void processPresentmentRequest(PresentmentHeader presentmentHeader);
}
