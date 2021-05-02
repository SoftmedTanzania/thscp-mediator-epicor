package tz.go.moh.him.thscp.mediator.epicor.orchestrator;

import org.codehaus.plexus.util.StringUtils;
import org.openhim.mediator.engine.MediatorConfig;
import org.openhim.mediator.engine.messages.MediatorHTTPRequest;
import tz.go.moh.him.mediator.core.domain.ResultDetail;
import tz.go.moh.him.mediator.core.validator.DateValidatorUtils;
import tz.go.moh.him.thscp.mediator.epicor.domain.ProductRecallAlertsRequest;
import tz.go.moh.him.thscp.mediator.epicor.utils.RequestConstantUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProductRecallAlertsOrchestrator extends BaseOrchestrator {

    /**
     * Initializes a new instance of the {@link ProductRecallAlertsOrchestrator} class.
     *
     * @param config The mediator configuration.
     */
    public ProductRecallAlertsOrchestrator(MediatorConfig config) {
        super(config);
    }


    /**
     * Validate ProductRecallAlerts Request Required Fields
     *
     * @param productRecallAlertsRequest to be validated
     * @return array list of validation results details for failed validations
     */
    public List<ResultDetail> validateMessage(List<ProductRecallAlertsRequest> productRecallAlertsRequest) {
        ArrayList<ResultDetail> resultDetailsList = new ArrayList<>();

        for (ProductRecallAlertsRequest request : productRecallAlertsRequest) {
            if (StringUtils.isBlank(request.getActionRequired()))
                resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("GENERIC_ERR"), "actionRequired"), null));

            if (StringUtils.isBlank(request.getAffectedCommunity()))
                resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("GENERIC_ERR"), "affectedCommunity"), null));

            if (StringUtils.isBlank(request.getBatchNumber()))
                resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("GENERIC_ERR"), "batchNumber"), null));

            if (StringUtils.isBlank(request.getClosureDate()))
                resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("GENERIC_ERR"), "closureDate"), null));

            if (StringUtils.isBlank(request.getDescription()))
                resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("GENERIC_ERR"), "description"), null));

            if (StringUtils.isBlank(String.valueOf(request.getDistributedQuantity())))
                resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("GENERIC_ERR"), "distributedQuantity"), null));

            if (StringUtils.isBlank(request.getIssue()))
                resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("GENERIC_ERR"), "issue"), null));

            if (StringUtils.isBlank(request.getRecallDate()))
                resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("GENERIC_ERR"), "recallDate"), null));

            if (StringUtils.isBlank(String.valueOf(request.getRecallFrequency())))
                resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("GENERIC_ERR"), "recallFrequency"), null));

            if (StringUtils.isBlank(String.valueOf(request.getRecalledQuantity())))
                resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("GENERIC_ERR"), "recallQuantity"), null));

            if (StringUtils.isBlank(String.valueOf(request.getStartDate())))
                resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("GENERIC_ERR"), "startDate"), null));

            if (StringUtils.isBlank(request.getUnit()))
                resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("GENERIC_ERR"), "unit"), null));

            try {
                if (!DateValidatorUtils.isValidPastDate(request.getRecallDate(), checkDateFormatStrings(request.getRecallDate()))) {
                    resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("ERROR_DATE_IS_NOT_VALID_PAST_DATE"), "recallDate"), null));
                } else {
                    SimpleDateFormat ProductRecallAlertsDateFormat = new SimpleDateFormat(checkDateFormatStrings(request.getRecallDate()));
                    request.setRecallDate(thscpDateFormat.format(ProductRecallAlertsDateFormat.parse(request.getRecallDate())));

                }
            } catch (ParseException e) {
                resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("ERROR_INVALID_DATE_FORMAT"), "recallDate"), null));
            }

            try {
                if (!DateValidatorUtils.isValidPastDate(request.getStartDate(), checkDateFormatStrings(request.getStartDate()))) {
                    resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("ERROR_DATE_IS_NOT_VALID_PAST_DATE"), "startDate"), null));
                } else {
                    SimpleDateFormat ProductRecallAlertsDateFormat = new SimpleDateFormat(checkDateFormatStrings(request.getStartDate()));
                    request.setStartDate(thscpDateFormat.format(ProductRecallAlertsDateFormat.parse(request.getStartDate())));
                }
            } catch (ParseException e) {
                resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("ERROR_INVALID_DATE_FORMAT"), "startDate"), null));
            }
        }

        return resultDetailsList;
    }

    @Override
    protected void onReceiveRequestInternal(MediatorHTTPRequest request) throws Exception {
        List<ProductRecallAlertsRequest> productRecallAlertsRequest = Arrays.asList(serializer.deserialize(request.getBody(), ProductRecallAlertsRequest[].class));

        sendDataToThscp(productRecallAlertsRequest, validateMessage(productRecallAlertsRequest), RequestConstantUtils.PRODUCT_RECALL_ALERTS_REQUEST);

    }
}
