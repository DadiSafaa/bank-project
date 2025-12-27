package ma.formations.multiconnector.dao;

import ma.formations.multiconnector.service.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    // NOUVELLE MÉTHODE pour RG_6 : vérifier l'unicité de l'email
    Optional<User> findByEmail(String email);
}
