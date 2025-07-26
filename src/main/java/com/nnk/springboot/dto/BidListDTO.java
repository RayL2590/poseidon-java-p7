package com.nnk.springboot.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BidListDTO {

    private Integer bidListId;

    @NotBlank(message = "Account is mandatory")
    @Size(max = 30, message = "Account must be less than 30 characters")
    private String account;

    @NotBlank(message = "Type is mandatory")
    @Size(max = 30, message = "Type must be less than 30 characters")
    private String type;

    @DecimalMin(value = "0.0", inclusive = true, message = "Bid quantity must be positive or zero")
    @Digits(integer = 10, fraction = 2, message = "Bid quantity must be a valid number with max 2 decimal places")
    private Double bidQuantity;

    @DecimalMin(value = "0.0", inclusive = true, message = "Ask quantity must be positive or zero")
    @Digits(integer = 10, fraction = 2, message = "Ask quantity must be a valid number with max 2 decimal places")
    private Double askQuantity;

    @DecimalMin(value = "0.0", inclusive = true, message = "Bid must be positive or zero")
    @Digits(integer = 10, fraction = 2, message = "Bid must be a valid number with max 2 decimal places")
    private Double bid;

    @DecimalMin(value = "0.0", inclusive = true, message = "Ask must be positive or zero")
    @Digits(integer = 10, fraction = 2, message = "Ask must be a valid number with max 2 decimal places")
    private Double ask;

    @Size(max = 125, message = "Benchmark must be less than 125 characters")
    private String benchmark;

    @Size(max = 125, message = "Commentary must be less than 125 characters")
    private String commentary;

    @Size(max = 125, message = "Security must be less than 125 characters")
    private String security;

    @Size(max = 10, message = "Status must be less than 10 characters")
    private String status;

    @Size(max = 125, message = "Trader must be less than 125 characters")
    private String trader;

    @Size(max = 125, message = "Book must be less than 125 characters")
    private String book;

    @Size(max = 125, message = "Creation name must be less than 125 characters")
    private String creationName;

    @Size(max = 125, message = "Revision name must be less than 125 characters")
    private String revisionName;

    @Size(max = 125, message = "Deal name must be less than 125 characters")
    private String dealName;

    @Size(max = 125, message = "Deal type must be less than 125 characters")
    private String dealType;

    @Size(max = 125, message = "Source list ID must be less than 125 characters")
    private String sourceListId;

    @Size(max = 125, message = "Side must be less than 125 characters")
    private String side;

    // Constructor pour les cas simples
    public BidListDTO(String account, String type, Double bidQuantity) {
        this.account = account;
        this.type = type;
        this.bidQuantity = bidQuantity;
    }
}