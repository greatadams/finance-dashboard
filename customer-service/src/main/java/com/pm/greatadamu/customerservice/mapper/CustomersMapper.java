package com.pm.greatadamu.customerservice.mapper;

import com.pm.greatadamu.customerservice.dto.CustomerRequestDTO;
import com.pm.greatadamu.customerservice.dto.CustomerResponseDTO;
import com.pm.greatadamu.customerservice.model.Customer;
import org.springframework.stereotype.Component;

@Component
public class CustomersMapper {
    // RequestDTO -> Entity (save to DB)
   public Customer mapToEntity(CustomerRequestDTO customerRequestDTO) {
       return Customer.builder()
               .firstName(customerRequestDTO.getFirstName())
               .lastName(customerRequestDTO.getLastName())
               .email(customerRequestDTO.getEmail())
               .phoneNumber(customerRequestDTO.getPhoneNumber())
               .address(customerRequestDTO.getAddress())
               .build();

   }

   public CustomerResponseDTO mapToResponse(Customer customer) {
       return new CustomerResponseDTO(
               customer.getId(),
               customer.getFirstName(),
               customer.getLastName(),
               customer.getEmail(),
               customer.getPhoneNumber(),
               customer.getAddress(),
               customer.getCreatedAt(),
               customer.getUpdatedAt()
       );
   }

   public void updateCustomerFromDTO(CustomerRequestDTO customerRequestDTO, Customer customer) {
       customer.setFirstName(customerRequestDTO.getFirstName());
       customer.setLastName(customerRequestDTO.getLastName());
       customer.setEmail(customerRequestDTO.getEmail());
       customer.setPhoneNumber(customerRequestDTO.getPhoneNumber());
       customer.setAddress(customerRequestDTO.getAddress());
   }
}
