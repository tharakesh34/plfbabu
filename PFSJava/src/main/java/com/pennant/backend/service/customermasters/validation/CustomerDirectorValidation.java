package com.pennant.backend.service.customermasters.validation;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.zkoss.util.resource.Labels;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.customermasters.DirectorDetailDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.DirectorDetail;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;

public class CustomerDirectorValidation {

	private DirectorDetailDAO directorDetailDAO;
	
	
	public CustomerDirectorValidation(DirectorDetailDAO directorDetailDAO) {
		this.directorDetailDAO = directorDetailDAO;
	}
	
	/**
	 * @return the directorDetailDAODAO
	 */
	public DirectorDetailDAO getDirectorDetailDAO() {
		return directorDetailDAO;
	}
	

	public AuditHeader directorValidation(AuditHeader auditHeader, String method){
		
		AuditDetail auditDetail =   validate(auditHeader.getAuditDetail(), method, auditHeader.getUsrLanguage());
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		return auditHeader;
	}

	public List<AuditDetail> directorListValidation(List<AuditDetail> auditDetails, String method,String  usrLanguage){
		
		if(auditDetails!=null && auditDetails.size()>0){
			List<AuditDetail> details = new ArrayList<AuditDetail>();
			for (int i = 0; i < auditDetails.size(); i++) {
				AuditDetail auditDetail =   validate(auditDetails.get(i), method, usrLanguage);
				details.add(auditDetail); 		
			}
			return details;
		}
		return new ArrayList<AuditDetail>();
	}

		
	private AuditDetail validate(AuditDetail auditDetail, String method,String  usrLanguage){
		
		auditDetail.setErrorDetails(new ArrayList<ErrorDetail>());			
		DirectorDetail directorDetail= (DirectorDetail) auditDetail.getModelData();

		DirectorDetail tempDirectorDetail= null;
		if (directorDetail.isWorkflow()){
			tempDirectorDetail = getDirectorDetailDAO().getDirectorDetailById(
					directorDetail.getDirectorId(),directorDetail.getCustID(), "_Temp");
		}
		DirectorDetail befDirectorDetail= getDirectorDetailDAO().getDirectorDetailById(
				directorDetail.getDirectorId(),directorDetail.getCustID(), "");

		DirectorDetail oldDirectorDetail= directorDetail.getBefImage();


		String[] errParm= new String[2];
		String[] valueParm= new String[2];
		String name = "";
        if(StringUtils.isNotBlank(directorDetail.getShortName())){
        	name = directorDetail.getShortName();
        }else{
        	 name = directorDetail.getFirstName() + "  " + directorDetail.getLastName();
        }
		
		valueParm[0] = StringUtils.trimToEmpty(directorDetail.getLovDescCustCIF());
		valueParm[1]=name;
	
		errParm[0] = PennantJavaUtil.getLabel("DirectorDetails") +" , " + PennantJavaUtil.getLabel("label_CustCIF") + ":" + valueParm[0]+ " and ";
	    errParm[1] = PennantJavaUtil.getLabel("label_DirectorDetailDialog_ShortName.value") + "-" + valueParm[1];
		
		if (directorDetail.isNew()){ // for New record or new record into work flow

			if (!directorDetail.isWorkflow()){// With out Work flow only new records  
				if (befDirectorDetail !=null){	// Record Already Exists in the table then error  
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41001", 
									errParm,valueParm), usrLanguage));
				}	
			}else{ // with work flow
				if (directorDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){ // if records type is new
					if (befDirectorDetail !=null || tempDirectorDetail!=null ){ // if records already exists in the main table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41001",
										errParm,valueParm), usrLanguage));
					}
				}else{ // if records not exists in the Main flow table
					if (befDirectorDetail ==null || tempDirectorDetail!=null ){
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41005", 
										errParm,valueParm), usrLanguage));
					}
				}
			}
		}else{
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!directorDetail.isWorkflow()){	// With out Work flow for update and delete

				if (befDirectorDetail ==null){ // if records not exists in the main table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41002", 
									errParm,valueParm), usrLanguage));
				}else{
					if (oldDirectorDetail!=null && !oldDirectorDetail.getLastMntOn().equals(
							befDirectorDetail.getLastMntOn())){
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType()).equalsIgnoreCase(
								PennantConstants.TRAN_DEL)){
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
									new ErrorDetail(PennantConstants.KEY_FIELD, "41003", 
											errParm,valueParm), usrLanguage));
						}else{
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
									new ErrorDetail(PennantConstants.KEY_FIELD, "41004", 
											errParm,valueParm), usrLanguage));
						}
					}
				}
			}else{

				if (tempDirectorDetail==null ){ // if records not exists in the Work flow table 
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41005", 
									errParm,valueParm), usrLanguage));
				}

				if (tempDirectorDetail != null && oldDirectorDetail!=null && !oldDirectorDetail.getLastMntOn().equals(
						tempDirectorDetail.getLastMntOn())){ 
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41005", 
									errParm,valueParm), usrLanguage));
				}
			}
		}

		auditDetail.setErrorDetail(screenValidations(directorDetail));
		
		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if("doApprove".equals(StringUtils.trimToEmpty(method)) || !directorDetail.isWorkflow()){
			directorDetail.setBefImage(befDirectorDetail);	
		}

		return auditDetail;
	}

	/**
	 * Method For Screen Level Validations
	 * 
	 * @param auditHeader
	 * @param usrLanguage
	 * @return
	 */
	public ErrorDetail  screenValidations(DirectorDetail directorDetail){
		
		String shareHolderName = "";
        if(StringUtils.isNotBlank(directorDetail.getShortName())){
        	shareHolderName = directorDetail.getShortName();
        }else if(StringUtils.isNotBlank(directorDetail.getFirstName()) && StringUtils.isNotBlank(directorDetail.getLastName())){
        	shareHolderName = directorDetail.getFirstName() + "  " + directorDetail.getLastName();
        }
		if(StringUtils.isBlank(directorDetail.getShortName()) && 
				StringUtils.isBlank(directorDetail.getFirstName()) && 
				StringUtils.isBlank(directorDetail.getLastName())){
			return	new ErrorDetail(PennantConstants.KEY_FIELD,"30535", 
					new String[] {Labels.getLabel("DirectorDetails"),
					Labels.getLabel("label_DirectorDetailDialog_ShortName.value"),
					Labels.getLabel("listheader_ShortName.label"),
					shareHolderName},
					new String[] {});	
		}	
		if(directorDetail.getSharePerc() == null){
			return	new ErrorDetail(PennantConstants.KEY_FIELD,"30535", 
					new String[] {Labels.getLabel("DirectorDetails"),
					Labels.getLabel("label_DirectorDetailDialog_SharePerc.value"),
					Labels.getLabel("listheader_ShortName.label"),
					shareHolderName},
					new String[] {});		
		}
		
		if(StringUtils.isBlank(directorDetail.getCustAddrCountry())){
			return	new ErrorDetail(PennantConstants.KEY_FIELD,"30535", 
					new String[] {Labels.getLabel("DirectorDetails"),
					Labels.getLabel("label_DirectorDetailDialog_CustAddrCountry.value"),
					Labels.getLabel("listheader_ShortName.label"),
					shareHolderName},
					new String[] {});	
		}
		
		return null;
	}
	
}
