package com.pennant.webui.applicationmaster.blacklistcustomer.model;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.blacklist.BlackListCustomers;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.util.DateUtil;

public class BlacklistCustomerListModelItemRenderer implements ListitemRenderer<BlackListCustomers>, Serializable {
	private static final long serialVersionUID = 1L;

	public BlacklistCustomerListModelItemRenderer() {
	    super();
	}

	@Override
	public void render(Listitem item, BlackListCustomers blacklistCustomer, int count) {

		Listcell lc;
		lc = new Listcell(blacklistCustomer.getCustCIF());
		lc.setParent(item);
		lc = new Listcell(DateUtil.formatToLongDate(blacklistCustomer.getCustDOB()));
		lc.setParent(item);

		if (StringUtils.isEmpty(blacklistCustomer.getCustCompName())) {
			lc = new Listcell(blacklistCustomer.getCustFName());
			lc.setParent(item);
		} else {
			lc = new Listcell(blacklistCustomer.getCustCompName());
			lc.setParent(item);
		}
		lc = new Listcell(blacklistCustomer.getCustCtgCode());
		lc.setParent(item);
		/*
		 * lc = new Listcell(blacklistCustomer.getCustLName()); lc.setParent(item);
		 */
		lc = new Listcell(PennantApplicationUtil.formatEIDNumber(blacklistCustomer.getCustCRCPR()));
		lc.setParent(item);
		lc = new Listcell(PennantApplicationUtil.formatEIDNumber(blacklistCustomer.getCustAadhaar()));
		lc.setParent(item);
		lc = new Listcell(blacklistCustomer.getMobileNumber());
		lc.setParent(item);
		lc = new Listcell(blacklistCustomer.getCustNationality());
		lc.setParent(item);
		lc = new Listcell(blacklistCustomer.getLovDescEmpName());
		lc.setParent(item);
		lc = new Listcell(blacklistCustomer.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(blacklistCustomer.getRecordType()));
		lc.setParent(item);

		item.setAttribute("id", blacklistCustomer.getId());

		ComponentsCtrl.applyForward(item, "onDoubleClick=onBlacklistCustomerItemDoubleClicked");
	}
}
