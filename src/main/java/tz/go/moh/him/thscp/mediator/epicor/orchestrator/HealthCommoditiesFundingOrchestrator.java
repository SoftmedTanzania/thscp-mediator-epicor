package tz.go.moh.him.thscp.mediator.epicor.orchestrator;

import akka.actor.ActorSelection;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpStatus;
import org.codehaus.plexus.util.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.http.HttpHeaders;
import org.json.JSONObject;
import org.openhim.mediator.engine.MediatorConfig;
import org.openhim.mediator.engine.messages.FinishRequest;
import org.openhim.mediator.engine.messages.MediatorHTTPRequest;
import org.openhim.mediator.engine.messages.MediatorHTTPResponse;
import tz.go.moh.him.mediator.core.domain.ErrorMessage;
import tz.go.moh.him.mediator.core.domain.ResultDetail;
import tz.go.moh.him.mediator.core.serialization.JsonSerializer;
import tz.go.moh.him.mediator.core.validator.DateValidatorUtils;
import tz.go.moh.him.thscp.mediator.epicor.domain.EmergencySupplyChainCommoditiesStockStatusRequest;
import tz.go.moh.him.thscp.mediator.epicor.domain.HealthCommoditiesFundingRequest;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.lang.reflect.Type;

public class HealthCommoditiesFundingOrchestrator extends BaseOrchestrator{

    /**
     * Initializes a new instance of the {@link HealthCommoditiesFundingOrchestrator} class.
     *
     * @param config The mediator configuration.
     */
    public HealthCommoditiesFundingOrchestrator(MediatorConfig config) {
        super(config);
    }

    /**
     * Handles data validations
     *
     * @param receivedList The object to be validated
     */
    protected List<HealthCommoditiesFundingRequest> validateData(List<HealthCommoditiesFundingRequest> receivedList) {
        List<HealthCommoditiesFundingRequest> validReceivedList = new ArrayList<>();

        for (HealthCommoditiesFundingRequest healthCommoditiesFundingRequest : receivedList) {
            ErrorMessage errorMessage = new ErrorMessage();
            errorMessage.setSource(new Gson().toJson(healthCommoditiesFundingRequest));

            List<ResultDetail> resultDetailsList = new ArrayList<>();

            if (healthCommoditiesFundingRequest == null) {
                resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, errorMessageResource.getString("ERROR_INVALID_PAYLOAD"), null));
            } else {
                resultDetailsList.addAll(validateRequiredFields(healthCommoditiesFundingRequest));
            }

            //TODO implement additional data validations checks
            if (resultDetailsList.size() == 0) {
                //No errors were found during data validation
                //adding the service received to the valid payload to be sent to HDR
                validReceivedList.add(healthCommoditiesFundingRequest);
            } else {
                //Adding the validation results to the Error message object
                errorMessage.setResultsDetails(resultDetailsList);
                errorMessages.add(errorMessage);
            }
        }
        return validReceivedList;
    }

    /**
     * Validate HealthCommodityFundingRequest Required Fields
     *
     * @param healthCommoditiesFundingRequest to be validated
     * @return array list of validation results details for failed validations
     */
    public List<ResultDetail> validateRequiredFields(HealthCommoditiesFundingRequest healthCommoditiesFundingRequest) {
        List<ResultDetail> resultDetailsList = new ArrayList<>();

        if (StringUtils.isBlank(healthCommoditiesFundingRequest.getUuid()))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("GENERIC_ERR"), "uuid"), null));

        if (StringUtils.isBlank(String.valueOf(healthCommoditiesFundingRequest.getAllocatedFund())))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("GENERIC_ERR"),"allocatedFund"), null));

        if (StringUtils.isBlank(String.valueOf(healthCommoditiesFundingRequest.getDisbursedFund())))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("GENERIC_ERR"),"disbursedFund"), null));

        if (StringUtils.isBlank(healthCommoditiesFundingRequest.getEndDate()))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("GENERIC_ERR"), "endDate"), null));

        if (StringUtils.isBlank(healthCommoditiesFundingRequest.getFacilityId()))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("GENERIC_ERR"),"facilityId"), null));

        if (StringUtils.isBlank(String.valueOf(healthCommoditiesFundingRequest.getProductCode())))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("GENERIC_ERR"),"productCode"), null));

        if (StringUtils.isBlank(healthCommoditiesFundingRequest.getProgram()))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("GENERIC_ERR"),"program"), null));

        if (StringUtils.isBlank(healthCommoditiesFundingRequest.getSource()))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("GENERIC_ERR"),"source"), null));

        if (StringUtils.isBlank(String.valueOf(healthCommoditiesFundingRequest.getStartDate())))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("GENERIC_ERR"),"startDate"), null));


        try {
            if (!DateValidatorUtils.isValidPastDate(healthCommoditiesFundingRequest.getStartDate(), checkDateFormatStrings(healthCommoditiesFundingRequest.getStartDate()))) {
                resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("ERROR_DATE_IS_NOT_VALID_PAST_DATE"),"startDate"), null));
            }
            else{
                SimpleDateFormat healthCommodityFundingDateFormat = new SimpleDateFormat(checkDateFormatStrings(healthCommoditiesFundingRequest.getStartDate()));
                healthCommoditiesFundingRequest.setStartDate(thscpDateFormat.format(healthCommodityFundingDateFormat.parse(healthCommoditiesFundingRequest.getStartDate())));

            }
        } catch (ParseException e) {
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("ERROR_INVALID_DATE_FORMAT"),"startDate"),null));
        }

        return resultDetailsList;
    }

    @Override
    public void onReceive(Object msg) {
        if (msg instanceof MediatorHTTPRequest)
        {
            workingRequest = (MediatorHTTPRequest) msg;

            log.info("Received request: " + workingRequest.getHost() + " " + workingRequest.getMethod() + " " + workingRequest.getPath());

            //Converting the received request body to POJO List
            List<HealthCommoditiesFundingRequest> healthCommoditiesFundingRequestList = new ArrayList<>();
            try {
                healthCommoditiesFundingRequestList = convertMessageBodyToPojoList(((MediatorHTTPRequest) msg).getBody());
            } catch (Exception e) {
                //In-case of an exception creating an error message with the stack trace
                ErrorMessage errorMessage = new ErrorMessage(
                        workingRequest.getBody(),
                        Arrays.asList(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, e.getMessage(), tz.go.moh.him.mediator.core.utils.StringUtils.writeStackTraceToString(e)))
                );
                errorMessages.add(errorMessage);
            }

            log.info("Received payload in JSON = " + new Gson().toJson(healthCommoditiesFundingRequestList));

            List<HealthCommoditiesFundingRequest> validatedObjects;
            if (healthCommoditiesFundingRequestList.isEmpty()) {
                ErrorMessage errorMessage = new ErrorMessage(
                        workingRequest.getBody(),
                        Arrays.asList(
                                new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, errorMessageResource.getString("ERROR_INVALID_PAYLOAD"), null)
                        )
                );
                errorMessages.add(errorMessage);
                validatedObjects = new ArrayList<>();
            } else {
                validatedObjects = validateData(healthCommoditiesFundingRequestList);
            }

            log.info("validated object is" +new Gson().toJson(validatedObjects));

            sendDataToThscp(new Gson().toJson(validatedObjects));
        } else if (msg instanceof MediatorHTTPResponse) { //respond
            log.info("Received response from thscp");
            (workingRequest).getRequestHandler().tell(((MediatorHTTPResponse) msg).toFinishRequest(), getSelf());
        } else {
            unhandled(msg);
        }
    }

    protected List<HealthCommoditiesFundingRequest> convertMessageBodyToPojoList(String msg) throws JsonSyntaxException {
        List<HealthCommoditiesFundingRequest> healthCommoditiesFundingRequestList = Arrays.asList(serializer.deserialize(msg,HealthCommoditiesFundingRequest[].class));
        return healthCommoditiesFundingRequestList;
    }

}

