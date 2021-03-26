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
import org.apache.commons.lang3.tuple.Pair;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.codehaus.plexus.util.StringUtils;
import org.glassfish.grizzly.compression.lzma.impl.Base;
import org.json.JSONObject;
import org.openhim.mediator.engine.MediatorConfig;
import org.openhim.mediator.engine.messages.FinishRequest;
import org.openhim.mediator.engine.messages.MediatorHTTPRequest;
import org.openhim.mediator.engine.messages.MediatorHTTPResponse;
import tz.go.moh.him.mediator.core.domain.ErrorMessage;
import tz.go.moh.him.mediator.core.domain.ResultDetail;
import tz.go.moh.him.mediator.core.serialization.JsonSerializer;
import tz.go.moh.him.mediator.core.validator.DateValidatorUtils;
import tz.go.moh.him.thscp.mediator.epicor.domain.ItemFillRateRequest;
import tz.go.moh.him.thscp.mediator.epicor.domain.PercentageHealthFacilitiesStaffRequest;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class PercentageHealthFacilitiesStaffOrchestrator extends BaseOrchestrator {

    /**
     * Initializes a new instance of the {@link DosProductOrchestrator} class.
     *
     * @param config The mediator configuration.
     */
    public PercentageHealthFacilitiesStaffOrchestrator(MediatorConfig config) {
        super(config);
    }

    /**
     * Handles data validations
     *
     * @param receivedList The object to be validated
     */
    protected List<PercentageHealthFacilitiesStaffRequest> validateData(List<PercentageHealthFacilitiesStaffRequest> receivedList) {
        List<PercentageHealthFacilitiesStaffRequest> validReceivedList = new ArrayList<>();

        for (PercentageHealthFacilitiesStaffRequest percentageHealthFacilitiesStaffRequest : receivedList) {
            ErrorMessage errorMessage = new ErrorMessage();
            errorMessage.setSource(new Gson().toJson(percentageHealthFacilitiesStaffRequest));

            List<ResultDetail> resultDetailsList = new ArrayList<>();

            if (percentageHealthFacilitiesStaffRequest == null) {
                resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, errorMessageResource.getString("ERROR_INVALID_PAYLOAD"), null));
            } else {
                resultDetailsList.addAll(validateRequiredFields(percentageHealthFacilitiesStaffRequest));
            }

            //TODO implement additional data validations checks
            if (resultDetailsList.size() == 0) {
                //No errors were found during data validation
                //adding the service received to the valid payload to be sent to HDR
                validReceivedList.add(percentageHealthFacilitiesStaffRequest);
            } else {
                //Adding the validation results to the Error message object
                errorMessage.setResultsDetails(resultDetailsList);
                errorMessages.add(errorMessage);
            }
        }
        return validReceivedList;
    }

    /**
     * Validate PercentageHealthFacilitiesStaffRequest Required Fields
     *
     * @param percentageHealthFacilitiesStaffRequest to be validated
     * @return array list of validation results details for failed validations
     */
    public List<ResultDetail> validateRequiredFields(PercentageHealthFacilitiesStaffRequest percentageHealthFacilitiesStaffRequest) {
        List<ResultDetail> resultDetailsList = new ArrayList<>();

        if (StringUtils.isBlank(percentageHealthFacilitiesStaffRequest.getUuid()))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("GENERIC_ERR"), "uuid"), null));

        if (StringUtils.isBlank(String.valueOf(percentageHealthFacilitiesStaffRequest.getFacilityId())))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("GENERIC_ERR"),"facilityId"), null));

        if (StringUtils.isBlank(percentageHealthFacilitiesStaffRequest.getPeriod()))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("GENERIC_ERR"),"period"), null));

        if (StringUtils.isBlank(percentageHealthFacilitiesStaffRequest.getPostId()))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("GENERIC_ERR"), "postId"), null));

        if (StringUtils.isBlank(percentageHealthFacilitiesStaffRequest.getPostName()))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("GENERIC_ERR"),"postName"), null));

        if (StringUtils.isBlank(String.valueOf(percentageHealthFacilitiesStaffRequest.getTotalPost())))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("GENERIC_ERR"),"totalPost"), null));

        if (StringUtils.isBlank(percentageHealthFacilitiesStaffRequest.getVacantPost()))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("GENERIC_ERR"),"vacantPost"), null));

        try {
            if (!DateValidatorUtils.isValidPastDate(percentageHealthFacilitiesStaffRequest.getPeriod(), checkDateFormatStrings(percentageHealthFacilitiesStaffRequest.getPeriod()))) {
                resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("ERROR_DATE_IS_NOT_VALID_PAST_DATE"),"period"), null));
            }
            else{
                SimpleDateFormat percentageHealthFacilitiesStaffDateFormat = new SimpleDateFormat(checkDateFormatStrings(percentageHealthFacilitiesStaffRequest.getPeriod()));
                percentageHealthFacilitiesStaffRequest.setPeriod(thscpDateFormat.format(percentageHealthFacilitiesStaffDateFormat.parse(percentageHealthFacilitiesStaffRequest.getPeriod())));

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
            List<PercentageHealthFacilitiesStaffRequest> percentageHealthFacilitiesStaffRequestList = new ArrayList<>();
            try {
                percentageHealthFacilitiesStaffRequestList = convertMessageBodyToPojoList(((MediatorHTTPRequest) msg).getBody());
            } catch (Exception e) {
                //In-case of an exception creating an error message with the stack trace
                ErrorMessage errorMessage = new ErrorMessage(
                        workingRequest.getBody(),
                        Arrays.asList(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, e.getMessage(), tz.go.moh.him.mediator.core.utils.StringUtils.writeStackTraceToString(e)))
                );
                errorMessages.add(errorMessage);
            }

            log.info("Received payload in JSON = " + new Gson().toJson(percentageHealthFacilitiesStaffRequestList));

            List<PercentageHealthFacilitiesStaffRequest> validatedObjects;
            if (percentageHealthFacilitiesStaffRequestList.isEmpty()) {
                ErrorMessage errorMessage = new ErrorMessage(
                        workingRequest.getBody(),
                        Arrays.asList(
                                new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, errorMessageResource.getString("ERROR_INVALID_PAYLOAD"), null)
                        )
                );
                errorMessages.add(errorMessage);
                validatedObjects = new ArrayList<>();
            } else {
                validatedObjects = validateData(percentageHealthFacilitiesStaffRequestList);
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

    protected List<PercentageHealthFacilitiesStaffRequest> convertMessageBodyToPojoList(String msg) throws JsonSyntaxException {
        List<PercentageHealthFacilitiesStaffRequest> percentageHealthFacilitiesStaffRequestList = Arrays.asList(serializer.deserialize(msg,PercentageHealthFacilitiesStaffRequest[].class));
        return percentageHealthFacilitiesStaffRequestList;
    }

}
