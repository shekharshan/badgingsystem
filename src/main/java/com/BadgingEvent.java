package com;

import java.util.Date;

public class BadgingEvent {

   private BadgingEventType eventType;
   private Building building;
   private String employeeId;
   private Date eventTime;

   public BadgingEvent(Building building, BadgingEventType eventType, String employeeId, Date eventTime){
      this.building = building;
      this.eventType = eventType;
      this.employeeId = employeeId;
      this.eventTime = eventTime;
   }

   public String getEmployeeId(){
      return employeeId;
   }

   public Building getBuilding(){
      return building;
   }

   public Date getEventTime(){
      return this.eventTime;
   }

   public BadgingEventType getEventType(){
      return eventType;
   }
}
