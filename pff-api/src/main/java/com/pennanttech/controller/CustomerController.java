package com.pennanttech.controller;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.cxf.phase.PhaseInterceptorChain;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;

import com.pennant.app.util.APIHeader;
import com.pennant.app.util.SessionUserDetails;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.LoggedInUser;
import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerAddres;
import com.pennant.backend.model.customermasters.CustomerBankInfo;
import com.pennant.backend.model.customermasters.CustomerChequeInfo;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.customermasters.CustomerDocument;
import com.pennant.backend.model.customermasters.CustomerEMail;
import com.pennant.backend.model.customermasters.CustomerEmploymentDetail;
import com.pennant.backend.model.customermasters.CustomerExtLiability;
import com.pennant.backend.model.customermasters.CustomerIncome;
import com.pennant.backend.model.customermasters.CustomerPhoneNumber;
import com.pennant.backend.service.customermasters.CustomerDetailsService;
import com.pennant.backend.service.customermasters.CustomerEmploymentDetailService;
import com.pennant.backend.service.customermasters.CustomerService;
import com.pennant.backend.service.errordetail.ErrorDetailService;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.exception.PFFInterfaceException;
import com.pennant.ws.exception.ServiceException;
import com.pennanttech.util.APIConstants;
import com.pennanttech.ws.model.customer.EmploymentDetail;
import com.pennanttech.ws.service.APIErrorHandlerService;

public class CustomerController {
	private final Logger logger = Logger.getLogger(CustomerController.class);

	HashMap<String, ArrayList<ErrorDetails>> overideMap;
	private CustomerService customerService;
	private CustomerDetailsService customerDetailsService;
	private ErrorDetailService errorDetailService;
	private CustomerEmploymentDetailService customerEmploymentDetailService;

	private final String PROCESS_TYPE_SAVE = "Save";
	private final String PROCESS_TYPE_UPDATE = "Update";

	/**
	 * 
	 * 
	 * @param customerDetails
	 * @return
	 * @throws PFFInterfaceException
	 */
	public CustomerDetails createCustomer(CustomerDetails customerDetails) throws ServiceException {
		logger.debug("Entering");

		// data preparation
		doSetRequiredDetails(customerDetails, PROCESS_TYPE_SAVE);

		CustomerDetails response = null;
		try {
			APIHeader reqHeaderDetails = (APIHeader) PhaseInterceptorChain.getCurrentMessage().getExchange().get(APIHeader.API_HEADER_KEY);
			AuditHeader auditHeader = getAuditHeader(customerDetails,PennantConstants.TRAN_WF);
			auditHeader.setApiHeader(reqHeaderDetails);
			auditHeader = customerDetailsService.doApprove(auditHeader);
			if (auditHeader.getErrorMessage() != null) {
				for (ErrorDetails errorDetail : auditHeader.getErrorMessage()) {
					response = new CustomerDetails();
					response.setReturnStatus(APIErrorHandlerService.getFailedStatus(errorDetail.getErrorCode(),
							errorDetail.getError()));
					return response;
				}
			}
		} catch (Exception e) {
			logger.error("Exception", e);
			response = new CustomerDetails();
			doEmptyResponseObject(response);
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus());
			return response;
		}

		// prepare create customer response object
		response = getCreateCustomerResponse(customerDetails.getCustomer().getCustCIF());

		logger.debug("Leaving");

		return response;
	}

	/**
	 * 
	 * @param customerDetails
	 * @return
	 * @throws PFFInterfaceException
	 */
	public WSReturnStatus updateCustomer(CustomerDetails customerDetails) throws ServiceException {
		logger.debug("Entering");
		try {
			
			doSetRequiredDetails(customerDetails, PROCESS_TYPE_UPDATE);
			
			APIHeader reqHeaderDetails = (APIHeader) PhaseInterceptorChain.getCurrentMessage().getExchange().get(APIHeader.API_HEADER_KEY);
			AuditHeader auditHeader = getAuditHeader(customerDetails,PennantConstants.TRAN_WF);
			auditHeader.setApiHeader(reqHeaderDetails);
			auditHeader = customerDetailsService.doApprove(auditHeader);

			if (auditHeader.getErrorMessage() != null) {
				for (ErrorDetails errorDetail : auditHeader.getErrorMessage()) {
					return APIErrorHandlerService.getFailedStatus(errorDetail.getErrorCode(), errorDetail.getError());
				}
			}
		} catch (Exception e) {
			logger.error("Exception:" + e);
			return APIErrorHandlerService.getFailedStatus();
		}
		logger.debug("Leaving");

		return APIErrorHandlerService.getSuccessStatus();
	}

	/**
	 * 
	 * 
	 * @param custCIF
	 * @return
	 */
	private CustomerDetails getCreateCustomerResponse(String custCIF) {
		logger.debug("Entering");

		CustomerDetails customerDetails = new CustomerDetails();
		Customer customer = customerDetailsService.getCustomerByCIF(custCIF);

		if (customer != null) {
			customerDetails.setCustCIF(custCIF);
			doEmptyResponseObject(customerDetails);
			customerDetails.setReturnStatus(APIErrorHandlerService.getSuccessStatus());
		} else {
			doEmptyResponseObject(customerDetails);
			customerDetails.setReturnStatus(APIErrorHandlerService.getFailedStatus("90104"));
		}

		logger.debug("Leaving");
		return customerDetails;
	}

	private void doEmptyResponseObject(CustomerDetails customerDetails) {
		customerDetails.setCustomer(null);
	}

	/**
	 * prepare customer detail object with required data to process customer creation.<br>
	 * 
	 * @param customerDetails
	 */
	private void doSetRequiredDetails(CustomerDetails customerDetails, String processType) {
		logger.debug("Entering");

		Customer curCustomer = customerDetails.getCustomer();
		CustomerDetails prvCustomerDetails = null;
		
		if (StringUtils.equals(processType, PROCESS_TYPE_UPDATE)) {
			// fetch customer object
			Customer customer = customerDetailsService.getCustomerByCIF(customerDetails.getCustCIF());
			// fetch customer details
			prvCustomerDetails = customerDetailsService.getCustomerDetailsById(customer.getCustID(), true, "");
		}
		
		// user language
		LoggedInUser userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());

		customerDetails.setUserDetails(userDetails);
		customerDetails.setSourceId(APIConstants.FINSOURCE_ID_API);
		curCustomer.setUserDetails(userDetails);

		// set values from customerDetails to customer
		if (StringUtils.isNotBlank(customerDetails.getCustCtgCode())) {
			curCustomer.setCustCtgCode(customerDetails.getCustCtgCode());
		}
		if (StringUtils.isNotBlank(customerDetails.getCustCtgCode())) {
			curCustomer.setCustDftBranch(customerDetails.getCustDftBranch());
		}
		if (StringUtils.isNotBlank(customerDetails.getCustCtgCode())) {
			curCustomer.setCustCoreBank(customerDetails.getCustCoreBank());
		}
		if (StringUtils.isNotBlank(customerDetails.getCustCtgCode())) {
			curCustomer.setCustBaseCcy(customerDetails.getCustBaseCcy());
		}
		if (StringUtils.isNotBlank(customerDetails.getCustCtgCode())) {
			curCustomer.setCustRO1(customerDetails.getPrimaryRelationOfficer());
		}
		if(StringUtils.isBlank(customerDetails.getCustomer().getCustLng())){
			curCustomer.setCustLng(PennantConstants.default_Language);
		}
		
		if (StringUtils.equals(processType, PROCESS_TYPE_SAVE)) {
			// generate new customer CIF
			String custCIF = customerDetailsService.getNewProspectCustomerCIF();
			curCustomer.setNewRecord(true);
			curCustomer.setRecordType(PennantConstants.RECORD_TYPE_NEW);
			curCustomer.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			curCustomer.setCustCIF(custCIF);
			curCustomer.setLastMntOn(new Timestamp(System.currentTimeMillis()));
			curCustomer.setLastMntBy(userDetails.getLoginUsrID());
		} else {
			Customer prvCustomer = prvCustomerDetails.getCustomer();
			customerDetails.setCustID(prvCustomer.getCustID());
			curCustomer.setCustCIF(customerDetails.getCustCIF());
			curCustomer.setCustID(prvCustomer.getCustID());
			curCustomer.setNewRecord(false);
			curCustomer.setRecordType(PennantConstants.RECORD_TYPE_UPD);
			curCustomer.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			curCustomer.setVersion((prvCustomer.getVersion()) + 1);
			curCustomer.setLastMntBy(userDetails.getLoginUsrID());
			curCustomer.setLastMntOn(new Timestamp(System.currentTimeMillis()));
			// copy properties
			BeanUtils.copyProperties(curCustomer, prvCustomer);
		}

		// customer employment details
		List<CustomerEmploymentDetail> curEmpDetails = customerDetails.getEmploymentDetailsList();
		if (curEmpDetails != null) {
			for (CustomerEmploymentDetail curEmpDetail : curEmpDetails) {
				if (StringUtils.equals(processType, PROCESS_TYPE_SAVE)) {
					curEmpDetail.setNewRecord(true);
					curEmpDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
					curEmpDetail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
				} else {
					List<CustomerEmploymentDetail> prvEmpDetails = prvCustomerDetails.getEmploymentDetailsList();
					if (prvEmpDetails != null) {
						for (CustomerEmploymentDetail prvEmpDetail : prvEmpDetails) {
							if (curEmpDetail.getCustEmpId() == prvEmpDetail.getCustEmpId()) {
								curEmpDetail.setNewRecord(false);
								curEmpDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
								curEmpDetail.setVersion(prvEmpDetail.getVersion() + 1);
								curEmpDetail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
								// copy properties
								BeanUtils.copyProperties(curEmpDetail, prvEmpDetails);
							}
						}
					}
				}
			}
		}

		// customer Address details
		List<CustomerAddres> curAddressList = customerDetails.getAddressList();
		if (curAddressList != null) {
			for (CustomerAddres curAddres : curAddressList) {
				if (StringUtils.equals(processType, PROCESS_TYPE_SAVE)) {
					curAddres.setNewRecord(true);
					curAddres.setRecordType(PennantConstants.RECORD_TYPE_NEW);
					curAddres.setLastMntOn(new Timestamp(System.currentTimeMillis()));
				} else {
					List<CustomerAddres> prvAddressList = prvCustomerDetails.getAddressList();
					if (curAddressList != null && prvAddressList != null) {
						for (CustomerAddres prvAddres : prvAddressList) {
							if (StringUtils.equals(curAddres.getCustAddrType(), prvAddres.getCustAddrType())) {
								curAddres.setNewRecord(false);
								curAddres.setRecordType(PennantConstants.RECORD_TYPE_UPD);
								curAddres.setVersion(prvAddres.getVersion() + 1);
								curAddres.setLastMntOn(new Timestamp(System.currentTimeMillis()));
								// copy properties
								BeanUtils.copyProperties(curAddres, prvAddres);
							}
						}
					}
				}
			}
		}

		// customer Phone details
		List<CustomerPhoneNumber> customerPhoneNumList = customerDetails.getCustomerPhoneNumList();
		if (customerPhoneNumList != null) {
			for (CustomerPhoneNumber curCustPhoneNum : customerPhoneNumList) {
				if (StringUtils.equals(processType, PROCESS_TYPE_SAVE)) {
					curCustPhoneNum.setNewRecord(true);
					curCustPhoneNum.setRecordType(PennantConstants.RECORD_TYPE_NEW);
					curCustPhoneNum.setLastMntOn(new Timestamp(System.currentTimeMillis()));
				} else {
					List<CustomerPhoneNumber> prvCustomerPhoneNumberList = prvCustomerDetails.getCustomerPhoneNumList();
					if (prvCustomerPhoneNumberList != null) {
						for (CustomerPhoneNumber prvCustomerPhoneNum : prvCustomerPhoneNumberList) {
							if (StringUtils.equals(curCustPhoneNum.getPhoneTypeCode(),prvCustomerPhoneNum.getPhoneTypeCode())) {
								curCustPhoneNum.setNewRecord(false);
								curCustPhoneNum.setRecordType(PennantConstants.RECORD_TYPE_UPD);
								curCustPhoneNum.setVersion(prvCustomerPhoneNum.getVersion() + 1);
								curCustPhoneNum.setLastMntOn(new Timestamp(System.currentTimeMillis()));
								// copy properties
								BeanUtils.copyProperties(curCustPhoneNum, prvCustomerPhoneNum);
							}
						}
					}
				}
			}
		}

		// customer Email details
		List<CustomerEMail> customerEMailList = customerDetails.getCustomerEMailList();
		if (customerEMailList != null) {
			for (CustomerEMail curCustEmail : customerEMailList) {
				if (StringUtils.equals(processType, PROCESS_TYPE_SAVE)) {
					curCustEmail.setNewRecord(true);
					curCustEmail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
					curCustEmail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
				} else {
					List<CustomerEMail> prvCustomerEMailList = prvCustomerDetails.getCustomerEMailList();
					if (prvCustomerEMailList != null) {
						for (CustomerEMail prvCustomerEMail : prvCustomerEMailList) {
							if (StringUtils.equals(curCustEmail.getCustEMailTypeCode(),
									prvCustomerEMail.getCustEMailTypeCode())) {
								curCustEmail.setNewRecord(false);
								curCustEmail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
								curCustEmail.setVersion(prvCustomerEMail.getVersion() + 1);
								curCustEmail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
								// copy properties
								BeanUtils.copyProperties(curCustEmail, prvCustomerEMail);
							}
						}
					}
				}
			}
		}

		// customer income details
		List<CustomerIncome> customerIncomes = customerDetails.getCustomerIncomeList();
		if (customerIncomes != null) {
			for (CustomerIncome curCustomerIncome : customerIncomes) {
				if (StringUtils.equals(processType, PROCESS_TYPE_SAVE)) {
					curCustomerIncome.setNewRecord(true);
					curCustomerIncome.setRecordType(PennantConstants.RECORD_TYPE_NEW);
					curCustomerIncome.setMargin(BigDecimal.ZERO);
					curCustomerIncome.setLastMntOn(new Timestamp(System.currentTimeMillis()));

				} else {
					List<CustomerIncome> prvCustomerIncomeList = prvCustomerDetails.getCustomerIncomeList();
					if (prvCustomerIncomeList != null) {
						for (CustomerIncome prvCustomerIncome : prvCustomerIncomeList) {
							if (StringUtils.equals(curCustomerIncome.getIncomeExpense(),
									prvCustomerIncome.getIncomeExpense())) {
								curCustomerIncome.setNewRecord(false);
								curCustomerIncome.setRecordType(PennantConstants.RECORD_TYPE_UPD);
								curCustomerIncome.setVersion(prvCustomerIncome.getVersion() + 1);
								curCustomerIncome.setLastMntOn(new Timestamp(System.currentTimeMillis()));
								// copy properties
								BeanUtils.copyProperties(curCustomerIncome, prvCustomerIncome);
							}
						}
					}
				}
			}
		}

		// customer document details
		List<CustomerDocument> customerDocumentsList = customerDetails.getCustomerDocumentsList();
		if (customerDocumentsList != null) {
			for (CustomerDocument curCustDocument : customerDocumentsList) {
				if (StringUtils.equals(processType, PROCESS_TYPE_SAVE)) {
					curCustDocument.setNewRecord(true);
					curCustDocument.setRecordType(PennantConstants.RECORD_TYPE_NEW);
					curCustDocument.setCustDocImage(PennantApplicationUtil.decode(curCustDocument.getCustDocImage()));
					curCustDocument.setLastMntOn(new Timestamp(System.currentTimeMillis()));
					if(StringUtils.equals(curCustDocument.getCustDocCategory(), "03") && 
							StringUtils.equals(customerDetails.getCustomer().getCustCtgCode(),PennantConstants.PFF_CUSTCTG_INDIV)) {
						customerDetails.getCustomer().setCustCRCPR(curCustDocument.getCustDocTitle());
					}
					if(StringUtils.equals(curCustDocument.getCustDocCategory(), "15") && 
							StringUtils.equals(customerDetails.getCustomer().getCustCtgCode(),PennantConstants.PFF_CUSTCTG_CORP)){
						customerDetails.getCustomer().setCustCRCPR(curCustDocument.getCustDocTitle());
					}
				} else {
					List<CustomerDocument> prvCustomerDocumentsList = prvCustomerDetails.getCustomerDocumentsList();
					if (prvCustomerDocumentsList != null) {
						for (CustomerDocument prvCustomerDocuments : prvCustomerDocumentsList) {
							if (StringUtils.equals(curCustDocument.getCustDocCategory(),
									prvCustomerDocuments.getCustDocCategory())) {
								curCustDocument.setNewRecord(false);
								curCustDocument.setRecordType(PennantConstants.RECORD_TYPE_UPD);
								curCustDocument.setVersion(prvCustomerDocuments.getVersion() + 1);
								curCustDocument.setLastMntOn(new Timestamp(System.currentTimeMillis()));
								// copy properties
								BeanUtils.copyProperties(curCustDocument, prvCustomerDocuments);

							}
						}
					}
				}
			}
		}

		// customer Banking information
		List<CustomerBankInfo> customerBankInfoList = customerDetails.getCustomerBankInfoList();
		if (customerBankInfoList != null) {
			for (CustomerBankInfo curCustBankInfo : customerBankInfoList) {
				if (StringUtils.equals(processType, PROCESS_TYPE_SAVE)) {
					curCustBankInfo.setNewRecord(true);
					curCustBankInfo.setRecordType(PennantConstants.RECORD_TYPE_NEW);
					curCustBankInfo.setLastMntOn(new Timestamp(System.currentTimeMillis()));
				} else {
					List<CustomerBankInfo> prvCustomerBankInfoList = prvCustomerDetails.getCustomerBankInfoList();
					if (prvCustomerBankInfoList != null) {
						for (CustomerBankInfo prvCustomerBankInfo : prvCustomerBankInfoList) {
							if (curCustBankInfo.getBankId() == prvCustomerBankInfo.getBankId()) {
								curCustBankInfo.setNewRecord(false);
								curCustBankInfo.setRecordType(PennantConstants.RECORD_TYPE_UPD);
								curCustBankInfo.setVersion(prvCustomerBankInfo.getVersion() + 1);
								curCustBankInfo.setLastMntOn(new Timestamp(System.currentTimeMillis()));
								// copy properties
								BeanUtils.copyProperties(curCustBankInfo, prvCustomerBankInfo);
							}
						}
					}
				}
			}
		}

		// customer Account behavior
		List<CustomerChequeInfo> customerChequeInfoList = customerDetails.getCustomerChequeInfoList();
		if (customerChequeInfoList != null) {
			for (CustomerChequeInfo curCustChequeInfo : customerChequeInfoList) {
				if (StringUtils.equals(processType, PROCESS_TYPE_SAVE)) {
					curCustChequeInfo.setNewRecord(true);
					curCustChequeInfo.setRecordType(PennantConstants.RECORD_TYPE_NEW);
					curCustChequeInfo.setLastMntOn(new Timestamp(System.currentTimeMillis()));
				} else {
					List<CustomerChequeInfo> prvCustomerChequeInfoList = prvCustomerDetails.getCustomerChequeInfoList();
					if (prvCustomerChequeInfoList != null) {
						for (CustomerChequeInfo prvCustomerChequeInfo : prvCustomerChequeInfoList) {
							if (curCustChequeInfo.getChequeSeq() == prvCustomerChequeInfo.getChequeSeq()) {
								curCustChequeInfo.setNewRecord(false);
								curCustChequeInfo.setRecordType(PennantConstants.RECORD_TYPE_UPD);
								curCustChequeInfo.setVersion(prvCustomerChequeInfo.getVersion() + 1);
								curCustChequeInfo.setLastMntOn(new Timestamp(System.currentTimeMillis()));
								// copy properties
								BeanUtils.copyProperties(curCustChequeInfo, prvCustomerChequeInfo);
							}
						}
					}
				}
			}
		}
		
		// customerExtLiability
		List<CustomerExtLiability> customerExtLiabilityList = customerDetails.getCustomerExtLiabilityList();
		if (customerExtLiabilityList != null) {
			for (CustomerExtLiability curCustomerExtLiability : customerExtLiabilityList) {
				if (StringUtils.equals(processType, PROCESS_TYPE_SAVE)) {
					curCustomerExtLiability.setNewRecord(true);
					curCustomerExtLiability.setRecordType(PennantConstants.RECORD_TYPE_NEW);
					curCustomerExtLiability.setLastMntOn(new Timestamp(System.currentTimeMillis()));
				} else {
					List<CustomerExtLiability> prvCustomerExtLiabilityList = prvCustomerDetails
							.getCustomerExtLiabilityList();
					if (prvCustomerExtLiabilityList != null) {
						for (CustomerExtLiability prvCustomerExtLiability : prvCustomerExtLiabilityList) {
							if (curCustomerExtLiability.getLiabilitySeq() == prvCustomerExtLiability.getLiabilitySeq()) {
								curCustomerExtLiability.setNewRecord(false);
								curCustomerExtLiability.setRecordType(PennantConstants.RECORD_TYPE_UPD);
								curCustomerExtLiability.setVersion(prvCustomerExtLiability.getVersion() + 1);
								curCustomerExtLiability.setLastMntOn(new Timestamp(System.currentTimeMillis()));
								// copy properties
								BeanUtils.copyProperties(curCustomerExtLiability, prvCustomerExtLiability);
							}
						}
					}
				}
			}
		}
		
		logger.debug("Leaving");
	}


	/**
	 * Get Audit Header Details
	 * 
	 * @param aCustomerDetails
	 * @param tranType
	 * @return AuditHeader
	 */
	private AuditHeader getAuditHeader(CustomerDetails aCustomerDetails, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aCustomerDetails.getBefImage(), aCustomerDetails);
		return new AuditHeader(String.valueOf(aCustomerDetails.getCustID()), String.valueOf(aCustomerDetails
				.getCustID()), null, null, auditDetail, aCustomerDetails.getUserDetails(),
				new HashMap<String, ArrayList<ErrorDetails>>());
	}

	/**
	 * Get Audit Header Details
	 * 
	 * @param aCustomerDetails
	 * @param tranType
	 * @return AuditHeader
	 */
	private AuditHeader getAuditHeader(CustomerEmploymentDetail aCustomerDetails, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aCustomerDetails.getBefImage(), aCustomerDetails);
		return new AuditHeader(String.valueOf(aCustomerDetails.getCustID()), String.valueOf(aCustomerDetails
				.getCustID()), null, null, auditDetail, aCustomerDetails.getUserDetails(),
				new HashMap<String, ArrayList<ErrorDetails>>());
	}

	/**
	 * get the Customer Details By the Customer Id
	 * 
	 * @param customerId
	 * @return
	 */
	public CustomerDetails getCustomerDetails(long customerId) {
		logger.debug("Entering");

		CustomerDetails response = new CustomerDetails();

		try {
			response = customerDetailsService.getApprovedCustomerById(customerId);

			if (response != null) {
				response.setCustCIF(response.getCustomer().getCustCIF());
				response.setCustCoreBank(response.getCustomer().getCustCoreBank());
				response.setCustCtgCode(response.getCustomer().getCustCtgCode());
				response.setCustDftBranch(response.getCustomer().getCustDftBranch());
				response.setCustBaseCcy(response.getCustomer().getCustBaseCcy());
				response.setPrimaryRelationOfficer(response.getCustomer().getCustRO1());
				response.setReturnStatus(APIErrorHandlerService.getSuccessStatus());
			} else {
				response = new CustomerDetails();
				response.setReturnStatus(APIErrorHandlerService.getFailedStatus());
			}
		} catch (Exception e) {
			logger.error("Exception", e);
			response = new CustomerDetails();
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus());
		}

		logger.debug("Leaving");
		return response;
	}

	/**
	 * delete the Customer by given CustomerId
	 * 
	 * @param customerId
	 * @return
	 */
	public WSReturnStatus deleteCustomerById(long customerId) {
		logger.debug("Entering");

		WSReturnStatus response = null;
		CustomerDetails customerDetails = customerDetailsService.getCustomerDetailsById(customerId, true, "");
		if (customerDetails != null) {
			// user language
			LoggedInUser userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());

			customerDetails.setUserDetails(userDetails);
			Customer customer = customerDetails.getCustomer();
			customer.setUserDetails(userDetails);
			customer.setRecordType(PennantConstants.RECORD_TYPE_DEL);

			doSetCustomerDeleteData(customerDetails);
			APIHeader reqHeaderDetails = (APIHeader) PhaseInterceptorChain.getCurrentMessage().getExchange().get(APIHeader.API_HEADER_KEY);
			AuditHeader auditHeader = getAuditHeader(customerDetails, PennantConstants.TRAN_WF);
			auditHeader.setApiHeader(reqHeaderDetails);
			try {
				// do customer delete
				auditHeader = customerDetailsService.delete(auditHeader);
			} catch (DataAccessException dae) {
				response = APIErrorHandlerService.getFailedStatus("90999", dae.getMessage());
				return response;
			} catch (Exception e) {
				logger.error("Exception", e);
				response = APIErrorHandlerService.getFailedStatus();
				return response;
			}

			if (auditHeader.getErrorMessage() != null) {
				for (ErrorDetails errorDetail : auditHeader.getErrorMessage()) {
					response = APIErrorHandlerService.getFailedStatus(errorDetail.getErrorCode(),
							errorDetail.getError());
				}
			} else {
				response = APIErrorHandlerService.getSuccessStatus();
			}
		}

		logger.debug("Leaving");

		return response;
	}

	//TODO: DDP- Need to change the below method
	/**
	 * set Record type while delete customer
	 * 
	 * @param customerDetails
	 */
	private void doSetCustomerDeleteData(CustomerDetails customerDetails) {

		// customer employment details
		List<CustomerEmploymentDetail> employmentDetails = customerDetails.getEmploymentDetailsList();
		if (employmentDetails != null) {
			for (CustomerEmploymentDetail employmentDetail : employmentDetails) {
				employmentDetail.setNewRecord(false);
				employmentDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}
		}

		// customer Address details
		List<CustomerAddres> addressList = customerDetails.getAddressList();
		if (addressList != null) {
			for (CustomerAddres custAddres : addressList) {
				custAddres.setNewRecord(false);
				custAddres.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}
		}

		// customer Phone details
		List<CustomerPhoneNumber> customerPhoneNumList = customerDetails.getCustomerPhoneNumList();
		if (customerPhoneNumList != null) {
			for (CustomerPhoneNumber custPhoneNum : customerPhoneNumList) {
				custPhoneNum.setNewRecord(false);
				custPhoneNum.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}
		}

		// customer Email details
		List<CustomerEMail> customerEMailList = customerDetails.getCustomerEMailList();
		if (customerEMailList != null) {
			for (CustomerEMail custEmail : customerEMailList) {
				custEmail.setNewRecord(false);
				custEmail.setRecordType(PennantConstants.RECORD_TYPE_DEL);

			}
		}

		// customer income details
		List<CustomerIncome> customerIncomes = customerDetails.getCustomerIncomeList();
		if (customerIncomes != null) {
			for (CustomerIncome customerIncome : customerIncomes) {
				customerIncome.setNewRecord(false);
				customerIncome.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}
		}

		// customer document details
		List<CustomerDocument> customerDocumentsList = customerDetails.getCustomerDocumentsList();
		if (customerDocumentsList != null) {
			for (CustomerDocument custDocument : customerDocumentsList) {
				custDocument.setNewRecord(false);
				custDocument.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}
		}

		// customer Banking information
		List<CustomerBankInfo> customerBankInfoList = customerDetails.getCustomerBankInfoList();
		if (customerBankInfoList != null) {
			for (CustomerBankInfo custBankInfo : customerBankInfoList) {
				custBankInfo.setNewRecord(false);
				custBankInfo.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}
		}

		// customer Account behavior
		List<CustomerChequeInfo> customerChequeInfoList = customerDetails.getCustomerChequeInfoList();
		if (customerChequeInfoList != null) {
			for (CustomerChequeInfo custChequeInfo : customerChequeInfoList) {
				custChequeInfo.setNewRecord(false);
				custChequeInfo.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}
		}
		// customerExtLiability
		List<CustomerExtLiability> customerExtLiabilityList = customerDetails.getCustomerExtLiabilityList();
		if (customerExtLiabilityList != null) {
			for (CustomerExtLiability customerExtLiability : customerExtLiabilityList) {
				customerExtLiability.setNewRecord(false);
				customerExtLiability.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				customerExtLiability.setLastMntOn(new Timestamp(System.currentTimeMillis()));
			}
		}
	}

	/**
	 * get the Customer Personal Information By the Customer Id
	 * 
	 * @param customerId
	 * @return
	 */
	public CustomerDetails getCustomerPersonalInfo(long customerId) {
		logger.debug("Entering");
		CustomerDetails response = null ;
		try {
			Customer customer = getCustomerService().getApprovedCustomerById(customerId);
			if (customer != null) {
				response = new CustomerDetails();
				response.setCustCIF(customer.getCustCIF());
				response.setCustCoreBank(customer.getCustCoreBank());
				response.setCustCtgCode(customer.getCustCtgCode());
				response.setCustDftBranch(customer.getCustDftBranch());
				response.setCustBaseCcy(customer.getCustBaseCcy());
				response.setPrimaryRelationOfficer(customer.getCustRO1());
				response.setCustomer(customer);
				response.setReturnStatus(APIErrorHandlerService.getSuccessStatus());
			} else {
				response = new CustomerDetails();
				response.setReturnStatus(APIErrorHandlerService.getFailedStatus());
			}
		} catch (Exception e) {
			logger.error("Exception", e);
			response = new CustomerDetails();
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus());
		}

		logger.debug("Leaving");
		return response;

	}

	/**
	 * 
	 * @param customerDetails
	 * @return WSReturnStatus
	 */
	public WSReturnStatus updateCustomerPersionalInfo(CustomerDetails customerDetails) {
		logger.debug("Entering");
		
		// user details from session
		LoggedInUser userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());
		
		Customer prvCustomer = customerDetailsService.getCustomerByCIF(customerDetails.getCustCIF());
		Customer curCustomer = customerDetails.getCustomer();
		curCustomer.setCustCIF(customerDetails.getCustCIF());
		curCustomer.setCustCoreBank(customerDetails.getCustCoreBank());
		curCustomer.setCustCtgCode(customerDetails.getCustCtgCode());
		curCustomer.setCustDftBranch(customerDetails.getCustDftBranch());
		curCustomer.setCustBaseCcy(customerDetails.getCustBaseCcy());
		curCustomer.setCustRO1(customerDetails.getPrimaryRelationOfficer());
		curCustomer.setCustID(prvCustomer.getCustID());
		curCustomer.setRecordType(PennantConstants.RECORD_TYPE_UPD);
		curCustomer.setNewRecord(false);
		curCustomer.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
		curCustomer.setLastMntBy(userDetails.getLoginUsrID());
		curCustomer.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		curCustomer.setVersion(prvCustomer.getVersion()+1);
		APIHeader reqHeaderDetails = (APIHeader) PhaseInterceptorChain.getCurrentMessage().getExchange().get(APIHeader.API_HEADER_KEY);
		AuditHeader auditHeader = getAuditHeader(curCustomer, PennantConstants.TRAN_WF);
		auditHeader.setApiHeader(reqHeaderDetails);
		auditHeader = customerService.saveOrUpdate(auditHeader);
		WSReturnStatus response = new WSReturnStatus();
		if (auditHeader.getErrorMessage() != null) {
			for (ErrorDetails errorDetail : auditHeader.getErrorMessage()) {
				response = (APIErrorHandlerService.getFailedStatus(errorDetail.getErrorCode(), errorDetail.getError()));
			}
		} else {
			response = APIErrorHandlerService.getSuccessStatus();
		}

		logger.debug("Leaving");
		return response;

	}

	/**
	 * get the Customer Employment By the Customer Id
	 * 
	 * @param customerId
	 * @return
	 */
	public CustomerDetails getCustomerEmployment(String cif) {
		logger.debug("Entering");

		CustomerDetails response = null;
		Customer customer = customerDetailsService.getCustomerByCIF(cif);
		try {
			List<CustomerEmploymentDetail> customerEmploymentDetailList = getCustomerEmploymentDetailService()
					.getApprovedCustomerEmploymentDetailById(customer.getCustID());
			if (customerEmploymentDetailList != null && !customerEmploymentDetailList.isEmpty()) {
				response = new CustomerDetails();
				response.setCustCIF(cif);
				for (CustomerEmploymentDetail detail : customerEmploymentDetailList) {
					detail.setLovDescCustCIF(null);
				}
				response.setEmploymentDetailsList(customerEmploymentDetailList);
				response.setCustomer(null);
				response.setReturnStatus(APIErrorHandlerService.getSuccessStatus());
			} else {
				response = new CustomerDetails();
				response.setCustomer(null);
				String[] valueParm = new String[1];
				valueParm[0] = cif;
				response.setReturnStatus(APIErrorHandlerService.getFailedStatus("90304", valueParm));
			}
		} catch (Exception e) {
			logger.error("Exception", e);
			CustomerDetails customerEmploymentsDetail = new CustomerDetails();
			customerEmploymentsDetail.setCustomer(null);
			customerEmploymentsDetail.setReturnStatus(APIErrorHandlerService.getFailedStatus());
		}

		logger.debug("Leaving");
		return response;

	}
	
	/**
	 * Method for create CustomerEmployment in PLF system.
	 * 
	 * @param customerEmploymentDetail
	 * 
	 */
	public EmploymentDetail addCustomerEmployment(CustomerEmploymentDetail customerEmploymentDetail,String cif) {
		
		EmploymentDetail response = null;
		logger.debug("Entering");
		try{
		LoggedInUser userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());
		Customer customer = customerDetailsService.getCustomerByCIF(cif);
		customerEmploymentDetail.setCustID(customer.getCustID());
		customerEmploymentDetail.setLovDescCustCIF(cif);
		customerEmploymentDetail.setLovDesccustEmpName(String.valueOf(customerEmploymentDetail.getCustEmpName()));
		customerEmploymentDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
		customerEmploymentDetail.setSourceId(APIConstants.FINSOURCE_ID_API);
		customerEmploymentDetail.setNewRecord(true);
		customerEmploymentDetail.setVersion(1);
		customerEmploymentDetail.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
		customerEmploymentDetail.setLastMntBy(userDetails.getLoginUsrID());
		customerEmploymentDetail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		APIHeader reqHeaderDetails = (APIHeader) PhaseInterceptorChain.getCurrentMessage().getExchange().get(APIHeader.API_HEADER_KEY);
		AuditHeader auditHeader = getAuditHeader(customerEmploymentDetail,PennantConstants.TRAN_WF);
		auditHeader.setApiHeader(reqHeaderDetails);
		auditHeader = customerEmploymentDetailService.doApprove(auditHeader);

		
		if (auditHeader.getErrorMessage() != null) {
			for (ErrorDetails errorDetail : auditHeader.getErrorMessage()) {
				response = new EmploymentDetail();
				response.setReturnStatus(APIErrorHandlerService.getFailedStatus(errorDetail.getErrorCode(),
						errorDetail.getError()));
			}
		} else {
			CustomerEmploymentDetail customerEmpDetail = (CustomerEmploymentDetail) auditHeader.getAuditDetail().getModelData();
			response = new EmploymentDetail();
			response.setEmployementId(customerEmpDetail.getCustEmpId());
			response.setReturnStatus(APIErrorHandlerService.getSuccessStatus());
		}
		}catch(Exception e){
			logger.error("Exception", e);
			response = new EmploymentDetail();
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus());
		}
		logger.debug("Leaving");

		return response;

	}
	/**
	 * Method for update CustomerEmploymentDetail in PLF system.
	 * 
	 * @param customerEmploymentDetail
	 */
	public WSReturnStatus updateCustomerEmployment(CustomerEmploymentDetail customerEmploymentDetail, String cif) {
		logger.debug("Entering");
		WSReturnStatus response = null;
		try {
			Customer prvCustomer = customerDetailsService.getCustomerByCIF(cif);

			// user language
			LoggedInUser userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());
			customerEmploymentDetail.setUserDetails(userDetails);
			customerEmploymentDetail.setCustID(prvCustomer.getCustID());
			customerEmploymentDetail.setLovDescCustCIF(cif);
			customerEmploymentDetail.setLovDesccustEmpName(String.valueOf(customerEmploymentDetail.getCustEmpName()));
			customerEmploymentDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
			customerEmploymentDetail.setSourceId(APIConstants.FINSOURCE_ID_API);
			customerEmploymentDetail.setNewRecord(false);
			customerEmploymentDetail.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			customerEmploymentDetail.setLastMntBy(userDetails.getLoginUsrID());
			customerEmploymentDetail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
			customerEmploymentDetail.setVersion((customerEmploymentDetailService.getVersion(
					customerEmploymentDetail.getCustID(), customerEmploymentDetail.getCustEmpName())) + 1);

			// call service method to update customer Employment details
			APIHeader reqHeaderDetails = (APIHeader) PhaseInterceptorChain.getCurrentMessage().getExchange().get(APIHeader.API_HEADER_KEY);
			AuditHeader auditHeader = getAuditHeader(customerEmploymentDetail, PennantConstants.TRAN_WF);
			auditHeader.setApiHeader(reqHeaderDetails);
			auditHeader = customerEmploymentDetailService.doApprove(auditHeader);

			response = new WSReturnStatus();
			if (auditHeader.getErrorMessage() != null) {
				for (ErrorDetails errorDetail : auditHeader.getErrorMessage()) {
					response = (APIErrorHandlerService.getFailedStatus(errorDetail.getErrorCode(),
							errorDetail.getError()));
				}
			} else {
				response = APIErrorHandlerService.getSuccessStatus();
			}
		} catch (Exception e) {
			logger.error("Exception", e);
			response = new WSReturnStatus();
			return APIErrorHandlerService.getFailedStatus();
		}
		logger.debug("Leaving");
		return response;
	}
	/**
	 * delete the customerEmploymentDetail.
	 * 
	 * @param customerEmploymentDetail
	 * @return WSReturnStatus
	 * 
	 */
	public WSReturnStatus deleteCustomerEmployment(CustomerEmploymentDetail customerEmploymentDetail) {
		// get the CustomerEmploymentDetail by the CustID and empName
		WSReturnStatus response = null;
		try {
			CustomerEmploymentDetail customerEmpDetail = customerEmploymentDetailService
					.getApprovedCustomerEmploymentDetailByCustEmpId(customerEmploymentDetail.getCustEmpId());
			LoggedInUser userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());
			customerEmpDetail.setUserDetails(userDetails);
			customerEmpDetail.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			customerEmpDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			customerEmpDetail.setNewRecord(false);
			customerEmpDetail.setSourceId(APIConstants.FINSOURCE_ID_API);
			APIHeader reqHeaderDetails = (APIHeader) PhaseInterceptorChain.getCurrentMessage().getExchange().get(APIHeader.API_HEADER_KEY);
			AuditHeader auditHeader = getAuditHeader(customerEmpDetail,PennantConstants.TRAN_WF);
			auditHeader.setApiHeader(reqHeaderDetails);
			auditHeader = customerEmploymentDetailService.doApprove(auditHeader);
			response = new WSReturnStatus();
			if (auditHeader.getErrorMessage() != null) {
				for (ErrorDetails errorDetail : auditHeader.getErrorMessage()) {
					response = (APIErrorHandlerService.getFailedStatus(errorDetail.getErrorCode(),
							errorDetail.getError()));
				}
			} else {

				response = APIErrorHandlerService.getSuccessStatus();
			}
		} catch (Exception e) {
			logger.error("Exception", e);
			response = new WSReturnStatus();
			return APIErrorHandlerService.getFailedStatus();
		}

		logger.debug("Leaving");
		return response;
	}


	/**
	 * Get Audit Header Details
	 * 
	 * @param aCustomer
	 * @param tranType
	 * @return AuditHeader
	 */
	private AuditHeader getAuditHeader(Customer aCustomer, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aCustomer.getBefImage(), aCustomer);
		return new AuditHeader(String.valueOf(aCustomer.getCustID()), String.valueOf(aCustomer.getCustID()), null,
				null, auditDetail, aCustomer.getUserDetails(), new HashMap<String, ArrayList<ErrorDetails>>());
	}

	public CustomerService getCustomerService() {
		return customerService;
	}

	public void setCustomerService(CustomerService customerService) {
		this.customerService = customerService;
	}

	public ErrorDetailService getErrorDetailService() {
		return errorDetailService;
	}

	public void setErrorDetailService(ErrorDetailService errorDetailService) {
		this.errorDetailService = errorDetailService;
	}

	public CustomerDetailsService getCustomerDetailsService() {
		return customerDetailsService;
	}

	public void setCustomerDetailsService(CustomerDetailsService customerDetailsService) {
		this.customerDetailsService = customerDetailsService;
	}

	public CustomerEmploymentDetailService getCustomerEmploymentDetailService() {
		return customerEmploymentDetailService;
	}

	public void setCustomerEmploymentDetailService(CustomerEmploymentDetailService customerEmploymentDetailService) {
		this.customerEmploymentDetailService = customerEmploymentDetailService;
	}
}
