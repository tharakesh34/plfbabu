package com.pennant.pff.noc.upload.web.list;

import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Window;

import com.pennant.pff.upload.list.AUploadListCtrl;
import com.pennant.pff.upload.service.UploadService;
import com.pennanttech.pff.file.UploadTypes;

public class CourierDetailUploadListCtrl extends AUploadListCtrl {
	private static final long serialVersionUID = 2L;

	protected Window courierDetailUploadList;

	protected CourierDetailUploadListCtrl(@Autowired UploadService courierDetailUploadService) {
		super(courierDetailUploadService, UploadTypes.COURIER_DETAILS);
	}

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		this.window = (Window) comp;
		super.doAfterCompose(comp);
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "CourierDetailUploadHeader";
		super.onCreate(getArgument("stage"), this.window);
	}
}
