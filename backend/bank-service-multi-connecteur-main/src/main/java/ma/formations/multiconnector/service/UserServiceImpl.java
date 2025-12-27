package ma.formations.multiconnector.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import ma.formations.multiconnector.dao.PermissionRepository;
import ma.formations.multiconnector.dao.RoleRepository;
import ma.formations.multiconnector.dao.UserRepository;
import ma.formations.multiconnector.dtos.user.PermissionVo;
import ma.formations.multiconnector.dtos.user.RoleVo;
import ma.formations.multiconnector.dtos.user.UserVo;
import ma.formations.multiconnector.service.exception.BusinessException;
import ma.formations.multiconnector.service.model.Permission;
import ma.formations.multiconnector.service.model.Role;
import ma.formations.multiconnector.service.model.User;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
@Transactional
@AllArgsConstructor
public class UserServiceImpl implements IUserService, UserDetailsService {
    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private PasswordEncoder passwordEncoder;
    private ModelMapper modelMapper;
    private PermissionRepository permissionRepository;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserVo userVo = modelMapper.map(userRepository.findByUsername(username), UserVo.class);
        List<RoleVo> permissions = new ArrayList<>();
        userVo.getAuthorities().forEach(
                roleVo -> roleVo.getAuthorities().forEach(
                        permission -> permissions.add(
                                RoleVo.builder().authority(permission.getAuthority()).build())));
        userVo.getAuthorities().addAll(permissions);
        return userVo;
    }

    @Override
    public void save(UserVo userVo) {
        User user = modelMapper.map(userVo, User.class);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setAuthorities(user.getAuthorities().stream().map(bo ->
                        roleRepository.findByAuthority(bo.getAuthority()).get()).
                collect(Collectors.toList()));
        userRepository.save(user);
    }

    @Override
    public void save(RoleVo roleVo) {
        Role role = modelMapper.map(roleVo, Role.class);
        role.setAuthorities(role.getAuthorities().stream().map(bo ->
                        permissionRepository.findByAuthority(bo.getAuthority()).get()).
                collect(Collectors.toList()));
        roleRepository.save(role);
    }

    @Override
    public void save(PermissionVo vo) {
        permissionRepository.save(modelMapper.map(vo, Permission.class));
    }

    @Override
    public RoleVo getRoleByName(String authority) {
        return modelMapper.map(roleRepository.findByAuthority(authority).get(), RoleVo.class);
    }

    @Override
    public PermissionVo getPermissionByName(String authority) {
        return modelMapper.map(permissionRepository.findByAuthority(authority), PermissionVo.class);
    }
    /**
     * UC-1 : Changer mot de passe
     * L'utilisateur doit fournir son ancien mot de passe pour le changer
     */
    @Override
    public void changePassword(String username, String oldPassword, String newPassword) {
        // Récupérer l'utilisateur
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException("Utilisateur introuvable"));

        // Vérifier que l'ancien mot de passe est correct
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BusinessException("L'ancien mot de passe est incorrect");
        }

        // Crypter et sauvegarder le nouveau mot de passe (RG_1)
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }


}
