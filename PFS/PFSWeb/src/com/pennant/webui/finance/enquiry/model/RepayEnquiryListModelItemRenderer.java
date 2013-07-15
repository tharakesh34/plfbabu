package com.pennant.webui.finance.enquiry.model;

import java.io.Serializable;

import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listgroup;
import org.zkoss.zul.Listgroupfoot;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.Repayments.FinanceRepayments;
import com.pennant.util.PennantAppUtil;

public class RepayEnquiryListModelItemRenderer implements ListitemRenderer, Serializable{

	private static final long serialVersionUID = 3541122568618470160L;
	private int formatter;

	public RepayEnquiryListModelItemRenderer(int formatter) {
		super();
		this.formatter = formatter;
	}
	//Upgraded to ZK-6.5.1.1 Added an additional parameter of type count 	
	@Override
	public void render(Listitem item, Object data, int count) throws Exception {
		
		if (item instanceof Listgroup) { 
			Object groupData = (Object) data; 
			final FinanceRepayments repayment= (FinanceRepayments)groupData;
			item.appendChild(new Listcell(DateUtility.getDBDate(repayment.getFinSchdDate().toString()).toString()));
		} else if (item instanceof Listgroupfoot) { 
			Listcell cell = new Listcell("");
			cell.setSpan(6);
			item.appendChild(cell); 
		} else { 

			final FinanceRepayments aFinRepayDetail = (FinanceRepayments) data;
			Listcell lc;
			lc = new Listcell(DateUtility.getDBDate(aFinRepayDetail.getFinPostDate().toString()).toString());
			lc.setParent(item);
			lc = new Listcell(String.valueOf(aFinRepayDetail.getLinkedTranId()));
			lc.setParent(item);
			lc = new Listcell(PennantAppUtil.amountFormate(aFinRepayDetail.getFinSchdPftPaid(),this.formatter));
			lc.setParent(item);
			lc = new Listcell(PennantAppUtil.amountFormate(aFinRepayDetail.getFinSchdPriPaid(),this.formatter));
			lc.setParent(item);
			lc = new Listcell(PennantAppUtil.amountFormate(aFinRepayDetail.getFinTotSchdPaid(),this.formatter));
			lc.setParent(item);
			lc = new Listcell(PennantAppUtil.amountFormate(aFinRepayDetail.getFinRefund(),this.formatter));
			lc.setParent(item);
		}
	}
	
}
