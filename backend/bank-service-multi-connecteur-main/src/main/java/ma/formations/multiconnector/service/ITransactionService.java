package ma.formations.multiconnector.service;

import ma.formations.multiconnector.dtos.transaction.AddWirerTransferRequest;
import ma.formations.multiconnector.dtos.transaction.AddWirerTransferResponse;
import ma.formations.multiconnector.dtos.transaction.GetTransactionListRequest;
import ma.formations.multiconnector.dtos.transaction.TransactionDto;

import java.util.List;
public interface ITransactionService {
    /**
     * UC-5 : Effectuer un virement
     * @param dto Les données du virement (ribFrom, ribTo, amount)
     * @param username Le username de l'utilisateur connecté (depuis JWT)
     * @return La réponse avec les 2 transactions créées
     */
    AddWirerTransferResponse wiredTransfer(AddWirerTransferRequest dto ,String username);
    List<TransactionDto> getTransactions(GetTransactionListRequest dto);
}
