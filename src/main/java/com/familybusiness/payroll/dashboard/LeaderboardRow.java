package com.familybusiness.payroll.dashboard;

import java.math.BigDecimal;

public record LeaderboardRow(String name, String detail, BigDecimal amount, String href) {
}
