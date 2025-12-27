package ma.formations.multiconnector.service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Role {
    @Id
    @GeneratedValue
    private int id;
    @Column(unique = true)
    private String authority;
    @ManyToMany(cascade = CascadeType.ALL)
    private List<Permission> authorities = new ArrayList<>();

}
