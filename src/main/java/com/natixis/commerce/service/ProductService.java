package com.natixis.commerce.service;

import com.natixis.commerce.controller.request.ProductRequest;
import com.natixis.commerce.controller.response.ProductResponse;
import com.natixis.commerce.exception.ServiceException;
import com.natixis.commerce.mapper.ProductMapper;
import com.natixis.commerce.model.Product;
import com.natixis.commerce.model.User;
import com.natixis.commerce.repository.ProductRepository;
import com.natixis.commerce.repository.UserProductRepository;
import com.natixis.commerce.repository.spec.ProductSpecification;
import com.natixis.commerce.utils.MessageStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository repository;
    private final UserProductRepository userProductRepository;
    private final ProductMapper mapper;

    @Transactional
    public ProductResponse save(ProductRequest request, Principal principal) {
        User user = (User) ((UsernamePasswordAuthenticationToken) principal).getPrincipal();
        Product product = mapper.toModel(request);
        product.setUser(user);
        return mapper.toResponse(repository.save(product));
    }

    @Transactional(rollbackFor = ServiceException.class)
    public ProductResponse update(Long id, ProductRequest request) {
        return repository.findById(id)
                .map(entity -> {
                    mapper.update(request, entity);
                    return repository.save(entity);
                }).map(mapper::toResponse)
                .orElseThrow(() -> new ServiceException(MessageStatus.RECORD_NOT_FOUND));
    }

    @Transactional
    public void delete(Long id) {
        repository.findById(id)
                .map(product -> {
                    product.setEnabled(false);
                    return repository.save(product);
                }).orElseThrow(() -> new ServiceException(MessageStatus.RECORD_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> get(ProductSpecification specification, Pageable pageable) {
        return repository.findAll(specification, pageable).map(mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public ProductResponse get(Long id) {
        Product product = repository.findById(id)
                .orElseThrow(() -> new ServiceException(MessageStatus.RECORD_NOT_FOUND));
        return mapper.toResponse(product);
    }


    @Transactional(readOnly = true)
    public List getUserProduct() {

        return userProductRepository.findAll();
    }
}
