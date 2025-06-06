package utility;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import model.TimeRegistration;
import model.TimeSheet;

/**
 * Utility class for validating business rules applied to a TimeSheet.
 * 
 * This class checks whether a list of TimeRegistration entries within a TimeSheet
 * satisfies specific business constraints, including:
 * 
 * There must be at least 11 hours of rest between the end of one work session and the start of the next.
 * The total number of hours worked across all time registrations within a 7-day period must not exceed 48 hours.
 * 
 * This class is not intended to be instantiated.
 * 
 * @author Sebastian Nørlund Nielsen
 */
public class ValidateTimeSheet {
	
	/**
     * Validates a TimeSheet against predefined business rules and updates its status flags accordingly.
     * 
     * @param timeSheet The TimeSheet object whose list will be validated.
     */
	public static void validateBusinessRules(TimeSheet timeSheet) {
		
		List<TimeRegistration> timeRegistrations = timeSheet.getTimeRegistrations();
		boolean timeBetweenIsValid = validateTimeBetweenRegistrations(timeRegistrations);
		boolean sevenDayPeriodIsValid = validateSevenDayPeriod(timeRegistrations);
		
		if (timeBetweenIsValid) {
			
			timeSheet.setTimeBetweenRegistrations(true);
		} else {
			
			timeSheet.setTimeBetweenRegistrations(false);
		}
		
		if (sevenDayPeriodIsValid) {
			
			timeSheet.setSevenDayPeriod(true);
		} else {
			
			timeSheet.setSevenDayPeriod(false);
		}
		
	}
	
	/**
     * Checks whether there is a minimum of 11 hours between the end of one time registration
     * and the start of the next registration (in chronological order).
     *
     * Assumes that timeRegistrations are sorted by date.
     * 
     * @param timeRegistrations A list of TimeRegistration entries.
     * @return true if any two consecutive registrations have at least 11 hours in between; otherwise false.
     */
	public static boolean validateTimeBetweenRegistrations(List<TimeRegistration> timeRegistrations) {
		
		boolean timeBetweenIsValid = false;
		
		if (timeRegistrations != null) {
			
			TimeRegistration previousTimeRegistration = null;
			LocalDate currentDate = null;
			
			for (TimeRegistration timeRegistration : timeRegistrations) {
				
				currentDate = timeRegistration.getDate();
				
				if (previousTimeRegistration != null && currentDate.isAfter(previousTimeRegistration.getDate())) {
					
					LocalDateTime previousEndTime = previousTimeRegistration.getEndTime();
					LocalDateTime currentStartTime = timeRegistration.getStartTime(); 
					double durationBetweenTimes = Duration.between(previousEndTime, currentStartTime).toHours();
					
					if (durationBetweenTimes >= 11) {
						
						timeBetweenIsValid = true;
					}
				} 
				
				previousTimeRegistration = timeRegistration;
			}
		}
		
		return timeBetweenIsValid;
	}
	
	/**
     * Validates that the total duration of all time registrations within a time sheet
     * does not exceed 48 hours over a 7-day period.
     *
     * This method assumes the full list of time registrations fall within a 7-day range.
     * 
     * @param timeRegistrations A list of TimeRegistration entries.
     * @return true if the total duration is less than or equal to 48 hours; otherwise false.
     */
	public static boolean validateSevenDayPeriod(List<TimeRegistration> timeRegistrations) {
		
		boolean sevenDayPeriodIsValid = false;
		double totalHoursInTimeSheet = 0;
		
		if (timeRegistrations != null) {
			
			for (TimeRegistration timeRegistration : timeRegistrations) {
				
				LocalDateTime currentStartTime = timeRegistration.getStartTime();
				LocalDateTime currentEndTime = timeRegistration.getEndTime();
				double durationBetweenStartEnd = Duration.between(currentStartTime, currentEndTime).toHours();
				totalHoursInTimeSheet += durationBetweenStartEnd;
			}
			
			if (totalHoursInTimeSheet <= 48) {
				
				sevenDayPeriodIsValid = true;
			}
		} 
		
		return sevenDayPeriodIsValid;
	}

}
