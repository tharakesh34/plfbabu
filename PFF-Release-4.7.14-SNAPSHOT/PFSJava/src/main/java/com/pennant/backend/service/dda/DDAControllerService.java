package com.pennant.backend.service.dda;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.zkoss.util.resource.Labels;

import com.pennant.Interface.service.DDAInterfaceService;
import com.pennant.Interface.service.EODFailPostingService;
import com.pennant.app.util.DateUtility;
import com.pennant.backend.dao.documentdetails.DocumentManagerDAO;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.customermasters.CustomerDocument;
import com.pennant.backend.model.customermasters.CustomerEMail;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.documentdetails.DocumentManager;
import com.pennant.backend.model.finance.DDAFTransactionLog;
import com.pennant.backend.model.finance.DDAProcessData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.constants.InterfaceConstants;
import com.pennanttech.pennapps.core.InterfaceException;

public class DDAControllerService {

	private static final Logger logger = Logger.getLogger(DDAControllerService.class);
	
	private DDAInterfaceService ddaInterfaceService;
	private DDAProcessService ddaProcessService;
	private EODFailPostingService	eodFailPostingService;
	private DocumentManagerDAO  documentManagerDAO;


	// DDA Payment Frequency Constants
	private String DAILY = "D"; 
	private String WEEKLY = "W"; 
	private String MONTHLY = "M"; 
	private String QUATERLY = "Q"; 
	private String HALY_YEARLY = "H"; 
	private String ANNUALLY = "A"; 
	private String ONETIME = "O"; 
	
	// Customer Type codes
	private String IN = "IN";
	private String NI = "NI";
	
	private String CAN_RES_CODE = "P01"; // Loan Settled
	private String CAPTURE_MODE = "I"; //Through Internet Banking Portal
	
	private String OIC_CODE_PBG = "812000146";
	private String OIC_CODE_WBG = "812000146";
	
	private String IDTYPE_IN = "EIDAC";
	private String IDTYPE_NI = "TRDLN";
	
	private String DDA_FORM_NAME = "DATA-CAPTUREDELECTRONICALLY";
	private String FORM_TYPE = "PDF";
	
	private boolean AHB_DPSP = false;

	/**
	 * This method performs the below actions<br>
	 *  1. Validate DDA Request and send Validate 
	 * 
	 * @param aFinanceDetail
	 * @param ahbDpEnable
	 * @throws InterfaceException
	 */
	public void doDDARequestProcess(FinanceDetail aFinanceDetail, boolean ahbDpSpEnable) throws InterfaceException {
		logger.debug("Entering");
		
		this.AHB_DPSP = ahbDpSpEnable;
		DDAProcessData ddaValidateRes = validateDDARequest(aFinanceDetail);
		
		if (ddaValidateRes != null) {
			ddaValidateRes.setSeqNo(Long.MIN_VALUE);
			ddaValidateRes.setPurpose(PennantConstants.REQ_TYPE_REG);
			List<DocumentDetails> list = aFinanceDetail.getDocumentDetailsList();
			byte[] ddaRegForm = null;
			
			for (DocumentDetails documentDetails : list) {
				if(StringUtils.equals(documentDetails.getDocCategory(), PennantConstants.DOCCTG_DDA_FORM)) {
					if(!StringUtils.equals(documentDetails.getDoctype(), FORM_TYPE)) {
						throw new InterfaceException("PTI9001", Labels.getLabel("DDA_DOCUMENT_PDF"));
					}
					
					ddaValidateRes.setDdaRegFormName(DDA_FORM_NAME);
					if(documentDetails.getDocImage()!=null){
						ddaRegForm = documentDetails.getDocImage();
					}else{
						DocumentManager doc = getDocumentManagerDAO().getById(documentDetails.getDocRefId());
						ddaRegForm = doc.getDocImage();
					}
					break;
				}
			}
			
			if(ddaRegForm == null) {
				throw new InterfaceException("PTI9001", Labels.getLabel("NO_DOCUMENT_FOUND"));
			}

			ddaValidateRes.setDdaRegFormData(PennantApplicationUtil.encode(ddaRegForm));
			DDAProcessData aDDARequest = new DDAProcessData();
			BeanUtils.copyProperties(ddaValidateRes, aDDARequest);
			
			// Send DDA Registration request
			DDAProcessData ddaRegResponse = getDdaInterfaceService().sendDDARegistrationReq(aDDARequest);

			//save the request and response data
			doSaveDDAProcess(ddaValidateRes, ddaRegResponse);
		}
	}

	/**
	 * validateDDARequest method done the following operations<br>
	 * 1. Prepare the DDARegistration request with purpose as 'VALIDATE'
	 * 
	 * 2. Validate the is request data changed? and send request to MQ<br> 
	 * 
	 * 3. Save the log details in Database<br>
	 * 
	 * @param aFinanceDetail
	 * @return DDAProcessData
	 * @throws InterfaceException
	 * @throws InterruptedException 
	 */
	public DDAProcessData validateDDARequest(FinanceDetail aFinanceDetail) throws InterfaceException {
		logger.debug("Entering");
		
		DDAProcessData ddaProcessRequest = prepareDDARegRequest(aFinanceDetail.getFinScheduleData().getFinanceMain(),
				aFinanceDetail.getCustomerDetails(), PennantConstants.REQ_TYPE_VALIDATE);
	
		DDAProcessData aDDAProcessRequest = new DDAProcessData();
		BeanUtils.copyProperties(ddaProcessRequest, aDDAProcessRequest);
	
		DDAProcessData ddaProcessResponse = null;
		
		//fetch DDA Initiate details from DDAReferenceLog with Active status
		DDAProcessData prvDDAProcessData = getDdaProcessService().getDDADetailsById(ddaProcessRequest.getFinRefence(), 
				PennantConstants.REQ_TYPE_VALIDATE);
		
		if(prvDDAProcessData == null) {
			ddaProcessResponse = getDdaInterfaceService().sendDDARegistrationReq(aDDAProcessRequest);
			
			if(!StringUtils.equals(ddaProcessResponse.getReturnCode(), PennantConstants.RES_TYPE_SUCCESS)) {
				throw new InterfaceException(ddaProcessResponse.getErrorCode(), ddaProcessResponse.getValidation());
			}
			//save the request and response data
			doSaveDDAProcess(ddaProcessRequest, ddaProcessResponse);
			
			return ddaProcessRequest;

		} else {
			// Validate DDA Registration sent or not
			DDAProcessData prvDDARegData = getDdaProcessService().getDDADetailsById(ddaProcessRequest.getFinRefence(),
							PennantConstants.REQ_TYPE_REG);
			if (prvDDARegData != null) {
				if (isDDADataChanged(ddaProcessRequest, prvDDARegData)) {
					
					if (!StringUtils.isBlank(prvDDARegData.getDdaReference())) {
						// Send DDA Cancellation request and then send DDA Validation request
						ddaProcessRequest = doDDAReRegistration(ddaProcessRequest);
					}
					return ddaProcessRequest;
					
				} else {
					return null;
				}
			} else {
				return ddaProcessRequest;
			}
		}
	}

	private DDAProcessData doDDAReRegistration(DDAProcessData ddaProcessRequest) throws InterfaceException {
		logger.debug("Entering");
		
		DDAProcessData ddaResponse = new DDAProcessData();
		
		// Send DDA Cancellation request to middleware
		cancelDDARegistration(ddaProcessRequest.getFinRefence());
		
		// Send DDA Validate request to middleware
		ddaResponse = getDdaInterfaceService().sendDDARegistrationReq(ddaProcessRequest);

		if(!StringUtils.equals(ddaResponse.getReturnCode(), PennantConstants.RES_TYPE_SUCCESS)) {
			throw new InterfaceException(ddaResponse.getErrorCode(), ddaResponse.getValidation());
		}
		
		//save the request and response data
		doSaveDDAProcess(ddaProcessRequest, ddaResponse);
		
		logger.debug("Leaving");
		return ddaProcessRequest;
	}

	/**
	 * Method for send Cancel DDA Registration Request to interface
	 * 
	 * @param financeMain
	 * @throws InterfaceException
	 */
	
	public void cancelDDARegistration(String finReference) throws InterfaceException  {
		logger.debug("Entering");
		DDAFTransactionLog ddaFTransactionLog;
		
		
		DDAProcessData ddaProceData = getDdaProcessService().getDDADetailsById(finReference, 
				PennantConstants.REQ_TYPE_REG);
		
		if(ddaProceData != null) {
			if(!StringUtils.isBlank(ddaProceData.getDdaReference())) {
				
				ddaProceData.setDdaCanResCode(CAN_RES_CODE);
				ddaProceData.setCaptureMode(CAPTURE_MODE);
				ddaProceData.setPurpose(PennantConstants.REQ_TYPE_CAN);
				DDAProcessData ddaCancelReply;
				try {
					ddaCancelReply = getDdaInterfaceService().cancelDDARegistration(ddaProceData);
					if(ddaCancelReply != null) {
						ddaCancelReply.setSeqNo(Long.MIN_VALUE);
						ddaCancelReply.setPurpose(PennantConstants.REQ_TYPE_CAN);
						
						if(StringUtils.equals(ddaCancelReply.getReturnCode(), InterfaceConstants.SUCCESS_CODE)) {
							// Inactive the previous DDA request based on finReference
							getDdaProcessService().updateActiveStatus(finReference);
							
							// Save the Cancellation Request and response
							ddaCancelReply.setActive(false);
							getDdaProcessService().save(ddaCancelReply);
						}
					}
				} catch (InterfaceException e) {
					
					//Saving DDA cancellation failed cases to process further
					ddaFTransactionLog=getEodFailPostingService().getDDAFTranDetailsById(finReference);
					
					//if record already exist update else save.
					if(ddaFTransactionLog!=null){
						ddaFTransactionLog.setNoofTries(ddaFTransactionLog.getNoofTries()+1);
						getEodFailPostingService().updateFailPostings(ddaFTransactionLog);
					}else{
						ddaFTransactionLog= new DDAFTransactionLog();
						ddaFTransactionLog.setFinRefence(finReference);
						ddaFTransactionLog.setValueDate(ddaProceData.getValueDate());
						ddaFTransactionLog.setErrorCode(e.getErrorCode());
						ddaFTransactionLog.setErrorDesc(e.getErrorMessage());
						ddaFTransactionLog.setNoofTries(1);
						getEodFailPostingService().saveFailPostings(ddaFTransactionLog);
					}
					throw e;		
				}
			}
				
			
		}
		logger.debug("Leaving");
	}
	
	/**
	 * Method for checking for DDA Request approved or not
	 * 
	 * @param finReference
	 * @return
	 * @throws InterfaceException
	 */
	public boolean validateDDAStatus(String finReference) throws InterfaceException {
		logger.debug("Entering");

		// Fetch DDA Registration details
		DDAProcessData ddaProcessData = getDdaProcessService().getDDADetailsById(finReference, PennantConstants.REQ_TYPE_REG);

		if(ddaProcessData != null) {
			if (!StringUtils.isBlank(ddaProcessData.getDdaReference()) && 
					StringUtils.isBlank(ddaProcessData.getDdaAckStatus())) {
				return true;
				
			} else if (!StringUtils.isBlank(ddaProcessData.getDdaAckStatus())) {
				String errMessage = Labels.getLabel("DDA_APPROVAL_REJECTED", new String[] {finReference});
				throw new InterfaceException("PTIDDS03", errMessage);
				
			} else {
				String errMessage = Labels.getLabel("DDA_APPROVAL_PENDING", new String[] {finReference});
				throw new InterfaceException(PennantConstants.DDA_PENDING_CODE, errMessage);
			}
		}
		return true;
	}
/*	
	*//**
	 * Method for checking for DDA Request approved or not
	 * 
	 * @param finReference
	 * @return
	 *//*
	public String checkDDAApproval(String finReference) {
		logger.debug("Entering");

		DDAProcessData ddaProcessData = getDdaProcessService().getDDADetailsById(finReference, PennantConstants.REQ_TYPE_REG);
		if(ddaProcessData != null) {
			if (!StringUtils.isBlank(ddaProcessData.getDdaReference()) && StringUtils.isBlank(ddaProcessData.getDdaAckStatus())) {
				return PennantConstants.DDA_ACK;
			} else if (!StringUtils.isBlank(ddaProcessData.getDdaAckStatus())) {
				return PennantConstants.DDA_NAK;
			} else {
				return PennantConstants.DDA_PENDING;
			}
		}
		return null;
	}*/
	
	/**
	 * Method for checking is DDA initiate request data changed?
	 * @param ddaProcessData 
	 * 
	 * @param ddaInitiateData
	 * @param ddaProcessRequest
	 * @return boolean
	 */
	public boolean isDDADataChanged(DDAProcessData ddaInitiateData, DDAProcessData ddaProcessData) {
		logger.debug("Entering");
		
		// Customer Type
		if(!StringUtils.equals(ddaProcessData.getCustomerType(), ddaInitiateData.getCustomerType())) {
			return true;
		}
		// Customer CIF
		if(!StringUtils.equals(ddaProcessData.getCustCIF(), ddaInitiateData.getCustCIF())) {
			return true;
		}
		// Customer Short Name
		if(!StringUtils.equals(ddaProcessData.getCustomerName(), ddaInitiateData.getCustomerName())) {
			return true;
		}
		// Mobile Number
		if(!StringUtils.equals(ddaProcessData.getMobileNum(), ddaInitiateData.getMobileNum())) {
			return true;
		}
		// Email
		if(!StringUtils.equals(ddaProcessData.getEmailID(), ddaInitiateData.getEmailID())) {
			return true;
		}
		// ID Type
		if(!StringUtils.equals(ddaProcessData.getIdType(), ddaInitiateData.getIdType())) {
			return true;
		}
		// ID Number
		if(!StringUtils.equals(ddaProcessData.getIdNum(), ddaInitiateData.getIdNum())) {
			return true;
		}
		// Bank Name
		if(!StringUtils.equals(ddaProcessData.getBankName(), ddaInitiateData.getBankName())) {
			return true;
		}
		// Account Type
		if(!StringUtils.equals(ddaProcessData.getAccountType(), ddaInitiateData.getAccountType())) {
			return true;
		}
		// IBAN
		if(!StringUtils.equals(ddaProcessData.getIban(), ddaInitiateData.getIban())) {
			return true;
		}
		//Finance Reference
		if(!StringUtils.equals(ddaProcessData.getFinRefence(), ddaInitiateData.getFinRefence())) {
			return true;
		}
		// CommenceOn
		if(DateUtility.compare(ddaProcessData.getCommenceOn(), ddaInitiateData.getCommenceOn()) != 0) {
			return true;
		}
		// Allowed Instances
		if(ddaProcessData.getAllowedInstances() != ddaInitiateData.getAllowedInstances()) {
			return true;
		}
		// Currency Code
		if(!StringUtils.equals(ddaProcessData.getCurrencyCode(), ddaInitiateData.getCurrencyCode())) {
			return true;
		}
		// Payment Frequency
		if(!StringUtils.equals(ddaProcessData.getPaymentFreq(), ddaInitiateData.getPaymentFreq())) {
			return true;
		}
		
		logger.debug("Leaving");
		
		return false;
	}

	/**
	 * Method to save the DDA Request and Response data
	 * 
	 * @param ddaProcessRequest
	 * @param ddaProcessResponse
	 */
	private void doSaveDDAProcess(DDAProcessData ddaProcessRequest, DDAProcessData ddaProcessResponse) {
		logger.debug("Entering");

		if(ddaProcessRequest == null || ddaProcessResponse == null) {
			return;
		}
		ddaProcessRequest.setReferenceNum(ddaProcessResponse.getReferenceNum());
		ddaProcessRequest.setValidation(StringUtils.substring(ddaProcessResponse.getValidation(), 0, 11));
		ddaProcessRequest.setReturnCode(ddaProcessResponse.getReturnCode());
		ddaProcessRequest.setReturnText(ddaProcessResponse.getReturnText());
		ddaProcessRequest.setError(ddaProcessResponse.getError());
		ddaProcessRequest.setErrorCode(ddaProcessResponse.getErrorCode());
		ddaProcessRequest.setErrorDesc(ddaProcessResponse.getErrorDesc());
		ddaProcessRequest.setActive(true);

		getDdaProcessService().save(ddaProcessRequest);

		logger.debug("Leaving");
	}
	
	/**
	 * Method for Prepare DDA Registration Data
	 * 
	 * @param detail
	 * @param purpose
	 * @return
	 * @throws InterfaceException 
	 */
	public DDAProcessData prepareDDARegRequest(FinanceDetail detail,String purpose) throws InterfaceException {
		FinanceMain aFinMain=detail.getFinScheduleData().getFinanceMain();
		CustomerDetails customerDetails=detail.getCustomerDetails();
		return prepareDDARegRequest(aFinMain,customerDetails,purpose);
	}

	/**
	 * Prepare the DDA Registration request data
	 * 
	 * @param aFinanceDetail
	 * @param purpose
	 * @return DDARegistration
	 * @throws InterfaceException 
	 */
	private DDAProcessData prepareDDARegRequest(FinanceMain aFinMain, CustomerDetails customerDetails, 
			String purpose) throws InterfaceException {
		logger.debug("Entering");
		
		if (aFinMain == null || customerDetails == null) {
			return null;
		}
		Customer customer = customerDetails.getCustomer();
		
		DDAProcessData ddaProcessData = new DDAProcessData();
		ddaProcessData.setPurpose(purpose);
		
		if(!StringUtils.isBlank(customer.getCustCtgCode())) {
			if(StringUtils.equals(customer.getCustCtgCode(), PennantConstants.PFF_CUSTCTG_INDIV)) {
				ddaProcessData.setCustomerType(IN);
			} else {
				ddaProcessData.setCustomerType(NI);
			}
		} else {
			ddaProcessData.setCustomerType("");
			throw new InterfaceException("PTIDDS01", Labels.getLabel("CUST_TYPE_MAND"));
		}
		
		ddaProcessData.setCustCIF(customer.getCustCIF());
		ddaProcessData.setCustomerName(customer.getCustShrtName());
		String[] mobileNum = PennantApplicationUtil.unFormatPhoneNumber(customer.getPhoneNumber());
		
		if(mobileNum != null && mobileNum.length >0 ) {
			mobileNum[0] = "+"+mobileNum[0];
			if(StringUtils.equals(mobileNum[0], "+971")) {
				ddaProcessData.setMobileNum(mobileNum[0]+mobileNum[1]+mobileNum[2]);
			} else {
				throw new InterfaceException("PTIDDS02", Labels.getLabel("DDA_MOB_VALIDATION"));
			}
		}
		
		for(CustomerEMail custEmail: customerDetails.getCustomerEMailList()) {
			if(!StringUtils.isBlank(custEmail.getCustEMailTypeCode())) {
				ddaProcessData.setEmailID(custEmail.getCustEMail());
				break;
			}
		}
		
		for(CustomerDocument custDocument:customerDetails.getCustomerDocumentsList()) {
			if(StringUtils.equals(customer.getCustCtgCode(), PennantConstants.PFF_CUSTCTG_INDIV)) {
				if(StringUtils.equals(custDocument.getCustDocCategory(), PennantConstants.CPRCODE)) {
					ddaProcessData.setIdType(IDTYPE_IN);
					ddaProcessData.setIdNum(custDocument.getCustDocTitle());
					break;
				}
			} else {
				if(StringUtils.equals(custDocument.getCustDocCategory(), PennantConstants.TRADELICENSE)) {
					ddaProcessData.setIdType(IDTYPE_NI);
					ddaProcessData.setIdNum(custDocument.getCustDocTitle());
					break;
				}
			}
		}
		
		ddaProcessData.setBankName(aFinMain.getBankName());
		ddaProcessData.setAccountType(aFinMain.getAccountType());
		ddaProcessData.setIban(aFinMain.getIban());
		ddaProcessData.setFinRefence(aFinMain.getFinReference());
		
		if(AHB_DPSP) {
			ddaProcessData.setFinRefence(aFinMain.getFinReference()+"_DP");
		}
		ddaProcessData.setCommenceOn(aFinMain.getNextRepayDate());
		ddaProcessData.setAllowedInstances(aFinMain.getNumberOfTerms() + aFinMain.getGraceTerms());
		ddaProcessData.setCurrencyCode(aFinMain.getFinCcy());
		
		if(aFinMain.getNumberOfTerms() == 1) {
			ddaProcessData.setPaymentFreq(ONETIME);
		} else {
			ddaProcessData.setPaymentFreq(setPaymentFrequency(aFinMain.getRepayFrq()));
		}
		
		FinanceType aFinaceType = getDdaProcessService().getFinTypeDetails(aFinMain.getFinType());
		if(aFinaceType != null) {
			if(StringUtils.equals(aFinaceType.getFinDivision(), FinanceConstants.FIN_DIVISION_RETAIL)) {
				ddaProcessData.setOic(OIC_CODE_PBG);//division
			} else if(StringUtils.equals(aFinaceType.getFinDivision(), FinanceConstants.FIN_DIVISION_CORPORATE)){
				ddaProcessData.setOic(OIC_CODE_WBG);//division
			}
			ddaProcessData.setDdaIssuedFor(setDDAIssuedFor(aFinaceType.getFinCategory()));//Product category
		} else {
			
		}
		
		ddaProcessData.setValueDate(DateUtility.getAppDate());
		ddaProcessData.setActive(true);
		
		logger.debug("Leaving");
		return ddaProcessData;
	}
	
	private String setDDAIssuedFor(String finCategory) {

		if(!StringUtils.isBlank(finCategory)) {
			switch (finCategory) {
			case FinanceConstants.PRODUCT_MURABAHA:
				return "050";
			case FinanceConstants.PRODUCT_MUSAWAMA:
				return "051";
			case FinanceConstants.PRODUCT_MUDARABA:
				return "052";
			case FinanceConstants.PRODUCT_MUSHARAKA:
				return "053";
			case FinanceConstants.PRODUCT_IJARAH:
				return "054";
			case FinanceConstants.PRODUCT_FWIJARAH:
				return "054";
			case FinanceConstants.PRODUCT_QARDHASSAN:
				return "056";
			default:
				return "057";
			}
		}
		return "";
	}

	private String setPaymentFrequency(String repayFrq) {
	   
		if(!StringUtils.isBlank(repayFrq)) {
			String frqType = (String) repayFrq.subSequence(0, 1);
			switch (frqType.toUpperCase()) {
			case "D":
				return DAILY;
			case "W":
				return WEEKLY;
			case "M":
				return MONTHLY;
			case "Q":
				return QUATERLY;
			case "H":
				return HALY_YEARLY;
			case "A":
				return ANNUALLY;
			default:
				break;
			}
		}
	    return null;
    }

	public DDAInterfaceService getDdaInterfaceService() {
		return ddaInterfaceService;
	}

	public void setDdaInterfaceService(DDAInterfaceService ddaInterfaceService) {
		this.ddaInterfaceService = ddaInterfaceService;
	}

	public DDAProcessService getDdaProcessService() {
		return ddaProcessService;
	}

	public void setDdaProcessService(DDAProcessService ddaProcessService) {
		this.ddaProcessService = ddaProcessService;
	}
	
	public EODFailPostingService getEodFailPostingService() {
		return eodFailPostingService;
	}

	public void setEodFailPostingService(EODFailPostingService eodFailPostingService) {
		this.eodFailPostingService = eodFailPostingService;
	}

	public DocumentManagerDAO getDocumentManagerDAO() {
		return documentManagerDAO;
	}

	public void setDocumentManagerDAO(DocumentManagerDAO documentManagerDAO) {
		this.documentManagerDAO = documentManagerDAO;
	}
}
