package com.pennant.util;

import java.util.List;

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

public class MyIdgenerator implements IdGenerator {

	public MyIdgenerator() {
	    super();
	}

	public String nextComponentUuid(Desktop desktop, Component comp, ComponentInfo compInfo) {

		String inputComponent = "textbox|button|text|uppercasebox|listbox|checkbox|radiogroup|radio|decimalbox"
				+ "|extendedcombobox|accountSelectionBox|currencyBox";
		String pageName;
		int i = Integer.parseInt(desktop.getAttribute("Id_Num").toString());
		i++;// Start from 1

		StringBuilder uuid = new StringBuilder();

		desktop.setAttribute("Id_Num", String.valueOf(i));
		if (compInfo != null) {
			String id = getId(compInfo);
			if (id != null) {
				uuid.append(id).append("_");
			}
		} else {
			uuid.append(getId(comp));

			if (uuid.length() > 0) {
				return uuid.toString();
			}
		}

		if (uuid.length() == 0) {
			return "zkcomp_" + i;
		} else {

			if (compInfo != null && compInfo.getTag() != null && inputComponent.indexOf(compInfo.getTag()) >= 0) {
				pageName = compInfo.getParent().getPageDefinition().getRequestPath();
				pageName = pageName.substring(
						compInfo.getParent().getPageDefinition().getRequestPath().lastIndexOf("/") + 1,
						compInfo.getParent().getPageDefinition().getRequestPath().lastIndexOf("zul") - 1);
				return pageName + "_" + uuid.append(i).toString();
			} else {
				return uuid.append(i).toString();
			}
		}
	}

	public String getId(Component comp) {
		String result = "";
		Component p = comp.getParent();

		if (p != null && p.getUuid() != null) {
			if (p instanceof ExtendedCombobox || p instanceof ExtendedSearchListBox
					|| p instanceof ExtendedStaticListBox || p instanceof AccountSelectionBox
					|| p instanceof CurrencyBox) {
				result = comp.getClass().getSimpleName() + "_" + p.getUuid();
			} else if (comp instanceof Textbox || comp instanceof Button || comp instanceof Decimalbox
					|| comp instanceof Listitem || comp instanceof Listcell) {
				Component g = p.getParent();

				if (g != null && g.getUuid() != null) {
					if (g instanceof ExtendedCombobox || g instanceof ExtendedSearchListBox
							|| g instanceof ExtendedStaticListBox || g instanceof AccountSelectionBox
							|| g instanceof CurrencyBox) {
						result = comp.getClass().getSimpleName() + "_" + p.getUuid();
					}
				}
			} else {
				// Need To Add Code For Other Components(like Menu Item, List Item, List Cell, etc..)
			}
		}

		return result;
	}

	public String getId(ComponentInfo compInfo) {
		try {
			List<Property> properties = compInfo.getProperties();
			for (Property property : properties) {
				if ("id".equals(property.getName())) {
					return property.getRawValue();
				}
			}
		} catch (Exception e) {
			return null;
		}

		return null;
	}

	public String getType(ComponentInfo compInfo) {
		try {

			List<Property> properties = compInfo.getProperties();
			for (Property property : properties) {
				if ("type".equals(property.getName())) {
					return property.getRawValue();
				}
			}

		} catch (Exception e) {
			return null;
		}

		return null;
	}

	public String getName(ComponentInfo compInfo) {

		try {
			List<Property> properties = compInfo.getProperties();
			for (Property property : properties) {
				if ("name".equals(property.getName())) {
					return property.getRawValue();
				}
			}
		} catch (Exception e) {
			return null;
		}

		return null;
	}

	public String nextDesktopId(Desktop desktop) {
		if (desktop.getAttribute("Id_Num") == null) {
			String number = "0";
			desktop.setAttribute("Id_Num", number);
		}
		return null;
	}

	public String nextPageUuid(Page page) {
		return null;
	}
}