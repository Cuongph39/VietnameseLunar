package com.khang.vietnameselunar;

import java.util.Calendar;

public class VietnameseLunar {

    public static final double PI = Math.PI;
    public static final double TZ = 7.0;
    public static String[] ngay = new String[]{"Thứ Hai", " Thứ Ba", "Thứ Tư", "Thứ Năm", "Thứ Sáu", "Thứ Bảy", "Chủ Nhật"};
    public static String[] can = new String[]{"Giáp", "Ất", "Bính", "Đinh", "Mậu", "Kỉ", "Canh", "Tân", "Nhâm", "Quý"};
    public static String[] chi = new String[]{"Tý", "Sửu", "Dần", "Mão", "Thìn", "Tị", "Ngọ", "Mùi", "Thân", "Dậu", "Tuất", "Hợi"};
    public static String[] chiT = new String[]{"Dần", "Mão", "Thìn", "Tị", "Ngọ", "Mùi", "Thân", "Dậu", "Tuất", "Hợi", "Tý", "Sửu"};
    private String[] lunarTime = {"Lập Xuân", "Vũ Thủy", "Kinh Trập", "Xuân Phân", "Thanh Minh", "Cốc Vũ", "Lập Hạ", "Tiển Mãn", "Mang Chủng", "Hạ Chí",
            "Tiểu Thử", "Đại Thử", "Lập Thu", "Xử Thử", "Bạch Lộ", "Thu Phân", "Hàn Lộ", "Sương Giáng", "Lập Đông", "Tiểu Tuyết",
            "Đại Tuyết", "Đông Chí", "Tiểu Hàn", "Đại Hàn"};

    /**
     *
     * @param dd
     * @param mm
     * @param yy
     * @return the number of days since 1 January 4713 BC (Julian calendar)
     */
    public static int jdFromDate(int dd, int mm, int yy) {
        int a = (14 - mm) / 12;
        int y = yy + 4800 - a;
        int m = mm + 12 * a - 3;
        int jd = dd + (153 * m + 2) / 5 + 365 * y + y / 4 - y / 100 + y / 400 - 32045;
        if (jd < 2299161) {
            jd = dd + (153 * m + 2) / 5 + 365 * y + y / 4 - 32083;
        }
        //jd = jd - 1721425;
        return jd;
    }

    /**
     * http://www.tondering.dk/claus/calendar.html Section: Is there a formula
     * for calculating the Julian day number?
     *
     * @param jd - the number of days since 1 January 4713 BC (Julian calendar)
     * @return
     */
    public static int[] jdToDate(int jd) {
        int a, b, c;
        if (jd > 2299160) { // After 5/10/1582, Gregorian calendar
            a = jd + 32044;
            b = (4 * a + 3) / 146097;
            c = a - (b * 146097) / 4;
        } else {
            b = 0;
            c = jd + 32082;
        }
        int d = (4 * c + 3) / 1461;
        int e = c - (1461 * d) / 4;
        int m = (5 * e + 2) / 153;
        int day = e - (153 * m + 2) / 5 + 1;
        int month = m + 3 - 12 * (m / 10);
        int year = b * 100 + d - 4800 + m / 10;
        return new int[]{day, month, year};
    }

    /**
     * Solar longitude in degrees Algorithm from: Astronomical Algorithms, by
     * Jean Meeus, 1998
     *
     * @param jdn - number of days since noon UTC on 1 January 4713 BC
     * @return
     */
    public static double SunLongitude(double jdn) {
        //return CC2K.sunLongitude(jdn);
        return SunLongitudeAA98(jdn);
    }

    public static double SunLongitudeAA98(double jdn) {
        double T = (jdn - 2451545.0) / 36525; // Time in Julian centuries from 2000-01-01 12:00:00 GMT
        double T2 = T * T;
        double dr = PI / 180; // degree to radian
        double M = 357.52910 + 35999.05030 * T - 0.0001559 * T2 - 0.00000048 * T * T2; // mean anomaly, degree
        double L0 = 280.46645 + 36000.76983 * T + 0.0003032 * T2; // mean longitude, degree
        double DL = (1.914600 - 0.004817 * T - 0.000014 * T2) * Math.sin(dr * M);
        DL = DL + (0.019993 - 0.000101 * T) * Math.sin(dr * 2 * M) + 0.000290 * Math.sin(dr * 3 * M);
        double L = L0 + DL; // true longitude, degree
        L = L - 360 * (INT(L / 360)); // Normalize to (0, 360)
        return L;
    }

    public static double NewMoon(int k) {
        //return CC2K.newMoonTime(k);
        return NewMoonAA98(k);
    }

    /**
     * Julian day number of the kth new moon after (or before) the New Moon of
     * 1900-01-01 13:51 GMT. Accuracy: 2 minutes Algorithm from: Astronomical
     * Algorithms, by Jean Meeus, 1998
     *
     * @param k
     * @return the Julian date number (number of days since noon UTC on 1
     * January 4713 BC) of the New Moon
     */
    public static double NewMoonAA98(int k) {
        double T = k / 1236.85; // Time in Julian centuries from 1900 January 0.5
        double T2 = T * T;
        double T3 = T2 * T;
        double dr = PI / 180;
        double Jd1 = 2415020.75933 + 29.53058868 * k + 0.0001178 * T2 - 0.000000155 * T3;
        Jd1 = Jd1 + 0.00033 * Math.sin((166.56 + 132.87 * T - 0.009173 * T2) * dr); // Mean new moon
        double M = 359.2242 + 29.10535608 * k - 0.0000333 * T2 - 0.00000347 * T3; // Sun's mean anomaly
        double Mpr = 306.0253 + 385.81691806 * k + 0.0107306 * T2 + 0.00001236 * T3; // Moon's mean anomaly
        double F = 21.2964 + 390.67050646 * k - 0.0016528 * T2 - 0.00000239 * T3; // Moon's argument of latitude
        double C1 = (0.1734 - 0.000393 * T) * Math.sin(M * dr) + 0.0021 * Math.sin(2 * dr * M);
        C1 = C1 - 0.4068 * Math.sin(Mpr * dr) + 0.0161 * Math.sin(dr * 2 * Mpr);
        C1 = C1 - 0.0004 * Math.sin(dr * 3 * Mpr);
        C1 = C1 + 0.0104 * Math.sin(dr * 2 * F) - 0.0051 * Math.sin(dr * (M + Mpr));
        C1 = C1 - 0.0074 * Math.sin(dr * (M - Mpr)) + 0.0004 * Math.sin(dr * (2 * F + M));
        C1 = C1 - 0.0004 * Math.sin(dr * (2 * F - M)) - 0.0006 * Math.sin(dr * (2 * F + Mpr));
        C1 = C1 + 0.0010 * Math.sin(dr * (2 * F - Mpr)) + 0.0005 * Math.sin(dr * (2 * Mpr + M));
        double deltat;
        if (T < -11) {
            deltat = 0.001 + 0.000839 * T + 0.0002261 * T2 - 0.00000845 * T3 - 0.000000081 * T * T3;
        } else {
            deltat = -0.000278 + 0.000265 * T + 0.000262 * T2;
        };
        double JdNew = Jd1 + C1 - deltat;
        return JdNew;
    }

    public static int INT(double d) {
        return (int) Math.floor(d);
    }

    public static double getSunLongitude(int dayNumber, double timeZone) {
        return SunLongitude(dayNumber - 0.5 - timeZone / 24);
    }

    public static int getNewMoonDay(int k, double timeZone) {
        double jd = NewMoon(k);
        return INT(jd + 0.5 + timeZone / 24);
    }

    public static int getLunarMonth11(int yy, double timeZone) {
        double off = jdFromDate(31, 12, yy) - 2415021.076998695;
        int k = INT(off / 29.530588853);
        int nm = getNewMoonDay(k, timeZone);
        int sunLong = INT(getSunLongitude(nm, timeZone) / 30);
        if (sunLong >= 9) {
            nm = getNewMoonDay(k - 1, timeZone);
        }
        return nm;
    }

    public static int getLeapMonthOffset(int a11, double timeZone) {
        int k = INT(0.5 + (a11 - 2415021.076998695) / 29.530588853);
        int last; // Month 11 contains point of sun longutide 3*PI/2 (December solstice)
        int i = 1; // We start with the month following lunar month 11
        int arc = INT(getSunLongitude(getNewMoonDay(k + i, timeZone), timeZone) / 30);
        do {
            last = arc;
            i++;
            arc = INT(getSunLongitude(getNewMoonDay(k + i, timeZone), timeZone) / 30);
        } while (arc != last && i < 14);
        return i - 1;
    }

    /**
     *
     * @param dd
     * @param mm
     * @param yy
     * @param timeZone
     * @return array of [lunarDay, lunarMonth, lunarYear, leapOrNot]
     */
    public static int[] convertSolar2Lunar(int dd, int mm, int yy, double timeZone) {
        int lunarDay, lunarMonth, lunarYear, lunarLeap;
        int dayNumber = jdFromDate(dd, mm, yy);
        int k = INT((dayNumber - 2415021.076998695) / 29.530588853);
        int monthStart = getNewMoonDay(k + 1, timeZone);
        if (monthStart > dayNumber) {
            monthStart = getNewMoonDay(k, timeZone);
        }
        int a11 = getLunarMonth11(yy, timeZone);
        int b11 = a11;
        if (a11 >= monthStart) {
            lunarYear = yy;
            a11 = getLunarMonth11(yy - 1, timeZone);
        } else {
            lunarYear = yy + 1;
            b11 = getLunarMonth11(yy + 1, timeZone);
        }
        lunarDay = dayNumber - monthStart + 1;
        int diff = INT((monthStart - a11) / 29);
        lunarLeap = 0;
        lunarMonth = diff + 11;
        if (b11 - a11 > 365) {
            int leapMonthDiff = getLeapMonthOffset(a11, timeZone);
            if (diff >= leapMonthDiff) {
                lunarMonth = diff + 10;
                if (diff == leapMonthDiff) {
                    lunarLeap = 1;
                }
            }
        }
        if (lunarMonth > 12) {
            lunarMonth = lunarMonth - 12;
        }
        if (lunarMonth >= 11 && diff < 4) {
            lunarYear -= 1;
        }
        return new int[]{lunarDay, lunarMonth, lunarYear, lunarLeap};
    }

    public static int[] convertLunar2Solar(int lunarDay, int lunarMonth, int lunarYear, int lunarLeap, double timeZone) {
        int a11, b11;
        if (lunarMonth < 11) {
            a11 = getLunarMonth11(lunarYear - 1, timeZone);
            b11 = getLunarMonth11(lunarYear, timeZone);
        } else {
            a11 = getLunarMonth11(lunarYear, timeZone);
            b11 = getLunarMonth11(lunarYear + 1, timeZone);
        }
        int k = INT(0.5 + (a11 - 2415021.076998695) / 29.530588853);
        int off = lunarMonth - 11;
        if (off < 0) {
            off += 12;
        }
        if (b11 - a11 > 365) {
            int leapOff = getLeapMonthOffset(a11, timeZone);
            int leapMonth = leapOff - 2;
            if (leapMonth < 0) {
                leapMonth += 12;
            }
            if (lunarLeap != 0 && lunarMonth != leapMonth) {
                System.out.println("Invalid input!");
                return new int[]{0, 0, 0};
            } else if (lunarLeap != 0 || off >= leapOff) {
                off += 1;
            }
        }
        int monthStart = getNewMoonDay(k + off, timeZone);
        return jdToDate(monthStart + lunarDay - 1);
    }

    public static String zodiacTime(int jd) {
        String lunarDate = convertToLunarDate(jd);
        String dayChi = lunarDate.substring(lunarDate.indexOf(" ")).trim();
        String zodiacTime = "";

        if (dayChi.equalsIgnoreCase("dần") || dayChi.equalsIgnoreCase("thân")) {
            zodiacTime = "Tí (23-1), Sửu (1-3), Thìn (7-9), Tỵ (9-11), Mùi (13-15), Tuất (19-21)";
        }
        if (dayChi.equalsIgnoreCase("mão") || dayChi.equalsIgnoreCase("dậu")) {
            zodiacTime = "Tí (23-1), Dần (3-5), Mão (5-7), Ngọ (11-13), Mùi (13-15), Dậu (17-19)";
        }
        if (dayChi.equalsIgnoreCase("thìn") || dayChi.equalsIgnoreCase("tuất")) {
            zodiacTime = "Dần (3-5), Thìn (7-9), Tỵ (9-11), Thân (15-17), Dậu (17-19), Hợi (21-23)";
        }
        if (dayChi.equalsIgnoreCase("tị") || dayChi.equalsIgnoreCase("hợi")) {
            zodiacTime = "Sửu (1-3), Thìn (7-9), Ngọ (11-13), Mùi (13-15), Tuất (19-21), Hợi (21-23)";
        }
        if (dayChi.equalsIgnoreCase("tí") || dayChi.equalsIgnoreCase("ngọ")) {
            zodiacTime = "Tí (23-1), Sửu (1-3), Mão (5-7), Ngọ (11-13), Thân (15-17), Dậu (17-19)";
        }
        if (dayChi.equalsIgnoreCase("sửu") || dayChi.equalsIgnoreCase("mùi")) {
            zodiacTime = "Dần (3-5), Mão (5-7), Tỵ (9-11), Thân (15-17), Tuất (19-21), Hợi (21-23)";
        }
        return zodiacTime;
    }

    public static String convertToLunarTime(double h, int jd) {
        int canIdxH;
        int chiIdxH;
        int canIdxNN = (jd + 9) % 10;

        if (h >= 1 && h < 3) {
            chiIdxH = 1;
            if (canIdxNN == 0 || canIdxNN == 5) {
                canIdxH = 1;
            } else if (canIdxNN == 1 || canIdxNN == 6) {
                canIdxH = 3;
            } else if (canIdxNN == 2 || canIdxNN == 7) {
                canIdxH = 5;
            } else if (canIdxNN == 3 || canIdxNN == 8) {
                canIdxH = 7;
            } else {
                canIdxH = 9;
            }
        } else if (h >= 3 && h < 5) {
            chiIdxH = 2;
            if (canIdxNN == 0 || canIdxNN == 5) {
                canIdxH = 2;
            } else if (canIdxNN == 1 || canIdxNN == 6) {
                canIdxH = 4;
            } else if (canIdxNN == 2 || canIdxNN == 7) {
                canIdxH = 6;
            } else if (canIdxNN == 3 || canIdxNN == 8) {
                canIdxH = 8;
            } else {
                canIdxH = 0;
            }
        } else if (h >= 5 && h < 7) {
            chiIdxH = 3;
            if (canIdxNN == 0 || canIdxNN == 5) {
                canIdxH = 3;
            } else if (canIdxNN == 1 || canIdxNN == 6) {
                canIdxH = 5;
            } else if (canIdxNN == 2 || canIdxNN == 7) {
                canIdxH = 7;
            } else if (canIdxNN == 3 || canIdxNN == 8) {
                canIdxH = 9;
            } else {
                canIdxH = 1;
            }
        } else if (h >= 7 && h < 9) {
            chiIdxH = 4;
            if (canIdxNN == 0 || canIdxNN == 5) {
                canIdxH = 4;
            } else if (canIdxNN == 1 || canIdxNN == 6) {
                canIdxH = 6;
            } else if (canIdxNN == 2 || canIdxNN == 7) {
                canIdxH = 8;
            } else if (canIdxNN == 3 || canIdxNN == 8) {
                canIdxH = 10;
            } else {
                canIdxH = 2;
            }
        } else if (h >= 9 && h < 11) {
            chiIdxH = 5;
            if (canIdxNN == 0 || canIdxNN == 5) {
                canIdxH = 5;
            } else if (canIdxNN == 1 || canIdxNN == 6) {
                canIdxH = 7;
            } else if (canIdxNN == 2 || canIdxNN == 7) {
                canIdxH = 9;
            } else if (canIdxNN == 3 || canIdxNN == 8) {
                canIdxH = 1;
            } else {
                canIdxH = 3;
            }
        } else if (h >= 11 && h < 13) {
            chiIdxH = 6;
            if (canIdxNN == 0 || canIdxNN == 5) {
                canIdxH = 6;
            } else if (canIdxNN == 1 || canIdxNN == 6) {
                canIdxH = 8;
            } else if (canIdxNN == 2 || canIdxNN == 7) {
                canIdxH = 0;
            } else if (canIdxNN == 3 || canIdxNN == 8) {
                canIdxH = 2;
            } else {
                canIdxH = 4;
            }
        } else if (h >= 13 && h < 15) {
            chiIdxH = 7;
            if (canIdxNN == 0 || canIdxNN == 5) {
                canIdxH = 7;
            } else if (canIdxNN == 1 || canIdxNN == 6) {
                canIdxH = 9;
            } else if (canIdxNN == 2 || canIdxNN == 7) {
                canIdxH = 1;
            } else if (canIdxNN == 3 || canIdxNN == 8) {
                canIdxH = 3;
            } else {
                canIdxH = 5;
            }
        } else if (h >= 15 && h < 17) {
            chiIdxH = 8;
            if (canIdxNN == 0 || canIdxNN == 5) {
                canIdxH = 8;
            } else if (canIdxNN == 1 || canIdxNN == 6) {
                canIdxH = 0;
            } else if (canIdxNN == 2 || canIdxNN == 7) {
                canIdxH = 2;
            } else if (canIdxNN == 3 || canIdxNN == 8) {
                canIdxH = 4;
            } else {
                canIdxH = 6;
            }
        } else if (h >= 17 && h < 19) {
            chiIdxH = 9;
            if (canIdxNN == 0 || canIdxNN == 5) {
                canIdxH = 9;
            } else if (canIdxNN == 1 || canIdxNN == 6) {
                canIdxH = 1;
            } else if (canIdxNN == 2 || canIdxNN == 7) {
                canIdxH = 3;
            } else if (canIdxNN == 3 || canIdxNN == 8) {
                canIdxH = 5;
            } else {
                canIdxH = 7;
            }
        } else if (h >= 19 && h < 21) {
            chiIdxH = 10;
            if (canIdxNN == 0 || canIdxNN == 5) {
                canIdxH = 0;
            } else if (canIdxNN == 1 || canIdxNN == 6) {
                canIdxH = 2;
            } else if (canIdxNN == 2 || canIdxNN == 7) {
                canIdxH = 4;
            } else if (canIdxNN == 3 || canIdxNN == 8) {
                canIdxH = 6;
            } else {
                canIdxH = 8;
            }
        } else if (h >= 21 && h < 23) {
            chiIdxH = 11;
            if (canIdxNN == 0 || canIdxNN == 5) {
                canIdxH = 1;
            } else if (canIdxNN == 1 || canIdxNN == 6) {
                canIdxH = 3;
            } else if (canIdxNN == 2 || canIdxNN == 7) {
                canIdxH = 5;
            } else if (canIdxNN == 3 || canIdxNN == 8) {
                canIdxH = 7;
            } else {
                canIdxH = 9;
            }
        } else {
            chiIdxH = 0;
            if (canIdxNN == 0 || canIdxNN == 5) {
                canIdxH = 0;
            } else if (canIdxNN == 1 || canIdxNN == 6) {
                canIdxH = 2;
            } else if (canIdxNN == 2 || canIdxNN == 7) {
                canIdxH = 4;
            } else if (canIdxNN == 3 || canIdxNN == 8) {
                canIdxH = 6;
            } else {
                canIdxH = 8;
            }
        }
        return can[canIdxH] + " " + chi[chiIdxH];
    }

    public static String convertToLunarDate(int jd) {
        // Tinh Can-Chi cua ngày;
        int canIdxNN = (jd + 9) % 10;
        int chiIdxNN = (jd + 11) % 12;
        return can[canIdxNN] + " " + chiT[chiIdxNN];
    }

    public static String convertToLunarYear(int jd) {
        // Tinh Can-Chi cua năm
        int[] s = jdToDate(jd);
        int[] l = convertSolar2Lunar(s[0], s[1], s[2], TZ);
        int[] s2 = convertLunar2Solar(l[0], l[1], l[2], l[3], TZ);
        int canIdxN = (l[2] + 6) % 10;
        int chiIdxN = (l[2] + 8) % 12;
        return can[canIdxN] + " " + chi[chiIdxN];
    }

    public static String convertToLunarDay(int jd) {
        // Tinh Can-Chi cua năm
        int[] s = jdToDate(jd);
        int ngayIdx = jd % 7;
        return ngay[ngayIdx];
    }

    public static String convertToLunarMonth(int jd) {
        // Tinh Can-Chi cua tháng
        int[] s = jdToDate(jd);
        int[] l = convertSolar2Lunar(s[0], s[1], s[2], TZ);
        int[] s2 = convertLunar2Solar(l[0], l[1], l[2], l[3], TZ);
        int canIdxT = (l[2] * 12 + l[1] + 3) % 10;
        return can[canIdxT] + " " + chiT[l[1] - 1];
    }

    public String lunarPeriod() {
        int jd = jdFromDate(Calendar.DAY_OF_MONTH, Calendar.MONTH + 1, Calendar.YEAR);
        int[] s = jdToDate(jd);
        int[] l = convertSolar2Lunar(s[0], s[1], s[2], TZ);

        Calendar tmp = Calendar.getInstance();
        tmp.set(l[0], l[1], l[2]);
        String zd = "";
        Calendar c1 = Calendar.getInstance();
        c1.set(4, 2, l[2]);
        Calendar c2 = Calendar.getInstance();
        c2.set(18, 2, l[2]);

        //Lap Xuan (From 4/2 to 18/2)
        if (((tmp.getTime().after(c1.getTime())
                || (tmp.getTime().equals(c1.getTime())))
                && ((tmp.before(c2.getTime()))
                || (tmp.getTime().equals(c2.getTime()))))) {
            zd = lunarTime[0];
        }

        c1.set(19, 2, l[2]);
        c2.set(5, 3, l[2]);
        //Vu Thuy (From 19/2 to 5/3)
        if (((tmp.getTime().after(c1.getTime())
                || (tmp.getTime().equals(c1.getTime())))
                && ((tmp.before(c2.getTime()))
                || (tmp.getTime().equals(c2.getTime()))))) {
            zd = lunarTime[1];
        }

        c1.set(6, 3, l[2]);
        c2.set(20, 3, l[2]);
        //Kinh Trap (From 6/3 to 20/3)
        if (((tmp.getTime().after(c1.getTime())
                || (tmp.getTime().equals(c1.getTime())))
                && ((tmp.before(c2.getTime()))
                || (tmp.getTime().equals(c2.getTime()))))) {
            zd = lunarTime[2];
        }

        c1.set(21, 3, l[2]);
        c2.set(4, 4, l[2]);
        //Xuan Phan (From 21/3 to 4/4)
        if (((tmp.getTime().after(c1.getTime())
                || (tmp.getTime().equals(c1.getTime())))
                && ((tmp.before(c2.getTime()))
                || (tmp.getTime().equals(c2.getTime()))))) {
            zd = lunarTime[3];
        }

        c1.set(5, 4, l[2]);
        c2.set(19, 4, l[2]);
        //Thanh Minh (From 5/4 to 19/4)
        if (((tmp.getTime().after(c1.getTime())
                || (tmp.getTime().equals(c1.getTime())))
                && ((tmp.before(c2.getTime()))
                || (tmp.getTime().equals(c2.getTime()))))) {
            zd = lunarTime[4];
        }

        c1.set(20, 4, l[2]);
        c2.set(5, 5, l[2]);
        //Coc Vu (From 20/4 to 5/5)
        if (((tmp.getTime().after(c1.getTime())
                || (tmp.getTime().equals(c1.getTime())))
                && ((tmp.before(c2.getTime()))
                || (tmp.getTime().equals(c2.getTime()))))) {
            zd = lunarTime[5];
        }

        c1.set(6, 5, l[2]);
        c2.set(20, 5, l[2]);
        //Lap Ha (From 6/5 to 20/5)
        if (((tmp.getTime().after(c1.getTime())
                || (tmp.getTime().equals(c1.getTime())))
                && ((tmp.before(c2.getTime()))
                || (tmp.getTime().equals(c2.getTime()))))) {
            zd = lunarTime[6];
        }

        c1.set(21, 5, l[2]);
        c2.set(5, 6, l[2]);
        //Tieu Man (From 21/5 to 5/6)
        if (((tmp.getTime().after(c1.getTime())
                || (tmp.getTime().equals(c1.getTime())))
                && ((tmp.before(c2.getTime()))
                || (tmp.getTime().equals(c2.getTime()))))) {
            zd = lunarTime[7];
        }

        c1.set(6, 6, l[2]);
        c2.set(20, 6, l[2]);
        //Mang Chung (From 6/6 to 20/6)
        if (((tmp.getTime().after(c1.getTime())
                || (tmp.getTime().equals(c1.getTime())))
                && ((tmp.before(c2.getTime()))
                || (tmp.getTime().equals(c2.getTime()))))) {
            zd = lunarTime[8];
        }

        c1.set(21, 6, l[2]);
        c2.set(6, 7, l[2]);
        //Ha Chi (From 21/6 to 6/7)
        if (((tmp.getTime().after(c1.getTime())
                || (tmp.getTime().equals(c1.getTime())))
                && ((tmp.before(c2.getTime()))
                || (tmp.getTime().equals(c2.getTime()))))) {
            zd = lunarTime[9];
        }

        c1.set(7, 7, l[2]);
        c2.set(22, 7, l[2]);
        //Tieu Thu (From 7/7 to 22/7)
        if (((tmp.getTime().after(c1.getTime())
                || (tmp.getTime().equals(c1.getTime())))
                && ((tmp.before(c2.getTime()))
                || (tmp.getTime().equals(c2.getTime()))))) {
            zd = lunarTime[10];
        }

        c1.set(23, 7, l[2]);
        c2.set(7, 8, l[2]);
        //Dai Thu (From 23/7 to 7/8)
        if (((tmp.getTime().after(c1.getTime())
                || (tmp.getTime().equals(c1.getTime())))
                && ((tmp.before(c2.getTime()))
                || (tmp.getTime().equals(c2.getTime()))))) {
            zd = lunarTime[11];
        }

        c1.set(8, 8, l[2]);
        c2.set(22, 8, l[2]);
        //Lap Thu (From 8/8 to 22/8)
        if (((tmp.getTime().after(c1.getTime())
                || (tmp.getTime().equals(c1.getTime())))
                && ((tmp.before(c2.getTime()))
                || (tmp.getTime().equals(c2.getTime()))))) {
            zd = lunarTime[12];
        }

        c1.set(23, 8, l[2]);
        c2.set(7, 9, l[2]);
        //Xu Thu (From 23/8 to 7/9)
        if (((tmp.getTime().after(c1.getTime())
                || (tmp.getTime().equals(c1.getTime())))
                && ((tmp.before(c2.getTime()))
                || (tmp.getTime().equals(c2.getTime()))))) {
            zd = lunarTime[13];
        }

        c1.set(8, 9, l[2]);
        c2.set(22, 9, l[2]);
        //Bach Lo (From 8/9 to 22/9)
        if (((tmp.getTime().after(c1.getTime())
                || (tmp.getTime().equals(c1.getTime())))
                && ((tmp.before(c2.getTime()))
                || (tmp.getTime().equals(c2.getTime()))))) {
            zd = lunarTime[14];
        }

        c1.set(23, 9, l[2]);
        c2.set(7, 10, l[2]);
        //Thu Phan (From 23/9 to 7/10)
        if (((tmp.getTime().after(c1.getTime())
                || (tmp.getTime().equals(c1.getTime())))
                && ((tmp.before(c2.getTime()))
                || (tmp.getTime().equals(c2.getTime()))))) {
            zd = lunarTime[15];
        }

        c1.set(8, 10, l[2]);
        c2.set(22, 10, l[2]);
        //Han Lo (From 8/10 to 22/10)
        if (((tmp.getTime().after(c1.getTime())
                || (tmp.getTime().equals(c1.getTime())))
                && ((tmp.before(c2.getTime()))
                || (tmp.getTime().equals(c2.getTime()))))) {
            zd = lunarTime[16];
        }

        c1.set(23, 10, l[2]);
        c2.set(6, 11, l[2]);
        //Suong Giang (From 23/10 to 6/11)
        if (((tmp.getTime().after(c1.getTime())
                || (tmp.getTime().equals(c1.getTime())))
                && ((tmp.before(c2.getTime()))
                || (tmp.getTime().equals(c2.getTime()))))) {
            zd = lunarTime[17];
        }

        c1.set(7, 11, l[2]);
        c2.set(21, 11, l[2]);
        //Lap Dong (From 7/11 to 21/11)
        if (((tmp.getTime().after(c1.getTime())
                || (tmp.getTime().equals(c1.getTime())))
                && ((tmp.before(c2.getTime()))
                || (tmp.getTime().equals(c2.getTime()))))) {
            zd = lunarTime[18];
        }

        c1.set(22, 11, l[2]);
        c2.set(5, 12, l[2]);
        //Tieu Tuyet (From 22/11 to 5/12)
        if (((tmp.getTime().after(c1.getTime())
                || (tmp.getTime().equals(c1.getTime())))
                && ((tmp.before(c2.getTime()))
                || (tmp.getTime().equals(c2.getTime()))))) {
            zd = lunarTime[19];
        }

        c1.set(6, 12, l[2]);
        c2.set(21, 12, l[2]);
        //Dai Tuyet (From 6/12 to 21/12)
        if (((tmp.getTime().after(c1.getTime())
                || (tmp.getTime().equals(c1.getTime())))
                && ((tmp.before(c2.getTime()))
                || (tmp.getTime().equals(c2.getTime()))))) {
            zd = lunarTime[20];
        }

        c1.set(22, 12, l[2]);
        c2.set(31, 12, l[2]);
        //Dong Chi (From 22/12 to 4/1)
        if (((tmp.getTime().after(c1.getTime())
                || (tmp.getTime().equals(c1.getTime())))
                && ((tmp.before(c2.getTime()))
                || (tmp.getTime().equals(c2.getTime()))))) {
            zd = lunarTime[21];
        }

        c1.set(1, 1, l[2]);
        c2.set(4, 1, l[2]);
        //Dong Chi (From 22/12 to 4/1)
        if (((tmp.getTime().after(c1.getTime())
                || (tmp.getTime().equals(c1.getTime())))
                && ((tmp.before(c2.getTime()))
                || (tmp.getTime().equals(c2.getTime()))))) {
            zd = lunarTime[21];
        }

        c1.set(5, 11, l[2]);
        c2.set(19, 1, l[2]);
        //Tieu Han (From 5/1 to 19/1)
        if (((tmp.getTime().after(c1.getTime())
                || (tmp.getTime().equals(c1.getTime())))
                && ((tmp.before(c2.getTime()))
                || (tmp.getTime().equals(c2.getTime()))))) {
            zd = lunarTime[22];
        }

        c1.set(20, 1, l[2]);
        c2.set(3, 2, l[2]);
        //Dai Han (From 20/1 to 3/2)
        if (((tmp.getTime().after(c1.getTime())
                || (tmp.getTime().equals(c1.getTime())))
                && ((tmp.before(c2.getTime()))
                || (tmp.getTime().equals(c2.getTime()))))) {
            zd = lunarTime[23];
        }
        return zd;
    }

    public static void main(String[] args) {
        double TZ = 7.0;
        int jd = jdFromDate(29, 8, 2018);
        int[] s = jdToDate(jd);
        int[] l = convertSolar2Lunar(s[0], s[1], s[2], TZ);
        int[] s2 = convertLunar2Solar(l[0], l[1], l[2], l[3], TZ);

        if (s[0] == s2[0] && s[1] == s2[1] && s[2] == s2[2]) {
            System.out.println("OK! " + s[0] + "/" + s[1] + "/" + s[2] + " -> " + l[0] + "/" + l[1] + "/" + l[2] + (l[3] == 0 ? "" : " leap"));
            System.out.println("\n");
            System.out.println(convertToLunarDay(jd));
            System.out.println("Giờ " + convertToLunarTime(9.5, jd));
            System.out.println("Ngày " + convertToLunarDate(jd));
            System.out.println("Tháng " + convertToLunarMonth(jd));
            System.out.println("Năm " + convertToLunarYear(jd));
            System.out.print("Giờ hoàng đạo: " + zodiacTime(jd));
        } else {
            System.err.println("ERROR! " + s[0] + "/" + s[1] + "/" + s[2] + " -> " + l[0] + "/" + l[1] + "/" + l[2] + (l[3] == 0 ? "" : " leap"));
        }
    }

}
