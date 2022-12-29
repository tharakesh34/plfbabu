package com.pennant.pff.pdc.upload.list;

import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.zul.Window;

import com.pennant.pff.upload.list.AUploadListCtrl;
import com.pennant.pff.upload.service.UploadService;
import com.pennanttech.pff.file.UploadTypes;

public class PDCUploadListCtrl extends AUploadListCtrl {
	private static final long serialVersionUID = -296360092L;

	protected Window chequeListWindow;

	protected PDCUploadListCtrl(@Autowired UploadService chequeUploadService) {
		super(chequeUploadService, UploadTypes.CHEQUE);
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "ChequeUpload";
		super.onCreate(getArgument("stage"), this.chequeListWindow);
	}

}