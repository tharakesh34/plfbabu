package com.pennant.backend.service.finance.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.app.util.DateUtility;
import com.pennant.backend.dao.finance.GSTInvoiceTxnDAO;
import com.pennant.backend.dao.rulefactory.RuleDAO;
import com.pennant.backend.model.applicationmaster.Entity;
import com.pennant.backend.model.applicationmaster.TaxDetail;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerAddres;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinTaxDetails;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.GSTInvoiceTxn;
import com.pennant.backend.model.finance.GSTInvoiceTxnDetails;
import com.pennant.backend.model.finance.ManualAdviseMovements;
import com.pennant.backend.model.finance.financetaxdetail.FinanceTaxDetail;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.rulefactory.Rule;
import com.pennant.backend.model.systemmasters.Province;
import com.pennant.backend.service.applicationmaster.EntityService;
import com.pennant.backend.service.customermasters.CustomerDetailsService;
import com.pennant.backend.service.finance.FinFeeDetailService;
import com.pennant.backend.service.finance.FinanceTaxDetailService;
import com.pennant.backend.service.finance.GSTInvoiceTxnService;
import com.pennant.backend.service.systemmasters.ProvinceService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.RuleConstants;
import com.pennanttech.pennapps.core.resource.Literal;

public class GSTInvoiceTxnServiceImpl implements GSTInvoiceTxnService {
	
	private static final Logger logger = Logger.getLogger(GSTInvoiceTxnServiceImpl.class);
	
	private GSTInvoiceTxnDAO gstInvoiceTxnDAO;
	private EntityService entityService;
	private ProvinceService provinceService;

	// GST Invoice preparation
	
	private transient CustomerDetailsService customerDetailsService;
	private transient FinanceTaxDetailService financeTaxDetailService;
	private FinFeeDetailService finFeeDetailService;
	private RuleDAO ruleDAO;

	public GSTInvoiceTxnServiceImpl() {
		super();
	}

	@Override
	public long save(GSTInvoiceTxn gstInvoiceTxn) {
		return this.gstInvoiceTxnDAO.save(gstInvoiceTxn);
	}
	
	@Override
	public Entity getEntity(String entityCode) {
		return this.entityService.getApprovedEntity(entityCode);
	}
	
	@Override
	public Entity getEntityByFinDivision(String divisionCode, String type) {
		return entityService.getEntityByFinDivision(divisionCode, type);
	}
	
	@Override
	public Province getApprovedProvince(String cPCountry, String cPProvince) {
		return provinceService.getApprovedProvinceById(cPCountry, cPProvince);
	}
	
	/**
	 * preparing GST Invoice Details and insert data into GST Invoice related tables
	 * @param aeEvent
	 * @param financeDetail
	 */
	@Override
	public void gstInvoicePreparation(long linkedTranId, FinanceDetail financeDetail, List<FinFeeDetail> finFeeDetailsList, 
			List<ManualAdviseMovements> movements, String invoiceType, String finReference) {
		logger.debug(Literal.ENTERING);
		
		if (CollectionUtils.isEmpty(finFeeDetailsList) && CollectionUtils.isEmpty(movements) && StringUtils.isBlank(finReference) && financeDetail == null) {
			return;
		} else {
			
			// Tax Details fetching
			financeDetail.setFinanceTaxDetails(financeTaxDetailService.getApprovedFinanceTaxDetail(finReference));
			
			FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();
			FinanceType financeType = financeDetail.getFinScheduleData().getFinanceType();
			List<GSTInvoiceTxnDetails> gstInvoiceTxnDetails = new ArrayList<GSTInvoiceTxnDetails>();

			// Invoice Transaction Preparation
			GSTInvoiceTxn gstInvoiceTxn = new GSTInvoiceTxn();
			gstInvoiceTxn.setTransactionID(linkedTranId);
			gstInvoiceTxn.setInvoiceType(invoiceType);
			gstInvoiceTxn.setInvoice_Status(PennantConstants.GST_INVOICE_STATUS_INITIATED);
			gstInvoiceTxn.setInvoiceDate(DateUtility.getAppDate()); //Need to confirm either it is system date or application date
			Entity entity = null;
			if (StringUtils.isNotBlank(financeMain.getLovDescEntityCode())) {
				entity = getEntity(financeMain.getLovDescEntityCode());
			} else {
				entity = getEntityByFinDivision(financeType.getFinDivision(), "_Aview");
			}
			gstInvoiceTxn.setCompanyCode(entity.getEntityCode());
			gstInvoiceTxn.setCompanyName(entity.getEntityDesc());
			gstInvoiceTxn.setPanNumber(entity.getPANNumber());
			gstInvoiceTxn.setLoanAccountNo(finReference);

			// Check whether State level Tax Details exists or not
			Province companyProvince = getApprovedProvince(entity.getCountry(), entity.getStateCode());
			if (companyProvince != null) {

				if (StringUtils.isBlank(companyProvince.getCPProvince()) || StringUtils.isBlank(companyProvince.getCPProvinceName())) {
					return; //FIXME write this case as a error message
				}
				
				gstInvoiceTxn.setCompany_State_Code(companyProvince.getCPProvince());
				gstInvoiceTxn.setCompany_State_Name(companyProvince.getCPProvinceName());

				if (CollectionUtils.isNotEmpty(companyProvince.getTaxDetailList())) {
					TaxDetail taxDetail = companyProvince.getTaxDetailList().get(0);
					
					if (StringUtils.isBlank(taxDetail.getHsnNumber()) 
							|| StringUtils.isBlank(taxDetail.getNatureService())
							|| StringUtils.isBlank(taxDetail.getPinCode())
							|| StringUtils.isBlank(taxDetail.getAddressLine1())
							|| StringUtils.isBlank(taxDetail.getTaxCode())) {
						return;		//FIXME write this case as a error message
					}
					
					gstInvoiceTxn.setCompany_GSTIN(taxDetail.getTaxCode());
					gstInvoiceTxn.setCompany_Address1(taxDetail.getAddressLine1());
					gstInvoiceTxn.setCompany_Address2(taxDetail.getAddressLine2());
					gstInvoiceTxn.setCompany_Address3(taxDetail.getAddressLine3());
					gstInvoiceTxn.setCompany_PINCode(taxDetail.getPinCode());
					gstInvoiceTxn.setHsnNumber(taxDetail.getHsnNumber());
					gstInvoiceTxn.setNatureService(taxDetail.getNatureService());
				} else {
					return;		//FIXME write this case as a error message
				}
			}

			FinanceTaxDetail finTaxDetail = financeDetail.getFinanceTaxDetails();
			Province customerProvince =  null;
			String country = "";
			String province = "";
			
			// If tax Details Exists on against Finance
			if (finTaxDetail != null && StringUtils.isNotBlank(finTaxDetail.getApplicableFor()) && !PennantConstants.List_Select.equals(finTaxDetail.getApplicableFor())) {
				country = finTaxDetail.getCountry();
				province = finTaxDetail.getProvince();
				gstInvoiceTxn.setCustomerID(finTaxDetail.getCustCIF());
				gstInvoiceTxn.setCustomerName(finTaxDetail.getCustShrtName());
				gstInvoiceTxn.setCustomerGSTIN(finTaxDetail.getTaxNumber());	
				gstInvoiceTxn.setCustomerAddress(finTaxDetail.getAddrLine1());
			} else {
				
				if (financeDetail.getCustomerDetails() == null) {
					financeDetail.setCustomerDetails(this.customerDetailsService.getCustomerDetailsById(financeMain.getCustID(), true, "_AView"));
				}
				List<CustomerAddres> addressList = financeDetail.getCustomerDetails().getAddressList();
				if (CollectionUtils.isNotEmpty(addressList)) {
					for (CustomerAddres customerAddres : addressList) {
						if (customerAddres.getCustAddrPriority() != Integer.valueOf(PennantConstants.KYC_PRIORITY_VERY_HIGH)) {
							continue;
						}
						country = customerAddres.getCustAddrCountry();
						province = customerAddres.getCustAddrProvince();

						Customer cust =  financeDetail.getCustomerDetails().getCustomer();
						gstInvoiceTxn.setCustomerID(cust.getCustCIF()); 
						gstInvoiceTxn.setCustomerName(cust.getCustShrtName());

						String custAddress = "";

						if (StringUtils.isNotBlank(customerAddres.getCustAddrHNbr())) {
							custAddress = customerAddres.getCustAddrHNbr();
						}

						if (StringUtils.isNotBlank(customerAddres.getCustFlatNbr())) {
							if (StringUtils.isBlank(custAddress)) {
								custAddress = custAddress.concat(customerAddres.getCustFlatNbr());
							} else {
								custAddress = custAddress + ", " + customerAddres.getCustFlatNbr();
							}
						}

						if (StringUtils.isNotBlank(customerAddres.getCustAddrStreet())) {
							if (StringUtils.isBlank(custAddress)) {
								custAddress = custAddress.concat(customerAddres.getCustAddrStreet());
							} else {
								custAddress = custAddress + ", " + customerAddres.getCustAddrStreet();
							}
						}

						if (StringUtils.isNotBlank(customerAddres.getLovDescCustAddrCityName())) {
							if (StringUtils.isBlank(custAddress)) {
								custAddress = custAddress.concat(customerAddres.getLovDescCustAddrCityName());
							} else {
								custAddress = custAddress + ", " + customerAddres.getLovDescCustAddrCityName();
							}
						}

						if (StringUtils.isNotBlank(customerAddres.getLovDescCustAddrProvinceName())) {
							if (StringUtils.isBlank(custAddress)) {
								custAddress = custAddress.concat(customerAddres.getLovDescCustAddrProvinceName());
							} else {
								custAddress = custAddress + ", " + customerAddres.getLovDescCustAddrProvinceName();
							}
						}

						if (StringUtils.isNotBlank(customerAddres.getLovDescCustAddrCountryName())) {
							if (StringUtils.isBlank(custAddress)) {
								custAddress = custAddress.concat(customerAddres.getLovDescCustAddrCountryName());
							} else {
								custAddress = custAddress + ", " + customerAddres.getLovDescCustAddrCountryName();
							}
						}

						if (StringUtils.isNotBlank(customerAddres.getCustPOBox())) {
							if (StringUtils.isBlank(custAddress)) {
								custAddress = custAddress.concat(customerAddres.getCustPOBox());
							} else {
								custAddress = custAddress + ", " + customerAddres.getCustPOBox();
							}
						}

						gstInvoiceTxn.setCustomerAddress(custAddress);
						break;
					}
				}
			}
			
			customerProvince = getApprovedProvince(country, province);
			gstInvoiceTxn.setCustomerStateCode(customerProvince.getTaxStateCode());	
			gstInvoiceTxn.setCustomerStateName(customerProvince.getCPProvinceName());
			
			BigDecimal invoiceAmout = BigDecimal.ZERO;
			// Invoice Transaction details preparation for Fee Details if any exists
			if (CollectionUtils.isNotEmpty(finFeeDetailsList)) {	//Fees
				for (FinFeeDetail feeDetail : finFeeDetailsList) {
					if (feeDetail.isOriginationFee()) {
						continue;
					}
					FinTaxDetails finTaxDetails = feeDetail.getFinTaxDetails();
					if (finTaxDetails != null) {
						GSTInvoiceTxnDetails feeTran = new GSTInvoiceTxnDetails();
						feeTran.setFeeCode(feeDetail.getFeeTypeCode());
						feeTran.setFeeDescription(feeDetail.getFeeTypeDesc());
						feeTran.setFeeAmount(feeDetail.getNetAmountOriginal());	//Fee Amount with out GST
						feeTran.setCGST_RATE(feeDetail.getCgst());
						feeTran.setIGST_RATE(feeDetail.getIgst());
						feeTran.setSGST_RATE(feeDetail.getSgst());
						feeTran.setUGST_RATE(feeDetail.getUgst());
						feeTran.setCGST_AMT(finTaxDetails.getNetCGST());
						feeTran.setIGST_AMT(finTaxDetails.getNetIGST());
						feeTran.setSGST_AMT(finTaxDetails.getNetSGST());
						feeTran.setUGST_AMT(finTaxDetails.getNetUGST());
						invoiceAmout = invoiceAmout.add(finTaxDetails.getNetCGST()).add(finTaxDetails.getNetIGST()).add(
								finTaxDetails.getNetSGST()).add(finTaxDetails.getNetUGST());
						gstInvoiceTxnDetails.add(feeTran);
					}
				}
			
			// Invoice Transaction details preparation for Manual/Rceivable advise Details if any exists
			} else if (CollectionUtils.isNotEmpty(movements)) {	// Receivable Advise
				
				Map<String, BigDecimal> taxPercmap = getTaxPercentages(financeDetail);
				
				BigDecimal cgstPerc = taxPercmap.get(RuleConstants.CODE_CGST);
				BigDecimal sgstPerc = taxPercmap.get(RuleConstants.CODE_SGST);
				BigDecimal ugstPerc = taxPercmap.get(RuleConstants.CODE_UGST);
				BigDecimal igstPerc = taxPercmap.get(RuleConstants.CODE_IGST);
				
				for (ManualAdviseMovements movement: movements) {
					if (movement != null) {
						GSTInvoiceTxnDetails advTran = new GSTInvoiceTxnDetails();
						if (StringUtils.isBlank(movement.getFeeTypeCode())) {
							continue;
						}
						advTran.setFeeCode(movement.getFeeTypeCode());
						advTran.setFeeDescription(movement.getFeeTypeDesc());
						advTran.setFeeAmount(movement.getMovementAmount());	//Fee Amount with out GST
						advTran.setCGST_RATE(cgstPerc);
						advTran.setSGST_RATE(sgstPerc);
						advTran.setIGST_RATE(igstPerc);
						advTran.setUGST_RATE(ugstPerc);
						advTran.setCGST_AMT(movement.getPaidCGST()); 
						advTran.setIGST_AMT(movement.getPaidIGST());
						advTran.setSGST_AMT(movement.getPaidSGST());
						advTran.setUGST_AMT(movement.getPaidUGST());
						invoiceAmout = invoiceAmout.add(movement.getPaidCGST()).add(movement.getPaidIGST()).add(
								movement.getPaidSGST()).add(movement.getPaidUGST());
						gstInvoiceTxnDetails.add(advTran);
					}
				}
				
				if (CollectionUtils.isEmpty(gstInvoiceTxnDetails)) {	
					return;	//TODO check this case
				}
			}
			
			gstInvoiceTxn.setInvoice_Amt(invoiceAmout);
			gstInvoiceTxn.setGstInvoiceTxnDetailsList(gstInvoiceTxnDetails);
			
			save(gstInvoiceTxn);
		}
		
		logger.debug(Literal.LEAVING);
	}
	
	/**
	 * Method for Preparing all GST fee amounts based on configurations
	 * 
	 * @param manAdvList
	 * @param financeDetail
	 * @return
	 */
	private Map<String, BigDecimal> getTaxPercentages(FinanceDetail financeDetail) {
		logger.debug(Literal.ENTERING);

		// Map Preparation for Executing GST rules
		HashMap<String, Object> dataMap = finFeeDetailService.prepareGstMappingDetails(financeDetail, null);

		List<Rule> rules = ruleDAO.getGSTRuleDetails(RuleConstants.MODULE_GSTRULE, "");
		String finCcy = financeDetail.getFinScheduleData().getFinanceMain().getFinCcy();

		BigDecimal totalTaxPerc = BigDecimal.ZERO;
		Map<String, BigDecimal> taxPercMap = new HashMap<>();

		for (Rule rule : rules) {
			BigDecimal taxPerc = BigDecimal.ZERO;
			if (StringUtils.equals(RuleConstants.CODE_CGST, rule.getRuleCode())) {
				taxPerc = finFeeDetailService.getFeeResult(rule.getSQLRule(), dataMap, finCcy);
				totalTaxPerc = totalTaxPerc.add(taxPerc);
				taxPercMap.put(RuleConstants.CODE_CGST, taxPerc);
			} else if (StringUtils.equals(RuleConstants.CODE_IGST, rule.getRuleCode())) {
				taxPerc = finFeeDetailService.getFeeResult(rule.getSQLRule(), dataMap, finCcy);
				totalTaxPerc = totalTaxPerc.add(taxPerc);
				taxPercMap.put(RuleConstants.CODE_IGST, taxPerc);
			} else if (StringUtils.equals(RuleConstants.CODE_SGST, rule.getRuleCode())) {
				taxPerc = finFeeDetailService.getFeeResult(rule.getSQLRule(), dataMap, finCcy);
				totalTaxPerc = totalTaxPerc.add(taxPerc);
				taxPercMap.put(RuleConstants.CODE_SGST, taxPerc);
			} else if (StringUtils.equals(RuleConstants.CODE_UGST, rule.getRuleCode())) {
				taxPerc = finFeeDetailService.getFeeResult(rule.getSQLRule(), dataMap, finCcy);
				totalTaxPerc = totalTaxPerc.add(taxPerc);
				taxPercMap.put(RuleConstants.CODE_UGST, taxPerc);
			}
		}
		taxPercMap.put("TOTALGST", totalTaxPerc);

		logger.debug(Literal.LEAVING);

		return taxPercMap;
	}

	public void setGstInvoiceTxnDAO(GSTInvoiceTxnDAO gstInvoiceTxnDAO) {
		this.gstInvoiceTxnDAO = gstInvoiceTxnDAO;
	}

	public void setEntityService(EntityService entityService) {
		this.entityService = entityService;
	}

	public void setProvinceService(ProvinceService provinceService) {
		this.provinceService = provinceService;
	}

	public FinanceTaxDetailService getFinanceTaxDetailService() {
		return financeTaxDetailService;
	}

	public void setFinanceTaxDetailService(FinanceTaxDetailService financeTaxDetailService) {
		this.financeTaxDetailService = financeTaxDetailService;
	}

	public FinFeeDetailService getFinFeeDetailService() {
		return finFeeDetailService;
	}

	public void setFinFeeDetailService(FinFeeDetailService finFeeDetailService) {
		this.finFeeDetailService = finFeeDetailService;
	}

	public RuleDAO getRuleDAO() {
		return ruleDAO;
	}

	public void setRuleDAO(RuleDAO ruleDAO) {
		this.ruleDAO = ruleDAO;
	}

	public CustomerDetailsService getCustomerDetailsService() {
		return customerDetailsService;
	}

	public void setCustomerDetailsService(CustomerDetailsService customerDetailsService) {
		this.customerDetailsService = customerDetailsService;
	}
}
