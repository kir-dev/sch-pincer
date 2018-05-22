package hu.gerviba.webschop.dao;

import hu.gerviba.webschop.model.UserEntity;

public class RoleEntryDao {

    private final String uidHash;
    private final String name;
    private final boolean sysadmin;
    private String permissions; 
    
    public RoleEntryDao(String uidHashed, UserEntity ue) {
        this.uidHash = uidHashed;
        this.name = ue.getName();
        this.sysadmin = ue.isSysadmin();
        this.permissions = (ue.getPermissions() == null || ue.getPermissions().size() == 0) 
                ? "-" : String.join(", ", ue.getPermissions());
    }

    public String getUidHash() {
        return uidHash;
    }

    public String getName() {
        return name;
    }

    public boolean isSysadmin() {
        return sysadmin;
    }

    public String getPermissions() {
        return permissions;
    }
    
}
