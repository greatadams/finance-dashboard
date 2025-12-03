package com.pm.greatadamu.customerservice.repository;

import com.pm.greatadamu.customerservice.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface CustomerRepository extends JpaRepository<Customer, Long> {
    //Get a single customer by email
    Optional<Customer> findCustomerByEmail(String email);
    //Check if something already exists (for validation)
    boolean existsByEmail(String email);


}
