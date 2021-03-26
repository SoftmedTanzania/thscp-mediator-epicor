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
import tz.go.moh.him.thscp.mediator.epicor.domain.DosProductRequest;
import tz.go.moh.him.thscp.mediator.epicor.domain.EmergencySupplyChainCommoditiesStockStatusRequest;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.lang.reflect.Type;

public class EmergencySupplyChainCommoditiesStockStatusOrchestrator extends BaseOrchestrator {

    /**
     * Initializes a new instance of the {@link DosProductOrchestrator} class.
     *
     * @param config The mediator configuration.
     */
    public EmergencySupplyChainCommoditiesStockStatusOrchestrator(MediatorConfig config) {
        super(config);
    }

    /**
     * Handles data validations
     *
     * @param receivedList The object to be validated
     */
    protected List<EmergencySupplyChainCommoditiesStockStatusRequest> validateData(List<EmergencySupplyChainCommoditiesStockStatusRequest> receivedList) {
        List<EmergencySupplyChainCommoditiesStockStatusRequest> validReceivedList = new ArrayList<>();

        for (EmergencySupplyChainCommoditiesStockStatusRequest emergencySupplyChainCommoditiesStockStatusRequest : receivedList) {
            ErrorMessage errorMessage = new ErrorMessage();
            errorMessage.setSource(new Gson().toJson(emergencySupplyChainCommoditiesStockStatusRequest));

            List<ResultDetail> resultDetailsList = new ArrayList<>();

            if (emergencySupplyChainCommoditiesStockStatusRequest == null) {
                resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, errorMessageResource.getString("ERROR_INVALID_PAYLOAD"), null));
            } else {
                resultDetailsList.addAll(validateRequiredFields(emergencySupplyChainCommoditiesStockStatusRequest));
            }

            //TODO implement additional data validations checks
            if (resultDetailsList.size() == 0) {
                //No errors were found during data validation
                //adding the service received to the valid payload to be sent to HDR
                validReceivedList.add(emergencySupplyChainCommoditiesStockStatusRequest);
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
     * @param emergencySupplyChainCommoditiesStockStatusRequest to be validated
     * @return array list of validation results details for failed validations
     */
    public List<ResultDetail> validateRequiredFields(EmergencySupplyChainCommoditiesStockStatusRequest emergencySupplyChainCommoditiesStockStatusRequest) {
        List<ResultDetail> resultDetailsList = new ArrayList<>();

        if (StringUtils.isBlank(emergencySupplyChainCommoditiesStockStatusRequest.getUuid()))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("GENERIC_ERR"),"uuid"), null));

        if (StringUtils.isBlank(String.valueOf(emergencySupplyChainCommoditiesStockStatusRequest.getAvailableQuantity())))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("GENERIC_ERR"),"availableQuantity"), null));

        if (StringUtils.isBlank(String.valueOf(emergencySupplyChainCommoditiesStockStatusRequest.getFacilityId())))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("GENERIC_ERR"),"facilityId"), null));

        if (StringUtils.isBlank(emergencySupplyChainCommoditiesStockStatusRequest.getPeriod()))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("GENERIC_ERR"),"period"), null));

        if (StringUtils.isBlank(emergencySupplyChainCommoditiesStockStatusRequest.getProductCode()))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("GENERIC_ERR"),"productCode"), null));

        if (StringUtils.isBlank(String.valueOf(emergencySupplyChainCommoditiesStockStatusRequest.getProgramCode())))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("GENERIC_ERR"),"programCode"), null));

        if (StringUtils.isBlank(String.valueOf(emergencySupplyChainCommoditiesStockStatusRequest.getStockOfMonth())))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("GENERIC_ERR"),"stockOfMonth"), null));

        if (StringUtils.isBlank(String.valueOf(emergencySupplyChainCommoditiesStockStatusRequest.getStockQuantity())))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("GENERIC_ERR"),"stockQuantity"), null));

        try {
            if (!DateValidatorUtils.isValidPastDate(emergencySupplyChainCommoditiesStockStatusRequest.getPeriod(), checkDateFormatStrings(emergencySupplyChainCommoditiesStockStatusRequest.getPeriod()))) {
                resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("ERROR_DATE_IS_NOT_VALID_PAST_DATE"),"period"), null));
            }
            else{
                SimpleDateFormat emergencySupplyChainDateFormat = new SimpleDateFormat(checkDateFormatStrings(emergencySupplyChainCommoditiesStockStatusRequest.getPeriod()));
                emergencySupplyChainCommoditiesStockStatusRequest.setPeriod(thscpDateFormat.format(emergencySupplyChainDateFormat.parse(emergencySupplyChainCommoditiesStockStatusRequest.getPeriod())));

            }
        } catch (ParseException e) {
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("ERROR_INVALID_DATE_FORMAT"),"period"),null));
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
            List<EmergencySupplyChainCommoditiesStockStatusRequest> emergencySupplyChainCommoditiesStockStatusRequestList = new ArrayList<>();
            try {
                emergencySupplyChainCommoditiesStockStatusRequestList = convertMessageBodyToPojoList(((MediatorHTTPRequest) msg).getBody());
            } catch (Exception e) {
                //In-case of an exception creating an error message with the stack trace
                ErrorMessage errorMessage = new ErrorMessage(
                        workingRequest.getBody(),
                        Arrays.asList(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, e.getMessage(), tz.go.moh.him.mediator.core.utils.StringUtils.writeStackTraceToString(e)))
                );
                errorMessages.add(errorMessage);
            }

            log.info("Received payload in JSON = " + new Gson().toJson(emergencySupplyChainCommoditiesStockStatusRequestList));

            List<EmergencySupplyChainCommoditiesStockStatusRequest> validatedObjects;
            if (emergencySupplyChainCommoditiesStockStatusRequestList.isEmpty()) {
                ErrorMessage errorMessage = new ErrorMessage(
                        workingRequest.getBody(),
                        Arrays.asList(
                                new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, errorMessageResource.getString("ERROR_INVALID_PAYLOAD"), null)
                        )
                );
                errorMessages.add(errorMessage);
                validatedObjects = new ArrayList<>();
            } else {
                validatedObjects = validateData(emergencySupplyChainCommoditiesStockStatusRequestList);
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


    protected List<EmergencySupplyChainCommoditiesStockStatusRequest> convertMessageBodyToPojoList(String msg) throws JsonSyntaxException {
        List<EmergencySupplyChainCommoditiesStockStatusRequest> emergencySupplyChainCommoditiesStockStatusRequestList = Arrays.asList(serializer.deserialize(msg,EmergencySupplyChainCommoditiesStockStatusRequest[].class));
        return emergencySupplyChainCommoditiesStockStatusRequestList;
    }
}
