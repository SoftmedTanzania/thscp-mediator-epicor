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
import tz.go.moh.him.mediator.core.validator.DateValidatorUtils;
import tz.go.moh.him.thscp.mediator.epicor.domain.ProcurementSupplyPlanRequest;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class ProcurementSupplyPlanOrchestrator extends UntypedActor {

    /**
     * The logger instance.
     */
    private final LoggingAdapter log = Logging.getLogger(getContext().system(), this);
    /**
     * The mediator configuration.
     */
    protected final MediatorConfig config;

    /**
     * Represents a mediator request.
     */
    protected MediatorHTTPRequest workingRequest;

    protected JSONObject errorMessageResource;

    protected SimpleDateFormat thscpDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    /**
     * Represents a list of error messages, if any,that have been caught during payload data validation to be returned to the source system as response.
     */
    protected List<ErrorMessage> errorMessages = new ArrayList<>();

    /**
     * Handles the received message.
     *
     * @param msg The received message.
     */

    /**
     * Initializes a new instance of the {@link ProcurementSupplyPlanOrchestrator} class.
     *
     * @param config The mediator configuration.
     */
    public ProcurementSupplyPlanOrchestrator(MediatorConfig config) {
        this.config = config;
        InputStream stream = getClass().getClassLoader().getResourceAsStream("error-messages.json");
        try {
            if (stream != null) {
                errorMessageResource = new JSONObject(IOUtils.toString(stream)).getJSONObject("PROCUREMENT_SUPPLY_PLAN_ERROR_MESSAGES");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
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
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, errorMessageResource.getString("UUID_IS_BLANK"), null));

        if (StringUtils.isBlank(String.valueOf(procurementSupplyPlanRequest.getContractDate())))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, errorMessageResource.getString("CONTRACT_DATE_IS_BLANK"), null));

        if (StringUtils.isBlank(String.valueOf(procurementSupplyPlanRequest.getCurrency())))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, errorMessageResource.getString("CURRENCY_IS_BLANK"), null));

        if (StringUtils.isBlank(String.valueOf(procurementSupplyPlanRequest.getLotAmount())))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, errorMessageResource.getString("LOT_AMOUNT_IS_BLANK"), null));

        if (StringUtils.isBlank(procurementSupplyPlanRequest.getMeasureUnit()))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, errorMessageResource.getString("MEASURE_UNIT_IS_BLANK"), null));

        if (StringUtils.isBlank(String.valueOf(procurementSupplyPlanRequest.getOrderDate())))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, errorMessageResource.getString("ORDER_DATE_IS_BLANK"), null));

        if (StringUtils.isBlank(procurementSupplyPlanRequest.getOrderId()))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, errorMessageResource.getString("ORDER_ID_BLANK"), null));

        if (StringUtils.isBlank(String.valueOf(procurementSupplyPlanRequest.getOrderQuantity())))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, errorMessageResource.getString("ORDER_QUANTITY_IS_BLANK"), null));

        if (StringUtils.isBlank(procurementSupplyPlanRequest.getProductCode()))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, errorMessageResource.getString("PRODUCT_CODE_IS_BLANK"), null));

        if (StringUtils.isBlank(String.valueOf(procurementSupplyPlanRequest.getReceivedAmount())))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, errorMessageResource.getString("RECEIVED_AMOUNT_IS_BLANK"), null));

        if (StringUtils.isBlank(procurementSupplyPlanRequest.getReceivedDate()))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, errorMessageResource.getString("RECEIVED_DATE_IS_BLANK"), null));

        if (StringUtils.isBlank(String.valueOf(procurementSupplyPlanRequest.getStatus())))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, errorMessageResource.getString("STATUS_IS_BLANK"), null));

        try {
            if (!DateValidatorUtils.isValidPastDate(procurementSupplyPlanRequest.getContractDate(), checkDateFormatStrings(procurementSupplyPlanRequest.getContractDate()))) {
                resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, errorMessageResource.getString("ERROR_CONTRACT_DATE_IS_NOT_VALID_PAST_DATE"), null));
            }
            else{
                SimpleDateFormat procurementSupplyPlanDateFormat = new SimpleDateFormat(checkDateFormatStrings(procurementSupplyPlanRequest.getContractDate()));
                procurementSupplyPlanRequest.setContractDate(thscpDateFormat.format(procurementSupplyPlanDateFormat.parse(procurementSupplyPlanRequest.getContractDate())));

            }
        } catch (ParseException e) {
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, errorMessageResource.getString("ERROR_CONTRACT_DATE_INVALID_FORMAT"),null));
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

            log.info("validated object is" +new Gson().toJson(validatedObjects));

            sendDataToThscp(new Gson().toJson(validatedObjects));
        } else if (msg instanceof MediatorHTTPResponse) { //respond
            log.info("Received response from thscp");
            (workingRequest).getRequestHandler().tell(((MediatorHTTPResponse) msg).toFinishRequest(), getSelf());
        } else {
            unhandled(msg);
        }
    }

    /**
     * Handle sending of data to thscp
     *
     * @param msg to be sent
     */
    private void sendDataToThscp(String msg) {
        if (!errorMessages.isEmpty()) {
            FinishRequest finishRequest = new FinishRequest(new Gson().toJson(errorMessages), "text/json", HttpStatus.SC_BAD_REQUEST);
            (workingRequest).getRequestHandler().tell(finishRequest, getSelf());
        } else {
            Map<String, String> headers = new HashMap<>();
            headers.put("Content-Type", "application/json");

            String scheme;
            String host;
            String path;
            int portNumber;

            if (config.getDynamicConfig().isEmpty()) {
                if (config.getProperty("thscp.secure").equals("true")) {
                    scheme = "https";
                } else {
                    scheme = "http";
                }

                host = config.getProperty("thscp.host");
                portNumber = Integer.parseInt(config.getProperty("thscp.api.port"));
                path = config.getProperty("thscp.api.path");
            } else {
                JSONObject connectionProperties = new JSONObject(config.getDynamicConfig()).getJSONObject("thscpConnectionProperties");

                if (!connectionProperties.getString("thscpUsername").isEmpty() && !connectionProperties.getString("thscpPassword").isEmpty()) {
                    String auth = connectionProperties.getString("thscpUsername") + ":" + connectionProperties.getString("thscpPassword");
                    byte[] encodedAuth = Base64.encodeBase64(
                            auth.getBytes(StandardCharsets.ISO_8859_1));
                    String authHeader = "Basic " + new String(encodedAuth);
                    headers.put(HttpHeaders.AUTHORIZATION, authHeader);
                }

                host = connectionProperties.getString("thscpHost");
                portNumber = connectionProperties.getInt("thscpPort");
                path = connectionProperties.getString("thscpPath");
                scheme = connectionProperties.getString("thscpScheme");
            }

            List<Pair<String, String>> params = new ArrayList<>();

            MediatorHTTPRequest forwardToThscpRequest = new MediatorHTTPRequest(
                    (workingRequest).getRequestHandler(), getSelf(), "Sending recall data to thscp", "POST", scheme,
                    host, portNumber, path, msg, headers, params
            );

            ActorSelection httpConnector = getContext().actorSelection(config.userPathFor("http-connector"));
            httpConnector.tell(forwardToThscpRequest, getSelf());
        }
    }

    /**
     * Handles checking for the correct date string format from a varierity of formats
     *
     * @param dateString of the date
     * @return the matching date string format
     */
    public static String checkDateFormatStrings(String dateString) {
        List<String> formatStrings = Arrays.asList("yyyy-MM-dd HH:mm:ss:ms", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd","yyyyMMdd");
        for (String formatString : formatStrings) {
            try {
                new SimpleDateFormat(formatString).parse(dateString);
                return formatString;
            }
            catch (ParseException e) {
                //  e.printStackTrace();
            }
        }

        return "";
    }


    protected List<ProcurementSupplyPlanRequest> convertMessageBodyToPojoList(String msg) throws JsonSyntaxException {
        List<ProcurementSupplyPlanRequest> procurementSupplyPlanRequestList;

        Type listType = new TypeToken<List<ProcurementSupplyPlanRequest>>() {
        }.getType();
        procurementSupplyPlanRequestList = new Gson().fromJson((workingRequest).getBody(), listType);

        return procurementSupplyPlanRequestList;
    }
}
