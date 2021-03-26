package tz.go.moh.him.thscp.mediator.epicor.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;

public class PercentageHealthFacilitiesStaffRequest {

    @JsonProperty("uuid")
    @SerializedName("uuid")
    private String uuid;

    @JsonProperty("facilityId")
    @SerializedName("facilityId")
    private String facilityId;

    @JsonProperty("period")
    @SerializedName("period")
    private String period;


    @JsonProperty("postId")
    @SerializedName("postId")
    private String postId;

    @JsonProperty("postName")
    @SerializedName("postName")
    private String postName;

    @JsonProperty("totalPost")
    @SerializedName("totalPost")
    private String totalPost;

    @JsonProperty("vacantPost")
    @SerializedName("vacantPost")
    private String vacantPost;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
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

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getPostName() {
        return postName;
    }

    public void setPostName(String postName) {
        this.postName = postName;
    }

    public String getTotalPost() {
        return totalPost;
    }

    public void setTotalPost(String totalPost) {
        this.totalPost = totalPost;
    }

    public String getVacantPost() {
        return vacantPost;
    }

    public void setVacantPost(String vacantPost) {
        this.vacantPost = vacantPost;
    }
}
