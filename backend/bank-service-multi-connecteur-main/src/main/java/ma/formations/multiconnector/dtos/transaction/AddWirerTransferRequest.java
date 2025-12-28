package ma.formations.multiconnector.dtos.transaction;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO pour UC-5 : Nouveau virement
 * Le username sera récupéré depuis le JWT (Authentication)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddWirerTransferRequest {

    @NotEmpty(message = "Le RIB émetteur est obligatoire")
    private String ribFrom;

    @NotEmpty(message = "Le RIB destinataire est obligatoire")
    private String ribTo;

    @NotNull(message = "Le montant est obligatoire")
    @Positive(message = "Le montant doit être positif")
    private Double amount;

    // ❌ RETIRÉ : private String username;
    // Le username sera récupéré depuis le token JWT dans le controller
}