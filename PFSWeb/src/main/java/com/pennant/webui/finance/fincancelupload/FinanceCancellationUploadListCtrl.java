package com.pennant.webui.finance.fincancelupload;

import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Window;

import com.pennant.pff.upload.list.AUploadListCtrl;
import com.pennant.pff.upload.service.UploadService;
import com.pennanttech.pff.file.UploadTypes;

public class FinanceCancellationUploadListCtrl extends AUploadListCtrl {

	private static final long serialVersionUID = -2963600927230407346L;

	protected Window window;

	protected FinanceCancellationUploadListCtrl(@Autowired UploadService financeCancellationUploadService) {
		super(financeCancellationUploadService, UploadTypes.LOAN_CANCEL);
	}

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		this.window = (Window) comp;
		super.doAfterCompose(comp);
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "LoanCancelUploadHeader";
		super.onCreate(getArgument("stage"), this.window);
	}
}
