package com.pennant.webui.reports.customer.model;

import java.io.Serializable;

import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.accounts.Accounts;
import com.pennant.util.PennantAppUtil;

public class CustomerAccountListModelItemRender implements ListitemRenderer,
		Serializable {

	private static final long serialVersionUID = -6954091801433341494L;
	//Upgraded to ZK-6.5.1.1 Added an additional parameter of type count 	
	@Override
	public void render(Listitem item, Object data, int count) throws Exception {

		final Accounts accounts = (Accounts) data;
		Listcell lc;
		lc = new Listcell(accounts.getAccountId());
		lc.setParent(item);
		lc = new Listcell(accounts.getAcShortName());
		lc.setParent(item);
		lc = new Listcell(accounts.getAcBranch());
		lc.setParent(item);
		lc = new Listcell(accounts.getAcType());
		lc.setParent(item);
		lc = new Listcell(accounts.getAcCcy());
		lc.setParent(item);
		lc = new Listcell(PennantAppUtil.getlabelDesc(accounts.getAcPurpose(),
				PennantAppUtil.getAccountPurpose()));
		lc.setParent(item);
		lc = new Listcell();
		final Checkbox cbIsInternalAc = new Checkbox();
		cbIsInternalAc.setDisabled(true);
		cbIsInternalAc.setChecked(accounts.isInternalAc());
		lc.appendChild(cbIsInternalAc);
		lc.setStyle("text-align:center");
		lc.setParent(item);
		lc = new Listcell();
		final Checkbox cbIsCustSysAc = new Checkbox();
		cbIsCustSysAc.setDisabled(true);
		cbIsCustSysAc.setChecked(accounts.isCustSysAc());
		lc.setStyle("text-align:center");
		lc.appendChild(cbIsCustSysAc);
		lc.setParent(item);
		lc = new Listcell(PennantAppUtil.amountFormate(
				accounts.getAcPrvDayBal().subtract(accounts.getAcTodayDr()).add(accounts.getAcTodayCr()), 
				accounts.getLovDescFinFormatter()));
		lc.setStyle("text-align:right;");
		lc.setParent(item);
		lc = new Listcell(PennantAppUtil.amountFormate(
				accounts.getAcAccrualBal(), accounts.getLovDescFinFormatter()));
		lc.setStyle("text-align:right;");
		lc.setParent(item);
	}
}
