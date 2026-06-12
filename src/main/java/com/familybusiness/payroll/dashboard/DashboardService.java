package com.familybusiness.payroll.dashboard;

import com.familybusiness.payroll.contractor.WorkSite;
import com.familybusiness.payroll.contractor.WorkSiteRepository;
import com.familybusiness.payroll.contractor.WorkSiteStatus;
import com.familybusiness.payroll.employee.Employee;
import com.familybusiness.payroll.employee.EmployeeRepository;
import com.familybusiness.payroll.employee.EmploymentStatus;
import com.familybusiness.payroll.workhour.PaymentStatus;
import com.familybusiness.payroll.workhour.WorkHour;
import com.familybusiness.payroll.workhour.WorkHourRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    private final EmployeeRepository employeeRepository;
    private final WorkHourRepository workHourRepository;
    private final WorkSiteRepository workSiteRepository;

    public DashboardService(
            EmployeeRepository employeeRepository,
            WorkHourRepository workHourRepository,
            WorkSiteRepository workSiteRepository
    ) {
        this.employeeRepository = employeeRepository;
        this.workHourRepository = workHourRepository;
        this.workSiteRepository = workSiteRepository;
    }

    @Transactional(readOnly = true)
    public DashboardSummary buildSummary() {
        List<Employee> employees = employeeRepository.findAllByOrderByFullNameAsc();
        List<WorkHour> workHours = workHourRepository.findAllByOrderByWorkDateDescEmployeeFullNameAsc();
        List<WorkSite> workSites = workSiteRepository.findAllByOrderByLocationAsc();

        BigDecimal totalRevenue = workSites.stream()
                .map(this::siteTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal payrollCost = workHours.stream()
                .map(WorkHour::getTotalPaymentAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal paidPayroll = workHours.stream()
                .map(this::paidAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal unpaidPayroll = payrollCost.subtract(paidPayroll);
        if (unpaidPayroll.signum() < 0) {
            unpaidPayroll = BigDecimal.ZERO;
        }
        BigDecimal estimatedProfit = totalRevenue.subtract(payrollCost);
        long activeEmployees = employees.stream()
                .filter(employee -> employee.getEmploymentStatus() == EmploymentStatus.ACTIVE)
                .count();
        long openJobs = workSites.stream()
                .filter(site -> site.getStatus() == WorkSiteStatus.IN_PROGRESS)
                .count();

        List<DashboardMetric> metrics = List.of(
                new DashboardMetric("Quoted Revenue", money(totalRevenue), workSites.size() + " total work sites", "revenue"),
                new DashboardMetric("Estimated Profit", money(estimatedProfit), "Revenue minus employee payroll", estimatedProfit.signum() >= 0 ? "profit" : "warning"),
                new DashboardMetric("Payroll Cost", money(payrollCost), money(unpaidPayroll) + " still unpaid", "payroll"),
                new DashboardMetric("Open Jobs", Long.toString(openJobs), activeEmployees + " active employees", "jobs")
        );

        return new DashboardSummary(
                metrics,
                monthlyTrends(workSites, workHours),
                serviceMix(workSites),
                jobStatusMix(workSites),
                payrollStatusMix(workHours),
                topCustomers(workSites),
                unpaidPayroll(workHours),
                activeJobs(workSites)
        );
    }

    private List<MonthlyTrend> monthlyTrends(List<WorkSite> workSites, List<WorkHour> workHours) {
        YearMonth currentMonth = YearMonth.now();
        Map<YearMonth, MonthlyTotals> totals = new LinkedHashMap<>();
        for (int index = 5; index >= 0; index--) {
            totals.put(currentMonth.minusMonths(index), new MonthlyTotals());
        }

        workSites.stream()
                .filter(site -> site.getInvoiceDate() != null)
                .forEach(site -> {
                    YearMonth month = YearMonth.from(site.getInvoiceDate());
                    MonthlyTotals monthlyTotals = totals.get(month);
                    if (monthlyTotals != null) {
                        monthlyTotals.revenue = monthlyTotals.revenue.add(siteTotal(site));
                    }
                });

        workHours.stream()
                .filter(workHour -> workHour.getWorkDate() != null)
                .forEach(workHour -> {
                    YearMonth month = YearMonth.from(workHour.getWorkDate());
                    MonthlyTotals monthlyTotals = totals.get(month);
                    if (monthlyTotals != null) {
                        monthlyTotals.payroll = monthlyTotals.payroll.add(workHour.getTotalPaymentAmount());
                    }
                });

        return totals.entrySet().stream()
                .map(entry -> {
                    MonthlyTotals value = entry.getValue();
                    String label = entry.getKey().getMonth().getDisplayName(TextStyle.SHORT, Locale.CANADA)
                            + " " + entry.getKey().getYear();
                    return new MonthlyTrend(label, value.revenue, value.payroll, value.revenue.subtract(value.payroll));
                })
                .toList();
    }

    private List<ChartSlice> serviceMix(List<WorkSite> workSites) {
        Map<String, BigDecimal> totals = new LinkedHashMap<>();
        workSites.forEach(site -> totals.merge(
                site.getServiceTypeDisplayName(),
                siteTotal(site),
                BigDecimal::add
        ));
        return chartSlices(totals);
    }

    private List<ChartSlice> jobStatusMix(List<WorkSite> workSites) {
        Map<WorkSiteStatus, BigDecimal> totals = new EnumMap<>(WorkSiteStatus.class);
        for (WorkSiteStatus status : WorkSiteStatus.values()) {
            totals.put(status, BigDecimal.ZERO);
        }
        workSites.forEach(site -> totals.merge(site.getStatus(), BigDecimal.ONE, BigDecimal::add));
        return totals.entrySet().stream()
                .map(entry -> new ChartSlice(displayEnum(entry.getKey().name()), entry.getValue()))
                .toList();
    }

    private List<ChartSlice> payrollStatusMix(List<WorkHour> workHours) {
        Map<PaymentStatus, BigDecimal> totals = new EnumMap<>(PaymentStatus.class);
        for (PaymentStatus status : PaymentStatus.values()) {
            totals.put(status, BigDecimal.ZERO);
        }
        workHours.forEach(workHour -> totals.merge(
                workHour.getPaymentStatus(),
                workHour.getTotalPaymentAmount(),
                BigDecimal::add
        ));
        return totals.entrySet().stream()
                .map(entry -> new ChartSlice(displayEnum(entry.getKey().name()), entry.getValue()))
                .toList();
    }

    private List<LeaderboardRow> topCustomers(List<WorkSite> workSites) {
        Map<Long, CustomerTotal> totals = new LinkedHashMap<>();
        workSites.forEach(site -> {
            Long contractorId = site.getContractor().getId();
            CustomerTotal total = totals.computeIfAbsent(
                    contractorId,
                    id -> new CustomerTotal(site.getContractor().getName(), BigDecimal.ZERO)
            );
            total.amount = total.amount.add(siteTotal(site));
        });

        return totals.entrySet().stream()
                .sorted((first, second) -> second.getValue().amount.compareTo(first.getValue().amount))
                .limit(5)
                .map(entry -> new LeaderboardRow(
                        entry.getValue().name,
                        "Quoted work",
                        entry.getValue().amount,
                        "/customers/" + entry.getKey()
                ))
                .toList();
    }

    private List<LeaderboardRow> unpaidPayroll(List<WorkHour> workHours) {
        return workHours.stream()
                .filter(workHour -> workHour.getPaymentStatus() != PaymentStatus.PAID)
                .collect(Collectors.groupingBy(
                        workHour -> workHour.getEmployee().getId(),
                        Collectors.reducing(BigDecimal.ZERO, WorkHour::getRemainingPaymentAmount, BigDecimal::add)
                ))
                .entrySet()
                .stream()
                .filter(entry -> entry.getValue().signum() > 0)
                .sorted(Map.Entry.<Long, BigDecimal>comparingByValue().reversed())
                .limit(5)
                .map(entry -> {
                    String employeeName = workHours.stream()
                            .filter(workHour -> workHour.getEmployee().getId().equals(entry.getKey()))
                            .findFirst()
                            .map(workHour -> workHour.getEmployee().getFullName())
                            .orElse("Employee");
                    return new LeaderboardRow(
                            employeeName,
                            "Outstanding payroll",
                            entry.getValue(),
                            "/work-hours?employeeId=" + entry.getKey()
                    );
                })
                .toList();
    }

    private List<LeaderboardRow> activeJobs(List<WorkSite> workSites) {
        return workSites.stream()
                .filter(site -> site.getStatus() == WorkSiteStatus.IN_PROGRESS)
                .sorted(Comparator.comparing(WorkSite::getLocation, String.CASE_INSENSITIVE_ORDER))
                .limit(5)
                .map(site -> new LeaderboardRow(
                        site.getLocation(),
                        site.getContractor().getName(),
                        siteTotal(site),
                        "/customers/" + site.getContractor().getId()
                ))
                .toList();
    }

    private List<ChartSlice> chartSlices(Map<String, BigDecimal> totals) {
        List<ChartSlice> slices = new ArrayList<>();
        totals.entrySet().stream()
                .sorted(Map.Entry.<String, BigDecimal>comparingByValue().reversed())
                .forEach(entry -> slices.add(new ChartSlice(entry.getKey(), entry.getValue())));
        return slices;
    }

    private BigDecimal siteTotal(WorkSite site) {
        return safe(site.getQuotedAmount()).add(site.getGstAmount());
    }

    private BigDecimal paidAmount(WorkHour workHour) {
        if (workHour.getPaymentStatus() == PaymentStatus.PAID) {
            return workHour.getTotalPaymentAmount();
        }
        if (workHour.getPaymentStatus() == PaymentStatus.PARTIAL) {
            return workHour.getPartialPaymentAmount();
        }
        return BigDecimal.ZERO;
    }

    private BigDecimal safe(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private String money(BigDecimal value) {
        return NumberFormat.getCurrencyInstance(Locale.CANADA).format(value);
    }

    private String displayEnum(String value) {
        String[] words = value.toLowerCase(Locale.CANADA).split("_");
        List<String> displayWords = new ArrayList<>();
        for (String word : words) {
            displayWords.add(word.substring(0, 1).toUpperCase(Locale.CANADA) + word.substring(1));
        }
        return String.join(" ", displayWords);
    }

    private static class MonthlyTotals {
        private BigDecimal revenue = BigDecimal.ZERO;
        private BigDecimal payroll = BigDecimal.ZERO;
    }

    private static class CustomerTotal {
        private final String name;
        private BigDecimal amount;

        private CustomerTotal(String name, BigDecimal amount) {
            this.name = name;
            this.amount = amount;
        }
    }
}
