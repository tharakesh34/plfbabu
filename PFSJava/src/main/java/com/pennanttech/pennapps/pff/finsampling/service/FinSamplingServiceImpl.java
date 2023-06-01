package com.pennanttech.pennapps.pff.finsampling.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.util.resource.Labels;

import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.extendedfield.ExtendedFieldData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.util.ExtendedFieldConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.pff.finsampling.dao.FinSamplingDAO;
import com.pennanttech.pennapps.pff.sampling.dao.SamplingDAO;
import com.pennanttech.pennapps.pff.sampling.model.Sampling;
import com.pennanttech.pennapps.pff.sampling.model.SamplingCollateral;
import com.pennanttech.pennapps.pff.sampling.model.SamplingDetail;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.sampling.Decision;
import com.pennanttech.pff.service.sampling.SamplingService;

public class FinSamplingServiceImpl implements FinSamplingService {
	private static final Logger logger = LogManager.getLogger(FinSamplingServiceImpl.class);

	@Autowired
	private FinSamplingDAO finSamplingDAO;

	@Autowired
	private SamplingService samplingService;

	@Autowired
	private SamplingDAO samplingDAO;

	@Override
	public AuditDetail saveOrUpdate(FinanceDetail financeDetail, String auditTranType) {
		logger.debug(Literal.ENTERING);
		Sampling sampling = financeDetail.getSampling();

		String[] fields = PennantJavaUtil.getFieldDetails(sampling, sampling.getExcludeFields());
		if (finSamplingDAO.isSnapExist(sampling.getId())) {
			logger.warn("", new AppException("Snap avilable for sampling details, further updatation not allowed."));
			return new AuditDetail(auditTranType, 1, fields[0], fields[1], sampling.getBefImage(), sampling);
		}

		Sampling samplingRemarks = new Sampling();
		BeanUtils.copyProperties(sampling, samplingRemarks);

		finSamplingDAO.updateSampling(sampling, TableType.MAIN_TAB);

		if (Decision.RESUBMIT.getKey().equals(sampling.getDecision())
				&& !samplingService.isExist(sampling.getKeyReference(), "_temp")) {
			samplingService.saveOnReSubmit(sampling);
		} else if (sampling.getDecision() == Decision.CREDITCAM.getKey() && !financeDetail.isActionSave()) {
			sampling.setUserDetails(financeDetail.getUserDetails());
			samplingService.saveSnap(sampling);
		}

		finSamplingDAO.saveOrUpdateRemarks(samplingRemarks, TableType.MAIN_TAB);
		samplingDAO.update(sampling, TableType.MAIN_TAB);
		logger.debug(Literal.LEAVING);
		return new AuditDetail(auditTranType, 1, fields[0], fields[1], sampling.getBefImage(), sampling);
	}

	public Sampling getSamplingDetails(String finReference, String type) {
		logger.debug(Literal.ENTERING);
		Sampling sampling = samplingService.getSampling(finReference, type);
		return calculateVariance(sampling);
	}

	public Sampling calculateVariance(Sampling sampling) {
		BigDecimal varaiance;
		BigDecimal original;
		BigDecimal current;
		SamplingDetail sd;

		if (sampling == null) {
			return null;
		}
		int formatter = sampling.getCcyeditfield();
		List<SamplingDetail> sdList = sampling.getSamplingDetailsList();
		sdList.clear();
		original = sampling.getOriginalTotalIncome();
		current = sampling.getTotalIncome();

		if (original == null) {
			original = BigDecimal.ZERO;
		}

		if (current == null) {
			current = BigDecimal.ZERO;
		}

		sd = new SamplingDetail();
		sd.setParameter(Labels.getLabel("label_FinSampling_Income.value"));
		sd.setBranchCam(PennantApplicationUtil.amountFormate(original, formatter));
		sd.setCreditCam(PennantApplicationUtil.amountFormate(current, formatter));

		varaiance = getVariance(original, current);
		if (varaiance.compareTo(new BigDecimal(25)) >= 0) {
			sampling.setSamplingTolerance("Out of Tolerance");
		} else {
			sampling.setSamplingTolerance("Tolerance");
		}

		if (original == BigDecimal.ZERO) {
			sd.setVariance(PennantApplicationUtil.amountFormate(varaiance, formatter));
		} else {
			sd.setVariance(String.valueOf(varaiance));
		}
		sd.setRemarks(String.valueOf(sampling.getReamrksMap().get("INCOME")));
		sd.setRemarksId("INCOME");
		sdList.add(sd);

		original = sampling.getOriginalTotalLiability();
		current = sampling.getTotalLiability();

		if (original == null) {
			original = BigDecimal.ZERO;
		}

		if (current == null) {
			current = BigDecimal.ZERO;
		}

		sd = new SamplingDetail();
		sd.setParameter(Labels.getLabel("label_FinSampling_Obligation.value"));
		sd.setBranchCam(PennantApplicationUtil.amountFormate(original, formatter));
		sd.setCreditCam(PennantApplicationUtil.amountFormate(current, formatter));
		sd.setVariance(getVariance(original, current, formatter));
		sd.setRemarks(String.valueOf(sampling.getReamrksMap().get("LIABILITY")));
		sd.setRemarksId("LIABILITY");
		sdList.add(sd);

		for (SamplingCollateral coll : sampling.getCollaterals()) {
			Map<String, List<ExtendedFieldData>> collaterals;
			String collateralType = coll.getCollateralType();
			String collateralRef = coll.getCollateralRef();
			String sequence = String.valueOf(coll.getSeqNo());
			long originallinkId = samplingService.getCollateralLinkId(collateralRef, sampling.getId(), "");
			long sanpLinkId = samplingService.getCollateralLinkId(collateralRef, sampling.getId(), "_snap");

			String linkId = "S".concat(String.valueOf(originallinkId));
			String snaplinkIdStr;
			if (sanpLinkId > 0) {
				snaplinkIdStr = String.valueOf(sanpLinkId);
				collaterals = samplingService.getCollateralFields(collateralType, linkId, snaplinkIdStr);
			} else {
				collaterals = samplingService.getCollateralFields(collateralType, linkId, collateralRef);
			}

			if (MapUtils.isNotEmpty(collaterals)) {
				sd = new SamplingDetail();
				sd.setCaption(String.format("%s - %s - %s", collateralRef, collateralType, sequence));
				sdList.add(sd);
			}

			for (Entry<String, List<ExtendedFieldData>> field : collaterals.entrySet()) {
				List<ExtendedFieldData> list = field.getValue();
				ExtendedFieldData originalField = list.get(0);
				ExtendedFieldData currentField = list.get(1);
				String fieldType = currentField.getFieldType();
				String key = collateralRef.concat("_").concat(currentField.getFieldName()).concat("_").concat(sequence);

				sd = new SamplingDetail();
				sd.setParameter(originalField.getFieldLabel());

				switch (fieldType) {
				case ExtendedFieldConstants.FIELDTYPE_TEXT:
				case ExtendedFieldConstants.FIELDTYPE_MULTILINETEXT:
				case ExtendedFieldConstants.FIELDTYPE_UPPERTEXT:
				case ExtendedFieldConstants.FIELDTYPE_STATICCOMBO:
					sd.setBranchCam(getStringValue(originalField));
					sd.setCreditCam(getStringValue(currentField));
					sd.setVariance("");
					sd.setAlignLeft(true);
					break;

				case ExtendedFieldConstants.FIELDTYPE_ACTRATE:
				case ExtendedFieldConstants.FIELDTYPE_DECIMAL:
				case ExtendedFieldConstants.FIELDTYPE_PERCENTAGE:
				case ExtendedFieldConstants.FIELDTYPE_AMOUNT:
					sd.setBranchCam(PennantApplicationUtil.amountFormate(getBigDecimal(originalField), formatter));
					sd.setCreditCam(PennantApplicationUtil.amountFormate(getBigDecimal(currentField), formatter));
					sd.setVariance(getVariance(getBigDecimal(originalField), getBigDecimal(currentField), formatter));
					break;

				case ExtendedFieldConstants.FIELDTYPE_INT:
					sd.setBranchCam(getIntValue(originalField));
					sd.setCreditCam(getIntValue(currentField));
					sd.setVariance(getVariance(getInt(originalField), getInt(currentField)));
					break;
				case ExtendedFieldConstants.FIELDTYPE_LONG:
					sd.setBranchCam(getLongValue(originalField));
					sd.setCreditCam(getLongValue(currentField));
					sd.setVariance(getVariance(getLong(originalField), getLong(currentField)));
					break;
				}
				sd.setRemarks(String.valueOf(sampling.getReamrksMap().get(key)));
				sd.setRemarksId(key);
				sdList.add(sd);
			}
		}

		original = sampling.getOriginalLoanEligibility();
		current = sampling.getLoanEligibility();

		if (original == null) {
			original = BigDecimal.ZERO;
		}

		if (current == null) {
			current = BigDecimal.ZERO;
		}
		sd = new SamplingDetail();
		sd.setParameter(Labels.getLabel("label_FinSampling_FinalRcmdAmt.value"));
		sd.setBranchCam(PennantApplicationUtil.amountFormate(original, formatter));
		sd.setCreditCam(PennantApplicationUtil.amountFormate(current, formatter));
		sd.setVariance(getVariance(original, current, formatter));
		sd.setRemarks(String.valueOf(sampling.getReamrksMap().get("RECOMMENDEDAMTREMARKS")));
		sd.setRemarksId("RECOMMENDEDAMTREMARKS");
		sdList.add(sd);

		logger.debug(Literal.LEAVING);
		return sampling;
	}

	private String getVariance(BigDecimal original, BigDecimal current, int formatter) {
		BigDecimal variance = (current.subtract(original));

		if (BigDecimal.ZERO.compareTo(original) == 0) {
			return PennantApplicationUtil.amountFormate(variance.abs(), formatter);
		}

		variance = variance.divide(original, formatter, RoundingMode.HALF_DOWN);
		variance = variance.multiply(new BigDecimal(100));
		return String.valueOf(variance.abs());
	}

	private BigDecimal getVariance(BigDecimal original, BigDecimal current) {
		BigDecimal variance = (current.subtract(original));

		if (BigDecimal.ZERO.compareTo(original) == 0) {
			return variance.abs();
		}
		variance = variance.divide(original, 2, RoundingMode.HALF_DOWN);
		variance = variance.multiply(new BigDecimal(100));
		return variance.abs();
	}

	private String getStringValue(ExtendedFieldData data) {
		Object object = data.getFieldValue();
		return object == null ? "" : object.toString();
	}

	private BigDecimal getBigDecimal(ExtendedFieldData data) {
		Object object = data.getFieldValue();
		if (object == null) {
			object = BigDecimal.ZERO;
		} else if (!StringUtils.isNumeric(object.toString())) {
			object = BigDecimal.ZERO;
		}

		return new BigDecimal(object.toString());
	}

	private Long getLong(ExtendedFieldData data) {
		Object object = data.getFieldValue();
		if (object == null) {
			object = "0";
		} else if (!StringUtils.isNumeric(object.toString())) {
			object = "0";
		} else {
			object = object.toString();
		}

		return Long.parseLong(object.toString());
	}

	private Integer getInt(ExtendedFieldData data) {
		Object object = data.getFieldValue();
		if (object == null) {
			object = "0";
		} else if (!StringUtils.isNumeric(object.toString())) {
			object = "0";
		} else {
			object = object.toString();
		}

		return Integer.parseInt(object.toString());
	}

	private String getVariance(Integer original, Integer current) {
		Integer variance = (current - original);
		if (original == 0) {
			return String.valueOf(Math.abs(variance));
		}
		variance = ((variance * (100) / original));

		return String.valueOf(Math.abs(variance));
	}

	private String getVariance(Long original, Long current) {
		Long variance = (current - original);
		if (original == 0) {
			return String.valueOf(Math.abs(variance));
		}
		variance = variance / original;
		variance = variance * (100);
		return String.valueOf(Math.abs(variance));
	}

	private String getIntValue(ExtendedFieldData data) {
		Object object = data.getFieldValue();
		if (object == null) {
			return "0";
		} else if (!StringUtils.isNumeric(object.toString())) {
			return "0";
		} else {
			return object.toString();
		}
	}

	private String getLongValue(ExtendedFieldData data) {
		Object object = data.getFieldValue();
		if (object == null) {
			return "0";
		} else if (!StringUtils.isNumeric(object.toString())) {
			return "0";
		} else {
			return object.toString();
		}
	}
}
