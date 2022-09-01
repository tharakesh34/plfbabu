package com.pennant.pff.mandate;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.util.resource.Labels;

import com.pennant.backend.model.ValueLabel;
import com.pennanttech.extension.FeatureExtension;

public enum InstrumentTypes {
	ECS,

	DDM,

	NACH,

	PDC,

	EMNDT,

	DAS,

	SI;

	private static List<ValueLabel> list;

	public static List<ValueLabel> list() {
		if (list != null) {
			return list;
		}

		list = new ArrayList<ValueLabel>(4);

		for (InstrumentTypes item : InstrumentTypes.values()) {
			if (!isEanbled(item)) {
				continue;
			}

			String label = null;
			switch (item) {
			case ECS:
				label = Labels.getLabel("label_Mandate_Ecs");
				break;
			case DDM:
				label = Labels.getLabel("label_Mandate_DD");
				break;
			case NACH:
				label = Labels.getLabel("label_Mandate_Nach");
				break;
			case PDC:
				label = Labels.getLabel("label_Mandate_PDC");
				break;
			case DAS:
				label = Labels.getLabel("label_Mandate_DAS");
				break;
			case SI:
				label = Labels.getLabel("label_Mandate_SI");
				break;
			case EMNDT:
				label = Labels.getLabel("label_Mandate_EMNDT");
				break;

			default:
				break;
			}

			list.add(new ValueLabel(item.name(), label));

		}

		return list;
	}

	public static boolean isECS(String instrumentType) {
		InstrumentTypes st = InstrumentTypes.valueOf(instrumentType);

		return st == null ? false : st == ECS;
	}

	public static boolean isDD(String instrumentType) {
		InstrumentTypes st = InstrumentTypes.valueOf(instrumentType);

		return st == null ? false : st == DDM;
	}

	public static boolean isNACH(String instrumentType) {
		InstrumentTypes st = InstrumentTypes.valueOf(instrumentType);

		return st == null ? false : st == NACH;
	}

	public static boolean isPDC(String instrumentType) {
		InstrumentTypes st = InstrumentTypes.valueOf(instrumentType);

		return st == null ? false : st == PDC;
	}

	public static boolean isEMNDT(String instrumentType) {
		InstrumentTypes st = InstrumentTypes.valueOf(instrumentType);

		return st == null ? false : st == EMNDT;
	}

	public static boolean isDAS(String instrumentType) {
		InstrumentTypes st = InstrumentTypes.valueOf(instrumentType);

		return st == null ? false : st == DAS;
	}

	public static boolean isSI(String instrumentType) {
		InstrumentTypes st = InstrumentTypes.valueOf(instrumentType);

		return st == null ? false : st == SI;
	}

	private static boolean isEanbled(InstrumentTypes mandateType) {
		return FeatureExtension.getValueAsBoolean(mandateType.name() + "_ALLOWED", true);
	}
}