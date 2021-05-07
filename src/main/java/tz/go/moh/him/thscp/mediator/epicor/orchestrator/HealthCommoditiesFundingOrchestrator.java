package tz.go.moh.him.thscp.mediator.epicor.orchestrator;
import org.codehaus.plexus.util.StringUtils;
import org.openhim.mediator.engine.MediatorConfig;
import org.openhim.mediator.engine.messages.MediatorHTTPRequest;
import tz.go.moh.him.mediator.core.domain.ResultDetail;
import tz.go.moh.him.mediator.core.validator.DateValidatorUtils;
import tz.go.moh.him.thscp.mediator.epicor.domain.HealthCommoditiesFundingRequest;
import tz.go.moh.him.thscp.mediator.epicor.utils.RequestConstantUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HealthCommoditiesFundingOrchestrator extends BaseOrchestrator {

    /**
     * Initializes a new instance of the {@link HealthCommoditiesFundingOrchestrator} class.
     *
     * @param config The mediator configuration.
     */
    public HealthCommoditiesFundingOrchestrator(MediatorConfig config) {
        super(config);
    }

    /**
     * Validate HealthCommodityFundingRequest Required Fields
     *
     * @param healthCommoditiesFundingRequest to be validated
     * @return array list of validation results details for failed validations
     */
    public List<ResultDetail> validateMessage(List<HealthCommoditiesFundingRequest> healthCommoditiesFundingRequest) {
        ArrayList<ResultDetail> resultDetailsList = new ArrayList<>();

        for (HealthCommoditiesFundingRequest request : healthCommoditiesFundingRequest) {
            if (StringUtils.isBlank(request.getUuid()))
                resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("GENERIC_ERR"), "uuid"), null));

            if (StringUtils.isBlank(String.valueOf(request.getAllocatedFund())))
                resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("GENERIC_ERR"), "allocatedFund"), null));

            if (StringUtils.isBlank(String.valueOf(request.getDisbursedFund())))
                resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("GENERIC_ERR"), "disbursedFund"), null));

            if (StringUtils.isBlank(request.getEndDate()))
                resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("GENERIC_ERR"), "endDate"), null));

            if (StringUtils.isBlank(request.getFacilityId()))
                resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("GENERIC_ERR"), "facilityId"), null));

//            if (StringUtils.isBlank(String.valueOf(request.getProductCode())))
//                resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("GENERIC_ERR"), "productCode"), null));

            if (StringUtils.isBlank(request.getProgram()))
                resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("GENERIC_ERR"), "program"), null));

            if (StringUtils.isBlank(request.getSource()))
                resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("GENERIC_ERR"), "source"), null));

            if (StringUtils.isBlank(String.valueOf(request.getStartDate())))
                resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("GENERIC_ERR"), "startDate"), null));


            try {
                if (!DateValidatorUtils.isValidPastDate(request.getStartDate(), checkDateFormatStrings(request.getStartDate()))) {
                    resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("ERROR_DATE_IS_NOT_VALID_PAST_DATE"), "startDate"), null));
                } else {
                    SimpleDateFormat healthCommodityFundingDateFormat = new SimpleDateFormat(checkDateFormatStrings(request.getStartDate()));
                    request.setStartDate(thscpDateFormat.format(healthCommodityFundingDateFormat.parse(request.getStartDate())));

                }
            } catch (ParseException e) {
                resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("ERROR_INVALID_DATE_FORMAT"), "startDate"), null));
            }
        }

        return resultDetailsList;
    }


    @Override
    protected void onReceiveRequestInternal(MediatorHTTPRequest request) throws Exception {
        List<HealthCommoditiesFundingRequest> healthCommoditiesFundingRequest = Arrays.asList(serializer.deserialize(request.getBody(), HealthCommoditiesFundingRequest[].class));

        sendDataToThscp(healthCommoditiesFundingRequest, validateMessage(healthCommoditiesFundingRequest), RequestConstantUtils.HEALTH_COMMODITIES_FUNDING_REQUEST);

    }

}

