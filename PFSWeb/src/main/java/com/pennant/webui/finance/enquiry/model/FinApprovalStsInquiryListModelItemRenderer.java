package com.pennant.webui.finance.enquiry.model;

import java.io.Serializable;
import java.math.BigDecimal;

import org.apache.commons.lang.StringUtils;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.finance.CustomerFinanceDetail;
import com.pennant.util.PennantAppUtil;

public class FinApprovalStsInquiryListModelItemRenderer implements ListitemRenderer<CustomerFinanceDetail>, Serializable{

	private static final long serialVersionUID = 5574543684897936853L;

	public FinApprovalStsInquiryListModelItemRenderer() {
		
	}
	
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
		BigDecimal finAmount = enquiry.getFinAmount();
		lc = new Listcell(PennantAppUtil.amountFormate(finAmount, CurrencyUtil.getFormat(enquiry.getFinCcy())));
		lc.setStyle("text-align:right;");
		lc.setParent(item);
		lc = new Listcell(DateUtility.formatToLongDate(enquiry.getFinStartDate()));
		lc.setParent(item);
		lc = new Listcell(StringUtils.trimToEmpty(enquiry.getLastMntByUser()));
		lc.setParent(item);
		lc = new Listcell(enquiry.getNextRoleDesc());
		lc.setParent(item);
		lc = new Listcell(enquiry.getPrvRoleDesc());
		lc.setParent(item);
		item.setAttribute("id", enquiry.getFinReference());
		ComponentsCtrl.applyForward(item, "onDoubleClick=onFinApprovalStsInquiryItemDoubleClicked");

	}
}
