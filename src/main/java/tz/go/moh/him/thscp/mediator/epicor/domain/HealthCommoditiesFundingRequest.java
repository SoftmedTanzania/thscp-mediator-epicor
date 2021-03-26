package tz.go.moh.him.thscp.mediator.epicor.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;

public class HealthCommoditiesFundingRequest {
    @JsonProperty("uuid")
    @SerializedName("uuid")
    private String uuid;

    @JsonProperty("allocatedFund")
    @SerializedName("allocatedFund")
    private int allocatedFund;

    @JsonProperty("disbursedFund")
    @SerializedName("disbursedFund")
    private int disbursedFund;

    @JsonProperty("endDate")
    @SerializedName("endDate")
    private String endDate;

    @JsonProperty("facilityId")
    @SerializedName("facilityId")
    private String facilityId;

    @JsonProperty("productCode")
    @SerializedName("productCode")
    private String productCode;

    @JsonProperty("program")
    @SerializedName("program")
    private String program;

    @JsonProperty("source")
    @SerializedName("source")
    private String source;

    @JsonProperty("startDate")
    @SerializedName("startDate")
    private String startDate;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public int getAllocatedFund() {
        return allocatedFund;
    }

    public void setAllocatedFund(int allocatedFund) {
        this.allocatedFund = allocatedFund;
    }

    public int getDisbursedFund() {
        return disbursedFund;
    }

    public void setDisbursedFund(int disbursedFund) {
        this.disbursedFund = disbursedFund;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getFacilityId() {
        return facilityId;
    }

    public void setFacilityId(String facilityId) {
        this.facilityId = facilityId;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getProgram() {
        return program;
    }

    public void setProgram(String program) {
        this.program = program;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }
}
