package com.pennant.webui.finance.enquiry.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Window;

import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.finance.BulkProcessDetails;

public class BulkChangeDialoglItemRenderer  implements ListitemRenderer<BulkProcessDetails>, Serializable{
	private static final long serialVersionUID = 5574543684897936853L;

	boolean isDisabled;
	Window windowComp;
	
	public BulkChangeDialoglItemRenderer() {
		
	}
	
	public BulkChangeDialoglItemRenderer(boolean isDisabled, Window window){
		this.isDisabled = isDisabled;
		this.windowComp = window;
	}
	@Override
	public void render(Listitem item, BulkProcessDetails enquiry, int count) throws Exception {

		Listcell lc;
		Checkbox checkbox = new Checkbox();
		List<Object> list = new ArrayList<>();
		list.add(checkbox);
		list.add(enquiry);
		
		checkbox.addForward("onCheck", this.windowComp, "onFinanceItemSelected" , list);
		lc = new Listcell();
		checkbox.setChecked(enquiry.isAlwProcess());
		checkbox.setDisabled(this.isDisabled);
		checkbox.setParent(lc);
		lc.setParent(item);
		lc = new Listcell(enquiry.getFinReference());
		lc.setParent(item);
		lc = new Listcell(String.valueOf(enquiry.getCustID()));
		lc.setParent(item);
		lc = new Listcell(enquiry.getCustName());
		lc.setParent(item);
		lc = new Listcell(enquiry.getFinType());
		lc.setParent(item);
		lc = new Listcell(enquiry.getFinBranch());
		lc.setParent(item);
		lc = new Listcell(enquiry.getFinCCY());
		lc.setParent(item);
		Date deferedSchdDate = enquiry.getDeferedSchdDate();
		lc = new Listcell(deferedSchdDate != null ? DateUtility.formatToLongDate(deferedSchdDate) : "");
		lc.setParent(item);
		lc = new Listcell(enquiry.getSchdMethodDesc());
		lc.setParent(item);
		Date reCalStartDate = enquiry.getReCalStartDate();
		lc = new Listcell(reCalStartDate != null ? DateUtility.formatToLongDate(reCalStartDate) : "");
		lc.setParent(item);
		Date reCalEnddate = enquiry.getReCalEndDate();
		lc = new Listcell(reCalEnddate != null ? DateUtility.formatToLongDate(reCalEnddate) : "");
		lc.setParent(item);
		lc = new Listcell(enquiry.getProfitDayBasisDesc());
		lc.setParent(item);
		
		item.setAttribute("data", enquiry);
		ComponentsCtrl.applyForward(item, "onDoubleClick=onBulkDefermentChangeItemDoubleClicked");
		
	}
}
