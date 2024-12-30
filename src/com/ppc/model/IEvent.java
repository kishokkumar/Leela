package com.ppc.model;

import java.time.LocalDate;

public interface IEvent {

	int getSequenceNo();

	void setSequenceNo(int sequenceNo);

	String getEmpId();

	void setEmpId(String empId);

	EventType getEvent();

	void setEvent(EventType event);

	LocalDate getEventDate();

	void setEventDate(LocalDate eventDate);

	String getNotes();

	void setNotes(String notes);

	String getValue();

	void setValue(String value);

	int getEventMonth();

	int getEventYear();

	boolean getIsCostEvent();
}
