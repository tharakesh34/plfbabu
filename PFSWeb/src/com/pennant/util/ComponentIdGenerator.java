package com.pennant.util;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.metainfo.ComponentInfo;
import org.zkoss.zk.ui.metainfo.Property;
import org.zkoss.zk.ui.sys.IdGenerator;

public class ComponentIdGenerator implements IdGenerator {
	private static final String PREFIX = "zk_comp_";

	@Override
	public String nextComponentUuid(Desktop desktop, Component comp,
			ComponentInfo compInfo) {
		String number;

		if ((number = (String) desktop.getAttribute("Id_Num")) == null) {
			number = "0";
			desktop.setAttribute("Id_Num", number);
		}

		int index = Integer.parseInt(number);
		index++;

		StringBuilder uuid = new StringBuilder("");

		// Use ID if one available
		if (compInfo != null) {
			for (Property property : compInfo.getProperties()) {
				if ("id".equals(property.getName())
						&& property.getRawValue() != null) {
					uuid.append(property.getRawValue()).append("_");
				}
			}
		}

		return null;
	}

	@Override
	public String nextPageUuid(Page page) {
		return null;
	}

	@Override
	public String nextDesktopId(Desktop desktop) {
		if (desktop.getAttribute("Id_Num") == null) {
			desktop.setAttribute("Id_Num", "0");
		}

		return null;
	}
}
