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
import tz.go.moh.him.thscp.mediator.epicor.domain.ProcurementSupplyPlanRequest;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProcurementSupplyPlanOrchestrator extends BaseOrchestrator {

    /**
     * Initializes a new instance of the {@link DosProductOrchestrator} class.
     *
     * @param config The mediator configuration.
     */
    public ProcurementSupplyPlanOrchestrator(MediatorConfig config) {
        super(config);
    }

    /**
     * Handles data validations
     *
     * @param receivedList The object to be validated
     */
    protected List<ProcurementSupplyPlanRequest> validateData(List<ProcurementSupplyPlanRequest> receivedList) {
        List<ProcurementSupplyPlanRequest> validReceivedList = new ArrayList<>();

        for (ProcurementSupplyPlanRequest procurementSupplyPlanRequest : receivedList) {
            ErrorMessage errorMessage = new ErrorMessage();
            errorMessage.setSource(new Gson().toJson(procurementSupplyPlanRequest));

            List<ResultDetail> resultDetailsList = new ArrayList<>();

            if (procurementSupplyPlanRequest == null) {
                resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, errorMessageResource.getString("ERROR_INVALID_PAYLOAD"), null));
            } else {
                resultDetailsList.addAll(validateRequiredFields(procurementSupplyPlanRequest));
            }

            //TODO implement additional data validations checks
            if (resultDetailsList.size() == 0) {
                //No errors were found during data validation
                //adding the service received to the valid payload to be sent to HDR
                validReceivedList.add(procurementSupplyPlanRequest);
            } else {
                //Adding the validation results to the Error message object
                errorMessage.setResultsDetails(resultDetailsList);
                errorMessages.add(errorMessage);
            }
        }
        return validReceivedList;
    }

    /**
     * Validate ProcurementSupplyPlanRequest Required Fields
     *
     * @param procurementSupplyPlanRequest to be validated
     * @return array list of validation results details for failed validations
     */
    public List<ResultDetail> validateRequiredFields(ProcurementSupplyPlanRequest procurementSupplyPlanRequest) {
        List<ResultDetail> resultDetailsList = new ArrayList<>();

        if (StringUtils.isBlank(procurementSupplyPlanRequest.getUuid()))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("GENERIC_ERR"), "uuid"), null));

        if (StringUtils.isBlank(String.valueOf(procurementSupplyPlanRequest.getContractDate())))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("GENERIC_ERR"), "contractDate"), null));

        if (StringUtils.isBlank(String.valueOf(procurementSupplyPlanRequest.getCurrency())))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("GENERIC_ERR"), "currency"), null));

        if (StringUtils.isBlank(String.valueOf(procurementSupplyPlanRequest.getLotAmount())))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("GENERIC_ERR"), "lotAmount"), null));

        if (StringUtils.isBlank(procurementSupplyPlanRequest.getMeasureUnit()))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("GENERIC_ERR"), "measureUnit"), null));

        if (StringUtils.isBlank(String.valueOf(procurementSupplyPlanRequest.getOrderDate())))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("GENERIC_ERR"), "orderDate"), null));

        if (StringUtils.isBlank(procurementSupplyPlanRequest.getOrderId()))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("GENERIC_ERR"), "orderId"), null));

        if (StringUtils.isBlank(String.valueOf(procurementSupplyPlanRequest.getOrderQuantity())))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("GENERIC_ERR"), "orderQuantity"), null));

        if (StringUtils.isBlank(procurementSupplyPlanRequest.getProductCode()))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("GENERIC_ERR"), "productCode"), null));

        if (StringUtils.isBlank(String.valueOf(procurementSupplyPlanRequest.getReceivedAmount())))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("GENERIC_ERR"), "receivedAmount"), null));

        if (StringUtils.isBlank(procurementSupplyPlanRequest.getReceivedDate()))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("GENERIC_ERR"), "receivedDate"), null));

        if (StringUtils.isBlank(String.valueOf(procurementSupplyPlanRequest.getStatus())))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("GENERIC_ERR"), "status"), null));

        try {
            if (!DateValidatorUtils.isValidPastDate(procurementSupplyPlanRequest.getContractDate(), checkDateFormatStrings(procurementSupplyPlanRequest.getContractDate()))) {
                resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("ERROR_DATE_IS_NOT_VALID_PAST_DATE"), "contractDate"), null));
            } else {
                SimpleDateFormat procurementSupplyPlanDateFormat = new SimpleDateFormat(checkDateFormatStrings(procurementSupplyPlanRequest.getContractDate()));
                procurementSupplyPlanRequest.setContractDate(thscpDateFormat.format(procurementSupplyPlanDateFormat.parse(procurementSupplyPlanRequest.getContractDate())));

            }
        } catch (ParseException e) {
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("ERROR_INVALID_DATE_FORMAT"), "contractDate"), null));
        }

        return resultDetailsList;
    }

    @Override
    public void onReceive(Object msg) {
        if (msg instanceof MediatorHTTPRequest) {
            workingRequest = (MediatorHTTPRequest) msg;

            log.info("Received request: " + workingRequest.getHost() + " " + workingRequest.getMethod() + " " + workingRequest.getPath());

            //Converting the received request body to POJO List
            List<ProcurementSupplyPlanRequest> procurementSupplyPlanRequestList = new ArrayList<>();
            try {
                procurementSupplyPlanRequestList = convertMessageBodyToPojoList(((MediatorHTTPRequest) msg).getBody());
            } catch (Exception e) {
                //In-case of an exception creating an error message with the stack trace
                ErrorMessage errorMessage = new ErrorMessage(
                        workingRequest.getBody(),
                        Arrays.asList(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, e.getMessage(), tz.go.moh.him.mediator.core.utils.StringUtils.writeStackTraceToString(e)))
                );
                errorMessages.add(errorMessage);
            }

            log.info("Received payload in JSON = " + new Gson().toJson(procurementSupplyPlanRequestList));

            List<ProcurementSupplyPlanRequest> validatedObjects;
            if (procurementSupplyPlanRequestList.isEmpty()) {
                ErrorMessage errorMessage = new ErrorMessage(
                        workingRequest.getBody(),
                        Arrays.asList(
                                new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, errorMessageResource.getString("ERROR_INVALID_PAYLOAD"), null)
                        )
                );
                errorMessages.add(errorMessage);
                validatedObjects = new ArrayList<>();
            } else {
                validatedObjects = validateData(procurementSupplyPlanRequestList);
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


    protected List<ProcurementSupplyPlanRequest> convertMessageBodyToPojoList(String msg) throws JsonSyntaxException {
        List<ProcurementSupplyPlanRequest> procurementSupplyPlanRequestList = Arrays.asList(serializer.deserialize(msg, ProcurementSupplyPlanRequest[].class));
        return procurementSupplyPlanRequestList;
    }
}
