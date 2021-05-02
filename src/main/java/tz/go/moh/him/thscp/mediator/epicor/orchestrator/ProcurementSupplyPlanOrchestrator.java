package tz.go.moh.him.thscp.mediator.epicor.orchestrator;

import org.codehaus.plexus.util.StringUtils;
import org.openhim.mediator.engine.MediatorConfig;
import org.openhim.mediator.engine.messages.MediatorHTTPRequest;
import tz.go.moh.him.mediator.core.domain.ResultDetail;
import tz.go.moh.him.mediator.core.validator.DateValidatorUtils;
import tz.go.moh.him.thscp.mediator.epicor.domain.ProcurementSupplyPlanRequest;
import tz.go.moh.him.thscp.mediator.epicor.utils.RequestConstantUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProcurementSupplyPlanOrchestrator extends BaseOrchestrator {

    /**
     * Initializes a new instance of the {@link ProcurementSupplyPlanOrchestrator} class.
     *
     * @param config The mediator configuration.
     */
    public ProcurementSupplyPlanOrchestrator(MediatorConfig config) {
        super(config);
    }


    /**
     * Validate ProcurementSupplyPlanRequest Required Fields
     *
     * @param procurementSupplyPlanRequest to be validated
     * @return array list of validation results details for failed validations
     */
    public List<ResultDetail> validateMessage(List<ProcurementSupplyPlanRequest> procurementSupplyPlanRequest) {
        ArrayList<ResultDetail> resultDetailsList = new ArrayList<>();

        for (ProcurementSupplyPlanRequest request : procurementSupplyPlanRequest) {
            if (StringUtils.isBlank(request.getUuid()))
                resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("GENERIC_ERR"), "uuid"), null));

            if (StringUtils.isBlank(String.valueOf(request.getContractDate())))
                resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("GENERIC_ERR"), "contractDate"), null));

            if (StringUtils.isBlank(String.valueOf(request.getCurrency())))
                resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("GENERIC_ERR"), "currency"), null));

            if (StringUtils.isBlank(String.valueOf(request.getLotAmount())))
                resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("GENERIC_ERR"), "lotAmount"), null));

            if (StringUtils.isBlank(request.getMeasureUnit()))
                resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("GENERIC_ERR"), "measureUnit"), null));

            if (StringUtils.isBlank(String.valueOf(request.getOrderDate())))
                resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("GENERIC_ERR"), "orderDate"), null));

            if (StringUtils.isBlank(request.getOrderId()))
                resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("GENERIC_ERR"), "orderId"), null));

            if (StringUtils.isBlank(String.valueOf(request.getOrderQuantity())))
                resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("GENERIC_ERR"), "orderQuantity"), null));

            if (StringUtils.isBlank(request.getProductCode()))
                resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("GENERIC_ERR"), "productCode"), null));

            if (StringUtils.isBlank(String.valueOf(request.getReceivedAmount())))
                resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("GENERIC_ERR"), "receivedAmount"), null));

            if (StringUtils.isBlank(request.getReceivedDate()))
                resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("GENERIC_ERR"), "receivedDate"), null));

            if (StringUtils.isBlank(String.valueOf(request.getStatus())))
                resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("GENERIC_ERR"), "status"), null));

            try {
                if (!DateValidatorUtils.isValidPastDate(request.getContractDate(), checkDateFormatStrings(request.getContractDate()))) {
                    resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("ERROR_DATE_IS_NOT_VALID_PAST_DATE"), "contractDate"), null));
                } else {
                    SimpleDateFormat procurementSupplyPlanDateFormat = new SimpleDateFormat(checkDateFormatStrings(request.getContractDate()));
                    request.setContractDate(thscpDateFormat.format(procurementSupplyPlanDateFormat.parse(request.getContractDate())));

                }
            } catch (ParseException e) {
                resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("ERROR_INVALID_DATE_FORMAT"), "contractDate"), null));
            }
        }

        return resultDetailsList;
    }

    @Override
    protected void onReceiveRequestInternal(MediatorHTTPRequest request) throws Exception {
        List<ProcurementSupplyPlanRequest> procurementSupplyPlanRequest = Arrays.asList(serializer.deserialize(request.getBody(), ProcurementSupplyPlanRequest[].class));

        sendDataToThscp(procurementSupplyPlanRequest, validateMessage(procurementSupplyPlanRequest), RequestConstantUtils.PROCUREMENT_SUPPLY_PLAN_REQUEST);

    }
}
