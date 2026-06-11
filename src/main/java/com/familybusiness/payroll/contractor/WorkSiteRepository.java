package com.familybusiness.payroll.contractor;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface WorkSiteRepository extends JpaRepository<WorkSite, Long> {

    @EntityGraph(attributePaths = {"contractor", "invoiceItems"})
    Optional<WorkSite> findById(Long id);

    @EntityGraph(attributePaths = {"contractor", "invoiceItems"})
    List<WorkSite> findAllByOrderByLocationAsc();

    @Query("select max(ws.invoiceNumber) from WorkSite ws")
    Integer findMaxInvoiceNumber();

    @Query("select distinct ws.serviceType from WorkSite ws where ws.serviceType is not null and ws.serviceType <> '' order by ws.serviceType")
    List<String> findDistinctServiceTypes();
}
