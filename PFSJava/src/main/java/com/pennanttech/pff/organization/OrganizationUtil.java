package com.pennanttech.pff.organization;

import java.util.List;

import com.pennant.backend.model.ValueLabel;

public class OrganizationUtil {
	private static List<ValueLabel> schoolClassName;

	public static List<ValueLabel> getschoolClassName() {
		if (schoolClassName == null) {
			synchronized (schoolClassName) {

			}
		}
		return schoolClassName;
	}

}
