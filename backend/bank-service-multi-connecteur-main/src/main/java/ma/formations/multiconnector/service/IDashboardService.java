package ma.formations.multiconnector.service;

import ma.formations.multiconnector.dtos.dashboard.DashboardResponse;

/**
 * Interface pour UC-4 : Tableau de bord client
 */
public interface IDashboardService {

    /**
     * Récupère les informations du dashboard pour un client
     * @param username Le username du client connecté (depuis JWT)
     * @param rib Le RIB du compte à afficher (optionnel, si null = compte le plus récent)
     * @param page Numéro de page pour la pagination des transactions (commence à 0)
     * @return DashboardResponse avec toutes les infos du dashboard
     */
    DashboardResponse getDashboard(String username, String rib, int page);
}