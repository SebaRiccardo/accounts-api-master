package unsl.controllers;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import unsl.entities.ResponseError;
import unsl.entities.UserAccounts;
import unsl.entities.Account;
import unsl.entities.Amount;
import unsl.services.AccountServices;

@RestController
public class AccountController {

  @Autowired
  AccountServices accountService;
 
  @GetMapping(value ="/accounts")
  @ResponseBody
  @ResponseStatus(HttpStatus.OK)  
  public List<Account> getAll(){
      return accountService.getAll();
  }

  @GetMapping(value ="/accounts/{id}")
  @ResponseBody
  public Object getAccount(@PathVariable("id")long accountId){
        Account account = accountService.getAccount(accountId);
        if ( account == null) {
         return new ResponseEntity(new ResponseError(404, String.format("Account with ID %d not found", accountId)), HttpStatus.NOT_FOUND);
      }
     return account;
  }

  @GetMapping(value = "/accounts/search")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public UserAccounts searchAccount(@RequestParam("holder")long holder){
    UserAccounts accounts = accountService.findByHolder(holder);
    return accounts;
   } 

  @PostMapping(value = "/accounts")
  @ResponseStatus(HttpStatus.CREATED)
  @ResponseBody
  public Object createAccount(@RequestBody Account account) {
    return accountService.saveAccount(account);
  }
  
  @PatchMapping(value="/accounts/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @ResponseBody
  public Object updateStatus(@PathVariable("id")long accountId){
      
      Account res = accountService.updateStatus(accountId);
      
      if ( res == null) {
        return new ResponseEntity(new ResponseError(404, String.format("Account with ID %d not found", res.getId())), HttpStatus.NOT_FOUND);
      }

    return res;
  }
  @PutMapping(value = "/accounts/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @ResponseBody
  public Object updateBalance(@PathVariable("id")long accountId,@RequestBody Amount amount){
    return accountService.updateBalance(accountId,amount.getAmount());
  }

}
