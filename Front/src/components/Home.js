import React from "react";
import { Link } from "react-router-dom";
const Home = () => {
    return (
        <div>
            <header className="header">
                <h1>Bienvenue sur e-Bank</h1>
                <p>Votre portail bancaire innovant pour une gestion financière simplifiée.</p>
            </header>

            <main className="main-content">
                <section className="section">
                    <h2>Services Bancaires Simplifiés</h2>
                    <p>Accédez à un large éventail de services bancaires à partir de votre domicile.</p>
                    <div className="services-list">
                        <div className="service">
                            <h3>Consultation de Compte</h3>
                            <p>Vérifiez vos soldes et transactions récentes en temps réel.</p>
                        </div>
                        <div className="service">
                            <h3>Transferts d'Argent</h3>
                            <p>Envoyez et recevez de l'argent instantanément à travers le monde.</p>
                        </div>
                        <div className="service">
                            <h3>Gestion des Investissements</h3>
                            <p>Investissez judicieusement avec nos outils de gestion de portefeuille.</p>
                        </div>
                        {/* Ajoutez d'autres services si nécessaire */}
                    </div>
                </section>

                <section className="section">
                    <h2>Témoignages Clients</h2>
                    <p>Ce que nos clients disent de nous.</p>
                    <div className="testimonials">
                        <blockquote>
                            "e-Bank a révolutionné ma façon de gérer mes finances. Facile à utiliser et très sécurisé."
                        </blockquote>
                        <blockquote>
                            "Le service clientèle est exceptionnel, toujours disponible pour aider en cas de besoin."
                        </blockquote>
                        {/* Ajoutez d'autres témoignages si nécessaire */}
                    </div>
                </section>

                <section className="section call-to-action">
                    <h2>Rejoignez e-Bank Aujourd'hui</h2>
                    <p>Commencez votre voyage avec e-Bank et découvrez une banque sans limites.</p>
                    <button className="btn btn-primary">
                    <Link to={"/register"} className="nav-link">
                    Inscrivez-vous Maintenant
                            </Link>
                        </button>
                    <button className="btn btn-secondary">
                    <Link to={"/login"} className="nav-link">
                    Connexion
                            </Link>
                        </button>
                </section>
            </main>

            <footer className="footer">
                <p>&copy; {new Date().getFullYear()} e-Bank. Tous droits réservés.</p>
            </footer>
        </div>
    );
};

export default Home;
