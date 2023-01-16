package com.pennant.webui.financemanagement.excesstransferupload;

import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.zul.Window;

import com.pennant.pff.upload.list.AUploadListCtrl;
import com.pennant.pff.upload.service.UploadService;
import com.pennanttech.pff.file.UploadTypes;

public class ExcessTransferUploadListCtrl extends AUploadListCtrl {
	private static final long serialVersionUID = 2L;

	protected Window excessTransferListWindow;

	protected ExcessTransferUploadListCtrl(@Autowired UploadService excessTransferUploadService) {
		super(excessTransferUploadService, UploadTypes.EXCESS_TRANSFER);
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "ExcessTransferUpload";
		super.onCreate(getArgument("stage"), this.excessTransferListWindow);
	}

}
