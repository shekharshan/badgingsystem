package com;

import java.time.Duration;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class EmployeeBadgingSystem {


    public long calculateTotalDurationInMinutes(List<BadgingEvent> employeeBadgingEvents) {

        //No events, employee did no work
        if (employeeBadgingEvents == null || employeeBadgingEvents.size() == 0){
            return 0;
        }

        Collections.sort(employeeBadgingEvents, Comparator.comparing(BadgingEvent::getEventTime));
        verifyAllEventsFromSameDay(employeeBadgingEvents);
        fillMissingEvents(employeeBadgingEvents);
        return calculateDurationInMinutes(employeeBadgingEvents);
    }

    private void verifyAllEventsFromSameDay(List<BadgingEvent> employeeBadgingEvents) {
        BadgingEvent firstBadgingEvent = employeeBadgingEvents.get(0);
        BadgingEvent lastBadgingEvent = employeeBadgingEvents.get(employeeBadgingEvents.size() - 1);
        assert firstBadgingEvent.getEventTime().getYear() == lastBadgingEvent.getEventTime().getYear();
        assert firstBadgingEvent.getEventTime().getMonth() == lastBadgingEvent.getEventTime().getMonth();
        assert firstBadgingEvent.getEventTime().getDay() == lastBadgingEvent.getEventTime().getDay();
    }

    private long calculateDurationInMinutes(List<BadgingEvent> employeeBadgingEvents) {
        long totalDurationInMillis = 0;
        for (int i = 0; i < employeeBadgingEvents.size(); i+=2){
            BadgingEvent eventOne = employeeBadgingEvents.get(i);
            BadgingEvent eventTwo = employeeBadgingEvents.get(i+1);
            totalDurationInMillis += eventTwo.getEventTime().getTime() - eventOne.getEventTime().getTime();
        }
        return Duration.ofMillis(totalDurationInMillis).toMinutes();
    }

    /**
     * Chronologically sorts badging events and then fills out missing events based on business rules
     *
     * @param employeeBadgingEvents
     */
    private void fillMissingEvents(List<BadgingEvent> employeeBadgingEvents) {
        assert employeeBadgingEvents.size() > 0;
        BadgingEvent firstEventOfTheDay = employeeBadgingEvents.get(0);
        BadgingEvent lastEventOfTheDay = employeeBadgingEvents.get(employeeBadgingEvents.size() - 1);
        handleCaseWhenFirstEventExit(employeeBadgingEvents, firstEventOfTheDay);
        handleCaseWhenLastEventEntry(employeeBadgingEvents, lastEventOfTheDay);
        handleMissingSingleEvent(employeeBadgingEvents);
        handleMissingPairOfEvents(employeeBadgingEvents);
    }

    private void handleCaseWhenFirstEventExit(List<BadgingEvent> employeeBadgingEvents, BadgingEvent firstEventOfTheDay) {
        //If first event is EXIT then create an ENTRY event at 00:00:01 am of that day
        if (firstEventOfTheDay.getEventType() == BadgingEventType.EXIT){
            //First event is EXIT, add an ENTRY event that starts at 1 seconds after midnight
            Date secondAfterMidnight = new Date(firstEventOfTheDay.getEventTime().getTime());
            secondAfterMidnight.setHours(0);
            secondAfterMidnight.setMinutes(0);
            secondAfterMidnight.setSeconds(0);
            firstEventOfTheDay = new BadgingEvent(firstEventOfTheDay.getBuilding(),
                    BadgingEventType.ENTRY, firstEventOfTheDay.getEmployeeId(), secondAfterMidnight);
            employeeBadgingEvents.add(0, firstEventOfTheDay);
        }
    }

    private void handleCaseWhenLastEventEntry(List<BadgingEvent> employeeBadgingEvents, BadgingEvent lastEventOfTheDay) {
        //If last event is ENTRY then create an EXIT event at 11:59:59 pm of that day
        if (lastEventOfTheDay.getEventType() == BadgingEventType.ENTRY){
            Date midnight = new Date(lastEventOfTheDay.getEventTime().getTime());
            midnight.setHours(23);
            midnight.setMinutes(59);
            midnight.setSeconds(59);
            lastEventOfTheDay = new BadgingEvent(lastEventOfTheDay.getBuilding(), BadgingEventType.EXIT, lastEventOfTheDay.getEmployeeId(),
                    midnight);
            employeeBadgingEvents.add(lastEventOfTheDay);
        }
    }

    /**
     * After this method executes we are ensured each even position is occupied by ENTRY event and each odd position
     * is occupied by EXIT event.
     *
     * @param employeeBadgingEvents
     */
    private void handleMissingSingleEvent(List<BadgingEvent> employeeBadgingEvents) {
        for (int i = 0; i < employeeBadgingEvents.size(); ++i){
            BadgingEvent currentEvent = employeeBadgingEvents.get(i);
            if ((i % 2 ==0) && currentEvent.getEventType() == BadgingEventType.EXIT){
                //On even index we have an EXIT event. Create missing ENTRY event in its place, using timestamp of previous exit event
                employeeBadgingEvents.add(i, new BadgingEvent(currentEvent.getBuilding(),
                        BadgingEventType.ENTRY, currentEvent.getEmployeeId(), employeeBadgingEvents.get(i-1).getEventTime()));
                continue;
            }
            currentEvent = employeeBadgingEvents.get(i);
            if ((i % 2 == 1) && currentEvent.getEventType() == BadgingEventType.ENTRY){
                //On odd index we have an ENTRY event. Create missing EXIT event in its place, using building id of previous entry event
                employeeBadgingEvents.add(i, new BadgingEvent(employeeBadgingEvents.get(i -1).getBuilding(),
                        BadgingEventType.EXIT, currentEvent.getEmployeeId(), currentEvent.getEventTime()));
            }
        }
    }

    /**
     * Method ensures that we address scenarios where an employee forgot to badge out of first building
     * and then forgot to badge into the second building. In this case we need to create an EXIT event
     * for first building and an ENTRY event for the second building
     *
     * @param employeeBadgingEvents
     */
    private void handleMissingPairOfEvents(List<BadgingEvent> employeeBadgingEvents) {
        for (int i = 0; i < employeeBadgingEvents.size(); ){
            BadgingEvent firstEvent = employeeBadgingEvents.get(i);
            BadgingEvent secondEvent = employeeBadgingEvents.get(i+1);
            assert firstEvent.getEventType() == BadgingEventType.ENTRY;
            assert secondEvent.getEventType() == BadgingEventType.EXIT;
            if (firstEvent.getBuilding() != secondEvent.getBuilding()){
                //Missing EXIT event from first building and missing ENTRY event on the second building
                employeeBadgingEvents.add(i + 1, new BadgingEvent(firstEvent.getBuilding(),
                        BadgingEventType.EXIT, firstEvent.getEmployeeId(), firstEvent.getEventTime()));
                employeeBadgingEvents.add(i + 2, new BadgingEvent(secondEvent.getBuilding(),
                        BadgingEventType.ENTRY, secondEvent.getEmployeeId(), firstEvent.getEventTime()));
            }
            i = i + 2;
        }
    }
}
