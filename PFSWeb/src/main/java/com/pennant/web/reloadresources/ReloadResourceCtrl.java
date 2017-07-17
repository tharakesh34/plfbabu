package com.pennant.web.reloadresources;

import java.io.File;
import java.io.Serializable;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Window;

import com.pennant.app.util.PathUtil;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.MessageUtil;
import com.pennanttech.interfacebajaj.FileDownloadListCtrl;
import com.pennanttech.pennapps.core.resource.Literal;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the /WEB-INF/pages/ReloadResources/ReloadResource.zul<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */


public class ReloadResourceCtrl extends GFCBaseListCtrl<String> implements Serializable   {

	private static final long	serialVersionUID	= 1L;
	private static final Logger	logger				= Logger.getLogger(FileDownloadListCtrl.class);
	
	protected Window			window_ReloadConfig;
	protected Borderlayout		borderLayout_ReloadConfig;
	protected Paging			pagingReloadConfig;
	protected Listbox			listBoxReloadConfig;
	protected Button			btnRefresh;
	
	/**
	 * default constructor.<br>
	 */
	public ReloadResourceCtrl() {
		super();
	}

	public void onCreate$window_ReloadConfig(Event event) throws Exception {
		logger.debug(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(window_ReloadConfig, borderLayout_ReloadConfig, listBoxReloadConfig, pagingReloadConfig);
		doRenderPage();

		String location = PathUtil.getPath(PathUtil.CONFIG) + File.separator + "i3-label.properties";
		location.replace('/', '\\');
		
		Listitem item = null;
		Listcell cell = null;
		Label label = null;

		item = new Listitem();

		cell = new Listcell();
		label = new Label("Custom labels");
		label.setParent(cell);
		cell.setParent(item);

		cell = new Listcell();
		label = new Label(location);
		label.setParent(cell);
		cell.setParent(item);

		cell = new Listcell();
		Button button = new Button();
		button.setLabel("Reload");
		button.addForward("onClick", self, "onClick_button");
		button.setParent(cell);
		cell.setParent(item);

		listBoxReloadConfig.appendChild(item);

		item = new Listitem();
		cell = new Listcell();
		label = new Label("Custom regular expressions");
		label.setParent(cell);
		cell.setParent(item);

		cell = new Listcell();
		label = new Label(PathUtil.getPath(PathUtil.CONFIG) + File.separator + "regex.properties");
		label.setParent(cell);
		cell.setParent(item);

		cell = new Listcell();
		Button button1 = new Button();
		button1.setLabel("Reload");
		button1.addForward("onClick", self, "onClick_button1");

		button1.setParent(cell);
		cell.setParent(item);

		listBoxReloadConfig.appendChild(item);

		logger.debug(Literal.LEAVING);
	}
	
	public void onClick_button(Event event) throws Exception {
		org.zkoss.util.resource.Labels.reset();
		MessageUtil.showMessage("Labels Reloaded.");

	}
	
	public void onClick_button1(Event event) throws Exception {
		org.zkoss.util.resource.Labels.reset();
		MessageUtil.showMessage("Labels Reloaded.");

	}
}
