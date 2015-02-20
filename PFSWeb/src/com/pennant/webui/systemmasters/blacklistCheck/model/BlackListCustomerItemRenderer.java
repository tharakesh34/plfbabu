package com.pennant.webui.systemmasters.blacklistCheck.model;

import java.io.Serializable;

import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.blacklist.BlackListCustomers;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;

public class BlackListCustomerItemRenderer implements ListitemRenderer<BlackListCustomers>, Serializable  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void render(Listitem item, BlackListCustomers blackListCustomer, int index)throws Exception {
		Listcell lc;
	  	lc = new Listcell(blackListCustomer.getCustCIF());
		lc.setParent(item);
	  	lc = new Listcell(PennantApplicationUtil.formateDate(blackListCustomer.getCustDOB(), PennantConstants.dateFormate));
		lc.setParent(item);
	  	lc = new Listcell(blackListCustomer.getCustFName());
		lc.setParent(item);
	  	lc = new Listcell(blackListCustomer.getCustLName());
		lc.setParent(item);
	  	lc = new Listcell(blackListCustomer.getCustCRCPR());
		lc.setParent(item);
	  	lc = new Listcell(blackListCustomer.getCustPassportNo());
		lc.setParent(item);
	  	lc = new Listcell(blackListCustomer.getPhoneNumber());
		lc.setParent(item);
	  	lc = new Listcell(blackListCustomer.getCustNationality());
		lc.setParent(item);
	  	lc = new Listcell(blackListCustomer.getEmployer());
		lc.setParent(item);
	  	lc = new Listcell(blackListCustomer.getWatchListRule());
		lc.setParent(item);
		lc = new Listcell();
		Checkbox chk = new Checkbox();
		
		if(!blackListCustomer.isOverride()) {
			chk.setDisabled(true);
		}
	  	chk.setParent(lc);
		lc.setParent(item);
		
		item.setAttribute("data", blackListCustomer);
		
	}

}
