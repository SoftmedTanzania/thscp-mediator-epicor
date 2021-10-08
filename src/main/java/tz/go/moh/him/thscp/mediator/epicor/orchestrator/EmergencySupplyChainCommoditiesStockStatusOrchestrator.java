package tz.go.moh.him.thscp.mediator.epicor.orchestrator;

import org.codehaus.plexus.util.StringUtils;
import org.openhim.mediator.engine.MediatorConfig;
import org.openhim.mediator.engine.messages.MediatorHTTPRequest;
import tz.go.moh.him.mediator.core.domain.ResultDetail;
import tz.go.moh.him.mediator.core.validator.DateValidatorUtils;
import tz.go.moh.him.thscp.mediator.epicor.domain.EmergencySupplyChainCommoditiesStockStatusRequest;
import tz.go.moh.him.thscp.mediator.epicor.utils.RequestConstantUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class EmergencySupplyChainCommoditiesStockStatusOrchestrator extends BaseOrchestrator {

    /**
     * Initializes a new instance of the {@link EmergencySupplyChainCommoditiesStockStatusOrchestrator} class.
     *
     * @param config The mediator configuration.
     */
    public EmergencySupplyChainCommoditiesStockStatusOrchestrator(MediatorConfig config) {
        super(config);
    }

    /**
     * Validate HealthCommodityFundingRequest Required Fields
     *
     * @param emergencySupplyChainCommoditiesStockStatusRequest to be validated
     * @return array list of validation results details for failed validations
     */
    public List<ResultDetail> validateMessage(List<EmergencySupplyChainCommoditiesStockStatusRequest> emergencySupplyChainCommoditiesStockStatusRequest) {
        ArrayList<ResultDetail> resultDetailsList = new ArrayList<>();

        for (EmergencySupplyChainCommoditiesStockStatusRequest request : emergencySupplyChainCommoditiesStockStatusRequest) {
            if (StringUtils.isBlank(request.getUuid()))
                resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("GENERIC_ERR"), "uuid"), null));

            if (StringUtils.isBlank(String.valueOf(request.getAvailableQuantity())))
                resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("GENERIC_ERR"), "availableQuantity"), null));

            if (StringUtils.isBlank(String.valueOf(request.getFacilityId())))
                resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("GENERIC_ERR"), "facilityId"), null));

            if (StringUtils.isBlank(request.getPeriod()))
                resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("GENERIC_ERR"), "period"), null));

            if (StringUtils.isBlank(request.getProductCode()))
                resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("GENERIC_ERR"), "productCode"), null));

            if (StringUtils.isBlank(String.valueOf(request.getProgramCode())))
                resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("GENERIC_ERR"), "programCode"), null));

            if (StringUtils.isBlank(String.valueOf(request.getStockOfMonth())))
                resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("GENERIC_ERR"), "stockOfMonth"), null));

            if (StringUtils.isBlank(String.valueOf(request.getStockQuantity())))
                resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("GENERIC_ERR"), "stockQuantity"), null));

            try {
                if (!DateValidatorUtils.isValidPastDate(request.getPeriod(), checkDateFormatStrings(request.getPeriod()))) {
                    resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("ERROR_DATE_IS_NOT_VALID_PAST_DATE"), "period"), null));
                } else {
                    SimpleDateFormat emergencySupplyChainDateFormat = new SimpleDateFormat(checkDateFormatStrings(request.getPeriod()));
                    request.setPeriod(thscpDateFormat.format(emergencySupplyChainDateFormat.parse(request.getPeriod())));

                }
            } catch (ParseException e) {
                resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("ERROR_INVALID_DATE_FORMAT"), "period"), null));
            }
        }

        return resultDetailsList;
    }

    @Override
    protected void onReceiveRequestInternal(MediatorHTTPRequest request) throws Exception {
        List<EmergencySupplyChainCommoditiesStockStatusRequest> emergencySupplyChainCommoditiesStockStatusRequest = Arrays.asList(serializer.deserialize(request.getBody(), EmergencySupplyChainCommoditiesStockStatusRequest[].class));

        sendDataToThscp(emergencySupplyChainCommoditiesStockStatusRequest, validateMessage(emergencySupplyChainCommoditiesStockStatusRequest), RequestConstantUtils.EMERGENCY_SUPPLY_CHAIN_COMMODITIES_STOCK_STATUS_REQUEST);

    }


}
