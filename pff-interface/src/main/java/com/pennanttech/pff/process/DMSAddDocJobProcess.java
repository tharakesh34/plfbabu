package com.pennanttech.pff.process;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.backend.dao.dms.DMSIdentificationDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennanttech.model.dms.DMSDocumentDetails;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.external.creditInformation.AbstractDMSIntegrationService;

public class DMSAddDocJobProcess {
	private static final Logger logger = Logger.getLogger(DMSAddDocJobProcess.class);
	private static final int retryCount = Integer.valueOf(App.getProperty("dms.document.retrycount"));
	@Autowired
	private DMSIdentificationDAO identificationDAO;
	@Autowired
	private AbstractDMSIntegrationService abstractDMSIntegrationService;
	
	public void process() {
		logger.debug(Literal.ENTERING);
		
		List<DMSDocumentDetails> dmsDocRefList = identificationDAO.retrieveDMSDocumentReference();
		
		if(CollectionUtils.isNotEmpty(dmsDocRefList)){
			for (DMSDocumentDetails dmsDocumentDetails : dmsDocRefList) {
				if(null!=dmsDocumentDetails){
					dmsDocumentDetails.setRetryCount(dmsDocumentDetails.getRetryCount()+1);
					boolean success=true;
					String errorMsg=null;
					AuditHeader auditHeader =new AuditHeader();
					auditHeader.setAuditDetail(new AuditDetail());
					auditHeader.getAuditDetail().setModelData(dmsDocumentDetails);
					AuditHeader responseAuditHeader = null;
					try{
						responseAuditHeader = abstractDMSIntegrationService.insertExternalDocument(auditHeader);
					}catch(Exception exception){
						success=false;
						if(null!=exception.getMessage()){
							if(exception.getMessage().length()>200){
								errorMsg=exception.getMessage().substring(0,200);
							}else{
								errorMsg=exception.getMessage();
							}
						}
					}
					
					if(success){
						if(null!=responseAuditHeader && null != responseAuditHeader.getAuditDetail() && null!=responseAuditHeader.getAuditDetail().getModelData()){
							Object object = responseAuditHeader.getAuditDetail().getModelData();
							DMSDocumentDetails responseDmsDocumentDetails=null;
							if (object instanceof DMSDocumentDetails) {
								responseDmsDocumentDetails = (DMSDocumentDetails) object;
							}
							if(null!=responseDmsDocumentDetails){
								identificationDAO.processSuccessResponse(dmsDocumentDetails, responseDmsDocumentDetails);
							}
						}
					}else{
						dmsDocumentDetails.setStatus("Error");
						dmsDocumentDetails.setErrorDesc(errorMsg);
						identificationDAO.processFailure(dmsDocumentDetails, retryCount);
					}
				}
			}
		}
		
		logger.debug(Literal.LEAVING);
	}
}
