package com.pennant.pff.mandate;

import org.apache.commons.lang3.StringUtils;

public enum MandateStatus {
	_FINANCE("FINANCE"),

	_NEW("NEW"),

	_AC("AC"),

	_APPROVED("APPROVED"),

	_REJECTED("REJECTED"),

	_HOLD("HOLD"),

	_RELEASE("RELEASE"),

	_CANCEL("CANCEL"),

	_INPROCESS("INPROCESS"),

	_ACK("ACK"),

	_ACCEPTED("ACCEPTED");

	private String code;

	private MandateStatus(String code) {
		this.code = code;
	}

	public static String FIN = _FINANCE.code;
	public static String NEW = _NEW.code;
	public static String AWAITCON = _AC.code;
	public static String APPROVED = _APPROVED.code;
	public static String REJECTED = _REJECTED.code;
	public static String HOLD = _HOLD.code;
	public static String RELEASE = _RELEASE.code;
	public static String CANCEL = _CANCEL.code;
	public static String INPROCESS = _INPROCESS.code;
	public static String ACKNOWLEDGE = _ACK.code;
	public static String ACCEPTED = _ACCEPTED.code;

	/**
	 * Determines if the given status is {@link MandateStatus#FINANCE} not.
	 * 
	 * @param mandateStatus The given status.
	 * 
	 * @return <code>true</code> if the given status is {@link MandateStatus#FINANCE}; <code>false</code> otherwise.
	 */
	public static boolean isFinance(String mandateStatus) {
		MandateStatus status = getStatus(mandateStatus);
		return status == null ? false : status == _FINANCE;
	}

	/**
	 * Determines if the given status is {@link MandateStatus#NEW} not.
	 * 
	 * @param mandateStatus The given status.
	 * 
	 * @return <code>true</code> if the given status is {@link MandateStatus#NEW}; <code>false</code> otherwise.
	 */
	public static boolean isNew(String mandateStatus) {
		MandateStatus status = getStatus(mandateStatus);
		return status == null ? false : status == _NEW;
	}

	/**
	 * Determines if the given status is {@link MandateStatus#AC} not.
	 * 
	 * @param mandateStatus The given status.
	 * 
	 * @return <code>true</code> if the given status is {@link MandateStatus#AC}; <code>false</code> otherwise.
	 */
	public static boolean isAwaitingConf(String mandateStatus) {
		MandateStatus status = getStatus(mandateStatus);
		return status == null ? false : status == _AC;
	}

	/**
	 * Determines if the given status is {@link MandateStatus#APPROVED} not.
	 * 
	 * @param mandateStatus The given status.
	 * 
	 * @return <code>true</code> if the given status is {@link MandateStatus#APPROVED}; <code>false</code> otherwise.
	 */
	public static boolean isApproved(String mandateStatus) {
		MandateStatus status = getStatus(mandateStatus);
		return status == null ? false : status == _APPROVED;
	}

	/**
	 * Determines if the given status is {@link MandateStatus#REJECTED} not.
	 * 
	 * @param mandateStatus The given status.
	 * 
	 * @return <code>true</code> if the given status is {@link MandateStatus#REJECTED}; <code>false</code> otherwise.
	 */
	public static boolean isRejected(String mandateStatus) {
		MandateStatus status = getStatus(mandateStatus);
		return status == null ? false : status == _REJECTED;
	}

	/**
	 * Determines if the given status is {@link MandateStatus#HOLD} not.
	 * 
	 * @param mandateStatus The given status.
	 * 
	 * @return <code>true</code> if the given status is {@link MandateStatus#HOLD}; <code>false</code> otherwise.
	 */
	public static boolean isHold(String mandateStatus) {
		MandateStatus status = getStatus(mandateStatus);
		return status == null ? false : status == _HOLD;
	}

	/**
	 * Determines if the given status is {@link MandateStatus#RELEASE} not.
	 * 
	 * @param mandateStatus The given status.
	 * 
	 * @return <code>true</code> if the given status is {@link MandateStatus#RELEASE}; <code>false</code> otherwise.
	 */
	public static boolean isRelease(String mandateStatus) {
		MandateStatus status = getStatus(mandateStatus);
		return status == null ? false : status == _RELEASE;
	}

	/**
	 * Determines if the given status is {@link MandateStatus#CANCEL} not.
	 * 
	 * @param mandateStatus The given status.
	 * 
	 * @return <code>true</code> if the given status is {@link MandateStatus#CANCEL}; <code>false</code> otherwise.
	 */
	public static boolean isCancel(String mandateStatus) {
		MandateStatus status = getStatus(mandateStatus);
		return status == null ? false : status == _CANCEL;
	}

	/**
	 * Determines if the given status is {@link MandateStatus#INPROCESS} not.
	 * 
	 * @param mandateStatus The given status.
	 * 
	 * @return <code>true</code> if the given status is {@link MandateStatus#INPROCESS}; <code>false</code> otherwise.
	 */
	public static boolean isInprocess(String mandateStatus) {
		MandateStatus status = getStatus(mandateStatus);
		return status == null ? false : status == _INPROCESS;
	}

	/**
	 * Determines if the given status is {@link MandateStatus#ACK} not.
	 * 
	 * @param mandateStatus The given status.
	 * 
	 * @return <code>true</code> if the given status is {@link MandateStatus#ACK}; <code>false</code> otherwise.
	 */
	public static boolean isAcknowledge(String mandateStatus) {
		MandateStatus status = getStatus(mandateStatus);
		return status == null ? false : status == _ACK;
	}

	/**
	 * Determines if the given status is {@link MandateStatus#ACCEPTED} not.
	 * 
	 * @param mandateStatus The given status.
	 * 
	 * @return <code>true</code> if the given status is {@link MandateStatus#ACCEPTED}; <code>false</code> otherwise.
	 */
	public static boolean isAccepted(String mandateStatus) {
		MandateStatus status = getStatus(mandateStatus);
		return status == null ? false : status == _ACCEPTED;
	}

	private static MandateStatus getStatus(String mandateStatus) {
		mandateStatus = "_" + mandateStatus;
		try {
			return MandateStatus.valueOf(StringUtils.upperCase(mandateStatus));
		} catch (Exception e) {
			//
		}
		return null;
	}

}
