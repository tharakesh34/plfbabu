package com.pennant.webui.collections;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Image;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Timer;
import org.zkoss.zul.Window;

import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.collection.Collection;
import com.pennant.backend.service.collection.CollectionService;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.constraint.PTListValidator;
import com.pennanttech.bajaj.process.collections.model.CollectionConstants;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.external.CollectionProcess;

/**
 * This is the controller class for the /WEB-INF/pages/DataExtraction/DataExtractionList.zul file.
 * 
 */
public class CollectionDialogCtrl extends GFCBaseCtrl<Collection> {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(CollectionDialogCtrl.class);

	protected Window window_CollectionDialog;
	protected Borderlayout borderLayout_CollectionDialog;

	protected Listbox listBoxCollections;
	protected Button btn_Start;
	protected Button btn_Restart;
	protected Combobox interfaceName;
	protected Timer timer;

	private static ArrayList<ValueLabel> collections;
	private List<ValueLabel> collectionTablesList = getCollectionTableNames();
	private transient CollectionService collectionService;
	private transient CollectionProcess collectionProcess;

	protected int curOdDays = 0;

	/**
	 * default constructor.<br>
	 */
	public CollectionDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "CollectionDialog";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_CollectionDialog(Event event) {
		logger.debug(Literal.ENTERING);

		try {
			setPageComponents(this.window_CollectionDialog);

			fillComboBox(this.interfaceName, CollectionConstants.INTERFACE_COLLECTION, this.collectionTablesList, "");
			readOnlyComponent(true, this.interfaceName);
			this.borderLayout_CollectionDialog.setHeight(getBorderLayoutHeight());

			// it will run when we run the EOD
			int executionCount = doCheckCollectionsExecution();
			logger.debug("Execution Count " + executionCount);
			logger.debug("arguments.containsKey " + arguments.containsKey("EOD"));

			if (arguments.containsKey("EOD") && executionCount == 0) {
				logger.debug("Entgered Collections Process " + executionCount);
				doStartCollections("EOD");
			}

		} catch (Exception e) {
			logger.error("Exception: ", e);
			MessageUtil.showError(e);
			closeDialog();
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * timer event
	 * 
	 * @param event
	 */
	public int doCheckCollectionsExecution() {
		logger.debug("Entering");

		int count = getCollectionService().getCollectionExecutionSts();

		if (count > 0) {
			this.btn_Start.setDisabled(true);
		} else {
			this.btn_Start.setDisabled(false);
			this.timer.stop();
		}

		List<Collection> collectionsList = getCollectionService().getCollectionTablesList();
		doFillCollectionsList(collectionsList);

		logger.debug("Leaving");
		return count;
	}

	/**
	 * timer event
	 * 
	 * @param event
	 */
	public void onTimer$timer(Event event) {
		this.timer.setDelay(1000);
		doCheckCollectionsExecution();
	}

	/**
	 * fill the Collection tables list with status
	 * 
	 * @param collections
	 */
	public void doFillCollectionsList(List<Collection> collections) {
		logger.debug("Entering");

		this.listBoxCollections.getItems().clear();

		if (collections != null && !collections.isEmpty()) {

			for (Collection collection : collections) {
				Listitem item = new Listitem();
				Listcell lc;

				// Table Name
				lc = new Listcell(collection.getTableName());
				lc.setParent(item);

				// Status
				lc = new Listcell(collection.getStatus());
				if (StringUtils.equals("FAILED", collection.getStatus())) {
					Image imgFail = new Image("/images/icons/ErrorFile.png");
					imgFail.setStyle("cursor:hand;cursor:pointer");
					imgFail.addForward("onClick", window_CollectionDialog, "onClickError", collection);
					imgFail.setParent(lc);
					if (timer.isRunning()) {
						timer.stop();
					}
					this.btn_Start.setDisabled(false);
				}
				lc.setParent(item);

				// Inserted count
				if (StringUtils.isBlank(collection.getStatus())) {
					lc = new Listcell("");
				} else {
					lc = new Listcell(String.valueOf(collection.getInsertCount()));
				}
				lc.setParent(item);

				this.listBoxCollections.appendChild(item);
			}
		}

		logger.debug("Leaving");
	}

	public void onClickError(ForwardEvent event) {
		logger.debug(Literal.ENTERING);

		Collection collections = (Collection) event.getData();

		if (collections != null && collections.getErrorMessage() != null) {
			Filedownload.save(collections.getErrorMessage(), "text/plain", collections.getTableName());
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Start the process
	 */
	public void onClick$btn_Start(Event event) {
		logger.debug(Literal.ENTERING);
		doStartCollections("");
		logger.debug("Leaving");
	}

	/**
	 * Start the process
	 */
	public void doStartCollections(String event) {
		logger.debug(Literal.ENTERING);

		if (this.collectionProcess != null) {
			this.btn_Start.setDisabled(true);

			String interfaceValue = doWriteComponentsToBean();
			this.timer.start();

			/**
			 * Both EOD and Manual Collection Run will get the data based on the LastBusinessdate only Micro EOD
			 * completed successfully and any Month end activity failed then application date changed </br>
			 * But Collection Extraction need to be run on LastBusinessdate only.
			 */

			CollectionProcessThread collectionProcessThread = null;
			try {
				collectionProcessThread = new CollectionProcessThread(interfaceValue, this.curOdDays,
						SysParamUtil.getLastBusinessdate(), getUserWorkspace().getLoggedInUser().getUserId());
				new Thread(new CollectionProcessThread(collectionProcessThread)).start();
				Thread.sleep(1000);
			} catch (Exception e) {
				logger.error(Literal.EXCEPTION, e);
				throw new InterfaceException("Collection", e.getMessage());
			}

			this.timer.setDelay(1000);
		}

		logger.debug("Leaving");
	}

	/**
	 * restart the process
	 */
	public void onClick$btn_Restart(Event event) {
		logger.debug(Literal.ENTERING);

		doWriteComponentsToBean();

		logger.debug("Leaving");
	}

	/**
	 * Validate the input parameters and process
	 */
	private String doWriteComponentsToBean() {
		logger.debug(Literal.ENTERING);

		String collections = null;

		doSetValidations();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			collections = getComboboxValue(this.interfaceName);
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

		logger.debug("Leaving");

		return collections;
	}

	/**
	 * Set the component validations.
	 */
	private void doSetValidations() {
		logger.debug(Literal.ENTERING);

		this.interfaceName.setConstraint(new PTListValidator<ValueLabel>(
				Labels.getLabel("label_CollectionDialog_InterfaceName.value"), this.collectionTablesList, true));

		logger.debug("Leaving");

	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug(Literal.ENTERING);

		this.interfaceName.setConstraint("");

		logger.debug("Leaving");
	}

	public static List<ValueLabel> getCollectionTableNames() {
		if (collections == null) {
			collections = new ArrayList<>(1);
			collections.add(new ValueLabel(CollectionConstants.INTERFACE_COLLECTION,
					Labels.getLabel("label_CollectionList_Collection")));
		}
		return collections;
	}

	/**
	 * Clears the components values. <br>
	 */
	public void doClear() {
		fillComboBox(this.interfaceName, "", this.collectionTablesList, "");
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public CollectionService getCollectionService() {
		return collectionService;
	}

	public void setCollectionService(CollectionService collectionService) {
		this.collectionService = collectionService;
	}

	public CollectionProcess getCollectionProcess() {
		return collectionProcess;
	}

	public void setCollectionProcess(CollectionProcess collectionProcess) {
		this.collectionProcess = collectionProcess;
	}

	public class CollectionProcessThread implements Runnable {
		private String interfaceValue;
		private int curOdDays;
		private Date appDate;
		private long lastMntBy;

		public CollectionProcessThread(Object... objects) {
			this.interfaceValue = (String) objects[0];
			this.curOdDays = (int) objects[1];
			this.appDate = (Date) objects[2];
			this.lastMntBy = (long) objects[3];
		}

		@Override
		public void run() {
			collectionProcess.process(interfaceValue, curOdDays, appDate, lastMntBy);
		}
	}

}