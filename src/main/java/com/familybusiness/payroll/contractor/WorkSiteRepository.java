package com.familybusiness.payroll.contractor;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface WorkSiteRepository extends JpaRepository<WorkSite, Long> {

    @EntityGraph(attributePaths = {"contractor", "invoiceItems"})
    Optional<WorkSite> findById(Long id);

    @EntityGraph(attributePaths = "contractor")
    List<WorkSite> findAllByOrderByLocationAsc();

    @Query("select max(ws.invoiceNumber) from WorkSite ws")
    Integer findMaxInvoiceNumber();
}
