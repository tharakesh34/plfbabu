package com.pennant.web.reloadresources;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Window;

import com.pennant.app.util.MasterDefUtil;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.ApplicationStartup;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the /WEB-INF/pages/ReloadResources/ReloadResource.zul<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class ReloadResourceCtrl extends GFCBaseListCtrl<String> {
	private static final long serialVersionUID = 1L;

	protected Window window_ReloadConfig;
	protected Borderlayout borderLayout_ReloadConfig;
	protected Paging pagingReloadConfig;
	protected Listbox listBoxReloadConfig;
	protected Button btnRefresh;

	/**
	 * default constructor.<br>
	 */
	public ReloadResourceCtrl() {
		super();
	}

	public void onCreate$window_ReloadConfig(Event event) {
		logger.debug(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(window_ReloadConfig, borderLayout_ReloadConfig, listBoxReloadConfig, pagingReloadConfig);
		doRenderPage();

		Listitem item = null;
		Listcell cell = null;
		Label label = null;

		/* Master Definitions */
		item = new Listitem();
		cell = new Listcell();
		label = new Label("Master Definitions");
		label.setParent(cell);
		cell.setParent(item);

		cell = new Listcell();
		Button button = new Button();
		button.setLabel("Reload");
		button.addForward("onClick", self, "onClick_button_masterDef");
		button.setParent(cell);
		cell.setParent(item);
		listBoxReloadConfig.appendChild(item);

		/* Custom labels */
		item = new Listitem();
		cell = new Listcell();
		label = new Label("Custom labels");
		label.setParent(cell);
		cell.setParent(item);

		cell = new Listcell();
		button = new Button();
		button.setLabel("Reload");
		button.addForward("onClick", self, "onClick_button_labels");
		button.setParent(cell);
		cell.setParent(item);
		listBoxReloadConfig.appendChild(item);

		/* Custom regular expressions */
		item = new Listitem();
		cell = new Listcell();
		label = new Label("Custom regular expressions");
		label.setParent(cell);
		cell.setParent(item);

		cell = new Listcell();
		button = new Button();
		button.setLabel("Reload");
		button.addForward("onClick", self, "onClick_button_regex");
		button.setParent(cell);
		cell.setParent(item);
		listBoxReloadConfig.appendChild(item);

		logger.debug(Literal.LEAVING);
	}

	public void onClick_button_masterDef(Event event) {
		MasterDefUtil.loadMasterDef();
		MessageUtil.showMessage("Master Definitions reloaded successfully.");
	}

	public void onClick_button_labels(Event event) {
		ApplicationStartup.loadCustomLabels();
		MessageUtil.showMessage("Label's reloaded successfully.");
	}

	public void onClick_button_regex(Event event) {
		ApplicationStartup.loadCustomRegex();
		MessageUtil.showMessage("Regular expression's reloaded successfully.");
	}
}
