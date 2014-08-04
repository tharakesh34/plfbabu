package com.pennant.webui.finance.enquiry.model;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.finance.CustomerFinanceDetail;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;

public class FinApprovalStsInquiryListModelItemRenderer implements ListitemRenderer<CustomerFinanceDetail>, Serializable{

	private static final long serialVersionUID = 5574543684897936853L;

	@Override
	public void render(Listitem item, CustomerFinanceDetail enquiry, int count) throws Exception {

		Listcell lc;
		lc = new Listcell(enquiry.getCustCIF());
		lc.setParent(item);
		lc = new Listcell(enquiry.getCustShrtName());
		lc.setParent(item);
		lc = new Listcell(enquiry.getFinReference());
		lc.setParent(item);
		lc = new Listcell(enquiry.getFinTypeDesc());
		lc.setParent(item);
		lc = new Listcell(PennantApplicationUtil.amountFormate(enquiry.getFinAmount(),enquiry.getCcyFormat()));
		lc.setParent(item);
		lc = new Listcell( DateUtility.formatUtilDate(enquiry.getFinStartDate(), PennantConstants.dateFormate));
		lc.setParent(item);
		lc = new Listcell(StringUtils.trimToEmpty(enquiry.getLastMntByUser()));
		lc.setParent(item);
		lc = new Listcell(enquiry.getNextRoleDesc());
		lc.setParent(item);
		lc = new Listcell(enquiry.getRoleDesc());
		lc.setParent(item);
		item.setAttribute("data", enquiry);
		ComponentsCtrl.applyForward(item, "onDoubleClick=onFinApprovalStsInquiryItemDoubleClicked");

	}
}
