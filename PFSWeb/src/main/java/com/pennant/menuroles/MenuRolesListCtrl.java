package com.pennant.menuroles;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.util.media.AMedia;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zkmax.zul.Filedownload;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.administration.SecurityGroup;
import com.pennant.backend.model.administration.SecurityRole;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.dataengine.util.ExcelUtil;
import com.pennanttech.framework.core.SearchOperator;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.web.components.SearchFilterControl;
import com.pennanttech.framework.web.components.SearchFilterControl.SearchFiltersRender;
import com.pennanttech.pennapps.core.DocType;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.jdbc.search.Search;
import com.pennanttech.pennapps.jdbc.search.SearchProcessor;
import com.pennanttech.pennapps.web.menu.MainMenu;
import com.pennanttech.pennapps.web.menu.Menu;
import com.pennanttech.pennapps.web.menu.MenuItem;
import com.pennanttech.pennapps.web.util.MessageUtil;

public class MenuRolesListCtrl extends GFCBaseListCtrl<MenuItem> {
	private static final long serialVersionUID = 1L;

	protected Window window_MenuRolesList;
	protected Borderlayout borderLayout_MenuRolesList;
	protected Paging pagingMenuRolesList;
	protected Listbox listBoxMenuRoles;

	protected Textbox menuName;

	protected Listbox sortOperator_menuName;

	protected Listheader listheader_MenuName;
	protected Listheader listheader_Groups;
	protected Listheader listheader_Roles;

	protected Button btnDownload;
	protected Button button_MenuRolesList_MenuSearch;

	@Autowired
	private SearchProcessor searchProcessor;

	private List<MenuItem> menuItemFinalList;

	public MenuRolesListCtrl() {
		super();
	}

	public void onCreate$window_MenuRolesList(Event event) {
		logger.debug(Literal.ENTERING);

		setPageComponents(window_MenuRolesList, borderLayout_MenuRolesList, listBoxMenuRoles, pagingMenuRolesList);

		sortOperator_menuName.setModel(new ListModelList<>(SearchOperator.getOperators(Operators.STRING)));
		sortOperator_menuName.setItemRenderer(new SearchFiltersRender());
		sortOperator_menuName.setSelectedIndex(0);

		listBoxMenuRoles.setItemRenderer(new MenuRolesListModelItemRenderer());

		List<MenuItem> menuItems = MainMenu.getMenuItems();
		menuItemFinalList = new ArrayList<>();
		getMenusList(menuItems);

		getPagedListWrapper().initList(menuItemFinalList, listBoxMenuRoles, pagingMenuRolesList);
		doRenderPage();

		logger.debug(Literal.LEAVING);
	}

	public void onClick$button_MenuRolesList_MenuSearch(Event event) {
		logger.debug(Literal.ENTERING);

		String opr = String.valueOf(sortOperator_menuName.getSelectedIndex());
		String menuname = menuName.getValue();

		List<MenuItem> menuItemDupList = new ArrayList<>();
		menuItemDupList.addAll(menuItemFinalList);

		if (!opr.isEmpty() && menuname.isEmpty()) {
			menuName.setValue("");
			getPagedListWrapper().initList(menuItemFinalList, listBoxMenuRoles, pagingMenuRolesList);
			return;
		}

		Predicate<MenuItem> p = null;
		switch (opr) {
		case "0":
			p = e -> {
				String label = Labels.getLabel(e.getId());
				return !label.equalsIgnoreCase(menuname);
			};
			break;
		case "1":
			p = e -> {
				String label = Labels.getLabel(e.getId());
				return label.equalsIgnoreCase(menuname);
			};
			break;
		case "2":
			p = e -> {
				String label = Labels.getLabel(e.getId());
				return !StringUtils.containsIgnoreCase(label, menuname);
			};
			break;
		default:
			break;
		}

		menuItemDupList.removeIf(p);

		getPagedListWrapper().initList(menuItemDupList, listBoxMenuRoles, pagingMenuRolesList);

		logger.debug(Literal.LEAVING);
	}

	public void onClick$btnRefresh(Event event) {
		logger.debug(Literal.ENTERING);

		sortOperator_menuName.setSelectedIndex(0);
		SearchFilterControl.resetFilters(menuName);
		getPagedListWrapper().initList(menuItemFinalList, listBoxMenuRoles, pagingMenuRolesList);

		logger.debug(Literal.LEAVING);
	}

	public void onClick$btnDownload(Event event) {
		logger.debug(Literal.ENTERING);

		createFile();

		logger.debug(Literal.LEAVING);
	}

	public void getMenusList(List<MenuItem> menuItems) {
		menuItems.forEach(mi -> appendMenuItemtoList(mi));
	}

	public void appendMenuItemtoList(MenuItem menuItem) {
		if (menuItem instanceof Menu) {
			Menu menu = (Menu) menuItem;
			menu.getItems().forEach(mi -> appendMenuItemtoList(mi));
		} else {
			menuItemFinalList.add(menuItem);
		}
	}

	/**
	 * Create the excel file and download to user's desktop.
	 */
	private void createFile() {
		String name = "Menu Roles and Rights";

		try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream stream = new ByteArrayOutputStream();) {
			// Create a new sheet.
			Sheet sheet = workbook.createSheet(name);

			// Create header row.
			ExcelUtil.createRow(sheet, 0, "Menu", "Level1", "Level2", "Level3", "Right", "Groups", "Roles");

			// Create data row for each menu item.
			int rowIndex = 1;

			for (MenuItem menuItem : MainMenu.getMenuItems()) {
				rowIndex = createMenu(sheet, rowIndex, 0, menuItem);
			}

			// Write out the workbook to stream and download the file at the client.
			workbook.write(stream);

			Filedownload.save(new AMedia(name, "xlsx", DocType.XLSX.getContentType(), stream.toByteArray()));
		} catch (IOException e) {
			logger.error(Literal.EXCEPTION, e);
			MessageUtil.showMessage(
					"Unable to generate the file. Please try again later or contact the system administrator.");
		}
	}

	private int createMenu(Sheet sheet, int rowIndex, int columnIndex, MenuItem menuItem) {
		// Create a row.
		Row row = sheet.createRow(rowIndex++);

		// Create a cell for the menu item.
		Cell cell = row.createCell(columnIndex);
		cell.setCellValue(Labels.getLabel(menuItem.getId()));

		// Create a cell for the right.
		cell = row.createCell(4);
		cell.setCellValue(menuItem.getRightName());

		// Create cells for groups and roles if the right available.
		if (StringUtils.isNotEmpty(menuItem.getRightName())) {
			cell = row.createCell(5);
			cell.setCellValue(getGroups(menuItem.getRightName(), ","));

			cell = row.createCell(6);
			cell.setCellValue(getRoles(menuItem.getRightName(), ","));
		}

		// Create data row for each child if available.
		if (menuItem instanceof Menu) {
			Menu menu = (Menu) menuItem;
			columnIndex++;

			for (MenuItem item : menu.getItems()) {
				rowIndex = createMenu(sheet, rowIndex, columnIndex, item);
			}
		}

		return rowIndex;
	}

	private String getRoles(String rightName, String appender) {
		// Prepare the where clause.
		StringBuilder whereClause = new StringBuilder();
		whereClause.append(" RoleId in (");
		whereClause.append(" Select RoleId from SecRoleGroups where  GrpID in (");
		whereClause.append(" Select GrpID from SecGroups where GrpID in (");
		whereClause.append(" Select GrpID from SecGroupRights where RightId = (");
		whereClause.append(" Select RightId from SecRights where RightName = '" + rightName + "'))))");

		// Create the search object.
		Search search = new Search();
		search.setSearchClass(SecurityRole.class);
		search.addField("RoleCd");
		search.addTabelName("SecRoles");
		search.addWhereClause(whereClause.toString());

		// Get the results.
		List<SecurityRole> result = searchProcessor.getResults(search);

		StringBuilder data = new StringBuilder();

		for (SecurityRole object : result) {
			if (data.length() > 0) {
				data.append(appender);
			}

			data.append(object.getRoleCd());
		}

		return data.toString();
	}

	private String getGroups(String rightName, String appender) {
		// Prepare the where clause.
		StringBuilder whereClause = new StringBuilder();
		whereClause.append(" GrpID in (");
		whereClause.append(" Select GrpID from SecGroups where GrpID in (");
		whereClause.append(" Select GrpID from SecGroupRights where RightId = (");
		whereClause.append(" Select RightId from SecRights where RightName = '" + rightName + "')))");

		// Create the search object.
		Search search = new Search();
		search.setSearchClass(SecurityGroup.class);
		search.addField("GrpCode");
		search.addTabelName("SecGroups");
		search.addWhereClause(whereClause.toString());

		// Get the results.
		List<SecurityGroup> result = searchProcessor.getResults(search);

		StringBuilder data = new StringBuilder();

		for (SecurityGroup object : result) {
			if (data.length() > 0) {
				data.append(appender);
			}

			data.append(object.getGrpCode());
		}

		return data.toString();
	}

	/**
	 * Item renderer for listItems in the listBox.
	 */
	private class MenuRolesListModelItemRenderer implements ListitemRenderer<MenuItem> {
		@Override
		public void render(Listitem item, MenuItem data, int index) {
			Listcell lc;

			lc = new Listcell(org.zkoss.util.resource.Labels.getLabel(data.getId()));
			lc.setParent(item);

			lc = new Listcell();
			String strGroups = getGroups(data.getRightName(), "\n");
			Label label = new Label();
			label.setValue(strGroups);
			label.setMultiline(true);
			lc.appendChild(label);
			lc.setParent(item);

			lc = new Listcell();
			String strRoles = getRoles(data.getRightName(), "\n");
			Label label1 = new Label();
			label1.setValue(strRoles);
			label1.setMultiline(true);
			lc.appendChild(label1);
			lc.setParent(item);
		}
	}
}
