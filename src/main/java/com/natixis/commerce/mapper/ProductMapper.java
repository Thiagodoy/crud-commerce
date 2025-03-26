package com.natixis.commerce.mapper;

import com.natixis.commerce.controller.request.ProductRequest;
import com.natixis.commerce.controller.response.ProductResponse;
import com.natixis.commerce.model.Product;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    ProductResponse toResponse(Product product);

    Product toModel(ProductRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void update(ProductRequest request, @MappingTarget Product product);
}
