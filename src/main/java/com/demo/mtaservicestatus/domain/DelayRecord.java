package com.demo.mtaservicestatus.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.Optional;

@Getter
@Setter

/**
 * This is a model class to hold delay record per route.
 *
 * @author Weiyao Tang
 * @version 1.0
 */
public class DelayRecord {

    /** Holds the total duration of all finished delays per route   */
    private long totalDelayDuration;

    /** Holds the start time of the current delay that the route is experiencing,
     * and would be set to empty if the route is not currently experiencing delay   */
    private Optional<Long> startTimeOfCurrentDelay;

    public DelayRecord() {
        this.totalDelayDuration = 0;
        this.startTimeOfCurrentDelay = Optional.empty();
    }

    /**
     * When the current delay ends, this method would be executed
     * to add duration to total duration
     *
     * The added duration amount would be calculated based on the
     * current delay's startTime
     *
     * @param endTime The end time for current delay
     * @author Weiyao Tang
     */
    public void addDuration(long endTime) {
        totalDelayDuration += endTime-startTimeOfCurrentDelay.orElse(DataHolder.getServiceStartTime());

        /** The start time would be set to empty since the current delay is finished*/
        startTimeOfCurrentDelay = Optional.empty();
    }

}
