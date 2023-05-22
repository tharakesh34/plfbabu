package com.pennant.webui.reports.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listgroup;
import org.zkoss.zul.Listgroupfoot;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.app.util.CurrencyUtil;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennanttech.pennapps.core.util.DateUtil;

public class LoanEnquiryPostingsListItemRenderer implements ListitemRenderer<Object>, Serializable {

	private static final long serialVersionUID = 5574543684897936853L;

	private int formatter;

	public LoanEnquiryPostingsListItemRenderer(int formatter) {
		super();
		this.formatter = formatter;
	}

	@Override
	public void render(Listitem item, Object data, int count) {

		if (item instanceof Listgroup) {
			Object groupData = (Object) data;
			final ReturnDataSet dataSet = (ReturnDataSet) groupData;
			item.appendChild(new Listcell(dataSet.getFinEvent() + "\t:\t" + dataSet.getLovDescEventCodeName()));
		} else if (item instanceof Listgroupfoot) {
			Listcell cell = new Listcell("");
			cell.setSpan(6);
			item.appendChild(cell);
		} else {

			Listcell lc;
			ReturnDataSet entry = (ReturnDataSet) data;
			lc = new Listcell(DateUtil.formatToLongDate(entry.getValueDate()));
			lc.setParent(item);
			lc = new Listcell(
					PennantApplicationUtil.getLabelDesc(entry.getDrOrCr(), PennantStaticListUtil.getTranType()));
			lc.setParent(item);
			lc = new Listcell(entry.getTranCode());
			lc.setParent(item);
			lc = new Listcell(entry.getRevTranCode());
			lc.setParent(item);
			lc = new Listcell(PennantApplicationUtil.formatAccountNumber(entry.getAccount()));
			lc.setStyle("font-weight:bold;");
			lc.setParent(item);
			BigDecimal amt = new BigDecimal(entry.getPostAmount().toString()).setScale(0, RoundingMode.HALF_DOWN);
			lc = new Listcell(CurrencyUtil.format(amt, this.formatter));
			lc.setStyle("font-weight:bold;text-align:right;");
			lc.setParent(item);
		}

	}

}
