package user;

import role.Role;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class User {
    private final String userName;
    private boolean isManager;
    private List<Role> userRoles;
    private boolean isOnline;
    private List<String> userExecutions;



    public User(String userName) {
        this.userName = userName;
        this.isManager = false;
        this.isOnline = true;
        userRoles = new ArrayList<>();
        userExecutions = new ArrayList<>();
    }

    public void setRoleList(List<Role> newRoles) {
        synchronized (userRoles) {
            this.userRoles.clear();
            this.userRoles.addAll(newRoles);
        }
    }

    public void replaceRole(Role role) {
        synchronized (userRoles) {
            this.userRoles.replaceAll(roleInList -> roleInList.getName().equals(role.getName()) ? role : roleInList);
        }
    }

    public synchronized List<String> getUserExecutions() {
        return userExecutions;
    }

    public synchronized void setOnline(boolean online) {
        isOnline = online;
    }

    public synchronized boolean getIsOnline() {
        return isOnline;
    }

    public synchronized void setManager(boolean manager) {
        isManager = manager;
    }

    public synchronized String getUserName() {
        return userName;
    }

    public synchronized boolean isManager() {
        return isManager;
    }

    public synchronized List<Role> getUserRoles() {
        return userRoles;
    }

    public synchronized void addExecution(String uuid) {
        userExecutions.add(uuid);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(userName);
        if(isOnline)
            stringBuilder.append(" (Online)");
        else
            stringBuilder.append(" (Offline)");

        return stringBuilder.toString();
    }
}
