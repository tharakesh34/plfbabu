package com.pennant.backend.model.reports;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.lang.StringUtils;

import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennanttech.pennapps.core.util.DateUtil;

public class ReportListDetail {

	private int reportFormat = SysParamUtil.getValueAsInt("APP_DFT_CURR_EDIT_FIELD");
	static int length = 15;

	private String fieldString01;
	private String fieldString02;
	private String fieldString03;
	private String fieldString04;
	private String fieldString05;
	private String fieldString06;
	private String fieldString07;
	private String fieldString08;
	private String fieldString09;
	private String fieldString10;
	private String fieldString11;
	private String fieldString12;
	private String fieldString13;
	private String fieldString14;
	private String fieldString15;
	private String fieldString16;
	private String fieldString17;
	private String fieldString18;
	private String fieldString19;
	private String fieldString20;

	private BigDecimal fieldBigDecimal01;
	private BigDecimal fieldBigDecimal02;
	private BigDecimal fieldBigDecimal03;
	private BigDecimal fieldBigDecimal04;
	private BigDecimal fieldBigDecimal05;
	private BigDecimal fieldBigDecimal06;
	private BigDecimal fieldBigDecimal07;
	private BigDecimal fieldBigDecimal08;
	private BigDecimal fieldBigDecimal09;
	private BigDecimal fieldBigDecimal10;
	private BigDecimal fieldBigDecimal11;
	private BigDecimal fieldBigDecimal12;
	private BigDecimal fieldBigDecimal13;
	private BigDecimal fieldBigDecimal14;
	private BigDecimal fieldBigDecimal15;
	private BigDecimal fieldBigDecimal16;
	private BigDecimal fieldBigDecimal17;
	private BigDecimal fieldBigDecimal18;
	private BigDecimal fieldBigDecimal19;
	private BigDecimal fieldBigDecimal20;

	private Integer fieldInt01;
	private Integer fieldInt02;
	private Integer fieldInt03;
	private Integer fieldInt04;
	private Integer fieldInt05;
	private Integer fieldInt06;
	private Integer fieldInt07;
	private Integer fieldInt08;
	private Integer fieldInt09;
	private Integer fieldInt10;
	private Integer fieldInt11;
	private Integer fieldInt12;
	private Integer fieldInt13;
	private Integer fieldInt14;
	private Integer fieldInt15;

	private Integer fieldBoolean01;
	private Integer fieldBoolean02;
	private Integer fieldBoolean03;
	private Integer fieldBoolean04;
	private Integer fieldBoolean05;
	private Integer fieldBoolean06;
	private Integer fieldBoolean07;
	private Integer fieldBoolean08;
	private Integer fieldBoolean09;
	private Integer fieldBoolean10;
	private Integer fieldBoolean11;
	private Integer fieldBoolean12;
	private Integer fieldBoolean13;
	private Integer fieldBoolean14;
	private Integer fieldBoolean15;

	private Long fieldLong01;
	private Long fieldLong02;
	private Long fieldLong03;
	private Long fieldLong04;
	private Long fieldLong05;
	private Long fieldLong06;
	private Long fieldLong07;
	private Long fieldLong08;
	private Long fieldLong09;
	private Long fieldLong10;
	private Long fieldLong11;
	private Long fieldLong12;
	private Long fieldLong13;
	private Long fieldLong14;
	private Long fieldLong15;

	private Date fieldDate01;
	private Date fieldDate02;
	private Date fieldDate03;
	private Date fieldDate04;
	private Date fieldDate05;
	private Date fieldDate06;
	private Date fieldDate07;
	private Date fieldDate08;
	private Date fieldDate09;
	private Date fieldDate10;
	private Date fieldDate11;
	private Date fieldDate12;
	private Date fieldDate13;
	private Date fieldDate14;
	private Date fieldDate15;

	public ReportListDetail() {
	    super();
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public int getReportFormat() {
		return reportFormat;
	}

	public void setReportFormat(int reportFormat) {
		this.reportFormat = reportFormat;
	}

	/** ================ String Parameters ============= **/

	public String getfieldString01() {
		return fieldString01;
	}

	public void setfieldString01(String fieldString01) {
		this.fieldString01 = fieldString01;
	}

	public String getfieldString02() {
		return fieldString02;
	}

	public void setfieldString02(String fieldString02) {
		this.fieldString02 = fieldString02;
	}

	public String getfieldString03() {
		return fieldString03;
	}

	public void setfieldString03(String fieldString03) {
		this.fieldString03 = fieldString03;
	}

	public String getfieldString04() {
		return fieldString04;
	}

	public void setfieldString04(String fieldString04) {
		this.fieldString04 = fieldString04;
	}

	public String getfieldString05() {
		return fieldString05;
	}

	public void setfieldString05(String fieldString05) {
		this.fieldString05 = fieldString05;
	}

	public String getfieldString06() {
		return fieldString06;
	}

	public void setfieldString06(String fieldString06) {
		this.fieldString06 = fieldString06;
	}

	public String getfieldString07() {
		return fieldString07;
	}

	public void setfieldString07(String fieldString07) {
		this.fieldString07 = fieldString07;
	}

	public String getfieldString08() {
		return fieldString08;
	}

	public void setfieldString08(String fieldString08) {
		this.fieldString08 = fieldString08;
	}

	public String getfieldString09() {
		return fieldString09;
	}

	public void setfieldString09(String fieldString09) {
		this.fieldString09 = fieldString09;
	}

	public String getfieldString10() {
		return fieldString10;
	}

	public void setfieldString10(String fieldString10) {
		this.fieldString10 = fieldString10;
	}

	public String getfieldString11() {
		return fieldString11;
	}

	public void setfieldString11(String fieldString11) {
		this.fieldString11 = fieldString11;
	}

	public String getfieldString12() {
		return fieldString12;
	}

	public void setfieldString12(String fieldString12) {
		this.fieldString12 = fieldString12;
	}

	public String getfieldString13() {
		return fieldString13;
	}

	public void setfieldString13(String fieldString13) {
		this.fieldString13 = fieldString13;
	}

	public String getfieldString14() {
		return fieldString14;
	}

	public void setfieldString14(String fieldString14) {
		this.fieldString14 = fieldString14;
	}

	public String getfieldString15() {
		return fieldString15;
	}

	public void setfieldString15(String fieldString15) {
		this.fieldString15 = fieldString15;
	}

	public String getFieldString16() {
		return fieldString16;
	}

	public void setFieldString16(String fieldString16) {
		this.fieldString16 = fieldString16;
	}

	public String getFieldString17() {
		return fieldString17;
	}

	public void setFieldString17(String fieldString17) {
		this.fieldString17 = fieldString17;
	}

	public String getFieldString18() {
		return fieldString18;
	}

	public void setFieldString18(String fieldString18) {
		this.fieldString18 = fieldString18;
	}

	public String getFieldString19() {
		return fieldString19;
	}

	public void setFieldString19(String fieldString19) {
		this.fieldString19 = fieldString19;
	}

	public String getFieldString20() {
		return fieldString20;
	}

	public void setFieldString20(String fieldString20) {
		this.fieldString20 = fieldString20;
	}

	/** ================ BigDecimal Parameters ============= **/
	public BigDecimal getFieldBigDecimal01() {
		return fieldBigDecimal01;
	}

	public void setFieldBigDecimal01(BigDecimal fieldBigDecimal01) {
		this.fieldBigDecimal01 = fieldBigDecimal01;

		if (fieldBigDecimal01 != null) {
			this.fieldString01 = format(fieldBigDecimal01);
		}
	}

	public BigDecimal getFieldBigDecimal02() {
		return fieldBigDecimal02;
	}

	public void setFieldBigDecimal02(BigDecimal fieldBigDecimal02) {
		this.fieldBigDecimal02 = fieldBigDecimal02;

		if (fieldBigDecimal02 != null) {
			this.fieldString02 = format(fieldBigDecimal02);
		}
	}

	public BigDecimal getFieldBigDecimal03() {
		return fieldBigDecimal03;
	}

	public void setFieldBigDecimal03(BigDecimal fieldBigDecimal03) {
		this.fieldBigDecimal03 = fieldBigDecimal03;

		if (fieldBigDecimal03 != null) {
			this.fieldString03 = format(fieldBigDecimal03);
		}
	}

	public BigDecimal getFieldBigDecimal04() {
		return fieldBigDecimal04;
	}

	public void setFieldBigDecimal04(BigDecimal fieldBigDecimal04) {
		this.fieldBigDecimal04 = fieldBigDecimal04;

		if (fieldBigDecimal04 != null) {
			this.fieldString04 = format(fieldBigDecimal04);
		}
	}

	public BigDecimal getFieldBigDecimal05() {
		return fieldBigDecimal05;
	}

	public void setFieldBigDecimal05(BigDecimal fieldBigDecimal05) {
		this.fieldBigDecimal05 = fieldBigDecimal05;

		if (fieldBigDecimal05 != null) {
			this.fieldString05 = format(fieldBigDecimal05);
		}
	}

	public BigDecimal getFieldBigDecimal06() {
		return fieldBigDecimal06;
	}

	public void setFieldBigDecimal06(BigDecimal fieldBigDecimal06) {
		this.fieldBigDecimal06 = fieldBigDecimal06;

		if (fieldBigDecimal06 != null) {
			this.fieldString06 = format(fieldBigDecimal06);
		}
	}

	public BigDecimal getFieldBigDecimal07() {
		return fieldBigDecimal07;
	}

	public void setFieldBigDecimal07(BigDecimal fieldBigDecimal07) {
		this.fieldBigDecimal07 = fieldBigDecimal07;

		if (fieldBigDecimal07 != null) {
			this.fieldString07 = format(fieldBigDecimal07);
		}
	}

	public BigDecimal getFieldBigDecimal08() {
		return fieldBigDecimal08;
	}

	public void setFieldBigDecimal08(BigDecimal fieldBigDecimal08) {
		this.fieldBigDecimal08 = fieldBigDecimal08;

		if (fieldBigDecimal08 != null) {
			this.fieldString08 = format(fieldBigDecimal08);
		}
	}

	public BigDecimal getFieldBigDecimal09() {
		return fieldBigDecimal09;
	}

	public void setFieldBigDecimal09(BigDecimal fieldBigDecimal09) {
		this.fieldBigDecimal09 = fieldBigDecimal09;

		if (fieldBigDecimal09 != null) {
			this.fieldString09 = format(fieldBigDecimal09);
		}
	}

	public BigDecimal getFieldBigDecimal10() {
		return fieldBigDecimal10;
	}

	public void setFieldBigDecimal10(BigDecimal fieldBigDecimal10) {
		this.fieldBigDecimal10 = fieldBigDecimal10;

		if (fieldBigDecimal10 != null) {
			this.fieldString10 = format(fieldBigDecimal10);
		}
	}

	public BigDecimal getFieldBigDecimal11() {
		return fieldBigDecimal11;
	}

	public void setFieldBigDecimal11(BigDecimal fieldBigDecimal11) {
		this.fieldBigDecimal11 = fieldBigDecimal11;

		if (fieldBigDecimal11 != null) {
			this.fieldString11 = format(fieldBigDecimal11);
		}
	}

	public BigDecimal getFieldBigDecimal12() {
		return fieldBigDecimal12;
	}

	public void setFieldBigDecimal12(BigDecimal fieldBigDecimal12) {
		this.fieldBigDecimal12 = fieldBigDecimal12;

		if (fieldBigDecimal12 != null) {
			this.fieldString12 = format(fieldBigDecimal12);
		}
	}

	public BigDecimal getFieldBigDecimal13() {
		return fieldBigDecimal13;
	}

	public void setFieldBigDecimal13(BigDecimal fieldBigDecimal13) {
		this.fieldBigDecimal13 = fieldBigDecimal13;

		if (fieldBigDecimal13 != null) {
			this.fieldString13 = format(fieldBigDecimal13);
		}
	}

	public BigDecimal getFieldBigDecimal14() {
		return fieldBigDecimal14;
	}

	public void setFieldBigDecimal14(BigDecimal fieldBigDecimal14) {
		this.fieldBigDecimal14 = fieldBigDecimal14;

		if (fieldBigDecimal14 != null) {
			this.fieldString14 = format(fieldBigDecimal14);
		}
	}

	public BigDecimal getFieldBigDecimal15() {
		return fieldBigDecimal15;
	}

	public void setFieldBigDecimal15(BigDecimal fieldBigDecimal15) {
		this.fieldBigDecimal15 = fieldBigDecimal15;

		if (fieldBigDecimal15 != null) {
			this.fieldString15 = format(fieldBigDecimal15);
		}
	}

	public BigDecimal getFieldBigDecimal16() {
		return fieldBigDecimal16;
	}

	public void setFieldBigDecimal16(BigDecimal fieldBigDecimal16) {
		this.fieldBigDecimal16 = fieldBigDecimal16;

		if (fieldBigDecimal16 != null) {
			this.fieldString16 = format(fieldBigDecimal16);
		}
	}

	public BigDecimal getFieldBigDecimal17() {
		return fieldBigDecimal17;
	}

	public void setFieldBigDecimal17(BigDecimal fieldBigDecimal17) {
		this.fieldBigDecimal17 = fieldBigDecimal17;

		if (fieldBigDecimal17 != null) {
			this.fieldString17 = format(fieldBigDecimal17);
		}
	}

	public BigDecimal getFieldBigDecimal18() {
		return fieldBigDecimal18;
	}

	public void setFieldBigDecimal18(BigDecimal fieldBigDecimal18) {
		this.fieldBigDecimal18 = fieldBigDecimal18;

		if (fieldBigDecimal18 != null) {
			this.fieldString18 = format(fieldBigDecimal18);
		}
	}

	public BigDecimal getFieldBigDecimal19() {
		return fieldBigDecimal19;
	}

	public void setFieldBigDecimal19(BigDecimal fieldBigDecimal19) {
		this.fieldBigDecimal19 = fieldBigDecimal19;

		if (fieldBigDecimal19 != null) {
			this.fieldString19 = format(fieldBigDecimal19);
		}
	}

	public BigDecimal getFieldBigDecimal20() {
		return fieldBigDecimal20;
	}

	public void setFieldBigDecimal20(BigDecimal fieldBigDecimal20) {
		this.fieldBigDecimal20 = fieldBigDecimal20;

		if (fieldBigDecimal20 != null) {
			this.fieldString20 = format(fieldBigDecimal20);
		}
	}

	/** ================ Integer Parameters ============= **/

	public Integer getFieldInt01() {
		return fieldInt01;
	}

	public void setFieldInt01(Integer fieldInt01) {
		this.fieldInt01 = fieldInt01;

		if (fieldInt01 != null) {
			this.fieldString01 = StringUtils.leftPad(PennantApplicationUtil.formateInt(fieldInt01), length);
		}
	}

	public Integer getFieldInt02() {
		return fieldInt02;
	}

	public void setFieldInt02(Integer fieldInt02) {
		this.fieldInt02 = fieldInt02;

		if (fieldInt02 != null) {
			this.fieldString02 = StringUtils.leftPad(PennantApplicationUtil.formateInt(fieldInt02), length);
		}
	}

	public Integer getFieldInt03() {
		return fieldInt03;
	}

	public void setFieldInt03(Integer fieldInt03) {
		this.fieldInt03 = fieldInt03;

		if (fieldInt03 != null) {
			this.fieldString03 = StringUtils.leftPad(PennantApplicationUtil.formateInt(fieldInt03), length);
		}
	}

	public Integer getFieldInt04() {
		return fieldInt04;
	}

	public void setFieldInt04(Integer fieldInt04) {
		this.fieldInt04 = fieldInt04;

		if (fieldInt04 != null) {
			this.fieldString04 = StringUtils.leftPad(PennantApplicationUtil.formateInt(fieldInt04), length);
		}
	}

	public Integer getFieldInt05() {
		return fieldInt05;
	}

	public void setFieldInt05(Integer fieldInt05) {
		this.fieldInt05 = fieldInt05;

		if (fieldInt05 != null) {
			this.fieldString05 = StringUtils.leftPad(PennantApplicationUtil.formateInt(fieldInt05), length);
		}
	}

	public Integer getFieldInt06() {
		return fieldInt06;
	}

	public void setFieldInt06(Integer fieldInt06) {
		this.fieldInt06 = fieldInt06;

		if (fieldInt06 != null) {
			this.fieldString06 = StringUtils.leftPad(PennantApplicationUtil.formateInt(fieldInt06), length);
		}
	}

	public Integer getFieldInt07() {
		return fieldInt07;
	}

	public void setFieldInt07(Integer fieldInt07) {
		this.fieldInt07 = fieldInt07;

		if (fieldInt07 != null) {
			this.fieldString07 = StringUtils.leftPad(PennantApplicationUtil.formateInt(fieldInt07), length);
		}
	}

	public Integer getFieldInt08() {
		return fieldInt08;
	}

	public void setFieldInt08(Integer fieldInt08) {
		this.fieldInt08 = fieldInt08;

		if (fieldInt08 != null) {
			this.fieldString08 = StringUtils.leftPad(PennantApplicationUtil.formateInt(fieldInt08), length);
		}
	}

	public Integer getFieldInt09() {
		return fieldInt09;
	}

	public void setFieldInt09(Integer fieldInt09) {
		this.fieldInt09 = fieldInt09;

		if (fieldInt09 != null) {
			this.fieldString09 = StringUtils.leftPad(PennantApplicationUtil.formateInt(fieldInt09), length);
		}
	}

	public Integer getFieldInt10() {
		return fieldInt10;
	}

	public void setFieldInt10(Integer fieldInt10) {
		this.fieldInt10 = fieldInt10;

		if (fieldInt10 != null) {
			this.fieldString10 = StringUtils.leftPad(PennantApplicationUtil.formateInt(fieldInt10), length);
		}
	}

	public Integer getFieldInt11() {
		return fieldInt11;
	}

	public void setFieldInt11(Integer fieldInt11) {
		this.fieldInt11 = fieldInt11;

		if (fieldInt11 != null) {
			this.fieldString11 = StringUtils.leftPad(PennantApplicationUtil.formateInt(fieldInt11), length);
		}
	}

	public Integer getFieldInt12() {
		return fieldInt12;
	}

	public void setFieldInt12(Integer fieldInt12) {
		this.fieldInt12 = fieldInt12;

		if (fieldInt12 != null) {
			this.fieldString12 = StringUtils.leftPad(PennantApplicationUtil.formateInt(fieldInt12), length);
		}
	}

	public Integer getFieldInt13() {
		return fieldInt13;
	}

	public void setFieldInt13(Integer fieldInt13) {
		this.fieldInt13 = fieldInt13;

		if (fieldInt13 != null) {
			this.fieldString13 = StringUtils.leftPad(PennantApplicationUtil.formateInt(fieldInt13), length);
		}
	}

	public Integer getFieldInt14() {
		return fieldInt14;
	}

	public void setFieldInt14(Integer fieldInt14) {
		this.fieldInt14 = fieldInt14;

		if (fieldInt14 != null) {
			this.fieldString14 = StringUtils.leftPad(PennantApplicationUtil.formateInt(fieldInt14), length);
		}
	}

	public Integer getFieldInt15() {
		return fieldInt15;
	}

	public void setFieldInt15(Integer fieldInt15) {
		this.fieldInt15 = fieldInt15;

		if (fieldInt15 != null) {
			this.fieldString15 = StringUtils.leftPad(PennantApplicationUtil.formateInt(fieldInt15), length);
		}
	}

	/** ================ Boolean Parameters ============= **/

	public Integer getFieldBoolean01() {
		return fieldBoolean01;
	}

	public void setFieldBoolean01(Integer fieldBoolean01) {
		this.fieldBoolean01 = fieldBoolean01;

		if (fieldBoolean01 != null) {
			this.fieldString01 = PennantApplicationUtil.formateBoolean(fieldBoolean01);
		}
	}

	public Integer getFieldBoolean02() {
		return fieldBoolean02;
	}

	public void setFieldBoolean02(Integer fieldBoolean02) {
		this.fieldBoolean02 = fieldBoolean02;

		if (fieldBoolean02 != null) {
			this.fieldString02 = PennantApplicationUtil.formateBoolean(fieldBoolean02);
		}
	}

	public Integer getFieldBoolean03() {
		return fieldBoolean03;
	}

	public void setFieldBoolean03(Integer fieldBoolean03) {
		this.fieldBoolean03 = fieldBoolean03;

		if (fieldBoolean03 != null) {
			this.fieldString03 = PennantApplicationUtil.formateBoolean(fieldBoolean03);
		}
	}

	public Integer getFieldBoolean04() {
		return fieldBoolean04;
	}

	public void setFieldBoolean04(Integer fieldBoolean04) {
		this.fieldBoolean04 = fieldBoolean04;

		if (fieldBoolean04 != null) {
			this.fieldString04 = PennantApplicationUtil.formateBoolean(fieldBoolean04);
		}
	}

	public Integer getFieldBoolean05() {
		return fieldBoolean05;
	}

	public void setFieldBoolean05(Integer fieldBoolean05) {
		this.fieldBoolean05 = fieldBoolean05;

		if (fieldBoolean05 != null) {
			this.fieldString05 = PennantApplicationUtil.formateBoolean(fieldBoolean05);
		}
	}

	public Integer getFieldBoolean06() {
		return fieldBoolean06;
	}

	public void setFieldBoolean06(Integer fieldBoolean06) {
		this.fieldBoolean06 = fieldBoolean06;

		if (fieldBoolean06 != null) {
			this.fieldString06 = PennantApplicationUtil.formateBoolean(fieldBoolean06);
		}
	}

	public Integer getFieldBoolean07() {
		return fieldBoolean07;
	}

	public void setFieldBoolean07(Integer fieldBoolean07) {
		this.fieldBoolean07 = fieldBoolean07;

		if (fieldBoolean07 != null) {
			this.fieldString07 = PennantApplicationUtil.formateBoolean(fieldBoolean07);
		}
	}

	public Integer getFieldBoolean08() {
		return fieldBoolean08;
	}

	public void setFieldBoolean08(Integer fieldBoolean08) {
		this.fieldBoolean08 = fieldBoolean08;

		if (fieldBoolean08 != null) {
			this.fieldString08 = PennantApplicationUtil.formateBoolean(fieldBoolean08);
		}
	}

	public Integer getFieldBoolean09() {
		return fieldBoolean09;
	}

	public void setFieldBoolean09(Integer fieldBoolean09) {
		this.fieldBoolean09 = fieldBoolean09;

		if (fieldBoolean09 != null) {
			this.fieldString09 = PennantApplicationUtil.formateBoolean(fieldBoolean09);
		}
	}

	public Integer getFieldBoolean10() {
		return fieldBoolean10;
	}

	public void setFieldBoolean10(Integer fieldBoolean10) {
		this.fieldBoolean10 = fieldBoolean10;

		if (fieldBoolean10 != null) {
			this.fieldString10 = PennantApplicationUtil.formateBoolean(fieldBoolean10);
		}
	}

	public Integer getFieldBoolean11() {
		return fieldBoolean11;
	}

	public void setFieldBoolean11(Integer fieldBoolean11) {
		this.fieldBoolean11 = fieldBoolean11;

		if (fieldBoolean11 != null) {
			this.fieldString11 = PennantApplicationUtil.formateBoolean(fieldBoolean11);
		}
	}

	public Integer getFieldBoolean12() {
		return fieldBoolean12;
	}

	public void setFieldBoolean12(Integer fieldBoolean12) {
		this.fieldBoolean12 = fieldBoolean12;

		if (fieldBoolean12 != null) {
			this.fieldString12 = PennantApplicationUtil.formateBoolean(fieldBoolean12);
		}
	}

	public Integer getFieldBoolean13() {
		return fieldBoolean13;
	}

	public void setFieldBoolean13(Integer fieldBoolean13) {
		this.fieldBoolean13 = fieldBoolean13;

		if (fieldBoolean13 != null) {
			this.fieldString13 = PennantApplicationUtil.formateBoolean(fieldBoolean13);
		}
	}

	public Integer getFieldBoolean14() {
		return fieldBoolean14;
	}

	public void setFieldBoolean14(Integer fieldBoolean14) {
		this.fieldBoolean14 = fieldBoolean14;

		if (fieldBoolean14 != null) {
			this.fieldString14 = PennantApplicationUtil.formateBoolean(fieldBoolean14);
		}
	}

	public Integer getFieldBoolean15() {
		return fieldBoolean15;
	}

	public void setFieldBoolean15(Integer fieldBoolean15) {
		this.fieldBoolean15 = fieldBoolean15;

		if (fieldBoolean15 != null) {
			this.fieldString15 = PennantApplicationUtil.formateBoolean(fieldBoolean15);
		}
	}

	/** ================ Long Parameters ============= **/

	public Long getFieldLong01() {
		return fieldLong01;
	}

	public void setFieldLong01(Long fieldLong01) {
		this.fieldLong01 = fieldLong01;

		if (fieldLong01 != null) {
			this.fieldString01 = StringUtils.leftPad(PennantApplicationUtil.formateLong(fieldLong01), length);
		}
	}

	public Long getFieldLong02() {
		return fieldLong02;
	}

	public void setFieldLong02(Long fieldLong02) {
		this.fieldLong02 = fieldLong02;

		if (fieldLong02 != null) {
			this.fieldString02 = StringUtils.leftPad(PennantApplicationUtil.formateLong(fieldLong02), length);
		}
	}

	public Long getFieldLong03() {
		return fieldLong03;
	}

	public void setFieldLong03(Long fieldLong03) {
		this.fieldLong03 = fieldLong03;

		if (fieldLong03 != null) {
			this.fieldString03 = StringUtils.leftPad(PennantApplicationUtil.formateLong(fieldLong03), length);
		}
	}

	public Long getFieldLong04() {
		return fieldLong04;
	}

	public void setFieldLong04(Long fieldLong04) {
		this.fieldLong04 = fieldLong04;

		if (fieldLong04 != null) {
			this.fieldString04 = StringUtils.leftPad(PennantApplicationUtil.formateLong(fieldLong04), length);
		}
	}

	public Long getFieldLong05() {
		return fieldLong05;
	}

	public void setFieldLong05(Long fieldLong05) {
		this.fieldLong05 = fieldLong05;

		if (fieldLong05 != null) {
			this.fieldString05 = StringUtils.leftPad(PennantApplicationUtil.formateLong(fieldLong05), length);
		}
	}

	public Long getFieldLong06() {
		return fieldLong06;
	}

	public void setFieldLong06(Long fieldLong06) {
		this.fieldLong06 = fieldLong06;

		if (fieldLong06 != null) {
			this.fieldString06 = StringUtils.leftPad(PennantApplicationUtil.formateLong(fieldLong06), length);
		}
	}

	public Long getFieldLong07() {
		return fieldLong07;
	}

	public void setFieldLong07(Long fieldLong07) {
		this.fieldLong07 = fieldLong07;

		if (fieldLong07 != null) {
			this.fieldString07 = StringUtils.leftPad(PennantApplicationUtil.formateLong(fieldLong07), length);
		}
	}

	public Long getFieldLong08() {
		return fieldLong08;
	}

	public void setFieldLong08(Long fieldLong08) {
		this.fieldLong08 = fieldLong08;

		if (fieldLong08 != null) {
			this.fieldString08 = StringUtils.leftPad(PennantApplicationUtil.formateLong(fieldLong08), length);
		}
	}

	public Long getFieldLong09() {
		return fieldLong09;
	}

	public void setFieldLong09(Long fieldLong09) {
		this.fieldLong09 = fieldLong09;

		if (fieldLong09 != null) {
			this.fieldString09 = StringUtils.leftPad(PennantApplicationUtil.formateLong(fieldLong09), length);
		}
	}

	public Long getFieldLong10() {
		return fieldLong10;
	}

	public void setFieldLong10(Long fieldLong10) {
		this.fieldLong10 = fieldLong10;

		if (fieldLong10 != null) {
			this.fieldString10 = StringUtils.leftPad(PennantApplicationUtil.formateLong(fieldLong10), length);
		}
	}

	public Long getFieldLong11() {
		return fieldLong11;
	}

	public void setFieldLong11(Long fieldLong11) {
		this.fieldLong11 = fieldLong11;

		if (fieldLong11 != null) {
			this.fieldString11 = StringUtils.leftPad(PennantApplicationUtil.formateLong(fieldLong11), length);
		}
	}

	public Long getFieldLong12() {
		return fieldLong12;
	}

	public void setFieldLong12(Long fieldLong12) {
		this.fieldLong12 = fieldLong12;

		if (fieldLong12 != null) {
			this.fieldString12 = StringUtils.leftPad(PennantApplicationUtil.formateLong(fieldLong12), length);
		}
	}

	public Long getFieldLong13() {
		return fieldLong13;
	}

	public void setFieldLong13(Long fieldLong13) {
		this.fieldLong13 = fieldLong13;

		if (fieldLong13 != null) {
			this.fieldString13 = StringUtils.leftPad(PennantApplicationUtil.formateLong(fieldLong13), length);
		}
	}

	public Long getFieldLong14() {
		return fieldLong14;
	}

	public void setFieldLong14(Long fieldLong14) {
		this.fieldLong14 = fieldLong14;

		if (fieldLong14 != null) {
			this.fieldString14 = StringUtils.leftPad(PennantApplicationUtil.formateLong(fieldLong14), length);
		}
	}

	public Long getFieldLong15() {
		return fieldLong15;
	}

	public void setFieldLong15(Long fieldLong15) {
		this.fieldLong15 = fieldLong15;

		if (fieldLong15 != null) {
			this.fieldString15 = StringUtils.leftPad(PennantApplicationUtil.formateLong(fieldLong15), length);
		}
	}

	/** ================ Date Parameters ============= **/

	public Date getFieldDate01() {
		return fieldDate01;
	}

	public void setFieldDate01(Date fieldDate01) {
		this.fieldDate01 = fieldDate01;

		if (fieldDate01 != null) {
			this.fieldString01 = DateUtil.formatToLongDate(fieldDate01);
		}
	}

	public Date getFieldDate02() {
		return fieldDate02;
	}

	public void setFieldDate02(Date fieldDate02) {
		this.fieldDate02 = fieldDate02;

		if (fieldDate02 != null) {
			this.fieldString02 = DateUtil.formatToLongDate(fieldDate02);
		}
	}

	public Date getFieldDate03() {
		return fieldDate03;
	}

	public void setFieldDate03(Date fieldDate03) {
		this.fieldDate03 = fieldDate03;

		if (fieldDate03 != null) {
			this.fieldString03 = DateUtil.formatToLongDate(fieldDate03);
		}
	}

	public Date getFieldDate04() {
		return fieldDate04;
	}

	public void setFieldDate04(Date fieldDate04) {
		this.fieldDate04 = fieldDate04;

		if (fieldDate04 != null) {
			this.fieldString04 = DateUtil.formatToLongDate(fieldDate04);
		}
	}

	public Date getFieldDate05() {
		return fieldDate05;
	}

	public void setFieldDate05(Date fieldDate05) {
		this.fieldDate05 = fieldDate05;

		if (fieldDate05 != null) {
			this.fieldString05 = DateUtil.formatToLongDate(fieldDate05);
		}
	}

	public Date getFieldDate06() {
		return fieldDate06;
	}

	public void setFieldDate06(Date fieldDate06) {
		this.fieldDate06 = fieldDate06;

		if (fieldDate06 != null) {
			this.fieldString06 = DateUtil.formatToLongDate(fieldDate06);
		}
	}

	public Date getFieldDate07() {
		return fieldDate07;
	}

	public void setFieldDate07(Date fieldDate07) {
		this.fieldDate07 = fieldDate07;

		if (fieldDate07 != null) {
			this.fieldString07 = DateUtil.formatToLongDate(fieldDate07);
		}
	}

	public Date getFieldDate08() {
		return fieldDate08;
	}

	public void setFieldDate08(Date fieldDate08) {
		this.fieldDate08 = fieldDate08;

		if (fieldDate08 != null) {
			this.fieldString08 = DateUtil.formatToLongDate(fieldDate08);
		}
	}

	public Date getFieldDate09() {
		return fieldDate09;
	}

	public void setFieldDate09(Date fieldDate09) {
		this.fieldDate09 = fieldDate09;

		if (fieldDate09 != null) {
			this.fieldString09 = DateUtil.formatToLongDate(fieldDate09);
		}
	}

	public Date getFieldDate10() {
		return fieldDate10;
	}

	public void setFieldDate10(Date fieldDate10) {
		this.fieldDate10 = fieldDate10;

		if (fieldDate10 != null) {
			this.fieldString10 = DateUtil.formatToLongDate(fieldDate10);
		}
	}

	public Date getFieldDate11() {
		return fieldDate11;
	}

	public void setFieldDate11(Date fieldDate11) {
		this.fieldDate11 = fieldDate11;

		if (fieldDate11 != null) {
			this.fieldString11 = DateUtil.formatToLongDate(fieldDate11);
		}
	}

	public Date getFieldDate12() {
		return fieldDate12;
	}

	public void setFieldDate12(Date fieldDate12) {
		this.fieldDate12 = fieldDate12;

		if (fieldDate12 != null) {
			this.fieldString12 = DateUtil.formatToLongDate(fieldDate12);
		}
	}

	public Date getFieldDate13() {
		return fieldDate13;
	}

	public void setFieldDate13(Date fieldDate13) {
		this.fieldDate13 = fieldDate13;

		if (fieldDate13 != null) {
			this.fieldString13 = DateUtil.formatToLongDate(fieldDate13);
		}
	}

	public Date getFieldDate14() {
		return fieldDate14;
	}

	public void setFieldDate14(Date fieldDate14) {
		this.fieldDate14 = fieldDate14;

		if (fieldDate14 != null) {
			this.fieldString14 = DateUtil.formatToLongDate(fieldDate14);
		}
	}

	public Date getFieldDate15() {
		return fieldDate15;
	}

	public void setFieldDate15(Date fieldDate15) {
		this.fieldDate15 = fieldDate15;

		if (fieldDate15 != null) {
			this.fieldString15 = DateUtil.formatToLongDate(fieldDate15);
		}
	}

	private String format(BigDecimal fieldBigDecimal15) {
		return StringUtils.leftPad(PennantApplicationUtil.amountFormate(fieldBigDecimal15, reportFormat), length);
	}

}
