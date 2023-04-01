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
 * * FileName : CovenantDocumentDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 20-12-2017 * *
 * Modified Date : * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 20-12-2017 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.finance.covenant;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.media.AMedia;
import org.zkoss.util.media.Media;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zul.A;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Iframe;
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.Property;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.finance.covenant.CovenantDocument;
import com.pennant.backend.model.systemmasters.DocumentType;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTDateValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.util.Constraint.StaticListValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.DocType;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pennapps.core.util.MediaUtil;
import com.pennanttech.pennapps.dms.service.DMSService;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.pff.document.DocumentCategories;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/Finance/Covenant/CovenantDocumentDialog.zul file.
 */
public class CovenantDocumentDialogCtrl extends GFCBaseCtrl<CovenantDocument> {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(CovenantDocumentDialogCtrl.class);

	protected Window window_CovenantDocumentDialog;

	protected ExtendedCombobox convDocType;
	protected Datebox docReceivedDate;
	protected Combobox frequencyBox;
	protected Textbox documentName;
	protected Button btnUploadDoc;
	protected A btnDownload;
	protected Iframe finDocumentPdfView;
	protected Div docDiv;
	protected Div finDocumentDiv;
	private transient boolean validationOn;
	protected Row rw_freqency;
	protected Checkbox originalDocument;

	private String userRole = "";
	private CovenantDocument covenantDocument;
	private CovenantsDialogCtrl covenantsDialogCtrl;
	private List<CovenantDocument> covenantDocuments;

	private String frequncy;
	private Date loanStartDate;
	private Date loanMaturityDate;
	private DMSService dMSService;
	protected Date receivableDate;
	protected Date nextFrequencyDate;

	/**
	 * default constructor.<br>
	 */
	public CovenantDocumentDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "CovenantDialog";
	}

	/**
	 * Before binding the data and calling the dialog window we check, if the zul-file is called with a parameter for a
	 * selected FinTypeExpenses object in a Map.
	 * 
	 * @param event
	 */
	public void onCreate$window_CovenantDocumentDialog(Event event) {
		logger.debug(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(window_CovenantDocumentDialog);

		try {

			if (arguments.containsKey("covenantDocument")) {
				this.covenantDocument = (CovenantDocument) arguments.get("covenantDocument");
				CovenantDocument befImage = new CovenantDocument();
				BeanUtils.copyProperties(this.covenantDocument, befImage);
				this.covenantDocument.setBefImage(befImage);
				setCovenantDocument(this.covenantDocument);
			} else {
				setCovenantDocument(null);
			}

			if (arguments.containsKey("covenantsDialogCtrl")) {
				setCovenantsDialogCtrl((CovenantsDialogCtrl) arguments.get("covenantsDialogCtrl"));
			} else {
				setCovenantsDialogCtrl(null);
			}

			if (arguments.containsKey("role")) {
				userRole = arguments.get("role").toString();
				getUserWorkspace().allocateRoleAuthorities(userRole, this.pageRightName);
			}

			if (arguments.containsKey("frequency")) {
				frequncy = arguments.get("frequency").toString();
			}

			if (arguments.containsKey("loanStartDate")) {
				loanStartDate = (Date) arguments.get("loanStartDate");
			}

			if (arguments.containsKey("loanMaturityDate")) {
				loanMaturityDate = (Date) arguments.get("loanMaturityDate");
			}

			if (arguments.containsKey("frequency")) {
				frequncy = arguments.get("frequency").toString();
			}

			if (arguments.containsKey("receivableDate")) {
				receivableDate = (Date) arguments.get("receivableDate");
			}

			if (arguments.containsKey("nextFrequecnyDate")) {
				nextFrequencyDate = (Date) arguments.get("nextFrequecnyDate");
			}

			// fillfrequencyDates();
			this.covenantDocument.setWorkflowId(0);
			doLoadWorkFlow(this.covenantDocument.isWorkflow(), this.covenantDocument.getWorkflowId(),
					this.covenantDocument.getNextTaskId());

			if (isWorkFlowEnabled()) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(), super.pageRightName);
			}

			doCheckRights();

			doSetFieldProperties();

			doShowDialog(getCovenantDocument());
		} catch (Exception e) {
			MessageUtil.showError(e);
			window_CovenantDocumentDialog.onClose();
		}
		logger.debug(Literal.LEAVING);
	}

	private List<Property> getFrequency(final Date startDate, final Date endDate, int frequency) {
		List<Property> list = new ArrayList<>();
		if (startDate == null || endDate == null) {
			return list;
		}

		Date tempStartDate = (Date) startDate.clone();
		Date tempEndDate = (Date) endDate.clone();

		while (DateUtil.compare(tempStartDate, tempEndDate) <= 0) {
			String key = DateUtil.format(tempStartDate, DateFormat.LONG_DATE);
			list.add(new Property(tempStartDate, key));
			tempStartDate = DateUtil.addMonths(tempStartDate, frequency);
		}

		return list;
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug(Literal.ENTERING);

		this.convDocType.getTextbox().setWidth("150px");
		this.convDocType.setMandatoryStyle(true);
		this.convDocType.setModuleName("DocumentType");
		this.convDocType.setValueColumn("DocTypeCode");
		this.convDocType.setDescColumn("DocTypeDesc");
		this.convDocType.setValidateColumns(new String[] { "DocTypeCode" });
		Filter filter[] = new Filter[1];
		filter[0] = new Filter("CategoryCode", DocumentCategories.COVENANT.getKey(), Filter.OP_EQUAL);
		this.convDocType.setFilters(filter);

		this.docReceivedDate.setFormat(DateFormat.SHORT_DATE.getPattern());

		if (isWorkFlowEnabled()) {
			this.groupboxWf.setVisible(true);
		} else {
			this.groupboxWf.setVisible(false);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * User rights check. <br>
	 * Only components are set visible=true if the logged-in <br>
	 * user have the right for it. <br>
	 * 
	 * The rights are get from the spring framework users grantedAuthority(). A right is only a string. <br>
	 */
	private void doCheckRights() {
		logger.debug(Literal.ENTERING);

		getUserWorkspace().allocateAuthorities(this.pageRightName, getRole());

		readOnlyComponent(isReadOnly("button_CovenantDialog_btnNew"), this.btnNew);
		readOnlyComponent(isReadOnly("button_CovenantDialog_btnEdit"), this.btnEdit);
		readOnlyComponent(isReadOnly("button_CovenantDialog_btnSave"), this.btnSave);
		readOnlyComponent(isReadOnly("button_CovenantDialog_btnDelete"), this.btnDelete);
		this.btnCancel.setVisible(false);

		logger.debug(Literal.LEAVING);
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
		logger.debug(Literal.LEAVING);
	}

	/**
	 * when the "edit" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnEdit(Event event) {
		logger.debug(event.toString());
		doEdit();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		logger.debug(event.toString());
		MessageUtil.showHelpWindow(event, window_CovenantDocumentDialog);
		logger.debug(Literal.LEAVING);
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
		logger.debug(Literal.LEAVING);
	}

	/**
	 * when the "cancel" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnCancel(Event event) {
		logger.debug(event.toString());
		doCancel();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		doClose(this.btnSave.isVisible());
	}

	public void onUpload$btnUploadDoc(UploadEvent event) throws InterruptedException {
		logger.debug(Literal.ENTERING);

		Media media = event.getMedia();

		List<DocType> allowed = new ArrayList<>();
		allowed.add(DocType.PDF);
		allowed.add(DocType.JPG);
		allowed.add(DocType.JPEG);
		allowed.add(DocType.PNG);
		allowed.add(DocType.MSG);
		allowed.add(DocType.DOC);
		allowed.add(DocType.DOCX);
		allowed.add(DocType.XLS);
		allowed.add(DocType.XLSX);
		allowed.add(DocType.ZIP);
		allowed.add(DocType.Z7);
		allowed.add(DocType.RAR);
		allowed.add(DocType.TXT);

		if (!MediaUtil.isValid(media, allowed)) {
			MessageUtil.showError(Labels.getLabel("UnSupported_Document"));
			return;
		}

		browseDoc(media, this.documentName);
		doSetDownLoadVisible();
		logger.debug(Literal.LEAVING);
	}

	public void onFulfill$convDocType(Event event) {
		logger.debug(Literal.ENTERING);

		Object dataObject = this.convDocType.getObject();

		if (dataObject instanceof String) {
			this.convDocType.setValue(dataObject.toString());
		} else {
			DocumentType details = (DocumentType) dataObject;
			if (details != null) {
				this.convDocType.setValue(details.getDocTypeCode());
				this.convDocType.setDescription(details.getDocTypeDesc());
				DocumentDetails documentDetails = new DocumentDetails();
				documentDetails.setDocCategory(details.getDocTypeCode());
				documentDetails.setLovDescDocCategoryName(details.getDocTypeDesc());
				this.documentName.setAttribute("data", documentDetails);
				covenantDocument.setDocCategory(details.getDocTypeCode());
			} else {
				this.convDocType.setValue("");
			}
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Do Set DownLoad link Properties. <br>
	 */
	private void doSetDownLoadVisible() {
		this.btnDownload.setVisible(false);
		if (StringUtils.isNotBlank(this.documentName.getValue())) {
			this.btnDownload.setLabel(this.documentName.getValue());
			this.btnDownload.setVisible(true);
		}

	}

	/**
	 * Cancel the actual operation. <br>
	 * <br>
	 * Resets to the original status.<br>
	 * 
	 */
	private void doCancel() {
		logger.debug(Literal.ENTERING);
		doWriteBeanToComponents(this.covenantDocument.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aFinTypeExpense FinTypeExpense
	 */
	public void doWriteBeanToComponents(CovenantDocument aCovenantDocument) {
		logger.debug(Literal.ENTERING);

		this.docReceivedDate.setValue(aCovenantDocument.getDocumentReceivedDate());
		this.documentName.setAttribute("data", aCovenantDocument.getDocumentDetail());
		this.originalDocument.setChecked(aCovenantDocument.isOriginalDocument());
		this.convDocType.setValue(StringUtils.trimToEmpty(aCovenantDocument.getDocCategory()));
		if (aCovenantDocument.getDocumentDetail() != null
				&& StringUtils.isNotEmpty(aCovenantDocument.getDocumentDetail().getDocName())) {
			this.documentName.setValue(aCovenantDocument.getDocumentDetail().getDocName());
		} else {
			this.documentName.setValue(aCovenantDocument.getDocName());
		}

		Date frequencyDate = aCovenantDocument.getFrequencyDate();
		String key = DateUtil.format(receivableDate, DateFormat.LONG_DATE);
		List<Property> list = new ArrayList<>();
		if (receivableDate != null) {
			list.add(new Property(receivableDate, key));
		}
		if (frequencyDate != null && nextFrequencyDate.compareTo(frequencyDate) > 0
				&& receivableDate.compareTo(frequencyDate) != 0) {
			list.add(new Property(frequencyDate, DateUtil.format(frequencyDate, DateFormat.LONG_DATE)));
		}
		if (frequncy.equals("M")) {
			list.addAll(getFrequency(nextFrequencyDate, loanMaturityDate, 1));
			list = fillfrequencyDates(list, aCovenantDocument.isNewRecord(), aCovenantDocument.getRecordType());
			fillList(this.frequencyBox, list, frequencyDate);
		} else if (frequncy.equals("Q")) {
			list.addAll(getFrequency(nextFrequencyDate, loanMaturityDate, 3));
			list = fillfrequencyDates(list, aCovenantDocument.isNewRecord(), aCovenantDocument.getRecordType());
			fillList(this.frequencyBox, list, frequencyDate);
		} else if (frequncy.equals("H")) {
			list.addAll(getFrequency(nextFrequencyDate, loanMaturityDate, 6));
			list = fillfrequencyDates(list, aCovenantDocument.isNewRecord(), aCovenantDocument.getRecordType());
			fillList(this.frequencyBox, list, frequencyDate);
		} else if (frequncy.equals("A")) {
			list.addAll(getFrequency(nextFrequencyDate, loanMaturityDate, 12));
			list = fillfrequencyDates(list, aCovenantDocument.isNewRecord(), aCovenantDocument.getRecordType());
			fillList(this.frequencyBox, list, frequencyDate);
		} else if (frequncy.equals("O")) {
			this.rw_freqency.setVisible(false);
		} else {
			this.rw_freqency.setVisible(false);
		}

		DocumentDetails documentDetails = aCovenantDocument.getDocumentDetail();

		AMedia amedia = null;
		if (documentDetails != null && documentDetails.getDocImage() != null) {
			if (documentDetails.getDoctype().equals(PennantConstants.DOC_TYPE_WORD)
					|| documentDetails.getDoctype().equals(PennantConstants.DOC_TYPE_MSG)
					|| documentDetails.getDoctype().equals(PennantConstants.DOC_TYPE_EXCEL)
					|| documentDetails.getDoctype().equals(PennantConstants.DOC_TYPE_ZIP)
					|| documentDetails.getDoctype().equals(PennantConstants.DOC_TYPE_7Z)
					|| documentDetails.getDoctype().equals(PennantConstants.DOC_TYPE_RAR)) {
				this.docDiv.getChildren().clear();
				this.docDiv.appendChild(getDocumentLink(documentDetails.getDocName(), documentDetails.getDoctype(),
						this.documentName.getValue(), documentDetails.getDocImage()));
			} else {
				amedia = new AMedia(documentDetails.getDocName(), null, null, documentDetails.getDocImage());
			}
			finDocumentPdfView.setContent(amedia);
		}

		if (aCovenantDocument.isNewRecord()) {
			this.docReceivedDate.setValue(SysParamUtil.getAppDate());
		}
		this.recordStatus.setValue(aCovenantDocument.getRecordStatus());

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aFinTypeExpense
	 * @throws InterruptedException
	 */
	public void doWriteComponentsToBean(CovenantDocument aCovenantDocument) throws InterruptedException {
		logger.debug(Literal.ENTERING);

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			aCovenantDocument.setCovenantType(this.convDocType.getValidatedValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (DateUtil.compare(this.docReceivedDate.getValue(), SysParamUtil.getAppDate()) == 1) {
				throw new WrongValueException(this.docReceivedDate, Labels.getLabel("DATE_NO_FUTURE",
						new String[] { Labels.getLabel("label_CovenantDocumentDialog_ReceivedDate.value") }));
			}
			aCovenantDocument.setDocumentReceivedDate(this.docReceivedDate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aCovenantDocument.setDocName(this.documentName.getValue());
			if (this.documentName.getAttribute("data") != null) {
				DocumentDetails details = (DocumentDetails) this.documentName.getAttribute("data");
				details.setDocRefId(details.getDocRefId());
				details.setDocReceivedDate(this.docReceivedDate.getValue());
				details.setDocName(this.documentName.getValue());
				aCovenantDocument.setDocumentDetail(details);
			} else {
				aCovenantDocument.setDocumentDetail(null);
			}

		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (this.frequencyBox.isVisible()) {
				if (this.frequencyBox.getSelectedItem() != null
						&& this.frequencyBox.getSelectedItem().getValue() != "#") {
					aCovenantDocument.setFrequencyDate(this.frequencyBox.getSelectedItem().getValue());
				}
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aCovenantDocument.setOriginalDocument(this.originalDocument.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		doRemoveValidation();
		if (wve.size() > 0) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}
		aCovenantDocument.setRecordStatus(this.recordStatus.getValue());
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param aFinTypeExpense
	 * @throws InterruptedException
	 */
	public void doShowDialog(CovenantDocument aCovenantDocument) throws InterruptedException {
		logger.debug(Literal.ENTERING);

		if (aCovenantDocument.isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.convDocType.focus();
		} else {
			this.docReceivedDate.focus();
			doEdit();
			btnCancel.setVisible(false);
		}
		try {
			// fill the components with the data
			doWriteBeanToComponents(aCovenantDocument);
			doSetDownLoadVisible();
			this.window_CovenantDocumentDialog.doModal();
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug(Literal.ENTERING);
		setValidationOn(true);

		if (!this.convDocType.isReadonly()) {
			this.convDocType.setConstraint(new PTStringValidator(
					Labels.getLabel("label_CovenantDocumentDialog_ConvDocType.value"), null, true, true));
		}

		if (!this.docReceivedDate.isDisabled()) {
			this.docReceivedDate.setConstraint(
					new PTDateValidator(Labels.getLabel("label_CovenantDocumentDialog_ReceivedDate.value"), true));
		}
		if (this.frequencyBox.isVisible() && !this.frequencyBox.isDisabled()) {
			setFrequencyValidation();
		}

		logger.debug(Literal.LEAVING);
	}

	public void setFrequencyValidation() {
		String key = DateUtil.format(receivableDate, DateFormat.LONG_DATE);
		List<Property> list = new ArrayList<>();
		if (receivableDate != null) {
			list.add(new Property(receivableDate, key));
		}
		if (frequncy.equals("M")) {
			list.addAll(getFrequency(nextFrequencyDate, loanMaturityDate, 1));
			this.frequencyBox.setConstraint(
					new StaticListValidator(list, Labels.getLabel("label_CovenantsDialog_CovenantFrequency.value")));
		} else if (frequncy.equals("Q")) {
			list.addAll(getFrequency(nextFrequencyDate, loanMaturityDate, 3));
			this.frequencyBox.setConstraint(
					new StaticListValidator(list, Labels.getLabel("label_CovenantsDialog_CovenantFrequency.value")));
		} else if (frequncy.equals("H")) {
			list.addAll(getFrequency(nextFrequencyDate, loanMaturityDate, 6));
			this.frequencyBox.setConstraint(
					new StaticListValidator(list, Labels.getLabel("label_CovenantsDialog_CovenantFrequency.value")));
		} else if (frequncy.equals("A")) {
			list.addAll(getFrequency(nextFrequencyDate, loanMaturityDate, 12));
			this.frequencyBox.setConstraint(
					new StaticListValidator(list, Labels.getLabel("label_CovenantsDialog_CovenantFrequency.value")));
		}

	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug(Literal.ENTERING);
		setValidationOn(false);
		this.convDocType.setConstraint("");
		this.docReceivedDate.setConstraint("");
		this.documentName.setConstraint("");
		logger.debug(Literal.LEAVING);
	}

	private void browseDoc(Media media, Textbox textbox) throws InterruptedException {
		logger.debug("Entering");

		try {
			String docType = "";
			if ("application/pdf".equals(media.getContentType())) {
				docType = PennantConstants.DOC_TYPE_PDF;
			} else if ("image/jpeg".equals(media.getContentType()) || "image/png".equals(media.getContentType())) {
				docType = PennantConstants.DOC_TYPE_IMAGE;
			} else if (media.getName().endsWith(".doc") || media.getName().endsWith(".docx")) {
				docType = PennantConstants.DOC_TYPE_WORD;
			} else if (media.getName().endsWith(".msg")) {
				docType = PennantConstants.DOC_TYPE_MSG;
			} else if (media.getName().endsWith(".xls") || media.getName().endsWith(".xlsx")) {
				docType = PennantConstants.DOC_TYPE_EXCEL;
			} else if ("application/x-zip-compressed".equals(media.getContentType())) {
				docType = PennantConstants.DOC_TYPE_ZIP;
			} else if ("application/octet-stream".equals(media.getContentType())) {
				docType = PennantConstants.DOC_TYPE_7Z;
			} else if ("application/x-rar-compressed".equals(media.getContentType())) {
				docType = PennantConstants.DOC_TYPE_RAR;
			}

			String fileName = media.getName();
			byte[] ddaImageData = IOUtils.toByteArray(media.getStreamData());
			// Data Fill by QR Bar Code Reader
			if (docType.equals(PennantConstants.DOC_TYPE_PDF)) {
				this.finDocumentPdfView
						.setContent(new AMedia(fileName, null, null, new ByteArrayInputStream(ddaImageData)));

			} else if (docType.equals(PennantConstants.DOC_TYPE_IMAGE)) {
				this.finDocumentPdfView.setContent(media);
			} else if (docType.equals(PennantConstants.DOC_TYPE_WORD) || docType.equals(PennantConstants.DOC_TYPE_MSG)
					|| docType.equals(PennantConstants.DOC_TYPE_EXCEL)) {
				this.docDiv.getChildren().clear();
				this.docDiv.appendChild(getDocumentLink(fileName, docType, fileName, ddaImageData));
			} else if (docType.equals(PennantConstants.DOC_TYPE_ZIP) || docType.equals(PennantConstants.DOC_TYPE_7Z)
					|| docType.equals(PennantConstants.DOC_TYPE_RAR)) {
				this.docDiv.getChildren().clear();
				this.docDiv.appendChild(getDocumentLink(fileName, docType, fileName, ddaImageData));
			}

			if (docType.equals(PennantConstants.DOC_TYPE_WORD) || docType.equals(PennantConstants.DOC_TYPE_MSG)
					|| docType.equals(PennantConstants.DOC_TYPE_EXCEL) || docType.equals(PennantConstants.DOC_TYPE_ZIP)
					|| docType.equals(PennantConstants.DOC_TYPE_7Z) || docType.equals(PennantConstants.DOC_TYPE_RAR)) {
				this.docDiv.setVisible(true);
				this.finDocumentPdfView.setVisible(false);
			} else {
				this.docDiv.setVisible(false);
				this.finDocumentPdfView.setVisible(true);
			}

			textbox.setValue(fileName);
			if (textbox.getAttribute("data") == null) {
				DocumentDetails documentDetails = new DocumentDetails(FinanceConstants.MODULE_NAME, "", docType,
						fileName, ddaImageData);
				documentDetails.setLovDescNewImage(true);
				textbox.setAttribute("data", documentDetails);
			} else {
				DocumentDetails documentDetails = (DocumentDetails) textbox.getAttribute("data");
				documentDetails.setDoctype(docType);
				documentDetails.setDocImage(ddaImageData);
				documentDetails.setLovDescNewImage(true);
				documentDetails.setDocRefId(null);
				documentDetails.setDocUri(null);
				textbox.setAttribute("data", documentDetails);
				covenantDocument.setDoctype(docType);
			}
		} catch (Exception ex) {
			logger.error(Literal.EXCEPTION, ex);
		}

		logger.debug(Literal.LEAVING);
	}

	protected boolean doCustomDelete(final CovenantDocument aCovenantDocument, String tranType) {
		tranType = PennantConstants.TRAN_DEL;
		AuditHeader auditHeader = newCovenantDocumentProcess(aCovenantDocument, tranType);
		auditHeader = ErrorControl.showErrorDetails(this.window_CovenantDocumentDialog, auditHeader);
		int retValue = auditHeader.getProcessStatus();
		if (retValue == PennantConstants.porcessCONTINUE || retValue == PennantConstants.porcessOVERIDE) {
			covenantsDialogCtrl.doFillCovenantDocument(this.covenantDocuments);
			return true;
		}

		return false;
	}

	private void doDelete() throws InterruptedException {
		logger.debug(Literal.ENTERING);

		final CovenantDocument aCovenantDocument = new CovenantDocument();
		BeanUtils.copyProperties(getCovenantDocument(), aCovenantDocument);

		final String keyReference = Labels.getLabel("label_CovenantDocumentDialog_ConvDocType.value") + " : "
				+ aCovenantDocument.getCovenantType();

		doDelete(keyReference, aCovenantDocument);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug(Literal.ENTERING);

		if (getCovenantDocument().isNewRecord()) {
		} else {
			this.convDocType.setReadonly(true);
		}

		if (StringUtils.isEmpty(frequncy)) {
			frequencyBox.setDisabled(true);
		}

		readOnlyComponent(isReadOnly("CovenantDialog_DocReceivedDate"), this.docReceivedDate);
		readOnlyComponent(isReadOnly("CovenantDialog_DocumentName"), this.documentName);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.covenantDocument.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			this.btnCtrl.setBtnStatus_Edit();
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public boolean isReadOnly(String componentName) {
		return getUserWorkspace().isReadOnly(componentName);
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug(Literal.ENTERING);

		readOnlyComponent(true, this.convDocType);
		readOnlyComponent(true, this.docReceivedDate);
		readOnlyComponent(true, this.documentName);
		readOnlyComponent(true, this.btnUploadDoc);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(true);
			}
		}

		if (isWorkFlowEnabled()) {
			this.recordStatus.setValue("");
			this.userAction.setSelectedIndex(0);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Clears the components values. <br>
	 */
	public void doClear() {
		logger.debug(Literal.ENTERING);

		this.convDocType.setValue("");
		this.convDocType.setDescription("");
		this.docReceivedDate.setValue(null);
		this.documentName.setValue("");

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug(Literal.ENTERING);

		final CovenantDocument aCovenantDocument = new CovenantDocument();
		BeanUtils.copyProperties(this.covenantDocument, aCovenantDocument);
		boolean isNew = false;

		doSetValidation();

		doWriteComponentsToBean(aCovenantDocument);

		isNew = aCovenantDocument.isNewRecord();
		String tranType = "";
		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aCovenantDocument.getRecordType())) {
				aCovenantDocument.setVersion(aCovenantDocument.getVersion() + 1);
				if (isNew) {
					aCovenantDocument.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aCovenantDocument.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aCovenantDocument.setNewRecord(true);
				}
			}
		} else {
			if (isNew) {
				aCovenantDocument.setVersion(1);
				aCovenantDocument.setRecordType(PennantConstants.RCD_ADD);
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
			if (StringUtils.isBlank(aCovenantDocument.getRecordType())) {
				aCovenantDocument.setVersion(aCovenantDocument.getVersion() + 1);
				aCovenantDocument.setRecordType(PennantConstants.RCD_UPD);
			}
			if (aCovenantDocument.getRecordType().equals(PennantConstants.RCD_ADD) && isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else if (aCovenantDocument.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				aCovenantDocument.setVersion(aCovenantDocument.getVersion() + 1);
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		try {
			AuditHeader auditHeader = newCovenantDocumentProcess(aCovenantDocument, tranType);
			auditHeader = ErrorControl.showErrorDetails(this.window_CovenantDocumentDialog, auditHeader);
			int retValue = auditHeader.getProcessStatus();

			if (retValue == PennantConstants.porcessCONTINUE || retValue == PennantConstants.porcessOVERIDE) {
				covenantsDialogCtrl.doFillCovenantDocument(this.covenantDocuments);
				closeDialog();
			}

		} catch (final DataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
			showMessage(e);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * This method validates FinTypeExpense details <br>
	 * and will return AuditHeader
	 *
	 */
	private AuditHeader newCovenantDocumentProcess(CovenantDocument aCovenantDocument, String tranType) {
		boolean recordAdded = false;
		AuditHeader auditHeader = getAuditHeader(aCovenantDocument, tranType);
		covenantDocuments = new ArrayList<CovenantDocument>();
		String[] valueParm = new String[1];
		String[] errParm = new String[1];
		valueParm[0] = aCovenantDocument.getDocCategory();
		errParm[0] = PennantJavaUtil.getLabel("label_CovenantDocumentDialog_ConvDocType.value") + ":" + valueParm[0];
		List<CovenantDocument> existingcovenantDocs = null;

		existingcovenantDocs = covenantsDialogCtrl.getCovenantDocuments();

		if (aCovenantDocument.isNewRecord() && PennantConstants.RCD_ADD.equals(aCovenantDocument.getRecordType())) {
			covenantDocuments.add(aCovenantDocument);
			return auditHeader;
		}

		if (CollectionUtils.isNotEmpty(existingcovenantDocs)) {
			for (int i = 0; i < existingcovenantDocs.size(); i++) {
				CovenantDocument covenantDocument = existingcovenantDocs.get(i);
				if (StringUtils.equals(covenantDocument.getDocCategory(), aCovenantDocument.getDocCategory())) {
					// Both Current and Existing list rating same
					if (aCovenantDocument.isNewRecord()) {
						auditHeader.setErrorDetails(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41008", errParm, valueParm),
								getUserWorkspace().getUserLanguage()));
						return auditHeader;
					}
					if (tranType == PennantConstants.TRAN_DEL) {
						if (aCovenantDocument.getRecordType().equals(PennantConstants.RECORD_TYPE_UPD)) {
							aCovenantDocument.setRecordType(PennantConstants.RECORD_TYPE_DEL);
							recordAdded = true;
							covenantDocuments.add(aCovenantDocument);
						} else if (aCovenantDocument.getRecordType().equals(PennantConstants.RCD_ADD)) {
							recordAdded = true;
						} else if (aCovenantDocument.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							aCovenantDocument.setRecordType(PennantConstants.RECORD_TYPE_CAN);
							recordAdded = true;
							covenantDocuments.add(aCovenantDocument);
						} else if (aCovenantDocument.getRecordType().equals(PennantConstants.RECORD_TYPE_CAN)) {
							recordAdded = true;
							List<CovenantDocument> savedList = covenantsDialogCtrl.getCovenantDocuments();
							for (int j = 0; j < savedList.size(); j++) {
								CovenantDocument accType = savedList.get(j);
								if (accType.getCovenantId() == aCovenantDocument.getCovenantId()) {
									covenantDocuments.add(accType);
								}
							}
						} else if (aCovenantDocument.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							aCovenantDocument.setNewRecord(true);
						}
					} else {
						if (tranType != PennantConstants.TRAN_UPD) {
							covenantDocuments.add(covenantDocument);
						}
					}
				} else {
					covenantDocuments.add(covenantDocument);
				}
			}
		}
		if (!recordAdded) {
			covenantDocuments.add(aCovenantDocument);
		}
		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	@Override
	protected void doClearMessage() {
		logger.debug(Literal.ENTERING);

		this.convDocType.setErrorMessage("");
		this.docReceivedDate.setErrorMessage("");
		this.documentName.setErrorMessage("");

		logger.debug(Literal.LEAVING);
	}

	/**
	 * 
	 * @param acCovenantDocument
	 * @param tranType
	 * @return
	 */
	private AuditHeader getAuditHeader(CovenantDocument acCovenantDocument, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, acCovenantDocument.getBefImage(), acCovenantDocument);
		return new AuditHeader(acCovenantDocument.getCovenantType(), null, null, null, auditDetail,
				acCovenantDocument.getUserDetails(), getOverideMap());
	}

	private void showMessage(Exception e) {
		AuditHeader auditHeader = new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_UNDEF, e.getMessage(), null));
			ErrorControl.showErrorControl(this.window_CovenantDocumentDialog, auditHeader);
		} catch (Exception exp) {
			logger.error(Literal.EXCEPTION, exp);
		}
	}

	public void onClick$btnNotes(Event event) {
		doShowNotes(this.covenantDocument);
	}

	@Override
	protected String getReference() {
		return String.valueOf(this.covenantDocument.getId());
	}

	public void setValidationOn(boolean validationOn) {
		this.validationOn = validationOn;
	}

	public boolean isValidationOn() {
		return this.validationOn;
	}

	public CovenantDocument getCovenantDocument() {
		return covenantDocument;
	}

	public void setCovenantDocument(CovenantDocument covenantDocument) {
		this.covenantDocument = covenantDocument;
	}

	public void setCovenantsDialogCtrl(CovenantsDialogCtrl covenantsDialogCtrl) {
		this.covenantsDialogCtrl = covenantsDialogCtrl;
	}

	public List<Property> fillfrequencyDates(List<Property> list, boolean isNewRecord, String recordType) {
		if (isNewRecord && recordType == null) {
			if (covenantsDialogCtrl.getCovenantDocuments() != null) {
				for (CovenantDocument covDoc : covenantsDialogCtrl.getCovenantDocuments()) {
					if (covDoc.getFrequencyDate() != null) {
						// frequencies.add(new Property(covDoc.getFrequencyDate(), key));
						for (int i = 0; i < list.size(); i++) {
							if (list.get(i).getKey().equals(covDoc.getFrequencyDate())) {
								list.remove(i);
							}
						}

					}
				}
			}

		}
		return list;
	}

	public void onClick$btnDownload(Event event) {
		doDownload();
	}

	/**
	 * To Download the upload Document
	 */
	private void doDownload() {
		AMedia amedia = null;
		byte[] docImage = null;

		DocumentDetails documentDetails = (DocumentDetails) this.documentName.getAttribute("data");

		if (documentDetails == null) {
			return;
		}

		if (documentDetails.getDocImage() == null) {
			docImage = dMSService.getById(documentDetails.getDocRefId());
		} else {
			docImage = documentDetails.getDocImage();
		}

		try (InputStream data = new ByteArrayInputStream(docImage)) {
			String docName = documentName.getValue();
			String doctype = documentDetails.getDoctype();
			if (doctype.equals(PennantConstants.DOC_TYPE_PDF)) {
				amedia = new AMedia(docName, "pdf", "application/pdf", data);
			} else if (doctype.equals(PennantConstants.DOC_TYPE_IMAGE)
					|| doctype.equals(PennantConstants.DOC_TYPE_JPG)) {
				amedia = new AMedia(docName, "jpeg", "image/jpeg", data);
			} else if (doctype.equals(PennantConstants.DOC_TYPE_WORD)
					|| doctype.equals(PennantConstants.DOC_TYPE_MSG)) {
				amedia = new AMedia(docName, "docx",
						"application/vnd.openxmlformats-officedocument.wordprocessingml.document", data);
			} else if (doctype.equals(PennantConstants.DOC_TYPE_EXCEL)) {
				amedia = new AMedia(docName, "xlsx",
						"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", data);
			} else if (doctype.equals(PennantConstants.DOC_TYPE_ZIP)) {
				amedia = new AMedia(docName, "x-zip-compressed", "application/x-zip-compressed", data);
			} else if (doctype.equals(PennantConstants.DOC_TYPE_7Z)) {
				amedia = new AMedia(docName, "octet-stream", "application/octet-stream", data);
			} else if (doctype.equals(PennantConstants.DOC_TYPE_RAR)) {
				amedia = new AMedia(docName, "x-rar-compressed", "application/x-rar-compressed", data);
			}
			Filedownload.save(amedia);
		} catch (Exception e) {
			//
		}

	}

	public void setdMSService(DMSService dMSService) {
		this.dMSService = dMSService;
	}

}
