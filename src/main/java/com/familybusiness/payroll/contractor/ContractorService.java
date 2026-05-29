package com.familybusiness.payroll.contractor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class ContractorService {

    // Change this value later if the square-foot quote rate changes.
    private static final BigDecimal SQUARE_FOOT_RATE = new BigDecimal("0.50");
    private static final BigDecimal GST_RATE = new BigDecimal("0.05");

    private final ContractorRepository contractorRepository;
    private final WorkSiteRepository workSiteRepository;

    public ContractorService(ContractorRepository contractorRepository, WorkSiteRepository workSiteRepository) {
        this.contractorRepository = contractorRepository;
        this.workSiteRepository = workSiteRepository;
    }

    @Transactional(readOnly = true)
    public List<Contractor> findContractors(String search) {
        List<Contractor> contractors = search == null || search.isBlank()
                ? contractorRepository.findDistinctByOrderByNameAsc()
                : contractorRepository.findDistinctByNameContainingIgnoreCaseOrderByNameAsc(search.trim());

        contractors.forEach(contractor ->
                contractor.getWorkSites().sort(Comparator.comparing(WorkSite::getLocation, String.CASE_INSENSITIVE_ORDER)));
        return contractors;
    }

    @Transactional(readOnly = true)
    public List<ContractorListRow> findContractorRows(String search) {
        List<ContractorListRow> rows = new ArrayList<>();
        for (Contractor contractor : findContractors(search)) {
            if (contractor.getWorkSites().isEmpty()) {
                rows.add(new ContractorListRow(contractor, null));
                continue;
            }
            for (WorkSite workSite : contractor.getWorkSites()) {
                rows.add(new ContractorListRow(contractor, workSite));
            }
        }
        return rows;
    }

    @Transactional(readOnly = true)
    public Contractor getContractor(Long id) {
        return contractorRepository.findById(id)
                .orElseThrow(() -> new ContractorNotFoundException(id));
    }

    @Transactional(readOnly = true)
    public WorkSite getWorkSite(Long id) {
        return workSiteRepository.findById(id)
                .orElseThrow(() -> new WorkSiteNotFoundException(id));
    }

    public Contractor createContractor(ContractorForm form) {
        Contractor contractor = new Contractor();
        copyFormToContractor(form, contractor);
        return contractorRepository.save(contractor);
    }

    public Contractor updateContractor(Long id, ContractorForm form) {
        Contractor contractor = getContractor(id);
        copyFormToContractor(form, contractor);
        return contractorRepository.save(contractor);
    }

    public void deleteContractor(Long id) {
        if (!contractorRepository.existsById(id)) {
            throw new ContractorNotFoundException(id);
        }
        contractorRepository.deleteById(id);
    }

    public WorkSite createWorkSite(Long contractorId, WorkSiteForm form) {
        Contractor contractor = getContractor(contractorId);
        WorkSite workSite = new WorkSite();
        workSite.setContractor(contractor);
        copyFormToWorkSite(form, workSite);
        return workSiteRepository.save(workSite);
    }

    public WorkSite updateWorkSite(Long contractorId, Long workSiteId, WorkSiteForm form) {
        WorkSite workSite = getWorkSite(workSiteId);
        if (!workSite.getContractor().getId().equals(contractorId)) {
            throw new WorkSiteNotFoundException(workSiteId);
        }
        copyFormToWorkSite(form, workSite);
        return workSiteRepository.save(workSite);
    }

    public void deleteWorkSite(Long contractorId, Long workSiteId) {
        WorkSite workSite = getWorkSite(workSiteId);
        if (!workSite.getContractor().getId().equals(contractorId)) {
            throw new WorkSiteNotFoundException(workSiteId);
        }
        workSiteRepository.delete(workSite);
    }

    private void copyFormToContractor(ContractorForm form, Contractor contractor) {
        contractor.setName(form.getName().trim());
        contractor.setPhoneNumber(cleanOptionalText(form.getPhoneNumber()));
        contractor.setCustomerType(form.getCustomerType());
        contractor.setBillingName(cleanOptionalText(form.getBillingName()));
        contractor.setAddress(cleanOptionalText(form.getAddress()));
        contractor.setNotes(cleanOptionalText(form.getNotes()));
        contractor.setAmountPaidToDate(form.getAmountPaidToDate());
        contractor.setAmountUnpaid(form.getAmountUnpaid());
    }

    private void copyFormToWorkSite(WorkSiteForm form, WorkSite workSite) {
        workSite.setLocation(form.getLocation().trim());
        workSite.setSquareArea(form.getSquareArea());
        workSite.setUnitOfMeasurement(form.getUnitOfMeasurement());
        workSite.setQuotedAmount(resolveQuotedAmount(form));
        workSite.setGstAmount(resolveQuotedAmount(form).multiply(GST_RATE).setScale(2, RoundingMode.HALF_UP));
        workSite.setStatus(form.getStatus() == null ? WorkSiteStatus.IN_PROGRESS : form.getStatus());
    }

    private BigDecimal resolveQuotedAmount(WorkSiteForm form) {
        BigDecimal squareArea = form.getSquareArea() == null ? BigDecimal.ZERO : form.getSquareArea();
        if (form.getUnitOfMeasurement() == UnitOfMeasurement.SFT) {
            return squareArea.multiply(SQUARE_FOOT_RATE).setScale(2, RoundingMode.HALF_UP);
        }
        BigDecimal quotedAmount = form.getQuotedAmount() == null ? BigDecimal.ZERO : form.getQuotedAmount();
        return quotedAmount.setScale(2, RoundingMode.HALF_UP);
    }

    private String cleanOptionalText(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}
