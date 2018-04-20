/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. 
 * All components/modules/functions/classes/logic in this software, unless 
 * otherwise stated, the property of Pennant Technologies. 
 * 
 * Copyright and other intellectual property laws protect these materials. 
 * Reproduction or retransmission of the materials, in whole or in part, in any manner, 
 * without the prior written consent of the copyright holder, is a violation of 
 * copyright law.
 */
package com.pennant.webui.verification.fieldinvestigation;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.North;
import org.zkoss.zul.Radio;
import org.zkoss.zul.South;
import org.zkoss.zul.Space;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.applicationmaster.ReasonCode;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.PTDateValidator;
import com.pennant.util.Constraint.PTMobileNumberValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.constraint.PTListValidator;
import com.pennanttech.dataengine.util.DateUtil.DateFormat;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.pff.verification.VerificationType;
import com.pennanttech.pennapps.pff.verification.fi.FILivingStandard;
import com.pennanttech.pennapps.pff.verification.fi.FINeighbourHoodFeedBack;
import com.pennanttech.pennapps.pff.verification.fi.FIOwnerShipStatus;
import com.pennanttech.pennapps.pff.verification.fi.FIStatus;
import com.pennanttech.pennapps.pff.verification.fi.FIVerificationType;
import com.pennanttech.pennapps.pff.verification.model.FieldInvestigation;
import com.pennanttech.pennapps.pff.verification.service.FieldInvestigationService;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.document.external.ExternalDocumentManager;

/**
 * This is the controller class for the
 * /WEB-INF/pages/Verification/FieldInvestigation/fieldInvestigationDialog.zul
 * file. <br>
 */
public class FieldInvestigationDialogCtrl extends GFCBaseCtrl<FieldInvestigation> {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(FieldInvestigationDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting by our 'extends
	 * GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_FieldInvestigationDialog;
	protected Tab verificationDetails;
	protected Groupbox gb_basicDetails;
	protected Textbox custCIF;
	protected Textbox finReference;
	protected Textbox custName;
	protected Textbox addrType;
	protected Textbox houseNo;
	protected Textbox flatNo;
	protected Textbox street;
	protected Textbox addressLine1;
	protected Textbox addressLine2;
	protected Textbox postBox;
	protected Textbox country;
	protected Textbox province;
	protected Textbox city;
	protected Textbox zipCode;
	protected Textbox contactNumber1;
	protected Textbox contactNumber2;

	protected Groupbox gb_observations;
	protected Datebox verificationDate;
	protected Combobox verificationType;
	protected Intbox yearsAtPresentAddress;
	protected Textbox personMet;
	protected Combobox ownerShipStatus;
	protected ExtendedCombobox relationShip;
	protected Combobox neighbourhoodCheckFeedBack;
	protected Radio positive;
	protected Radio negative;
	protected Textbox contactNo;
	protected Textbox observationRemarks;
	protected Combobox livingStandard;
	protected Checkbox negativeCheck;
	protected Intbox noOfAttempts;

	protected Groupbox gb_summary;
	protected Textbox agentCode;
	protected Textbox agentName;
	protected Combobox recommendations;
	protected ExtendedCombobox reason;
	protected Textbox summaryRemarks;
	protected North north;
	protected South south;

	protected Button btnNew_FieldInvestigationDocuments;
	protected Space space_Reason;
	protected Tab documentDetails;
	private FieldInvestigation fieldInvestigation;
	protected Listbox listBoxVerificationDocuments;
    protected Map<String, DocumentDetails>		docDetailMap			= null;
    private List<DocumentDetails>				documentDetailsList		= new ArrayList<DocumentDetails>();
    @Autowired
	private transient FieldInvestigationDocumentDialogCtrl fieldInvestigationDocumentDialogCtrl;
	private transient FieldInvestigationListCtrl fieldInvestigationListCtrl;

	@Autowired
	private transient FieldInvestigationService fieldInvestigationService;
	private ExternalDocumentManager				externalDocumentManager			= null;
	
	private boolean fromLoanOrg;

	/**
	 * default constructor.<br>
	 */
	public FieldInvestigationDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "FieldInvestigationDialog";
	}

	/**
	 * 
	 * The framework calls this event handler when an application requests that
	 * the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 * @throws Exception
	 */
	public void onCreate$window_FieldInvestigationDialog(Event event) throws Exception {
		logger.debug(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(window_FieldInvestigationDialog);
		registerButton(btnNew_FieldInvestigationDocuments, "button_FieldInvestigationDocumentDialog_btnNew", true);

		try {
			// Get the required arguments.
			this.fieldInvestigation = (FieldInvestigation) arguments.get("fieldInvestigation");
			
			if (arguments.get("fieldInvestigationListCtrl") != null) {
				this.fieldInvestigationListCtrl = (FieldInvestigationListCtrl) arguments
						.get("fieldInvestigationListCtrl");
			}

			if (arguments.get("LOAN_ORG") != null) {
				fromLoanOrg = true;
				enqiryModule = true;
			}
			
			if (arguments.get("enqiryModule") != null) {
				enqiryModule = (boolean) arguments.get("enqiryModule");
			}
			

			if (this.fieldInvestigation == null) {
				throw new Exception(Labels.getLabel("error.unhandled"));
			}

			// Store the before image.
			FieldInvestigation fieldInvestigation = new FieldInvestigation();
			BeanUtils.copyProperties(this.fieldInvestigation, fieldInvestigation);
			this.fieldInvestigation.setBefImage(fieldInvestigation);
			
			// Render the page and display the data.
			doLoadWorkFlow(this.fieldInvestigation.isWorkflow(), this.fieldInvestigation.getWorkflowId(),
					this.fieldInvestigation.getNextTaskId());
			
			if (isWorkFlowEnabled() && !enqiryModule) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(), this.pageRightName);
			} else if(fromLoanOrg) {
				setWorkFlowEnabled(true);
			}
			
			
			doSetFieldProperties();
			doCheckRights();
			doShowDialog(this.fieldInvestigation);
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

		this.reason.setMaxlength(8);
		this.reason.setMandatoryStyle(false);
		this.reason.setModuleName("FIStatusReason");
		this.reason.setValueColumn("Code");
		this.reason.setDescColumn("Description");
		this.reason.setValidateColumns(new String[] { "Code" });

		this.relationShip.setMaxlength(8);
		this.relationShip.setMandatoryStyle(false);
		this.relationShip.setModuleName("PRelationCode");
		this.relationShip.setValueColumn("PRelationCode");
		this.relationShip.setDescColumn("PRelationDesc");
		this.relationShip.setValidateColumns(new String[] { "PRelationCode" });

		this.yearsAtPresentAddress.setMaxlength(2);
		this.personMet.setMaxlength(20);
		this.contactNo.setMaxlength(10);
		this.observationRemarks.setMaxlength(50);
		this.noOfAttempts.setMaxlength(2);
		this.agentCode.setMaxlength(8);
		this.agentName.setMaxlength(20);
		this.summaryRemarks.setMaxlength(500);

		this.verificationDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		setStatusDetails();

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set Visible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug(Literal.ENTERING);
		
		getUserWorkspace().allocateAuthorities(this.pageRightName, getRole());

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_FieldInvestigationDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_FieldInvestigationDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_FieldInvestigationDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_FieldInvestigationDialog_btnSave"));
		
		this.btnNew_FieldInvestigationDocuments.setVisible(getUserWorkspace().isAllowed("button_FieldInvestigationDialog_NewDocuments"));
		this.btnCancel.setVisible(false);

		logger.debug(Literal.LEAVING);
	}
	
	public void onClick$btnNew_FieldInvestigationDocuments(Event event) throws Exception {
		logger.debug(Literal.ENTERING);
		
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("newRecord", "true");
		map.put("roleCode", getRole());
		DocumentDetails documentDetails = new DocumentDetails();
		documentDetails.setNewRecord(true);
		documentDetails.setWorkflowId(0);
		map.put("fieldInvestigationDialogCtrl", this);
		map.put("documentDetails", documentDetails);
		
		try {
			Executions.createComponents("/WEB-INF/pages/Verification/FieldInvestigation/FieldInvestigationDocumentDialog.zul",
					null, map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		
		logger.debug(Literal.LEAVING);
	}
	
	public void onFIDocumentItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		// get the selected invoiceHeader object
		final Listitem item = this.listBoxVerificationDocuments.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			DocumentDetails fIDocumentDetail = (DocumentDetails) item.getAttribute("data");
			if (StringUtils.trimToEmpty(fIDocumentDetail.getRecordType()).equalsIgnoreCase(
					PennantConstants.RECORD_TYPE_CAN)) {
				MessageUtil.showError(Labels.getLabel("common_NoMaintainance"));
			} else {
				final HashMap<String, Object> map = new HashMap<String, Object>();
				if (fIDocumentDetail.getDocImage()== null) {
					if (fIDocumentDetail.getDocRefId() != Long.MIN_VALUE) {
						fIDocumentDetail.setDocImage(
								PennantApplicationUtil.getDocumentImage(fIDocumentDetail.getDocRefId()));
					} else if (StringUtils.isNotBlank(fIDocumentDetail.getDocUri())) {
						try {
							// Fetch document from interface
							String custCif = this.custCIF.getValue();
							// here document name is required to identify the file type
							DocumentDetails detail = externalDocumentManager.getExternalDocument(
									fIDocumentDetail.getDocName(), fIDocumentDetail.getDocUri(), custCif);
							if (detail != null && detail.getDocImage() != null) {
								fIDocumentDetail.setDocImage(detail.getDocImage());
								fIDocumentDetail.setDocName(detail.getDocName());
							}
						} catch (InterfaceException e) {
							MessageUtil.showError(e);
						}
					}
				}
				
				map.put("documentDetails", fIDocumentDetail);
				map.put("fieldInvestigationDialogCtrl", this);
				map.put("roleCode", getRole());
				map.put("enqiryModule", enqiryModule);
				
				// call the zul-file with the parameters packed in a map
				try {
					Executions.createComponents(
							"/WEB-INF/pages/Verification/FieldInvestigation/FieldInvestigationDocumentDialog.zul", null, map);
				} catch (Exception e) {
					MessageUtil.showError(e);
				}
			}
		}
		
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * The framework calls this event handler when user clicks the save button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnSave(Event event) {
		logger.debug(Literal.ENTERING);
		doSave();
		logger.debug(Literal.LEAVING);

	}

	/**
	 * The framework calls this event handler when user clicks the edit button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnEdit(Event event) {
		logger.debug(Literal.ENTERING);
		doEdit();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the help button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnHelp(Event event) {
		logger.debug(Literal.ENTERING);
		MessageUtil.showHelpWindow(event, super.window);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the delete
	 * button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnDelete(Event event) throws InterruptedException {
		logger.debug(Literal.ENTERING);
		doDelete();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the cancel
	 * button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnCancel(Event event) {
		logger.debug(Literal.ENTERING);
		doCancel();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event
	 *            An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		logger.debug(Literal.ENTERING);
		doClose(this.btnSave.isVisible());
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the notes button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnNotes(Event event) {
		logger.debug(Literal.ENTERING);
		doShowNotes(this.fieldInvestigation);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	private void refreshList() {
		logger.debug(Literal.ENTERING);
		fieldInvestigationListCtrl.search();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Cancel the actual operation and Resets to the original status
	 */
	private void doCancel() {
		logger.debug(Literal.ENTERING);

		doWriteBeanToComponents(this.fieldInvestigation.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);

		logger.debug(Literal.LEAVING);
	}

	public void onFulfill$reason(Event event) {
		logger.debug("Entering");
		Object dataObject = reason.getObject();
		if (dataObject instanceof String) {
			this.reason.setValue(dataObject.toString());
			this.reason.setDescription("");
			this.reason.setAttribute("ReasonId", null);
		} else {
			ReasonCode details = (ReasonCode) dataObject;
			if (details != null) {
				this.reason.setAttribute("ReasonId", details.getId());
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param fi
	 * 
	 */
	public void doWriteBeanToComponents(FieldInvestigation fi) {
		logger.debug(Literal.ENTERING);

		this.custCIF.setValue(fi.getCif());
		this.finReference.setValue(fi.getKeyReference());
		this.custName.setValue(fi.getName());
		this.addrType.setValue(fi.getAddressType());
		this.houseNo.setValue(fi.getHouseNumber());
		this.flatNo.setValue(fi.getFlatNumber());
		this.street.setValue(fi.getStreet());
		this.addressLine1.setValue(fi.getAddressLine1());
		this.addressLine2.setValue(fi.getAddressLine2());
		this.postBox.setValue(fi.getPoBox());
		this.country.setValue(fi.getCountry());
		this.city.setValue(fi.getCity());
		this.province.setValue(fi.getProvince());
		this.zipCode.setValue(fi.getZipCode());
		this.contactNumber1.setValue(fi.getContactNumber1());
		this.contactNumber2.setValue(fi.getContactNumber2());

		this.verificationDate.setValue(fi.getDate());
		this.yearsAtPresentAddress.setValue(fi.getYearsAtPresentAddress());
		this.personMet.setValue(fi.getPersonMet());
		this.relationShip.setValue(StringUtils.trimToEmpty((fi.getRelationship())),
				StringUtils.trimToEmpty(fi.getLovrelationdesc()));
		this.contactNo.setValue(fi.getContactNumber());
		this.observationRemarks.setValue(fi.getObservationRemarks());
		this.livingStandard.setValue(String.valueOf(fi.getLivingStandard()));
		this.negativeCheck.setChecked(fi.isNegativeCheck());
		this.noOfAttempts.setValue(fi.getNoofAttempts());
		this.agentCode.setValue(fi.getAgentCode());
		this.agentName.setValue(fi.getAgentName());
		this.recommendations.setValue(String.valueOf(fi.getStatus()));
		if (!fi.isNewRecord()) {
			this.reason.setValue(StringUtils.trimToEmpty((fi.getReasonCode())),
					StringUtils.trimToEmpty(fi.getReasonDesc()));
			if (fi.getReason() != null) {
				this.reason.setAttribute("ReasonId", fi.getReason());
			} else {
				this.reason.setAttribute("ReasonId", null);
			}
		}
	
		this.summaryRemarks.setValue(fi.getSummaryRemarks());
		fillComboBox(this.verificationType, fieldInvestigation.getType(), FIVerificationType.getList());
		fillComboBox(this.ownerShipStatus, fi.getOwnershipStatus(), FIOwnerShipStatus.getList());
		fillComboBox(this.livingStandard, fi.getLivingStandard(), FILivingStandard.getList());
		fillComboBox(this.recommendations, fi.getStatus(), FIStatus.getList());
		fillComboBox(this.neighbourhoodCheckFeedBack, fi.getNeighbourhoodFeedBack(), FINeighbourHoodFeedBack.getList());

		this.recordStatus.setValue(fi.getRecordStatus());
		doFillDocumentDetails(fi.getDocuments());

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param verification
	 */
	public void doWriteComponentsToBean(FieldInvestigation fi) {
		logger.debug(Literal.LEAVING);

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		fi.setCif(this.custCIF.getValue());
		fi.setKeyReference(this.finReference.getValue());
		fi.setName(this.custName.getValue());
		fi.setAddressType(this.addrType.getValue());
		fi.setHouseNumber(this.houseNo.getValue());
		fi.setStreet(this.street.getValue());
		fi.setAddressLine1(this.addressLine1.getValue());
		fi.setAddressLine2(this.addressLine2.getValue());
		fi.setAddressLine3(StringUtils.trimToEmpty(this.fieldInvestigation.getAddressLine3()));
		fi.setAddressLine4(StringUtils.trimToEmpty(this.fieldInvestigation.getAddressLine4()));
		fi.setAddressLine5(StringUtils.trimToEmpty(this.fieldInvestigation.getAddressLine5()));
		fi.setPoBox(this.postBox.getValue());
		fi.setCountry(this.country.getValue());
		fi.setProvince(this.province.getValue());
		fi.setCity(this.city.getValue());
		fi.setZipCode(this.zipCode.getValue());
		fi.setContactNumber1(this.contactNumber1.getValue());
		fi.setContactNumber2(this.contactNumber2.getValue());

		try {
			fi.setDate(this.verificationDate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if ("0".equals(getComboboxValue(this.verificationType))) {
				throw new WrongValueException(this.verificationType, Labels.getLabel("STATIC_INVALID",
						new String[] { Labels.getLabel("label_FieldInvestigationDialog_VerificationType.value") }));
			} else {
				fi.setType(Integer.parseInt(getComboboxValue(this.verificationType)));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			fi.setYearsAtPresentAddress(this.yearsAtPresentAddress.intValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			fi.setPersonMet(this.personMet.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if ("0".equals(getComboboxValue(this.ownerShipStatus))) {
				throw new WrongValueException(this.ownerShipStatus, Labels.getLabel("STATIC_INVALID",
						new String[] { Labels.getLabel("label_FieldInvestigationDialog_OwnerShipStatus.value") }));
			} else {
				fi.setOwnershipStatus(Integer.parseInt(getComboboxValue(this.ownerShipStatus)));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			fi.setLovrelationdesc(this.relationShip.getDescription());
			fi.setRelationship(StringUtils.trimToNull(this.relationShip.getValidatedValue()));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			fi.setNeighbourhoodFeedBack(Integer.parseInt(getComboboxValue(this.neighbourhoodCheckFeedBack)));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			fi.setContactNumber(this.contactNo.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			fi.setObservationRemarks(this.observationRemarks.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			fi.setLivingStandard(Integer.parseInt(getComboboxValue(this.livingStandard)));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			fi.setNegativeCheck(this.negativeCheck.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			fi.setNoofAttempts(this.noOfAttempts.intValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			fi.setAgentCode(this.agentCode.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			fi.setAgentName(this.agentName.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if ("0".equals(getComboboxValue(this.recommendations))) {
				throw new WrongValueException(this.recommendations, Labels.getLabel("STATIC_INVALID",
						new String[] { Labels.getLabel("label_FieldInvestigationDialog_Recommendations.value") }));
			} else {
				fi.setStatus(Integer.parseInt(getComboboxValue(this.recommendations)));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			fi.setReasonDesc(this.reason.getDescription());
			fi.setReasonCode(this.reason.getValue());
			this.reason.getValidatedValue();
			Object object = this.reason.getAttribute("ReasonId");
			if (object != null) {
				fi.setReason((Long.parseLong(object.toString())));
			} else {
				fi.setReason(null);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			fi.setSummaryRemarks(this.summaryRemarks.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		doRemoveValidation();

		showErrorDetails(wve, this.verificationDetails);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Displays the dialog page.
	 * 
	 * @param fieldInvestigation
	 *            The entity that need to be render.
	 */
	public void doShowDialog(FieldInvestigation fieldInvestigation) {
		logger.debug(Literal.LEAVING);

		if (fieldInvestigation.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.verificationDate.focus();
		} else {

			if (isWorkFlowEnabled()) {
				if (StringUtils.isNotBlank(fieldInvestigation.getRecordType())) {
					this.btnNotes.setVisible(true);
				}
				// setFocus
				this.verificationDate.focus();
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
		
		if(fromLoanOrg) {
			north.setVisible(false);
			south.setVisible(false);
		}

		doWriteBeanToComponents(fieldInvestigation);
		if (!fromLoanOrg) {
			setDialog(DialogType.EMBEDDED);
		} else {
			window_FieldInvestigationDialog.setHeight("100%");
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method to show error details if occurred
	 * 
	 **/
	private void showErrorDetails(ArrayList<WrongValueException> wve, Tab tab) {
		logger.debug("Entering");

		doRemoveValidation();

		if (wve.size() > 0) {
			logger.debug("Throwing occured Errors By using WrongValueException");
			tab.setSelected(true);
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = wve.get(i);
				if(i == 0){
					Component comp = wvea[i].getComponent();
					if(comp instanceof HtmlBasedComponent){
						Clients.scrollIntoView(comp);
					}
				}
				logger.debug(wvea[i]);
			}
			throw new WrongValuesException(wvea);
		}
		logger.debug("Leaving");
	}
	
	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug(Literal.LEAVING);

		if (this.verificationDate.isVisible() && !this.verificationDate.isReadonly()) {
			this.verificationDate.setConstraint(
					new PTDateValidator(Labels.getLabel("label_FieldInvestigationDialog_VerificationDate.value"), true,
							DateUtil.getDatePart(fieldInvestigation.getCreatedOn()),
							DateUtil.getDatePart(DateUtil.getSysDate()), true));
		}
		if (!this.yearsAtPresentAddress.isReadonly()) {
			this.yearsAtPresentAddress.setConstraint(
					new PTStringValidator(Labels.getLabel("label_FieldInvestigationDialog_AddrType.value"),
							PennantRegularExpressions.REGEX_NUMERIC, false));
		}

		if (!this.personMet.isReadonly()) {
			this.personMet.setConstraint(
					new PTStringValidator(Labels.getLabel("label_FieldInvestigationDialog_AddrType.value"),
							PennantRegularExpressions.REGEX_CUST_NAME, false));
		}
		if (!this.contactNo.isReadonly()) {
			this.contactNo.setConstraint(
					new PTMobileNumberValidator(Labels.getLabel("label_FieldInvestigationDialog_ContactNo.value"),
							false, null, this.contactNo.getMaxlength()));
		}
		if (!this.observationRemarks.isReadonly()) {
			this.observationRemarks.setConstraint(
					new PTStringValidator(Labels.getLabel("label_FieldInvestigationDialog_Remarks.value"),
							PennantRegularExpressions.REGEX_DESCRIPTION, false));
		}

		if (!this.noOfAttempts.isReadonly()) {
			this.noOfAttempts.setConstraint(
					new PTStringValidator(Labels.getLabel("label_FieldInvestigationDialog_NoOfAttempts.value"),
							PennantRegularExpressions.REGEX_NUMERIC, true));
		}
		if (!this.agentCode.isReadonly()) {
			this.agentCode.setConstraint(
					new PTStringValidator(Labels.getLabel("label_FieldInvestigationDialog_AgentCode.value"),
							PennantRegularExpressions.REGEX_UPP_BOX_ALPHANUM, true));
		}
		if (!this.agentName.isReadonly()) {
			this.agentName.setConstraint(
					new PTStringValidator(Labels.getLabel("label_FieldInvestigationDialog_AgentName.value"),
							PennantRegularExpressions.REGEX_CUST_NAME, true));
		}
		if (!this.recommendations.isDisabled()) {
			this.recommendations.setConstraint(new PTListValidator(
					Labels.getLabel("label_FieldInvestigationDialog_Status.value"), FIStatus.getList(), true));
		}
		if (!this.summaryRemarks.isReadonly()) {
			this.summaryRemarks.setConstraint(
					new PTStringValidator(Labels.getLabel("label_FieldInvestigationDialog_Remarks.value"),
							PennantRegularExpressions.REGEX_DESCRIPTION, false));
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug(Literal.LEAVING);

		this.verificationDate.setConstraint("");
		this.verificationType.setConstraint("");
		this.yearsAtPresentAddress.setConstraint("");
		this.personMet.setConstraint("");
		this.ownerShipStatus.setConstraint("");
		this.relationShip.setConstraint("");
		this.contactNo.setConstraint("");
		this.observationRemarks.setConstraint("");
		this.noOfAttempts.setConstraint("");
		this.agentCode.setConstraint("");
		this.agentName.setConstraint("");
		this.recommendations.setConstraint("");
		this.reason.setConstraint("");
		this.summaryRemarks.setConstraint("");
		this.summaryRemarks.setConstraint("");

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Clears validation error messages from all the fields of the dialog
	 * controller.
	 */
	@Override
	protected void doClearMessage() {
		logger.debug(Literal.LEAVING);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Deletes a FieldInvestigation object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug(Literal.LEAVING);

		final FieldInvestigation entity = new FieldInvestigation();
		BeanUtils.copyProperties(this.fieldInvestigation, entity);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> "
				+ entity.getId();
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.trimToEmpty(entity.getRecordType()).equals("")) {
				entity.setVersion(entity.getVersion() + 1);
				entity.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()) {
					entity.setRecordStatus(userAction.getSelectedItem().getValue().toString());
					entity.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
					getWorkFlowDetails(userAction.getSelectedItem().getLabel(), entity.getNextTaskId(), entity);
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}

			try {
				if (doProcess(entity, tranType)) {
					refreshList();
					closeDialog();
				}

			} catch (DataAccessException e) {
				MessageUtil.showError(e);
			}
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug(Literal.LEAVING);

		if (this.fieldInvestigation.isNewRecord()) {
			this.btnCancel.setVisible(false);
		} else {
			this.btnCancel.setVisible(true);
		}
		readOnlyComponent(isReadOnly("FieldInvestigationDialog_Date"), this.verificationDate);
		readOnlyComponent(isReadOnly("FieldInvestigationDialog_VerificationType"), this.verificationType);
		readOnlyComponent(isReadOnly("FieldInvestigationDialog_YearsAtPresentAddress"), this.yearsAtPresentAddress);
		readOnlyComponent(isReadOnly("FieldInvestigationDialog_PersonMet"), this.personMet);
		readOnlyComponent(isReadOnly("FieldInvestigationDialog_OwnershipStatus"), this.ownerShipStatus);
		readOnlyComponent(isReadOnly("FieldInvestigationDialog_Relationship"), this.relationShip);
		readOnlyComponent(isReadOnly("FieldInvestigationDialog_NeighborhoodCheck"), this.neighbourhoodCheckFeedBack);
		readOnlyComponent(isReadOnly("FieldInvestigationDialog_contactNo"), this.contactNo);
		readOnlyComponent(isReadOnly("FieldInvestigationDialog_Remarks"), this.observationRemarks);
		readOnlyComponent(isReadOnly("FieldInvestigationDialog_LivingStandard"), this.livingStandard);
		readOnlyComponent(isReadOnly("FieldInvestigationDialog_NegativeCheck"), this.negativeCheck);
		readOnlyComponent(isReadOnly("FieldInvestigationDialog_NoofAttempts"), this.noOfAttempts);

		readOnlyComponent(isReadOnly("FieldInvestigationDialog_AgentCode"), this.agentCode);
		readOnlyComponent(isReadOnly("FieldInvestigationDialog_AgentName"), this.agentName);
		readOnlyComponent(isReadOnly("FieldInvestigationDialog_Recommendations"), this.recommendations);
		readOnlyComponent(isReadOnly("FieldInvestigationDialog_Reason"), this.reason);
		readOnlyComponent(isReadOnly("FieldInvestigationDialog_AgentRemarks"), this.summaryRemarks);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.fieldInvestigation.isNewRecord()) {
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

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug(Literal.ENTERING);

		this.custCIF.setReadonly(true);
		this.finReference.setReadonly(true);
		this.custName.setReadonly(true);
		this.addrType.setReadonly(true);
		this.houseNo.setReadonly(true);
		this.flatNo.setReadonly(true);
		this.street.setReadonly(true);
		this.addressLine1.setReadonly(true);
		this.addressLine2.setReadonly(true);
		this.postBox.setReadonly(true);
		this.country.setReadonly(true);
		this.province.setReadonly(true);
		this.city.setReadonly(true);
		this.zipCode.setReadonly(true);
		this.contactNumber1.setReadonly(true);
		this.contactNumber2.setReadonly(true);
		
		this.verificationDate.setReadonly(true);		
		this.verificationType.setReadonly(true);
		this.yearsAtPresentAddress.setReadonly(true);
		this.personMet.setReadonly(true);
		this.ownerShipStatus.setReadonly(true);
		this.relationShip.setReadonly(true);
		this.neighbourhoodCheckFeedBack.setReadonly(true);
		this.contactNo.setReadonly(true);
		this.observationRemarks.setReadonly(true);
		this.livingStandard.setReadonly(true);
		this.negativeCheck.setDisabled(true);
		this.noOfAttempts.setReadonly(true);

		this.agentCode.setReadonly(true);
		this.agentName.setReadonly(true);
		this.recommendations.setReadonly(true);
		this.reason.setReadonly(true);
		this.summaryRemarks.setReadonly(true);

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
	 * Saves the components to table. <br>
	 */
	public void doSave() {
		logger.debug(Literal.ENTERING);
		final FieldInvestigation fi = new FieldInvestigation();
		BeanUtils.copyProperties(this.fieldInvestigation, fi);
		boolean isNew = false;

		doSetValidation();
		doWriteComponentsToBean(fi);

		isNew = fi.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(fi.getRecordType())) {
				fi.setVersion(fi.getVersion() + 1);
				if (isNew) {
					fi.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					fi.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					fi.setNewRecord(true);
				}
			}
		} else {
			fi.setVersion(fi.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}
		//TODO
		if (fieldInvestigationDocumentDialogCtrl != null) {
			fi.setDocuments(getDocumentDetailsList());
		}else{
			fi.setDocuments(getFieldInvestigation().getDocuments());
		}
		

		try {
			if (doProcess(fi, tranType)) {
				refreshList();
				closeDialog();
			}

		} catch (final DataAccessException e) {
			logger.error(e);
			MessageUtil.showError(e);
		}
		logger.debug(Literal.LEAVING);
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
	private boolean doProcess(FieldInvestigation fieldInvestigation, String tranType) {
		logger.debug(Literal.ENTERING);
		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		fieldInvestigation.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		fieldInvestigation.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		fieldInvestigation.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			fieldInvestigation.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(fieldInvestigation.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, fieldInvestigation);
				}

				if (isNotesMandatory(taskId, fieldInvestigation)) {
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

			fieldInvestigation.setTaskId(taskId);
			fieldInvestigation.setNextTaskId(nextTaskId);
			fieldInvestigation.setRoleCode(getRole());
			fieldInvestigation.setNextRoleCode(nextRoleCode);

			//Document Details
			if (fieldInvestigation.getDocuments() != null && !fieldInvestigation.getDocuments().isEmpty()) {
				for (DocumentDetails details : fieldInvestigation.getDocuments()) {
					
					if (StringUtils.isEmpty(StringUtils.trimToEmpty(details.getRecordType()))) {
						continue;
					}
					details.setReferenceId(String.valueOf(fieldInvestigation.getVerificationId()));
					details.setDocModule(VerificationType.FI.getCode());
					details.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
					details.setLastMntOn(new Timestamp(System.currentTimeMillis()));
					details.setRecordStatus(fieldInvestigation.getRecordStatus());
					details.setWorkflowId(fieldInvestigation.getWorkflowId());
					details.setTaskId(taskId);
					details.setNextTaskId(nextTaskId);
					details.setRoleCode(getRole());
					details.setNextRoleCode(nextRoleCode);
					if (PennantConstants.RECORD_TYPE_DEL.equals(fieldInvestigation.getRecordType())) {
						if (StringUtils.trimToNull(details.getRecordType()) == null) {
							details.setRecordType(fieldInvestigation.getRecordType());
							details.setNewRecord(true);
						}
					}
				}
			}
			
			
			auditHeader = getAuditHeader(fieldInvestigation, tranType);
			String operationRefs = getServiceOperations(taskId, fieldInvestigation);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(fieldInvestigation, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(fieldInvestigation, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}

		logger.debug(Literal.LEAVING);
		return processCompleted;
	}

	/**
	 * Get the result after processing DataBase Operations
	 * 
	 * @param AuditHeader
	 *            auditHeader
	 * @param method
	 *            (String)
	 * @return boolean
	 * 
	 */

	private boolean doSaveProcess(AuditHeader auditHeader, String method) {
		logger.debug(Literal.ENTERING);
		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		FieldInvestigation fieldInvestigation = (FieldInvestigation) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		try {

			while (retValue == PennantConstants.porcessOVERIDE) {

				if (StringUtils.isBlank(method)) {
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
						auditHeader = fieldInvestigationService.delete(auditHeader);
						deleteNotes = true;
					} else {
						auditHeader = fieldInvestigationService.saveOrUpdate(auditHeader);
					}

				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
						auditHeader = fieldInvestigationService.doApprove(auditHeader);

						if (fieldInvestigation.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}

					} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = fieldInvestigationService.doReject(auditHeader);
						if (fieldInvestigation.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}

					} else {
						auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_9999,
								Labels.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_FieldInvestigationDialog, auditHeader);
						return processCompleted;
					}
				}

				auditHeader = ErrorControl.showErrorDetails(this.window_FieldInvestigationDialog, auditHeader);
				retValue = auditHeader.getProcessStatus();

				if (retValue == PennantConstants.porcessCONTINUE) {
					processCompleted = true;

					if (deleteNotes) {
						deleteNotes(getNotes(this.fieldInvestigation), true);
					}
				}

				if (retValue == PennantConstants.porcessOVERIDE) {
					auditHeader.setOveride(true);
					auditHeader.setErrorMessage(null);
					auditHeader.setInfoMessage(null);
					auditHeader.setOverideMessage(null);
				}
			}
		} catch (InterruptedException e) {
			logger.error("Exception: ", e);
		}
		setOverideMap(auditHeader.getOverideMap());

		logger.debug(Literal.LEAVING);
		return processCompleted;
	}

	/**
	 * @param aAuthorizedSignatoryRepository
	 * @param tranType
	 * @return
	 */

	private AuditHeader getAuditHeader(FieldInvestigation fieldInvestigation, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, fieldInvestigation.getBefImage(), fieldInvestigation);
		return new AuditHeader(getReference(), null, null, null, auditDetail, fieldInvestigation.getUserDetails(),
				getOverideMap());
	}

	@Override
	protected String getReference() {
		return String.valueOf(this.fieldInvestigation.getId());
	}
	
	public FieldInvestigation getFieldInvestigation() {
		return fieldInvestigation;
	}

	public void setFieldInvestigation(FieldInvestigation fieldInvestigation) {
		this.fieldInvestigation = fieldInvestigation;
	}
	
	public FieldInvestigationListCtrl getFieldInvestigationListCtrl() {
		return fieldInvestigationListCtrl;
	}

	public void setFieldInvestigationListCtrl(FieldInvestigationListCtrl fieldInvestigationListCtrl) {
		this.fieldInvestigationListCtrl = fieldInvestigationListCtrl;
	}

	public void setFieldInvestigationService(FieldInvestigationService fieldInvestigationService) {
		this.fieldInvestigationService = fieldInvestigationService;
	}

	public List<DocumentDetails> getDocumentDetailsList() {
		return documentDetailsList;
	}

	public void setDocumentDetailsList(List<DocumentDetails> documentDetailsList) {
		this.documentDetailsList = documentDetailsList;
	}

	public FieldInvestigationDocumentDialogCtrl getFieldInvestigationDocumentDialogCtrl() {
		return fieldInvestigationDocumentDialogCtrl;
	}

	public void setFieldInvestigationDocumentDialogCtrl(
			FieldInvestigationDocumentDialogCtrl fieldInvestigationDocumentDialogCtrl) {
		this.fieldInvestigationDocumentDialogCtrl = fieldInvestigationDocumentDialogCtrl;
	}

	private void fillComboBox(Combobox combobox, int value, List<ValueLabel> list) {
		combobox.getChildren().clear();
		for (ValueLabel valueLabel : list) {
			Comboitem comboitem = new Comboitem();
			comboitem.setValue(valueLabel.getValue());
			comboitem.setLabel(valueLabel.getLabel());
			combobox.appendChild(comboitem);
			if (Integer.parseInt(valueLabel.getValue()) == value) {
				combobox.setSelectedItem(comboitem);
			}
		}
	}
	public void doFillDocumentDetails(List<DocumentDetails> documentDetails) {
		logger.debug("Entering");

		docDetailMap = new HashMap<String, DocumentDetails>();
		this.listBoxVerificationDocuments.getItems().clear();
		setDocumentDetailsList(documentDetails);
		ArrayList<ValueLabel> documentTypes = PennantAppUtil.getDocumentTypes();
		List<DocumentDetails> sortdocumentDetails = documentDetails;
		//sortdocumentDetails.addAll(sortDocumentDetails(documentDetails));

		for (DocumentDetails documentDetail : sortdocumentDetails) {
			Listitem listitem = new Listitem();
			Listcell listcell;
			listcell = new Listcell(documentDetail.getDocName());
			listitem.appendChild(listcell);
			listcell = new Listcell(documentDetail.getDocName());
			listitem.appendChild(listcell);
			listcell = new Listcell(PennantJavaUtil.getLabel(documentDetail.getRecordStatus()));
			listitem.appendChild(listcell);
			listcell = new Listcell(PennantJavaUtil.getLabel(documentDetail.getRecordType()));
			listitem.appendChild(listcell);
			listitem.setAttribute("data", documentDetail);
			ComponentsCtrl.applyForward(listitem, "onDoubleClick=onFIDocumentItemDoubleClicked");
			if (!documentDetail.isDocIsCustDoc()) {
				this.listBoxVerificationDocuments.appendChild(listitem);
			}
			docDetailMap.put(documentDetail.getDocCategory(), documentDetail);
		}
		logger.debug("Leaving");
	}
	
}
