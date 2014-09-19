/** Copyright 2011 - Pennant Technologies This file is part of Pennant Java
 * Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise
 * stated, the property of Pennant Technologies. Copyright and other
 * intellectual property laws protect these materials. Reproduction or
 * retransmission of the materials, in whole or in part, in any manner, without
 * the prior written consent of the copyright holder, is a violation of
 * copyright law. */

/** FILE HEADER * * FileName : FinanceTypeDialogCtrl.java * * Author : PENNANT
 * TECHONOLOGIES * * Creation Date : 30-06-2011 * * Modified Date : 30-06-2011 *
 * * Description : * * Date Author Version Comments * 30-06-2011 Pennant 0.1
 * Files Created * 1-07-2011 Satish 0.1 Started Working UI * 22-07-2011 Satish
 * Completed SRS * 1-08-2011 Satish Changes and Bug Fixes * 8-08-2011 Satish
 * Composite Key issue * 10-08-2011 Satish Removed Two Fields * 13-08-2011
 * Satish New Tab And New Fields and New * Methods for other tables for Rate* *
 * * */

package com.pennant.webui.rmtmasters.commodityFinanceType;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.spring.SpringUtil;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.FieldComparator;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Row;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.dao.rmtmasters.AccountEngineRuleDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.applicationmaster.Currency;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.rmtmasters.AccountEngineRule;
import com.pennant.backend.model.rmtmasters.AccountType;
import com.pennant.backend.model.rmtmasters.FinanceMarginSlab;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.rmtmasters.FinanceMarginSlabService;
import com.pennant.backend.service.rmtmasters.FinanceTypeService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.component.Uppercasebox;
import com.pennant.search.Filter;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.AmountValidator;
import com.pennant.util.Constraint.IntValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.rmtmasters.commodityFinanceType.model.FinanceMarginSlabListModelItemRenderer;
import com.pennant.webui.util.ButtonStatusCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MultiLineMessageBox;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.pagging.PagedListWrapper;
import com.pennant.webui.util.searchdialogs.ExtendedSearchListBox;

/** ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/SolutionFactory/CommodityFinanceType/commodityFinanceTypeDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br> */

public class CommodityFinanceTypeDialogCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = 4493449538614654801L;
	private final static Logger	logger = Logger.getLogger(CommodityFinanceTypeDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window			window_CommodityFinanceTypeDialog;		// autoWired
	protected Uppercasebox		finType;								// autoWired
	protected Textbox			finTypeDesc;							// autoWired
	protected Textbox			finCcy;									// autoWired
	protected Combobox			cbfinDaysCalType;						// autoWired
	protected Textbox			finAcType;								// autoWired
	protected Textbox			finContingentAcType;					// autoWired
	protected Checkbox			finIsGenRef;							// autoWired
	protected Checkbox			finIsOpenNewFinAc;						// autoWired
	protected Decimalbox		finMinAmount;							// autoWired
	protected Decimalbox		finMaxAmount;							// autoWired
	protected Combobox			cbfinDepositRestrictedTo;				// autoWired
	protected Intbox			finHistRetension;						// autoWired
	
	protected Textbox			finAEBuyOrInception;					// autoWired
	protected Textbox			finAEAmzNorm;							// autoWired
	protected Textbox			finAESellOrMaturity;					// autoWired
	
	protected Label				recordStatus;							// autoWired
	protected Radiogroup		userAction;
	protected Groupbox			groupboxWf;
	protected Row				statusRow;
	
	// not auto wired Var's
	private FinanceType			financeType;    // overHanded per parameter
	private transient CommodityFinanceTypeListCtrl	commodityFinanceTypeListCtrl; // overHanded per parameter

	// old value Var's for edit mode. that we can check if something
	// on the values are edited since the last initialization.
	private transient String		oldVar_finType;
	private transient String		oldVar_finTypeDesc;
	private transient String		oldVar_finCcy;
	private transient int			oldVar_finDaysCalType;
	private transient String		oldVar_finAcType;
	private transient String		oldVar_finContingentAcType;
	private transient boolean		oldVar_finIsGenRef;
	private transient boolean		oldVar_finIsOpenNewFinAc;
	private transient BigDecimal	oldVar_finMaxAmount;
	private transient BigDecimal	oldVar_finMinAmount;
	private transient int			oldVar_finDepositRestrictedTo;
	private transient int			oldVar_finHistRetension;
	private transient String		oldVar_recordStatus;
	
	private transient String		oldVar_finAEBuyOrInception;
	private transient String		oldVar_finAEAmzNorm;
	private transient String		oldVar_finAESellOrMaturity;

	private transient boolean 	validationOn;
	private boolean				notes_Entered = false;
	private boolean				validate = false;	//To Differ save and submit validation	

	// Button controller for the CRUD buttons
	private transient final String		btnCtroller_ClassPrefix	= "button_FinanceTypeDialog_";	
	private transient ButtonStatusCtrl	btnCtrl;
	protected Button  btnNew;	 // autoWire
	protected Button  btnEdit;	 // autoWire
	protected Button  btnDelete; // autoWire
	protected Button  btnSave;	 // autoWire
	protected Button  btnCancel; // autoWire
	protected Button  btnClose;	 // autoWire
	protected Button  btnHelp;	 // autoWire
	protected Button  btnNotes;	 // autoWire

	protected Button  			btnSearchFinCcy;		       				// autoWire
	protected Textbox			lovDescFinCcyName;
	private transient String	oldVar_lovDescFinCcyName;
	protected Button  			btnSearchFinAcType;							// autoWire
	protected Textbox			lovDescFinAcTypeName;
	private transient String	oldVar_lovDescFinAcTypeName;
	protected Button  			btnSearchFinContingentAcType;				// autoWire
	protected Textbox			lovDescFinContingentAcTypeName;
	private transient String	oldVar_lovDescFinContingentAcTypeName;
	
	protected Button			btnSearchFinAEBuyOrInception;				// autoWire
	protected Textbox			lovDescFinAEBuyOrInceptionName;
	private transient String	oldVar_lovDescFinAEBuyOrInceptionName;
	protected Button			btnSearchFinAEAmzNorm;						// autoWire
	protected Textbox			lovDescFinAEAmzNormName;
	private transient String	oldVar_lovDescFinAEAmzNormName;
	protected Button			btnSearchFinAESellOrMaturity;				// autoWire
	protected Textbox			lovDescFinAESellOrMaturityName;
	private transient String	oldVar_lovDescFinAESellOrMaturityName;

	// ServiceDAOs / Domain Classes
	private transient FinanceTypeService	financeTypeService;
	private transient FinanceMarginSlabService	financeMarginSlabService;
	private transient PagedListService		pagedListService;
	private Tab	 FinanceType;		// autoWired
	private Tab	 AccountingEvents;	// autoWired

	private int countRows = PennantConstants.listGridSize;

	//Declaration of listHeaders in listBox of FeeTier
	protected Listheader listheader_FinanceMarginSlab_Slab;
	protected Listheader listheader_FinanceMarginSlab_Margin;
	protected Listheader listheader_FinanceMarginSlab_RecordStatus;
	protected Listheader listheader_FinanceMarginSlab_RecordType;
	
	// MarginsForDepositSlabs List
	protected Button 		btnNew_FinanceMarginSlabs;
	protected Paging 		pagingFinanceMarginSlabs;
	protected Listbox 		listboxFinanceMarginsSlabs;
	private List<FinanceMarginSlab> commodityFinanceMarginSlabsList = new ArrayList<FinanceMarginSlab>();
	private List<FinanceMarginSlab> oldVar_commodityFinanceMarginSlabsList = new ArrayList<FinanceMarginSlab>();
	private PagedListWrapper<FinanceMarginSlab> financeMarginSlabsPagedListWrapper;
	
	// FeeAndCharges List
	protected Button 		btnNew_FeeAndCharges;
	protected Paging 		pagingFeeAndChargesList;
	protected Listbox 		listboxFeeAndCharges;

	private transient AccountEngineRuleDAO accountEngineRuleDAO;
	private transient AccountEngineRule AEnRule=new AccountEngineRule();
	
	/** default constructor.<br> */
	public CommodityFinanceTypeDialogCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/** Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected FinanceType object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception */
	public void onCreate$window_CommodityFinanceTypeDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		// Set the setter objects for PagedListwrapper classes to Initialize

		setFinanceMarginSlabsPagedListWrapper();

		/* set components visible dependent of the users rights */
		doCheckRights();

		/* create the Button Controller. Disable not used buttons during working */
		this.btnCtrl = new ButtonStatusCtrl(getUserWorkspace(), this.btnCtroller_ClassPrefix, true, this.btnNew,
				this.btnEdit, this.btnDelete, this.btnSave, this.btnCancel, this.btnClose, this.btnNotes);

		// get the parameters map that are overHanded by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		// READ OVERHANDED parameters !
		if (args.containsKey("financeType")) {
			this.financeType = (FinanceType) args.get("financeType");
			FinanceType befImage = new FinanceType("");
			BeanUtils.copyProperties(this.financeType, befImage);
			this.financeType.setBefImage(befImage);

			setFinanceType(this.financeType);
		} else {
			setFinanceType(null);
		}

		doLoadWorkFlow(this.financeType.isWorkflow(), this.financeType.getWorkflowId(), this.financeType.getNextTaskId());

		if (isWorkFlowEnabled()) {
			this.userAction = setListRecordStatus(this.userAction);
			getUserWorkspace().alocateRoleAuthorities(getRole(), "FinanceTypeDialog");
		}
		// READ OVERHANDED parameters !
		// we get the financeTypeListWindow controller. So we have access
		// to it and can synchronize the shown data when we do insert, edit or
		// delete financeType here.
		if (args.containsKey("commodityFinanceTypeListCtrl")) {
			setCommodityFinanceTypeListCtrl((CommodityFinanceTypeListCtrl) args.get("commodityFinanceTypeListCtrl"));
		} else {
			setCommodityFinanceTypeListCtrl(null);
		}
		
		this.listheader_FinanceMarginSlab_Slab.setSortAscending(new FieldComparator("slabAmount", true));
		this.listheader_FinanceMarginSlab_Slab.setSortDescending(new FieldComparator("slabAmount", false));
		this.listheader_FinanceMarginSlab_Margin.setSortAscending(new FieldComparator("slabMargin", true));
		this.listheader_FinanceMarginSlab_Margin.setSortDescending(new FieldComparator("slabMargin", false));

		if (isWorkFlowEnabled()){
			this.listheader_FinanceMarginSlab_RecordStatus.setSortAscending(new FieldComparator("recordStatus", true));
			this.listheader_FinanceMarginSlab_RecordStatus.setSortDescending(new FieldComparator("recordStatus", false));
			this.listheader_FinanceMarginSlab_RecordType.setSortAscending(new FieldComparator("recordType", true));
			this.listheader_FinanceMarginSlab_RecordType.setSortDescending(new FieldComparator("recordType", false));
		}else{
			this.listheader_FinanceMarginSlab_RecordStatus.setVisible(false);
			this.listheader_FinanceMarginSlab_RecordType.setVisible(false);
		}

		// set Field Properties
		doSetFieldProperties();
		doShowDialog(getFinanceType());
		logger.debug("Leaving" + event.toString());
	}

	/** Set the properties of the fields, like maxLength.<br> */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		// Empty sent any required attributes
		this.finType.setMaxlength(8);
		this.finTypeDesc.setMaxlength(50);
		this.finCcy.setMaxlength(3);
		this.finAcType.setMaxlength(8);
		this.finContingentAcType.setMaxlength(8);
		this.finMaxAmount.setMaxlength(18);
		this.finMaxAmount.setFormat(PennantApplicationUtil.getAmountFormate(getFinanceType().getLovDescFinFormetter()));
		this.finMinAmount.setMaxlength(18);	
		this.finMinAmount.setFormat(PennantApplicationUtil.getAmountFormate(getFinanceType().getLovDescFinFormetter()));
		this.finHistRetension.setMaxlength(3);
		this.finAEBuyOrInception.setMaxlength(8);
		this.finAEAmzNorm.setMaxlength(8);
		this.finAESellOrMaturity.setMaxlength(8);

		if (isWorkFlowEnabled()) {
			this.groupboxWf.setVisible(true);
			this.statusRow.setVisible(true);
		} else {
			this.groupboxWf.setVisible(false);
			this.statusRow.setVisible(false);
		}
		logger.debug("Leaving");
	}

	/** User rights check. <br>
	 * Only components are set visible=true if the logged-in <br>
	 * user have the right for it. <br>
	 * The rights are get from the spring framework users grantedAuthority(). A
	 * right is only a string. <br> */
	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().alocateAuthorities("FinanceTypeDialog");

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_CommodityFinanceTypeDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_CommodityFinanceTypeDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_CommodityFinanceTypeDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_CommodityFinanceTypeDialog_btnSave"));
		this.btnNew_FeeAndCharges.setVisible(getUserWorkspace().isAllowed("button_CommodityFinanceTypeDialog_btnNew_FeeAndCharges"));
		this.btnNew_FinanceMarginSlabs.setVisible(getUserWorkspace().isAllowed("button_CommodityFinanceTypeDialog_btnNew_FinanceMarginSlabs"));
		this.btnCancel.setVisible(false);
		logger.debug("Leaving");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++ Components events +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/** If we close the dialog window. <br>
	 * 
	 * @param event
	 * @throws Exception */
	public void onClose$window_CommodityFinanceTypeDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		doClose();
		logger.debug("Leaving" + event.toString());
	}

	/** when the "save" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException */
	public void onClick$btnSave(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		doSave();
		logger.debug("Leaving" + event.toString());
	}

	/** when the "edit" button is clicked. <br>
	 * 
	 * @param event */
	public void onClick$btnEdit(Event event) {
		logger.debug("Entering" + event.toString());
		doEdit();
		logger.debug("Leaving" + event.toString());
	}

	/** when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		PTMessageUtils.showHelpWindow(event, window_CommodityFinanceTypeDialog);
		logger.debug("Leaving" + event.toString());
	}

	/** when the "new" button is clicked. <br>
	 * 
	 * @param event */
	public void onClick$btnNew(Event event) {
		logger.debug("Entering" + event.toString());
		doNew();
		logger.debug("Leaving" + event.toString());
	}

	/** when the "delete" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException */
	public void onClick$btnDelete(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		doDelete();
		logger.debug("Leaving" + event.toString());
	}

	/** when the "cancel" button is clicked. <br>
	 * 
	 * @param event */
	public void onClick$btnCancel(Event event) {
		logger.debug("Entering" + event.toString());
		doCancel();
		logger.debug("Leaving" + event.toString());
	}

	/** when the "close" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException */
	public void onClick$btnClose(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		try {
			doClose();
		} catch (final WrongValueException e) {
			logger.error(e);
			throw e;
		}
		logger.debug("Leaving" + event.toString());
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++ GUI operations +++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/** Closes the dialog window. <br>
	 * <br>
	 * Before closing we check if there are unsaved changes in <br>
	 * the components and ask the user if saving the modifications. <br>
	 * 
	 * @throws InterruptedException */
	private void doClose() throws InterruptedException {
		doClearErrMessages();
		logger.debug("Entering");
		boolean close = true;
		if (isDataChanged()) {
			logger.debug("Data Changed(): True");

			// Show a confirm box
			final String msg = Labels.getLabel("message_Data_Modified_Save_Data_YesNo");
			final String title = Labels.getLabel("message.Information");

			MultiLineMessageBox.doSetTemplate();
			int conf = MultiLineMessageBox.show(msg, title, MultiLineMessageBox.YES | MultiLineMessageBox.NO,
					MultiLineMessageBox.QUESTION, true);

			if (conf == MultiLineMessageBox.YES) {
				logger.debug("doClose: Yes");
				doSave();
				close = false;
			} else {
				logger.debug("doClose: No");
			}
		} else {
			logger.debug("Data Changed(): false");
		}
		if (close) {
			closeDialog(this.window_CommodityFinanceTypeDialog, "FinanceType");
		}
		logger.debug("Leaving");
	}

	/** Cancel the actual operation. <br>
	 * <br>
	 * Resets to the original status.<br> */
	private void doCancel() {
		logger.debug("Entering");
		doResetInitValues();
		doReadOnly();
		this.btnCtrl.setInitEdit();
		logger.debug("Leaving");
	}

	/** Writes the bean data to the components.<br>
	 * 
	 * @param aFinanceType
	 *           FinanceType */

	public void doWriteBeanToComponents(FinanceType aFinanceType) {
		logger.debug("Entering");
		
		doFillMarginSlabs(aFinanceType.getFinanceMarginSlabsList());

		
		this.finType.setValue(aFinanceType.getFinType());
		this.finTypeDesc.setValue(aFinanceType.getFinTypeDesc());
		this.finCcy.setValue(aFinanceType.getFinCcy());
		this.finAcType.setValue(aFinanceType.getFinAcType());
		this.finContingentAcType.setValue(aFinanceType.getFinContingentAcType());
		this.finIsGenRef.setChecked(aFinanceType.isFinIsGenRef());
		this.finIsOpenNewFinAc.setChecked(aFinanceType.isFinIsOpenNewFinAc());
		this.finMaxAmount.setValue(PennantAppUtil.formateAmount(aFinanceType.getFinMaxAmount(),getFinanceType().getLovDescFinFormetter()));
		this.finMinAmount.setValue(PennantAppUtil.formateAmount(aFinanceType.getFinMinAmount(), getFinanceType().getLovDescFinFormetter()));

		fillProfitDaysBasis(this.cbfinDaysCalType,aFinanceType.getFinDaysCalType());
		fillDepositRestrictedTo(this.cbfinDepositRestrictedTo,aFinanceType.getFinDepositRestrictedTo());

		this.finHistRetension.setValue(aFinanceType.getFinHistRetension());
		
		this.finAEBuyOrInception.setValue(aFinanceType.getFinAEBuyOrInception());
		this.finAEAmzNorm.setValue(aFinanceType.getFinAEAmzNorm());
		this.finAESellOrMaturity.setValue(aFinanceType.getFinAESellOrMaturity());
		
		if (aFinanceType.isNewRecord()) {
			this.lovDescFinCcyName.setValue("");
			this.lovDescFinAcTypeName.setValue("");
			this.lovDescFinContingentAcTypeName.setValue("");

			this.finAEAmzNorm.setValue(String.valueOf(3));
			this.lovDescFinAEAmzNormName.setValue("SYSTEM-System Default");
			this.finAEBuyOrInception.setValue(String.valueOf(3));
			this.lovDescFinAEBuyOrInceptionName.setValue("SYSTEM-System Default");
			this.finAESellOrMaturity.setValue(String.valueOf(3));
			this.lovDescFinAESellOrMaturityName.setValue("SYSTEM-System Default");

		} else {

			if (aFinanceType.getLovDescFinCcyName() != null) {
				this.lovDescFinCcyName.setValue(aFinanceType.getFinCcy() + "-" + aFinanceType.getLovDescFinCcyName());
			}
			if (aFinanceType.getLovDescFinAcTypeName() != null) {
				this.lovDescFinAcTypeName.setValue(aFinanceType.getFinAcType() + "-"
						+ aFinanceType.getLovDescFinAcTypeName());
			}
			if (aFinanceType.getLovDescFinContingentAcTypeName() != null) {
				this.lovDescFinContingentAcTypeName.setValue(aFinanceType.getFinContingentAcType() + "-"
						+ aFinanceType.getLovDescFinContingentAcTypeName());
			}
			
			if (aFinanceType.getLovDescFinAEAmzNormName() != null) {
				this.lovDescFinAEAmzNormName.setValue(aFinanceType.getLovDescEVFinAEAmzNormName() + "-"
						+ aFinanceType.getLovDescFinAEAmzNormName());
			} else {
				AEnRule.setAEEvent("AMZ");
				AEnRule = this.accountEngineRuleDAO.getAccountEngineRuleBySysDflt(AEnRule, "",false);
				if (AEnRule.getaERuleId() != Long.MIN_VALUE) {
					this.finAEAmzNorm.setValue(AEnRule.getStringaERuleId());
					this.lovDescFinAEAmzNormName.setValue(AEnRule.getAERule() + "-" + AEnRule.getAERuleDesc());
				}

			}
			
			if (aFinanceType.getLovDescFinAEBuyOrInceptionName() != null) {
				this.lovDescFinAEBuyOrInceptionName.setValue(aFinanceType.getLovDescEVFinAEBuyOrInceptionName() + "-"
						+ aFinanceType.getLovDescFinAEBuyOrInceptionName());
			} else {
				AEnRule.setAEEvent("BUY");
				AEnRule = this.accountEngineRuleDAO.getAccountEngineRuleBySysDflt(AEnRule, "",false);
				if (AEnRule.getaERuleId() != Long.MIN_VALUE) {
					this.finAEBuyOrInception.setValue(AEnRule.getStringaERuleId());
					this.lovDescFinAEBuyOrInceptionName.setValue(AEnRule.getAERule() + "-" + AEnRule.getAERuleDesc());
				}

			}

			if (aFinanceType.getLovDescFinAESellOrMaturityName() != null) {
				this.lovDescFinAESellOrMaturityName.setValue(aFinanceType.getLovDescEVFinAESellOrMaturityName() + "-"
						+ aFinanceType.getLovDescFinAESellOrMaturityName());
			} else {
				AEnRule.setAEEvent("SELL");
				AEnRule = this.accountEngineRuleDAO.getAccountEngineRuleBySysDflt(AEnRule, "",false);
				if (AEnRule.getaERuleId() != Long.MIN_VALUE) {
					this.finAESellOrMaturity.setValue(AEnRule.getStringaERuleId());
					this.lovDescFinAESellOrMaturityName.setValue(AEnRule.getAERule() + "-" + AEnRule.getAERuleDesc());
				}

			}
		}
		this.recordStatus.setValue(aFinanceType.getRecordStatus());
		logger.debug("Leaving");
	}

	/** Writes the components values to the bean.<br>
	 * 
	 * @param aFinanceType */

	public void doWriteComponentsToBean(FinanceType aFinanceType) {
		logger.debug("Entering");

		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		// +++++++++++++ Start of FinanceType tab ++++++++++++//
		try {
			aFinanceType.setFinType(this.finType.getValue().toUpperCase());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinanceType.setFinTypeDesc(this.finTypeDesc.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinanceType.setLovDescFinCcyName(this.lovDescFinCcyName.getValue());			
			if(this.finCcy.getValue().equals("")) {
				wve.add(new WrongValueException(this.lovDescFinCcyName, Labels.getLabel("FIELD_NO_INVALID", new String[] { Labels.getLabel("label_CommodityFinanceTypeDialog_FinCcy") })));
			} else {
				aFinanceType.setFinCcy(this.finCcy.getValue());
			}			
			
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if(validate && validateCombobox(this.cbfinDaysCalType).equals("#")){
				throw new WrongValueException(this.cbfinDaysCalType, Labels.getLabel("STATIC_INVALID",
						new String[] { Labels.getLabel("label_CommodityFinanceTypeDialog_FinDaysCalType.value") }));
			}
			aFinanceType.setFinDaysCalType(validateCombobox(this.cbfinDaysCalType));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinanceType.setLovDescFinAcTypeName(this.lovDescFinAcTypeName.getValue());
			aFinanceType.setFinAcType(this.finAcType.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinanceType.setLovDescFinContingentAcTypeName(this.lovDescFinContingentAcTypeName.getValue());
			aFinanceType.setFinContingentAcType(this.finContingentAcType.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinanceType.setFinIsGenRef(this.finIsGenRef.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (this.finMaxAmount.getValue() != null) {
				aFinanceType.setFinMaxAmount(PennantAppUtil.unFormateAmount(
						this.finMaxAmount.getValue(), getFinanceType().getLovDescFinFormetter()));
			} else {
				aFinanceType.setFinMaxAmount(BigDecimal.ZERO);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (this.finMinAmount.getValue() != null) {
				aFinanceType.setFinMinAmount(PennantAppUtil.unFormateAmount(
						this.finMinAmount.getValue(), getFinanceType().getLovDescFinFormetter()));
			} else {
				aFinanceType.setFinMinAmount(BigDecimal.ZERO);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if(validate && validateCombobox(this.cbfinDepositRestrictedTo).equals("#")){
				throw new WrongValueException(this.cbfinDepositRestrictedTo, Labels.getLabel("STATIC_INVALID",
						new String[] { Labels.getLabel("label_CommodityFinanceTypeDialog_FinDepositRestrictedTo.value") }));
			}
			aFinanceType.setFinDepositRestrictedTo(validateCombobox(this.cbfinDepositRestrictedTo));
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aFinanceType.setFinIsOpenNewFinAc(this.finIsOpenNewFinAc.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinanceType.setFinHistRetension(this.finHistRetension.getValue());

		} catch (WrongValueException we) {
			wve.add(we);
		}
		// To check finMaxAmount has higher value than the finMinAmount
		try {
			mustBeHigher(finMaxAmount, finMinAmount, "label_CommodityFinanceTypeDialog_FinMaxAmount.value",
			"label_CommodityFinanceTypeDialog_FinMinAmount.value");

		} catch (WrongValueException we) {
			wve.add(we);
		}

		showErrorDetails(wve, FinanceType);

		// +++++++++++ End of Finance Type Tab +++++++++++++++++//

		// ++++++++++++++++ Start of Accounting Event Tab ++++++++++++//
		
		try {
			aFinanceType.setLovDescFinAEAmzNormName(this.lovDescFinAEAmzNormName.getValue());
			aFinanceType.setFinAEAmzNorm(this.finAEAmzNorm.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			aFinanceType.setLovDescFinAEBuyOrInceptionName(this.lovDescFinAEBuyOrInceptionName.getValue());
			aFinanceType.setFinAEBuyOrInception(this.finAEBuyOrInception.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			aFinanceType.setLovDescFinAESellOrMaturityName(this.lovDescFinAESellOrMaturityName.getValue());
			aFinanceType.setFinAESellOrMaturity(this.finAESellOrMaturity.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		showErrorDetails(wve, AccountingEvents);

		// ++++++++++++++ End of Accounting Event Tab +++++++++++++++++++//

		aFinanceType.setFinanceMarginSlabsList(this.commodityFinanceMarginSlabsList);
		
		doRemoveValidation();
		doRemoveLOVValidation();
		aFinanceType.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving");

	}

	// For Tab Wise validations
	private void showErrorDetails(ArrayList<WrongValueException> wve, Tab tab) {
		logger.debug("Entering");
		if (wve.size() > 0) {
			tab.setSelected(true);
			doRemoveValidation();
			doRemoveLOVValidation();
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}
		logger.debug("Leaving");

	}

	/** Opens the Dialog window modal. It checks if the dialog opens with a new or
	 * existing object and set the readOnly mode accordingly.
	 * 
	 * @param aFinanceType
	 * @throws InterruptedException */
	public void doShowDialog(FinanceType aFinanceType) throws InterruptedException {
		logger.debug("Entering");

		// if aFinanceType == null then we opened the Dialog without
		// arguments for a given entity, so we get a new Object().
		if (aFinanceType == null) {
			/** !!! DO NOT BREAK THE TIERS !!! */
			// We don't create a new DomainObject() in the frontEnd.
			// We GET it from the backEnd.
			aFinanceType = getFinanceTypeService().getNewCommodityFinanceType();

			setFinanceType(aFinanceType);
		} else {
			setFinanceType(aFinanceType);
		}

		// set ReadOnly mode accordingly if the object is new or not.
		if (aFinanceType.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.finType.focus();
		} else {
			this.finTypeDesc.focus();
			if (isWorkFlowEnabled()) {
				this.btnNotes.setVisible(true);
				doEdit();
			} else {
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
			}
		}

		try {
			// fill the components with the data
			doWriteBeanToComponents(aFinanceType);
			doStoreInitValues();

			if (getFinanceType().isNewRecord()) {
				
			}
			// stores the initial data for comparing if they are changed
			// during user action.
			doStoreInitValues();
			setDialog(this.window_CommodityFinanceTypeDialog);
		} catch (final Exception e) {
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++++++++ helpers ++++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/** Stores the initial values in member Var's. <br> */
	private void doStoreInitValues() {
		logger.debug("Entering");
		this.oldVar_finType = this.finType.getValue();
		this.oldVar_finTypeDesc = this.finTypeDesc.getValue();
		this.oldVar_finCcy = this.finCcy.getValue();
		this.oldVar_lovDescFinCcyName = this.lovDescFinCcyName.getValue();
		this.oldVar_finDaysCalType = this.cbfinDaysCalType.getSelectedIndex();
		this.oldVar_finAcType = this.finAcType.getValue();
		this.oldVar_lovDescFinAcTypeName = this.lovDescFinAcTypeName.getValue();
		this.oldVar_finContingentAcType = this.finContingentAcType.getValue();
		this.oldVar_lovDescFinContingentAcTypeName = this.lovDescFinContingentAcTypeName.getValue();
		this.oldVar_finIsGenRef = this.finIsGenRef.isChecked();
		this.oldVar_finMaxAmount = this.finMaxAmount.getValue();
		this.oldVar_finMinAmount = this.finMinAmount.getValue();
		this.oldVar_finDepositRestrictedTo = this.cbfinDepositRestrictedTo.getSelectedIndex();
		this.oldVar_finIsOpenNewFinAc = this.finIsOpenNewFinAc.isChecked();
		this.oldVar_finHistRetension = this.finHistRetension.intValue();
	
		this.oldVar_finAEAmzNorm = this.finAEAmzNorm.getValue();
		this.oldVar_lovDescFinAEAmzNormName = this.lovDescFinAEAmzNormName.getValue();
		this.oldVar_finAEBuyOrInception = this.finAEBuyOrInception.getValue();
		this.oldVar_lovDescFinAEBuyOrInceptionName = this.lovDescFinAEBuyOrInceptionName.getValue();
		this.oldVar_finAESellOrMaturity = this.finAESellOrMaturity.getValue();
		this.oldVar_lovDescFinAESellOrMaturityName = this.lovDescFinAESellOrMaturityName.getValue();
		this.oldVar_recordStatus = this.recordStatus.getValue();
		this.oldVar_commodityFinanceMarginSlabsList = this.commodityFinanceMarginSlabsList;
		logger.debug("Leaving");
	}

	/** Resets the initial values from member Var's. <br> */
	private void doResetInitValues() {
		logger.debug("Entering");
		this.finType.setValue(this.oldVar_finType);
		this.finTypeDesc.setValue(this.oldVar_finTypeDesc);
		this.finCcy.setValue(this.oldVar_finCcy);
		this.lovDescFinCcyName.setValue(this.oldVar_lovDescFinCcyName);
		this.finAcType.setValue(this.oldVar_finAcType);
		this.lovDescFinAcTypeName.setValue(this.oldVar_lovDescFinAcTypeName);
		this.finContingentAcType.setValue(this.oldVar_finContingentAcType);
		this.lovDescFinContingentAcTypeName.setValue(this.oldVar_lovDescFinContingentAcTypeName);
		this.finIsGenRef.setChecked(this.oldVar_finIsGenRef);
		this.finMaxAmount.setValue(this.oldVar_finMaxAmount);
		this.finMinAmount.setValue(this.oldVar_finMinAmount);
		this.finIsOpenNewFinAc.setChecked(this.oldVar_finIsOpenNewFinAc);
		this.finHistRetension.setValue(this.oldVar_finHistRetension);
		this.cbfinDepositRestrictedTo.setSelectedIndex(this.oldVar_finDepositRestrictedTo);
		this.cbfinDaysCalType.setSelectedIndex(this.oldVar_finDaysCalType);
		this.finAEAmzNorm.setValue(this.oldVar_finAEAmzNorm);
		this.lovDescFinAEAmzNormName.setValue(this.oldVar_lovDescFinAEAmzNormName);
		this.finAEBuyOrInception.setValue(this.oldVar_finAEBuyOrInception);
		this.lovDescFinAEBuyOrInceptionName.setValue(this.oldVar_lovDescFinAEBuyOrInceptionName);
		this.finAESellOrMaturity.setValue(this.oldVar_finAESellOrMaturity);
		this.lovDescFinAESellOrMaturityName.setValue(this.oldVar_lovDescFinAESellOrMaturityName);
		this.recordStatus.setValue(this.oldVar_recordStatus);

		if (isWorkFlowEnabled()) {
			this.userAction.setSelectedIndex(0);
		}
		logger.debug("Leaving");
	}

	/** Checks, if data are changed since the last call of <br>
	 * doStoreInitData() . <br>
	 * 
	 * @return true, if data are changed, otherwise false */
	private boolean isDataChanged() {
		// To clear the Error Messages
		doClearMessage();

		if (this.oldVar_finType != this.finType.getValue()) {
			return true;
		}
		if (this.oldVar_finTypeDesc != this.finTypeDesc.getValue()) {
			return true;
		}
		if (this.oldVar_finCcy != this.finCcy.getValue()) {
			return true;
		}
		if (this.oldVar_finAcType != this.finAcType.getValue()) {
			return true;
		}
		if (this.oldVar_finContingentAcType != this.finContingentAcType.getValue()) {
			return true;
		}
		if (this.oldVar_finIsGenRef != this.finIsGenRef.isChecked()) {
			return true;
		}
		if (this.oldVar_finMaxAmount != this.finMaxAmount.getValue()) {
			return true;
		}
		if (this.oldVar_finMinAmount != this.finMinAmount.getValue()) {
			return true;
		}
		if (this.oldVar_finIsOpenNewFinAc != this.finIsOpenNewFinAc.isChecked()) {
			return true;
		}
		if (this.oldVar_finHistRetension != this.finHistRetension.intValue()) {
			return true;
		}
		if (this.oldVar_finAEAmzNorm != this.finAEAmzNorm.getValue()) {
			return true;
		}
		if (this.oldVar_finAEBuyOrInception != this.finAEBuyOrInception.getValue()) {
			return true;
		}
		if (this.oldVar_finAESellOrMaturity != this.finAESellOrMaturity.getValue()) {
			return true;
		}
		if(this.oldVar_finDaysCalType != this.cbfinDaysCalType.getSelectedIndex()){
			return true;
		}
		if(this.oldVar_finDepositRestrictedTo != this.cbfinDepositRestrictedTo.getSelectedIndex()){
			return true;
		}
		
		//margin Slabs list comparison
		if (this.oldVar_commodityFinanceMarginSlabsList != this.commodityFinanceMarginSlabsList) {
			return true;
		}
		
		//Fee Charges list comparison
	
		
		return false;
	}

	/** Sets the Validation by setting the accordingly constraints to the fields. */

	private void doSetValidation() {
		logger.debug("Entering");
		setValidationOn(true);
		
		// ++++++++++++ Finance Type tab +++++++++++++++++++//
		
		if (!this.finType.isReadonly()) {
			this.finType.setConstraint(new PTStringValidator(Labels.getLabel("label_CommodityFinanceTypeDialog_FinType.value"),
					         PennantRegularExpressions.REGEX_ALPHANUM, true));
		}
		
		if (validate) {// To Check Whether it is save or submit
						// if save no validation else it should validate
			if (!this.finTypeDesc.isReadonly()) {
				this.finTypeDesc.setConstraint(new PTStringValidator(Labels
						.getLabel("label_CommodityFinanceTypeDialog_FinTypeDesc.value"), PennantRegularExpressions.REGEX_DESCRIPTION, true));
			}
			if (!this.finMaxAmount.isReadonly() && this.finMaxAmount.getValue() != null
					&& this.finMaxAmount.getValue().intValue() != 0) {
				this.finMaxAmount.setConstraint(new AmountValidator(18, 0, Labels
						.getLabel("label_CommodityFinanceTypeDialog_FinMaxAmount.value")));
			}
			if (!this.finMinAmount.isReadonly() && this.finMinAmount.getValue() != null
					&& this.finMinAmount.getValue().intValue() != 0) {
				this.finMinAmount.setConstraint(new AmountValidator(18, 0, Labels
						.getLabel("label_CommodityFinanceTypeDialog_FinMinAmount.value")));
			}
			if (!this.finHistRetension.isReadonly() && this.finHistRetension.getValue().intValue() != 0) {
				this.finHistRetension.setConstraint(new IntValidator(3, Labels
						.getLabel("label_CommodityFinanceTypeDialog_FinHistRetension.value")));
			}
		}
		logger.debug("Leaving");
	}

	/** Disables the Validation by setting empty constraints. */
	private void doRemoveValidation() {
		logger.debug("Entering");
		setValidationOn(false);
		this.finType.setConstraint("");
		this.finTypeDesc.setConstraint("");
		this.finMaxAmount.setConstraint("");
		this.finMinAmount.setConstraint("");
		this.finHistRetension.setConstraint("");
		logger.debug("Leaving");
	}

	/** Set Validations for LOV Fields */
	private void doSetLOVValidation() {
		logger.debug("Entering");

		// +++++++ Finance Type Tab +++++++++++++//
		
		this.lovDescFinCcyName.setConstraint(new PTStringValidator(Labels.getLabel(
						"label_CommodityFinanceTypeDialog_FinCcy.value"), null, true));
		if (validate) {
			this.lovDescFinAcTypeName.setConstraint(new PTStringValidator(Labels.getLabel(
						"label_CommodityFinanceTypeDialog_FinAcType.value"), null, true));
			this.lovDescFinContingentAcTypeName.setConstraint(new PTStringValidator(Labels.getLabel(
						"label_CommodityFinanceTypeDialog_FinContingentAcType.value"), null, true));

		// ++++++++++ Accounting Events tab ++++++++++++++//
			
			this.lovDescFinAEAmzNormName.setConstraint(new PTStringValidator(Labels.getLabel("label_CommodityFinanceTypeDialog_FinAEAmzNorm.value")
					, null, true));
			this.lovDescFinAEBuyOrInceptionName.setConstraint(new PTStringValidator(Labels.getLabel("label_CommodityFinanceTypeDialog_FinAEBuyOrInception.value"),
					null, true));
			this.lovDescFinAESellOrMaturityName.setConstraint(new PTStringValidator(Labels.getLabel("label_CommodityFinanceTypeDialog_FinAESellOrMaturity.value"),
					null, true));
		}
		logger.debug("Leaving");
	}

	/** Remove validations for LOV Fields */
	private void doRemoveLOVValidation() {
		logger.debug("Entering");
		this.lovDescFinCcyName.setConstraint("");
		this.lovDescFinAcTypeName.setConstraint("");
		this.lovDescFinContingentAcTypeName.setConstraint("");
		this.lovDescFinAEAmzNormName.setConstraint("");
		this.lovDescFinAEBuyOrInceptionName.setConstraint("");
		this.lovDescFinAESellOrMaturityName.setConstraint("");
		logger.debug("Leaving");
	}

	/** Remove Error Messages for Fields */
	private void doClearMessage() {
		logger.debug("Enterring");
		this.finType.setConstraint("");
		this.finTypeDesc.setErrorMessage("");
		this.finMaxAmount.setErrorMessage("");
		this.finMinAmount.setErrorMessage("");
		this.finHistRetension.setErrorMessage("");
		logger.debug("Leaving");
	}
	
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++++ CRUD operations +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/** Deletes a FinanceType object from database.<br>
	 * 
	 * @throws InterruptedException */
	@SuppressWarnings("rawtypes")
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");
		final FinanceType aFinanceType = new FinanceType("");
		BeanUtils.copyProperties(getFinanceType(), aFinanceType);
		String tranType = PennantConstants.TRAN_WF;
		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> "
		+ aFinanceType.getFinType();
		final String title = Labels.getLabel("message.Deleting.Record");
		MultiLineMessageBox.doSetTemplate();

		int conf = (MultiLineMessageBox.show(msg, title, MultiLineMessageBox.YES | MultiLineMessageBox.NO,
				Messagebox.QUESTION, true));

		if (conf == MultiLineMessageBox.YES) {
			logger.debug("doDelete: Yes");

			if (StringUtils.trimToEmpty(aFinanceType.getRecordType()).equals("")) {
				aFinanceType.setVersion(aFinanceType.getVersion() + 1);
				aFinanceType.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()) {
					aFinanceType.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}
			try {
				if (doProcess(aFinanceType, tranType)) {

					final JdbcSearchObject<FinanceType> soFinanceType = getCommodityFinanceTypeListCtrl().getSearchObj();
					// Set the ListModel
					getCommodityFinanceTypeListCtrl().getPagedListWrapper().setSearchObject(soFinanceType);

					// now synchronize the FinanceType listBox
					final ListModelList lml = (ListModelList) getCommodityFinanceTypeListCtrl().listBoxFinanceType.getListModel();

					// Check if the FinanceType object is new or updated -1
					// means that the object is not in the list, so it's new ..
					if (lml.indexOf(aFinanceType) == -1) {
					} else {
						lml.remove(lml.indexOf(aFinanceType));
					}
					closeDialog(this.window_CommodityFinanceTypeDialog, "FinanceType");
				}
			} catch (DataAccessException e) {
				logger.error(e);
				showMessage(e);
			}
		}
		logger.debug("Leaving");
	}

	/** Create a new FinanceType object. <br> */
	private void doNew() {
		logger.debug("Entering");

		// remember the old Var's
		doStoreInitValues();
		/** !!! DO NOT BREAK THE TIERS !!! */
		// we don't create a new FinanceType() in the frontEnd.
		// we get it from the backEnd.
		final FinanceType aFinanceType = getFinanceTypeService().getNewCommodityFinanceType();
		aFinanceType.setNewRecord(true);
		setFinanceType(aFinanceType);
		doClear(); // clear all components
		doEdit(); // edit mode
		this.btnCtrl.setBtnStatus_New();

		// setFocus
		this.finType.focus();
		logger.debug("Leaving");
	}

	/** Set the components for edit mode. <br>
	 * MSTGRP1_MAKER */
	private void doEdit() {
		logger.debug("Entering");
		if (getFinanceType().isNewRecord()) {
			this.finType.setReadonly(false);
			this.btnCancel.setVisible(false);
			this.finIsOpenNewFinAc.setChecked(true);
			this.finHistRetension.setValue(12);
		} else {
			this.finType.setReadonly(true);
			this.btnCancel.setVisible(true);
		}

		this.finTypeDesc.setReadonly(isReadOnly("CommodityFinanceTypeDialog_finTypeDesc"));
		this.btnSearchFinCcy.setDisabled(isReadOnly("CommodityFinanceTypeDialog_finCcy"));
		this.lovDescFinCcyName.setReadonly(isReadOnly("CommodityFinanceTypeDialog_finCcy"));
		this.btnSearchFinAcType.setDisabled(isReadOnly("CommodityFinanceTypeDialog_finAcType"));
		this.btnSearchFinContingentAcType.setDisabled(isReadOnly("CommodityFinanceTypeDialog_finContingentAcType"));
		this.finIsGenRef.setDisabled(isReadOnly("CommodityFinanceTypeDialog_finIsGenRef"));
		this.finMaxAmount.setReadonly(isReadOnly("CommodityFinanceTypeDialog_finMaxAmount"));
		this.finMinAmount.setReadonly(isReadOnly("CommodityFinanceTypeDialog_finMinAmount"));
		this.finIsOpenNewFinAc.setDisabled(isReadOnly("CommodityFinanceTypeDialog_finIsOpenNewFinAc"));
		this.cbfinDaysCalType.setDisabled(isReadOnly("CommodityFinanceTypeDialog_finDaysCalType"));
		this.cbfinDepositRestrictedTo.setDisabled(isReadOnly("CommodityFinanceTypeDialog_finDepositRestrictedTo"));
		this.finHistRetension.setReadonly(isReadOnly("CommodityFinanceTypeDialog_finHistRetension"));
		this.btnSearchFinAEAmzNorm.setDisabled(isReadOnly("CommodityFinanceTypeDialog_finAEAmzNorm"));
		this.btnSearchFinAEBuyOrInception.setDisabled(isReadOnly("CommodityFinanceTypeDialog_finAEBuyOrInception"));
		this.btnSearchFinAESellOrMaturity.setDisabled(isReadOnly("CommodityFinanceTypeDialog_finAESellOrMaturity"));

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.financeType.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			this.btnCtrl.setBtnStatus_Edit();
			btnCancel.setVisible(true);
		}
		logger.debug("Leaving");
	}

	/** Set the components to ReadOnly. <br> */
	public void doReadOnly() {
		logger.debug("Entering");
		this.finType.setReadonly(true);
		this.finTypeDesc.setReadonly(true);
		this.btnSearchFinCcy.setDisabled(true);
		this.lovDescFinCcyName.setReadonly(true);
		this.btnSearchFinAcType.setDisabled(true);
		this.btnSearchFinContingentAcType.setDisabled(true);
		this.finIsGenRef.setDisabled(true);
		this.finMaxAmount.setReadonly(true);
		this.finMinAmount.setReadonly(true);
		this.finIsOpenNewFinAc.setDisabled(true);
		this.finHistRetension.setReadonly(true);
		this.cbfinDaysCalType.setDisabled(true);
		this.cbfinDepositRestrictedTo.setDisabled(true);
		this.btnSearchFinAEAmzNorm.setDisabled(true);
		this.btnSearchFinAEBuyOrInception.setDisabled(true);
		this.btnSearchFinAESellOrMaturity.setDisabled(true);
		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(true);
			}
		}
		if (isWorkFlowEnabled()) {
			this.recordStatus.setValue("");
			this.userAction.setSelectedIndex(0);
		}
		logger.debug("Leaving");
	}

	/** Clears the components values. <br> */
	public void doClear() {
		logger.debug("Entering");
		// remove validation, if there are a save before
		this.finType.setValue("");
		this.finTypeDesc.setValue("");
		this.finCcy.setValue("");
		this.lovDescFinCcyName.setValue("");
		this.finAcType.setValue("");
		this.lovDescFinAcTypeName.setValue("");
		this.finContingentAcType.setValue("");
		this.lovDescFinContingentAcTypeName.setValue("");
		this.finIsGenRef.setChecked(false);
		this.finMaxAmount.setValue("");
		this.finMinAmount.setValue("");
		this.finHistRetension.setText("");
		this.cbfinDaysCalType.setSelectedIndex(0);
		this.cbfinDepositRestrictedTo.setSelectedIndex(0);
		this.finAEAmzNorm.setValue("");
		this.lovDescFinAEAmzNormName.setValue("");
		this.finAEBuyOrInception.setValue("");
		this.lovDescFinAEBuyOrInceptionName.setValue("");
		this.finAESellOrMaturity.setValue("");
		this.lovDescFinAESellOrMaturityName.setValue("");
		logger.debug("Leaving");
	}

	/** Saves the components to table. <br>
	 * 
	 * @throws InterruptedException */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");
		
		final FinanceType aFinanceType = new FinanceType("");
		BeanUtils.copyProperties(getFinanceType(), aFinanceType);
		
		aFinanceType.setFinIsActive(true);
		boolean isNew = false;
		if ("Submit".equals(userAction.getSelectedItem().getLabel())) {
			validate = true;// Stop validations in save mode
		}else {
			validate = false;// Stop validations in save mode
		}
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// force validation, if on, than execute by component.getValue()
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		doSetValidation();
		doSetLOVValidation();
		// fill the FinanceType object with the components data
		doWriteComponentsToBean(aFinanceType);

		// Write the additional validations as per below example
		// get the selected branch object from the listBox
		// Do data level validations here

		isNew = aFinanceType.isNewRecord();
		String tranType = "";
		
		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.trimToEmpty(aFinanceType.getRecordType()).equals("")) {
				aFinanceType.setVersion(aFinanceType.getVersion() + 1);
				if (isNew) {
					aFinanceType.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aFinanceType.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aFinanceType.setNewRecord(true);
				}
			}
		} else {
			aFinanceType.setVersion(aFinanceType.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {
			if (doProcess(aFinanceType, tranType)) {
				doWriteBeanToComponents(aFinanceType);
				// ++ create the searchObject and initialize sorting ++ //
				final JdbcSearchObject<FinanceType> soFinanceType = getCommodityFinanceTypeListCtrl().getSearchObj();

				// Set the ListModel
				getCommodityFinanceTypeListCtrl().pagingFinanceTypeList.setActivePage(0);
				getCommodityFinanceTypeListCtrl().getPagedListWrapper().setSearchObject(soFinanceType);

				// call from cusromerList then synchronize the FinanceType
				// listBox
				if (getCommodityFinanceTypeListCtrl().listBoxFinanceType != null) {
					// now synchronize the FinanceType listBox
					getCommodityFinanceTypeListCtrl().listBoxFinanceType.getListModel();
				}

				doReadOnly();
				this.btnCtrl.setBtnStatus_Save();

				// Close the Existing Dialog
				closeDialog(this.window_CommodityFinanceTypeDialog, "FinanceType");
			}

		} catch (final DataAccessException e) {
			logger.error(e);
			showMessage(e);
		}
		logger.debug("Leaving");
	}

	/** Set the workFlow Details List to Object
	 * 
	 * @param aFinanceType
	 *           (FinanceType)
	 * @param tranType
	 *           (String)
	 * @return boolean */
	private boolean doProcess(FinanceType aFinanceType, String tranType) {
		logger.debug("Entering");
		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aFinanceType.setLastMntBy(getUserWorkspace().getLoginUserDetails().getLoginUsrID());
		aFinanceType.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aFinanceType.setUserDetails(getUserWorkspace().getLoginUserDetails());

		if (isWorkFlowEnabled()) {
			String taskId = getWorkFlow().getTaskId(getRole());
			String nextTaskId = "";
			//Upgraded to ZK-6.5.1.1 Added casting to String 	
			aFinanceType.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aFinanceType.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getWorkFlow().getNextTaskIds(taskId, aFinanceType);
				}

				if (PennantConstants.WF_Audit_Notes.equals(getWorkFlow().getAuditingReq(taskId, aFinanceType))) {
					try {
						if (!isNotes_Entered()) {
							PTMessageUtils.showErrorMessage(Labels.getLabel("Notes_NotEmpty"));
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

			aFinanceType.setTaskId(taskId);
			aFinanceType.setNextTaskId(nextTaskId);
			aFinanceType.setRoleCode(getRole());
			aFinanceType.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aFinanceType, tranType);
			String operationRefs = getWorkFlow().getOperationRefs(taskId, aFinanceType);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aFinanceType, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aFinanceType, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}
		logger.debug("Leaving");
		return processCompleted;
	}

	/** Get the result after processing DataBase Operations
	 * 
	 * @param auditHeader
	 *           (AuditHeader)
	 * @param method
	 *           (String)
	 * @return boolean */
	private boolean doSaveProcess(AuditHeader auditHeader, String method) {
		logger.debug("Entering");
		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		FinanceType afinanceType = (FinanceType) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		try {
			while (retValue == PennantConstants.porcessOVERIDE) {
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase("")) {
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
						auditHeader = getFinanceTypeService().delete(auditHeader);
						deleteNotes = true;
					} else {
						auditHeader = getFinanceTypeService().saveOrUpdate(auditHeader);
					}

				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
						auditHeader = getFinanceTypeService().doApprove(auditHeader);
						if (afinanceType.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}

					} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = getFinanceTypeService().doReject(auditHeader);
						if (afinanceType.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}
					} else {

						auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_CommodityFinanceTypeDialog, auditHeader);
						return processCompleted;
					}
				}

				auditHeader = ErrorControl.showErrorDetails(this.window_CommodityFinanceTypeDialog, auditHeader);
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
			logger.error(e);
			e.printStackTrace();
		}
		logger.debug("Leaving");
		return processCompleted;
	}

	/** 
	 * To get the currency LOV List From RMTCurrencies Table And Amount is
	 * formatted based on the currency 
	 *
	 **/
	public void onChange$lovDescFinCcyName(Event event) {
		logger.debug("Entering" + event.toString());

		this.lovDescFinCcyName.clearErrorMessage();

		Currency details = (Currency)PennantAppUtil.getCurrencyBycode(this.lovDescFinCcyName.getValue());

		if(details == null) {	
			this.finCcy.setValue("");
			throw new WrongValueException(this.lovDescFinCcyName, Labels.getLabel("FIELD_NO_INVALID", new String[] { Labels.getLabel("label_CommodityFinanceTypeDialog_FinCcy") }));
		} else {
			if (details != null) {
				this.finCcy.setValue(details.getCcyCode());
				this.lovDescFinCcyName.setValue(details.getCcyCode() + "-" + details.getCcyDesc());
				fillProfitDaysBasis(this.cbfinDaysCalType, details.getCcyDrRateBasisCode());
				// To Format Amount based on the currency

				getFinanceType().setLovDescFinFormetter(details.getCcyEditField());
				this.finMaxAmount.setFormat(PennantApplicationUtil.getAmountFormate(getFinanceType().getLovDescFinFormetter()));
				this.finMinAmount.setFormat(PennantApplicationUtil.getAmountFormate(getFinanceType().getLovDescFinFormetter()));

			}
		}

		logger.debug("Leaving" + event.toString());
	}
	
	
	/** 
	 * To get the currency LOV List From RMTCurrencies Table And Amount is
	 * formatted based on the currency 
	 *
	 **/
	public void onClick$btnSearchFinCcy(Event event) {
		logger.debug("Entering" + event.toString());
		Object dataObject = ExtendedSearchListBox.show(this.window_CommodityFinanceTypeDialog, "Currency");
		if (dataObject instanceof String) {
			this.finCcy.setValue(dataObject.toString());
			this.lovDescFinCcyName.setValue("");
		} else {
			Currency details = (Currency) dataObject;
			if (details != null) {
				this.finCcy.setValue(details.getCcyCode());
				this.lovDescFinCcyName.setValue(details.getCcyCode() + "-" + details.getCcyDesc());
				fillProfitDaysBasis(this.cbfinDaysCalType, details.getCcyDrRateBasisCode());
				// To Format Amount based on the currency

				getFinanceType().setLovDescFinFormetter(details.getCcyEditField());
				this.finMaxAmount.setFormat(PennantApplicationUtil.getAmountFormate(getFinanceType().getLovDescFinFormetter()));
				this.finMinAmount.setFormat(PennantApplicationUtil.getAmountFormate(getFinanceType().getLovDescFinFormetter()));

			}
		}
		logger.debug("Leaving" + event.toString());
	}

	/** 
	 * To get the AccountType LOV List From RMTAccountTypes Table filter is
	 * applied to get non internal account and it's purpose is movement
	 * 
	 **/
	public void onClick$btnSearchFinAcType(Event event) {
		logger.debug("Entering" + event.toString());
		Filter[] filters = new Filter[2];
		filters[0] = new Filter("AcPurpose", "F", Filter.OP_EQUAL);
		filters[1] = new Filter("InternalAc", "0", Filter.OP_EQUAL);

		Object dataObject = ExtendedSearchListBox.show(this.window_CommodityFinanceTypeDialog, "AccountType", filters);
		if (dataObject instanceof String) {
			this.finAcType.setValue(dataObject.toString());
			this.lovDescFinAcTypeName.setValue("");
		} else {
			AccountType details = (AccountType) dataObject;
			if (details != null) {
				this.finAcType.setValue(details.getAcType());
				this.lovDescFinAcTypeName.setValue(details.getAcType() + "-" + details.getAcTypeDesc());
			}
		}
		logger.debug("Leaving onClick$btnSearchFinAcType");
	}


	/** 
	 * To get the AccountType LOV List From RMTAccountTypes Table filter is
	 * applied to get only an internal account and it's purpose is movement and
	 * it is a Contingent account
	 * 
	 **/
	public void onClick$btnSearchFinContingentAcType(Event event) {
		logger.debug("Entering" + event.toString());
		Filter[] filters = new Filter[3];
		filters[0] = new Filter("AcPurpose", "M", Filter.OP_EQUAL);
		filters[1] = new Filter("InternalAc", "1", Filter.OP_EQUAL);
		filters[2] = new Filter("CustSysAc", "1", Filter.OP_EQUAL);

		Object dataObject = ExtendedSearchListBox.show(this.window_CommodityFinanceTypeDialog, "AccountType", filters);
		if (dataObject instanceof String) {
			this.finContingentAcType.setValue(dataObject.toString());
			this.lovDescFinContingentAcTypeName.setValue("");
		} else {
			AccountType details = (AccountType) dataObject;
			if (details != null) {
				this.finContingentAcType.setValue(details.getAcType());
				this.lovDescFinContingentAcTypeName.setValue(details.getAcType() + "-" + details.getAcTypeDesc());
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	/** 
	 * To get the AccountEngineRule LOV List From RMTAERules Table Records are
	 * filtered by AEEvent where AEEvent=AMZ 
	 * 
	 **/
	public void onClick$btnSearchFinAEAmzNorm(Event event) {
		logger.debug("Entering" + event.toString());

		Filter[] filter = new Filter[1];
		filter[0] = new Filter("AEEvent", "AMZ", Filter.OP_LIKE);
		Object dataObject = ExtendedSearchListBox.show(this.window_CommodityFinanceTypeDialog, "AccountEngineRule", filter);
		if (dataObject instanceof String) {
			AEnRule.setAEEvent("AMZ");
			AEnRule = this.accountEngineRuleDAO.getAccountEngineRuleBySysDflt(AEnRule, "",false);
			if (AEnRule.getaERuleId() != Long.MIN_VALUE) {
				this.finAEAmzNorm.setValue(AEnRule.getStringaERuleId());
				this.lovDescFinAEAmzNormName.setValue(AEnRule.getAERule() + "-" + AEnRule.getAERuleDesc());
			} else {
				this.finAEAmzNorm.setValue(null);
				this.lovDescFinAEAmzNormName.setValue("");
			}
		} else {
			AccountEngineRule details = (AccountEngineRule) dataObject;
			if (details != null) {
				this.finAEAmzNorm.setValue(String.valueOf(details.getaERuleId()));
				this.lovDescFinAEAmzNormName.setValue(details.getAERule() + "-" + details.getAERuleDesc());
			}
		}
		logger.debug("Leaving" + event.toString());
	}
	
	/** 
	 * To get the AccountEngineRule LOV List From RMTAERules Table Records are
	 * filtered by AEEvent where AEEvent=BUY 
	 * 
	 **/
	public void onClick$btnSearchFinAEBuyOrInception(Event event) {
		logger.debug("Entering" + event.toString());
		
		Filter[] filter = new Filter[1];
		filter[0] = new Filter("AEEvent", "BUY", Filter.OP_LIKE);
		Object dataObject = ExtendedSearchListBox.show(this.window_CommodityFinanceTypeDialog, "AccountEngineRule", filter);
		if (dataObject instanceof String) {
			AEnRule.setAEEvent("BUY");
			AEnRule = this.accountEngineRuleDAO.getAccountEngineRuleBySysDflt(AEnRule, "",false);
			if (AEnRule.getaERuleId() != Long.MIN_VALUE) {
				this.finAEBuyOrInception.setValue(AEnRule.getStringaERuleId());
				this.lovDescFinAEBuyOrInceptionName.setValue(AEnRule.getAERule() + "-" + AEnRule.getAERuleDesc());
			} else {
				this.finAEBuyOrInception.setValue(null);
				this.lovDescFinAEBuyOrInceptionName.setValue("");
			}
		} else {
			AccountEngineRule details = (AccountEngineRule) dataObject;
			if (details != null) {
				this.finAEBuyOrInception.setValue(String.valueOf(details.getaERuleId()));
				this.lovDescFinAEBuyOrInceptionName.setValue(details.getAERule() + "-" + details.getAERuleDesc());
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	/** 
	 * To get the AccountEngineRule LOV List From RMTAERules Table Records are
	 * filtered by AEEvent where AEEvent=BUY 
	 * 
	 **/
	public void onClick$btnSearchFinAESellOrMaturity(Event event) {
		logger.debug("Entering" + event.toString());
		
		Filter[] filter = new Filter[1];
		filter[0] = new Filter("AEEvent", "SELL", Filter.OP_LIKE);
		Object dataObject = ExtendedSearchListBox.show(this.window_CommodityFinanceTypeDialog, "AccountEngineRule", filter);
		if (dataObject instanceof String) {
			AEnRule.setAEEvent("SELL");
			AEnRule = this.accountEngineRuleDAO.getAccountEngineRuleBySysDflt(AEnRule, "",false);
			if (AEnRule.getaERuleId() != Long.MIN_VALUE) {
				this.finAESellOrMaturity.setValue(AEnRule.getStringaERuleId());
				this.lovDescFinAESellOrMaturityName.setValue(AEnRule.getAERule() + "-" + AEnRule.getAERuleDesc());
			} else {
				this.finAESellOrMaturity.setValue(null);
				this.lovDescFinAESellOrMaturityName.setValue("");
			}
		} else {
			AccountEngineRule details = (AccountEngineRule) dataObject;
			if (details != null) {
				this.finAESellOrMaturity.setValue(String.valueOf(details.getaERuleId()));
				this.lovDescFinAESellOrMaturityName.setValue(details.getAERule() + "-" + details.getAERuleDesc());
			}
		}
		logger.debug("Leaving" + event.toString());
	}
	
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++ WorkFlow Components +++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	/** Get Audit Header Details
	 * 
	 * @param aFinanceType
	 *           (FinanceType)
	 * @param tranType
	 *           (String)
	 * @return auditHeader */
	private AuditHeader getAuditHeader(FinanceType aFinanceType, String tranType) {

		AuditDetail auditDetail = new AuditDetail(tranType, 1, aFinanceType.getBefImage(), aFinanceType);
		return new AuditHeader(String.valueOf(aFinanceType.getId()), null, null, null, auditDetail,
				aFinanceType.getUserDetails(), getOverideMap());
	}

	// To Show Error messages
	private void showMessage(Exception e) {
		logger.debug("Entering");
		AuditHeader auditHeader = new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_UNDEF, e.getMessage(), null));
			ErrorControl.showErrorControl(this.window_CommodityFinanceTypeDialog, auditHeader);
		} catch (Exception exp) {
			logger.error(exp);
		}
		logger.debug("Leaving");
	}

	/** To get Note Dialog on clicking the button note */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void onClick$btnNotes(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		final HashMap map = new HashMap();
		map.put("notes", getNotes());
		map.put("control", this);
		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/notes/notes.zul", null, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving" + event.toString());
	}
	
	// Check notes Entered or not
	public void setNotes_entered(String notes) {
		if (!isNotes_Entered()) {
			if (org.apache.commons.lang.StringUtils.trimToEmpty(notes).equalsIgnoreCase("Y")) {
				setNotes_Entered(true);
			} else {
				setNotes_Entered(false);
			}
		}
	}

	// Get the notes entered for rejected reason
	private Notes getNotes() {
		logger.debug("Entering getNotes");
		Notes notes = new Notes();
		notes.setModuleName("FinanceType");
		notes.setReference(getFinanceType().getFinType());
		notes.setVersion(getFinanceType().getVersion());
		logger.debug("Leaving getNotes");
		return notes;
	}

	/** To Check the user action based on the result removes the error messages; */
	public void onCheck$userAction(Event event) {
		if ("Save".equals(userAction.getSelectedItem().getLabel())) {
			doClearErrMessages();
		}
	}

	/** TO clear all error messages */
	private void doClearErrMessages() {
		logger.debug("Entering");
		
		// Basic Tab
		this.finType.clearErrorMessage();
		this.finTypeDesc.clearErrorMessage();
		this.lovDescFinCcyName.clearErrorMessage();
		this.cbfinDaysCalType.clearErrorMessage();
		this.cbfinDepositRestrictedTo.clearErrorMessage();
		this.lovDescFinAcTypeName.clearErrorMessage();
		this.lovDescFinContingentAcTypeName.clearErrorMessage();
		this.finMaxAmount.clearErrorMessage();
		this.finMinAmount.clearErrorMessage();
		this.finHistRetension.clearErrorMessage();
		
		// Accounting Tab
		this.lovDescFinAEAmzNormName.clearErrorMessage();
		this.lovDescFinAEBuyOrInceptionName.clearErrorMessage();
		this.lovDescFinAESellOrMaturityName.clearErrorMessage();
		logger.debug("Leaving");
	}

	/** To get the ComboBox selected value */
	private String validateCombobox(Combobox combobox) {
		String comboValue = "";
		if (combobox.getSelectedItem() != null) {
			comboValue = combobox.getSelectedItem().getValue().toString();
		} else {
			combobox.setSelectedIndex(0);
		}
		return comboValue;
	}

	/** To check the higher of the give two decimal boxes
	 * 
	 * @param Decimalbox
	 *           ,DecimalBox,String,String
	 * @throws WrongValueException */
	private void mustBeHigher(Decimalbox maxvalue, Decimalbox minvalue, String maxlabel, String minlabel) {
		if ((maxvalue.getValue() != null) && (minvalue.getValue() != null)
				&& (maxvalue.getValue().compareTo(BigDecimal.ZERO) != 0)) {
			if (maxvalue.getValue().compareTo(minvalue.getValue()) != 1) {
				throw new WrongValueException(maxvalue, Labels.getLabel("FIELD_IS_GREATER",
						new String[] { Labels.getLabel(maxlabel), Labels.getLabel(minlabel) }));
			}
		}
	}

	/**
	 * Method to fill the profit days basis comboBox From BMTIntRateBasisCodes Table
	 * 
	 **/
	private void fillProfitDaysBasis(Combobox codeCombobox, String value) {
		logger.debug("Entering fillProfitDaysBasis");
		List<ValueLabel> pftDays = PennantAppUtil.getProfitDaysBasis();
		Comboitem comboitem = new Comboitem();
		comboitem.setValue("#");
		comboitem.setLabel(Labels.getLabel("Combo.Select"));
		codeCombobox.appendChild(comboitem);
		codeCombobox.setSelectedItem(comboitem);
		for (int i = 0; i < pftDays.size(); i++) {
			comboitem = new Comboitem();
			comboitem.setValue(pftDays.get(i).getValue());
			comboitem.setLabel(pftDays.get(i).getLabel());
			codeCombobox.appendChild(comboitem);
			if (StringUtils.trimToEmpty(value).equals(pftDays.get(i).getValue())) {
				codeCombobox.setSelectedItem(comboitem);
			}
		}
		logger.debug("Leaving fillProfitDaysBasis");
	}

	/**
	 * Method to fill the profit days basis comboBox From BMTIntRateBasisCodes Table
	 * 
	 **/
	private void fillDepositRestrictedTo(Combobox codeCombobox, String value) {
		logger.debug("Entering fillDepositRestrictedTo");
		List<ValueLabel> depositRestriction = PennantAppUtil.getDepositRestrictedTo();
		Comboitem comboitem = new Comboitem();
		comboitem.setValue("#");
		comboitem.setLabel(Labels.getLabel("Combo.Select"));
		codeCombobox.appendChild(comboitem);
		codeCombobox.setSelectedItem(comboitem);
		for (int i = 0; i < depositRestriction.size(); i++) {
			comboitem = new Comboitem();
			comboitem.setValue(depositRestriction.get(i).getValue());
			comboitem.setLabel(depositRestriction.get(i).getLabel());
			codeCombobox.appendChild(comboitem);
			if (StringUtils.trimToEmpty(value).equals(depositRestriction.get(i).getValue())) {
				codeCombobox.setSelectedItem(comboitem);
			}
		}
		logger.debug("Leaving fillDepositRestrictedTo");
	}

	/**
	 * Event to load the fee details in fee select dialog. 
	 * 
	 * @throws InterruptedException,SuspendNotAllowedException
	 **/
	public void onClick$btnNew_FinanceMarginSlabs() throws Exception{
		logger.debug("Entering");
		FinanceMarginSlab financeMarginSlab =new FinanceMarginSlab();
		financeMarginSlab.setNewRecord(true);
		financeMarginSlab.setWorkflowId(0);
		
		final HashMap<String, Object> map = new HashMap<String, Object>();
		selectNewFinMarginSlabBtn();
		financeMarginSlab.setFinType(this.finType.getValue());
		map.put("financeMarginSlab", financeMarginSlab);
		map.put("commodityFinanceTypeDialogCtrl", this);
		map.put("newRecord", "true");
		map.put("roleCode", getRole());
		try{
		Executions.createComponents("/WEB-INF/pages/SolutionFactory/CommodityFinanceType/FinanceMarginSlabDialog.zul",
				window_CommodityFinanceTypeDialog, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / "
					+ e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		
		logger.debug("Leaving");
	}
	
	/**
	 * get called when ever value is entered into finType. <br>
	 * 
	 * @param event
	 */
	public void onBlur$finType(Event event){
		logger.debug("Entering" + event.toString());
		selectNewFinMarginSlabBtn();
		logger.debug("Leaving" + event.toString());
	}
	
	/**
	 * Allow Adding Margin Slab List by button as Visible/not
	 */
	private void selectNewFinMarginSlabBtn(){
		logger.debug("Entering");

		if(!(this.finType.getValue() ==null || this.finType.getValue().equals(""))){
			this.finType.setReadonly(true);
		}
		logger.debug("Leaving");
	}
	
	public void onFinanceMarginSlabItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering");
		// get the selected invoiceHeader object
		final Listitem item = this.listboxFinanceMarginsSlabs.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final FinanceMarginSlab financeMarginSlab = (FinanceMarginSlab) item.getAttribute("data");

			if (financeMarginSlab.getRecordStatus().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
				PTMessageUtils.showErrorMessage("Not Allowed to maintain This Record");
			} else {
				final HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("financeMarginSlab", financeMarginSlab);
				map.put("commodityFinanceTypeDialogCtrl", this);
				map.put("roleCode", getRole());

				// call the ZUL-file with the parameters packed in a map
				try {
					Executions.createComponents("/WEB-INF/pages/SolutionFactory/CommodityFinanceType/FinanceMarginSlabDialog.zul",
							window_CommodityFinanceTypeDialog, map);
				} catch (final Exception e) {
					logger.error("onOpenWindow:: error opening window / "+ e.getMessage());
					PTMessageUtils.showErrorMessage(e.toString());
				}
			}
		}
		logger.debug("Leaving");
	}
	
	/**
	 * Event to load the fee details in fee select dialog. 
	 * 
	 * @throws InterruptedException,SuspendNotAllowedException
	 **/
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void onClick$btnNew_FeeAndCharges() throws SuspendNotAllowedException, InterruptedException{
		logger.debug("Entering");
		final HashMap map = new HashMap();
		map.put("commodityFinanceTypeDialogCtrl", this);
		map.put("newRecord", true);
		Executions.createComponents("/WEB-INF/pages/SolutionFactory/FinanceType/FeeListSelect.zul",null, map);
		logger.debug("Leaving");
	}
	
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// +++++++++ Finance Related Lists Refreshing ++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	/**
	 * Generate the Margin Slab Details List in the CustomerDialogCtrl and
	 * set the list in the listBoxCustomerAddress listBox by using Pagination
	 */
	public void doFillMarginSlabs(List<FinanceMarginSlab> financeMarginSlab) {
		logger.debug("Entering");
		setCommodityFinanceMarginSlabsList(financeMarginSlab);
		this.pagingFinanceMarginSlabs.setPageSize(getCountRows());
		this.pagingFinanceMarginSlabs.setDetailed(true);
		getFinanceMarginSlabsPagedListWrapper().initList(commodityFinanceMarginSlabsList,
				this.listboxFinanceMarginsSlabs, this.pagingFinanceMarginSlabs);
		this.listboxFinanceMarginsSlabs.setItemRenderer(new FinanceMarginSlabListModelItemRenderer());
		logger.debug("Leaving");
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

	public FinanceType getFinanceType() {
		return this.financeType;
	}
	public void setFinanceType(FinanceType financeType) {
		this.financeType = financeType;
	}

	public void setFinanceTypeService(FinanceTypeService financeTypeService) {
		this.financeTypeService = financeTypeService;
	}
	public FinanceTypeService getFinanceTypeService() {
		return this.financeTypeService;
	}

	public PagedListService getPagedListService() {
		return pagedListService;
	}
	public void setPagedListService(PagedListService pagedListService) {
		this.pagedListService = pagedListService;
	}
	
	public void setCommodityFinanceTypeListCtrl(CommodityFinanceTypeListCtrl commodityFinanceTypeListCtrl) {
		this.commodityFinanceTypeListCtrl = commodityFinanceTypeListCtrl;
	}
	public CommodityFinanceTypeListCtrl getCommodityFinanceTypeListCtrl() {
		return commodityFinanceTypeListCtrl;
	}
	
	public int getCountRows() {
		return countRows;
	}
	public void setCountRows(int countRows) {
		this.countRows = countRows;
	}


	
	public PagedListWrapper<FinanceMarginSlab> getFinanceMarginSlabsPagedListWrapper() {
		return financeMarginSlabsPagedListWrapper;
	}
	@SuppressWarnings("unchecked")
	public void setFinanceMarginSlabsPagedListWrapper() {
		if (this.financeMarginSlabsPagedListWrapper == null) {
			this.financeMarginSlabsPagedListWrapper = (PagedListWrapper<FinanceMarginSlab>) SpringUtil.getBean("pagedListWrapper");
		}
	}
	
	public boolean isNotes_Entered() {
		return notes_Entered;
	}
	public void setNotes_Entered(boolean notesEntered) {
		this.notes_Entered = notesEntered;
	}



	public String getFinType() {
		return this.finType.getValue();
	}

	public void setFinType(Uppercasebox finType) {
		this.finType = finType;
	}

	public String getFinTypeDesc() {
		return this.finTypeDesc.getValue();
	}

	public void setFinTypeDesc(Textbox finTypeDesc) {
		this.finTypeDesc = finTypeDesc;
	}

	public List<FinanceMarginSlab> getCommodityFinanceMarginSlabsList() {
		return commodityFinanceMarginSlabsList;
	}

	public void setCommodityFinanceMarginSlabsList(
			List<FinanceMarginSlab> commodityFinanceMarginSlabsList) {
		this.commodityFinanceMarginSlabsList = commodityFinanceMarginSlabsList;
	}

	public void setFinanceMarginSlabService(FinanceMarginSlabService financeMarginSlabService) {
		this.financeMarginSlabService = financeMarginSlabService;
	}

	public FinanceMarginSlabService getFinanceMarginSlabService() {
		return financeMarginSlabService;
	}

}
