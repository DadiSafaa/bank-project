import React, { useState, useEffect } from "react";
import CustomerList from "./CustomerList";
import CustomersService from "../services/customers.service";
import AuthService from "../services/auth.service";

const CustomerComponent = () => {
    /* State definition - UC-2 avec TOUS les champs requis */
    const [id, setId] = useState("");
    const [identityRef, setIdentityRef] = useState("");
    const [firstname, setFirstname] = useState("");
    const [lastname, setLastname] = useState("");
    const [username, setUsername] = useState("");
    const [email, setEmail] = useState("");
    const [dateOfBirth, setDateOfBirth] = useState("");
    const [address, setAddress] = useState("");
    
    const [messageInfo, setMessageInfo] = useState("");
    const [messageError, setMessageError] = useState("");
    const [customers, setCustomers] = useState([]);
    const [showAgentGuichetBoard, setShowAgentGuichetBoard] = useState(false);

    /**
     * UC-2 : Sauvegarder un nouveau client (CREATE) ou modifier (UPDATE)
     * Respecte RG_4, RG_5, RG_6, RG_7
     */
    async function save(event) {
        event.preventDefault();
        setMessageError("");
        setMessageInfo("");

        // Validation côté client (RG_5 : champs obligatoires)
        if (!identityRef || !firstname || !lastname || !username || !email || !dateOfBirth || !address) {
            setMessageError("Tous les champs sont obligatoires (RG_5)");
            return;
        }

        // Validation format email
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (!emailRegex.test(email)) {
            setMessageError("Format d'email invalide");
            return;
        }

        try {
            if (id) {
                // Mode UPDATE
                await CustomersService.updateCustomer(identityRef, {
                    firstname,
                    lastname,
                    username,
                    email,
                    dateOfBirth,
                    address,
                });
                setMessageInfo("Client modifié avec succès");
            } else {
                // Mode CREATE (UC-2)
                const result = await CustomersService.createCustomer({
                    identityRef,
                    firstname,
                    lastname,
                    username,
                    email,
                    dateOfBirth,
                    address,
                });
                
                // RG_7 : Message de confirmation avec email envoyé
                setMessageInfo(
                    `✅ Client créé avec succès ! Un email avec les identifiants a été envoyé à ${email}`
                );
            }

            // Reset du formulaire
            resetForm();
            await loadCustomers();
        } catch (error) {
            console.error(error);
            // Afficher les erreurs backend (RG_4 : identité unique, RG_6 : email unique)
            const errorMessage = error.response?.data?.details?.[0] || 
                                 error.response?.data?.message || 
                                 "Erreur lors de la sauvegarde";
            setMessageError(errorMessage);
        }
    }

    /**
     * Réinitialiser le formulaire
     */
    function resetForm() {
        setId("");
        setFirstname("");
        setLastname("");
        setIdentityRef("");
        setUsername("");
        setEmail("");
        setDateOfBirth("");
        setAddress("");
    }

    /**
     * Éditer un client existant
     */
    async function editCustomer(customer) {
        setFirstname(customer.firstname);
        setLastname(customer.lastname);
        setIdentityRef(customer.identityRef);
        setUsername(customer.username);
        setEmail(customer.email || "");
        setDateOfBirth(customer.dateOfBirth || "");
        setAddress(customer.address || "");
        setId(customer.id);
        setMessageError("");
        setMessageInfo("");
    }

    /**
     * Supprimer un client
     */
    async function deleteCustomer(identityRef) {
        setMessageError("");
        setMessageInfo("");
        
        if (!window.confirm(`Êtes-vous sûr de vouloir supprimer le client ${identityRef} ?`)) {
            return;
        }

        try {
            const result = await CustomersService.deleteCustomer(identityRef);
            setMessageInfo(result.data);
            await loadCustomers();
        } catch (error) {
            console.error(error);
            const errorMessage = error.response?.data?.message || "Erreur lors de la suppression";
            setMessageError(errorMessage);
        }
    }

    /**
     * Charger la liste des clients
     */
    async function loadCustomers() {
        try {
            const result = await CustomersService.getCustomers();
            setCustomers(result.data);
        } catch (error) {
            console.error(error);
            const errorMessage = error.response?.data?.details || 
                                 error.response?.data?.message || 
                                 "Erreur lors du chargement des clients";
            setMessageError(errorMessage);
        }
    }

    /**
     * Initialisation au chargement du composant
     */
    useEffect(() => {
        const user = AuthService.getCurrentUser();
        if (user) {
            setShowAgentGuichetBoard(user.roles.includes("ROLE_AGENT_GUICHET"));
        }
        if (user) {
            loadCustomers();
        }
    }, []);

    return (
        <div className="container mt-4">
            <h2 className="mb-4">Gestion des Clients</h2>

            {/* Messages de succès et d'erreur */}
            <div className="container">
                {messageError && (
                    <div className="alert alert-danger alert-dismissible fade show" role="alert">
                        <strong>❌ Erreur :</strong> {messageError}
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

            {/* Formulaire UC-2 : Ajouter nouveau client (visible uniquement pour AGENT_GUICHET) */}
            {showAgentGuichetBoard && (
                <div className="card mb-4">
                    <div className="card-header bg-primary text-white">
                        <h5>{id ? "Modifier un client" : "Ajouter un nouveau client"}</h5>
                    </div>
                    <div className="card-body">
                        <form onSubmit={save}>
                            <input hidden type="text" value={id} onChange={(e) => setId(e.target.value)} />
                            
                            <div className="row mb-3">
                                <div className="col-md-6">
                                    <label className="form-label">Nom *</label>
                                    <input
                                        type="text"
                                        className="form-control"
                                        value={lastname}
                                        onChange={(e) => setLastname(e.target.value)}
                                        placeholder="Nom de famille"
                                        required
                                    />
                                </div>
                                <div className="col-md-6">
                                    <label className="form-label">Prénom *</label>
                                    <input
                                        type="text"
                                        className="form-control"
                                        value={firstname}
                                        onChange={(e) => setFirstname(e.target.value)}
                                        placeholder="Prénom"
                                        required
                                    />
                                </div>
                            </div>

                            <div className="row mb-3">
                                <div className="col-md-6">
                                    <label className="form-label">Numéro d'identité * (RG_4 : unique)</label>
                                    <input
                                        type="text"
                                        className="form-control"
                                        value={identityRef}
                                        onChange={(e) => setIdentityRef(e.target.value)}
                                        placeholder="Ex: AB123456"
                                        disabled={!!id}
                                        required
                                    />
                                    <small className="form-text text-muted">
                                        Le numéro d'identité ne peut pas être modifié
                                    </small>
                                </div>
                                <div className="col-md-6">
                                    <label className="form-label">Date de naissance * (RG_5)</label>
                                    <input
                                        type="date"
                                        className="form-control"
                                        value={dateOfBirth}
                                        onChange={(e) => setDateOfBirth(e.target.value)}
                                        required
                                    />
                                </div>
                            </div>

                            <div className="row mb-3">
                                <div className="col-md-6">
                                    <label className="form-label">Username (Login) *</label>
                                    <input
                                        type="text"
                                        className="form-control"
                                        value={username}
                                        onChange={(e) => setUsername(e.target.value)}
                                        placeholder="Login pour se connecter"
                                        required
                                    />
                                </div>
                                <div className="col-md-6">
                                    <label className="form-label">Email * (RG_6 : unique)</label>
                                    <input
                                        type="email"
                                        className="form-control"
                                        value={email}
                                        onChange={(e) => setEmail(e.target.value)}
                                        placeholder="exemple@email.com"
                                        required
                                    />
                                    <small className="form-text text-muted">
                                        RG_7 : Un email sera envoyé avec les identifiants
                                    </small>
                                </div>
                            </div>

                            <div className="row mb-3">
                                <div className="col-md-12">
                                    <label className="form-label">Adresse postale * (RG_5)</label>
                                    <textarea
                                        className="form-control"
                                        value={address}
                                        onChange={(e) => setAddress(e.target.value)}
                                        placeholder="Adresse complète"
                                        rows="2"
                                        required
                                    ></textarea>
                                </div>
                            </div>

                            <div className="d-flex gap-2">
                                <button type="submit" className="btn btn-primary">
                                    {id ? "Modifier" : "Créer le client"}
                                </button>
                                {id && (
                                    <button 
                                        type="button" 
                                        className="btn btn-secondary" 
                                        onClick={resetForm}
                                    >
                                        Annuler
                                    </button>
                                )}
                            </div>
                        </form>
                    </div>
                </div>
            )}

            {/* Liste des clients */}
            <CustomerList
                customers={customers}
                editCustomer={editCustomer}
                deleteCustomer={deleteCustomer}
            />
        </div>
    );
};

export default CustomerComponent;