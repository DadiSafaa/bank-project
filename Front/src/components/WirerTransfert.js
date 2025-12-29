import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import TransactionsService from "../services/transactions.service";
import DashboardService from "../services/dashboard.service";
import AuthService from "../services/auth.service";

const WireTransfert = () => {
    const navigate = useNavigate();

    /* State definition - UC-5 */
    const [ribFrom, setRibFrom] = useState("");
    const [ribTo, setRibTo] = useState("");
    const [amount, setAmount] = useState("");
    const [myAccounts, setMyAccounts] = useState([]);
    const [selectedAccountBalance, setSelectedAccountBalance] = useState(null);
    
    const [messageInfo, setMessageInfo] = useState("");
    const [messageError, setMessageError] = useState("");
    const [loading, setLoading] = useState(false);
    const [showConfirmation, setShowConfirmation] = useState(false);

    /**
     * UC-5 : Effectuer un virement
     * Respecte RG_11, RG_12, RG_13, RG_14, RG_15
     */
    async function handleWireTransfer(event) {
        event.preventDefault();
        setMessageError("");
        setMessageInfo("");

        // Validation côté client
        if (!ribFrom || !ribTo || !amount) {
            setMessageError("❌ Tous les champs sont obligatoires");
            return;
        }

        // Validation RIB format (24 chiffres)
        const ribFromClean = ribFrom.replace(/\s/g, "");
        const ribToClean = ribTo.replace(/\s/g, "");

        if (!/^\d{24}$/.test(ribFromClean)) {
            setMessageError("❌ Le RIB source doit contenir 24 chiffres");
            return;
        }

        if (!/^\d{24}$/.test(ribToClean)) {
            setMessageError("❌ Le RIB destinataire doit contenir 24 chiffres");
            return;
        }

        // Validation montant
        const amountNum = parseFloat(amount);
        if (isNaN(amountNum) || amountNum <= 0) {
            setMessageError("❌ Le montant doit être supérieur à 0");
            return;
        }

        // Vérifier que le RIB source != RIB destinataire
        if (ribFromClean === ribToClean) {
            setMessageError("❌ Vous ne pouvez pas effectuer un virement vers le même compte");
            return;
        }

        // RG_12 : Vérification solde côté client (le backend vérifiera aussi)
        if (selectedAccountBalance !== null && amountNum > selectedAccountBalance) {
            setMessageError("❌ Solde insuffisant (RG_12)");
            return;
        }

        setLoading(true);

        try {
            // RG_11, RG_12, RG_13, RG_14, RG_15 : Gérés côté backend
            const result = await TransactionsService.createWireTransfer(
                ribFromClean,
                ribToClean,
                amountNum
            );

            // Message de succès
            setMessageInfo(
                `✅ ${result.data.message || "Virement effectué avec succès !"}`
            );
            setShowConfirmation(true);

            // Reset formulaire après succès
            setTimeout(() => {
                resetForm();
                // Rediriger vers le dashboard après 3 secondes
                setTimeout(() => {
                    navigate("/consult_account");
                }, 2000);
            }, 3000);

        } catch (error) {
            console.error(error);
            const errorMessage = error.response?.data?.details?.[0] || 
                                 error.response?.data?.message || 
                                 "Erreur lors du virement";
            
            // Affichage des erreurs métier (RG_11, RG_12)
            setMessageError(`❌ ${errorMessage}`);
        } finally {
            setLoading(false);
        }
    }

    /**
     * Réinitialiser le formulaire
     */
    function resetForm() {
        setRibTo("");
        setAmount("");
        setShowConfirmation(false);
    }

    /**
     * Formater le RIB pour l'affichage
     */
    function formatRib(rib) {
        if (!rib) return "";
        const cleaned = rib.replace(/\s/g, "");
        return cleaned.match(/.{1,4}/g)?.join(" ") || cleaned;
    }

    /**
     * Gérer la saisie du RIB destinataire avec formatage
     */
    function handleRibToChange(e) {
        const value = e.target.value.replace(/\s/g, "");
        if (/^\d*$/.test(value) && value.length <= 24) {
            setRibTo(formatRib(value));
        }
    }

    /**
     * Gérer le changement de compte source
     */
    function handleAccountChange(e) {
        const selectedRib = e.target.value;
        setRibFrom(selectedRib);

        // Trouver le solde du compte sélectionné
        const account = myAccounts.find(acc => acc.rib === selectedRib);
        if (account) {
            setSelectedAccountBalance(account.amount);
        }
    }

    /**
     * Charger les comptes du client connecté
     */
    async function loadMyAccounts() {
        try {
            const result = await DashboardService.getDashboard();
            const accounts = result.data.autresComptes || [];
            setMyAccounts(accounts);

            // Sélectionner automatiquement le premier compte
            if (accounts.length > 0) {
                const firstAccount = accounts[0];
                setRibFrom(firstAccount.rib);
                setSelectedAccountBalance(firstAccount.amount);
            }
        } catch (error) {
            console.error(error);
            setMessageError("Erreur lors du chargement de vos comptes");
        }
    }

    /**
     * Initialisation
     */
    useEffect(() => {
        const user = AuthService.getCurrentUser();
        if (!user) {
            navigate("/login");
            return;
        }

        // Vérifier que l'utilisateur est un CLIENT
        if (!user.roles.includes("ROLE_CLIENT")) {
            setMessageError("Accès refusé : Cette fonctionnalité est réservée aux clients");
            return;
        }

        loadMyAccounts();
    }, [navigate]);

    return (
        <div className="container mt-4">
            <h2 className="mb-4">💸 Nouveau Virement</h2>

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

            {/* Formulaire de virement */}
            {!showConfirmation ? (
                <div className="card shadow-sm">
                    <div className="card-header bg-primary text-white">
                        <h5 className="mb-0">Formulaire de Virement</h5>
                    </div>
                    <div className="card-body">
                        <form onSubmit={handleWireTransfer}>
                            {/* Compte source (RIB From) */}
                            <div className="mb-4 p-3 bg-light rounded">
                                <h6 className="text-primary">1️⃣ Compte Source</h6>
                                
                                {myAccounts.length > 1 ? (
                                    <>
                                        <label className="form-label">Sélectionnez le compte à débiter :</label>
                                        <select
                                            className="form-select form-select-lg font-monospace"
                                            value={ribFrom}
                                            onChange={handleAccountChange}
                                            required
                                        >
                                            {myAccounts.map((account) => (
                                                <option key={account.id} value={account.rib}>
                                                    {formatRib(account.rib)} - Solde: {account.amount?.toFixed(2)} MAD
                                                </option>
                                            ))}
                                        </select>
                                    </>
                                ) : myAccounts.length === 1 ? (
                                    <>
                                        <label className="form-label">Votre compte :</label>
                                        <div className="form-control form-control-lg font-monospace bg-white" disabled>
                                            {formatRib(ribFrom)}
                                        </div>
                                    </>
                                ) : (
                                    <div className="alert alert-warning">
                                        Aucun compte bancaire trouvé. Veuillez contacter votre agence.
                                    </div>
                                )}

                                {selectedAccountBalance !== null && (
                                    <div className="mt-2">
                                        <small className={`badge ${selectedAccountBalance > 0 ? "bg-success" : "bg-danger"}`}>
                                            Solde disponible : {selectedAccountBalance.toFixed(2)} MAD
                                        </small>
                                    </div>
                                )}
                            </div>

                            {/* Compte destinataire (RIB To) */}
                            <div className="mb-4 p-3 bg-light rounded">
                                <h6 className="text-primary">2️⃣ Compte Destinataire</h6>
                                <label className="form-label">RIB du bénéficiaire * (24 chiffres)</label>
                                <input
                                    type="text"
                                    className="form-control form-control-lg font-monospace"
                                    value={ribTo}
                                    onChange={handleRibToChange}
                                    placeholder="XXXX XXXX XXXX XXXX XXXX XXXX"
                                    maxLength="29"
                                    required
                                />
                                <small className="form-text text-muted">
                                    Saisissez le RIB du compte que vous souhaitez créditer
                                </small>
                                {ribTo && (
                                    <div className="mt-2">
                                        <small className={`badge ${ribTo.replace(/\s/g, "").length === 24 ? "bg-success" : "bg-warning"}`}>
                                            {ribTo.replace(/\s/g, "").length} / 24 chiffres
                                        </small>
                                    </div>
                                )}
                            </div>

                            {/* Montant */}
                            <div className="mb-4 p-3 bg-light rounded">
                                <h6 className="text-primary">3️⃣ Montant</h6>
                                <label className="form-label">Montant à transférer * (MAD)</label>
                                <div className="input-group input-group-lg">
                                    <input
                                        type="number"
                                        className="form-control"
                                        value={amount}
                                        onChange={(e) => setAmount(e.target.value)}
                                        placeholder="0.00"
                                        step="0.01"
                                        min="0.01"
                                        required
                                    />
                                    <span className="input-group-text">MAD</span>
                                </div>
                                <small className="form-text text-muted">
                                    RG_12 : Le montant doit être inférieur ou égal à votre solde
                                </small>
                                
                                {/* Vérification du solde en temps réel */}
                                {amount && selectedAccountBalance !== null && (
                                    <div className="mt-2">
                                        {parseFloat(amount) > selectedAccountBalance ? (
                                            <div className="alert alert-danger py-2 mb-0">
                                                ⚠️ Montant supérieur au solde disponible !
                                            </div>
                                        ) : (
                                            <div className="alert alert-success py-2 mb-0">
                                                ✅ Solde suffisant (Reste: {(selectedAccountBalance - parseFloat(amount)).toFixed(2)} MAD)
                                            </div>
                                        )}
                                    </div>
                                )}
                            </div>

                            {/* Récapitulatif avant validation */}
                            {ribFrom && ribTo && amount && (
                                <div className="alert alert-info">
                                    <h6 className="alert-heading">📋 Récapitulatif du virement</h6>
                                    <hr />
                                    <p className="mb-1"><strong>De :</strong> {formatRib(ribFrom)}</p>
                                    <p className="mb-1"><strong>Vers :</strong> {formatRib(ribTo)}</p>
                                    <p className="mb-0"><strong>Montant :</strong> {parseFloat(amount).toFixed(2)} MAD</p>
                                </div>
                            )}

                            {/* Boutons d'action */}
                            <div className="d-flex gap-2">
                                <button
                                    type="submit"
                                    className="btn btn-success btn-lg flex-grow-1"
                                    disabled={
                                        loading || 
                                        !ribFrom || 
                                        !ribTo || 
                                        !amount || 
                                        ribTo.replace(/\s/g, "").length !== 24 ||
                                        (selectedAccountBalance !== null && parseFloat(amount) > selectedAccountBalance)
                                    }
                                >
                                    {loading ? (
                                        <>
                                            <span className="spinner-border spinner-border-sm me-2"></span>
                                            Traitement en cours...
                                        </>
                                    ) : (
                                        "✅ Valider le Virement"
                                    )}
                                </button>
                                <button
                                    type="button"
                                    className="btn btn-secondary btn-lg"
                                    onClick={resetForm}
                                >
                                    Annuler
                                </button>
                            </div>

                            {/* Avertissement RG */}
                            <div className="mt-3">
                                <small className="text-muted">
                                    <strong>Règles appliquées :</strong>
                                    <ul className="mb-0 mt-1">
                                        <li>Le compte ne doit pas être bloqué ou clôturé</li>
                                        <li>Le solde doit être suffisant</li>
                                        <li>Votre compte sera débité</li>
                                        <li>Le compte destinataire sera crédité</li>
                                        <li>Les deux opérations seront tracées avec date précise</li>
                                    </ul>
                                </small>
                            </div>
                        </form>
                    </div>
                </div>
            ) : (
                /* Écran de confirmation après succès */
                <div className="card shadow-sm border-success">
                    <div className="card-body text-center py-5">
                        <div className="mb-4">
                            <div className="display-1 text-success">✅</div>
                        </div>
                        <h3 className="text-success mb-3">Virement Effectué avec Succès !</h3>
                        <p className="lead">Votre virement a été traité et enregistré.</p>
                        <hr />
                        <p className="text-muted">
                            Vous allez être redirigé vers votre tableau de bord dans quelques instants...
                        </p>
                        <button
                            className="btn btn-primary btn-lg mt-3"
                            onClick={() => navigate("/consult_account")}
                        >
                            Voir mon Tableau de Bord
                        </button>
                    </div>
                </div>
            )}
        </div>
    );
};

export default WireTransfert;