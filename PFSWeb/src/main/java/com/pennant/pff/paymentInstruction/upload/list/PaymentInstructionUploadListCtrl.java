package com.pennant.pff.paymentInstruction.upload.list;

import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Window;

import com.pennant.pff.upload.list.AUploadListCtrl;
import com.pennant.pff.upload.service.UploadService;
import com.pennanttech.pff.file.UploadTypes;

public class PaymentInstructionUploadListCtrl extends AUploadListCtrl {

	private static final long serialVersionUID = -2963600927230407346L;

	protected Window window;

	protected PaymentInstructionUploadListCtrl(@Autowired UploadService paymentInstructionUploadService) {
		super(paymentInstructionUploadService, UploadTypes.PAYMINS);
	}

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		this.window = (Window) comp;
		super.doAfterCompose(comp);
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "PaymentInstructionUploadHeader";
		super.onCreate(getArgument("stage"), this.window);
	}
}
