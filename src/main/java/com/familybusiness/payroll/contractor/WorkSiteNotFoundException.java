package com.familybusiness.payroll.contractor;

public class WorkSiteNotFoundException extends RuntimeException {

    public WorkSiteNotFoundException(Long id) {
        super("Work site not found: " + id);
    }
}
