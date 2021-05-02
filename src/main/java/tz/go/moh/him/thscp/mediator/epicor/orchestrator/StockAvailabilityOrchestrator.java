package tz.go.moh.him.thscp.mediator.epicor.orchestrator;

import org.codehaus.plexus.util.StringUtils;
import org.openhim.mediator.engine.MediatorConfig;
import org.openhim.mediator.engine.messages.MediatorHTTPRequest;
import tz.go.moh.him.mediator.core.domain.ResultDetail;
import tz.go.moh.him.mediator.core.validator.DateValidatorUtils;
import tz.go.moh.him.thscp.mediator.epicor.domain.StockAvailabilityRequest;
import tz.go.moh.him.thscp.mediator.epicor.utils.RequestConstantUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StockAvailabilityOrchestrator extends BaseOrchestrator {

    /**
     * Initializes a new instance of the {@link StockAvailabilityOrchestrator} class.
     *
     * @param config The mediator configuration.
     */
    public StockAvailabilityOrchestrator(MediatorConfig config) {
        super(config);
    }

    /**
     * Validate stockAvailabilityRequest Required Fields
     *
     * @param stockAvailabilityRequest to be validated
     * @return array list of validation results details for failed validations
     */
    public List<ResultDetail> validateMessage(List<StockAvailabilityRequest> stockAvailabilityRequest) {
        ArrayList<ResultDetail> resultDetailsList = new ArrayList<>();

        for (StockAvailabilityRequest request : stockAvailabilityRequest) {
            if (StringUtils.isBlank(request.getUuid()))
                resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("GENERIC_ERR"), "uuid"), null));

            if (StringUtils.isBlank(String.valueOf(request.getDistrict())))
                resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("GENERIC_ERR"), "district"), null));

            if (StringUtils.isBlank(String.valueOf(request.getExpected())))
                resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("GENERIC_ERR"), "expected"), null));

            if (StringUtils.isBlank(request.getPeriod()))
                resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("GENERIC_ERR"), "period"), null));

            if (StringUtils.isBlank(request.getProgram()))
                resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("GENERIC_ERR"), "program"), null));

            try {
                if (!DateValidatorUtils.isValidPastDate(request.getPeriod(), checkDateFormatStrings(request.getPeriod()))) {
                    resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("ERROR_DATE_IS_NOT_VALID_PAST_DATE"), "period"), null));
                } else {
                    SimpleDateFormat stockAvailabilityDateFormat = new SimpleDateFormat(checkDateFormatStrings(request.getPeriod()));
                    request.setPeriod(thscpDateFormat.format(stockAvailabilityDateFormat.parse(request.getPeriod())));

                }
            } catch (ParseException e) {
                resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("ERROR_INVALID_DATE_FORMAT"), "period"), null));
            }
        }

        return resultDetailsList;
    }

    @Override
    protected void onReceiveRequestInternal(MediatorHTTPRequest request) throws Exception {
        List<StockAvailabilityRequest> stockAvailabilityRequest = Arrays.asList(serializer.deserialize(request.getBody(), StockAvailabilityRequest[].class));

        sendDataToThscp(stockAvailabilityRequest, validateMessage(stockAvailabilityRequest), RequestConstantUtils.STOCK_AVAILABILITY_REQUEST);

    }
}
