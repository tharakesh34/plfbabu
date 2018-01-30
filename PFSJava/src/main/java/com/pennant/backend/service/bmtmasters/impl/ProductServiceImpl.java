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
 * FileName    		:  ProductServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  12-08-2011    														*
 *                                                                  						*
 * Modified Date    :  12-08-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 12-08-2011       Pennant	                 0.1                                            * 
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
package com.pennant.backend.service.bmtmasters.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.bmtmasters.ProductDAO;
import com.pennant.backend.dao.rmtmasters.ProductAssetDAO;
import com.pennant.backend.model.ErrorDetail;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.bmtmasters.Product;
import com.pennant.backend.model.rmtmasters.ProductAsset;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.bmtmasters.ProductService;
import com.pennant.backend.service.rmtmasters.validation.ProductAssetValidation;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Service implementation for methods that depends on <b>Product</b>.<br>
 * 
 */
public class ProductServiceImpl extends GenericService<Product> implements ProductService {
	private static final Logger logger = Logger.getLogger(ProductServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private ProductDAO productDAO;
	private ProductAssetDAO productAssetDAO;
	private ProductAssetValidation productAssetValidation;

	public ProductServiceImpl() {
		super();
	}
	
	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public AuditHeaderDAO getAuditHeaderDAO() {
		return auditHeaderDAO;
	}
	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	public ProductDAO getProductDAO() {
		return productDAO;
	}
	public void setProductDAO(ProductDAO productDAO) {
		this.productDAO = productDAO;
	}

	public ProductAssetDAO getProductAssetDAO() {
		return productAssetDAO;
	}
	public void setProductAssetDAO(ProductAssetDAO productAssetDAO) {
		this.productAssetDAO = productAssetDAO;
	}

	public ProductAssetValidation getProductAssetValidation(){

		if(productAssetValidation==null){
			this.productAssetValidation = new ProductAssetValidation(productAssetDAO);
		}
		return this.productAssetValidation;
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business
	 * validation by using businessValidation(auditHeader) method if there is
	 * any error or warning message then return the auditHeader. 2) Do Add or
	 * Update the Record a) Add new Record for the new record in the DB table
	 * BMTProduct/BMTProduct_Temp by using ProductDAO's save method b) Update
	 * the Record in the table. based on the module workFlow Configuration. by
	 * using ProductDAO's update method 3) Audit the record in to AuditHeader
	 * and AdtBMTProduct by using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */

	@Override
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.debug("Entering");	

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		auditHeader = businessValidation(auditHeader,"saveOrUpdate");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		String tableType="";
		Product product = (Product) auditHeader.getAuditDetail().getModelData();

		if (product.isWorkflow()) {
			tableType="_Temp";
		}

		if (product.isNew()) {
			getProductDAO().save(product,tableType);
		}else{
			getProductDAO().update(product,tableType);
		}

		if(product.getProductAssetList()!=null && product.getProductAssetList().size()>0){
			List<AuditDetail> details = product.getAuditDetailMap().get("ProductAsset");
			details = processingProductAssetList(details,tableType);
			auditDetails.addAll(details);
		}

		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(),1, product.getBefImage(), product));
		auditHeader.setAuditDetails(auditDetails);

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) delete Record for the DB
	 * table BMTProduct by using ProductDAO's delete method with type as Blank
	 * 3) Audit the record in to AuditHeader and AdtBMTProduct by using
	 * auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */

	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		logger.debug("Entering");

		auditHeader = businessValidation(auditHeader, "delete");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		Product product = (Product) auditHeader.getAuditDetail().getModelData();
		getProductDAO().delete(product, "");
		listDeletion(product, "");

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getProductById fetch the details by using ProductDAO's getProductById
	 * method.
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return Product
	 */

	@Override
	public Product getProductById(String id, String code) {
		logger.debug("Entering");
		Product product = getProductDAO().getProductByID(id,code, "_View");
		product.setProductAssetList(getProductAssetDAO().getProductAssetByProdCode(product.getProductCode(), "_View"));
		logger.debug("Leaving");
		return product;
	}

	@Override
	public ProductAsset getNewProductAsset() {
		return getProductAssetDAO().getNewProductAsset();
	}	
	/**
	 * getApprovedProductById fetch the details by using ProductDAO's
	 * getProductById method . with parameter id and type as blank. it fetches
	 * the approved records from the BMTProduct.
	 * 
	 * @param id
	 *            (String)
	 * @return Product
	 */

	public Product getApprovedProductById(String id, String code) {
		Product product = getProductDAO().getProductByID(id,code, "_AView");
		product.setProductAssetList(getProductAssetDAO().getProductAssetByProdCode(product.getProductCode(),"_AView"));
		return product;
	}
	
	public String getProductCtgByProduct(String productCode) {
		return getProductDAO().getProductCtgByProduct(productCode);
	}


	/**
	 * doApprove method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) based on the Record type
	 * do following actions a) DELETE Delete the record from the main table by
	 * using getProductDAO().delete with parameters product,"" b) NEW Add new
	 * record in to main table by using getProductDAO().save with parameters
	 * product,"" c) EDIT Update record in the main table by using
	 * getProductDAO().update with parameters product,"" 3) Delete the record
	 * from the workFlow table by using getProductDAO().delete with parameters
	 * product,"_Temp" 4) Audit the record in to AuditHeader and AdtBMTProduct
	 * by using auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit the
	 * record in to AuditHeader and AdtBMTProduct by using
	 * auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */

	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.debug("Entering");

		String tranType = "";
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		auditHeader = businessValidation(auditHeader, "doApprove");
		if (!auditHeader.isNextProcess()) {
			return auditHeader;
		}

		Product product = new Product();
		BeanUtils.copyProperties((Product) auditHeader.getAuditDetail().getModelData(), product);

		if (product.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			getProductDAO().delete(product, "");
			listDeletion(product, "");
		} else {
			product.setRoleCode("");
			product.setNextRoleCode("");
			product.setTaskId("");
			product.setNextTaskId("");
			product.setWorkflowId(0);

			if (product.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				product.setRecordType("");
				getProductDAO().save(product, "");
			} else {
				tranType = PennantConstants.TRAN_UPD;
				product.setRecordType("");
				getProductDAO().update(product, "");
			}

			//Retrieving List of Audit Details For Product Asset related modules
			if(product.getProductAssetList()!=null && product.getProductAssetList().size()>0){
				List<AuditDetail> details = product.getAuditDetailMap().get("ProductAsset");
				details = processingProductAssetList(details,"");
				auditDetails.addAll(details);
			}
		}

		getProductDAO().delete(product, "_Temp");
		listDeletion(product, "_Temp");

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(),1, product.getBefImage(), product));
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(),1, product.getBefImage(), product));
		auditHeader.setAuditDetails(getListAuditDetails(auditDetails));
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");

		return auditHeader;
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) Delete the record from
	 * the workFlow table by using getProductDAO().delete with parameters
	 * product,"_Temp" 3) Audit the record in to AuditHeader and AdtBMTProduct
	 * by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */

	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.debug("Entering");

		auditHeader = businessValidation(auditHeader, "doReject");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		Product product = (Product) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getProductDAO().delete(product, "_Temp");

		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, product.getBefImage(), product));
		listDeletion(product, "_Temp");
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");

		return auditHeader;
	}

	/**
	 * businessValidation method do the following steps. 1) get the details from
	 * the auditHeader. 2) fetch the details from the tables 3) Validate the
	 * Record based on the record details. 4) Validate for any business
	 * validation.
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	private AuditHeader businessValidation(AuditHeader auditHeader,String method) {
		logger.debug("Entering");

		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(),auditHeader.getUsrLanguage(), method);
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader = getAuditDetails(auditHeader, method);

		Product product = (Product) auditHeader.getAuditDetail().getModelData();
		String usrLanguage = product.getUserDetails().getLanguage();

		// ProductAsset Validation
		if(product.getProductAssetList()!=null && product.getProductAssetList().size()>0){
			List<AuditDetail> details = product.getAuditDetailMap().get("ProductAsset");
			details = getProductAssetValidation().pAssetListValidation(details, method, usrLanguage);
			if(details!= null){
			auditDetails.addAll(details);
			}
		}

		for (int i = 0; i < auditDetails.size(); i++) {
			auditHeader.setErrorList(auditDetails.get(i).getErrorDetails());	
		}

		auditHeader = nextProcess(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * For Validating AuditDetals object getting from Audit Header, if any
	 * mismatch conditions Fetch the error details from
	 * getProductDAO().getErrorDetail with Error ID and language as parameters.
	 * if any error/Warnings then assign the to auditDeail Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @param method
	 * @return
	 */
	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage,String method) {
		logger.debug("Entering");

		auditDetail.setErrorDetails(new ArrayList<ErrorDetail>());
		Product product = (Product) auditDetail.getModelData();

		Product tempProduct = null;
		if (product.isWorkflow()) {
			tempProduct = getProductDAO().getProductByID(product.getId(),product.getProductCode(),"_Temp");
		}
		Product befProduct = getProductDAO().getProductByID(product.getId(),product.getProductCode(), "");
		Product oldProduct = product.getBefImage();

		String[] valueParm = new String[1];
		String[] errParm = new String[1];
		valueParm[0] = product.getProductCode();

		errParm[0] = PennantJavaUtil.getLabel("label_ProductCode") + ":"+ valueParm[0];

		if (product.isNew()) { // for New record or new record into work flow

			if (!product.isWorkflow()) {// With out Work flow only new records
				if (befProduct != null) { // Record Already Exists in the table then error
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41001",errParm,valueParm),usrLanguage));

				}
			} else { // with work flow
				if (product.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) { // if records type is new
					if (befProduct != null || tempProduct != null) { // if records already exists in the main table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm,valueParm),usrLanguage));
					}
				} else { // if records not exists in the Main flow table
					if (befProduct == null || tempProduct != null) {
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm,valueParm),usrLanguage));
					}
				}

			}
		} else {
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!product.isWorkflow()) { // With out Work flow for update and delete

				if (befProduct == null) { // if records not exists in the main table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(
							PennantConstants.KEY_FIELD, "41002",errParm, valueParm),usrLanguage));
				} else {
					if (oldProduct != null && !oldProduct.getLastMntOn().equals(befProduct.getLastMntOn())) {
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType())
								.equalsIgnoreCase(PennantConstants.TRAN_DEL)) {
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(
									PennantConstants.KEY_FIELD, "41003",errParm, valueParm),usrLanguage));
						} else {
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(
									PennantConstants.KEY_FIELD, "41004",errParm, valueParm),usrLanguage));
						}
					}
				}
			} else {

				if (tempProduct == null) { // if records not exists in the Work flow table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(
							PennantConstants.KEY_FIELD, "41005",errParm, valueParm),usrLanguage));
				}

				if (tempProduct != null && oldProduct != null && !oldProduct.getLastMntOn().equals(
						tempProduct.getLastMntOn())) {
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(
							PennantConstants.KEY_FIELD, "41005",errParm, valueParm),usrLanguage));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if ("doApprove".equals(StringUtils.trimToEmpty(method))|| !product.isWorkflow()) {
			auditDetail.setBefImage(befProduct);
		}
		logger.debug("Leaving");
		return auditDetail;
	}

	/**
	 * Common Method for Retrieving AuditDetails List
	 * @param auditHeader
	 * @param method
	 * @return
	 */
	private AuditHeader getAuditDetails(AuditHeader auditHeader,String method ){
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		HashMap<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();


		Product product = (Product) auditHeader.getAuditDetail().getModelData();

		String auditTranType="";

		if("saveOrUpdate".equals(method) || "doApprove".equals(method) || "doReject".equals(method) ){
			if (product.isWorkflow()) {
				auditTranType= PennantConstants.TRAN_WF;
			}
		}

		if(product.getProductAssetList()!=null && product.getProductAssetList().size()>0){
			auditDetailMap.put("ProductAsset", setPAssetAuditData(product,auditTranType,method));
			auditDetails.addAll(auditDetailMap.get("ProductAsset"));
		}

		product.setAuditDetailMap(auditDetailMap);
		auditHeader.getAuditDetail().setModelData(product);
		auditHeader.setAuditDetails(auditDetails);

		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * Methods for Creating List of Audit Details with detailed fields
	 * @param Product
	 * @param auditTranType
	 * @param method
	 * @return
	 */
	private List<AuditDetail> setPAssetAuditData(Product product,String auditTranType,String method) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		String[] fields = PennantJavaUtil.getFieldDetails(new ProductAsset());


		for (int i = 0; i < product.getProductAssetList().size(); i++) {
			ProductAsset productAsset = product.getProductAssetList().get(i);
			
			if(StringUtils.isEmpty(productAsset.getRecordType())){
				continue;
			}
			
			productAsset.setWorkflowId(product.getWorkflowId());
			productAsset.setProductCode(product.getProductCode());

			boolean isRcdType= false;

			if (productAsset.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				productAsset.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType=true;
			}else if (productAsset.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
				productAsset.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				isRcdType=true;
			}else if (productAsset.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
				productAsset.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}

			if ("saveOrUpdate".equals(method) && isRcdType) {
				productAsset.setNewRecord(true);
			}

			if(!auditTranType.equals(PennantConstants.TRAN_WF)){
				if (productAsset.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType= PennantConstants.TRAN_ADD;
				} else if (productAsset.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
						|| productAsset.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					auditTranType= PennantConstants.TRAN_DEL;
				}else{
					auditTranType= PennantConstants.TRAN_UPD;
				}
			}

			productAsset.setRecordStatus(product.getRecordStatus());
			productAsset.setLoginDetails(product.getUserDetails());
			productAsset.setLastMntOn(product.getLastMntOn());

			auditDetails.add(new AuditDetail(auditTranType, i+1, fields[0], fields[1], productAsset.getBefImage(), productAsset));
		}

		logger.debug("Leaving");
		return auditDetails;
	}
	/**
	 * Method For Preparing List of AuditDetails for Product Assets
	 * @param auditDetails
	 * @param type
	 * @param custId
	 * @return
	 */
	private List<AuditDetail> processingProductAssetList(List<AuditDetail> auditDetails, String type) {

		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec=false;


		for (int i = 0; i < auditDetails.size(); i++) {

			ProductAsset productAsset = (ProductAsset) auditDetails.get(i).getModelData();
			saveRecord = false;
			updateRecord = false;
			deleteRecord = false;
			approveRec=false;
			String rcdType ="";	
			String recordStatus ="";
			if (StringUtils.isEmpty(type)) {
				approveRec=true;
				productAsset.setRoleCode("");
				productAsset.setNextRoleCode("");
				productAsset.setTaskId("");
				productAsset.setNextTaskId("");
			}

			productAsset.setWorkflowId(0);

			if (productAsset.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				deleteRecord=true;
			}else  if(productAsset.isNewRecord()){
				saveRecord=true;
				if (productAsset.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					productAsset.setRecordType(PennantConstants.RECORD_TYPE_NEW);	
				} else if (productAsset.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
					productAsset.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (productAsset.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
					productAsset.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}

			}else if (productAsset.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
				if(approveRec){
					saveRecord=true;
				}else{
					updateRecord=true;
				}
			}else if (productAsset.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
				updateRecord=true;
			}else if (productAsset.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
				if(approveRec){
					deleteRecord=true;
				}else if(productAsset.isNew()){
					saveRecord=true;
				}else{
					updateRecord=true;
				}
			}
			if(approveRec){
				rcdType= productAsset.getRecordType();
				recordStatus = productAsset.getRecordStatus();
				productAsset.setRecordType("");
				productAsset.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			}
			if (saveRecord) {
				productAssetDAO.save(productAsset, type);
			}

			if (updateRecord) {
				productAssetDAO.update(productAsset, type);
			}

			if (deleteRecord) {
				productAssetDAO.delete(productAsset, type);
			}

			if(approveRec){
				productAsset.setRecordType(rcdType);
				productAsset.setRecordStatus(recordStatus);
			}
			auditDetails.get(i).setModelData(productAsset);
		}

		return auditDetails;

	}

	/**
	 * Method deletion of productAsset list with existing product type
	 * @param product
	 * @param tableType
	 */
	public void listDeletion(Product product, String tableType) {

		if(product.getProductAssetList()!=null && product.getProductAssetList().size()>0){
			getProductAssetDAO().deleteByProduct(product.getProductAssetList().get(0), tableType);
		}
	}	

	/** 
	 * Common Method for Product Asset list validation
	 * @param list
	 * @param method
	 * @param userDetails
	 * @param lastMntON
	 * @return
	 */
	private List<AuditDetail> getListAuditDetails(List<AuditDetail> list){
		logger.debug("Entering");
		List<AuditDetail> auditDetailsList =new ArrayList<AuditDetail>();

		if (list != null && list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {

				String transType="";
				String rcdType = "";
				ProductAsset productAsset = (ProductAsset) ((AuditDetail)list.get(i)).getModelData();			

				rcdType = productAsset.getRecordType();

				if (rcdType.equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					transType= PennantConstants.TRAN_ADD;
				} else if (rcdType.equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL) || 
						rcdType.equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					transType= PennantConstants.TRAN_DEL;
				}else{
					transType= PennantConstants.TRAN_UPD;
				}

				if(StringUtils.isNotEmpty(transType)){
					//check and change below line for Complete code
					auditDetailsList.add(new AuditDetail(transType, ((AuditDetail)list.get(i)).getAuditSeq(),
							productAsset.getBefImage(), productAsset));
				}
			}
		}
		logger.debug("Leaving");
		return auditDetailsList;
	}
}