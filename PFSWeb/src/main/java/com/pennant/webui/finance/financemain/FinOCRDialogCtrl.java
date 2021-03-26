package com.pennant.webui.finance.financemain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.CurrencyBox;
import com.pennant.ExtendedCombobox;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.finance.FinAdvancePayments;
import com.pennant.backend.model.finance.FinOCRCapture;
import com.pennant.backend.model.finance.FinOCRDetail;
import com.pennant.backend.model.finance.FinOCRHeader;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.ocrmaster.OCRDetail;
import com.pennant.backend.model.ocrmaster.OCRHeader;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.service.finance.FinOCRHeaderService;
import com.pennant.backend.service.systemmasters.OCRHeaderService;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.util.Constraint.PTDecimalValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.jdbc.search.Search;
import com.pennanttech.pennapps.jdbc.search.SearchProcessor;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.core.TableType;

public class FinOCRDialogCtrl extends GFCBaseCtrl<FinOCRHeader> {
	private static final long serialVersionUID = 4157448822555239535L;
	private static final Logger logger = LogManager.getLogger(FinOCRDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_FinOCRDialog;
	//OCR Definition Fields
	protected Listbox listBoxFinOCRSteps;
	protected Div ocrStepsDiv;
	protected Button btnNew_FinOCRStep;
	protected ExtendedCombobox ocrID;
	protected Textbox ocrDescription;
	protected Decimalbox customerPortion;
	protected Combobox ocrType;
	protected CurrencyBox totalDemand;
	protected CurrencyBox totalReceivable;
	//OCR capture Fields
	protected Textbox loanReference;
	protected CurrencyBox ocrCprTotReceivble;
	protected CurrencyBox ocrTotalDemand;
	protected CurrencyBox ocrTotalPaid;

	protected CurrencyBox tdTotalDemand;
	protected CurrencyBox tdTotalReceivable;

	protected Button btnNew_FinOCRCapture;
	protected Listbox listBoxOCRCapture;
	// For Dynamically calling of this Controller
	private Object financeMainDialogCtrl;
	private FinanceDetail financeDetail;
	private Component parent = null;
	private FinOCRHeader finOCRHeader;
	private List<FinOCRDetail> finOCRDetailList = new ArrayList<FinOCRDetail>();
	private String roleCode = "";
	private transient boolean newFinance;
	protected Groupbox finBasicdetails;
	private FinBasicDetailsCtrl finBasicDetailsCtrl;
	private String allowedRoles;
	private FinanceType financeType;
	private boolean definitionApproved = false;
	private String moduleName;
	private ArrayList<Object> headerList;
	private List<ValueLabel> applicableList = PennantStaticListUtil.getOCRApplicableList();
	private OCRHeaderService ocrHeaderService;
	private FinOCRHeaderService finOCRHeaderService;
	@Autowired
	private SearchProcessor searchProcessor;
	//Fin OCR Capture list
	private List<FinOCRCapture> finOCRCaptureList = new ArrayList<FinOCRCapture>();
	private int ccyFormatter = 0;

	private Tab tabOCRDefinition;
	private Tab tabOCRCapture;

	/**
	 * default constructor.<br>
	 */
	public FinOCRDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "FinOCRDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the zul-file is called with a parameter for a
	 * selected FinOCRDialog object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void onCreate$window_FinOCRDialog(ForwardEvent event) throws Exception {
		logger.debug(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(window_FinOCRDialog);

		try {
			if (event.getTarget().getParent() != null) {
				parent = event.getTarget().getParent();
			}

			if (arguments.containsKey("financeMainDialogCtrl")) {
				this.financeMainDialogCtrl = arguments.get("financeMainDialogCtrl");
				try {
					financeMainDialogCtrl.getClass().getMethod("setFinOCRDialogCtrl", this.getClass())
							.invoke(getFinanceMainDialogCtrl(), this);
				} catch (Exception e) {
					logger.error("Exception: ", e);
				}
				this.window_FinOCRDialog.setTitle("");
			}

			if (arguments.containsKey("ccyFormatter")) {
				ccyFormatter = (Integer) arguments.get("ccyFormatter");
			}

			if (arguments.containsKey("enqiryModule")) {
				this.enqiryModule = (Boolean) arguments.get("enqiryModule");
			}

			if (arguments.containsKey("definitionApproved")) {
				definitionApproved = (boolean) arguments.get("definitionApproved");
			}

			if (arguments.containsKey("roleCode")) {
				roleCode = (String) arguments.get("roleCode");
				getUserWorkspace().allocateRoleAuthorities(roleCode, this.pageRightName);
			}

			if (arguments.containsKey("financeDetail")) {
				setFinanceDetail((FinanceDetail) arguments.get("financeDetail"));
			}

			if (arguments.containsKey("allowedRoles")) {
				allowedRoles = (String) arguments.get("allowedRoles");
			}

			//moduleName
			if (arguments.containsKey("moduleName")) {
				this.moduleName = (String) arguments.get("moduleName");
			}

			if (arguments.containsKey("finHeaderList")) {
				headerList = (ArrayList<Object>) arguments.get("finHeaderList");
			}

			if (getFinanceDetail() != null) {
				financeType = getFinanceDetail().getFinScheduleData().getFinanceType();
				FinanceMain financeMain = getFinanceDetail().getFinScheduleData().getFinanceMain();
				OCRHeader ocrHeader = null;

				if (getFinanceDetail().getFinOCRHeader() != null) {
					if (financeMain != null && StringUtils.isNotEmpty(financeMain.getParentRef())) {
						this.totalDemand.setDisabled(true);
					}
					setFinOCRHeader(getFinanceDetail().getFinOCRHeader());
				}

				if (financeMain != null && StringUtils.isNotEmpty(financeMain.getParentRef())) {
					FinOCRHeader finOCRHeader = finOCRHeaderService
							.getApprovedFinOCRHeaderByRef(financeMain.getParentRef(), TableType.VIEW.getSuffix());
					ocrHeader = ocrHeaderService.getOCRHeaderByOCRId(finOCRHeader.getOcrID(),
							TableType.AVIEW.getSuffix());
					setFinOCRHeader(copyOCRHeaderProperties(ocrHeader));
					if (getFinanceDetail().getFinOCRHeader() != null
							&& StringUtils.isNotEmpty(getFinanceDetail().getFinOCRHeader().getRecordStatus())) {
						getFinOCRHeader().setNewRecord(false);
						getFinOCRHeader().setHeaderID(getFinanceDetail().getFinOCRHeader().getHeaderID());
						getFinOCRHeader().setTotalDemand(getFinanceDetail().getFinOCRHeader().getTotalDemand());
						getFinOCRHeader().getFinOCRCapturesList()
								.addAll(getFinanceDetail().getFinOCRHeader().getFinOCRCapturesList());
					}
					if ((finOCRHeader != null) && finOCRHeader.getTotalDemand() != null) {
						getFinOCRHeader().setTotalDemand(finOCRHeader.getTotalDemand());
					}
					this.totalDemand.setDisabled(true);
				} else if (financeType != null && StringUtils.isNotEmpty(financeType.getDefaultOCR())
						&& (getFinanceDetail().getFinOCRHeader() != null
								&& StringUtils.isEmpty(getFinanceDetail().getFinOCRHeader().getOcrID()))) {
					// get default OCR header details from loan type
					ocrHeader = ocrHeaderService.getOCRHeaderByOCRId(financeType.getDefaultOCR(),
							TableType.AVIEW.getSuffix());
					setFinOCRHeader(copyOCRHeaderProperties(ocrHeader));
					if (getFinanceDetail().getFinOCRHeader() != null
							&& StringUtils.isNotEmpty(getFinanceDetail().getFinOCRHeader().getRecordStatus())) {
						getFinOCRHeader().setNewRecord(false);
						getFinOCRHeader().setHeaderID(getFinanceDetail().getFinOCRHeader().getHeaderID());
						getFinOCRHeader().setTotalDemand(getFinanceDetail().getFinOCRHeader().getTotalDemand());
						getFinOCRHeader().getFinOCRCapturesList()
								.addAll(getFinanceDetail().getFinOCRHeader().getFinOCRCapturesList());
					}
				}
			}
			if (getFinOCRHeader() == null) {
				this.finOCRHeader = new FinOCRHeader();
				this.finOCRHeader.setNewRecord(true);
			}

			doLoadWorkFlow(this.finOCRHeader.isWorkflow(), this.finOCRHeader.getWorkflowId(),
					this.finOCRHeader.getNextTaskId());

			if (isWorkFlowEnabled() && !enqiryModule) {
				getUserWorkspace().allocateRoleAuthorities(roleCode, this.pageRightName);
			}

			doSetFieldProperties();
			doCheckRights();
			doShowDialog(finOCRHeader);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug(Literal.LEAVING + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug(Literal.ENTERING);
		this.ocrID.setMandatoryStyle(true);
		this.ocrID.setMaxlength(20);
		this.ocrID.setTextBoxWidth(150);
		this.ocrID.setModuleName("OCRHeader");
		this.ocrID.setValueColumn("OcrID");
		this.ocrID.setValidateColumns(new String[] { "OcrID" });
		//Filters for OCR Id as per the loan type
		if (financeType != null && !StringUtils.isEmpty(financeType.getAllowedOCRS())) {
			List<String> detailsList = Arrays.asList(financeType.getAllowedOCRS().split(","));
			this.ocrID.setFilters(new Filter[] { new Filter("OcrID", detailsList, Filter.OP_IN) });
		}
		this.ocrDescription.setMaxlength(100);
		this.customerPortion.setMaxlength(6);
		this.totalDemand.setProperties(true, ccyFormatter);
		this.totalReceivable.setDisabled(true);
		this.totalReceivable.setProperties(true, ccyFormatter);
		this.ocrStepsDiv.setVisible(false);
		this.listBoxFinOCRSteps.setVisible(false);
		this.loanReference.setDisabled(true);
		this.ocrCprTotReceivble.setDisabled(true);
		this.ocrCprTotReceivble.setProperties(true, ccyFormatter);
		this.ocrTotalDemand.setDisabled(true);
		this.ocrTotalDemand.setProperties(true, ccyFormatter);
		this.ocrTotalPaid.setDisabled(true);
		this.ocrTotalPaid.setProperties(true, ccyFormatter);
		this.tdTotalDemand.setDisabled(true);
		this.tdTotalReceivable.setDisabled(true);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Remove Error Messages for Fields
	 */

	@Override
	protected void doClearMessage() {
		logger.debug(Literal.ENTERING);
		logger.debug(Literal.LEAVING);
	}

	private void doCheckRights() {
		logger.debug(Literal.ENTERING);
		if (!enqiryModule) {
			this.btnNew_FinOCRStep.setVisible(!isReadOnly("FinOCRDialog_btnNew_FinOCRStep"));
			this.btnNew_FinOCRCapture.setVisible(!isReadOnly("FinOCRDialog_btnNew_FinOCRCapture"));
		} else {
			this.btnNew_FinOCRStep.setVisible(false);
			this.btnNew_FinOCRCapture.setVisible(false);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @throws InterruptedException
	 */
	public void doShowDialog(FinOCRHeader finOCRHeader) throws InterruptedException {
		logger.debug(Literal.ENTERING);

		try {
			if (!enqiryModule) {
				appendFinBasicDetails();
			}
			doEdit();
			doCheckEnquiry();
			doCheckDefApproved();
			doWriteBeanToComponents(finOCRHeader);
			this.listBoxFinOCRSteps.setHeight(((borderLayoutHeight - 80) / 2) - 100 + "px");
			this.listBoxOCRCapture.setHeight(borderLayoutHeight - 326 + "px");
			if (parent != null) {
				this.window_FinOCRDialog.setHeight(borderLayoutHeight - 75 + "px");
				parent.appendChild(this.window_FinOCRDialog);
			}
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param finOCRHeader
	 * 
	 */
	public void doWriteBeanToComponents(FinOCRHeader finOCRHeader) {
		logger.debug(Literal.ENTERING);

		fillComboBox(this.ocrType, finOCRHeader.getOcrType(), applicableList, "");
		this.ocrID.setValue(finOCRHeader.getOcrID());
		this.ocrDescription.setValue(finOCRHeader.getOcrDescription());
		this.customerPortion.setValue(finOCRHeader.getCustomerPortion());
		this.loanReference.setValue(financeDetail.getFinScheduleData().getFinanceMain().getFinReference());

		this.totalDemand.setValue(finOCRHeader.getTotalDemand());
		this.totalReceivable
				.setValue(getCurrentTranchAmount(finOCRHeader.getTotalDemand(), finOCRHeader.getCustomerPortion()));
		if (StringUtils.equals(PennantConstants.SEGMENTED_VALUE, finOCRHeader.getOcrType())) {
			this.ocrStepsDiv.setVisible(true);
			this.listBoxFinOCRSteps.setVisible(true);
		} else {
			this.ocrStepsDiv.setVisible(false);
			this.listBoxFinOCRSteps.setVisible(false);
		}

		//FinOCRStep Details
		doFillFinOCRStepDetails(finOCRHeader.getOcrDetailList());
		financeDetail.setFinOCRHeader(finOCRHeader);
		for (FinOCRCapture finOCRCapture : finOCRHeader.getFinOCRCapturesList()) {
			finOCRCapture.setDocImage(finOCRHeaderService.getDocumentManImage(finOCRCapture.getDocumentRef()));
		}
		//FinOCRStep Details
		doFillFinOCRCaptureDetails(finOCRHeader.getFinOCRCapturesList());
		logger.debug(Literal.LEAVING);
	}

	private void doCheckEnquiry() {
		if (enqiryModule) {
			this.btnNew_FinOCRStep.setVisible(false);
			readOnlyComponent(true, this.ocrID);
			readOnlyComponent(true, this.ocrDescription);
			readOnlyComponent(true, this.customerPortion);
			readOnlyComponent(true, this.ocrType);
			readOnlyComponent(true, this.totalDemand);
			readOnlyComponent(true, this.totalReceivable);
			//readOnlyComponent(true, this.splitApplicable);
			this.btnNew_FinOCRCapture.setVisible(false);

		}
	}

	private void doCheckDefApproved() {
		if (definitionApproved) {
			this.btnNew_FinOCRStep.setVisible(false);
			readOnlyComponent(true, this.ocrID);
			readOnlyComponent(true, this.ocrDescription);
			readOnlyComponent(true, this.customerPortion);
			readOnlyComponent(true, this.ocrType);
			readOnlyComponent(true, this.totalDemand);
			readOnlyComponent(true, this.totalReceivable);
			//readOnlyComponent(true, this.splitApplicable);
		}

	}

	/**
	 * Double click event for FinOCRStep item
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onFinOCRStepItemDoubleClicked(Event event) throws InterruptedException {
		logger.debug(Literal.ENTERING + event.toString());
		Clients.clearWrongValue(this.btnNew_FinOCRStep);

		Listitem listitem = this.listBoxFinOCRSteps.getSelectedItem();
		if (listitem != null && listitem.getAttribute("data") != null) {
			final FinOCRHeader aFinOCRHeader = new FinOCRHeader();
			BeanUtils.copyProperties(this.finOCRHeader, aFinOCRHeader);
			doWriteComponentsToBean(aFinOCRHeader);

			final FinOCRDetail aFinOCRDetail = (FinOCRDetail) listitem.getAttribute("data");

			if (!enqiryModule && isDeleteRecord(aFinOCRDetail)) {
				MessageUtil.showMessage(Labels.getLabel("common_NoMaintainance"));
				return;
			}

			aFinOCRDetail.setNewRecord(false);
			aFinOCRDetail.setWorkflowId(0);

			final HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("finOCRDetail", aFinOCRDetail);
			map.put("finOCRHeader", aFinOCRHeader);
			map.put("roleCode", roleCode);
			map.put("finOCRDialogCtrl", this);
			map.put("allowedRoles", allowedRoles);
			map.put("enqiryModule", enqiryModule);
			// call the ZUL-file with the parameters packed in a map
			try {
				Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/FinOCRStepDialog.zul", null, map);
			} catch (Exception e) {
				MessageUtil.showError(e);
			}
		}

		logger.debug(Literal.LEAVING + event.toString());
	}

	/**
	 * Double click event for FinOCRStep item
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onFinOCRCaptureItemDoubleClicked(Event event) throws InterruptedException {
		logger.debug(Literal.ENTERING + event.toString());
		Clients.clearWrongValue(this.btnNew_FinOCRCapture);
		Listitem listitem = this.listBoxOCRCapture.getSelectedItem();
		if (listitem != null && listitem.getAttribute("data") != null) {
			final FinOCRCapture aFinOCRCapture = (FinOCRCapture) listitem.getAttribute("data");
			if (!enqiryModule && isDeleteRecord(aFinOCRCapture)) {
				MessageUtil.showMessage(Labels.getLabel("common_NoMaintainance"));
				return;
			}
			aFinOCRCapture.setNewRecord(false);
			aFinOCRCapture.setWorkflowId(0);
			if (!enqiryModule) {
				financeDetail = getFinancedetailsFromBase();
			}
			final HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("finOCRCapture", aFinOCRCapture);
			map.put("financeDetail", financeDetail);
			map.put("roleCode", roleCode);
			map.put("allowedRoles", allowedRoles);
			map.put("finOCRHeader", getFinOCRHeaderData());
			map.put("ccyFormatter", ccyFormatter);
			map.put("finOCRDialogCtrl", this);
			map.put("enqiryModule", enqiryModule);
			// call the ZUL-file with the parameters packed in a map try {
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/FinOCRCaptureDialog.zul", null, map);
		}

		logger.debug(Literal.LEAVING + event.toString());
	}

	/**
	 * This method is for append finance basic details to respective parent tabs
	 */
	private void appendFinBasicDetails() {
		try {
			final HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("parentCtrl", this);
			map.put("finHeaderList", headerList);
			map.put("moduleName", moduleName);
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/FinBasicDetails.zul", this.finBasicdetails,
					map);
		} catch (Exception e) {
			logger.debug(e);
		}
	}

	public void doSetLabels(ArrayList<Object> finHeaderList) {
		getFinBasicDetailsCtrl().doWriteBeanToComponents(finHeaderList);
	}

	public boolean doSave(FinanceDetail financeDetail, Tab tab, boolean recSave, Radiogroup userAction) {
		logger.debug(Literal.ENTERING);
		this.userAction = userAction;
		doClearMessage();
		doSetValidation();
		final FinOCRHeader aFinOCRHeader = new FinOCRHeader();
		BeanUtils.copyProperties(this.finOCRHeader, aFinOCRHeader);
		aFinOCRHeader.setBefImage(this.finOCRHeader);
		ArrayList<WrongValueException> wve = doWriteComponentsToBean(aFinOCRHeader);
		if (!wve.isEmpty() && tab != null) {
			tab.setSelected(true);
		}

		showErrorDetails(wve, this.tabOCRDefinition);
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();

		aFinOCRHeader.setFinReference(financeMain.getFinReference());
		aFinOCRHeader.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aFinOCRHeader.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aFinOCRHeader.setUserDetails(getUserWorkspace().getLoggedInUser());
		financeDetail.setFinOCRHeader(aFinOCRHeader);

		BigDecimal demandAmt = BigDecimal.ZERO;
		BigDecimal ocrpaid = BigDecimal.ZERO;
		BigDecimal custPortion = aFinOCRHeader.getCustomerPortion();
		BigDecimal finAmount = BigDecimal.ZERO;
		BigDecimal totalDemandRaised = BigDecimal.ZERO;

		List<FinOCRCapture> captureList = aFinOCRHeader.getFinOCRCapturesList();
		if (captureList != null) {
			for (FinOCRCapture finOCRCapture : captureList) {
				demandAmt = demandAmt.add(finOCRCapture.getDemandAmount());
				ocrpaid = ocrpaid.add(finOCRCapture.getPaidAmount());
			}
			demandAmt = PennantApplicationUtil.formateAmount(demandAmt, ccyFormatter);
			ocrpaid = PennantApplicationUtil.formateAmount(ocrpaid, ccyFormatter);
		}
		if (StringUtils.isNotEmpty(financeMain.getParentRef())) {
			//Parent total demand amount
			FinOCRHeader finOCRHeader = finOCRHeaderService.getApprovedFinOCRHeaderByRef(financeMain.getParentRef(),
					TableType.AVIEW.getSuffix());
			if (finOCRHeader != null) {
				BigDecimal customerportion = getCurrentTranchAmount(finOCRHeader.getTotalDemand(),
						finOCRHeader.getCustomerPortion());
				totalDemandRaised = totalDemandRaised.add(finOCRHeader.getTotalDemand().subtract(customerportion));
			}
		} else {
			BigDecimal customerportion = getCurrentTranchAmount(aFinOCRHeader.getTotalDemand(),
					finOCRHeader.getCustomerPortion());
			totalDemandRaised = totalDemandRaised.add(aFinOCRHeader.getTotalDemand().subtract(customerportion));
		}

		finAmount = getDisbAmount(financeDetail);
		if (finAmount.compareTo(totalDemandRaised) > 0 && !recSave) {
			String msg = Labels.getLabel("OCR_DISB_AMOUNT_VALIDATION_MSG");
			MessageUtil.showMessage(msg);
			return false;
		}

		//rule for segmentations
		if (StringUtils.equals(this.ocrType.getSelectedItem().getValue(), PennantConstants.SEGMENTED_VALUE)) {
			BigDecimal cumCustSum = getCumCustSum(demandAmt);
			// Rule 1
			if (ocrpaid.compareTo(cumCustSum) < 0 && !recSave) {
				String msg = Labels.getLabel("OCR_NOT_SUFFICIENT_MSG");
				if (MessageUtil.confirm(msg) == MessageUtil.NO) {
					return false;
				} else {
					financeMain.setOcrDeviation(true);
					if (tab != null) {
						tab.setSelected(true);
					}
				}
			}
			// Rule 2
			if (finAmount.add(ocrpaid).compareTo(demandAmt) > 0 && !recSave) {
				String msg = Labels.getLabel("OCR_DISB_AMOUNT_CUM_VALIDATION_MSG");
				if (MessageUtil.confirm(msg) == MessageUtil.NO) {
					return false;
				} else {
					financeMain.setOcrDeviation(true);
					if (tab != null) {
						tab.setSelected(true);
					}
				}
			}
		}

		//validation 
		BigDecimal amountTobepaid = getCurrentTranchAmount(demandAmt, custPortion);

		if (StringUtils.equals(this.ocrType.getSelectedItem().getValue(), PennantConstants.PRORATA_VALUE)) {
			//Rule 1
			if (ocrpaid.compareTo(amountTobepaid) < 0 && !recSave) {
				String msg = Labels.getLabel("OCR_NOT_SUFFICIENT_MSG");
				if (MessageUtil.confirm(msg) == MessageUtil.NO) {
					return false;
				} else {
					financeMain.setOcrDeviation(true);
					if (tab != null) {
						tab.setSelected(true);
					}
				}
			}

			//rule 2
			if (finAmount.add(ocrpaid).compareTo(demandAmt) > 0 && !recSave) {
				String msg = Labels.getLabel("OCR_DISB_AMOUNT_CUM_VALIDATION_MSG");
				if (MessageUtil.confirm(msg) == MessageUtil.NO) {
					return false;
				} else {
					financeMain.setOcrDeviation(true);
					if (tab != null) {
						tab.setSelected(true);
					}
				}
			}
		}

		logger.debug(Literal.LEAVING);
		return true;
	}

	private BigDecimal getDisbAmount(FinanceDetail financeDetail) {
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();
		List<FinAdvancePayments> finAdvancePayments = financeDetail.getAdvancePaymentsList();
		BigDecimal finAmount = BigDecimal.ZERO;
		List<FinanceMain> fmlist = null;
		//Current Loan disbursment amount
		if (finAdvancePayments != null) {
			for (FinAdvancePayments disb : finAdvancePayments) {
				finAmount = finAmount.add(disb.getAmtToBeReleased());
			}
		} else {
			finAmount.add(financeMain.getFinAmount());
		}

		// Parent Disbursement amounts
		if (StringUtils.isNotEmpty(financeMain.getParentRef())) {
			// Parent Disbursement amounts
			List<FinAdvancePayments> disbursementObject = getFinAdvancePaymentsObject(financeMain.getParentRef());
			if (disbursementObject != null) {
				for (FinAdvancePayments disb : disbursementObject) {
					finAmount = finAmount.add(disb.getAmtToBeReleased());
				}
			} else {
				List<FinanceMain> finmain = getFinanceMain(financeMain.getFinReference());
				if (CollectionUtils.isNotEmpty(finmain)) {
					finAmount.add(finmain.get(0).getFinAmount());
				}
			}

			// List of childs having parent of given FinReference
			fmlist = getFinanceMainObject(financeMain.getFinReference());
			for (FinanceMain financemain : fmlist) {
				disbursementObject = getFinAdvancePaymentsObject(financemain.getFinReference());
				if (disbursementObject != null) {
					for (FinAdvancePayments disb : disbursementObject) {
						finAmount = finAmount.add(disb.getAmtToBeReleased());
					}
				} else {
					List<FinanceMain> finmain = getFinanceMain(financeMain.getFinReference());
					if (CollectionUtils.isNotEmpty(finmain)) {
						finAmount.add(finmain.get(0).getFinAmount());
					}
				}
			}

		}
		finAmount = PennantApplicationUtil.formateAmount(finAmount, ccyFormatter);
		return finAmount;
	}

	private BigDecimal getCumCustSum(BigDecimal demandAmt) {
		BigDecimal ocrtotalDemand = this.totalDemand.getActualValue();
		BigDecimal cumcustsum = BigDecimal.ZERO;
		BigDecimal cumfincsum = BigDecimal.ZERO;
		BigDecimal total = BigDecimal.ZERO;
		BigDecimal rem = BigDecimal.ZERO;
		boolean custr = false;
		boolean finc = false;
		for (FinOCRDetail finOCRDetail : getFinOCRDetailList()) {

			if (finOCRDetail.getCustomerContribution().compareTo(BigDecimal.ZERO) != 0) {
				BigDecimal cust = getCurrentTranchAmount(ocrtotalDemand, finOCRDetail.getCustomerContribution());
				cumcustsum = cumcustsum.add(cust);
				total = total.add(cust);

				if (total.compareTo(demandAmt) > 0) {
					custr = true;
					rem = total.subtract(demandAmt);
					break;
				}
			}
			if (finOCRDetail.getFinancerContribution().compareTo(BigDecimal.ZERO) != 0) {
				BigDecimal fin = getCurrentTranchAmount(ocrtotalDemand, finOCRDetail.getFinancerContribution());
				cumfincsum = cumfincsum.add(fin);
				total = total.add(fin);

				if (total.compareTo(demandAmt) > 0) {
					finc = true;
					rem = total.subtract(demandAmt);
					break;
				}
			}
		}
		if (custr) {
			cumcustsum = cumcustsum.subtract(rem);
		}
		if (finc) {
			cumfincsum = cumfincsum.subtract(rem);
		}
		return cumcustsum;
	}

	private BigDecimal getCumCustSum(FinanceDetail financeDetail, BigDecimal demandAmt) {
		FinOCRHeader aFinOCRHeader = financeDetail.getFinOCRHeader();
		BigDecimal ocrtotalDemand = aFinOCRHeader.getTotalDemand();
		BigDecimal cumcustsum = BigDecimal.ZERO;
		BigDecimal cumfincsum = BigDecimal.ZERO;
		BigDecimal total = BigDecimal.ZERO;
		BigDecimal rem = BigDecimal.ZERO;
		boolean custr = false;
		boolean finc = false;
		//BigDecimal disbAmount = getDisbAmount(financeDetail);
		for (FinOCRDetail finOCRDetail : aFinOCRHeader.getOcrDetailList()) {
			if (finOCRDetail.getCustomerContribution().compareTo(BigDecimal.ZERO) != 0) {
				BigDecimal cust = getCurrentTranchAmount(ocrtotalDemand, finOCRDetail.getCustomerContribution());
				cumcustsum = cumcustsum.add(cust);
				total = total.add(cust);

				if (total.compareTo(demandAmt) > 0) {
					custr = true;
					rem = total.subtract(demandAmt);
					break;
				}
			}
			if (finOCRDetail.getFinancerContribution().compareTo(BigDecimal.ZERO) != 0) {
				BigDecimal fin = getCurrentTranchAmount(ocrtotalDemand, finOCRDetail.getFinancerContribution());
				cumfincsum = cumfincsum.add(fin);
				total = total.add(fin);

				if (total.compareTo(demandAmt) > 0) {
					finc = true;
					rem = total.subtract(demandAmt);
					break;
				}
			}
		}
		if (custr) {
			cumcustsum = cumcustsum.subtract(rem);

		}
		if (finc) {
			cumfincsum = cumfincsum.subtract(rem);
		}
		return cumcustsum;
	}

	private List<FinAdvancePayments> getFinAdvancePaymentsObject(String parentRef) {
		Search search = new Search(FinAdvancePayments.class);
		search.addField("AmtToBeReleased");
		search.addTabelName("FinAdvancePayments_view");
		search.addFilter(new Filter("FinReference", parentRef, Filter.OP_EQUAL));
		List<FinAdvancePayments> list = searchProcessor.getResults(search);
		return list;
	}

	private List<FinanceMain> getFinanceMainObject(String parentRef) {
		logger.debug(Literal.LEAVING);
		Search search = new Search(FinanceMain.class);
		search.addField("FinAmount");
		search.addTabelName("FinanceMain_view");
		search.addFilter(new Filter("ParentRef", parentRef, Filter.OP_EQUAL));
		List<FinanceMain> list = searchProcessor.getResults(search);
		return list;
	}

	private List<FinanceMain> getFinanceMain(String finReference) {
		logger.debug(Literal.LEAVING);
		Search search = new Search(FinanceMain.class);
		search.addField("FinAmount");
		search.addTabelName("FinanceMain_view");
		search.addFilter(new Filter("finReference", finReference, Filter.OP_EQUAL));
		List<FinanceMain> list = searchProcessor.getResults(search);
		return list;
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param verification
	 * @return
	 */
	public ArrayList<WrongValueException> doWriteComponentsToBean(FinOCRHeader aFinOCRHeader) {
		logger.debug(Literal.ENTERING);
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			aFinOCRHeader.setOcrID(this.ocrID.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aFinOCRHeader.setOcrDescription(this.ocrDescription.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aFinOCRHeader.setCustomerPortion(this.customerPortion.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aFinOCRHeader.setTotalDemand(this.totalDemand.getActualValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aFinOCRHeader.setTotalReceivable(this.totalReceivable.getActualValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if ("#".equals(getComboboxValue(this.ocrType))) {
				if (!this.ocrType.isDisabled()) {
					throw new WrongValueException(this.ocrType, Labels.getLabel("STATIC_INVALID",
							new String[] { Labels.getLabel("label_FinOCRDialog_OCRApplicableOn.value") }));
				}
			} else {
				aFinOCRHeader.setOcrType(getComboboxValue(this.ocrType));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		//aFinOCRHeader.setSplitApplicable(this.splitApplicable.isChecked());
		//Fin OCR Step Details
		aFinOCRHeader.setOcrDetailList(getFinOCRDetailList());

		//Fin OCR Capture Details
		aFinOCRHeader.setFinOCRCapturesList(getFinOCRCaptureList());
		logger.debug(Literal.LEAVING);
		return wve;
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug(Literal.ENTERING);

		int finFormatter = CurrencyUtil.getFormat(getFinanceDetail().getFinScheduleData().getFinanceMain().getFinCcy());
		if (!this.ocrID.isReadonly()) {
			this.ocrID.setConstraint(
					new PTStringValidator(Labels.getLabel("label_FinOCRDialog_OCRID.value"), null, true));
		}

		if (!this.ocrDescription.isReadonly()) {
			this.ocrDescription.setConstraint(new PTStringValidator(
					Labels.getLabel("label_FinOCRDialog_OCRDescription.value"), null, true, false));
		}

		if (!this.customerPortion.isReadonly()) {
			this.customerPortion.setConstraint(new PTDecimalValidator(
					Labels.getLabel("label_FinOCRDialog_CustomerPortion.value"), PennantConstants.defaultCCYDecPos, true, false, 0, 100));
		}

		if (!this.totalDemand.isReadonly()) {
			this.totalDemand
					.setConstraint(new PTDecimalValidator(Labels.getLabel("label_FinOCRDialog_Totaldemand.value"),
							finFormatter, true, false, 1, Double.MAX_VALUE));
		}

		if (!this.totalReceivable.isReadonly()) {
			this.totalDemand.setConstraint(new PTStringValidator(
					Labels.getLabel("label_FinOCRDialog_TotalOCRReceivable.value"), null, true, false));
		}

		if (!this.totalDemand.isReadonly()) {
			this.totalDemand
					.setConstraint(new PTDecimalValidator(Labels.getLabel("label_FinOCRDialog_Totaldemand.value"),
							finFormatter, true, false, 1, Double.MAX_VALUE));
		}

		if (!this.totalReceivable.isReadonly()) {
			this.totalDemand.setConstraint(new PTStringValidator(
					Labels.getLabel("label_FinOCRDialog_TotalOCRReceivable.value"), null, true, false));
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the showErrorDetails method for .<br>
	 * displaying exceptions if occured
	 */
	private void showErrorDetails(ArrayList<WrongValueException> wve, Tab tab) {
		logger.debug(Literal.ENTERING);

		doRemoveValidation();

		if (!wve.isEmpty()) {
			logger.debug("Throwing occured Errors By using WrongValueException");
			if (tab != null) {
				tab.setSelected(true);
			}
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		logger.debug(Literal.LEAVING);
	}

	private void doRemoveValidation() {
		logger.debug(Literal.ENTERING);
		this.ocrID.setConstraint("");
		this.ocrDescription.setConstraint("");
		this.customerPortion.setConstraint("");
		this.ocrType.setConstraint("");
		logger.debug(Literal.LEAVING);

	}

	private FinOCRHeader copyOCRHeaderProperties(OCRHeader ocrHeader) {
		FinOCRHeader finOCRHeader = new FinOCRHeader();
		finOCRHeader.setNewRecord(true);
		List<FinOCRDetail> finOCRDetailList = new ArrayList<>();
		if (ocrHeader != null) {
			finOCRHeader.setOcrType(ocrHeader.getOcrType());
			finOCRHeader.setOcrID(ocrHeader.getOcrID());
			finOCRHeader.setOcrDescription(ocrHeader.getOcrDescription());
			finOCRHeader.setCustomerPortion(ocrHeader.getCustomerPortion());
			//finOCRHeader.setSplitApplicable(ocrHeader.isSplitApplicable());
			if (StringUtils.isBlank(finOCRHeader.getRecordType())) {
				finOCRHeader.setVersion(finOCRHeader.getVersion() + 1);
				finOCRHeader.setRecordType(PennantConstants.RECORD_TYPE_NEW);
			}
			//setting the work flow values for 
			if (!CollectionUtils.isEmpty(ocrHeader.getOcrDetailList())) {
				for (OCRDetail ocrDetail : ocrHeader.getOcrDetailList()) {
					FinOCRDetail finOCRDetail = new FinOCRDetail();
					finOCRDetail.setStepSequence(ocrDetail.getStepSequence());
					finOCRDetail.setContributor(ocrDetail.getContributor());
					finOCRDetail.setCustomerContribution(ocrDetail.getCustomerContribution());
					finOCRDetail.setFinancerContribution(ocrDetail.getFinancerContribution());
					finOCRDetail.setNewRecord(true);
					if (StringUtils.isBlank(finOCRDetail.getRecordType())) {
						finOCRDetail.setVersion(finOCRDetail.getVersion() + 1);
						finOCRDetail.setRecordType(PennantConstants.RCD_ADD);
					}
					finOCRDetailList.add(finOCRDetail);
				}
			}
			finOCRHeader.setOcrDetailList(finOCRDetailList);
		}
		return finOCRHeader;

	}

	/**
	 * Called when changing the value of the text box
	 * 
	 * @param event
	 * @throws InterruptedException
	 * @throws WrongValueException
	 */
	public void onValueChange$totalDemand(Event event) throws Exception {
		logger.trace(Literal.ENTERING);
		//this.totalReceivable.setValue((totalDemand.getValue()*customerPortion.getValue())/100);
		this.totalReceivable
				.setValue(getCurrentTranchAmount(this.totalDemand.getValidateValue(), this.customerPortion.getValue()));
		logger.trace(Literal.LEAVING);
	}

	/**
	 * Called when changing the value of the text box
	 * 
	 * @param event
	 * @throws InterruptedException
	 * @throws WrongValueException
	 */
	public void onFulfill$totalDemand(Event event) throws Exception {
		logger.trace(Literal.ENTERING);
		//this.totalReceivable.setValue((totalDemand.getValue()*customerPortion.getValue())/100);
		this.totalReceivable
				.setValue(getCurrentTranchAmount(this.totalDemand.getActualValue(), this.customerPortion.getValue()));
		logger.trace(Literal.LEAVING);
	}

	/**
	 * This method will set the data from FinOCRHeader bean to components
	 * 
	 * @param event
	 */
	public void onFulfill$ocrID(Event event) {
		logger.debug(Literal.ENTERING);
		OCRHeader ocrHeader = (OCRHeader) this.ocrID.getObject();
		FinOCRHeader finOCRHeader = new FinOCRHeader();
		if (ocrHeader != null) {
			//render the Master OCR details in loan queue
			ocrHeader = ocrHeaderService.getApprovedOCRHeader(ocrHeader.getHeaderID());
			setFinOCRHeader(copyOCRHeaderProperties(ocrHeader));
			doWriteBeanToComponents(getFinOCRHeader());
		} else {
			doWriteBeanToComponents(finOCRHeader);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * This method will render the ocr steps
	 * 
	 * @param finOCRDetailList
	 */
	public void doFillFinOCRStepDetails(List<FinOCRDetail> finOCRDetailList) {
		logger.debug(Literal.ENTERING);
		BigDecimal totalCust = BigDecimal.ZERO;
		BigDecimal totalFin = BigDecimal.ZERO;
		this.listBoxFinOCRSteps.getItems().clear();
		setFinOCRDetailList(finOCRDetailList);
		if (finOCRDetailList != null && !finOCRDetailList.isEmpty()) {
			Collections.sort(finOCRDetailList);
			for (FinOCRDetail detail : finOCRDetailList) {
				Listitem item = new Listitem();
				Listcell lc;

				lc = new Listcell(String.valueOf(detail.getStepSequence()));
				lc.setParent(item);
				//skipping the cancel and DELETE records
				if (!PennantConstants.RCD_DEL.equalsIgnoreCase(detail.getRecordType())
						&& !PennantConstants.RECORD_TYPE_CAN.equalsIgnoreCase(detail.getRecordType())) {
					totalCust = totalCust.add(detail.getCustomerContribution());
					totalFin = totalFin.add(detail.getFinancerContribution());
				}

				String custContribution = "--";
				if (detail.getCustomerContribution().compareTo(BigDecimal.ZERO) > 0) {
					custContribution = String.valueOf(detail.getCustomerContribution()).concat("%");
				}
				lc = new Listcell(custContribution);
				lc.setParent(item);

				String finContribution = "--";
				if (detail.getFinancerContribution().compareTo(BigDecimal.ZERO) > 0) {
					finContribution = String.valueOf(detail.getFinancerContribution()).concat("%");
				}

				lc = new Listcell(finContribution);
				lc.setParent(item);

				lc = new Listcell(PennantJavaUtil.getLabel(detail.getRecordStatus()));
				lc.setParent(item);

				lc = new Listcell(PennantJavaUtil.getLabel(detail.getRecordType()));
				lc.setParent(item);

				item.setAttribute("data", detail);
				ComponentsCtrl.applyForward(item, "onDoubleClick=onFinOCRStepItemDoubleClicked");
				this.listBoxFinOCRSteps.appendChild(item);
			}
			//group total
			if (listBoxFinOCRSteps != null && listBoxFinOCRSteps.getItems().size() > 0) {
				Listitem item = new Listitem();
				Listcell lc = new Listcell(Labels.getLabel("listheader_AdvancePayments_GrandTotal.label"));
				lc.setStyle("font-weight:bold");
				lc.setParent(item);

				lc = new Listcell(String.valueOf(totalCust).concat("%"));
				lc.setParent(item);

				lc = new Listcell(String.valueOf(totalFin).concat("%"));
				lc.setParent(item);

				lc = new Listcell();
				lc.setParent(item);

				lc = new Listcell();
				lc.setParent(item);
				this.listBoxFinOCRSteps.appendChild(item);
			}
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * This method will redirect to FinOCRCapture Dialog while click on new button
	 */
	public void onClick$btnNew_FinOCRCapture(Event event) throws InterruptedException {
		logger.debug(Literal.ENTERING + event.toString());
		Clients.clearWrongValue(this.btnNew_FinOCRCapture);
		final FinOCRCapture aFinOCRCapture = new FinOCRCapture();
		aFinOCRCapture.setNewRecord(true);
		aFinOCRCapture.setWorkflowId(0);
		financeDetail = getFinancedetailsFromBase();
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("finOCRCapture", aFinOCRCapture);
		map.put("financeDetail", financeDetail);
		map.put("finOCRHeader", getFinOCRHeaderData());
		map.put("roleCode", roleCode);
		map.put("allowedRoles", allowedRoles);
		map.put("ccyFormatter", ccyFormatter);
		map.put("finOCRDialogCtrl", this);
		// call the ZUL-file with the parameters packed in a map try {
		Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/FinOCRCaptureDialog.zul", null, map);
		logger.debug(Literal.LEAVING + event.toString());
	}

	private FinOCRHeader getFinOCRHeaderData() {
		if (finOCRHeader != null) {
			finOCRHeader.setCustomerPortion(this.customerPortion.getValue());
		}
		return finOCRHeader;

	}

	/**
	 * Validations for OCR Steps on ocrType change event
	 */
	private void doCheckFinOCRStepDetails() {
		if (StringUtils.equals(this.ocrType.getSelectedItem().getValue(), PennantConstants.SEGMENTED_VALUE)) {
			this.ocrStepsDiv.setVisible(true);
			this.listBoxFinOCRSteps.setVisible(true);
		} else {
			if (doCheckOCRStepsDeleted()) {
				MessageUtil.showError(Labels.getLabel("label_FinOCRDialog_error"));
				//this condition is for switching segmented to prorata 
				fillComboBox(this.ocrType, PennantConstants.SEGMENTED_VALUE, applicableList, "");
				return;
			}
			this.ocrStepsDiv.setVisible(false);
			this.listBoxFinOCRSteps.setVisible(false);
		}
	}

	private boolean doCheckOCRStepsDeleted() {
		if (!CollectionUtils.isEmpty(getFinOCRDetailList())) {
			for (FinOCRDetail finOCRDetail : getFinOCRDetailList()) {
				//skipping the cancel and DELETE records
				if (!PennantConstants.RECORD_TYPE_DEL.equalsIgnoreCase(finOCRDetail.getRecordType())
						&& !PennantConstants.RECORD_TYPE_CAN.equalsIgnoreCase(finOCRDetail.getRecordType())) {
					return true;
				}
			}

		}
		return false;
	}

	/**
	 * onCheck Event For ocrType Combobox
	 */
	public void onSelect$ocrType(Event event) {
		logger.debug(Literal.ENTERING + event.toString());
		doCheckFinOCRStepDetails();
		logger.debug(Literal.LEAVING + event.toString());
	}

	public static boolean isDeleteRecord(AbstractWorkflowEntity abstractWorkflowEntity) {
		if (StringUtils.equals(PennantConstants.RECORD_TYPE_CAN, abstractWorkflowEntity.getRecordType())
				|| StringUtils.equals(PennantConstants.RECORD_TYPE_DEL, abstractWorkflowEntity.getRecordType())) {
			return true;
		}
		return false;
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug(Literal.ENTERING);

		if (finOCRHeader.isNewRecord()) {
			this.ocrID.setReadonly(false);
		} else {
			this.ocrID.setReadonly(true);
		}

		this.ocrDescription.setReadonly(!getUserWorkspace().isAllowed("FinOCRDialog_ocrDescription"));
		this.customerPortion.setReadonly(!getUserWorkspace().isAllowed("FinOCRDialog_customerPortion"));
		this.totalDemand.setReadonly(!getUserWorkspace().isAllowed("FinOCRDialog_customerPortion"));
		this.totalReceivable.setReadonly(!getUserWorkspace().isAllowed("FinOCRDialog_customerPortion"));
		this.ocrType.setDisabled(!getUserWorkspace().isAllowed("FinOCRDialog_ocrApplicableOn"));
		logger.debug(Literal.LEAVING);
	}

	/**
	 * This method will redirect to FinOCRStep Dialog while click on new button
	 */
	public void onClick$btnNew_FinOCRStep(Event event) throws InterruptedException {
		logger.debug(Literal.ENTERING + event.toString());
		Clients.clearWrongValue(this.btnNew_FinOCRStep);
		//with out selecting ocr header 
		if (StringUtils.isEmpty(this.finOCRHeader.getOcrID())) {
			doWriteComponentsToBean(this.finOCRHeader);
		}
		final FinOCRHeader aFinOCRHeader = new FinOCRHeader();
		BeanUtils.copyProperties(this.finOCRHeader, aFinOCRHeader);
		doWriteComponentsToBean(aFinOCRHeader);
		final FinOCRDetail aFinOCRDetail = new FinOCRDetail();
		aFinOCRDetail.setNewRecord(true);
		aFinOCRDetail.setWorkflowId(0);

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("finOCRDetail", aFinOCRDetail);
		map.put("finOCRHeader", aFinOCRHeader);
		map.put("roleCode", roleCode);
		map.put("finOCRDialogCtrl", this);
		map.put("allowedRoles", allowedRoles);

		// call the ZUL-file with the parameters packed in a map try {
		Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/FinOCRStepDialog.zul", null, map);
		logger.debug(Literal.LEAVING + event.toString());
	}

	private FinanceDetail getFinancedetailsFromBase() {
		try {
			if (financeMainDialogCtrl != null) {
				return (FinanceDetail) financeMainDialogCtrl.getClass().getMethod("getFinanceDetail")
						.invoke(financeMainDialogCtrl);
			}

		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
		return null;

	}
	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public void setFinanceMainDialogCtrl(Object financeMainDialogCtrl) {
		this.financeMainDialogCtrl = financeMainDialogCtrl;
	}

	public Object getFinanceMainDialogCtrl() {
		return financeMainDialogCtrl;
	}

	public boolean isNewFinance() {
		return newFinance;
	}

	public void setNewFinance(boolean newFinance) {
		this.newFinance = newFinance;
	}

	public void setFinanceDetail(FinanceDetail financeDetail) {
		this.financeDetail = financeDetail;
	}

	public FinanceDetail getFinanceDetail() {
		return financeDetail;
	}

	public FinBasicDetailsCtrl getFinBasicDetailsCtrl() {
		return finBasicDetailsCtrl;
	}

	public void setFinBasicDetailsCtrl(FinBasicDetailsCtrl finBasicDetailsCtrl) {
		this.finBasicDetailsCtrl = finBasicDetailsCtrl;
	}

	public FinOCRHeader getFinOCRHeader() {
		return finOCRHeader;
	}

	public void setFinOCRHeader(FinOCRHeader finOCRHeader) {
		this.finOCRHeader = finOCRHeader;
	}

	public OCRHeaderService getOcrHeaderService() {
		return ocrHeaderService;
	}

	public void setOcrHeaderService(OCRHeaderService ocrHeaderService) {
		this.ocrHeaderService = ocrHeaderService;
	}

	public FinOCRHeaderService getFinOCRHeaderService() {
		return finOCRHeaderService;
	}

	public void setFinOCRHeaderService(FinOCRHeaderService finOCRHeaderService) {
		this.finOCRHeaderService = finOCRHeaderService;
	}

	public List<FinOCRDetail> getFinOCRDetailList() {
		return finOCRDetailList;
	}

	public void setFinOCRDetailList(List<FinOCRDetail> finOCRDetailList) {
		this.finOCRDetailList = finOCRDetailList;
	}

	public List<FinOCRCapture> getFinOCRCaptureList() {
		return finOCRCaptureList;
	}

	public void setFinOCRCaptureList(List<FinOCRCapture> finOCRCaptureList) {
		this.finOCRCaptureList = finOCRCaptureList;
	}

	public void doFillFinOCRCaptureDetails(List<FinOCRCapture> finOCRCaptureList) {
		logger.debug(Literal.ENTERING);
		this.listBoxOCRCapture.getItems().clear();
		setFinOCRCaptureList(finOCRCaptureList);
		if (CollectionUtils.isNotEmpty(finOCRCaptureList)) {
			Collections.sort(finOCRCaptureList);
			BigDecimal ocrTotReceiveble = BigDecimal.ZERO;
			BigDecimal ocrTotalDemand = BigDecimal.ZERO;
			BigDecimal ocrTotalpaid = BigDecimal.ZERO;

			for (FinOCRCapture detail : finOCRCaptureList) {
				Listitem item = new Listitem();
				Listcell lc;

				lc = new Listcell(String.valueOf(detail.getDisbSeq()));//1
				lc.setParent(item);

				lc = new Listcell(PennantApplicationUtil.amountFormate(detail.getDemandAmount(), ccyFormatter));//2
				lc.setParent(item);
				ocrTotalDemand = ocrTotalDemand.add(detail.getDemandAmount());
				lc = new Listcell(PennantApplicationUtil.amountFormate(detail.getPaidAmount(), ccyFormatter));
				lc.setParent(item);
				ocrTotalpaid = ocrTotalpaid.add(detail.getPaidAmount());

				lc = new Listcell(DateUtil.format(detail.getReceiptDate(), DateFormat.SHORT_DATE));
				lc.setParent(item);

				lc = new Listcell(detail.getRemarks());
				lc.setParent(item);

				lc = new Listcell(PennantJavaUtil.getLabel(detail.getRecordStatus()));
				lc.setParent(item);

				lc = new Listcell(PennantJavaUtil.getLabel(detail.getRecordType()));
				lc.setParent(item);

				item.setAttribute("data", detail);
				ComponentsCtrl.applyForward(item, "onDoubleClick=onFinOCRCaptureItemDoubleClicked");
				this.listBoxOCRCapture.appendChild(item);

				this.ocrTotalDemand.setValue(PennantApplicationUtil.formateAmount(ocrTotalDemand, ccyFormatter));
				this.ocrTotalPaid.setValue(PennantApplicationUtil.formateAmount(ocrTotalpaid, ccyFormatter));
			}
			if (this.totalDemand.getActualValue().compareTo(BigDecimal.ZERO) > 0) {
				financeDetail.getFinOCRHeader().setTotalDemand(totalDemand.getActualValue());
			}
			if (StringUtils.equals(financeDetail.getFinOCRHeader().getOcrType(), PennantConstants.SEGMENTED_VALUE)) {
				FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();
				if (financeMain != null && StringUtils.isNotEmpty(financeMain.getParentRef())
						&& getFinanceDetail().getFinOCRHeader() == null) {
					//BigDecimal totalDemandRaised =BigDecimal.ZERO;
					if (StringUtils.isNotEmpty(financeMain.getParentRef())) {
						//Parent total demand amount
						FinOCRHeader finOCRHeader = finOCRHeaderService
								.getApprovedFinOCRHeaderByRef(financeMain.getParentRef(), TableType.VIEW.getSuffix());
						BigDecimal parentocrpaid = BigDecimal.ZERO;
						BigDecimal parentdemandAmt = BigDecimal.ZERO;
						BigDecimal parentfinancierpaid = BigDecimal.ZERO;
						BigDecimal cumcustsum = BigDecimal.ZERO;
						BigDecimal cumfincsum = BigDecimal.ZERO;
						BigDecimal tdOCRPaid = BigDecimal.ZERO;

						for (FinOCRCapture cpr : finOCRHeader.getFinOCRCapturesList()) {
							parentocrpaid = parentocrpaid.add(cpr.getPaidAmount());
							parentdemandAmt = parentdemandAmt.add(cpr.getDemandAmount());
						}
						parentocrpaid = PennantApplicationUtil.formateAmount(parentocrpaid, ccyFormatter);
						parentdemandAmt = PennantApplicationUtil.formateAmount(parentdemandAmt, ccyFormatter);
						parentfinancierpaid = parentdemandAmt.subtract(parentocrpaid);
						ocrTotalDemand = PennantApplicationUtil.formateAmount(ocrTotalDemand, ccyFormatter);
						ocrTotalDemand = ocrTotalDemand.add(parentdemandAmt);
						cumcustsum = getCumCustSum(financeDetail, ocrTotalDemand);
						cumfincsum = ocrTotalDemand.subtract(cumcustsum);
						cumcustsum = cumcustsum.subtract(parentocrpaid);
						tdOCRPaid = parentocrpaid.add(PennantApplicationUtil.formateAmount(ocrTotalpaid, ccyFormatter));
						parentocrpaid = parentocrpaid.add(cumcustsum);
						cumfincsum = cumfincsum.subtract(parentfinancierpaid);
						parentfinancierpaid = parentfinancierpaid.add(cumfincsum);
						this.ocrCprTotReceivble.setValue(cumcustsum);
						this.tdTotalDemand.setValue(parentfinancierpaid.add(parentocrpaid));
						this.tdTotalReceivable.setValue(tdOCRPaid);
					}

				} else {
					ocrTotReceiveble = ocrTotReceiveble.add(getCumCustSum(financeDetail,
							PennantApplicationUtil.formateAmount(ocrTotalDemand, ccyFormatter)));
					this.ocrCprTotReceivble.setValue(ocrTotReceiveble);

					if (financeMain != null && StringUtils.isNotEmpty(financeMain.getParentRef())) {
						//Parent total demand amount
						FinOCRHeader finOCRHeader = finOCRHeaderService
								.getApprovedFinOCRHeaderByRef(financeMain.getParentRef(), TableType.VIEW.getSuffix());
						BigDecimal parentocrpaid = BigDecimal.ZERO;
						BigDecimal parentdemandAmt = BigDecimal.ZERO;
						BigDecimal parentfinancierpaid = BigDecimal.ZERO;
						BigDecimal cumcustsum = BigDecimal.ZERO;
						BigDecimal cumfincsum = BigDecimal.ZERO;
						BigDecimal tdOCRPaid = BigDecimal.ZERO;

						for (FinOCRCapture cpr : finOCRHeader.getFinOCRCapturesList()) {
							parentocrpaid = parentocrpaid.add(cpr.getPaidAmount());
							parentdemandAmt = parentdemandAmt.add(cpr.getDemandAmount());
						}
						parentocrpaid = PennantApplicationUtil.formateAmount(parentocrpaid, ccyFormatter);
						parentdemandAmt = PennantApplicationUtil.formateAmount(parentdemandAmt, ccyFormatter);
						parentfinancierpaid = parentdemandAmt.subtract(parentocrpaid);
						ocrTotalDemand = PennantApplicationUtil.formateAmount(ocrTotalDemand, ccyFormatter);
						ocrTotalDemand = ocrTotalDemand.add(parentdemandAmt);
						cumcustsum = getCumCustSum(financeDetail, ocrTotalDemand);
						cumfincsum = ocrTotalDemand.subtract(cumcustsum);
						cumcustsum = cumcustsum.subtract(parentocrpaid);
						tdOCRPaid = parentocrpaid.add(PennantApplicationUtil.formateAmount(ocrTotalpaid, ccyFormatter));
						parentocrpaid = parentocrpaid.add(cumcustsum);
						cumfincsum = cumfincsum.subtract(parentfinancierpaid);
						parentfinancierpaid = parentfinancierpaid.add(cumfincsum);

						this.tdTotalDemand.setValue(parentfinancierpaid.add(parentocrpaid));
						this.tdTotalReceivable.setValue(tdOCRPaid);
					}
				}
			} else {
				ocrTotReceiveble = ocrTotReceiveble.add(
						getCurrentTranchAmount(ocrTotalDemand, financeDetail.getFinOCRHeader().getCustomerPortion()));
				this.ocrCprTotReceivble.setValue(PennantApplicationUtil.formateAmount(ocrTotReceiveble, ccyFormatter));
			}

		}
	}

	private BigDecimal getCurrentTranchAmount(BigDecimal demand, BigDecimal customerPortion) {
		BigDecimal amount = BigDecimal.ZERO;
		if (finOCRHeader != null) {
			amount = demand.multiply(customerPortion).divide(new BigDecimal(100), ccyFormatter, RoundingMode.HALF_DOWN);
		}
		return amount;
	}
}
