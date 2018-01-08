package com.pennanttech.pff.document.external;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.external.DocumentManagementService;

public class ExternalDocumentManager {
	private static final Logger logger = Logger.getLogger(ExternalDocumentManager.class);

	@Autowired(required = false)
	private DocumentManagementService	documentManagementService;

	public DocumentDetails getExternalDocument(String docRefId, String reference) {
		logger.debug(Literal.ENTERING);
		
		String[] docRefIds = null;
		List<DocumentDetails> documentDetailList = new ArrayList<>(1);
		if (StringUtils.contains(docRefId, ",")) {
			docRefIds = docRefId.split(",");
		} else {
			docRefIds = new String[1];
			docRefIds[0] = docRefId;
		}
		
//		docRefIds = new String[2];
//		docRefIds[0] = "284068";
//		docRefIds[1] = "271980";
		
		for (String docExternalRefId : docRefIds) {
			DocumentDetails details = documentManagementService.getExternalDocument(docExternalRefId, reference);
			documentDetailList.add(details);
		}
		
		if (!documentDetailList.isEmpty()) {
			if ( documentDetailList.size()==1) {
				return documentDetailList.get(0);
			}else{
//				int length=0;
//				
//				for (DocumentDetails documentDetails : documentDetailList) {
//					length = length + documentDetails.getDocImage().length;
//				}
//
//				byte[] combined = new byte[length];
//				
//				int postion=0;
//				for (DocumentDetails documentDetails : documentDetailList) {
//					System.arraycopy(documentDetails.getDocImage(), 0, combined, postion, documentDetails.getDocImage().length);
//					postion=documentDetails.getDocImage().length;
//				}
//				documentDetailList.get(0).setDocImage(combined);
				return documentDetailList.get(0);
			}
		}
		
		logger.debug(Literal.LEAVING);
		return  documentDetailList.get(0);
	}
}
