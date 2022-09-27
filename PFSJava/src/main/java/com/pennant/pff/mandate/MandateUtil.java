package com.pennant.pff.mandate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.zkoss.util.resource.Labels;

import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.util.MandateConstants;
import com.pennanttech.extension.FeatureExtension;

public class MandateUtil {

	private MandateUtil() {
		super();
	}

	private static List<ValueLabel> instrumentTypes;
	private static List<ValueLabel> repaymentMethods;
	private static List<ValueLabel> mandateStatus;
	private static List<ValueLabel> chequeTypesList;

	public static List<ValueLabel> getInstrumentTypes() {
		if (instrumentTypes != null) {
			return instrumentTypes;
		}

		instrumentTypes = new ArrayList<>(4);

		for (InstrumentType item : InstrumentType.values()) {
			switch (item) {
			case NACH:
			case ECS:
			case ENACH:
			case EMANDATE:
			case PDC:
			case SI:
			case DAS:
				instrumentTypes.add(new ValueLabel(item.name(), item.code()));
				break;

			default:
				continue;
			}
		}

		return instrumentTypes;
	}

	public static List<ValueLabel> excludeRepayMethods(String... instrumentTypes) {
		List<ValueLabel> repayMethods = new ArrayList<ValueLabel>(getRepayMethods());
		List<String> instrumentCodes = Arrays.asList(instrumentTypes);

		repayMethods.removeIf(x -> instrumentCodes.contains(x.getValue()));

		return repayMethods;
	}

	public static List<ValueLabel> getRepayMethods() {
		if (repaymentMethods != null) {
			return repaymentMethods;
		}

		repaymentMethods = new ArrayList<>(4);

		for (InstrumentType item : InstrumentType.values()) {
			switch (item) {
			case NACH:
			case ECS:
			case ENACH:
			case EMANDATE:
			case PDC:
			case SI:
			case DAS:
				repaymentMethods.add(new ValueLabel(item.name(), item.code()));
				break;
			case MANUAL:
				repaymentMethods.add(new ValueLabel(item.name(), item.description()));
				break;
			default:
				continue;
			}
		}

		return repaymentMethods;
	}

	public static List<ValueLabel> getMandateStatus() {

		if (mandateStatus != null) {
			return mandateStatus;
		}

		mandateStatus = new ArrayList<>(7);

		String customStatus = SysParamUtil.getValueAsString(MandateConstants.MANDATE_CUSTOM_STATUS);

		if (customStatus != null) {
			mandateStatus.add(new ValueLabel(customStatus, Labels.getLabel("label_Mandate_" + customStatus)));
		}

		mandateStatus.add(new ValueLabel(MandateStatus.NEW, Labels.getLabel("label_Mandate_NEW")));
		mandateStatus.add(new ValueLabel(MandateStatus.AWAITCON, Labels.getLabel("label_Mandate_AWAITCON")));
		mandateStatus.add(new ValueLabel(MandateStatus.APPROVED, Labels.getLabel("label_Mandate_APPROVED")));
		mandateStatus.add(new ValueLabel(MandateStatus.REJECTED, Labels.getLabel("label_Mandate_REJECTED")));
		mandateStatus.add(new ValueLabel(MandateStatus.HOLD, Labels.getLabel("label_Mandate_HOLD")));
		mandateStatus.add(new ValueLabel(MandateStatus.RELEASE, Labels.getLabel("label_Mandate_RELEASE")));
		mandateStatus.add(new ValueLabel(MandateStatus.FIN, Labels.getLabel("label_Mandate_FINANCE")));
		mandateStatus.add(new ValueLabel(MandateStatus.CANCEL, Labels.getLabel("label_Mandate_CANCEL")));
		mandateStatus.add(new ValueLabel(MandateStatus.INPROCESS, Labels.getLabel("label_Mandate_INPROCESS")));

		return mandateStatus;
	}

	public static List<ValueLabel> getChequeTypes() {
		if (chequeTypesList != null) {
			return chequeTypesList;
		}

		String labelSPDC = InstrumentType.SPDC.code().concat("/").concat(InstrumentType.UDC.code());

		chequeTypesList = new ArrayList<>(2);
		chequeTypesList.add(new ValueLabel(InstrumentType.PDC.name(), InstrumentType.PDC.code()));
		chequeTypesList.add(new ValueLabel(InstrumentType.SPDC.name(), labelSPDC));

		return chequeTypesList;
	}

	private static boolean isEanbled(InstrumentType mandateType) {
		return FeatureExtension.getValueAsBoolean(mandateType.name() + "_ALLOWED", true);
	}

	public static List<ValueLabel> excludeRepayMethods(InstrumentType das, InstrumentType si) {
		// TODO Auto-generated method stub
		return null;
	}

}