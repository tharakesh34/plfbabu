package com.pennant.webui.alerts;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.Property;
import com.pennant.backend.model.finance.covenant.Covenant;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pff.staticlist.AppStaticList;

public class CovenantAlertsModelItemRenderer implements ListitemRenderer<Covenant>, Serializable {
	private static final long serialVersionUID = 5574543684897936853L;

	boolean recordFound;
	private transient List<Property> listFrequency = AppStaticList.getFrequencies();

	public CovenantAlertsModelItemRenderer() {
	    super();
	}

	@Override
	public void render(Listitem item, Covenant covenant, int index) {
		Listcell lc;
		lc = new Listcell(covenant.getKeyReference());
		lc.setParent(item);
		lc = new Listcell(covenant.getCode());
		lc.setParent(item);
		lc = new Listcell(covenant.getDescription());
		lc.setParent(item);
		lc = new Listcell(getFrequency(covenant.getFrequency()));
		lc.setParent(item);
		lc = new Listcell(String.valueOf(covenant.getAlertDays()));
		lc.setParent(item);
		lc = new Listcell(DateUtil.format(covenant.getNextFrequencyDate(), DateFormat.LONG_DATE.getPattern()));
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
