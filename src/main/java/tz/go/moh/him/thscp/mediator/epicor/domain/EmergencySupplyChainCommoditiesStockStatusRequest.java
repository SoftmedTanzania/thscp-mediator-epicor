package tz.go.moh.him.thscp.mediator.epicor.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;

public class EmergencySupplyChainCommoditiesStockStatusRequest {

    @JsonProperty("uuid")
    @SerializedName("uuid")
    private String uuid;

    @JsonProperty("availableQuantity")
    @SerializedName("availableQuantity")
    private int availableQuantity;

    @JsonProperty("facility_id")
    @SerializedName("facility_id")
    private String facilityId;

    @JsonProperty("period")
    @SerializedName("period")
    private String period;

    @JsonProperty("productCode")
    @SerializedName("productCode")
    private String productCode;

    @JsonProperty("programCode")
    @SerializedName("programCode")
    private String programCode;

    @JsonProperty("stockOfMonth")
    @SerializedName("stockOfMonth")
    private int stockOfMonth;

    @JsonProperty("stockQuantity")
    @SerializedName("stockQuantity")
    private int stockQuantity;


    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public int getAvailableQuantity() {
        return availableQuantity;
    }

    public void setAvailableQuantity(int availableQuantity) {
        this.availableQuantity = availableQuantity;
    }

    public String getFacilityId() {
        return facilityId;
    }

    public void setFacilityId(String facilityId) {
        this.facilityId = facilityId;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getProgramCode() {
        return programCode;
    }

    public void setProgramCode(String programCode) {
        this.programCode = programCode;
    }

    public int getStockOfMonth() {
        return stockOfMonth;
    }

    public void setStockOfMonth(int stockOfMonth) {
        this.stockOfMonth = stockOfMonth;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

}
