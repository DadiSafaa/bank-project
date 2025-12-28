import api from "./axiosConfig";

/**
 * Récupérer tous les clients
 * @returns {Promise} Liste des clients
 */
const getCustomers = () => {
  return api.get("/agent_guichet/all");
};

/**
 * UC-2 : Créer un nouveau client (version complète)
 * Respecte RG_4, RG_5, RG_6, RG_7
 * @param {Object} customerData - Données du client
 * @param {string} customerData.identityRef - Numéro d'identité (unique, obligatoire)
 * @param {string} customerData.firstname - Prénom (obligatoire)
 * @param {string} customerData.lastname - Nom (obligatoire)
 * @param {string} customerData.username - Login (unique, obligatoire)
 * @param {string} customerData.email - Email (unique, obligatoire)
 * @param {string} customerData.dateOfBirth - Date de naissance (obligatoire) format: YYYY-MM-DD
 * @param {string} customerData.address - Adresse postale (obligatoire)
 * @returns {Promise} Réponse avec message de succès
 */
const createCustomer = (customerData) => {
  return api.post("/agent_guichet/create", {
    identityRef: customerData.identityRef,
    firstname: customerData.firstname,
    lastname: customerData.lastname,
    username: customerData.username,
    email: customerData.email,
    dateOfBirth: customerData.dateOfBirth,
    address: customerData.address,
  });
};

/**
 * Mettre à jour un client existant
 * @param {string} identityRef - Numéro d'identité du client
 * @param {Object} customerData - Nouvelles données
 * @returns {Promise} Réponse de l'API
 */
const updateCustomer = (identityRef, customerData) => {
  return api.put(`/agent_guichet/update/${identityRef}`, {
    firstname: customerData.firstname,
    lastname: customerData.lastname,
    username: customerData.username,
    email: customerData.email,
    dateOfBirth: customerData.dateOfBirth,
    address: customerData.address,
  });
};

/**
 * Supprimer un client
 * @param {string} identityRef - Numéro d'identité du client
 * @returns {Promise} Réponse de l'API
 */
const deleteCustomer = (identityRef) => {
  return api.delete(`/agent_guichet/delete/${identityRef}`);
};

/**
 * Récupérer un client par son numéro d'identité
 * @param {string} identityRef - Numéro d'identité
 * @returns {Promise} Données du client
 */
const getCustomerByIdentity = (identityRef) => {
  return api.get(`/identity/${identityRef}`);
};

const CustomersService = {
  getCustomers,
  createCustomer,
  updateCustomer,
  deleteCustomer,
  getCustomerByIdentity,
};

export default CustomersService;