package ma.formations.multiconnector;

import ma.formations.multiconnector.dtos.bankaccount.AddBankAccountRequest;
import ma.formations.multiconnector.dtos.customer.AddCustomerRequest;
import ma.formations.multiconnector.dtos.transaction.AddWirerTransferRequest;
import ma.formations.multiconnector.dtos.user.PermissionVo;
import ma.formations.multiconnector.dtos.user.RoleVo;
import ma.formations.multiconnector.dtos.user.UserVo;
import ma.formations.multiconnector.enums.Permisssions;
import ma.formations.multiconnector.enums.Roles;
import ma.formations.multiconnector.service.IBankAccountService;
import ma.formations.multiconnector.service.ICustomerService;
import ma.formations.multiconnector.service.ITransactionService;
import ma.formations.multiconnector.service.IUserService;
import net.devh.boot.grpc.server.security.authentication.BasicGrpcAuthenticationReader;
import net.devh.boot.grpc.server.security.authentication.GrpcAuthenticationReader;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@SpringBootApplication
public class BankServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(BankServiceApplication.class, args);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public GrpcAuthenticationReader grpcAuthenticationReader() {
        return new BasicGrpcAuthenticationReader();
    }

    @Bean
    CommandLineRunner initDataBase(ICustomerService customerService,
                                   IBankAccountService bankAccountService,
                                   ITransactionService transactionService,
                                   IUserService userService) {

        return args -> {

            // ============================================
            // ÉTAPE 1 : CRÉER LES PERMISSIONS D'ABORD
            // ============================================
            System.out.println("📝 Création des permissions...");
            Arrays.stream(Permisssions.values()).toList().forEach(permission ->
                    userService.save(PermissionVo.builder().authority(permission.name()).build()));
            System.out.println("✅ Permissions créées");

            // ============================================
            // ÉTAPE 2 : CRÉER LES RÔLES
            // ============================================
            System.out.println("📝 Création des rôles...");

            // Agent guichet pour toutes les opérations CRUD
            RoleVo roleAgentGuichet = RoleVo.builder()
                    .authority(Roles.ROLE_AGENT_GUICHET.name())
                    .authorities(List.of(
                            userService.getPermissionByName(Permisssions.GET_ALL_CUSTUMERS.name()),
                            userService.getPermissionByName(Permisssions.GET_CUSTOMER_BY_IDENTITY.name()),
                            userService.getPermissionByName(Permisssions.CREATE_CUSTOMER.name()),
                            userService.getPermissionByName(Permisssions.UPDATE_CUSTOMER.name()),
                            userService.getPermissionByName(Permisssions.DELETE_CUSTOMER.name()),
                            userService.getPermissionByName(Permisssions.GET_ALL_BANK_ACCOUNT.name()),
                            userService.getPermissionByName(Permisssions.GET_BANK_ACCOUNT_BY_RIB.name()),
                            userService.getPermissionByName(Permisssions.CREATE_BANK_ACCOUNT.name())
                    ))
                    .build();

            // Agent guichet pour lecture seule
            RoleVo roleAgentGuichetGet = RoleVo.builder()
                    .authority(Roles.ROLE_AGENT_GUICHET_GET.name())
                    .authorities(List.of(
                            userService.getPermissionByName(Permisssions.GET_ALL_CUSTUMERS.name()),
                            userService.getPermissionByName(Permisssions.GET_CUSTOMER_BY_IDENTITY.name()),
                            userService.getPermissionByName(Permisssions.GET_ALL_BANK_ACCOUNT.name()),
                            userService.getPermissionByName(Permisssions.GET_BANK_ACCOUNT_BY_RIB.name())
                    ))
                    .build();

            // Rôle Client
            RoleVo roleClient = RoleVo.builder()
                    .authority(Roles.ROLE_CLIENT.name())
                    .authorities(List.of(
                            userService.getPermissionByName(Permisssions.GET_CUSTOMER_BY_IDENTITY.name()),
                            userService.getPermissionByName(Permisssions.GET_BANK_ACCOUNT_BY_RIB.name()),
                            userService.getPermissionByName(Permisssions.ADD_WIRED_TRANSFER.name()),
                            userService.getPermissionByName(Permisssions.GET_TRANSACTIONS.name())
                    ))
                    .build();

            userService.save(roleAgentGuichet);
            userService.save(roleAgentGuichetGet);
            userService.save(roleClient);
            System.out.println("✅ Rôles créés");

            // ============================================
            // ÉTAPE 3 : CRÉER LES USERS (AGENTS)
            // ============================================
            System.out.println("📝 Création des utilisateurs...");

            UserVo agentguichet = UserVo.builder()
                    .username("agentguichet")
                    .password("agentguichet")
                    .email("agent@ebank.ma")
                    .accountNonExpired(true)
                    .accountNonLocked(true)
                    .enabled(true)
                    .credentialsNonExpired(true)
                    .authorities(List.of(roleAgentGuichet))
                    .build();

            UserVo agentguichet2 = UserVo.builder()
                    .username("agentguichet2")
                    .password("agentguichet2")
                    .email("agent2@ebank.ma")
                    .accountNonExpired(true)
                    .accountNonLocked(true)
                    .enabled(true)
                    .credentialsNonExpired(true)
                    .authorities(List.of(roleAgentGuichetGet))
                    .build();

            UserVo client = UserVo.builder()
                    .username("client")
                    .password("client")
                    .email("client@example.com")
                    .accountNonExpired(true)
                    .accountNonLocked(true)
                    .enabled(true)
                    .credentialsNonExpired(true)
                    .authorities(List.of(roleClient))
                    .build();

            UserVo superadmin = UserVo.builder()
                    .username("superadmin")
                    .password("superadmin")
                    .email("admin@ebank.ma")
                    .accountNonExpired(true)
                    .accountNonLocked(true)
                    .enabled(true)
                    .credentialsNonExpired(true)
                    .authorities(List.of(roleClient, roleAgentGuichet))
                    .build();

            userService.save(agentguichet);
            userService.save(agentguichet2);
            userService.save(client);
            userService.save(superadmin);
            System.out.println("✅ Utilisateurs créés");

            // ============================================
            // ÉTAPE 4 : CRÉER LES CUSTOMERS (UC-2)
            // AVEC LES NOUVEAUX CHAMPS OBLIGATOIRES
            // ============================================
            System.out.println("📝 Création des customers...");

            customerService.createCustomer(AddCustomerRequest.builder()
                    .username("user1")
                    .identityRef("A100")
                    .firstname("FIRST_NAME1")
                    .lastname("LAST_NAME1")
                    .dateAnniversaire(new Date())
                    .email("user1@example.com")
                    .adressePostale("123 Rue Mohammed V, Rabat")
                    .build());

            customerService.createCustomer(AddCustomerRequest.builder()
                    .username("user2")
                    .identityRef("A200")
                    .firstname("FIRST_NAME2")
                    .lastname("LAST_NAME2")
                    .dateAnniversaire(new Date())
                    .email("user2@example.com")
                    .adressePostale("456 Avenue Hassan II, Casablanca")
                    .build());

            customerService.createCustomer(AddCustomerRequest.builder()
                    .username("user3")
                    .identityRef("A900")
                    .firstname("FIRST_NAME9")
                    .lastname("LAST_NAME9")
                    .dateAnniversaire(new Date())
                    .email("user3@example.com")
                    .adressePostale("789 Boulevard Zerktouni, Marrakech")
                    .build());

            customerService.createCustomer(AddCustomerRequest.builder()
                    .username("user4")
                    .identityRef("A800")
                    .firstname("FIRST_NAME8")
                    .lastname("LAST_NAME8")
                    .dateAnniversaire(new Date())
                    .email("user4@example.com")
                    .adressePostale("321 Rue de Fès, Tangier")
                    .build());

            System.out.println("✅ Customers créés (emails envoyés dans les logs)");

            // ============================================
            // ÉTAPE 5 : CRÉER LES COMPTES BANCAIRES
            // ============================================
            System.out.println("📝 Création des comptes bancaires...");

            bankAccountService.saveBankAccount(AddBankAccountRequest.builder()
                    .rib("RIB_1")
                    .amount(1000000d)
                    .customerIdentityRef("A100")
                    .build());

            bankAccountService.saveBankAccount(AddBankAccountRequest.builder()
                    .rib("RIB_11")
                    .amount(2000000d)
                    .customerIdentityRef("A100")
                    .build());

            bankAccountService.saveBankAccount(AddBankAccountRequest.builder()
                    .rib("RIB_2")
                    .amount(2000000d)
                    .customerIdentityRef("A200")
                    .build());

            bankAccountService.saveBankAccount(AddBankAccountRequest.builder()
                    .rib("RIB_9")
                    .amount(-25000d)
                    .customerIdentityRef("A900")
                    .build());

            bankAccountService.saveBankAccount(AddBankAccountRequest.builder()
                    .rib("RIB_8")
                    .amount(0.0)
                    .customerIdentityRef("A800")
                    .build());

            System.out.println("✅ Comptes bancaires créés");

            // ============================================
            // ÉTAPE 6 : EFFECTUER DES VIREMENTS
            // ============================================
            System.out.println("📝 Exécution des virements...");

            transactionService.wiredTransfer(AddWirerTransferRequest.builder()
                    .ribFrom("RIB_1")
                    .ribTo("RIB_2")
                    .amount(10000.0)
                    .username("user1")
                    .build());

            transactionService.wiredTransfer(AddWirerTransferRequest.builder()
                    .ribFrom("RIB_1")
                    .ribTo("RIB_9")
                    .amount(20000.0)
                    .username("user1")
                    .build());

            transactionService.wiredTransfer(AddWirerTransferRequest.builder()
                    .ribFrom("RIB_1")
                    .ribTo("RIB_8")
                    .amount(500.0)
                    .username("user1")
                    .build());

            transactionService.wiredTransfer(AddWirerTransferRequest.builder()
                    .ribFrom("RIB_2")
                    .ribTo("RIB_11")
                    .amount(300.0)
                    .username("user2")
                    .build());

            System.out.println("✅ Virements effectués");

            System.out.println("\n🎉 INITIALISATION TERMINÉE AVEC SUCCÈS !");
            System.out.println("================================================");
            System.out.println("Comptes de test disponibles:");
            System.out.println("- Agent: agentguichet / agentguichet");
            System.out.println("- Client: client / client");
            System.out.println("- SuperAdmin: superadmin / superadmin");
            System.out.println("================================================\n");
        };
    }
}