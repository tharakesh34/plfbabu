package com.pennanttech.external;

import com.pennant.pff.receipt.model.CreateReceiptUpload;

public interface ExtReceiptServiceHook {
	void createExtReceipt(CreateReceiptUpload reaceipt, String entityCode);
}
