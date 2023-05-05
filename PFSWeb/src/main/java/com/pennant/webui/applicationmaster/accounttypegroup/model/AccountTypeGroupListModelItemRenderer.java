package com.pennant.webui.applicationmaster.accounttypegroup.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.applicationmaster.AccountTypeGroup;
import com.pennant.backend.util.PennantJavaUtil;

public class AccountTypeGroupListModelItemRenderer implements ListitemRenderer<AccountTypeGroup>, Serializable {

	private static final long serialVersionUID = 1277410242979825193L;

	public AccountTypeGroupListModelItemRenderer() {
	    super();
	}

	@Override
	public void render(Listitem item, AccountTypeGroup accountTypeGroup, int count) {

		Listcell lc;
		lc = new Listcell(accountTypeGroup.getGroupCode());
		lc.setParent(item);
		lc = new Listcell(String.valueOf(accountTypeGroup.getAcctTypeLevel()));
		lc.setParent(item);
		lc = new Listcell(accountTypeGroup.getParentGroup());
		lc.setParent(item);
		lc = new Listcell(accountTypeGroup.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(accountTypeGroup.getRecordType()));
		lc.setParent(item);

		item.setAttribute("id", accountTypeGroup.getId());

		ComponentsCtrl.applyForward(item, "onDoubleClick=onAccountTypeGroupItemDoubleClicked");
	}
}