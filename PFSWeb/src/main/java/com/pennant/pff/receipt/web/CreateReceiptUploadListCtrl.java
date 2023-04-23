package com.pennant.pff.receipt.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Window;

import com.pennant.pff.upload.list.AUploadListCtrl;
import com.pennant.pff.upload.service.UploadService;
import com.pennanttech.pff.file.UploadTypes;

public class CreateReceiptUploadListCtrl extends AUploadListCtrl {
	private static final long serialVersionUID = 1L;

	protected CreateReceiptUploadListCtrl(@Autowired UploadService createReceiptUploadService) {
		super(createReceiptUploadService, UploadTypes.CREATE_RECEIPT);
	}

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		this.window = (Window) comp;
		super.doAfterCompose(comp);
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "CreateReceiptUploadHeader";
		super.onCreate(getArgument("stage"), this.window);
	}

}
