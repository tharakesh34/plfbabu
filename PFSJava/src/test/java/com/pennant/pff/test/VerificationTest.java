package com.pennant.pff.test;

import java.util.HashMap;
import java.util.List;

import com.pennant.backend.model.ValueLabel;
import com.pennanttech.pennapps.pff.verification.Agencies;
import com.pennanttech.pennapps.pff.verification.RequestType;
import com.pennanttech.pennapps.pff.verification.StatuReasons;

/**
 * 
 */

/**
 * @author swamy.p
 *
 */
public class VerificationTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		List<ValueLabel> list = RequestType.getList();
		RequestType[] values = RequestType.values();
		HashMap<String, String> hashMap = new HashMap<>();
		int i = 0;
		for (ValueLabel valueLabel : list) {
			for (; i < values.length;) {
				hashMap.put(valueLabel.getValue(), values[i].name());
				i++;
				break;
			}
		}
		// System.out.println(hashMap.toString());
		// System.out.println(VerificationType.FI.getValue());
		System.out.println(Agencies.FIAGENCY.getKey());
		System.out.println(Agencies.FIAGENCY.getValue());
		System.out.println(StatuReasons.FISRES.getKey());
		System.out.println(StatuReasons.FISRES.getValue());

	}

}
