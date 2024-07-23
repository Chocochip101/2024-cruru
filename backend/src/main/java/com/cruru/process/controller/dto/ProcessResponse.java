package com.cruru.process.controller.dto;

import com.cruru.applicant.controller.dto.DashboardApplicantResponse;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record ProcessResponse(
        @JsonProperty("process_id")
        long processId,

        @JsonProperty("order_index")
        int sequence,

        String name,

        String description,

        @JsonProperty("applicants")
        List<DashboardApplicantResponse> dashboardApplicantResponses
) {

}