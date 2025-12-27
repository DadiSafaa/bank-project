package ma.formations.multiconnector.dtos.bankaccount;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class AddBankAccountRequest {
    private String rib;
    private Double amount;
    private String customerIdentityRef;
}
