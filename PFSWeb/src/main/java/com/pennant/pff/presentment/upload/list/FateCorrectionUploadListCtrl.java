package com.pennant.pff.presentment.upload.list;

import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Window;

import com.pennant.pff.upload.list.AUploadListCtrl;
import com.pennant.pff.upload.service.UploadService;
import com.pennanttech.pff.file.UploadTypes;

public class FateCorrectionUploadListCtrl extends AUploadListCtrl {
	private static final long serialVersionUID = 2L;

	protected Window window;

	protected FateCorrectionUploadListCtrl(@Autowired UploadService fateCorrectionUploadService) {
		super(fateCorrectionUploadService, UploadTypes.FATE_CORRECTION);
	}

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		this.window = (Window) comp;
		super.doAfterCompose(comp);
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "FateCorrection";
		super.onCreate(getArgument("stage"), this.window);
	}

}
