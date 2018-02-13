package com.pennant.backend.service.rmtmasters.validation;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.rmtmasters.ProductAssetDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.rmtmasters.ProductAsset;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;

public class ProductAssetValidation {

	private ProductAssetDAO productAssetDAO;


	public ProductAssetValidation(ProductAssetDAO productAssetDAO) {
		this.productAssetDAO = productAssetDAO;
	}
	public ProductAssetDAO getProductAssetDAO() {
		return productAssetDAO;
	}


	public AuditHeader pAssetValidation(AuditHeader auditHeader, String method){

		AuditDetail auditDetail =   validate(auditHeader.getAuditDetail(), method, auditHeader.getUsrLanguage());
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		return auditHeader;
	}

	public List<AuditDetail> pAssetListValidation(List<AuditDetail> auditDetails, String method,String  usrLanguage){

		if(auditDetails!=null && auditDetails.size()>0){
			List<AuditDetail> details = new ArrayList<AuditDetail>();
			for (int i = 0; i < auditDetails.size(); i++) {
				AuditDetail auditDetail =   validate(auditDetails.get(i), method, usrLanguage);
				details.add(auditDetail); 		
			}
			return details;
		}
		return null;
	}

	/**
	 * For Validating AuditDetals object getting from Audit Header, if any
	 * mismatch conditions Fetch the error details from ErrorControl with Error
	 * ID and language as parameters. if any error/Warnings then assign the to
	 * auditDeail Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @param method
	 * @return
	 */	
	private AuditDetail validate(AuditDetail auditDetail, String method,String  usrLanguage){

		ProductAsset productAsset= (ProductAsset) auditDetail.getModelData();
		ProductAsset tempProductAsset= null;
		if (productAsset.isWorkflow()){
			tempProductAsset = getProductAssetDAO().getProductAssetById(productAsset.getId(),"_Temp");
		}

		ProductAsset befProductAsset= getProductAssetDAO().getProductAssetById(productAsset.getId(),"");
		ProductAsset oldProductAsset= productAsset.getBefImage();

		String[] valueParm = new String[3];
		String[] errParm = new String[3];

		valueParm[0] = String.valueOf(productAsset.getAssetID());
		valueParm[1] = productAsset.getProductCode();
		valueParm[2] = productAsset.getAssetCode();

		errParm[0] = PennantJavaUtil.getLabel("label_AssetID") + ":"+ valueParm[0];
		errParm[1] = PennantJavaUtil.getLabel("label_ProductCode") + ":"+valueParm[1];
		errParm[2] = PennantJavaUtil.getLabel("label_AssetCode") + ":"+valueParm[2];

		if (productAsset.isNew()){ // for New record or new record into work flow

			if (!productAsset.isWorkflow()){// With out Work flow only new records  
				if (befProductAsset !=null){	// Record Already Exists in the table then error  
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41001",errParm,null));
				}	
			}else{ // with work flow

				if (productAsset.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){ // if records type is new
					if (befProductAsset !=null || tempProductAsset!=null ){ // if records already exists in the main table
						auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41001",errParm,null));
					}
				}else{ // if records not exists in the Main flow table
					if (befProductAsset ==null || tempProductAsset!=null ){
						auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41005",errParm,null));
					}
				}
			}
		}else{
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!productAsset.isWorkflow()){	// With out Work flow for update and delete

				if (befProductAsset ==null){ // if records not exists in the main table
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41002",errParm,null));
				}else{

					if (oldProductAsset!=null && !oldProductAsset.getLastMntOn().equals(befProductAsset.getLastMntOn())){
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType()).equalsIgnoreCase(PennantConstants.TRAN_DEL)){
							auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41003",errParm,null));	
						}else{
							auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41004",errParm,null));
						}
					}
				}

			}else{

				if (tempProductAsset==null ){ // if records not exists in the Work flow table 
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41005",errParm,null));
				}

				if (tempProductAsset!=null  && oldProductAsset!=null && !oldProductAsset.getLastMntOn().equals(tempProductAsset.getLastMntOn())){ 
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41005",errParm,null));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if("doApprove".equals(StringUtils.trimToEmpty(method)) || !productAsset.isWorkflow()){
			auditDetail.setBefImage(befProductAsset);	
		}

		return auditDetail;
	}

}
