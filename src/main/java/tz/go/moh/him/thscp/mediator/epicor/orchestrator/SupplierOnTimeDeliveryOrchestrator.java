package tz.go.moh.him.thscp.mediator.epicor.orchestrator;

import org.codehaus.plexus.util.StringUtils;
import org.openhim.mediator.engine.MediatorConfig;
import org.openhim.mediator.engine.messages.MediatorHTTPRequest;
import tz.go.moh.him.mediator.core.domain.ResultDetail;
import tz.go.moh.him.mediator.core.validator.DateValidatorUtils;
import tz.go.moh.him.thscp.mediator.epicor.domain.ItemFillRateRequest;
import tz.go.moh.him.thscp.mediator.epicor.domain.SupplierOnTimeDeliveryRequest;
import tz.go.moh.him.thscp.mediator.epicor.utils.RequestConstantUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SupplierOnTimeDeliveryOrchestrator extends BaseOrchestrator {

    /**
     * Initializes a new instance of the {@link SupplierOnTimeDeliveryOrchestrator} class.
     *
     * @param config The mediator configuration.
     */
    public SupplierOnTimeDeliveryOrchestrator(MediatorConfig config) {
        super(config);
    }

    /**
     * Validate itemFillRateRequest Required Fields
     *
     * @param supplierOnTimeDeliveryRequests to be validated
     * @return array list of validation results details for failed validations
     */
    public List<ResultDetail> validateMessage(List<SupplierOnTimeDeliveryRequest> supplierOnTimeDeliveryRequests) {
        ArrayList<ResultDetail> resultDetailsList = new ArrayList<>();

        for (SupplierOnTimeDeliveryRequest request : supplierOnTimeDeliveryRequests) {
            if (StringUtils.isBlank(request.getUuid()))
                resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("GENERIC_ERR"), "uuid"), null));

            if (StringUtils.isBlank(String.valueOf(request.getDeliveredQuantity())))
                resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("GENERIC_ERR"), "deliveredQuantity"), null));

            if (StringUtils.isBlank(request.getDeliveryDate()))
                resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("GENERIC_ERR"), "deliveryDate"), null));

            if (StringUtils.isBlank(request.getSupplierName()))
                resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("GENERIC_ERR"), "supplierName"), null));

            if (StringUtils.isBlank(request.getOrderDate()))
                resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("GENERIC_ERR"), "orderDate"), null));

            if (StringUtils.isBlank(String.valueOf(request.getMsdZoneCodeOrderer())))
                resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("GENERIC_ERR"), "msdZoneCodeOrderer"), null));

            if (StringUtils.isBlank(String.valueOf(request.getDeliveryPromiseDate())))
                resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("GENERIC_ERR"), "deliveryPromiseDate"), null));

            if (StringUtils.isBlank(request.getOrderId()))
                resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("GENERIC_ERR"), "orderId"), null));

            if (StringUtils.isBlank(request.getOrderStatus()))
                resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("GENERIC_ERR"), "orderStatus"), null));

            if (StringUtils.isBlank(String.valueOf(request.getOrderType())))
                resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("GENERIC_ERR"), "orderType"), null));

            if (StringUtils.isBlank(String.valueOf(request.getOrderedQuantity())))
                resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("GENERIC_ERR"), "orderedQuantity"), null));

            if (StringUtils.isBlank(request.getProductCode()))
                resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("GENERIC_ERR"), "productCode"), null));

            if (StringUtils.isBlank(String.valueOf(request.getTargetDays())))
                resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("GENERIC_ERR"), "targetDays"), null));

            try {
                if (!DateValidatorUtils.isValidPastDate(request.getDeliveryDate(), checkDateFormatStrings(request.getDeliveryDate()))) {
                    resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("ERROR_DATE_IS_NOT_VALID_PAST_DATE"), "deliveryDate"), null));
                } else {
                    SimpleDateFormat itemFillRateDateFormat = new SimpleDateFormat(checkDateFormatStrings(request.getDeliveryDate()));
                    request.setDeliveryDate(thscpDateFormat.format(itemFillRateDateFormat.parse(request.getDeliveryDate())));

                }
            } catch (ParseException e) {
                resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("ERROR_INVALID_DATE_FORMAT"), "deliveryDate"), null));
            }

            try {
                if (!DateValidatorUtils.isValidPastDate(request.getOrderDate(), checkDateFormatStrings(request.getOrderDate()))) {
                    resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("ERROR_DATE_IS_NOT_VALID_PAST_DATE"), "orderDate"), null));
                } else {
                    SimpleDateFormat itemFillRateDateFormat = new SimpleDateFormat(checkDateFormatStrings(request.getOrderDate()));
                    request.setOrderDate(thscpDateFormat.format(itemFillRateDateFormat.parse(request.getOrderDate())));

                }
            } catch (ParseException e) {
                resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("ERROR_INVALID_DATE_FORMAT"), "orderDate"), null));
            }

        }

        return resultDetailsList;
    }

    @Override
    protected void onReceiveRequestInternal(MediatorHTTPRequest request) throws Exception {
        List<SupplierOnTimeDeliveryRequest> supplierOnTimeDeliveryRequests = Arrays.asList(serializer.deserialize(request.getBody(), SupplierOnTimeDeliveryRequest[].class));

        sendDataToThscp(supplierOnTimeDeliveryRequests, validateMessage(supplierOnTimeDeliveryRequests), RequestConstantUtils.SUPPLIER_ON_TIME_DELIVERY_REQUEST);

    }

}
