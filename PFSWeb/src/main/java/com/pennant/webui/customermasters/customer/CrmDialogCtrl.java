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
 * * FileName : SelectFinReferenceDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 30-08-2016 * *
 * Modified Date : 30-08-2016 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 16-11-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.customermasters.customer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zkoss.util.media.Media;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.backend.model.crm.CrmDetails;
import com.pennant.backend.model.crm.ResponseData;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.service.customermasters.CustomerCrmService;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.util.Constraint.StaticListValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.interfacebajaj.fileextract.PresentmentDetailExtract;
import com.pennanttech.interfacebajaj.fileextract.service.FileExtractService;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;

public class CrmDialogCtrl extends GFCBaseCtrl<CrmDetails> {
	private static final long serialVersionUID = 8556168885363682933L;
	private static final Logger logger = LogManager.getLogger(CrmDialogCtrl.class);
	protected Textbox txtFileName;
	protected Textbox description;
	protected ExtendedCombobox finReference;
	protected Combobox requestType;
	String errorMsg = null;
	protected Button btnUpload;
	protected Label custId;
	protected Label custName;

	private FileExtractService<PresentmentDetailExtract> presentmentExtractService;

	protected Window window_CrmDialog; // autoWired
	protected Button btnProceed; // autoWireddialogCtrl
	private CustomerDetails customerDetails;

	CrmDetails crmDetails = new CrmDetails();

	public CrmDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "";
	}

	// Component Events

	public void onCreate$window_CrmDialog(Event event) {
		logger.debug(Literal.ENTERING);
		setPageComponents(window_CrmDialog);
		try {
			if (arguments.containsKey("customerDetails")) {
				customerDetails = (CustomerDetails) arguments.get("customerDetails");
			}
			doSetFieldProperties();
		} catch (Exception e) {
			closeDialog();
			MessageUtil.showError(e);
		}
		try {
			this.window_CrmDialog.doModal();
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug(Literal.LEAVING);
	}

	public void onClick$btnRaiseReq(Event event) {
		logger.debug(Literal.ENTERING);

		doSetValidation();
		ArrayList<WrongValueException> wve = new ArrayList<>();

		CustomerCrmService CustomerCrmService = new CustomerCrmService();

		crmDetails.setCustId(customerDetails.getCustID());
		crmDetails.setCustCif(customerDetails.getCustomer().getCustCIF());
		try {
			crmDetails.setDescription(this.description.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			crmDetails.setRelationshipNumber(this.finReference.getValue());
			crmDetails.setFinReference(this.finReference.getValue());
			if (this.finReference.getObject() != null) {
				FinanceMain object = (FinanceMain) this.finReference.getObject();
				crmDetails.setFinType(object.getFinType());
				crmDetails.setProduct(object.getProduct());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			crmDetails.setOrigin(this.requestType.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		if (!wve.isEmpty()) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		ResponseData processRequest = CustomerCrmService.ProcessRequest(crmDetails);
		if (processRequest != null && processRequest.getCaseResponse() != null) {
			String temp = processRequest.getCaseResponse().getCaseNumber() + " "
					+ processRequest.getCaseResponse().getMessage();
			Clients.showNotification(temp, "info", null, null, -1);
		} else {
			Clients.showNotification(processRequest.getReturnStatus().getReturnText(), "info", null, null, -1);
		}
		doClose(false);
		logger.debug(Literal.LEAVING);
	}

	public void onUpload$btnUpload(UploadEvent event) throws IOException {
		logger.debug(Literal.ENTERING);

		txtFileName.setText("");
		errorMsg = null;
		Media media = event.getMedia();
		txtFileName.setText(media.getName());
		crmDetails.setFileName(media.getName());
		crmDetails.setContentType(media.getContentType());

		String encodedString = Base64.getEncoder().encodeToString(media.getByteData());
		crmDetails.setFileData("," + encodedString);
		logger.debug(Literal.LEAVING);
	}

	private void doSetValidation() {
		logger.debug("Entering");

		if (!this.description.isReadonly()) {
			this.description.setConstraint(new PTStringValidator(Labels.getLabel("label_CrmDialog_Description.value"),
					PennantRegularExpressions.REGEX_DESCRIPTION, true));
		}
		if (!this.finReference.isReadonly()) {
			this.finReference.setConstraint(
					new PTStringValidator(Labels.getLabel("label_CrmDialog_FinReference"), null, true, true));
		}
		if (!this.requestType.isDisabled()) {
			this.requestType.setConstraint(new StaticListValidator(PennantStaticListUtil.getCrmRequestType(),
					Labels.getLabel("label_CrmDialog_RequestType.value")));
		}
		logger.debug("Leaving");
	}

	private void doSetFieldProperties() {
		logger.debug("Entering");
		Filter[] f1 = new Filter[1];

		f1[0] = new Filter("CustId", customerDetails.getCustID());
		this.finReference.setFilters(f1);
		this.finReference.setModuleName("FinanceMain");
		this.finReference.setValueColumn("FinReference");
		this.finReference.setDescColumn("FinType");
		this.finReference.setValidateColumns(new String[] { "FinReference" });
		this.finReference.setMandatoryStyle(true);

		fillComboBox(requestType, "", PennantStaticListUtil.getCrmRequestType(), "");

		this.custId.setValue(customerDetails.getCustomer().getCustCIF());
		this.custName.setValue(
				customerDetails.getCustomer().getCustFName() + " " + customerDetails.getCustomer().getCustLName());
		if (customerDetails.getCustomer().getCustCtgCode().equals("CORP")) {
			this.custName.setValue(customerDetails.getCustomer().getCustShrtName());
		}
		logger.debug("Leaving");
	}

	/**
	 * When user clicks on button "btnProceed" button
	 * 
	 * @param event
	 */
	public void onClick$btnProceed(Event event) {
		logger.debug("Entering " + event.toString());

		logger.debug("Leaving " + event.toString());
	}

	public void onClick$btnClose(Event event) {
		doClose(false);
	}

	@Override
	protected void doClearMessage() {
		logger.debug("Entering");
		logger.debug("Leaving");
	}

	public FileExtractService<PresentmentDetailExtract> getPresentmentExtractService() {
		return presentmentExtractService;
	}

	public void setPresentmentExtractService(FileExtractService<PresentmentDetailExtract> presentmentExtractService) {
		this.presentmentExtractService = presentmentExtractService;
	}
}
