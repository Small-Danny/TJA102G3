package com.tibafit.dto.article;



public class ReportStatusDTO {

    private Integer id;
    private String statusName;

    public ReportStatusDTO() {}

    public ReportStatusDTO(Integer id, String statusName) {
        this.id = id;
        this.statusName = statusName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }
}
