package com.pennanttech.pff.core.util;

import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.util.FinanceConstants;

/**
 * <p>
 * A suite of utility methods for Product.
 * </p>
 */
public class ProductUtil {
	private ProductUtil() {
		super();
	}

	public static boolean isConventional(String productCategory) {
		return FinanceConstants.PRODUCT_CONVENTIONAL.equals(productCategory);
	}

	public static boolean isConventional(final FinanceMain fm) {
		return isConventional(fm.getProductCategory());
	}

	public static boolean isConventional(final FinanceType ft) {
		return isConventional(ft.getProductCategory());
	}

	public static boolean isCD(String productCategory) {
		return FinanceConstants.PRODUCT_CD.equals(productCategory);
	}

	public static boolean isCD(final FinanceMain fm) {
		return isCD(fm.getProductCategory());
	}

	public static boolean isCD(final FinanceType ft) {
		return isCD(ft.getProductCategory());
	}

	public static boolean isOverDraft(final FinanceType ft) {
		return isOverDraft(ft.getProductCategory());
	}

	public static boolean isOverDraft(String productCategory) {
		return FinanceConstants.PRODUCT_ODFACILITY.equals(productCategory);
	}

	public static boolean isOverDraft(final FinanceMain fm) {
		return isOverDraft(fm.getProductCategory());
	}

	public static boolean isNotOverDraft(final FinanceMain fm) {
		return !isOverDraft(fm);
	}

	public static boolean isOverDraftChargeReq(final FinanceMain fm) {
		return isOverDraft(fm) && fm.isOverdraftTxnChrgReq();
	}

	public static boolean isOverDraftChargeNotReq(final FinanceMain fm) {
		return !isOverDraftChargeReq(fm);
	}

}
