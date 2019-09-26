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
import unsl.entities.User;
import unsl.services.AccountServices;
import unsl.utils.RestService;

@RestController
public class AccountController {

  @Autowired
  AccountServices accountService;

  @Autowired
  RestService restService;

  @GetMapping(value = "/accounts")
  @ResponseBody
  @ResponseStatus(HttpStatus.OK)
  public List<Account> getAll() {
    return accountService.getAll();
  }

  @GetMapping(value = "/accounts/{id}")
  @ResponseBody
  @ResponseStatus(HttpStatus.OK)
  public Object getAccount(@PathVariable("id") long accountId) {
    Account account = accountService.getAccount(accountId);

    if (account == null) {
      return new ResponseEntity(new ResponseError(404, String.format("Account with ID %d not found", accountId)),
          HttpStatus.NOT_FOUND);
    }

    return account;
  }

  @GetMapping(value = "accounts/{id}/users")
  @ResponseBody
  @ResponseStatus(HttpStatus.OK)
  public Object getUserData(@PathVariable("id") long accountId) {

    long holder_id = accountService.getHolderId(accountId);
    if (holder_id == -1) {

      return new ResponseEntity(new ResponseError(404, String.format("Fatal error account with no holder")),
          HttpStatus.NOT_FOUND);
    }
    User user;
    try {
      user = restService.getUser("http://localhost:8888/users/" + holder_id);
    } catch (Exception e) {
       return new ResponseEntity(new ResponseError(404, String.format("Can't request user with id %d",holder_id)), HttpStatus.NOT_FOUND);
    }

     return user;
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
  public Object updateStatus(@PathVariable("id")long accountId,@RequestBody Account account){
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
