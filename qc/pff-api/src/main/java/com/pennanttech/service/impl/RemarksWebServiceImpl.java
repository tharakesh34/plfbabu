package com.pennanttech.service.impl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pennant.backend.dao.administration.SecurityUserDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.WSReturnStatus;
import com.pennant.ws.exception.ServiceException;
import com.pennanttech.controller.RemarksController;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pffws.RemarksRestService;
import com.pennanttech.pffws.RemarksSoapService;
import com.pennanttech.ws.service.APIErrorHandlerService;

@Service
public class RemarksWebServiceImpl implements RemarksSoapService, RemarksRestService {
	
	private final Logger logger = Logger.getLogger(getClass());
	
	private FinanceMainDAO financeMainDAO;
	private SecurityUserDAO securityUserDAO;
	private RemarksController remarksController;
	
	public RemarksWebServiceImpl() {
		super();
	}
	
	@Override
	public WSReturnStatus addRemarks(List<Notes> remarks) throws ServiceException {
		
		logger.debug(Literal.ENTERING);
		
		WSReturnStatus returnStatus=null;
		
		returnStatus = validateRemarks(remarks);
		if(returnStatus!=null){
			return returnStatus;
		} else {
			returnStatus = remarksController.doAddRemarks(remarks);
		}
		logger.debug(Literal.LEAVING);
		
		return returnStatus;
	}

	private WSReturnStatus validateRemarks(List<Notes> remarks) {
		
		logger.debug(Literal.ENTERING);
		
		WSReturnStatus returnStatus=null;
		
		if (remarks.isEmpty())	{
			returnStatus = new WSReturnStatus();
			returnStatus.setReturnCode("90502");
			returnStatus.setReturnText("Empty Notes List");			
		} else	{
			for (Notes notes : remarks) {
				if (StringUtils.isBlank(notes.getReference())) {
					String[] valueParm = new String[1];
					valueParm[0] = "Reference";
					
					return APIErrorHandlerService.getFailedStatus("90502", valueParm);
				}
				
				int count = financeMainDAO.getFinanceCountById(notes.getReference(), "_Temp", false);
				if (count <= 0) {
					String[] valueParm = new String[1];
					valueParm[0] = notes.getReference();
					return APIErrorHandlerService.getFailedStatus("90201", valueParm);
				}
				
				if (StringUtils.isBlank(notes.getRemarks())) {
					String[] valueParm = new String[1];
					valueParm[0] = "Remarks";
					return APIErrorHandlerService.getFailedStatus("90502", valueParm);
				}
				
				if(StringUtils.isNotBlank(notes.getUsrLogin()))	{	
					long userID = securityUserDAO.getUserByName(notes.getUsrLogin());
					if (userID <= 0) {
						String[] param = new String[2];
						param[0] = "User Name";
						param[1] = String.valueOf(notes.getUsrLogin());
						return	APIErrorHandlerService.getFailedStatus("90224", param);
					} else { 
						notes.setInputBy(userID);
					} 
				} 
				
				if (StringUtils.isNotEmpty(notes.getAlignType()) && StringUtils.isNotEmpty(notes.getRemarkType()))	{
					if (StringUtils.containsNone(notes.getAlignType(), "R") && StringUtils.containsNone(notes.getAlignType(), "F"))	{
						String[] param = new String[2];
						param[0] = "Align Type";
						param[1] = notes.getAlignType();
						return	APIErrorHandlerService.getFailedStatus("90224", param);
					}
					
					if (StringUtils.containsNone(notes.getRemarkType(), "N") && StringUtils.containsNone(notes.getRemarkType(), "I"))	{
						String[] param = new String[2];
						param[0] = "Remark Type";
						param[1] = notes.getRemarkType();
						return	APIErrorHandlerService.getFailedStatus("90224", param);
					}
				}
			}
		}
		logger.debug(Literal.LEAVING);
		
		return returnStatus;
	}

	@Autowired
	public void setRemarksController(RemarksController remarksController) {
		this.remarksController = remarksController;
	}

	@Autowired
	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	@Autowired
	public void setSecurityUserDAO(SecurityUserDAO securityUserDAO) {
		this.securityUserDAO = securityUserDAO;
	}

}
