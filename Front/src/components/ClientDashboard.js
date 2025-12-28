import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import DashboardService from "../services/dashboard.service";
import AuthService from "../services/auth.service";

const ClientDashboard = () => {
    const navigate = useNavigate();

    /* State definition - UC-4 */
    const [selectedRib, setSelectedRib] = useState("");
    const [currentPage, setCurrentPage] = useState(0);
    const [dashboardData, setDashboardData] = useState(null);
    const [loading, setLoading] = useState(true);
    const [messageError, setMessageError] = useState("");

    /**
     * UC-4 : Charger le tableau de bord
     * Affiche : solde, RIB, 10 dernières transactions, autres comptes
     */
    async function loadDashboard(rib = null, page = 0) {
        setLoading(true);
        setMessageError("");

        try {
            const result = await DashboardService.getDashboard(rib, page);
            setDashboardData(result.data);
            
            // Si c'est le premier chargement et qu'aucun RIB n'est sélectionné,
            // on sélectionne le RIB du compte principal
            if (!selectedRib && result.data.rib) {
                setSelectedRib(result.data.rib);
            }
        } catch (error) {
            console.error(error);
            const errorMessage = error.response?.data?.details?.[0] || 
                                 error.response?.data?.message || 
                                 "Erreur lors du chargement du dashboard";
            setMessageError(errorMessage);
        } finally {
            setLoading(false);
        }
    }

    /**
     * Changer de compte dans la liste déroulante
     */
    function handleAccountChange(e) {
        const newRib = e.target.value;
        setSelectedRib(newRib);
        setCurrentPage(0);
        loadDashboard(newRib, 0);
    }

    /**
     * Navigation pagination
     */
    function goToPage(page) {
        setCurrentPage(page);
        loadDashboard(selectedRib, page);
    }

    /**
     * Formater le RIB pour l'affichage
     */
    function formatRib(rib) {
        if (!rib) return "";
        return rib.match(/.{1,4}/g)?.join(" ") || rib;
    }

    /**
     * Formater la date
     */
    function formatDate(dateString) {
        if (!dateString) return "-";
        const date = new Date(dateString);
        return date.toLocaleDateString('fr-FR', {
            day: '2-digit',
            month: '2-digit',
            year: 'numeric',
            hour: '2-digit',
            minute: '2-digit'
        });
    }

    /**
     * Obtenir l'icône selon le type de transaction
     */
    function getTransactionIcon(type) {
        return type === "CREDIT" ? "⬇️" : "⬆️";
    }

    /**
     * Obtenir la classe CSS selon le type
     */
    function getTransactionClass(type) {
        return type === "CREDIT" ? "text-success" : "text-danger";
    }

    /**
     * Initialisation au chargement
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

        loadDashboard();
    }, [navigate]);

    // Loading state
    if (loading && !dashboardData) {
        return (
            <div className="container mt-5 text-center">
                <div className="spinner-border text-primary" role="status">
                    <span className="visually-hidden">Chargement...</span>
                </div>
                <p className="mt-3">Chargement de votre tableau de bord...</p>
            </div>
        );
    }

    // Error state
    if (messageError && !dashboardData) {
        return (
            <div className="container mt-5">
                <div className="alert alert-danger" role="alert">
                    <h4 className="alert-heading">❌ Erreur</h4>
                    <p>{messageError}</p>
                </div>
            </div>
        );
    }

    return (
        <div className="container mt-4">
            <h2 className="mb-4">💼 Mon Tableau de Bord</h2>

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

            {dashboardData && (
                <>
                    {/* Section 1 : Sélection du compte et solde */}
                    <div className="row mb-4">
                        {/* Carte de sélection du compte */}
                        <div className="col-md-6">
                            <div className="card shadow-sm">
                                <div className="card-header bg-primary text-white">
                                    <h5 className="mb-0">🏦 Mes Comptes</h5>
                                </div>
                                <div className="card-body">
                                    {dashboardData.autresComptes && dashboardData.autresComptes.length > 1 ? (
                                        <>
                                            <label className="form-label">Sélectionnez un compte :</label>
                                            <select
                                                className="form-select form-select-lg font-monospace"
                                                value={selectedRib}
                                                onChange={handleAccountChange}
                                            >
                                                {dashboardData.autresComptes.map((account) => (
                                                    <option key={account.id} value={account.rib}>
                                                        {formatRib(account.rib)} - {account.amount?.toFixed(2)} MAD
                                                    </option>
                                                ))}
                                            </select>
                                            <small className="form-text text-muted mt-2 d-block">
                                                Vous avez {dashboardData.autresComptes.length} compte(s) bancaire(s)
                                            </small>
                                        </>
                                    ) : (
                                        <div>
                                            <label className="form-label">Numéro de compte (RIB) :</label>
                                            <div className="font-monospace fs-5 fw-bold text-primary">
                                                {formatRib(dashboardData.rib)}
                                            </div>
                                        </div>
                                    )}
                                </div>
                            </div>
                        </div>

                        {/* Carte du solde */}
                        <div className="col-md-6">
                            <div className="card shadow-sm bg-success text-white">
                                <div className="card-header">
                                    <h5 className="mb-0">💰 Solde Actuel</h5>
                                </div>
                                <div className="card-body text-center">
                                    <h1 className="display-3 fw-bold">
                                        {dashboardData.solde?.toFixed(2)} <small className="fs-4">MAD</small>
                                    </h1>
                                    <p className="mb-0 mt-3">
                                        <small>Dernière mise à jour : {new Date().toLocaleString('fr-FR')}</small>
                                    </p>
                                </div>
                            </div>
                        </div>
                    </div>

                    {/* Section 2 : Bouton Nouveau Virement */}
                    <div className="row mb-3">
                        <div className="col-12">
                            <button
                                className="btn btn-primary btn-lg w-100"
                                onClick={() => navigate("/add_wirer_transfer")}
                            >
                                💸 Effectuer un Nouveau Virement
                            </button>
                        </div>
                    </div>

                    {/* Section 3 : Liste des 10 dernières transactions */}
                    <div className="card shadow-sm">
                        <div className="card-header bg-secondary text-white">
                            <h5 className="mb-0">📊 Dernières Opérations</h5>
                        </div>
                        <div className="card-body p-0">
                            {dashboardData.dernieresTransactions && dashboardData.dernieresTransactions.length > 0 ? (
                                <div className="table-responsive">
                                    <table className="table table-hover mb-0">
                                        <thead className="table-light">
                                            <tr>
                                                <th>Date</th>
                                                <th>Intitulé</th>
                                                <th>Type</th>
                                                <th className="text-end">Montant</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            {dashboardData.dernieresTransactions.map((transaction) => (
                                                <tr key={transaction.id}>
                                                    <td className="text-muted">
                                                        <small>{formatDate(transaction.createdAt)}</small>
                                                    </td>
                                                    <td>
                                                        <div className="d-flex align-items-center">
                                                            <span className="me-2">
                                                                {getTransactionIcon(transaction.transactionType)}
                                                            </span>
                                                            <div>
                                                                <div className="fw-bold">
                                                                    {transaction.intitule}
                                                                </div>
                                                            </div>
                                                        </div>
                                                    </td>
                                                    <td>
                                                        <span className={`badge ${
                                                            transaction.transactionType === "CREDIT" 
                                                                ? "bg-success" 
                                                                : "bg-danger"
                                                        }`}>
                                                            {transaction.transactionType === "CREDIT" 
                                                                ? "Crédit" 
                                                                : "Débit"}
                                                        </span>
                                                    </td>
                                                    <td className={`text-end fw-bold ${getTransactionClass(transaction.transactionType)}`}>
                                                        {transaction.transactionType === "CREDIT" ? "+" : "-"}
                                                        {transaction.amount?.toFixed(2)} MAD
                                                    </td>
                                                </tr>
                                            ))}
                                        </tbody>
                                    </table>
                                </div>
                            ) : (
                                <div className="text-center py-5 text-muted">
                                    <p className="mb-0">Aucune transaction trouvée</p>
                                    <small>Effectuez votre premier virement pour voir l'historique</small>
                                </div>
                            )}
                        </div>

                        {/* Pagination */}
                        {dashboardData.totalPages > 1 && (
                            <div className="card-footer">
                                <nav>
                                    <ul className="pagination justify-content-center mb-0">
                                        <li className={`page-item ${currentPage === 0 ? "disabled" : ""}`}>
                                            <button
                                                className="page-link"
                                                onClick={() => goToPage(currentPage - 1)}
                                                disabled={currentPage === 0}
                                            >
                                                ← Précédent
                                            </button>
                                        </li>

                                        {[...Array(dashboardData.totalPages)].map((_, index) => (
                                            <li
                                                key={index}
                                                className={`page-item ${currentPage === index ? "active" : ""}`}
                                            >
                                                <button
                                                    className="page-link"
                                                    onClick={() => goToPage(index)}
                                                >
                                                    {index + 1}
                                                </button>
                                            </li>
                                        ))}

                                        <li className={`page-item ${currentPage === dashboardData.totalPages - 1 ? "disabled" : ""}`}>
                                            <button
                                                className="page-link"
                                                onClick={() => goToPage(currentPage + 1)}
                                                disabled={currentPage === dashboardData.totalPages - 1}
                                            >
                                                Suivant →
                                            </button>
                                        </li>
                                    </ul>
                                </nav>
                                <div className="text-center mt-2">
                                    <small className="text-muted">
                                        Page {currentPage + 1} sur {dashboardData.totalPages} 
                                        ({dashboardData.totalTransactions} transaction(s) au total)
                                    </small>
                                </div>
                            </div>
                        )}
                    </div>
                </>
            )}
        </div>
    );
};

export default ClientDashboard;