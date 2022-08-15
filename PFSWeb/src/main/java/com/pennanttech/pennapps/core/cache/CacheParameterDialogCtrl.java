package com.pennanttech.pennapps.core.cache;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Window;

import com.pennant.util.Constraint.PTNumberValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;

public class CacheParameterDialogCtrl extends GFCBaseCtrl<CacheStats> {
	private static final long serialVersionUID = -210929672381582779L;
	private static final Logger logger = LogManager.getLogger(CacheParameterDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting auto wired by our 'extends GFCBaseCtrl' GenericForwardComposer.
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
	public void onCreate$window_CacheProcessDialog(Event event) {
		logger.debug(Literal.ENTERING);

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
		logger.debug(Literal.LEAVING);
	}

	public void onClick$btnSave(Event event) throws InterruptedException {
		logger.debug(Literal.ENTERING);
		doSave();
		logger.debug(Literal.LEAVING);
	}

	public void onClick$btnClose(Event event) {
		logger.debug(Literal.ENTERING);
		doClose(this.btnSave.isVisible());
		logger.debug(Literal.LEAVING);
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
		logger.debug(Literal.ENTERING);
		for (String key : arguments.keySet()) {
			if (StringUtils.contains(key, "NODE_COUNT")) {
				this.nodeCount.setValue((int) arguments.get(key));
			} else if (StringUtils.contains(key, "CACHE_VERIFY_SLEEP")) {
				this.verifyTime.setValue((long) arguments.get(key));
			} else if (StringUtils.contains(key, "CACHE_UPDATE_SLEEP")) {
				this.sleepTime.setValue((long) arguments.get(key));
			}
		}

		logger.debug(Literal.LEAVING);
	}

	public void doWriteComponentsToBean(CacheStats cacheStats) {
		logger.debug(Literal.ENTERING);

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

		logger.debug(Literal.LEAVING);

	}

	private void doSetValidation() {
		logger.debug(Literal.ENTERING);

		if (!this.verifyTime.isReadonly()) {
			this.verifyTime.setConstraint(new PTNumberValidator("Verify Time", true, false, 999999999));
		}
		if (!this.sleepTime.isReadonly()) {
			this.sleepTime.setConstraint(new PTNumberValidator("Sleep Time", true, false, 999999999));
		}
		if (!this.nodeCount.isReadonly()) {
			this.nodeCount.setConstraint(new PTNumberValidator("Node Count", true, false, 999));
		}
		logger.debug(Literal.LEAVING);
	}

	private void doRemoveValidation() {
		logger.debug(Literal.ENTERING);
		this.nodeCount.setConstraint("");
		this.sleepTime.setConstraint("");
		this.verifyTime.setConstraint("");
		logger.debug(Literal.LEAVING);
	}

	protected void refreshList() {
		getCacheProcessListCtrl().dofillCacheNodes();
	}

	public void doSave() throws InterruptedException {
		logger.debug(Literal.ENTERING);
		final CacheStats cacheStats = new CacheStats();
		doSetValidation();
		doWriteComponentsToBean(cacheStats);

		cacheStats.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		cacheStats.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		getCacheAdmin().updateParameters(cacheStats);
		refreshList();
		closeDialog();
		logger.debug(Literal.LEAVING);
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
