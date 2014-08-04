package com.pennant.util;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.metainfo.ComponentInfo;
import org.zkoss.zk.ui.metainfo.Property;
import org.zkoss.zk.ui.sys.IdGenerator;

public class MyIdgenerator implements IdGenerator {

	public String nextComponentUuid(Desktop desktop, Component comp,
			ComponentInfo compInfo) {
		
		String inputComponent = "textbox|button|text|uppercasebox|listbox|checkbox|radiogroup|radio|decimalbox";
		String pageName;
		int i = Integer.parseInt(desktop.getAttribute("Id_Num").toString());
		i++;// Start from 1

		StringBuilder uuid = new StringBuilder("");

		desktop.setAttribute("Id_Num", String.valueOf(i));
		if (compInfo != null) {
			String id = getId(compInfo);
			if (id != null) {
				uuid.append(id).append("_");
			}
			String tag = compInfo.getTag();
			/*if (tag != null) {
				uuid.append(tag).append("_");
			}*/
		}
			if (uuid.length() == 0) {

				return "zkcomp_" + i;
			}
			else
			{
				
				if (compInfo.getTag() != null && inputComponent.indexOf(compInfo.getTag()) >= 0) {
					pageName = compInfo.getParent().getPageDefinition().getRequestPath();
					pageName = pageName.substring(compInfo.getParent().getPageDefinition().getRequestPath().lastIndexOf("/")+1, compInfo.getParent().getPageDefinition().getRequestPath().lastIndexOf("zul")-1);
					return pageName + "_" + uuid.append(i).toString();		
				}
				else {
					return uuid.append(i).toString();		
				}
			}
	}

	public String getId(ComponentInfo compInfo) {
		try {
		List<Property> properties = compInfo.getProperties();
		for (Property property : properties) {
			if ("id".equals(property.getName())) {
				return property.getRawValue();
			}
		}
		}
		catch (Exception e) {
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
	
		}
		catch (Exception e) {
			return null;
		}

		return null;
	}

	public String getName(ComponentInfo compInfo) {
		
		try 
		{
		List<Property> properties = compInfo.getProperties();
		for (Property property : properties) {
			if ("name".equals(property.getName())) {
				return property.getRawValue();
			}
		}
		}
		catch (Exception e) {
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