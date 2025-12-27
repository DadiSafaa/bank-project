package ma.formations.multiconnector.dtos.user;
import org.springframework.security.core.GrantedAuthority;

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
public class RoleVo implements GrantedAuthority{
    private int id;
    private String authority;
    private List<PermissionVo> authorities = new ArrayList<>();

}
