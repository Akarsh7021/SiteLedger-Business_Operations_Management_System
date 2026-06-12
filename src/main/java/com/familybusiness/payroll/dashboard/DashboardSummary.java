package com.familybusiness.payroll.dashboard;

import java.util.List;

public record DashboardSummary(
        List<DashboardMetric> metrics,
        List<MonthlyTrend> monthlyTrends,
        List<ChartSlice> serviceMix,
        List<ChartSlice> jobStatusMix,
        List<ChartSlice> payrollStatusMix,
        List<LeaderboardRow> topCustomers,
        List<LeaderboardRow> unpaidPayroll,
        List<LeaderboardRow> activeJobs
) {
}
