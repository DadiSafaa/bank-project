import React from "react";
import Home from "./Home";
import Login from "./Login";
import Register from "./Register";
import Profile from "./Profile";
import CustomerComponent from "./CustomerComponent";
import BankAccount from "./BankAccount";
import WirerTransfert from "./WirerTransfert";
import ClientDashboard from "./ClientDashboard";
import { Route, Routes } from "react-router-dom";

const RoutesApplications = () => {
    return (
        <div className="container mt-3">
            <Routes>
                <Route path="/" element={<Home />} />
                <Route path="/home" element={<Home />} />
                <Route path="/login" element={<Login />} />
                <Route path="/register" element={<Register />} />
                <Route path="/profile" element={<Profile />} />
                
                {/* AGENT_GUICHET routes */}
                <Route path="/manage_customers" element={<CustomerComponent />} />
                <Route path="/manage_bankaccounts" element={<BankAccount />} />
                
                {/* CLIENT routes */}
                <Route path="/consult_account" element={<ClientDashboard />} />
                <Route path="/add_wirer_transfer" element={<WirerTransfert />} />
            </Routes>
        </div>
    );
};

export default RoutesApplications;