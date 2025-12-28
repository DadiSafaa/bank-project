package ma.formations.multiconnector.service;

import lombok.AllArgsConstructor;
import ma.formations.multiconnector.dao.BankAccountRepository;
import ma.formations.multiconnector.dao.BankAccountTransactionRepository;
import ma.formations.multiconnector.dao.UserRepository;
import ma.formations.multiconnector.dtos.transaction.AddWirerTransferRequest;
import ma.formations.multiconnector.dtos.transaction.AddWirerTransferResponse;
import ma.formations.multiconnector.dtos.transaction.GetTransactionListRequest;
import ma.formations.multiconnector.dtos.transaction.TransactionDto;
import ma.formations.multiconnector.enums.AccountStatus;
import ma.formations.multiconnector.enums.TransactionType;
import ma.formations.multiconnector.service.exception.BusinessException;
import ma.formations.multiconnector.service.model.BankAccount;
import ma.formations.multiconnector.service.model.BankAccountTransaction;
import ma.formations.multiconnector.service.model.GetTransactionListBo;
import ma.formations.multiconnector.service.model.User;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implémentation du service de transactions
 * UC-5 : Nouveau virement (avec messages d'erreur adaptés)
 */
@Service
@Transactional
@AllArgsConstructor
public class TransactionServiceImpl implements ITransactionService {

    private final BankAccountRepository bankAccountRepository;
    private final BankAccountTransactionRepository bankAccountTransactionRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    /**
     * UC-5 : Effectuer un nouveau virement
     * Respecte RG_11, RG_12, RG_13, RG_14, RG_15
     */
    @Override
    public AddWirerTransferResponse wiredTransfer(AddWirerTransferRequest dto, String username) {
        String ribFrom = dto.getRibFrom();
        String ribTo = dto.getRibTo();
        Double amount = dto.getAmount();

        // Vérifier que l'utilisateur existe
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException(
                        String.format("Utilisateur [%s] introuvable", username)));

        // Récupérer le compte émetteur
        BankAccount bankAccountFrom = bankAccountRepository.findByRib(ribFrom)
                .orElseThrow(() -> new BusinessException(
                        String.format("Le compte avec le RIB %s n'existe pas", ribFrom)));

        // Récupérer le compte destinataire
        BankAccount bankAccountTo = bankAccountRepository.findByRib(ribTo)
                .orElseThrow(() -> new BusinessException(
                        String.format("Le compte destinataire avec le RIB %s n'existe pas", ribTo)));

        // Vérifier les règles métier (RG_11, RG_12)
        checkBusinessRules(bankAccountFrom, bankAccountTo, amount);

        // RG_13 : Débiter le compte émetteur
        bankAccountFrom.setAmount(bankAccountFrom.getAmount() - amount);

        // RG_14 : Créditer le compte destinataire
        bankAccountTo.setAmount(bankAccountTo.getAmount() + amount);

        // RG_15 : Tracer les deux opérations avec leurs dates précises
        Date now = new Date();

        // Transaction DEBIT (débit du compte émetteur)
        BankAccountTransaction transactionFrom = BankAccountTransaction.builder()
                .amount(amount)
                .transactionType(TransactionType.DEBIT)
                .bankAccount(bankAccountFrom)
                .user(user)
                .createdAt(now)
                .build();

        // Transaction CREDIT (crédit du compte destinataire)
        BankAccountTransaction transactionTo = BankAccountTransaction.builder()
                .amount(amount)
                .transactionType(TransactionType.CREDIT)
                .bankAccount(bankAccountTo)
                .user(user)
                .createdAt(now)
                .build();

        // Sauvegarder les transactions
        bankAccountTransactionRepository.save(transactionFrom);
        bankAccountTransactionRepository.save(transactionTo);

        // Préparer la réponse
        return AddWirerTransferResponse.builder()
                .message(String.format(
                        "Virement de %.2f MAD du compte %s vers le compte %s effectué avec succès",
                        amount, ribFrom, ribTo))
                .transactionFrom(modelMapper.map(transactionFrom, TransactionDto.class))
                .transactionTo(modelMapper.map(transactionTo, TransactionDto.class))
                .build();
    }

    /**
     * Vérifier les règles métier pour un virement
     * RG_11 : Le compte ne doit pas être bloqué ou clôturé
     * RG_12 : Le solde doit être suffisant
     */
    private void checkBusinessRules(BankAccount bankAccountFrom, BankAccount bankAccountTo, Double amount) {
        // RG_11 : Vérifier que le compte émetteur n'est pas bloqué ou clôturé
        if (bankAccountFrom.getAccountStatus() == AccountStatus.CLOSED) {
            throw new BusinessException("Compte bancaire bloqué ou clôturé");
        }

        if (bankAccountFrom.getAccountStatus() == AccountStatus.BLOCKED) {
            throw new BusinessException("Compte bancaire bloqué ou clôturé");
        }

        // Vérifier que le compte destinataire n'est pas bloqué ou clôturé
        if (bankAccountTo.getAccountStatus() == AccountStatus.CLOSED) {
            throw new BusinessException(
                    String.format("Le compte destinataire %s est clôturé", bankAccountTo.getRib()));
        }

        if (bankAccountTo.getAccountStatus() == AccountStatus.BLOCKED) {
            throw new BusinessException(
                    String.format("Le compte destinataire %s est bloqué", bankAccountTo.getRib()));
        }

        // RG_12 : Vérifier que le solde est suffisant
        if (bankAccountFrom.getAmount() < amount) {
            throw new BusinessException("Solde insuffisant");
        }
    }

    @Override
    public List<TransactionDto> getTransactions(GetTransactionListRequest requestDTO) {
        GetTransactionListBo data = modelMapper.map(requestDTO, GetTransactionListBo.class);
        return bankAccountTransactionRepository.findByBankAccount_RibAndCreatedAtBetween(
                        data.getRib(), data.getDateFrom(), data.getDateTo())
                .stream()
                .map(bo -> modelMapper.map(bo, TransactionDto.class))
                .collect(Collectors.toList());
    }
}