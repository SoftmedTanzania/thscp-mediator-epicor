package tz.go.moh.him.thscp.mediator.epicor;

import akka.actor.ActorSystem;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.openhim.mediator.engine.*;
import tz.go.moh.him.thscp.mediator.epicor.orchestrator.HealthCommoditiesFundingOrchestrator;
import tz.go.moh.him.thscp.mediator.epicor.orchestrator.ProcurementSupplyPlanOrchestrator;
import tz.go.moh.him.thscp.mediator.epicor.orchestrator.ProductRecallAlertsOrchestrator;
import tz.go.moh.him.thscp.mediator.epicor.orchestrator.EmergencySupplyChainCommoditiesStockStatusOrchestrator;
import tz.go.moh.him.thscp.mediator.epicor.orchestrator.ItemFillRateOrchestrator;
import tz.go.moh.him.thscp.mediator.epicor.orchestrator.PercentageHealthFacilitiesStaffOrchestrator;
import tz.go.moh.him.thscp.mediator.epicor.orchestrator.DosProductOrchestrator;
import tz.go.moh.him.thscp.mediator.epicor.orchestrator.StockOnHandPercentageWastageOrchestrator;
import tz.go.moh.him.thscp.mediator.epicor.orchestrator.StockAvailabilityOrchestrator;
import tz.go.moh.him.thscp.mediator.epicor.orchestrator.ProgramListOrchestrator;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class MediatorMain {

    private static RoutingTable buildRoutingTable() throws RoutingTable.RouteAlreadyMappedException {
        RoutingTable routingTable = new RoutingTable();

        //TODO Configure routes here
        //...
        routingTable.addRoute("/thscp-health-commodities-funding", HealthCommoditiesFundingOrchestrator.class);
        routingTable.addRoute("/thscp-procurement-supply-plan", ProcurementSupplyPlanOrchestrator.class);
        routingTable.addRoute("/thscp-product-recall-alerts", ProductRecallAlertsOrchestrator.class);
        routingTable.addRoute("/thscp-emergency-supply-chain-commodities-stock-status", EmergencySupplyChainCommoditiesStockStatusOrchestrator.class);
        routingTable.addRoute("/thscp-item-fill-rate", ItemFillRateOrchestrator.class);
        routingTable.addRoute("/thscp-percentage-health-facilities-staff", PercentageHealthFacilitiesStaffOrchestrator.class);
        routingTable.addRoute("/thscp-dos-product", DosProductOrchestrator.class);
        routingTable.addRoute("/thscp-stock-on-hand-percentage-wastage", StockOnHandPercentageWastageOrchestrator.class);
        routingTable.addRoute("/thscp-stock-availability", StockAvailabilityOrchestrator.class);
        routingTable.addRoute("/thscp-program-list", ProgramListOrchestrator.class);

        return routingTable;
    }

    private static StartupActorsConfig buildStartupActorsConfig() {
        StartupActorsConfig startupActors = new StartupActorsConfig();

        //TODO Add own startup actors here
        //...

        return startupActors;
    }

    private static MediatorConfig loadConfig(String configPath) throws IOException, RoutingTable.RouteAlreadyMappedException {
        MediatorConfig config = new MediatorConfig();

        if (configPath != null) {
            Properties props = new Properties();
            File conf = new File(configPath);
            InputStream in = FileUtils.openInputStream(conf);
            props.load(in);
            IOUtils.closeQuietly(in);

            config.setProperties(props);
        } else {
            config.setProperties("mediator.properties");
        }

        config.setName(config.getProperty("mediator.name"));
        config.setServerHost(config.getProperty("mediator.host"));
        config.setServerPort(Integer.parseInt(config.getProperty("mediator.port")));
        config.setRootTimeout(Integer.parseInt(config.getProperty("mediator.timeout")));

        config.setCoreHost(config.getProperty("core.host"));
        config.setCoreAPIUsername(config.getProperty("core.api.user"));
        config.setCoreAPIPassword(config.getProperty("core.api.password"));
        if (config.getProperty("core.api.port") != null) {
            config.setCoreAPIPort(Integer.parseInt(config.getProperty("core.api.port")));
        }

        config.setRoutingTable(buildRoutingTable());
        config.setStartupActors(buildStartupActorsConfig());

        InputStream regInfo = MediatorMain.class.getClassLoader().getResourceAsStream("mediator-registration-info.json");
        RegistrationConfig regConfig = new RegistrationConfig(regInfo);
        config.setRegistrationConfig(regConfig);

        if (config.getProperty("mediator.heartbeats") != null && "true".equalsIgnoreCase(config.getProperty("mediator.heartbeats"))) {
            config.setHeartbeatsEnabled(true);
        }

        return config;
    }

    public static void main(String... args) throws Exception {
        //setup actor system
        final ActorSystem system = ActorSystem.create("mediator");
        //setup logger for main
        final LoggingAdapter log = Logging.getLogger(system, "main");

        //setup actors
        log.info("Initializing mediator actors...");

        String configPath = null;
        if (args.length == 2 && args[0].equals("--conf")) {
            configPath = args[1];
            log.info("Loading mediator configuration from '" + configPath + "'...");
        } else {
            log.info("No configuration specified. Using default properties...");
        }

        MediatorConfig config = loadConfig(configPath);

        //TODO this should be removed in production environments it is unsafe
        config.setSSLContext(new MediatorConfig.SSLContext(true));

        final MediatorServer server = new MediatorServer(system, config);

        //setup shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                log.info("Shutting down mediator");
                server.stop();
                system.shutdown();
            }
        });

        log.info("Starting mediator server...");
        server.start();

        log.info(String.format("%s listening on %s:%s", config.getName(), config.getServerHost(), config.getServerPort()));
        Thread.currentThread().join();
    }
}
