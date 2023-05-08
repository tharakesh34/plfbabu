package com.pennanttech.external.api.casavalidation.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "faml")
@XmlAccessorType(XmlAccessType.FIELD)
public class CasaAccountValidationResp implements Serializable {

	public static final long serialVersionUID = 1L;

	@XmlElement(name = "requestdata")
	private RequestData requestData;

	@XmlElement(name = "responsedata")
	private ResponseData respData;

	@XmlElement(name = "rc")
	private ResponseCodes responseCodes;

	@XmlElement(name = "css")
	private Css css;

	@XmlElement(name = "extsysname")
	private String extSysName;

	private String xmlResponse;

	private boolean isException;

	@XmlRootElement(name = "requestdata")
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class RequestData implements Serializable {

		private static final long serialVersionUID = 1L;

		@XmlElement(name = "custacctdetails")
		private CustAccDtlsResp custAccDtlsResp;

		@XmlAccessorType(XmlAccessType.FIELD)
		public static class CustAccDtlsResp implements Serializable {

			private static final long serialVersionUID = 1L;

			@XmlElement(name = "accountNo")
			private String accNum;

			public String getAccNum() {
				return accNum;
			}

			public void setAccNum(String accNum) {
				this.accNum = accNum;
			}

			@Override
			public String toString() {
				return "CustAccDtlsResp [accNum=" + accNum + "]";
			}

		}

		public CustAccDtlsResp getCustAccDtlsResp() {
			return custAccDtlsResp;
		}

		public void setCustAccDtlsResp(CustAccDtlsResp custAccDtlsResp) {
			this.custAccDtlsResp = custAccDtlsResp;
		}

		@Override
		public String toString() {
			return "RequestData [custAccDtlsInResp=" + custAccDtlsResp + "]";
		}

	}

	@XmlRootElement(name = "responsedata")
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class ResponseData {

		@XmlElement(name = "customerdetails")
		private CustomerDetails custDetails;

		public CustomerDetails getCustDetails() {
			return custDetails;
		}

		@Override
		public String toString() {
			return "ResponseData [custDetails=" + custDetails + "]";
		}

		@XmlRootElement(name = "customerdetails")
		@XmlAccessorType(XmlAccessType.FIELD)
		public static class CustomerDetails implements Serializable {

			public static final long serialVersionUID = 1L;

			@XmlElement(name = "casaaccounts")
			public CasaAccounts casaAcc;

			@XmlElement(name = "accounts")
			public Accounts acc;

			@XmlRootElement(name = "casaaccounts")
			@XmlAccessorType(XmlAccessType.FIELD)
			public static class CasaAccounts implements Serializable {

				public static final long serialVersionUID = 1L;

				@XmlElement(name = "accdetails")
				private AccDetails accDtls;
				@XmlElement(name = "linkcustIds")
				private String linkCustIds;
				@XmlElement(name = "errorcode")
				private String errorCode;
				@XmlElement(name = "errormessage")
				private String errorMessage;

				@XmlAccessorType(XmlAccessType.FIELD)
				public static class AccDetails {

					@XmlAttribute(name = "accountbranchcode")
					private String accountbranchcode;

					@XmlAttribute(name = "accountnumber")
					private String accountNumber;

					@XmlAttribute(name = "accountopeningdate")
					private String accountOpeningDate;

					@XmlAttribute(name = "accountstatus")
					private String accountStatus;

					@XmlAttribute(name = "accounttitle")
					private String accountTitle;

					@XmlAttribute(name = "balminreqd")
					private String balMinReqd;

					@XmlAttribute(name = "codprod")
					private String codProd;

					@XmlAttribute(name = "currency")
					private String currency;

					@XmlAttribute(name = "customername")
					private String customerName;

					@XmlAttribute(name = "memoflg")
					private String memoFlg;

					@XmlAttribute(name = "namcurrency")
					private String namCurrency;

					@XmlAttribute(name = "nreflg")
					private String nreFlg;

					@XmlAttribute(name = "pricustomerid")
					private String priCustomerId;

					@XmlAttribute(name = "servicebranchcode")
					private String serviceBranchCode;

					public String getAccountbranchcode() {
						return accountbranchcode;
					}

					public void setAccountbranchcode(String accountbranchcode) {
						this.accountbranchcode = accountbranchcode;
					}

					public String getAccountNumber() {
						return accountNumber;
					}

					public void setAccountNumber(String accountNumber) {
						this.accountNumber = accountNumber;
					}

					public String getAccountOpeningDate() {
						return accountOpeningDate;
					}

					public void setAccountOpeningDate(String accountOpeningDate) {
						this.accountOpeningDate = accountOpeningDate;
					}

					public String getAccountStatus() {
						return accountStatus;
					}

					public void setAccountStatus(String accountStatus) {
						this.accountStatus = accountStatus;
					}

					public String getAccountTitle() {
						return accountTitle;
					}

					public void setAccountTitle(String accountTitle) {
						this.accountTitle = accountTitle;
					}

					public String getBalMinReqd() {
						return balMinReqd;
					}

					public void setBalMinReqd(String balMinReqd) {
						this.balMinReqd = balMinReqd;
					}

					public String getCodProd() {
						return codProd;
					}

					public void setCodProd(String codProd) {
						this.codProd = codProd;
					}

					public String getCurrency() {
						return currency;
					}

					public void setCurrency(String currency) {
						this.currency = currency;
					}

					public String getCustomerName() {
						return customerName;
					}

					public void setCustomerName(String customerName) {
						this.customerName = customerName;
					}

					public String getMemoFlg() {
						return memoFlg;
					}

					public void setMemoFlg(String memoFlg) {
						this.memoFlg = memoFlg;
					}

					public String getNamCurrency() {
						return namCurrency;
					}

					public void setNamCurrency(String namCurrency) {
						this.namCurrency = namCurrency;
					}

					public String getNreFlg() {
						return nreFlg;
					}

					public void setNreFlg(String nreFlg) {
						this.nreFlg = nreFlg;
					}

					public String getPriCustomerId() {
						return priCustomerId;
					}

					public void setPriCustomerId(String priCustomerId) {
						this.priCustomerId = priCustomerId;
					}

					public String getServiceBranchCode() {
						return serviceBranchCode;
					}

					public void setServiceBranchCode(String serviceBranchCode) {
						this.serviceBranchCode = serviceBranchCode;
					}

					@Override
					public String toString() {
						return "Accdetails [accountbranchcode=" + accountbranchcode + ", accountNumber=" + accountNumber
								+ ", accountOpeningDate=" + accountOpeningDate + ", accountStatus=" + accountStatus
								+ ", accountTitle=" + accountTitle + ", balMinReqd=" + balMinReqd + ", codProd="
								+ codProd + ", currency=" + currency + ", customerName=" + customerName + ", memoFlg="
								+ memoFlg + ", namCurrency=" + namCurrency + ", nreFlg=" + nreFlg + ", priCustomerId="
								+ priCustomerId + ", serviceBranchCode=" + serviceBranchCode + "]";
					}

				}

				public AccDetails getAccDtls() {
					return accDtls;
				}

				public void setAccDtls(AccDetails accDtls) {
					this.accDtls = accDtls;
				}

				public String getLinkCustIds() {
					return linkCustIds;
				}

				public void setLinkCustIds(String linkCustIds) {
					this.linkCustIds = linkCustIds;
				}

				public String getErrorCode() {
					return errorCode;
				}

				public void setErrorCode(String errorCode) {
					this.errorCode = errorCode;
				}

				public String getErrorMessage() {
					return errorMessage;
				}

				public void setErrorMessage(String errorMessage) {
					this.errorMessage = errorMessage;
				}

				@Override
				public String toString() {
					return "CasaAccounts [accDtls=" + accDtls + ", linkCustIds=" + linkCustIds + ", errorCode="
							+ errorCode + ", errorMessage=" + errorMessage + "]";
				}

			}

			@XmlRootElement(name = "accounts")
			@XmlAccessorType(XmlAccessType.FIELD)
			public static class Accounts {

				@XmlElement(name = "accountnumber")
				private String Accountnum;
				@XmlElement(name = "errorcode")
				private String errorCode;
				@XmlElement(name = "errormessage")
				private String errorMessage;

				public String getAccountnum() {
					return Accountnum;
				}

				public void setAccountnum(String accountnum) {
					Accountnum = accountnum;
				}

				public String getErrorCode() {
					return errorCode;
				}

				public void setErrorCode(String errorCode) {
					this.errorCode = errorCode;
				}

				public String getErrorMessage() {
					return errorMessage;
				}

				public void setErrorMessage(String errorMessage) {
					this.errorMessage = errorMessage;
				}

				@Override
				public String toString() {
					return "Accounts [Accountnum=" + Accountnum + ", errorCode=" + errorCode + ", errorMessage="
							+ errorMessage + "]";
				}

			}

			public CasaAccounts getCasaAcc() {
				return casaAcc;
			}

			public void setCasaAcc(CasaAccounts casaAcc) {
				this.casaAcc = casaAcc;
			}

			public Accounts getAcc() {
				return acc;
			}

			public void setAcc(Accounts acc) {
				this.acc = acc;
			}

			@Override
			public String toString() {
				return "CustomerDetails [casaAcc=" + casaAcc + ", acc=" + acc + "]";
			}
		}

	}

	@XmlAccessorType(XmlAccessType.FIELD)
	public static class ResponseCodes {
		@XmlAttribute(name = "errorcode")
		private String errorCode;
		@XmlAttribute(name = "errormessage")
		private String errorMessage;
		@XmlAttribute(name = "returncode")
		private String returnCode;

		public String getErrorCode() {
			return errorCode;
		}

		public void setErrorCode(String errorCode) {
			this.errorCode = errorCode;
		}

		public String getErrorMessage() {
			return errorMessage;
		}

		public void setErrorMessage(String errorMessage) {
			this.errorMessage = errorMessage;
		}

		public String getReturnCode() {
			return returnCode;
		}

		public void setReturnCode(String returnCode) {
			this.returnCode = returnCode;
		}

		@Override
		public String toString() {
			return "ResponseCodes [errorCode=" + errorCode + ", errorMessage=" + errorMessage + ", returnCode="
					+ returnCode + "]";
		}

	}

	@XmlAccessorType(XmlAccessType.FIELD)
	public static class Css {
		@XmlAttribute(name = "langcss")
		private String langCss;
		@XmlAttribute(name = "perscss")
		private String persCss;

		public String getLangCss() {
			return langCss;
		}

		public void setLangCss(String langCss) {
			this.langCss = langCss;
		}

		public String getPersCss() {
			return persCss;
		}

		public void setPersCss(String persCss) {
			this.persCss = persCss;
		}

		@Override
		public String toString() {
			return "css [langCss=" + langCss + ", persCss=" + persCss + "]";
		}

	}

	public String getExtSysName() {
		return extSysName;
	}

	public void setExtSysName(String extSysName) {
		this.extSysName = extSysName;
	}

	public CasaAccountValidationResp() {
	    super();
	}

	public ResponseData getRespData() {
		return respData;
	}

	public void setRespData(ResponseData respData) {
		this.respData = respData;
	}

	public RequestData getRequestData() {
		return requestData;
	}

	public void setRequestData(RequestData requestData) {
		this.requestData = requestData;
	}

	public ResponseCodes getResponseCodes() {
		return responseCodes;
	}

	public void setResponseCodes(ResponseCodes responseCodes) {
		this.responseCodes = responseCodes;
	}

	public Css getCss() {
		return css;
	}

	public void setCss(Css css) {
		this.css = css;
	}

	public String getXmlResponse() {
		return xmlResponse;
	}

	public void setXmlResponse(String xmlResponse) {
		this.xmlResponse = xmlResponse;
	}

	public boolean isException() {
		return isException;
	}

	public void setException(boolean isException) {
		this.isException = isException;
	}

	@Override
	public String toString() {
		return "SIAccValidationResp [requestData=" + requestData + ", respData=" + respData + ", responseCodes="
				+ responseCodes + ", css=" + css + ", extSysName=" + extSysName + "]";
	}

}
