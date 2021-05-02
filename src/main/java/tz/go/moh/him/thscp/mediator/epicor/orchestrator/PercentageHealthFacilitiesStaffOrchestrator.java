package tz.go.moh.him.thscp.mediator.epicor.orchestrator;

import org.codehaus.plexus.util.StringUtils;
import org.openhim.mediator.engine.MediatorConfig;
import org.openhim.mediator.engine.messages.MediatorHTTPRequest;
import tz.go.moh.him.mediator.core.domain.ResultDetail;
import tz.go.moh.him.mediator.core.validator.DateValidatorUtils;
import tz.go.moh.him.thscp.mediator.epicor.domain.PercentageHealthFacilitiesStaffRequest;
import tz.go.moh.him.thscp.mediator.epicor.utils.RequestConstantUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PercentageHealthFacilitiesStaffOrchestrator extends BaseOrchestrator {

    /**
     * Initializes a new instance of the {@link PercentageHealthFacilitiesStaffOrchestrator} class.
     *
     * @param config The mediator configuration.
     */
    public PercentageHealthFacilitiesStaffOrchestrator(MediatorConfig config) {
        super(config);
    }

    /**
     * Validate PercentageHealthFacilitiesStaffRequest Required Fields
     *
     * @param percentageHealthFacilitiesStaffRequest to be validated
     * @return array list of validation results details for failed validations
     */
    public List<ResultDetail> validateMessage(List<PercentageHealthFacilitiesStaffRequest> percentageHealthFacilitiesStaffRequest) {
        ArrayList<ResultDetail> resultDetailsList = new ArrayList<>();

        for (PercentageHealthFacilitiesStaffRequest request : percentageHealthFacilitiesStaffRequest) {
            if (StringUtils.isBlank(request.getUuid()))
                resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("GENERIC_ERR"), "uuid"), null));

            if (StringUtils.isBlank(String.valueOf(request.getFacilityId())))
                resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("GENERIC_ERR"), "facilityId"), null));

            if (StringUtils.isBlank(request.getPeriod()))
                resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("GENERIC_ERR"), "period"), null));

            if (StringUtils.isBlank(request.getPostId()))
                resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("GENERIC_ERR"), "postId"), null));

            if (StringUtils.isBlank(request.getPostName()))
                resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("GENERIC_ERR"), "postName"), null));

            if (StringUtils.isBlank(String.valueOf(request.getTotalPost())))
                resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("GENERIC_ERR"), "totalPost"), null));

            if (StringUtils.isBlank(request.getVacantPost()))
                resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("GENERIC_ERR"), "vacantPost"), null));

            try {
                if (!DateValidatorUtils.isValidPastDate(request.getPeriod(), checkDateFormatStrings(request.getPeriod()))) {
                    resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("ERROR_DATE_IS_NOT_VALID_PAST_DATE"), "period"), null));
                } else {
                    SimpleDateFormat percentageHealthFacilitiesStaffDateFormat = new SimpleDateFormat(checkDateFormatStrings(request.getPeriod()));
                    request.setPeriod(thscpDateFormat.format(percentageHealthFacilitiesStaffDateFormat.parse(request.getPeriod())));

                }
            } catch (ParseException e) {
                resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("ERROR_INVALID_DATE_FORMAT"), "period"), null));
            }

        }

        return resultDetailsList;
    }

    @Override
    protected void onReceiveRequestInternal(MediatorHTTPRequest request) throws Exception {
        List<PercentageHealthFacilitiesStaffRequest> percentageHealthFacilitiesStaffRequest = Arrays.asList(serializer.deserialize(request.getBody(), PercentageHealthFacilitiesStaffRequest[].class));

        sendDataToThscp(percentageHealthFacilitiesStaffRequest, validateMessage(percentageHealthFacilitiesStaffRequest), RequestConstantUtils.PERCENTAGE_HEALTH_FACILITIES_STAFF_REQUEST);

    }

}
