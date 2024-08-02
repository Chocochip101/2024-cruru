package com.cruru.applyform.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record QuestionResponse(
        long id,

        String type,

        String content,

        String description,

        @JsonProperty("order_index")
        int orderIndex,

        @JsonProperty("choices")
        List<ChoiceResponse> choiceResponses
) {

}