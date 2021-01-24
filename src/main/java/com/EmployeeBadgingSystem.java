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
        BadgingEvent firstEventOfTheDay = employeeBadgingEvents.get(0);
        BadgingEvent lastEventOfTheDay = employeeBadgingEvents.get(employeeBadgingEvents.size() - 1);
        handleCaseWhenFirstEventExit(employeeBadgingEvents, firstEventOfTheDay);
        handleCaseWhenLastEventEntry(employeeBadgingEvents, lastEventOfTheDay);
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
        long durationInOffice = employeeBadgingEvents.get(employeeBadgingEvents.size() - 1).getEventTime().getTime() -
                employeeBadgingEvents.get(0).getEventTime().getTime();
        for (int i = 0; i < employeeBadgingEvents.size(); ++i){
            BadgingEvent eventOne = employeeBadgingEvents.get(i);
            BadgingEvent eventTwo = i+1 < employeeBadgingEvents.size() ? employeeBadgingEvents.get(i+1): null;
            if (eventOne.getEventType() == BadgingEventType.EXIT && eventTwo !=null && eventTwo.getEventType() == BadgingEventType.ENTRY){
                long timeBetweenExitAndEntry = eventTwo.getEventTime().getTime() - eventOne.getEventTime().getTime();
                durationInOffice = durationInOffice - timeBetweenExitAndEntry;
                ++i;
            }
        }
        return Duration.ofMillis(durationInOffice).toMinutes();
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
}
