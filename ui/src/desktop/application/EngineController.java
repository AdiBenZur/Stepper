package desktop.application;

import manager.system.Manager;
import manager.system.ManagerImpl;

public class EngineController {

    // Singleton class

    private static Manager manager;

    public EngineController() {
        manager = new ManagerImpl();
    }

    public static Manager getInstance() {
        return manager;
    }
}
