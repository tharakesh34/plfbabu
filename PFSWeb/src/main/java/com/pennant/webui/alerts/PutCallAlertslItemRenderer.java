package com.pennant.webui.alerts;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.Property;
import com.pennant.backend.model.finance.finoption.FinOption;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pff.staticlist.AppStaticList;

public class PutCallAlertslItemRenderer implements ListitemRenderer<FinOption>, Serializable {

	private static final long serialVersionUID = 5574543684897936853L;
	boolean recordFound;
	private transient List<Property> listFrequency = AppStaticList.getFrequencies();

	public PutCallAlertslItemRenderer() {
	    super();
	}

	@Override
	public void render(Listitem item, FinOption finOption, int index) {

		Listcell lc;
		lc = new Listcell(finOption.getFinReference());
		lc.setParent(item);
		lc = new Listcell(finOption.getOptionType());
		lc.setParent(item);
		lc = new Listcell(getFrequency(finOption.getFrequency()));
		lc.setParent(item);
		lc = new Listcell(String.valueOf(finOption.getAlertDays()));
		lc.setParent(item);
		lc = new Listcell(DateUtil.format(finOption.getNextOptionDate(), DateFormat.LONG_DATE.getPattern()));
		lc.setParent(item);

		recordFound = true;
	}

	private String getFrequency(String frequency) {
		for (Property property : listFrequency) {
			if (StringUtils.equals(property.getKey().toString(), frequency)) {
				return property.getValue();
			}
		}
		return "";
	}
}
