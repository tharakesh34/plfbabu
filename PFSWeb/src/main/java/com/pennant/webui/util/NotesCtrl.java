/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */

/**
 ********************************************************************************************
 * FILE HEADER *
 ********************************************************************************************
 *
 * FileName : NotesCtl.java *
 * 
 * Author : PENNANT TECHONOLOGIES *
 * 
 * Creation Date : 26-04-2011 *
 * 
 * Modified Date : 26-04-2011 *
 * 
 * Description : *
 * 
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 26-04-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.util;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Div;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Hlayout;
import org.zkoss.zul.Html;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listhead;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Separator;
import org.zkoss.zul.Space;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.UserWorkspace;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.service.NotesService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.component.PTCKeditor;
import com.pennant.webui.collateral.collateralsetup.CollateralBasicDetailsCtrl;
import com.pennant.webui.finance.financemain.FinBasicDetailsCtrl;
import com.pennanttech.framework.web.AbstractDialogController;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.web.util.MessageUtil;

public class NotesCtrl extends GFCBaseCtrl<Notes> {
	private static final long serialVersionUID = -1351367303946249042L;
	private static final Logger logger = LogManager.getLogger(NotesCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_notesDialog;
	protected Tabbox tabbox;
	protected Listbox listboxNotes;
	protected Listhead listheadNotes;
	protected PTCKeditor remarks;
	protected Textbox remarksText;
	protected Radiogroup remarkType;
	protected Label label_NotesDialog_AlignType;
	protected Radiogroup alignType;
	protected Hlayout hlayout_cbType;
	protected Div div_toolbar;
	protected Label label_title;
	protected Separator separator1;
	protected Separator separator2;
	protected Space space_type1;
	protected Space space_type2;
	protected Label label_NotesDialog_Type;

	// not auto wired variables
	private Notes notes; // overHanded per parameter

	private transient NotesService notesService;
	// not auto wired variables
	private Notes newNotes; // overHanded per parameter

	private AbstractDialogController<Object> mainControl = null;
	private transient boolean validationOn;
	private List<ValueLabel> remarkTypeList = PennantStaticListUtil.getRemarkType();
	private List<ValueLabel> recommandTypeList = PennantStaticListUtil.getRecommandType();
	private List<ValueLabel> alignTypeList = PennantStaticListUtil.getAlignType();
	private List<Notes> notesList;
	private boolean isFinanceNotes = false;
	private boolean isRecommendMand = false;
	private String roleCode = "";
	private String moduleName = "";
	private Tabpanel tabpanel = null;
	private boolean isEnquiry = false;
	private boolean isNotFinanceProcess = false;
	private boolean maxLenReq = false;

	private FinBasicDetailsCtrl finBasicDetailsCtrl;
	private CollateralBasicDetailsCtrl collateralBasicDetailsCtrl;
	protected Groupbox finBasicdetails;

	private boolean isSystemDate = true;

	public NotesCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the ZUL-file is called with a parameter for a
	 * selected bankDetails object in a Map.
	 * 
	 * @param event
	 */
	@SuppressWarnings("unchecked")
	public void onCreate$window_notesDialog(Event event) {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_notesDialog);

		if (event.getTarget().getParent() != null) {
			tabpanel = (Tabpanel) event.getTarget().getParent();
		}

		/* set components visible dependent of the users rights */
		doCheckRights();

		// READ OVERHANDED parameters !
		if (arguments.containsKey("notes")) {
			this.notes = (Notes) arguments.get("notes");
		} else {
			setNotes(null);
		}

		if (arguments.containsKey("isFinanceNotes")) {
			this.isFinanceNotes = (Boolean) arguments.get("isFinanceNotes");
		}
		if (arguments.containsKey("isRecommendMand")) {
			this.isRecommendMand = (Boolean) arguments.get("isRecommendMand");
		}

		if (arguments.containsKey("userRole")) {
			this.roleCode = (String) arguments.get("userRole");
		}
		if (arguments.containsKey("moduleName")) {
			this.moduleName = (String) arguments.get("moduleName");
		}

		if (arguments.containsKey("enqModule")) {
			isEnquiry = true;
		}

		if (arguments.containsKey("notesList")) {
			this.notesList = (List<Notes>) arguments.get("notesList");
		}
		if (arguments.containsKey("isNotFinanceProcess")) {
			this.isNotFinanceProcess = (boolean) arguments.get("isNotFinanceProcess");
		}

		// append finance basic details
		if (arguments.containsKey("finHeaderList")) {
			appendFinBasicDetails((ArrayList<Object>) arguments.get("finHeaderList"));
		}

		if (arguments.containsKey("maxLenReq")) {
			this.maxLenReq = (boolean) arguments.get("maxLenReq");
		}

		this.mainControl = (AbstractDialogController<Object>) arguments.get("control");
		this.remarks.setCustomConfigurationsPath(PTCKeditor.SIMPLE_LIST);

		getBorderLayoutHeight();

		// set Field Properties
		doSetFieldProperties();

		doShowDialog(getNotes());
		doCheckEnquiry();

		this.remarks.setValue("");

		logger.debug("Leaving" + event.toString());
	}

	private void doSetFieldProperties() {
		logger.debug(Literal.ENTERING);
		if (maxLenReq) {
			this.remarksText.setMaxlength(2000);
		}
		logger.debug(Literal.LEAVING);
	}

	private void doCheckEnquiry() {
		logger.debug("Entering");
		if (isEnquiry) {
			this.btnSave.setVisible(false);
			this.btnClose.setVisible(false);
			this.remarks.setVisible(false);
			this.remarksText.setVisible(false);
			this.hlayout_cbType.setVisible(false);
			this.div_toolbar.setVisible(false);
			this.separator1.setVisible(false);
			this.separator2.setVisible(false);
		}
	}

	private void doCheckNoteEnquiry() {
		logger.debug("Entering");
		if (isEnquiry) {
			this.btnSave.setVisible(false);
			this.remarks.setVisible(false);
			this.remarksText.setVisible(false);
			this.hlayout_cbType.setVisible(false);
			this.separator1.setVisible(false);
			this.separator2.setVisible(false);
		}
	}

	/**
	 * User rights check. <br>
	 * Only components are set visible=true if the logged-in <br>
	 * user have the right for it. <br>
	 * 
	 * The rights are get from the spring framework users grantedAuthority(). A right is only a string. <br>
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		this.window_notesDialog.setVisible(true);
		this.btnSave.setVisible(true);
		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user clicks the save button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnSave(Event event) {
		logger.debug("Entering");
		doSave();

		if (mainControl != null) {
			mainControl.setNotesEntered(true);
		}

		if (!this.isFinanceNotes) {
			closeDialog();
		}

		logger.debug("Leaving");
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
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		doClose(this.btnSave.isVisible());
	}

	/**
	 * Cancel the actual operation. <br>
	 * <br>
	 * Resets to the original status.<br>
	 * 
	 */
	private void doCancel() {
		logger.debug("Entering");

		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aNotes
	 */

	public void doWriteComponentsToBean() {
		logger.debug(Literal.ENTERING);

		Notes aNotes = new Notes();
		doClearMessage();
		ArrayList<WrongValueException> wve = new ArrayList<>();

		String rmrks = remarks.getValue();
		aNotes.setRemarks(rmrks);

		if (!isFinanceNotes) {
			if (this.remarksText.getValue() == null || this.remarksText.getValue().trim().length() <= 0) {
				wve.add(new WrongValueException(this.remarksText, Labels.getLabel("Notes_NotEmpty")));
			} else {
				aNotes.setRemarks(this.remarksText.getValue().trim());
			}
		} else if (this.remarks.getValue() != null) {
			remarks.setValue(rmrks);
			aNotes.setRemarks(rmrks);
		}

		if (wve.size() > 0) {

			this.remarksText.setConstraint("");
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		try {
			aNotes.setRemarkType(this.remarkType.getSelectedItem().getValue().toString());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aNotes.setAlignType(this.alignType.getSelectedItem().getValue().toString());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		UserWorkspace workspace = getUserWorkspace();

		aNotes.setModuleName(this.notes.getModuleName());
		aNotes.setReference(this.notes.getReference());
		aNotes.setVersion(this.notes.getVersion());
		aNotes.setInputBy(workspace.getLoggedInUser().getUserId());
		if (isSystemDate) {
			aNotes.setInputDate(new Timestamp(System.currentTimeMillis()));
		} else {
			Date date = SysParamUtil.getAppDate();
			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(System.currentTimeMillis());
			calendar.set(DateUtil.getYear(date), DateUtil.getMonth(date) - 1, DateUtil.getDay(date));
			aNotes.setInputDate(new Timestamp(calendar.getTimeInMillis()));
		}
		aNotes.setRoleCode(getNotes().getRoleCode());
		this.newNotes = aNotes;

		logger.debug(Literal.LEAVING);
	}

	public void doShowDialog(Notes aNotes) {
		logger.debug("Entering");

		try {
			// fill the components with the data
			setAlignTypeList();
			this.listboxNotes.getItems().clear();

			if (this.isFinanceNotes) {

				setRecommendTypeList();
				getRecommendations();
				this.tabpanel.appendChild(this.window_notesDialog);
				this.window_notesDialog.setWidth("100%");
				if (!isEnquiry) {
					this.window_notesDialog.setHeight("680px");
				}
				this.label_NotesDialog_Type.setValue("Type");
				this.label_NotesDialog_Type.setStyle("color:#555");
				this.space_type1.setWidth("5px");
				this.space_type2.setWidth("180px");
				this.btnClose.setVisible(false);
				this.label_title.setValue(Labels.getLabel("MemoDetails"));

				this.alignType.setVisible(false);
				this.label_NotesDialog_AlignType.setVisible(false);
				this.alignType.setSelectedIndex(0);

				this.remarksText.setVisible(false);
				this.remarks.setVisible(true);
			} else {
				this.remarksText.setVisible(true);
				this.remarks.setVisible(false);
				setRemarkTypeList();
				getList();
				doCheckNoteEnquiry();
				this.window_notesDialog.doModal(); // open the dialog in modal mode
			}
			if (enqiryModule) {
				this.remarkType.setVisible(false);
				this.btnSave.setVisible(false);
				this.remarks.setVisible(false);
				this.label_NotesDialog_Type.setVisible(false);
				this.label_NotesDialog_AlignType.setVisible(false);
			}
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table.
	 */
	public void doSave() {
		logger.debug("Entering");
		// fill the BankDetails object with the components data
		doWriteComponentsToBean();
		// save it to database

		try {
			if (isFinanceNotes) {

				if (StringUtils.trimToEmpty(this.remarks.getValue()).isEmpty()) {
					MessageUtil.showError(Labels.getLabel("Notes_NotEmpty"));
					return;
				}
				if (StringUtils.equals(this.newNotes.getReference(), "null")
						|| StringUtils.isBlank(this.newNotes.getReference())) {
					String finReferece = "";
					try {
						Object object = this.mainControl.getClass().getMethod("getReference").invoke(this.mainControl);
						if (object != null) {
							finReferece = object.toString();
						}
					} catch (Exception e) {
						logger.debug(e);
					}

					if (StringUtils.isBlank(finReferece) || StringUtils.equals(finReferece, "null")) {
						MessageUtil.showError("Reference Must be Entered");
						return;
					}
					this.newNotes.setReference(finReferece);
					this.notes.setReference(finReferece);
				}
			}

			if (this.notesList == null) {
				this.newNotes.setRoleCode(roleCode);
				getNotesService().saveOrUpdate(this.newNotes);
			} else {
				this.newNotes.setUsrLogin(getUserWorkspace().getLoggedInUser().getUserName());
				this.newNotes.setUsrName(getUserWorkspace().getLoggedInUser().getFullName());

				this.notesList.add(this.newNotes);
			}
			setAlignTypeList();
			this.listboxNotes.getItems().clear();

			if (this.isFinanceNotes) {
				getRecommendations();
				this.tabpanel.appendChild(this.window_notesDialog);
				this.window_notesDialog.setTitle("");

				this.btnClose.setVisible(false);
				this.label_title.setValue(Labels.getLabel("MemoDetails"));

				this.alignType.setVisible(false);
				this.alignType.setSelectedIndex(0);
			} else {
				setRemarkTypeList();
				getList();
			}

			this.remarks.setValue("");
			this.remarksText.setValue("");

			notesEntered = true;
		} catch (final DataAccessException e) {
			throw e;
		}
		logger.debug("Leaving");
	}

	public void setRemarkTypeList() {
		Radio radio;
		for (int i = 0; i < remarkTypeList.size(); i++) {
			radio = new Radio();
			radio.setValue(remarkTypeList.get(i).getValue());
			radio.setLabel(remarkTypeList.get(i).getLabel());
			remarkType.appendChild(radio);
		}
		this.remarkType.setSelectedIndex(0);
	}

	public void setRecommendTypeList() {
		Radio radio;
		for (int i = 0; i < recommandTypeList.size(); i++) {
			radio = new Radio();
			radio.setValue(recommandTypeList.get(i).getValue());
			radio.setLabel(recommandTypeList.get(i).getLabel());
			remarkType.appendChild(radio);
		}
		this.remarkType.setSelectedIndex(0);
	}

	public void setAlignTypeList() {
		Radio radio;
		for (int i = 0; i < alignTypeList.size(); i++) {
			radio = new Radio();
			radio.setValue(alignTypeList.get(i).getValue());
			radio.setLabel(alignTypeList.get(i).getLabel());
			alignType.appendChild(radio);
		}
		this.alignType.setSelectedIndex(0);
	}

	public void getList() {
		logger.debug("Entering");

		// Retrieve Notes List By Module Reference
		List<Notes> appList = getNotesService().getNotesList(this.notes, true);

		Listitem item = null;
		Listcell lc = null;
		String alignSide = "left";
		for (int i = 0; i < appList.size(); i++) {

			Notes note = (Notes) appList.get(i);
			if (note != null) {
				item = new Listitem();
				lc = new Listcell();
				lc.setStyle("border:0px");
				lc.setSpan(4);
				Html html = new Html();

				if ("R".equals(note.getAlignType())) {
					if ("right".equals(alignSide)) {
						alignSide = "left";
					} else {
						alignSide = "right";
					}
				}

				String usrAlign = "";
				if ("right".equals(alignSide)) {
					usrAlign = "left";
				} else {
					usrAlign = "right";
				}
				// Fixed Stored Cross Site Scripting Vulnerability in Notes Dialogue
				String content = "<p class='triangle-right " + alignSide + "'> <font style='font-weight:bold;'> "
						+ StringEscapeUtils.escapeHtml(note.getRemarks()) + " </font> <br>  ";
				String date = DateUtil.format(note.getInputDate(), PennantConstants.dateTimeAMPMFormat);
				if ("I".equals(note.getRemarkType())) {
					content = "<div style='word-wrap: break-word; width: 400px'>" + content
							+ "<font style='color:#FF0000;float:" + usrAlign + ";'>" + note.getUsrLogin().toLowerCase()
							+ " : " + date + "</font></p>";
				} else {
					content = "<div style='word-wrap: break-word; width: 400px'>" + content
							+ "<font style='color:white;float:" + usrAlign + ";'>" + note.getUsrLogin().toLowerCase()
							+ " : " + date + "</font></p>";
				}
				html.setContent(content);
				lc.appendChild(html);
				lc.setParent(item);
				listboxNotes.appendChild(item);

			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Method for clear Error messages to Fields
	 */
	public void doClearMessage() {
		logger.debug("Entering");
		this.remarksText.setErrorMessage("");
		Clients.clearWrongValue(this.remarks);
		logger.debug("Leaving");

	}

	/**
	 * Method for clear Error messages to Fields
	 */
	public void onFulfill$remarks() {
		logger.debug("Entering");
		doClearMessage();
		logger.debug("Leaving");

	}

	public void getRecommendations() {
		logger.debug("Entering");

		// Retrieve Notes List By Module Reference
		List<Notes> appList = new ArrayList<>();
		if (StringUtils.isNotEmpty(this.notes.getReference())) {
			appList = getNotesService().getNotesList(this.notes, false);
		}

		if (this.notesList != null && this.notesList.size() > 0) {
			appList.addAll(notesList);
		}

		Listitem item = null;
		Listcell lc = null;
		this.listboxNotes.setSizedByContent(true);
		for (int i = 0; i < appList.size(); i++) {

			Notes note = (Notes) appList.get(i);
			if (note != null) {

				if (i == 0 && isRecommendMand
						&& StringUtils.trimToEmpty(roleCode).equalsIgnoreCase(note.getRoleCode())) {
					try {
						if (mainControl.getClass().getMethod("setRecommendEntered", Boolean.class) != null) {
							mainControl.getClass().getMethod("setRecommendEntered", Boolean.class).invoke(mainControl,
									true);
						}
					} catch (Exception e) {
						logger.error("Exception: ", e);
					}
				}

				item = new Listitem();

				// 1
				lc = new Listcell(note.getUsrName());
				lc.setStyle("cursor:default;text-align:center;");
				lc.setParent(item);
				// 2
				lc = new Listcell();
				if ("R".equals(note.getRemarkType())) {
					lc.setLabel("Recommend");
					lc.setStyle("color:orange;cursor:default;text-align:center;");
				} else {
					lc.setLabel("Comment");
					lc.setStyle("color:green;cursor:default;text-align:center;");
				}
				lc.setParent(item);
				// 3
				lc = new Listcell(DateUtil.format(note.getInputDate(), PennantConstants.dateTimeAMPMFormat));
				lc.setStyle("cursor:default;");
				lc.setParent(item);

				// 4
				lc = new Listcell();
				Html html = new Html();
				// Fixed Stored Cross Site Scripting Vulnerability in Notes Dialogue
				html.setContent(note.getRemarks());
				lc.appendChild(html);
				lc.setStyle("cursor:default;");
				lc.setParent(item);

				listboxNotes.appendChild(item);

			}
		}
		logger.debug("Leaving");
	}

	/**
	 * This method is for append finance basic details to respective parent tabs
	 */
	private void appendFinBasicDetails(ArrayList<Object> finHeaderList) {
		try {
			final Map<String, Object> map = new HashMap<String, Object>();
			map.put("parentCtrl", this);
			map.put("finHeaderList", finHeaderList);
			map.put("moduleName", moduleName);
			if (isNotFinanceProcess) {
				Executions.createComponents("/WEB-INF/pages/Collateral/CollateralSetup/CollateralBasicDetails.zul",
						this.finBasicdetails, map);
			} else {
				Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/FinBasicDetails.zul",
						this.finBasicdetails, map);
			}
		} catch (Exception e) {
			logger.debug(e);
		}
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public Notes getNotes() {
		return notes;
	}

	public void setNotes(Notes notes) {
		this.notes = notes;
	}

	public NotesService getNotesService() {
		return notesService;
	}

	public void setNotesService(NotesService notesService) {
		this.notesService = notesService;
	}

	public void setValidationOn(boolean validationOn) {
		this.validationOn = validationOn;
	}

	public boolean isValidationOn() {
		return this.validationOn;
	}

	public FinBasicDetailsCtrl getFinBasicDetailsCtrl() {
		return finBasicDetailsCtrl;
	}

	public void setFinBasicDetailsCtrl(FinBasicDetailsCtrl finBasicDetailsCtrl) {
		this.finBasicDetailsCtrl = finBasicDetailsCtrl;
	}

	public CollateralBasicDetailsCtrl getCollateralBasicDetailsCtrl() {
		return collateralBasicDetailsCtrl;
	}

	public void setCollateralBasicDetailsCtrl(CollateralBasicDetailsCtrl collateralBasicDetailsCtrl) {
		this.collateralBasicDetailsCtrl = collateralBasicDetailsCtrl;
	}

}
