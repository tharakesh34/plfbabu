package com.pennant.webui.financemanagement.regenerateagreement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Window;

import com.aspose.words.SaveFormat;
import com.pennant.ExtendedCombobox;
import com.pennant.backend.dao.documentdetails.DocumentDetailsDAO;
import com.pennant.backend.dao.documentdetails.DocumentManagerDAO;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.applicationmaster.AgreementDefinition;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.extendedfield.ExtendedField;
import com.pennant.backend.model.extendedfield.ExtendedFieldData;
import com.pennant.backend.model.extendedfield.ExtendedFieldRender;
import com.pennant.backend.model.finance.AgreementDetail;
import com.pennant.backend.model.finance.FinReceiptHeader;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.service.applicationmaster.AgreementDefinitionService;
import com.pennant.backend.service.extendedfields.ExtendedFieldDetailsService;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.util.ExtendedFieldConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.util.AgreementEngine;
import com.pennant.util.AgreementGeneration;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.constants.FinServiceEvent;

/**
 * This is the controller class for the /WEB-INF/pages/Finance Management/RegenerateAgreement/RegenerateAgreement.zul
 */
public class RegenerateAgreementCtrl extends GFCBaseCtrl<FinReceiptHeader> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8965600625656119486L;

	private static final Logger logger = LogManager.getLogger(RegenerateAgreementCtrl.class);

	private ExtendedFieldDetailsService extendedFieldDetailsService;
	private FinanceDetailService financeDetailService;
	private AgreementDefinitionService agreementDefinitionService;
	private DocumentDetailsDAO documentDetailsDAO;
	private AgreementGeneration agreementGeneration;
	private DocumentManagerDAO documentManagerDAO;

	protected Window window_RegenerateAgreement;
	protected Combobox agreementCode;
	protected ExtendedCombobox loanReference;
	protected Button btnProceed;
	protected Button btnView;
	protected Button btnRegenerate;
	protected DocumentDetails detail;
	protected AgreementDefinition aggDef;

	/**
	 * default constructor.<br>
	 */
	public RegenerateAgreementCtrl() {
		super();
	}

	public void onCreate$window_RegenerateAgreement(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_RegenerateAgreement);

		doSetFieldProperties();
		try {

		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_RegenerateAgreement.onClose();
		}

		logger.debug("Leaving" + event.toString());
	}

	private void doSetFieldProperties() {
		this.loanReference.setMandatoryStyle(true);

		this.loanReference.setButtonDisabled(false);
		this.loanReference.setTextBoxWidth(155);
		this.loanReference.setMandatoryStyle(true);
		this.loanReference.setModuleName("FinanceMain");
		this.loanReference.setValueColumn("FinReference");
		this.loanReference.setDescColumn("FinType");
		this.loanReference.setValidateColumns(new String[] { "FinReference" });

		List<String> aggrements = documentDetailsDAO.getRegenerateAggDocTypes();
		ArrayList<ValueLabel> productList = new ArrayList<ValueLabel>();
		for (String aggrement : aggrements) {
			ValueLabel product = new ValueLabel(aggrement, aggrement);
			productList.add(product);
		}

		fillComboBox(this.agreementCode, "", productList, "");

	}

	/**
	 * On Selecting
	 * 
	 * @param event
	 */
	public void onSelect$agreementCode(Event event) {
		logger.debug("Entering" + event.toString());
		this.btnView.setVisible(false);
		this.btnRegenerate.setVisible(false);
		logger.debug("Leaving" + event.toString());
	}

	public void onFulfill$loanReference(Event event) {
		logger.debug("Entering");
		this.btnView.setVisible(false);
		this.btnRegenerate.setVisible(false);
		Clients.clearWrongValue(this.loanReference);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * When user clicks on button "btnProceed" button
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnProceed(Event event) throws Exception {
		logger.debug("Entering " + event.toString());

		ArrayList<WrongValueException> wve = new ArrayList<>();
		if ("#".equals(getComboboxValue(this.agreementCode))) {
			wve.add(new WrongValueException(this.agreementCode, Labels.getLabel("FIELD_IS_MAND",
					new String[] { Labels.getLabel("label_RegenerateAgreement_AgreementCode.value") })));
		}
		if (StringUtils.trimToNull(this.loanReference.getValue()) == null) {
			wve.add(new WrongValueException(this.loanReference, Labels.getLabel("FIELD_IS_MAND",
					new String[] { Labels.getLabel("label_RegenerateAgreement_LoanReference.value") })));
		}

		if (!wve.isEmpty()) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		this.aggDef = agreementDefinitionService.getAgreementDefinitionByCode(this.agreementCode.getValue(), "_View");

		if (aggDef == null || StringUtils.isEmpty(aggDef.getDocType())) {
			MessageUtil.showError("Agreement is not autogenerated");
			return;
		}

		String docType = StringUtils.trimToNull(aggDef.getDocType());

		long docId = documentDetailsDAO.getDocIdByDocTypeAndFinRef(this.loanReference.getValue(), docType, "_view");
		this.detail = documentDetailsDAO.getDocumentDetailsById(docId, "_View", true);

		if (detail == null) {
			MessageUtil.showError("Document does not exists for the given details.");
			return;
		}

		if (PennantConstants.DOC_TYPE_WORD.equals(docType) || PennantConstants.DOC_TYPE_MSG.equals(docType)
				|| PennantConstants.DOC_TYPE_DOC.equals(docType) || PennantConstants.DOC_TYPE_DOCX.equals(docType)
				|| PennantConstants.DOC_TYPE_EXCEL.equals(docType)) {
			this.btnView.setLabel("Download");
		} else {
			this.btnView.setLabel("View");
		}
		this.btnView.setVisible(true);
		this.btnRegenerate.setVisible(true);
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * When user clicks on button "btnView" button
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnView(Event event) throws Exception {
		logger.debug("Entering " + event.toString());
		displayDocDetails(this.detail);
		logger.debug("Leaving " + event.toString());
	}

	private void displayDocDetails(DocumentDetails detail) {
		if (StringUtils.isNotBlank(detail.getDocName()) && detail.getDocImage() != null
				&& StringUtils.isNotBlank(detail.getDocImage().toString())) {
			try {
				if (StringUtils.trimToEmpty(detail.getDoctype()).equals(PennantConstants.DOC_TYPE_WORD)
						|| StringUtils.trimToEmpty(detail.getDoctype()).equals(PennantConstants.DOC_TYPE_MSG)
						|| StringUtils.trimToEmpty(detail.getDoctype()).equals(PennantConstants.DOC_TYPE_DOC)
						|| StringUtils.trimToEmpty(detail.getDoctype()).equals(PennantConstants.DOC_TYPE_DOCX)
						|| StringUtils.trimToEmpty(detail.getDoctype()).equals(PennantConstants.DOC_TYPE_EXCEL)) {
					Filedownload.save(detail.getDocImage(), "application/octet-stream", detail.getDocName());
				} else {
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("FinDocumentDetail", detail);
					Executions.createComponents("/WEB-INF/pages/util/ImageView.zul", null, map);
				}
			} catch (Exception e) {
				logger.debug(e);
			}
		} else if (StringUtils.isNotBlank(detail.getDocUri())) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("documentRef", detail);
			Executions.createComponents("/WEB-INF/pages/util/ImageView.zul", null, map);
		} else {
			MessageUtil.showError("Document Details not Found.");
		}

	}

	/**
	 * When user clicks on button "btnRegenerate" button
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnRegenerate(Event event) throws Exception {
		logger.debug("Entering " + event.toString());

		if (MessageUtil.YES == MessageUtil.confirm(
				"The selected agreement will be regenerated and uploaded against the loan. Do you want to proceed?")) {
		} else {
			return;
		}

		Long finID = financeDetailService.getFinID(detail.getReferenceId());

		FinanceDetail financeDetail = getFinanceDetails(finID);

		AgreementDetail agreementData = getAgreementGeneration().getAggrementData(financeDetail,
				this.aggDef.getAggImage(), getUserWorkspace().getUserDetails());
		detail.setDocName(this.aggDef.getAggReportName());
		generateDocument(financeDetail, detail, agreementData);
		logger.debug("Leaving " + event.toString());
	}

	private FinanceDetail getFinanceDetails(long finID) {
		logger.debug("Enetring");
		FinanceDetail fd = null;
		try {
			fd = financeDetailService.getFinanceDetailById(finID, false, "", false, FinServiceEvent.ORG, "");

			if (fd != null) {
				String finReference = fd.getFinScheduleData().getFinanceMain().getFinReference();
				List<ExtendedField> extField = extendedFieldDetailsService.getExtndedFieldDetails(
						ExtendedFieldConstants.MODULE_LOAN, fd.getFinScheduleData().getFinanceMain().getFinCategory(),
						FinServiceEvent.ORG, finReference);
				ExtendedFieldRender exdFieldRender = new ExtendedFieldRender();
				for (ExtendedField extendedField : extField) {
					Map<String, Object> mapValues = new HashMap<String, Object>();
					for (ExtendedFieldData extFieldData : extendedField.getExtendedFieldDataList()) {
						mapValues.put(extFieldData.getFieldName(), extFieldData.getFieldValue());
						exdFieldRender.setMapValues(mapValues);
					}
				}
				fd.setExtendedFieldRender(exdFieldRender);
			} else {
				fd = new FinanceDetail();
			}

		} catch (Exception e) {
			logger.error("Exception: ", e);
			fd = new FinanceDetail();
			return fd;
		}

		logger.debug("Leaving");
		return fd;

	}

	private void generateDocument(FinanceDetail financeDetail, DocumentDetails docDetails,
			AgreementDetail agreementDetail) {
		String docName = docDetails.getDocName();
		String docType = docDetails.getDoctype();
		AgreementEngine engine;
		try {
			engine = new AgreementEngine();
			engine.setTemplate(docName);
			engine.loadTemplate();
			engine.mergeFields(agreementDetail);

			if (StringUtils.equalsIgnoreCase(PennantConstants.DOC_TYPE_PDF, docType)
					&& docDetails.getDocName().endsWith(PennantConstants.DOC_TYPE_WORD_EXT)) {
				docDetails.setDocName(docDetails.getDocName().replaceAll(PennantConstants.DOC_TYPE_WORD_EXT,
						PennantConstants.DOC_TYPE_PDF_EXT));
				docDetails.setDocImage(engine.getDocumentInByteArray(docDetails.getDocName(), SaveFormat.PDF));
			} else if (StringUtils.equalsIgnoreCase(PennantConstants.DOC_TYPE_DOCX, docType)
					&& docDetails.getDocName().endsWith(PennantConstants.DOC_TYPE_PDF_EXT)) {
				docDetails.setDocName(docDetails.getDocName().replaceAll(PennantConstants.DOC_TYPE_PDF_EXT,
						PennantConstants.DOC_TYPE_WORD_EXT));
				docDetails.setDocImage(engine.getDocumentInByteArray(docDetails.getDocName(), SaveFormat.DOCX));
			}

			documentManagerDAO.update(docDetails.getDocRefId(), docDetails.getDocImage());
			displayDocDetails(docDetails);
		} catch (Exception e) {
			MessageUtil.showError("Template does not exists");
			e.printStackTrace();
		}

	}

	public ExtendedFieldDetailsService getExtendedFieldDetailsService() {
		return extendedFieldDetailsService;
	}

	public void setExtendedFieldDetailsService(ExtendedFieldDetailsService extendedFieldDetailsService) {
		this.extendedFieldDetailsService = extendedFieldDetailsService;
	}

	public AgreementDefinitionService getAgreementDefinitionService() {
		return agreementDefinitionService;
	}

	public void setAgreementDefinitionService(AgreementDefinitionService agreementDefinitionService) {
		this.agreementDefinitionService = agreementDefinitionService;
	}

	public DocumentDetailsDAO getDocumentDetailsDAO() {
		return documentDetailsDAO;
	}

	public void setDocumentDetailsDAO(DocumentDetailsDAO documentDetailsDAO) {
		this.documentDetailsDAO = documentDetailsDAO;
	}

	public FinanceDetailService getFinanceDetailService() {
		return financeDetailService;
	}

	public void setFinanceDetailService(FinanceDetailService financeDetailService) {
		this.financeDetailService = financeDetailService;
	}

	public AgreementGeneration getAgreementGeneration() {
		return agreementGeneration;
	}

	public void setAgreementGeneration(AgreementGeneration agreementGeneration) {
		this.agreementGeneration = agreementGeneration;
	}

	public DocumentManagerDAO getDocumentManagerDAO() {
		return documentManagerDAO;
	}

	public void setDocumentManagerDAO(DocumentManagerDAO documentManagerDAO) {
		this.documentManagerDAO = documentManagerDAO;
	}

}
