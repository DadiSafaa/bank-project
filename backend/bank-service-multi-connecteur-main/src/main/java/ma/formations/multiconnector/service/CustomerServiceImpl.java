package ma.formations.multiconnector.service;

import lombok.AllArgsConstructor;
import ma.formations.multiconnector.dao.CustomerRepository;
import ma.formations.multiconnector.dao.RoleRepository;
import ma.formations.multiconnector.dao.UserRepository;
import ma.formations.multiconnector.dtos.customer.*;
import ma.formations.multiconnector.service.exception.BusinessException;
import ma.formations.multiconnector.service.model.Customer;
import ma.formations.multiconnector.service.model.Role;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
public class CustomerServiceImpl implements ICustomerService {

    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final ModelMapper modelMapper;

    @Override
    public List<CustomerDto> getAllCustomers() {
        return customerRepository.findAll().stream()
                .map(customer -> modelMapper.map(customer, CustomerDto.class))
                .collect(Collectors.toList());
    }

    /**
     * UC-2 : Créer un nouveau client
     * Respecte RG_4, RG_5, RG_6, RG_7
     */
    @Override
    public AddCustomerResponse createCustomer(AddCustomerRequest addCustomerRequest) {
        String identityRef = addCustomerRequest.getIdentityRef();
        String username = addCustomerRequest.getUsername();
        String email = addCustomerRequest.getEmail();

        // RG_4 : Le numéro d'identité doit être unique
        customerRepository.findByIdentityRef(identityRef).ifPresent(a -> {
            throw new BusinessException(String.format("Customer with the same identity [%s] exist", identityRef));
        });

        // Vérifier que le username n'existe pas déjà
        customerRepository.findByUsername(username).ifPresent(a -> {
            throw new BusinessException(String.format("The username [%s] is already used", username));
        });

        // RG_6 : L'adresse email doit être unique
        userRepository.findByEmail(email).ifPresent(a -> {
            throw new BusinessException(String.format("The email [%s] is already used", email));
        });

        // Générer un mot de passe temporaire
        String temporaryPassword = generateTemporaryPassword();

        // Créer le Customer avec les nouveaux champs (RG_5)
        Customer customer = modelMapper.map(addCustomerRequest, Customer.class);
        customer.setPassword(passwordEncoder.encode(temporaryPassword));
        customer.setEnabled(true);
        customer.setAccountNonExpired(true);
        customer.setAccountNonLocked(true);
        customer.setCredentialsNonExpired(true);

        // Assigner le rôle CLIENT
        Role clientRole = roleRepository.findByAuthority("ROLE_CLIENT")
                .orElseThrow(() -> new BusinessException("Role ROLE_CLIENT not found"));
        customer.setAuthorities(List.of(clientRole));

        // Sauvegarder le customer
        Customer savedCustomer = customerRepository.save(customer);

        // RG_7 : Envoyer un email avec les credentials
        emailService.sendCredentialsEmail(email, username, temporaryPassword);

        // Préparer la réponse
        AddCustomerResponse response = modelMapper.map(savedCustomer, AddCustomerResponse.class);
        response.setMessage(String.format(
                "Customer : [identity= %s, First Name= %s, Last Name= %s, username= %s, email= %s] was created with success. An email has been sent with credentials.",
                response.getIdentityRef(),
                response.getFirstname(),
                response.getLastname(),
                response.getUsername(),
                email
        ));

        return response;
    }

    /**
     * Génère un mot de passe temporaire aléatoire
     */
    private String generateTemporaryPassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$";
        StringBuilder password = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 12; i++) {
            password.append(chars.charAt(random.nextInt(chars.length())));
        }
        return password.toString();
    }

    @Override
    public UpdateCustomerResponse updateCustomer(String identityRef, UpdateCustomerRequest updateCustomerRequest) {
        Customer customerToPersist = modelMapper.map(updateCustomerRequest, Customer.class);
        Customer customerFound = customerRepository.findAll().stream()
                .filter(bo -> bo.getIdentityRef().equals(identityRef))
                .findFirst()
                .orElseThrow(() -> new BusinessException(String.format("No Customer with identity [%s] exist !", identityRef)));

        customerToPersist.setId(customerFound.getId());
        customerToPersist.setIdentityRef(identityRef);
        UpdateCustomerResponse updateCustomerResponse = modelMapper.map(customerRepository.save(customerToPersist), UpdateCustomerResponse.class);
        updateCustomerResponse.setMessage(String.format("Customer identity %s is updated with success", identityRef));
        return updateCustomerResponse;
    }

    @Override
    public CustomerDto getCustomByIdentity(String identity) {
        return modelMapper.map(
                customerRepository.findByIdentityRef(identity)
                        .orElseThrow(() -> new BusinessException(String.format("No Customer with identity [%s] exist !", identity))),
                CustomerDto.class);
    }

    @Override
    public String deleteCustomerByIdentityRef(String identityRef) {
        if (identityRef == null || identityRef.isEmpty())
            throw new BusinessException("Enter a correct identity customer");

        Customer customerFound = customerRepository.findAll().stream()
                .filter(customer -> customer.getIdentityRef().equals(identityRef))
                .findFirst()
                .orElseThrow(() -> new BusinessException(String.format("No customer with identity %s exist in database", identityRef)));

        customerRepository.delete(customerFound);
        return String.format("Customer with identity %s is deleted with success", identityRef);
    }
}