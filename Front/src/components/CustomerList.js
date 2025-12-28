import React from "react";
import { useState, useEffect } from "react";
import AuthService from "../services/auth.service";

const CustomerList = ({ customers, editCustomer, deleteCustomer }) => {
    const [showAgentGuichetBoard, setShowAgentGuichetBoard] = useState(false);

    useEffect(() => {
        const user = AuthService.getCurrentUser();
        if (user) {
            setShowAgentGuichetBoard(user.roles.includes("ROLE_AGENT_GUICHET"));
        }
    }, []);

    return (
        <div className="card">
            <div className="card-header bg-secondary text-white">
                <h5>Liste des Clients ({customers.length})</h5>
            </div>
            <div className="card-body p-0">
                <div className="table-responsive">
                    <table className="table table-hover table-striped mb-0">
                        <thead className="table-dark">
                            <tr>
                                <th scope="col">Nº</th>
                                <th scope="col">Nom</th>
                                <th scope="col">Prénom</th>
                                <th scope="col">Identité</th>
                                <th scope="col">Username</th>
                                <th scope="col">Email</th>
                                <th scope="col">Date Naissance</th>
                                <th scope="col">Adresse</th>
                                {showAgentGuichetBoard && <th scope="col">Actions</th>}
                            </tr>
                        </thead>
                        <tbody>
                            {customers.length === 0 ? (
                                <tr>
                                    <td colSpan="9" className="text-center text-muted py-4">
                                        Aucun client trouvé
                                    </td>
                                </tr>
                            ) : (
                                customers.map((customer) => (
                                    <tr key={customer.id}>
                                        <td>{customer.id}</td>
                                        <td>{customer.lastname}</td>
                                        <td>{customer.firstname}</td>
                                        <td>
                                            <span className="badge bg-info text-dark">
                                                {customer.identityRef}
                                            </span>
                                        </td>
                                        <td>{customer.username}</td>
                                        <td>{customer.email || <span className="text-muted">-</span>}</td>
                                        <td>
                                            {customer.dateOfBirth ? (
                                                new Date(customer.dateOfBirth).toLocaleDateString('fr-FR')
                                            ) : (
                                                <span className="text-muted">-</span>
                                            )}
                                        </td>
                                        <td>
                                            {customer.address ? (
                                                <span title={customer.address}>
                                                    {customer.address.length > 30
                                                        ? customer.address.substring(0, 30) + "..."
                                                        : customer.address}
                                                </span>
                                            ) : (
                                                <span className="text-muted">-</span>
                                            )}
                                        </td>
                                        {showAgentGuichetBoard && (
                                            <td>
                                                <div className="btn-group btn-group-sm" role="group">
                                                    <button
                                                        type="button"
                                                        className="btn btn-warning"
                                                        onClick={() => editCustomer(customer)}
                                                        title="Modifier"
                                                    >
                                                        ✏️ Éditer
                                                    </button>
                                                    <button
                                                        type="button"
                                                        className="btn btn-danger"
                                                        onClick={() => deleteCustomer(customer.identityRef)}
                                                        title="Supprimer"
                                                    >
                                                        🗑️ Supprimer
                                                    </button>
                                                </div>
                                            </td>
                                        )}
                                    </tr>
                                ))
                            )}
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    );
};

export default CustomerList;