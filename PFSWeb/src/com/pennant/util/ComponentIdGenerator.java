package com.pennant.util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.IdSpace;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.metainfo.ComponentInfo;
import org.zkoss.zk.ui.metainfo.Property;
import org.zkoss.zk.ui.sys.IdGenerator;
import org.zkoss.zul.Button;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Window;

public class ComponentIdGenerator implements IdGenerator {
	private static final String DEFAULT_PREFIX = "zk_comp_";
	private static final String[] COMPONENTS = { "Textbox", "Intbox",
			"Decimalbox", "Datebox", "Checkbox", "Radio", "Radiogroup",
			"Combobox", "Listbox", "Button", "Menu", "Menuitem", "Tab" };
	private static final String[] CUSTOM_COMPONENTS = { "Uppercasebox",
			"ExtendedCombobox", "CurrencyBox", "AccountSelectionBox",
			"ExtendedSearchListBox" };
	private static final boolean LOG_REQUESTED = false;
	private static final String LOG_PATH = "D:/IDLog.txt";

	@Override
	public String nextComponentUuid(Desktop desktop, Component comp,
			ComponentInfo compInfo) {
		// Generate the default UUID for unlisted components
		if (!contains(COMPONENTS, comp.getClass().getSimpleName())
				&& !contains(CUSTOM_COMPONENTS, comp.getClass().getSimpleName())) {
			return null;
		}

		// Use "id" if one available
		String uuid = getId(comp, compInfo);

		if ("".equals(uuid)) {
			// Derive id for descendants of custom components
			uuid = getCustomComponentChildId(comp);
		}

		if ("".equals(uuid)) {
			// Derive id for "action" within workflow process
			if ("Radio".equals(comp.getClass().getSimpleName())) {
				uuid = getWorkflowActionId(comp);
			}
		}

		if ("".equals(uuid)) {
			// Unable to generate "UUID"
			logInfo(comp, compInfo);

			return DEFAULT_PREFIX + getNextIndex(desktop);
		} else {
			if (desktop.getComponentByUuidIfAny(uuid) == null) {
				return uuid;
			} else {
				// The UUID already exist within the desktop
				logInfo(comp, compInfo);
				
				return uuid + "_dupl_" + getNextIndex(desktop);
			}
		}
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

	private String getId(Component comp, ComponentInfo info) {
		String uuid = comp.getId();

		if ("".equals(uuid) && info != null) {
			for (Property property : info.getProperties()) {
				if ("id".equals(property.getName())) {
					uuid = property.getRawValue();
					break;
				}
			}
		}

		if (!"".equals(uuid)) {
			// Get the id of the window
			String windowId = "";
			IdSpace idSpace = comp.getSpaceOwner();

			if (idSpace != null && idSpace instanceof Window) {
				windowId = ((Window) idSpace).getId();
			}

			if (!"".equals(windowId)) {
				uuid = windowId + "_" + uuid;
			} else {
				uuid = "";
			}
		}

		return uuid;
	}

	private String getCustomComponentChildId(Component comp) {
		return getCustomComponentChildId(comp, comp, 1);
	}

	private String getCustomComponentChildId(Component orig, Component comp,
			int level) {
		Component parent = comp.getParent();

		if (parent != null) {
			if (contains(CUSTOM_COMPONENTS, parent.getClass().getSimpleName())) {
				String suffix = orig.getClass().getSimpleName();

				if ("ExtendedSearchListBox".equals(parent.getClass()
						.getSimpleName())) {
					if ("Button".equals(orig.getClass().getSimpleName())) {
						Button button = (Button) orig;

						if (!"".equals(button.getLabel())) {
							suffix += "_" + button.getLabel();
						}
					}
				}

				return parent.getUuid() + "_" + suffix;
			}

			if (level == 6) {
				return "";
			}

			return getCustomComponentChildId(orig, parent, level + 1);
		}

		return "";
	}

	private String getWorkflowActionId(Component comp) {
		Component parent = comp.getParent();

		if (parent != null) {
			if ("Radiogroup".equals(parent.getClass().getSimpleName())
					&& "userAction".equals(parent.getId())) {
				return parent.getUuid() + "_" + ((Radio) comp).getLabel();
			}
		}

		return "";
	}

	private int getNextIndex(Desktop desktop) {
		// Generate new id number
		Object idNum = desktop.getAttribute("Id_Num");
		int index = idNum == null ? 0 : Integer.parseInt((String) idNum);
		index++;

		// Set the new id number back to the desktop
		desktop.setAttribute("Id_Num", String.valueOf(index));

		return index;
	}

	private boolean contains(String[] array, String value) {
		for (String element : array) {
			if (element.equals(value)) {
				return true;
			}
		}

		return false;
	}

	protected void logInfo(String message) {
		if (!LOG_REQUESTED) {
			return;
		}

		BufferedWriter writer;

		try {
			writer = new BufferedWriter(new FileWriter(LOG_PATH, true));
			writer.append(message);
			writer.append("\n");

			writer.close();
		} catch (IOException e) {
			// Skip
		} finally {
			writer = null;
		}
	}

	protected void logInfo(StringBuffer message) {
		if (!LOG_REQUESTED) {
			return;
		}

		logInfo(message.toString());
	}

	protected void logInfo(Component comp, ComponentInfo info) {
		if (!LOG_REQUESTED) {
			return;
		}

		StringBuffer message = new StringBuffer();
		message.append(comp.toString());
		message.append("\n");

		if (info != null) {
			for (Property property : info.getProperties()) {
				message.append("- Property: ");
				message.append(property.getName());
				message.append(":");
				message.append(property.getRawValue());
				message.append("\n");
			}
		}

		message.append("- Ancestors: ");
		message.append(getAncestors(comp));
		message.append("\n");

		logInfo(message);
	}

	protected String getAncestors(Component comp) {
		String result = "";
		Component parent = comp.getParent();

		if (parent != null) {
			result = " // " + parent + getAncestors(parent);
		}

		return result;
	}
}
