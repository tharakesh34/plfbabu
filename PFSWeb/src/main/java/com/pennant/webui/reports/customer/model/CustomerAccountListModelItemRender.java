package com.pennant.webui.reports.customer.model;

import java.io.Serializable;

import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.app.util.CurrencyUtil;
import com.pennant.backend.model.accounts.Accounts;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantStaticListUtil;

public class CustomerAccountListModelItemRender implements ListitemRenderer<Accounts>, Serializable {

	private static final long serialVersionUID = -6954091801433341494L;

	public CustomerAccountListModelItemRender() {
	    super();
	}

	@Override
	public void render(Listitem item, Accounts accounts, int count) throws Exception {

		Listcell lc;
		lc = new Listcell(PennantApplicationUtil.formatAccountNumber(accounts.getAcNumber()));
		lc.setParent(item);
		lc = new Listcell(PennantApplicationUtil.formatAccountNumber(accounts.getAcNumber()));
		lc.setParent(item);
		lc = new Listcell(accounts.getAcBranch());
		lc.setParent(item);
		lc = new Listcell(accounts.getAcType());
		lc.setParent(item);
		lc = new Listcell(accounts.getAcCcy());
		lc.setParent(item);
		lc = new Listcell(PennantApplicationUtil.getLabelDesc(accounts.getAcPurpose(),
				PennantStaticListUtil.getAccountPurpose()));
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
		lc = new Listcell(CurrencyUtil.format(accounts.getAcBalance(), accounts.getLovDescFinFormatter()));

		lc.setStyle("text-align:right;");
		lc.setParent(item);
		lc = new Listcell(CurrencyUtil.format(accounts.getShadowBal(), accounts.getLovDescFinFormatter()));
		lc.setStyle("text-align:right;");
		lc.setParent(item);
	}
}
