// Copyright 2011 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.querybuilder.derby;

import java.sql.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public final class DbDateUtilities
{
  public static final long MILLIS_A_PER_SECOND = 1000;
  public static final long MILLIS_B_PER_MINUTE = MILLIS_A_PER_SECOND * 60;
  public static final long MILLIS_C_PER_HOUR = MILLIS_B_PER_MINUTE * 60;
  public static final long MILLIS_D_PER_DAY = MILLIS_C_PER_HOUR * 24;
  public static final long MILLIS_E_AT_EPOCH = new GregorianCalendar(1, 1, 1).getTimeInMillis();
  public static final int PERIOD_A_DAYS_PER_WEEK = 7;
  public static final int PERIOD_B_MONTHS_PER_YEAR = 12;
  public static final int PERIOD_C_QUARTERS_PER_YEAR = 4;
  
  private static final Cache<Date, GregorianCalendar> cache = new Cache<Date, GregorianCalendar>(100);

  public static String getDayName(Date sqlDate)
  {
    return getDayName(getGregorianCalendar(sqlDate));
  }
  
  public static String getDayName(GregorianCalendar calendar)
  {
    String dayName = calendar.getDisplayName(GregorianCalendar.DAY_OF_WEEK, GregorianCalendar.LONG, Locale.getDefault());
    return dayName;
  }
  
  public static int getDayOfEpoch(Date sqlDate)
  {
    return getDayOfEpoch(getGregorianCalendar(sqlDate));
  }
  
  public static int getDayOfEpoch(GregorianCalendar calendar)
  {
    // NB: GregorianCalendar timeInMillis accounts for Julian / Gregorian discontinuity
    long calendarMillis = calendar.getTimeInMillis();
    long deltaMillis = calendarMillis - MILLIS_E_AT_EPOCH;
    int dayOfEpoch = (int)(deltaMillis / MILLIS_D_PER_DAY);
    return dayOfEpoch;
  }

  public static int getDayOfMonth(GregorianCalendar calendar)
  {
    int dayOfMonth = calendar.get(GregorianCalendar.DAY_OF_MONTH);
    return dayOfMonth;
  }

  private static GregorianCalendar getGregorianCalendar(Date sqlDate)
  {
    GregorianCalendar gregorianCalendar = cache.get(sqlDate);
    if (gregorianCalendar == null)
    {
      gregorianCalendar = new GregorianCalendar();
      gregorianCalendar.setTimeInMillis(sqlDate.getTime());
      cache.put(sqlDate, gregorianCalendar);
    }
    return gregorianCalendar;
  }

  public static int getHour(GregorianCalendar calendar)
  {
    int hour = calendar.get(GregorianCalendar.HOUR_OF_DAY);
    return hour;
  }

  public static int getMinute(GregorianCalendar calendar)
  {
    int minute = calendar.get(GregorianCalendar.MINUTE);
    return minute;
  }

  public static String getMonthName(Date sqlDate)
  {
    return getMonthName(getGregorianCalendar(sqlDate));
  }
  
  public static String getMonthName(GregorianCalendar calendar)
  {
    String monthName = calendar.getDisplayName(GregorianCalendar.MONTH, GregorianCalendar.LONG, Locale.getDefault());
    return monthName;
  }

  public static int getMonthOfEpoch(Date sqlDate)
  {
    return getMonthOfEpoch(getGregorianCalendar(sqlDate));
  }
  
  public static int getMonthOfEpoch(GregorianCalendar calendar)
  {
    int previousYears = calendar.get(GregorianCalendar.YEAR) - 1;
    int previousMonths = previousYears * PERIOD_B_MONTHS_PER_YEAR;
    int monthOfEpoch = previousMonths + calendar.get(GregorianCalendar.MONTH) + 1;
    return monthOfEpoch;
  }

  public static int getMonthOfYear(GregorianCalendar calendar)
  {
    int monthOfYear = calendar.get(GregorianCalendar.MONTH) + 1;
    return monthOfYear;
  }

  public static int getOffsetIntoWeek(Date sqlDate)
  {
    return getOffsetIntoWeek(getGregorianCalendar(sqlDate));
  }
  
  public static int getOffsetIntoWeek(GregorianCalendar calendar)
  {
    // Use ISO-8601 with Monday=1 and Sunday=7 (see http://en.wikipedia.org/wiki/ISO_8601)
    int dayOfWeek = calendar.get(GregorianCalendar.DAY_OF_WEEK);
    int firstDayOfWeek = GregorianCalendar.MONDAY;
    int offsetIntoWeek = 1 + ((7 + dayOfWeek - firstDayOfWeek) % 7);
    return offsetIntoWeek;
  }

  public static int getQuarterOfEpoch(Date sqlDate)
  {
    return getQuarterOfEpoch(getGregorianCalendar(sqlDate));
  }
  
  public static int getQuarterOfEpoch(GregorianCalendar calendar)
  {
    int previousYears = calendar.get(GregorianCalendar.YEAR) - 1;
    int previousQuarters = previousYears * PERIOD_C_QUARTERS_PER_YEAR;
    int quarterOfYear = getQuarterOfYear(calendar);
    int quarterOfEpoch = previousQuarters + quarterOfYear;
    return quarterOfEpoch;
  }

  public static int getQuarterOfYear(Date sqlDate)
  {
    return getQuarterOfYear(getGregorianCalendar(sqlDate));
  }
  
  public static int getQuarterOfYear(GregorianCalendar calendar)
  {
    int monthOfYear = calendar.get(GregorianCalendar.MONTH) + 1;
    int quarterOfYear = (monthOfYear / 4) + 1;
    return quarterOfYear;
  }

  public static int getSecond(GregorianCalendar calendar)
  {
    int second = calendar.get(GregorianCalendar.SECOND);
    return second;
  }

  public static int getWeekOfEpoch(Date sqlDate)
  {
    return getWeekOfEpoch(getGregorianCalendar(sqlDate));
  }
  
  public static int getWeekOfEpoch(GregorianCalendar calendar)
  {
    int dayOfEpoch = getDayOfEpoch(calendar);
    int weekOfEpoch = dayOfEpoch / PERIOD_A_DAYS_PER_WEEK;
    return weekOfEpoch;
  }

  public static int getWeekOfMonth(Date sqlDate)
  {
    return getWeekOfMonth(getGregorianCalendar(sqlDate));
  }
  
  public static int getWeekOfMonth(GregorianCalendar calendar)
  {
    int weekOfMonth = calendar.get(GregorianCalendar.WEEK_OF_MONTH);
    return weekOfMonth;
  }

  public static int getWeekOfYear(Date sqlDate)
  {
    return getWeekOfYear(getGregorianCalendar(sqlDate));
  }
  
  public static int getWeekOfYear(GregorianCalendar calendar)
  {
    int weekOfYear = calendar.get(GregorianCalendar.WEEK_OF_YEAR);
    return weekOfYear;
  }

  public static int getYear(GregorianCalendar calendar)
  {
    int year = calendar.get(GregorianCalendar.YEAR);
    return year;
  }

}
