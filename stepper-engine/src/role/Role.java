package role;


import java.util.List;

public class Role {
    private final String name;
    private final String description;
    private final List<String> allowedFlows; // Flow defined by name

    public Role(String name, String description, List<String> listOfFlowsParticipantInRole) {
        this.name = name;
        this.description = description;
        allowedFlows = listOfFlowsParticipantInRole;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getAllowedFlows() {
        synchronized (allowedFlows) {
            return allowedFlows;
        }
    }

    public void setFlowsList(List<String> newFlows) {
        synchronized (allowedFlows) {
            this.allowedFlows.clear();
            this.allowedFlows.addAll(newFlows);
        }
    }

    public void setAllowedFlows(List<String> allowedFlows) {
        synchronized (allowedFlows) {
            this.allowedFlows.clear();
            this.allowedFlows.addAll(allowedFlows);
        }
    }

    public void addFlowNameToRole(String name) {
        synchronized (allowedFlows) {
            allowedFlows.add(name);
        }
    }


}
