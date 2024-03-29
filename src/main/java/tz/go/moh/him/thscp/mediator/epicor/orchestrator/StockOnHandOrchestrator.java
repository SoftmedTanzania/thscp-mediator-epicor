package tz.go.moh.him.thscp.mediator.epicor.orchestrator;

import org.codehaus.plexus.util.StringUtils;
import org.openhim.mediator.engine.MediatorConfig;
import org.openhim.mediator.engine.messages.MediatorHTTPRequest;
import tz.go.moh.him.mediator.core.domain.ResultDetail;
import tz.go.moh.him.mediator.core.validator.DateValidatorUtils;
import tz.go.moh.him.thscp.mediator.epicor.domain.StockOnHandRequest;
import tz.go.moh.him.thscp.mediator.epicor.utils.RequestConstantUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StockOnHandOrchestrator extends BaseOrchestrator {


    /**
     * Initializes a new instance of the {@link StockOnHandOrchestrator} class.
     *
     * @param config The mediator configuration.
     */
    public StockOnHandOrchestrator(MediatorConfig config) {
        super(config);
    }


    /**
     * Validate stockOnHandPercentageWastageRequest Required Fields
     *
     * @param stockOnHandPercentageWastageRequest to be validated
     * @return array list of validation results details for failed validations
     */
    public List<ResultDetail> validateMessage(List<StockOnHandRequest> stockOnHandPercentageWastageRequest) {
        List<ResultDetail> resultDetailsList = new ArrayList<>();

        for (StockOnHandRequest request : stockOnHandPercentageWastageRequest) {
            if (StringUtils.isBlank(request.getUuid()))
                resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("GENERIC_ERR"), "uuid"), null));

            if (StringUtils.isBlank(String.valueOf(request.getConsumedQuantity())))
                resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("GENERIC_ERR"), "consumedQuantity"), null));

            if (StringUtils.isBlank(request.getMsdZoneCode()))
                resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("GENERIC_ERR"), "msdZoneCode"), null));

            if (StringUtils.isBlank(String.valueOf(request.getMonthsOfStock())))
                resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("GENERIC_ERR"), "monthsOfStock"), null));

            if (StringUtils.isBlank(String.valueOf(request.getPeriod())))
                resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("GENERIC_ERR"), "period"), null));

            if (StringUtils.isBlank(String.valueOf(request.getProductCode())))
                resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("GENERIC_ERR"), "productCode"), null));

            if (StringUtils.isBlank(String.valueOf(request.getQuantity())))
                resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("GENERIC_ERR"), "quantity"), null));

            try {
                if (!DateValidatorUtils.isValidPastDate(request.getPeriod(), checkDateFormatStrings(request.getPeriod()))) {
                    resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("ERROR_DATE_IS_NOT_VALID_PAST_DATE"), "period"), null));
                } else {
                    SimpleDateFormat stockOnHandPercentageWastageDateFormat = new SimpleDateFormat(checkDateFormatStrings(request.getPeriod()));
                    request.setPeriod(thscpDateFormat.format(stockOnHandPercentageWastageDateFormat.parse(request.getPeriod())));

                }
            } catch (ParseException e) {
                resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("ERROR_INVALID_DATE_FORMAT"), "period"), null));
            }
        }

        return resultDetailsList;
    }

    @Override
    protected void onReceiveRequestInternal(MediatorHTTPRequest request) throws Exception {
        List<StockOnHandRequest> stockOnHandPercentageWastageRequest = Arrays.asList(serializer.deserialize(request.getBody(), StockOnHandRequest[].class));

        sendDataToThscp(stockOnHandPercentageWastageRequest, validateMessage(stockOnHandPercentageWastageRequest), RequestConstantUtils.STOCK_ON_HAND_REQUEST);

    }

}
