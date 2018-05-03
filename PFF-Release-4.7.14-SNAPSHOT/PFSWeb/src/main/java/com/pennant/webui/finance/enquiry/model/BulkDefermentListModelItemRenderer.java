package com.pennant.webui.finance.enquiry.model;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.finance.BulkProcessHeader;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantStaticListUtil;

public class BulkDefermentListModelItemRenderer implements ListitemRenderer<BulkProcessHeader>, Serializable{

	private static final long serialVersionUID = 5574543684897936853L;

	public BulkDefermentListModelItemRenderer() {

	}

	@Override
	public void render(Listitem item, BulkProcessHeader enquiry, int count) throws Exception {

		Listcell lc;
		lc = new Listcell(DateUtility.formatToLongDate(enquiry.getFromDate()));
		lc.setParent(item);
		lc = new Listcell(DateUtility.formatToLongDate(enquiry.getToDate()));
		lc.setParent(item);
		if("R".equals(StringUtils.trimToEmpty(enquiry.getBulkProcessFor()))){			
			lc = new Listcell(enquiry.getNewProcessedRate().toString());
		} else {
			lc = new Listcell("0.00");
		}
		lc.setParent(item);
		if("D".equals(StringUtils.trimToEmpty(enquiry.getBulkProcessFor()))){
			Date recalFromDate = enquiry.getReCalFromDate();
			lc = new Listcell(recalFromDate != null ? DateUtility.formatToLongDate(recalFromDate) : "");
			lc.setParent(item);
			Date recalToDate = enquiry.getReCalToDate();
			lc = new Listcell(recalToDate != null ? DateUtility.formatToLongDate(recalToDate) : "");
			lc.setParent(item);
		}
		lc = new Listcell(PennantStaticListUtil.getlabelDesc(enquiry.getReCalType(), PennantStaticListUtil.getSchCalCodes()));
		lc.setParent(item);

		lc = new Listcell(enquiry.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(enquiry.getRecordType()));
		lc.setParent(item);
		item.setAttribute("id", enquiry.getId());
		item.setAttribute("bulkProcessFor", enquiry.getBulkProcessFor());
		ComponentsCtrl.applyForward(item, "onDoubleClick=onBulkDefermentChangeItemDoubleClicked");

	}
}
