package com.pennant.backend.util;

import java.util.ArrayList;
import java.util.List;

import com.pennant.backend.model.Property;

public class AdvanceEMI {

	private AdvanceEMI() {
		super();
	}

	public enum AdvanceType {
		AE("AE", "Advance EMI"),
		UF("UF", "Upfront Full Tenor"),
		UT("UT", "Upfront Few Terms"),
		AF("AF", "Advance at Interest Frequency");

		private final String code;
		private final String value;

		private AdvanceType(String code, String value) {
			this.code = code;
			this.value = value;
		}

		public String getCode() {
			return code;
		}

		public String getValue() {
			return value;
		}

		public static AdvanceType getType(String code) {
			for (AdvanceType type : values()) {
				if (type.getCode().equals(code)) {
					return type;
				}
			}
			return null;
		}

		public static List<Property> getGrcList() {
			List<Property> list = new ArrayList<>();
			for (AdvanceType type : values()) {
				if (AdvanceType.AE == type) {
					continue;
				}
				list.add(new Property(type.code, type.value));
			}
			return list;
		}

		public static List<Property> getRepayList() {
			List<Property> list = new ArrayList<>();
			for (AdvanceType type : values()) {
				list.add(new Property(type.code, type.value));
			}
			return list;
		}

	}

	public enum AdvanceStage {
		FE("FE", "Front End"), RE("RE", "Rear End");

		private final String code;
		private final String value;

		private AdvanceStage(String code, String value) {
			this.code = code;
			this.value = value;
		}

		public String getCode() {
			return code;
		}

		public String getValue() {
			return value;
		}

		public static AdvanceStage getStage(String code) {
			for (AdvanceStage type : values()) {
				if (type.getCode().equals(code)) {
					return type;
				}
			}
			return null;
		}

		public static List<Property> getList() {
			List<Property> list = new ArrayList<>();
			for (AdvanceStage type : values()) {
				list.add(new Property(type.code, type.value));
			}
			return list;
		}

	}

	public enum AdvanceRuleCode {
		ADVINT, ADVEMI, CASHCLT, DSF;

		public static String getRule(String code) {
			for (AdvanceRuleCode type : values()) {
				if (type.name().equals(code)) {
					return type.name();
				}
			}
			return null;
		}
	}

}
