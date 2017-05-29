package com.pennant.webui.finance.enquiry.model;

import java.io.Serializable;

import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.app.util.CurrencyUtil;
import com.pennant.backend.model.applicationmaster.SysNotificationDetails;
import com.pennant.util.PennantAppUtil;

public class SysNotificationDialogModelItemRenderer implements ListitemRenderer<SysNotificationDetails>, Serializable{

	private static final long serialVersionUID = 5574543684897936853L;

	public SysNotificationDialogModelItemRenderer() {

	}

	@Override
	public void render(Listitem item, SysNotificationDetails details, int count) throws Exception {

		((Listbox)item.getParent()).setMultiple(true);
		Listcell lc;
		lc = new Listcell(details.getCustCIF());
		lc.setParent(item);
		lc = new Listcell(details.getCustShrtName());
		lc.setParent(item);
		lc = new Listcell(details.getFinReference());
		lc.setParent(item);
		lc = new Listcell(details.getFinBranch());
		lc.setParent(item);
		lc = new Listcell(details.getFinCcy());
		lc.setParent(item);
		lc = new Listcell(String.valueOf(details.getFinCurODDays()));
 		lc.setParent(item);
 		lc = new Listcell(PennantAppUtil.amountFormate(details.getFinCurODAmt(), CurrencyUtil.getFormat(details.getFinCcy())));
		lc.setStyle("text-align:right;");
		lc.setParent(item);
 
		item.setAttribute("data", details);
 
	}
}
