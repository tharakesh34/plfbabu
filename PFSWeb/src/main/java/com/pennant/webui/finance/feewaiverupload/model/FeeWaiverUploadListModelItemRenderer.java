package com.pennant.webui.finance.feewaiverupload.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.expenses.FeeWaiverUploadHeader;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.UploadConstants;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;

public class FeeWaiverUploadListModelItemRenderer implements ListitemRenderer<FeeWaiverUploadHeader>, Serializable {

	private static final long serialVersionUID = 6352065299727172054L;

	String moduleName = "";

	public FeeWaiverUploadListModelItemRenderer(String moduleName) {
		this.moduleName = moduleName;
	}

	public FeeWaiverUploadListModelItemRenderer() {
	    super();
	}

	@Override
	public void render(Listitem item, FeeWaiverUploadHeader uploadHeader, int count) throws Exception {
		((Listbox) item.getParent()).setMultiple(true);
		Listcell lc;
		lc = new Listcell(String.valueOf(uploadHeader.getUploadId()));
		lc.setParent(item);
		lc = new Listcell(uploadHeader.getFileName());
		lc.setParent(item);
		lc = new Listcell(DateUtil.format(uploadHeader.getTransactionDate(), DateFormat.LONG_DATE.getPattern()));
		lc.setParent(item);
		lc = new Listcell(uploadHeader.getUserName());
		lc.setParent(item);
		lc = new Listcell(String.valueOf(uploadHeader.getSuccessCount()));
		lc.setParent(item);
		lc = new Listcell(String.valueOf(uploadHeader.getFailedCount()));
		lc.setParent(item);
		lc = new Listcell(String.valueOf(uploadHeader.getTotalRecords()));
		lc.setParent(item);
		lc = new Listcell(uploadHeader.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(uploadHeader.getRecordType()));
		lc.setParent(item);

		item.setAttribute("data", uploadHeader);
		item.setAttribute("id", uploadHeader.getId());

		if (!(UploadConstants.FEE_WAIVER_APPROVER.equals(this.moduleName))) {
			ComponentsCtrl.applyForward(item, "onDoubleClick=onUploadItemDoubleClicked");
		}
	}
}
