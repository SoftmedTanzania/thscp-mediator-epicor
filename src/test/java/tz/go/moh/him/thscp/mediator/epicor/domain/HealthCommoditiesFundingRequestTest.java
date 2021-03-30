package tz.go.moh.him.thscp.mediator.epicor.domain;
import com.google.gson.Gson;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class HealthCommoditiesFundingRequestTest {

    @Test
    public void testThscpAck() throws Exception {
        String jsonThscpAckPayload = "{\"uuid\":\"5ff2b29416a3c934156395d3\",\"allocatedFund\":200000," +
                "\"disbursedFund\":100000,\"endDate\":\"2020-05-10\",\"facilityId\":\"12345\"," +
                "\"productCode\":\"10020035MD\", \"program\":\"COVID-19\",\"source\":\"GOT\" ," +
                "\"startDate\":\"2020-01-01\"}";
        HealthCommoditiesFundingRequest healthCommoditiesFundingRequest = new Gson().fromJson(jsonThscpAckPayload, HealthCommoditiesFundingRequest.class);

        assertEquals("5ff2b29416a3c934156395d3", healthCommoditiesFundingRequest.getUuid());
        assertEquals(200000, healthCommoditiesFundingRequest.getAllocatedFund());
        assertEquals(100000, healthCommoditiesFundingRequest.getDisbursedFund());
        assertEquals("2020-05-10", healthCommoditiesFundingRequest.getEndDate());
        assertEquals("12345", healthCommoditiesFundingRequest.getFacilityId());
        assertEquals("10020035MD", healthCommoditiesFundingRequest.getProductCode());
        assertEquals("COVID-19", healthCommoditiesFundingRequest.getProgram());
        assertEquals("GOT", healthCommoditiesFundingRequest.getSource());
        assertEquals("2020-01-01", healthCommoditiesFundingRequest.getStartDate());
    }
}
