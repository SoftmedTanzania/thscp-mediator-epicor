package tz.go.moh.him.thscp.mediator.epicor.mock;
import tz.go.moh.him.mediator.core.serialization.JsonSerializer;
import org.junit.Assert;
import org.openhim.mediator.engine.messages.MediatorHTTPRequest;
import org.openhim.mediator.engine.testing.MockHTTPConnector;
import tz.go.moh.him.thscp.mediator.epicor.domain.*;

import java.util.Collections;
import java.util.Map;
import java.util.List;
import java.util.Arrays;


/**
 * Represents a mock destination.
 */
public class MockDestination extends MockHTTPConnector {

    /**
     * Gets the response.
     *
     * @return Returns the response.
     */
    @Override
    public String getResponse() {
        return null;
    }

    /**
     * Gets the status code.
     *
     * @return Returns the status code.
     */
    @Override
    public Integer getStatus() {
        return 200;
    }

    /**
     * Gets the HTTP headers.
     *
     * @return Returns the HTTP headers.
     */
    @Override
    public Map<String, String> getHeaders() {
        return Collections.emptyMap();
    }

    /**
     * The expected message type
     */
    private final String expectedMessageType;

    public MockDestination(String expectedMessageType) {
        this.expectedMessageType = expectedMessageType;
    }

    JsonSerializer serializer = new JsonSerializer();
    /**
     * Handles the message.
     *
     * @param msg The message.
     */
    @Override
    public void executeOnReceive(MediatorHTTPRequest msg){
        System.out.println("Received body : " + msg.getBody());

        switch(expectedMessageType) {
            case "HealthCommoditiesFunding":
                List<HealthCommoditiesFundingRequest> healthCommoditiesFunding = Arrays.asList(serializer.deserialize(msg.getBody(), HealthCommoditiesFundingRequest[].class));
                Assert.assertNotNull(healthCommoditiesFunding);
                Assert.assertEquals(1, healthCommoditiesFunding.size());
                Assert.assertEquals("3d378375-6a0c-4974-b737-4160c293774d", healthCommoditiesFunding.get(0).getUuid());
                Assert.assertEquals(200000, healthCommoditiesFunding.get(0).getAllocatedFund());
                Assert.assertEquals(100000, healthCommoditiesFunding.get(0).getDisbursedFund());
                Assert.assertEquals("2020-12-03", healthCommoditiesFunding.get(0).getEndDate());
                Assert.assertEquals("123456", healthCommoditiesFunding.get(0).getFacilityId());
                Assert.assertEquals("10020035MD", healthCommoditiesFunding.get(0).getProductCode());
                Assert.assertEquals("COVID-19", healthCommoditiesFunding.get(0).getProgram());
                Assert.assertEquals("GOT", healthCommoditiesFunding.get(0).getSource());
                Assert.assertEquals("2020-12-03", healthCommoditiesFunding.get(0).getStartDate());
                break;

            case "DosProduct":
                List<DosProductRequest> DosProduct = Arrays.asList(serializer.deserialize(msg.getBody(), DosProductRequest[].class));
                Assert.assertNotNull(DosProduct);
                Assert.assertEquals(1, DosProduct.size());
                Assert.assertEquals("453b906b-5dc4-4ba2-a37b-527f2987b0d8", DosProduct.get(0).getUuid());
                Assert.assertEquals("catagory10", DosProduct.get(0).getCategory());
                Assert.assertEquals("108431-8", DosProduct.get(0).getMsdId());
                Assert.assertEquals("2020-12-07", DosProduct.get(0).getPeriod());
                Assert.assertEquals("first class", DosProduct.get(0).getProductClass());
                Assert.assertEquals("PR-10", DosProduct.get(0).getProductCode());
                Assert.assertEquals(100, DosProduct.get(0).getQuantity());
                Assert.assertEquals("Dodoma", DosProduct.get(0).getRegion());
                break;


            case "ProductRecallAlerts":
                List<ProductRecallAlertsRequest> ProductRecallAlerts = Arrays.asList(serializer.deserialize(msg.getBody(), ProductRecallAlertsRequest[].class));
                Assert.assertNotNull(ProductRecallAlerts);
                Assert.assertEquals(1, ProductRecallAlerts.size());
                Assert.assertEquals("action1", ProductRecallAlerts.get(0).getActionRequired());
                Assert.assertEquals("rai" ,ProductRecallAlerts.get(0).getAffectedCommunity());
                Assert.assertEquals("02", ProductRecallAlerts.get(0).getBatchNumber());
                Assert.assertEquals("2020-11-27", ProductRecallAlerts.get(0).getClosureDate());
                Assert.assertEquals("this is action", ProductRecallAlerts.get(0).getDescription());
                Assert.assertEquals(10, ProductRecallAlerts.get(0).getDistributedQuantity());
                Assert.assertEquals("issuedone", ProductRecallAlerts.get(0).getIssue());
                Assert.assertEquals("2020-11-27", ProductRecallAlerts.get(0).getRecallDate());
                Assert.assertEquals(10, ProductRecallAlerts.get(0).getRecallFrequency());
                Assert.assertEquals(200, ProductRecallAlerts.get(0).getRecalledQuantity());
                Assert.assertEquals(2000, ProductRecallAlerts.get(0).getReceivedQuantity());
                Assert.assertEquals("2020-11-27", ProductRecallAlerts.get(0).getStartDate());
                Assert.assertEquals("unit", ProductRecallAlerts.get(0).getUnit());
                break;

            case "ProgramList":
                List<ProgramListRequest> programList = Arrays.asList(serializer.deserialize(msg.getBody(), ProgramListRequest[].class));
                Assert.assertNotNull(programList);
                Assert.assertEquals(1, programList.size());
                Assert.assertEquals("453b906b-5dc4-4ba2-a37b-527f2987b0d8", programList.get(0).getUuid());
                Assert.assertEquals("COVID 19", programList.get(0).getDescription());
                Assert.assertEquals("COVID 19", programList.get(0).getName());
                Assert.assertEquals("PG", programList.get(0).getProgramCode());
                break;

            case "StockAvailability":
                List<StockAvailabilityRequest> stockAvailability = Arrays.asList(serializer.deserialize(msg.getBody(), StockAvailabilityRequest[].class));
                Assert.assertNotNull(stockAvailability);
                Assert.assertEquals(1, stockAvailability.size());
                Assert.assertEquals("61ee3f67-992c-432b-8536-2b89aa3165a8", stockAvailability.get(0).getUuid());
                Assert.assertEquals("Kigoma", stockAvailability.get(0).getDistrict());
                Assert.assertEquals(5, stockAvailability.get(0).getExpected());
                Assert.assertEquals("2020-11-13", stockAvailability.get(0).getPeriod());
                Assert.assertEquals("COVID", stockAvailability.get(0).getProgram());
                break;

            case "StockOnHandPercentageWastage":
                List<StockOnHandPercentageWastageRequest> stockOnHandPercentageWastage= Arrays.asList(serializer.deserialize(msg.getBody(), StockOnHandPercentageWastageRequest[].class));
                Assert.assertNotNull(stockOnHandPercentageWastage);
                Assert.assertEquals(1, stockOnHandPercentageWastage.size());
                Assert.assertEquals("5821daab-b583-4abf-a8b0-f0a6c414d7a5", stockOnHandPercentageWastage.get(0).getUuid());
                Assert.assertEquals(10, stockOnHandPercentageWastage.get(0).getConsumedQuantity());
                Assert.assertEquals("106091-2", stockOnHandPercentageWastage.get(0).getFacilityId());
                Assert.assertEquals(0, stockOnHandPercentageWastage.get(0).getFacilityLevel());
                Assert.assertEquals("PR-01", stockOnHandPercentageWastage.get(0).getProductCode());
                Assert.assertEquals(10, stockOnHandPercentageWastage.get(0).getMonthsOfStock());
                Assert.assertEquals("PC-01", stockOnHandPercentageWastage.get(0).getProgramCode());
                Assert.assertEquals(100, stockOnHandPercentageWastage.get(0).getQuantity());
                Assert.assertEquals("001", stockOnHandPercentageWastage.get(0).getStockId());
                Assert.assertEquals(30, stockOnHandPercentageWastage.get(0).getDamagedPercentage());
                Assert.assertEquals(30, stockOnHandPercentageWastage.get(0).getExpiredPercentage());
                Assert.assertEquals("40", stockOnHandPercentageWastage.get(0).getLostPercentage());
                break;
            default:
                break;

        }



    }
}