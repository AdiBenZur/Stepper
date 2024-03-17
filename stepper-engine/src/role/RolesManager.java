package role;

import java.util.ArrayList;
import java.util.List;

public class RolesManager {
    private List<Role> allRoles;

    public RolesManager() {
        allRoles = new ArrayList<>();
    }

    public void addRole(Role role) {
        synchronized (allRoles) {
            allRoles.add(role);
        }
    }


    public List<Role> getDefinedRoles() {
        synchronized (allRoles) {
            return allRoles;
        }
    }

    public void addFlowNameToAllFlowsRole(String name) {
        synchronized (allRoles) {
            Role allFlows = allRoles.get(1);
            allFlows.addFlowNameToRole(name);
        }
    }

    public void addFlowNameToFictiveManagerRole(String name) {
        synchronized (allRoles) {
            Role managerFictiveRole = allRoles.get(2);
            managerFictiveRole.addFlowNameToRole(name);
        }
    }

    public void addFlowNameToReadOnlyRole(String name) {
        synchronized (allRoles) {
            Role allFlows = allRoles.get(0);
            allFlows.addFlowNameToRole(name);
        }
    }

    public Role findRoleByName(String name) {
        synchronized (allRoles) {
            for (Role role : allRoles) {
                if (role.getName().equals(name))
                    return role;
            }
        }
        return null;
    }

    public boolean isRoleExist(String name) {
        synchronized (allRoles) {
            for (Role role : allRoles) {
                if (role.getName().equals(name))
                    return true;
            }
        }
        return false;
    }



}
