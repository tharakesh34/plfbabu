package com.pennant.pff.manualknockoff.upload.list;

import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.zul.Window;

import com.pennant.pff.upload.list.AUploadListCtrl;
import com.pennant.pff.upload.service.UploadService;
import com.pennanttech.pff.file.UploadTypes;

public class ManualKnockOffUploadListCtrl extends AUploadListCtrl {
	private static final long serialVersionUID = 2L;

	protected Window manualKnockOffUploadWindow;

	protected ManualKnockOffUploadListCtrl(@Autowired UploadService manualKnockOffUploadServiceImpl) {
		super(manualKnockOffUploadServiceImpl, UploadTypes.MANUAL_KNOCKOFF);
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "ManualKnockOff";
		super.onCreate(getArgument("stage"), this.manualKnockOffUploadWindow);
	}
}
