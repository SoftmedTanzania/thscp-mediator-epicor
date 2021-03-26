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
import tz.go.moh.him.thscp.mediator.epicor.domain.ProcurementSupplyPlanRequest;
import tz.go.moh.him.thscp.mediator.epicor.domain.ProductRecallAlertsRequest;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.lang.reflect.Type;

public class   ProductRecallAlertsOrchestrator extends BaseOrchestrator{

    /**
     * Initializes a new instance of the {@link DosProductOrchestrator} class.
     *
     * @param config The mediator configuration.
     */
    public ProductRecallAlertsOrchestrator(MediatorConfig config) {
        super(config);
    }

    /**
     * Handles data validations
     *
     * @param receivedList The object to be validated
     */
    protected List<ProductRecallAlertsRequest> validateData(List<ProductRecallAlertsRequest> receivedList) {
        List<ProductRecallAlertsRequest> validReceivedList = new ArrayList<>();

        for (ProductRecallAlertsRequest productRecallAlertsRequest : receivedList) {
            ErrorMessage errorMessage = new ErrorMessage();
            errorMessage.setSource(new Gson().toJson(productRecallAlertsRequest));

            List<ResultDetail> resultDetailsList = new ArrayList<>();

            if (productRecallAlertsRequest == null) {
                resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, errorMessageResource.getString("ERROR_INVALID_PAYLOAD"), null));
            } else {
                resultDetailsList.addAll(validateRequiredFields(productRecallAlertsRequest));
            }

            //TODO implement additional data validations checks
            if (resultDetailsList.size() == 0) {
                //No errors were found during data validation
                //adding the service received to the valid payload to be sent to HDR
                validReceivedList.add(productRecallAlertsRequest);
            } else {
                //Adding the validation results to the Error message object
                errorMessage.setResultsDetails(resultDetailsList);
                errorMessages.add(errorMessage);
            }
        }
        return validReceivedList;
    }

    /**
     * Validate ProductRecallAlerts Request Required Fields
     *
     * @param productRecallAlertsRequest to be validated
     * @return array list of validation results details for failed validations
     */
    public List<ResultDetail> validateRequiredFields(ProductRecallAlertsRequest productRecallAlertsRequest) {
        List<ResultDetail> resultDetailsList = new ArrayList<>();

        if (StringUtils.isBlank(productRecallAlertsRequest.getActionRequired()))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("GENERIC_ERR"),"actionRequired"), null));

        if (StringUtils.isBlank(productRecallAlertsRequest.getAffectedCommunity()))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("GENERIC_ERR"),"affectedCommunity"), null));

        if (StringUtils.isBlank(productRecallAlertsRequest.getBatchNumber()))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("GENERIC_ERR"),"batchNumber"), null));

        if (StringUtils.isBlank(productRecallAlertsRequest.getClosureDate()))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("GENERIC_ERR"),"closureDate"), null));

        if (StringUtils.isBlank(productRecallAlertsRequest.getDescription()))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("GENERIC_ERR"),"description"), null));

        if (StringUtils.isBlank(String.valueOf(productRecallAlertsRequest.getDistributedQuantity())))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("GENERIC_ERR"),"distributedQuantity"), null));

        if (StringUtils.isBlank(productRecallAlertsRequest.getIssue()))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("GENERIC_ERR"),"issue"), null));

        if (StringUtils.isBlank(productRecallAlertsRequest.getRecallDate()))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("GENERIC_ERR"),"recallDate"), null));

        if (StringUtils.isBlank(String.valueOf(productRecallAlertsRequest.getRecallFrequency())))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("GENERIC_ERR"),"recallFrequency"), null));

        if (StringUtils.isBlank(String.valueOf(productRecallAlertsRequest.getRecalledQuantity())))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("GENERIC_ERR"),"recallQuantity"), null));

        if (StringUtils.isBlank(String.valueOf(productRecallAlertsRequest.getStartDate())))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("GENERIC_ERR"),"startDate"), null));

        if (StringUtils.isBlank(productRecallAlertsRequest.getUnit()))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("GENERIC_ERR"),"unit"), null));

        try {
            if (!DateValidatorUtils.isValidPastDate(productRecallAlertsRequest.getRecallDate(), checkDateFormatStrings(productRecallAlertsRequest.getRecallDate()))) {
                resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("ERROR_DATE_IS_NOT_VALID_PAST_DATE"),"recallDate"), null));
            }
            else{
                SimpleDateFormat ProductRecallAlertsDateFormat = new SimpleDateFormat(checkDateFormatStrings(productRecallAlertsRequest.getRecallDate()));
                productRecallAlertsRequest.setRecallDate(thscpDateFormat.format(ProductRecallAlertsDateFormat.parse(productRecallAlertsRequest.getRecallDate())));

            }
        } catch (ParseException e) {
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("ERROR_INVALID_DATE_FORMAT"),"recallDate"),null));
        }

        try {
            if (!DateValidatorUtils.isValidPastDate(productRecallAlertsRequest.getStartDate(), checkDateFormatStrings(productRecallAlertsRequest.getStartDate()))) {
                resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("ERROR_DATE_IS_NOT_VALID_PAST_DATE"),"startDate"), null));
            }
            else{
                SimpleDateFormat ProductRecallAlertsDateFormat = new SimpleDateFormat(checkDateFormatStrings(productRecallAlertsRequest.getStartDate()));
                productRecallAlertsRequest.setStartDate(thscpDateFormat.format(ProductRecallAlertsDateFormat.parse(productRecallAlertsRequest.getStartDate())));
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
            List<ProductRecallAlertsRequest> productRecallAlertsRequestList = new ArrayList<>();
            try {
                productRecallAlertsRequestList = convertMessageBodyToPojoList(((MediatorHTTPRequest) msg).getBody());
            } catch (Exception e) {
                //In-case of an exception creating an error message with the stack trace
                ErrorMessage errorMessage = new ErrorMessage(
                        workingRequest.getBody(),
                        Arrays.asList(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, e.getMessage(), tz.go.moh.him.mediator.core.utils.StringUtils.writeStackTraceToString(e)))
                );
                errorMessages.add(errorMessage);
            }

            log.info("Received payload in JSON = " + new Gson().toJson(productRecallAlertsRequestList));

            List<ProductRecallAlertsRequest> validatedObjects;
            if (productRecallAlertsRequestList.isEmpty()) {
                ErrorMessage errorMessage = new ErrorMessage(
                        workingRequest.getBody(),
                        Arrays.asList(
                                new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, errorMessageResource.getString("ERROR_INVALID_PAYLOAD"), null)
                        )
                );
                errorMessages.add(errorMessage);
                validatedObjects = new ArrayList<>();
            } else {
                validatedObjects = validateData(productRecallAlertsRequestList);
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

    protected List<ProductRecallAlertsRequest> convertMessageBodyToPojoList(String msg) throws JsonSyntaxException {
        List<ProductRecallAlertsRequest> productRecallAlertsRequestList = Arrays.asList(serializer.deserialize(msg,ProductRecallAlertsRequest[].class));
        return productRecallAlertsRequestList;
    }
}
