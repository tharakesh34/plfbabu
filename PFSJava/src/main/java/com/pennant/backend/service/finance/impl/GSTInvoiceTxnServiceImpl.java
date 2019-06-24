package com.pennant.backend.service.finance.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.app.util.DateUtility;
import com.pennant.app.util.GSTCalculator;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.applicationmaster.BranchDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.GSTInvoiceTxnDAO;
import com.pennant.backend.model.applicationmaster.Branch;
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
import com.pennant.backend.model.systemmasters.Province;
import com.pennant.backend.service.applicationmaster.EntityService;
import com.pennant.backend.service.customermasters.CustomerDetailsService;
import com.pennant.backend.service.customermasters.CustomerService;
import com.pennant.backend.service.finance.FinanceTaxDetailService;
import com.pennant.backend.service.finance.GSTInvoiceTxnService;
import com.pennant.backend.service.systemmasters.ProvinceService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.RuleConstants;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennanttech.pennapps.core.resource.Literal;

public class GSTInvoiceTxnServiceImpl implements GSTInvoiceTxnService {
	private static final Logger logger = Logger.getLogger(GSTInvoiceTxnServiceImpl.class);

	private GSTInvoiceTxnDAO gstInvoiceTxnDAO;
	private EntityService entityService;
	private ProvinceService provinceService;

	// GST Invoice preparation
	private transient CustomerDetailsService customerDetailsService;
	private transient CustomerService customerService;
	private transient FinanceTaxDetailService financeTaxDetailService;
	private BranchDAO branchDAO;
	private FinanceMainDAO financeMainDAO;

	public GSTInvoiceTxnServiceImpl() {
		super();
	}

	/**
	 * preparing GST Invoice Details and insert data into GST Invoice related tables
	 * 
	 * @param aeEvent
	 * @param financeDetail
	 */
	@Override
	public void gstInvoicePreparation(long linkedTranId, FinanceDetail financeDetail,
			List<FinFeeDetail> finFeeDetailsList, List<ManualAdviseMovements> movements, String invoiceType,
			boolean origination, boolean isWaiver) {
		logger.debug(Literal.ENTERING);

		if (CollectionUtils.isEmpty(finFeeDetailsList) && CollectionUtils.isEmpty(movements) && financeDetail == null) {
			return;
		} else {

			BigDecimal invoiceAmout = BigDecimal.ZERO;
			List<GSTInvoiceTxnDetails> gstInvoiceTxnDetails = new ArrayList<GSTInvoiceTxnDetails>();
			FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();
			FinanceType financeType = financeDetail.getFinScheduleData().getFinanceType();
			String finReference = financeMain.getFinReference();

			// Tax Details fetching
			if (financeDetail.getFinanceTaxDetail() == null) {
				financeDetail.setFinanceTaxDetail(financeTaxDetailService.getApprovedFinanceTaxDetail(finReference));
			}

			// Invoice Transaction Preparation
			GSTInvoiceTxn gstInvoiceTxn = new GSTInvoiceTxn();
			gstInvoiceTxn.setTransactionID(linkedTranId);
			gstInvoiceTxn.setInvoiceType(invoiceType);
			gstInvoiceTxn.setInvoice_Status(PennantConstants.GST_INVOICE_STATUS_INITIATED);
			gstInvoiceTxn.setInvoiceDate(DateUtility.getAppDate()); //Need to confirm either it is system date or application date
			
			Entity entity = null;
			if (StringUtils.isNotBlank(financeMain.getLovDescEntityCode())) {
				entity = this.entityService.getApprovedEntity(financeMain.getLovDescEntityCode());
			} else {
				entity = this.entityService.getEntityByFinDivision(financeType.getFinDivision(), "_Aview");
			}

			if (entity == null) {
				return; // write this case as a error message
			}
			
			gstInvoiceTxn.setCompanyCode(entity.getEntityCode());
			gstInvoiceTxn.setCompanyName(entity.getEntityDesc());
			gstInvoiceTxn.setPanNumber(entity.getPANNumber());
			gstInvoiceTxn.setLoanAccountNo(finReference);

			// Checking Finance Branch exist or not
			if (StringUtils.isBlank(financeMain.getFinBranch())) {
				FinanceMain financeMainTemp = financeMainDAO.getFinanceMainForBatch(finReference);
				if (financeMainTemp == null || StringUtils.isBlank(financeMainTemp.getFinBranch())) {
					return; // write this case as a error message
				} else {
					financeMain.setFinBranch(financeMainTemp.getFinBranch());
				}
			}
			Branch fromBranch = branchDAO.getBranchById(financeMain.getFinBranch(), "_AView");

			if (fromBranch == null) {
				return; // write this case as a error message
			}

			Province companyProvince = this.provinceService.getApprovedProvinceById(fromBranch.getBranchCountry(), fromBranch.getBranchProvince());
			// Province companyProvince = this.provinceService.getApprovedProvinceById(entity.getCountry(), entity.getStateCode());

			if (companyProvince != null) {

				if (StringUtils.isBlank(companyProvince.getCPProvince())
						|| StringUtils.isBlank(companyProvince.getCPProvinceName())) {
					return; // write this case as a error message
				}

				if (CollectionUtils.isNotEmpty(companyProvince.getTaxDetailList())) {
					TaxDetail taxDetail = companyProvince.getTaxDetailList().get(0);

					if (SysParamUtil.isAllowed(SMTParameterConstants.INVOICE_ADDRESS_ENTITY_BASIS)) {

						if (StringUtils.isBlank(taxDetail.getHsnNumber())
								|| StringUtils.isBlank(taxDetail.getNatureService())
								|| StringUtils.isBlank(taxDetail.getPinCode())
								|| StringUtils.isBlank(taxDetail.getAddressLine1())
								|| StringUtils.isBlank(taxDetail.getTaxCode())) {
							return; // write this case as a error message
						}
						gstInvoiceTxn.setCompany_State_Code(companyProvince.getCPProvince());
						gstInvoiceTxn.setCompany_State_Name(companyProvince.getCPProvinceName());
						gstInvoiceTxn.setCompany_Address1(taxDetail.getAddressLine1());
						gstInvoiceTxn.setCompany_PINCode(taxDetail.getPinCode());
						gstInvoiceTxn.setCompany_Address2(taxDetail.getAddressLine2());
						gstInvoiceTxn.setCompany_Address3(taxDetail.getAddressLine3());
					} else {
						if (StringUtils.isBlank(taxDetail.getHsnNumber())
								|| StringUtils.isBlank(taxDetail.getNatureService())
								|| StringUtils.isBlank(fromBranch.getPinCode())
								|| StringUtils.isBlank(fromBranch.getBranchAddrHNbr())
								|| StringUtils.isBlank(taxDetail.getTaxCode())
								|| StringUtils.isBlank(fromBranch.getLovDescBranchProvinceName())) {
							return; // write this case as a error message
						}

						String address1 = fromBranch.getBranchAddrHNbr();

						if (StringUtils.isNotBlank(fromBranch.getBranchFlatNbr())) {
							address1 = address1 + ", " + fromBranch.getBranchFlatNbr();
						}
						if (StringUtils.isNotBlank(fromBranch.getBranchAddrStreet())) {
							address1 = address1 + ", " + fromBranch.getBranchAddrStreet();
						}

						gstInvoiceTxn.setCompany_Address1(address1);
						gstInvoiceTxn.setCompany_Address2(fromBranch.getBranchAddrLine1());
						gstInvoiceTxn.setCompany_Address3(fromBranch.getBranchAddrLine2());
						gstInvoiceTxn.setCompany_PINCode(fromBranch.getPinCode());
						gstInvoiceTxn.setCompany_State_Code(fromBranch.getBranchProvince());
						gstInvoiceTxn.setCompany_State_Name(fromBranch.getLovDescBranchProvinceName());
					}

					gstInvoiceTxn.setCompany_GSTIN(taxDetail.getTaxCode());
					gstInvoiceTxn.setHsnNumber(taxDetail.getHsnNumber());
					gstInvoiceTxn.setNatureService(taxDetail.getNatureService());
				} else {
					return; // write this case as a error message
				}
			}

			FinanceTaxDetail finTaxDetail = financeDetail.getFinanceTaxDetail();
			Province customerProvince = null;
			String country = "";
			String province = "";

			// If tax Details Exists on against Finance
			if (finTaxDetail != null && StringUtils.isNotBlank(finTaxDetail.getApplicableFor())
					&& !PennantConstants.List_Select.equals(finTaxDetail.getApplicableFor())) {
				country = finTaxDetail.getCountry();
				province = finTaxDetail.getProvince();
				gstInvoiceTxn.setCustomerID(finTaxDetail.getCustCIF());
				gstInvoiceTxn.setCustomerName(finTaxDetail.getCustShrtName());
				gstInvoiceTxn.setCustomerGSTIN(finTaxDetail.getTaxNumber());
				gstInvoiceTxn.setCustomerAddress(finTaxDetail.getAddrLine1());
			} else {

				if (financeDetail.getCustomerDetails() == null) {
					financeDetail.setCustomerDetails(this.customerDetailsService
							.getCustomerDetailsById(financeMain.getCustID(), true, "_AView"));
				}
				List<CustomerAddres> addressList = financeDetail.getCustomerDetails().getAddressList();
				if (CollectionUtils.isNotEmpty(addressList)) {
					for (CustomerAddres customerAddres : addressList) {
						if (customerAddres.getCustAddrPriority() != Integer
								.valueOf(PennantConstants.KYC_PRIORITY_VERY_HIGH)) {
							continue;
						}
						country = customerAddres.getCustAddrCountry();
						province = customerAddres.getCustAddrProvince();

						Customer cust = financeDetail.getCustomerDetails().getCustomer();
						if (cust == null) {
							cust = this.customerService.getApprovedCustomerById(financeMain.getCustID());
						}

						if (cust == null) {
							return; // write this case as a error message
						}
						gstInvoiceTxn.setCustomerID(cust.getCustCIF());
						gstInvoiceTxn.setCustomerName(cust.getCustShrtName());
						
						//Preparing customer Address
						prepareCustAddress(gstInvoiceTxn, customerAddres);
						
						break;
					}
				}
			}

			customerProvince = this.provinceService.getApprovedProvinceById(country, province);

			if (customerProvince == null) {
				return; // write this case as a error message
			}

			gstInvoiceTxn.setCustomerStateCode(customerProvince.getTaxStateCode());
			gstInvoiceTxn.setCustomerStateName(customerProvince.getCPProvinceName());

			// Invoice Transaction details preparation for Fee Details if any exists
			if (CollectionUtils.isNotEmpty(finFeeDetailsList)) { // Fees
				for (FinFeeDetail feeDetail : finFeeDetailsList) {
					
					if (!feeDetail.isTaxApplicable() || StringUtils.isBlank(feeDetail.getFeeTypeCode())) {
						continue;
					}

					if (origination) {
						if ((!isWaiver && BigDecimal.ZERO.compareTo(feeDetail.getNetAmountOriginal()) == 0) ||
								(isWaiver && BigDecimal.ZERO.compareTo(feeDetail.getWaivedAmount()) == 0)) {
							continue;
						}
					} else {
						if (feeDetail.isOriginationFee()) {
							continue;
						}
						if ((!isWaiver && BigDecimal.ZERO.compareTo(feeDetail.getNetAmountOriginal()) == 0)
								|| (isWaiver && BigDecimal.ZERO.compareTo(feeDetail.getWaivedAmount()) == 0)) {
							continue;
						}
					}
					FinTaxDetails finTaxDetails = feeDetail.getFinTaxDetails();
					if (finTaxDetails != null) {
						
						GSTInvoiceTxnDetails feeTran = new GSTInvoiceTxnDetails();
						feeTran.setFeeCode(feeDetail.getFeeTypeCode());
						feeTran.setFeeDescription(feeDetail.getFeeTypeDesc());
						feeTran.setCGST_RATE(feeDetail.getCgst());
						feeTran.setIGST_RATE(feeDetail.getIgst());
						feeTran.setSGST_RATE(feeDetail.getSgst());
						feeTran.setUGST_RATE(feeDetail.getUgst());
						
						if (isWaiver) {
							BigDecimal gstAmount = finTaxDetails.getWaivedCGST().add(finTaxDetails.getWaivedIGST()).add(finTaxDetails.getWaivedSGST()).add(finTaxDetails.getWaivedUGST());
							if (gstAmount.compareTo(BigDecimal.ZERO) <= 0) {
								continue;
							}
							if (FinanceConstants.FEE_TAXCOMPONENT_INCLUSIVE.equals(feeDetail.getTaxComponent())) {
								feeTran.setFeeAmount(feeDetail.getWaivedAmount().subtract(gstAmount)); //Fee Amount with out GST
							} else {
								feeTran.setFeeAmount(feeDetail.getWaivedAmount()); //Fee Amount with out GST
							}
							feeTran.setCGST_AMT(finTaxDetails.getWaivedCGST());
							feeTran.setIGST_AMT(finTaxDetails.getWaivedIGST());
							feeTran.setSGST_AMT(finTaxDetails.getWaivedSGST());
							feeTran.setUGST_AMT(finTaxDetails.getWaivedUGST());
							invoiceAmout = invoiceAmout.add(gstAmount);
						} else {
							BigDecimal gstAmount = finTaxDetails.getNetCGST().add(finTaxDetails.getNetIGST()).add(finTaxDetails.getNetSGST()).add(finTaxDetails.getNetUGST());
							if (gstAmount.compareTo(BigDecimal.ZERO) <= 0) {
								continue;
							}
							feeTran.setFeeAmount(feeDetail.getNetAmountOriginal()); //Fee Amount with out GST
							feeTran.setCGST_AMT(finTaxDetails.getNetCGST());
							feeTran.setIGST_AMT(finTaxDetails.getNetIGST());
							feeTran.setSGST_AMT(finTaxDetails.getNetSGST());
							feeTran.setUGST_AMT(finTaxDetails.getNetUGST());
							invoiceAmout = invoiceAmout.add(gstAmount);
						}
						
						gstInvoiceTxnDetails.add(feeTran);
					}
				}

				// Invoice Transaction details preparation for Manual/Receivable advise Details if any exists
			} else if (CollectionUtils.isNotEmpty(movements)) { // Receivable Advise
				Map<String, BigDecimal> taxPercmap = GSTCalculator.getTaxPercentages(finReference);

				BigDecimal cgstPerc = taxPercmap.get(RuleConstants.CODE_CGST);
				BigDecimal sgstPerc = taxPercmap.get(RuleConstants.CODE_SGST);
				BigDecimal ugstPerc = taxPercmap.get(RuleConstants.CODE_UGST);
				BigDecimal igstPerc = taxPercmap.get(RuleConstants.CODE_IGST);

				for (ManualAdviseMovements movement : movements) {
					
					if (movement != null && StringUtils.isNotBlank(movement.getFeeTypeCode())) {
					
						GSTInvoiceTxnDetails advTran = new GSTInvoiceTxnDetails();
						advTran.setFeeCode(movement.getFeeTypeCode());
						advTran.setFeeDescription(movement.getFeeTypeDesc());
						advTran.setCGST_RATE(cgstPerc);
						advTran.setSGST_RATE(sgstPerc);
						advTran.setIGST_RATE(igstPerc);
						advTran.setUGST_RATE(ugstPerc);
						
						if (isWaiver) {
							
							BigDecimal gstAmount = movement.getWaivedCGST().add(movement.getWaivedIGST()).add(movement.getWaivedSGST()).add(movement.getWaivedUGST());
							
							if (BigDecimal.ZERO.compareTo(movement.getWaivedAmount()) == 0 || BigDecimal.ZERO.compareTo(gstAmount) == 0) {
								continue;
							}
							
							if (FinanceConstants.FEE_TAXCOMPONENT_INCLUSIVE.equals(movement.getTaxComponent())) {
								advTran.setFeeAmount(movement.getWaivedAmount().subtract(gstAmount)); //Fee Amount with out GST
							} else {
								advTran.setFeeAmount(movement.getWaivedAmount()); //Fee Amount with out GST
							}
							
							advTran.setCGST_AMT(movement.getWaivedCGST());
							advTran.setIGST_AMT(movement.getWaivedIGST());
							advTran.setSGST_AMT(movement.getWaivedSGST());
							advTran.setUGST_AMT(movement.getWaivedUGST());
							
							invoiceAmout = invoiceAmout.add(gstAmount);
						} else {
							
							BigDecimal gstAmount = movement.getPaidCGST().add(movement.getPaidIGST()).add(movement.getPaidSGST()).add(movement.getPaidUGST());
							
							if (BigDecimal.ZERO.compareTo(movement.getPaidAmount()) == 0 || BigDecimal.ZERO.compareTo(gstAmount) == 0) {
								continue;
							}
							
							if (FinanceConstants.FEE_TAXCOMPONENT_INCLUSIVE.equals(movement.getTaxComponent())) {
								advTran.setFeeAmount(movement.getPaidAmount().subtract(gstAmount)); //Fee Amount with out GST
							} else {
								advTran.setFeeAmount(movement.getPaidAmount()); //Fee Amount with out GST
							}
							
							advTran.setCGST_AMT(movement.getPaidCGST());
							advTran.setIGST_AMT(movement.getPaidIGST());
							advTran.setSGST_AMT(movement.getPaidSGST());
							advTran.setUGST_AMT(movement.getPaidUGST());
							
							invoiceAmout = invoiceAmout.add(gstAmount);
						}
						
						gstInvoiceTxnDetails.add(advTran);
					}
				}
			}

			if (CollectionUtils.isEmpty(gstInvoiceTxnDetails)) {
				return;
			}

			gstInvoiceTxn.setInvoice_Amt(invoiceAmout);
			gstInvoiceTxn.setGstInvoiceTxnDetailsList(gstInvoiceTxnDetails);

			this.gstInvoiceTxnDAO.save(gstInvoiceTxn);
		}

		logger.debug(Literal.LEAVING);
	}

	private String prepareCustAddress(GSTInvoiceTxn gstInvoiceTxn, CustomerAddres customerAddres) {
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
		return custAddress;
	}

	@Override
	public void createProfitScheduleInovice(long linkedTranId, FinanceDetail financeDetail, BigDecimal invoiceAmout, String invoiceType) {
		logger.debug(Literal.ENTERING);

		String feeCode = SysParamUtil.getValueAsString(PennantConstants.FEETYPE_EXEMPTED);

		if (StringUtils.isBlank(feeCode) || financeDetail == null) {
			return;
		}

		List<GSTInvoiceTxnDetails> gstInvoiceTxnDetails = new ArrayList<GSTInvoiceTxnDetails>();
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();
		FinanceType financeType = financeDetail.getFinScheduleData().getFinanceType();
		String finReference = financeMain.getFinReference();

		if (StringUtils.isBlank(finReference) || financeMain == null || financeType == null) {
			return; // write this case as a error message
		}
		
		// Tax Details fetching
		if (financeDetail.getFinanceTaxDetail() == null) {
			financeDetail.setFinanceTaxDetail(financeTaxDetailService.getApprovedFinanceTaxDetail(finReference));
		}

		// Invoice Transaction Preparation
		GSTInvoiceTxn gstInvoiceTxn = new GSTInvoiceTxn();
		gstInvoiceTxn.setTransactionID(linkedTranId);
		gstInvoiceTxn.setInvoiceType(invoiceType);
		gstInvoiceTxn.setInvoice_Status(PennantConstants.GST_INVOICE_STATUS_INITIATED);
		gstInvoiceTxn.setInvoiceDate(DateUtility.getAppDate());
		Entity entity = null;
		if (StringUtils.isNotBlank(financeMain.getLovDescEntityCode())) {
			entity = this.entityService.getApprovedEntity(financeMain.getLovDescEntityCode());
		} else {
			entity = this.entityService.getEntityByFinDivision(financeType.getFinDivision(), "_Aview");
		}

		if (entity == null) {
			return; // write this case as a error message
		}
		gstInvoiceTxn.setCompanyCode(entity.getEntityCode());
		gstInvoiceTxn.setCompanyName(entity.getEntityDesc());
		gstInvoiceTxn.setPanNumber(entity.getPANNumber());
		gstInvoiceTxn.setLoanAccountNo(finReference);

		// Checking Finance Branch exist or not 
		if (StringUtils.isBlank(financeMain.getFinBranch())) {
			FinanceMain financeMainTemp = financeMainDAO.getFinanceMainForBatch(finReference);
			if (financeMainTemp == null || StringUtils.isBlank(financeMainTemp.getFinBranch())) {
				return; // write this case as a error message
			} else {
				financeMain.setFinBranch(financeMainTemp.getFinBranch());
			}
		}
		Branch fromBranch = branchDAO.getBranchById(financeMain.getFinBranch(), "");

		if (fromBranch == null) {
			return; // write this case as a error message
		}

		// Check whether State level Tax Details exists or not
		Province companyProvince = this.provinceService.getApprovedProvinceById(fromBranch.getBranchCountry(),
				fromBranch.getBranchProvince());

		if (companyProvince != null) {

			if (StringUtils.isBlank(companyProvince.getCPProvince())
					|| StringUtils.isBlank(companyProvince.getCPProvinceName())) {
				return; // write this case as a error message
			}

			gstInvoiceTxn.setCompany_State_Code(companyProvince.getCPProvince());
			gstInvoiceTxn.setCompany_State_Name(companyProvince.getCPProvinceName());

			if (CollectionUtils.isNotEmpty(companyProvince.getTaxDetailList())) {
				TaxDetail taxDetail = companyProvince.getTaxDetailList().get(0);

				if (StringUtils.isBlank(taxDetail.getHsnNumber()) || StringUtils.isBlank(taxDetail.getNatureService())
						|| StringUtils.isBlank(taxDetail.getPinCode())
						|| StringUtils.isBlank(taxDetail.getAddressLine1())
						|| StringUtils.isBlank(taxDetail.getTaxCode())) {
					return; // write this case as a error message
				}

				gstInvoiceTxn.setCompany_GSTIN(taxDetail.getTaxCode());
				gstInvoiceTxn.setCompany_Address1(taxDetail.getAddressLine1());
				gstInvoiceTxn.setCompany_Address2(taxDetail.getAddressLine2());
				gstInvoiceTxn.setCompany_Address3(taxDetail.getAddressLine3());
				gstInvoiceTxn.setCompany_PINCode(taxDetail.getPinCode());
				gstInvoiceTxn.setHsnNumber(taxDetail.getHsnNumber());
				gstInvoiceTxn.setNatureService(taxDetail.getNatureService());
			} else {
				return; // write this case as a error message
			}
		}

		FinanceTaxDetail finTaxDetail = financeDetail.getFinanceTaxDetail();
		Province customerProvince = null;
		String country = "";
		String province = "";

		// If tax Details Exists on against Finance
		if (finTaxDetail != null && StringUtils.isNotBlank(finTaxDetail.getApplicableFor())
				&& !PennantConstants.List_Select.equals(finTaxDetail.getApplicableFor())) {
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

					Customer cust = financeDetail.getCustomerDetails().getCustomer();
					if (cust == null) {
						cust = this.customerService.getApprovedCustomerById(financeMain.getCustID());
					}

					if (cust == null) {
						return; // write this case as a error message
					}
					gstInvoiceTxn.setCustomerID(cust.getCustCIF());
					gstInvoiceTxn.setCustomerName(cust.getCustShrtName());

					//Preparing customer Address
					prepareCustAddress(gstInvoiceTxn, customerAddres);
					
					break;
				}
			}
		}

		customerProvince = this.provinceService.getApprovedProvinceById(country, province);

		if (customerProvince == null) {
			return; // write this case as a error message
		}

		gstInvoiceTxn.setCustomerStateCode(customerProvince.getTaxStateCode());
		gstInvoiceTxn.setCustomerStateName(customerProvince.getCPProvinceName());
		gstInvoiceTxn.setInvoice_Amt(invoiceAmout);

		GSTInvoiceTxnDetails feeTran = new GSTInvoiceTxnDetails();
		feeTran.setFeeCode(feeCode);
		feeTran.setFeeDescription(feeCode);
		feeTran.setFeeAmount(invoiceAmout);
		feeTran.setCGST_RATE(BigDecimal.ZERO);
		feeTran.setIGST_RATE(BigDecimal.ZERO);
		feeTran.setSGST_RATE(BigDecimal.ZERO);
		feeTran.setUGST_RATE(BigDecimal.ZERO);
		feeTran.setCGST_AMT(BigDecimal.ZERO);
		feeTran.setIGST_AMT(BigDecimal.ZERO);
		feeTran.setSGST_AMT(BigDecimal.ZERO);
		feeTran.setUGST_AMT(BigDecimal.ZERO);
		gstInvoiceTxnDetails.add(feeTran);

		gstInvoiceTxn.setGstInvoiceTxnDetailsList(gstInvoiceTxnDetails);

		this.gstInvoiceTxnDAO.save(gstInvoiceTxn);

		logger.debug(Literal.LEAVING);
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

	public void setFinanceTaxDetailService(FinanceTaxDetailService financeTaxDetailService) {
		this.financeTaxDetailService = financeTaxDetailService;
	}

	public void setCustomerDetailsService(CustomerDetailsService customerDetailsService) {
		this.customerDetailsService = customerDetailsService;
	}

	public void setCustomerService(CustomerService customerService) {
		this.customerService = customerService;
	}

	public void setBranchDAO(BranchDAO branchDAO) {
		this.branchDAO = branchDAO;
	}

	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}
}
