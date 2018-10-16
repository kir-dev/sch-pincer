package hu.gerviba.webschop.dao;

import hu.gerviba.webschop.model.UserEntity;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@ToString
@EqualsAndHashCode
public class RoleEntryDao {

    @Getter
    private final String uidHash;
    @Getter
    private final String name;
    @Getter
    private final boolean sysadmin;
    @Getter
    private String permissions; 
    
    public RoleEntryDao(String uidHashed, UserEntity ue) {
        this.uidHash = uidHashed;
        this.name = ue.getName();
        this.sysadmin = ue.isSysadmin();
        this.permissions = (ue.getPermissions() == null || ue.getPermissions().size() == 0) 
                ? "-" : String.join(", ", ue.getPermissions());
    }

}
