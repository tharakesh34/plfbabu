package com.pennant.webui.finance.enquiry.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.finance.BulkRateChangeHeader;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantStaticListUtil;

public class BulkRateChangeListModelItemRenderer implements ListitemRenderer<BulkRateChangeHeader>, Serializable{

	private static final long serialVersionUID = 5574543684897936853L;

	public BulkRateChangeListModelItemRenderer() {

	}

	@Override
	public void render(Listitem item, BulkRateChangeHeader bulkRateChangeHeader, int count) throws Exception {

		Listcell lc;
		lc = new Listcell(bulkRateChangeHeader.getBulkRateChangeRef());
		lc.setParent(item);
		lc = new Listcell(bulkRateChangeHeader.getFinType());
		lc.setParent(item);
		lc = new Listcell(DateUtility.formatToLongDate(bulkRateChangeHeader.getFromDate()));
		lc.setParent(item);
		lc = new Listcell(DateUtility.formatToLongDate(bulkRateChangeHeader.getToDate()));
		lc.setParent(item);
		lc = new Listcell(PennantApplicationUtil.formatRate(bulkRateChangeHeader.getRateChange().doubleValue(), 
				PennantConstants.rateFormate));
		lc.setParent(item);
		lc = new Listcell(PennantStaticListUtil.getlabelDesc(bulkRateChangeHeader.getReCalType(), PennantStaticListUtil.getSchCalCodes()));
		lc.setParent(item);
		lc = new Listcell(DateUtility.formatToLongDate(bulkRateChangeHeader.getLastMntOn()));
		lc.setParent(item);
		lc = new Listcell(bulkRateChangeHeader.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(bulkRateChangeHeader.getRecordType()));
		lc.setParent(item);

		item.setAttribute("data", bulkRateChangeHeader);
		ComponentsCtrl.applyForward(item, "onDoubleClick = onBulkRateChangeItemDoubleClicked");

	}
}
