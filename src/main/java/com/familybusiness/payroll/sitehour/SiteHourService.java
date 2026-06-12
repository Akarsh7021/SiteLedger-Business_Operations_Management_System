package com.familybusiness.payroll.sitehour;

import com.familybusiness.payroll.contractor.WorkSite;
import com.familybusiness.payroll.contractor.WorkSiteRepository;
import com.familybusiness.payroll.contractor.WorkSiteStatus;
import com.familybusiness.payroll.workhour.WorkHour;
import com.familybusiness.payroll.workhour.WorkHourRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
@Transactional
public class SiteHourService {

    private final WorkSiteRepository workSiteRepository;
    private final WorkHourRepository workHourRepository;

    public SiteHourService(WorkSiteRepository workSiteRepository, WorkHourRepository workHourRepository) {
        this.workSiteRepository = workSiteRepository;
        this.workHourRepository = workHourRepository;
    }

    @Transactional(readOnly = true)
    public List<SiteHourSummary> findSiteHourSummaries() {
        return findSiteHourSummaries(null, null);
    }

    @Transactional(readOnly = true)
    public List<SiteHourSummary> findSiteHourSummaries(String search, WorkSiteStatus status) {
        List<WorkSite> workSites = workSiteRepository.findAllByOrderByLocationAsc();
        List<WorkHour> workHours = workHourRepository.findAllWithWorkSite();
        List<SiteHourSummary> summaries = new ArrayList<>();
        String cleanSearch = search == null ? "" : search.trim().toLowerCase(Locale.CANADA);

        for (WorkSite workSite : workSites) {
            if (status != null && workSite.getStatus() != status) {
                continue;
            }
            if (!cleanSearch.isBlank() && !matchesSearch(workSite, cleanSearch)) {
                continue;
            }
            BigDecimal accumulatedHours = BigDecimal.ZERO;
            BigDecimal totalSpent = BigDecimal.ZERO;
            Map<String, BigDecimal> employeeHours = new LinkedHashMap<>();

            for (WorkHour workHour : workHours) {
                if (workHour.getWorkSite() == null || !workSite.getId().equals(workHour.getWorkSite().getId())) {
                    continue;
                }
                BigDecimal hours = workHour.getTotalHours();
                accumulatedHours = accumulatedHours.add(hours);
                totalSpent = totalSpent.add(workHour.getTotalPaymentAmount());
                employeeHours.merge(workHour.getEmployee().getFullName(), hours, BigDecimal::add);
            }

            List<SiteEmployeeHours> employeeBreakdown = employeeHours.entrySet().stream()
                    .map(entry -> new SiteEmployeeHours(entry.getKey(), entry.getValue()))
                    .toList();
            summaries.add(new SiteHourSummary(workSite, accumulatedHours, totalSpent, employeeBreakdown));
        }

        return summaries;
    }

    private boolean matchesSearch(WorkSite workSite, String search) {
        return contains(workSite.getLocation(), search)
                || contains(workSite.getServiceTypeDisplayName(), search)
                || contains(workSite.getContractor().getName(), search)
                || contains(workSite.getContractor().getBillingName(), search);
    }

    private boolean contains(String value, String search) {
        return value != null && value.toLowerCase(Locale.CANADA).contains(search);
    }

    public void updateStatus(Long workSiteId, WorkSiteStatus status) {
        WorkSite workSite = workSiteRepository.findById(workSiteId)
                .orElseThrow(() -> new IllegalArgumentException("Work site not found: " + workSiteId));
        workSite.setStatus(status);
        workSiteRepository.save(workSite);
    }
}
