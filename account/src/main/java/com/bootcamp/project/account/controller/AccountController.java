package com.bootcamp.project.account.controller;

import com.bootcamp.project.account.entity.AccountEntity;
import com.bootcamp.project.account.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @GetMapping(value = "/FindOne/{accountNumber}")
    public Mono<AccountEntity> Get_One(@PathVariable("accountNumber") String accountNumber){
        return accountService.getOne(accountNumber);
    }
    @GetMapping(value = "/FindAll")
    public Flux<AccountEntity> Get_All(){

        return accountService.getAll();
    }
    @PostMapping(value = "/Save")
    public Mono<AccountEntity> Save(@RequestBody AccountEntity col){

        return accountService.save(col);
    }
    @PutMapping(value = "/Update/{accountNumber}/{balance}")
    public Mono<AccountEntity> Update(@PathVariable("accountNumber") String accountNumber,@PathVariable("type") double balance){
        return accountService.update(accountNumber, balance);
    }
    @DeleteMapping  (value = "/Delete/{accountNumber}")
    public Mono<Void> Delete(@PathVariable("accountNumber") String accountNumber){
        return accountService.delete(accountNumber);
    }

    @GetMapping(value = "/GetByClientAndProduct/{clientDocumentNumber}/{productCode}")
    public Mono<AccountEntity> getByClientAndProduct(@PathVariable("clientDocumentNumber") String clientDocumentNumber,@PathVariable("productCode") String productCode){
        return accountService.getByClientAndProduct(clientDocumentNumber,productCode);
    }
    @GetMapping(value = "/GetBalance/{accountNumber}")
    public Mono<Double> getBalance(@PathVariable("accountNumber") String accountNumber){
        return accountService.getBalance(accountNumber);
    }
    @PutMapping(value = "/DepositBalance/{accountNumber}/{balance}")
    public Mono<AccountEntity> depositBalance(@PathVariable("accountNumber") String accountNumber,@PathVariable("balance") double balance){
        return accountService.depositBalance(accountNumber,balance);
    }
    @PutMapping(value = "/WithdrawBalance/{accountNumber}/{balance}")
    public Mono<AccountEntity> withdrawBalance(@PathVariable("accountNumber") String accountNumber,@PathVariable("balance") double balance){
        return accountService.withdrawBalance(accountNumber,balance);
    }
    @PutMapping(value = "/ApplyMaintenanceFee/{accountNumber}")
    public Mono<AccountEntity> applyMaintenanceFee(@PathVariable("accountNumber") String accountNumber){
        return accountService.applyMaintenanceFee(accountNumber);
    }
    @PostMapping(value = "/RegisterPersonal")
    public Mono<AccountEntity> registerPersonalAccount(@RequestBody AccountEntity col){
        return accountService.registerPersonalAccount(col);
    }
    @PostMapping(value = "/RegisterCompany")
    public Mono<AccountEntity> registerCompanyAccount(@RequestBody AccountEntity col){
        return accountService.registerCompanyAccount(col);
    }
    @PutMapping(value = "/TransferBalance/{sourceAccountNumber}/{targetAccountNumber}/{balance}")
    public Mono<AccountEntity> transferBalance(@PathVariable("sourceAccountNumber") String sourceAccountNumber,@PathVariable("targetAccountNumber") String targetAccountNumber,@PathVariable("balance") double balance){
        return accountService.transferBalance(sourceAccountNumber,targetAccountNumber,balance);
    }
    @PutMapping(value = "/ApplyCommissionFee/{accountNumber}/{amount}")
    public Mono<AccountEntity> applyCommissionFee(@PathVariable("accountNumber") String accountNumber,@PathVariable("amount") double amount){
        return accountService.applyCommissionFee(accountNumber,amount);
    }
    @GetMapping(value = "/CheckMinimumDailyBalance/{accountNumber}")
    public Mono<Boolean> checkMinimumDailyBalance(@PathVariable("accountNumber") String accountNumber){
        return accountService.checkMinimumDailyBalance(accountNumber);
    }
    @GetMapping(value = "/GetAverageBalance/{clientDocumentNumber}")
    public Mono<Double> getAverageBalance(@PathVariable("clientDocumentNumber") String clientDocumentNumber){
        return accountService.getAverageBalance(clientDocumentNumber);
    }
    //New Method: Valida si la tarjeta de débito ingresada ya tiene una cuenta principal asociada (True = sí tiene, False = NO).
    @GetMapping(value = "/CheckDebitCardMainAccount/{debitCardNumber}")
    public Mono<Boolean> checkDebitCardMainAccount(@PathVariable("debitCardNumber") String debitCardNumber){
        return accountService.checkDebitCardMainAccount(debitCardNumber);
    }
    //New Method: Trae el saldo (balance) de la cuenta principal asociada a la tarjeta de débito.
    @GetMapping(value = "/GetBalanceByDebitCard/{debitCardNumber}")
    public Mono<Double> getBalanceByDebitCard(@PathVariable("debitCardNumber") String debitCardNumber){
        return accountService.getBalanceByDebitCard(debitCardNumber);
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
    //New Method: Cuando se realiza alguna compra o pago con la tarjeta de débito, se llama al método con el número de la tarjeta y el monto consumido.
    // El método luego trae todas las cuentas asociadas a la tarjeta de débito y filtra solo aquellas que tengan el saldo suficiente para realizar el pago.
    // Luego las ordena por el campo debitCardPriorityOrder y trae el primer registro con Next().
    //Se extrae el valor del monto de la compra/pago de la cuenta obtenida.
    @PutMapping(value = "/AddDebitCardPayment/{debitCardNumber}/{amount}")
    public Mono<AccountEntity> addDebitCardPayment(@PathVariable("debitCardNumber") String debitCardNumber,@PathVariable("amount") double amount){
        return accountService.addDebitCardPayment(debitCardNumber,amount);
    }
    //New Method: Trae todas las cuentas asociadas a un cliente (a su número de documento).
    //La idea es utilizar este método para obtener la data para el reporte completo y consolidado solicitado.
    @GetMapping(value = "/GetByClient/{clientDocumentNumber}")
    public Flux<AccountEntity> getByClient(@PathVariable("clientDocumentNumber") String clientDocumentNumber){
        return accountService.getByClient(clientDocumentNumber);
    }
    //New Method: Trae todas las cuentas asociadas a un cliente (a su número de documento) creadas entre las fechas ingresadas.
    //La idea es utilizar este método para obtener la data para el reporte completo y consolidado solicitado.
    @GetMapping(value = "/GetByClientAndDates/{clientDocumentNumber}/{initialDate}/{finalDate}")
    public Flux<AccountEntity> getByClientAndDates(@PathVariable("clientDocumentNumber") String clientDocumentNumber,@PathVariable("initialDate") Date initialDate,@PathVariable("finalDate") Date finalDate){
        return accountService.getByClientAndDates(clientDocumentNumber,initialDate,finalDate);
    }
}
