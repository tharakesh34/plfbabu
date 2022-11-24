package com.pennanttech.pff.file;

import java.util.Arrays;
import java.util.List;

public class UploadContants {
	private UploadContants() {
		super();
	}

	public enum Status {
		DEFAULT(0),

		DOWNLOADED(1),

		APPROVED(2),

		REJECTED(3),

		IMPORT_IN_PROCESS(4),

		IMPORTED(5),

		IN_PROCESS(6),

		PROCESS_FAILED(7),

		IMPORT_FAILED(8);

		private int status;

		private Status(int status) {
			this.status = status;
		}

		public int getValue() {
			return this.status;
		}

		public static Status value(int status) {
			List<Status> list = Arrays.asList(Status.values());

			for (Status it : list) {
				if (it.getValue() == status) {
					return it;
				}
			}

			return null;
		}
	}

	public static final boolean ACTIVE = true;
	public static final boolean INACTIVE = false;

	public static final int ATTEMPSTATUS_INPROCESS = 1;
	public static final int ATTEMPSTATUS_DONE = 2;

	public static final int EXCEL_MAX_ALLOWED_RECORDS = 10000;
}
