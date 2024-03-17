package user;

import java.util.*;

public class UsersManager {

    // Manager all the users that register to the system.

    private final Map<String, User> usersInSystem; // Map from session id to user
    private boolean isAdminLogin = false;

    public UsersManager() {
        usersInSystem = new HashMap<>();
    }


    public boolean isAdminLogin() {
        return isAdminLogin;
    }

    public void setAdminLogin(boolean adminLogin) {
        isAdminLogin = adminLogin;
    }

    public synchronized void addUser(String session, User user) {
        usersInSystem.put(session, user);
    }

    public synchronized void removeUser(String username) {
        usersInSystem.remove(username);
    }

    public synchronized Map<String, User> getUserMap() {
        return usersInSystem;
    }

    public synchronized boolean isUserExists(String username) {
        for(User user : usersInSystem.values()) {
            if(user.getUserName().equals(username) && user.getIsOnline())
                return true;
        }
        return false;
    }

    public synchronized boolean isUserExistsByName(String username) {
        for(User user : usersInSystem.values()) {
            if(user.getUserName().equals(username))
                return true;
        }
        return false;
    }

    public synchronized boolean updateUserInMap(User userToChange) {
        for(User user : usersInSystem.values()) {
            if(user.getUserName().equals(userToChange.getUserName())) {
                user.setRoleList(userToChange.getUserRoles());
                user.setManager(userToChange.isManager());
                user.setOnline(userToChange.getIsOnline());
                return true;
            }
        }
        return false;
    }

    public synchronized void loginEnteredToSystemAgain(String session, String username) {
        User userToUpdate = null;
        String oldKey = null;
        for (Map.Entry<String, User> entry : usersInSystem.entrySet()) {
            String key = entry.getKey();
            User user = entry.getValue();

            if (user.getUserName().equals(username)) {
                userToUpdate = user;
                oldKey = key;
                break; // Stop searching after finding the user with the target name
            }
        }

        // If the user is found, remove the old entry and insert the user with the new key
        if (userToUpdate != null) {
            userToUpdate.setOnline(true);
            usersInSystem.remove(oldKey);
            usersInSystem.put(session, userToUpdate);
        }
    }

    public synchronized boolean addUserExecutions(String username, String uuid) {
        for(User user : usersInSystem.values()) {
            if(user.getUserName().equals(username)) {
                user.addExecution(uuid);
                return true;
            }
        }
        return false;
    }
}
