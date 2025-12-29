import React, { useState, useEffect } from "react";
import { Link } from "react-router-dom";
import AuthService from "../services/auth.service";

const NavBar = () => {
    const [currentUser, setCurrentUser] = useState(undefined);
    const [showClientBoard, setShowClientBoard] = useState(false);
    const [showAgentGuichetBoard, setShowAgentGuichetBoard] = useState(false);

    useEffect(() => {
        const user = AuthService.getCurrentUser();
        if (user) {
            setCurrentUser(user);
            setShowClientBoard(user.roles.includes("ROLE_CLIENT"));
            setShowAgentGuichetBoard(user.roles.includes("ROLE_AGENT_GUICHET"));
        }
    }, []);

    const logOut = () => {
        AuthService.logout();
        setShowClientBoard(false);
        setShowAgentGuichetBoard(false);
        setCurrentUser(undefined);
    };

    return (
        <nav className="navbar navbar-expand navbar-dark bg-dark">
            <Link to={"/"} className="navbar-brand" style={{ marginLeft: '20px' }}>
                e-Bank
            </Link>
            <div className="navbar-nav mr-auto">
                <li className="nav-item">
                    <Link to={"/home"} className="nav-link">
                        Home
                    </Link>
                </li>

                {/* Menu AGENT_GUICHET */}
                {showAgentGuichetBoard && (
                    <>
                        <li className="nav-item">
                            <Link to={"/manage_customers"} className="nav-link">
                                Customers Management
                            </Link>
                        </li>
                        <li className="nav-item">
                            <Link to={"/manage_bankaccounts"} className="nav-link">
                                Bank Accounts Management
                            </Link>
                        </li>
                    </>
                )}

                {/* Menu CLIENT */}
                {showClientBoard && (
                    <>
                        <li className="nav-item">
                            <Link to={"/consult_account"} className="nav-link">
                                💼 Tableau de Bord
                            </Link>
                        </li>
                        <li className="nav-item">
                            <Link to={"/add_wirer_transfer"} className="nav-link">
                                💸 Nouveau Virement
                            </Link>
                        </li>
                    </>
                )}

            </div>

            {currentUser ? (
                <div className="navbar-nav ml-auto">
                    <li className="nav-item">
                        <Link to={"/profile"} className="nav-link">
                            👤 {currentUser.username}
                        </Link>
                    </li>
                    <li className="nav-item">
                        <a href="/login" className="nav-link" onClick={logOut}>
                            LogOut
                        </a>
                    </li>
                </div>
            ) : (
                <div className="navbar-nav ml-auto">
                    <li className="nav-item">
                        <Link to={"/login"} className="nav-link">
                            Login
                        </Link>
                    </li>
                    <li className="nav-item">
                        <Link to={"/register"} className="nav-link">
                            Sign Up
                        </Link>
                    </li>
                </div>
            )}
        </nav>
    );
};

export default NavBar;