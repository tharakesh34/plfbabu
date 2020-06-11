package com.pennanttech.controller;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.cxf.phase.PhaseInterceptorChain;
import org.apache.log4j.Logger;

import com.pennant.app.util.APIHeader;
import com.pennant.app.util.SessionUserDetails;
import com.pennant.backend.model.amtmasters.VehicleDealer;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.bmtmasters.BankBranch;
import com.pennant.backend.service.amtmasters.VehicleDealerService;
import com.pennant.backend.service.bmtmasters.BankBranchService;
import com.pennant.backend.util.PennantConstants;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.util.APIConstants;
import com.pennanttech.ws.service.APIErrorHandlerService;

public class DealerController {
	Logger								logger				= Logger.getLogger(DealerController.class);
	
	VehicleDealerService vehicleDealerService;
	BankBranchService  bankBranchService;
	
	/**
	 * Method for create Mandate in PLF system.
	 * 
	 * @param vehicleDealer
	 * @return Mandate
	 */
	public VehicleDealer createDealer(VehicleDealer vehicleDealer) {
		logger.debug("Entering");
		VehicleDealer response = null;
		try{
		// setting required values which are not received from API
		prepareRequiredData(vehicleDealer);
		vehicleDealer.setRecordType(PennantConstants.RECORD_TYPE_NEW);
		vehicleDealer.setNewRecord(true);
		vehicleDealer.setVersion(1);
		//get the header details from the request
		APIHeader reqHeaderDetails = (APIHeader) PhaseInterceptorChain.getCurrentMessage().getExchange().get(APIHeader.API_HEADER_KEY);
		AuditHeader auditHeader = getAuditHeader(vehicleDealer, PennantConstants.TRAN_WF);
		//set the headerDetails to AuditHeader
		auditHeader.setApiHeader(reqHeaderDetails);
		
		auditHeader = vehicleDealerService.doApprove(auditHeader);
		
		if (auditHeader.getErrorMessage() != null) {
			for (ErrorDetail errorDetail : auditHeader.getErrorMessage()) {
				response = new VehicleDealer();
				response.setReturnStatus(APIErrorHandlerService.getFailedStatus(errorDetail.getCode(),
						errorDetail.getError()));
			}
		} else {
			 vehicleDealer = (VehicleDealer) auditHeader.getAuditDetail().getModelData();
				response = new VehicleDealer();
				response.setDealerId(vehicleDealer.getDealerId());
				response.setActive(vehicleDealer.isActive());
				response.setBankBranchID(vehicleDealer.getBankBranchID());
			response.setReturnStatus(APIErrorHandlerService.getSuccessStatus());
		}
		}catch (Exception e) {
			logger.error("Exception", e);
			APIErrorHandlerService.logUnhandledException(e);
			response = new VehicleDealer();
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus());
		}
		logger.debug("Leaving");

		return response;
	}
	
	/**
	 * Setting default values from Mandate object
	 * 
	 * @param vehicleDealer
	 * 
	 */
	private void prepareRequiredData(VehicleDealer vehicleDealer) {
		logger.debug("Entering");
		BankBranch bankBranch = new BankBranch();
		if (vehicleDealer.getBankBranchID() > 0 ) {
			bankBranch = bankBranchService.getApprovedBankBranchById(vehicleDealer.getBankBranchID());
		} 
		LoggedInUser userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());
		vehicleDealer.setUserDetails(userDetails);
		vehicleDealer.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
		vehicleDealer.setBankBranchCode(bankBranch.getBranchCode());
		vehicleDealer.setBankBranchCodeName(bankBranch.getBranchDesc());
		vehicleDealer.setBankName(bankBranch.getBankName());
		vehicleDealer.setBankBranchID(bankBranch.getBankBranchID());
		vehicleDealer.setBranchIFSCCode(bankBranch.getIFSC());
		vehicleDealer.setBranchMICRCode(bankBranch.getMICR());
		vehicleDealer.setBranchCity(bankBranch.getCity());
		vehicleDealer.setLastMntBy(userDetails.getUserId());
        /*vehicleDealer.setDealerType("DSA");*/
		vehicleDealer.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		vehicleDealer.setSourceId(APIConstants.FINSOURCE_ID_API);
		logger.debug("Leaving");

	}
	

	/**
	 * Get Audit Header Details
	 * 
	 * @param vehicleDealer
	 * @param tranType
	 * @return AuditHeader
	 */
	private AuditHeader getAuditHeader(VehicleDealer vehicleDealer, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, vehicleDealer.getBefImage(), vehicleDealer);
		return new AuditHeader(String.valueOf(vehicleDealer.getDealerId()), String.valueOf(vehicleDealer.getDealerId()), null,
				null, auditDetail, vehicleDealer.getUserDetails(), new HashMap<String, ArrayList<ErrorDetail>>());
	}
	
	//Setter and Getter
	
	public VehicleDealerService getVehicleDealerService() {
		return vehicleDealerService;
	}

	public void setVehicleDealerService(VehicleDealerService vehicleDealerService) {
		this.vehicleDealerService = vehicleDealerService;
	}

	public BankBranchService getBankBranchService() {
		return bankBranchService;
	}

	public void setBankBranchService(BankBranchService bankBranchService) {
		this.bankBranchService = bankBranchService;
	}

}
