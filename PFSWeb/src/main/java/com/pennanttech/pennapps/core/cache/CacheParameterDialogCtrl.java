package com.pennanttech.pennapps.core.cache;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Window;

import com.pennant.util.Constraint.PTNumberValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.cache.CacheAdmin;
import com.pennanttech.pennapps.core.cache.CacheStats;
import com.pennanttech.pennapps.web.util.MessageUtil;

public class CacheParameterDialogCtrl extends GFCBaseCtrl<CacheStats> {
	private static final long serialVersionUID = -210929672381582779L;
	private static final Logger logger = Logger.getLogger(CacheParameterDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting auto wired by
	 * our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_CacheProcessDialog;

	protected Intbox nodeCount;
	protected Longbox sleepTime;
	protected Longbox verifyTime;

	private transient CacheNodesListCtrl cacheProcessListCtrl;

	private transient CacheAdmin cacheAdmin;

	/**
	 * default constructor.<br>
	 */
	public CacheParameterDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "CacheParametersDialog";
	}

	@SuppressWarnings("unchecked")
	public void onCreate$window_CacheProcessDialog(Event event) throws Exception {
		logger.debug("Entering");

		setPageComponents(window_CacheProcessDialog);

		try {

			if (arguments.containsKey("cacheProcessListCtrl")) {
				setCacheProcessListCtrl((CacheNodesListCtrl) arguments.get("cacheProcessListCtrl"));
			} else {
				setCacheProcessListCtrl(null);
			}
			if (arguments.containsKey("mapData")) {
				Map<String, Object> mapData = ((Map<String, Object>) arguments.get("mapData"));
				doWriteBeanToComponents(mapData);
			}

			setDialog(DialogType.EMBEDDED);
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_CacheProcessDialog.onClose();
		}
		logger.debug("Leaving" + event.toString());
	}

	public void onClick$btnSave(Event event) throws InterruptedException {
		logger.debug(event.toString());
		doSave();
		logger.debug("Leaving" + event.toString());
	}

	public void onClick$btnClose(Event event) {
		doClose(this.btnSave.isVisible());
	}

	public void onChange$nodeCount(Event event) throws InterruptedException {
		this.btnSave.setVisible(true);
	}

	public void onChange$sleepTime(Event event) throws InterruptedException {
		this.btnSave.setVisible(true);
	}

	public void onChange$verifyTime(Event event) throws InterruptedException {
		this.btnSave.setVisible(true);
	}

	public void doWriteBeanToComponents(Map<String, Object> arguments) {
		logger.debug("Entering ");
		for (String key : arguments.keySet()) {
			if (StringUtils.contains(key, "node_count")) {
				this.nodeCount.setValue((int) arguments.get(key));
			} else if (StringUtils.contains(key, "cache_verify_sleep")) {
				this.verifyTime.setValue((long) arguments.get(key));
			} else if (StringUtils.contains(key, "cache_update_sleep")) {
				this.sleepTime.setValue((long) arguments.get(key));
			}
		}

		logger.debug("Leaving ");
	}

	public void doWriteComponentsToBean(CacheStats cacheStats) {
		logger.debug("Entering ");

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			cacheStats.setNodeCount(this.nodeCount.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			cacheStats.setUpdateSleepTime(this.sleepTime.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			cacheStats.setVerifySleepTime(this.verifyTime.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		doRemoveValidation();

		if (wve.size() > 0) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		logger.debug("Leaving ");

	}

	private void doSetValidation() {
		logger.debug("Entering ");

		if (!this.verifyTime.isReadonly()) {
			this.verifyTime.setConstraint(new PTNumberValidator("Verify Time", true, false, 999999999));
		}
		if (!this.sleepTime.isReadonly()) {
			this.sleepTime.setConstraint(new PTNumberValidator("Sleep Time", true, false, 999999999));
		}
		if (!this.nodeCount.isReadonly()) {
			this.nodeCount.setConstraint(new PTNumberValidator("Node Count", true, false, 999));
		}
		logger.debug("Leaving ");
	}

	private void doRemoveValidation() {
		logger.debug("Entering ");
		this.nodeCount.setConstraint("");
		this.sleepTime.setConstraint("");
		this.verifyTime.setConstraint("");
		logger.debug("Leaving ");
	}

	private void refreshList() {
		getCacheProcessListCtrl().dofillCacheNodes();
	}

	public void doSave() throws InterruptedException {
		logger.debug("Entering ");
		final CacheStats cacheStats = new CacheStats();
		doSetValidation();
		doWriteComponentsToBean(cacheStats);

		cacheStats.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		cacheStats.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		getCacheAdmin().updateParameters(cacheStats);
		refreshList();
		closeDialog();
		logger.debug("Leaving");
	}

	public CacheNodesListCtrl getCacheProcessListCtrl() {
		return cacheProcessListCtrl;
	}

	public void setCacheProcessListCtrl(CacheNodesListCtrl cacheProcessListCtrl) {
		this.cacheProcessListCtrl = cacheProcessListCtrl;
	}

	public CacheAdmin getCacheAdmin() {
		return cacheAdmin;
	}

	public void setCacheAdmin(CacheAdmin cacheAdmin) {
		this.cacheAdmin = cacheAdmin;
	}

}
