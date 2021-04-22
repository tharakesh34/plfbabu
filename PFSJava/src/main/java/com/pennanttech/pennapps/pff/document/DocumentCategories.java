package com.pennanttech.pennapps.pff.document;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.util.resource.Labels;

import com.pennant.backend.model.ValueLabel;

public enum DocumentCategories {

	CUSTOMER("CUSTOMER", Labels.getLabel("label_documents_category_customer")),
	FINANCE("FINANCE", Labels.getLabel("label_documents_category_loan")),
	COLLATERAL("COLLATERAL", Labels.getLabel("label_documents_category_collateral")),
	VERIFICATION_TV("VERIFICATION_TV", Labels.getLabel("label_documents_category_tv")),
	VERIFICATION_RCU("VERIFICATION_RCU", Labels.getLabel("label_documents_category_rcu")),
	VERIFICATION_FI("VERIFICATION_FI", Labels.getLabel("label_documents_category_fi")),
	VERIFICATION_LV("VERIFICATION_LV", Labels.getLabel("label_documents_category_lv")),
	VERIFICATION_VT("VERIFICATION_VT", Labels.getLabel("label_documents_category_vetting")),
	SAMPLING("SAMPLING", Labels.getLabel("label_documents_category_sampling")),
	VERIFICATION_PD("VERIFICATION_PD", Labels.getLabel("label_documents_category_pd")),
	BUILDER_PROJ_DOC("BUILDER_PROJ_DOC", Labels.getLabel("label_documents_category_builderproject")),
	COVENANT("COVENANT", Labels.getLabel("label_documents_category_covenants")),
	UPFNT_FEE_RECEIPTS("UPFRTFEERECEIPT", Labels.getLabel("label_documents_category_receipts")),
	MANUAL_ADVISE_PAYABLE("MANLADSPAYABLE", Labels.getLabel("label_documents_category_payableadvise")),
	PERFIOS("PERFIOS", Labels.getLabel("label_documents_category_Perfios"));

	private final String key;
	private final String value;

	private DocumentCategories(String key, String value) {
		this.key = key;
		this.value = value;
	}

	public String getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}

	public static DocumentCategories getType(String key) {
		for (DocumentCategories type : values()) {
			if (type.getKey() == key) {
				return type;
			}
		}
		return null;
	}

	public static List<ValueLabel> getList() {
		List<ValueLabel> list = new ArrayList<>();
		for (DocumentCategories type : values()) {
			list.add(new ValueLabel(String.valueOf(type.getKey()), type.getValue()));
		}

		return list;
	}

}
