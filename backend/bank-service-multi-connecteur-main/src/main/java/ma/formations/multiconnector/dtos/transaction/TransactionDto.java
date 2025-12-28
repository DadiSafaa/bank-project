package ma.formations.multiconnector.dtos.transaction;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.formations.multiconnector.dtos.bankaccount.BankAccountDto;
import ma.formations.multiconnector.dtos.user.UserDto;

/**
 * DTO mis à jour pour UC-4 avec l'intitulé de l'opération
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class TransactionDto {
    private Long id;
    private String createdAt;
    private String transactionType;
    private Double amount;
    private BankAccountDto bankAccount;
    private UserDto user;

    // 🆕 NOUVEAU CHAMP pour UC-4 : Intitulé de l'opération
    // Exemple: "Virement en votre faveur de client@email.com"
    private String intitule;
}