package org.opentripplanner.transit.raptor.rangeraptor.path;

import org.junit.Test;
import org.opentripplanner.transit.raptor.api.transit.RaptorTripSchedule;
import org.opentripplanner.transit.raptor.rangeraptor.multicriteria.arrivals.AccessStopArrival;
import org.opentripplanner.transit.raptor.rangeraptor.multicriteria.arrivals.TransitStopArrival;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.opentripplanner.transit.raptor._data.transit.TestTransfer.walk;

public class DestinationArrivalTest {

    private static final int BOARD_SLACK = 60;
    private static final int ACCESS_STOP = 100;
    private static final int ACCESS_DEPARTURE_TIME = 8 * 60 * 60;
    private static final int ACCESS_DURATION_TIME =  72;
    private static final int ACCESS_COST = 420;

    private static final int TRANSIT_STOP = 101;
    private static final int TRANSIT_BOARD_TIME = ACCESS_DEPARTURE_TIME + 10 * 60;
    private static final int TRANSIT_ALIGHT_TIME = TRANSIT_BOARD_TIME + 4 * 60;
    private static final RaptorTripSchedule A_TRIP = null;
    private static final int TRANSIT_COST = 200;

    private static final int DESTINATION_DURATION_TIME = 50;
    private static final int DESTINATION_COST = 500;

    private static final int EXPECTED_ARRIVAL_TIME = TRANSIT_ALIGHT_TIME + DESTINATION_DURATION_TIME;
    private static final int EXPECTED_TOTAL_COST = ACCESS_COST + TRANSIT_COST + DESTINATION_COST;

    /**
     * Setup a simple journey with an access leg, one transit and a egress leg.
     */
    private static final AccessStopArrival<RaptorTripSchedule> ACCESS_ARRIVAL = new AccessStopArrival<>(
            ACCESS_DEPARTURE_TIME,
            ACCESS_COST,
            walk(ACCESS_STOP, ACCESS_DURATION_TIME)
    );

    private static final TransitStopArrival<RaptorTripSchedule> TRANSIT_ARRIVAL = new TransitStopArrival<>(
            ACCESS_ARRIVAL.timeShiftNewArrivalTime(TRANSIT_BOARD_TIME - BOARD_SLACK),
            TRANSIT_STOP,
            TRANSIT_ALIGHT_TIME,
            TRANSIT_COST,
            A_TRIP
    );

    private final DestinationArrival<RaptorTripSchedule> subject = new DestinationArrival<>(
            walk(TRANSIT_STOP, DESTINATION_DURATION_TIME),
            TRANSIT_ARRIVAL,
            TRANSIT_ALIGHT_TIME + DESTINATION_DURATION_TIME,
            DESTINATION_COST
    );

    @Test
    public void arrivalTime() {
        assertEquals(EXPECTED_ARRIVAL_TIME, subject.arrivalTime());
    }

    @Test
    public void cost() {
        assertEquals(EXPECTED_TOTAL_COST, subject.cost());
    }

    @Test
    public void round() {
        assertEquals(1, subject.round());
    }

    @Test
    public void previous() {
        assertSame(TRANSIT_ARRIVAL, subject.previous());
    }

    @Test
    public void testToString() {
        assertEquals(
            "Egress { round: 1, from-stop: 101, duration: 50s, arrival-time: 8:14:50 $1120 }",
            subject.toString()
        );
    }
}