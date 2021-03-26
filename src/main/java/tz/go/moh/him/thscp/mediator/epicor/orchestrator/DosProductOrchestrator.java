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
import tz.go.moh.him.thscp.mediator.epicor.domain.DosProductRequest;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DosProductOrchestrator extends BaseOrchestrator {

    /**
     * Initializes a new instance of the {@link DosProductOrchestrator} class.
     *
     * @param config The mediator configuration.
     */
    public DosProductOrchestrator(MediatorConfig config) {
        super(config);
    }


    protected List<DosProductRequest> validateData(List<DosProductRequest> receivedList) {
        List<DosProductRequest> validReceivedList = new ArrayList<>();

        for (DosProductRequest dosProductRequest : receivedList) {
            ErrorMessage errorMessage = new ErrorMessage();
            errorMessage.setSource(new Gson().toJson(dosProductRequest));

            List<ResultDetail> resultDetailsList = new ArrayList<>();

            if (dosProductRequest == null) {
                resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, errorMessageResource.getString("ERROR_INVALID_PAYLOAD"), null));
            } else {
                resultDetailsList.addAll(validateRequiredFields(dosProductRequest));
            }

            //TODO implement additional data validations checks
            if (resultDetailsList.size() == 0) {
                //No errors were found during data validation
                //adding the service received to the valid payload to be sent to HDR
                validReceivedList.add(dosProductRequest);
            } else {
                //Adding the validation results to the Error message object
                errorMessage.setResultsDetails(resultDetailsList);
                errorMessages.add(errorMessage);
            }
        }
        return validReceivedList;
    }

    /**
     * Validate DosProductRequest Required Fields
     *
     * @param dosProductRequest to be validated
     * @return array list of validation results details for failed validations
     */
    public List<ResultDetail> validateRequiredFields(DosProductRequest dosProductRequest) {
        List<ResultDetail> resultDetailsList = new ArrayList<>();

        if (StringUtils.isBlank(dosProductRequest.getUuid()))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("GENERIC_ERR"), "uuid"), null));

        if (StringUtils.isBlank(String.valueOf(dosProductRequest.getCategory())))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("GENERIC_ERR"), "category"), null));

        if (StringUtils.isBlank(dosProductRequest.getMsdId()))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("GENERIC_ERR"), "msdId"), null));

        if (StringUtils.isBlank(dosProductRequest.getPeriod()))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("GENERIC_ERR"), "period"), null));

        if (StringUtils.isBlank(dosProductRequest.getProductClass()))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("GENERIC_ERR"), "productClass"), null));

        if (StringUtils.isBlank(String.valueOf(dosProductRequest.getProductCode())))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("GENERIC_ERR"), "productCode"), null));

        if (StringUtils.isBlank(String.valueOf(dosProductRequest.getQuantity())))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("GENERIC_ERR"), "quantity"), null));

        if (StringUtils.isBlank(String.valueOf(dosProductRequest.getRegion())))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("GENERIC_ERR"), "region"), null));

        try {
            if (!DateValidatorUtils.isValidPastDate(dosProductRequest.getPeriod(), checkDateFormatStrings(dosProductRequest.getPeriod()))) {
                resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("ERROR_DATE_IS_NOT_VALID_PAST_DATE"), "period"), null));
            } else {
                SimpleDateFormat DosProductDateFormat = new SimpleDateFormat(checkDateFormatStrings(dosProductRequest.getPeriod()));
                dosProductRequest.setPeriod(thscpDateFormat.format(DosProductDateFormat.parse(dosProductRequest.getPeriod())));

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
            List<DosProductRequest> DosProductRequestList = new ArrayList<>();
            try {
                DosProductRequestList = convertMessageBodyToPojoList(((MediatorHTTPRequest) msg).getBody());
            } catch (Exception e) {
                //In-case of an exception creating an error message with the stack trace
                ErrorMessage errorMessage = new ErrorMessage(
                        workingRequest.getBody(),
                        Arrays.asList(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, e.getMessage(), tz.go.moh.him.mediator.core.utils.StringUtils.writeStackTraceToString(e)))
                );
                errorMessages.add(errorMessage);
            }

            log.info("Received payload in JSON = " + new Gson().toJson(DosProductRequestList));

            List<DosProductRequest> validatedObjects;
            if (DosProductRequestList.isEmpty()) {
                ErrorMessage errorMessage = new ErrorMessage(
                        workingRequest.getBody(),
                        Arrays.asList(
                                new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, errorMessageResource.getString("ERROR_INVALID_PAYLOAD"), null)
                        )
                );
                errorMessages.add(errorMessage);
                validatedObjects = new ArrayList<>();
            } else {
                validatedObjects = validateData(DosProductRequestList);
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


    protected List<DosProductRequest> convertMessageBodyToPojoList(String msg) throws JsonSyntaxException {
        List<DosProductRequest> dosProductRequestList = Arrays.asList(serializer.deserialize(msg, DosProductRequest[].class));
        return dosProductRequestList;
    }

}
