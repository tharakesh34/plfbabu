/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. 
 * All components/modules/functions/classes/logic in this software, unless 
 * otherwise stated, the property of Pennant Technologies. 
 * 
 * Copyright and other intellectual property laws protect these materials. 
 * Reproduction or retransmission of the materials, in whole or in part, in any manner, 
 * without the prior written consent of the copyright holder, is a violation of 
 * copyright law.
 */

/**
 ********************************************************************************************
 *                                 FILE HEADER                                              *
 ********************************************************************************************
 *																							*
 * FileName    		:  WeekendMasterDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  11-07-2011    														*
 *                                                                  						*
 * Modified Date    :  11-07-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 11-07-2011       Pennant	                 0.1                                            * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
 */

package com.pennant.webui.smtmasters.weekendmaster;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.spring.SpringUtil;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.applicationmaster.Currency;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.smtmasters.WeekendMaster;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.smtmasters.WeekendMasterService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.util.ButtonStatusCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MultiLineMessageBox;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.pagging.PagedListWrapper;
import com.pennant.webui.util.searchdialogs.ExtendedSearchListBox;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/SolutionFactory/WeekendMaster/weekendMasterDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */

public class WeekendMasterDialogCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = -4145707224044632347L;
	private final static Logger logger = Logger.getLogger(WeekendMasterDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_WeekendMasterDialog; // autowired

	protected Textbox weekendCode; // autowired
	protected Textbox weekendDesc; // autowired
	protected Listbox weekend; // autowired
	protected Textbox weekendText; // autowired

	protected Label recordStatus; // autowired
	protected Radiogroup userAction;
	protected Groupbox gb;

	// not auto wired vars
	private WeekendMaster weekendMaster; // overhanded per param
	private transient WeekendMasterListCtrl weekendMasterListCtrl; // overhanded
	// per param

	// old value vars for edit mode. that we can check if something
	// on the values are edited since the last init.
	private transient String oldVar_weekendCode;
	private transient String oldVar_weekendDesc;
	private transient String oldVar_weekend;
	private transient String oldVar_recordStatus;

	private transient boolean validationOn;
	private boolean notes_Entered = false;

	// Button controller for the CRUD buttons
	private transient final String btnCtroller_ClassPrefix = "button_WeekendMasterDialog_";
	private transient ButtonStatusCtrl btnCtrl;
	protected Button btnNew; // autowire
	protected Button btnEdit; // autowire
	protected Button btnDelete; // autowire
	protected Button btnSave; // autowire
	protected Button btnCancel; // autowire
	protected Button btnClose; // autowire
	protected Button btnHelp; // autowire
	protected Button btnNotes; // autowire

	protected Button btnSearchCurrencyCode;// autowire

	private List<ValueLabel> weekendList = null;
	protected Paging paging;
	protected PagedListWrapper<ValueLabel> listWrapper;
	private Map<String, String> checkMap = new HashMap<String, String>();

	// ServiceDAOs / Domain Classes
	private transient WeekendMasterService weekendMasterService;
	private transient PagedListService pagedListService;

	/**
	 * default constructor.<br>
	 */
	public WeekendMasterDialogCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * zul-file is called with a parameter for a selected WeekendMaster object
	 * in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_WeekendMasterDialog(Event event)
			throws Exception {
		logger.debug(event.toString());

		/* set components visible dependent of the users rights */
		doCheckRights();
		setListWrapper();

		weekendList = PennantAppUtil.getWeekName();

		/* create the Button Controller. Disable not used buttons during working */
		this.btnCtrl = new ButtonStatusCtrl(getUserWorkspace(),
				this.btnCtroller_ClassPrefix, true, this.btnNew, this.btnEdit,
				this.btnDelete, this.btnSave, this.btnCancel, this.btnClose,
				this.btnNotes);

		// get the params map that are overhanded by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		// READ OVERHANDED params !
		if (args.containsKey("weekendMaster")) {
			this.weekendMaster = (WeekendMaster) args.get("weekendMaster");
			WeekendMaster befImage = new WeekendMaster();
			BeanUtils.copyProperties(this.weekendMaster, befImage);
			this.weekendMaster.setBefImage(befImage);

			setWeekendMaster(this.weekendMaster);
		} else {
			setWeekendMaster(null);
		}

		/*
		 * doLoadWorkFlow(this.weekendMaster.isWorkflow(),
		 * this.weekendMaster.getWorkflowId(),
		 * this.weekendMaster.getNextTaskId());
		 */

		if (isWorkFlowEnabled()) {
			this.userAction = setListRecordStatus(this.userAction);
			getUserWorkspace().alocateRoleAuthorities(getRole(),
					"WeekendMasterDialog");
		}

		// READ OVERHANDED params !
		// we get the weekendMasterListWindow controller. So we have access
		// to it and can synchronize the shown data when we do insert, edit or
		// delete weekendMaster here.
		if (args.containsKey("weekendMasterListCtrl")) {
			setWeekendMasterListCtrl((WeekendMasterListCtrl) args
					.get("weekendMasterListCtrl"));
		} else {
			setWeekendMasterListCtrl(null);
		}

		// set Field Properties
		doSetFieldProperties();
		doShowDialog(getWeekendMaster());
		logger.debug("Leaving onCreate$window_WeekendMasterDialog()");
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		// Empty sent any required attributes
		this.weekendCode.setMaxlength(3);
		this.weekendDesc.setMaxlength(50);
		this.weekend.setMaxlength(50);

		if (isWorkFlowEnabled()) {
			this.gb.setVisible(true);
		} else {
			this.gb.setVisible(false);
		}
		logger.debug("Leaving");
	}

	/**
	 * User rights check. <br>
	 * Only components are set visible=true if the logged-in <br>
	 * user have the right for it. <br>
	 * 
	 * The rights are get from the spring framework users grantedAuthority(). A
	 * right is only a string. <br>
	 */
	private void doCheckRights() {
		logger.debug("Entering doCheckRights()");
		getUserWorkspace().alocateAuthorities("WeekendMasterDialog");

		this.btnNew.setVisible(getUserWorkspace().isAllowed(
				"button_WeekendMasterDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed(
				"button_WeekendMasterDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed(
				"button_WeekendMasterDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed(
				"button_WeekendMasterDialog_btnSave"));
		this.btnCancel.setVisible(false);
		logger.debug("Leaving doCheckRights()");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++ Components events +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * If we close the dialog window. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClose$window_WeekendMasterDialog(Event event)
			throws Exception {
		logger.debug(event.toString());
		doClose();
	}

	/**
	 * when the "save" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnSave(Event event) throws InterruptedException {
		logger.debug(event.toString());

		doSave();
	}

	/**
	 * when the "edit" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnEdit(Event event) {
		logger.debug(event.toString());
		doEdit();
		doStoreInitValues();
	}

	/**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		logger.debug(event.toString());
		PTMessageUtils.showHelpWindow(event, window_WeekendMasterDialog);
	}

	/**
	 * when the "new" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnNew(Event event) {
		logger.debug(event.toString());
		doNew();
	}

	/**
	 * when the "delete" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnDelete(Event event) throws InterruptedException {
		logger.debug(event.toString());
		doDelete();
	}

	/**
	 * when the "cancel" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnCancel(Event event) {
		logger.debug(event.toString());
		doCancel();
	}

	/**
	 * when the "close" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnClose(Event event) throws InterruptedException {
		logger.debug(event.toString());

		try {
			doClose();
		} catch (final Exception e) {

			closeDialog(this.window_WeekendMasterDialog, "WeekendMaster");
		}
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++ GUI operations +++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Closes the dialog window. <br>
	 * <br>
	 * Before closing we check if there are unsaved changes in <br>
	 * the components and ask the user if saving the modifications. <br>
	 * 
	 * @throws InterruptedException
	 * 
	 */
	private void doClose() throws InterruptedException {
		logger.debug("Entering doClose()");
		if (isDataChanged()) {
			logger.debug("Data Changed(): True");

			// Show a confirm box
			final String msg = Labels
					.getLabel("message_Data_Modified_Save_Data_YesNo");
			final String title = Labels.getLabel("message.Information");

			MultiLineMessageBox.doSetTemplate();
			int conf = MultiLineMessageBox.show(msg, title,
					MultiLineMessageBox.YES | MultiLineMessageBox.NO,
					MultiLineMessageBox.QUESTION, true);

			if (conf == MultiLineMessageBox.YES) {
				logger.debug("doClose: Yes");
				doSave();
			} else {
				logger.debug("doClose: No");
			}
		} else {
			logger.debug("Data Changed(): false");
		}

		closeDialog(this.window_WeekendMasterDialog, "WeekendMaster");
		logger.debug("Leaving doClose()");
	}

	/**
	 * Cancel the actual operation. <br>
	 * <br>
	 * Resets to the original status.<br>
	 * 
	 */
	private void doCancel() {
		logger.debug("Entering doCancel()");
		doResetInitValues();
		doReadOnly();
		this.btnCtrl.setInitEdit();
		logger.debug("Leaving doCancel()");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aWeekendMaster
	 *            WeekendMaster
	 */
	public void doWriteBeanToComponents(WeekendMaster aWeekendMaster) {
		logger.debug("Entering doWriteBeanToComponents()");
		this.weekendCode.setValue(aWeekendMaster.getWeekendCode());
		this.weekendDesc.setValue(aWeekendMaster.getWeekendDesc());
		this.weekendText.setValue(aWeekendMaster.getWeekend());
		if (aWeekendMaster.isNewRecord()) {
			this.weekendCode.setValue("");
		} else {
			this.weekendCode.setValue(aWeekendMaster.getWeekendCode());
			StringTokenizer st = new StringTokenizer(
					aWeekendMaster.getWeekend(), ",");
			while (st.hasMoreTokens()) {
				String token = st.nextToken();
				checkMap.put(token, token);
			}
		}
		loadWeekEnd();
		logger.debug("Leaving doWriteBeanToComponents()");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aWeekendMaster
	 */
	public void doWriteComponentsToBean(WeekendMaster aWeekendMaster) {
		logger.debug("Entering doWriteComponentsToBean()");
		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			if (StringUtils.trimToEmpty(this.weekendCode.getValue()).equals("")) {
				throw new WrongValueException(
						this.weekendCode,
						Labels.getLabel(
								"FIELD_NO_EMPTY",
								new String[] { Labels
										.getLabel("label_WeekendMasterDialog_WeekendCode.value") }));
			}
			aWeekendMaster.setWeekendCode(this.weekendCode.getValue());

		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (StringUtils.trimToEmpty(this.weekendDesc.getValue()).equals("")) {
				throw new WrongValueException(
						this.weekendDesc,
						Labels.getLabel(
								"FIELD_NO_EMPTY",
								new String[] { Labels
										.getLabel("label_WeekendMasterDialog_WeekendDesc.value") }));
			}
			aWeekendMaster.setWeekendDesc(this.weekendDesc.getValue());

		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (this.weekendText.getValue().equals("")) {
				throw new WrongValueException(
						weekend,
						Labels.getLabel(
								"CHECK_NO_EMPTY",
								new String[] { Labels
										.getLabel("label_WeekendMasterDialog_Weekend.value") }));
			}
			if (this.weekendText.getValue().endsWith(",")) {
				aWeekendMaster
						.setWeekend(this.weekendText.getValue().substring(0,
								this.weekendText.getValue().length() - 1));
			} else {
				aWeekendMaster.setWeekend(this.weekendText.getValue());
			}

		} catch (WrongValueException we) {
			wve.add(we);
		}

		if (wve.size() > 0) {
			doRemoveValidation();
			doRemoveLOVValidation();
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		logger.debug("Leaving doWriteComponentsToBean()");

	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aWeekendMaster
	 * @throws InterruptedException
	 */
	public void doShowDialog(WeekendMaster aWeekendMaster)
			throws InterruptedException {
		logger.debug("Entering doShowDialog()");
		// if aWeekendMaster == null then we opened the Dialog without
		// args for a given entity, so we get a new Obj().
		if (aWeekendMaster == null) {
			/** !!! DO NOT BREAK THE TIERS !!! */
			// We don't create a new DomainObject() in the frontend.
			// We GET it from the backend.
			aWeekendMaster = getWeekendMasterService().getNewWeekendMaster();

			setWeekendMaster(aWeekendMaster);
		} else {
			setWeekendMaster(aWeekendMaster);
		}

		// set Readonly mode accordingly if the object is new or not.
		if (aWeekendMaster.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			btnCancel.setVisible(false);
			// setFocus
			this.weekendCode.focus();
		} else {
			this.weekendDesc.focus();
			if (isWorkFlowEnabled()) {
				this.btnNotes.setVisible(true);
				doEdit();
			} else {
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
			}
			btnSave.setVisible(true);
		}

		try {
			// fill the components with the data
			doWriteBeanToComponents(aWeekendMaster);
			// stores the inital data for comparing if they are changed
			// during user action.
			doStoreInitValues();
			setDialog(this.window_WeekendMasterDialog);
		} catch (final Exception e) {
			PTMessageUtils.showErrorMessage(e.toString());
			logger.error("doShowDialog() " + e.getMessage());
		}
		logger.debug("Leaving doShowDialog()");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++++++++ helpers ++++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Stores the init values in vars. <br>
	 */
	private void doStoreInitValues() {
		logger.debug("Entering doStoreInitValues()");
		this.oldVar_weekendCode = this.weekendCode.getValue();
		this.oldVar_weekendDesc = this.weekendDesc.getValue();
		this.oldVar_weekend = this.weekendText.getValue();
		logger.debug("Leaving doStoreInitValues()");
	}

	/**
	 * Resets the init values from mem vars. <br>
	 */
	private void doResetInitValues() {
		logger.debug("Entering doResetInitValues()");
		this.weekendCode.setValue(this.oldVar_weekendCode);
		this.weekendDesc.setValue(this.oldVar_weekendDesc);
		for (int i = 0; i < PennantAppUtil.getWeekName().size(); i++) {
			if (PennantAppUtil.getWeekName().get(i).equals(this.oldVar_weekend)) {
				this.weekend.setSelectedIndex(i);
			} else {
				this.weekend.setSelectedIndex(0);
			}
		}
		this.recordStatus.setValue(this.oldVar_recordStatus);

		if (isWorkFlowEnabled()) {
			this.userAction.setSelectedIndex(0);
		}
		logger.debug("Leaving doResetInitValues()");
	}

	/**
	 * Checks, if data are changed since the last call of <br>
	 * doStoreInitData() . <br>
	 * 
	 * @return true, if data are changed, otherwise false
	 */
	private boolean isDataChanged() {
		logger.debug("Entering isDataChanged()");
		boolean changed = false;

		if (this.oldVar_weekendCode != this.weekendCode.getValue()) {
			changed = true;
		}
		if (this.oldVar_weekendDesc != this.weekendDesc.getValue()) {
			changed = true;
		}
		if (this.oldVar_weekend != this.weekendText.getValue()) {
			changed = true;
		}
		logger.debug("Leaving isDataChanged()");
		return changed;

	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering doSetValidation()");
		setValidationOn(true);

		if (!this.weekendCode.isReadonly()) {
			this.weekendCode
					.setConstraint("NO EMPTY:"
							+ Labels.getLabel(
									"FIELD_NO_EMPTY",
									new String[] { Labels
											.getLabel("label_WeekendMasterDialog_WeekendCode.value") }));
		}
		if (!this.weekendDesc.isReadonly()) {
			this.weekendDesc
					.setConstraint("NO EMPTY:"
							+ Labels.getLabel(
									"FIELD_NO_EMPTY",
									new String[] { Labels
											.getLabel("label_WeekendMasterDialog_WeekendDesc.value") }));
		}
		logger.debug("Leaving doSetValidation()");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering doRemoveValidation()");
		setValidationOn(false);
		this.weekendCode.setConstraint("");
		this.weekendDesc.setConstraint("");
		this.weekend.setCheckmark(false);
		logger.debug("Leaving doRemoveValidation()");
	}

	// +++++++++++++++++++++++++ crud operations +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Deletes a WeekendMaster object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering doDelete()");
		final WeekendMaster aWeekendMaster = new WeekendMaster();
		BeanUtils.copyProperties(getWeekendMaster(), aWeekendMaster);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels
				.getLabel("message.Question.Are_you_sure_to_delete_this_record")
				+ "\n\n --> " + aWeekendMaster.getWeekendCode();
		final String title = Labels.getLabel("message.Deleting.Record");
		MultiLineMessageBox.doSetTemplate();

		int conf = (MultiLineMessageBox.show(msg, title,
				MultiLineMessageBox.YES | MultiLineMessageBox.NO,
				Messagebox.QUESTION, true));

		if (conf == MultiLineMessageBox.YES) {
			logger.debug("doDelete: Yes");

			tranType = PennantConstants.TRAN_DEL;

			/*
			 * if
			 * (StringUtils.trimToEmpty(aWeekendMaster.getRecordType()).equals(
			 * "")) { aWeekendMaster.setVersion(aWeekendMaster.getVersion() +
			 * 1);
			 * aWeekendMaster.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			 * 
			 * if (isWorkFlowEnabled()) { aWeekendMaster.setNewRecord(true);
			 * tranType = PennantConstants.TRAN_WF; } else { tranType =
			 * PennantConstants.TRAN_DEL; } }
			 */

			try {
				if (doProcess(aWeekendMaster, tranType)) {
					refreshList();
					final JdbcSearchObject<WeekendMaster> soWeekendMaster = getWeekendMasterListCtrl()
							.getSearchObj();
					// Set the ListModel
					getWeekendMasterListCtrl().getPagedListWrapper()
							.setSearchObject(soWeekendMaster);

					// now synchronize the WeekendMaster listBox
					final ListModelList lml = (ListModelList) getWeekendMasterListCtrl().listBoxWeekendMaster
							.getListModel();

					// Check if the WeekendMaster object is new or updated -1
					// means that the obj is not in the list, so it's new ..
					if (lml.indexOf(aWeekendMaster) == -1) {
					} else {
						lml.remove(lml.indexOf(aWeekendMaster));
					}
					closeDialog(this.window_WeekendMasterDialog, "WeekendMaster");
				}

			} catch (DataAccessException e) {
				showMessage(e);
			}
		}
		logger.debug("Leaving doDelete()");
	}

	/**
	 * Create a new WeekendMaster object. <br>
	 */
	private void doNew() {
		logger.debug("Entering doNew()");
		/** !!! DO NOT BREAK THE TIERS !!! */
		// we don't create a new WeekendMaster() in the frontend.
		// we get it from the backend.
		final WeekendMaster aWeekendMaster = getWeekendMasterService()
				.getNewWeekendMaster();
		aWeekendMaster.setNewRecord(true);
		setWeekendMaster(aWeekendMaster);
		doClear(); // clear all commponents
		doEdit(); // edit mode
		this.btnCtrl.setBtnStatus_New();

		// remember the old vars
		doStoreInitValues();

		// setFocus
		this.weekendCode.focus();
		logger.debug("Leaving doNew()");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering doEdit()");
		if (getWeekendMaster().isNewRecord()) {
			this.weekendCode.setReadonly(false);
			this.btnCancel.setVisible(false);
		} else {
			this.weekendCode.setReadonly(true);
			this.btnSearchCurrencyCode.setVisible(false);
			this.btnCancel.setVisible(true);
		}

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.weekendMaster.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			this.btnCtrl.setBtnStatus_Edit();
			btnCancel.setVisible(true);
		}
		logger.debug("Leaving doEdit()");
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering doReadOnly()");
		this.weekendCode.setReadonly(true);
		this.btnSearchCurrencyCode.setDisabled(true);
		this.weekendDesc.setReadonly(false);
		this.weekend.setDisabled(true);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(true);
			}
		}

		if (isWorkFlowEnabled()) {
			this.recordStatus.setValue("");
			this.userAction.setSelectedIndex(0);
		}
		logger.debug("Leaving doReadOnly()");
	}

	public void onClick$btnSearchCurrencyCode(Event event) {
		logger.debug("onClick$btnSearchCurrencyCode()");
		Object dataObject = ExtendedSearchListBox.show(
				this.window_WeekendMasterDialog, "Currency");
		if (dataObject instanceof String) {
			this.weekendCode.setValue(dataObject.toString());

		} else {
			Currency details = (Currency) dataObject;
			if (details != null) {
				this.weekendCode.setValue(details.getCcyCode());
				this.weekendDesc.setValue(details.getCcyDesc());
			}
		}
	}

	/**
	 * Clears the components values. <br>
	 */
	public void doClear() {
		logger.debug("Entering doClear()");
		// remove validation, if there are a save before
		this.weekendCode.setValue("");
		this.weekendDesc.setValue("");
		this.weekend.setSelectedIndex(0);
		logger.debug("Leaving doClear()");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering doSave()");
		final WeekendMaster aWeekendMaster = new WeekendMaster();
		BeanUtils.copyProperties(getWeekendMaster(), aWeekendMaster);
		boolean isNew = false;

		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// force validation, if on, than execute by component.getValue()
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		doSetValidation();
		// fill the WeekendMaster object with the components data
		doWriteComponentsToBean(aWeekendMaster);

		// Write the additional validations as per below example
		// get the selected branch object from the listbox
		// Do data level validations here

		isNew = aWeekendMaster.isNew();
		String tranType = "";

		/*
		 * { if (StringUtils.trimToEmpty(aWeekendMaster.getRecordType()).equals(
		 * "")) { aWeekendMaster.setVersion(aWeekendMaster.getVersion() + 1); if
		 * (isNew) {
		 * aWeekendMaster.setRecordType(PennantConstants.RECORD_TYPE_NEW); }
		 * else {
		 * aWeekendMaster.setRecordType(PennantConstants.RECORD_TYPE_UPD);
		 * aWeekendMaster.setNewRecord(true); } } } else
		 */
		// aWeekendMaster.setVersion(aWeekendMaster.getVersion() + 1);
		if (isNew) {
			tranType = PennantConstants.TRAN_ADD;
		} else {
			tranType = PennantConstants.TRAN_UPD;
		}

		// save it to database
		try {

			if (doProcess(aWeekendMaster, tranType)) {
				refreshList();
				aWeekendMaster.setNewRecord(false);
				doWriteBeanToComponents(aWeekendMaster);
				// ++ create the searchObject and init sorting ++ //
				final JdbcSearchObject<WeekendMaster> soWeekendMaster = getWeekendMasterListCtrl()
						.getSearchObj();

				// Set the ListModel
				getWeekendMasterListCtrl().pagingWeekendMasterList
						.setActivePage(0);
				getWeekendMasterListCtrl().getPagedListWrapper()
						.setSearchObject(soWeekendMaster);

				// call from cusromerList then synchronize the WeekendMaster
				// listBox
				if (getWeekendMasterListCtrl().listBoxWeekendMaster != null) {
					// now synchronize the WeekendMaster listBox
					getWeekendMasterListCtrl().listBoxWeekendMaster
							.getListModel();
				}

				doReadOnly();
				this.btnCtrl.setBtnStatus_Save();

				// Close the Existing Dialog
				closeDialog(this.window_WeekendMasterDialog, "WeekendMaster");
			}

		} catch (final DataAccessException e) {
			showMessage(e);
		}
		logger.debug("Leaving doSave()");
	}

	private boolean doProcess(WeekendMaster aWeekendMaster, String tranType) {
		logger.debug("Entering doProcess()");
		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aWeekendMaster.setLastMntBy(getUserWorkspace().getLoginUserDetails()
				.getLoginUsrID());
		aWeekendMaster.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aWeekendMaster.setUserDetails(getUserWorkspace().getLoginUserDetails());

		if (isWorkFlowEnabled()) {
			String taskId = getWorkFlow().getTaskId(getRole());
			String nextTaskId = "";
			// aWeekendMaster.setRecordStatus(userAction.getSelectedItem().getValue());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				// nextTaskId =
				// StringUtils.trimToEmpty(aWeekendMaster.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getWorkFlow().getNextTaskIds(taskId,
							aWeekendMaster);
				}

				if (PennantConstants.WF_Audit_Notes.equals(getWorkFlow()
						.getAuditingReq(taskId, aWeekendMaster))) {
					try {
						if (!isNotes_Entered()) {
							PTMessageUtils.showErrorMessage(Labels
									.getLabel("Notes_NotEmpty"));
							return false;
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}

			if (!StringUtils.trimToEmpty(nextTaskId).equals("")) {
				nextRoleCode = getWorkFlow().firstTask.owner;
				String[] nextTasks = nextTaskId.split(";");

				if (nextTasks != null && nextTasks.length > 0) {
					for (int i = 0; i < nextTasks.length; i++) {

						if (nextRoleCode.length() > 1) {
							nextRoleCode = nextRoleCode + ",";
						}
						nextRoleCode = getWorkFlow().getTaskOwner(nextTasks[i]);
					}
				} else {
					nextRoleCode = getWorkFlow().getTaskOwner(nextTaskId);
				}
			}

			/*
			 * aWeekendMaster.setTaskId(taskId);
			 * aWeekendMaster.setNextTaskId(nextTaskId);
			 * aWeekendMaster.setRoleCode(getRole());
			 * aWeekendMaster.setNextRoleCode(nextRoleCode);
			 */

			auditHeader = getAuditHeader(aWeekendMaster, tranType);

			String operationRefs = getWorkFlow().getOperationRefs(taskId,
					aWeekendMaster);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aWeekendMaster,
							PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
				}
			}
		} else {

			auditHeader = getAuditHeader(aWeekendMaster, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}
		logger.debug("Leaving doProcess()");
		return processCompleted;
	}

	private boolean doSaveProcess(AuditHeader auditHeader, String method) {
		logger.debug("Entering doSaveProcess()");
		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		// WeekendMaster aWeekendMaster = (WeekendMaster)
		// auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;
		try {

			while (retValue == PennantConstants.porcessOVERIDE) {

				if (StringUtils.trimToEmpty(method).equalsIgnoreCase("")) {
					if (auditHeader.getAuditTranType().equals(
							PennantConstants.TRAN_DEL)) {
						auditHeader = getWeekendMasterService().delete(
								auditHeader);
						deleteNotes = true;
					} else {
						auditHeader = getWeekendMasterService().saveOrUpdate(
								auditHeader);
					}

				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(
							PennantConstants.method_doApprove)) {
						auditHeader = getWeekendMasterService().doApprove(
								auditHeader);

						/*
						 * if(aWeekendMaster.getRecordType().equals(PennantConstants
						 * .RECORD_TYPE_DEL)){ deleteNotes=true; }
						 */

					} else if (StringUtils.trimToEmpty(method)
							.equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = getWeekendMasterService().doReject(
								auditHeader);

						/*
						 * if(aWeekendMaster.getRecordType().equals(PennantConstants
						 * .RECORD_TYPE_NEW)){ deleteNotes=true; }
						 */

					} else {
						auditHeader.setErrorDetails(new ErrorDetails(
								PennantConstants.ERR_9999, Labels
										.getLabel("InvalidWorkFlowMethod"),
								null));
						retValue = ErrorControl.showErrorControl(
								this.window_WeekendMasterDialog, auditHeader);
						return processCompleted;
					}
				}

				auditHeader = ErrorControl.showErrorDetails(
						this.window_WeekendMasterDialog, auditHeader);
				retValue = auditHeader.getProcessStatus();

				if (retValue == PennantConstants.porcessCONTINUE) {
					processCompleted = true;

					if (deleteNotes) {
						deleteNotes(getNotes(), true);
					}

				}

				if (retValue == PennantConstants.porcessOVERIDE) {
					auditHeader.setOveride(true);
					auditHeader.setErrorMessage(null);
					auditHeader.setInfoMessage(null);
					auditHeader.setOverideMessage(null);
				}

			}

			setOverideMap(auditHeader.getOverideMap());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		logger.debug("Leaving doSaveProcess()");
		return processCompleted;
	}

	private AuditHeader getAuditHeader(WeekendMaster aWeekendMaster,
			String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1,
				aWeekendMaster.getBefImage(), aWeekendMaster);

		return new AuditHeader(String.valueOf(aWeekendMaster.getId()), null,
				null, null, auditDetail, aWeekendMaster.getUserDetails(),
				getOverideMap());
	}

	private void showMessage(Exception e) {
		logger.debug("Entering");
		AuditHeader auditHeader = new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetails(
					PennantConstants.ERR_UNDEF, e.getMessage(), null));
			ErrorControl.showErrorControl(this.window_WeekendMasterDialog,
					auditHeader);
		} catch (Exception exp) {
			logger.error(exp);
		}
		logger.debug("Leaving ");
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void onClick$btnNotes(Event event) throws Exception {
		logger.debug(event.toString());
		final HashMap map = new HashMap();
		map.put("notes", getNotes());
		map.put("control", this);

		// call the zul-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/notes/notes.zul", null,
					map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / "
					+ e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
	}

	public void setNotes_entered(String notes) {
		logger.debug("Entering setNotes_entered()");
		if (!isNotes_Entered()) {
			if (org.apache.commons.lang.StringUtils.trimToEmpty(notes)
					.equalsIgnoreCase("Y")) {
				setNotes_Entered(true);
			} else {
				setNotes_Entered(false);
			}
		}
		logger.debug("Leaving setNotes_entered()");
	}

	private void doSetLOVValidation() {
	}

	private void doRemoveLOVValidation() {
	}

	/*
	 * private void deleteNotes() { logger.debug("Entering deleteNotes()");
	 * NotesService notesService = (NotesService)
	 * SpringUtil.getBean("notesService"); notesService.delete(getNotes());
	 * logger.debug("Leaving deleteNotes()"); }
	 */

	private Notes getNotes() {
		logger.debug("Entering getNotes()");
		Notes notes = new Notes();
		notes.setModuleName("WeekendMaster");
		notes.setReference(getWeekendMaster().getWeekendCode());
		notes.setVersion(getWeekendMaster().getVersion());
		logger.debug("Leaving getNotes()");
		return notes;
	}

	private void loadWeekEnd() {
		logger.debug("Entering loadWeekEnd()");
		paging.setPageSize(8);
		paging.setDetailed(true);
		listWrapper.initList(weekendList, this.weekend, paging);
		this.weekend.setItemRenderer(new WeekendItemRenderer());
		logger.debug("Leaving loadWeekEnd()");
	}

	public class WeekendItemRenderer implements ListitemRenderer<ValueLabel>, Serializable {

		private static final long serialVersionUID = 1L;
		//Upgraded to ZK-6.5.1.1 Added an additional parameter of type count 	
		@Override
		public void render(Listitem item, ValueLabel valueLabel, int count) throws Exception {
			Listcell lc;
			Checkbox checkbox = new Checkbox();
			checkbox.setValue(valueLabel.getValue());
			checkbox.setLabel(valueLabel.getLabel());
			checkbox.setChecked(checkMap.containsKey(valueLabel.getValue()));
			checkbox.addEventListener("onCheck", new onCheckBoxCheked());
			lc = new Listcell();
			lc.appendChild(checkbox);
			lc.setParent(item);
		}
	}

	public final class onCheckBoxCheked implements EventListener<Event> {

		@SuppressWarnings({ "unchecked", "rawtypes" })
		public void onEvent(Event event) throws Exception {
			logger.debug("onEvent()");
			Checkbox checkbox = (Checkbox) event.getTarget();
			//Upgraded to ZK-6.5.1.1 Added casting to String 	
			if (checkbox.isChecked()) {
				checkMap.put(checkbox.getValue().toString(), checkbox.getValue().toString());
			} else {
				checkMap.remove(checkbox.getValue());
			}
			List list = new ArrayList();
			list.addAll(checkMap.keySet());
			Collections.sort(list);
			String str = "";
			for (int i = 0; i < list.size(); i++) {
				str = str + list.get(i) + ",";
			}
			weekendText.setValue(str);
			logger.debug("Leaving onEvent()");
		}

	}
	
	private void refreshList(){
		final JdbcSearchObject<WeekendMaster> soWeekendMaster = getWeekendMasterListCtrl().getSearchObj();
		getWeekendMasterListCtrl().pagingWeekendMasterList.setActivePage(0);
		getWeekendMasterListCtrl().getPagedListWrapper().setSearchObject(soWeekendMaster);
		if(getWeekendMasterListCtrl().listBoxWeekendMaster!=null){
			getWeekendMasterListCtrl().listBoxWeekendMaster.getListModel();
		}
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setValidationOn(boolean validationOn) {
		this.validationOn = validationOn;
	}

	public boolean isValidationOn() {
		return this.validationOn;
	}

	public WeekendMaster getWeekendMaster() {
		return this.weekendMaster;
	}

	public void setWeekendMaster(WeekendMaster weekendMaster) {
		this.weekendMaster = weekendMaster;
	}

	public void setWeekendMasterService(
			WeekendMasterService weekendMasterService) {
		this.weekendMasterService = weekendMasterService;
	}

	public WeekendMasterService getWeekendMasterService() {
		return this.weekendMasterService;
	}

	public void setWeekendMasterListCtrl(
			WeekendMasterListCtrl weekendMasterListCtrl) {
		this.weekendMasterListCtrl = weekendMasterListCtrl;
	}

	public WeekendMasterListCtrl getWeekendMasterListCtrl() {
		return this.weekendMasterListCtrl;
	}

	public PagedListService getPagedListService() {
		return pagedListService;
	}

	public void setPagedListService(PagedListService pagedListService) {
		this.pagedListService = pagedListService;
	}

	public boolean isNotes_Entered() {
		return notes_Entered;
	}

	public void setNotes_Entered(boolean notes_Entered) {
		this.notes_Entered = notes_Entered;
	}

	public PagedListWrapper<ValueLabel> getListWrapper() {
		return listWrapper;
	}

	@SuppressWarnings("unchecked")
	public void setListWrapper() {
		if (this.listWrapper == null) {
			this.listWrapper = (PagedListWrapper<ValueLabel>) SpringUtil
					.getBean("pagedListWrapper");
		}
	}

}
