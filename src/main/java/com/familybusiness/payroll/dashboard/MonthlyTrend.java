package com.familybusiness.payroll.dashboard;

import java.math.BigDecimal;

public record MonthlyTrend(String label, BigDecimal revenue, BigDecimal payroll, BigDecimal profit) {
}
