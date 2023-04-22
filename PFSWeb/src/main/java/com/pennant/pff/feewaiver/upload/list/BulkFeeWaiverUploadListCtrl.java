package com.pennant.pff.feewaiver.upload.list;

import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Window;

import com.pennant.pff.upload.list.AUploadListCtrl;
import com.pennant.pff.upload.service.UploadService;
import com.pennanttech.pff.file.UploadTypes;

public class BulkFeeWaiverUploadListCtrl extends AUploadListCtrl {

	private static final long serialVersionUID = 2L;

	protected Window bulkFeeWaiverListWindow;

	protected BulkFeeWaiverUploadListCtrl(@Autowired UploadService bulkFeeWaiverUploadService) {
		super(bulkFeeWaiverUploadService, UploadTypes.FEE_WAIVER);
	}

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		this.window = (Window) comp;
		super.doAfterCompose(comp);
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "BulkFeeWaiverUploadHeader";
		super.onCreate(getArgument("stage"), this.window);
	}
}
