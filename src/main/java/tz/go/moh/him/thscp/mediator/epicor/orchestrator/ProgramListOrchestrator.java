package tz.go.moh.him.thscp.mediator.epicor.orchestrator;

import org.codehaus.plexus.util.StringUtils;
import org.openhim.mediator.engine.MediatorConfig;
import org.openhim.mediator.engine.messages.MediatorHTTPRequest;
import tz.go.moh.him.mediator.core.domain.ResultDetail;
import tz.go.moh.him.thscp.mediator.epicor.domain.ProgramListRequest;
import tz.go.moh.him.thscp.mediator.epicor.utils.RequestConstantUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProgramListOrchestrator extends BaseOrchestrator {

    /**
     * Initializes a new instance of the {@link ProgramListOrchestrator} class.
     *
     * @param config The mediator configuration.
     */
    public ProgramListOrchestrator(MediatorConfig config) {
        super(config);
    }


    /**
     * Validate programListRequest Required Fields
     *
     * @param programListRequest to be validated
     * @return array list of validation results details for failed validations
     */
    public List<ResultDetail> validateMessage(List<ProgramListRequest> programListRequest) {
        ArrayList<ResultDetail> resultDetailsList = new ArrayList<>();

        for (ProgramListRequest request : programListRequest) {
            if (StringUtils.isBlank(request.getUuid()))
                resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("GENERIC_ERR"), "uuid"), null));

            if (StringUtils.isBlank(String.valueOf(request.getDescription())))
                resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("GENERIC_ERR"), "description"), null));

            if (StringUtils.isBlank(String.valueOf(request.getName())))
                resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("GENERIC_ERR"), "name"), null));

            if (StringUtils.isBlank(request.getProgramCode()))
                resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(errorMessageResource.getString("GENERIC_ERR"), "programCode"), null));

        }
        return resultDetailsList;
    }

    @Override
    protected void onReceiveRequestInternal(MediatorHTTPRequest request) throws Exception {
        List<ProgramListRequest> programListRequest = Arrays.asList(serializer.deserialize(request.getBody(), ProgramListRequest[].class));

        sendDataToThscp(programListRequest, validateMessage(programListRequest), RequestConstantUtils.PROGRAM_LIST_REQUEST);

    }
}
