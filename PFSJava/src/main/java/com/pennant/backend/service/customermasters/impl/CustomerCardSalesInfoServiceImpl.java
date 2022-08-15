package com.pennant.backend.service.customermasters.impl;

import java.sql.Timestamp;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.customermasters.CustomerCardSalesInfoDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.CustCardSales;
import com.pennant.backend.model.customermasters.CustCardSalesDetails;
import com.pennant.backend.service.customermasters.CustomerCardSalesInfoService;
import com.pennant.backend.service.systemmasters.LovFieldDetailService;
import com.pennant.backend.util.PennantConstants;
import com.pennanttech.pennapps.core.model.ErrorDetail;

public class CustomerCardSalesInfoServiceImpl implements CustomerCardSalesInfoService {
	private static Logger logger = LogManager.getLogger(CustomerCardSalesInfoServiceImpl.class);

	private CustomerCardSalesInfoDAO customerCardSalesInfoDAO;
	private AuditHeaderDAO auditHeaderDAO;
	private LovFieldDetailService lovFieldDetailService;

	/**
	 * getBankInfoByCustomerId fetch the details by using CustomerBankInfoDAO's getBankInfoByCustomer method . with
	 * parameter custID. it fetches the records from the CustomerBankInfo.
	 * 
	 * @param id
	 * 
	 * @return CustomerBankInfo List
	 */
	@Override
	public List<CustCardSales> getCardSalesInfoByCustomerId(long id) {
		return getCustomerCardSalesInfoDAO().getCardSalesInfoByCustomer(id, "_View");
	}

	/**
	 * getBankInfoByCustomerId fetch the details by using CustomerBankInfoDAO's getBankInfoByCustomer method . with
	 * parameter custID. it fetches the Approved records from the CustomerBankInfo.
	 * 
	 * @param id
	 * 
	 * @return CustomerBankInfo List
	 */
	@Override
	public List<CustCardSales> getApprovedCardSalesInfoByCustomerId(long id) {
		return getCustomerCardSalesInfoDAO().getCardSalesInfoByCustomer(id, "_AView");
	}

	@Override
	public CustCardSales getCustomerCardSalesInfoById(long bankId) {
		return getCustomerCardSalesInfoDAO().getCustomerCardSalesInfoById(bankId, "_AView");
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) based on the Record type do
	 * following actions a) DELETE Delete the record from the main table by using getCustomerBankInfoDAO().delete with
	 * parameters customerBankInfo,"" b) NEW Add new record in to main table by using getCustomerBankinfoDAO().save with
	 * parameters customerBankInfo,"" c) EDIT Update record in the main table by using getCustomerBankInfoDAO().update
	 * with parameters customerBankInfo,"" 3) Delete the record from the workFlow table by using
	 * getCustomerBankInfoDAO().delete with parameters customerBankInfo,"_Temp" 4) Audit the record in to AuditHeader
	 * and AdtCustomerBankInfo by using auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit the record in to
	 * AuditHeader and AdtCustomerBankInfo by using auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
	 * 
	 * @param AuditHeader (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.debug("Entering");

		String tranType = "";

		CustCardSales customerCardSalesInfo = new CustCardSales();
		BeanUtils.copyProperties((CustCardSales) auditHeader.getAuditDetail().getModelData(), customerCardSalesInfo);

		if (customerCardSalesInfo.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			getCustomerCardSalesInfoDAO().delete(customerCardSalesInfo, "");
		} else {
			customerCardSalesInfo.setRoleCode("");
			customerCardSalesInfo.setNextRoleCode("");
			customerCardSalesInfo.setTaskId("");
			customerCardSalesInfo.setNextTaskId("");
			customerCardSalesInfo.setWorkflowId(0);

			if (customerCardSalesInfo.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				customerCardSalesInfo.setRecordType("");
				customerCardSalesInfo.setId(getCustomerCardSalesInfoDAO().save(customerCardSalesInfo, ""));
				if (customerCardSalesInfo.getCustCardMonthSales() != null) {
					for (CustCardSalesDetails custCardSalesDetails : customerCardSalesInfo.getCustCardMonthSales()) {
						custCardSalesDetails.setNewRecord(true);
						custCardSalesDetails.setVersion(1);
						custCardSalesDetails.setRecordType("");
						custCardSalesDetails.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
						custCardSalesDetails.setLastMntBy(customerCardSalesInfo.getLastMntBy());
						custCardSalesDetails.setLastMntOn(new Timestamp(System.currentTimeMillis()));
						custCardSalesDetails.setCardSalesId(customerCardSalesInfo.getId());
						getCustomerCardSalesInfoDAO().save(custCardSalesDetails, "");
					}
				}

			} else {
				tranType = PennantConstants.TRAN_UPD;
				customerCardSalesInfo.setRecordType("");
				getCustomerCardSalesInfoDAO().update(customerCardSalesInfo, "");
				if (customerCardSalesInfo.getCustCardMonthSales() != null) {
					for (CustCardSalesDetails custCardSalesDetails : customerCardSalesInfo.getCustCardMonthSales()) {
						custCardSalesDetails.setNewRecord(true);
						custCardSalesDetails.setVersion(customerCardSalesInfo.getVersion());
						custCardSalesDetails.setRecordType("");
						custCardSalesDetails.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
						custCardSalesDetails.setLastMntBy(customerCardSalesInfo.getLastMntBy());
						custCardSalesDetails.setLastMntOn(new Timestamp(System.currentTimeMillis()));
						custCardSalesDetails.setCardSalesId(customerCardSalesInfo.getId());
						getCustomerCardSalesInfoDAO().update(custCardSalesDetails, "");
					}
				}
			}
		}
		if (!StringUtils.equals(customerCardSalesInfo.getSourceId(), PennantConstants.FINSOURCE_ID_API)) {
			customerCardSalesInfoDAO.delete(customerCardSalesInfo, "_Temp");
			// card month sales
			if (customerCardSalesInfo.getCustCardMonthSales().size() > 0) {
				for (CustCardSalesDetails custCAdMnthSaleInfoDetail : customerCardSalesInfo.getCustCardMonthSales()) {
					customerCardSalesInfoDAO.delete(custCAdMnthSaleInfoDetail, "_Temp");
				}
			}
			auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
			getAuditHeaderDAO().addAudit(auditHeader);
		}

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(customerCardSalesInfo);

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	@Override
	public List<CustCardSalesDetails> getCardSalesInfoSubDetailById(long CardSaleId, String type) {

		return getCustomerCardSalesInfoDAO().getCardSalesInfoSubDetailById(CardSaleId, type);
	}

	@Override
	public int getVersion(long id) {
		return getCustomerCardSalesInfoDAO().getVersion(id);
	}

	public AuditHeaderDAO getAuditHeaderDAO() {
		return auditHeaderDAO;
	}

	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	/**
	 * Validate CustomerBankInfo.
	 * 
	 * @param customerCardSalesInfo
	 * @return AuditDetail
	 */
	@Override
	public AuditDetail doValidations(CustCardSales customerCardSalesInfo, String recordType) {

		AuditDetail auditDetail = new AuditDetail();
		ErrorDetail errorDetail = new ErrorDetail();

		// validate Master code with PLF system masters
		/*
		 * int count = getCustomerCardSalesInfoDAO().getCount(customerCardSalesInfo.getMerchantName()); if (count <= 0)
		 * { String[] valueParm = new String[2]; valueParm[0] = "BankCode"; valueParm[1] =
		 * customerCardSalesInfo.getMerchantName(); errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90701", "",
		 * valueParm), "EN"); auditDetail.setErrorDetail(errorDetail); return auditDetail; }
		 */

		auditDetail.setErrorDetail(errorDetail);
		return auditDetail;

	}

	public LovFieldDetailService getLovFieldDetailService() {
		return lovFieldDetailService;
	}

	public void setLovFieldDetailService(LovFieldDetailService lovFieldDetailService) {
		this.lovFieldDetailService = lovFieldDetailService;
	}

	public CustomerCardSalesInfoDAO getCustomerCardSalesInfoDAO() {
		return customerCardSalesInfoDAO;
	}

	public void setCustomerCardSalesInfoDAO(CustomerCardSalesInfoDAO customerCardSalesInfoDAO) {
		this.customerCardSalesInfoDAO = customerCardSalesInfoDAO;
	}

}
