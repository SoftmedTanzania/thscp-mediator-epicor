package tz.go.moh.him.thscp.mediator.epicor.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;

public class StockOnHandPercentageWastageRequest {

    @JsonProperty("uuid")
    @SerializedName("uuid")
    private String uuid;

    @JsonProperty("consumedQuantity")
    @SerializedName("consumedQuantity")
    private int consumedQuantity;

    @JsonProperty("actionRequired")
    @SerializedName("actionRequired")
    private String actionRequired;

    @JsonProperty("damagedPercentage")
    @SerializedName("damagedPercentage")
    private float damagedPercentage;

    @JsonProperty("expiredPercentage")
    @SerializedName("expiredPercentage")
    private float expiredPercentage;

    @JsonProperty("facilityId")
    @SerializedName("facilityId")
    private String facilityId;

    @JsonProperty("lostPercentage")
    @SerializedName("lostPercentage")
    private float lostPercentage;

    @JsonProperty("facilityLevel")
    @SerializedName("facilityLevel")
    private int facilityLevel;

    @JsonProperty("monthsOfStock")
    @SerializedName("monthsOfStock")
    private int monthsOfStock;

    @JsonProperty("period")
    @SerializedName("period")
    private String period;

    @JsonProperty("productCode")
    @SerializedName("productCode")
    private String productCode;

    @JsonProperty("programCode")
    @SerializedName("programCode")
    private String programCode;

    @JsonProperty("quantity")
    @SerializedName("quantity")
    private int quantity;

    @JsonProperty("stockId")
    @SerializedName("stockId")
    private String stockId;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public int getConsumedQuantity() {
        return consumedQuantity;
    }

    public void setConsumedQuantity(int consumedQuantity) {
        this.consumedQuantity = consumedQuantity;
    }

    public String getActionRequired() {
        return actionRequired;
    }

    public void setActionRequired(String actionRequired) {
        this.actionRequired = actionRequired;
    }

    public float getDamagedPercentage() {
        return damagedPercentage;
    }

    public void setDamagedPercentage(float damagedPercentage) {
        this.damagedPercentage = damagedPercentage;
    }

    public float getExpiredPercentage() {
        return expiredPercentage;
    }

    public void setExpiredPercentage(float expiredPercentage) {
        this.expiredPercentage = expiredPercentage;
    }

    public String getFacilityId() {
        return facilityId;
    }

    public void setFacilityId(String facilityId) {
        this.facilityId = facilityId;
    }

    public float getLostPercentage() {
        return lostPercentage;
    }

    public void setLostPercentage(float lostPercentage) {
        this.lostPercentage = lostPercentage;
    }

    public int getFacilityLevel() {
        return facilityLevel;
    }

    public void setFacilityLevel(int facilityLevel) {
        this.facilityLevel = facilityLevel;
    }

    public int getMonthsOfStock() {
        return monthsOfStock;
    }

    public void setMonthsOfStock(int monthsOfStock) {
        this.monthsOfStock = monthsOfStock;
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

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getStockId() {
        return stockId;
    }

    public void setStockId(String stockId) {
        this.stockId = stockId;
    }
}
