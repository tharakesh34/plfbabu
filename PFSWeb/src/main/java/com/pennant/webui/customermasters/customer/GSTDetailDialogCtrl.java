package com.pennant.webui.customermasters.customer;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.systemmasters.ProvinceDAO;
import com.pennant.backend.model.applicationmaster.PinCode;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.GSTDetail;
import com.pennant.backend.model.systemmasters.City;
import com.pennant.backend.model.systemmasters.Country;
import com.pennant.backend.model.systemmasters.Province;
import com.pennant.backend.util.FacilityConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.component.Uppercasebox;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.jdbc.DataType;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;

public class GSTDetailDialogCtrl extends GFCBaseCtrl<GSTDetail> {
	private static final long serialVersionUID = -5741525035755500515L;
	private static final Logger logger = LogManager.getLogger(GSTDetailDialogCtrl.class);

	protected Window window_gstDetailsDialog;
	protected Uppercasebox gstNumber;
	protected Textbox address;
	protected Textbox addressLine1;
	protected Textbox addressLine2;
	protected Textbox addressLine3;
	protected Textbox addressLine4;
	protected ExtendedCombobox countryCode;
	protected ExtendedCombobox stateCode;
	protected ExtendedCombobox cityCode;
	protected ExtendedCombobox pincode;
	protected Checkbox tin;
	protected Checkbox tinName;
	protected Checkbox tinAddress;

	private boolean newRecord = false;
	private boolean newCustomer = false;
	private String moduleType = "";
	private String userRole = "";
	private boolean isFinanceProcess = false;

	private CustomerDialogCtrl customerDialogCtrl;

	private GSTDetail detail;
	private List<GSTDetail> gstDetails;
	private ProvinceDAO provinceDAO;

	public GSTDetailDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "GST_Details_Dialog";
	}

	public void onCreate$window_gstDetailsDialog(Event event) throws Exception {
		logger.debug(Literal.ENTERING);

		setPageComponents(window_gstDetailsDialog);

		try {
			if (arguments.containsKey("gstDetails")) {
				this.detail = (GSTDetail) arguments.get("gstDetails");
				GSTDetail befImage = new GSTDetail();
				BeanUtils.copyProperties(this.detail, befImage);
				this.detail.setBefImage(befImage);
				setDetail(this.detail);
			} else {
				setDetail(null);
			}

			if (detail.isNewRecord()) {
				setNewRecord(true);
			}

			if (arguments.containsKey("customerDialogCtrl")) {
				setCustomerDialogCtrl((CustomerDialogCtrl) arguments.get("customerDialogCtrl"));
				setNewCustomer(true);

				if (arguments.containsKey("newRecord")) {
					setNewRecord(true);
				} else {
					setNewRecord(false);
				}
				this.detail.setWorkflowId(0);
				if (arguments.containsKey("roleCode")) {
					userRole = arguments.get("roleCode").toString();
					getUserWorkspace().allocateRoleAuthorities(userRole, "GST_Details_Dialog");
				}
			}

			if (arguments.containsKey("customerViewDialogCtrl")) {
				setNewCustomer(true);

				if (arguments.containsKey("newRecord")) {
					setNewRecord(true);
				} else {
					setNewRecord(false);
				}
				this.detail.setWorkflowId(0);
				if (arguments.containsKey("roleCode")) {
					userRole = arguments.get("roleCode").toString();
					getUserWorkspace().allocateRoleAuthorities(userRole, "GST_Details_Dialog");
				}
			}

			if (arguments.containsKey("isFinanceProcess")) {
				isFinanceProcess = (Boolean) arguments.get("isFinanceProcess");
			}

			doLoadWorkFlow(this.detail.isWorkflow(), this.detail.getWorkflowId(), this.detail.getNextTaskId());

			doCheckRights();

			if (isWorkFlowEnabled()) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(), "GST_Details_Dialog");
			}

			doSetFieldProperties();
			doShowDialog(detail);
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_gstDetailsDialog.onClose();
		}

		logger.debug(Literal.LEAVING);
	}

	private void doSetFieldProperties() {
		logger.debug(Literal.ENTERING);

		this.countryCode.setModuleName("Country");
		this.countryCode.setValueColumn("CountryCode");
		this.countryCode.setDescColumn("CountryDesc");
		this.countryCode.setDisplayStyle(2);
		this.countryCode.setMandatoryStyle(true);
		this.countryCode.setValidateColumns(new String[] { "CountryCode" });

		this.stateCode.setModuleName("Province");
		this.stateCode.setValueColumn("CPProvince");
		this.stateCode.setDescColumn("CPProvinceName");
		this.stateCode.setDisplayStyle(2);
		this.stateCode.setMandatoryStyle(true);
		this.stateCode.setValidateColumns(new String[] { "CPProvince" });

		this.cityCode.setModuleName("City");
		this.cityCode.setValueColumn("PCCity");
		this.cityCode.setDescColumn("PCCityName");
		this.cityCode.setDisplayStyle(2);
		this.cityCode.setMandatoryStyle(true);
		this.cityCode.setValidateColumns(new String[] { "PCCity" });

		this.pincode.setModuleName("PinCode");
		this.pincode.setValueColumn("PinCodeId");
		this.pincode.setDescColumn("AreaName");
		this.pincode.setValueType(DataType.LONG);
		this.pincode.setInputAllowed(false);
		this.pincode.setMandatoryStyle(true);
		this.pincode.setValidateColumns(new String[] { "PinCodeId" });
		this.address.setMaxlength(50);

		if (isWorkFlowEnabled()) {
			this.groupboxWf.setVisible(true);
		} else {
			this.groupboxWf.setVisible(false);
			this.south.setHeight("0px");
		}

		logger.debug(Literal.LEAVING);
	}

	private void doCheckRights() {
		logger.debug(Literal.ENTERING);

		getUserWorkspace().allocateAuthorities("GST_Details_Dialog", userRole);

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_GST_Details_Dialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_GST_Details_Dialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_GST_Details_Dialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_GST_Details_Dialog_btnSave"));
		this.btnCancel.setVisible(false);

		if (this.enqiryModule) {
			this.btnNew.setVisible(false);
			this.btnEdit.setVisible(false);
			this.btnDelete.setVisible(false);
			this.btnSave.setVisible(false);
		}

		logger.debug(Literal.LEAVING);
	}

	private void doShowDialog(GSTDetail gst) {
		if (newRecord) {
			this.btnCtrl.setInitNew();
			doEdit();
		} else {
			if (newCustomer) {
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
			doWriteBeanToComponents(gst);

			doCheckEnquiry();

			if (newCustomer) {
				this.window_gstDetailsDialog.setHeight("80%");
				this.window_gstDetailsDialog.setWidth("80%");
				this.groupboxWf.setVisible(false);
				this.window_gstDetailsDialog.doModal();
			} else {
				if (FacilityConstants.MODULE_NAME.equals(this.moduleType)) {
					this.window_gstDetailsDialog.setWidth("80%");
					this.window_gstDetailsDialog.setHeight("80%");
					setDialog(DialogType.MODAL);
				} else {
					this.window_gstDetailsDialog.setWidth("80%");
					this.window_gstDetailsDialog.setHeight("80%");
					setDialog(DialogType.EMBEDDED);
				}
			}
		} catch (UiException e) {
			logger.error(Literal.EXCEPTION, e);
			this.window_gstDetailsDialog.onClose();
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			throw e;
		}
	}

	private void doCheckEnquiry() {
		if (PennantConstants.MODULETYPE_ENQ.equals(this.moduleType)) {
			this.gstNumber.setReadonly(true);
			this.address.setReadonly(true);
			this.addressLine1.setReadonly(true);
			this.addressLine2.setReadonly(true);
			this.addressLine3.setReadonly(true);
			this.addressLine4.setReadonly(true);
			this.countryCode.setReadonly(true);
			this.stateCode.setReadonly(true);
			this.cityCode.setReadonly(true);
			this.pincode.setReadonly(true);
			this.tin.setDisabled(true);
			this.tinName.setDisabled(true);
			this.tinAddress.setDisabled(true);

			this.btnSave.setVisible(false);
			this.btnDelete.setVisible(false);
		}
	}

	private void doWriteBeanToComponents(GSTDetail gst) {
		logger.debug(Literal.ENTERING);

		this.gstNumber.setValue(gst.getGstNumber());
		this.address.setValue(gst.getAddress());
		this.addressLine1.setValue(gst.getAddressLine1());
		this.addressLine2.setValue(gst.getAddressLine2());
		this.addressLine3.setValue(gst.getAddressLine3());
		this.addressLine4.setValue(gst.getAddressLine4());

		this.countryCode.setValue(gst.getCountryCode());
		this.countryCode.setDescription(gst.getCountryName());

		this.stateCode.setValue(gst.getStateCode());
		this.stateCode.setDescription(gst.getStateName());

		this.cityCode.setValue(gst.getCityCode());
		this.cityCode.setDescription(gst.getCityName());

		this.pincode.setValue(gst.getPinCode());
		this.pincode.setDescription(gst.getPinCodeName());
		this.pincode.setAttribute("pinCodeId", gst.getPinCodeId());

		this.tin.setChecked(gst.isTin());
		this.tinName.setChecked(gst.isTinName());
		this.tinAddress.setChecked(gst.isTinAddress());

		this.recordStatus.setValue(gst.getRecordStatus());

		logger.debug(Literal.LEAVING);
	}

	public void onFulfill$countryCode(Event event) {
		logger.debug(Literal.ENTERING);

		Object dataObject = countryCode.getObject();
		String pcProvince = null;
		if (dataObject instanceof String) {
			this.stateCode.setValue("");
			this.stateCode.setDescription("");
			this.cityCode.setValue("");
			this.cityCode.setDescription("");
			this.pincode.setValue("");
			this.pincode.setDescription("");
			fillPindetails(null, null);
		} else if (!(dataObject instanceof String)) {
			Country country = (Country) dataObject;
			if (country == null) {
				fillProvinceDetails(null);
			}
			if (country != null) {
				this.stateCode.setErrorMessage("");
				pcProvince = country.getCountryCode();
				fillProvinceDetails(pcProvince);
			} else {
				this.stateCode.setObject("");
				this.cityCode.setObject("");
				this.pincode.setObject("");
				this.stateCode.setValue("");
				this.stateCode.setDescription("");
				this.cityCode.setValue("");
				this.cityCode.setDescription("");
				this.pincode.setValue("", "");
				this.pincode.setAttribute("pinCodeId", null);
				this.pincode.setDescription("");
			}
			fillPindetails(null, null);
		}

		logger.debug(Literal.LEAVING);
	}

	public void onFulfill$stateCode(Event event) {
		logger.debug(Literal.ENTERING);

		Object dataObject = stateCode.getObject();
		String pcProvince = this.stateCode.getValue();
		if (dataObject instanceof String) {
			this.cityCode.setValue("");
			this.cityCode.setDescription("");
			this.pincode.setValue("");
			this.pincode.setDescription("");
			fillPindetails(null, null);
		} else if (!(dataObject instanceof String)) {
			Province province = (Province) dataObject;
			if (province == null) {
				fillPindetails(null, null);
			}
			if (province != null) {
				this.stateCode.setErrorMessage("");
				pcProvince = this.stateCode.getValue();
				this.countryCode.setValue(province.getCPCountry());
				this.countryCode.setDescription(province.getLovDescCPCountryName());
				this.cityCode.setValue("");
				this.cityCode.setDescription("");
				this.pincode.setValue("");
				this.pincode.setDescription("");
				fillPindetails(null, pcProvince);
			} else {
				this.cityCode.setObject("");
				this.pincode.setObject("");
				this.cityCode.setValue("");
				this.cityCode.setDescription("");
				this.pincode.setValue("", "");
				this.pincode.setAttribute("pinCodeId", null);
				this.pincode.setDescription("");
			}
		}

		fillCitydetails(pcProvince);

		logger.debug(Literal.LEAVING);
	}

	private void fillProvinceDetails(String country) {
		logger.debug(Literal.ENTERING);

		this.stateCode.setMandatoryStyle(true);
		this.stateCode.setModuleName("Province");
		this.stateCode.setValueColumn("CPProvince");
		this.stateCode.setDescColumn("CPProvinceName");
		this.stateCode.setValidateColumns(new String[] { "CPProvince" });

		Filter[] filters = new Filter[1];

		if (country == null || country.equals("")) {
			filters[0] = new Filter("CPCountry", null, Filter.OP_NOT_EQUAL);
		} else {
			filters[0] = new Filter("CPCountry", country, Filter.OP_EQUAL);
		}

		this.stateCode.setFilters(filters);
		logger.debug(Literal.LEAVING);
	}

	public void onFulfill$cityCode(Event event) {
		logger.debug(Literal.ENTERING);

		doRemoveValidation();
		doClearMessage();
		Object dataObject = cityCode.getObject();
		String cityValue = null;
		if (!(dataObject instanceof String)) {
			City details = (City) dataObject;
			if (details == null) {
				fillPindetails(null, null);
			}
			if (details != null) {
				this.stateCode.setValue(details.getPCProvince());
				this.stateCode.setDescription(details.getLovDescPCProvinceName());
				this.countryCode.setValue(details.getPCCountry());
				this.countryCode.setDescription(details.getLovDescPCCountryName());
				this.pincode.setValue("");
				this.pincode.setDescription("");
				cityValue = details.getPCCity();
				fillPindetails(cityValue, this.stateCode.getValue());
			} else {
				this.cityCode.setObject("");
				this.pincode.setObject("");
				this.pincode.setValue("", "");
				this.pincode.setAttribute("pinCodeId", null);
				this.pincode.setDescription("");
				this.stateCode.setErrorMessage("");
				this.countryCode.setErrorMessage("");
				fillPindetails(null, this.stateCode.getValue());
			}
		} else if ("".equals(dataObject)) {
			this.pincode.setValue("");
			this.pincode.setDescription("");
			this.stateCode.setObject("");
		}

		logger.debug(Literal.LEAVING);
	}

	private void fillCitydetails(String state) {
		logger.debug(Literal.ENTERING);

		this.cityCode.setModuleName("City");
		this.cityCode.setValueColumn("PCCity");
		this.cityCode.setDescColumn("PCCityName");
		this.cityCode.setValidateColumns(new String[] { "PCCity" });
		Filter[] filters1 = new Filter[1];

		if (state == null || state.isEmpty()) {
			filters1[0] = new Filter("PCProvince", null, Filter.OP_NOT_EQUAL);
		} else {
			filters1[0] = new Filter("PCProvince", state, Filter.OP_EQUAL);
		}

		this.cityCode.setFilters(filters1);

		logger.debug(Literal.LEAVING);
	}

	public void onFulfill$pincode(Event event) {
		logger.debug(Literal.ENTERING);

		Object dataObject = pincode.getObject();
		if (dataObject instanceof String) {

		} else {
			PinCode details = (PinCode) dataObject;

			if (details != null) {
				this.countryCode.setValue(details.getpCCountry());
				this.countryCode.setDescription(details.getLovDescPCCountryName());
				this.cityCode.setValue(details.getCity());
				this.cityCode.setDescription(details.getPCCityName());
				this.stateCode.setValue(details.getPCProvince());
				this.stateCode.setDescription(details.getLovDescPCProvinceName());
				this.cityCode.setErrorMessage("");
				this.stateCode.setErrorMessage("");
				this.countryCode.setErrorMessage("");
				Clients.clearWrongValue(pincode);
				this.pincode.setAttribute("pinCodeId", details.getPinCodeId());
				this.pincode.setValue(details.getPinCode());
				this.pincode.setDescription(details.getAreaName());
			}

		}
		Filter[] filters = new Filter[1];
		if (this.cityCode.getValue() != null && !this.cityCode.getValue().isEmpty()) {
			filters[0] = new Filter("City", this.cityCode.getValue(), Filter.OP_EQUAL);
		} else {
			filters[0] = new Filter("City", null, Filter.OP_NOT_EQUAL);
		}

		this.pincode.setFilters(filters);

		logger.debug(Literal.LEAVING);
	}

	private void fillPindetails(String id, String province) {
		logger.debug(Literal.ENTERING);

		this.pincode.setModuleName("PinCode");
		this.pincode.setValueColumn("PinCodeId");
		this.pincode.setDescColumn("AreaName");
		this.pincode.setValidateColumns(new String[] { "PinCodeId" });
		this.pincode.setErrorMessage("");
		Filter[] filters = new Filter[1];

		if (id != null) {
			filters[0] = new Filter("City", id, Filter.OP_EQUAL);
		} else if (province != null && !province.isEmpty()) {
			filters[0] = new Filter("PCProvince", province, Filter.OP_EQUAL);
		} else {
			filters[0] = new Filter("City", null, Filter.OP_NOT_EQUAL);
		}

		this.pincode.setFilters(filters);
		logger.debug(Literal.LEAVING);
	}

	private void doReadOnly() {
		logger.debug(Literal.ENTERING);

		this.gstNumber.setReadonly(true);
		this.address.setReadonly(true);
		this.addressLine1.setReadonly(true);
		this.addressLine2.setReadonly(true);
		this.addressLine3.setReadonly(true);
		this.addressLine4.setReadonly(true);
		this.countryCode.setReadonly(true);
		this.stateCode.setReadonly(true);
		this.cityCode.setReadonly(true);
		this.pincode.setReadonly(true);
		this.tin.setDisabled(true);
		this.tinName.setDisabled(true);
		this.tinAddress.setDisabled(true);

		logger.debug(Literal.ENTERING);
	}

	private void doEdit() {
		logger.debug(Literal.ENTERING);

		if (newRecord) {
			this.gstNumber.setReadonly(isReadOnly("GST_Details_Dialog_GSTNumber"));
		} else {
			this.btnCancel.setVisible(true);
			this.gstNumber.setReadonly(true);
		}
		this.address.setReadonly(isReadOnly("GST_Details_Dialog_Address"));
		this.addressLine1.setReadonly(isReadOnly("GST_Details_Dialog_Address"));
		this.addressLine2.setReadonly(isReadOnly("GST_Details_Dialog_Address"));
		this.addressLine3.setReadonly(isReadOnly("GST_Details_Dialog_Address"));
		this.addressLine4.setReadonly(isReadOnly("GST_Details_Dialog_Address"));
		this.countryCode.setReadonly(isReadOnly("GST_Details_Dialog_CountryCode"));
		this.stateCode.setReadonly(isReadOnly("GST_Details_Dialog_StateCode"));
		this.cityCode.setReadonly(isReadOnly("GST_Details_Dialog_CityCode"));
		this.pincode.setReadonly(isReadOnly("GST_Details_Dialog_Pincode"));
		this.tin.setDisabled(isReadOnly("GST_Details_Dialog_TIN"));
		this.tinName.setDisabled(isReadOnly("GST_Details_Dialog_TINName"));
		this.tinAddress.setDisabled(isReadOnly("GST_Details_Dialog_TINAddress"));

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.detail.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			if (newCustomer) {
				if (PennantConstants.MODULETYPE_ENQ.equals(this.moduleType)) {
					this.btnCtrl.setBtnStatus_New();
					this.btnSave.setVisible(false);
					btnCancel.setVisible(false);
				} else if (newRecord) {
					this.btnCtrl.setBtnStatus_Edit();
				} else {
					this.btnCtrl.setWFBtnStatus_Edit(newCustomer);
				}
			} else {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(true);
			}
		}

		logger.debug(Literal.LEAVING);
	}

	public boolean isReadOnly(String componentName) {
		boolean isCustomerWorkflow = false;
		if (customerDialogCtrl != null) {
			isCustomerWorkflow = customerDialogCtrl.getCustomerDetails().getCustomer().isWorkflow();
		}
		if (isWorkFlowEnabled() || isCustomerWorkflow) {
			return getUserWorkspace().isReadOnly(componentName);
		}

		return false;
	}

	public void onClick$btnSave(Event event) throws InterruptedException {
		logger.debug(Literal.ENTERING);
		doSave();
		logger.debug(Literal.LEAVING);
	}

	protected void doSave() {
		logger.debug(Literal.ENTERING);

		final GSTDetail gst = new GSTDetail();
		BeanUtils.copyProperties(this.detail, gst);
		boolean isNew = false;

		doSetValidation();
		doWriteComponentsToBean(gst);

		isNew = gst.isNewRecord();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(gst.getRecordType())) {
				gst.setVersion(gst.getVersion() + 1);
				if (isNew) {
					gst.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					gst.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					gst.setNewRecord(true);
				}
			}
		} else {
			if (newCustomer) {
				if (newRecord) {
					gst.setVersion(1);
					gst.setRecordType(PennantConstants.RCD_ADD);
				} else {
					tranType = PennantConstants.TRAN_UPD;
				}

				if (StringUtils.isBlank(gst.getRecordType())) {
					gst.setVersion(gst.getVersion() + 1);
					gst.setRecordType(PennantConstants.RCD_UPD);
				}

				if (gst.getRecordType().equals(PennantConstants.RCD_ADD) && newRecord) {
					tranType = PennantConstants.TRAN_ADD;
				} else if (gst.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
					tranType = PennantConstants.TRAN_UPD;
				}

			} else {
				gst.setVersion(gst.getVersion() + 1);
				if (isNew) {
					tranType = PennantConstants.TRAN_ADD;
				} else {
					tranType = PennantConstants.TRAN_UPD;
				}
			}
		}

		try {
			AuditHeader auditHeader = newGSTDetailProcess(gst, tranType);
			auditHeader = ErrorControl.showErrorDetails(this.window_gstDetailsDialog, auditHeader);
			int retValue = auditHeader.getProcessStatus();

			if (retValue == PennantConstants.porcessCONTINUE || retValue == PennantConstants.porcessOVERIDE) {
				customerDialogCtrl.doFillGstDetails(this.gstDetails);
				closeDialog();
			}

		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	private AuditHeader newGSTDetailProcess(GSTDetail gst, String tranType) {
		logger.debug(Literal.ENTERING);
		boolean recordAdded = false;

		AuditHeader auditHeader = getAuditHeader(gst, tranType);
		gstDetails = new ArrayList<>();
		String taxNumber = gst.getGstNumber();
		String panNumber = customerDialogCtrl.getCustcrcpr();

		String gstStateCode = "";
		Province province = this.provinceDAO.getProvinceById(gst.getCountryCode(), gst.getStateCode(), "");

		if (province != null) {
			gstStateCode = province.getTaxStateCode();
		}

		if (StringUtils.isNotBlank(panNumber)) {
			if (!panNumber.equalsIgnoreCase(taxNumber.substring(2, 12))) {
				auditHeader.setErrorDetails(
						ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "65024", null, null)));
			}
		}

		if (StringUtils.isNotBlank(gstStateCode)
				&& !StringUtils.equalsIgnoreCase(gstStateCode, taxNumber.substring(0, 2))) { // if GST State
			auditHeader.setErrorDetails(
					ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "65023", null, null)));
			return auditHeader;
		}

		String[] valueParm = new String[2];
		String[] errParm = new String[2];

		valueParm[0] = String.valueOf(gst.getStateCode());
		valueParm[1] = gst.getStateName();

		errParm[0] = PennantJavaUtil.getLabel("label_StateCode") + ":" + valueParm[0];
		errParm[1] = PennantJavaUtil.getLabel("label_LovDescPCProvinceName") + ":" + valueParm[1];

		List<GSTDetail> gstDtlsList = customerDialogCtrl.getGstDetailsList();

		if (CollectionUtils.isNotEmpty(gstDtlsList)) {
			for (GSTDetail gstDetail : gstDtlsList) {
				if (gst.getCustID() == gstDetail.getCustID()
						&& StringUtils.equals(gst.getStateCode(), gstDetail.getStateCode())) {

					if (newRecord) {
						auditHeader.setErrorDetails(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm),
								getUserWorkspace().getUserLanguage()));
						return auditHeader;
					}

					if (PennantConstants.TRAN_DEL.equals(tranType)) {
						String recordType = gst.getRecordType();
						if (PennantConstants.RECORD_TYPE_UPD.equals(recordType)) {
							gst.setRecordType(PennantConstants.RECORD_TYPE_DEL);
							recordAdded = true;
							gstDetails.add(gst);
						} else if (PennantConstants.RCD_ADD.equals(recordType)) {
							recordAdded = true;
						} else if (PennantConstants.RECORD_TYPE_NEW.equals(recordType)) {
							gst.setRecordType(PennantConstants.RECORD_TYPE_CAN);
							recordAdded = true;
							gstDetails.add(gst);
						} else if (PennantConstants.RECORD_TYPE_CAN.equals(recordType)) {
							recordAdded = true;
							List<GSTDetail> customerGST = customerDialogCtrl.getCustomerDetails().getGstDetailsList();
							for (GSTDetail details : customerGST) {
								if (gst.getCustID() == gstDetail.getCustID() && gst.getId() == (gstDetail.getId())) {
									gstDetails.add(details);
								}
							}
						}
					} else {
						if (!PennantConstants.TRAN_UPD.equals(tranType)) {
							gstDetails.add(gstDetail);
						}
					}
				} else {
					gstDetails.add(gstDetail);
				}
			}
		}

		if (!recordAdded) {
			gstDetails.add(gst);
		}

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	private AuditHeader getAuditHeader(GSTDetail gstDetail, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, gstDetail.getBefImage(), gstDetail);
		return new AuditHeader(getReference(), String.valueOf(gstDetail.getCustID()), null, null, auditDetail,
				gstDetail.getUserDetails(), getOverideMap());
	}

	private void doWriteComponentsToBean(GSTDetail gst) {
		logger.debug(Literal.ENTERING);

		List<WrongValueException> wve = new ArrayList<>();
		gst.setCustID(detail.getCustID());
		try {
			gst.setGstNumber(this.gstNumber.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			gst.setAddress(this.address.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			gst.setAddressLine1(this.addressLine1.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			gst.setAddressLine2(this.addressLine2.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			gst.setAddressLine3(this.addressLine3.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			gst.setAddressLine4(this.addressLine4.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			gst.setTin(this.tin.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			gst.setTinName(this.tinName.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			gst.setTinAddress(this.tinAddress.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			gst.setCountryCode(this.countryCode.getValidatedValue());
			gst.setCountryName(this.countryCode.getDescription());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			gst.setStateCode(this.stateCode.getValidatedValue());
			gst.setStateName(this.stateCode.getDescription());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			gst.setCityCode(this.cityCode.getValidatedValue());
			gst.setCityName(this.cityCode.getDescription());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (this.pincode.getAttribute("pinCodeId") == null) {
				throw new WrongValueException(this.pincode, Labels.getLabel("FIELD_IS_MAND",
						new String[] { Labels.getLabel("label_gstDetailsDialog_pincode.value") }));
			}

			gst.setPinCode(this.pincode.getValidatedValue());
			gst.setPinCodeName(this.pincode.getDescription());
			gst.setPinCodeId((long) this.pincode.getAttribute("pinCodeId"));
		} catch (WrongValueException we) {
			wve.add(we);
		}

		doRemoveValidation();

		boolean focus = false;
		if (wve.size() > 0) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
				Component component = wve.get(i).getComponent();
				if (!focus) {
					focus = setComponentFocus(component);
				}
			}
			throw new WrongValuesException(wvea);
		}

		gst.setRecordStatus(this.recordStatus.getValue());
		setDetail(gst);

		logger.debug(Literal.LEAVING);
	}

	private void doRemoveValidation() {
		this.gstNumber.setConstraint("");
		this.address.setConstraint("");
		this.addressLine1.setConstraint("");
		this.addressLine2.setConstraint("");
		this.addressLine3.setConstraint("");
		this.addressLine4.setConstraint("");
		this.cityCode.setConstraint("");
		this.countryCode.setConstraint("");
		this.pincode.setConstraint("");
		this.stateCode.setConstraint("");
	}

	private void doSetValidation() {
		logger.debug(Literal.ENTERING);
		doClearMessage();

		if (!this.gstNumber.isReadonly()) {
			this.gstNumber
					.setConstraint(new PTStringValidator(Labels.getLabel("label_gstDetailsDialog_gstNumber.value"),
							PennantRegularExpressions.REGEX_GSTIN, true));
		}

		if (!this.address.isReadonly()) {
			this.address.setConstraint(new PTStringValidator(Labels.getLabel("label_gstDetailsDialog_Address.value"),
					PennantRegularExpressions.REGEX_DESCRIPTION, true));
		}

		if (!this.addressLine1.isReadonly()) {
			this.addressLine1
					.setConstraint(new PTStringValidator(Labels.getLabel("label_gstDetailsDialog_AddressLine1.value"),
							PennantRegularExpressions.REGEX_DESCRIPTION, true));
		}

		if (!this.countryCode.isReadonly() && this.countryCode.isMandatory()) {
			this.countryCode.setConstraint(new PTStringValidator(
					Labels.getLabel("label_gstDetailsDialog_countryCode.value"), null, true, true));
		}

		if (!this.stateCode.isReadonly() && this.stateCode.isMandatory()) {
			this.stateCode.setConstraint(
					new PTStringValidator(Labels.getLabel("label_gstDetailsDialog_stateCode.value"), null, true, true));
		}

		if (!this.cityCode.isReadonly() && this.cityCode.isMandatory()) {
			this.cityCode.setConstraint(
					new PTStringValidator(Labels.getLabel("label_gstDetailsDialog_cityCode.value"), null, true, true));
		}
		if (!this.pincode.isReadonly() && this.pincode.isMandatory()) {
			this.pincode.setConstraint(
					new PTStringValidator(Labels.getLabel("label_gstDetailsDialog_pincode.value"), null, true, true));
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	protected void doClearMessage() {
		logger.debug(Literal.ENTERING);

		this.gstNumber.setErrorMessage("");
		this.address.setErrorMessage("");
		this.addressLine1.setErrorMessage("");
		this.addressLine2.setErrorMessage("");
		this.addressLine3.setErrorMessage("");
		this.addressLine4.setErrorMessage("");
		this.cityCode.setErrorMessage("");
		this.countryCode.setErrorMessage("");
		this.pincode.setErrorMessage("");
		this.stateCode.setErrorMessage("");

		logger.debug(Literal.LEAVING);
	}

	public void onClick$btnHelp(Event event) throws InterruptedException {
		logger.debug(Literal.ENTERING);
		MessageUtil.showHelpWindow(event, window_gstDetailsDialog);
		logger.debug(Literal.LEAVING);
	}

	public void onClick$btnEdit(Event event) {
		logger.debug(Literal.ENTERING);
		doEdit();
		logger.debug(Literal.LEAVING);
	}

	public void onClick$btnDelete(Event event) throws InterruptedException {
		logger.debug(Literal.ENTERING);
		doDelete();
		logger.debug(Literal.LEAVING);
	}

	private void doDelete() throws InterruptedException {
		logger.debug(Literal.ENTERING);

		final GSTDetail gst = new GSTDetail();
		BeanUtils.copyProperties(this.detail, gst);
		String tranType = PennantConstants.TRAN_WF;

		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> "
				+ Labels.getLabel("label_gstDetailsDialog_stateCode.value") + " : " + gst.getStateCode();

		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.isBlank(gst.getRecordType())) {
				gst.setVersion(gst.getVersion() + 1);
				gst.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				if (!isFinanceProcess && customerDialogCtrl != null
						&& customerDialogCtrl.getCustomerDetails().getCustomer().isWorkflow()) {
					gst.setNewRecord(true);
				}
				if (isWorkFlowEnabled()) {
					gst.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}

			try {
				tranType = PennantConstants.TRAN_DEL;
				AuditHeader auditHeader = newGSTDetailProcess(gst, tranType);
				auditHeader = ErrorControl.showErrorDetails(this.window_gstDetailsDialog, auditHeader);
				int retValue = auditHeader.getProcessStatus();
				if (retValue == PennantConstants.porcessCONTINUE || retValue == PennantConstants.porcessOVERIDE) {
					customerDialogCtrl.doFillGstDetails(this.gstDetails);
					closeDialog();
				}
			} catch (DataAccessException e) {
				MessageUtil.showError(e);
			}
		}

		logger.debug(Literal.LEAVING);
	}

	public void onClick$btnClose(Event event) {
		doClose(this.btnSave.isVisible());
	}

	public void setDetail(GSTDetail detail) {
		this.detail = detail;
	}

	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}

	public void setCustomerDialogCtrl(CustomerDialogCtrl customerDialogCtrl) {
		this.customerDialogCtrl = customerDialogCtrl;
	}

	public void setNewCustomer(boolean newCustomer) {
		this.newCustomer = newCustomer;
	}

	public void setProvinceDAO(ProvinceDAO provinceDAO) {
		this.provinceDAO = provinceDAO;
	}

}