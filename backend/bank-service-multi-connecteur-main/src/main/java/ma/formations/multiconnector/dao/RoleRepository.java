package ma.formations.multiconnector.dao;
import ma.formations.multiconnector.service.model.Role;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Integer>{
    Optional<Role> findByAuthority(String authority);


}
