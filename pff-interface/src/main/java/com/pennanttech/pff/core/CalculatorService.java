package com.pennanttech.pff.core;

import java.math.BigDecimal;

public interface CalculatorService {
	public enum TaxComponent {
		INCLUSIVE("I"), EXCLUSIVE("E");

		private final String code;

		private TaxComponent(String code) {
			this.code = code;
		}

		public String getTaxComponent() {
			return code;
		}
	}

	public default BigDecimal getTotalGST(String finReferece, BigDecimal amount, TaxComponent taxComponent) {
		return BigDecimal.ZERO;
	}
}
