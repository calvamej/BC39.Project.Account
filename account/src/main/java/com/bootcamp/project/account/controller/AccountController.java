package com.bootcamp.project.account.controller;

import com.bootcamp.project.account.entity.AccountEntity;
import com.bootcamp.project.account.entity.report.AccountDailyReportEntity;
import com.bootcamp.project.account.entity.report.AccountReportEntity;
import com.bootcamp.project.account.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Date;

@RestController
@RequestMapping(value="/Account")
public class AccountController {
    @Autowired
    AccountService accountService;

    @GetMapping(value = "/FindAll")
    public Flux<AccountEntity> Get_All(){

        return accountService.getAll();
    }
    @GetMapping(value = "/FindOne/{accountNumber}")
    public Mono<AccountEntity> Get_One(@PathVariable("accountNumber") String accountNumber){
        return accountService.getOne(accountNumber);
    }
    @PostMapping(value = "/Save")
    public Mono<AccountEntity> Save(@RequestBody AccountEntity col){

        return accountService.save(col);
    }
    @PutMapping(value = "/Update/{accountNumber}/{balance}")
    public Mono<AccountEntity> Update(@PathVariable("accountNumber") String accountNumber,@PathVariable("balance") double balance){
        return accountService.update(accountNumber, balance);
    }
    @DeleteMapping  (value = "/Delete/{accountNumber}")
    public Mono<Void> Delete(@PathVariable("accountNumber") String accountNumber){
        return accountService.delete(accountNumber);
    }
    @PostMapping(value = "/RegisterPersonal")
    public Mono<AccountEntity> registerPersonalAccount(@RequestBody AccountEntity col){
        return accountService.registerPersonalAccount(col);
    }
    @PostMapping(value = "/RegisterCompany")
    public Mono<AccountEntity> registerCompanyAccount(@RequestBody AccountEntity col){
        return accountService.registerCompanyAccount(col);
    }
    @GetMapping(value = "/GetByClient/{clientDocumentNumber}")
    public Flux<AccountEntity> getByClient(@PathVariable("clientDocumentNumber") String clientDocumentNumber){
        return accountService.getByClient(clientDocumentNumber);
    }
    @GetMapping(value = "/GetBalance/{accountNumber}")
    public Mono<Double> getBalance(@PathVariable("accountNumber") String accountNumber){
        return accountService.getBalance(accountNumber);
    }
    @PutMapping(value = "/DepositBalance/{accountNumber}/{amount}")
    public Mono<AccountEntity> depositBalance(@PathVariable("accountNumber") String accountNumber,@PathVariable("amount") double amount){
        return accountService.depositBalance(accountNumber,amount);
    }
    @PutMapping(value = "/WithdrawBalance/{accountNumber}/{amount}")
    public Mono<AccountEntity> withdrawBalance(@PathVariable("accountNumber") String accountNumber,@PathVariable("amount") double amount){
        return accountService.withdrawBalance(accountNumber,amount);
    }
    @PutMapping(value = "/ApplyMaintenanceFee/{accountNumber}")
    public Mono<AccountEntity> applyMaintenanceFee(@PathVariable("accountNumber") String accountNumber){
        return accountService.applyMaintenanceFee(accountNumber);
    }
    @GetMapping(value = "/CheckIfCommissionApply/{accountNumber}")
    public Mono<Boolean> checkIfCommissionApply(@PathVariable("accountNumber") String accountNumber){
        return accountService.checkIfCommissionApply(accountNumber);
    }
    @GetMapping(value = "/CheckVIPMinimumBalanceCompliance/{accountNumber}")
    public Mono<Boolean> checkVIPMinimumDailyBalance(@PathVariable("accountNumber") String accountNumber){
        return accountService.checkVIPMinimumDailyBalance(accountNumber);
    }
    @GetMapping(value = "/GetAverageBalanceByClient/{clientDocumentNumber}")
    public Flux<AccountDailyReportEntity> getAverageBalanceByClient(@PathVariable("clientDocumentNumber") String clientDocumentNumber){
        return accountService.getAverageBalanceByClient(clientDocumentNumber);
    }
    @GetMapping(value = "/GetCreditsByDates/{initialDate}/{finalDate}")
    public Flux<AccountReportEntity> getCreditsByDates(@PathVariable("initialDate") @DateTimeFormat(pattern = "dd-MM-yyyy") Date initialDate, @PathVariable("finalDate") @DateTimeFormat(pattern = "dd-MM-yyyy") Date finalDate){
        return accountService.getCreditsByDates(initialDate,finalDate);
    }
    @GetMapping(value = "/GetCreditsByClient/{clientDocumentNumber}")
    public Flux<AccountReportEntity> getCreditsByClient(@PathVariable("clientDocumentNumber") String clientDocumentNumber){
        return accountService.getCreditsByClient(clientDocumentNumber);
    }


    @PutMapping(value = "/TransferBalance/{sourceAccountNumber}/{targetAccountNumber}/{balance}")
    public Mono<AccountEntity> transferBalance(@PathVariable("sourceAccountNumber") String sourceAccountNumber,@PathVariable("targetAccountNumber") String targetAccountNumber,@PathVariable("balance") double balance){
        return accountService.transferBalance(sourceAccountNumber,targetAccountNumber,balance);
    }
    //New Method: Valida si la tarjeta de débito ingresada ya tiene una cuenta principal asociada (True = sí tiene, False = NO).
    @GetMapping(value = "/CheckDebitCardMainAccount/{debitCardNumber}")
    public Mono<Boolean> checkDebitCardMainAccount(@PathVariable("debitCardNumber") String debitCardNumber){
        return accountService.checkDebitCardMainAccount(debitCardNumber);
    }
    //New Method: Asocia la cuenta ingresada a la tarjeta de débito ingresada.
    // Le coloca debitCardMainAccount = true porque será cuenta principal asociada a la tarjeta.
    @PutMapping(value = "/LinkDebitCardMainAccount/{accountNumber}/{debitCardNumber}")
    public Mono<AccountEntity> linkDebitCardMainAccount(@PathVariable("accountNumber") String accountNumber,@PathVariable("debitCardNumber") String debitCardNumber){
        return accountService.linkDebitCardMainAccount(accountNumber,debitCardNumber);
    }
    //New Method: Asocia la cuenta ingresada a la tarjeta de débito ingresada.
    // Le coloca debitCardMainAccount = false porque será una cuenta secundaria asociada a la tarjeta.
    @PutMapping(value = "/LinkDebitCardSecondaryAccount/{accountNumber}/{debitCardNumber}")
    public Mono<AccountEntity> linkDebitCardSecondaryAccount(@PathVariable("accountNumber") String accountNumber,@PathVariable("debitCardNumber") String debitCardNumber){
        return accountService.linkDebitCardSecondaryAccount(accountNumber,debitCardNumber);
    }
    //New Method: Busca todas las cuentas asociadas al número de documento ingresado.
    // Se valida también que no tengan ya una tarjeta de débito asociada o sean la cuenta principal de la tarjeta que se quiere asociar.
    //Luego se asocia a todas las cuentas resultantes con la tarjeta de débito ingresada con debitCardMainAccount = false.

    @PutMapping(value = "/LinkDebitCardSecondaryAccounts/{clientDocumentNumber}/{debitCardNumber}")
    public Flux<AccountEntity> linkDebitCardSecondaryAccounts(@PathVariable("clientDocumentNumber") String clientDocumentNumber,@PathVariable("debitCardNumber") String debitCardNumber){
        return accountService.linkDebitCardSecondaryAccounts(clientDocumentNumber,debitCardNumber);
    }
    //New Method: Trae el saldo (balance) de la cuenta principal asociada a la tarjeta de débito.
    @GetMapping(value = "/GetBalanceByDebitCard/{debitCardNumber}")
    public Mono<Double> getBalanceByDebitCard(@PathVariable("debitCardNumber") String debitCardNumber){
        return accountService.getBalanceByDebitCard(debitCardNumber);
    }
    //New Method: Cuando se realiza alguna compra o pago con la tarjeta de débito, se llama al método con el número de la tarjeta y el monto consumido.
    // El método luego trae todas las cuentas asociadas a la tarjeta de débito y filtra solo aquellas que tengan el saldo suficiente para realizar el pago.
    // Luego las ordena por el campo debitCardPriorityOrder y trae el primer registro con Next().
    //Se extrae el valor del monto de la compra/pago de la cuenta obtenida.
    @PutMapping(value = "/AddDebitCardPayment/{debitCardNumber}/{amount}")
    public Mono<AccountEntity> addDebitCardPayment(@PathVariable("debitCardNumber") String debitCardNumber,@PathVariable("amount") double amount){
        return accountService.addDebitCardPayment(debitCardNumber,amount);
    }
}
