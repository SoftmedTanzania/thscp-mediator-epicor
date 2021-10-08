package tz.go.moh.him.thscp.mediator.epicor.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;

public class StockOnHandRequest {
    @JsonProperty("uuid")
    @SerializedName("uuid")
    private String uuid;

    @JsonProperty("consumedQuantity")
    @SerializedName("consumedQuantity")
    private int consumedQuantity;

    @JsonProperty("msdZoneCode")
    @SerializedName("msdZoneCode")
    private String msdZoneCode;

    @JsonProperty("monthsOfStock")
    @SerializedName("monthsOfStock")
    private int monthsOfStock;

    @JsonProperty("period")
    @SerializedName("period")
    private String period;

    @JsonProperty("productCode")
    @SerializedName("productCode")
    private String productCode;

    @JsonProperty("quantity")
    @SerializedName("quantity")
    private int quantity;

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

    public String getMsdZoneCode() {
        return msdZoneCode;
    }

    public void setMsdZoneCode(String msdZoneCode) {
        this.msdZoneCode = msdZoneCode;
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

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
