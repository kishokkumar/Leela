package com.ppc.model;

import java.time.Month;

public interface IMonthlyReportFormat {

	Month getMonth();

	void setMonth(Month month);

	int getYear();

	void setYear(int year);

	int getTotalEmployees();

	void setTotalEmployees(int totalEmployees);

	double getAmount();

	void setAmount(double amount);
}
