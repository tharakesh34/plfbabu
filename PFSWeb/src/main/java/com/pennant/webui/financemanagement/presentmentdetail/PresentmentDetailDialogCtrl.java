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
 * * FileName : presentmentHeaderDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 01-05-2017 * *
 * Modified Date : 01-05-2017 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 01-05-2017 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.financemanagement.presentmentdetail;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.zkoss.spring.SpringUtil;
import org.zkoss.util.media.Media;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.A;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobutton;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.app.constants.LengthConstants;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.PathUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.financemanagement.PresentmentDetailService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.backend.util.RepayConstants;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennant.pff.mandate.InstrumentType;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.pagging.PagedListWrapper;
import com.pennanttech.pennapps.core.DocType;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.MediaUtil;
import com.pennanttech.pennapps.jdbc.DataType;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.jdbc.search.SearchResult;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.presentment.model.PresentmentDetail;
import com.pennanttech.pff.presentment.model.PresentmentHeader;

/**
 * This is the controller class for the /WEB-INF/pages/financemanagement/PresentmentHeader/presentmentDetailDialog.zul
 * file. <br>
 */
public class PresentmentDetailDialogCtrl extends GFCBaseCtrl<PresentmentHeader> {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(PresentmentDetailDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_PresentmentHeaderDialog;
	private PresentmentHeader presentmentHeader;
	protected Borderlayout borderlayoutPresentmentHeader;

	protected Button btn_AddExlude;
	protected Button btn_AddInclude;
	protected Button btn_ExcludeAll;
	protected Button btn_IncludeAll;
	protected Listbox listBox_Include;
	protected Listbox listBox_ManualExclude;
	protected Listbox listBox_AutoExclude;
	protected ExtendedCombobox partnerBank;
	protected Button excludeUploadBtn;
	protected Textbox txtFileName;
	private Media media = null;
	protected Textbox uploadedfileName;
	protected Combobutton addTo;
	protected Label window_title;

	protected Label label_PresentmentReference;
	protected Label label_PresentmentStatus;

	// db Status fields
	protected Grid dBStatusGrid;
	protected Grid searchGrid;
	protected Label label_TotalPresentments;
	protected Label label_SuccessPresentments;
	protected Label label_FailedPresentments;

	protected Listheader listheader_PresentmentDetail_Description;

	protected Tab includeTab;
	protected Tab manualExcludeTab;
	protected Tab autoExcludeTab;
	private boolean isRepresentment;
	protected Listheader listHeader_Include_BankName;
	protected Listheader listHeader_Include_PrvsBatchNumb;
	protected Listheader listHeader_ManualExclude_BankName;
	protected Listheader listHeader_ManualExclude_PrvsBatchNumb;
	protected Listheader listHeader_AutoExclude_BankName;
	protected Listheader listHeader_AutoExclude_PrvsBatchNumb;
	protected Listheader listheaderPresentmentDetailAction;
	protected Label label_PresentmentDetailList_Status;

	private transient PresentmentDetailListCtrl presentmentDetailListCtrl;
	private transient PresentmentDetailService presentmentDetailService;

	private String moduleType;
	private boolean isvalidData = true;

	protected JdbcSearchObject<PresentmentDetail> parameterSearchObject;
	private PagedListWrapper<PresentmentDetail> presentmentDetailPagedListWrapper;
	private PagedListService pagedListService;
	protected Paging pagingIncludeList;
	protected Paging pagingManualExcludeList;
	protected Paging pagingAutoExcludeList;

	protected A sampleFileDownload;
	protected Textbox insertFinReference;
	protected org.zkoss.zul.Row PresentmentIncludeExcludeChanges;
	private FinanceMainDAO financeMainDAO;

	List<Long> includeList = new ArrayList<>();
	List<Long> excludeList = new ArrayList<>();
	List<Long> allIncludeIds = new ArrayList<>();
	List<Long> allExcludeIds = new ArrayList<>();

	/**
	 * default constructor.<br>
	 */
	public PresentmentDetailDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "PresentmentDetailDialog";
	}

	@Override
	protected String getReference() {
		return String.valueOf(this.presentmentHeader.getId());
	}

	/**
	 * 
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 * @throws Exception
	 */
	public void onCreate$window_PresentmentHeaderDialog(Event event) throws Exception {
		logger.debug(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(window_PresentmentHeaderDialog);
		try {
			// Get the required arguments.
			this.presentmentHeader = (PresentmentHeader) arguments.get("presentmentHeader");
			this.moduleType = (String) arguments.get("moduleType");
			this.presentmentDetailListCtrl = (PresentmentDetailListCtrl) arguments.get("presentmentDetailListCtrl");

			if (this.presentmentHeader == null) {
				throw new Exception(Labels.getLabel("error.unhandled"));
			}

			isRepresentment = PennantConstants.PROCESS_REPRESENTMENT
					.equalsIgnoreCase(presentmentHeader.getPresentmentType());
			getUserWorkspace().allocateRoleAuthorities(getRole(), this.pageRightName);

			doSetFieldProperties();
			doCheckRights();
			doShowDialog(this.presentmentHeader);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
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

		if ("E".equalsIgnoreCase(moduleType) || "A".equalsIgnoreCase(moduleType)) {
			this.listheader_PresentmentDetail_Description.setVisible(true);
		} else {
			this.listheader_PresentmentDetail_Description.setVisible(false);
		}
		this.partnerBank.setMaxlength(LengthConstants.LEN_MASTER_CODE);
		this.partnerBank.setModuleName("PresentMents_PartnerBank");
		this.partnerBank.setValueColumn("PartnerBankId");
		this.partnerBank.setValueType(DataType.LONG);
		this.partnerBank.setDescColumn("PartnerBankCode");
		this.partnerBank.setValidateColumns(new String[] { "PartnerBankId" });
		this.partnerBank.setMandatoryStyle(true);

		Filter[] filters = null;
		if (StringUtils.isNotEmpty(presentmentHeader.getEntityCode())) {
			filters = new Filter[2];
			filters[0] = new Filter("AlwReceipt", 1, Filter.OP_EQUAL);
			filters[1] = new Filter("Entity", presentmentHeader.getEntityCode(), Filter.OP_EQUAL);
		} else {
			filters = new Filter[1];
			filters[0] = new Filter("AlwReceipt", 1, Filter.OP_EQUAL);
		}
		this.partnerBank.setFilters(filters);

		this.listBox_Include.setHeight(getListBoxHeight(7));
		this.listBox_ManualExclude.setHeight(getListBoxHeight(7));
		this.listBox_AutoExclude.setHeight(getListBoxHeight(7));

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set Visible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug(Literal.ENTERING);

		getUserWorkspace().allocateAuthorities(this.pageRightName, getRole());
		if ("N".equalsIgnoreCase(moduleType)) {
			this.window_title.setValue(Labels.getLabel("lable_window_PresentmentBatchCreation_title"));
			this.btnSave.setVisible(getUserWorkspace().isAllowed("button_PresentmentDetailDialog_btnSave"));
			this.btn_AddExlude.setVisible(getUserWorkspace().isAllowed("button_PresentmentDetailDialog_btnExclude"));
			this.btn_AddInclude.setVisible(getUserWorkspace().isAllowed("button_PresentmentDetailDialog_btnInclude"));
			this.btn_ExcludeAll.setVisible(getUserWorkspace().isAllowed("button_PresentmentDetailDialog_btnExclude"));
			this.btn_IncludeAll.setVisible(getUserWorkspace().isAllowed("button_PresentmentDetailDialog_btnInclude"));
			readOnlyComponent(isReadOnly("PresentmentDetailDialog_partnerBank"), this.partnerBank);
			this.partnerBank.setReadonly(!getUserWorkspace().isAllowed("PresentmentDetailDialog_partnerBank"));
		} else if ("A".equalsIgnoreCase(moduleType)) {
			this.window_title.setValue(Labels.getLabel("lable_window_PresentmentBatchApprove_title"));
			this.btnSave.setVisible(getUserWorkspace().isAllowed("button_PresentmentDetailDialog_btnSave"));
			this.btn_AddExlude.setVisible(false);
			this.btn_AddInclude.setVisible(false);
			this.btn_ExcludeAll.setVisible(false);
			this.btn_IncludeAll.setVisible(false);
			this.PresentmentIncludeExcludeChanges.setVisible(false);
			readOnlyComponent(true, this.partnerBank);
		} else if ("E".equalsIgnoreCase(moduleType)) {
			this.window_title.setValue(Labels.getLabel("lable_window_PresentmentBatchEnquiry_title"));
			this.btnSave.setVisible(false);
			this.btn_AddExlude.setVisible(false);
			this.btn_AddInclude.setVisible(false);
			this.btn_ExcludeAll.setVisible(false);
			this.btn_IncludeAll.setVisible(false);
			this.PresentmentIncludeExcludeChanges.setVisible(false);
			readOnlyComponent(true, this.partnerBank);
		}
		this.btnCancel.setVisible(false);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the save button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnSave(Event event) {
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
		doShowNotes(this.presentmentHeader);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	protected void refreshList() {
		logger.debug(Literal.ENTERING);
		presentmentDetailListCtrl.search();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * 
	 * /** Writes the bean data to the components.<br>
	 * 
	 * @param presentmentHeader
	 * 
	 */
	public void doWriteBeanToComponents(PresentmentHeader aPresentmentHeader) {
		logger.debug(Literal.ENTERING);

		if (presentmentHeader.getPartnerBankId() != null && presentmentHeader.getPartnerBankId() != 0) {
			this.partnerBank.setValue(String.valueOf(presentmentHeader.getPartnerBankId()));
			this.partnerBank.setDescription(presentmentHeader.getPartnerBankName());
			if (SysParamUtil.isAllowed(SMTParameterConstants.GROUP_BATCH_BY_BANK)) {
				this.partnerBank.setReadonly(true);
			}
		}

		this.label_PresentmentReference.setValue(presentmentHeader.getReference());
		this.label_PresentmentStatus.setValue(PennantStaticListUtil.getPropertyValue(
				PennantStaticListUtil.getPresentmentBatchStatusList(), presentmentHeader.getStatus()));

		boolean isApprove = "A".equals(moduleType);

		prepareList(aPresentmentHeader, this.listBox_Include, this.pagingIncludeList,
				RepayConstants.PRESENTMENT_INCLUDE, isApprove);
		prepareList(aPresentmentHeader, this.listBox_ManualExclude, this.pagingManualExcludeList,
				RepayConstants.PRESENTMENT_MANUALEXCLUDE, isApprove);
		prepareList(aPresentmentHeader, this.listBox_AutoExclude, this.pagingAutoExcludeList,
				RepayConstants.PRESENTMENT_AUTOEXCLUDE, isApprove);

		if ("E".equals(moduleType)) {
			this.dBStatusGrid.setVisible(true);
			this.label_TotalPresentments.setValue(String.valueOf(aPresentmentHeader.getTotalRecords()));
			this.label_SuccessPresentments.setValue(String.valueOf(aPresentmentHeader.getSuccessRecords()));
			this.label_FailedPresentments.setValue(String.valueOf(aPresentmentHeader.getFailedRecords()));
		} else {
			this.dBStatusGrid.setVisible(false);
		}

		logger.debug(Literal.LEAVING);
	}

	private void prepareList(PresentmentHeader aPresentmentHeader, Listbox listBox, Paging paggingList, String listType,
			boolean isApprove) {
		logger.debug(Literal.ENTERING);
		setPresentmentDetailPagedListWrapper();
		this.parameterSearchObject = new JdbcSearchObject<PresentmentDetail>();
		this.parameterSearchObject.setSearchClass(PresentmentDetail.class);

		if (InstrumentType.isPDC(aPresentmentHeader.getMandateType())
				|| InstrumentType.isIPDC(aPresentmentHeader.getMandateType())) {
			this.parameterSearchObject.addTabelName("PresentmentDetails_PDCAView");
		} else {
			this.parameterSearchObject.addTabelName("PresentmentDetails_AView");
		}

		parameterSearchObject.addSort("FINREFERENCE", false);

		StringBuilder whereClause = new StringBuilder();
		whereClause.append(" PresentmentId = " + aPresentmentHeader.getId() + " AND ");
		if (RepayConstants.PRESENTMENT_INCLUDE.equals(listType)) {
			if (isApprove) {
				whereClause.append(" ExcludeReason = " + 0 + " AND (Status = '" + RepayConstants.PEXC_IMPORT
						+ "' OR Status = '" + RepayConstants.PEXC_FAILURE + "')");
			} else {
				whereClause.append(" ExcludeReason = " + RepayConstants.PEXC_EMIINCLUDE + "");
			}
		} else if (RepayConstants.PRESENTMENT_MANUALEXCLUDE.equals(listType)) {
			whereClause.append(" ExcludeReason = " + RepayConstants.PEXC_MANUAL_EXCLUDE);
		} else {
			whereClause.append(" ExcludeReason != " + RepayConstants.PEXC_EMIINCLUDE + " AND ExcludeReason != "
					+ RepayConstants.PEXC_MANUAL_EXCLUDE + " AND ExcludeReason != " + RepayConstants.PEXC_ADVINT);
		}
		this.parameterSearchObject.addWhereClause(whereClause.toString());
		listBox.setPageSize(10);
		paggingList.setDetailed(true);

		SearchResult<PresentmentDetail> searchResult = pagedListService.getSRBySearchObject(parameterSearchObject);

		if (RepayConstants.PRESENTMENT_MANUALEXCLUDE.equals(listType)) {
			allExcludeIds = new ArrayList<>();
			for (PresentmentDetail pd : searchResult.getResult()) {
				allExcludeIds.add(pd.getId());
			}
		}

		if (RepayConstants.PRESENTMENT_INCLUDE.equals(listType)) {
			allIncludeIds = new ArrayList<>();
			for (PresentmentDetail pd : searchResult.getResult()) {
				allIncludeIds.add(pd.getId());
			}
		}

		getPresentmentDetailPagedListWrapper().initList(searchResult.getResult(), listBox, paggingList);
		listBox.setItemRenderer(new PresentmentDetailListModelItemRenderer());
		logger.debug(Literal.LEAVING);
	}

	public void onClick_listCellCheckBox(ForwardEvent event) throws Exception {
		logger.debug("Entering");

		Checkbox checkBox = (Checkbox) event.getOrigin().getTarget();

		PresentmentDetail presentmentDetail = (PresentmentDetail) checkBox.getAttribute("Data");
		long id = presentmentDetail.getId();
		if (checkBox.isChecked()) {
			if (RepayConstants.PEXC_EMIINCLUDE == presentmentDetail.getExcludeReason()) {
				excludeList.add(id);
			} else if (RepayConstants.PEXC_MANUAL_EXCLUDE == presentmentDetail.getExcludeReason()) {
				includeList.add(id);
			}
		} else {
			if (RepayConstants.PEXC_EMIINCLUDE == presentmentDetail.getExcludeReason()) {
				if (excludeList.contains(id)) {
					excludeList.remove(id);
				}
			} else if (RepayConstants.PEXC_MANUAL_EXCLUDE == presentmentDetail.getExcludeReason()) {
				if (includeList.contains(id)) {
					includeList.remove(id);
				}
			}

		}
		logger.debug(Literal.LEAVING);
	}

	private class PresentmentDetailListModelItemRenderer implements ListitemRenderer<PresentmentDetail>, Serializable {

		private static final long serialVersionUID = 1L;

		@Override
		public void render(Listitem item, PresentmentDetail presentmentDetail, int index) throws Exception {
			List<ValueLabel> excludeReasonList = PennantStaticListUtil.getPresentmentExclusionList();
			List<ValueLabel> statusList = PennantStaticListUtil.getPresentmentsStatusList();
			int format = CurrencyUtil.getFormat(presentmentDetail.getFinCcy());

			Listcell lc;
			lc = new Listcell();

			boolean excludeflag = RepayConstants.PEXC_EMIINCLUDE == presentmentDetail.getExcludeReason()
					|| RepayConstants.PEXC_MANUAL_EXCLUDE == presentmentDetail.getExcludeReason();

			if ("E".equalsIgnoreCase(moduleType) && excludeflag) {
				listheaderPresentmentDetailAction.setVisible(false);
				addCell(item, "");
			}

			final Checkbox cbActive = new Checkbox();
			cbActive.setChecked(false);
			cbActive.addForward("onClick", self, "onClick_listCellCheckBox");
			cbActive.setAttribute("Data", presentmentDetail);

			if (!"E".equalsIgnoreCase(moduleType) && (excludeflag)) {
				lc.appendChild(cbActive);
				lc.setParent(item);
				if (includeList.contains(presentmentDetail.getId())
						|| excludeList.contains(presentmentDetail.getId())) {
					cbActive.setChecked(true);
				}
			}
			addCell(item, presentmentDetail.getCustomerName());
			addCell(item, presentmentDetail.getFinReference());
			addCell(item, presentmentDetail.getFinType());
			addCell(item, DateUtility.formatToLongDate(presentmentDetail.getSchDate()));
			addCell(item, PennantApplicationUtil.amountFormate(presentmentDetail.getAdvanceAmt(), format));
			addCell(item, PennantApplicationUtil.amountFormate(presentmentDetail.getPresentmentAmt(), format));
			addCell(item, presentmentDetail.getPresentmentRef());
			addCell(item, presentmentDetail.getBankName());
			addCell(item, "");

			if (InstrumentType.isIPDC(presentmentDetail.getMandateType())) {
				addCell(item, Labels.getLabel("label_Mandate_IPDC"));
			} else if (InstrumentType.isPDC(presentmentDetail.getMandateType())) {
				addCell(item, Labels.getLabel("label_Mandate_PDC"));
			} else {
				addCell(item, presentmentDetail.getMandateType());
			}

			if (presentmentDetail.getExcludeReason() != 0) {
				addCell(item, PennantStaticListUtil.getlabelDesc(String.valueOf(presentmentDetail.getExcludeReason()),
						excludeReasonList));
			} else {
				addCell(item, PennantStaticListUtil.getlabelDesc(presentmentDetail.getStatus(), statusList));
				if ("E".equalsIgnoreCase(moduleType) || "A".equalsIgnoreCase(moduleType)) {
					addCell(item, presentmentDetail.getErrorDesc());
				}
			}
			item.setAttribute("Id", presentmentDetail.getId());
			item.setAttribute("Action", cbActive);
		}
	}

	private void addCell(Listitem item, String val) {
		Listcell cell;
		cell = new Listcell(val);
		cell.setParent(item);
	}

	public void onClick$btn_AddExlude(Event event) throws InterruptedException {
		logger.debug(Literal.ENTERING + event.toString());

		Clients.clearWrongValue(this.listBox_Include);
		if (this.excludeList.isEmpty()) {
			MessageUtil.showError(" Please select at least one record. ");
			return;
		} else if (this.partnerBank.getValue().isEmpty()) {
			MessageUtil.showError("Please Select Partner Bank to proceed");
			return;
		} else {
			List<Long> empltyIncludeList = new ArrayList<>();
			saveModifiedList(empltyIncludeList, excludeList);
			excludeList = new ArrayList<>();
		}

		logger.debug(Literal.LEAVING + event.toString());
	}

	public void onClick$btn_AddInclude(Event event) throws InterruptedException {
		logger.debug(Literal.ENTERING + event.toString());
		if (this.includeList.isEmpty()) {
			MessageUtil.showError(" Please select at least one record. ");
			return;
		} else if (this.partnerBank.getValue().isEmpty()) {
			MessageUtil.showError("Please Select Partner Bank to proceed");
			return;
		} else {
			List<Long> empltyExcludeList = new ArrayList<>();
			saveModifiedList(includeList, empltyExcludeList);
			includeList = new ArrayList<>();
		}
		logger.debug(Literal.LEAVING + event.toString());
	}

	public void onClick$btn_ExcludeAll(Event event) throws InterruptedException {
		logger.debug(Literal.ENTERING + event.toString());
		for (Listitem listitem : this.listBox_Include.getItems()) {
			Checkbox checkBox = (Checkbox) listitem.getAttribute("Action");
			checkBox.setChecked(true);
			PresentmentDetail presentmentDetail = (PresentmentDetail) checkBox.getAttribute("Data");
			long id = presentmentDetail.getId();

			if (!excludeList.contains(id)) {
				excludeList.add(id);
			}

		}

		for (Long id : allIncludeIds) {
			if (!excludeList.contains(id)) {
				excludeList.add(id);
			}
		}
		logger.debug(Literal.LEAVING + event.toString());
	}

	public void onClick$btn_IncludeAll(Event event) throws InterruptedException {
		logger.debug(Literal.ENTERING + event.toString());
		for (Listitem listitem : this.listBox_ManualExclude.getItems()) {
			Checkbox checkBox = (Checkbox) listitem.getAttribute("Action");
			checkBox.setChecked(true);
			PresentmentDetail presentmentDetail = (PresentmentDetail) checkBox.getAttribute("Data");
			long id = presentmentDetail.getId();

			if (!includeList.contains(id)) {
				includeList.add(id);
			}

		}

		for (Long id : allExcludeIds) {
			if (!includeList.contains(id)) {
				includeList.add(id);
			}
		}

		logger.debug(Literal.LEAVING + event.toString());
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aPresentmentHeader
	 */
	public void doWriteComponentsToBean(PresentmentHeader aPresentmentHeader) {
		logger.debug(Literal.LEAVING);

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		try {
			if (StringUtils.trimToNull(this.partnerBank.getValue()) == null) {
				throw new WrongValueException(this.partnerBank, Labels.getLabel("FIELD_IS_MAND",
						new String[] { Labels.getLabel("label_PresentmentDetailList_Bank.value") }));
			}
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

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Displays the dialog page.
	 * 
	 * @param presentmentHeader The entity that need to be render.
	 */
	public void doShowDialog(PresentmentHeader presentmentHeader) {
		logger.debug(Literal.LEAVING);

		doEdit();
		doWriteBeanToComponents(presentmentHeader);

		if (isRepresentment) {
			this.window_title.setValue(Labels.getLabel("RePresentmentHeaderDialog.title"));
			this.label_PresentmentDetailList_Status
					.setValue(Labels.getLabel("label_RePresentmentDetailList_PresentmentStatus.value"));
		}

		try {
			setDialog(DialogType.EMBEDDED);
		} catch (UiException e) {
			logger.error(Literal.EXCEPTION, e);
			this.window_PresentmentHeaderDialog.onClose();
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			throw e;
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug(Literal.LEAVING);
		this.partnerBank.setConstraint("");
		this.partnerBank.setErrorMessage("");
		this.insertFinReference.setConstraint("");
		this.insertFinReference.setErrorMessage("");
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Clears validation error messages from all the fields of the dialog controller.
	 */
	@Override
	protected void doClearMessage() {
		logger.debug(Literal.LEAVING);
		this.partnerBank.setErrorMessage("");
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug(Literal.ENTERING);

		if ("N".equalsIgnoreCase(moduleType)) {
			this.userAction.appendItem("Save", "Saved");
			this.userAction.appendItem("Submit", "Submit");
			this.userAction.appendItem("Cancel", "Cancel");
			this.userAction.setSelectedIndex(0);
		} else if ("A".equalsIgnoreCase(moduleType)) {
			this.userAction.appendItem("Approve", "Approved");
			this.userAction.appendItem("Resubmit", "Resubmited");
			this.listBox_Include.setMultiple(false);
			this.listBox_Include.setCheckmark(false);
			this.listBox_ManualExclude.setMultiple(false);
			this.listBox_ManualExclude.setCheckmark(false);
			this.userAction.setSelectedIndex(0);
		} else if ("E".equalsIgnoreCase(moduleType)) {
			this.userAction.setVisible(false);
			this.listBox_Include.setMultiple(false);
			this.listBox_Include.setMultiple(false);
			this.listBox_Include.setCheckmark(false);
			this.listBox_ManualExclude.setMultiple(false);
			this.listBox_ManualExclude.setCheckmark(false);
			this.groupboxWf.setVisible(false);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Clears the components values. <br>
	 */
	public void doClear() {
		logger.debug(Literal.ENTERING);

		this.partnerBank.setValue("");

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Saves the components to table. <br>
	 */
	public void doSave() {
		logger.debug(Literal.ENTERING);

		final PresentmentHeader aPresentmentHeader = new PresentmentHeader();
		BeanUtils.copyProperties(this.presentmentHeader, aPresentmentHeader);

		long partnerBankId = 0;
		String userAction = this.userAction.getSelectedItem().getLabel();

		if (!"Cancel".equals(userAction)) {
			boolean includeExists = this.presentmentDetailService.searchIncludeList(this.presentmentHeader.getId(), 0);

			if (!includeExists) {
				MessageUtil.showError("No records are available in include list.");
				return;
			}

			doWriteComponentsToBean(aPresentmentHeader);
			partnerBankId = Long.valueOf(this.partnerBank.getValue());
		}

		if ("Approve".equals(userAction)) {
			List<Long> excludeList = this.presentmentDetailService
					.getExcludePresentmentDetailIdList(this.presentmentHeader.getId(), true);
			aPresentmentHeader.setExcludeList(excludeList);
		}

		aPresentmentHeader.setPartnerBankId(partnerBankId);
		aPresentmentHeader.setUserDetails(getUserWorkspace().getLoggedInUser());
		aPresentmentHeader.setUserAction(userAction);

		try {
			this.presentmentDetailService.updatePresentmentDetails(aPresentmentHeader);
		} catch (Exception e) {
			logger.debug(Literal.EXCEPTION, e);
			MessageUtil.showError(e);
		}

		refreshList();
		closeDialog();

		logger.debug(Literal.LEAVING);
	}

	// Insert Finance Reference for adding the record to include.
	public void onClick$include(Event event) {
		logger.debug(Literal.ENTERING);
		List<Long> includeList = new ArrayList<>();
		List<Long> excludeList = new ArrayList<>();
		if (StringUtils.trimToNull(this.insertFinReference.getValue()) == null) {
			throw new WrongValueException(this.insertFinReference,
					Labels.getLabel("Presentment_IncldeExclude_Empty_FinReference"));
		}

		String finreference = this.insertFinReference.getValue();
		Long finID = presentmentDetailService.getFinID(finreference);

		if (finID == null) {
			throw new WrongValueException(this.insertFinReference,
					Labels.getLabel("Presentment_IncldeExclude_Invalid_FinReference"));
		}

		PresentmentDetail presentmentDetail = this.presentmentDetailService.getPresentmentDetailByFinRefAndPresID(finID,
				this.presentmentHeader.getId());
		if (presentmentDetail != null) {
			if (RepayConstants.PEXC_MANUAL_EXCLUDE == presentmentDetail.getExcludeReason()) {
				includeList.add(presentmentDetail.getId());
			} else if (RepayConstants.PEXC_EMIINCLUDE == presentmentDetail.getExcludeReason()) {
				throw new WrongValueException(this.insertFinReference,
						Labels.getLabel("Presentment_IncldeExclude_IncludeExists_FinReference"));
			} else {
				throw new WrongValueException(this.insertFinReference,
						Labels.getLabel("Presentment_IncldeExclude_AutoExclude_FinReference"));
			}
		} else {
			throw new WrongValueException(this.insertFinReference,
					Labels.getLabel("Presentment_IncldeExclude_Invalid_FinReference"));
		}

		saveModifiedList(includeList, excludeList);
		logger.debug(Literal.LEAVING);
	}

	// Insert Finance Reference for adding the record to exclude manually
	public void onClick$exclude(Event event) {
		logger.debug(Literal.ENTERING);
		List<Long> includeList = new ArrayList<>();
		List<Long> excludeList = new ArrayList<>();
		if (StringUtils.trimToNull(this.insertFinReference.getValue()) == null) {
			throw new WrongValueException(this.insertFinReference,
					Labels.getLabel("Presentment_IncldeExclude_Empty_FinReference"));
		}

		String finreference = this.insertFinReference.getValue();
		Long finID = presentmentDetailService.getFinID(finreference);

		if (finID == null) {
			throw new WrongValueException(this.insertFinReference,
					Labels.getLabel("Presentment_IncldeExclude_Invalid_FinReference"));
		}

		PresentmentDetail presentmentDetail = this.presentmentDetailService.getPresentmentDetailByFinRefAndPresID(finID,
				this.presentmentHeader.getId());
		if (presentmentDetail != null) {
			if (RepayConstants.PEXC_EMIINCLUDE == presentmentDetail.getExcludeReason()) {
				excludeList.add(presentmentDetail.getId());
			} else if (RepayConstants.PEXC_MANUAL_EXCLUDE == presentmentDetail.getExcludeReason()) {
				throw new WrongValueException(this.insertFinReference,
						Labels.getLabel("Presentment_IncldeExclude_ExcludeExists_FinReference"));
			} else {
				throw new WrongValueException(this.insertFinReference,
						Labels.getLabel("Presentment_IncldeExclude_AutoExclude_FinReference"));
			}
		} else {
			throw new WrongValueException(this.insertFinReference,
					Labels.getLabel("Presentment_IncldeExclude_Invalid_FinReference"));
		}

		saveModifiedList(includeList, excludeList);
		logger.debug(Literal.LEAVING);
	}

	// sample file download for adding to include or exclude manually
	public void onClick$sampleFileDownload(Event event) throws Exception {
		logger.debug(Literal.ENTERING);
		String path = PathUtil.getPath(PathUtil.TEMPLATES);
		String fileName = "Presentment_IncludeExclude_Changes.xlsx";

		File template = new File(path.concat(File.separator).concat(fileName));

		if (!template.exists()) {
			MessageUtil.showError(fileName + " template not found in " + path + " location.");
			return;
		}

		Filedownload.save(template, DocType.XLSX.getContentType());

		logger.debug(Literal.LEAVING);
	}

	// File import for adding to include or exclude manually
	public void onUpload$btnUpload(UploadEvent event) throws Exception {
		logger.debug(Literal.ENTERING);
		boolean header = true;
		List<PresentmentDetail> presentmentDetailList = new ArrayList<>();
		media = event.getMedia();
		Sheet firstSheet;

		if (!MediaUtil.isExcel(media) || media.getName().length() > 100) {
			MessageUtil.showError(Labels.getLabel("upload_document_invalid", new String[] { "excel" }));
			this.uploadedfileName.setValue("");
			return;
		}

		this.uploadedfileName.setValue(media.getName());

		if (MediaUtil.isXls(media)) {
			firstSheet = new HSSFWorkbook(media.getStreamData()).getSheetAt(0);
		} else {
			firstSheet = new XSSFWorkbook(media.getStreamData()).getSheetAt(0);
		}

		Iterator<Row> iterator = firstSheet.iterator();

		while (iterator.hasNext()) {
			try {
				Row nextRow = iterator.next();

				if (header) {
					if (nextRow.getPhysicalNumberOfCells() != 2) {
						MessageUtil.showError(Labels.getLabel("Presentment_IncldeExclude_Columns"));
						this.uploadedfileName.setValue("");
						return;
					}
					header = false;
					continue;
				}
				parseExcelData(presentmentDetailList, nextRow);
				if (!isvalidData) {
					MessageUtil.showError(Labels.getLabel("label_File_Format"));
					isvalidData = true;
					this.uploadedfileName.setValue("");
					return;
				}
			} catch (Exception e) {
				logger.debug(e);
			}
		}

		if (CollectionUtils.isEmpty(presentmentDetailList)) {
			return;
		}

		List<PresentmentDetail> changeList = new ArrayList<>();
		List<Long> includeList = new ArrayList<>();
		List<Long> excludeList = new ArrayList<>();

		for (PresentmentDetail pd : presentmentDetailList) {

			Long finID = financeMainDAO.getFinIDByFinReference(pd.getFinReference(), "", false);

			if (finID == null) {
				MessageUtil.showError("Please Provide Valid Reference:" + pd.getFinReference());
				return;
			}

			pd.setFinID(finID);
			PresentmentDetail presentmentDetail2 = this.presentmentDetailService
					.getPresentmentDetailByFinRefAndPresID(pd.getFinID(), this.presentmentHeader.getId());
			if (presentmentDetail2 == null) {
				pd.setExcludeReason(4);
				changeList.add(pd);
				continue;
			}

			int excludeReason1 = pd.getExcludeReason();
			int excludeReason2 = presentmentDetail2.getExcludeReason();
			if (RepayConstants.PEXC_EMIINCLUDE == excludeReason1
					&& RepayConstants.PEXC_MANUAL_EXCLUDE == excludeReason2) {
				changeList.add(presentmentDetail2);
				includeList.add(presentmentDetail2.getId());
			} else if (RepayConstants.PEXC_MANUAL_EXCLUDE == excludeReason1
					&& RepayConstants.PEXC_EMIINCLUDE == excludeReason2) {
				changeList.add(presentmentDetail2);
				excludeList.add(presentmentDetail2.getId());
			} else if (RepayConstants.PEXC_EMIINCLUDE == excludeReason1
					&& RepayConstants.PEXC_EMIINCLUDE == excludeReason2) {
				presentmentDetail2.setExcludeReason(1);
				changeList.add(presentmentDetail2);
			} else if (RepayConstants.PEXC_MANUAL_EXCLUDE == excludeReason1
					&& RepayConstants.PEXC_MANUAL_EXCLUDE == excludeReason2) {
				presentmentDetail2.setExcludeReason(2);
				changeList.add(presentmentDetail2);
			} else {
				presentmentDetail2.setExcludeReason(3);
				changeList.add(presentmentDetail2);
			}
		}
		showPresentmentDetailImportChangesList(changeList, includeList, excludeList);

		logger.debug(Literal.LEAVING);
	}

	private void showPresentmentDetailImportChangesList(List<PresentmentDetail> presentmentDetailImportChangesList,
			List<Long> includeList, List<Long> excludeList) {
		logger.debug(Literal.ENTERING);
		final Map<String, Object> map = new HashMap<String, Object>();
		map.put("presentmentDetailImportChangesList", presentmentDetailImportChangesList);
		map.put("moduleType", moduleType);
		map.put("presentmentDetailDialogCtrl", this);
		map.put("includeList", includeList);
		map.put("excludeList", excludeList);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents(
					"/WEB-INF/pages/FinanceManagement/PresentmentDetail/PresentmentDetailImportChangesList.zul",
					window_PresentmentHeaderDialog, map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug(Literal.LEAVING);

	}

	private PresentmentDetail parseExcelData(List<PresentmentDetail> presentmentDetail, Row nextRow) {
		PresentmentDetail presentmentDetail1 = new PresentmentDetail();
		presentmentDetail1.setFinReference(nextRow.getCell(0).toString());
		String status = nextRow.getCell(1).toString();
		if ("E".equals(status)) {
			presentmentDetail1.setExcludeReason(RepayConstants.PEXC_MANUAL_EXCLUDE);
		} else if ("I".equals(status)) {
			presentmentDetail1.setExcludeReason(RepayConstants.PEXC_EMIINCLUDE);
		} else {
			presentmentDetail1.setExcludeReason(1);
		}
		presentmentDetail.add(presentmentDetail1);
		return presentmentDetail1;
	}

	public void saveModifiedList(List<Long> includeList, List<Long> excludeList) {
		long partnerBankId = 0;
		if (this.partnerBank.getValue().isEmpty()) {
			MessageUtil.showError("Please Select Partner Bank to proceed");
			this.uploadedfileName.setValue("");
			return;
		} else {
			partnerBankId = Long.valueOf(this.partnerBank.getValue());
		}
		try {
			this.presentmentDetailService.saveModifiedPresentments(excludeList, includeList,
					this.presentmentHeader.getId(), partnerBankId);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
		doWriteBeanToComponents(this.presentmentHeader);
	}

	public PresentmentDetailService getPresentmentDetailService() {
		return presentmentDetailService;
	}

	public void setPresentmentDetailService(PresentmentDetailService presentmentDetailService) {
		this.presentmentDetailService = presentmentDetailService;
	}

	public Media getMedia() {
		return media;
	}

	public void setMedia(Media media) {
		this.media = media;
	}

	@SuppressWarnings("unchecked")
	public void setPresentmentDetailPagedListWrapper() {
		this.presentmentDetailPagedListWrapper = (PagedListWrapper<PresentmentDetail>) SpringUtil
				.getBean("pagedListWrapper");
	}

	public PagedListWrapper<PresentmentDetail> getPresentmentDetailPagedListWrapper() {
		return presentmentDetailPagedListWrapper;
	}

	public void setPresentmentDetailPagedListWrapper(
			PagedListWrapper<PresentmentDetail> presentmentDetailPagedListWrapper) {
		this.presentmentDetailPagedListWrapper = presentmentDetailPagedListWrapper;
	}

	public PagedListService getPagedListService() {
		return pagedListService;
	}

	public void setPagedListService(PagedListService pagedListService) {
		this.pagedListService = pagedListService;
	}

	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}
}
