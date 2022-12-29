package com.pennant.pff.mandate.upload.list;

import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.zul.Window;

import com.pennant.pff.upload.list.AUploadListCtrl;
import com.pennant.pff.upload.service.UploadService;
import com.pennanttech.pff.file.UploadTypes;

public class MandateUploadListCtrl extends AUploadListCtrl {
	private static final long serialVersionUID = 1L;
	protected Window mandateUploadListWindow;

	protected MandateUploadListCtrl(@Autowired UploadService mandateUploadService) {
		super(mandateUploadService, UploadTypes.MANDATES);
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "MandateUploadHeader";
		super.onCreate(getArgument("stage"), this.mandateUploadListWindow);
	}

}
