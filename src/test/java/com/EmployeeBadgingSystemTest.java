package com;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Unit test for simple App.
 */
public class EmployeeBadgingSystemTest
{
    EmployeeBadgingSystem employeeBadgingSystem = new EmployeeBadgingSystem();

    @Test
    public void testFirstEventExit()
    {
        List<BadgingEvent> badgingEvents = new LinkedList<>();
        badgingEvents.add(new BadgingEvent(Building.BUILDING2, BadgingEventType.EXIT, "emp-001", createTimeWithHourMinuteSecond(10, 0,0)));
        long durationInMinutes = employeeBadgingSystem.calculateTotalDurationInMinutes(badgingEvents);

        assertTrue(badgingEvents.size() == 2);
        assertTrue(badgingEvents.get(0).getEventTime().getHours() == 0);
        assertTrue(badgingEvents.get(0).getEventTime().getMinutes() == 0);
        assertTrue(badgingEvents.get(0).getEventTime().getSeconds() == 0);

        //Verify that employee is considered to have worked from midnight to 10 am
        assertTrue(durationInMinutes == (60 * 10));
    }

    @Test
    public void testLastEventEntry(){
        List<BadgingEvent> badgingEvents = new LinkedList<>();
        badgingEvents.add(new BadgingEvent(Building.BUILDING2, BadgingEventType.ENTRY, "emp-001", createTimeWithHourMinuteSecond(9, 0, 0)));
        long durationInMinutes = employeeBadgingSystem.calculateTotalDurationInMinutes(badgingEvents);

        assertTrue(badgingEvents.size() == 2);
        assertTrue(badgingEvents.get(1).getEventTime().getHours() == 23);
        assertTrue(badgingEvents.get(1).getEventTime().getMinutes() == 59);
        assertTrue(badgingEvents.get(1).getEventTime().getSeconds() == 59);

        //Verify that employee is considered to have worked from 9 am till 11:59:59 pm
        assertTrue(durationInMinutes == (14 * 60 + 59));
    }

    @Test
    public void testOneBadgeInAndOneBadgeOut(){
        List<BadgingEvent> badgingEvents = new LinkedList<>();
        badgingEvents.add(new BadgingEvent(Building.BUILDING1, BadgingEventType.ENTRY, "emp-001", createTimeWithHourMinuteSecond(9, 30, 0)));
        badgingEvents.add(new BadgingEvent(Building.BUILDING1, BadgingEventType.EXIT, "emp-001", createTimeWithHourMinuteSecond(14, 0, 0)));

        long durationInMinutes = employeeBadgingSystem.calculateTotalDurationInMinutes(badgingEvents);

        assertTrue(badgingEvents.size() == 2);

        //Verify that employee is considered to have worked for 4 hours and 30 minutes
        assertTrue(durationInMinutes == (4 * 60 + 30));
    }

    @Test
    public void testMissingOneEntryEvent(){
       List<BadgingEvent> badgingEvents = new LinkedList<>();
       badgingEvents.add(new BadgingEvent(Building.BUILDING1, BadgingEventType.ENTRY, "emp-001", createTimeWithHourMinuteSecond(8, 0, 0)));
       badgingEvents.add(new BadgingEvent(Building.BUILDING1, BadgingEventType.EXIT, "emp-001", createTimeWithHourMinuteSecond(10, 0, 0)));
       badgingEvents.add(new BadgingEvent(Building.BUILDING2, BadgingEventType.EXIT, "emp-001", createTimeWithHourMinuteSecond(15, 0, 0)));

       long durationInMinutes = employeeBadgingSystem.calculateTotalDurationInMinutes(badgingEvents);

       //Verify that employee is considered to have worked from 8 am till 3 pm
        assertTrue(durationInMinutes == (7 * 60));
    }

    @Test
    public void testMissingExitEvent(){
       List<BadgingEvent> badgingEvents = new LinkedList<>();
       badgingEvents.add(new BadgingEvent(Building.BUILDING1, BadgingEventType.ENTRY, "emp-001", createTimeWithHourMinuteSecond(7, 0, 0)));
       badgingEvents.add(new BadgingEvent(Building.BUILDING2, BadgingEventType.ENTRY, "emp-001", createTimeWithHourMinuteSecond(10, 0, 0)));
       badgingEvents.add(new BadgingEvent(Building.BUILDING2, BadgingEventType.EXIT, "emp-001", createTimeWithHourMinuteSecond(16,0,0)));

       long durationInMinutes = employeeBadgingSystem.calculateTotalDurationInMinutes(badgingEvents);

       //Verify employee considered to have work 9 hours
       assertTrue(durationInMinutes == (9 * 60));
    }

    @Test
    public void testMissingExitFollowedByMissingEntry(){
       List<BadgingEvent> badgingEvents = new LinkedList<>();
       badgingEvents.add(new BadgingEvent(Building.BUILDING1, BadgingEventType.ENTRY, "emp-001", createTimeWithHourMinuteSecond(8, 0, 0)));
       badgingEvents.add(new BadgingEvent(Building.BUILDING2, BadgingEventType.EXIT, "emp-001", createTimeWithHourMinuteSecond(14,0,0)));

       long durationInMinutes = employeeBadgingSystem.calculateTotalDurationInMinutes(badgingEvents);

       assertTrue(durationInMinutes == (6 * 60));
    }

    @Test
    public void testNoMissingEventsButShortWorkDay(){
       List<BadgingEvent> badgingEvents = new LinkedList<>();
       badgingEvents.add(new BadgingEvent(Building.BUILDING1, BadgingEventType.ENTRY, "emp-001", createTimeWithHourMinuteSecond(8, 0, 0)));
       badgingEvents.add(new BadgingEvent(Building.BUILDING1, BadgingEventType.EXIT, "emp-001", createTimeWithHourMinuteSecond(9, 0, 0)));
       badgingEvents.add(new BadgingEvent(Building.BUILDING2, BadgingEventType.ENTRY, "emp-001", createTimeWithHourMinuteSecond(11, 0, 0)));
       badgingEvents.add(new BadgingEvent(Building.BUILDING2, BadgingEventType.EXIT, "emp-001", createTimeWithHourMinuteSecond(13, 0, 0)));

       long durationInMinutes = employeeBadgingSystem.calculateTotalDurationInMinutes(badgingEvents);

       //Verify employee worked 1 hour in Building1 and 2 hours in Building2
        assertTrue(durationInMinutes == (3* 60));
    }

    private Date createTimeWithHourMinuteSecond(int hour, int minute, int second){
        Date date = new Date();
        date.setHours(hour);
        date.setMinutes(minute);
        date.setSeconds(second);
        return date;
    }
}
