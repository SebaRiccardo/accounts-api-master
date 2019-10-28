package unsl.controllers;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import unsl.entities.Account;
import unsl.entities.Amount;
import unsl.entities.ResponseError;
import unsl.entities.User;
import unsl.entities.UserAccounts;
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
      return new ResponseEntity(new ResponseError(404, String.format("Account with id: %d not found", accountId)),
          HttpStatus.NOT_FOUND);
    }

    return account;
  }

  @GetMapping(value = "/accounts/{id}/users")
  @ResponseBody
  @ResponseStatus(HttpStatus.OK)
  public Object getUserData(@PathVariable("id") long accountId) {

    long holder_id = accountService.getHolderId(accountId);
    if (holder_id == -1) {
      return new ResponseEntity(new ResponseError(404, String.format("The account with id: %d does not exist",accountId)),HttpStatus.NOT_FOUND);
    }
    User user;
    try {                           
       user = restService.getUser("http://54.166.25.16:8888/users/" + holder_id);
    } catch (Exception e) {
       return new ResponseEntity(new ResponseError(404, String.format("Holder with id: %d not found",holder_id)), HttpStatus.NOT_FOUND);
    }

     return user;
   }

  @GetMapping(value = "/asccount/search")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public Object searchAccount(@RequestParam("holder")long holder){
    UserAccounts accounts = accountService.findByHolder(holder);
    if (accounts.getUserAccounts().isEmpty()){
      return new ResponseEntity(new ResponseError(404,String.format("The holder with id: %d has no accounts",holder)), HttpStatus.NOT_FOUND);
    }else{
      return accounts;
    }
   } 

  @PostMapping(value = "/accounts")
  @ResponseStatus(HttpStatus.CREATED)
  @ResponseBody
  public Object createAccount(@RequestBody Account account) {
   /** funcion ppara acreditarle 500  si la cuenta es la primera cuenta en pesos argentinos */
      UserAccounts current_accounts;
      boolean flag_already_has=false; 
      current_accounts = accountService.findByHolder(account.getHolder());

      /** se fija si tiene alguna cuenta */
      if(current_accounts.getUserAccounts().size()>0){    
          /** cantidad maxima de cuentas 3 */
          if(current_accounts.getUserAccounts().size()==3){
            return new ResponseEntity(new ResponseError(404,String.format("You have the maximum (%d) amount of accounts",current_accounts.getUserAccounts().size())), HttpStatus.BAD_REQUEST);
          }
         
          for(Account a: current_accounts.getUserAccounts()){
            if((a.getCurrency().compareTo(account.getCurrency()))==0){
              return new ResponseEntity(new ResponseError(404,"You can't open another account of type "+account.getCurrency() +", already exist one."), HttpStatus.BAD_REQUEST);   
            }

          } /** Si sino tiene cuenta en pesos y la cuenta que quiere guardar en es pesos le da 500 */
          if(account.getCurrency().compareTo(Account.Currency.PESO_AR)==0){
           account.setAccount_balance(new BigDecimal(500));
           return accountService.saveAccount(account);
          }else{
           account.setAccount_balance(new BigDecimal(0));
           return accountService.saveAccount(account);
          }
      }else{
        /** Si no tiene ninguna cuenta y abre una en pesos le da 500 */
        if(account.getCurrency().compareTo(Account.Currency.PESO_AR)==0){
          
          account.setAccount_balance(new BigDecimal(500));
          return accountService.saveAccount(account);
        }else{
          account.setAccount_balance(new BigDecimal(0));
          return accountService.saveAccount(account);
        }
      }
  }
  
  @PatchMapping(value="/accounts/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @ResponseBody
  public Object updateStatus(@PathVariable("id")long accountId,@RequestBody Account account){
       // busca la cuenta para ver si existe
      Account currentAccount= accountService.getAccount(accountId);
      if (currentAccount == null) {
           return new ResponseEntity(new ResponseError(404, String.format("Account with id: %d not found", currentAccount.getId())), HttpStatus.NOT_FOUND);
      }else{
        // se fija si el saldo es cero para poder darla de baja
       if(currentAccount.getAccount_balance().compareTo(new BigDecimal(0.00)) == 0 ){ 
           currentAccount = accountService.updateStatus(currentAccount,account.getStatus());
        }else{
          return new ResponseEntity(new ResponseError(400, String.format("The account balance MUST be 0 to set BAJA status")), HttpStatus.BAD_REQUEST);
        }
       return currentAccount;
     }
  }

  @PutMapping(value = "/accounts/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @ResponseBody
  public Object updateBalance(@PathVariable("id")long accountId,@RequestBody Amount amount){  
    if(amount.getAmount()==null){
      return new ResponseEntity(new ResponseError(404, String.format("The amount is null")), HttpStatus.BAD_REQUEST);
    }
    return accountService.updateBalance(accountId,amount.getAmount());
  }
}
