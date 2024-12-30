package com.ppc;

import com.ppc.service.FileProcessor;
import com.ppc.util.Helper;
import com.ppc.model.*;

import java.io.Console;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Arrays;

public class PayrollProcessingSystem {
	public static void main(String[] args) {
		if (args.length < 1) {
			System.out.println("Please provide at least one CSV file path.");
			return;
		}

		String inputFilePath = args[0];

		FileProcessor fileProcessor = new FileProcessor();
		try {
			fileProcessor.processFile(inputFilePath);

			Initialize();
			defaultCurrentDate(fileProcessor);

			Console console = System.console();
			while (true) {
				try {
					System.out.println();
					System.out.println();
					String input = console.readLine();

					// Exit loop if 'q' is pressed
					if (input.equals("q") || input.equals("Q")) {
						System.out.println("Exiting the program.");
						break; // Exit the loop
					}

					String[] splitInput = input.split(",");
					LocalDate dateToProcess = splitInput.length > 1 ? Helper.parseDate(splitInput[1]) : LocalDate.now();

					switch (splitInput[0]) {
					case "1":
						getTotalEmployees(fileProcessor);
						break;
					case "2":
						getMonthWiseReport(fileProcessor, dateToProcess);
						break;
					case "3":
						getMonthlySalaryReport(fileProcessor, dateToProcess);
						break;
					case "4":
						getEmployeeWiseReport(fileProcessor, dateToProcess);
						break;
					case "5":
						getMonthlyAmountReleasedReport(fileProcessor, dateToProcess);
						break;
					case "6":
						getYearlyFinancialReport(fileProcessor, dateToProcess);
						break;
					}

					// Handle other keys (display the ASCII value of the key pressed)
					System.out.println("You pressed: " + input);
				} catch (Exception e) {
					System.out.println("An error occurred while reading input.");
					break; // Exit if there is an error
				}
			}

		} catch (IOException e) {
			System.out.println("An error occurred while processing the file: " + e.getMessage());
		}
	}

	private static void Initialize() {
		System.out.println("1. Total number of employees in an organization: <Enter 1>");
		System.out.println(
				"2. Month wise following details: <Enter 2,Date << dd-mm-yyyy >> (Month to which needs to extract)>");
		System.out
				.println("3. Monthly salary report <Enter 3,Date << dd-mm-yyyy >> (Month to which needs to extract)>");
		System.out.println(
				"4. Employee wise financial report <Enter 4,Date << dd-mm-yyyy >> (Month to which needs to extract)>");
		System.out.println(
				"5. Monthly amount released <Enter 5,Date << dd-mm-yyyy >> (Month to which needs to extract)>");
		System.out.println(
				"6. Yearly financial report <Enter 6,Date << dd-mm-yyyy >> (Month to which needs to extract)>");
		System.out.println("Enter q or Q to exit");
		System.out.println();
		System.out.println();
	}

	private static void defaultCurrentDate(FileProcessor fileProcessor) {
		getTotalEmployees(fileProcessor);
		getMonthWiseReport(fileProcessor, LocalDate.now());
		getMonthlySalaryReport(fileProcessor, LocalDate.now());
		getEmployeeWiseReport(fileProcessor, LocalDate.now());
		getMonthlyAmountReleasedReport(fileProcessor, LocalDate.now());
		getYearlyFinancialReport(fileProcessor, LocalDate.now());
	}

	private static void getTotalEmployees(FileProcessor fileProcessor) {
		/// 1. Total number of employees in an organization.
		System.out.println("1. Total number of employees in an organization: " + fileProcessor.getTotalEmployees());
	}

	private static void getMonthWiseReport(FileProcessor fileProcessor, LocalDate currentDate) {
		System.out.println();
		/// 2. Month wise following details.
		System.out.println("2. Month wise following details:");
		System.out.println("a. Total number of employees joined the organization in the month of " + currentDate);

		for (IEmployeeDetails employee : fileProcessor.getNewEmployeesOfMonth(currentDate)) {
			System.out.printf("Emp id: %s, Designation: %s, Name: %s, Surname: %s %n", employee.getEmpId(),
					employee.getDesignation(), employee.getFirstName(), employee.getLastName());
		}

		System.out.println();
		System.out.println("b. Total number of employees exit organization in the month of " + currentDate);
		for (IEmployeeDetails employee : fileProcessor.geExitedEmployeesOfMonth(currentDate)) {
			System.out.printf("Name: %s, Surname: %s %n", employee.getFirstName(), employee.getLastName());
		}
	}

	private static void getMonthlySalaryReport(FileProcessor fileProcessor, LocalDate currentDate) {
		System.out.println();
		System.out.println("3. Monthly salary report");
		// LocalDate salaryReportMonth = LocalDate.of(2022, 11, 1);
		IMonthlyReportFormat totalMonthlyForamt = fileProcessor.getTotalSalaryOfMonth(currentDate);
		System.out.printf("Month: %s, Total Salary: %.2f, Total Employee: %d %n", totalMonthlyForamt.getMonth(),
				totalMonthlyForamt.getAmount(), totalMonthlyForamt.getTotalEmployees());
	}

	private static void getEmployeeWiseReport(FileProcessor fileProcessor, LocalDate salaryReportMonth) {
		System.out.println();
		System.out.println("4. Employee wise financial report");

		for (ITotalMonthlyReportFormat employeeDetails : fileProcessor.getEmployeeMonthlyReport(salaryReportMonth)) {
			System.out.printf("Emp id: %s, Name: %s, Surname: %s, Total amount paid: %.2f %n",
					employeeDetails.getEmpId(), employeeDetails.getFirstName(), employeeDetails.getLastName(),
					employeeDetails.getAmount());
		}
	}

	private static void getMonthlyAmountReleasedReport(FileProcessor fileProcessor, LocalDate currentDate) {
		System.out.println();
		System.out.println("5. Monthly amount released");
		// LocalDate totalAmountReportMonth = LocalDate.of(2022, 11, 1);
		IMonthlyReportFormat totalAmountMonthlyForamt = fileProcessor.getTotalCostOfMonth(currentDate);
		System.out.printf("Month: %s, Total Salary  (Salary + Bonus + REIMBURSEMENT): %.2f, Total Employee: %d %n",
				totalAmountMonthlyForamt.getMonth(), totalAmountMonthlyForamt.getAmount(),
				totalAmountMonthlyForamt.getTotalEmployees());
	}

	private static void getYearlyFinancialReport(FileProcessor fileProcessor, LocalDate salaryReportMonth) {
		System.out.println();
		System.out.println("6. Yearly financial report");

		for (ICompleteEvent event : fileProcessor.getAnnualReport(salaryReportMonth)) {
			System.out.printf("Event: %s, Emp id: %s, Event Date: %s, Event Value: %s %n", event.getEvent(),
					event.getEmpId(), event.getEventDate(), event.getValue());
		}
	}

}
