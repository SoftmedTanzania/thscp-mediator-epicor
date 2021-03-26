package tz.go.moh.him.thscp.mediator.epicor.orchestrator;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.codehaus.plexus.util.StringUtils;
import org.openhim.mediator.engine.MediatorConfig;
import org.openhim.mediator.engine.messages.MediatorHTTPRequest;
import org.openhim.mediator.engine.messages.MediatorHTTPResponse;
import tz.go.moh.him.mediator.core.domain.ErrorMessage;
import tz.go.moh.him.mediator.core.domain.ResultDetail;
import tz.go.moh.him.thscp.mediator.epicor.domain.ProgramListRequest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProgramListOrchestrator extends BaseOrchestrator {

    /**
     * Initializes a new instance of the {@link DosProductOrchestrator} class.
     *
     * @param config The mediator configuration.
     */
    public ProgramListOrchestrator(MediatorConfig config) {
        super(config);
    }

    /**
     * Handles data validations
     *
     * @param receivedList The object to be validated
     */
    protected List<ProgramListRequest> validateData(List<ProgramListRequest> receivedList) {
        List<ProgramListRequest> validReceivedList = new ArrayList<>();

        for (ProgramListRequest programListRequest : receivedList) {
            ErrorMessage errorMessage = new ErrorMessage();
            errorMessage.setSource(new Gson().toJson(programListRequest));

            List<ResultDetail> resultDetailsList = new ArrayList<>();

            if (programListRequest == null) {
                resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, errorMessageResource.getString("ERROR_INVALID_PAYLOAD"), null));
            } else {
                resultDetailsList.addAll(validateRequiredFields(programListRequest));
            }

            //TODO implement additional data validations checks
            if (resultDetailsList.size() == 0) {
                //No errors were found during data validation
                //adding the service received to the valid payload to be sent to HDR
                validReceivedList.add(programListRequest);
            } else {
                //Adding the validation results to the Error message object
                errorMessage.setResultsDetails(resultDetailsList);
                errorMessages.add(errorMessage);
            }
        }
        return validReceivedList;
    }

    /**
     * Validate programListRequest Required Fields
     *
     * @param programListRequest to be validated
     * @return array list of validation results details for failed validations
     */
    public List<ResultDetail> validateRequiredFields(ProgramListRequest programListRequest) {
        List<ResultDetail> resultDetailsList = new ArrayList<>();

        if (StringUtils.isBlank(programListRequest.getUuid()))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("GENERIC_ERR"), "uuid"), null));

        if (StringUtils.isBlank(String.valueOf(programListRequest.getDescription())))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("GENERIC_ERR"), "description"), null));

        if (StringUtils.isBlank(String.valueOf(programListRequest.getName())))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("GENERIC_ERR"), "name"), null));

        if (StringUtils.isBlank(programListRequest.getProgramCode()))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("GENERIC_ERR"), "programCode"), null));

        return resultDetailsList;
    }

    @Override
    public void onReceive(Object msg) {
        if (msg instanceof MediatorHTTPRequest) {
            workingRequest = (MediatorHTTPRequest) msg;

            log.info("Received request: " + workingRequest.getHost() + " " + workingRequest.getMethod() + " " + workingRequest.getPath());

            //Converting the received request body to POJO List
            List<ProgramListRequest> programListRequestList = new ArrayList<>();
            try {
                programListRequestList = convertMessageBodyToPojoList(((MediatorHTTPRequest) msg).getBody());
            } catch (Exception e) {
                //In-case of an exception creating an error message with the stack trace
                ErrorMessage errorMessage = new ErrorMessage(
                        workingRequest.getBody(),
                        Arrays.asList(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, e.getMessage(), tz.go.moh.him.mediator.core.utils.StringUtils.writeStackTraceToString(e)))
                );
                errorMessages.add(errorMessage);
            }

            log.info("Received payload in JSON = " + new Gson().toJson(programListRequestList));

            List<ProgramListRequest> validatedObjects;
            if (programListRequestList.isEmpty()) {
                ErrorMessage errorMessage = new ErrorMessage(
                        workingRequest.getBody(),
                        Arrays.asList(
                                new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, errorMessageResource.getString("ERROR_INVALID_PAYLOAD"), null)
                        )
                );
                errorMessages.add(errorMessage);
                validatedObjects = new ArrayList<>();
            } else {
                validatedObjects = validateData(programListRequestList);
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

    protected List<ProgramListRequest> convertMessageBodyToPojoList(String msg) throws JsonSyntaxException {
        List<ProgramListRequest> programListRequestList = Arrays.asList(serializer.deserialize(msg, ProgramListRequest[].class));
        return programListRequestList;
    }
}
