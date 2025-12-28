package ma.formations.multiconnector.dao;

import ma.formations.multiconnector.service.model.BankAccount;
import ma.formations.multiconnector.service.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BankAccountRepository extends JpaRepository<BankAccount, Long> {
    Optional<BankAccount> findByRib(String rib);

    // 🆕 NOUVELLE MÉTHODE pour UC-4 : Récupérer tous les comptes d'un client
    List<BankAccount> findByCustomer(Customer customer);

    // 🆕 NOUVELLE MÉTHODE pour UC-4 : Récupérer le compte le plus récemment mouvementé
    Optional<BankAccount> findFirstByCustomerOrderByIdDesc(Customer customer);
}