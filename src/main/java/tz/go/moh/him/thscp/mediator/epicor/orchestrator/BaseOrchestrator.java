package tz.go.moh.him.thscp.mediator.epicor.orchestrator;

import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.google.gson.Gson;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.json.JSONObject;
import org.openhim.mediator.engine.MediatorConfig;
import org.openhim.mediator.engine.messages.FinishRequest;
import org.openhim.mediator.engine.messages.MediatorHTTPRequest;
import org.openhim.mediator.engine.messages.SimpleMediatorRequest;
import tz.go.moh.him.mediator.core.domain.ErrorMessage;
import tz.go.moh.him.mediator.core.domain.ResultDetail;
import tz.go.moh.him.mediator.core.serialization.JsonSerializer;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public abstract class BaseOrchestrator extends UntypedActor {

    /**
     * The serializer
     */
    public static final JsonSerializer serializer = new JsonSerializer();
    /**
     * The logger instance.
     */
    public final LoggingAdapter log = Logging.getLogger(getContext().system(), this);
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
     * Initializes a new instance of the {@link BaseOrchestrator} class.
     *
     * @param config The mediator configuration.
     */
    public BaseOrchestrator(MediatorConfig config) {
        this.config = config;
        InputStream stream = getClass().getClassLoader().getResourceAsStream("error-messages.json");
        try {
            if (stream != null) {
                errorMessageResource = new JSONObject(IOUtils.toString(stream));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String checkDateFormatStrings(String dateString) {
        List<String> formatStrings = Arrays.asList("yyyy-MM-dd HH:mm:ss:ms", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd", "yyyyMMdd");
        for (String formatString : formatStrings) {
            try {
                new SimpleDateFormat(formatString).parse(dateString);
                return formatString;
            } catch (ParseException e) {
                //  e.printStackTrace();
            }
        }

        return "";
    }

    /**
     * Handles the received message.
     *
     * @param message The received message.
     */
    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof MediatorHTTPRequest) {
            workingRequest = (MediatorHTTPRequest) message;
            this.onReceiveRequestInternal((MediatorHTTPRequest) message);
        } else {
            unhandled(message);
        }
    }

    /**
     * Handles the received message.
     *
     * @param request The request.
     * @throws Exception if an exception occurs.
     */
    protected abstract void onReceiveRequestInternal(MediatorHTTPRequest request) throws Exception;


    /**
     * Method that handles sending of data to the THSCP Actor
     *
     * @param objectsList   list of objects to be sent to THSCP
     * @param resultDetails Result details of the data validation
     */
    protected void sendDataToThscp(List<?> objectsList, List<ResultDetail> resultDetails, String payloadType) {
        // if there are any errors
        // we need to serialize the results and return
        if (resultDetails.stream().anyMatch(c -> c.getType() == ResultDetail.ResultsDetailsType.ERROR)) {
            FinishRequest finishRequest = new FinishRequest(serializer.serializeToString(resultDetails), "application/json", HttpStatus.SC_BAD_REQUEST);
            workingRequest.getRequestHandler().tell(finishRequest, getSelf());

        } else {
            log.info("Sending data to Thscp Actor");

            ActorRef actor = getContext().actorOf(Props.create(ThscpActor.class, config, payloadType));
            actor.tell(
                    new SimpleMediatorRequest<>(
                            workingRequest.getRequestHandler(),
                            getSelf(),
                            new Gson().toJson(objectsList)), getSelf());
        }
    }

}
