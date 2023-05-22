package com.pennant.webui.finance.externalagreement;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.zkoss.util.media.AMedia;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zul.A;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Window;

import com.jayway.jsonpath.JsonPath;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.service.masters.MasterDefService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.batchupload.fileprocessor.BatchUploadProcessorConstatnt;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.web.util.MessageUtil;

import net.minidev.json.JSONArray;

public class Comm360DialogCtrl extends GFCBaseCtrl<DocumentDetails> {

	private static final long serialVersionUID = 1698046844035290444L;
	private static final Logger logger = LogManager.getLogger(Comm360DialogCtrl.class);

	protected Window window_FetchExternalAgreementDialog;
	protected Groupbox finBasicdetails;
	protected Listbox listBoxAgreements;
	protected MasterDefService masterDefService;
	private FinanceDetail financeDetail;
	private FinanceMain financeMain;

	public Comm360DialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "COMM360Dialog";
	}

	public void onCreate$window_FetchExternalAgreementDialog(Event event) {
		logger.debug(Literal.ENTERING);

		setPageComponents(window_FetchExternalAgreementDialog);

		if (arguments.containsKey("finHeaderList") && arguments.get("finHeaderList") != null) {
			appendFinBasicDetails(arguments.get("finHeaderList"));
		}

		if (arguments.containsKey("financeDetail")) {
			financeDetail = (FinanceDetail) arguments.get("financeDetail");
		}

		if (financeDetail != null) {
			financeMain = financeDetail.getFinScheduleData().getFinanceMain();
		}

		try {

			if (isWorkFlowEnabled() && !enqiryModule) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(), this.pageRightName);
			}

			doSetFieldProperties();
			checkRights();
			doShowDialog();
		} catch (Exception e) {
			closeDialog();
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	private void appendFinBasicDetails(Object finHeaderList) {
		logger.debug(Literal.ENTERING);
		try {
			final HashMap<String, Object> map = new HashMap<>();
			map.put("parentCtrl", this);
			map.put("finHeaderList", finHeaderList);
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/FinBasicDetails.zul", this.finBasicdetails,
					map);
		} catch (Exception e) {
			logger.debug(e);
		}
		logger.debug(Literal.LEAVING);
	}

	private void doSetFieldProperties() {

	}

	private void doShowDialog() {
		logger.debug(Literal.ENTERING);

		if (enqiryModule) {
			this.btnCtrl.setBtnStatus_Enquiry();
		}

		doFillDocuments();
		logger.debug(Literal.LEAVING);
	}

	private void doFillDocuments() {

		this.listBoxAgreements.getItems().clear();
		if (financeMain != null && StringUtils.isNotBlank(financeMain.getFinType())) {
			String finType = financeMain.getFinType();
			Map<String, String> agreements = masterDefService
					.getMasterDef("COMM_".concat(StringUtils.trimToEmpty(finType)));
			if (!agreements.isEmpty()) {
				for (Map.Entry<String, String> entry : agreements.entrySet()) {

					Listitem listitem = new Listitem();
					Listcell listcell;

					listcell = new Listcell(entry.getKey());
					listitem.appendChild(listcell);

					listcell = new Listcell();
					A docLink = new A();
					docLink.setLabel(entry.getKey());
					docLink.addForward("onClick", self, "onClickDownload", entry);
					docLink.setStyle("text-decoration:underline;");
					listcell.appendChild(docLink);
					listcell.setParent(listitem);

					listitem.setAttribute("document", entry);
					this.listBoxAgreements.appendChild(listitem);

				}
			}
		}
	}

	/**
	 * To Download the Agreement
	 */
	public void onClickDownload(ForwardEvent event) {
		logger.debug(Literal.ENTERING);

		if (financeMain == null || (financeMain != null && StringUtils.isBlank(financeMain.getFinReference()))) {
			MessageUtil.showMessage("LAN Reference not yet generated");
			return;
		}

		Map.Entry<String, String> map = (Entry<String, String>) event.getData();

		JSONObject reqObj = new JSONObject();
		reqObj.put("lanNumber", financeMain.getFinReference());
		reqObj.put("reportType", map.getValue());

		String req = reqObj.toString();
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		sb.append(req);
		sb.append("]");
		try {
			WebClient client = null;
			client = getClient(App.getProperty("comm360.api.url"), String.valueOf(Math.random()));
			Response response = client.post(sb.toString());

			if (response != null) {
				String responseBody = response.readEntity(String.class);
				JSONArray jsonImgArray = JsonPath.read(responseBody, "$.listOfLanAndBase64[:1].base64String");
				String image = jsonImgArray.get(0).toString();
				byte[] decoder = Base64.getDecoder().decode(image);
				InputStream data = new ByteArrayInputStream(decoder);
				AMedia amedia = new AMedia(map.getValue(), "pdf", "application/pdf", data);
				String message = JsonPath.read(responseBody, "$.message");
				Filedownload.save(amedia);
				MessageUtil.showMessage(message);
			}
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			MessageUtil.showMessage("Unable to generate");
		}
		logger.debug(Literal.LEAVING);
	}

	private WebClient getClient(String serviceEndPoint, String messageId) {
		WebClient client = null;
		try {
			client = WebClient.create(serviceEndPoint);
			client.accept(MediaType.APPLICATION_JSON);
			client.type(MediaType.APPLICATION_JSON);
			client.header("MessageId", String.valueOf(Math.random()));
			client.header("RequestTime", DateUtil.getSysDate(PennantConstants.APIDateFormatter));

		} catch (Exception e) {
			logger.error(BatchUploadProcessorConstatnt.EXCEPTION, e);
		}
		return client;
	}

	public void setMasterDefService(MasterDefService masterDefService) {
		this.masterDefService = masterDefService;
	}
}
