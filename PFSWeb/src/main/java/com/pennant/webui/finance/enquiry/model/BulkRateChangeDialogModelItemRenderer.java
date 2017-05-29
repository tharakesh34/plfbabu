package com.pennant.webui.finance.enquiry.model;

import java.io.Serializable;

import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.finance.BulkRateChangeDetails;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.util.PennantAppUtil;

public class BulkRateChangeDialogModelItemRenderer implements ListitemRenderer<BulkRateChangeDetails>, Serializable{

	private static final long serialVersionUID = 5574543684897936853L;

	public BulkRateChangeDialogModelItemRenderer() {

	}

	public void render(Listitem item, BulkRateChangeDetails bulkRateChange, int count) throws Exception {

		((Listbox)item.getParent()).setMultiple(true);
		Listcell lc;
		lc = new Listcell(bulkRateChange.getFinReference());
		lc.setParent(item);
		lc = new Listcell(bulkRateChange.getCustCIF());
		lc.setParent(item);
		lc = new Listcell(bulkRateChange.getFinBranch());
		lc.setParent(item);
		lc = new Listcell(bulkRateChange.getFinCCY());
		lc.setParent(item);
		lc = new Listcell(PennantAppUtil.amountFormate(bulkRateChange.getFinAmount(), bulkRateChange.getLovDescFinFormatter()));
		lc.setStyle("text-align:right;");
		lc.setParent(item);
		lc = new Listcell(PennantApplicationUtil.formatRate(bulkRateChange.getOldProfitRate().doubleValue(), 
				PennantConstants.rateFormate));
		lc.setParent(item);
		lc = new Listcell(PennantAppUtil.amountFormate(bulkRateChange.getOldProfit(), bulkRateChange.getLovDescFinFormatter()));
		lc.setStyle("text-align:right;");
		lc.setParent(item);
		lc = new Listcell(PennantApplicationUtil.formatRate(bulkRateChange.getNewProfitRate().doubleValue(), 
				PennantConstants.rateFormate));
		lc.setParent(item);
		lc = new Listcell(PennantAppUtil.amountFormate(bulkRateChange.getNewProfit(), bulkRateChange.getLovDescFinFormatter()));
		lc.setStyle("text-align:right;");
		lc.setParent(item);
		lc = new Listcell("");
		lc.setParent(item);


		/*if(StringUtils.trimToEmpty(bulkRateChange.getRecordType()).equals("") && bulkRateChange.getLastMntOn() == null){
			item.setSelected(false);
		} else {
			item.setSelected(!bulkRateChange.isAlwProcess());
		}*/
		item.setAttribute("data", bulkRateChange);
		//	item.setDisabled(this.isDisabled);
		//ComponentsCtrl.applyForward(item, "onDoubleClick = onBulkRateChangeItemDoubleClicked");

	}
	
}
