package com.pennant.util;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.metainfo.ComponentInfo;
import org.zkoss.zk.ui.metainfo.Property;
import org.zkoss.zk.ui.sys.IdGenerator;

public class ComponentIdGenerator implements IdGenerator {
	private static final String PREFIX = "zk_comp_";
	private static final String COMP_SET = "";

	@Override
	public String nextComponentUuid(Desktop desktop, Component comp,
			ComponentInfo compInfo) {
		// Set the index back to the desktop
		String number;

		if ((number = (String) desktop.getAttribute("Id_Num")) == null) {
			number = "0";
		}

		int index = Integer.parseInt(number);
		index++;

		desktop.setAttribute("Id_Num", index);

		// Get the page file name
		String pageName = "";

		if (compInfo != null) {
			pageName = compInfo.getParent().getPageDefinition()
					.getRequestPath();
			pageName = pageName.substring(pageName.lastIndexOf("/") + 1,
					pageName.lastIndexOf("zul") - 1);
		}

		StringBuilder uuid = new StringBuilder("");

		// Use "id" if one available
		if (compInfo != null) {
			for (Property property : compInfo.getProperties()) {
				if ("id".equals(property.getName())) {
					uuid.append(pageName).append("_");
					uuid.append(property.getRawValue()).append("_");
					uuid.append(index);

					return uuid.toString();
				}
			}
		}

		if (uuid.length() == 0) {
			uuid.append(PREFIX);
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
