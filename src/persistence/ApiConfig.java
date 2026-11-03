package persistence;

/**
 * Configuration of the remote API used by the persistence layer.
 * The base URL is read from the API_BASE_URL environment variable or from
 * the api.baseUrl system property (-Dapi.baseUrl=...). If neither is set, a
 * local placeholder is used, the API check fails and the application falls
 * back to the local JSON and CSV files.
 */
public final class ApiConfig {
    private static final String DEFAULT_BASE_URL = "http://localhost/dpoo";

    private ApiConfig() {
    }

    public static String baseUrl() {
        String fromEnv = System.getenv("API_BASE_URL");
        if (fromEnv != null && !fromEnv.isBlank()) {
            return fromEnv;
        }
        return System.getProperty("api.baseUrl", DEFAULT_BASE_URL);
    }
}
