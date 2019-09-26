package unsl.entities;

import java.util.List;

public class UserAccounts {

    private List<Account> userAccounts;

    public UserAccounts() {

    }

    public List<Account> getUserAccounts() {
        return userAccounts;
    }

    public void setUserAccounts(List<Account> userAccounts) {
        this.userAccounts = userAccounts;
    }



}