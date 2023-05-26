package com.pennant.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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

import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.net.SystemUtils;

public class ComponentIdGenerator implements IdGenerator {
	private static final Logger logger = LogManager.getLogger(ComponentIdGenerator.class);

	private static final String DEFAULT_PREFIX = "zk_comp_";
	private static final String[] COMPONENTS = { "Textbox", "Intbox", "Decimalbox", "Datebox", "Checkbox", "Radio",
			"Radiogroup", "Combobox", "Listbox", "Listheader", "Listitem", "Button", "Menu", "Menuitem", "Tab",
			"Groupbox" };
	private static final String[] CUSTOM_COMPONENTS = { "Uppercasebox", "ExtendedCombobox", "CurrencyBox",
			"AccountSelectionBox", "ExtendedSearchListBox", "FrequencyBox", "ExtendedMultipleSearchListBox",
			"ExtendedStaticListBox", "MultiSelectionSearchListBox", "MultiSelectionStaticListBox" };
	private static final boolean LOG_REQUESTED = false;
	private static final String LOG_PATH = "D:/IDLog.txt";

	private List<String[]> xpathData = new ArrayList<String[]>();
	private XSSFWorkbook workbook = new XSSFWorkbook();
	private XSSFSheet sheet = null;
	private String windowId = null;
	private String prevWindowId = null;

	private static final String xpathGenPath = App.getProperty("xpath.storge.path");
	private static final String xpathFileName = App.getProperty("xpath.storage.filename");

	public ComponentIdGenerator() {
		super();
	}

	public List<String[]> getXpathData() {
		return xpathData;
	}

	@Override
	public String nextComponentUuid(Desktop desktop, Component comp, ComponentInfo compInfo) {
		// Generate the default UUID for unlisted components

		if (!contains(COMPONENTS, comp.getClass().getSimpleName())
				&& !contains(CUSTOM_COMPONENTS, comp.getClass().getSimpleName())) {
			return null;
		}

		String id = null;

		// Use "id" if one available
		String uuid = getId(comp, compInfo);

		if ("".equals(uuid)) {
			// Derive id for descendants of custom components
			uuid = getCustomComponentChildId(comp);
			if (comp.getParent() != null && comp.getParent().getParent() != null
					&& "FrequencyBox".equals(comp.getParent().getParent().getClass().getSimpleName())
					&& uuid.contains("Combobox") && uuid.contains("Listbox")) {
				uuid = getNextFrqboxID(desktop, uuid);
			}
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

			id = DEFAULT_PREFIX + getNextIndex(desktop);
		} else {
			if (desktop.getComponentByUuidIfAny(uuid) == null) {
				id = uuid;
			} else {
				// The UUID already exist within the desktop
				logInfo(comp, compInfo);

				id = uuid + "_dupl_" + getNextIndex(desktop);
			}
		}

		if (contains(COMPONENTS, comp.getClass().getSimpleName())
				|| contains(CUSTOM_COMPONENTS, comp.getClass().getSimpleName())) {

			try {

				if (AutomationUtil.getSingleton().isRecordXpath()) {
					generateXpath(desktop, comp, compInfo, id);
				} else {
					xpathData.clear();
					for (int i = workbook.getNumberOfSheets() - 1; i >= 0; i--) {
						workbook.removeSheetAt(i);
					}
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return id;

	}

	private String getNextFrqboxID(Desktop desktop, String uuid) {
		if (desktop.getComponentByUuidIfAny(uuid) == null) {
			return uuid;
		}
		if (desktop.getComponentByUuidIfAny(uuid + "_month") == null) {
			uuid = uuid + "_month";
			return uuid;
		} else {
			uuid = uuid + "_day";
			return uuid;
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

			if (idSpace instanceof Window) {
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

	private String getCustomComponentChildId(Component orig, Component comp, int level) {
		Component parent = comp.getParent();

		if (parent != null) {
			if (contains(CUSTOM_COMPONENTS, parent.getClass().getSimpleName())) {
				String suffix = orig.getClass().getSimpleName();

				if ("Button".equals(orig.getClass().getSimpleName())) {
					Button button = (Button) orig;

					if (!"".equals(button.getLabel())) {
						suffix += "_" + button.getLabel();
					}
				}

				return parent.getUuid() + "_" + suffix;
			} else if ("Window".equals(parent.getClass().getSimpleName())) {
				String suffix = orig.getClass().getSimpleName();
				if ("Button".equals(orig.getClass().getSimpleName())) {
					Button button = (Button) orig;

					if (!"".equals(button.getLabel())) {
						suffix += "_" + button.getLabel();
					}
				}

				return parent.getId() + "_" + suffix;

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
			if ("Radiogroup".equals(parent.getClass().getSimpleName()) && "userAction".equals(parent.getId())) {
				return parent.getUuid() + "_"
						+ StringUtils.replace(StringUtils.replace(((Radio) comp).getLabel(), " ", ""), "-", "");
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

	public void logInfo(String message) {
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
			logger.error("Exception: ", e);
		} finally {
			writer = null;
		}
	}

	public void logInfo(StringBuilder message) {
		if (!LOG_REQUESTED) {
			return;
		}

		logInfo(message.toString());
	}

	public void logInfo(Component comp, ComponentInfo info) {
		if (!LOG_REQUESTED) {
			return;
		}

		StringBuilder message = new StringBuilder();
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

	public void generateXpath(Desktop desktop, Component comp, ComponentInfo compInfo, String id) throws IOException {

		try {

			String compType = comp.getClass().getSimpleName();
			switch (compType) {

			case "Combobox":
				addXpathData(comp.getClass().getSimpleName(), id, "(.//*[@id='" + id + "']//input[1])[1]", comp);
				break;

			case "Radio":
			case "Radiogroup":
				addXpathData(comp.getClass().getSimpleName(), id, "(.//*[@id='" + id + "'])[1]", comp);
				break;

			case "Datebox":
				addXpathData(comp.getClass().getSimpleName(), id, "(.//span[@id='" + id + "']//input[1])[1]", comp);
				break;

			case "Intbox":
			case "Decimalbox":
			case "Checkbox":
				addXpathData(comp.getClass().getSimpleName(), id, "(.//span[@id='" + id + "'])[1]", comp);
				break;

			case "Listbox":
				addXpathData(comp.getClass().getSimpleName(), id, "(.//select[@id='" + id + "'])[1]", comp);
				break;
			//
			// case "Listheader":
			// addXpathData(comp.getClass().getSimpleName(), id, "(.//th[@id='" + id + "']//div//span[1])[1]", comp);

			case "Textbox":
			case "Uppercasebox":
				if (id.contains("notesDialog_remarksText")) {
					addXpathData(comp.getClass().getSimpleName(), id, "(.//textarea[@id='" + id + "'])[1]", comp);
				} else {
					addXpathData(comp.getClass().getSimpleName(), id, "(.//input[@id='" + id + "'])[1]", comp);
				}

				break;

			case "Button":
				addXpathData(comp.getClass().getSimpleName(), id, "(.//button[@id='" + id + "'])[1]", comp);
				break;

			case "Tab":
				addXpathData(comp.getClass().getSimpleName(), id,
						"(.//li[@id='" + id + "']//following::span[text()][1])[1]", comp);
				break;

			case "Menuitem":
			case "Menu":
				addXpathData(comp.getClass().getSimpleName(), id, "(.//div[@id='" + id + "'])[1]", comp);
				break;
			case "ExtendedCombobox":
				addXpathData(comp.getClass().getSimpleName(), id, "(.//table[@id='" + id + "'])[1]", comp);
				break;

			// default:
			// addXpathData(comp.getClass().getSimpleName(), id, "Default " + id, comp);
			// break;
			}

			if (AutomationUtil.getSingleton().isRecordXpath()) {
				generateXpathFile();
			}

		} catch (Exception e) {
			logger.error("Exception: ", e);
		}

	}

	public void addXpathData(String compname, String id, String xpath, Component comp) {
		/* not to add the xpath if it already contains the list */

		IdSpace idSpace = comp.getSpaceOwner();

		if (idSpace instanceof Window) {
			// windowId = ((Window) idSpace).getId();
			/*
			 * New sheet created with windowId is the name given to sheet.
			 * 
			 * Excel UI doesn't allow to set the sheet name having length greater than 31 characters, truncating the
			 * length if exceeds beyond 31 char max
			 */
			windowId = StringUtils.truncate(((Window) idSpace).getId(), 31);

			if ("".contentEquals(windowId)) {
				windowId = StringUtils.truncate(((Window) idSpace).getParent().getId() + "_Ext", 31);
			}
		}

		/* Remove old data from the list if the window changes */
		if (!StringUtils.equals(prevWindowId, windowId)) {
			xpathData.clear();
		}
		prevWindowId = windowId;

		/* check if sheet founds in the workbook */
		sheet = workbook.getSheet(windowId);

		if (sheet == null) {
			xpathData.clear();
			sheet = workbook.createSheet(windowId);
		}

		boolean flag = false;
		if (!getXpathData().isEmpty()) { /* initial state */
			for (String[] xpathList : xpathData) { /* iterate over the existing list to check duplicates */
				flag = xpathList[2].contains(
						xpath); /*
								 * check xpath from the list contains in the existing list or not to avoid duplication
								 */
				if (flag) {
					break;
				}
			}
			if (!flag) {
				xpathData.add(new String[] { compname, id, xpath });
			}
		} else {
			xpathData.add(new String[] { compname, id, xpath });
		}
	}

	public void generateXpathFile() throws IOException {

		List<String[]> data = new ArrayList<>();
		data.add(new String[] { "Property", "Component Id", "Xpath" });
		data.addAll(getXpathData());

		int rownum = 0;
		for (int i = 0; i < data.size(); i++) {
			Row row = sheet.createRow(rownum++);
			String[] list = data.get(i);
			int cellnum = 0;

			for (Object obj : list) {

				// This line creates a cell in the next
				// column of that row
				Cell cell = row.createCell(cellnum++);

				if (obj instanceof String)
					cell.setCellValue((String) obj);

				else if (obj instanceof Integer)
					cell.setCellValue((Integer) obj);
			}

		}

		// Try block to check for exceptions
		try {

			String filePath = xpathGenPath.concat("/").concat(xpathFileName);
			filePath = SystemUtils.appendseparatorsToSystem(filePath);
			logInfo("filePath" + filePath);

			// Writing the workbook
			FileOutputStream out = new FileOutputStream(new File(filePath));
			workbook.write(out);

			// Closing file output connections
			// out.close();

			// Console message for successful execution of
			// program
		} // Catch block to handle exceptions
		catch (Exception e) {

			// Display exceptions along with line number
			// using printStackTrace() method
			e.printStackTrace();
		} finally {
			// workbook.close();

		}

	}
}
