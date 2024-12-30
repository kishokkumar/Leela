package com.ppc.model;

import java.time.LocalDate;
import java.util.EnumSet;

public class EmployeeEvent implements ICompleteEvent {
	private String empId;
	private String firstName;
	private String lastName;
	private String designation;
	private EventType event;
	private LocalDate eventDate;
	private String notes;
	private String value;
	private int sequenceNo;
	private int eventMonth;
	private int eventYear;
	private boolean isCostEvent;

	public int getSequenceNo() {
		return sequenceNo;
	}

	public void setSequenceNo(int sequenceNo) {
		this.sequenceNo = sequenceNo;
	}

	public String getEmpId() {
		return empId;
	}

	public void setEmpId(String empId) {
		this.empId = empId;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getDesignation() {
		return designation;
	}

	public void setDesignation(String designation) {
		this.designation = designation;
	}

	public EventType getEvent() {
		return event;
	}

	public void setEvent(EventType event) {
		this.event = event;
		this.isCostEvent = EnumSet.of(EventType.SALARY, EventType.BONUS, EventType.REIMBURSEMENT).contains(this.event);
	}

	public LocalDate getEventDate() {
		return eventDate;
	}

	public void setEventDate(LocalDate eventDate) {
		this.eventDate = eventDate;
		this.eventMonth = this.eventDate.getMonthValue();
		this.eventYear = this.eventDate.getYear();
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public boolean getIsCostEvent() {
		return isCostEvent;
	}

	public int getEventMonth() {
		return this.eventMonth;
	}

	public int getEventYear() {
		return this.eventYear;
	}
}
