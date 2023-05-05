package com.pennant.pff.blockautolettergenerate.upload.list;

import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Window;

import com.pennant.pff.upload.list.AUploadListCtrl;
import com.pennant.pff.upload.service.UploadService;
import com.pennanttech.pff.file.UploadTypes;

public class BlockAutoGenLetterUploadListCtrl extends AUploadListCtrl {
	private static final long serialVersionUID = -256791887358524752L;
	protected Window window;

	protected BlockAutoGenLetterUploadListCtrl(@Autowired UploadService blockAutoGenLetterUploadService) {
		super(blockAutoGenLetterUploadService, UploadTypes.BLOCK_AUTO_GEN_LTR);
	}

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		this.window = (Window) comp;
		super.doAfterCompose(comp);
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "BlockAutoGenLetterUploadHeader";
		super.onCreate(getArgument("stage"), this.window);
	}
}
