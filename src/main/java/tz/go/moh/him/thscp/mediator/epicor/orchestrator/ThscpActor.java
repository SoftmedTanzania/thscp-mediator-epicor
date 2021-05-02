package tz.go.moh.him.thscp.mediator.epicor.orchestrator;

import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.http.HttpHeaders;
import org.json.JSONObject;
import org.openhim.mediator.engine.MediatorConfig;
import org.openhim.mediator.engine.messages.MediatorHTTPRequest;
import org.openhim.mediator.engine.messages.MediatorHTTPResponse;
import org.openhim.mediator.engine.messages.SimpleMediatorRequest;
import tz.go.moh.him.thscp.mediator.epicor.utils.RequestConstantUtils;

import java.nio.charset.StandardCharsets;
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
     * The message type.
     */
    private final String messageType;

    /**
     * Initializes a new instance of the {@link ThscpActor} class.
     *
     * @param config The mediator configuration.
     */
    public ThscpActor(MediatorConfig config, String messageType) {
        this.config = config;
        this.messageType = messageType;
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
        String username;
        String password;
        int portNumber;

        if (config.getDynamicConfig().isEmpty()) {
            if (config.getProperty("destination.scheme").equals("https")) {
                scheme = "https";
            } else {
                scheme = "http";
            }

            host = config.getProperty("destination.host");
            portNumber = Integer.parseInt(config.getProperty("destination.api.port"));

            switch (message) {
                case RequestConstantUtils.DOS_PRODUCT_REQUEST:
                    path = config.getProperty("destination.api.path.dos_product");
                    break;
                case RequestConstantUtils.EMERGENCY_SUPPLY_CHAIN_COMMODITIES_STOCK_STATUS_REQUEST:
                    path = config.getProperty("destination.api.path.emergency_supply_chain_commodities_stock_status");
                    break;
                case RequestConstantUtils.HEALTH_COMMODITIES_FUNDING_REQUEST:
                    path = config.getProperty("destination.api.path.health_commodities_funding");
                    break;
                case RequestConstantUtils.ITEM_FILL_RATE_REQUEST:
                    path = config.getProperty("destination.api.path.item_fill_rate");
                    break;
                case RequestConstantUtils.PERCENTAGE_HEALTH_FACILITIES_STAFF_REQUEST:
                    path = config.getProperty("destination.api.path.percentage_health_facilities_staff");
                    break;
                case RequestConstantUtils.PROCUREMENT_SUPPLY_PLAN_REQUEST:
                    path = config.getProperty("destination.api.path.procurement_supply_plan");
                    break;
                case RequestConstantUtils.PRODUCT_RECALL_ALERTS_REQUEST:
                    path = config.getProperty("destination.api.path.product_recall_alerts");
                    break;
                case RequestConstantUtils.PROGRAM_LIST_REQUEST:
                    path = config.getProperty("destination.api.path.program_list");
                    break;
                case RequestConstantUtils.STOCK_AVAILABILITY_REQUEST:
                    path = config.getProperty("destination.api.path.stock_availability");
                    break;
                case RequestConstantUtils.STOCK_ON_HAND_PERCENTAGE_WASTAGE_REQUEST:
                    path = config.getProperty("destination.api.path.stock_on_hand_percentage_wastage");
                    break;
                default:
                    path = null;
                    break;
            }

        } else {
            JSONObject connectionProperties = new JSONObject(config.getDynamicConfig()).getJSONObject("destinationConnectionProperties");

            host = connectionProperties.getString("destinationHost");
            portNumber = connectionProperties.getInt("destinationPort");
            scheme = connectionProperties.getString("destinationScheme");

            if (connectionProperties.has("destinationUsername") && connectionProperties.has("destinationPassword")) {
                username = connectionProperties.getString("destinationUsername");
                password = connectionProperties.getString("destinationPassword");

                // if we have a username and a password
                // we want to add the username and password as the Basic Auth header in the HTTP request
                if (username != null && !"".equals(username) && password != null && !"".equals(password)) {
                    String auth = username + ":" + password;
                    byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(StandardCharsets.ISO_8859_1));
                    String authHeader = "Basic " + new String(encodedAuth);
                    headers.put(HttpHeaders.AUTHORIZATION, authHeader);
                }
            }

            switch (message) {
                case RequestConstantUtils.DOS_PRODUCT_REQUEST:
                    path = config.getProperty("destinationPathDosProduct");
                    break;
                case RequestConstantUtils.EMERGENCY_SUPPLY_CHAIN_COMMODITIES_STOCK_STATUS_REQUEST:
                    path = config.getProperty("destinationPathEmergencySupplyChainCommoditiesStockStatus");
                    break;
                case RequestConstantUtils.HEALTH_COMMODITIES_FUNDING_REQUEST:
                    path = config.getProperty("destinationPathHealthCommoditiesFunding");
                    break;
                case RequestConstantUtils.ITEM_FILL_RATE_REQUEST:
                    path = config.getProperty("destinationPathItemFillRate");
                    break;
                case RequestConstantUtils.PERCENTAGE_HEALTH_FACILITIES_STAFF_REQUEST:
                    path = config.getProperty("destinationPathPercentageHealthFacilitiesStaff");
                    break;
                case RequestConstantUtils.PROCUREMENT_SUPPLY_PLAN_REQUEST:
                    path = config.getProperty("destinationPathProcurementSupplyPlan");
                    break;
                case RequestConstantUtils.PRODUCT_RECALL_ALERTS_REQUEST:
                    path = config.getProperty("destinationPathProductRecallAlerts");
                    break;
                case RequestConstantUtils.PROGRAM_LIST_REQUEST:
                    path = config.getProperty("destinationPathProgramList");
                    break;
                case RequestConstantUtils.STOCK_AVAILABILITY_REQUEST:
                    path = config.getProperty("destinationPathStockAvailability");
                    break;
                case RequestConstantUtils.STOCK_ON_HAND_PERCENTAGE_WASTAGE_REQUEST:
                    path = config.getProperty("destinationPathStockOnHandPercentageWastage");
                    break;
                default:
                    path = null;
                    break;
            }

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
