package tz.go.moh.him.thscp.mediator.epicor.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;

public class SupplierOnTimeDeliveryRequest {

    @JsonProperty("uuid")
    @SerializedName("uuid")
    private String uuid;

    @JsonProperty("deliveredQuantity")
    @SerializedName("deliveredQuantity")
    private int deliveredQuantity;

    @JsonProperty("deliveryDate")
    @SerializedName("deliveryDate")
    private String deliveryDate;

    @JsonProperty("supplierName")
    @SerializedName("supplierName")
    private String supplierName;

    @JsonProperty("orderDate")
    @SerializedName("orderDate")
    private String orderDate;

    @JsonProperty("msdZoneCodeOrderer")
    @SerializedName("msdZoneCodeOrderer")
    private String msdZoneCodeOrderer;

    @JsonProperty("deliveryPromiseDate")
    @SerializedName("deliveryPromiseDate")
    private String deliveryPromiseDate;

    @JsonProperty("orderId")
    @SerializedName("orderId")
    private String orderId;

    @JsonProperty("orderStatus")
    @SerializedName("orderStatus")
    private String orderStatus;

    @JsonProperty("orderType")
    @SerializedName("orderType")
    private String orderType;

    @JsonProperty("orderedQuantity")
    @SerializedName("orderedQuantity")
    private int orderedQuantity;

    @JsonProperty("productCode")
    @SerializedName("productCode")
    private String productCode;

    @JsonProperty("targetDays")
    @SerializedName("targetDays")
    private int targetDays;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public int getDeliveredQuantity() {
        return deliveredQuantity;
    }

    public void setDeliveredQuantity(int deliveredQuantity) {
        this.deliveredQuantity = deliveredQuantity;
    }

    public String getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(String deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getMsdZoneCodeOrderer() {
        return msdZoneCodeOrderer;
    }

    public void setMsdZoneCodeOrderer(String msdZoneCodeOrderer) {
        this.msdZoneCodeOrderer = msdZoneCodeOrderer;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public int getOrderedQuantity() {
        return orderedQuantity;
    }

    public void setOrderedQuantity(int orderedQuantity) {
        this.orderedQuantity = orderedQuantity;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public int getTargetDays() {
        return targetDays;
    }

    public void setTargetDays(int targetDays) {
        this.targetDays = targetDays;
    }

    public String getDeliveryPromiseDate() {
        return deliveryPromiseDate;
    }

    public void setDeliveryPromiseDate(String deliveryPromiseDate) {
        this.deliveryPromiseDate = deliveryPromiseDate;
    }
}
