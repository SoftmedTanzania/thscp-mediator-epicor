package tz.go.moh.him.thscp.mediator.epicor.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;

public class PercentageWastageRequest {

    @JsonProperty("uuid")
    @SerializedName("uuid")
    private String uuid;

    @JsonProperty("damagedPercentage")
    @SerializedName("damagedPercentage")
    private float damagedPercentage;

    @JsonProperty("expiredPercentage")
    @SerializedName("expiredPercentage")
    private float expiredPercentage;

    @JsonProperty("msdZoneCode")
    @SerializedName("msdZoneCode")
    private String msdZoneCode;

    @JsonProperty("lostPercentage")
    @SerializedName("lostPercentage")
    private float lostPercentage;

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

    public String getMsdZoneCode() {
        return msdZoneCode;
    }

    public void setMsdZoneCode(String msdZoneCode) {
        this.msdZoneCode = msdZoneCode;
    }

    public float getLostPercentage() {
        return lostPercentage;
    }

    public void setLostPercentage(float lostPercentage) {
        this.lostPercentage = lostPercentage;
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
