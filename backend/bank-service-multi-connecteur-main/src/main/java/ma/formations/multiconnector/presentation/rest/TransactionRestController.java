package ma.formations.multiconnector.presentation.rest;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import ma.formations.multiconnector.dtos.transaction.AddWirerTransferRequest;
import ma.formations.multiconnector.dtos.transaction.AddWirerTransferResponse;
import ma.formations.multiconnector.dtos.transaction.GetTransactionListRequest;
import ma.formations.multiconnector.dtos.transaction.TransactionDto;
import ma.formations.multiconnector.service.ITransactionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller pour les transactions
 * UC-5 : Nouveau virement (corrigé)
 */
@AllArgsConstructor
@RestController
@RequestMapping("/api/rest/transaction")
@CrossOrigin("http://localhost:3000/")
public class TransactionRestController {

    private ITransactionService transactionService;

    /**
     * UC-5 : Effectuer un nouveau virement
     * Le username est récupéré depuis le JWT (Authentication)
     */
    @PostMapping("/create")
    @PreAuthorize("hasAuthority('ADD_WIRED_TRANSFER')")
    public ResponseEntity<AddWirerTransferResponse> addWirerTransfer(
            @Valid @RequestBody AddWirerTransferRequest dto,
            Authentication authentication) {

        // Récupérer le username depuis le token JWT
        String username = authentication.getName();

        // Effectuer le virement
        AddWirerTransferResponse response = transactionService.wiredTransfer(dto, username);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('GET_TRANSACTIONS')")
    public List<TransactionDto> getTransactions(GetTransactionListRequest dto) {
        return transactionService.getTransactions(dto);
    }
}