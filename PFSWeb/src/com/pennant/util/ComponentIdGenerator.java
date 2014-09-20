package com.pennant.util;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.metainfo.ComponentInfo;
import org.zkoss.zk.ui.metainfo.Property;
import org.zkoss.zk.ui.sys.IdGenerator;
import org.zkoss.zul.Button;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Textbox;

import com.pennant.AccountSelectionBox;
import com.pennant.CurrencyBox;
import com.pennant.ExtendedCombobox;
import com.pennant.webui.util.searchdialogs.ExtendedSearchListBox;
import com.pennant.webui.util.searchdialogs.ExtendedStaticListBox;

public class ComponentIdGenerator implements IdGenerator {
	private static final String PREFIX = "zk_comp_";

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

		desktop.setAttribute("Id_Num", String.valueOf(index));

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

		// Derive based on parent
		Component parent = comp.getParent();

		if (parent != null) {
			if (isCustomComponent(parent)) {
				uuid.append(comp.getClass().getSimpleName()).append("_")
						.append(parent.getUuid());

				return uuid.toString();
			}

			if (isCustomChild(comp)) {
				Component g = parent.getParent();

				if (g != null && isCustomComponent(g)) {
					uuid.append(comp.getClass().getSimpleName()).append("_")
							.append(parent.getUuid());

					return uuid.toString();
				}
			}
		}

		if (uuid.length() == 0) {
			return PREFIX + index;
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

	private boolean isCustomComponent(Component component) {
		if (component instanceof ExtendedCombobox) {
			return true;
		} else if (component instanceof ExtendedSearchListBox) {
			return true;
		} else if (component instanceof ExtendedStaticListBox) {
			return true;
		} else if (component instanceof AccountSelectionBox) {
			return true;
		} else if (component instanceof CurrencyBox) {
			return true;
		}

		return false;
	}

	private boolean isCustomChild(Component component) {
		if (component instanceof Textbox) {
			return true;
		} else if (component instanceof Button) {
			return true;
		} else if (component instanceof Decimalbox) {
			return true;
		} else if (component instanceof Listitem) {
			return true;
		} else if (component instanceof Listcell) {
			return true;
		}

		return false;
	}
}
