package com.natixis.commerce.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Table(name = "USER_PRODUCT")
@Data
public class UserProduct {
    @Id
    private Long id;
    private String productName;
    private BigDecimal  price;
    private String name;
}
