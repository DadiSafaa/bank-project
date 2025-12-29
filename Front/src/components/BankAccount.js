import React, { useState, useEffect } from "react";
import BankAccountsService from "../services/bankaccounts.service";
import CustomersService from "../services/customers.service";
import AuthService from "../services/auth.service";

const BankAccount = () => {
    /* State definition - UC-3 */
    const [rib, setRib] = useState("");
    const [customerIdentityRef, setCustomerIdentityRef] = useState("");
    const [customerInfo, setCustomerInfo] = useState(null);
    const [bankAccounts, setBankAccounts] = useState([]);
    
    const [messageInfo, setMessageInfo] = useState("");
    const [messageError, setMessageError] = useState("");
    const [loading, setLoading] = useState(false);
    const [searchingCustomer, setSearchingCustomer] = useState(false);
    
    const [showAgentGuichetBoard, setShowAgentGuichetBoard] = useState(false);

    /**
     * UC-3 : Rechercher un client par son numéro d'identité (RG_8)
     */
    async function searchCustomer() {
        if (!customerIdentityRef.trim()) {
            setMessageError("Veuillez saisir un numéro d'identité");
            return;
        }

        setSearchingCustomer(true);
        setMessageError("");
        setCustomerInfo(null);

        try {
            const result = await CustomersService.getCustomerByIdentity(customerIdentityRef);
            setCustomerInfo(result.data);
            setMessageInfo(`✅ Client trouvé : ${result.data.firstname} ${result.data.lastname}`);
        } catch (error) {
            console.error(error);
            // RG_8 : Le numéro d'identité doit exister
            setMessageError("❌ Aucun client avec ce numéro d'identité (RG_8)");
            setCustomerInfo(null);
        } finally {
            setSearchingCustomer(false);
        }
    }

    /**
     * UC-3 : Créer un nouveau compte bancaire
     * Respecte RG_8, RG_9, RG_10
     */
    async function createBankAccount(event) {
        event.preventDefault();
        setMessageError("");
        setMessageInfo("");

        // Validation RIB (RG_9 : doit être valide - 24 chiffres)
        const ribClean = rib.replace(/\s/g, "");
        if (!/^\d{24}$/.test(ribClean)) {
            setMessageError("❌ Le RIB doit contenir exactement 24 chiffres (RG_9)");
            return;
        }

        // Vérifier que le client existe (RG_8)
        if (!customerInfo) {
            setMessageError("❌ Veuillez d'abord rechercher un client valide (RG_8)");
            return;
        }

        setLoading(true);

        try {
            const result = await BankAccountsService.createBankAccount(
                ribClean,
                customerIdentityRef
            );

            // RG_10 : Le compte est créé avec le statut "Ouvert"
            setMessageInfo(
                `✅ Compte créé avec succès ! RIB: ${ribClean} - Statut: OUVERT (RG_10)`
            );

            // Reset formulaire
            setRib("");
            setCustomerIdentityRef("");
            setCustomerInfo(null);

            // Recharger la liste des comptes
            await loadBankAccounts();
        } catch (error) {
            console.error(error);
            const errorMessage = error.response?.data?.details?.[0] || 
                                 error.response?.data?.message || 
                                 "Erreur lors de la création du compte";
            setMessageError(`❌ ${errorMessage}`);
        } finally {
            setLoading(false);
        }
    }

    /**
     * Charger tous les comptes bancaires
     */
    async function loadBankAccounts() {
        try {
            const result = await BankAccountsService.getAllBankAccounts();
            setBankAccounts(result.data);
        } catch (error) {
            console.error(error);
        }
    }

    /**
     * Formater le RIB pour l'affichage (groupes de 4 chiffres)
     */
    function formatRib(value) {
        const cleaned = value.replace(/\s/g, "");
        const formatted = cleaned.match(/.{1,4}/g)?.join(" ") || cleaned;
        return formatted;
    }

    /**
     * Gérer la saisie du RIB avec formatage automatique
     */
    function handleRibChange(e) {
        const value = e.target.value.replace(/\s/g, "");
        if (/^\d*$/.test(value) && value.length <= 24) {
            setRib(formatRib(value));
        }
    }

    /**
     * Initialisation au chargement
     */
    useEffect(() => {
        const user = AuthService.getCurrentUser();
        if (user) {
            setShowAgentGuichetBoard(user.roles.includes("ROLE_AGENT_GUICHET"));
        }
        if (user) {
            loadBankAccounts();
        }
    }, []);

    return (
        <div className="container mt-4">
            <h2 className="mb-4">Gestion des Comptes Bancaires</h2>

            {/* Messages de succès et d'erreur */}
            <div className="container">
                {messageError && (
                    <div className="alert alert-danger alert-dismissible fade show" role="alert">
                        {messageError}
                        <button 
                            type="button" 
                            className="btn-close" 
                            onClick={() => setMessageError("")}
                        ></button>
                    </div>
                )}
                {messageInfo && (
                    <div className="alert alert-success alert-dismissible fade show" role="alert">
                        {messageInfo}
                        <button 
                            type="button" 
                            className="btn-close" 
                            onClick={() => setMessageInfo("")}
                        ></button>
                    </div>
                )}
            </div>

            {/* Formulaire UC-3 : Créer un nouveau compte (AGENT_GUICHET uniquement) */}
            {showAgentGuichetBoard && (
                <div className="card mb-4">
                    <div className="card-header bg-success text-white">
                        <h5>Créer un Nouveau Compte Bancaire</h5>
                    </div>
                    <div className="card-body">
                        <form onSubmit={createBankAccount}>
                            {/* Étape 1 : Rechercher le client (RG_8) */}
                            <div className="mb-4 p-3 bg-light rounded">
                                <h6 className="text-primary">Étape 1 : Identifier le client</h6>
                                <div className="row">
                                    <div className="col-md-8">
                                        <label className="form-label">Numéro d'identité du client *</label>
                                        <input
                                            type="text"
                                            className="form-control"
                                            value={customerIdentityRef}
                                            onChange={(e) => setCustomerIdentityRef(e.target.value)}
                                            placeholder="Ex: AB123456"
                                            disabled={searchingCustomer}
                                        />
                                        <small className="form-text text-muted">
                                            Le client doit exister dans la base de données
                                        </small>
                                    </div>
                                    <div className="col-md-4 d-flex align-items-end">
                                        <button
                                            type="button"
                                            className="btn btn-primary w-100"
                                            onClick={searchCustomer}
                                            disabled={searchingCustomer || !customerIdentityRef.trim()}
                                        >
                                            {searchingCustomer ? (
                                                <>
                                                    <span className="spinner-border spinner-border-sm me-2"></span>
                                                    Recherche...
                                                </>
                                            ) : (
                                                "🔍 Rechercher"
                                            )}
                                        </button>
                                    </div>
                                </div>

                                {/* Affichage des infos du client trouvé */}
                                {customerInfo && (
                                    <div className="alert alert-info mt-3 mb-0">
                                        <strong>Client identifié :</strong>
                                        <ul className="mb-0 mt-2">
                                            <li><strong>Nom :</strong> {customerInfo.lastname} {customerInfo.firstname}</li>
                                            <li><strong>Identité :</strong> {customerInfo.identityRef}</li>
                                            <li><strong>Email :</strong> {customerInfo.email}</li>
                                        </ul>
                                    </div>
                                )}
                            </div>

                            {/* Étape 2 : Saisir le RIB (RG_9) */}
                            <div className="mb-4 p-3 bg-light rounded">
                                <h6 className="text-primary">Étape 2 : Saisir le RIB</h6>
                                <div className="row">
                                    <div className="col-md-12">
                                        <label className="form-label">RIB (Relevé d'Identité Bancaire) *</label>
                                        <input
                                            type="text"
                                            className="form-control form-control-lg font-monospace"
                                            value={rib}
                                            onChange={handleRibChange}
                                            placeholder="XXXX XXXX XXXX XXXX XXXX XXXX"
                                            maxLength="29"
                                            required
                                            disabled={!customerInfo}
                                        />
                                        <small className="form-text text-muted">
                                            Le RIB doit contenir 24 chiffres (formaté automatiquement)
                                        </small>
                                        {rib && (
                                            <div className="mt-2">
                                                <small className={`badge ${rib.replace(/\s/g, "").length === 24 ? "bg-success" : "bg-warning"}`}>
                                                    {rib.replace(/\s/g, "").length} / 24 chiffres
                                                </small>
                                            </div>
                                        )}
                                    </div>
                                </div>
                            </div>

                            {/* Bouton de création */}
                            <div className="d-flex gap-2">
                                <button
                                    type="submit"
                                    className="btn btn-success btn-lg"
                                    disabled={loading || !customerInfo || rib.replace(/\s/g, "").length !== 24}
                                >
                                    {loading ? (
                                        <>
                                            <span className="spinner-border spinner-border-sm me-2"></span>
                                            Création en cours...
                                        </>
                                    ) : (
                                        "✅ Créer le compte (Statut: OUVERT)"
                                    )}
                                </button>
                                <button
                                    type="button"
                                    className="btn btn-secondary"
                                    onClick={() => {
                                        setRib("");
                                        setCustomerIdentityRef("");
                                        setCustomerInfo(null);
                                        setMessageError("");
                                        setMessageInfo("");
                                    }}
                                >
                                    Réinitialiser
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            )}

            {/* Liste des comptes bancaires existants */}
            <div className="card">
                <div className="card-header bg-secondary text-white">
                    <h5>Liste des Comptes Bancaires ({bankAccounts.length})</h5>
                </div>
                <div className="card-body p-0">
                    <div className="table-responsive">
                        <table className="table table-hover table-striped mb-0">
                            <thead className="table-dark">
                                <tr>
                                    <th>RIB</th>
                                    <th>Client</th>
                                    <th>Solde</th>
                                    <th>Statut</th>
                                    <th>Date création</th>
                                </tr>
                            </thead>
                            <tbody>
                                {bankAccounts.length === 0 ? (
                                    <tr>
                                        <td colSpan="5" className="text-center text-muted py-4">
                                            Aucun compte bancaire trouvé
                                        </td>
                                    </tr>
                                ) : (
                                    bankAccounts.map((account) => (
                                        <tr key={account.id}>
                                            <td className="font-monospace fw-bold">{formatRib(account.rib)}</td>
                                            <td>
                                                {account.customer ? (
                                                    <>
                                                        {account.customer.lastname} {account.customer.firstname}
                                                        <br />
                                                        <small className="text-muted">
                                                            ID: {account.customer.identityRef}
                                                        </small>
                                                    </>
                                                ) : (
                                                    <span className="text-muted">-</span>
                                                )}
                                            </td>
                                            <td>
                                                <span className={`badge ${account.amount > 0 ? "bg-success" : "bg-danger"}`}>
                                                    {account.amount?.toFixed(2)} MAD
                                                </span>
                                            </td>
                                            <td>
                                                <span className={`badge ${
                                                    account.accountStatus === "OPENED" ? "bg-success" :
                                                    account.accountStatus === "BLOCKED" ? "bg-warning" :
                                                    "bg-danger"
                                                }`}>
                                                    {account.accountStatus}
                                                </span>
                                            </td>
                                            <td>
                                                {account.createdAt ? (
                                                    new Date(account.createdAt).toLocaleDateString('fr-FR')
                                                ) : (
                                                    <span className="text-muted">-</span>
                                                )}
                                            </td>
                                        </tr>
                                    ))
                                )}
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default BankAccount;