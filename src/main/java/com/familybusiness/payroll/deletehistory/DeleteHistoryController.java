package com.familybusiness.payroll.deletehistory;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DeleteHistoryController {

    private final DeleteHistoryService deleteHistoryService;

    public DeleteHistoryController(DeleteHistoryService deleteHistoryService) {
        this.deleteHistoryService = deleteHistoryService;
    }

    @GetMapping("/delete-history")
    public String deleteHistory(Model model) {
        model.addAttribute("deleteHistory", deleteHistoryService.findAll());
        return "delete-history";
    }
}
