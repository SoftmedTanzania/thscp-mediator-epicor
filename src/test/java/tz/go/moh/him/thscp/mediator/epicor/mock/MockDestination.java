package tz.go.moh.him.thscp.mediator.epicor.mock;
import tz.go.moh.him.mediator.core.serialization.JsonSerializer;
import org.junit.Assert;
import org.openhim.mediator.engine.messages.MediatorHTTPRequest;
import org.openhim.mediator.engine.testing.MockHTTPConnector;
import tz.go.moh.him.thscp.mediator.epicor.domain.HealthCommoditiesFundingRequest;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Map;
import java.util.List;
import java.util.Arrays;
import org.apache.commons.io.IOUtils;
import tz.go.moh.him.thscp.mediator.epicor.orchestrator.HealthCommoditiesFundingOrchestratorTest;

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
     * Handles the message.
     *
     * @param msg The message.
     */
    @Override
    public void executeOnReceive(MediatorHTTPRequest msg){
        System.out.println("Received body : " + msg.getBody());

        InputStream stream = HealthCommoditiesFundingOrchestratorTest.class.getClassLoader().getResourceAsStream("request.json");

        Assert.assertNotNull(stream);

        JsonSerializer serializer = new JsonSerializer();

        List<HealthCommoditiesFundingRequest> expected;

        try {
            expected = Arrays.asList(serializer.deserialize(IOUtils.toByteArray(stream), HealthCommoditiesFundingRequest[].class));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        List<HealthCommoditiesFundingRequest> actual = Arrays.asList(serializer.deserialize(msg.getBody(), HealthCommoditiesFundingRequest[].class));

        Assert.assertNotNull(actual);
        Assert.assertNotNull(expected);

        Assert.assertEquals(1, actual.size());
        Assert.assertEquals(expected.size(), actual.size());

        Assert.assertEquals(expected.get(0).getUuid(), actual.get(0).getUuid());
        Assert.assertEquals(expected.get(0).getAllocatedFund(), actual.get(0).getAllocatedFund());
        Assert.assertEquals(expected.get(0).getDisbursedFund(), actual.get(0).getDisbursedFund());
        Assert.assertEquals(expected.get(0).getEndDate(), actual.get(0).getEndDate());
        Assert.assertEquals(expected.get(0).getFacilityId(), actual.get(0).getFacilityId());
        Assert.assertEquals(expected.get(0).getProductCode(), actual.get(0).getProductCode());
        Assert.assertEquals(expected.get(0).getProgram(), actual.get(0).getProgram());
        Assert.assertEquals(expected.get(0).getSource(), actual.get(0).getSource());
        Assert.assertEquals(expected.get(0).getStartDate(), actual.get(0).getStartDate());



    }
}