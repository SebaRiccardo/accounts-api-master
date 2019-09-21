package unsl.controllers;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import unsl.entities.ResponseError;
import unsl.entities.Account;
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
         return new ResponseEntity(new ResponseError(404, String.format("Account %d not found", accountId)), HttpStatus.NOT_FOUND);
      }
     return account;
  }

  @GetMapping(value = "/accounts/search")
  @ResponseBody
  public List<Account> searchAccount(@RequestParam("holder")long holder){
    List<Account> accounts = accountService.findByHolder(holder);
  
    return accounts;
   } 

  @PostMapping(value = "/accounts")
  @ResponseStatus(HttpStatus.CREATED)
  @ResponseBody
  public Object createAccount(@RequestBody Account account) {
    return accountService.saveAccount(account);
  }
  
  // se puede dejar con un PACH 
  @PatchMapping(value="/accounts")
  @ResponseBody
  public Object updateStatus(@RequestBody Account account){
      Account res = accountService.updateStatus(account);
      if ( res == null) {
        return new ResponseEntity(new ResponseError(404, String.format("Account with ID %d not found", account.getId())), HttpStatus.NOT_FOUND);
     }
    return res;
  }
}
