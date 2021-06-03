package univesp.pi5.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Medidas {

    @JsonProperty
    double volume1;

    @JsonProperty
    double peso1;

    @JsonProperty
    double volume2;

    @JsonProperty
    double peso2;

}
