package tz.go.moh.him.thscp.mediator.epicor.orchestrator;

import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import org.apache.commons.lang3.tuple.Pair;
import org.json.JSONObject;
import org.openhim.mediator.engine.MediatorConfig;
import org.openhim.mediator.engine.messages.MediatorHTTPRequest;
import org.openhim.mediator.engine.messages.MediatorHTTPResponse;
import org.openhim.mediator.engine.messages.SimpleMediatorRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ThscpActor extends UntypedActor {
    /**
     * The mediator configuration.
     */
    private final MediatorConfig config;

    /**
     * The logger instance.
     */
    private final LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    /**
     * The request handler that handles requests and responses.
     */
    private ActorRef requestHandler;


    /**
     * Initializes a new instance of the {@link ThscpActor} class.
     *
     * @param config The mediator configuration.
     */
    public ThscpActor(MediatorConfig config) {
        this.config = config;
    }

    /**
     * Forwards the message to the Tanzania Supply Chain Portal
     *
     * @param message to be sent to the Thscp
     */
    private void forwardToThscp(String message) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");

        String scheme;
        String host;
        String path;
        int portNumber;
        if (config.getDynamicConfig().isEmpty()) {
            if (config.getProperty("destination.scheme").equals("https")) {
                scheme = "https";
            } else {
                scheme = "http";
            }

            host = config.getProperty("destination.host");
            portNumber = Integer.parseInt(config.getProperty("destination.api.port"));
            path = config.getProperty("destination.api.path");
        } else {
            JSONObject connectionProperties = new JSONObject(config.getDynamicConfig()).getJSONObject("destinationConnectionProperties");

            host = connectionProperties.getString("destinationHost");
            portNumber = connectionProperties.getInt("destinationPort");
            path = connectionProperties.getString("destinationPath");
            scheme = connectionProperties.getString("destinationScheme");
        }

        List<Pair<String, String>> params = new ArrayList<>();

        host = scheme + "://" + host + ":" + portNumber + path;

        MediatorHTTPRequest forwardToThscpRequest = new MediatorHTTPRequest(
                requestHandler, getSelf(), "Sending Data to the THSCP Server", "POST",
                host, message, headers, params
        );

        ActorSelection httpConnector = getContext().actorSelection(config.userPathFor("http-connector"));
        httpConnector.tell(forwardToThscpRequest, getSelf());
    }

    /**
     * Handles the received message.
     *
     * @param msg The received message.
     */
    @Override
    public void onReceive(Object msg) throws Exception {
        if (SimpleMediatorRequest.isInstanceOf(String.class, msg)) { //process message
            log.info("Sending data Thscp ...");
            requestHandler = ((SimpleMediatorRequest) msg).getRequestHandler();
            forwardToThscp(((SimpleMediatorRequest) msg).getRequestObject().toString());

        } else if (msg instanceof MediatorHTTPResponse) { //respond
            log.info("Received response from THSCP");
            requestHandler.tell(((MediatorHTTPResponse) msg).toFinishRequest(), getSelf());
        } else {
            unhandled(msg);
        }
    }
}
