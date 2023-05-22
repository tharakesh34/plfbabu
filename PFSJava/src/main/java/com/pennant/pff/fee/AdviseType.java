package com.pennant.pff.fee;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.zkoss.util.resource.Labels;

import com.pennant.backend.model.ValueLabel;

public enum AdviseType {

	RECEIVABLE(1),

	PAYABLE(2);

	private int id;

	private AdviseType(int id) {
		this.id = id;
	}

	public int id() {
		return id;
	}

	public String type() {
		return String.valueOf(id);
	}

	public static boolean isReceivable(int id) {
		return isEqual(RECEIVABLE, adviseType(id));
	}

	public static boolean isPayable(int id) {
		return isEqual(PAYABLE, adviseType(id));
	}

	public static boolean isReceivable(String id) {
		return isEqual(RECEIVABLE, adviseType(id));
	}

	public static boolean isPayable(String id) {
		return isEqual(PAYABLE, adviseType(id));
	}

	private static boolean isEqual(AdviseType adviseType, AdviseType type) {
		if (type != null) {
			return type == adviseType;
		}

		return false;
	}

	public static AdviseType adviseType(String id) {
		if (StringUtils.isEmpty(id)) {
			return null;
		}

		try {
			return adviseType(Integer.valueOf(id));
		} catch (NumberFormatException e) {
			return null;
		}

	}

	public static AdviseType adviseType(int id) {
		List<AdviseType> list = Arrays.asList(AdviseType.values());

		for (AdviseType pa : list) {
			if (pa.id() == id) {
				return pa;
			}
		}

		return null;
	}

	private static List<ValueLabel> list;

	public static List<ValueLabel> getList() {
		if (list == null) {
			list = new ArrayList<>(2);
			list.add(new ValueLabel(String.valueOf(RECEIVABLE.id()), Labels.getLabel("label_ManualAdvise_Receivable")));
			list.add(new ValueLabel(String.valueOf(PAYABLE.id()), Labels.getLabel("label_ManualAdvise_Payable")));
		}

		return list;
	}

}
