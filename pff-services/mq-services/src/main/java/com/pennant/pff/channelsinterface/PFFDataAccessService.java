/********************************************************************************************/
/*                  FILE HEADER                                                             */
/********************************************************************************************/
/*                                                                                          */
/*  FileName    :  AccountDetailsUpdateService.java                                         */
/*                                                                                          */
/*  Author      :  PENNANT TECHONOLOGIES              					                    */
/*                                                                                          */
/*  Date        :  Mar 20, 2009                                                             */
/*                                                                                          */
/*  Description :  Class to handle Account Details Updataion                  				*/
/*                                                                                          */
/********************************************************************************************/
/* Date             Who              Version      Comments                                  */
/********************************************************************************************/
/*                                                                                          */
/* Mar 20, 2009     Srinivas Varma   1.0          Initial version                           */
/*------------------------------------------------------------------------------------------*/
/*                                                                                          */
/*                                                                                          */
/*------------------------------------------------------------------------------------------*/
/********************************************************************************************/

package com.pennant.pff.channelsinterface;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.pennant.exception.PFFEngineException;
import com.pennant.interfaces.model.AmortizationSchedulePeriod;
import com.pennant.interfaces.model.BondRedeemDetail;
import com.pennant.interfaces.model.Broker;
import com.pennant.interfaces.model.Brokers;
import com.pennant.interfaces.model.Categories;
import com.pennant.interfaces.model.CustEmployeeDetail;
import com.pennant.interfaces.model.Customer;
import com.pennant.interfaces.model.CustomerAddres;
import com.pennant.interfaces.model.CustomerDocument;
import com.pennant.interfaces.model.CustomerEMail;
import com.pennant.interfaces.model.CustomerPhoneNumber;
import com.pennant.interfaces.model.DDAUpdateRequest;
import com.pennant.interfaces.model.DDAUpdateStatusRequest;
import com.pennant.interfaces.model.Drawee;
import com.pennant.interfaces.model.Drawees;
import com.pennant.interfaces.model.FetchFinCustDetailResponse;
import com.pennant.interfaces.model.FetchFinanceDetailsResponse;
import com.pennant.interfaces.model.Finance;
import com.pennant.interfaces.model.FinanceMainExt;
import com.pennant.interfaces.model.Guarantor;
import com.pennant.interfaces.model.JointBorrower;
import com.pennant.interfaces.model.LimitActivationRequest;
import com.pennant.interfaces.model.LimitDetails;
import com.pennant.interfaces.model.ProductCodes;
import com.pennant.interfaces.model.Repayment;
import com.pennant.interfaces.model.Transaction;
import com.pennant.pff.interfaces.util.FinanceConstants;

public class PFFDataAccessService {

	private final Logger logger = LoggerFactory.getLogger(PFFDataAccessService.class);

	private PFFDataAccess dao;
	private DataSourceTransactionManager transManager;
	private DefaultTransactionDefinition transDef;

	private boolean categoryDelSts = false;
	private boolean productDelSts = false;
	private boolean draweeDelSts = false;
	private boolean brokerDelSts = false;
	
	private void init() {
		transManager = new DataSourceTransactionManager(dao.getDataSource());

		transDef = new DefaultTransactionDefinition();
		transDef.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
		transDef.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
		transDef.setTimeout(60);
	}

	public List<Finance> getCustomerFinanceList(String customerNo, PFFDataAccess dao) throws PFFEngineException{
		logger.debug("Entering");
		List<Finance> financeList = null;
		this.dao = dao;
		init();
		TransactionStatus txnStatus = transManager.getTransaction(transDef);
		try {
			financeList = dao.getCustomerFinanceList(customerNo);
			transManager.commit(txnStatus);
		} catch (EmptyResultDataAccessException erx) {
			transManager.rollback(txnStatus);
			logger.error(erx.getMessage());
			throw new PFFEngineException(FinanceConstants.NO_RECORDS, "No records found.");
		} catch (Exception e) {
			transManager.rollback(txnStatus);
			logger.error(e.getMessage());
			throw new PFFEngineException(FinanceConstants.PROCESS_FAILED, "Internal error unable to process the request.");
		}  finally {
			txnStatus.flush();
			txnStatus = null;
		} 
		logger.debug("Leaving");
		return financeList;
	}

	public FetchFinanceDetailsResponse getFinanceDetails(String financeRef, PFFDataAccess dao) throws PFFEngineException { 
		logger.debug("Entering");
		FetchFinanceDetailsResponse financeDetails = null;
		this.dao = dao;
		init();
		TransactionStatus txnStatus = transManager.getTransaction(transDef);
		try {
			financeDetails = dao.getFinanceDetails(financeRef);
			transManager.commit(txnStatus);
		} catch (EmptyResultDataAccessException erx) {
			transManager.rollback(txnStatus);
			logger.error(erx.getMessage());
			throw new PFFEngineException(FinanceConstants.NO_RECORDS, "No records found.");
		} catch (Exception e) {
			transManager.rollback(txnStatus);
			logger.error(e.getMessage());
			throw new PFFEngineException(FinanceConstants.PROCESS_FAILED, "Internal error unable to process the request.");
		}  finally {
			txnStatus.flush();
			txnStatus = null;
		} 
		logger.debug("Leaving");
		return financeDetails;
	}

	public List<Guarantor> getGuarantorDetails(String financeRef, PFFDataAccess dao) throws PFFEngineException {
		logger.debug("Entering");
		List<Guarantor> guarantorList = null;
		this.dao = dao;
		init();
		TransactionStatus txnStatus = transManager.getTransaction(transDef);
		try {
			guarantorList = dao.getGuarantorDetails(financeRef);
			transManager.commit(txnStatus);
		} catch (EmptyResultDataAccessException erx) {
			transManager.rollback(txnStatus);
			logger.error(erx.getMessage());
			throw new PFFEngineException(FinanceConstants.NO_RECORDS, "No records found.");
		} catch (Exception e) {
			transManager.rollback(txnStatus);
			logger.error(e.getMessage());
			throw new PFFEngineException(FinanceConstants.PROCESS_FAILED, "Internal error unable to process the request.");
		}  finally {
			txnStatus.flush();
			txnStatus = null;
		} 
		logger.debug("Leaving");
		return guarantorList;
	}

	public List<JointBorrower> getJointBorrowerDetails(String financeRef, PFFDataAccess dao) throws PFFEngineException {
		logger.debug("Entering");
		List<JointBorrower> jointBorrowerList = null;
		this.dao = dao;
		init();
		TransactionStatus txnStatus = transManager.getTransaction(transDef);
		try {
			jointBorrowerList = dao.getJointBorrowerDetails(financeRef);
			transManager.commit(txnStatus);
		} catch (EmptyResultDataAccessException erx) {
			transManager.rollback(txnStatus);
			logger.error(erx.getMessage());
			throw new PFFEngineException(FinanceConstants.NO_RECORDS, "No records found.");
		} catch (Exception e) {
			transManager.rollback(txnStatus);
			logger.error(e.getMessage());
			throw new PFFEngineException(FinanceConstants.PROCESS_FAILED, "Internal error unable to process the request.");
		}  finally {
			txnStatus.flush();
			txnStatus = null;
		} 
		logger.debug("Leaving");
		return jointBorrowerList;
	}

	public List<AmortizationSchedulePeriod> getFinanceScheduleDetails(String financeRef, PFFDataAccess dao) throws PFFEngineException {
		logger.debug("Entering");
		List<AmortizationSchedulePeriod> scheduleList = null;
		this.dao = dao;
		init();
		TransactionStatus txnStatus = transManager.getTransaction(transDef);
		try {
			scheduleList = dao.getFinanceScheduleDetails(financeRef);
			transManager.commit(txnStatus);
		} catch (EmptyResultDataAccessException erx) {
			transManager.rollback(txnStatus);
			logger.error(erx.getMessage());
			throw new PFFEngineException(FinanceConstants.NO_RECORDS, "No records found.");
		} catch (Exception e) {
			transManager.rollback(txnStatus);
			logger.error(e.getMessage());
			throw new PFFEngineException(FinanceConstants.PROCESS_FAILED, "Internal error unable to process the request.");
		} finally {
			txnStatus.flush();
			txnStatus = null;
		} 
		logger.debug("Leaving");
		return scheduleList;
	}

	public List<Transaction> getFinTransactionDetails(String financeRef, Date txnFromDate, Date txnToDate, 
			BigDecimal txnFromAmt,BigDecimal txnToAmt, PFFDataAccess dao) throws PFFEngineException{
		logger.debug("Entering");
		List<Transaction> transactionList = null;
		this.dao = dao;
		init();
		TransactionStatus txnStatus = transManager.getTransaction(transDef);
		try {
			transactionList = dao.getFinTransactionDetails(financeRef, txnFromDate, txnToDate, txnFromAmt, txnToAmt);
			transManager.commit(txnStatus);
		} catch (EmptyResultDataAccessException erx) {
			transManager.rollback(txnStatus);
			logger.error(erx.getMessage());
			throw new PFFEngineException(FinanceConstants.NO_RECORDS, "No records found.");
		} catch (Exception e) {
			transManager.rollback(txnStatus);
			logger.error(e.getMessage());
			throw new PFFEngineException(FinanceConstants.PROCESS_FAILED, "Internal error unable to process the request.");
		} finally {
			txnStatus.flush();
			txnStatus = null;
		} 
		logger.debug("Leaving");
		return transactionList;
	}

	public List<Repayment> getFinRepayDetails(String acctNum, PFFDataAccess dao) throws PFFEngineException {
		logger.debug("Entering");
		List<Repayment> repaymentsList = null;
		this.dao = dao;
		init();
		TransactionStatus txnStatus = transManager.getTransaction(transDef);
		try {
			repaymentsList = dao.getFinRepayDetails(acctNum);
			transManager.commit(txnStatus);
		} catch (EmptyResultDataAccessException erx) {
			transManager.rollback(txnStatus);
			logger.error(erx.getMessage());
			throw new PFFEngineException(FinanceConstants.NO_RECORDS, "No records found.");
		} catch (Exception e) {
			transManager.rollback(txnStatus);
			logger.error(e.getMessage());
			throw new PFFEngineException(FinanceConstants.PROCESS_FAILED, "Internal error unable to process the request.");
		} finally {
			txnStatus.flush();
			txnStatus = null;
		}
		logger.debug("Leaving");
		return repaymentsList;
	}

	public boolean saveLimitActivation(LimitActivationRequest lmtactReqBean, PFFDataAccess dao) {
		logger.debug("Entering");
		this.dao = dao;
		init();
		TransactionStatus transaction = transManager.getTransaction(transDef);
		try {
			if(lmtactReqBean != null) {
				for (LimitDetails limitDetails : lmtactReqBean.getLimitDetails()) {

					this.categoryDelSts = false;
					this.productDelSts = false;
					this.draweeDelSts = false;
					this.brokerDelSts = false;

					limitDetails.setCustomerReference(lmtactReqBean.getCustomerReference());
					limitDetails.setBranchCode(lmtactReqBean.getBranchCode());

					// Save limit details
					saveOrUpdateLimitDetails(limitDetails);

					if(limitDetails.getCategories() != null && !limitDetails.getCategories().isEmpty()) {
						for (Categories categories : limitDetails.getCategories()) {

							if(!StringUtils.isBlank(categories.getCategory())) {
								categories.setLimitRef(limitDetails.getLimitRef());

								// Save limit category details
								saveOrUpdateCategoryDetails(categories);
							}
						}
					}
					if(limitDetails.getProductCodes() != null && !limitDetails.getProductCodes().isEmpty()) {
						for (ProductCodes productCodes : limitDetails.getProductCodes()) {

							if(!StringUtils.isBlank(productCodes.getProductCode())) {
								productCodes.setLimitRef(limitDetails.getLimitRef());

								// Save Customer limit product code details
								saveOrUpdateProductCodes(productCodes);
							}
						}
					}	
					if(limitDetails.getDrawees() != null && !limitDetails.getDrawees().isEmpty()) {
						for (Drawees drawees : limitDetails.getDrawees()) {
							if(drawees != null && drawees.getDrawee() != null) {
								for (Drawee drawee : drawees.getDrawee()) {
									if(!StringUtils.isBlank(drawee.getDraweeID())) {

										drawee.setLimitRef(limitDetails.getLimitRef());

										// Save limit drawee details
										saveOrUpdateDraweeDetails(drawee);
									}
								}	
							}	
						}
					}	
					if(limitDetails.getBrokers() != null && !limitDetails.getBrokers().isEmpty()) {
						for (Brokers brokers : limitDetails.getBrokers()) {
							if(brokers != null && brokers.getBroker() != null) {
								for (Broker broker : brokers.getBroker()) {
									if(!StringUtils.isBlank(broker.getBrokerID())) {

										broker.setLimitRef(limitDetails.getLimitRef());

										// Save the limit broker details
										saveOrUpdateBrokerDetails(broker);	
									}
								}	
							}
						}
					}

					// Save the S&D Fee charges details in separate table to process Amortization calculation
					saveOrUpdateStudyFeeCharges(limitDetails);

				}
			}
			transManager.commit(transaction);
		} catch (Exception e) {
			transManager.rollback(transaction);
			logger.debug("Leaving");
			logger.error(e.getMessage());
			logger.info(" return value false");
			return false;
		}
		logger.debug("Leaving");
		logger.info("return value true");
		return true;
	}

	/**
	 * Method for save or update the S&D Fee details
	 * 
	 * @param limitDetails
	 */
	private void saveOrUpdateStudyFeeCharges(LimitDetails limitDetails) {
		logger.debug("Entering");

		if(limitDetails != null) {
			// Validate limit category details
			LimitDetails aLimitDetails = this.dao.getStudyFeeDetails(limitDetails.getCustomerReference(), 
					limitDetails.getLimitRef());

			if(aLimitDetails == null) {
				this.dao.saveStudyFeeCharges(limitDetails);
			} else {
				this.dao.updateStudyFeeCharges(limitDetails);
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Method for save or update limit broker details
	 * 
	 * @param broker
	 */
	private void saveOrUpdateBrokerDetails(Broker broker) {
		logger.debug("Entering");

		if(!this.brokerDelSts) {
			// Delete limit Broker details
			this.dao.deleteBrokerDetails(broker.getLimitRef());
			
			this.brokerDelSts = true;
		}

		// Save Broker details
		this.dao.saveBrokerDetails(broker);

		logger.debug("Leaving");
	}

	/**
	 * Method for save or update limit drawee details
	 * 
	 * @param drawee
	 */
	private void saveOrUpdateDraweeDetails(Drawee drawee) {
		logger.debug("Entering");

		if(!this.draweeDelSts) {
			// Delete limit category details
			this.dao.deleteDraweeDetails(drawee.getLimitRef());
			
			this.draweeDelSts = true;
		}

		// Save Drawee details
		this.dao.saveDraweeDetails(drawee);

		logger.debug("Leaving");
	}

	/**
	 * Method for save or update limit product codes
	 * 
	 * @param productCodes
	 */
	private void saveOrUpdateProductCodes(ProductCodes productCodes) {
		logger.debug("Entering");

		if(!this.productDelSts) {
			// Delete limit product details
			this.dao.deleteLimitProductCodes(productCodes.getLimitRef());
			
			this.productDelSts = true;
		}

		// Save Limit Product Codes
		this.dao.saveProductCodes(productCodes);

		logger.debug("Leaving");
	}

	/**
	 * Method for save or update limit category details
	 * 
	 * @param categories
	 */
	private void saveOrUpdateCategoryDetails(Categories categories) {
		logger.debug("Entering");

		if(!this.categoryDelSts) {
			// Delete limit category details
			this.dao.deleteLimitCategories(categories.getLimitRef());
			
			this.categoryDelSts = true;
		}

		// Save Limit Category Details
		this.dao.saveLimitCategories(categories);

		logger.debug("Leaving");
	}

	/**
	 * Method for save or update the customer limit details
	 * 
	 * @param limitDetails
	 */
	private void saveOrUpdateLimitDetails(LimitDetails limitDetails) {
		logger.debug("Entering");

		// Validate Limit details
		LimitDetails aLimitDetails = this.dao.getLimitDetails(limitDetails.getCustomerReference(), limitDetails.getLimitRef());

		logger.debug("Entering");
		if(aLimitDetails == null) {
			this.dao.saveLimitDetails(limitDetails);
		} else {
			this.dao.updateLimitDetails(limitDetails);
		}

		logger.debug("Leaving");
	}

	public FetchFinCustDetailResponse getFinCustDetails(String finReference, PFFDataAccess dao) throws PFFEngineException{
		logger.debug("Entering");
		FetchFinCustDetailResponse detailsResponse = null;
		this.dao = dao;
		init();
		TransactionStatus txnStatus = transManager.getTransaction(transDef);
		try {
			detailsResponse = dao.getFinCustInstDetails(finReference);
			transManager.commit(txnStatus);
		} catch (EmptyResultDataAccessException erx) {
			transManager.rollback(txnStatus);
			logger.error(erx.getMessage());
			throw new PFFEngineException(FinanceConstants.NO_RECORDS, "No records found.");
		} catch (Exception e) {
			transManager.rollback(txnStatus);
			logger.error(e.getMessage());
			throw new PFFEngineException(FinanceConstants.PROCESS_FAILED, "Internal error unable to process the request.");
		} finally {
			txnStatus.flush();
			txnStatus = null;
		}
		logger.debug("Leaving");
		return detailsResponse;
	}

	public boolean updateDDAReference(DDAUpdateRequest ddaUpdateRequest, PFFDataAccess dao) throws PFFEngineException{
		logger.debug("Entering");
		boolean isDDAUpdated = false;
		this.dao = dao;
		init();
		TransactionStatus txnStatus = transManager.getTransaction(transDef);
		try {
			isDDAUpdated = dao.updateDDAReference(ddaUpdateRequest.getDDAReferenceNo(), ddaUpdateRequest.getFinReference());
			if(isDDAUpdated) {
				dao.updateDDARefLog(ddaUpdateRequest.getDDAReferenceNo(), ddaUpdateRequest.getFinReference());
			}
			transManager.commit(txnStatus);
		} catch (Exception e) {
			transManager.rollback(txnStatus);
			logger.error(e.getMessage());
			throw new PFFEngineException(FinanceConstants.PROCESS_FAILED, "Internal error unable to process the request.");
		} finally {
			txnStatus.flush();
			txnStatus = null;
		}
		logger.debug("Leaving");
		return isDDAUpdated;
	}

	/**
	 * Method to Update DDA Status with status response as 'ACK/NAK'
	 * 
	 * @param ddaUpdateStatus
	 * @param dao
	 */
	public boolean updateDDAStatus(DDAUpdateStatusRequest ddaUpdateStatus, PFFDataAccess dao) throws PFFEngineException {
		logger.debug("Entering");
		boolean isDDAStsUpdated = false;
		this.dao = dao;
		init();
		TransactionStatus txnStatus = transManager.getTransaction(transDef);
		try {
			isDDAStsUpdated = dao.updateDDAStatus(ddaUpdateStatus);
			transManager.commit(txnStatus);
		} catch (Exception e) {
			transManager.rollback(txnStatus);
			logger.error(e.getMessage());
			throw new PFFEngineException(FinanceConstants.PROCESS_FAILED, "Internal error unable to process the request.");
		} finally {
			txnStatus.flush();
			txnStatus = null;
		}
		if (!isDDAStsUpdated) {
			throw new PFFEngineException(FinanceConstants.NO_RECORDS, "No records found.");
		}
		logger.debug("Leaving");
		return isDDAStsUpdated;
	}

	public boolean updateCustomer(Customer customer, String type, PFFDataAccess dao) throws PFFEngineException {
		logger.debug("Entering");

		boolean isCustomerUpdated = false;
		this.dao = dao;
		init();
		TransactionStatus txnStatus = transManager.getTransaction(transDef);

		try {
			if(customer == null) {
				throw new PFFEngineException(FinanceConstants.CUSTOMER_BLANK, "Customer can not be blank");
			}
			boolean isExists = dao.getCustomer(customer.getCustID(), type);
			if(isExists) {
				isCustomerUpdated = dao.updateCustomer(customer, type);
			} else {
				throw new PFFEngineException(FinanceConstants.CUSTOMER_NOT_EXISTS, "Unable to update, Customer does not exists");
			}
			transManager.commit(txnStatus);
		} catch (PFFEngineException pfe) {
			transManager.rollback(txnStatus);
			logger.error(pfe.getMessage());
			throw pfe;
		} catch (Exception e) {
			transManager.rollback(txnStatus);
			logger.error(e.getMessage());
			throw new PFFEngineException(FinanceConstants.PROCESS_FAILED, "Internal error unable to process the request.");
		} finally {
			txnStatus.flush();
			txnStatus = null;
		}

		logger.debug("Leaving");
		return isCustomerUpdated;
	}

	public boolean updateCustDocuments(CustomerDocument customerDocument, String type, PFFDataAccess dao) throws PFFEngineException {
		logger.debug("Entering");

		boolean isCustDocsUpdated = false;
		this.dao = dao;
		init();
		TransactionStatus txnStatus = transManager.getTransaction(transDef);

		try {
			if(customerDocument == null) {
				throw new PFFEngineException(FinanceConstants.PROCESS_FAILED, "CustomerDocuments can not be blank");
			}
			boolean isExists = dao.getCustomerDocuments(customerDocument, type);
			if(isExists) {
				isCustDocsUpdated = dao.updateCustDocuments(customerDocument, type);
			} else {
				customerDocument.setRoleCode("");
				customerDocument.setNextRoleCode("");
				customerDocument.setTaskId("");
				customerDocument.setNextTaskId("");
				customerDocument.setRecordType("");
				customerDocument.setWorkflowId(0);
				customerDocument.setRecordStatus("Approved");
				customerDocument.setLastMntOn(new Timestamp(20151210));
				customerDocument.setLastMntBy(1001);
				customerDocument.setVersion(980);
				//isCustDocsUpdated = dao.saveCustomerDocuments(customerDocument, type);
			}
			transManager.commit(txnStatus);
		} catch (Exception e) {
			transManager.rollback(txnStatus);
			logger.error(e.getMessage());
			throw new PFFEngineException(FinanceConstants.PROCESS_FAILED, "Internal error unable to process the request.");

		} finally {
			txnStatus.flush();
			txnStatus = null;
		}

		logger.debug("Leaving");
		return isCustDocsUpdated;
	}

	public boolean updateCustEmployee(CustEmployeeDetail custEmployeeDetail, String type, PFFDataAccess dao) throws PFFEngineException {
		logger.debug("Entering");

		boolean isCustEmpUpdated = false;
		this.dao = dao;
		init();
		TransactionStatus txnStatus = transManager.getTransaction(transDef);

		try {
			if(custEmployeeDetail == null) {
				throw new PFFEngineException(FinanceConstants.PROCESS_FAILED, "CustEmployeeDetail can not be blank");
			}
			boolean isExists = dao.getCustEmployee(custEmployeeDetail, type);
			if(isExists) {
				isCustEmpUpdated = dao.updateCustEmployee(custEmployeeDetail, type);
			} else {
				custEmployeeDetail.setRoleCode("");
				custEmployeeDetail.setNextRoleCode("");
				custEmployeeDetail.setTaskId("");
				custEmployeeDetail.setNextTaskId("");
				custEmployeeDetail.setRecordType("");
				custEmployeeDetail.setWorkflowId(0);
				custEmployeeDetail.setRecordStatus("Approved");
				custEmployeeDetail.setLastMntOn(new Timestamp(20151210));
				custEmployeeDetail.setLastMntBy(1001);
				custEmployeeDetail.setVersion(980);
				isCustEmpUpdated = dao.saveCustEmployee(custEmployeeDetail, type);
			}
			transManager.commit(txnStatus);
		} catch (Exception e) {
			transManager.rollback(txnStatus);
			logger.error(e.getMessage());
			throw new PFFEngineException(FinanceConstants.PROCESS_FAILED, "Internal error unable to process the request.");

		} finally {
			txnStatus.flush();
			txnStatus = null;
		}

		logger.debug("Leaving");
		return isCustEmpUpdated;
	}

	public boolean updateCustAddress(CustomerAddres custAddress, String type, PFFDataAccess dao) throws PFFEngineException {
		logger.debug("Entering");

		boolean isCustAddresUpdated = false;
		this.dao = dao;
		init();
		TransactionStatus txnStatus = transManager.getTransaction(transDef);

		try {
			if(custAddress == null) {
				throw new PFFEngineException(FinanceConstants.PROCESS_FAILED, "CustomerAddres can not be blank");
			}
			boolean isExists = dao.getCustAddress(custAddress, type);
			if(isExists) {
				isCustAddresUpdated = dao.updateCustAddress(custAddress, type);
			} else {
				custAddress.setRoleCode("");
				custAddress.setNextRoleCode("");
				custAddress.setTaskId("");
				custAddress.setNextTaskId("");
				custAddress.setRecordType("");
				custAddress.setWorkflowId(0);
				custAddress.setRecordStatus("Approved");
				custAddress.setLastMntOn(new Timestamp(20151210));
				custAddress.setLastMntBy(1001);
				custAddress.setVersion(980);
				isCustAddresUpdated = dao.saveCustAddress(custAddress, type);
			}
			transManager.commit(txnStatus);
		} catch (Exception e) {
			transManager.rollback(txnStatus);
			logger.error(e.getMessage());
			throw new PFFEngineException(FinanceConstants.PROCESS_FAILED, "Internal error unable to process the request.");

		} finally {
			txnStatus.flush();
			txnStatus = null;
		}

		logger.debug("Leaving");
		return isCustAddresUpdated;
	}

	public boolean updateCustPhonenumber(CustomerPhoneNumber custPhoneNumber, String type, PFFDataAccess dao)  throws PFFEngineException {
		logger.debug("Entering");

		boolean isCustPhoneUpdated = false;
		this.dao = dao;
		init();
		TransactionStatus txnStatus = transManager.getTransaction(transDef);

		try {
			if(custPhoneNumber == null) {
				throw new PFFEngineException(FinanceConstants.PROCESS_FAILED, "CustomerPhoneNumber can not be blank");
			}
			boolean isExists = dao.getCustPhoneNumber(custPhoneNumber, type);
			if(isExists) {
				isCustPhoneUpdated = dao.updateCustPhoneNumber(custPhoneNumber, type);
			} else {
				custPhoneNumber.setRoleCode("");
				custPhoneNumber.setNextRoleCode("");
				custPhoneNumber.setTaskId("");
				custPhoneNumber.setNextTaskId("");
				custPhoneNumber.setRecordType("");
				custPhoneNumber.setWorkflowId(0);
				custPhoneNumber.setRecordStatus("Approved");
				custPhoneNumber.setLastMntOn(new Timestamp(20151210));
				custPhoneNumber.setLastMntBy(1001);
				custPhoneNumber.setVersion(980);
				isCustPhoneUpdated = dao.saveCustPhoneNumber(custPhoneNumber, type);
			}
			transManager.commit(txnStatus);
		} catch (Exception e) {
			transManager.rollback(txnStatus);
			logger.error(e.getMessage());
			throw new PFFEngineException(FinanceConstants.PROCESS_FAILED, "Internal error unable to process the request.");

		} finally {
			txnStatus.flush();
			txnStatus = null;
		}

		logger.debug("Leaving");
		return isCustPhoneUpdated;
	}

	public boolean updateCustEmail(CustomerEMail custEmail, String type, PFFDataAccess dao) throws PFFEngineException {
		logger.debug("Entering");

		boolean isCustEmailUpdated = false;
		this.dao = dao;
		init();
		TransactionStatus txnStatus = transManager.getTransaction(transDef);

		try {
			if(custEmail == null) {
				throw new PFFEngineException(FinanceConstants.PROCESS_FAILED, "CustomerEMail can not be blank");
			}
			boolean isExists = dao.getCustEmail(custEmail, type);
			if(isExists) {
				isCustEmailUpdated = dao.updateCustEmail(custEmail, type);
			} else {
				custEmail.setRoleCode("");
				custEmail.setNextRoleCode("");
				custEmail.setTaskId("");
				custEmail.setNextTaskId("");
				custEmail.setRecordType("");
				custEmail.setWorkflowId(0);
				custEmail.setRecordStatus("Approved");
				custEmail.setLastMntOn(new Timestamp(20151210));
				custEmail.setLastMntBy(1001);
				custEmail.setVersion(980);
				isCustEmailUpdated = dao.saveCustEmail(custEmail, type);
			}
			transManager.commit(txnStatus);
		} catch (Exception e) {
			transManager.rollback(txnStatus);
			logger.error(e.getMessage());
			throw new PFFEngineException(FinanceConstants.PROCESS_FAILED, "Internal error unable to process the request.");

		} finally {
			txnStatus.flush();
			txnStatus = null;
		}

		logger.debug("Leaving");
		return isCustEmailUpdated;
	}

	/**
	 * Method for verify bond details and do appropriate action(Save/Update)
	 * 
	 * @param detail
	 * @param dataAccess
	 * @return
	 * @throws PFFEngineException 
	 */
	public boolean saveOrUpdateBondDetails(BondRedeemDetail detail, PFFDataAccess dao) throws PFFEngineException {
		logger.debug("Entering");
		boolean bondRedeemStatus = false;
		this.dao = dao;
		init();
		TransactionStatus txnStatus = transManager.getTransaction(transDef);
		try {
			BondRedeemDetail nbDetail = dao.getBondDetails(detail.getPurchaseRef(), detail.getHostRef());
			if(nbDetail == null) {
				bondRedeemStatus = dao.saveBondRedeemDetails(detail);
			} else {
				bondRedeemStatus = dao.updateBondRedeemDetails(detail);
			}
			transManager.commit(txnStatus);
		} catch (Exception e) {
			transManager.rollback(txnStatus);
			logger.error(e.getMessage());
			throw new PFFEngineException(FinanceConstants.PROCESS_FAILED, "Internal error unable to process the request.");
		} finally {
			txnStatus.flush();
			txnStatus = null;
		}
		if (!bondRedeemStatus) {
			throw new PFFEngineException(FinanceConstants.NO_RECORDS, "No records found.");
		}
		logger.debug("Leaving");
		return bondRedeemStatus;
	}

	/**
	 * Method for fetch bond details
	 * 
	 * @param purchaseRef
	 * @param hostRef
	 * @param dao 
	 * @return
	 */
	public BondRedeemDetail getBondDetails(BondRedeemDetail bondRedeemDetail, PFFDataAccess dao) {
		return dao.getBondDetails(bondRedeemDetail.getPurchaseRef(), bondRedeemDetail.getHostRef());
	}

	public FinanceMainExt getFinanceBondDetails(String purchaseRef, PFFDataAccess dao) {
		return dao.getFinanceBondDetails(purchaseRef);
	}
}