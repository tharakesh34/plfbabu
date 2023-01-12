package com.pennant.pff.holdrefund.upload.list;

import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.zul.Window;

import com.pennant.pff.upload.list.AUploadListCtrl;
import com.pennant.pff.upload.service.UploadService;
import com.pennanttech.pff.file.UploadTypes;

public class HoldRefundUploadListCtrl extends AUploadListCtrl {
	private static final long serialVersionUID = -2963600927230407346L;

	protected Window uploadListWindow;

	protected HoldRefundUploadListCtrl(@Autowired UploadService holdRefundUploadService) {
		super(holdRefundUploadService, UploadTypes.HOLD_REFUND);
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "HoldRefundUploadHeader";
		super.onCreate(getArgument("stage"), this.uploadListWindow);
	}

}
