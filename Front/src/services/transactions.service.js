import axios from "axios";
import AuthService from "./auth.service";

const api = axios.create({
  baseURL: "http://localhost:2000/api/rest/transaction",
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
 * UC-5 : Effectuer un virement bancaire
 * @param {string} ribFrom - RIB du compte émetteur
 * @param {string} ribTo - RIB du compte destinataire
 * @param {number} amount - Montant du virement
 * @returns {Promise} Réponse de l'API
 */
const createWireTransfer = (ribFrom, ribTo, amount) => {
  return api.post("/create", {
    ribFrom,
    ribTo,
    amount,
  });
};

/**
 * Récupérer les transactions d'un compte
 * @param {string} rib - RIB du compte
 * @param {Date} dateFrom - Date de début
 * @param {Date} dateTo - Date de fin
 * @returns {Promise} Liste des transactions
 */
const getTransactions = (rib, dateFrom, dateTo) => {
  return api.get("", {
    params: {
      rib,
      dateFrom: dateFrom?.toISOString(),
      dateTo: dateTo?.toISOString(),
    },
  });
};

const TransactionsService = {
  createWireTransfer,
  getTransactions,
};

export default TransactionsService;