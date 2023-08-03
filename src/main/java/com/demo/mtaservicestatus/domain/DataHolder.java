package com.demo.mtaservicestatus.domain;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * This is a model class to hold data shared among classes.
 *
 * @author Weiyao Tang
 * @version 1.0
 */
public class DataHolder {

    /** The start time of the service */
    private static long serviceStartTime;

    /** The api access key for NYC MTA API */
    private final static String apiKey = "qmGTZ9vEOi8unltuUVVFJ6IMRuCaw41S52gSnnQE";

    /** The API URL for NYC MTA subway service alert JSON Feeds */
    private final static String apiUrl = "https://api-endpoint.mta.info/Dataservice/mtagtfsfeeds/camsys%2Fsubway-alerts.json";

    /** The map to hold status for each subway route */
    private static Map<String, Status> routeToStatus = new HashMap<>() {{
        put("A", Status.AS_PLANNED);
        put("C", Status.AS_PLANNED);
        put("E", Status.AS_PLANNED);
        put("B", Status.AS_PLANNED);
        put("D", Status.AS_PLANNED);
        put("F", Status.AS_PLANNED);
        put("M", Status.AS_PLANNED);
        put("G", Status.AS_PLANNED);
        put("J", Status.AS_PLANNED);
        put("Z", Status.AS_PLANNED);
        put("N", Status.AS_PLANNED);
        put("Q", Status.AS_PLANNED);
        put("R", Status.AS_PLANNED);
        put("W", Status.AS_PLANNED);
        put("L", Status.AS_PLANNED);
        put("1", Status.AS_PLANNED);
        put("2", Status.AS_PLANNED);
        put("3", Status.AS_PLANNED);
        put("4", Status.AS_PLANNED);
        put("5", Status.AS_PLANNED);
        put("6", Status.AS_PLANNED);
        put("7", Status.AS_PLANNED);
        put("SIR", Status.AS_PLANNED);
    }};

    /** The map to hold delay record for each subway route */

    private static Map<String, DelayRecord> routeToDelayRecord = new HashMap<>() {{
        put("A", new DelayRecord());
        put("C", new DelayRecord());
        put("E", new DelayRecord());
        put("B", new DelayRecord());
        put("D", new DelayRecord());
        put("F", new DelayRecord());
        put("M", new DelayRecord());
        put("G", new DelayRecord());
        put("J", new DelayRecord());
        put("Z", new DelayRecord());
        put("N", new DelayRecord());
        put("Q", new DelayRecord());
        put("R", new DelayRecord());
        put("W", new DelayRecord());
        put("L", new DelayRecord());
        put("1", new DelayRecord());
        put("2", new DelayRecord());
        put("3", new DelayRecord());
        put("4", new DelayRecord());
        put("5", new DelayRecord());
        put("6", new DelayRecord());
        put("7", new DelayRecord());
        put("SIR", new DelayRecord());
    }};

    public static Map<String, Status> getRouteToStatus() {
        return routeToStatus;
    }

    public static Map<String, DelayRecord> getRouteToDelayRecord() {
        return routeToDelayRecord;
    }


    public static String getApiKey(){
        return apiKey;
    }

    public static String getApiUrl() {
        return apiUrl;
    }

    public static long getServiceStartTime() {
        return serviceStartTime;
    }

    public static void setServiceStartTime(long time) {
        serviceStartTime = time;
    }
}
