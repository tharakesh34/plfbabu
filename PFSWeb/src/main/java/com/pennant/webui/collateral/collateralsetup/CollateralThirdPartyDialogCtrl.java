package com.pennant.webui.collateral.collateralsetup;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Button;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.constants.LengthConstants;
import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.collateral.CollateralThirdParty;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.service.collateral.CollateralSetupService;
import com.pennant.backend.service.customermasters.CustomerDetailsService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.search.Filter;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennant.webui.util.ScreenCTL;

public class CollateralThirdPartyDialogCtrl extends GFCBaseCtrl<CollateralThirdParty> implements Serializable {
	private static final long				serialVersionUID		= 1L;
	private static final Logger				logger					= Logger.getLogger(CollateralThirdPartyDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window						window_CollateralThirdPartyDialog;

	protected Groupbox						gb_statusDetails;
	protected Textbox						collateralReference;
	protected Textbox						customerCif;
	protected Button						btnSearchSelection;
	protected Button						viewInfo;
	protected Longbox						customerId;

	private boolean							enqModule = false;
	private CollateralSetupDialogCtrl		collateralSetupDialogCtrl;
	private CollateralThirdParty			collateralThirdParty;
	private int								index;
	private String[]						cifFilters;
	private boolean							newRecord;
	protected JdbcSearchObject<Customer>	custCIFSearchObject;
	private List<CollateralThirdParty>		collateralThirdPartyList;
	private String							primaryCustCif;
	private boolean							newThirdParty	= false;
	
	private CustomerDetailsService 			customerDetailsService;
	private CollateralSetupService			collateralSetupService;

	public CollateralThirdPartyDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "CollateralThirdPartyDialog";
	}

	public void onCreate$window_CollateralThirdPartyDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_CollateralThirdPartyDialog);

		try {
			// READ OVERHANDED params !
			if (arguments.containsKey("collateralThirdParty")) {
				this.collateralThirdParty = (CollateralThirdParty) arguments.get("collateralThirdParty");
				CollateralThirdParty befImage = new CollateralThirdParty();
				BeanUtils.copyProperties(this.collateralThirdParty, befImage);
				this.collateralThirdParty.setBefImage(befImage);
				setCollateralThirdParty(this.collateralThirdParty);
			} else {
				setCollateralThirdParty(null);
			}
			if (arguments.containsKey("enqModule")) {
				enqModule = (Boolean) arguments.get("enqModule");
			} 

			if (arguments.containsKey("primaryCustCif")) {
				primaryCustCif = (String) arguments.get("primaryCustCif");
			}

			if (arguments.containsKey("index")) {
				this.index = (Integer) arguments.get("index");
			}

			if (arguments.containsKey("filter")) {
				this.cifFilters = (String[]) arguments.get("filter");
			}

			//collateralSetupCtrl
			if (arguments.containsKey("collateralSetupCtrl")) {
				setCollateralSetupDialogCtrl((CollateralSetupDialogCtrl) arguments.get("collateralSetupCtrl"));
				setNewThirdParty(true);
				if (arguments.containsKey("newRecord")) {
					setNewRecord(true);
				} else {
					setNewRecord(false);
				}
				this.collateralThirdParty.setWorkflowId(0);
				if (arguments.containsKey("roleCode")) {
					setRole((String) arguments.get("roleCode"));
					getUserWorkspace().allocateRoleAuthorities(getRole(), "CollateralThirdPartyDialog");
				}
			}
			doLoadWorkFlow(this.collateralThirdParty.isWorkflow(), this.collateralThirdParty.getWorkflowId(),
					this.collateralThirdParty.getNextTaskId());

			doCheckRights();

			doSetFieldProperties();
			doShowDialog(getCollateralThirdParty());
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
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
	 * when the "cancel" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnCancel(Event event) {
		logger.debug("Entering" + event.toString());
		doWriteBeanToComponents(this.collateralThirdParty.getBefImage());
		doEdit();
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
		MessageUtil.showHelpWindow(event, window_CollateralThirdPartyDialog);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event
	 *            An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		doClose(this.btnSave.isVisible());
	}

	/**
	 * Get the window for entering Notes
	 * 
	 * @param event
	 *            (Event)
	 * 
	 * @throws Exception
	 */
	public void onClick$btnNotes(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		try {
			ScreenCTL.displayNotes(
					getNotes("CollateralThirdParty", String.valueOf(getCollateralThirdParty().getCustomerId()),
							getCollateralThirdParty().getVersion()), this);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * When user clicks on button "Search Selection based on posting Against" button
	 * 
	 * @param event
	 */
	public void onClick$btnSearchSelection(Event event) throws SuspendNotAllowedException, InterruptedException {
		logger.debug("Entering " + event.toString());

		this.customerCif.setErrorMessage("");
		doSearchSelection();

		logger.debug("Leaving " + event.toString());
	}

	private void doSearchSelection() throws SuspendNotAllowedException, InterruptedException {
		logger.debug("Entering");
		List<Filter> filterList = new ArrayList<>();
		filterList.add(new Filter("CustCoreBank", "", Filter.OP_NOT_EQUAL));
		for (int i = 0; i < cifFilters.length; i++) {
			filterList.add(new Filter("CustCIF", cifFilters[i], Filter.OP_NOT_EQUAL));
		}
		
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("DialogCtrl", this);
		map.put("filtersList", filterList);
		map.put("searchObject", this.custCIFSearchObject);
		Executions.createComponents("/WEB-INF/pages/CustomerMasters/Customer/CustomerSelect.zul", null, map);
		
		logger.debug("Leaving");
	}

	public void doSetCustomer(Object nCustomer, JdbcSearchObject<Customer> newSearchObject) throws InterruptedException {
		logger.debug("Entering");
		
		this.customerCif.clearErrorMessage();
		this.custCIFSearchObject = newSearchObject;

		Customer customer = (Customer) nCustomer;
		if (customer != null) {
			getCollateralThirdParty().setCustCIF(customer.getCustCIF());
			getCollateralThirdParty().setCustShrtName(customer.getCustShrtName());
			getCollateralThirdParty().setCustCRCPR(customer.getCustCRCPR());
			getCollateralThirdParty().setCustPassportNo(customer.getCustPassportNo());
			getCollateralThirdParty().setCustCtgCode(customer.getCustCtgCode());
			getCollateralThirdParty().setCustNationality(customer.getCustNationality());
			this.customerCif.setValue(customer.getCustCIF());
			this.customerId.setValue(customer.getCustID());
		} else {
			this.customerCif.setValue("");
		}
		logger.debug("Leaving");
	}

	public void onClick$viewInfo(Event event) {
		logger.debug("Entering");
		
		if ((!this.btnSearchSelection.isDisabled()) && StringUtils.isEmpty(this.customerCif.getValue())) {
			throw new WrongValueException(this.customerCif, Labels.getLabel("FIELD_NO_INVALID",
					new String[] { Labels.getLabel("listheader_ThirdPartyAssignmentDetails_CustomerCIF.label") }));
		}
		
		// Customer Cross check against DB
		if(StringUtils.isNotEmpty(this.customerCif.getValue())){
			Customer customer = getCustomerDetailsService().getCheckCustomerByCIF(this.customerCif.getValue());
			if(customer == null){
				MessageUtil.showError(Labels.getLabel("Cust_NotFound"));
				return;
			}
		}

		try {
			HashMap<String, Object> map = new HashMap<String, Object>();
			if (!this.customerCif.getValue().equals("")) {
				map.put("custid", this.customerId.getValue());
				Executions.createComponents("/WEB-INF/pages/CustomerMasters/Customer/FinCustomerDetailsEnq.zul",
						window_CollateralThirdPartyDialog, map);
			}
		} catch (Exception e) {
			logger.error("Exception :", e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param collateralThirdParty
	 * @throws InterruptedException
	 */
	public void doShowDialog(CollateralThirdParty collateralThirdParty) throws InterruptedException {
		logger.debug("Entering");

		if (isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
			this.collateralReference.focus();
		} else {
			if (isNewThirdParty()) {
				doEdit();
			} else if (isWorkFlowEnabled()) {
				this.btnNotes.setVisible(true);
				doEdit();
			} else {
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
			}
		}
		try {
			doWriteBeanToComponents(collateralThirdParty);
			if (isNewThirdParty()) {
				this.groupboxWf.setVisible(false);
			}
			this.window_CollateralThirdPartyDialog.doModal();
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");

		if (isNewRecord()) {
			this.btnCancel.setVisible(false);
			this.customerCif.setReadonly(false);
			this.btnSearchSelection.setDisabled(false);
		} else {
			this.customerCif.setReadonly(true);
			this.btnCancel.setVisible(true);
			this.btnSearchSelection.setDisabled(true);
		}
		this.collateralReference.setReadonly(true);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.collateralThirdParty.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			if (isNewThirdParty()) {
				if (isNewRecord()) {
					this.btnCtrl.setBtnStatus_Edit();
					btnCancel.setVisible(false);
				} else {
					this.btnCtrl.setWFBtnStatus_Edit(isNewThirdParty());
				}
			} else {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(true);
			}
		}
		
	     this.btnSave.setVisible(isNewRecord());
		
		logger.debug("Leaving");
	}

	public boolean isReadOnly(String componentName) {
		if (isWorkFlowEnabled() || isNewThirdParty()) {
			return getUserWorkspace().isReadOnly(componentName);
		}
		return false;
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");

		this.collateralReference.setReadonly(true);
		this.customerCif.setReadonly(true);

		logger.debug("Leaving");
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
		if (!enqModule) {
			getUserWorkspace().allocateAuthorities("CollateralThirdPartyDialog", getRole());
			this.btnNew.setVisible(getUserWorkspace().isAllowed("button_CollateralThirdPartyDialog_btnNew"));
			this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_CollateralThirdPartyDialog_btnEdit"));
			this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_CollateralThirdPartyDialog_btnDelete"));
			this.btnSave.setVisible(getUserWorkspace().isAllowed("button_CollateralThirdPartyDialog_btnSave"));
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		this.customerCif.setMaxlength(LengthConstants.LEN_CIF);
		this.collateralReference.setMaxlength(20);
		setStatusDetails(gb_statusDetails, groupboxWf, south, enqModule);
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aGuarantorDetail
	 *            GuarantorDetail
	 */
	public void doWriteBeanToComponents(CollateralThirdParty collateralThirdParty) {
		logger.debug("Entering");

		this.customerCif.setValue(collateralThirdParty.getCustCIF());
		this.customerId.setValue(collateralThirdParty.getCustomerId());
		this.collateralReference.setValue(collateralThirdParty.getCollateralRef());

		logger.debug("Leaving");
	}

	/**
	 * Display Message in Error Box
	 * 
	 * @param e
	 *            (Exception)
	 */
	private void showMessage(Exception e) {
		logger.debug("Entering");
		AuditHeader auditHeader = new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_UNDEF, e.getMessage(), null));
			ErrorControl.showErrorControl(this.window_CollateralThirdPartyDialog, auditHeader);
		} catch (Exception exp) {
			logger.error("Exception: ", exp);
		}
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param thirdPartyDetails
	 */
	public void doWriteComponentsToBean(CollateralThirdParty thirdPartyDetails) {
		logger.debug("Entering");
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			thirdPartyDetails.setCustomerId(this.customerId.longValue());
			thirdPartyDetails.setCustCIF(this.customerCif.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			thirdPartyDetails.setCollateralRef(this.collateralReference.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		doRemoveValidation();
		if (!wve.isEmpty()) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}
		setCollateralThirdParty(thirdPartyDetails);
		logger.debug("Leaving");
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");
		
		doClearMessage();
		this.customerCif.setConstraint(new PTStringValidator(Labels.getLabel("label_CollateralThirdPartyDialog_CustomerCif.value"), PennantRegularExpressions.REGEX_ALPHANUM, true));

		logger.debug("Leaving");
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");

		this.collateralReference.setConstraint("");
		this.customerCif.setConstraint("");

		logger.debug("Leaving");
	}

	/**
	 * Remove Error Messages for Fields
	 */
	@Override
	protected void doClearMessage() {
		logger.debug("Entering");

		this.collateralReference.setErrorMessage("");
		this.customerCif.setErrorMessage("");

		logger.debug("Leaving");
	}

	/**
	 * Deletes a GuarantorDetail object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");
		final CollateralThirdParty collateralThirdParty = new CollateralThirdParty();
		BeanUtils.copyProperties(getCollateralThirdParty(), collateralThirdParty);
		String tranType = PennantConstants.TRAN_WF;
		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> "
				+ (collateralThirdParty.getCustCIF());

		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			
			if (StringUtils.isBlank(collateralThirdParty.getRecordType())) {
				
				if (collateralThirdParty.getCustomerId() > 0) {
					boolean exist = this.collateralSetupService.isThirdPartyUsed(collateralThirdParty.getCollateralRef(),
							collateralThirdParty.getCustomerId());

					if (exist) {
						MessageUtil.showError(ErrorUtil.getErrorDetail(new ErrorDetail("90338", null)));
						return;
					}
				}
				
				collateralThirdParty.setVersion(collateralThirdParty.getVersion() + 1);
				collateralThirdParty.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				collateralThirdParty.setNewRecord(true);
				if (isWorkFlowEnabled()) {
					collateralThirdParty.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}
			try {
				if (isNewThirdParty()) {
					tranType = PennantConstants.TRAN_DEL;
					AuditHeader auditHeader = newThirdPartyDetailProcess(collateralThirdParty, tranType);
					auditHeader = ErrorControl.showErrorDetails(this.window_CollateralThirdPartyDialog, auditHeader);
					int retValue = auditHeader.getProcessStatus();
					if (retValue == PennantConstants.porcessCONTINUE || retValue == PennantConstants.porcessOVERIDE) {
						if (getCollateralSetupDialogCtrl() != null) {
							getCollateralSetupDialogCtrl().doFillCollateralThirdPartyDetails(
									this.collateralThirdPartyList);
						}
						closeDialog();
					}
				}
			} catch (DataAccessException e) {
				logger.error("Exception: ", e);
				showMessage(e);
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Clears the components values. <br>
	 */
	public void doClear() {
		logger.debug("Entering");

		this.customerCif.setValue("");
		this.collateralReference.setValue("");

		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");

		final CollateralThirdParty acollateralThirdParty = new CollateralThirdParty();
		BeanUtils.copyProperties(getCollateralThirdParty(), acollateralThirdParty);
		boolean isNew = false;

		// force validation, if on, than execute by component.getValue()
		doSetValidation();

		// fill the DocumentDetails object with the components data
		doWriteComponentsToBean(acollateralThirdParty);
		// Write the additional validations as per below example
		// get the selected branch object from the listBox
		// Do data level validations here
		
		// Customer Cross check against DB
		Customer customer = null;
		if(StringUtils.isNotEmpty(this.customerCif.getValue())){
			 customer = getCustomerDetailsService().getCheckCustomerByCIF(this.customerCif.getValue());
				if (customer == null) {
				MessageUtil.showError(Labels.getLabel("Cust_NotFound"));
					return;
				}
		}
		
		if (this.customerId.longValue() == 0) {
			getCollateralThirdParty().setCustomerId(customer.getCustID());
			getCollateralThirdParty().setCustCIF(customer.getCustCIF());
			getCollateralThirdParty().setCustShrtName(customer.getCustShrtName());
			getCollateralThirdParty().setCustCRCPR(customer.getCustCRCPR());
			getCollateralThirdParty().setCustPassportNo(customer.getCustPassportNo());
			getCollateralThirdParty().setCustCtgCode(customer.getCustCtgCode());
			getCollateralThirdParty().setCustNationality(customer.getCustNationality());
			this.customerCif.setValue(customer.getCustCIF());
			this.customerId.setValue(customer.getCustID());
		} 

		isNew = acollateralThirdParty.isNew();
		String tranType = "";
		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(acollateralThirdParty.getRecordType())) {
				acollateralThirdParty.setVersion(acollateralThirdParty.getVersion() + 1);
				if (isNew) {
					acollateralThirdParty.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					acollateralThirdParty.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					acollateralThirdParty.setNewRecord(true);
				}
			}
		} else {
			if (isNewThirdParty()) {
				if (isNewRecord()) {
					acollateralThirdParty.setVersion(1);
					acollateralThirdParty.setRecordType(PennantConstants.RCD_ADD);
				} else {
					tranType = PennantConstants.TRAN_UPD;
				}
				if (StringUtils.isBlank(acollateralThirdParty.getRecordType())) {
					acollateralThirdParty.setVersion(acollateralThirdParty.getVersion() + 1);
					acollateralThirdParty.setRecordType(PennantConstants.RCD_UPD);
					acollateralThirdParty.setNewRecord(true);
				}
				if (acollateralThirdParty.getRecordType().equals(PennantConstants.RCD_ADD) && isNewRecord()) {
					tranType = PennantConstants.TRAN_ADD;
				} else if (acollateralThirdParty.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
					tranType = PennantConstants.TRAN_UPD;
				}
			} else {
				acollateralThirdParty.setVersion(acollateralThirdParty.getVersion() + 1);
				if (isNew) {
					tranType = PennantConstants.TRAN_ADD;
				} else {
					tranType = PennantConstants.TRAN_UPD;
				}
			}
		}
		// save it to database
		try {
			if (isNewThirdParty()) {
				AuditHeader auditHeader = newThirdPartyDetailProcess(acollateralThirdParty, tranType);
				auditHeader = ErrorControl.showErrorDetails(this.window_CollateralThirdPartyDialog, auditHeader);
				int retValue = auditHeader.getProcessStatus();
				if (retValue == PennantConstants.porcessCONTINUE || retValue == PennantConstants.porcessOVERIDE) {
					if (getCollateralSetupDialogCtrl() != null) {
						getCollateralSetupDialogCtrl().doFillCollateralThirdPartyDetails(this.collateralThirdPartyList);
					}
					closeDialog();
				}
			}
		} catch (final DataAccessException e) {
			logger.error("Exception: ", e);
			showMessage(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the workFlow Details List to Object
	 * 
	 * @param aAuthorizedSignatoryRepository
	 *            (AuthorizedSignatoryRepository)
	 * 
	 * @param tranType
	 *            (String)
	 * 
	 * @return boolean
	 * 
	 */
	private AuditHeader newThirdPartyDetailProcess(CollateralThirdParty acollateralThirdParty, String tranType) {

		boolean recordAdded = false;
		boolean duplicateRecord = false;

		AuditHeader auditHeader = getAuditHeader(acollateralThirdParty, tranType);
		this.collateralThirdPartyList = new ArrayList<CollateralThirdParty>();

		String[] valueParm = new String[2];
		String[] errParm = new String[2];
		valueParm[0] = acollateralThirdParty.getCustCIF();
		errParm[0] = PennantJavaUtil.getLabel("label_CoOwnerCIF") + ": " + valueParm[0];

		// Checks whether custCIF is same as actual custCIF
		if (StringUtils.trimToNull(primaryCustCif).equals(acollateralThirdParty.getCustCIF())) {
			auditHeader.setErrorDetails(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001",
					errParm, valueParm), getUserWorkspace().getUserLanguage()));
		}

		List<CollateralThirdParty> collaThirdPartyList = null;
		if (getCollateralSetupDialogCtrl() != null) {
			collaThirdPartyList = getCollateralSetupDialogCtrl().getCollateralThirdPartyList();
		}
		if (collaThirdPartyList != null && !collaThirdPartyList.isEmpty()) {
			for (CollateralThirdParty details : collaThirdPartyList) {
				if (acollateralThirdParty.getCustomerId() == details.getCustomerId()) {
					duplicateRecord = true;
				}
				if (duplicateRecord) {
					if (isNewRecord()) {
						auditHeader.setErrorDetails(ErrorUtil.getErrorDetail(new ErrorDetail(
								PennantConstants.KEY_FIELD, "41001", errParm, valueParm), getUserWorkspace()
								.getUserLanguage()));
						return auditHeader;
					}
					if (PennantConstants.TRAN_DEL.equals(tranType)) {
						if (PennantConstants.RECORD_TYPE_UPD.equals(details.getRecordType())) {
							details.setRecordType(PennantConstants.RECORD_TYPE_DEL);
							recordAdded = true;
							this.collateralThirdPartyList.add(details);
						} else if (PennantConstants.RCD_ADD.equals(details.getRecordType())) {
							recordAdded = true;
						} else if (PennantConstants.RECORD_TYPE_NEW.equals(details.getRecordType())) {
							details.setRecordType(PennantConstants.RECORD_TYPE_CAN);
							recordAdded = true;
							this.collateralThirdPartyList.add(details);
						} else if (PennantConstants.RECORD_TYPE_CAN.equals(details.getRecordType())) {
							recordAdded = true;
						}
					} else {
						this.collateralThirdPartyList.add(details);
					}
				} else {
					this.collateralThirdPartyList.add(details);
				}
				duplicateRecord = false;
			}
		}
		if (PennantConstants.TRAN_UPD.equals(tranType )) {
			this.collateralThirdPartyList.remove(index);
			this.collateralThirdPartyList.add(acollateralThirdParty);
			recordAdded = true;
		}
		if (!recordAdded) {
			this.collateralThirdPartyList.add(acollateralThirdParty);
		}
		return auditHeader;
	}

	// WorkFlow Components

	/**
	 * @param aAuthorizedSignatoryRepository
	 * @param tranType
	 * @return
	 */
	private AuditHeader getAuditHeader(CollateralThirdParty detail, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, detail.getBefImage(), detail);
		return new AuditHeader(String.valueOf(detail.getCustCIF()), null, null, null, auditDetail,
				detail.getUserDetails(), getOverideMap());
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public boolean isNewRecord() {
		return newRecord;
	}
	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}

	public CollateralSetupDialogCtrl getCollateralSetupDialogCtrl() {
		return collateralSetupDialogCtrl;
	}
	public void setCollateralSetupDialogCtrl(CollateralSetupDialogCtrl collateralSetupDialogCtrl) {
		this.collateralSetupDialogCtrl = collateralSetupDialogCtrl;
	}

	public CollateralThirdParty getCollateralThirdParty() {
		return collateralThirdParty;
	}
	public void setCollateralThirdParty(CollateralThirdParty collateralThirdParty) {
		this.collateralThirdParty = collateralThirdParty;
	}

	public List<CollateralThirdParty> getCollateralThirdPartyList() {
		return collateralThirdPartyList;
	}
	public void setCollateralThirdPartyList(List<CollateralThirdParty> collateralThirdPartyList) {
		this.collateralThirdPartyList = collateralThirdPartyList;
	}

	public boolean isNewThirdParty() {
		return newThirdParty;
	}
	public void setNewThirdParty(boolean newThirdParty) {
		this.newThirdParty = newThirdParty;
	}

	public CustomerDetailsService getCustomerDetailsService() {
		return customerDetailsService;
	}
	public void setCustomerDetailsService(CustomerDetailsService customerDetailsService) {
		this.customerDetailsService = customerDetailsService;
	}
	

	public CollateralSetupService getCollateralSetupService() {
		return collateralSetupService;
	}

	public void setCollateralSetupService(CollateralSetupService collateralSetupService) {
		this.collateralSetupService = collateralSetupService;
	}
}
