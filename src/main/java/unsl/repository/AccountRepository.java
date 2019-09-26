package unsl.repository;

import unsl.entities.Account;
import unsl.entities.UserAccounts;

import org.springframework.data.repository.query.Param;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {
   
    UserAccounts findByHolder(@Param("holder") Long holder);

}
