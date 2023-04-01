package com.pennant.webui.finance.dms;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.backend.dao.collateral.CollateralSetupDAO;
import com.pennant.backend.dao.documentdetails.DocumentDetailsDAO;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.collateral.CollateralAssignment;
import com.pennant.backend.model.collateral.CollateralSetup;
import com.pennant.backend.model.customermasters.CustomerDocument;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.JointAccountDetail;
import com.pennant.backend.util.CollateralConstants;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.finance.financemain.DocumentDetailDialogCtrl;
import com.pennant.webui.finance.financemain.FinBasicDetailsCtrl;
import com.pennant.webui.finance.financemain.FinanceMainBaseCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.external.dms.model.ExternalDocument;
import com.pennanttech.model.dms.DMSModule;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.dms.model.DMSQueue;
import com.pennanttech.pennapps.dms.service.DMSService;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.pff.document.DocumentCategories;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.constants.FinServiceEvent;
import com.pennanttech.pff.external.DocumentManagementService;

public class DMSDialogCtrl extends GFCBaseCtrl<DocumentDetails> {
	private static final long serialVersionUID = -5248483770568884148L;
	private static final Logger logger = LogManager.getLogger(DMSDialogCtrl.class);

	protected Window window_DocManagementControlDialog;
	protected Groupbox finBasicdetails;
	protected Listbox listBoxDMSDocuments;
	protected Button retrieveDMS;
	protected Button sendDMS;

	private FinBasicDetailsCtrl finBasicDetailsCtrl;
	protected DMSService dmsService;
	protected DocumentManagementService documentManagementService;

	private List<DocumentDetails> finDocumentDetailList = new ArrayList<DocumentDetails>();
	private Object financeMainDialogCtrl;
	private List<ValueLabel> custCifs = new ArrayList<ValueLabel>();
	private List<ValueLabel> collateralsList = new ArrayList<ValueLabel>();
	private DocumentDetailsDAO documentDetailsDAO;
	private CollateralSetupDAO collateralSetupDAO;

	ArrayList<ValueLabel> documentTypes = PennantAppUtil.getDocumentTypes();
	Tab tab = null;

	public DMSDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "DMSDialog";
	}

	public void onCreate$window_DocManagementControlDialog(Event event) {
		logger.debug(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(window_DocManagementControlDialog);

		if (arguments.containsKey("finHeaderList") && arguments.get("finHeaderList") != null) {
			appendFinBasicDetails(arguments.get("finHeaderList"));
		}

		if (arguments.containsKey("financeMainDialogCtrl")) {
			this.financeMainDialogCtrl = arguments.get("financeMainDialogCtrl");

			if (financeMainDialogCtrl instanceof FinanceMainBaseCtrl) {
				((FinanceMainBaseCtrl) financeMainDialogCtrl).setDmsDialogCtrl(this);
			}
		}

		if (arguments.containsKey("tab")) {
			tab = (Tab) arguments.get("tab");
		}

		try {

			if (isWorkFlowEnabled() && !enqiryModule) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(), this.pageRightName);
			}

			doSetFieldProperties();
			checkRights();
			doShowDialog();
		} catch (Exception e) {
			closeDialog();
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	private void appendFinBasicDetails(Object finHeaderList) {
		logger.debug(Literal.ENTERING);
		try {
			final HashMap<String, Object> map = new HashMap<>();
			map.put("parentCtrl", this);
			map.put("finHeaderList", finHeaderList);
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/FinBasicDetails.zul", this.finBasicdetails,
					map);
		} catch (Exception e) {
			logger.debug(e);
		}
		logger.debug(Literal.LEAVING);
	}

	private void doSetFieldProperties() {
		// TODO Auto-generated method stub

	}

	private void doShowDialog() {
		logger.debug(Literal.ENTERING);

		if (enqiryModule) {
			this.btnCtrl.setBtnStatus_Enquiry();
		}

		logger.debug(Literal.LEAVING);
	}

	public void onClick$retrieveDMS(Event event) {
		logger.debug(Literal.ENTERING);

		try {
			ExternalDocument extDocument = new ExternalDocument();
			FinanceDetail financeDetail = ((FinanceMainBaseCtrl) financeMainDialogCtrl).getFinanceDetail();
			if (financeMainDialogCtrl != null) {
				if (financeMainDialogCtrl instanceof FinanceMainBaseCtrl) {
					String applicationNo = ((FinanceMainBaseCtrl) financeMainDialogCtrl).getApplicationNo();
					String leadId = ((FinanceMainBaseCtrl) financeMainDialogCtrl).getLeadId();
					extDocument.setLeadId(leadId);
					extDocument.setApplicationNumber(applicationNo);
					extDocument.setFinReference(financeDetail.getFinScheduleData().getFinanceMain().getFinReference());

					if (StringUtils.isNotEmpty(App.getProperty("exteranal.interface.dms.leadId.based"))) {
						extDocument.setLeadId(financeDetail.getFinScheduleData().getFinanceMain().getOfferId());
					}
					custCifs = ((FinanceMainBaseCtrl) financeMainDialogCtrl).getCustomerCifs();
					collateralsList = ((FinanceMainBaseCtrl) financeMainDialogCtrl).getAssignedCollateralRefs();
				}
			}

			List<ExternalDocument> listOfExternalDocs = documentManagementService.getExternalDocument(extDocument);

			doFillExtDocuments(listOfExternalDocs);
		} catch (Exception e) {
			logger.debug(Literal.EXCEPTION, e);
			MessageUtil.showMessage(Labels.getLabel("label_DMSDialog_error"));
		}

		logger.debug(Literal.LEAVING);
	}

	private void doFillExtDocuments(List<ExternalDocument> listOfExternalDocs) {
		logger.debug(Literal.ENTERING);

		this.listBoxDMSDocuments.getItems().clear();
		if (CollectionUtils.isNotEmpty(listOfExternalDocs)) {
			for (ExternalDocument documentDetail : listOfExternalDocs) {

				Listitem listitem = new Listitem();
				Listcell listcell;
				String docdesc = getlabelDesc(documentDetail.getDocumentType(), documentTypes);
				listcell = new Listcell(documentDetail.getDocumentType() + " - " + docdesc);
				listitem.appendChild(listcell);

				listcell = new Listcell(StringUtils.trimToEmpty(String.valueOf(documentDetail.getDocRefId())));
				listitem.appendChild(listcell);

				listcell = new Listcell(documentDetail.getRevisedDate());
				listitem.appendChild(listcell);

				listcell = new Listcell(StringUtils.trimToEmpty(documentDetail.getDocName()));
				listitem.appendChild(listcell);

				if (StringUtils.equalsIgnoreCase(documentDetail.getCategoryOfDocument(), "Customer")) {
					listcell = new Listcell();
					Combobox custCif = new Combobox();
					custCif.setId(String.valueOf(documentDetail.getDocRefId()) + "_Cif");
					fillComboBox(custCif, "", custCifs, "");
					listcell.appendChild(custCif);
					listitem.appendChild(listcell);

				} else if (StringUtils.contains(documentDetail.getCategoryOfDocument(), "Collateral")) {
					listcell = new Listcell();
					Combobox coll = new Combobox();
					coll.setId(String.valueOf(documentDetail.getDocRefId()) + "_coll");
					fillComboBox(coll, "", collateralsList, "");
					listcell.appendChild(coll);
					listitem.appendChild(listcell);

				} else {
					listcell = new Listcell(StringUtils.trimToEmpty(documentDetail.getCategoryOfDocument()));
					listitem.appendChild(listcell);
				}

				if (StringUtils.equalsIgnoreCase(documentDetail.getCategoryOfDocument(), "Customer")) {
					listcell = new Listcell();
					ExtendedCombobox docCategory = new ExtendedCombobox();
					docCategory.setId(String.valueOf(documentDetail.getDocRefId()) + "_CatOfDoc");
					docCategory.setModuleName("DocumentType");
					docCategory.setValueColumn("DocTypeCode");
					docCategory.setDescColumn("DocTypeDesc");
					docCategory.setValidateColumns(new String[] { "DocTypeCode" });

					Filter docCategoryFilter[] = new Filter[1];
					docCategoryFilter[0] = new Filter("categoryid", 1, Filter.OP_EQUAL);
					docCategory.setFilters(docCategoryFilter);
					docCategory.addForward("onFulfill", self, "onFulfillDocCategory", listitem);
					docCategory.setReadonly(true);
					docCategory.setButtonDisabled(true);
					listcell.appendChild(docCategory);
					listitem.appendChild(listcell);
					listcell.setVisible(false);

				} else if (StringUtils.contains(documentDetail.getCategoryOfDocument(), "Collateral")) {
					listcell = new Listcell();
					ExtendedCombobox docCategory = new ExtendedCombobox();
					docCategory.setId(String.valueOf(documentDetail.getDocRefId()) + "_CatOfDoc");
					docCategory.setModuleName("DocumentType");
					docCategory.setValueColumn("DocTypeCode");
					docCategory.setDescColumn("DocTypeDesc");
					docCategory.setValidateColumns(new String[] { "DocTypeCode" });

					Filter docCategoryFilter[] = new Filter[1];
					docCategoryFilter[0] = new Filter("categoryid", 1, Filter.OP_EQUAL);
					docCategory.setFilters(docCategoryFilter);
					docCategory.addForward("onFulfill", self, "onFulfillDocCategory", listitem);
					docCategory.setReadonly(true);
					docCategory.setButtonDisabled(true);
					listcell.appendChild(docCategory);
					listitem.appendChild(listcell);
					listcell.setVisible(false);
				}

				listcell = new Listcell();
				Button viewBtn = new Button(Labels.getLabel("listheader_DMS_DocView.label"));
				viewBtn.addForward("onClick", window_DocManagementControlDialog, "onDocViewButtonClicked",
						documentDetail);
				listcell.appendChild(viewBtn);
				viewBtn.setStyle("font-weight:bold;");
				listitem.appendChild(listcell);

				listitem.setAttribute("document", documentDetail);
				this.listBoxDMSDocuments.appendChild(listitem);
			}
		}
		logger.debug(Literal.LEAVING);
	}

	public void onDocViewButtonClicked(Event event) {
		logger.debug(Literal.ENTERING + event.toString());

		ExternalDocument documentDetail = (ExternalDocument) event.getData();
		DocumentDetails docDetail = new DocumentDetails();

		DMSQueue dmsQueue = dmsService.getImageByUri(documentDetail.getDocRefId());
		documentDetail.setDocImage(dmsQueue.getDocImage());
		documentDetail.setDocExtn(dmsQueue.getDocExt());
		if (dmsQueue.getDocImage() != null && StringUtils.isNotBlank(dmsQueue.getDocImage().toString())) {
			if (StringUtils.equalsIgnoreCase(documentDetail.getDocExtn(), PennantConstants.DOC_TYPE_WORD)
					|| StringUtils.equalsIgnoreCase(documentDetail.getDocExtn(), PennantConstants.DOC_TYPE_MSG)
					|| StringUtils.equalsIgnoreCase(documentDetail.getDocExtn(), PennantConstants.DOC_TYPE_DOC)
					|| StringUtils.equalsIgnoreCase(documentDetail.getDocExtn(), PennantConstants.DOC_TYPE_DOCX)
					|| StringUtils.equalsIgnoreCase(documentDetail.getDocExtn(), PennantConstants.DOC_TYPE_EXCEL)) {
				Filedownload.save(documentDetail.getDocImage(), StringUtils.upperCase(documentDetail.getDocExtn()),
						documentDetail.getDocName());
			} else {
				try {
					HashMap<String, Object> map = new HashMap<String, Object>();
					docDetail.setDoctype(documentDetail.getDocExtn());
					if (StringUtils.equalsIgnoreCase(docDetail.getDoctype(), "JPEG")
							|| StringUtils.equalsIgnoreCase(docDetail.getDoctype(), PennantConstants.DOC_TYPE_JPG)) {
						docDetail.setDoctype(PennantConstants.DOC_TYPE_IMAGE);
					}
					docDetail.setDoctype(docDetail.getDoctype().toUpperCase());
					docDetail.setDocImage(dmsQueue.getDocImage());
					map.put("FinDocumentDetail", docDetail);
					Executions.createComponents("/WEB-INF/pages/util/ImageView.zul", null, map);
				} catch (Exception e) {
					logger.debug(e);
				}
			}
		} else {
			MessageUtil.showError("Document Details not Found.");
		}

		logger.debug(Literal.LEAVING + event.toString());
	}

	public void onClick$sendDMS(Event event) {
		logger.debug(Literal.ENTERING);

		try {
			List<ExternalDocument> updatedDocumentsList = null;
			List<ExternalDocument> addFinDocuments = new ArrayList<>();

			if (financeMainDialogCtrl != null) {
				if (financeMainDialogCtrl instanceof FinanceMainBaseCtrl) {
					DocumentDetailDialogCtrl documentDetailDialogCtrl = ((FinanceMainBaseCtrl) financeMainDialogCtrl)
							.getDocumentDetailDialogCtrl();

					String applicationNo = ((FinanceMainBaseCtrl) financeMainDialogCtrl).getApplicationNo();

					FinanceDetail financeDetail = ((FinanceMainBaseCtrl) financeMainDialogCtrl).getFinanceDetail();
					String custCIF = financeDetail.getCustomerDetails().getCustomer().getCustCIF();
					String offerId = financeDetail.getFinScheduleData().getFinanceMain().getOfferId();

					// Finance Documents
					if (documentDetailDialogCtrl != null) {

						List<DocumentDetails> documentDetailList = documentDetailDialogCtrl.getDocumentDetailsList();
						if (CollectionUtils.isNotEmpty(documentDetailList)) {
							updatedDocumentsList = new ArrayList<ExternalDocument>();
							for (DocumentDetails documentDetails : documentDetailList) {
								if (!StringUtils.equals(StringUtils.trimToEmpty(documentDetails.getCategoryCode()),
										DocumentCategories.CUSTOMER.getKey())) {
									if (!documentDetails.isDocReceived()) {
										if (StringUtils.isNotBlank(documentDetails.getDocUri())) {
											ExternalDocument extDocument = new ExternalDocument();
											extDocument.setDocRefId(documentDetails.getDocUri());
											extDocument.setFinReference(documentDetails.getReferenceId());
											extDocument.setCustCIF(StringUtils.trimToEmpty(custCIF));
											extDocument.setRemarks1(documentDetails.getRemarks());
											extDocument.setLeadId(StringUtils.trimToEmpty(offerId));
											updatedDocumentsList.add(extDocument);
										} else {
											byte[] docImage = dmsService.getById(documentDetails.getDocRefId());
											if (docImage != null) {
												ExternalDocument extDocument = new ExternalDocument();
												extDocument.setLeadId(StringUtils.trimToEmpty(offerId));
												extDocument.setCustCIF(StringUtils.trimToEmpty(custCIF));
												extDocument.setApplicationNumber(applicationNo);
												extDocument.setFinReference(documentDetails.getReferenceId());
												extDocument.setDocumentType(documentDetails.getDocCategory());
												extDocument.setDocTypeDesc(documentDetails.getLovDescDocCategoryName());
												extDocument.setCategoryOfDocument(documentDetails.getDocModule());
												extDocument.setRemarks1(documentDetails.getRemarks());
												extDocument.setDocName(documentDetails.getDocName());
												extDocument.setDocImage(docImage);
												extDocument.setBranch(financeDetail.getFinScheduleData()
														.getFinanceMain().getLovDescFinBranchName());
												extDocument.setLoggedInUser(
														getUserWorkspace().getLoggedInUser().getUserName());

												addFinDocuments.add(extDocument);
											}
										}
									}
								}
							}

							if (CollectionUtils.isNotEmpty(updatedDocumentsList)) {
								documentManagementService.updateExternalDocuments(updatedDocumentsList);
							}

							if (CollectionUtils.isNotEmpty(addFinDocuments)) {
								documentManagementService.addExternalDocument(addFinDocuments);
							}
						}
					}

					// Customer Documents
					List<CustomerDocument> custDocuments = financeDetail.getCustomerDetails()
							.getCustomerDocumentsList();
					String applicantCIF = financeDetail.getCustomerDetails().getCustomer().getCustCIF();
					if (CollectionUtils.isNotEmpty(custDocuments)) {
						Map<String, List<ExternalDocument>> map = prepareUpdateDocumentsList(custDocuments,
								applicantCIF, applicationNo, financeDetail);

						if (CollectionUtils.isNotEmpty(map.get("update"))) {
							documentManagementService.updateExternalDocuments(map.get("update"));
						}

						if (CollectionUtils.isNotEmpty(map.get("add"))) {
							documentManagementService.addExternalDocument(map.get("add"));
						}
					}

					// CoApplicant Documents
					if (CollectionUtils.isNotEmpty(financeDetail.getJointAccountDetailList())) {
						for (JointAccountDetail coApplicant : financeDetail.getJointAccountDetailList()) {
							List<CustomerDocument> coApplicantDocs = coApplicant.getCustomerDetails()
									.getCustomerDocumentsList();

							if (CollectionUtils.isNotEmpty(coApplicantDocs)) {
								Map<String, List<ExternalDocument>> coAppMap = prepareUpdateDocumentsList(
										coApplicantDocs, coApplicant.getCustCIF(), applicationNo, financeDetail);
								if (CollectionUtils.isNotEmpty(coAppMap.get("update"))) {
									documentManagementService.updateExternalDocuments(coAppMap.get("update"));
								}
								if (CollectionUtils.isNotEmpty(coAppMap.get("add"))) {
									documentManagementService.addExternalDocument(coAppMap.get("add"));
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			logger.debug(Literal.EXCEPTION, e);
			MessageUtil.showMessage(Labels.getLabel("label_DMSDialog_error"));
			return;
		}
		MessageUtil.showMessage(Labels.getLabel("label_DMSDialog_upload"));
		logger.debug(Literal.LEAVING);
	}

	private Map<String, List<ExternalDocument>> prepareUpdateDocumentsList(List<CustomerDocument> documentsList,
			String applicantCIF, String applicationNo, FinanceDetail financeDetail) {

		Map<String, List<ExternalDocument>> map = null;
		try {
			map = new HashMap<String, List<ExternalDocument>>();
			List<ExternalDocument> updatedDocumentsList = new ArrayList<>();
			List<ExternalDocument> addFinDocuments = new ArrayList<>();

			for (CustomerDocument document : documentsList) {
				if (StringUtils.isNotBlank(document.getDocUri())) {
					ExternalDocument extDocument = new ExternalDocument();
					extDocument.setLeadId(financeDetail.getFinScheduleData().getFinanceMain().getOfferId());
					extDocument.setDocName(document.getCustDocName());
					extDocument.setDocRefId(document.getDocUri());
					extDocument.setCustCIF(applicantCIF);
					extDocument.setRemarks1(document.getRemarks());
					extDocument.setApplicationNumber(applicationNo);
					extDocument.setFinReference(financeDetail.getFinScheduleData().getFinanceMain().getFinReference());
					extDocument.setCategoryOfDocument(DocumentCategories.CUSTOMER.getKey());
					extDocument
							.setBranch(financeDetail.getFinScheduleData().getFinanceMain().getLovDescFinBranchName());
					extDocument.setLoggedInUser(getUserWorkspace().getLoggedInUser().getUserName());
					updatedDocumentsList.add(extDocument);
				} else {
					if (document.getDocRefId() != null) {
						byte[] docImage = dmsService.getById(document.getDocRefId());
						if (docImage != null) {
							ExternalDocument extDocument = new ExternalDocument();
							extDocument.setLeadId((financeDetail.getFinScheduleData().getFinanceMain().getOfferId()));
							extDocument.setDocName(document.getCustDocName());
							extDocument.setFinReference(
									financeDetail.getFinScheduleData().getFinanceMain().getFinReference());
							extDocument.setCustCIF(applicantCIF);
							extDocument.setDocumentType(document.getCustDocCategory());
							extDocument.setCategoryOfDocument(DocumentCategories.CUSTOMER.getKey());
							extDocument.setDocTypeDesc(document.getLovDescCustDocCategory());
							extDocument.setRemarks1(document.getRemarks());
							extDocument.setApplicationNumber(applicationNo);
							extDocument.setDocImage(docImage);
							extDocument.setBranch(
									financeDetail.getFinScheduleData().getFinanceMain().getLovDescFinBranchName());
							extDocument.setLoggedInUser(getUserWorkspace().getLoggedInUser().getUserName());
							addFinDocuments.add(extDocument);
						}
					}
				}
			}

			map.put("update", updatedDocumentsList);
			map.put("add", addFinDocuments);
		} catch (Exception e) {
			logger.debug(Literal.EXCEPTION, e);
		}
		return map;
	}

	public void onClick$saveDMS(Event event) {
		logger.debug(Literal.ENTERING);

		logger.debug(Literal.LEAVING);
	}

	public List<DocumentDetails> prepareFinanceDocuments(List<DocumentDetails> documentDetails) {
		logger.debug(Literal.ENTERING);

		List<DocumentDetails> newFinDocumentdetails = new ArrayList<>();
		Map<String, DocumentDetails> externalDocMap = new HashMap<>();
		FinanceDetail financeDetail = ((FinanceMainBaseCtrl) financeMainDialogCtrl).getFinanceDetail();
		String finReference = financeDetail.getFinScheduleData().getFinanceMain().getFinReference();
		String applicationNo = ((FinanceMainBaseCtrl) financeMainDialogCtrl).getApplicationNo();
		String leadId = ((FinanceMainBaseCtrl) financeMainDialogCtrl).getLeadId();

		if (CollectionUtils.isNotEmpty(documentDetails)) {
			for (DocumentDetails documentDetail : documentDetails) {
				if (StringUtils.equalsIgnoreCase(documentDetail.getDocModule(), FinanceConstants.MODULE_NAME)) {
					if (StringUtils.isNotBlank(documentDetail.getDocUri())) {
						if ((StringUtils.equals("DM", documentDetail.getDocCategory())
								|| StringUtils.equals("CAM", documentDetail.getDocCategory())
								|| StringUtils.equals("WL", documentDetail.getDocCategory())
								|| StringUtils.equals("SLP", documentDetail.getDocCategory())
								|| StringUtils.equals("SL", documentDetail.getDocCategory()))
								&& !financeDetail.isActionSave()) {
							String uri = documentDetail.getDocUri();
							documentDetail.setDocUri(null);
							externalDocMap.put(uri, documentDetail);
						} else {
							externalDocMap.put(documentDetail.getDocUri(), documentDetail);
						}
					} else {
						documentDetail.setApplicationNo(StringUtils.trimToEmpty(applicationNo));
						documentDetail.setLeadId(StringUtils.trimToEmpty(leadId));
						newFinDocumentdetails.add(documentDetail);
					}
				}
			}
		}

		if (this.listBoxDMSDocuments.getItemCount() > 0) {
			for (Listitem listitem : listBoxDMSDocuments.getItems()) {

				ExternalDocument extDoc = (ExternalDocument) listitem.getAttribute("document");

				if (!StringUtils.equalsIgnoreCase(extDoc.getCategoryOfDocument(), "FINANCE")
						&& !(StringUtils.equals("DM", extDoc.getDocumentType())
								|| StringUtils.equals("CAM", extDoc.getDocumentType())
								|| StringUtils.equals("WL", extDoc.getDocumentType())
								|| StringUtils.equals("SLP", extDoc.getDocumentType())
								|| StringUtils.equals("SL", extDoc.getDocumentType())
								|| StringUtils.equals("PERFIOS", extDoc.getDocumentType()))) {

					DocumentDetails docDetail = new DocumentDetails();
					docDetail.setDocUri(extDoc.getDocRefId());
					docDetail.setDocModule(FinanceConstants.MODULE_NAME);
					docDetail.setDocCategory(extDoc.getDocumentType());
					docDetail.setDocName(extDoc.getDocName());
					if (StringUtils.equalsAnyIgnoreCase(extDoc.getDocExtn(), "XLSX")
							|| StringUtils.equalsAnyIgnoreCase(extDoc.getDocExtn(), "XLS")) {
						docDetail.setDoctype(PennantConstants.DOC_TYPE_EXCEL);
					} else {
						docDetail.setDoctype(extDoc.getDocExtn().toUpperCase());
					}
					docDetail.setRemarks(extDoc.getRemarks1());
					docDetail.setDocReceivedDate(
							DateUtil.getDate(extDoc.getRevisedDate(), PennantConstants.DBDateTimeFormat));
					docDetail.setVersion(1);
					docDetail.setReferenceId(finReference);

					if (externalDocMap.containsKey(extDoc.getDocRefId())) {
						docDetail.setApplicationNo(StringUtils.trimToEmpty(applicationNo));
						docDetail.setLeadId(StringUtils.trimToEmpty(leadId));
						docDetail.setNewRecord(false);
						docDetail.setDocId(externalDocMap.get(extDoc.getDocRefId()).getDocId());
						docDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
						docDetail.setRecordStatus(externalDocMap.get(extDoc.getDocRefId()).getRecordStatus());
						DocumentDetails doc = externalDocMap.get(extDoc.getDocRefId());
						if (!doc.isLovDescNewImage()
								&& NumberUtils.toLong(doc.getDocUri()) < NumberUtils.toLong(extDoc.getDocRefId())) {
							externalDocMap.put(docDetail.getDocUri(), docDetail);
						}
					} else {
						boolean rcdFound = false;
						if (CollectionUtils.isNotEmpty(newFinDocumentdetails)) {
							for (DocumentDetails doc : newFinDocumentdetails) {
								if (StringUtils.equals(StringUtils.trimToEmpty(doc.getDocCategory()),
										extDoc.getDocumentType())
										&& !StringUtils.equals(StringUtils.trimToEmpty(doc.getRecordType()),
												PennantConstants.RECORD_TYPE_DEL)
										&& !StringUtils.equals(StringUtils.trimToEmpty(doc.getRecordType()),
												PennantConstants.RECORD_TYPE_CAN)) {
									rcdFound = true;
									if (!doc.isLovDescNewImage() && NumberUtils.toLong(doc.getDocUri()) < NumberUtils
											.toLong(extDoc.getDocRefId())) {
										doc.setDocUri(extDoc.getDocRefId());
										doc.setDoctype(extDoc.getDocExtn().toUpperCase());
										doc.setDocName(extDoc.getDocName());
										doc.setRecordType(PennantConstants.RECORD_TYPE_NEW);
										doc.setNewRecord(false);
										doc.setLastMntOn(new Timestamp(System.currentTimeMillis()));
										doc.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
									}
									break;
								}
							}
						}

						if (!rcdFound) {
							docDetail.setApplicationNo(StringUtils.trimToEmpty(applicationNo));
							docDetail.setLeadId(StringUtils.trimToEmpty(leadId));
							docDetail.setRecordType(PennantConstants.RCD_ADD);
							docDetail.setDocCategory(extDoc.getDocumentType());
							docDetail.setNewRecord(true);
							newFinDocumentdetails.add(docDetail);
						}
					}
					// Calling metaData update API to update that doc is received - Piramal implementation
					if (StringUtils.isNotEmpty(App.getProperty("exteranal.interface.dms.leadId.based"))) {
						documentManagementService.updateExternalDocuments(extDoc,
								financeDetail.getFinScheduleData().getFinanceMain().getCustID());

					}
				}
			}
		}

		newFinDocumentdetails.addAll(externalDocMap.values());

		logger.debug(Literal.LEAVING);
		return newFinDocumentdetails;
	}

	public Map<String, CustomerDocument> getAlreadyMappedUris(List<CustomerDocument> custDocList,
			Map<String, CustomerDocument> mappedUris) {

		if (CollectionUtils.isNotEmpty(custDocList)) {
			for (CustomerDocument custDoc : custDocList) {
				if (StringUtils.isNotBlank(custDoc.getDocUri())) {
					mappedUris.put(custDoc.getDocUri(), custDoc);
				}
			}
		}

		return mappedUris;
	}

	public List<CustomerDocument> prepareCustomerDocuments(List<CustomerDocument> custDocList, Long custId,
			Map<String, CustomerDocument> mappedUris) {
		logger.debug(Literal.ENTERING);

		List<CustomerDocument> newCustDocumentdetails = new ArrayList<>();
		Map<String, CustomerDocument> exteranlDocMap = new HashMap<>();
		List<WrongValueException> wve = new ArrayList<>();

		if (CollectionUtils.isNotEmpty(custDocList)) {
			for (CustomerDocument custDoc : custDocList) {
				String applicationNo = ((FinanceMainBaseCtrl) financeMainDialogCtrl).getApplicationNo();
				String leadId = ((FinanceMainBaseCtrl) financeMainDialogCtrl).getLeadId();
				custDoc.setApplicationNo(applicationNo);
				custDoc.setOfferId(leadId);
				newCustDocumentdetails.add(custDoc);
			}
		}

		if (this.listBoxDMSDocuments.getItemCount() > 0) {
			for (Listitem listitem : listBoxDMSDocuments.getItems()) {

				ExternalDocument extDoc = (ExternalDocument) listitem.getAttribute("document");

				if (StringUtils.equalsIgnoreCase(extDoc.getCategoryOfDocument(), "Customer")) {

					Combobox custIdCombobox = (Combobox) (listitem)
							.getFellowIfAny(String.valueOf(extDoc.getDocRefId()) + "_Cif");
					custIdCombobox.clearErrorMessage();
					Clients.clearWrongValue(custIdCombobox);
					String custIdVal = getComboboxValue(custIdCombobox);
					try {
						if (custIdVal.equals(PennantConstants.List_Select)) {
							throw new WrongValueException(custIdCombobox,
									Labels.getLabel("STATIC_INVALID", new String[] { "Customer CIF" }));
						}
					} catch (WrongValueException e) {
						wve.add(e);
					}

					long mappedCustId = NumberUtils.toLong(custIdVal);

					if (custId == mappedCustId) {

						CustomerDocument docDetail = new CustomerDocument();
						docDetail.setDocUri(extDoc.getDocRefId());
						docDetail.setCustDocName(extDoc.getDocName());
						if (StringUtils.equalsAnyIgnoreCase(extDoc.getDocExtn(), "XLSX")
								|| StringUtils.equalsAnyIgnoreCase(extDoc.getDocExtn(), "XLS")) {
							docDetail.setCustDocType(PennantConstants.DOC_TYPE_EXCEL);
						} else {
							docDetail.setCustDocType(extDoc.getDocExtn().toUpperCase());
						}
						docDetail.setRemarks(extDoc.getRemarks1());
						docDetail.setCustDocRcvdOn(DateUtil.getTimestamp(
								DateUtil.getDate(extDoc.getRevisedDate(), PennantConstants.DBDateTimeFormat)));
						docDetail.setCustDocIssuedCountry(PennantApplicationUtil.getDefaultCounty().getCountryCode());
						String applicationNo = ((FinanceMainBaseCtrl) financeMainDialogCtrl).getApplicationNo();
						docDetail.setApplicationNo(applicationNo);
						docDetail.setVersion(1);
						docDetail.setCustID(custId);
						docDetail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
						docDetail.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());

						boolean rcdFound = false;
						if (CollectionUtils.isNotEmpty(newCustDocumentdetails)) {
							for (CustomerDocument custDoc : newCustDocumentdetails) {
								if (StringUtils.equals(StringUtils.trimToEmpty(custDoc.getCustDocCategory()),
										extDoc.getDocumentType())
										&& !StringUtils.equals(StringUtils.trimToEmpty(custDoc.getRecordType()),
												PennantConstants.RECORD_TYPE_DEL)
										&& !StringUtils.equals(StringUtils.trimToEmpty(custDoc.getRecordType()),
												PennantConstants.RECORD_TYPE_CAN)) {
									rcdFound = true;
									if (!custDoc.isLovDescNewImage() && NumberUtils
											.toLong(custDoc.getDocUri()) < NumberUtils.toLong(extDoc.getDocRefId())) {
										custDoc.setDocUri(extDoc.getDocRefId());
										custDoc.setCustDocType(extDoc.getDocExtn().toUpperCase());
										custDoc.setCustDocName(extDoc.getDocName());
										custDoc.setRecordType(PennantConstants.RECORD_TYPE_NEW);
										custDoc.setNewRecord(false);
										custDoc.setLastMntOn(new Timestamp(System.currentTimeMillis()));
										custDoc.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
									}
									break;
								}
							}
						}

						if (!rcdFound) {
							docDetail.setRecordType(PennantConstants.RCD_ADD);
							docDetail.setCustDocCategory(extDoc.getDocumentType());
							docDetail.setNewRecord(true);
							newCustDocumentdetails.add(docDetail);
						}

						// Calling metaData update API to update CIFs tagged while saving - Piramal implementation
						if (StringUtils.isNotEmpty(App.getProperty("exteranal.interface.dms.leadId.based"))) {
							documentManagementService.updateExternalDocuments(extDoc, custId);

						}
					}
				}
			}
		}

		if (wve.size() > 0) {
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

		newCustDocumentdetails.addAll(exteranlDocMap.values());
		logger.debug(Literal.LEAVING);
		return newCustDocumentdetails;
	}

	public void prepareCollateralDocuments(FinanceDetail financeDetail) {
		logger.debug(Literal.ENTERING);
		List<CollateralSetup> collateralSetupList = financeDetail.getCollaterals();
		List<CollateralAssignment> assignementList = financeDetail.getCollateralAssignmentList();
		String finReference = financeDetail.getFinScheduleData().getFinanceMain().getFinReference();
		long custId = financeDetail.getFinScheduleData().getFinanceMain().getCustID();
		String applicationNo = ((FinanceMainBaseCtrl) financeMainDialogCtrl).getApplicationNo();
		String leadId = ((FinanceMainBaseCtrl) financeMainDialogCtrl).getLeadId();

		if (CollectionUtils.isNotEmpty(collateralSetupList)) {
			for (CollateralSetup collateralSetup : collateralSetupList) {
				List<DocumentDetails> collateralDocuments = collateralSetup.getDocuments();
				List<DocumentDetails> existingDocList = new ArrayList<>();

				for (DocumentDetails documentDetail : collateralDocuments) {
					if (StringUtils.equalsIgnoreCase(documentDetail.getDocModule(), CollateralConstants.MODULE_NAME)) {
						existingDocList.add(documentDetail);
					}
				}
				List<DocumentDetails> documents = preapreCollateralDMSDocs(existingDocList,
						collateralSetup.getCollateralRef(), applicationNo, leadId, finReference);
				collateralSetup.setDocuments(documents);
			}
		} else if (CollectionUtils.isNotEmpty(assignementList)) {
			List<CollateralSetup> collateralList = new ArrayList<>();
			for (CollateralAssignment collateralAssignment : assignementList) {
				String collateralRef = collateralAssignment.getCollateralRef();
				CollateralSetup collateralSetup = collateralSetupDAO.getCollateralSetupByRef(collateralRef, "");
				collateralSetup.setFromLoan(true);
				collateralSetup.setSourceId(DMSModule.DMS.name());
				List<DocumentDetails> existingDocList = documentDetailsDAO.getDocumentDetailsByRef(collateralRef,
						CollateralConstants.MODULE_NAME, FinServiceEvent.ORG, "");
				if (CollectionUtils.isNotEmpty(existingDocList)) {
					for (DocumentDetails documentDetail : existingDocList) {
						// Already approved documents need to synch with DMS so if docUri is blank setting the record
						// type as update.
						if (StringUtils.isBlank(documentDetail.getDocUri())) {
							documentDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
							documentDetail.setNewRecord(false);
							documentDetail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
							documentDetail.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
						}
					}
				}
				List<DocumentDetails> documents = preapreCollateralDMSDocs(existingDocList,
						collateralSetup.getCollateralRef(), applicationNo, leadId, finReference);
				for (DocumentDetails documentDetails : documents) {
					documentDetails.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
					documentDetails.setCustId(custId);
				}
				collateralSetup.setDocuments(documents);
				collateralList.add(collateralSetup);
			}
			financeDetail.setDmsCollateralDocuments(collateralList);
		}
		logger.debug(Literal.LEAVING);
	}

	private List<DocumentDetails> preapreCollateralDMSDocs(List<DocumentDetails> existingDocList, String collRef,
			String applicationNo, String leadId, String finReference) {

		logger.debug(Literal.ENTERING);
		List<DocumentDetails> newCollDocList = new ArrayList<>();
		List<WrongValueException> wve = new ArrayList<>();

		if (CollectionUtils.isNotEmpty(existingDocList)) {
			for (DocumentDetails doc : existingDocList) {
				doc.setApplicationNo(applicationNo);
				doc.setLeadId(leadId);
				doc.setFinReference(finReference);
				newCollDocList.add(doc);
			}
		}

		if (this.listBoxDMSDocuments.getItemCount() > 0) {
			for (Listitem listitem : listBoxDMSDocuments.getItems()) {

				ExternalDocument extDoc = (ExternalDocument) listitem.getAttribute("document");

				if (StringUtils.equalsIgnoreCase(extDoc.getCategoryOfDocument(), "COLLATERAL1")
						|| StringUtils.equalsIgnoreCase(extDoc.getCategoryOfDocument(), "COLLATERAL2")
						|| StringUtils.equalsIgnoreCase(extDoc.getCategoryOfDocument(), "COLLATERAL3")) {

					Combobox collCombobox = (Combobox) (listitem)
							.getFellowIfAny(String.valueOf(extDoc.getDocRefId()) + "_coll");
					collCombobox.clearErrorMessage();
					Clients.clearWrongValue(collCombobox);
					String collateralRef = getComboboxValue(collCombobox);
					try {
						if (collateralRef.equals(PennantConstants.List_Select)) {
							throw new WrongValueException(collCombobox,
									Labels.getLabel("STATIC_INVALID", new String[] { "Collaterl Ref" }));
						}
					} catch (WrongValueException e) {
						wve.add(e);
					}

					if (StringUtils.equals(collateralRef, collRef)) {
						boolean rcdFound = false;
						if (CollectionUtils.isNotEmpty(newCollDocList)) {
							for (DocumentDetails collateralDoc : newCollDocList) {
								if (StringUtils.equals(StringUtils.trimToEmpty(collateralDoc.getDocCategory()),
										extDoc.getDocumentType())
										&& !StringUtils.equals(StringUtils.trimToEmpty(collateralDoc.getRecordType()),
												PennantConstants.RECORD_TYPE_DEL)
										&& !StringUtils.equals(StringUtils.trimToEmpty(collateralDoc.getRecordType()),
												PennantConstants.RECORD_TYPE_CAN)) {
									rcdFound = true;
									if (!collateralDoc.isLovDescNewImage()
											&& NumberUtils.toLong(collateralDoc.getDocUri()) < NumberUtils
													.toLong(extDoc.getDocRefId())) {
										collateralDoc.setDocUri(extDoc.getDocRefId());
										collateralDoc.setDoctype(extDoc.getDocExtn().toUpperCase());
										collateralDoc.setDocName(extDoc.getDocName());
										// Record type setting as EDIT for already approved documents (Collateral
										// created outside)if same category matched.
										if (StringUtils.isBlank(collateralDoc.getRecordType()) || StringUtils.equals(
												collateralDoc.getRecordType(), PennantConstants.RECORD_TYPE_UPD)) {
											collateralDoc.setRecordType(PennantConstants.RECORD_TYPE_UPD);
										} else {
											collateralDoc.setRecordType(PennantConstants.RECORD_TYPE_NEW);
										}
										collateralDoc.setNewRecord(false);
										collateralDoc.setLastMntOn(new Timestamp(System.currentTimeMillis()));
										collateralDoc.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
									}
									break;
								}
							}
						}

						if (!rcdFound) {
							DocumentDetails docDetail = new DocumentDetails();
							docDetail.setDocUri(extDoc.getDocRefId());
							docDetail.setDocModule(CollateralConstants.MODULE_NAME);
							docDetail.setDocCategory(extDoc.getDocumentType());
							docDetail.setDocName(extDoc.getDocName());
							docDetail.setApplicationNo(applicationNo);
							docDetail.setFinReference(finReference);
							docDetail.setLeadId(leadId);
							if (StringUtils.equalsAnyIgnoreCase(extDoc.getDocExtn(), "XLSX")
									|| StringUtils.equalsAnyIgnoreCase(extDoc.getDocExtn(), "XLS")) {
								docDetail.setDoctype(PennantConstants.DOC_TYPE_EXCEL);
							} else {
								docDetail.setDoctype(extDoc.getDocExtn().toUpperCase());
							}
							docDetail.setRemarks(extDoc.getRemarks1());
							docDetail.setDocReceivedDate(
									DateUtil.getDate(extDoc.getRevisedDate(), PennantConstants.DBDateTimeFormat));
							docDetail.setVersion(1);
							docDetail.setReferenceId(collateralRef);
							docDetail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
							docDetail.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
							docDetail.setRecordType(PennantConstants.RCD_ADD);
							docDetail.setDocCategory(extDoc.getDocumentType());
							docDetail.setNewRecord(true);
							newCollDocList.add(docDetail);
						}
					}
				}
			}
		}

		if (wve.size() > 0) {
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
		return newCollDocList;
	}

	public static String getlabelDesc(String value, List<ValueLabel> list) {
		for (ValueLabel valueLabel : list) {
			if (valueLabel.getValue().equalsIgnoreCase(value)) {
				return valueLabel.getLabel();
			}
		}
		return "";
	}

	public FinBasicDetailsCtrl getFinBasicDetailsCtrl() {
		return finBasicDetailsCtrl;
	}

	public void setFinBasicDetailsCtrl(FinBasicDetailsCtrl finBasicDetailsCtrl) {
		this.finBasicDetailsCtrl = finBasicDetailsCtrl;
	}

	@Autowired
	public void setDmsService(DMSService dmsService) {
		this.dmsService = dmsService;
	}

	@Autowired(required = false)
	public void setDocumentManagementService(DocumentManagementService documentManagementService) {
		this.documentManagementService = documentManagementService;
	}

	public List<DocumentDetails> getDocumentDetailList() {
		return finDocumentDetailList;
	}

	public void setDocumentDetailList(List<DocumentDetails> documentDetailList) {
		this.finDocumentDetailList = documentDetailList;
	}

	public void setDocumentDetailsDAO(DocumentDetailsDAO documentDetailsDAO) {
		this.documentDetailsDAO = documentDetailsDAO;
	}

	public void setCollateralSetupDAO(CollateralSetupDAO collateralSetupDAO) {
		this.collateralSetupDAO = collateralSetupDAO;
	}
}
