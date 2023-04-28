package com.pennant.pff.upload.list;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.zkoss.zul.Window;

import com.pennant.backend.util.PennantConstants;
import com.pennant.pff.upload.model.FieUploadDTO;
import com.pennant.pff.upload.model.FileUploadHeader;
import com.pennant.pff.upload.service.UploadService;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.pagging.PagedListWrapper;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.file.UploadContants.Status;
import com.pennanttech.pff.file.UploadTypes;

public abstract class AUploadListCtrl extends GFCBaseListCtrl<FileUploadHeader> {
	private static final long serialVersionUID = 130258732943933059L;

	@Autowired
	private DataSource dataSource;
	private UploadTypes type;

	@Autowired
	@Qualifier("pagedListWrapper")
	private PagedListWrapper<FileUploadHeader> listWrapper;

	private transient UploadService uploadService;

	protected AUploadListCtrl(UploadService uploadService, UploadTypes type) {
		super();
		this.uploadService = uploadService;
		this.type = type;
	}

	protected void onCreate(String stage, Window uploadListWindow) {
		logger.debug(Literal.ENTERING);

		FieUploadDTO uploadDTO = new FieUploadDTO();

		uploadDTO.setWindow(uploadListWindow);
		uploadDTO.setUserId(getUserWorkspace().getUserId());
		uploadDTO.setService(this.uploadService);
		uploadDTO.setDataSource(this.dataSource);
		uploadDTO.setListWrapper(this.listWrapper);
		uploadDTO.setStage(stage);
		List<String> workFlowRoles = getWorkFlowRoles();

		List<String> roles = new ArrayList<>();

		for (String role : workFlowRoles) {
			if (stage.equals("A") && role.contains("APPROVER")) {
				roles.add(role);
			}
		}

		uploadDTO.setRoleCodes(roles);

		FileUploadHeader header = new FileUploadHeader();

		LoggedInUser loggedInUser = getUserWorkspace().getLoggedInUser();
		header.setUserDetails(loggedInUser);
		header.setLastMntBy(loggedInUser.getUserId());
		if ("M".equals(stage)) {
			header = getUploadHeader();
			header.setCreatedBy(loggedInUser.getUserId());
			header.setCreatedByName(loggedInUser.getUserName());
		} else {
			header.setType(this.type.name());
		}

		uploadDTO.setHeader(header);

		new FileUploadList(uploadDTO, this.type);

		logger.debug(Literal.LEAVING);
	}

	private FileUploadHeader getUploadHeader() {
		FileUploadHeader header = uploadService.getUploadHeader(this.moduleCode);

		doLoadWorkFlow(header.isWorkflow(), header.getWorkflowId(), header.getNextTaskId());

		prepareHeader(header);

		return header;
	}

	private void prepareHeader(FileUploadHeader header) {
		header.setNewRecord(true);
		header.setProgress(Status.IN_PROCESS.getValue());

		String taskId = getTaskId(getRole());
		String nextTaskId;

		if ("Save".equals(header.getUserAction())) {
			nextTaskId = taskId + ";";
		} else {
			nextTaskId = StringUtils.trimToEmpty(header.getNextTaskId());

			nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
			if ("".equals(nextTaskId)) {
				nextTaskId = getNextTaskIds(taskId, header);
			}
		}

		if (!StringUtils.isBlank(nextTaskId)) {
			String[] nextTasks = nextTaskId.split(";");

			if (nextTasks != null && nextTasks.length > 0) {
				for (int i = 0; i < nextTasks.length; i++) {

					if (nextRoleCode.length() > 1) {
						nextRoleCode = nextRoleCode.concat(",");
					}
					nextRoleCode = getTaskOwner(nextTasks[i]);
				}
			} else {
				nextRoleCode = getTaskOwner(nextTaskId);
			}
		}

		header.setType(this.type.name());
		header.setRoleCode(getRole());
		header.setTaskId(taskId);
		header.setNextTaskId(nextTaskId);
		header.setNextRoleCode(nextRoleCode);
		header.setVersion(header.getVersion() + 1);
		header.setRecordType(PennantConstants.RECORD_TYPE_NEW);
		header.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		header.setCreatedOn(new Timestamp(System.currentTimeMillis()));
	}
}
