package com.pennant.webui.reports.model;

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
import com.pennant.util.PennantAppUtil;

public class LoanEnquiryPostingsListItemRenderer implements ListitemRenderer<Object>, Serializable{
	
	private static final long serialVersionUID = 5574543684897936853L;
	
	private int formatter;

	public LoanEnquiryPostingsListItemRenderer(int formatter) {
		super();
		this.formatter = formatter;
	}
	//Upgraded to ZK-6.5.1.1 Added an additional parameter of type count 	
	@Override
	public void render(Listitem item, Object data, int count) throws Exception {

		if (item instanceof Listgroup) { 
			Object groupData = (Object) data; 
			final ReturnDataSet dataSet= (ReturnDataSet)groupData;
			item.appendChild(new Listcell(dataSet.getFinEvent()+"\t:\t"+dataSet.getLovDescEventCodeName()));
		} else if (item instanceof Listgroupfoot) { 
			Listcell cell = new Listcell("");
			cell.setSpan(6);
			item.appendChild(cell); 
		} else { 

			Listcell lc;
			ReturnDataSet entry = (ReturnDataSet) data;
			lc = new Listcell(DateUtility.getDBDate(entry.getValueDate().toString()).toString());
			lc.setParent(item);
			/*lc = new Listcell(entry.getTranDesc());
			lc.setParent(item);*/
			lc = new Listcell(PennantAppUtil.getlabelDesc(entry.getDrOrCr(),PennantAppUtil.getTranType()));
			lc.setParent(item);
			lc = new Listcell(entry.getTranCode());
			lc.setParent(item);
			lc = new Listcell(entry.getRevTranCode());
			lc.setParent(item);
			lc = new Listcell(entry.getAccount());
			lc.setStyle("font-weight:bold;");
			lc.setParent(item);
			BigDecimal amt = new BigDecimal(entry.getPostAmount().toString()).setScale(0,RoundingMode.HALF_DOWN);
			lc = new Listcell(PennantAppUtil.amountFormate(amt,
					this.formatter));
			lc.setStyle("font-weight:bold;text-align:right;");
			lc.setParent(item);
		}

	}
	
}
