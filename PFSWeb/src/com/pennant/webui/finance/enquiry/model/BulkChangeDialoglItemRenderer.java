package com.pennant.webui.finance.enquiry.model;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.finance.BulkProcessDetails;
import com.pennant.backend.util.PennantConstants;
import com.pennant.util.PennantAppUtil;

public class BulkChangeDialoglItemRenderer  implements ListitemRenderer<BulkProcessDetails>, Serializable{
	private static final long serialVersionUID = 5574543684897936853L;

	boolean isDisabled;
	public BulkChangeDialoglItemRenderer(boolean isDisabled){
		this.isDisabled = isDisabled;
	}
	@Override
	public void render(Listitem item, BulkProcessDetails enquiry, int count) throws Exception {

		((Listbox)item.getParent()).setMultiple(true);
		Listcell lc;
		lc = new Listcell(enquiry.getFinReference());
		lc.setParent(item);
		lc = new Listcell(String.valueOf(enquiry.getCustID()));
		lc.setParent(item);
		lc = new Listcell(enquiry.getFinType());
		lc.setParent(item);
		lc = new Listcell(enquiry.getFinBranch());
		lc.setParent(item);
		lc = new Listcell(enquiry.getFinCCY());
		lc.setParent(item);
		Date deferedSchdDate = enquiry.getDeferedSchdDate();
		lc = new Listcell(deferedSchdDate != null ? PennantAppUtil.formateDate(deferedSchdDate, PennantConstants.dateFormate).toString() : "");
		lc.setParent(item);
		lc = new Listcell(enquiry.getScheduleMethod());
		lc.setParent(item);
		Date reCalStartDate = enquiry.getReCalStartDate();
		lc = new Listcell(reCalStartDate != null ? PennantAppUtil.formateDate(reCalStartDate, PennantConstants.dateFormate).toString() : "");
		lc.setParent(item);
		Date reCalEnddate = enquiry.getReCalEndDate();
		lc = new Listcell(reCalEnddate != null ? PennantAppUtil.formateDate(reCalEnddate, PennantConstants.dateFormate).toString() : "");
		lc.setParent(item);
		lc = new Listcell(enquiry.getProfitDaysBasis());
		lc.setParent(item);
		
		if(StringUtils.trimToEmpty(enquiry.getRecordType()).equals("") && enquiry.getLastMntOn() == null){
			item.setSelected(false);
		} else {
			item.setSelected(!enquiry.isAlwProcess());
		}
		item.setAttribute("data", enquiry);
		item.setDisabled(this.isDisabled);
		ComponentsCtrl.applyForward(item, "onDoubleClick=onBulkRateChangeItemDoubleClicked");
		
	}
}
