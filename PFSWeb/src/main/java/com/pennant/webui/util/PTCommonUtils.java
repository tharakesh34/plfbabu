package com.pennant.webui.util;

import java.io.Serializable;
import java.util.Collections;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.Panel;
import org.zkoss.zul.Panelchildren;
import org.zkoss.zul.Tabpanel;

/**
 * Common methods.<br>
 * 
 * 1. createTabPanelContent / creates the gui module in a tabpanel.<br>
 * 
 * @author sge
 * 
 */
public class PTCommonUtils implements Serializable {

	private static final long serialVersionUID = 1L;

	private PTCommonUtils() {
	    super();
	}

	/**
	 * EN: Creates the TapPanels Content from a loaded zul-template. The caller mainController can be overhanded.
	 * 
	 * @param tabPanelID
	 * @param mainCtrl
	 * @param mainCtrlName
	 * @param zulFilePathName
	 */
	public static void createTabPanelContent(Tabpanel tabPanelID, Object mainCtrl, String mainCtrlName,
			String zulFilePathName) {

		if (tabPanelID != null && mainCtrl != null && !StringUtils.isEmpty(mainCtrlName)
				&& !StringUtils.isEmpty(zulFilePathName)) {

			// overhanded this controller self in the paramMap
			final Map<String, Object> map = Collections.singletonMap(mainCtrlName, mainCtrl);

			// clears the old content
			tabPanelID.getChildren().clear();

			// TabPanel acepts only a Panel/PanelChildren
			final Panel panel = new Panel();
			final Panelchildren pChildren = new Panelchildren();

			panel.appendChild(pChildren);
			tabPanelID.appendChild(panel);

			// call the zul-file and put it on the tab.
			Executions.createComponents(zulFilePathName, pChildren, map);
		}
	}

}
