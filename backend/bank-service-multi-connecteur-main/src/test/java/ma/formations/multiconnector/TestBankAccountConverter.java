package ma.formations.multiconnector;

import ma.formations.multiconnector.dtos.bankaccount.AddBankAccountRequest;
import ma.formations.multiconnector.service.model.BankAccount;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class TestBankAccountConverter {
    @Autowired
    private ModelMapper modelMapper;

    /**
     * for testing the Mapper
     */
    @Test
    void test1() {
        AddBankAccountRequest dto = AddBankAccountRequest.builder().
                customerIdentityRef("FA66962").
                rib("RIB_13").
                amount(15000.0).
                build();
        BankAccount bo = modelMapper.map(dto, BankAccount.class);
        assertThat(bo.getCustomer()).isNotNull();
        assertThat(bo.getCustomer().getIdentityRef()).isEqualTo(dto.getCustomerIdentityRef());
    }

}

