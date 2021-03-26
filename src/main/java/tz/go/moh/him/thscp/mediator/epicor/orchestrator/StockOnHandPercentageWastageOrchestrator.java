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
import tz.go.moh.him.thscp.mediator.epicor.domain.StockOnHandPercentageWastageRequest;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StockOnHandPercentageWastageOrchestrator extends BaseOrchestrator {


    /**
     * Initializes a new instance of the {@link DosProductOrchestrator} class.
     *
     * @param config The mediator configuration.
     */
    public StockOnHandPercentageWastageOrchestrator(MediatorConfig config) {
        super(config);
    }

    /**
     * Handles data validations
     *
     * @param receivedList The object to be validated
     */
    protected List<StockOnHandPercentageWastageRequest> validateData(List<StockOnHandPercentageWastageRequest> receivedList) {
        List<StockOnHandPercentageWastageRequest> validReceivedList = new ArrayList<>();

        for (StockOnHandPercentageWastageRequest stockOnHandPercentageWastageRequest : receivedList) {
            ErrorMessage errorMessage = new ErrorMessage();
            errorMessage.setSource(new Gson().toJson(stockOnHandPercentageWastageRequest));

            List<ResultDetail> resultDetailsList = new ArrayList<>();

            if (stockOnHandPercentageWastageRequest == null) {
                resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, errorMessageResource.getString("ERROR_INVALID_PAYLOAD"), null));
            } else {
                resultDetailsList.addAll(validateRequiredFields(stockOnHandPercentageWastageRequest));
            }

            //TODO implement additional data validations checks
            if (resultDetailsList.size() == 0) {
                //No errors were found during data validation
                //adding the service received to the valid payload to be sent to HDR
                validReceivedList.add(stockOnHandPercentageWastageRequest);
            } else {
                //Adding the validation results to the Error message object
                errorMessage.setResultsDetails(resultDetailsList);
                errorMessages.add(errorMessage);
            }
        }
        return validReceivedList;
    }

    /**
     * Validate stockOnHandPercentageWastageRequest Required Fields
     *
     * @param stockOnHandPercentageWastageRequest to be validated
     * @return array list of validation results details for failed validations
     */
    public List<ResultDetail> validateRequiredFields(StockOnHandPercentageWastageRequest stockOnHandPercentageWastageRequest) {
        List<ResultDetail> resultDetailsList = new ArrayList<>();

        if (StringUtils.isBlank(stockOnHandPercentageWastageRequest.getUuid()))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("GENERIC_ERR"), "uuid"), null));

        if (StringUtils.isBlank(String.valueOf(stockOnHandPercentageWastageRequest.getConsumedQuantity())))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("GENERIC_ERR"), "consumedQuantity"), null));

        if (StringUtils.isBlank(String.valueOf(stockOnHandPercentageWastageRequest.getDamagedPercentage())))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("GENERIC_ERR"), "damagedPercentage"), null));

        if (StringUtils.isBlank(String.valueOf(stockOnHandPercentageWastageRequest.getExpiredPercentage())))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("GENERIC_ERR"), "expiredPercentage"), null));

        if (StringUtils.isBlank(stockOnHandPercentageWastageRequest.getFacilityId()))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("GENERIC_ERR"), "facilityId"), null));

        if (StringUtils.isBlank(String.valueOf(stockOnHandPercentageWastageRequest.getLostPercentage())))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("GENERIC_ERR"), "lostPercentage"), null));

        if (StringUtils.isBlank(String.valueOf(stockOnHandPercentageWastageRequest.getFacilityLevel())))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("GENERIC_ERR"), "facilityLevel"), null));

        if (StringUtils.isBlank(String.valueOf(stockOnHandPercentageWastageRequest.getMonthsOfStock())))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("GENERIC_ERR"), "monthsOfStock"), null));

        if (StringUtils.isBlank(String.valueOf(stockOnHandPercentageWastageRequest.getPeriod())))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("GENERIC_ERR"), "period"), null));

        if (StringUtils.isBlank(String.valueOf(stockOnHandPercentageWastageRequest.getProductCode())))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("GENERIC_ERR"), "productCode"), null));

        if (StringUtils.isBlank(stockOnHandPercentageWastageRequest.getProgramCode()))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("GENERIC_ERR"), "programCode"), null));

        if (StringUtils.isBlank(String.valueOf(stockOnHandPercentageWastageRequest.getQuantity())))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("GENERIC_ERR"), "quantity"), null));

        if (StringUtils.isBlank(stockOnHandPercentageWastageRequest.getStockId()))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("GENERIC_ERR"), "stockId"), null));


        try {
            if (!DateValidatorUtils.isValidPastDate(stockOnHandPercentageWastageRequest.getPeriod(), checkDateFormatStrings(stockOnHandPercentageWastageRequest.getPeriod()))) {
                resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("ERROR_DATE_IS_NOT_VALID_PAST_DATE"), "period"), null));
            } else {
                SimpleDateFormat stockOnHandPercentageWastageDateFormat = new SimpleDateFormat(checkDateFormatStrings(stockOnHandPercentageWastageRequest.getPeriod()));
                stockOnHandPercentageWastageRequest.setPeriod(thscpDateFormat.format(stockOnHandPercentageWastageDateFormat.parse(stockOnHandPercentageWastageRequest.getPeriod())));

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
            List<StockOnHandPercentageWastageRequest> stockOnHandPercentageWastageRequestList = new ArrayList<>();
            try {
                stockOnHandPercentageWastageRequestList = convertMessageBodyToPojoList(((MediatorHTTPRequest) msg).getBody());
            } catch (Exception e) {
                //In-case of an exception creating an error message with the stack trace
                ErrorMessage errorMessage = new ErrorMessage(
                        workingRequest.getBody(),
                        Arrays.asList(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, e.getMessage(), tz.go.moh.him.mediator.core.utils.StringUtils.writeStackTraceToString(e)))
                );
                errorMessages.add(errorMessage);
            }

            log.info("Received payload in JSON = " + new Gson().toJson(stockOnHandPercentageWastageRequestList));

            List<StockOnHandPercentageWastageRequest> validatedObjects;
            if (stockOnHandPercentageWastageRequestList.isEmpty()) {
                ErrorMessage errorMessage = new ErrorMessage(
                        workingRequest.getBody(),
                        Arrays.asList(
                                new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, errorMessageResource.getString("ERROR_INVALID_PAYLOAD"), null)
                        )
                );
                errorMessages.add(errorMessage);
                validatedObjects = new ArrayList<>();
            } else {
                validatedObjects = validateData(stockOnHandPercentageWastageRequestList);
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


    protected List<StockOnHandPercentageWastageRequest> convertMessageBodyToPojoList(String msg) throws JsonSyntaxException {
        List<StockOnHandPercentageWastageRequest> stockOnHandPercentageWastageRequestList = Arrays.asList(serializer.deserialize(msg, StockOnHandPercentageWastageRequest[].class));
        return stockOnHandPercentageWastageRequestList;
    }

}
