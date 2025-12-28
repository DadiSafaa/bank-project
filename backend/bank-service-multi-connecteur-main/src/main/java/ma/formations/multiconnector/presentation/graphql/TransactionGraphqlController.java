package ma.formations.multiconnector.presentation.graphql;

import lombok.AllArgsConstructor;
import ma.formations.multiconnector.common.CommonTools;
import ma.formations.multiconnector.dtos.transaction.AddWirerTransferRequest;
import ma.formations.multiconnector.dtos.transaction.AddWirerTransferResponse;
import ma.formations.multiconnector.dtos.transaction.GetTransactionListRequest;
import ma.formations.multiconnector.dtos.transaction.TransactionDto;
import ma.formations.multiconnector.service.ITransactionService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

import java.util.List;

/**
 * GraphQL Controller pour les transactions
 * UC-5 : Nouveau virement (corrigé pour récupérer username depuis JWT)
 */
@Controller
@AllArgsConstructor
public class TransactionGraphqlController {

    private ITransactionService transactionService;
    private CommonTools commonTools;

    /**
     * UC-5 : Effectuer un nouveau virement via GraphQL
     * Le username est récupéré depuis le contexte de sécurité Spring
     */
    @MutationMapping
    public AddWirerTransferResponse addWirerTransfer(@Argument("dto") AddWirerTransferRequest dto) {
        // Récupérer l'authentification depuis le contexte de sécurité
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        // Appeler le service avec les 2 paramètres (dto + username)
        return transactionService.wiredTransfer(dto, username);
    }

    @QueryMapping
    public List<TransactionDto> getTransactions(@Argument GetTransactionListRequest dto) {
        return transactionService.getTransactions(dto);
    }
}