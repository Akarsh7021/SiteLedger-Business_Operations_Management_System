package com.familybusiness.payroll.contractor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
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
    private final InvoiceItemRepository invoiceItemRepository;

    public ContractorService(
            ContractorRepository contractorRepository,
            WorkSiteRepository workSiteRepository,
            InvoiceItemRepository invoiceItemRepository
    ) {
        this.contractorRepository = contractorRepository;
        this.workSiteRepository = workSiteRepository;
        this.invoiceItemRepository = invoiceItemRepository;
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

    public WorkSite prepareInvoice(Long workSiteId) {
        WorkSite workSite = getWorkSite(workSiteId);
        if (workSite.getInvoiceNumber() == null) {
            workSite.setInvoiceNumber(nextInvoiceNumber());
        }
        if (workSite.getStatus() == WorkSiteStatus.COMPLETE) {
            if (workSite.getInvoiceDate() == null) {
                workSite.setInvoiceDate(LocalDate.now());
            }
        } else {
            workSite.setInvoiceDate(LocalDate.now());
        }
        if (workSite.getInvoiceBillingAddress() == null || workSite.getInvoiceBillingAddress().isBlank()) {
            workSite.setInvoiceBillingAddress(defaultBillingAddress(workSite));
        }
        return workSiteRepository.save(workSite);
    }

    public WorkSite updateInvoice(Long workSiteId, LocalDate invoiceDate, String invoiceBillingAddress) {
        WorkSite workSite = prepareInvoice(workSiteId);
        workSite.setInvoiceDate(invoiceDate);
        workSite.setInvoiceBillingAddress(cleanOptionalText(invoiceBillingAddress));
        return workSiteRepository.save(workSite);
    }

    public void addInvoiceItem(Long workSiteId, String description, BigDecimal price) {
        WorkSite workSite = prepareInvoice(workSiteId);
        InvoiceItem item = new InvoiceItem();
        item.setWorkSite(workSite);
        item.setDescription(description.trim());
        item.setPrice(price == null ? BigDecimal.ZERO : price.setScale(2, RoundingMode.HALF_UP));
        invoiceItemRepository.save(item);
    }

    public void deleteInvoiceItem(Long itemId) {
        invoiceItemRepository.deleteById(itemId);
    }

    public BigDecimal invoiceSubtotal(WorkSite workSite) {
        BigDecimal subtotal = workSite.getQuotedAmount();
        for (InvoiceItem item : workSite.getInvoiceItems()) {
            subtotal = subtotal.add(item.getPrice());
        }
        return subtotal.setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal invoiceGst(WorkSite workSite) {
        return invoiceSubtotal(workSite).multiply(GST_RATE).setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal invoiceTotal(WorkSite workSite) {
        return invoiceSubtotal(workSite).add(invoiceGst(workSite)).setScale(2, RoundingMode.HALF_UP);
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
        workSite.setServiceType(form.getServiceType());
        workSite.setSquareArea(form.getSquareArea());
        workSite.setUnitOfMeasurement(form.getUnitOfMeasurement());
        workSite.setQuotedAmount(resolveQuotedAmount(form));
        workSite.setGstAmount(resolveQuotedAmount(form).multiply(GST_RATE).setScale(2, RoundingMode.HALF_UP));
        workSite.setStatus(form.getStatus() == null ? WorkSiteStatus.IN_PROGRESS : form.getStatus());
    }

    private BigDecimal resolveQuotedAmount(WorkSiteForm form) {
        BigDecimal squareArea = form.getSquareArea() == null ? BigDecimal.ZERO : form.getSquareArea();
        if (form.getServiceType() == ServiceType.DEEP_FULL_SERVICE_CLEANUP
                && form.getUnitOfMeasurement() == UnitOfMeasurement.SFT) {
            return squareArea.multiply(SQUARE_FOOT_RATE).setScale(2, RoundingMode.HALF_UP);
        }
        BigDecimal quotedAmount = form.getQuotedAmount() == null ? BigDecimal.ZERO : form.getQuotedAmount();
        return quotedAmount.setScale(2, RoundingMode.HALF_UP);
    }

    private Integer nextInvoiceNumber() {
        Integer maxInvoiceNumber = workSiteRepository.findMaxInvoiceNumber();
        if (maxInvoiceNumber == null) {
            return 1;
        }
        return maxInvoiceNumber + 1;
    }

    private String defaultBillingAddress(WorkSite workSite) {
        Contractor contractor = workSite.getContractor();
        String billingName = contractor.getBillingName() == null || contractor.getBillingName().isBlank()
                ? contractor.getName()
                : contractor.getBillingName();
        if (contractor.getAddress() == null || contractor.getAddress().isBlank()) {
            return billingName;
        }
        return billingName + System.lineSeparator() + contractor.getAddress();
    }

    private String cleanOptionalText(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}
