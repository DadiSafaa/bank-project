package ma.formations.multiconnector.dtos.user;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO pour le changement de mot de passe (UC-1)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChangePasswordRequest {
    @NotEmpty(message = "L'ancien mot de passe est obligatoire")
    private String oldPassword;

    @NotEmpty(message = "Le nouveau mot de passe est obligatoire")
    private String newPassword;
}