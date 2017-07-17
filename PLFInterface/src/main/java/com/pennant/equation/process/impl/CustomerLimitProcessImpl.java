package com.pennant.equation.process.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.ConnectionPoolException;
import com.ibm.as400.data.ProgramCallDocument;
import com.pennant.coreinterface.model.CustomerLimit;
import com.pennant.coreinterface.model.limit.CustomerLimitDetail;
import com.pennant.coreinterface.model.limit.CustomerLimitPosition;
import com.pennant.coreinterface.model.limit.CustomerLimitUtilization;
import com.pennant.coreinterface.process.CustomerLimitProcess;
import com.pennant.equation.util.DateUtility;
import com.pennant.equation.util.GenericProcess;
import com.pennant.equation.util.HostConnection;
import com.pennanttech.pennapps.core.InterfaceException;

public class CustomerLimitProcessImpl extends GenericProcess implements CustomerLimitProcess {

	private static Logger logger = Logger.getLogger(CustomerLimitProcessImpl.class);

	private HostConnection hostConnection;
	
	public CustomerLimitProcessImpl() {
		super();
	}
	
	/**
	 * Method for Fetching List of Limt Category Customers
	 * @param pageNo
	 * @param pageSize
	 * @return
	 * @throws CustomerLimitProcessException 
	 */
	@Override
	public Map<String, Object> fetchCustLimitEnqList(int pageNo, int pageSize) throws InterfaceException {
		logger.debug("Entering");

		AS400 as400 = null;
		ProgramCallDocument pcmlDoc = null;
		String pcml = "PFFCLE";			//get List of Customer Limits

		int[] indices = new int[1]; 	// Indices for access array value
		int dsRspCount;              	// Number of records returned 
		Map<String, Object> custLimitMap = new HashMap<String, Object>();
		List<CustomerLimit> list = new ArrayList<CustomerLimit>();
		CustomerLimit item = null;
		String errorMessage = null;

		try {

			as400 = this.hostConnection.getConnection();
			pcmlDoc = new ProgramCallDocument(as400, pcml);

			pcmlDoc.setValue(pcml + ".@REQDTA.PageNumber", pageNo); 	// Page Number
			pcmlDoc.setValue(pcml + ".@REQDTA.PageSize", pageSize); 	// Page Size

			pcmlDoc.setValue(pcml + ".@ERCOD", "0000"); 	
			pcmlDoc.setValue(pcml + ".@ERPRM", ""); 	

			logger.debug(" Before PCML Call");
			getHostConnection().callAPI(pcmlDoc, pcml);
			logger.debug(" After PCML Call");

			if ("0000".equals(pcmlDoc.getValue(pcml + ".@ERCOD").toString())) {	
				
				dsRspCount = Integer.parseInt(String.valueOf(pcmlDoc.getValue(pcml + ".@RSPDTA.RspCount")));
				custLimitMap.put("PageNumber", Integer.parseInt(String.valueOf(pcmlDoc.getValue(pcml + ".@RSPDTA.PageNumber"))));
				custLimitMap.put("TotalSize", Integer.parseInt(String.valueOf(pcmlDoc.getValue(pcml + ".@RSPDTA.TotalSize"))));

				for (indices[0] = 0; indices[0] < dsRspCount; indices[0]++){
					item = new CustomerLimit();

					String strDate = null;
					item.setCustMnemonic(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.Customer",indices).toString());
					item.setCustLocation(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.CustLocation",indices).toString());
					item.setCustName(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.CustName",indices).toString());
					item.setLimitCategory(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.LmtCategory",indices).toString());
					item.setLimitCurrency(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.LmtCcy",indices).toString());

					strDate = StringUtils.trimToEmpty(String.valueOf(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.LmtExpiry", indices)));	
					if (!("0".equals(strDate) || StringUtils.isEmpty(strDate))) {
						item.setLimitExpiry(DateUtility.getUtilDate(strDate, "ddMMyyyy"));
					}
					item.setLimitBranch(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.LmtBranch",indices).toString());
					item.setRepeatThousands(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.RptThousands",indices).toString());
					item.setCheckLimit(pcmlDoc.getValue(pcml +  ".@RSPDTA.DETDTA.CheckLimit",indices).toString());					
					item.setSeqNum(String.valueOf(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.SeqNum",indices)));

					list.add(item);
				}
				
				custLimitMap.put("CustLimitList", list);

			} else {
				errorMessage = pcmlDoc.getValue(pcml + ".@ERPRM").toString();
				throw new InterfaceException("9999",errorMessage);
			}

		}catch (ConnectionPoolException e){
			logger.error("Exception: ", e);
			throw new InterfaceException("9999","Host Connection Failed.. Please contact administrator ");
		} catch (Exception e) {
			logger.error("Exception: ", e);
			throw new InterfaceException("9999",e.getMessage());
		} finally {	
				this.hostConnection.closeConnection(as400);
		}

		logger.debug("Leaving");
		return custLimitMap;
	}

	/**
	 * Method for Fetching List of Limit details depends on Parameter key fields
	 * @param CustomerLimit
	 * @return List<CustomerLimit>
	 * @throws 	CustomerLimitProcessException
	 * */
	@Override
	public List<CustomerLimit> fetchLimitDetails(CustomerLimit custLimit) throws InterfaceException {
		logger.debug("Entering");

		AS400 as400 = null;
		ProgramCallDocument pcmlDoc = null;
		String pcml = "PFFCUSTLMT";			//get List of Customer Limits

		int[] indices = new int[1]; 	// Indices for access array value
		int dsRspCount;              	// Number of records returned 
		List<CustomerLimit> list = new ArrayList<CustomerLimit>();
		CustomerLimit item = null;
		String errorMessage = null;

		try {

			as400 = this.hostConnection.getConnection();

			pcmlDoc = new ProgramCallDocument(as400, pcml);

			pcmlDoc.setValue(pcml + ".@REQDTA.CustMnemonic", custLimit.getCustMnemonic()); 	// Customer mnemonic
			pcmlDoc.setValue(pcml + ".@REQDTA.CustLocation", custLimit.getCustLocation()); 	// Customer Location
			pcmlDoc.setValue(pcml + ".@REQDTA.LmtCategory", custLimit.getLimitCategory()); 	// Customer Limit Category

			pcmlDoc.setValue(pcml + ".@RSPDTA.@NOREQ", 0); 
			pcmlDoc.setValue(pcml + ".@ERCOD", "0000"); 	
			pcmlDoc.setValue(pcml + ".@ERPRM", ""); 	

			logger.debug(" Before PCML Call");
			getHostConnection().callAPI(pcmlDoc, pcml);
			logger.debug(" After PCML Call");

			if ("0000".equals(pcmlDoc.getValue(pcml + ".@ERCOD").toString())) {	
				dsRspCount = Integer.parseInt(pcmlDoc.getValue(pcml + ".@RSPDTA.@NOREQ").toString());

				for (indices[0] = 0; indices[0] < dsRspCount; indices[0]++){
					item = new CustomerLimit();

					String strDate = null;
					
					item.setCustCountry(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.LmtCountry",indices).toString());
					item.setCustCountryDesc(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.LmtCnaDesc",indices).toString());
					item.setCustGrpCode(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.LmtGrpCode",indices).toString());
					item.setCustGrpDesc(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.LmtGrpDesc",indices).toString());
					item.setLimitCategory(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.LmtCategory",indices).toString());
					item.setLimitCategoryDesc(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.LmtCtgDesc",indices).toString());
					item.setLimitCurrency(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.LmtCurrency",indices).toString());

					strDate = StringUtils.trimToEmpty(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.LmtExpiry", indices).toString());	
					item.setLimitExpiry(DateUtility.convertDateFromAS400(new BigDecimal(strDate)));
					
					item.setLimitAmount(new BigDecimal(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.LmtAmount",indices).toString()));
					item.setAvailAmount(new BigDecimal(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.AvailAmount",indices).toString()));
					item.setRiskAmount(new BigDecimal(pcmlDoc.getValue(pcml +  ".@RSPDTA.DETDTA.RiskAmount",indices).toString()));					
					/*item.setErrorId(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.ErrorId",indices).toString());
					item.setErrorMsg(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.ErrorMsg",indices).toString());*/

					list.add(item);
				}

			} else {
				errorMessage = pcmlDoc.getValue(pcml + ".@ERPRM").toString();
				throw new InterfaceException("9999",errorMessage);
			}

		}catch (ConnectionPoolException e){
			logger.error("Exception: ", e);
			throw new InterfaceException("9999","Host Connection Failed.. Please contact administrator ");
		}catch (Exception e) {
			logger.error("Exception: ", e);
			throw new InterfaceException("9999",e.getMessage());
		} finally {	
				this.hostConnection.closeConnection(as400);
		}

		logger.debug("Leaving");
		return list;
	}
	
	@Override
	public CustomerLimitPosition fetchLimitEnqDetails(CustomerLimitPosition custLimitSummary) throws InterfaceException {
		logger.debug("Entering");
		logger.debug("Leaving");
		return null;
	}
	
	/**
	 * Method for Fetching List of Group Limit details depends on Parameter key fields
	 * @param CustomerLimit
	 * @return List<CustomerLimit>
	 * @throws 	CustomerLimitProcessException
	 * */
	@Override
	public List<CustomerLimit> fetchGroupLimitDetails(CustomerLimit custLimit) throws InterfaceException {
		logger.debug("Entering");

		AS400 as400 = null;
		ProgramCallDocument pcmlDoc = null;
		String pcml = "PFFGLMT";			//get Group limits

		int[] indices = new int[1]; 	// Indices for access array value
		int dsRspCount;              	// Number of records returned 
		List<CustomerLimit> list = new ArrayList<CustomerLimit>();
		CustomerLimit item = null;
		String errorMessage = null;

		try {


			as400 = this.hostConnection.getConnection();

			pcmlDoc = new ProgramCallDocument(as400, pcml);

			pcmlDoc.setValue(pcml + ".@REQDTA.CustMnemonic", custLimit.getCustMnemonic()); 	// Customer mnemonic
			pcmlDoc.setValue(pcml + ".@REQDTA.CustLocation", custLimit.getCustLocation()); 	// Customer Location
			pcmlDoc.setValue(pcml + ".@REQDTA.LmtCategory", custLimit.getLimitCategory()); 	// Customer Limit Category

			pcmlDoc.setValue(pcml + ".@RSPDTA.@NOREQ", 0); 
			pcmlDoc.setValue(pcml + ".@ERCOD", "0000"); 	
			pcmlDoc.setValue(pcml + ".@ERPRM", ""); 	

			logger.debug(" Before PCML Call");
			getHostConnection().callAPI(pcmlDoc, pcml);
			logger.debug(" After PCML Call");

			if ("0000".equals(pcmlDoc.getValue(pcml + ".@ERCOD").toString())) {	
				dsRspCount = Integer.parseInt(pcmlDoc.getValue(pcml + ".@RSPDTA.@NOREQ").toString());

				for (indices[0] = 0; indices[0] < dsRspCount; indices[0]++){
					item = new CustomerLimit();
					
					String strDate = null;
					
					item.setLimitCategory(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.LmtCategory",indices).toString());
					item.setGroupLimit(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.GroupLimit",indices).toString());
					item.setLimitCurrency(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.LmtCurrency",indices).toString());
										
					strDate = StringUtils.trimToEmpty(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.LmtExpiry", indices).toString());	
					
					if (!("0".equals(strDate) || StringUtils.isEmpty(strDate))) {
						item.setLimitExpiry(DateUtility.getUtilDate(strDate, "ddMMyyyy"));
					} 
										
					item.setLimitAmount(new BigDecimal(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.LmtAmount",indices).toString()));
					item.setAvailAmount(new BigDecimal(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.AvailAmount",indices).toString()));
					item.setRiskAmount(new BigDecimal(pcmlDoc.getValue(pcml +  ".@RSPDTA.DETDTA.RiskAmount",indices).toString()));					
					item.setErrorId(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.ErrorId",indices).toString());
					item.setErrorMsg(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.ErrorMsg",indices).toString());
										
					list.add(item);
				}

			} else {
				errorMessage = pcmlDoc.getValue(pcml + ".@ERPRM").toString();
				throw new InterfaceException("9999",errorMessage);
			}

		} catch (Exception e) {
			logger.error("Exception: ", e);
			throw new InterfaceException("9999",e.getMessage());
		} finally {	
				this.hostConnection.closeConnection(as400);
		}

		logger.debug("Leaving");
		return list;
	}

	/**
	 * Method for fetching the limit details from Equation
	 * 
	 */
	@Override
	public CustomerLimitDetail getLimitDetails(String limitRef,	String branchCode) throws InterfaceException {
		return null;
	}

	@Override
	public CustomerLimitUtilization doPredealCheck(
			CustomerLimitUtilization limitUtilReq) throws InterfaceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CustomerLimitUtilization doReserveUtilization(
			CustomerLimitUtilization limitUtilReq) throws InterfaceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CustomerLimitUtilization doOverrideAndReserveUtil(
			CustomerLimitUtilization limitUtilReq) throws InterfaceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CustomerLimitUtilization doConfirmReservation(
			CustomerLimitUtilization limitUtilReq) throws InterfaceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CustomerLimitUtilization doCancelReservation(
			CustomerLimitUtilization limitUtilReq) throws InterfaceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CustomerLimitUtilization doCancelUtilization(
			CustomerLimitUtilization limitUtilReq) throws InterfaceException {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public CustomerLimitUtilization doLimitAmendment(
			CustomerLimitUtilization limitUtilReq) throws InterfaceException {
		// TODO Auto-generated method stub
		return null;
	}
	
	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public void setHostConnection(HostConnection hostConnection) {
		this.hostConnection = hostConnection;
	}
	public HostConnection getHostConnection() {
		return hostConnection;
	}

}
