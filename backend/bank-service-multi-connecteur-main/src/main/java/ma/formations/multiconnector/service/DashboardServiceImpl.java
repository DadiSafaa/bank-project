package ma.formations.multiconnector.service;

import lombok.AllArgsConstructor;
import ma.formations.multiconnector.dao.BankAccountRepository;
import ma.formations.multiconnector.dao.BankAccountTransactionRepository;
import ma.formations.multiconnector.dao.CustomerRepository;
import ma.formations.multiconnector.dtos.bankaccount.BankAccountDto;
import ma.formations.multiconnector.dtos.dashboard.DashboardResponse;
import ma.formations.multiconnector.dtos.transaction.TransactionDto;
import ma.formations.multiconnector.enums.TransactionType;
import ma.formations.multiconnector.service.exception.BusinessException;
import ma.formations.multiconnector.service.model.BankAccount;
import ma.formations.multiconnector.service.model.BankAccountTransaction;
import ma.formations.multiconnector.service.model.Customer;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implémentation du service Dashboard pour UC-4
 */
@Service
@Transactional
@AllArgsConstructor
public class DashboardServiceImpl implements IDashboardService {

    private final CustomerRepository customerRepository;
    private final BankAccountRepository bankAccountRepository;
    private final BankAccountTransactionRepository transactionRepository;
    private final ModelMapper modelMapper;


// ✅ APRÈS - Vérifier d'abord si c'est un Customer
        @Override
        public DashboardResponse getDashboard(String username, String rib, int page) {
            // Vérifier que l'utilisateur est bien un CLIENT (dans la table Customer)
            Customer customer = customerRepository.findByUsername(username)
                    .orElseThrow(() -> new BusinessException(
                            "Aucun compte client trouvé. Cette fonctionnalité est réservée aux clients."));

            // ... reste du code

        // 2️⃣ Récupérer tous les comptes du client
        List<BankAccount> allAccounts = bankAccountRepository.findByCustomer(customer);

        if (allAccounts.isEmpty()) {
            throw new BusinessException("Aucun compte bancaire trouvé pour ce client");
        }

        // 3️⃣ Déterminer le compte à afficher
        BankAccount mainAccount;
        if (rib != null && !rib.isEmpty()) {
            // Si un RIB est spécifié, on l'utilise
            mainAccount = allAccounts.stream()
                    .filter(acc -> acc.getRib().equals(rib))
                    .findFirst()
                    .orElseThrow(() -> new BusinessException(
                            String.format("Le compte [%s] n'appartient pas à ce client", rib)));
        } else {
            // Sinon, on prend le compte le plus récemment mouvementé (dernier créé)
            mainAccount = allAccounts.stream()
                    .max((a1, a2) -> a1.getId().compareTo(a2.getId()))
                    .orElseThrow(() -> new BusinessException("Impossible de déterminer le compte principal"));
        }

        // 4️⃣ Récupérer les 10 dernières transactions avec pagination
        Pageable pageable = PageRequest.of(page, 10);
        Page<BankAccountTransaction> transactionsPage = transactionRepository
                .findByBankAccountOrderByCreatedAtDesc(mainAccount, pageable);

        // 5️⃣ Convertir les transactions en DTO avec intitulé
        List<TransactionDto> transactionDtos = transactionsPage.getContent().stream()
                .map(transaction -> {
                    TransactionDto dto = modelMapper.map(transaction, TransactionDto.class);
                    // Générer l'intitulé de l'opération
                    dto.setIntitule(generateIntitule(transaction, customer));
                    return dto;
                })
                .collect(Collectors.toList());

        // 6️⃣ Convertir les autres comptes en DTO
        List<BankAccountDto> accountDtos = allAccounts.stream()
                .map(account -> modelMapper.map(account, BankAccountDto.class))
                .collect(Collectors.toList());

        // 7️⃣ Construire la réponse
        return DashboardResponse.builder()
                .rib(mainAccount.getRib())
                .solde(mainAccount.getAmount())
                .dernieresTransactions(transactionDtos)
                .autresComptes(accountDtos)
                .currentPage(page)
                .totalPages(transactionsPage.getTotalPages())
                .totalTransactions(transactionsPage.getTotalElements())
                .build();
    }

    /**
     * Génère l'intitulé de l'opération selon le type et le contexte
     * Exemple: "Virement en votre faveur de client@email.com"
     */
    private String generateIntitule(BankAccountTransaction transaction, Customer customer) {
        String username = transaction.getUser().getUsername();
        TransactionType type = transaction.getTransactionType();
        Double amount = transaction.getAmount();

        if (type == TransactionType.CREDIT) {
            // C'est un crédit (argent reçu)
            return String.format("Virement en votre faveur de %s - Montant: %.2f MAD", username, amount);
        } else {
            // C'est un débit (argent envoyé)
            return String.format("Virement émis vers %s - Montant: %.2f MAD", username, amount);
        }
    }
}