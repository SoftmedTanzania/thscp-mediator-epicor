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

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class ItemFillRateOrchestrator extends BaseOrchestrator {

    /**
     * Initializes a new instance of the {@link ItemFillRateOrchestrator} class.
     *
     * @param config The mediator configuration.
     */
    public ItemFillRateOrchestrator(MediatorConfig config) {
        super(config);
    }

    /**
     * Handles data validations
     *
     * @param receivedList The object to be validated
     */
    protected List<ItemFillRateRequest> validateData(List<ItemFillRateRequest> receivedList) {
        List<ItemFillRateRequest> validReceivedList = new ArrayList<>();

        for (ItemFillRateRequest itemFillRateRequest : receivedList) {
            ErrorMessage errorMessage = new ErrorMessage();
            errorMessage.setSource(new Gson().toJson(itemFillRateRequest));

            List<ResultDetail> resultDetailsList = new ArrayList<>();

            if (itemFillRateRequest == null) {
                resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, errorMessageResource.getString("ERROR_INVALID_PAYLOAD"), null));
            } else {
                resultDetailsList.addAll(validateRequiredFields(itemFillRateRequest));
            }

            //TODO implement additional data validations checks
            if (resultDetailsList.size() == 0) {
                //No errors were found during data validation
                //adding the service received to the valid payload to be sent to HDR
                validReceivedList.add(itemFillRateRequest);
            } else {
                //Adding the validation results to the Error message object
                errorMessage.setResultsDetails(resultDetailsList);
                errorMessages.add(errorMessage);
            }
        }
        return validReceivedList;
    }

    /**
     * Validate itemFillRateRequest Required Fields
     *
     * @param itemFillRateRequest to be validated
     * @return array list of validation results details for failed validations
     */
    public List<ResultDetail> validateRequiredFields(ItemFillRateRequest itemFillRateRequest) {
        List<ResultDetail> resultDetailsList = new ArrayList<>();

        if (StringUtils.isBlank(itemFillRateRequest.getUuid()))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("GENERIC_ERR"), "uuid"), null));

        if (StringUtils.isBlank(String.valueOf(itemFillRateRequest.getDeliveredQuantity())))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("GENERIC_ERR"),"deliveredQuantity"), null));

        if (StringUtils.isBlank(itemFillRateRequest.getDeliveryDate()))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("GENERIC_ERR"),"deliveryDate"), null));

        if (StringUtils.isBlank(itemFillRateRequest.getDeliveryFromFacilityId()))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("GENERIC_ERR"), "deliveryFromFacilityId"), null));

        if (StringUtils.isBlank(itemFillRateRequest.getOrderDate()))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("GENERIC_ERR"),"orderDate"), null));

        if (StringUtils.isBlank(String.valueOf(itemFillRateRequest.getOrderFromFacilityId())))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("GENERIC_ERR"),"orderFromFacilityId"), null));

        if (StringUtils.isBlank(String.valueOf(itemFillRateRequest.getDeliveryPromiseDate())))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("GENERIC_ERR"),"deliveryPromiseDate"), null));

        if (StringUtils.isBlank(itemFillRateRequest.getOrderId()))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("GENERIC_ERR"),"orderId"), null));

        if (StringUtils.isBlank(itemFillRateRequest.getOrderStatus()))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("GENERIC_ERR"),"orderStatus"), null));

        if (StringUtils.isBlank(String.valueOf(itemFillRateRequest.getOrderType())))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("GENERIC_ERR"),"orderType"), null));

        if (StringUtils.isBlank(String.valueOf(itemFillRateRequest.getOrderedQuantity())))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("GENERIC_ERR"),"orderedQuantity"), null));

        if (StringUtils.isBlank(itemFillRateRequest.getProductCode()))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("GENERIC_ERR"),"productCode"), null));

        if (StringUtils.isBlank(itemFillRateRequest.getProgramCode()))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("GENERIC_ERR"),"programCode"), null));

        if (StringUtils.isBlank(String.valueOf(itemFillRateRequest.getTargetDays())))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("GENERIC_ERR"),"targetDays"), null));

        try {
            if (!DateValidatorUtils.isValidPastDate(itemFillRateRequest.getDeliveryDate(), checkDateFormatStrings(itemFillRateRequest.getDeliveryDate()))) {
                resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("ERROR_DATE_IS_NOT_VALID_PAST_DATE"),"deliveryDate"), null));
            }
            else{
                SimpleDateFormat itemFillRateDateFormat = new SimpleDateFormat(checkDateFormatStrings(itemFillRateRequest.getDeliveryDate()));
                itemFillRateRequest.setDeliveryDate(thscpDateFormat.format(itemFillRateDateFormat.parse(itemFillRateRequest.getDeliveryDate())));

            }
        } catch (ParseException e) {
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("ERROR_INVALID_DATE_FORMAT"),"deliveryDate"),null));
        }

        try {
            if (!DateValidatorUtils.isValidPastDate(itemFillRateRequest.getOrderDate(), checkDateFormatStrings(itemFillRateRequest.getOrderDate()))) {
                resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("ERROR_DATE_IS_NOT_VALID_PAST_DATE"),"orderDate"), null));
            }
            else{
                SimpleDateFormat itemFillRateDateFormat = new SimpleDateFormat(checkDateFormatStrings(itemFillRateRequest.getOrderDate()));
                itemFillRateRequest.setOrderDate(thscpDateFormat.format(itemFillRateDateFormat.parse(itemFillRateRequest.getOrderDate())));

            }
        } catch (ParseException e) {
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("ERROR_INVALID_DATE_FORMAT"),"orderDate"),null));
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
            List<ItemFillRateRequest> itemFillRateRequestList = new ArrayList<>();
            try {
                itemFillRateRequestList = convertMessageBodyToPojoList(((MediatorHTTPRequest) msg).getBody());
            } catch (Exception e) {
                //In-case of an exception creating an error message with the stack trace
                ErrorMessage errorMessage = new ErrorMessage(
                        workingRequest.getBody(),
                        Arrays.asList(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, e.getMessage(), tz.go.moh.him.mediator.core.utils.StringUtils.writeStackTraceToString(e)))
                );
                errorMessages.add(errorMessage);
            }

            log.info("Received payload in JSON = " + new Gson().toJson(itemFillRateRequestList));

            List<ItemFillRateRequest> validatedObjects;
            if (itemFillRateRequestList.isEmpty()) {
                ErrorMessage errorMessage = new ErrorMessage(
                        workingRequest.getBody(),
                        Arrays.asList(
                                new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, errorMessageResource.getString("ERROR_INVALID_PAYLOAD"), null)
                        )
                );
                errorMessages.add(errorMessage);
                validatedObjects = new ArrayList<>();
            } else {
                validatedObjects = validateData(itemFillRateRequestList);
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

    protected List<ItemFillRateRequest> convertMessageBodyToPojoList(String msg) throws JsonSyntaxException {
        List<ItemFillRateRequest> itemFillRateRequestList = Arrays.asList(serializer.deserialize(msg,ItemFillRateRequest[].class));
        return itemFillRateRequestList;
    }

}
