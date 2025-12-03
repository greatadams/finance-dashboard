package com.pm.greatadamu.customerservice.controller;

import com.pm.greatadamu.customerservice.dto.CustomerRequestDTO;
import com.pm.greatadamu.customerservice.dto.CustomerResponseDTO;
import com.pm.greatadamu.customerservice.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {
    private final CustomerService customerService;

    @PostMapping
    public ResponseEntity<CustomerResponseDTO>createCustomer(@Valid @RequestBody CustomerRequestDTO customerRequestDTO){
        //call service
        CustomerResponseDTO customerResponseDTO = customerService.createCustomer(customerRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(customerResponseDTO);

    }

    @GetMapping
    public ResponseEntity<List<CustomerResponseDTO>> getAllCustomer(){
        //call service
       List<CustomerResponseDTO> customer = customerService.findAllCustomers();
       return ResponseEntity.ok(customer);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponseDTO> getCustomerById(@PathVariable("id") Long id){
        CustomerResponseDTO customerResponseDTO = customerService.findCustomerById(id);
        return ResponseEntity.ok(customerResponseDTO);
    }

    @GetMapping("/by-email")
    public ResponseEntity<CustomerResponseDTO> getCustomerByEmail(@RequestParam String email){
        CustomerResponseDTO customerResponseDTO = customerService.findCustomerByEmail(email);
        return ResponseEntity.ok(customerResponseDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomerResponseDTO>uodateCustomer(
            @PathVariable Long id,
            @Valid @RequestBody CustomerRequestDTO customerRequestDTO){
        //call service
        CustomerResponseDTO customerResponseDTO =customerService.updateCustomerById(id,customerRequestDTO);
        return ResponseEntity.ok(customerResponseDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void>deleteCustomer(@PathVariable("id") Long id){
        customerService.deleteCustomerById(id);
       return ResponseEntity.noContent().build();
    }

}
