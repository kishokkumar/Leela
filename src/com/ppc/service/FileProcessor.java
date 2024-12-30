package com.ppc.service;

import com.ppc.model.*;
import com.ppc.util.Helper;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class FileProcessor {

	public List<ICompleteEvent> employeeEvents = new ArrayList<>();
	public Map<String, Employee> employees = new HashMap<>();

	public void processFile(String filePath) throws IOException {

		BufferedReader reader = new BufferedReader(new FileReader(filePath));
		String line;

		reader.readLine();
		Pattern pattern = Pattern.compile(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");

		while ((line = reader.readLine()) != null) {
			String[] fields = pattern.split(line);
			if (fields.length < 6) {

				System.err.println("Skipping invalid record due to insufficient fields: " + line);
				continue;
			}

			try {

				if (fields[5] != null && fields[5].trim().equals(EventType.ONBOARD.toString())) {
					processOnboarding(fields);
				} else {
					EventType eventType = EventType.valueOf(fields[2]);

					switch (eventType) {
					case SALARY:
					case BONUS:
					case REIMBURSEMENT:
						// If Specific overrides needed in the future we can break it but for this use
						// case we can keep it simple.
						// processBonus();
						// processSalary(fields);
						// processReimbursement();
						processCostEvent(fields);
						break;
					case EXIT:
						processExit(fields);
						break;
					}
				}

			} catch (Exception e) {
				System.err.println("Error processing record: " + line);
				continue; // Skip this record if an error occurs
			}

		}

		reader.close();
	}

	public int getTotalEmployees() {
		return employees.size();
	}

	public List<IEmployeeDetails> getNewEmployeesOfMonth(LocalDate date) {
		return getSpecificEvent(date, EventType.ONBOARD);
	}

	public List<IEmployeeDetails> geExitedEmployeesOfMonth(LocalDate date) {
		List<IEmployeeDetails> filterEmployeeDetails = getSpecificEvent(date, EventType.EXIT);

		List<IEmployeeDetails> employeeDetails = new ArrayList<>();
		for (IEmployeeDetails employee : filterEmployeeDetails) {
			employeeDetails.add(employees.get(employee.getEmpId()));
		}

		return employeeDetails;
	}

	public IMonthlyReportFormat getTotalSalaryOfMonth(LocalDate date) {
		var salaries = getSalaryEvents(date);

		IMonthlyReportFormat totalMonthlyFormat = new EmployeeReportFormat();
		totalMonthlyFormat.setTotalEmployees(salaries.size());
		totalMonthlyFormat.setYear(date.getYear());
		totalMonthlyFormat.setMonth(date.getMonth());

		double totalSalary = salaries.values().stream().mapToDouble(Double::doubleValue).sum();

		totalMonthlyFormat.setAmount(totalSalary);

		return totalMonthlyFormat;
	}

	public List<ITotalMonthlyReportFormat> getEmployeeMonthlyReport(LocalDate date) {
		var salaries = getCostEvents(date);

		List<ITotalMonthlyReportFormat> employeeDetails = new ArrayList<>();
		for (String key : salaries.keySet()) {
			ITotalMonthlyReportFormat totalMonthlyFormat = new EmployeeReportFormat();
			totalMonthlyFormat.setAmount(salaries.get(key));
			totalMonthlyFormat.setYear(date.getYear());
			totalMonthlyFormat.setMonth(date.getMonth());
			totalMonthlyFormat.setEmpId(key);

			Employee employee = employees.get(key);
			totalMonthlyFormat.setFirstName(employee.firstName);
			totalMonthlyFormat.setLastName(employee.lastName);

			employeeDetails.add(totalMonthlyFormat);
		}

		return employeeDetails;
	}

	public IMonthlyReportFormat getTotalCostOfMonth(LocalDate date) {
		var totalCost = getCostEvents(date);

		IMonthlyReportFormat totalMonthlyFormat = new EmployeeReportFormat();
		totalMonthlyFormat.setTotalEmployees(totalCost.size());
		totalMonthlyFormat.setYear(date.getYear());
		totalMonthlyFormat.setMonth(date.getMonth());

		double totalSalary = totalCost.values().stream().mapToDouble(Double::doubleValue).sum();

		totalMonthlyFormat.setAmount(totalSalary);

		return totalMonthlyFormat;
	}

	public List<ICompleteEvent> getAnnualReport(LocalDate date) {
		return employeeEvents.stream().filter(employee -> employee.getEventYear() == date.getYear())
				.collect(Collectors.toList());
	}

	private List<IEmployeeDetails> getSpecificEvent(LocalDate date, EventType eventType) {
		return employeeEvents.stream()
				.filter(employee -> employee.getEventMonth() == date.getMonthValue()
						&& employee.getEventYear() == date.getYear() && employee.getEvent() == eventType)
				.collect(Collectors.toList());
	}

	private Map<String, Double> getSalaryEvents(LocalDate date) {
		return getSpecificEvents(date, EventType.SALARY.toString());
	}

	private Map<String, Double> getCostEvents(LocalDate date) {
		return getSpecificEvents(date, "Cost");
	}

	private Map<String, Double> getSpecificEvents(LocalDate date, String eventType) {
		return employeeEvents.stream().filter(employee -> employee.getEventMonth() == date.getMonthValue()
				&& employee.getEventYear() == date.getYear()
				&& (eventType.equals("Cost") ? employee.getIsCostEvent() : employee.getEvent() == EventType.SALARY))
				.collect(Collectors.groupingBy(ICompleteEvent::getEmpId,
						Collectors.summingDouble(event -> Double.parseDouble(event.getValue()))));
	}

	private void processOnboarding(String[] fields) {
		/// Add to EmployeeEvent for Audit
		/// Check if the employee exists with the same id skip
		/// Add employee to employees list

		ICompleteEvent employeeEvent = getEmployeeEvent(fields);
		addToEmployeeEvent(employeeEvent);
		addEmployee(employeeEvent);
	}

	private void processExit(String[] fields) {
		/// Add to EmployeeEvent for Audit
		/// Check if the employee is active, if not skip
		/// Extract Employee and update exit date.

		IEvent employeeEvent = getOtherEvent(fields, 2);
		addToEmployeeEvent((EmployeeEvent) employeeEvent);
		UpdateExitDate(employeeEvent);
	}

	private void processCostEvent(String[] fields) {
		/// Add to EmployeeEvent for Audit
		/// Check if the employee is active, if not skip
		/// Add salary to Employee Cost
		IEvent employeeEvent = getOtherEvent(fields, 2);
		addToEmployeeEvent((EmployeeEvent) employeeEvent);
	}

	private ICompleteEvent getEmployeeEvent(String[] fields) {
		ICompleteEvent employeeEvent = (EmployeeEvent) getOtherEvent(fields, 5);

		employeeEvent.setFirstName(fields[2].trim());
		employeeEvent.setLastName(fields[3].trim());
		employeeEvent.setDesignation(fields[4].trim());

		return employeeEvent;
	}

	private IEvent getOtherEvent(String[] fields, int offset) {
		IEvent employeeEvent = new EmployeeEvent();

		employeeEvent.setSequenceNo(Integer.parseInt(fields[0].trim()));
		employeeEvent.setEmpId(fields[1].trim());
		employeeEvent.setEvent(EventType.valueOf(fields[offset]));
		employeeEvent.setValue(fields[offset + 1].trim());
		employeeEvent.setEventDate(Helper.parseDate(fields[offset + 2].trim()));
		employeeEvent.setNotes(fields[offset + 2].trim());

		return employeeEvent;
	}

	private void addToEmployeeEvent(ICompleteEvent employeeEvent) {
		employeeEvents.add(employeeEvent);
	}

	private void addEmployee(ICompleteEvent employeeEvent) {
		if (!employees.containsKey(employeeEvent.getEmpId())) {
			Employee employee = new Employee();
			employee.setEmpId(employeeEvent.getEmpId());
			employee.setonBoardingDate(Helper.parseDate(employeeEvent.getValue()));
			employee.setFirstName(employeeEvent.getFirstName());
			employee.setLastName(employeeEvent.getLastName());
			employee.setDesignation(employeeEvent.getDesignation());
			employees.put(employeeEvent.getEmpId(), employee);
		} else {
			System.err.println("Employee Already exists " + employeeEvent.getEmpId());
		}
	}

	private void UpdateExitDate(IEvent event) {
		Employee employee = employees.get(event.getEmpId());

		if (employee != null) {
			employee.setexitDate(Helper.parseDate(event.getValue()));
		} else {
			System.err.println("Employee not found " + event.getEmpId());
		}
	}
}
