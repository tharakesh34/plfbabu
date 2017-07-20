package com.pennant.mq.process.impl;

import org.apache.log4j.Logger;
import org.jaxen.JaxenException;

import com.pennant.coreinterface.model.dda.DDAAmendment;
import com.pennant.coreinterface.model.dda.DDACancellation;
import com.pennant.coreinterface.model.dda.DDARegistration;
import com.pennant.coreinterface.model.dda.DDAUpdate;
import com.pennant.coreinterface.process.DDAProcess;
import com.pennant.mq.processutil.DDAAmendmentProcess;
import com.pennant.mq.processutil.DDACancelProcess;
import com.pennant.mq.processutil.DDARequestProcess;
import com.pennant.mq.processutil.DDAUpdateProcess;
import com.pennant.mq.util.InterfaceMasterConfigUtil;
import com.pennanttech.pennapps.core.InterfaceException;

public class DDAServiceImpl implements DDAProcess {
	private static final Logger logger = Logger.getLogger(DDAServiceImpl.class);
	
	private DDARequestProcess ddaRequestProcess;
	private DDAAmendmentProcess ddaAmendmentProcess;
	private DDAUpdateProcess ddaUpdateProcess;
	private DDACancelProcess ddaCancelProcess;

	public DDAServiceImpl() {
		super();
	}
	
	/**
	 * Send the DDARequest Request to MQ <br>
	 * 
	 * sendDDARequest method do the following steps.<br>
	 *  1)  Send DDA_Request Request to MQ<br>
	 *  2)  Receive Response from MQ
	 *  
	 *  @return DDARequestDetail
	 */
	@Override
	public DDARegistration sendDDARequest(DDARegistration ddsRequest) throws InterfaceException {
		logger.debug("Entering");

		DDARegistration ddsReply = null;
		try {
			ddsReply = getDdaRequestProcess().sendDDARequest(ddsRequest, InterfaceMasterConfigUtil.DDA_REQ);
		} catch(JaxenException jxe) {
			logger.warn("Exception: ", jxe);
			throw new InterfaceException("PTI9008", jxe.getMessage());
		}
		logger.debug("Leaving");

		return ddsReply;
	}

	/**
	 * Send the DDAAmendmnet Request MQ <br>
	 * 
	 * sendDDAAmendment method do the following steps.<br>
	 *  1)  Send DDA_Amendment Request to MQ<br>
	 *  2)  Receive Response from MQ
	 *  
	 *  @return DDAAmendmentDetail
	 */
	@Override
	public DDAAmendment sendDDAAmendment(DDAAmendment ddaAmendmentReq) throws InterfaceException {
		logger.debug("Entering");

		DDAAmendment ddaAmendmentReply = getDdaAmendmentProcess().sendDDAAmendment(ddaAmendmentReq, 
				InterfaceMasterConfigUtil.DDA_AMEND);

		logger.debug("Leaving");

		return ddaAmendmentReply;
	}

	/**
	 * Send the DDAUpdate Request MQ <br>
	 * 
	 * sendDDAUpdate method do the following steps.<br>
	 *  1)  Send DDA_Update Request to MQ<br>
	 *  2)  Receive Response from MQ
	 *  
	 *  @return DDAUpdateDetail
	 */
	@Override
	public DDAUpdate sendDDAUpdate(DDAUpdate ddaUpdateReq) throws InterfaceException {
		logger.debug("Entering");

		DDAUpdate ddaUpdateReply = getDdaUpdateProcess().sendDDAUpdate(ddaUpdateReq, InterfaceMasterConfigUtil.DDA_UPDATE);

		logger.debug("Leaving");

		return ddaUpdateReply;
	}
	
	/**
	 * This method is for Cancel DDA Registration
	 * 
	 * cancelDDARegistration method do the following steps.<br>
	 *  1)  Send DDA.CANCELLATION Request to interface<br>
	 *  2)  Receive Response from interface
	 *  
	 *  @return DDACancellation
	 */
	@Override
	public DDACancellation cancelDDARegistration(DDACancellation ddaCancellationReq) throws InterfaceException {
		logger.debug("Entering");

		DDACancellation ddaCancellationRply = getDdaCancelProcess().cancelDDARegistration(ddaCancellationReq, 
				InterfaceMasterConfigUtil.DDA_CANCELLATION);

		logger.debug("Leaving");

		return ddaCancellationRply;
	}
	
	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public DDARequestProcess getDdaRequestProcess() {
		return ddaRequestProcess;
	}
	public void setDdaRequestProcess(DDARequestProcess ddaRequestProcess) {
		this.ddaRequestProcess = ddaRequestProcess;
	}

	public DDAAmendmentProcess getDdaAmendmentProcess() {
		return ddaAmendmentProcess;
	}
	public void setDdaAmendmentProcess(DDAAmendmentProcess ddaAmendmentProcess) {
		this.ddaAmendmentProcess = ddaAmendmentProcess;
	}

	public DDAUpdateProcess getDdaUpdateProcess() {
		return ddaUpdateProcess;
	}
	public void setDdaUpdateProcess(DDAUpdateProcess ddaUpdateProcess) {
		this.ddaUpdateProcess = ddaUpdateProcess;
	}

	public DDACancelProcess getDdaCancelProcess() {
		return ddaCancelProcess;
	}

	public void setDdaCancelProcess(DDACancelProcess ddaCancelProcess) {
		this.ddaCancelProcess = ddaCancelProcess;
	}

}
