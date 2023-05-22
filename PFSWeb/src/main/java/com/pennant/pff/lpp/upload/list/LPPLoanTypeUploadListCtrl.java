package com.pennant.pff.lpp.upload.list;

import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Window;

import com.pennant.pff.upload.list.AUploadListCtrl;
import com.pennant.pff.upload.service.UploadService;
import com.pennanttech.pff.file.UploadTypes;

public class LPPLoanTypeUploadListCtrl extends AUploadListCtrl {
	private static final long serialVersionUID = -256791887358524752L;
	protected Window window;

	protected LPPLoanTypeUploadListCtrl(@Autowired UploadService lPPUploadService) {
		super(lPPUploadService, UploadTypes.LPP_LOAN_TYPE);
	}

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		this.window = (Window) comp;
		super.doAfterCompose(comp);
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "LPPLoanTypeUploadHeader";
		super.onCreate(getArgument("stage"), this.window);
	}
}
