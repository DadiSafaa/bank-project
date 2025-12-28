package ma.formations.multiconnector.service;

import lombok.AllArgsConstructor;
import ma.formations.multiconnector.dao.BankAccountRepository;
import ma.formations.multiconnector.dao.CustomerRepository;
import ma.formations.multiconnector.dtos.bankaccount.AddBankAccountRequest;
import ma.formations.multiconnector.dtos.bankaccount.AddBankAccountResponse;
import ma.formations.multiconnector.dtos.bankaccount.BankAccountDto;
import ma.formations.multiconnector.enums.AccountStatus;
import ma.formations.multiconnector.service.exception.BusinessException;
import ma.formations.multiconnector.service.model.BankAccount;
import ma.formations.multiconnector.service.model.Customer;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implémentation du service de gestion des comptes bancaires
 * UC-3 : Nouveau compte bancaire
 */
@Service
@Transactional
@AllArgsConstructor
public class BankAccountServiceImpl implements IBankAccountService {
    private final BankAccountRepository bankAccountRepository;
    private final CustomerRepository customerRepository;
    private final RibValidator ribValidator;
    private ModelMapper modelMapper;

    /**
     * UC-3 : Créer un nouveau compte bancaire
     * Respecte RG_8, RG_9, RG_10
     */
    @Override
    public AddBankAccountResponse saveBankAccount(AddBankAccountRequest dto) {
        BankAccount bankAccount = modelMapper.map(dto, BankAccount.class);

        // RG_9 : Le RIB doit être un RIB valide
        String validationError = ribValidator.getValidationError(bankAccount.getRib());
        if (validationError != null) {
            throw new BusinessException(validationError);
        }

        // RG_8 : Le numéro d'identité doit exister au niveau de la base de données
        Customer customerP = customerRepository.findByIdentityRef(dto.getCustomerIdentityRef())
                .orElseThrow(() -> new BusinessException(
                        String.format("No customer with the identity: %s exist", dto.getCustomerIdentityRef())));

        // RG_10 : Le compte bancaire sera créé avec le statut « Ouvert »
        bankAccount.setAccountStatus(AccountStatus.OPENED);
        bankAccount.setCustomer(customerP);
        bankAccount.setCreatedAt(new Date());

        AddBankAccountResponse response = modelMapper.map(
                bankAccountRepository.save(bankAccount),
                AddBankAccountResponse.class
        );

        response.setMessage(String.format(
                "RIB number [%s] for the customer [%s] has been successfully created",
                dto.getRib(),
                dto.getCustomerIdentityRef()
        ));

        return response;
    }

    @Override
    public List<BankAccountDto> getAllBankAccounts() {
        return bankAccountRepository.findAll().stream()
                .map(bankAccount -> modelMapper.map(bankAccount, BankAccountDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public BankAccountDto getBankAccountByRib(String rib) {
        return modelMapper.map(
                bankAccountRepository.findByRib(rib)
                        .orElseThrow(() -> new BusinessException(
                                String.format("No Bank Account with rib [%s] exist", rib))),
                BankAccountDto.class
        );
    }
}