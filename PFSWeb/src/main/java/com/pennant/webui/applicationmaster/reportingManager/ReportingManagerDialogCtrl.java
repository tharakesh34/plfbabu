package com.pennant.webui.applicationmaster.reportingManager;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.model.administration.ReportingManager;
import com.pennant.backend.model.administration.SecurityUser;
import com.pennant.backend.model.administration.SecurityUserHierarchy;
import com.pennant.backend.model.applicationmaster.Branch;
import com.pennant.backend.model.applicationmaster.BusinessVertical;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.bmtmasters.Product;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.service.applicationmaster.ReportingManagerService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.administration.securityuser.SecurityUserDialogCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.jdbc.DataType;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.jdbc.search.Search;
import com.pennanttech.pennapps.jdbc.search.SearchProcessor;
import com.pennanttech.pennapps.web.util.MessageUtil;

public class ReportingManagerDialogCtrl extends GFCBaseCtrl<ReportingManager> {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(ReportingManagerDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_ReportingManagerDialog;
	protected Textbox usrid;
	protected ExtendedCombobox businessvertical;
	protected ExtendedCombobox productcode;
	protected ExtendedCombobox fintype;
	protected ExtendedCombobox branchcode;
	protected ExtendedCombobox reportingto;
	private boolean workflow = false;
	private boolean child = false;

	private ReportingManager reportingManager;
	private SecurityUser securityUser;
	private List<ReportingManager> reportingManagerlist;
	private SecurityUserDialogCtrl securityUserDialogCtrl;
	private boolean newRecord = false;

	private transient ReportingManagerService reportingManagerService;

	@Autowired
	SearchProcessor searchProcessor;

	/**
	 * default constructor.<br>
	 */
	public ReportingManagerDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "SecurityUserDialog_RM";
	}

	@Override
	protected String getReference() {
		return String.valueOf(this.reportingManager.getId());
	}

	/**
	 * 
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_ReportingManagerDialog(Event event) {
		logger.debug(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(window_ReportingManagerDialog);

		try {
			// Get the required arguments.

			if (arguments.containsKey("reportingManager")) {
				this.reportingManager = (ReportingManager) arguments.get("reportingManager");
				ReportingManager befImage = new ReportingManager();
				BeanUtils.copyProperties(this.reportingManager, befImage);
				this.reportingManager.setBefImage(befImage);
				setReportingManager(this.reportingManager);

			} else {
				setReportingManager(null);
			}

			if (arguments.containsKey("SecurityUserDialogCtrl")) {
				securityUserDialogCtrl = (SecurityUserDialogCtrl) arguments.get("SecurityUserDialogCtrl");
				securityUser = securityUserDialogCtrl.getSecurityUser();
				child = true;
				if (arguments.containsKey("newRecord")) {
					newRecord = true;
					this.reportingManager.setNewRecord(true);
				} else {
					newRecord = false;
				}

				this.reportingManager.setWorkflowId(0);
				if (arguments.containsKey("roleCode")) {
					this.role = arguments.get("roleCode").toString();
				}
			}

			if (securityUserDialogCtrl != null) {
				workflow = securityUserDialogCtrl.getSecurityUser().isWorkflow();
				this.securityUser = securityUserDialogCtrl.getSecurityUser();
			}

			// Render the page and display the data.
			doLoadWorkFlow(reportingManager.isWorkflow(), reportingManager.getWorkflowId(),
					reportingManager.getNextTaskId());

			if (isWorkFlowEnabled()) {
				this.userAction = setListRecordStatus(this.userAction);
			}

			getUserWorkspace().allocateRoleAuthorities(getRole(), pageRightName);

			doSetFieldProperties();
			doCheckRights();
			doShowDialog(getReportingManager());
		} catch (Exception e) {
			closeDialog();
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug(Literal.ENTERING);

		this.businessvertical.setModuleName("BusinessVertical");
		this.businessvertical.setValueColumn("Id");
		this.businessvertical.setValueType(DataType.LONG);
		this.businessvertical.setDescColumn("Description");
		this.businessvertical.setValidateColumns(new String[] { "Id", "Code" });

		this.productcode.setModuleName("Product");
		this.productcode.setValueColumn("ProductCode");
		this.productcode.setDescColumn("ProductDesc");
		this.productcode.setValidateColumns(new String[] { "ProductCode" });

		this.fintype.setModuleName("FinanceType");
		this.fintype.setValueColumn("FinType");
		this.fintype.setDescColumn("FinTypeDesc");
		this.fintype.setValidateColumns(new String[] { "FinType" });

		this.branchcode.setModuleName("Branch");
		this.branchcode.setValueColumn("BranchCode");
		this.branchcode.setDescColumn("BranchDesc");
		this.branchcode.setValidateColumns(new String[] { "BranchCode" });

		this.reportingto.setMandatoryStyle(true);
		this.reportingto.setModuleName("reportingTo");
		this.reportingto.setValueColumn("UserName");
		this.reportingto.setDescColumn("UserName");
		this.reportingto.setValidateColumns(new String[] { "UserName" });

		setStatusDetails();

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set Visible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug(Literal.ENTERING);

		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_SecurityUserDialog_RM_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_SecurityUserDialog_RM_btnSave"));
		this.btnCancel.setVisible(false);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the save button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 * @throws InterruptedException
	 */
	public void onClick$btnSave(Event event) throws InterruptedException {
		logger.debug(Literal.ENTERING);
		doSave();
		logger.debug(Literal.LEAVING);

	}

	/**
	 * The framework calls this event handler when user clicks the edit button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnEdit(Event event) {
		logger.debug(Literal.ENTERING);
		doEdit();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the help button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnHelp(Event event) {
		logger.debug(Literal.ENTERING);
		MessageUtil.showHelpWindow(event, super.window);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the delete button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnDelete(Event event) throws InterruptedException {
		logger.debug(Literal.ENTERING);
		doDelete();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the cancel button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnCancel(Event event) {
		logger.debug(Literal.ENTERING);
		doCancel();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		logger.debug(Literal.ENTERING);
		doClose(this.btnSave.isVisible());
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the notes button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnNotes(Event event) {
		logger.debug(Literal.ENTERING);
		doShowNotes(this.reportingManager);
		logger.debug(Literal.LEAVING);
	}

	public void onFulfill$reportingto(Event event) {
		logger.debug(Literal.ENTERING);
		Object dataObject = reportingto.getObject();
		if (dataObject instanceof String || dataObject == null) {
			this.reportingto.setValue("");
			this.reportingto.setDescription("");
		} else {
			SecurityUserHierarchy details = (SecurityUserHierarchy) dataObject;
			if (details != null) {
				this.reportingto.setAttribute("UserId", details.getUserId());
				this.reportingto.setValue(details.getUserName());
				this.reportingto.setDescription(details.getUserName());
			}
		}

		logger.debug(Literal.LEAVING);
	}

	public void onFulfill$businessvertical(Event event) {
		logger.debug(Literal.ENTERING);
		Object dataObject = businessvertical.getObject();
		if (dataObject instanceof String || dataObject == null) {
			this.businessvertical.setValue("");
			this.businessvertical.setDescription("");
		} else {
			BusinessVertical details = (BusinessVertical) dataObject;
			this.businessvertical.setValue(details.getCode());
			this.businessvertical.setDescription(details.getDescription());
		}

		filterBranches();
		logger.debug(Literal.LEAVING);
	}

	public void onFulfill$productcode(Event event) {
		logger.debug(Literal.ENTERING);
		Object dataObject = productcode.getObject();
		if (dataObject instanceof String || dataObject == null) {
			this.productcode.setValue("");
			this.productcode.setDescription("");
		} else {
			Product details = (Product) dataObject;
			this.productcode.setValue(details.getProductCode());
			this.productcode.setDescription(details.getProductDesc());
		}
		filterBranches();
		logger.debug(Literal.LEAVING);
	}

	public void onFulfill$fintype(Event event) {
		logger.debug(Literal.ENTERING);
		Object dataObject = fintype.getObject();
		if (dataObject instanceof String || dataObject == null) {
			this.fintype.setValue("");
			this.fintype.setDescription("");
		} else {
			FinanceType details = (FinanceType) dataObject;
			this.fintype.setValue(details.getFinType());
			this.fintype.setDescription(details.getFinTypeDesc());
		}
		filterBranches();
		logger.debug(Literal.LEAVING);
	}

	public void onFulfill$branchcode(Event event) {
		logger.debug(Literal.ENTERING);
		Object dataObject = branchcode.getObject();
		if (dataObject instanceof String || dataObject == null) {
			this.branchcode.setValue("");
			this.branchcode.setDescription("");
		} else {
			Branch details = (Branch) dataObject;
			this.branchcode.setValue(details.getBranchCode());
			this.branchcode.setDescription(details.getBranchDesc());
		}

		logger.debug(Literal.LEAVING);
	}

	public void filterBranches() {
		Search search = new Search();
		search.addTabelName("userhierarchy_view");
		search.addField("userId");

		int hierarchyUsers = 0;

		if (search != null) {
			hierarchyUsers = searchProcessor.getCount(search);
		}
		String businessVerticalId = null;
		String productCode = null;
		String loanType = null;
		String branch = null;

		int filterSize = 0;

		if (!StringUtils.isEmpty(businessvertical.getValue())) {
			BusinessVertical businessVertical = (BusinessVertical) this.businessvertical.getObject();
			businessVerticalId = businessVertical.getCode();
		}

		if (!StringUtils.isEmpty(productcode.getValue())) {
			Product product = (Product) this.productcode.getObject();
			productCode = product.getProductCode();
		}

		if (!StringUtils.isEmpty(fintype.getValue())) {
			FinanceType financeType = (FinanceType) this.fintype.getObject();
			loanType = financeType.getFinType();
		}

		if (!StringUtils.isEmpty(branchcode.getValue())) {
			Branch branch2 = (Branch) this.branchcode.getObject();
			branch = branch2.getBranchCode();
		}

		if (businessVerticalId != null) {
			++filterSize;
		}

		if (productCode != null) {
			++filterSize;
		}

		if (loanType != null) {
			++filterSize;
		}

		if (branch != null) {
			++filterSize;
		}

		Filter[] filters = new Filter[filterSize];
		int index = 0;
		if (businessVerticalId != null) {
			filters[index++] = new Filter("businessVerticalCode", businessVerticalId);
		}

		if (productCode != null) {
			filters[index++] = new Filter("Product", productCode);
		}

		if (loanType != null) {
			filters[index++] = new Filter("finType", loanType);
		}

		if (branch != null) {
			filters[index++] = new Filter("Branch", branch);
		}

		if (hierarchyUsers > 0) {
			this.reportingto.setFilters(filters);
		}
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	protected void refreshList() {
		logger.debug(Literal.ENTERING);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Cancel the actual operation and Resets to the original status
	 */
	private void doCancel() {
		logger.debug(Literal.ENTERING);

		doWriteBeanToComponents(this.reportingManager.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param reportingManagerd
	 * 
	 */
	public void doWriteBeanToComponents(ReportingManager aReportingManager) {
		logger.debug(Literal.ENTERING);

		this.usrid.setValue(String.valueOf(this.securityUser.getUsrLogin()));
		this.usrid.setReadonly(true);

		aReportingManager.setUserId(this.securityUser.getUsrID());

		if (aReportingManager.getBusinessVertical() != null) {
			this.businessvertical.setValue(aReportingManager.getBusinessVerticalCode());
			this.businessvertical.setDescription(aReportingManager.getBusinessVerticalDesc());
			BusinessVertical businessVertical = new BusinessVertical();
			businessVertical.setId(aReportingManager.getBusinessVertical());
			this.businessvertical.setObject(businessVertical);
		}

		this.reportingto.setAttribute("UserId", aReportingManager.getReportingTo());
		this.reportingto.setValue(aReportingManager.getReportingToUserName());
		this.reportingto.setDescription(aReportingManager.getReportingToUserName());

		if (aReportingManager.getProduct() != null) {
			this.productcode.setValue(aReportingManager.getProduct());
			this.productcode.setDescription(aReportingManager.getProductDesc());
			Product product = new Product();
			product.setProductCode(aReportingManager.getProduct());
			this.productcode.setObject(product);
		}

		if (aReportingManager.getFinType() != null) {
			this.fintype.setValue(aReportingManager.getFinType());
			this.fintype.setDescription(aReportingManager.getFinTypeDesc());
			FinanceType financeType = new FinanceType();
			financeType.setFinType(aReportingManager.getFinType());
			this.fintype.setObject(financeType);
		}

		if (aReportingManager.getBranch() != null) {
			this.branchcode.setValue(aReportingManager.getBranch());
			this.branchcode.setDescription(aReportingManager.getBranchDesc());
			Branch branch = new Branch();
			branch.setBranchCode(aReportingManager.getBranch());
			this.branchcode.setObject(branch);
		}

		this.recordStatus.setValue(aReportingManager.getRecordStatus());

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aReportingManager
	 */
	public void doWriteComponentsToBean(ReportingManager aReportingManager) {
		logger.debug(Literal.LEAVING);

		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		aReportingManager.setUserId(securityUser.getUsrID());

		try {
			this.businessvertical.getValue();
			aReportingManager.setBusinessVerticalCode(this.businessvertical.getValue());
			Object object = this.businessvertical.getObject();
			if (object != null) {
				aReportingManager.setBusinessVertical(((BusinessVertical) object).getId());
			} else {
				aReportingManager.setBusinessVertical(null);
			}

		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			this.productcode.getValue();
			Object object = this.productcode.getObject();
			if (object != null) {
				aReportingManager.setProduct(((Product) object).getProductCode());
			} else {
				aReportingManager.setProduct(null);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			this.fintype.getValue();
			Object object = this.fintype.getObject();
			if (object != null) {
				aReportingManager.setFinType(((FinanceType) object).getFinType());
			} else {
				aReportingManager.setFinType(null);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {

			this.branchcode.getValue();
			Object object = this.branchcode.getObject();
			if (object != null) {
				aReportingManager.setBranch(((Branch) object).getBranchCode());
			} else {
				aReportingManager.setBranch(null);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			this.reportingto.getValue();
			Object object = this.reportingto.getAttribute("UserId");
			if (object != null) {
				aReportingManager.setReportingTo(Long.parseLong(object.toString()));
				aReportingManager.setReportingToUserName(this.reportingto.getValue());
			} else {
				aReportingManager.setReportingTo(Long.MIN_VALUE);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		doRemoveValidation();
		doRemoveLOVValidation();

		if (!wve.isEmpty()) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Displays the dialog page.
	 * 
	 * @param reportingManager The entity that need to be render.
	 */
	public void doShowDialog(ReportingManager reportingManager) {
		logger.debug(Literal.LEAVING);

		if (newRecord) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.businessvertical.focus();
		} else {
			if (child) {
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

		if (enqiryModule) {
			this.btnCtrl.setBtnStatus_Enquiry();
			this.btnNotes.setVisible(false);
		}

		doWriteBeanToComponents(reportingManager);
		this.window_ReportingManagerDialog.doModal();

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug(Literal.LEAVING);

		if (!this.usrid.isReadonly()) {
			this.usrid.setConstraint(
					new PTStringValidator(Labels.getLabel("label_ReportingManangerDialog_UserId.value"), null, true));
		}
		if (!this.businessvertical.isReadonly()) {
			this.businessvertical.setConstraint(new PTStringValidator(
					Labels.getLabel("label_ReportingManangerDialog_BusinessVerticalId.value"), null, false));
		}
		if (!this.productcode.isReadonly()) {
			this.productcode.setConstraint(
					new PTStringValidator(Labels.getLabel("label_ReportingManangerDialog_Product.value"), null, false));
		}
		if (!this.fintype.isReadonly()) {
			this.fintype.setConstraint(new PTStringValidator(
					Labels.getLabel("label_ReportingManangerDialog_LoanType.value"), null, false));
		}
		if (!this.branchcode.isReadonly()) {
			this.branchcode.setConstraint(
					new PTStringValidator(Labels.getLabel("label_ReportingManangerDialog_Branch.value"), null, false));
		}
		if (!this.reportingto.isReadonly()) {
			this.reportingto.setConstraint(new PTStringValidator(
					Labels.getLabel("label_ReportingManangerDialog_ReportingTo.value"), null, true));
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug(Literal.LEAVING);

		this.usrid.setConstraint("");
		this.businessvertical.setConstraint("");
		this.productcode.setConstraint("");
		this.fintype.setConstraint("");
		this.branchcode.setConstraint("");
		this.reportingto.setConstraint("");

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set Validations for LOV Fields
	 */

	private void doSetLOVValidation() {
		logger.debug(Literal.LEAVING);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */

	private void doRemoveLOVValidation() {
		logger.debug(Literal.LEAVING);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Clears validation error messages from all the fields of the dialog controller.
	 */
	@Override
	protected void doClearMessage() {
		logger.debug(Literal.LEAVING);

		logger.debug(Literal.LEAVING);
	}

	private void doDelete() throws InterruptedException {
		logger.debug(Literal.ENTERING);

		final ReportingManager aReportingManager = new ReportingManager();
		BeanUtils.copyProperties(this.reportingManager, aReportingManager);

		final String keyReference = String.valueOf(aReportingManager.getId());

		doDelete(keyReference, aReportingManager);

		logger.debug(Literal.LEAVING);
	}

	protected void onDoDelete(final ReportingManager aReportingManager) {
		String tranType = PennantConstants.TRAN_WF;

		if (StringUtils.trimToEmpty(aReportingManager.getRecordType()).equals("")) {
			aReportingManager.setVersion(aReportingManager.getVersion() + 1);
			aReportingManager.setRecordType(PennantConstants.RECORD_TYPE_DEL);

			if (securityUserDialogCtrl != null && securityUserDialogCtrl.getSecurityUser().isWorkflow()) {
				aReportingManager.setNewRecord(true);
			}

			if (isWorkFlowEnabled()) {
				aReportingManager.setRecordStatus(userAction.getSelectedItem().getValue().toString());
				aReportingManager.setNewRecord(true);
				tranType = PennantConstants.TRAN_WF;
				getWorkFlowDetails(userAction.getSelectedItem().getLabel(), aReportingManager.getNextTaskId(),
						aReportingManager);
			} else {
				tranType = PennantConstants.TRAN_DEL;
			}
		}

		try {
			if (child) {
				tranType = PennantConstants.TRAN_DEL;
				AuditHeader auditHeader = newReportingManagerProcess(aReportingManager, tranType);
				auditHeader = ErrorControl.showErrorDetails(this.window_ReportingManagerDialog, auditHeader);
				int retValue = auditHeader.getProcessStatus();
				if (retValue == PennantConstants.porcessCONTINUE || retValue == PennantConstants.porcessOVERIDE) {
					securityUserDialogCtrl.doFillReportingManagerDetails(this.reportingManagerlist);
					closeDialog();

				}
			} else if (doProcess(aReportingManager, tranType)) {
				refreshList();
				closeDialog();
			}

		} catch (DataAccessException e) {
			MessageUtil.showError(e);
		}
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug(Literal.LEAVING);

		if (newRecord) {
			this.btnCancel.setVisible(false);
		} else {
			this.btnCancel.setVisible(true);
			this.businessvertical.setReadonly(true);
			this.productcode.setReadonly(true);
			this.branchcode.setReadonly(true);
			this.fintype.setReadonly(true);
		}

		readOnlyComponent(isReadOnly("SecurityUserDialog_RM_reportingto"), this.reportingto);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.reportingManager.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else if (newRecord) {
			this.btnCtrl.setBtnStatus_Edit();
			btnCancel.setVisible(false);
		} else {
			this.btnCtrl.setWFBtnStatus_Edit(true);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug(Literal.LEAVING);

		readOnlyComponent(true, this.usrid);
		readOnlyComponent(true, this.businessvertical);
		readOnlyComponent(true, this.productcode);
		readOnlyComponent(true, this.fintype);
		readOnlyComponent(true, this.branchcode);
		readOnlyComponent(true, this.reportingto);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(true);
			}
			this.recordStatus.setValue("");
			this.userAction.setSelectedIndex(0);

		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Clears the components values. <br>
	 */
	public void doClear() {
		logger.debug("Entering");
		this.usrid.setValue("");
		this.businessvertical.setValue("");
		this.productcode.setValue("");
		this.fintype.setValue("");
		this.reportingto.setValue("");

		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");
		final ReportingManager aReportingManager = new ReportingManager();
		BeanUtils.copyProperties(getReportingManager(), aReportingManager);
		boolean isNew = false;

		doSetValidation();
		doWriteComponentsToBean(aReportingManager);

		isNew = aReportingManager.isNewRecord();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aReportingManager.getRecordType())) {
				aReportingManager.setVersion(aReportingManager.getVersion() + 1);
				if (isNew) {
					aReportingManager.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aReportingManager.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aReportingManager.setNewRecord(true);
				}
			}
		} else {
			if (aReportingManager.isNewRecord()) {
				aReportingManager.setVersion(1);
				aReportingManager.setRecordType(PennantConstants.RCD_ADD);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				if (workflow && StringUtils.isBlank(aReportingManager.getRecordType())) {
					aReportingManager.setNewRecord(true);
				}
			}

			if (StringUtils.isBlank(aReportingManager.getRecordType())) {
				aReportingManager.setVersion(aReportingManager.getVersion() + 1);
				aReportingManager.setRecordType(PennantConstants.RCD_UPD);
			}

			if (aReportingManager.getRecordType().equals(PennantConstants.RCD_ADD) && newRecord) {
				tranType = PennantConstants.TRAN_ADD;
			} else if (aReportingManager.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_UPD;
			} else {
				aReportingManager.setVersion(aReportingManager.getVersion() + 1);
				if (isNew) {
					tranType = PennantConstants.TRAN_ADD;
				} else {
					tranType = PennantConstants.TRAN_UPD;
				}
			}

		}

		try {

			if (child) {
				AuditHeader auditHeader = newReportingManagerProcess(aReportingManager, tranType);
				auditHeader = ErrorControl.showErrorDetails(this.window_ReportingManagerDialog, auditHeader);
				int retValue = auditHeader.getProcessStatus();

				if (retValue == PennantConstants.porcessCONTINUE || retValue == PennantConstants.porcessOVERIDE) {
					securityUserDialogCtrl.doFillReportingManagerDetails(this.reportingManagerlist);
					closeDialog();

				}

			} else if (doProcess(aReportingManager, tranType)) {
				refreshList();
				closeDialog();
			}

		} catch (final DataAccessException e) {
			logger.error(e);
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	private boolean isEqual(ReportingManager rm, ReportingManager arm) {
		return rm.getBusinessVertical() == arm.getBusinessVertical()
				&& StringUtils.equals(rm.getFinType(), arm.getFinType())
				&& StringUtils.equals(rm.getProduct(), arm.getProduct())
				&& StringUtils.equals(rm.getBranch(), arm.getBranch());
	}

	private AuditHeader newReportingManagerProcess(ReportingManager arm, String tranType) {
		boolean recordAdded = false;

		AuditHeader auditHeader = getAuditHeader(arm, tranType);
		reportingManagerlist = new ArrayList<ReportingManager>();

		String[] valueParm = new String[5];

		StringBuilder builder = new StringBuilder();

		builder.append(App.getLabel("label_UserId")).append(": ").append(arm.getUserId());
		builder.append("\n");
		builder.append(App.getLabel("label_BusinessVertical")).append(": ");
		builder.append(StringUtils.trimToEmpty(arm.getBusinessVerticalCode()));
		builder.append("\n");
		builder.append(App.getLabel("label_LoanType")).append(": ").append(StringUtils.trimToEmpty(arm.getFinType()));
		builder.append("\n");
		builder.append(App.getLabel("label_Product")).append(": ").append(StringUtils.trimToEmpty(arm.getProduct()));
		builder.append("\n");
		builder.append(App.getLabel("label_Branch")).append(": ").append(StringUtils.trimToEmpty(arm.getBranch()));
		builder.append("\n");

		valueParm[0] = " ";
		valueParm[1] = builder.toString();

		List<ReportingManager> reportingmanagers = securityUserDialogCtrl.getReportingManagerDetailList();

		if (CollectionUtils.isNotEmpty(reportingmanagers)) {
			for (ReportingManager rm : reportingmanagers) {

				if (!isEqual(rm, arm)) {
					reportingManagerlist.add(rm);
					continue;
				}

				if (newRecord) {
					auditHeader.setErrorDetails(ErrorUtil
							.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", valueParm, null)));
					return auditHeader;
				}

				if (PennantConstants.TRAN_DEL.equals(tranType)) {
					String recordType = arm.getRecordType();
					if (recordType.equals(PennantConstants.RCD_UPD)) {
						arm.setRecordType(PennantConstants.RECORD_TYPE_DEL);
						recordAdded = true;
						reportingManagerlist.add(arm);
					} else if (recordType.equals(PennantConstants.RCD_ADD)) {
						recordAdded = true;
					} else if (recordType.equals(PennantConstants.RECORD_TYPE_NEW)) {
						arm.setRecordType(PennantConstants.RECORD_TYPE_CAN);
						recordAdded = true;
						reportingManagerlist.add(arm);
					} else if (recordType.equals(PennantConstants.RECORD_TYPE_CAN)) {
						recordAdded = true;
					}
				} else {
					if (!PennantConstants.TRAN_UPD.equals(tranType)) {
						reportingManagerlist.add(arm);
					}
				}
			}
		}

		if (!recordAdded) {
			reportingManagerlist.add(arm);
		}

		return auditHeader;

	}

	/**
	 * Set the workFlow Details List to Object
	 * 
	 * @param aAuthorizedSignatoryRepository (AuthorizedSignatoryRepository)
	 * 
	 * @param tranType                       (String)
	 * 
	 * @return boolean
	 * 
	 */
	protected boolean doProcess(ReportingManager aReportingManager, String tranType) {
		logger.debug("Entering");
		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aReportingManager.setLastMntBy(getUserWorkspace().getLoggedInUser().getLoginLogId());
		aReportingManager.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aReportingManager.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(role);
			String nextTaskId = "";
			aReportingManager.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aReportingManager.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aReportingManager);
				}

				if (isNotesMandatory(taskId, aReportingManager)) {
					if (!notesEntered) {
						MessageUtil.showError(Labels.getLabel("Notes_NotEmpty"));
						return false;
					}

				}
			}
			if (!StringUtils.isBlank(nextTaskId)) {
				String[] nextTasks = nextTaskId.split(";");

				if (nextTasks != null && nextTasks.length > 0) {
					for (int i = 0; i < nextTasks.length; i++) {

						if (nextRoleCode.length() > 1) {
							nextRoleCode = nextRoleCode.concat(",");
						}
						nextRoleCode = getTaskOwner(nextTasks[i]);
					}
				} else {
					nextRoleCode = getTaskOwner(nextTaskId);
				}
			}

			aReportingManager.setTaskId(taskId);
			aReportingManager.setNextTaskId(nextTaskId);
			aReportingManager.setRoleCode(role);
			aReportingManager.setNextRoleCode(nextRoleCode);
			aReportingManager.setWorkflowId(aReportingManager.getWorkflowId());
			aReportingManager.setWorkflowId(reportingManager.getWorkflowId());
			auditHeader = getAuditHeader(aReportingManager, tranType);
			String operationRefs = getServiceOperations(taskId, aReportingManager);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aReportingManager, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aReportingManager, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}

		logger.debug(Literal.LEAVING);
		return processCompleted;
	}

	/**
	 * Get the result after processing DataBase Operations
	 * 
	 * @param AuditHeader auditHeader
	 * @param method      (String)
	 * @return boolean
	 * 
	 */

	private boolean doSaveProcess(AuditHeader auditHeader, String method) {
		logger.debug("Entering");
		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		ReportingManager aReportingManager = (ReportingManager) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		try {

			while (retValue == PennantConstants.porcessOVERIDE) {

				if (StringUtils.isBlank(method)) {
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
						auditHeader = reportingManagerService.delete(auditHeader);
						deleteNotes = true;
					} else {
						auditHeader = reportingManagerService.saveOrUpdate(auditHeader);
					}

				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
						auditHeader = reportingManagerService.doApprove(auditHeader);

						if (aReportingManager.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}

					} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = reportingManagerService.doReject(auditHeader);
						if (aReportingManager.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}

					} else {
						auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_9999,
								Labels.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_ReportingManagerDialog, auditHeader);
						return processCompleted;
					}
				}

				auditHeader = ErrorControl.showErrorDetails(this.window_ReportingManagerDialog, auditHeader);
				retValue = auditHeader.getProcessStatus();

				if (retValue == PennantConstants.porcessCONTINUE) {
					processCompleted = true;

					if (deleteNotes) {
						deleteNotes(getNotes(this.reportingManager), true);
					}
				}

				if (retValue == PennantConstants.porcessOVERIDE) {
					auditHeader.setOveride(true);
					auditHeader.setErrorMessage(null);
					auditHeader.setInfoMessage(null);
					auditHeader.setOverideMessage(null);
				}
			}
		} catch (AppException e) {
			logger.error("Exception: ", e);
		}
		setOverideMap(auditHeader.getOverideMap());

		logger.debug("Leaving");
		return processCompleted;
	}

	/**
	 * @param aAuthorizedSignatoryRepository
	 * @param tranType
	 * @return
	 */

	private AuditHeader getAuditHeader(ReportingManager aReportingManager, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aReportingManager.getBefImage(), aReportingManager);
		return new AuditHeader(getReference(), null, null, null, auditDetail, aReportingManager.getUserDetails(),
				getOverideMap());
	}

	public void setReportingManagerService(ReportingManagerService reportingManagerService) {
		this.reportingManagerService = reportingManagerService;
	}

	public ReportingManager getReportingManager() {
		return reportingManager;
	}

	public void setReportingManager(ReportingManager reportingManager) {
		this.reportingManager = reportingManager;
	}

	public List<ReportingManager> getReportingManagerlist() {
		return reportingManagerlist;
	}

	public void setReportingManagerlist(List<ReportingManager> reportingManagerlist) {
		this.reportingManagerlist = reportingManagerlist;
	}
}
