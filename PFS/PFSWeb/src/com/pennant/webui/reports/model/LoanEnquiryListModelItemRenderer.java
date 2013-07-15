package com.pennant.webui.reports.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.util.PennantConstants;
import com.pennant.util.PennantAppUtil;

public class LoanEnquiryListModelItemRenderer implements ListitemRenderer<FinanceMain>, Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5574543684897936853L;
	//Upgraded to ZK-6.5.1.1 Added an additional parameter of type count 	
	@Override
	public void render(Listitem item, FinanceMain aFinanceMain, int count) throws Exception {
		//final FinanceMain aFinanceMain = (FinanceMain) data;
		Listcell lc;
		lc = new Listcell(aFinanceMain.getLovDescCustCIF());
		lc.setParent(item);
		lc = new Listcell(aFinanceMain.getFinReference());
		lc.setParent(item);
		lc = new Listcell(aFinanceMain.getFinBranch());
		lc.setParent(item);
		lc = new Listcell(DateUtility.formatUtilDate(aFinanceMain.getFinStartDate(),
				PennantConstants.dateFormate));
		lc.setParent(item);
		lc = new Listcell(DateUtility.formatUtilDate(aFinanceMain.getGrcPeriodEndDate(),
				PennantConstants.dateFormate));
		lc.setParent(item);
		lc = new Listcell(String.valueOf(aFinanceMain.getNumberOfTerms()));
		lc.setParent(item);
		lc = new Listcell(DateUtility.formatUtilDate(aFinanceMain.getMaturityDate(),
				PennantConstants.dateFormate));
		lc.setParent(item);
		lc = new Listcell(aFinanceMain.getFinCcy());
		lc.setParent(item);
		lc = new Listcell(PennantAppUtil.amountFormate(aFinanceMain.getFinAmount(),aFinanceMain.getLovDescFinFormatter()));
		lc.setStyle("text-align:right");
		lc.setParent(item);
		if(aFinanceMain.getFinRepaymentAmount()!=null){
			lc = new Listcell(PennantAppUtil.amountFormate(aFinanceMain.getFinAmount()
					.subtract(aFinanceMain.getFinRepaymentAmount()),aFinanceMain.getLovDescFinFormatter()));
			lc.setStyle("text-align:right");
		}else{
			lc = new Listcell("");
			
		}
		lc.setParent(item);
		item.setAttribute("data", aFinanceMain);
		ComponentsCtrl.applyForward(item, "onDoubleClick=onLoanItemDoubleClicked");
	}
}
