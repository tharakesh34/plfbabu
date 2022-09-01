package com.pennant.pff.mandate;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.util.resource.Labels;

import com.pennant.backend.model.ValueLabel;
import com.pennanttech.extension.FeatureExtension;

public class MandateUtil {

	private static List<ValueLabel> instrumentTypes;
	private static List<ValueLabel> repaymentMethods;

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

	private static boolean isEanbled(InstrumentType mandateType) {
		return FeatureExtension.getValueAsBoolean(mandateType.name() + "_ALLOWED", true);
	}
}