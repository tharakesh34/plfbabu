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
 * FileName    		:  HolidayMasterDialogCtrl.java                                                   * 	  
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

package com.pennant.webui.smtmasters.holidaymaster;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.HolidayUtil;
import com.pennant.backend.dao.smtmasters.WeekendMasterDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.applicationmaster.Currency;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.smtmasters.HolidayDetail;
import com.pennant.backend.model.smtmasters.HolidayMaster;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.smtmasters.HolidayMasterService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.ButtonStatusCtrl;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.MultiLineMessageBox;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.pagging.PagedListWrapper;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/SolutionFactory/SolutionFactory/holidayMasterDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */

public class HolidayMasterDialogCtrl extends GFCBaseListCtrl<HolidayMaster> implements Serializable {

	private static final long serialVersionUID = -6497477637239109557L;
	private final static Logger logger = Logger.getLogger(HolidayMasterDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_HolidayMasterDialog; // autoWired

	protected ExtendedCombobox holidayCode; 		// autoWired
	protected Textbox holidayCodeDesc; 	// autoWired
	protected Decimalbox holidayYear; 	// autoWired
	protected Combobox holidayType;		// autoWired
	protected Listbox listBoxHolidayDet;
	protected Paging pagingHolidayDetList;

	private List<HolidayDetail> holidayDetails = new ArrayList<HolidayDetail>();
	private PagedListWrapper<HolidayDetail> holidayDetailPagedListWrapper;

	protected Label recordStatus; // autoWired
	protected Radiogroup userAction;
	protected Groupbox groupboxWf;
	protected Grid grid_Basicdetails;

	private HolidayMaster holidayMaster; // overhanded per param
	private transient HolidayMasterListCtrl holidayMasterListCtrl; // overhanded
																	// per param
	private transient WeekendMasterDAO weekendMasterDAO;

	// old value vars for edit mode. that we can check if something
	// on the values are edited since the last init.
	private transient String 	 oldVar_holidayCode;
	private transient String 	 oldVar_holidayCodeDesc;
	private transient Combobox 	 oldVar_holidayType;
	private transient BigDecimal oldVar_holidayYear;
	private transient String 	 oldVar_holidays;
	private transient String 	 oldVar_holidaysDesc;
	private transient String 	 oldVar_recordStatus;

	private transient String var_holidays;
	private transient String var_holidaysDesc;

	private transient boolean validationOn;

	// Button controller for the CRUD buttons
	private transient final String btnCtroller_ClassPrefix = "button_HolidayMasterDialog_";
	private transient ButtonStatusCtrl btnCtrl;

	protected Button btnNew; 			// autowire
	protected Button btnEdit; 			// autowire
	protected Button btnDelete; 		// autowire
	protected Button btnSave; 			// autowire
	protected Button btnCancel; 		// autowire
	protected Button btnClose; 			// autowire
	protected Button btnHelp; 			// autowire
	protected Button btnNotes; 			// autowire
	protected Button btnNew_HolidayDet; // autowire
	protected Listheader listheader_HolidayCode;

	// ServiceDAOs / Domain Classes
	private transient HolidayMasterService holidayMasterService;
	private transient PagedListService pagedListService;
	private LinkedHashMap<Integer, HolidayDetail> h_Map;
	private transient List<HolidayDetail> holidayList = new ArrayList<HolidayDetail>();

	private transient String old_holidayCode = "";
	private transient BigDecimal old_holidayYear = BigDecimal.ZERO;
	private transient List<HolidayDetail> oldVar_holidayDetailList = new ArrayList<HolidayDetail>();
	int listRows;

	/**
	 * default constructor.<br>
	 */
	public HolidayMasterDialogCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected HolidayMaster object
	 * in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_HolidayMasterDialog(Event event)
			throws Exception {
		logger.debug("Entering" + event.toString());

		/* set components visible dependent of the users rights */
		doCheckRights();
		setHolidayDetailPagedListWrapper();

		/* create the Button Controller. Disable not used buttons during working */
		this.btnCtrl = new ButtonStatusCtrl(getUserWorkspace(),
				this.btnCtroller_ClassPrefix, true, this.btnNew, this.btnEdit,
				this.btnDelete, this.btnSave, this.btnCancel, this.btnClose,
				this.btnNotes);

		// get the params map that are overHanded by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		if (args.containsKey("holidayMaster")) {
			this.holidayMaster = (HolidayMaster) args.get("holidayMaster");
			HolidayMaster befHolidayMaster = new HolidayMaster();
			setHolidayMaster(this.holidayMaster);
			BeanUtils.copyProperties(this.holidayMaster, befHolidayMaster);
		} else {
			setHolidayMaster(null);
		}
		// READ OVERHANDED params !
		// we get the holidayMasterListWindow controller. So we have access
		// to it and can synchronize the shown data when we do insert, edit or
		// delete holidayMaster here.
		if (args.containsKey("holidayMasterListCtrl")) {
			setHolidayMasterListCtrl((HolidayMasterListCtrl) args
					.get("holidayMasterListCtrl"));
		} else {
			setHolidayMasterListCtrl(null);
		}
		
		getBorderLayoutHeight();
		grid_Basicdetails.getRows().getVisibleItemCount();
		int dialogHeight =  grid_Basicdetails.getRows().getVisibleItemCount()* 20 + 140 ; 
		int listboxHeight = borderLayoutHeight-dialogHeight;
		listBoxHolidayDet.setHeight(listboxHeight+"px");
		listRows = Math.round(listboxHeight/ 22);

		// set Field Properties
		doSetFieldProperties();
		doShowDialog(getHolidayMaster());
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering ");
		// Empty sent any required attributes
		this.holidayCode.setMaxlength(3);
		this.holidayCode.setMandatoryStyle(true);
		this.holidayCode.setModuleName("Currency");
		this.holidayCode.setValueColumn("CcyCode");
		this.holidayCode.setDescColumn("CcyDesc");
		this.holidayCode.setValidateColumns(new String[] { "CcyCode" });
		
		this.holidayCodeDesc.setMaxlength(50);
		this.holidayYear.setMaxlength(4);
		this.holidayYear.setFormat("###0");

		if (isWorkFlowEnabled()) {
			this.groupboxWf.setVisible(true);
		} else {
			this.groupboxWf.setVisible(false);
		}
		logger.debug("Leaving ");

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
		logger.debug("Entering");
		getUserWorkspace().alocateAuthorities("HolidayMasterDialog");
		this.btnNew.setVisible(getUserWorkspace().isAllowed(
				"button_HolidayMasterDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed(
				"button_HolidayMasterDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed(
				"button_HolidayMasterDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed(
				"button_HolidayMasterDialog_btnSave"));
		this.btnCancel.setVisible(false);
		logger.debug("Leaving");
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
	public void onClose$window_HolidayMasterDialog(Event event)
			throws Exception {
		logger.debug("Entering" + event.toString());
		doClose();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "save" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnSave(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		doSave();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "edit" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnEdit(Event event) {
		logger.debug("Entering" + event.toString());
		doEdit();
		// remember the old vars
		doStoreInitValues();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		PTMessageUtils.showHelpWindow(event, window_HolidayMasterDialog);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "delete" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnDelete(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		doDelete();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "cancel" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnCancel(Event event) {
		logger.debug("Entering" + event.toString());
		doCancel();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "close" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnClose(Event event) throws InterruptedException {

		logger.debug("Entering" + event.toString());
		try {
			doClose();
		} catch (final Exception e) {
			// close anyway
			closeDialog(this.window_HolidayMasterDialog, "HolidayMaster");
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "Search" button is clicked. <br>
	 * 
	 * @param event
	 * 
	 */
	public void onFulfill$holidayCode(Event event) {
		logger.debug("Entering" + event.toString());
		Object dataObject = holidayCode.getObject();
		if (dataObject instanceof String) {
			this.holidayCode.setValue(dataObject.toString());
			this.holidayCode.setDescription("");
			this.holidayCodeDesc.setValue("");
		} else {
			Currency details = (Currency) dataObject;
			if (details != null) {
				this.holidayCode.setValue(details.getCcyCode());
				this.holidayCode.setDescription("");
				this.holidayCodeDesc.setValue(details.getCcyDesc());
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "selection" is done. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onSelect$holidayType(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		if (holidayType.getSelectedItem().getValue().toString().equals("P")) {
			holidayYear.setValue(BigDecimal.ZERO);
			holidayYear.setReadonly(true);
			h_Map = null;
			doFillHolidayDetails();
			btnNew_HolidayDet.setVisible(true);
		} else {
			holidayYear.setText("");
			holidayYear.setReadonly(false);
			btnNew_HolidayDet.setVisible(false);
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "New" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnNew_HolidayDet(Event event)
			throws InterruptedException {

		logger.debug("Entering" + event.toString());
		HolidayDetail aHolidayDetail = new HolidayDetail();
		aHolidayDetail.setNewRecord(true);
		aHolidayDetail.setHolidayCode(this.holidayCode.getValue());
		aHolidayDetail.setHolidayCodeDesc(this.holidayCodeDesc.getValue());
		aHolidayDetail.setHolidayType(this.holidayType.getSelectedItem()
				.getValue().toString());
		aHolidayDetail.setHolidayYear(this.holidayYear.getValue());
		if (this.holidayYear.getValue().intValue() == 0) {
			aHolidayDetail.setHolidayYear(new BigDecimal(DateUtility
					.getYear(new Date())));
		}
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("holidayDetail", aHolidayDetail);
		map.put("HolidayMasterDialogCtrl", this);

		try {
			Executions.createComponents(
					"/WEB-INF/pages/SolutionFactory/HolidayMaster/HolidayMasterDetailsDialog.zul",
							null, map);
		} catch (Exception e) {
			logger.error("onOpenWindow:: error opening window / "
					+ e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the leaving the HolidayCode TextBox <br>
	 * 
	 * @param event
	 * 
	 * @throws Exception
	 */
	public void onBlur$btnSearchHolidayCode(Event event) throws Exception {
		logger.debug("Entering " + event.toString());

		if (!holidayCode.getValue().equalsIgnoreCase(old_holidayCode)) {
			old_holidayCode = holidayCode.getValue();
		}

		if (!holidayCode.getValue().equals("")
				&& holidayYear.getValue() != null
				&& !holidayYear.getValue().equals(BigDecimal.ZERO)) {
			btnNew_HolidayDet.setVisible(true);
		} else {
			btnNew_HolidayDet.setVisible(false);
		}
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * when the leaving the HolidayYear TextBox <br>
	 * 
	 * @param event
	 * 
	 * @throws Exception
	 */
	public void onBlur$holidayYear(Event event) throws Exception {
		logger.debug("Entering " + event.toString());
		if (holidayYear.getValue() != null
				&& !holidayYear.getValue().equals(old_holidayYear)) {

		}
		if (!holidayCode.getValue().equals("")
				&& holidayYear.getValue() != null
				&& !holidayYear.getValue().equals(BigDecimal.ZERO)
				&& Integer.toString(holidayYear.getValue().intValue()).length() == 4
				&& holidayType.getSelectedItem().getValue().toString()
						.equals("N") && !(holidayType.getSelectedIndex() < 0)) {

			btnNew_HolidayDet.setVisible(true);
			List<HolidayDetail> weekEndList = HolidayUtil.getWeekendList(
					holidayCode.getValue(), holidayYear.getValue().intValue());

			if ((!old_holidayYear.equals(holidayYear.getValue()))
					&& listBoxHolidayDet.getItemCount() > 1) {

				final String msg = Labels
						.getLabel("message_Data_Cleared_Save_Data_YesNo");
				final String title = Labels.getLabel("message.Information");
				MultiLineMessageBox.doSetTemplate();
				int conf = MultiLineMessageBox.show(msg, title,
						MultiLineMessageBox.YES | MultiLineMessageBox.NO,
						MultiLineMessageBox.QUESTION, true);

				if (conf == MultiLineMessageBox.YES) {
					logger.debug("doClose: Yes");
					h_Map = null;
					for (int i = 0; i < weekEndList.size(); i++) {
						addMap(weekEndList.get(i));
					}
					old_holidayYear = this.holidayYear.getValue();
					doFillHolidayDetails();
				} else {
					logger.debug("doClose: No");
					this.holidayYear.setValue(old_holidayYear);
				}
			} else {
				if (!(holidayType.getSelectedIndex() < 0)) {
					h_Map = null;
					for (int i = 0; i < weekEndList.size(); i++) {
						addMap(weekEndList.get(i));
					}
					old_holidayYear = this.holidayYear.getValue();
					doFillHolidayDetails();
				}
			}

		} else if (!(holidayType.getSelectedIndex() < 0)
				&& holidayType.getSelectedItem().getValue().toString()
						.equals("P")) {
			h_Map = null;
			btnNew_HolidayDet.setVisible(true);
		} else {
			btnNew_HolidayDet.setVisible(false);
		}
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * This method is forwarded from the listboxes item renderer. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onHolidayDetailItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		// get the selected invoiceHeader object
		final Listitem item = this.listBoxHolidayDet.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final HolidayDetail aHolidayDetail = (HolidayDetail) item
					.getAttribute("data");
			final HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("holidayDetail", aHolidayDetail);
			map.put("HolidayMasterDialogCtrl", this);

			// call the zul-file with the parameters packed in a map
			try {
				Executions.createComponents(
						"/WEB-INF/pages/SolutionFactory/HolidayMaster/HolidayMasterDetailsDialog.zul",
								null, map);
			} catch (final Exception e) {
				logger.error("onOpenWindow:: error opening window / "
						+ e.getMessage());
				PTMessageUtils.showErrorMessage(e.toString());
			}
		}
		logger.debug("Leaving" + event.toString());
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
		logger.debug("Entering ");
		if (isDataChanged()) {
			logger.debug("Data Changed(): True");

			// Show a confirm box
			final String msg = Labels.getLabel(
					"message_Data_Modified_Save_Data_YesNo");
			final String title = Labels.getLabel(
					"message.Information");

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
		closeDialog(this.window_HolidayMasterDialog, "HolidayMaster");
		logger.debug("Leaving ");
	}

	/**
	 * Cancel the actual operation. <br>
	 * <br>
	 * Resets to the original status.<br>
	 * 
	 */
	private void doCancel() {
		logger.debug("Entering ");
		doResetInitValues();
		doReadOnly();
		this.btnCtrl.setInitEdit();
		logger.debug("Leaving ");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aHolidayMaster
	 */
	public void doWriteBeanToComponents(HolidayMaster aHolidayMaster) {
		logger.debug("Entering ");
		this.holidayCode.setValue(aHolidayMaster.getHolidayCode());
		this.holidayCodeDesc.setValue(aHolidayMaster.getHolidayCodeDesc());
		for (int i = 0; i < holidayType.getItemCount(); i++) {
			if (holidayType.getItemAtIndex(i).getValue()
					.equals(aHolidayMaster.getHolidayType())) {
				holidayType.setSelectedIndex(i);
			}
		}
		this.holidayYear.setValue(aHolidayMaster.getHolidayYear());
		logger.debug("Leaving ");

	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aHolidayMaster
	 */
	private HolidayMaster doWriteComponentsToBean(HolidayMaster aHolidayMaster) {
		logger.debug("Entering ");
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			aHolidayMaster.setHolidayCode(this.holidayCode.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aHolidayMaster.setHolidayType(this.holidayType.getSelectedItem()
					.getValue().toString());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aHolidayMaster.setHolidayCodeDesc(this.holidayCodeDesc.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aHolidayMaster.setHolidayYear(this.holidayYear.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		if (!(aHolidayMaster.getHolidayType() == null)
				&& aHolidayMaster.getHolidayType().equalsIgnoreCase("P")) {

			if (aHolidayMaster.isNewRecord()) {
				aHolidayMaster.setHolidayYear(BigDecimal.ZERO);
			}

			aHolidayMaster.setHolidayCodeDesc("Perminent Holidays");
		}

		try {
			if (listBoxHolidayDet.getItemCount() < 1) {
				throw new WrongValueException(listheader_HolidayCode,
						"Holiday Details must be entered");
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		aHolidayMaster.setHolidays(var_holidays);
		aHolidayMaster.setHolidaysDesc(var_holidaysDesc);

		if (wve.size() > 0) {
			doRemoveValidation();
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}
		logger.debug("Leaving ");
		return aHolidayMaster;

	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aHolidayMaster
	 * @throws InterruptedException
	 */
	public void doShowDialog(HolidayMaster aHolidayMaster)
			throws InterruptedException {
		logger.debug("Entering ");
		// if aHolidayMaster == null then we opened the Dialog without
		// args for a given entity, so we get a new Obj().

		if (aHolidayMaster == null) {
			aHolidayMaster = getHolidayMasterService().getNewHolidayMaster();
			aHolidayMaster.setHolidayType("N");
			setHolidayMaster(aHolidayMaster);
		} else {
			setHolidayMaster(aHolidayMaster);
		}

		// set Readonly mode accordingly if the object is new or not.
		if (aHolidayMaster.isNew()) {
			this.btnCtrl.setInitNew();
		}
		doEdit();
		btnCancel.setVisible(false);
		this.btnSave.setVisible(true);

		try {
			// fill the components with the data
			doWriteBeanToComponents(aHolidayMaster);
			// Set the list of Holidays
			setMap();
			// stores the inital data for comparing if they are changed
			// during user action.
			doStoreInitValues();
			setDialog(this.window_HolidayMasterDialog);

		} catch (final Exception e) {
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving ");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++++++++ helpers ++++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Stores the init values in mem vars. <br>
	 */
	private void doStoreInitValues() {
		logger.debug("Entering ");
		this.oldVar_holidayCode = this.holidayCode.getValue();
		this.oldVar_holidayCodeDesc = this.holidayCodeDesc.getValue();
		this.oldVar_holidayYear = this.holidayYear.getValue();
		this.oldVar_recordStatus = this.recordStatus.getValue();
		this.oldVar_holidays = this.var_holidays;
		this.oldVar_holidaysDesc = this.var_holidaysDesc;
		this.oldVar_holidayType = this.holidayType;
		this.oldVar_holidayDetailList = this.holidayList;
		logger.debug("Leaving ");
	}

	/**
	 * Resets the init values from mem vars. <br>
	 */
	private void doResetInitValues() {
		logger.debug("Entering ");
		this.holidayCode.setValue(this.oldVar_holidayCode);
		this.holidayCodeDesc.setValue(this.oldVar_holidayCodeDesc);
		this.holidayType = this.oldVar_holidayType;
		this.holidayYear.setValue(this.oldVar_holidayYear);
		this.recordStatus.setValue(this.oldVar_recordStatus);
		this.var_holidays = this.oldVar_holidays;
		this.var_holidaysDesc = this.oldVar_holidaysDesc;

		if (isWorkFlowEnabled()) {
			this.userAction.setSelectedIndex(0);
		}
		logger.debug("Leaving ");
	}

	/**
	 * Checks, if data are changed since the last call of <br>
	 * doStoreInitData() . <br>
	 * 
	 * @return true, if data are changed, otherwise false
	 */
	private boolean isDataChanged() {
		logger.debug("Entering ");
		boolean changed = false;

		if (this.oldVar_holidayCode != this.holidayCode.getValue()) {
			changed = true;
		}

		if (this.oldVar_holidayCodeDesc != this.holidayCodeDesc.getValue()) {
			changed = true;
		}

		if (this.oldVar_holidayType != this.holidayType) {
			changed = true;
		}

		if (this.oldVar_holidayYear != this.holidayYear.getValue()) {
			changed = true;
		}

		if (this.oldVar_holidayDetailList != this.holidayList) {
			changed = true;
		}

		logger.debug("Leaving ");
		return changed;
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering ");
		setValidationOn(true);

		if (!this.holidayCodeDesc.isReadonly()) {
			this.holidayCode.setConstraint(new PTStringValidator(Labels.getLabel(
									"label_HolidayMasterDialog_HolidayCode.value"), PennantRegularExpressions.REGEX_DESCRIPTION, true));
			this.holidayCodeDesc.setConstraint(new PTStringValidator(Labels.getLabel(
									"label_HolidayMasterDialog_HolidayCodeDesc.value"), PennantRegularExpressions.REGEX_DESCRIPTION, true));
		}

		if (!this.holidayType.isReadonly()) {
			this.holidayType.setConstraint(new PTStringValidator(Labels.getLabel(
									"label_HolidayMasterDialog_HolidayType.value"), null, true));
		}

		if (!this.holidayYear.isReadonly()) {
			this.holidayYear.setConstraint("NO EMPTY:"+ Labels.getLabel(
								"FIELD_NO_EMPTY",new String[] { Labels.getLabel(
									"label_HolidayMasterDialog_HolidayYear.value") }));
		}
		logger.debug("Leaving ");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering ");
		setValidationOn(false);
		this.holidayCode.setConstraint("");
		this.holidayCodeDesc.setConstraint("");
		this.holidayYear.setConstraint("");
		logger.debug("Leaving ");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++++ CRUD operations +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Deletes a HolidayMaster object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		// TODO TO BE IMPLEMENTED.
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering ");
		if (getHolidayMaster().isNewRecord()) {
			this.holidayCode.setReadonly(false);
			this.holidayType.setDisabled(false);
			this.holidayYear.setDisabled(false);
			this.btnCancel.setVisible(false);
			this.holidayCode.focus();
			this.btnNew_HolidayDet.setVisible(false);
		} else {
			this.holidayCode.setReadonly(true);
			this.holidayType.setDisabled(true);
			this.holidayYear.setDisabled(true);
			this.btnCancel.setVisible(true);
			this.holidayCodeDesc.focus();
			this.btnNew_HolidayDet.setVisible(true);
		}
		this.btnCtrl.setBtnStatus_Edit();
		btnCancel.setVisible(true);
		logger.debug("Leaving ");
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering ");
		this.holidayCode.setReadonly(true);
		this.holidayCodeDesc.setReadonly(true);
		this.holidayYear.setReadonly(true);
		logger.debug("Leaving ");
	}

	/**
	 * Clears the components values. <br>
	 */
	public void doClear() {
		logger.debug("Entering ");
		this.holidayCode.setValue("");
		this.holidayCodeDesc.setValue("");
		this.holidayType.setValue("N");
		this.holidayYear.setValue("0");
		logger.debug("Leaving ");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering ");
		HolidayMaster aHolidayMaster = new HolidayMaster();
		BeanUtils.copyProperties(getHolidayMaster(), aHolidayMaster);

		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// force validation, if on, than execute by component.getValue()
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		doSetValidation();
		// fill the HolidayMaster object with the components data
		holidayMaster = doWriteComponentsToBean(holidayMaster);

		// Write the additional validations as per below example
		// get the selected branch object from the listbox
		// Do data level validations here

		// save it to database
		try {
			if (doProcess(holidayMaster)) {
				refreshList();
				doWriteBeanToComponents(holidayMaster);
				// ++ create the searchObject and init sorting ++ //
				final JdbcSearchObject<HolidayMaster> soHolidayMaster = getHolidayMasterListCtrl()
						.getSearchObj();
				// Set the ListModel
				getHolidayMasterListCtrl().pagingHolidayMasterList
						.setActivePage(0);
				getHolidayMasterListCtrl().getPagedListWrapper()
						.setSearchObject(soHolidayMaster);

				// call from cusromerList then synchronize the HolidayMaster
				// listBox
				if (getHolidayMasterListCtrl().listBoxHolidayMaster != null) {
					// now synchronize the HolidayMaster listBox
					getHolidayMasterListCtrl().listBoxHolidayMaster
							.getListModel();
				}

				doReadOnly();
				this.btnCtrl.setBtnStatus_Save();
				// Close the Existing Dialog
				closeDialog(this.window_HolidayMasterDialog, "HolidayMaster");
			}

		} catch (final DataAccessException e) {
			showMessage(e);
		}
		logger.debug("Leaving ");
	}

	/**
	 * Set the workFlow Details List to Object
	 * 
	 * @param aHolidayMaster
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doProcess(HolidayMaster aHolidayMaster) {
		logger.debug("Entering ");
		boolean processCompleted = false;
		String tranType = "";

		AuditHeader auditHeader = null;

		aHolidayMaster.setLastMntBy(getUserWorkspace().getLoginUserDetails()
				.getLoginUsrID());
		aHolidayMaster.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aHolidayMaster.setUserDetails(getUserWorkspace().getLoginUserDetails());

		if (aHolidayMaster.isNew()) {
			tranType = PennantConstants.TRAN_ADD;
		} else {
			tranType = PennantConstants.TRAN_UPD;
		}
		auditHeader = getAuditHeader(aHolidayMaster, tranType);
		processCompleted = doSaveProcess(auditHeader, null);
		logger.debug("Leaving ");
		return processCompleted;
	}

	/**
	 * Get the result after the DataBase Operations
	 * 
	 * @param auditHeader
	 *            (AuditHeader)
	 * 
	 * @param method
	 *            (String)
	 * 
	 * @return boolean
	 * 
	 */
	@SuppressWarnings("unused")
	private boolean doSaveProcess(AuditHeader auditHeader, String method) {
		logger.debug("Entering ");
		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		// HolidayMaster aHolidayMaster = (HolidayMaster)
		// auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;
		try {
			while (retValue == PennantConstants.porcessOVERIDE) {
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase("")) {
					if (auditHeader.getAuditTranType().equals(
							PennantConstants.TRAN_DEL)) {
						auditHeader = getHolidayMasterService().delete(
								auditHeader);
						deleteNotes = true;
					} else {
						auditHeader = getHolidayMasterService().saveOrUpdate(
								auditHeader);
					}

				}
				auditHeader = ErrorControl.showErrorDetails(
						this.window_HolidayMasterDialog, auditHeader);
				retValue = auditHeader.getProcessStatus();

				if (retValue == PennantConstants.porcessCONTINUE) {
					processCompleted = true;
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
		logger.debug("Leaving ");
		return processCompleted;
	}

	/**
	 * Adding the details to Render
	 * 
	 */
	public void doFillHolidayDetails() {
		logger.debug("Entering ");
		setHolidayMap();
		this.pagingHolidayDetList.setPageSize(listRows);
		this.pagingHolidayDetList.setDetailed(true);
		this.listBoxHolidayDet.getItems().clear();
		if (holidayList == null) {
			holidayList = new ArrayList<HolidayDetail>();
		}
		getHolidayDetailPagedListWrapper().initList(holidayList,
				this.listBoxHolidayDet, this.pagingHolidayDetList);
		this.listBoxHolidayDet
				.setItemRenderer(new HolidayDetailslistItemRenderer());
		logger.debug("Leaving ");
	}

	/**
	 * Item renderer for listItems in the listBox.
	 * 
	 */
	public class HolidayDetailslistItemRenderer implements ListitemRenderer<HolidayDetail>,
			Serializable {

		private static final long serialVersionUID = 7338566091174304005L;
		//Upgraded to ZK-6.5.1.1 Added an additional parameter of type count 	
		@Override
		public void render(Listitem item, HolidayDetail holidayDetail, int count) throws Exception {

			//final HolidayDetail holidayDetail = (HolidayDetail) data;
			Listcell lc;
			lc = new Listcell(holidayDetail.getHolidayCode());
			item.appendChild(lc);
			lc = new Listcell(holidayDetail.getHolidayYear().toString());
			item.appendChild(lc);
			lc = new Listcell(DateUtility.formatUtilDate(holidayDetail
					.getHoliday().getTime(), PennantConstants.dateFormate));
			item.appendChild(lc);
			lc = new Listcell(holidayDetail.getHolidayDescription());
			item.appendChild(lc);

			item.setAttribute("data", holidayDetail);
			ComponentsCtrl.applyForward(item,
					"onDoubleClick=onHolidayDetailItemDoubleClicked");
		}
	}

	/**
	 * Get Audit Header Details
	 * 
	 * @param aHolidayMaster
	 *            (HolidayMaster)
	 * 
	 * @param tranType
	 *            (String)
	 * 
	 * @return auditHeader
	 */
	private AuditHeader getAuditHeader(HolidayMaster aHolidayMaster,
			String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1,
				aHolidayMaster.getBefImage(), aHolidayMaster);

		return new AuditHeader(String.valueOf(aHolidayMaster.getId()), null,
				null, null, auditDetail, aHolidayMaster.getUserDetails(),
				getOverideMap());
	}

	/**
	 * To show the Message
	 * 
	 * @throws Exception
	 * 
	 */
	private void showMessage(Exception e) {
		logger.debug("Entering");
		AuditHeader auditHeader = new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetails(
					PennantConstants.ERR_UNDEF, e.getMessage(), null));
			ErrorControl.showErrorControl(this.window_HolidayMasterDialog,
					auditHeader);
		} catch (Exception exp) {
			logger.error(exp);
		}
		logger.debug("Leaving ");
	}

	/**
	 * Adding the details to Map
	 * 
	 */
	private void setMap() {
		logger.debug("Entering ");
		h_Map = null;
		if (getHolidayMaster() != null && holidayYear.getValue() != null) {
			List<HolidayDetail> holidayDetails = getHolidayMaster()
					.getHolidayList(holidayYear.getValue());
			for (int i = 0; i < holidayDetails.size(); i++) {
				addMap(holidayDetails.get(i));
			}
			doFillHolidayDetails();
		}
		logger.debug("Leaving ");
	}

	/**
	 * Adding the details to Map
	 * 
	 */
	private void addMap(HolidayDetail detail) {
		logger.debug("Entering ");
		if (detail != null) {
			if (h_Map == null) {
				h_Map = new LinkedHashMap<Integer, HolidayDetail>();
			}
			h_Map.put(detail.getJulionDate(), detail);
		}
		logger.debug("Leaving ");
	}

	@SuppressWarnings("unchecked")
	public void setHolidayDetailPagedListWrapper() {
		if (this.holidayDetailPagedListWrapper == null) {
			this.holidayDetailPagedListWrapper = (PagedListWrapper<HolidayDetail>) SpringUtil
					.getBean("pagedListWrapper");
		}
	}

	/**
	 * Adding and Concatenating the details to List
	 * 
	 */
	private void setHolidayMap() {
		logger.debug("Entering ");
		holidayList = new ArrayList<HolidayDetail>();

		var_holidays = null;
		var_holidaysDesc = null;
		if (h_Map != null) {
			Object[] objects = h_Map.values().toArray();
			for (int i = 0; i < objects.length; i++) {
				HolidayDetail detail = (HolidayDetail) objects[i];

				if (var_holidays == null) {
					var_holidays = String.valueOf(detail.getJulionDate());
					var_holidaysDesc = detail.getHolidayDescription();
				} else {
					var_holidays = var_holidays.concat(","
							+ String.valueOf(detail.getJulionDate()));
					var_holidaysDesc = var_holidaysDesc.concat(","
							+ detail.getHolidayDescription());
				}

				holidayList.add(detail);
			}
		}
		logger.debug("Leaving ");
	}

	/**
	 * Adding and Deleting the holidays to Map
	 * 
	 * @param holidayDetail
	 * 
	 * @param action
	 * 
	 */
	public boolean syncHolidays(HolidayDetail holidayDetail, String action) {
		logger.debug("Entering ");
		if (h_Map == null) {
			h_Map = new LinkedHashMap<Integer, HolidayDetail>();
		}
		if (holidayDetail == null) {
			return false;
		}
		if (StringUtils.trim(action).equalsIgnoreCase(PennantConstants.RCD_ADD)) {
			holidayDetail.setNewRecord(false);
			if (h_Map.get(holidayDetail.getJulionDate()) == null) {
				h_Map.put(holidayDetail.getJulionDate(), holidayDetail);
			} else {
				return false;
			}
		} else if (StringUtils.trim(action).equalsIgnoreCase(
				PennantConstants.RCD_DEL)) {

			if (h_Map.containsKey(holidayDetail.getJulionDate())) {
				h_Map.remove(holidayDetail.getJulionDate());
			} else {
				return false;
			}
		} else {
			if (h_Map.containsKey(holidayDetail.getJulionDate())) {
				h_Map.remove(holidayDetail.getJulionDate());
				h_Map.put(holidayDetail.getJulionDate(), holidayDetail);
			} else {
				return false;
			}
		}
		doFillHolidayDetails();
		logger.debug("Leaving ");
		return true;
	}

	private void refreshList() {
		final JdbcSearchObject<HolidayMaster> soHolidayMaster = getHolidayMasterListCtrl()
				.getSearchObj();
		getHolidayMasterListCtrl().pagingHolidayMasterList.setActivePage(0);
		getHolidayMasterListCtrl().getPagedListWrapper().setSearchObject(
				soHolidayMaster);
		if (getHolidayMasterListCtrl().listBoxHolidayMaster != null) {
			getHolidayMasterListCtrl().listBoxHolidayMaster.getListModel();
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

	public void setHolidayMasterService(
			HolidayMasterService holidayMasterService) {
		this.holidayMasterService = holidayMasterService;
	}
	public HolidayMasterService getHolidayMasterService() {
		return this.holidayMasterService;
	}

	public void setHolidayMasterListCtrl(
			HolidayMasterListCtrl holidayMasterListCtrl) {
		this.holidayMasterListCtrl = holidayMasterListCtrl;
	}
	public HolidayMasterListCtrl getHolidayMasterListCtrl() {
		return this.holidayMasterListCtrl;
	}

	public PagedListService getPagedListService() {
		return pagedListService;
	}
	public void setPagedListService(PagedListService pagedListService) {
		this.pagedListService = pagedListService;
	}

	public HolidayMaster getHolidayMaster() {
		return holidayMaster;
	}
	public void setHolidayMaster(HolidayMaster holidayMaster) {
		this.holidayMaster = holidayMaster;
	}

	public WeekendMasterDAO getWeekendMasterDAO() {
		return weekendMasterDAO;
	}
	public void setWeekendMasterDAO(WeekendMasterDAO weekendMasterDAO) {
		this.weekendMasterDAO = weekendMasterDAO;
	}

	public List<HolidayDetail> getHolidayDetails() {
		return holidayDetails;
	}
	public void setHolidayDetails(List<HolidayDetail> holidayDetails) {
		this.holidayDetails = holidayDetails;
	}

	public PagedListWrapper<HolidayDetail> getHolidayDetailPagedListWrapper() {
		return holidayDetailPagedListWrapper;
	}

}