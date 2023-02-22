package com.pennant.menuroles;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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

import com.pennant.webui.util.GFCBaseListCtrl;
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

	private void createFile() {
		int rowIndex = 0;
		String name = "Menu Roles and Rights";
		Sheet sheet = null;

		try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream stream = new ByteArrayOutputStream();) {
			sheet = workbook.createSheet(name);

			Row row = sheet.createRow((int) rowIndex++);

			Cell cell = row.createCell(0);
			cell.setCellValue("Menu");

			cell = row.createCell(1);
			cell.setCellValue("Level1");

			cell = row.createCell(2);
			cell.setCellValue("Level2");

			cell = row.createCell(3);
			cell.setCellValue("Level3");

			cell = row.createCell(4);
			cell.setCellValue("Right");

			cell = row.createCell(5);
			cell.setCellValue("Groups");

			cell = row.createCell(6);
			cell.setCellValue("Roles");

			for (MenuItem menuItem : MainMenu.getMenuItems()) {
				rowIndex = createMenu(rowIndex, 0, sheet, menuItem);
			}

			workbook.write(stream);

			Filedownload.save(new AMedia(name, "xlsx", DocType.XLSX.getContentType(), stream.toByteArray()));
		} catch (IOException e) {
			logger.error(Literal.EXCEPTION, e);
			MessageUtil.showMessage(
					"Unable to generate the file. Please try again later or contact the system administrator.");
		}
	}

	private int createMenu(int rowIndex, int celIndex, Sheet sheet, MenuItem menuItem) {
		Row row = sheet.createRow((int) rowIndex++);
		if (menuItem instanceof Menu) {
			Cell cell = row.createCell(celIndex++);
			cell.setCellValue(org.zkoss.util.resource.Labels.getLabel(menuItem.getId()));

			cell = row.createCell(4);
			cell.setCellValue(menuItem.getRightName());

			if (StringUtils.isNotEmpty(menuItem.getRightName())) {
				cell = row.createCell(5);
				cell.setCellValue(getGroups(menuItem.getRightName(), ","));
			}

			if (StringUtils.isNotEmpty(menuItem.getRightName())) {
				cell = row.createCell(6);
				cell.setCellValue(getRoles(menuItem.getRightName(), ","));
			}

			Menu menu = (Menu) menuItem;
			for (MenuItem item : menu.getItems()) {
				rowIndex = createMenu(rowIndex, celIndex, sheet, item);
			}
		} else {
			Cell cell = row.createCell(celIndex++);
			cell.setCellValue(org.zkoss.util.resource.Labels.getLabel(menuItem.getId()));

			cell = row.createCell(4);
			cell.setCellValue(menuItem.getRightName());

			if (StringUtils.isNotEmpty(menuItem.getRightName())) {
				cell = row.createCell(5);
				cell.setCellValue(getGroups(menuItem.getRightName(), ","));
			}

			if (StringUtils.isNotEmpty(menuItem.getRightName())) {
				cell = row.createCell(6);
				cell.setCellValue(getRoles(menuItem.getRightName(), ","));
			}
		}

		return rowIndex;
	}

	private String getRoles(String rightName, String appender) {
		StringBuilder data = new StringBuilder();
		StringBuilder sql = new StringBuilder();
		Search search = new Search();
		search.addField("RoleCd");
		search.addTabelName("SecRoles");
		sql.append(" RoleId in (");
		sql.append(" Select RoleId from SecRoleGroups where  GrpID in (");
		sql.append(" Select GrpID from SecGroups where GrpID in (");
		sql.append(" Select GrpID from SecGroupRights where RightId = (");
		sql.append(" Select RightId from SecRights where RightName ='" + rightName + "'))))");
		search.addWhereClause(sql.toString());

		List<Object> result = searchProcessor.getResults(search);

		for (Object object : result) {
			Map<String, Object> map = (Map) object;
			if (data.length() > 0) {
				data.append(appender);
			}

			data.append(map.get("rolecd"));
		}

		return data.toString();
	}

	private String getGroups(String rightName, String appender) {
		StringBuilder data = new StringBuilder();
		StringBuilder sql = new StringBuilder();
		Search search = new Search();
		search.addField("GrpCode");
		search.addTabelName("SecGroups");
		sql.append(" GrpID in (");
		sql.append(" Select GrpID from SecGroups where GrpID in (");
		sql.append(" Select GrpID from SecGroupRights where RightId = (");
		sql.append(" Select RightId from SecRights where RightName ='" + rightName + "')))");
		search.addWhereClause(sql.toString());

		List<Object> result = searchProcessor.getResults(search);

		for (Object object : result) {
			Map<String, Object> map = (Map) object;
			if (data.length() > 0) {
				data.append(appender);
			}

			data.append(map.get("grpcode"));
		}

		return data.toString();
	}

	/**
	 * Item renderer for listItems in the listBox.
	 */
	private class MenuRolesListModelItemRenderer implements ListitemRenderer<MenuItem>, Serializable {
		private static final long serialVersionUID = 1L;

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
