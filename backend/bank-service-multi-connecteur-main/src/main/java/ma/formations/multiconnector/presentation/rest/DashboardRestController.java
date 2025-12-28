package ma.formations.multiconnector.presentation.rest;

import lombok.AllArgsConstructor;
import ma.formations.multiconnector.dtos.dashboard.DashboardResponse;
import ma.formations.multiconnector.service.IDashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller pour UC-4 : Tableau de bord client
 */
@RestController
@RequestMapping("/api/rest/dashboard")
@AllArgsConstructor
@PreAuthorize("hasRole('ROLE_CLIENT')")
public class DashboardRestController {

    private final IDashboardService dashboardService;

    /**
     * Récupère les informations du dashboard pour le client connecté
     *
     * @param authentication Authentification JWT (username récupéré automatiquement)
     * @param rib RIB du compte à afficher (optionnel, si null = compte le plus récent)
     * @param page Numéro de page pour la pagination (par défaut = 0)
     * @return DashboardResponse avec toutes les infos
     */
    @GetMapping
    public ResponseEntity<DashboardResponse> getDashboard(
            Authentication authentication,
            @RequestParam(required = false) String rib,
            @RequestParam(defaultValue = "0") int page) {

        // Récupérer le username depuis le token JWT
        String username = authentication.getName();

        DashboardResponse response = dashboardService.getDashboard(username, rib, page);
        return ResponseEntity.ok(response);
    }
}