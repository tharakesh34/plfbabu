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
 * FileName    		:  CarLoanDetailDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  08-10-2011    														*
 *                                                                  						*
 * Modified Date    :  08-10-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 08-10-2011       Pennant	                 0.1                                            * 
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
package com.pennant.webui.lmtmasters.enquiry;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zul.Caption;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.lmtmasters.CarLoanDetail;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.search.Filter;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.util.GFCBaseCtrl;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/LMTMasters/CarLoanDetail/carLoanDetailDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class CarLoanDetailEnquiryDialogCtrl extends GFCBaseCtrl implements Serializable {
	private static final long serialVersionUID = 5058430665774376406L;
	private final static Logger logger = Logger.getLogger(CarLoanDetailEnquiryDialogCtrl.class);
	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_CarLoanDetailDialog; // autowired
	protected Textbox loanRefNumber; // autowired
	protected Checkbox loanRefType; // autowired
	protected ExtendedCombobox carLoanFor; // autowired
	protected ExtendedCombobox carUsage; // autowired
	protected ExtendedCombobox carManufacturer; // autowired
	protected ExtendedCombobox carModel; // autowired
	protected ExtendedCombobox carVersion; // autowired
	protected Intbox carMakeYear; // autowired
//The Below CarCapacity Field Is Changed To Number Of Cylinders (Only LableName and Purpose)
	protected Intbox carCapacity; // autowired
	protected ExtendedCombobox carDealer; // autowired
	protected Intbox carCc; // autowired
	protected Textbox carChasisNo; // autowired
	protected Textbox carInsuranceNo; // autowired
	protected Textbox carRegNo; // autowired
	protected Combobox cbCarColor; // autowired
	protected Caption caption_carLoan;
	protected Textbox engineNumber;
	protected Combobox insuranceType;
 //The Below InsuranceDescription Field Is Changed To Sales Person Name (Only LableName and Purpose)
	protected Textbox insuranceDesc;
	protected Combobox paymentMode;
	protected Textbox purchageOdrNumber;
	protected Textbox quoationNbr;
	protected Datebox quoationDate;
	protected Textbox dealerPhone;
	protected Datebox purchaseDate;
	protected Label recordStatus; // autowired
	protected Radiogroup userAction;
	protected Groupbox groupboxWf;
	protected Row statusRow;
	// not auto wired vars
	// overhanded per param
	// ServiceDAOs / Domain Classes
	private final List<ValueLabel> carColors = PennantStaticListUtil.getCarColors();
	private final List<ValueLabel> insurenceType =  PennantAppUtil.getInsurenceTypes();
	private final List<ValueLabel> paymentModes = PennantStaticListUtil.getPaymentModes();
	/**
	 * default constructor.<br>
	 */
	public CarLoanDetailEnquiryDialogCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected CarLoanDetail object
	 * in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	
	public void onCreate$window_CarLoanDetailDialog(ForwardEvent event) throws Exception {
		logger.debug("Entering" + event.toString());
		try {

			final Map<String, Object> args = getCreationArgsMap(event);
			doSetFieldProperties();

			if (args.containsKey("data")) {
				CarLoanDetail carLoanDetail = (CarLoanDetail) args.get("data");
				doWriteBeanToComponents(carLoanDetail);
			}
			doReadOnly();

		} catch (Exception e) {
			logger.debug("Leaving" + e.getMessage());
			this.window_CarLoanDetailDialog.onClose();
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		// Empty sent any required attributes
		this.carLoanFor.setInputAllowed(false);
		this.carLoanFor.setDisplayStyle(3);
		this.carLoanFor.setModuleName("CarLoanFor");
		this.carLoanFor.setValueColumn("FieldCodeValue");
		this.carLoanFor.setDescColumn("ValueDesc");
		this.carLoanFor.setValidateColumns(new String[] { "FieldCodeValue" });
		
		this.carUsage.setInputAllowed(false);
		this.carUsage.setDisplayStyle(3);
		this.carUsage.setModuleName("CarUsage");
		this.carUsage.setValueColumn("FieldCodeValue");
		this.carUsage.setDescColumn("ValueDesc");
		this.carUsage.setValidateColumns(new String[] { "FieldCodeValue" });
		
		this.carManufacturer.setInputAllowed(false);
		this.carManufacturer.setDisplayStyle(3);
		this.carManufacturer.setModuleName("VehicleManufacturer");
		this.carManufacturer.setValueColumn("ManufacturerId");
		this.carManufacturer.setDescColumn("ManufacturerName");
		this.carManufacturer.setValidateColumns(new String[] { "ManufacturerId" });
		Filter[] manufactureFilters = new Filter[1];
		manufactureFilters[0] = new Filter("ManufacturerId", 0, Filter.OP_NOT_EQUAL);
		this.carManufacturer.setFilters(manufactureFilters);
		
		this.carModel.setInputAllowed(false);
		this.carModel.setDisplayStyle(3);
		this.carModel.setModuleName("VehicleModel");
		this.carModel.setValueColumn("VehicleModelId");
		this.carModel.setDescColumn("VehicleModelDesc");
		this.carModel.setValidateColumns(new String[] { "VehicleModelId" });
		
		this.carVersion.setInputAllowed(false);
		this.carVersion.setDisplayStyle(3);
		this.carVersion.setModuleName("VehicleVersion");
		this.carVersion.setValueColumn("VehicleVersionId");
		this.carVersion.setDescColumn("VehicleVersionCode");
		this.carVersion.setValidateColumns(new String[] { "VehicleVersionId" });
		
		this.carDealer.setInputAllowed(false);
		this.carDealer.setDisplayStyle(3);
		this.carDealer.setModuleName("VehicleDealer");
		this.carDealer.setValueColumn("DealerId");
		this.carDealer.setDescColumn("DealerName");
		this.carDealer.setValidateColumns(new String[] { "DealerId" });
		Filter carDealerfilter[] = new Filter[1];
		carDealerfilter[0] = new Filter("DealerType", "V", Filter.OP_EQUAL);
		this.carDealer.setFilters(carDealerfilter);
		
		this.carMakeYear.setMaxlength(4);
		this.carCapacity.setMaxlength(2);
		this.engineNumber.setMaxlength(50);
		this.insuranceDesc.setMaxlength(50);
		this.quoationNbr.setMaxlength(50);
		this.purchageOdrNumber.setMaxlength(50);
		this.carCc.setMaxlength(5);
		this.carChasisNo.setMaxlength(19);
		this.carInsuranceNo.setMaxlength(19);
		this.carRegNo.setMaxlength(19);
		this.quoationDate.setFormat(PennantConstants.dateFormat);
		this.purchaseDate.setFormat(PennantConstants.dateFormat);
		logger.debug("Leaving");
	}




	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aCarLoanDetail
	 *            CarLoanDetail
	 */
	public void doWriteBeanToComponents(CarLoanDetail aCarLoanDetail) {
		logger.debug("Entering");
		this.loanRefNumber.setValue(aCarLoanDetail.getLoanRefNumber());
		this.loanRefType.setChecked(aCarLoanDetail.isLoanRefType());
		this.carLoanFor.setValue(String.valueOf(aCarLoanDetail.getCarLoanFor()));
		this.carUsage.setValue(String.valueOf(aCarLoanDetail.getCarUsage()));
		this.carManufacturer.setValue(String.valueOf(aCarLoanDetail.getManufacturerId()));
		this.carModel.setValue(String.valueOf(aCarLoanDetail.getVehicleModelId()));
		this.carVersion.setValue(String.valueOf(aCarLoanDetail.getCarVersion()));
		this.carMakeYear.setValue(aCarLoanDetail.getCarMakeYear());
		this.carCapacity.setValue(aCarLoanDetail.getCarCapacity());
		this.carDealer.setValue(String.valueOf(aCarLoanDetail.getCarDealer()));
		this.carCc.setValue(aCarLoanDetail.getCarCc());
		this.dealerPhone.setValue(aCarLoanDetail.getDealerPhone());
		this.carChasisNo.setValue(aCarLoanDetail.getCarChasisNo());
		this.carInsuranceNo.setValue(aCarLoanDetail.getCarInsuranceNo());
		this.carRegNo.setValue(aCarLoanDetail.getCarRegNo());
		fillComboBox(this.cbCarColor, aCarLoanDetail.getCarColor(), carColors, "");
		fillComboBox(this.insuranceType, aCarLoanDetail.getInsuranceType(), insurenceType, "");
		fillComboBox(this.paymentMode, aCarLoanDetail.getPaymentMode(), paymentModes, "");
		this.engineNumber.setValue(aCarLoanDetail.getEngineNumber());
		this.insuranceDesc.setValue(aCarLoanDetail.getInsuranceDesc());
		this.purchageOdrNumber.setValue(aCarLoanDetail.getPurchageOdrNumber());
		this.quoationNbr.setValue(aCarLoanDetail.getQuoationNbr());
		this.quoationDate.setValue(aCarLoanDetail.getQuoationDate());
		this.purchaseDate.setValue(aCarLoanDetail.getPurchaseDate());
		
		this.carLoanFor.setDescription(aCarLoanDetail.getLovDescLoanForValue());
		this.carUsage.setDescription(aCarLoanDetail.getLovDescCarUsageValue());
		this.carManufacturer.setDescription(aCarLoanDetail.getLovDescManufacturerName());
		this.carModel.setDescription(aCarLoanDetail.getLovDescModelDesc());
		this.carVersion.setDescription(aCarLoanDetail.getLovDescVehicleVersionCode());
		this.carDealer.setDescription(aCarLoanDetail.getLovDescCarDealerName());
	
		logger.debug("Leaving");
	}


	

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");
		this.loanRefNumber.setReadonly(true);
		this.loanRefType.setDisabled(true);
		this.carLoanFor.setReadonly(true);
		this.carUsage.setReadonly(true);
		this.carManufacturer.setReadonly(true);
		this.carModel.setReadonly(true);
		this.carVersion.setReadonly(true);
		this.carMakeYear.setReadonly(true);
		this.carCapacity.setReadonly(true);
		this.carDealer.setReadonly(true);
		this.carCc.setReadonly(true);
		this.dealerPhone.setReadonly(true);
		this.engineNumber.setReadonly(true);
		this.insuranceType.setDisabled(true);
		this.paymentMode.setDisabled(true);
		this.cbCarColor.setDisabled(true);
		this.carChasisNo.setReadonly(true);
		this.insuranceDesc.setReadonly(true);
		this.carInsuranceNo.setReadonly(true);
		this.carRegNo.setReadonly(true);
		this.quoationNbr.setReadonly(true);
		this.quoationDate.setDisabled(true);
		this.purchageOdrNumber.setReadonly(true);
		this.purchaseDate.setDisabled(true);
		logger.debug("Leaving");
	}


}
