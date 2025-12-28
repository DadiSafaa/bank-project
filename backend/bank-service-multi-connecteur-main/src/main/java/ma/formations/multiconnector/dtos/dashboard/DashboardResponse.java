package ma.formations.multiconnector.dtos.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.formations.multiconnector.dtos.bankaccount.BankAccountDto;
import ma.formations.multiconnector.dtos.transaction.TransactionDto;

import java.util.List;

/**
 * DTO pour UC-4 : Tableau de bord client
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardResponse {

    // Informations du compte principal (affiché par défaut)
    private String rib;
    private Double solde;

    // Les 10 dernières transactions du compte
    private List<TransactionDto> dernieresTransactions;

    // Liste de tous les comptes du client (pour la liste déroulante)
    private List<BankAccountDto> autresComptes;

    // Informations de pagination
    private int currentPage;
    private int totalPages;
    private long totalTransactions;
}