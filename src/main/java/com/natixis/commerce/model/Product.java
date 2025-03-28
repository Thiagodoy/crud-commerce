package com.natixis.commerce.model;

import com.natixis.commerce.controller.response.ProductResponse;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "tb_product")
public class Product {

    @Id
    @SequenceGenerator(name = "product_seq", sequenceName = "PRODUCT_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "product_seq")
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Long quantity;
    @ManyToOne
    @JoinColumn(name="user_id", nullable=false)
    private User user;
    @Builder.Default
    private boolean enabled = true;
}
