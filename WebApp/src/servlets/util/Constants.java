package servlets.util;

public class Constants {
    public static final String USERNAME = "username";
    public static final String MANDATORY = "MANDATORY";
    public static final String OPTIONAL = "OPTIONAL";
    public static final String OUTPUT = "NA";
    public static final String FLOW_INFORMATION_NAME = "information";
    public static final String MANAGER_FICTIVE_ROLE_NAME = "Manager fictive role";
    public static final String FLOW_NAME_TO_EXECUTE = "flow name to execute";
    public static final String UUID = "flow uuid";
    public static final String FLOW_NAME = "flow name";

    // Paths
    public final static String BASE_DOMAIN = "localhost";
    private final static String BASE_URL = "http://" + BASE_DOMAIN + ":8080";
    private final static String FULL_SERVER_PATH = BASE_URL + "/WebApp_Web" ;

    // Servlets path
    public static final String LOGIN = FULL_SERVER_PATH + "/users/login";
    public static final String UPLOAD_FILE = FULL_SERVER_PATH + "/admin/upload/file";
    public static final String USERS_LIST = FULL_SERVER_PATH + "/users/list";
    public static final String USERS = FULL_SERVER_PATH + "/users";
    public static final String ROLES = FULL_SERVER_PATH + "/roles";
    public static final String FLOWS = FULL_SERVER_PATH + "/flows";
    public static final String FLOW_INFORMATION = FULL_SERVER_PATH + "/flow/information";
    public static final String ALLOWED_FLOWS = FULL_SERVER_PATH + "/users/allowed/flows";
    public static final String FREE_INPUTS = FULL_SERVER_PATH + "/collect/free/inputs";
    public static final String USER_LAST_UPDATE = FULL_SERVER_PATH + "/get/user/last/update";
    public static final String FLOW_EXECUTION = FULL_SERVER_PATH + "/flow/execution";
    public static final String GET_FLOW_EXECUTION_AFTER_EXECUTION = FULL_SERVER_PATH + "/flow/execution/after";
    public static final String RUN_FLOW = FULL_SERVER_PATH + "/run/flow";
    public static final String ALL_EXECUTIONS = FULL_SERVER_PATH + "/all/executions";
    public static final String CONTINUATION = FULL_SERVER_PATH + "/flow/continuations";
    public static final String FREE_INPUTS_CONTINUATIONS = FULL_SERVER_PATH + "/collect/free/inputs/continuation";
    public static final String USER_EXECUTIONS = FULL_SERVER_PATH + "/user/executions";
    public static final String FREE_INPUTS_RERUN = FULL_SERVER_PATH + "/collect/free/inputs/rerun";
    public static final String STATISTICS = FULL_SERVER_PATH + "/statistics";
    public static final String USER_NUMBER_OF_EXECUTION = FULL_SERVER_PATH + "/get/number/of/executions";

}
