package tz.go.moh.him.thscp.mediator.epicor.orchestrator;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.codehaus.plexus.util.StringUtils;
import org.openhim.mediator.engine.MediatorConfig;
import org.openhim.mediator.engine.messages.MediatorHTTPRequest;
import org.openhim.mediator.engine.messages.MediatorHTTPResponse;
import tz.go.moh.him.mediator.core.domain.ErrorMessage;
import tz.go.moh.him.mediator.core.domain.ResultDetail;
import tz.go.moh.him.mediator.core.validator.DateValidatorUtils;
import tz.go.moh.him.thscp.mediator.epicor.domain.StockAvailabilityRequest;

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
     * Handles data validations
     *
     * @param receivedList The object to be validated
     */
    protected List<StockAvailabilityRequest> validateData(List<StockAvailabilityRequest> receivedList) {
        List<StockAvailabilityRequest> validReceivedList = new ArrayList<>();

        for (StockAvailabilityRequest stockAvailabilityRequest : receivedList) {
            ErrorMessage errorMessage = new ErrorMessage();
            errorMessage.setSource(new Gson().toJson(stockAvailabilityRequest));

            List<ResultDetail> resultDetailsList = new ArrayList<>();

            if (stockAvailabilityRequest == null) {
                resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, errorMessageResource.getString("ERROR_INVALID_PAYLOAD"), null));
            } else {
                resultDetailsList.addAll(validateRequiredFields(stockAvailabilityRequest));
            }

            //TODO implement additional data validations checks
            if (resultDetailsList.size() == 0) {
                //No errors were found during data validation
                //adding the service received to the valid payload to be sent to HDR
                validReceivedList.add(stockAvailabilityRequest);
            } else {
                //Adding the validation results to the Error message object
                errorMessage.setResultsDetails(resultDetailsList);
                errorMessages.add(errorMessage);
            }
        }
        return validReceivedList;
    }

    /**
     * Validate stockAvailabilityRequest Required Fields
     *
     * @param stockAvailabilityRequest to be validated
     * @return array list of validation results details for failed validations
     */
    public List<ResultDetail> validateRequiredFields(StockAvailabilityRequest stockAvailabilityRequest) {
        List<ResultDetail> resultDetailsList = new ArrayList<>();

        if (StringUtils.isBlank(stockAvailabilityRequest.getUuid()))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("GENERIC_ERR"), "uuid"), null));

        if (StringUtils.isBlank(String.valueOf(stockAvailabilityRequest.getDistrict())))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("GENERIC_ERR"), "district"), null));

        if (StringUtils.isBlank(String.valueOf(stockAvailabilityRequest.getExpected())))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("GENERIC_ERR"), "expected"), null));

        if (StringUtils.isBlank(stockAvailabilityRequest.getPeriod()))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("GENERIC_ERR"), "period"), null));

        if (StringUtils.isBlank(stockAvailabilityRequest.getProgram()))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("GENERIC_ERR"), "program"), null));

        try {
            if (!DateValidatorUtils.isValidPastDate(stockAvailabilityRequest.getPeriod(), checkDateFormatStrings(stockAvailabilityRequest.getPeriod()))) {
                resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("ERROR_DATE_IS_NOT_VALID_PAST_DATE"), "period"), null));
            } else {
                SimpleDateFormat stockAvailabilityDateFormat = new SimpleDateFormat(checkDateFormatStrings(stockAvailabilityRequest.getPeriod()));
                stockAvailabilityRequest.setPeriod(thscpDateFormat.format(stockAvailabilityDateFormat.parse(stockAvailabilityRequest.getPeriod())));

            }
        } catch (ParseException e) {
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("ERROR_INVALID_DATE_FORMAT"), "period"), null));
        }

        return resultDetailsList;
    }

    @Override
    public void onReceive(Object msg) {
        if (msg instanceof MediatorHTTPRequest) {
            workingRequest = (MediatorHTTPRequest) msg;

            log.info("Received request: " + workingRequest.getHost() + " " + workingRequest.getMethod() + " " + workingRequest.getPath());

            //Converting the received request body to POJO List
            List<StockAvailabilityRequest> stockAvailabilityRequestList = new ArrayList<>();
            try {
                stockAvailabilityRequestList = convertMessageBodyToPojoList(((MediatorHTTPRequest) msg).getBody());
            } catch (Exception e) {
                //In-case of an exception creating an error message with the stack trace
                ErrorMessage errorMessage = new ErrorMessage(
                        workingRequest.getBody(),
                        Arrays.asList(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, e.getMessage(), tz.go.moh.him.mediator.core.utils.StringUtils.writeStackTraceToString(e)))
                );
                errorMessages.add(errorMessage);
            }

            log.info("Received payload in JSON = " + new Gson().toJson(stockAvailabilityRequestList));

            List<StockAvailabilityRequest> validatedObjects;
            if (stockAvailabilityRequestList.isEmpty()) {
                ErrorMessage errorMessage = new ErrorMessage(
                        workingRequest.getBody(),
                        Arrays.asList(
                                new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, errorMessageResource.getString("ERROR_INVALID_PAYLOAD"), null)
                        )
                );
                errorMessages.add(errorMessage);
                validatedObjects = new ArrayList<>();
            } else {
                validatedObjects = validateData(stockAvailabilityRequestList);
            }

            log.info("validated object is" + new Gson().toJson(validatedObjects));

            sendDataToThscp(new Gson().toJson(validatedObjects));
        } else if (msg instanceof MediatorHTTPResponse) { //respond
            log.info("Received response from thscp");
            (workingRequest).getRequestHandler().tell(((MediatorHTTPResponse) msg).toFinishRequest(), getSelf());
        } else {
            unhandled(msg);
        }
    }


    protected List<StockAvailabilityRequest> convertMessageBodyToPojoList(String msg) throws JsonSyntaxException {
        List<StockAvailabilityRequest> stockAvailabilityRequestList = Arrays.asList(serializer.deserialize(msg, StockAvailabilityRequest[].class));
        return stockAvailabilityRequestList;
    }
}
