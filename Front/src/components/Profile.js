import React, { useState, useEffect } from "react";
import AuthService from "../services/auth.service";

const Profile = () => {
    const [user, setUser] = useState(null);

    useEffect(() => {
        const currentUser = AuthService.getCurrentUser();
        if (currentUser) {
            setUser(currentUser);
        }
    }, []);

    if (!user) {
        return (
            <div className="container mt-5 text-center">
                <div className="spinner-border text-primary" role="status">
                    <span className="visually-hidden">Chargement...</span>
                </div>
            </div>
        );
    }

    return (
        <div className="container mt-5">
            <div className="row justify-content-center">
                <div className="col-md-8">
                    {/* Carte principale du profil */}
                    <div className="card shadow-lg">
                        <div className="card-header bg-primary text-white text-center py-4">
                            <div className="mb-3">
                                <div 
                                    className="rounded-circle bg-white d-inline-flex align-items-center justify-content-center"
                                    style={{ width: '80px', height: '80px' }}
                                >
                                    <span className="display-4 text-primary">
                                        {user.username.charAt(0).toUpperCase()}
                                    </span>
                                </div>
                            </div>
                            <h3 className="mb-0">{user.username}</h3>
                            <p className="mb-0 mt-2">
                                <span className="badge bg-light text-dark">
                                    {user.roles && user.roles.includes("ROLE_CLIENT") 
                                        ? "👤 Client" 
                                        : "👔 Agent Guichet"}
                                </span>
                            </p>
                        </div>

                        <div className="card-body p-4">
                            {/* Informations du compte */}
                            <h5 className="mb-3">📋 Informations du Compte</h5>
                            
                            <div className="row mb-3">
                                <div className="col-md-6">
                                    <div className="p-3 bg-light rounded">
                                        <small className="text-muted">Nom d'utilisateur</small>
                                        <div className="fw-bold">{user.username}</div>
                                    </div>
                                </div>
                                <div className="col-md-6">
                                    <div className="p-3 bg-light rounded">
                                        <small className="text-muted">Type de compte</small>
                                        <div className="fw-bold">
                                            {user.roles && user.roles.includes("ROLE_CLIENT") 
                                                ? "Compte Client" 
                                                : "Compte Agent Guichet"}
                                        </div>
                                    </div>
                                </div>
                            </div>

                            {/* Permissions */}
                            <h5 className="mb-3 mt-4">🔑 Permissions</h5>
                            <div className="row">
                                {user.roles && user.roles.map((role, index) => {
                                    // Filtrer uniquement les vrais rôles (ROLE_xxx)
                                    if (role.startsWith("ROLE_")) {
                                        return (
                                            <div key={index} className="col-md-6 mb-2">
                                                <div className="p-2 border rounded bg-white">
                                                    <span className="badge bg-success me-2">✓</span>
                                                    {role.replace("ROLE_", "").replace("_", " ")}
                                                </div>
                                            </div>
                                        );
                                    }
                                    return null;
                                })}
                            </div>

                            {/* Statistiques de session */}
                            <h5 className="mb-3 mt-4">📊 Session Actuelle</h5>
                            <div className="alert alert-info">
                                <div className="row">
                                    <div className="col-md-6">
                                        <small className="text-muted">Token JWT</small>
                                        <div className="font-monospace small">
                                            {user.jwtToken.substring(0, 20)}...
                                        </div>
                                    </div>
                                    <div className="col-md-6">
                                        <small className="text-muted">État</small>
                                        <div>
                                            <span className="badge bg-success">🟢 Actif</span>
                                        </div>
                                    </div>
                                </div>
                            </div>

                            {/* Actions rapides */}
                            <div className="mt-4 text-center">
                                <button 
                                    className="btn btn-outline-danger"
                                    onClick={() => {
                                        AuthService.logout();
                                        window.location.href = "/login";
                                    }}
                                >
                                    🚪 Se Déconnecter
                                </button>
                            </div>
                        </div>

                        <div className="card-footer text-muted text-center">
                            <small>
                                Session sécurisée par JWT • Expire dans 1 heure
                            </small>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default Profile;