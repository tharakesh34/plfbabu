package com.pennant.pff.presentment.upload.list;

import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.zul.Window;

import com.pennant.pff.upload.list.AUploadListCtrl;
import com.pennant.pff.upload.service.UploadService;
import com.pennanttech.pff.file.UploadTypes;

public class RepresentmentUploadListCtrl extends AUploadListCtrl {
	private static final long serialVersionUID = -2963600927230407346L;

	protected Window uploadListWindow;

	protected RepresentmentUploadListCtrl(@Autowired UploadService rePresentmentUploadService) {
		super(rePresentmentUploadService, UploadTypes.RE_PRESENTMENT);
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "RepresentUploadHeader";
		super.onCreate(getArgument("stage"), this.uploadListWindow);
	}

}
