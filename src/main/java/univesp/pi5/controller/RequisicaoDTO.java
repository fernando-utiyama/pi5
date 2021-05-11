package univesp.pi5.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class RequisicaoDTO {

    @JsonProperty
    Long id;

    @JsonProperty
    String command;

    @JsonProperty
    Float peso;

    @JsonProperty
    String status;

}
