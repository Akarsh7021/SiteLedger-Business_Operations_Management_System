package com.familybusiness.payroll.deletehistory;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class DeleteHistoryService {

    private final DeleteHistoryRepository deleteHistoryRepository;

    public DeleteHistoryService(DeleteHistoryRepository deleteHistoryRepository) {
        this.deleteHistoryRepository = deleteHistoryRepository;
    }

    public void record(String itemType, String itemName, String details) {
        DeleteHistory history = new DeleteHistory();
        history.setItemType(itemType);
        history.setItemName(itemName);
        history.setDetails(details);
        deleteHistoryRepository.save(history);
    }

    @Transactional(readOnly = true)
    public List<DeleteHistory> findAll() {
        return deleteHistoryRepository.findAllByOrderByDeletedAtDesc();
    }
}
