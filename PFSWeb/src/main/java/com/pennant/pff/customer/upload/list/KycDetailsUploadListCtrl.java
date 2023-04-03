package com.pennant.pff.customer.upload.list;

import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Window;

import com.pennant.pff.upload.list.AUploadListCtrl;
import com.pennant.pff.upload.service.UploadService;
import com.pennanttech.pff.file.UploadTypes;

public class KycDetailsUploadListCtrl extends AUploadListCtrl {
	private static final long serialVersionUID = 1L;
	protected Window window;

	protected KycDetailsUploadListCtrl(@Autowired UploadService kycDetailsUploadService) {
		super(kycDetailsUploadService, UploadTypes.CUSTOMER_KYC_DETAILS);
	}

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		this.window = (Window) comp;
		super.doAfterCompose(comp);
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "KycDetailsUploadHeader";
		super.onCreate(getArgument("stage"), this.window);
	}

}
