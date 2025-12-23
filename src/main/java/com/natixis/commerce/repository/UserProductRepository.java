package com.natixis.commerce.repository;

import com.natixis.commerce.model.UserProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.stereotype.Repository;

@Repository
public interface UserProductRepository  extends JpaRepository<UserProduct, Long> {
}
