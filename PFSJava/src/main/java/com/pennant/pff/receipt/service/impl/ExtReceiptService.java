package com.pennant.pff.receipt.service.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pennant.pff.receipt.model.CreateReceiptUpload;
import com.pennanttech.external.ExtReceiptServiceHook;
import com.pennanttech.pennapps.core.resource.Literal;

public class ExtReceiptService implements ExtReceiptServiceHook {
	private static final Logger logger = LogManager.getLogger(ExtReceiptService.class);
	private CreateReceiptUploadServiceImpl createReceiptUploadService;

	@Override
	public void createExtReceipt(CreateReceiptUpload reaceipt, String entityCode) {
		logger.debug(Literal.ENTERING);
		createReceiptUploadService.createExtReceipt(reaceipt, entityCode);
		logger.debug(Literal.LEAVING);
	}

	public void setCreateReceiptUploadService(CreateReceiptUploadServiceImpl createReceiptUploadService) {
		this.createReceiptUploadService = createReceiptUploadService;
	}

}
