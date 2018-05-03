package com.pennanttech.pff.external.creditInformation;

import java.util.Iterator;
import java.util.LinkedHashMap;
import org.apache.commons.lang.StringUtils;

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
	

	LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
	LinkedHashMap<String, String> returnMessages = new LinkedHashMap<String, String>();

	

	public LinkedHashMap<String, String> getCibilResponseArray(String str) {
		if(str.startsWith("ERRR")){
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
		}
		else{	
		map.put("PN", "03N");
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
		int indexSC = details.indexOf("SC10CIBILTUSCR");
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
					idmsg = details.substring(indexID, indexPT);
					returnMessages.put("ID", idmsg);
					break;
				} else if (indexID != -1 && indexEC != -1) {
					idmsg = details.substring(indexID, indexEC);
					returnMessages.put("ID", idmsg);
					break;
				} else if (indexID != -1 && indexEM != -1) {
					idmsg = details.substring(indexID, indexEM);
					returnMessages.put("ID", idmsg);
					break;
				} else if (indexID != -1 && indexPI != -1) {
					idmsg = details.substring(indexID, indexPI);
					returnMessages.put("ID", idmsg);
					break;
				} else if (indexID != -1 && indexSC != -1) {
					idmsg = details.substring(indexID, indexSC);
					returnMessages.put("ID", idmsg);
					break;
				} else if (indexID != -1 && indexPA != -1) {
					idmsg = details.substring(indexID, indexPA);
					returnMessages.put("ID", idmsg);
					break;
				} else if (indexID != -1 && indexTL != -1) {
					idmsg = details.substring(indexID, indexTL);
					returnMessages.put("ID", idmsg);
					break;
				} else if (indexID != -1 && indexIQ != -1) {
					idmsg = details.substring(indexID, indexIQ);
					returnMessages.put("ID", idmsg);
					break;
				} else if (indexID != -1 && indexDR != -1) {
					idmsg = details.substring(indexID, indexDR);
					returnMessages.put("ID", idmsg);
					break;
				} else if (indexID != -1 && indexES != -1) {
					idmsg = details.substring(indexID, indexES);
					returnMessages.put("ID", idmsg);
					break;
				}
				break;
			case "PT":
				if (indexEC != -1 && indexPT != -1) {
					ptmsg = details.substring(indexPT, indexEC);
					returnMessages.put("PT", ptmsg);
					break;
				} else if (indexEM != -1) {
					ptmsg = details.substring(indexPT, indexEM);
					returnMessages.put("PT", ptmsg);
					break;
				} else if (indexPI != -1) {
					ptmsg = details.substring(indexPT, indexPI);
					returnMessages.put("PT", ptmsg);
					break;
				} else if (indexSC != -1) {
					ptmsg = details.substring(indexPT, indexSC);
					returnMessages.put("PT", ptmsg);
					break;
				} else if (indexPA != -1) {
					ptmsg = details.substring(indexPT, indexPA);
					returnMessages.put("PT", ptmsg);
					break;
				} else if (indexTL != -1) {
					ptmsg = details.substring(indexPT, indexTL);
					returnMessages.put("PT", ptmsg);
					break;
				} else if (indexIQ != -1) {
					ptmsg = details.substring(indexPT, indexIQ);
					returnMessages.put("PT", ptmsg);
					break;
				} else if (indexDR != -1) {
					ptmsg = details.substring(indexPT, indexDR);
					returnMessages.put("PT", ptmsg);
					break;
				} else if (indexES != -1) {
					ptmsg = details.substring(indexPT, indexES);
					returnMessages.put("PT", ptmsg);
					break;
				}
				
			case "EC":
				if (indexEC != -1 && indexEM != -1) {
					ecmsg = details.substring(indexEC, indexEM);
					returnMessages.put("EC", ecmsg);
					break;
				} else if (indexEC != -1 && indexPI != -1) {
					ecmsg = details.substring(indexEC, indexPI);
					returnMessages.put("EC", ecmsg);
					break;
				} else if (indexEC != -1 && indexSC != -1) {
					ecmsg = details.substring(indexEC, indexSC);
					returnMessages.put("EC", ecmsg);
					break;
				} else if (indexEC != -1 && indexPA != -1) {
					ecmsg = details.substring(indexEC, indexPA);
					returnMessages.put("EC", ecmsg);
					break;
				} else if (indexEC != -1 && indexTL != -1) {
					ecmsg = details.substring(indexEC, indexTL);
					returnMessages.put("EC", ecmsg);
					break;
				} else if (indexEC != -1 && indexPA != -1) {
					ecmsg = details.substring(indexEC, indexPA);
					returnMessages.put("EC", ecmsg);
					break;
				} else if (indexEC != -1 && indexIQ != -1) {
					ecmsg = details.substring(indexEC, indexIQ);
					returnMessages.put("EC", ecmsg);
					break;
				} else if (indexEC != -1 && indexDR != -1) {
					ecmsg = details.substring(indexEC, indexDR);
					returnMessages.put("EC", ecmsg);
					break;
				} else if (indexEC != -1 && indexES != -1) {
					ecmsg = details.substring(indexEC, indexES);
					returnMessages.put("EC", ecmsg);
					break;
				}
			case "EM":
				if (indexEM != -1 && indexPI != -1) {
					emmsg = details.substring(indexEM, indexPI);
					returnMessages.put("EM", emmsg);
					break;
				} else if (indexEM != -1 && indexSC != -1) {
					emmsg = details.substring(indexEM, indexSC);
					returnMessages.put("EM", emmsg);
					break;
				} else if (indexEM != -1 && indexPA != -1) {
					emmsg = details.substring(indexEM, indexPA);
					returnMessages.put("EM", emmsg);
					break;
				} else if (indexEM != -1 && indexTL != -1) {
					emmsg = details.substring(indexEM, indexTL);
					returnMessages.put("EM", emmsg);
					break;
				} else if (indexEM != -1 && indexIQ != -1) {
					emmsg = details.substring(indexEM, indexIQ);
					returnMessages.put("EM", emmsg);
					break;
				} else if (indexEM != -1 && indexDR != -1) {
					emmsg = details.substring(indexEM, indexDR);
					returnMessages.put("EM", emmsg);
					break;
				} else if (indexEM != -1 && indexES != -1) {
					emmsg = details.substring(indexEM, indexES);
					returnMessages.put("EM", emmsg);
					break;
				}
			case "PI":
				if (indexSC != -1 && indexPI != -1) {
					pimsg = details.substring(indexPI, indexSC);
					returnMessages.put("PI", pimsg);
					break;
				} else if (indexPA != -1 && indexPI != -1) {
					pimsg = details.substring(indexPI, indexPA);
					returnMessages.put("PI", pimsg);
					break;
				} else if (indexTL != -1 && indexPI != -1) {
					pimsg = details.substring(indexPI, indexTL);
					returnMessages.put("PI", pimsg);
					break;
				} else if (indexIQ != -1 && indexPI != -1) {
					pimsg = details.substring(indexPI, indexIQ);
					returnMessages.put("PI", pimsg);
					break;
				} else if (indexDR != -1 && indexPI != -1) {
					pimsg = details.substring(indexPI, indexDR);
					returnMessages.put("PI", pimsg);
					break;
				} else if (indexES != -1 && indexPI != -1) {
					pimsg = details.substring(indexPI, indexES);
					returnMessages.put("PI", pimsg);
					break;
				}
				break;
			case "SC":
				if (indexSC != -1 && indexPA != -1) {
					scmsg = details.substring(indexSC, indexPA);
					returnMessages.put("SC", scmsg);
					break;
				} else if (indexSC != -1 && indexTL != -1) {
					scmsg = details.substring(indexSC, indexTL);
					returnMessages.put("SC", scmsg);
					break;
				} else if (indexSC != -1 && indexIQ != -1) {
					scmsg = details.substring(indexSC, indexIQ);
					returnMessages.put("SC", scmsg);
					break;
				} else if (indexSC != -1 && indexDR != -1) {
					scmsg = details.substring(indexSC, indexDR);
					returnMessages.put("SC", scmsg);
					break;
				} else if (indexSC != -1 && indexES != -1) {
					scmsg = details.substring(indexSC, indexES);
					returnMessages.put("SC", scmsg);
					break;
				}
				break;
			case "PA":
				if (indexTL != -1 && indexPA != -1) {
					pamsg = details.substring(indexPA, indexTL);
					returnMessages.put("PA", pamsg);
					break;
				} else if (indexIQ != -1 && indexPA != -1) {
					pamsg = details.substring(indexPA, indexIQ);
					returnMessages.put("PA", pamsg);
					break;
				} else if (indexDR != -1 && indexPA != -1) {
					pamsg = details.substring(indexPA, indexDR);
					returnMessages.put("PA", pamsg);
					break;
				} else if (indexES != -1 && indexPA != -1) {
					pamsg = details.substring(indexPA, indexES);
					returnMessages.put("PA", pamsg);
					break;
				}
			case "TL":
				if (indexTL != -1 && indexIQ != -1) {
					tlmsg = details.substring(indexTL, indexIQ);
					returnMessages.put("TL", tlmsg);
					break;
				} else if (indexTL != -1 && indexDR != -1) {
					tlmsg = details.substring(indexTL, indexDR);
					returnMessages.put("TL", tlmsg);
					break;
				} else if (indexTL != -1 && indexES != -1) {
					tlmsg = details.substring(indexTL, indexES);
					returnMessages.put("TL", tlmsg);
					break;
				}
				
			case "IQ":
				if (indexDR != -1 && indexIQ != -1) {
					iqmsg = details.substring(indexIQ, indexDR);
					returnMessages.put("IQ", iqmsg);
					break;
				} else if (indexDR != -1 && indexIQ != -1) {
					iqmsg = details.substring(indexIQ, indexDR);
					returnMessages.put("IQ", iqmsg);
					break;
				} else if (indexES != -1 && indexIQ != -1) {
					iqmsg = details.substring(indexIQ, indexES);
					returnMessages.put("IQ", iqmsg);
					break;
				}

			case "DR":
				if (indexDR != -1 && indexES != -1) {
					drmsg = details.substring(indexDR, indexES);
					returnMessages.put("DR", drmsg);
					break;
				} else if (indexDR != -1 && indexES != -1) {
					drmsg = details.substring(indexDR, indexES);
					returnMessages.put("DR", drmsg);
					break;
				}
				break;
			case "ES":
				esmsg = details.substring(indexES, endIndex);
				returnMessages.put("ES", esmsg);
				break;
			default:
				break;

			}
		}
		}
		return returnMessages;
	}
	
	public LinkedHashMap<String, String> getCibilResponseDetails(String str) {
		
		LinkedHashMap<String, String> detailsMap= new LinkedHashMap<String, String>();
		
		String tempStr = "";
		String balStr = "";

		balStr = str;

		do {

			if (tempStr == "") {
				tempStr = StringUtils.substring(str, 0, 4);

				balStr = StringUtils.substring(str, tempStr.length(),
						str.length());

				int value = Integer.parseInt(tempStr.substring(2, 4));

				String newValue = StringUtils.substring(balStr, 0, value);
				
				detailsMap.put(StringUtils.substring(tempStr, 0, 2), newValue);

				balStr = StringUtils.substring(balStr, value, balStr.length());

			} else {

				tempStr = StringUtils.substring(balStr, 0, 4);

				balStr = StringUtils.substring(balStr, tempStr.length(),
						balStr.length());

				int value = Integer.parseInt(tempStr.substring(2, 4));

				String newValue = StringUtils.substring(balStr, 0, value);
				
				detailsMap.put(StringUtils.substring(tempStr, 0, 2), newValue);

				balStr = StringUtils.substring(balStr, value, balStr.length());

			}

		} while (balStr.length() != 0);
		
		
		return detailsMap;
		
		
		
	}
	
	
	
}
