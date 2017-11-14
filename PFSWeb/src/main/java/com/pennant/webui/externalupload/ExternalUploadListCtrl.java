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

/**
 ********************************************************************************************
 *                                 FILE HEADER                                              *
 ********************************************************************************************
 *																							*
 * FileName    		:  PaymentHeaderListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  27-05-2017    														*
 *                                                                  						*
 * Modified Date    :  27-05-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 27-05-2017       PENNANT	                 0.1                                            * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
*/

package com.pennant.webui.externalupload;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.media.Media;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zul.Button;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.backend.model.systemmasters.DocumentType;
import com.pennant.backend.service.pdfdocumentdetail.PdfDocumentDetailsService;
import com.pennant.backend.service.pdfdocumentdetail.impl.PdfDocumentDetailsServiceImpl;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.search.Filter;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.MessageUtil;
import com.pennanttech.pennapps.core.resource.Literal;

/**
 * This is the controller class for the
 * /WEB-INF/pages/com.pennant.payment/PaymentHeader/PaymentHeaderList.zul file.
 * 
 */
public class ExternalUploadListCtrl extends GFCBaseListCtrl<Object> {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(ExternalUploadListCtrl.class);

	protected Window window_ExternalUploadsList;
	protected Button btnImport;
	protected Row row1;
	protected Textbox fileName;
	protected Button btnFileUpload;
	protected Textbox pdfPassword;
	protected Row passwordRow;
	protected Listbox resultListBox;
	protected ExtendedCombobox formType;
	protected Textbox year;

	private byte[] fileByte;
	private long fileTypeRef = 0;
	/**
	 * default constructor.<br>
	 */
	public ExternalUploadListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
	}

	/**
	 * The framework calls this event handler when an application requests that
	 * the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_ExternalUploadsList(Event event) {
		logger.debug("Entering");
		setPageComponents(window_ExternalUploadsList);
		row1.setVisible(true);
		this.btnImport.setDisabled(true);
		doSetFieldProperties();
		logger.debug("Leaving");
	}

	private void doSetFieldProperties() {
		logger.debug("Entering");

		this.formType.setMaxlength(8);
		this.formType.setMandatoryStyle(true);
		this.formType.setModuleName("PdfDocumentType");
		this.formType.setValueColumn("PdfMappingRef");
		this.formType.setDescColumn("DocTypeCode");
		this.formType.setValidateColumns(new String[] { "PdfMappingRef" });
		Filter[] filters = new Filter[1];
		filters[0] = new Filter("DocIsPdfExtRequired", "1", Filter.OP_EQUAL);
		formType.setFilters(filters);
		logger.debug("Leaving");
	}

	public void onFulfill$formType(Event event) {
		logger.debug("Entering" + event.toString());
		resetListBox();
		Object dataObject = this.formType.getObject();

		if (dataObject instanceof DocumentType) {
			DocumentType documentType = (DocumentType) dataObject;
			fileTypeRef = documentType.getPdfMappingRef();
			if (documentType.isDocIsPasswordProtected()) {
				passwordRow.setVisible(true);
			}else{
				passwordRow.setVisible(false);
			}
		}
		checkReadButtonVisibility();
		logger.debug("Leaving" + event.toString());

	}

	/**
	 * @param event
	 * @throws Exception
	 */
	/** on double click on SELECTED FILE */
	public void onUpload$btnFileUpload(UploadEvent event) throws Exception {
		logger.debug("Entering");

		Media media = event.getMedia();
		fileName.setText(media.getName());
		fileByte = media.getByteData();
		resetListBox();
		checkReadButtonVisibility();
		logger.debug("Leaving");
	}

	private void checkReadButtonVisibility() {
		if (StringUtils.isBlank(formType.getValue()) || StringUtils.isBlank(fileName.getText())
				|| StringUtils.isBlank(year.getText())) {
			this.btnImport.setDisabled(true);
		} else {
			if (passwordRow.isVisible() && StringUtils.isBlank(pdfPassword.getText())) {
				this.btnImport.setDisabled(true);
			} else {
				this.btnImport.setDisabled(false);
			}
		}
	}

	public void onChange$year(Event event) {
		logger.debug("Entering");
		checkReadButtonVisibility();
		logger.debug("Leaving");
	}

	public void onChange$pdfPassword(Event event) {
		logger.debug("Entering");
		checkReadButtonVisibility();
		logger.debug("Leaving");
	}

	/**
	 * when the Source type is changed. <br>
	 * 
	 * @param event
	 * @throws Exception
	 * 
	 *             /** on click UPLOAD BUTTON
	 */
	public void onClick$btnImport(Event event) {
		logger.debug("Entering");

		doSetValidation();
	PdfDocumentDetailsService  documentDetailsService = new PdfDocumentDetailsServiceImpl(); //creating 
		try {
			if (fileByte != null) {
				// entry point
				Map<String, Object> outPut = documentDetailsService.getValueByTypeNYear(fileByte, fileName.getText(),
						pdfPassword.getText(), fileTypeRef, year.getText().trim()); 
				renderResult(outPut);
				MessageUtil.showMessage("File is processed successfully.");
			}
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			if (e.getLocalizedMessage() != null) {
				MessageUtil.showError(e.getLocalizedMessage());
			} else {
				MessageUtil.showError("Something Went Wrong Please Try Again Later.");
			}
			return;
		} finally {
			this.fileByte = null;
			this.passwordRow.setVisible(false);
			this.btnImport.setDisabled(true);
			doRemoveValidation();
			doClearMessage();
		}
		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering ");
		this.fileName.setConstraint("");
		this.formType.setConstraint("");
		this.pdfPassword.setConstraint("");
		this.year.setConstraint("");
		logger.debug("Leaving ");
	}

	/**
	 * Remove Error Messages for Fields
	 */
	protected void doClearMessage() {
		logger.debug("Enterring");
		this.fileName.setErrorMessage("");
		this.pdfPassword.setErrorMessage("");
		this.formType.setErrorMessage("");
		this.year.setErrorMessage("");

		this.fileName.setText("");
		this.pdfPassword.setText("");
		this.formType.setValue("");
		this.year.setText("");
		logger.debug("Leaving");
	}

	private void renderResult(Map<String, Object> outPut) {

		if (!outPut.isEmpty()) {
			this.resultListBox.setVisible(true);
		}
		int i = 1;
		for (Map.Entry<String, Object> entry : outPut.entrySet()) {
			Listitem item = new Listitem();
			Listcell lc;
			lc = new Listcell(String.valueOf(i));
			lc.setParent(item);
			lc = new Listcell(entry.getKey());
			lc.setParent(item);
			lc = new Listcell(String.valueOf(entry.getValue()));
			lc.setParent(item);
			this.resultListBox.appendChild(item);
			i++;
		}

	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering ");

		if (!this.formType.isReadonly()) {
			this.formType
					.setConstraint(new PTStringValidator(Labels.getLabel("label_Form_Type.value"), null, true, true));
		}
		if (this.fileName.isReadonly()) {
			this.fileName.setConstraint(new PTStringValidator(Labels.getLabel("label_FileName.value"), null, true));
		}
		if (this.passwordRow.isVisible()) {
			this.pdfPassword.setConstraint(new PTStringValidator(Labels.getLabel("label_pdf_password.value"),
					PennantRegularExpressions.REGEX_ALPHANUM_SPACE_SPL_COMMAHIPHEN, true));
		}
		if (!this.year.isReadonly()) {
			this.year.setConstraint(new PTStringValidator(Labels.getLabel("label_Ext_Year.value"),
					PennantRegularExpressions.REGEX_NUMERIC, true));
		}
		logger.debug("Leaving ");
	}

	/**
	 *
	 * Resetting listbox data
	 */
	private void resetListBox() {
		this.resultListBox.getItems().clear();
		this.resultListBox.setVisible(false);
	}
}