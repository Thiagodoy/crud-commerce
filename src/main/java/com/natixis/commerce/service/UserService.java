package com.natixis.commerce.service;

import com.natixis.commerce.controller.request.UserRequest;
import com.natixis.commerce.controller.response.UserResponse;
import com.natixis.commerce.exception.ServiceException;
import com.natixis.commerce.mapper.UserMapper;
import com.natixis.commerce.model.User;
import com.natixis.commerce.repository.UserRepository;
import com.natixis.commerce.repository.spec.UserSpecification;
import com.natixis.commerce.utils.MessageStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;
    private final UserMapper mapper;

    @Transactional
    public UserResponse save(UserRequest request) {
        User user = mapper.toModel(request);
        return mapper.toResponse(repository.save(user));
    }

    @Transactional
    public UserResponse update(Long id, UserRequest request) {
        return repository.findById(id)
                .map(entity -> {
                    mapper.update(request, entity);
                    return repository.save(entity);
                }).map(mapper::toResponse)
                .orElseThrow(() -> new ServiceException(MessageStatus.RECORD_NOT_FOUND));
    }

    @Transactional
    public void delete(Long userId) {
        repository.findById(userId)
                .map(user -> {
                    user.setEnabled(false);
                    return repository.save(user);
                }).orElseThrow(() -> new ServiceException(MessageStatus.RECORD_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public Page<UserResponse> get(UserSpecification specification, Pageable pageable) {
        return repository.findAll(specification, pageable).map(mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public UserResponse get(Long id) {
        User user = repository.findById(id).orElseThrow(() -> new ServiceException(MessageStatus.RECORD_NOT_FOUND));
        return mapper.toResponse(user);
    }
}
