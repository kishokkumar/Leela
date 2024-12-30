package com.ppc.util;

import com.ppc.model.EmployeeEvent;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Helper {

	// Helper method to parse the date
	public static java.time.LocalDate parseDate(String date) {
		try {
			return java.time.LocalDate.parse(date, java.time.format.DateTimeFormatter.ofPattern("d-M-yyyy"));
		} catch (Exception e) {
			throw new IllegalArgumentException("Invalid date format: " + date);
		}
	}
}
