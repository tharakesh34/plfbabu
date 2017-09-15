package com.pennanttech.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.backend.dao.bmtmasters.ProductDAO;
import com.pennant.backend.dao.finance.FinTypeVASProductsDAO;
import com.pennant.backend.model.bmtmasters.Product;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.rmtmasters.Promotion;
import com.pennant.backend.model.solutionfactory.StepPolicyHeader;
import com.pennant.backend.service.rmtmasters.FinanceTypeService;
import com.pennant.backend.service.rmtmasters.PromotionService;
import com.pennant.backend.service.solutionfactory.StepPolicyService;
import com.pennant.backend.util.FinanceConstants;
import com.pennanttech.ws.model.financetype.BasicDetail;
import com.pennanttech.ws.model.financetype.FinanceTypeRequest;
import com.pennanttech.ws.model.financetype.FinanceTypeResponse;
import com.pennanttech.ws.model.financetype.GraceDetail;
import com.pennanttech.ws.model.financetype.Insurance;
import com.pennanttech.ws.model.financetype.OverdueDetail;
import com.pennanttech.ws.model.financetype.OverdueProfitDetail;
import com.pennanttech.ws.model.financetype.ProductType;
import com.pennanttech.ws.model.financetype.PromotionType;
import com.pennanttech.ws.model.financetype.RepayDetail;
import com.pennanttech.ws.model.financetype.StepDetail;
import com.pennanttech.ws.service.APIErrorHandlerService;

public class FinanceTypeController {

	private static final Logger logger = Logger.getLogger(FinanceTypeController.class);

	private FinanceTypeService financeTypeService;
	private StepPolicyService stepPolicyService;
	private ProductDAO productDAO;
	private FinTypeVASProductsDAO finTypeVASProductsDAO;
	private PromotionService promotionService;
	
	
	public FinanceTypeResponse getFinanceTypeDetails(FinanceTypeRequest finTypeRequest, boolean isPromotion) {
		logger.debug("Entering");

		FinanceTypeResponse response = new FinanceTypeResponse();

		// fetch FinanceType details
		FinanceType financeType = financeTypeService.getApprovedFinanceTypeById(finTypeRequest.getFinType());

		if (isPromotion) {
			Promotion promotion = promotionService.getApprovedPromotionById(finTypeRequest.getPromotionType(),
					FinanceConstants.MODULEID_PROMOTION, true);
			financeType.setFInTypeFromPromotiion(promotion);
			financeType.setFinTypeFeesList(promotion.getFinTypeFeesList());
			financeType.setFinTypeInsurances(promotion.getFinTypeInsurancesList());
			financeType.setFinTypeAccountingList(promotion.getFinTypeAccountingList());
		}

		if (financeType != null) {
			response.setFinType(financeType.getFinType());
			response.setFinTypeDesc(financeType.getFinTypeDesc());
			response.setEndDate(financeType.getEndDate());
			response.setStartDate(financeType.getStartDate());
			// prepare FinanceType Basic details
			if (finTypeRequest.isBasicDetailReq()) {
				BasicDetail basicDetail = new BasicDetail();
				BeanUtils.copyProperties(financeType, basicDetail);
				response.setBasicDetail(basicDetail);
			}

			// prepare FinanceType Grace details
			if (finTypeRequest.isGrcDetailReq()) {
				GraceDetail graceDetail = new GraceDetail();
				BeanUtils.copyProperties(financeType, graceDetail);
				response.setGraceDetail(graceDetail);
			}

			// prepare FinanceType Repay details
			if (finTypeRequest.isRepayDetailReq()) {
				RepayDetail repayDetail = new RepayDetail();
				BeanUtils.copyProperties(financeType, repayDetail);
				response.setRepayDetail(repayDetail);
			}

			// prepare FinanceType Overdue details
			if (finTypeRequest.isOverdueDetailReq()) {
				OverdueDetail overdueDetail = new OverdueDetail();
				BeanUtils.copyProperties(financeType, overdueDetail);
				response.setOverdueDetail(overdueDetail);
			}

			// prepare FinanceType OverdueProfit details
			if (finTypeRequest.isOverdueProfitDetailReq()) {
				OverdueProfitDetail overduProfit = new OverdueProfitDetail();
				BeanUtils.copyProperties(financeType, overduProfit);
				response.setOverdueProfitDetail(overduProfit);
			}

			// prepare FinanceType Insurance details
			if (finTypeRequest.isInsuranceDetailReq()) {
				Insurance insurance = new Insurance();
				BeanUtils.copyProperties(financeType, insurance);
				response.setInsurance(insurance);
			}

			// prepare FinanceType Step details
			if (finTypeRequest.isStepDetailReq()) {
				StepDetail stepDetail = new StepDetail();
				BeanUtils.copyProperties(financeType, stepDetail);
				response.setStepDetail(stepDetail);
			}

			// prepare FinanceType Fee details
			if (finTypeRequest.isFeeReq()) {
				if (financeType.getFinTypeFeesList() != null) {
					response.setFinTypeFeesList(financeType.getFinTypeFeesList());
				}
			}
			response.setFinTypeVASProductsList(finTypeVASProductsDAO.getVASProductsByFinType(
					finTypeRequest.getFinType(), ""));
			
			if(finTypeRequest.isPartnerBankDetailReq()){
				response.setFinTypePartnerBankList(financeType.getFinTypePartnerBankList());
			}

			response.setReturnStatus(APIErrorHandlerService.getSuccessStatus());
		}

		logger.debug("Leaving");
		return response;
	}

	/**
	 * Method to fetch StepHeader and Policy details by given policyCode
	 * 
	 * @param policyCode
	 * @return
	 */
	public StepPolicyHeader getStepPolicyDetails(String policyCode) {
		logger.debug("Entering");
		
		// fetch StepHeader details
		StepPolicyHeader stepPolicyHeader = stepPolicyService.getStepPolicyHeaderById(policyCode);
		
		if(stepPolicyHeader == null) {
			StepPolicyHeader stepHeader = new StepPolicyHeader();
			stepHeader.setReturnStatus(APIErrorHandlerService.getFailedStatus());
			return stepHeader;
		}
		
		// add return status
		stepPolicyHeader.setReturnStatus(APIErrorHandlerService.getSuccessStatus());
		
		logger.debug("Leaving");
		return stepPolicyHeader;
	}
	
	/**
	 * Method to fetch ProductType details by given productCode
	 * 
	 * @param productCode
	 * @return
	 */
	public ProductType getLoanTypes(String productCode) {
		logger.debug("Entering");

		// fetch Product details
		ProductType response = new ProductType();
		try {
			Product product = productDAO.getProductByID(productCode, productCode, "_AView");
			if (product == null) {
				ProductType reponse = new ProductType();
				reponse.setReturnStatus(APIErrorHandlerService.getFailedStatus());
				return reponse;
			}
			response.setProductCode(productCode);
			response.setProductDesc(product.getProductDesc());

			// call the loantypeDetail
			List<FinanceType> financeTypeList = financeTypeService.getFinanceTypeByProduct(productCode);
			List<FinanceTypeResponse> financeTypeResponseList = new ArrayList<FinanceTypeResponse>();

			for (FinanceType detail : financeTypeList) {
				FinanceTypeResponse financeTypeResponse = new FinanceTypeResponse();
				financeTypeResponse.setFinType(detail.getFinType());
				financeTypeResponse.setFinTypeDesc(detail.getFinTypeDesc());
				financeTypeResponse.setFinTypePartnerBankList(null);
				financeTypeResponseList.add(financeTypeResponse);
			}
			response.setFinanceTypeList(financeTypeResponseList);

			// add return status
			response.setReturnStatus(APIErrorHandlerService.getSuccessStatus());
		} catch (Exception e) {
			logger.error("Exception", e);
			APIErrorHandlerService.logUnhandledException(e);
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus());
		}
		logger.debug("Leaving");
		return response;
	}

	/**
	 * 
	 * @param productCode
	 * @return
	 */
	public FinanceTypeResponse getPromotionsByFinType(String finType) {
		logger.debug("Entering");

		FinanceTypeResponse response = new FinanceTypeResponse();
		try {
			// fetch All promotions by FinanceType
			List<Promotion> promoton = promotionService.getPromotionsByFinType(finType, "_AView");
			List<PromotionType> promotions = new ArrayList<PromotionType>();

			for (Promotion detail : promoton) {
				PromotionType promotionType = new PromotionType();
				promotionType.setPromotionCode(detail.getPromotionCode());
				promotionType.setPromotionDesc(detail.getPromotionDesc());
				response.setFinType(detail.getFinType());
				response.setFinTypeDesc(detail.getFinTypeDesc());
				promotions.add(promotionType);
			}

			response.setPromotions(promotions);
			response.setReturnStatus(APIErrorHandlerService.getSuccessStatus());

		} catch (Exception e) {
			logger.error("Exception", e);
			APIErrorHandlerService.logUnhandledException(e);
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus());
		}

		logger.debug("Leaving");
		return response;
	}
	
	public void setFinanceTypeService(FinanceTypeService financeTypeService) {
		this.financeTypeService = financeTypeService;
	}

	public void setStepPolicyService(StepPolicyService stepPolicyService) {
		this.stepPolicyService = stepPolicyService;
	}
	
	public void setProductDAO(ProductDAO productDAO) {
		this.productDAO = productDAO;
	}

	public void setFinTypeVASProductsDAO(FinTypeVASProductsDAO finTypeVASProductsDAO) {
		this.finTypeVASProductsDAO = finTypeVASProductsDAO;
	}

	public void setPromotionService(PromotionService promotionService) {
		this.promotionService = promotionService;
	}
}
