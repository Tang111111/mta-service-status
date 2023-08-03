package com.demo.mtaservicestatus.service;
import com.demo.mtaservicestatus.dao.StatusDao;
import com.demo.mtaservicestatus.domain.DataHolder;
import com.demo.mtaservicestatus.domain.DelayRecord;
import com.demo.mtaservicestatus.domain.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Service to monitor and get route status
 *
 * @author Weiyao Tang
 * @version 1.0
 */
@Service
public class StatusService {
    private final StatusDao statusDao;

    @Autowired
    public StatusService(StatusDao statusDao) {
        this.statusDao = statusDao;
    }

    public String getStatusByRouteName(String routeName) {
        if (!DataHolder.getRouteToStatus().containsKey(routeName)) {
            return "NOT FOUND";
        }

        return statusDao.getStatusByRouteName(routeName) ;
    }

    /**
     * Get the uptime of a route line since service gets started
     *
     * Uptime = 1 - (delay/totalDuration)
     *
     * @param routeName The name of route
     * @return The uptime of the route
     * @author Weiyao Tang
     */
    public double getUptimeByRouteName(String routeName) {
        if (!DataHolder.getRouteToStatus().containsKey(routeName)) {
            return -1.0;
        }
        DelayRecord delayRecord = DataHolder.getRouteToDelayRecord().get(routeName);
        long currentTime = System.currentTimeMillis() / 1000;

        long totalDuration = currentTime - DataHolder.getServiceStartTime();
        long totalDelayDuration = delayRecord.getTotalDelayDuration();

        /**
         * The total delay duration would added with additional time between the running delay's startTime and current time
         *  if the route is currently experiencing delay
         *  */
        if (delayRecord.getStartTimeOfCurrentDelay().isPresent()) {
            totalDelayDuration += (currentTime-delayRecord.getStartTimeOfCurrentDelay().get());
        }
        return 1.0 - (double)totalDelayDuration/(double) totalDuration;
    }

    /**
     * Monitor all route lines every 10 seconds to detect if a line is delayed or not
     * print out lines if the status changes
     *
     * @author Weiyao Tang
     */
    @Scheduled(fixedRate = 10000)
    public void monitorStatus() {
        long currentTime = System.currentTimeMillis() / 1000;
        System.out.println("Current time is " + currentTime + " Monitoring refreshes every 10 seconds...");
        Set<String> delaySet = statusDao.monitorStatus();

        /** Traverse the recorded status of all lines */
        for (Map.Entry<String, Status> entry: DataHolder.getRouteToStatus().entrySet()) {
            String route = entry.getKey();

            /** If the line shifted from delay to as planned */
            if (entry.getValue() == Status.DELAY && !delaySet.contains(route)) {
                entry.setValue(Status.AS_PLANNED);
                System.out.println("Line " + entry.getKey() +" is now recovered.");
                DelayRecord delayRecord = DataHolder.getRouteToDelayRecord().get(route);
                delayRecord.addDuration(currentTime);
            }
            /** If the line shifted from as planned to delay */
            else if (entry.getValue() == Status.AS_PLANNED && delaySet.contains(route)) {
                entry.setValue(Status.DELAY);
                System.out.println("Line " + entry.getKey() +" is experiencing delays.");
                DelayRecord delayRecord = DataHolder.getRouteToDelayRecord().get(route);
                delayRecord.setStartTimeOfCurrentDelay(Optional.of(currentTime));
            }
        }
    }

}
