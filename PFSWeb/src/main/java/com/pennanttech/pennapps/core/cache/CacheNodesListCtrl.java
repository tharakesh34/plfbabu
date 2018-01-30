package com.pennanttech.pennapps.core.cache;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Window;

import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.core.cache.CacheAdmin;
import com.pennanttech.pennapps.core.cache.CacheStats;
import com.pennanttech.pennapps.web.util.MessageUtil;

public class CacheNodesListCtrl extends GFCBaseListCtrl<CacheStats> {
	private static final long serialVersionUID = 485796535935527728L;
	private static final Logger logger = Logger.getLogger(CacheNodesListCtrl.class);

	protected Window window_CacheNodesList;
	protected Borderlayout borderLayout_CacheNodes;
	protected Paging pagingCacheNodes;
	protected Listbox listBoxCacheNodes;
	protected Listbox listBoxCacheParameter;

	private CacheAdmin cacheAdmin;
	private List<CacheStats> cacheStats;

	public CacheNodesListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "CacheStats";
		super.tableName = "CACHE_PARAMETERS";
		super.queueTableName = "CACHE_PARAMETERS";
	}

	public void onCreate$window_CacheNodesList(Event event) {
		logger.debug("Entering" + event.toString());

		setPageComponents(window_CacheNodesList, borderLayout_CacheNodes, listBoxCacheNodes,
				pagingCacheNodes);
		doRenderPage();

		int divKycHeight = this.borderLayoutHeight - 80;
		int semiBorderlayoutHeights = divKycHeight / 2;
		this.listBoxCacheNodes.setHeight(semiBorderlayoutHeights - 350 + "px");

		dofillCacheNodes();
		doFillCacheParameter(getCacheAdmin().getCacheList());

		logger.debug("Leaving" + event.toString());
	}

	public void dofillCacheNodes() {
		logger.debug("Entering");

		Map<String, Object> cacheParams = getCacheAdmin().getParameters();
		this.listBoxCacheNodes.getItems().clear();

		Listitem item = new Listitem();
		Listcell lc;
		for (String key : cacheParams.keySet()) {
			lc = new Listcell(cacheParams.get(key).toString());
			lc.setParent(item);
			item.setAttribute("data", cacheParams);
		}
		ComponentsCtrl.applyForward(item, "onDoubleClick=onCacheAdministrationItemDoubleClicked");
		this.listBoxCacheNodes.appendChild(item);
		doFillCacheParameter(getCacheAdmin().getCacheList());

		logger.debug("Leaving");
	}

	public void doFillCacheParameter(List<CacheStats> cacheStats) {
		logger.debug("Entering");

		this.listBoxCacheParameter.getItems().clear();

		if (cacheStats != null && !cacheStats.isEmpty()) {
			Listitem item = null;
			Listcell lc;
			for (CacheStats stats : cacheStats) {
				item = new Listitem();
				lc = new Listcell(stats.getClusterName());
				lc.setParent(item);
				lc = new Listcell(stats.getCurrentNode());
				lc.setParent(item);
				lc = new Listcell(stats.getClusterIp());
				lc.setParent(item);
				if (stats.isActive()) {
					lc = new Listcell(stats.getManagerCacheStatus());
					lc.setStyle("color:#00FF00");
					lc.setParent(item);
				} else {
					lc = new Listcell(stats.getManagerCacheStatus());
					lc.setParent(item);
				}

				lc = new Listcell();
				final Checkbox enable = new Checkbox();
				enable.setDisabled(true);
				enable.setChecked(stats.isEnabled());
				lc.appendChild(enable);
				lc.setParent(item);

				lc = new Listcell();
				final Checkbox active = new Checkbox();
				active.setDisabled(true);
				active.setChecked(stats.isActive());
				if(stats.isActive()){
					lc.setSclass("checklistboxgreen");
				}else{
					lc.setSclass("checklistboxred");
				}
				lc.appendChild(active);
				lc.setParent(item);

				item.setAttribute("data", stats);
				ComponentsCtrl.applyForward(item, "onDoubleClick=onCacheStatsItemDoubleClicked");
				this.listBoxCacheParameter.appendChild(item);
			}
		}

		logger.debug("Leaving");
	}

	public void onTimer$timer(Event event) {
		logger.debug("Entering" + event.toString());

		doFillCacheParameter(getCacheAdmin().getCacheList());

		logger.debug("Leaving" + event.toString());
	}

	/**
	 * The framework calls this event handler when user clicks the refresh
	 * button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnRefresh(Event event) {
		logger.debug("Entering" + event.toString());

		doFillCacheParameter(getCacheAdmin().getCacheList());

		logger.debug("Leaving" + event.toString());
	}

	@SuppressWarnings("unchecked")
	public void onCacheAdministrationItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		final Listitem item = this.listBoxCacheNodes.getSelectedItem();

		if (item != null) {
			final HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("cacheProcessListCtrl", this);
			map.put("mapData", (HashMap<String, Object>) item.getAttribute("data"));
			try {
				Executions.createComponents("/WEB-INF/pages/Cache/CacheParameterDialog.zul",
						window_CacheNodesList, map);
			} catch (Exception e) {
				MessageUtil.showError(e);
			}
		}

		logger.debug("Leaving" + event.toString());
	}

	public CacheAdmin getCacheAdmin() {
		return cacheAdmin;
	}

	public void setCacheAdmin(CacheAdmin cacheAdmin) {
		this.cacheAdmin = cacheAdmin;
	}

	public List<CacheStats> getCacheStats() {
		return cacheStats;
	}

	public void setCacheStats(List<CacheStats> cacheStats) {
		this.cacheStats = cacheStats;
	}

}