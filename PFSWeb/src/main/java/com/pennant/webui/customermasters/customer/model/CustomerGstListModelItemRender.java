package com.pennant.webui.customermasters.customer.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.customermasters.CustomerGST;

public class CustomerGstListModelItemRender implements ListitemRenderer<CustomerGST>, Serializable {

	public CustomerGstListModelItemRender() {

	}

	@Override
	public void render(Listitem item, CustomerGST customerGST, int index) throws Exception {
		// TODO Auto-generated method stub
		/*
		 * Listcell lc; lc = new Listcell(customerGST.getGstNumber().trim()); lc.setParent(item); lc = new
		 * Listcell(customerGST.getf); lc.setParent(item); lc = new Listcell(customerGST.getCustShrtName());
		 * lc.setParent(item); lc = new Listcell(customerGST.getCustDftBranch()); lc.setParent(item); lc = new
		 * Listcell(customerGST.getLovDescCustCtgCodeName()); lc.setParent(item); lc = new
		 * Listcell(customerGST.getLovDescCustTypeCodeName()); lc.setParent(item); lc = new Listcell(
		 * StringUtils.equals(customerGST.getLovDescRequestStage(), ",") ? "" : customer.getLovDescRequestStage());
		 * lc.setParent(item); lc = new Listcell(customer.getRecordStatus()); lc.setParent(item); lc = new
		 * Listcell(PennantJavaUtil.getLabel(customer.getRecordType())); lc.setParent(item);
		 * 
		 * item.setAttribute("id", customer.getCustID()); item.setAttribute("data", customer);
		 */
		ComponentsCtrl.applyForward(item, "onDoubleClick=onCustomerItemDoubleClicked");
	}

}
