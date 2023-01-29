package com.pennant.pff.lpp.upload.list;

import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.zul.Window;

import com.pennant.pff.upload.list.AUploadListCtrl;
import com.pennant.pff.upload.service.UploadService;
import com.pennanttech.pff.file.UploadTypes;

public class LPPUploadListCtrl extends AUploadListCtrl {
	private static final long serialVersionUID = -256791887358524752L;

	protected Window lppUploadListWindow;

	protected LPPUploadListCtrl(@Autowired UploadService lPPUploadService) {
		super(lPPUploadService, UploadTypes.LPP);
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "LPPUploadHeader";
		super.onCreate(getArgument("stage"), this.lppUploadListWindow);
	}
}
