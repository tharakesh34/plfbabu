package com.pennant.webui.finance.enquiry.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

import org.apache.commons.lang.StringUtils;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listgroup;
import org.zkoss.zul.Listgroupfoot;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.app.util.CurrencyUtil;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennanttech.pennapps.core.util.DateUtil;

public class FinanceEnquiryPostingsListItemRenderer implements ListitemRenderer<ReturnDataSet>, Serializable {

	private static final long serialVersionUID = 5574543684897936853L;

	public FinanceEnquiryPostingsListItemRenderer() {

	}

	@Override
	public void render(Listitem item, ReturnDataSet dataSet, int count) {

		if (item instanceof Listgroup) {
			if (StringUtils.equals(PennantConstants.EVENTBASE, dataSet.getPostingGroupBy())) {
				item.appendChild(new Listcell(dataSet.getFinEvent() + " : " + dataSet.getLovDescEventCodeName()));
				item.setStyle("text-align:left;");
			} else if (StringUtils.equals(PennantConstants.POSTDATE, dataSet.getPostingGroupBy())) {
				item.appendChild(new Listcell(DateUtil.formatToLongDate(dataSet.getPostDate())));
			} else if (StringUtils.equals(PennantConstants.VALUEDATE, dataSet.getPostingGroupBy())) {
				item.appendChild(new Listcell(DateUtil.formatToLongDate(dataSet.getValueDate())));
			} else if (StringUtils.equals(PennantConstants.ACCNO, dataSet.getPostingGroupBy())) {
				item.appendChild(new Listcell(String.valueOf(dataSet.getAccount())));
			} else {
				item.appendChild(new Listcell(String.valueOf("")));
				item.appendChild(new Listcell(dataSet.getFinEvent() + " : " + dataSet.getLovDescEventCodeName()));
				item.setStyle("text-align:left;");
			}
		} else if (item instanceof Listgroupfoot) {
			Listcell cell = new Listcell("");
			cell.setSpan(9);
			item.appendChild(cell);
		} else {

			Listcell lc;
			if (StringUtils.equals(PennantConstants.EVENTBASE, dataSet.getPostingGroupBy())
					|| StringUtils.isBlank(dataSet.getPostingGroupBy())) {
				lc = new Listcell("");
				lc.setParent(item);
			} else {
				lc = new Listcell(String.valueOf(dataSet.getFinEvent() + "\t:\t" + dataSet.getLovDescEventCodeName()));
				lc.setParent(item);
			}

			if (StringUtils.equals(PennantConstants.POSTDATE, dataSet.getPostingGroupBy())) {
				lc = new Listcell("");
				lc.setParent(item);
			} else {
				lc = new Listcell(DateUtil.formatToLongDate(dataSet.getPostDate()));
				lc.setParent(item);
			}
			if (StringUtils.equals(PennantConstants.VALUEDATE, dataSet.getPostingGroupBy())) {
				lc = new Listcell("");
				lc.setParent(item);
			} else {
				lc = new Listcell(DateUtil.formatToLongDate(dataSet.getValueDate()));
				lc.setParent(item);
			}

			lc = new Listcell(dataSet.getTranDesc());
			lc.setParent(item);
			lc = new Listcell(
					PennantApplicationUtil.getLabelDesc(dataSet.getDrOrCr(), PennantStaticListUtil.getTranType()));
			lc.setParent(item);
			lc = new Listcell(dataSet.getTranCode());
			lc.setParent(item);
			lc = new Listcell(dataSet.getRevTranCode());
			lc.setParent(item);
			lc = new Listcell(dataSet.getGlCode());
			lc.setParent(item);
			lc = new Listcell(PennantApplicationUtil.formatAccountNumber(dataSet.getAccount()));
			lc.setStyle("font-weight:bold;");
			lc.setParent(item);
			lc = new Listcell(dataSet.getAcCcy());
			lc.setParent(item);
			BigDecimal amt = new BigDecimal(dataSet.getPostAmount().toString()).setScale(0, RoundingMode.HALF_DOWN);
			lc = new Listcell(CurrencyUtil.format(amt, CurrencyUtil.getFormat(dataSet.getAcCcy())));
			lc.setStyle("font-weight:bold;text-align:right;");
			lc.setParent(item);
		}
	}
}
