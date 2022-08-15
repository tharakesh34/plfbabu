package com.pennanttech.pff.external.creditInformation;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.pennant.app.util.SysParamUtil;

public class CibilResponseDetails {

	String pnMsg = "";
	String idmsg = "";
	String ptmsg = "";
	String ecmsg = "";
	String pimsg = "";
	String scmsg = "";
	String pamsg = "";
	String tlmsg = "";
	String iqmsg = "";
	String drmsg = "";
	String esmsg = "";
	String emmsg = "";
	String urmsg = "";

	Map<String, String> map = new LinkedHashMap<String, String>();
	Map<String, String> returnMessages = new LinkedHashMap<String, String>();

	public Map<String, String> getCibilResponseArray(String str) {
		if (str.startsWith("ERRR")) {
			map.put("UR", "03U");
			map.put("ES", "");
			Iterator<String> itr = map.keySet().iterator();
			int indexUR = str.indexOf("UR03U0");
			str.substring(indexUR);
			int endIndex = str.lastIndexOf("**");
			int indexES = str.indexOf("ES07");

			while (itr.hasNext()) {
				String key = itr.next();
				map.get(key);
				switch (key) {
				case "UR":
					if (indexUR != -1 && indexES != -1) {
						urmsg = str.substring(indexUR, indexES);
						returnMessages.put("UR", urmsg);
						break;
					} else if (indexUR != -1 && indexES != -1) {
						urmsg = str.substring(indexUR, indexES);
						returnMessages.put("DR", urmsg);
						break;
					}
					break;
				case "ES":
					esmsg = str.substring(indexES, endIndex);
					returnMessages.put("ES", esmsg);
					break;
				default:
					break;

				}
			}
		} else {
			map.put("PN", "03N");
			/*
			 * if (str.contains("ID03I01")) { map.put("ID", ""); } if (str.contains("PT03T01")) { map.put("PT", "03T");
			 * } if (str.contains("EC03C01")) { map.put("EC", ""); }
			 */
			map.put("ID", "");
			map.put("PT", "03T");
			map.put("EC", "");
			map.put("EM", "");
			map.put("PI", "");
			map.put("SC", "");
			map.put("PA", "03A");
			map.put("TL", "");
			map.put("IQ", "04I");
			map.put("DR", "");
			map.put("ES", "");

			Iterator<String> itr = map.keySet().iterator();

			int indexPN = str.indexOf("PN03N01");
			String details = str.substring(indexPN);
			int indexPT = details.indexOf("PT03T01");
			int indexPA = details.indexOf("PA03A01");
			int indexIQ = details.indexOf("IQ04I");
			// int indexSC = details.indexOf("SC10CIBILTUSCR");

			int indexSC = details.indexOf(getCibilScoreVersion());

			int indexEM = details.indexOf("EM03E01");
			int indexES = details.indexOf("ES07");
			int indexEC = details.indexOf("EC03C01");
			int indexPI = details.indexOf("PI03I01");
			int indexTL = details.indexOf("TL04T001");
			int indexDR = details.indexOf("DR03D01");
			int indexID = details.indexOf("ID03I01");
			int endIndex = details.lastIndexOf("**");

			while (itr.hasNext()) {
				String key = itr.next();
				map.get(key);
				switch (key) {
				case "PN":
					if (indexID != -1) {
						pnMsg = details.substring(0, indexID);
						returnMessages.put("PN", pnMsg);
						break;
					} else if (indexPT != -1) {
						pnMsg = details.substring(0, indexPT);
						returnMessages.put("PN", pnMsg);
						break;
					} else if (indexEC != -1) {
						pnMsg = details.substring(0, indexEC);
						returnMessages.put("PN", pnMsg);
						break;
					} else if (indexEM != -1) {
						pnMsg = details.substring(0, indexEM);
						returnMessages.put("PN", pnMsg);
						break;
					} else if (indexPI != -1) {
						pnMsg = details.substring(0, indexPI);
						returnMessages.put("PN", pnMsg);
						break;
					} else if (indexSC != -1) {
						pnMsg = details.substring(0, indexSC);
						returnMessages.put("PN", pnMsg);
						break;
					} else if (indexPA != -1) {
						pnMsg = details.substring(0, indexPA);
						returnMessages.put("PN", pnMsg);
						break;
					} else if (indexTL != -1) {
						pnMsg = details.substring(0, indexTL);
						returnMessages.put("PN", pnMsg);
						break;
					} else if (indexIQ != -1) {
						pnMsg = details.substring(0, indexIQ);
						returnMessages.put("PN", pnMsg);
						break;
					} else if (indexDR != -1) {
						pnMsg = details.substring(0, indexDR);
						returnMessages.put("PN", pnMsg);
						break;
					} else if (indexES != -1) {
						pnMsg = details.substring(0, indexES);
						returnMessages.put("PN", pnMsg);
						break;
					}
				case "ID":
					if (indexID != -1 && indexPT != -1) {
						idmsg = indexID < indexPT ? details.substring(indexID, indexPT)
								: details.substring(indexPT, indexID);
						returnMessages.put("ID", idmsg);
						break;
					} else if (indexID != -1 && indexEC != -1) {
						idmsg = indexID < indexEC ? details.substring(indexID, indexEC)
								: details.substring(indexEC, indexID);
						returnMessages.put("ID", idmsg);
						break;
					} else if (indexID != -1 && indexEM != -1) {
						idmsg = indexID < indexEM ? details.substring(indexID, indexEM)
								: details.substring(indexEM, indexID);
						returnMessages.put("ID", idmsg);
						break;
					} else if (indexID != -1 && indexPI != -1) {
						idmsg = indexID < indexPI ? details.substring(indexID, indexPI)
								: details.substring(indexPI, indexID);
						returnMessages.put("ID", idmsg);
						break;
					} else if (indexID != -1 && indexSC != -1) {
						idmsg = indexID < indexSC ? details.substring(indexID, indexSC)
								: details.substring(indexSC, indexID);
						returnMessages.put("ID", idmsg);
						break;
					} else if (indexID != -1 && indexPA != -1) {
						idmsg = indexID < indexPA ? details.substring(indexID, indexPA)
								: details.substring(indexPA, indexID);
						returnMessages.put("ID", idmsg);
						break;
					} else if (indexID != -1 && indexTL != -1) {
						idmsg = indexID < indexPA ? details.substring(indexID, indexPA)
								: details.substring(indexPA, indexID);
						returnMessages.put("ID", idmsg);
						break;
					} else if (indexID != -1 && indexIQ != -1) {
						idmsg = indexID < indexIQ ? details.substring(indexID, indexIQ)
								: details.substring(indexIQ, indexID);
						returnMessages.put("ID", idmsg);
						break;
					} else if (indexID != -1 && indexDR != -1) {
						idmsg = indexID < indexDR ? details.substring(indexID, indexDR)
								: details.substring(indexDR, indexID);
						returnMessages.put("ID", idmsg);
						break;
					} else if (indexID != -1 && indexES != -1) {
						idmsg = indexID < indexES ? details.substring(indexID, indexES)
								: details.substring(indexES, indexID);
						returnMessages.put("ID", idmsg);
						break;
					}
					break;
				case "PT":
					if (indexPT != -1 && indexEC != -1) {
						ptmsg = indexPT < indexEC ? details.substring(indexPT, indexEC)
								: details.substring(indexEC, indexPT);
						returnMessages.put("PT", ptmsg);
						break;
					} else if (indexPT != -1 && indexEM != -1) {
						ptmsg = indexPT < indexEM ? details.substring(indexPT, indexEM)
								: details.substring(indexEM, indexPT);
						returnMessages.put("PT", ptmsg);
						break;
					} else if (indexPT != -1 && indexPI != -1) {
						ptmsg = indexPT < indexPI ? details.substring(indexPT, indexPI)
								: details.substring(indexPI, indexPT);
						returnMessages.put("PT", ptmsg);
						break;
					} else if (indexPT != -1 && indexSC != -1) {
						ptmsg = indexPT < indexSC ? details.substring(indexPT, indexSC)
								: details.substring(indexPI, indexSC);
						returnMessages.put("PT", ptmsg);
						break;
					} else if (indexPT != -1 && indexPA != -1) {
						ptmsg = indexPT < indexPA ? details.substring(indexPT, indexPA)
								: details.substring(indexPI, indexPA);
						returnMessages.put("PT", ptmsg);
						break;
					} else if (indexPT != -1 && indexTL != -1) {
						ptmsg = indexPT < indexTL ? details.substring(indexPT, indexTL)
								: details.substring(indexTL, indexPT);
						returnMessages.put("PT", ptmsg);
						break;
					} else if (indexPT != -1 && indexIQ != -1) {
						ptmsg = indexPT < indexIQ ? details.substring(indexPT, indexIQ)
								: details.substring(indexIQ, indexPT);
						returnMessages.put("PT", ptmsg);
						break;
					} else if (indexPT != -1 && indexDR != -1) {
						ptmsg = indexPT < indexDR ? details.substring(indexPT, indexDR)
								: details.substring(indexDR, indexPT);
						returnMessages.put("PT", ptmsg);
						break;
					} else if (indexPT != -1 && indexES != -1) {
						ptmsg = indexPT < indexES ? details.substring(indexPT, indexES)
								: details.substring(indexES, indexPT);
						returnMessages.put("PT", ptmsg);
						break;
					}

				case "EC":
					if (indexEC != -1 && indexEM != -1) {
						ecmsg = indexEC < indexEM ? details.substring(indexEC, indexEM)
								: details.substring(indexEM, indexEC);
						returnMessages.put("EC", ecmsg);
						break;
					} else if (indexEC != -1 && indexPI != -1) {
						ecmsg = indexEC < indexPI ? details.substring(indexEC, indexPI)
								: details.substring(indexPI, indexEC);
						returnMessages.put("EC", ecmsg);
						break;
					} else if (indexEC != -1 && indexSC != -1) {
						ecmsg = indexEC < indexSC ? details.substring(indexEC, indexSC)
								: details.substring(indexSC, indexEC);
						returnMessages.put("EC", ecmsg);
						break;
					} else if (indexEC != -1 && indexPA != -1) {
						ecmsg = indexEC < indexPA ? details.substring(indexEC, indexPA)
								: details.substring(indexPA, indexEC);
						returnMessages.put("EC", ecmsg);
						break;
					} else if (indexEC != -1 && indexTL != -1) {
						ecmsg = indexEC < indexTL ? details.substring(indexEC, indexTL)
								: details.substring(indexTL, indexEC);
						returnMessages.put("EC", ecmsg);
						break;
					} else if (indexEC != -1 && indexPA != -1) {
						ecmsg = indexEC < indexPA ? details.substring(indexEC, indexPA)
								: details.substring(indexPA, indexEC);
						returnMessages.put("EC", ecmsg);
						break;
					} else if (indexEC != -1 && indexIQ != -1) {
						ecmsg = indexEC < indexIQ ? details.substring(indexEC, indexIQ)
								: details.substring(indexIQ, indexEC);
						returnMessages.put("EC", ecmsg);
						break;
					} else if (indexEC != -1 && indexDR != -1) {
						ecmsg = indexEC < indexDR ? details.substring(indexEC, indexDR)
								: details.substring(indexDR, indexEC);
						returnMessages.put("EC", ecmsg);
						break;
					} else if (indexEC != -1 && indexES != -1) {
						ecmsg = indexEC < indexES ? details.substring(indexEC, indexES)
								: details.substring(indexES, indexEC);
						returnMessages.put("EC", ecmsg);
						break;
					}
				case "EM":
					if (indexEM != -1 && indexPI != -1) {
						emmsg = indexEM < indexPI ? details.substring(indexEM, indexPI)
								: details.substring(indexPI, indexEM);
						returnMessages.put("EM", emmsg);
						break;
					} else if (indexEM != -1 && indexSC != -1) {
						emmsg = indexEM < indexSC ? details.substring(indexEM, indexSC)
								: details.substring(indexSC, indexEM);
						returnMessages.put("EM", emmsg);
						break;
					} else if (indexEM != -1 && indexPA != -1) {
						emmsg = indexEM < indexPA ? details.substring(indexEM, indexPA)
								: details.substring(indexPA, indexEM);
						returnMessages.put("EM", emmsg);
						break;
					} else if (indexEM != -1 && indexTL != -1) {
						emmsg = indexEM < indexTL ? details.substring(indexEM, indexTL)
								: details.substring(indexTL, indexEM);
						returnMessages.put("EM", emmsg);
						break;
					} else if (indexEM != -1 && indexIQ != -1) {
						emmsg = indexEM < indexIQ ? details.substring(indexEM, indexIQ)
								: details.substring(indexIQ, indexEM);
						returnMessages.put("EM", emmsg);
						break;
					} else if (indexEM != -1 && indexDR != -1) {
						emmsg = indexEM < indexDR ? details.substring(indexEM, indexDR)
								: details.substring(indexDR, indexEM);
						returnMessages.put("EM", emmsg);
						break;
					} else if (indexEM != -1 && indexES != -1) {
						emmsg = indexEM < indexES ? details.substring(indexEM, indexES)
								: details.substring(indexES, indexEM);
						returnMessages.put("EM", emmsg);
						break;
					}
				case "PI":
					if (indexSC != -1 && indexPI != -1) {
						pimsg = indexPI < indexSC ? details.substring(indexPI, indexSC)
								: details.substring(indexSC, indexPI);
						returnMessages.put("PI", pimsg);
						break;
					} else if (indexPA != -1 && indexPI != -1) {
						pimsg = indexPI < indexPA ? details.substring(indexPI, indexPA)
								: details.substring(indexPA, indexPI);
						returnMessages.put("PI", pimsg);
						break;
					} else if (indexTL != -1 && indexPI != -1) {
						pimsg = indexPI < indexTL ? details.substring(indexPI, indexTL)
								: details.substring(indexTL, indexPI);
						returnMessages.put("PI", pimsg);
						break;
					} else if (indexIQ != -1 && indexPI != -1) {
						pimsg = indexPI < indexIQ ? details.substring(indexPI, indexIQ)
								: details.substring(indexIQ, indexPI);
						returnMessages.put("PI", pimsg);
						break;
					} else if (indexDR != -1 && indexPI != -1) {
						pimsg = indexPI < indexDR ? details.substring(indexPI, indexDR)
								: details.substring(indexDR, indexPI);
						returnMessages.put("PI", pimsg);
						break;
					} else if (indexES != -1 && indexPI != -1) {
						pimsg = indexPI < indexES ? details.substring(indexPI, indexES)
								: details.substring(indexES, indexPI);
						returnMessages.put("PI", pimsg);
						break;
					}
					break;
				case "SC":
					if (indexSC != -1 && indexPA != -1) {
						scmsg = indexSC < indexPA ? details.substring(indexSC, indexPA)
								: details.substring(indexPA, indexSC);
						returnMessages.put("SC", scmsg);
						break;
					} else if (indexSC != -1 && indexTL != -1) {
						scmsg = indexSC < indexTL ? details.substring(indexSC, indexTL)
								: details.substring(indexTL, indexSC);
						returnMessages.put("SC", scmsg);
						break;
					} else if (indexSC != -1 && indexIQ != -1) {
						scmsg = indexSC < indexIQ ? details.substring(indexSC, indexIQ)
								: details.substring(indexIQ, indexSC);
						returnMessages.put("SC", scmsg);
						break;
					} else if (indexSC != -1 && indexDR != -1) {
						scmsg = indexSC < indexDR ? details.substring(indexSC, indexDR)
								: details.substring(indexDR, indexSC);
						returnMessages.put("SC", scmsg);
						break;
					} else if (indexSC != -1 && indexES != -1) {
						scmsg = indexSC < indexES ? details.substring(indexSC, indexES)
								: details.substring(indexES, indexSC);
						returnMessages.put("SC", scmsg);
						break;
					}
					break;
				case "PA":
					if (indexTL != -1 && indexPA != -1) {
						pamsg = indexTL < indexPA ? details.substring(indexTL, indexPA)
								: details.substring(indexPA, indexTL);
						returnMessages.put("PA", pamsg);
						break;
					} else if (indexIQ != -1 && indexPA != -1) {
						pamsg = indexIQ < indexPA ? details.substring(indexIQ, indexPA)
								: details.substring(indexPA, indexIQ);
						returnMessages.put("PA", pamsg);
						break;
					} else if (indexDR != -1 && indexPA != -1) {
						pamsg = indexDR < indexPA ? details.substring(indexDR, indexPA)
								: details.substring(indexPA, indexDR);
						returnMessages.put("PA", pamsg);
						break;
					} else if (indexES != -1 && indexPA != -1) {
						pamsg = indexPA < indexES ? details.substring(indexPA, indexES)
								: details.substring(indexES, indexPA);
						returnMessages.put("PA", pamsg);
						break;
					}
				case "TL":
					if (indexTL != -1 && indexIQ != -1) {
						tlmsg = indexTL < indexIQ ? details.substring(indexTL, indexIQ)
								: details.substring(indexIQ, indexTL);
						returnMessages.put("TL", tlmsg);
						break;
					} else if (indexTL != -1 && indexDR != -1) {
						tlmsg = indexTL < indexDR ? details.substring(indexTL, indexDR)
								: details.substring(indexDR, indexTL);
						returnMessages.put("TL", tlmsg);
						break;
					} else if (indexTL != -1 && indexES != -1) {
						tlmsg = indexTL < indexES ? details.substring(indexTL, indexES)
								: details.substring(indexES, indexTL);
						returnMessages.put("TL", tlmsg);
						break;
					}

				case "IQ":
					if (indexDR != -1 && indexIQ != -1) {
						iqmsg = indexDR < indexIQ ? details.substring(indexDR, indexIQ)
								: details.substring(indexIQ, indexDR);
						returnMessages.put("IQ", iqmsg);
						break;
					} /*
						 * else if (indexDR != -1 && indexIQ != -1) { iqmsg = details.substring(indexIQ, indexDR);
						 * returnMessages.put("IQ", iqmsg); break; }
						 */ else if (indexES != -1 && indexIQ != -1) {
						iqmsg = indexES < indexIQ ? details.substring(indexES, indexIQ)
								: details.substring(indexIQ, indexES);
						returnMessages.put("IQ", iqmsg);
						break;
					}

				case "DR":
					if (indexDR != -1 && indexES != -1) {
						drmsg = indexES < indexDR ? details.substring(indexES, indexDR)
								: details.substring(indexDR, indexES);
						returnMessages.put("DR", drmsg);
						break;
					} /*
						 * else if (indexDR != -1 && indexES != -1) { drmsg = details.substring(indexDR, indexES);
						 * returnMessages.put("DR", drmsg); break; }
						 */
					break;
				case "ES":
					if (indexES != -1 && endIndex != -1) {
						esmsg = indexES < endIndex ? details.substring(indexES, endIndex)
								: details.substring(endIndex, indexES);
						returnMessages.put("ES", esmsg);
						break;
					}
					/*
					 * esmsg = indexES < endIndex ? details.substring(indexES, endIndex) :
					 * details.substring(endIndex,indexES); returnMessages.put("ES", esmsg); break;
					 */
				default:
					break;

				}
			}
		}
		return returnMessages;
	}

	private String getCibilScoreVersion() {
		String scoreVersion = null;
		String scoreType = null;
		scoreType = SysParamUtil.getValueAsString("CBIL_ENQUIRY_SCORE_TYPE");

		switch (scoreType) {
		case "00":
			scoreVersion = ""; // Not requesting a score
			break;
		case "01":
			scoreVersion = "SC10CIBILTUSCR"; // Requesting the CIBIL TransUnion Score Version 1.0 (CIBILTUSCR) only.
			break;
		case "02":
			scoreVersion = "SC07PLSCORE"; // Requesting the Personal Loan Score (PLSCORE) only.
			break;
		case "03":
			scoreVersion = "SC10CIBILTUSCR"; // Requesting both the CIBILTUSCR and PLSCORE scores.
			break;
		case "04":
			scoreVersion = "SC10CIBILTUSC2"; // Requesting the CIBIL TransUnion Score Version 2.0 (CIBILTUSC2) only
			break;
		/*
		 * case "06": scoreVersion = "SC10CIBILTUSC2#PLSCORE"; // Requesting both the CIBILTUSC2 and PLSCORE scores.
		 * break;
		 */
		case "08":
			scoreVersion = "SC10CIBILTUSC3"; // Requesting the CreditVision© Score (CIBILTUSC3) only.
			break;
		case "12":
			scoreVersion = "SC10CIBILTUIE1";// Requesting both Income Estimator Score (CIBILTUIE1) and CIBILTUSCR.
			break;

		/*
		 * case "10": scoreVersion = "CIBILTUSC3#PLSCORE";// Requesting both CreditVision© Score (CIBILTUSC3) and
		 * PLSCORE. break;
		 * 
		 * case "13": scoreVersion = "CIBILTUIE1#CIBILTUSC2"; //Requesting both Income Estimator Score (CIBILTUIE1) and
		 * CIBILTUSC2. break; case "14": scoreVersion = "CIBILTUIE1#CIBILTUSC3";//Requesting both Income Estimator Score
		 * (CIBILTUIE1) and CIBILTUSC3. break; case "15": scoreVersion = "CIBILTUIE1#CIBILTUSCR#PLSCORE"; //Requesting
		 * Income Estimator Score (CIBILTUIE1), CIBILTUSCR and PLSCORE. break; case "16": scoreVersion =
		 * "CIBILTUIE1#CIBILTUSC2#PLSCORE"; //Requesting Income Estimator Score (CIBILTUIE1), CIBILTUSC2 and PLSCORE.
		 * break; case "17": scoreVersion = "CIBILTUIE1#CIBILTUSC3#PLSCORE";//Requesting Income Estimator Score
		 * (CIBILTUIE1), CIBILTUSC3 and PLSCORE. break;
		 */

		default:
			scoreVersion = "SC10CIBILTUSCR";
			break;
		}

		return scoreVersion;
	}

	public Map<String, String> getCibilResponseDetails(String str) {

		Map<String, String> detailsMap = new LinkedHashMap<String, String>();

		String tempStr = "";
		String balStr = "";

		balStr = str;

		do {

			if (tempStr == "") {
				tempStr = StringUtils.substring(str, 0, 4);

				balStr = StringUtils.substring(str, tempStr.length(), str.length());

				int value = Integer.parseInt(tempStr.substring(2, 4));

				String newValue = StringUtils.substring(balStr, 0, value);

				detailsMap.put(StringUtils.substring(tempStr, 0, 2), newValue);

				balStr = StringUtils.substring(balStr, value, balStr.length());

			} else {

				tempStr = StringUtils.substring(balStr, 0, 4);

				balStr = StringUtils.substring(balStr, tempStr.length(), balStr.length());

				int value = Integer.parseInt(tempStr.substring(2, 4));

				String newValue = StringUtils.substring(balStr, 0, value);

				detailsMap.put(StringUtils.substring(tempStr, 0, 2), newValue);

				balStr = StringUtils.substring(balStr, value, balStr.length());

			}

		} while (balStr.length() != 0);

		return detailsMap;

	}

}
