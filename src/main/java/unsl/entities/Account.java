package unsl.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

import javax.persistence.*;


@Entity 
@Table (name= "accounts",
  uniqueConstraints={
     @UniqueConstraint(columnNames={"id"})})

  public class Account {

        public Account(){


        }
        public static enum Currency {
            PESO_AR,
            DOLAR,
            EURO
        }
     
        public static enum Status {
            ACTIVA,
            BAJA
        }
         
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private long id;

        private BigDecimal account_balance;
     
        @JsonProperty("holder")
        private long holder;
     
        @Enumerated(EnumType.STRING)
        @JsonProperty("currency")
        private Currency currency;
     
        @Enumerated(EnumType.STRING)
        private Status status;
     
        public long getId() {
            return id;
        }
     
        public void setId(long id) {
            this.id = id;
        }
     
        public BigDecimal getAccount_balance() {
            return account_balance;
        }
     
        public void setAccount_balance(BigDecimal account_balance) {
            this.account_balance = account_balance;
        }
     
        public Long getHolder() {
            return holder;
        }
     
        public void setHolder(Long holder) {
            this.holder = holder;
        }
     
        public Currency getCurrency() {
            return currency;
        }
     
        public void setCurrency(Currency currency) {
            this.currency = currency;
        }
     
        public Status getStatus() {
            return status;
        }
     
        public void setStatus(Status status) {
            this.status = status;
        }
    }
    

    
