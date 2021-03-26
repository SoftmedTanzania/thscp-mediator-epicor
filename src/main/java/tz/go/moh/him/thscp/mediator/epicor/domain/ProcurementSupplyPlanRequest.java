package tz.go.moh.him.thscp.mediator.epicor.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;

public class ProcurementSupplyPlanRequest {

    @JsonProperty("uuid")
    @SerializedName("uuid")
    private String uuid;

    @JsonProperty("contractDate")
    @SerializedName("contractDate")
    private String contractDate;

    @JsonProperty("currency")
    @SerializedName("currency")
    private String currency;

    @JsonProperty("lotAmount")
    @SerializedName("lotAmount")
    private int lotAmount;

    @JsonProperty("measureUnit")
    @SerializedName("measureUnit")
    private String measureUnit;

    @JsonProperty("orderDate")
    @SerializedName("orderDate")
    private String orderDate;

    @JsonProperty("orderId")
    @SerializedName("orderId")
    private String orderId;

    @JsonProperty("orderQuantity")
    @SerializedName("orderQuantity")
    private int orderQuantity;

    @JsonProperty("productCode")
    @SerializedName("productCode")
    private String productCode;

    @JsonProperty("receivedAmount")
    @SerializedName("receivedAmount")
    private int receivedAmount;

    @JsonProperty("receivedDate")
    @SerializedName("receivedDate")
    private String receivedDate;

    @JsonProperty("status")
    @SerializedName("status")
    private int status;

    @JsonProperty("supplierId")
    @SerializedName("supplierId")
    private String supplierId;


    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getContractDate() {
        return contractDate;
    }

    public void setContractDate(String contractDate) {
        this.contractDate = contractDate;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public int getLotAmount() {
        return lotAmount;
    }

    public void setLotAmount(int lotAmount) {
        this.lotAmount = lotAmount;
    }

    public String getMeasureUnit() {
        return measureUnit;
    }

    public void setMeasureUnit(String measureUnit) {
        this.measureUnit = measureUnit;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public int getOrderQuantity() {
        return orderQuantity;
    }

    public void setOrderQuantity(int orderQuantity) {
        this.orderQuantity = orderQuantity;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public int getReceivedAmount() {
        return receivedAmount;
    }

    public void setReceivedAmount(int receivedAmount) {
        this.receivedAmount = receivedAmount;
    }

    public String getReceivedDate() {
        return receivedDate;
    }

    public void setReceivedDate(String receivedDate) {
        this.receivedDate = receivedDate;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(String supplierId) {
        this.supplierId = supplierId;
    }


}
