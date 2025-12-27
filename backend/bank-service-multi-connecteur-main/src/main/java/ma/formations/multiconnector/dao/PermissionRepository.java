package ma.formations.multiconnector.dao;

import ma.formations.multiconnector.service.model.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PermissionRepository extends JpaRepository<Permission, Integer> {
    Optional<Permission> findByAuthority(String authority);
}
