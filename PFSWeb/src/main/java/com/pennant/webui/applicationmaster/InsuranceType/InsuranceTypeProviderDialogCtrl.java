package com.pennant.webui.applicationmaster.InsuranceType;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.model.ErrorDetail;
import com.pennant.backend.model.applicationmaster.InsuranceTypeProvider;
import com.pennant.backend.model.applicationmaster.TakafulProvider;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;

public class InsuranceTypeProviderDialogCtrl extends GFCBaseCtrl<InsuranceTypeProvider> {
	private static final long			serialVersionUID	= -6945930303723518608L;
	private static final Logger			logger				= Logger.getLogger(InsuranceTypeProviderDialogCtrl.class);

	protected Window					window_InsuranceTypeProviderDialog;
	protected ExtendedCombobox			provider;
	protected Decimalbox				insRate;
	private InsuranceTypeProvider		insuranceTypeProvider;

	private InsuranceTypeDialogCtrl		insuranceTypeDialogCtrl;
	private String						userRole;
	private List<InsuranceTypeProvider>	insuranceTypeProviderList;
	private Textbox                     insuranceType;
	private Textbox                     insuranceTypeDesc;

	/**
	 * default constructor.<br>
	 */
	public InsuranceTypeProviderDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "InsuranceTypeProviderDialog";
	}

	/**
	 * 
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 * @throws Exception
	 */
	public void onCreate$window_InsuranceTypeProviderDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_InsuranceTypeProviderDialog);
		if (arguments.containsKey("insuranceTypeProvider")) {
			this.insuranceTypeProvider = (InsuranceTypeProvider) arguments.get("insuranceTypeProvider");
			InsuranceTypeProvider befImage = new InsuranceTypeProvider();
			BeanUtils.copyProperties(this.insuranceTypeProvider, befImage);
			this.insuranceTypeProvider.setBefImage(befImage);
			setInsuranceTypeProvider(this.insuranceTypeProvider);

		} else {
			setInsuranceTypeProvider(null);
		}

		this.insuranceTypeProvider.setWorkflowId(0);
		doLoadWorkFlow(this.insuranceTypeProvider.isWorkflow(), this.insuranceTypeProvider.getWorkflowId(),
				this.insuranceTypeProvider.getNextTaskId());

		if (isWorkFlowEnabled()) {
			this.userAction = setListRecordStatus(this.userAction);
			getUserWorkspace().allocateRoleAuthorities(getRole(), "InsuranceTypeProviderDialog");
		}

		if (arguments.containsKey("role")) {
			userRole = arguments.get("role").toString();
			getUserWorkspace().allocateRoleAuthorities(arguments.get("role").toString(), "InsuranceTypeProviderDialog");
		}

		doCheckRights();
		// READ OVERHANDED params !
		// we get the transactionEntryListWindow controller. So we have access
		// to it and can synchronize the shown data when we do insert, edit or
		// delete transactionEntry here.
		if (arguments.containsKey("InsuranceTypeDialogCtrl")) {
			setInsuranceTypeDialogCtrl((InsuranceTypeDialogCtrl) arguments.get("InsuranceTypeDialogCtrl"));
		} else {
			setInsuranceTypeDialogCtrl(null);
		}

		//set Field Properties
		doSetFieldProperties();
		doShowDialog(getInsuranceTypeProvider());
	}

	private void doSetFieldProperties() {

		this.provider.setMandatoryStyle(true);
		this.provider.setTextBoxWidth(120);
		this.provider.setModuleName("TakafulProvider");
		this.provider.setValueColumn("TakafulCode");
		this.provider.setDescColumn("TakafulName");
		this.provider.setValidateColumns(new String[] { "TakafulCode" });
		this.provider.setTextBoxWidth(143);
		
		this.insRate.setReadonly(true);
		this.insRate.setFormat(PennantConstants.rateFormate9);

		if (isWorkFlowEnabled()) {
			this.groupboxWf.setVisible(true);
		} else {
			this.groupboxWf.setVisible(false);
		}

	}

	public void onFulfill$provider(Event event) throws InterruptedException {
		Object dataObject = provider.getObject();

		if (dataObject instanceof String) {
			this.insRate.setText("");
		} else {
			TakafulProvider details = (TakafulProvider) dataObject;
			if(details!=null){				
				this.insRate.setValue(details.getTakafulRate());
			}
		}

	}

	private void doShowDialog(InsuranceTypeProvider insuranceTypeProvider) throws InterruptedException {
		logger.debug("Entering");

		// set ReadOnly mode accordingly if the object is new or not.
		if (insuranceTypeProvider.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.provider.focus();

		} else {
			this.provider.focus();
			doEdit();
			btnCancel.setVisible(false);
			this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_InsuranceTypeProviderDialog_btnDelete"));
		}
		try {
			// fill the components with the data
			doWriteBeanToComponents(insuranceTypeProvider);

			this.window_InsuranceTypeProviderDialog.doModal();
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	private void doEdit() {

		logger.debug("Entering");
		if (getInsuranceTypeProvider().isNewRecord()) {
			this.provider.setReadonly(isReadOnly("InsuranceTypeProviderDialog_InsuranceProvider"));
		} else {
			this.provider.setReadonly(true);
		}

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.insuranceTypeProvider.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
				this.btnSave.setVisible(false);
			}
		} else {
			this.btnCtrl.setBtnStatus_Edit();
			if (!this.insuranceTypeProvider.isNewRecord()) {
				this.btnSave.setVisible(false);
			}
		}
		logger.debug("Leaving");

	}
	
	public boolean isReadOnly(String componentName) {
		return getUserWorkspace().isReadOnly(componentName);
	}

	private void doWriteBeanToComponents(InsuranceTypeProvider insuranceTypeProvider) {
		this.insuranceType.setValue(insuranceTypeProvider.getInsuranceType());
		this.insuranceTypeDesc.setValue(insuranceTypeProvider.getInsuranceTypeDesc());
		this.provider.setValue(insuranceTypeProvider.getProviderCode());
		this.provider.setDescription(insuranceTypeProvider.getProviderName());
		this.insRate.setValue(insuranceTypeProvider.getInsuranceRate());
		

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
		logger.debug("Leaving");
	}

	private void doSave() throws InterruptedException {

		logger.debug("Entering");

		final InsuranceTypeProvider insuranceTypeProvider = new InsuranceTypeProvider();
		BeanUtils.copyProperties(getInsuranceTypeProvider(), insuranceTypeProvider);
		boolean isNew = false;

		// force validation, if on, than execute by component.getValue()
		doClearMessage();
		doSetValidation();

		// fill the TransactionEntry object with the components data
		doWriteComponentsToBean(insuranceTypeProvider);

		// Write the additional validations as per below example
		// get the selected branch object from the lisBox
		// Do data level validations here

		isNew = insuranceTypeProvider.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(insuranceTypeProvider.getRecordType())) {
				insuranceTypeProvider.setVersion(insuranceTypeProvider.getVersion() + 1);
				if (isNew) {
					insuranceTypeProvider.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					insuranceTypeProvider.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					insuranceTypeProvider.setNewRecord(true);
				}
			}
		} else {

			if (isNew) {
				insuranceTypeProvider.setVersion(1);
				insuranceTypeProvider.setRecordType(PennantConstants.RCD_ADD);
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}

			if (StringUtils.isBlank(insuranceTypeProvider.getRecordType())) {
				insuranceTypeProvider.setVersion(insuranceTypeProvider.getVersion() + 1);
				insuranceTypeProvider.setRecordType(PennantConstants.RCD_UPD);
			}

			if (insuranceTypeProvider.getRecordType().equals(PennantConstants.RCD_ADD) && isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else if (insuranceTypeProvider.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {
			AuditHeader auditHeader = newInsuranceTypeProviderEntryProcess(insuranceTypeProvider, tranType);
			auditHeader = ErrorControl.showErrorDetails(this.window_InsuranceTypeProviderDialog, auditHeader);
			int retValue = auditHeader.getProcessStatus();
			if (retValue == PennantConstants.porcessCONTINUE || retValue == PennantConstants.porcessOVERIDE) {
				getInsuranceTypeDialogCtrl().doFillInsuranceTypeProviders(this.insuranceTypeProviderList);
				closeDialog();
			}
		} catch (final DataAccessException e) {
			logger.error("Exception: ", e);
			showMessage(e);
		}
		logger.debug("Leaving");

		// TODO Auto-generated method stub

	}

	private void showMessage(Exception e) {
		AuditHeader auditHeader = new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_UNDEF, e.getMessage(), null));
			ErrorControl.showErrorControl(this.window_InsuranceTypeProviderDialog, auditHeader);
		} catch (Exception exp) {
			logger.error("Exception: ", exp);
		}
	}

	private AuditHeader getAuditHeader(InsuranceTypeProvider aInsuranceTypeProvider, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aInsuranceTypeProvider.getBefImage(),
				aInsuranceTypeProvider);
		return new AuditHeader(aInsuranceTypeProvider.getInsuranceType(), null, null, null, auditDetail,
				aInsuranceTypeProvider.getUserDetails(), getOverideMap());
	}

	private AuditHeader newInsuranceTypeProviderEntryProcess(InsuranceTypeProvider aInsuranceTypeProvider,
			String tranType) {
		boolean recordAdded = false;
		AuditHeader auditHeader = getAuditHeader(aInsuranceTypeProvider, tranType);
		insuranceTypeProviderList = new ArrayList<InsuranceTypeProvider>();
		String[] valueParm = new String[2];
		String[] errParm = new String[2];
		valueParm[0] = aInsuranceTypeProvider.getProviderCode();
		valueParm[1] = aInsuranceTypeProvider.getInsuranceType();
		errParm[0] = PennantJavaUtil.getLabel("label_InsuranceTypeProviderDialog_Provider.value") + ":" + valueParm[0];
		errParm[1] = PennantJavaUtil.getLabel("label_FinTypeInsuranceDialog_InsuranceType.value") + ":" + valueParm[1];
		List<InsuranceTypeProvider> list = getInsuranceTypeDialogCtrl().getInsuranceTypeProviderList();
		if (list != null && list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				InsuranceTypeProvider insuranceTypeProvider = list.get(i);
				if (insuranceTypeProvider.getProviderCode().equals(aInsuranceTypeProvider.getProviderCode())
						&& insuranceTypeProvider.getInsuranceType().equals(aInsuranceTypeProvider.getInsuranceType())) {
					// Both Current and Existing list rating same
					if (aInsuranceTypeProvider.isNew()) {
						auditHeader.setErrorDetails(ErrorUtil.getErrorDetail(new ErrorDetail(
								PennantConstants.KEY_FIELD, "41008", errParm, valueParm), getUserWorkspace()
								.getUserLanguage()));
						this.provider.setValue("");
						this.provider.setDescription("");
						return auditHeader;
					}
					if (PennantConstants.TRAN_DEL.equals(tranType)) {
						if (PennantConstants.RECORD_TYPE_UPD.equals(aInsuranceTypeProvider.getRecordType())) {
							aInsuranceTypeProvider.setRecordType(PennantConstants.RECORD_TYPE_DEL);
							recordAdded = true;
							insuranceTypeProviderList.add(aInsuranceTypeProvider);
						} else if (PennantConstants.RCD_ADD.equals(aInsuranceTypeProvider.getRecordType())) {
							recordAdded = true;
						} else if (PennantConstants.RECORD_TYPE_NEW.equals(aInsuranceTypeProvider.getRecordType())) {
							aInsuranceTypeProvider.setRecordType(PennantConstants.RECORD_TYPE_CAN);
							recordAdded = true;
							insuranceTypeProviderList.add(aInsuranceTypeProvider);
						} else if (PennantConstants.RECORD_TYPE_CAN.equals(aInsuranceTypeProvider.getRecordType())) {
							recordAdded = true;
							List<InsuranceTypeProvider> savedList = getInsuranceTypeDialogCtrl().getInsuranceType()
									.getInsuranceProviders();
							for (int j = 0; j < savedList.size(); j++) {
								InsuranceTypeProvider insType = savedList.get(j);
								if (insType.getInsuranceType().equals(aInsuranceTypeProvider.getInsuranceType())) {
									insuranceTypeProviderList.add(insType);
								}
							}
						} else if (PennantConstants.RECORD_TYPE_DEL.equals(aInsuranceTypeProvider.getRecordType())) {
							aInsuranceTypeProvider.setNewRecord(true);
						}
					} else {
						if (!PennantConstants.TRAN_UPD.equals(tranType)) {
							insuranceTypeProviderList.add(insuranceTypeProvider);
						}
					}
				} else {
					insuranceTypeProviderList.add(insuranceTypeProvider);
				}
			}
		}
		if (!recordAdded) {
			insuranceTypeProviderList.add(aInsuranceTypeProvider);
		}
		logger.debug("Leaving");
		return auditHeader;

	}

	private void doWriteComponentsToBean(InsuranceTypeProvider aInsuranceTypeProvider) {
		aInsuranceTypeProvider.setProviderCode(this.provider.getValue());
		aInsuranceTypeProvider.setProviderName(this.provider.getDescription());
		aInsuranceTypeProvider.setInsuranceRate(this.insRate.getValue());
		doClearMessage();

	}

	private void doSetValidation() {
		if (this.provider.isButtonVisible()) {
			this.provider.setConstraint(new PTStringValidator(Labels
					.getLabel("label_InsuranceTypeProviderDialog_Provider.value"), null, true, true));
		}

	}

	@Override
	protected void doClearMessage(){
		this.provider.setConstraint("");
		this.provider.setErrorMessage("");
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

	private void doCheckRights() {

		logger.debug("Entering");
		getUserWorkspace().allocateAuthorities("InsuranceTypeProviderDialog", userRole);
		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_InsuranceTypeProviderDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_InsuranceTypeProviderDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_InsuranceTypeProviderDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_InsuranceTypeProviderDialog_btnSave"));
		this.btnCancel.setVisible(false);

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
		logger.debug("Leaving");
	}
	
	/**
	 * Deletes a FinTypeAccount object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");
		final InsuranceTypeProvider aInsTyeProvider = new InsuranceTypeProvider();
		BeanUtils.copyProperties(getInsuranceTypeProvider(), aInsTyeProvider);
		String tranType = PennantConstants.TRAN_WF;
		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> " + 
				Labels.getLabel("label_FinTypeAccountDialog_FinCcy.value")+" : "+aInsTyeProvider.getInsuranceType()+","+
				Labels.getLabel("label_FinTypeAccountDialog_Event.value")+" : "+ aInsTyeProvider.getProviderCode();
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.isBlank(aInsTyeProvider.getRecordType())) {
				aInsTyeProvider.setVersion(aInsTyeProvider.getVersion() + 1);
				aInsTyeProvider.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				if (isWorkFlowEnabled()) {
					aInsTyeProvider.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			} else if (StringUtils.trimToEmpty(aInsTyeProvider.getRecordType()).equals(PennantConstants.RCD_UPD)) {
				aInsTyeProvider.setVersion(aInsTyeProvider.getVersion() + 1);
				aInsTyeProvider.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}
			try {
				tranType = PennantConstants.TRAN_DEL;
				AuditHeader auditHeader = newInsuranceTypeProviderEntryProcess(aInsTyeProvider, tranType);
				auditHeader = ErrorControl.showErrorDetails(this.window_InsuranceTypeProviderDialog, auditHeader);
				int retValue = auditHeader.getProcessStatus();
				if (retValue == PennantConstants.porcessCONTINUE || retValue == PennantConstants.porcessOVERIDE) {
					getInsuranceTypeDialogCtrl().doFillInsuranceTypeProviders(this.insuranceTypeProviderList);
					closeDialog();
				}
			} catch (DataAccessException e) {
				logger.error("Exception: ", e);
				showMessage(e);
			}
		}
		logger.debug("Leaving");
	}

	public InsuranceTypeProvider getInsuranceTypeProvider() {
		return insuranceTypeProvider;
	}

	public void setInsuranceTypeProvider(InsuranceTypeProvider insuranceTypeProvider) {
		this.insuranceTypeProvider = insuranceTypeProvider;
	}

	public InsuranceTypeDialogCtrl getInsuranceTypeDialogCtrl() {
		return insuranceTypeDialogCtrl;
	}

	public void setInsuranceTypeDialogCtrl(InsuranceTypeDialogCtrl insuranceTypeDialogCtrl) {
		this.insuranceTypeDialogCtrl = insuranceTypeDialogCtrl;
	}

	public List<InsuranceTypeProvider> getInsuranceTypeProviderList() {
		return insuranceTypeProviderList;
	}

	public void setInsuranceTypeProviderList(List<InsuranceTypeProvider> insuranceTypeProviderList) {
		this.insuranceTypeProviderList = insuranceTypeProviderList;
	}

}
