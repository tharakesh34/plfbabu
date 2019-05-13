package com.pennant.webui.sampling;

import java.text.ParseException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Window;

import com.pennant.backend.model.extendedfield.ExtendedFieldHeader;
import com.pennant.backend.model.extendedfield.ExtendedFieldRender;
import com.pennant.backend.model.solutionfactory.ExtendedFieldDetail;
import com.pennant.backend.util.CollateralConstants;
import com.pennant.backend.util.ExtendedFieldConstants;
import com.pennant.component.extendedfields.ExtendedFieldCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.pff.sampling.model.Sampling;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.service.sampling.SamplingService;

public class SamplingExtFieldCaptureDialogCtrl extends GFCBaseCtrl<Sampling> {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(SamplingExtFieldCaptureDialogCtrl.class);

	protected Window window_SamplingExtendedFieldDialog;

	private Sampling sampling;
	private transient SamplingDialogCtrl samplingDialogCtrl;
	private ExtendedFieldCtrl extendedFieldCtrl = null;
	protected Tab samplingExtFields;
	protected Tabpanel samplingExtFieldsTabPanel;
	private Map<String, ExtendedFieldRender> extFieldRenderList = new LinkedHashMap<>();
	@Autowired
	private transient SamplingService samplingService;
	protected String sLinkId;

	public SamplingExtFieldCaptureDialogCtrl() {
		super();
	}

	@SuppressWarnings("unchecked")
	public void onCreate$window_SamplingExtendedFieldDialog(Event event) throws Exception {
		logger.debug(Literal.ENTERING);

		setPageComponents(window_SamplingExtendedFieldDialog);

		try {
			this.sampling = (Sampling) arguments.get("sampling");
			this.samplingDialogCtrl = (SamplingDialogCtrl) arguments.get("samplingDialogCtrl");

			if (this.sampling == null) {
				throw new Exception(Labels.getLabel("error.unhandled"));
			}

			if (arguments.containsKey("extFieldRenderList") && arguments.get("extFieldRenderList") != null) {
				this.extFieldRenderList.putAll((Map<String, ExtendedFieldRender>) arguments.get("extFieldRenderList"));
			}

			doLoadWorkFlow(this.sampling.isWorkflow(), this.sampling.getWorkflowId(), this.sampling.getNextTaskId());
			if (arguments.get("enqiryModule") != null) {
				enqiryModule = (boolean) arguments.get("enqiryModule");
			}

			if (isWorkFlowEnabled() && !enqiryModule) {
				//this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(), this.pageRightName);
			}

			doCheckRights();
			doShowDialog(sampling);

		} catch (Exception e) {
			closeDialog();
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	public void doShowDialog(Sampling sampling) {
		logger.debug(Literal.ENTERING);

		renderExtendedFieldDetails(sampling);
		window_SamplingExtendedFieldDialog.setWidth("80%");
		window_SamplingExtendedFieldDialog.setHeight("80%");
		setDialog(DialogType.MODAL);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set Visible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug(Literal.ENTERING);

		this.btnSave.setVisible(getUserWorkspace().isAllowed("SamplingDialog_CollateralExtFields"));

		logger.debug(Literal.LEAVING);
	}

	private void renderExtendedFieldDetails(Sampling sampling) {
		logger.debug(Literal.ENTERING);
		try {
			extendedFieldCtrl = new ExtendedFieldCtrl();
			ExtendedFieldHeader extendedFieldHeader = extendedFieldCtrl.getExtendedFieldHeader(
					CollateralConstants.MODULE_NAME, sampling.getCollateral().getCollateralType(),
					ExtendedFieldConstants.EXTENDEDTYPE_TECHVALUATION);

			if (extendedFieldHeader == null) {
				return;
			}
			// Extended Field Details
			StringBuilder table = new StringBuilder();
			table.append(CollateralConstants.VERIFICATION_MODULE);
			table.append("_");
			table.append(extendedFieldHeader.getSubModuleName());
			table.append("_tv");

			List<ExtendedFieldDetail> detailsList = extendedFieldHeader.getExtendedFieldDetails();
			int fieldSize = 0;
			if (detailsList != null && !detailsList.isEmpty()) {
				fieldSize = detailsList.size();
				if (fieldSize != 0) {
					fieldSize = fieldSize / 2;
					fieldSize = fieldSize + 1;
				}
			}

			for (ExtendedFieldDetail extendedFieldDetail : detailsList) {
				if (extendedFieldDetail.isAllowInRule()) {
					sampling.getCollateralFieldsForRule().add(extendedFieldDetail.getFieldName());
				}
			}

			ExtendedFieldRender extendedFieldRender = null;

			String CollateralRef = sampling.getCollateral().getCollateralRef();
			int seqNo = sampling.getCollateral().getSeqNo();
			long linkId = samplingService.getCollateralLinkId(sampling.getId(), CollateralRef);
			sLinkId = "S".concat(String.valueOf(linkId));

			if (extFieldRenderList.containsKey(sLinkId.concat("-").concat(String.valueOf(seqNo)))) {
				extendedFieldRender = extFieldRenderList.get(sLinkId.concat("-").concat(String.valueOf(seqNo)));
				extendedFieldCtrl.setExtendedFieldRender(extendedFieldRender);
			} else if (extFieldRenderList.containsKey(CollateralRef)) {
				extendedFieldRender = extFieldRenderList.get(CollateralRef);
				if (extendedFieldRender.getSeqNo() == seqNo) {
					extendedFieldCtrl.setExtendedFieldRender(extendedFieldRender);
				}
			} else {
				extendedFieldRender = extendedFieldCtrl.getExtendedFieldRender(
						sLinkId.concat("-").concat(String.valueOf(seqNo)), table.toString().toLowerCase(), "_view");
			}

			extendedFieldCtrl.setTabpanel(samplingExtFieldsTabPanel);
			extendedFieldCtrl.setTab(this.samplingExtFields);
			sampling.setExtendedFieldHeader(extendedFieldHeader);
			sampling.setExtendedFieldRender(extendedFieldRender);

			if (sampling.getBefImage() != null) {
				sampling.getBefImage().setExtendedFieldHeader(extendedFieldHeader);
				sampling.getBefImage().setExtendedFieldRender(extendedFieldRender);
			}
			extendedFieldCtrl.setCcyFormat(2);
			extendedFieldCtrl.setReadOnly(this.enqiryModule);
			extendedFieldCtrl.setReadOnly(isReadOnly("SamplingDialog_CollateralExtFields"));
			extendedFieldCtrl.setWindow(this.window_SamplingExtendedFieldDialog);
			extendedFieldCtrl.render();
			this.samplingExtFields.setLabel(Labels.getLabel("label_LegalVerificationDialog_VerificationDetails.value"));
			this.samplingExtFieldsTabPanel.setHeight((fieldSize * 37) + "px");
		} catch (Exception e) {
			closeDialog();
			logger.error(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);
	}

	public void onClick$btnSave() {
		doSave();
	}

	private void doSave() {
		logger.debug(Literal.ENTERING);

		final Sampling sampling = new Sampling();
		BeanUtils.copyProperties(this.sampling, sampling);
		doWriteComponentsToBean(sampling);
		closeDialog();
		samplingDialogCtrl.doFillExtendedFileds(extFieldRenderList);

		logger.debug(Literal.LEAVING);
	}

	private void doWriteComponentsToBean(Sampling sampling) {
		logger.debug(Literal.ENTERING);
		if (sampling.getExtendedFieldHeader() != null) {
			try {
				ExtendedFieldRender fields = extendedFieldCtrl.save(true);
				fields.setSeqNo(sampling.getCollateral().getSeqNo());
				sampling.setExtendedFieldRender(fields);
			} catch (ParseException e) {
				logger.debug(Literal.EXCEPTION);
			}
		}
		int seqNo = sampling.getCollateral().getSeqNo();
		if (!extFieldRenderList.containsKey(sLinkId.concat("-").concat(String.valueOf(seqNo)))) {
			this.extFieldRenderList.put(sLinkId.concat("-").concat(String.valueOf(seqNo)),
					sampling.getExtendedFieldRender());
		} else if (extFieldRenderList.containsKey(sLinkId.concat("-").concat(String.valueOf(seqNo)))) {
			extFieldRenderList.replace(sLinkId.concat("-").concat(String.valueOf(seqNo)),
					sampling.getExtendedFieldRender());
		}
		logger.debug(Literal.LEAVING);
	}

	public void onClick$btnClose(Event event) {
		doClose(this.btnSave.isVisible());
	}

}
