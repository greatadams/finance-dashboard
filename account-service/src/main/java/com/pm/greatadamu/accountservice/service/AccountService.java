package com.pm.greatadamu.accountservice.service;

import com.pm.greatadamu.accountservice.dto.AccountRequestDto;
import com.pm.greatadamu.accountservice.dto.AccountResponseDto;
import com.pm.greatadamu.accountservice.exception.AccountNotFoundException;
import com.pm.greatadamu.accountservice.mapper.AccountMapper;
import com.pm.greatadamu.accountservice.model.Account;
import com.pm.greatadamu.accountservice.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;


@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;

    public AccountResponseDto createAccount(AccountRequestDto accountRequestDto) {
        //take user response and map to account entity
        Account account = accountMapper.mapToEntity(accountRequestDto);

        // save entity to DB(insert into accounts table)
        Account savedAccount=  accountRepository.save(account);

        //send it(entity) to responseDTO so user can see account created
        return accountMapper.mapToResponse(savedAccount);

    }

    public AccountResponseDto updateAccount(Long id,AccountRequestDto accountRequestDto) {
        //get account from db
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException("Account not found"));
        //update only the fields that are allowed to change
           account.setAccountName(accountRequestDto.getAccountName());
           account.setAccountType(accountRequestDto.getAccountType());

           //save updated account
        Account updatedAccount = accountRepository.save(account);

              //show user the update made
        return accountMapper.mapToResponse(updatedAccount);
    }

    public AccountResponseDto getAccountByAccountNumber(String accountNumber){
        //get acct from db and save it in acct entity
        Account acct = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException(accountNumber));
        return accountMapper.mapToResponse(acct);
    }

    public void deleteAccountByAccountNumber(String accountNumber){
        //get account by account from db and delete
        Account account = accountRepository.findByAccountNumber(accountNumber)
                        .orElseThrow(() ->new AccountNotFoundException(accountNumber));
        accountRepository.delete(account);

    }


}
