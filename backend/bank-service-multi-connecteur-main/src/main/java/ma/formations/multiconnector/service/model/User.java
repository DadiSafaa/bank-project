package ma.formations.multiconnector.service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Inheritance(strategy = InheritanceType.JOINED)
public class User {
    @Id
    @GeneratedValue
    protected Long id;
    protected String username;
    protected String firstname;
    protected String lastname;
    protected String password;
    @OneToMany(mappedBy = "user")
    private List<BankAccountTransaction> bankAccountTransactionList;
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "USER_ROLE")

    private List<Role> authorities = new ArrayList<Role>();
    private boolean enabled;
    private boolean accountNonExpired;
    private boolean credentialsNonExpired;
    private boolean accountNonLocked;
    private String email;

    public User(String username) {
        this.username = username;

    }

}
