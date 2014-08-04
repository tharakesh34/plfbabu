package com.pennant.webui.finance.enquiry.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listgroup;
import org.zkoss.zul.Listgroupfoot;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.util.PennantAppUtil;

public class FinanceEnquiryPostingsListItemRenderer implements ListitemRenderer<ReturnDataSet>, Serializable{
	
	private static final long serialVersionUID = 5574543684897936853L;
	
	@Override
	public void render(Listitem item, ReturnDataSet dataSet, int count) throws Exception {

		if (item instanceof Listgroup) { 
			item.appendChild(new Listcell(dataSet.getFinEvent()+"\t:\t"+dataSet.getLovDescEventCodeName()));
		} else if (item instanceof Listgroupfoot) { 
			Listcell cell = new Listcell("");
			cell.setSpan(8);
			item.appendChild(cell); 
		} else { 

			Listcell lc;
			lc = new Listcell(DateUtility.getDBDate(dataSet.getValueDate().toString()).toString());
			lc.setParent(item);
			lc = new Listcell(dataSet.getTranDesc());
			lc.setParent(item);
			lc = new Listcell(PennantAppUtil.getlabelDesc(dataSet.getDrOrCr(),PennantStaticListUtil.getTranType()));
			lc.setParent(item);
			lc = new Listcell(dataSet.getTranCode());
			lc.setParent(item);
			lc = new Listcell(dataSet.getRevTranCode());
			lc.setParent(item);
			lc = new Listcell(PennantApplicationUtil.formatAccountNumber(dataSet.getAccount()));
			lc.setStyle("font-weight:bold;");
			lc.setParent(item);
			lc = new Listcell(dataSet.getAcCcy());
			lc.setParent(item);
			BigDecimal amt = new BigDecimal(dataSet.getPostAmount().toString()).setScale(0,RoundingMode.HALF_DOWN);
			lc = new Listcell(PennantAppUtil.amountFormate(amt,
					dataSet.getFormatter()));
			lc.setStyle("font-weight:bold;text-align:right;");
			lc.setParent(item);
		}

	}
	
}
