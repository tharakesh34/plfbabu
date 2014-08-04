package com.pennant.webui.finance.enquiry.model;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.finance.BulkProcessHeader;
import com.pennant.backend.util.PennantConstants;
import com.pennant.util.PennantAppUtil;

public class BulkRateChangeListModelItemRenderer implements ListitemRenderer<BulkProcessHeader>, Serializable{

	private static final long serialVersionUID = 5574543684897936853L;

	@Override
	public void render(Listitem item, BulkProcessHeader enquiry, int count) throws Exception {

		Listcell lc;
		lc = new Listcell(String.valueOf(PennantAppUtil.formateDate(enquiry.getFromDate(), PennantConstants.dateFormate)));
		lc.setParent(item);
		lc = new Listcell(String.valueOf(PennantAppUtil.formateDate(enquiry.getToDate(), PennantConstants.dateFormate)));
		lc.setParent(item);
		if(StringUtils.trimToEmpty(enquiry.getBulkProcessFor()).equals("R")){			
			lc = new Listcell(enquiry.getNewProcessedRate().toString());
		} else {
			lc = new Listcell("0.00");
		}
		lc.setParent(item);
		if(StringUtils.trimToEmpty(enquiry.getBulkProcessFor()).equals("D")){
			Date recalFromDate = enquiry.getReCalFromDate();
			lc = new Listcell(recalFromDate != null ? PennantAppUtil.formateDate(recalFromDate, PennantConstants.dateFormate).toString() : "");
			lc.setParent(item);
			Date reToFromDate = enquiry.getReCalFromDate();
			lc = new Listcell( reToFromDate != null ? PennantAppUtil.formateDate(reToFromDate, PennantConstants.dateFormate).toString() : "");
			lc.setParent(item);
		}
		String reCalType = "";
		if(enquiry.getReCalType().equals("CURPRD")){
			reCalType = Labels.getLabel("label_Current_Period");
		} else if(enquiry.getReCalType().equals("TILLMDT")){
			reCalType = Labels.getLabel("label_Till_Maturity");
		} else if(enquiry.getReCalType().equals("ADJMDT")){
			reCalType = Labels.getLabel("label_Adj_To_Maturity");
		}else if(enquiry.getReCalType().equals("TILLDATE")){
			reCalType = Labels.getLabel("label_Till_Date");
		}
		lc = new Listcell(reCalType);
		lc.setParent(item);
		
		lc = new Listcell(enquiry.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(enquiry.getRecordType());
		lc.setParent(item);
		item.setAttribute("data", enquiry);
		ComponentsCtrl.applyForward(item, "onDoubleClick=onBulkRateChangeItemDoubleClicked");
		
	}
}
