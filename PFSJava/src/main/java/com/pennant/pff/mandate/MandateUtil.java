package com.pennant.pff.mandate;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.util.resource.Labels;

import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.util.MandateConstants;
import com.pennanttech.extension.FeatureExtension;

public class MandateUtil {

	private static List<ValueLabel> instrumentTypes;
	private static List<ValueLabel> repaymentMethods;
	private static List<ValueLabel> mandateStatus;

	public static List<ValueLabel> getInstrumentTypes() {
		if (instrumentTypes != null) {
			return instrumentTypes;
		}

		instrumentTypes = new ArrayList<ValueLabel>(4);

		for (InstrumentType item : InstrumentType.values()) {
			if (!isEanbled(item)) {
				continue;
			}

			String label = null;
			switch (item) {
			case NACH:
				label = Labels.getLabel("label_Mandate_Nach");
				break;
			case ECS:
				label = Labels.getLabel("label_Mandate_Ecs");
				break;
			case ENACH:
				label = Labels.getLabel("label_Mandate_ENach");
				break;
			case EMANDATE:
				label = Labels.getLabel("label_Mandate_EMandate");
				break;
			case PDC:
				label = Labels.getLabel("label_Mandate_PDC");
				break;
			case DDM:
				label = Labels.getLabel("label_Mandate_DD");
				break;
			case SI:
				label = Labels.getLabel("label_Mandate_SI");
				break;
			case DAS:
				label = Labels.getLabel("label_Mandate_DAS");
				break;

			default:
				continue;
			}

			instrumentTypes.add(new ValueLabel(item.name(), label));
		}

		return instrumentTypes;
	}

	public static List<ValueLabel> getRepayMethods() {
		if (repaymentMethods != null) {
			return repaymentMethods;
		}

		repaymentMethods = new ArrayList<ValueLabel>(4);

		repaymentMethods.add(new ValueLabel(InstrumentType.MANUAL.name(), Labels.getLabel("label_RepayMethod_Manual")));

		if (!isEanbled(InstrumentType.CASA)) {
			repaymentMethods.add(new ValueLabel(InstrumentType.CASA.name(), Labels.getLabel("label_RepayMethod_Casa")));
		}

		repaymentMethods.addAll(getInstrumentTypes());

		return repaymentMethods;
	}

	public static List<ValueLabel> getMandateStatus() {

		if (mandateStatus != null) {
			return mandateStatus;
		}

		mandateStatus = new ArrayList<ValueLabel>(7);

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

	private static boolean isEanbled(InstrumentType mandateType) {
		return FeatureExtension.getValueAsBoolean(mandateType.name() + "_ALLOWED", true);
	}
}