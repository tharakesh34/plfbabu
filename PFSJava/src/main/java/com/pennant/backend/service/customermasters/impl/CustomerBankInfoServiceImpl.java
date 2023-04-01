package com.pennant.backend.service.customermasters.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.applicationmaster.BankDetailDAO;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.beneficiary.BeneficiaryDAO;
import com.pennant.backend.dao.bmtmasters.BankBranchDAO;
import com.pennant.backend.dao.customermasters.CustomerBankInfoDAO;
import com.pennant.backend.model.applicationmaster.BankDetail;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.beneficiary.Beneficiary;
import com.pennant.backend.model.bmtmasters.BankBranch;
import com.pennant.backend.model.customermasters.BankInfoDetail;
import com.pennant.backend.model.customermasters.BankInfoSubDetail;
import com.pennant.backend.model.customermasters.CustomerBankInfo;
import com.pennant.backend.model.systemmasters.LovFieldDetail;
import com.pennant.backend.service.customermasters.CustomerBankInfoService;
import com.pennant.backend.service.systemmasters.LovFieldDetailService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.util.DateUtil;

public class CustomerBankInfoServiceImpl implements CustomerBankInfoService {
	private static Logger logger = LogManager.getLogger(CustomerBankInfoServiceImpl.class);

	private CustomerBankInfoDAO customerBankInfoDAO;
	private AuditHeaderDAO auditHeaderDAO;
	private LovFieldDetailService lovFieldDetailService;
	private BankDetailDAO bankDetailDAO;
	private BankBranchDAO bankBranchDAO;
	private BeneficiaryDAO beneficiaryDAO;

	/**
	 * getBankInfoByCustomerId fetch the details by using CustomerBankInfoDAO's getBankInfoByCustomer method . with
	 * parameter custID. it fetches the records from the CustomerBankInfo.
	 * 
	 * @param id
	 * 
	 * @return CustomerBankInfo List
	 */
	@Override
	public List<CustomerBankInfo> getBankInfoByCustomerId(long id) {
		return getCustomerBankInfoDAO().getBankInfoByCustomer(id, "_View");
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
	public List<CustomerBankInfo> getApprovedBankInfoByCustomerId(long id) {
		return getCustomerBankInfoDAO().getBankInfoByCustomer(id, "_AView");
	}

	@Override
	public CustomerBankInfo getCustomerBankInfoById(long bankId) {
		return getCustomerBankInfoDAO().getCustomerBankInfoById(bankId, "_AView");
	}

	@Override
	public List<BankInfoSubDetail> getBankInfoSubDetailById(long id, Date monthYear) {
		// TODO Auto-generated method stub
		return getCustomerBankInfoDAO().getBankInfoSubDetailById(id, monthYear, "_AView");
	}

	@Override
	public CustomerBankInfo getSumOfAmtsCustomerBankInfoByCustId(Set<Long> custId) {
		return getCustomerBankInfoDAO().getSumOfAmtsCustomerBankInfoByCustId(custId);
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

		CustomerBankInfo customerBankInfo = new CustomerBankInfo();
		BeanUtils.copyProperties((CustomerBankInfo) auditHeader.getAuditDetail().getModelData(), customerBankInfo);

		if (customerBankInfo.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			for (BankInfoDetail bankInfoDetail : customerBankInfo.getBankInfoDetails()) {
				getCustomerBankInfoDAO().delete(bankInfoDetail.getBankInfoSubDetails(), "");
				getCustomerBankInfoDAO().delete(bankInfoDetail, "");
			}
			getCustomerBankInfoDAO().delete(customerBankInfo, "");
		} else {
			customerBankInfo.setRoleCode("");
			customerBankInfo.setNextRoleCode("");
			customerBankInfo.setTaskId("");
			customerBankInfo.setNextTaskId("");
			customerBankInfo.setWorkflowId(0);

			if (customerBankInfo.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				customerBankInfo.setRecordType("");
				long id = getCustomerBankInfoDAO().save(customerBankInfo, "");
				customerBankInfo.setBankId(id);
				// BankInfoDetails
				if (customerBankInfo.getBankInfoDetails().size() > 0) {
					for (BankInfoDetail bankInfoDetail : customerBankInfo.getBankInfoDetails()) {
						bankInfoDetail.setBankId(customerBankInfo.getBankId());
						for (BankInfoSubDetail bankInfoSubDetail : bankInfoDetail.getBankInfoSubDetails()) {
							bankInfoSubDetail.setBankId(customerBankInfo.getBankId());
						}
						customerBankInfoDAO.save(bankInfoDetail, "");
						customerBankInfoDAO.save(bankInfoDetail.getBankInfoSubDetails(), "");
					}

				}
			} else {
				tranType = PennantConstants.TRAN_UPD;
				customerBankInfo.setRecordType("");
				getCustomerBankInfoDAO().update(customerBankInfo, "");
				// BankInfoDetails
				if (customerBankInfo.getBankInfoDetails().size() > 0) {
					for (BankInfoDetail bankInfoDetail : customerBankInfo.getBankInfoDetails()) {
						bankInfoDetail.setBankId(customerBankInfo.getBankId());
						if (bankInfoDetail.isNewRecord()) {
							customerBankInfoDAO.save(bankInfoDetail, "");
							for (BankInfoSubDetail bankInfoSubDetail : bankInfoDetail.getBankInfoSubDetails()) {
								bankInfoSubDetail.setBankId(customerBankInfo.getBankId());
							}
							if (CollectionUtils.isNotEmpty(bankInfoDetail.getBankInfoSubDetails())) {
								customerBankInfoDAO.save(bankInfoDetail.getBankInfoSubDetails(), "");
							}
						} else {
							customerBankInfoDAO.update(bankInfoDetail, "");
							if (CollectionUtils.isNotEmpty(bankInfoDetail.getBankInfoSubDetails())) {
								for (BankInfoSubDetail bankInfoSubDetail : bankInfoDetail.getBankInfoSubDetails()) {
									bankInfoSubDetail.setBankId(customerBankInfo.getBankId());
									if (!bankInfoSubDetail.isNewRecord()) {
										customerBankInfoDAO.update(bankInfoSubDetail, "");
									}
								}
							}
						}
					}
				}
			}
		}
		if (!StringUtils.equals(customerBankInfo.getSourceId(), PennantConstants.FINSOURCE_ID_API)) {
			getCustomerBankInfoDAO().delete(customerBankInfo, "_Temp");
			// BankInfoDetails
			if (customerBankInfo.getBankInfoDetails().size() > 0) {
				for (BankInfoDetail bankInfoDetail : customerBankInfo.getBankInfoDetails()) {
					customerBankInfoDAO.delete(bankInfoDetail, "_Temp");
				}
			}
			auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
			getAuditHeaderDAO().addAudit(auditHeader);
		}

		if (customerBankInfo.isAddToBenficiary()) {
			Beneficiary beneficiary = new Beneficiary();
			beneficiary.setCustID(customerBankInfo.getCustID());
			beneficiary.setBankBranchID(customerBankInfo.getBankBranchID());
			beneficiary.setAccNumber(customerBankInfo.getAccountNumber());
			beneficiary.setAccHolderName(customerBankInfo.getAccountHolderName());
			beneficiary.setPhoneNumber(customerBankInfo.getPhoneNumber());
			beneficiaryDAO.save(beneficiary, "");
		}

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(customerBankInfo);

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	@Override
	public List<BankInfoDetail> getBankInfoDetailById(long id) {
		// TODO Auto-generated method stub
		return customerBankInfoDAO.getBankInfoDetailById(id, "_AView");
	}

	@Override
	public int getVersion(long id) {
		return getCustomerBankInfoDAO().getVersion(id);
	}

	public CustomerBankInfoDAO getCustomerBankInfoDAO() {
		return customerBankInfoDAO;
	}

	public void setCustomerBankInfoDAO(CustomerBankInfoDAO customerBankInfoDAO) {
		this.customerBankInfoDAO = customerBankInfoDAO;
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
	 * @param customerBankInfo
	 * @return AuditDetail
	 */
	@Override
	public AuditDetail doValidations(CustomerBankInfo customerBankInfo, String recordType, AuditDetail auditDetail) {
		ErrorDetail errorDetail = new ErrorDetail();

		// validate Master code with PLF system masters
		if (customerBankInfo.isAddToBenficiary()) {
			if (StringUtils.isNotBlank(customerBankInfo.getiFSC())) {
				if (customerBankInfo.getBankBranchID() == null) {
					String[] valueParm = new String[1];
					valueParm[0] = "bankBranchID";
					errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("51004", "", valueParm), "EN");
					auditDetail.setErrorDetail(errorDetail);
					return auditDetail;
				}
				BankBranch bb = bankBranchDAO.getBankBrachByIFSC(customerBankInfo.getiFSC(), "_AView");
				if ((customerBankInfo.getBankBranchID().equals(bb.getBankBranchID()))
						&& (customerBankInfo.getBankName().equals(bb.getBankCode()))) {
					customerBankInfo.setBankName(bb.getBankCode());
					customerBankInfo.setBankBranch(bb.getBranchDesc());
				} else {
					String[] valueParm = new String[2];
					valueParm[0] = customerBankInfo.getiFSC();
					valueParm[1] = "bankBranchID and bankName given";
					errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90329", "", valueParm), "EN");
					auditDetail.setErrorDetail(errorDetail);
				}
			}
			if (StringUtils.isBlank(customerBankInfo.getAccountHolderName())) {
				String[] valueParm = new String[1];
				valueParm[0] = "accountHolderName";
				errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("51004", "", valueParm), "EN");
				auditDetail.setErrorDetail(errorDetail);
			}
		} else {
			if (customerBankInfo.getBankBranchID() != null) {
				String[] valueParm = new String[1];
				valueParm[0] = "bankBranchID when addToBenficiary is true";
				errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("51004", "", valueParm), "EN");
				auditDetail.setErrorDetail(errorDetail);
			}
		}

		int count = getCustomerBankInfoDAO().getBankCodeCount(customerBankInfo.getBankName());
		if (count <= 0) {
			String[] valueParm = new String[2];
			valueParm[0] = "BankCode";
			valueParm[1] = customerBankInfo.getBankName();
			errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90701", "", valueParm), "EN");
			auditDetail.setErrorDetail(errorDetail);
			return auditDetail;
		} else {
			BankDetail bankDetail = bankDetailDAO.getAccNoLengthByCode(customerBankInfo.getBankName());
			if (bankDetail != null) {
				int maxAccNoLength = bankDetail.getAccNoLength();
				int minAccNoLength = bankDetail.getMinAccNoLength();
				if (customerBankInfo.getAccountNumber().length() < minAccNoLength
						|| customerBankInfo.getAccountNumber().length() > maxAccNoLength) {
					if (minAccNoLength == maxAccNoLength) {
						String[] valueParm = new String[2];
						valueParm[0] = "AccountNumber";
						valueParm[1] = String.valueOf(maxAccNoLength) + " characters";
						errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("30570", "", valueParm));
						auditDetail.setErrorDetail(errorDetail);
						return auditDetail;
					} else {
						String[] valueParm = new String[3];
						valueParm[0] = "AccountNumber";
						valueParm[1] = String.valueOf(minAccNoLength) + " characters";
						valueParm[2] = String.valueOf(maxAccNoLength) + " characters";
						errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("BNK001", "", valueParm));
						auditDetail.setErrorDetail(errorDetail);
						return auditDetail;
					}
				}
			}
		}
		if (StringUtils.isEmpty(customerBankInfo.getAccountNumber())) {
			String[] valueParm = new String[1];
			valueParm[0] = "accountNumber";
			errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90502", "", valueParm), "EN");
			auditDetail.setErrorDetail(errorDetail);
		}
		LovFieldDetail lovFieldDetail = getLovFieldDetailService().getApprovedLovFieldDetailById("ACC_TYPE",
				customerBankInfo.getAccountType());
		if (lovFieldDetail == null) {
			String[] valueParm = new String[2];
			valueParm[0] = "Acctype";
			valueParm[1] = customerBankInfo.getAccountType();
			errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90701", "", valueParm), "EN");
			auditDetail.setErrorDetail(errorDetail);
		}
		if (StringUtils.equals(recordType, PennantConstants.RECORD_TYPE_NEW)) {
			for (BankInfoDetail bankInfoDetail : customerBankInfo.getBankInfoDetails()) {
				List<String> daysList = new ArrayList<>();
				List<String> daysInputlis = new ArrayList<>();
				if (bankInfoDetail.getMonthYear() == null) {
					String[] valueParm = new String[1];
					valueParm[0] = "monthYear";
					errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90502", "", valueParm), "EN");
					auditDetail.setErrorDetail(errorDetail);
				}
				if (StringUtils.isBlank(String.valueOf(bankInfoDetail.getDebitNo()))) {
					String[] valueParm = new String[1];
					valueParm[0] = "debitNo";
					errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90502", "", valueParm), "EN");
					auditDetail.setErrorDetail(errorDetail);
				}
				if (bankInfoDetail.getDebitAmt() == null) {
					String[] valueParm = new String[1];
					valueParm[0] = "debitAmt";
					errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90502", "", valueParm), "EN");
					auditDetail.setErrorDetail(errorDetail);
				}
				if (bankInfoDetail.getCreditAmt() == null) {
					String[] valueParm = new String[1];
					valueParm[0] = "creditAmt";
					errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90502", "", valueParm), "EN");
					auditDetail.setErrorDetail(errorDetail);
				}

				if (bankInfoDetail.getBounceIn() == null) {
					String[] valueParm = new String[1];
					valueParm[0] = "bounceIn";
					errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90502", "", valueParm), "EN");
					auditDetail.setErrorDetail(errorDetail);
				}
				if (bankInfoDetail.getBounceOut() == null) {
					String[] valueParm = new String[1];
					valueParm[0] = "bounceOut";
					errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90502", "", valueParm), "EN");
					auditDetail.setErrorDetail(errorDetail);
				}
				if (SysParamUtil.isAllowed(SMTParameterConstants.CUSTOMER_BANKINFOTAB_ACCBEHAVIOR_DAYBALANCE_REQ)) {
					String configDay = SysParamUtil.getValueAsString(SMTParameterConstants.BANKINFO_DAYS);
					String[] days = configDay.split(PennantConstants.DELIMITER_COMMA);
					for (String type : days) {
						daysList.add(type);
					}
					if (bankInfoDetail.getBankInfoSubDetails().size() != daysList.size()) {
						String[] valueParm = new String[2];
						valueParm[0] = "BankInfoSubDetails";
						valueParm[1] = SysParamUtil.getValueAsString("BANKINFO_DAYS");
						errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("30540", "", valueParm));
						auditDetail.setErrorDetail(errorDetail);
						return auditDetail;

					}
				}
				for (BankInfoSubDetail bankInfoSubDetail : bankInfoDetail.getBankInfoSubDetails()) {
					if (bankInfoSubDetail.getMonthYear() == null) {
						String[] valueParm = new String[1];
						valueParm[0] = "MonthYear";
						errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90502", "", valueParm), "EN");
						auditDetail.setErrorDetail(errorDetail);
					} else {
						if (DateUtil.compare(bankInfoSubDetail.getMonthYear(), bankInfoDetail.getMonthYear()) != 0) {
							String[] valueParm = new String[2];
							valueParm[0] = "bankInfoDetails:MonthYear";
							valueParm[1] = "bankInfoSubDetails:MonthYear";
							errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90277", "", valueParm), "EN");
							auditDetail.setErrorDetail(errorDetail);
						}
					}
					if (SysParamUtil.isAllowed(SMTParameterConstants.CUSTOMER_BANKINFOTAB_ACCBEHAVIOR_DAYBALANCE_REQ)) {
						if (bankInfoSubDetail.getDay() <= 0) {
							String[] valueParm = new String[2];
							valueParm[0] = "BankInfoSubDetails:Day";
							valueParm[1] = "Zero";
							errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("91121", "", valueParm));
							auditDetail.setErrorDetail(errorDetail);
						} else {
							daysInputlis.add(String.valueOf(bankInfoSubDetail.getDay()));
						}
					}
					if (bankInfoSubDetail.getBalance() == null) {
						String[] valueParm = new String[1];
						valueParm[0] = "balance";
						errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90502", "", valueParm), "EN");
						auditDetail.setErrorDetail(errorDetail);
					}
				}
				if (SysParamUtil.isAllowed(SMTParameterConstants.CUSTOMER_BANKINFOTAB_ACCBEHAVIOR_DAYBALANCE_REQ)) {
					for (String day : daysInputlis) {
						boolean flag = true;
						for (String detai : daysList) {
							if (StringUtils.equals(day, detai)) {
								flag = false;
								break;
							}
						}
						if (flag) {
							String[] valueParm = new String[2];
							valueParm[0] = "day";
							valueParm[1] = SysParamUtil.getValueAsString("BANKINFO_DAYS");
							errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("30540", "", valueParm));
							auditDetail.setErrorDetail(errorDetail);
							return auditDetail;
						}
					}
				}
			}
		}
		auditDetail.setErrorDetail(errorDetail);
		return auditDetail;
	}

	public LovFieldDetailService getLovFieldDetailService() {
		return lovFieldDetailService;
	}

	public void setLovFieldDetailService(LovFieldDetailService lovFieldDetailService) {
		this.lovFieldDetailService = lovFieldDetailService;
	}

	public void setBankDetailDAO(BankDetailDAO bankDetailDAO) {
		this.bankDetailDAO = bankDetailDAO;
	}

	@Autowired
	public void setBankBranchDAO(BankBranchDAO bankBranchDAO) {
		this.bankBranchDAO = bankBranchDAO;
	}

	@Autowired
	public void setBeneficiaryDAO(BeneficiaryDAO beneficiaryDAO) {
		this.beneficiaryDAO = beneficiaryDAO;
	}

}
