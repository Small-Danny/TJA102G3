package com.tibafit.dto.article;

public class ReportTypeDTO {

    private Integer reportTypeId;
    private String reportTypeName;

    public ReportTypeDTO() {
    }

    public ReportTypeDTO(Integer reportTypeId, String reportTypeName) {
        this.reportTypeId = reportTypeId;
        this.reportTypeName = reportTypeName;
    }

    public Integer getReportTypeId() {
        return reportTypeId;
    }

    public void setReportTypeId(Integer reportTypeId) {
        this.reportTypeId = reportTypeId;
    }

    public String getReportTypeName() {
        return reportTypeName;
    }

    public void setReportTypeName(String reportTypeName) {
        this.reportTypeName = reportTypeName;
    }
}
