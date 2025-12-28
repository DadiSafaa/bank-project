import axios from "axios";
import AuthService from "./auth.service";

const api = axios.create({
  baseURL: "http://localhost:2000/api/rest/dashboard",
});

// Intercepteur pour ajouter le token JWT
api.interceptors.request.use(
  (config) => {
    const user = AuthService.getCurrentUser();
    if (user && user.jwtToken) {
      config.headers.Authorization = `Bearer ${user.jwtToken}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

/**
 * UC-4 : Récupérer le tableau de bord du client
 * @param {string} rib - RIB du compte à afficher (optionnel)
 * @param {number} page - Numéro de page pour la pagination (par défaut 0)
 * @returns {Promise} Dashboard avec solde, transactions, autres comptes
 */
const getDashboard = (rib = null, page = 0) => {
  const params = { page };
  if (rib) {
    params.rib = rib;
  }
  return api.get("", { params });
};

const DashboardService = {
  getDashboard,
};

export default DashboardService;