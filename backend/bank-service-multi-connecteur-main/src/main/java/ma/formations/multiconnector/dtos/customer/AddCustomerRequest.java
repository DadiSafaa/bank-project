package ma.formations.multiconnector.dtos.customer;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * DTO pour la création d'un client (UC-2)
 * Respecte RG_4, RG_5, RG_6
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class AddCustomerRequest {

    @NotEmpty(message = "Le nom d'utilisateur est obligatoire")
    private String username;

    @NotEmpty(message = "Le numéro d'identité est obligatoire")
    private String identityRef;

    @NotEmpty(message = "Le nom est obligatoire")
    private String firstname;

    @NotEmpty(message = "Le prénom est obligatoire")
    private String lastname;

    // NOUVEAUX CHAMPS SELON RG_5
    @NotNull(message = "La date d'anniversaire est obligatoire")
    private Date dateAnniversaire;

    @NotEmpty(message = "L'adresse email est obligatoire")
    @Email(message = "L'adresse email doit être valide")
    private String email;

    @NotEmpty(message = "L'adresse postale est obligatoire")
    private String adressePostale;
}