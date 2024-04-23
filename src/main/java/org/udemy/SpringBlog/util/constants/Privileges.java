package org.udemy.SpringBlog.util.constants;

public enum Privileges {
    RESET_ANY_USER_PASSWORD(1l, "RESET_ANY_USER_PASSWORD"),
    ACCESS_ADMIN_PANEL(2l, "ACCESS_ADMIN_PANEL");

    private Long ID;
    private String privilege;

    private Privileges(Long ID, String privilege) {
        this.ID = ID;
        this.privilege = privilege;
    }
    public Long getID() {
        return ID;
    }
    public String getPrivilege() {
        return privilege;  
    }  
    
}
