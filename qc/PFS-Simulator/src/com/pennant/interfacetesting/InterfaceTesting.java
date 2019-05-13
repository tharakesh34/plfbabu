package com.pennant.interfacetesting;
import java.io.FileInputStream;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.impl.llom.util.AXIOMUtil;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import com.pff.framework.logging.Log;
import com.pff.framework.logging.LogFactory;
import com.pff.framework.util.PFFXmlUtil;
import com.pff.service.CreateCIFService;
import com.pff.vo.PFFMQHeaderVo;


public class InterfaceTesting {

	private static Log LOG = LogFactory.getLog(InterfaceTesting.class);
	
	public static void main(String[] args) throws Exception {
		LOG.entering("Testing");
		OMElement requestData = AXIOMUtil.stringToOM(StringUtils.trimToEmpty(getData()));
		System.out.println(requestData);
		PFFMQHeaderVo headerVo = PFFXmlUtil.retrieveHeader(requestData);
		// AccountService service=new AccountService();
		// CreateAccountService service=new CreateAccountService();
		// ReleaseCIFService service= new ReleaseCIFService();
		// ReserveCIFService service= new ReserveCIFService();
		 CreateCIFService service= new CreateCIFService();
		// CustomerService service=new CustomerService();
		// CustomerLimitService service=new CustomerLimitService();
		//   DDAService service=new DDAService();
		// DDAAmendmentService service=new DDAAmendmentService();
		// CustomerDedupService service=new CustomerDedupService();
		// CustomerLimitPositionService service=new CustomerLimitPositionService();
		// CustomerLimitUtilizationService service=new CustomerLimitUtilizationService();
		// AccountHoldService service=new AccountHoldService();
		// AddHoldService  service=new AddHoldService();
		// RemoveHoldService service=new RemoveHoldService();
		// CollateralMarkService service=new CollateralMarkService();
		// CollateralDeMarkService service=new CollateralDeMarkService();
		//   AccountPostingsService service=new AccountPostingsService();
		System.out.println(service.processRequest(requestData, headerVo));
	}

	private static String getData() throws Exception{
		
		// FileInputStream stream = new FileInputStream("E:/PFF PROJECT/Req-Res/CustomerDedup-Req-Res/Customer Duplicate Chek_Req.xml");
		// FileInputStream stream = new FileInputStream("E:/PFF PROJECT/Req-Res/AccountsFetch-Req-Res/20 - FetchAllAccountsRequest.xml");
		// FileInputStream stream = new FileInputStream("E:/PFF PROJECT/Req-Res/FetchAccount-Req-Res/FetchAccountRequest.xml");
		// FileInputStream stream = new FileInputStream("E:/PFF PROJECT/Req-Res/CreateAccount-Req-Res/Create Account T24 Req.xml");
		// FileInputStream stream = new FileInputStream("E:/PFF PROJECT/Req-Res/ReleaseCIF-Req-Resp/ReleaseCIF_Req.xml");
		// FileInputStream stream = new FileInputStream("E:/PFF PROJECT/Req-Res/ReserveCIF-Req-Res/ReserveCIF_Req.xml");
		   FileInputStream stream = new FileInputStream("E:/PFF PROJECT/Req-Res/CreateCIF-Req-Res/CreateCIFReq.xml");
		// FileInputStream stream = new FileInputStream("E:/PFF PROJECT/Req-Res/CustomerLimitDetail/CustomerLimiDetail_Req.xml");
		// FileInputStream stream = new FileInputStream("E:/PFF PROJECT/Req-Res/CustomerFetch-Req-Res/GetCustomerDetailsMDM_Request_GBO.xml");
		// FileInputStream stream = new FileInputStream("E:/PFF PROJECT/Req-Res/UAEDDS-Req-Res/DDARequest.xml");
		// FileInputStream stream = new FileInputStream("E:/PFF PROJECT/Req-Res/DDAAmmendment-Req-Res/DDAAmendmentRequest.xml");
		// FileInputStream stream = new FileInputStream("E:/PFF PROJECT/Req-Res/CustomerLimitSummary-Req-Res/CustomerLimitSummary_Req.xml");
		// FileInputStream stream = new FileInputStream("E:/PFF PROJECT/Req-Res/LimitUtilization-Req-Res/CustomerLimitUtilization_Req.xml");
		// FileInputStream stream = new FileInputStream("E:/PFF PROJECT/Req-Res/AddHold-Req-Res/AddHoldRequest.xml");
		// FileInputStream stream = new FileInputStream("E:/PFF PROJECT/Req-Res/RemoveHold-Req-Res/RemoveHoldRequest.xml");
		// FileInputStream stream = new FileInputStream("E:/PFF PROJECT/Req-Res/Collateral/CollateralBlockingRequest_Account.xml");
		// FileInputStream stream = new FileInputStream("E:/PFF PROJECT/Req-Res/Collateral/CollateralBlockingRequest_Deposit.xml");
		// FileInputStream stream = new FileInputStream("E:/PFF PROJECT/Req-Res/Collateral/CollateralUnblockingRequest_Account.xml");
		// FileInputStream stream = new FileInputStream("E:/PFF PROJECT/Req-Res/Collateral/CollateralUnblockingRequest_Deposit.xml");
		// FileInputStream stream = new FileInputStream("E:/PFF PROJECT/Req-Res/AccountPostings/AccountPostingsRequest.xml");
		return IOUtils.toString(stream);
	}
	

}
