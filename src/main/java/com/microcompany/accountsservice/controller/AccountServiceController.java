package com.microcompany.accountsservice.controller;

import com.microcompany.accountsservice.model.Account;
import com.microcompany.accountsservice.model.StatusMessage;
import com.microcompany.accountsservice.payload.MoneyForOwner;
import com.microcompany.accountsservice.services.AccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping("/accounts")
@Validated
public class AccountServiceController {
    private static final Logger logger = LoggerFactory.getLogger(AccountServiceController.class);

    @Autowired
    private AccountService service;

    @GetMapping("")
    public ResponseEntity<List<Account>> getAll() {
        if (service.getAccounts().size() > 0) {
            return new ResponseEntity<>(service.getAccounts(), HttpStatus.OK);
        } else return new ResponseEntity<>(service.getAccounts(), HttpStatus.NO_CONTENT);
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponseEntity<Account> save(@RequestBody @Valid Account newAccount) {
        logger.info("newAccount:" + newAccount);
        newAccount.setId(null);
        return new ResponseEntity<>(service.create(newAccount), HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{pid}", method = RequestMethod.GET)
    public Account getOne(@PathVariable("pid") Long id) {
        return service.getAccount(id);
    }

    @RequestMapping(value = "/{pid}", method = RequestMethod.DELETE)
    public ResponseEntity delete(@PathVariable("pid") Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }


//    //    @RequestMapping(value = "/{pid}", method = RequestMethod.PUT)
    @PutMapping("/{pid}")
    public ResponseEntity<Object> update(@PathVariable("pid") @Min(1) Long id, @RequestBody Account account) {
        if (id == account.getId()) {
            return new ResponseEntity<>(service.updateAccount(id,account), HttpStatus.ACCEPTED);
        } else {
            return new ResponseEntity<>(new StatusMessage(HttpStatus.PRECONDITION_FAILED.value(),
            "Id y account.id deben coincidir"), HttpStatus.PRECONDITION_FAILED);
        }
    }
    @PutMapping("/addmoney/{id}")
    public ResponseEntity<Account> addMoney(
            @PathVariable Long id,
            @RequestBody MoneyForOwner moneyForOwner
    ) {
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(service.addBalance(id, moneyForOwner.getAmount(), moneyForOwner.getOwnerId()));
    }
    //@PutMapping("/addBalance/{pid}/cantidad/{cantidad}/propietario/{propietario}")
//    @PutMapping("/addBalance/{pid}{cantidad}{propietario}")
//    public ResponseEntity<Object> addBalance(@PathVariable("pid") Long id,
//                                             @PathVariable("cantidad") int cantidad,
//                                             @PathVariable("propietario") Long propietario,
//                                             @RequestBody Account account) {
//        logger.info("account:" + account);
//        logger.info("id:" + account.getId());
//        logger.info("owner:" + account.getOwner().getId());
//        if (id == account.getId() && propietario.equals(account.getOwner().getId())) {
//            return new ResponseEntity<>(service.addBalance(id,cantidad,propietario), HttpStatus.ACCEPTED);
//        } else {
//            return new ResponseEntity<>(new StatusMessage(HttpStatus.PRECONDITION_FAILED.value(),
//            "Id y Owner deben coincidir"), HttpStatus.PRECONDITION_FAILED);
//        }
//    }
 @PutMapping("/withdraw/{id}")
    public ResponseEntity<Account> withdraw(
            @PathVariable Long id,
            @RequestBody MoneyForOwner moneyForOwner
    ) {
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(service.withdrawBalance(id, moneyForOwner.getAmount(), moneyForOwner.getOwnerId()));
    }

    @DeleteMapping("delAccountsOwner/{ownerId}")
    public ResponseEntity<Object> deleteCuentasUsuario(@PathVariable("ownerId") Long ownerId) {
        service.deleteAccountsUsingOwnerId(ownerId);
        return ResponseEntity.noContent().build();
    }
//
//    @PostMapping(value = "/duplicarProducto/{pid}")
//    public ResponseEntity<Product> duplicate(@PathVariable Long pid) {
//        return new ResponseEntity<>(service.duplicate(pid), HttpStatus.CREATED);
//    }

}