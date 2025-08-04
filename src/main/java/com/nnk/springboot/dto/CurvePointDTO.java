package com.nnk.springboot.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CurvePointDTO {

    private Integer id;

    @NotNull(message = "Curve ID is mandatory")
    @Min(value = 1, message = "Curve ID must be positive")
    private Integer curveId;

    @NotNull(message = "Term is mandatory")
    @DecimalMin(value = "0.0", inclusive = true, message = "Term must be positive or zero")
    @Digits(integer = 10, fraction = 4, message = "Term must be a valid number with max 4 decimal places")
    private Double term;

    @NotNull(message = "Value is mandatory")
    @Digits(integer = 10, fraction = 4, message = "Value must be a valid number with max 4 decimal places")
    private Double value;

    // Constructor pour les cas simples
    public CurvePointDTO(Integer curveId, Double term, Double value) {
        this.curveId = curveId;
        this.term = term;
        this.value = value;
    }
}
