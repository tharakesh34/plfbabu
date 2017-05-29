package com.pennant.webui.finance.enquiry.model;

import java.io.Serializable;

import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.finance.BulkDefermentChange;

public class BulkDefermentChangeListModelItemRenderer implements ListitemRenderer<BulkDefermentChange>, Serializable{

	private static final long serialVersionUID = 5574543684897936853L;

	public BulkDefermentChangeListModelItemRenderer() {
		
	}
	
	@Override
	public void render(Listitem item, BulkDefermentChange enquiry, int count) throws Exception {

		Listcell lc;
		lc = new Listcell(enquiry.getFinReference());
		lc.setParent(item);
		lc = new Listcell(enquiry.getFinType());
		lc.setParent(item);
		lc = new Listcell(enquiry.getFinCcy());
		lc.setParent(item);
		lc = new Listcell(enquiry.getScheduleMethod());
		lc.setParent(item);
		lc = new Listcell(enquiry.getProfitDaysBasis());
		lc.setParent(item);
		lc = new Listcell(enquiry.getCustCIF());
		lc.setParent(item);
		lc = new Listcell(enquiry.getFinBranch());
		lc.setParent(item);
	}
}
