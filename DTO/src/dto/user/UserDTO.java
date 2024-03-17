package dto.user;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UserDTO {
    private final String username;
    private final boolean isManager;
    private final List<String> roles;
    private final boolean isOnline;
    private final List<String> executions;

    public UserDTO(String username, boolean isManager, List<String> roles, boolean isOnline, List<String> userExecutions) {
        this.username = username;
        this.isManager = isManager;
        this.roles = roles;
        this.isOnline = isOnline;

        executions = userExecutions;

    }

    public String getUsername() {
        return username;
    }

    public boolean isManager() {
        return isManager;
    }

    public List<String> getRoles() {
        return roles;
    }

    public boolean isOnline() {
        return isOnline;
    }
}
