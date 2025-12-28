import axios from "axios";
import AuthService from "./auth.service";

const api = axios.create({
  baseURL: "http://localhost:2000/api/rest/bank",
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
 * UC-3 : Créer un nouveau compte bancaire
 * @param {string} rib - RIB du compte (24 chiffres)
 * @param {string} customerIdentityRef - Numéro d'identité du client
 * @returns {Promise} Réponse de l'API
 */
const createBankAccount = (rib, customerIdentityRef) => {
  return api.post("/create", {
    rib,
    customerIdentityRef,
  });
};

/**
 * Récupérer tous les comptes bancaires
 * @returns {Promise} Liste des comptes
 */
const getAllBankAccounts = () => {
  return api.get("/all");
};

/**
 * Récupérer un compte bancaire par RIB
 * @param {string} rib - RIB du compte
 * @returns {Promise} Compte bancaire
 */
const getBankAccountByRib = (rib) => {
  return api.get("", { params: { rib } });
};

const BankAccountsService = {
  createBankAccount,
  getAllBankAccounts,
  getBankAccountByRib,
};

export default BankAccountsService;