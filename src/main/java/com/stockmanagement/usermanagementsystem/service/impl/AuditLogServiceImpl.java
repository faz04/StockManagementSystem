package com.stockmanagement.usermanagementsystem.service.impl;

import com.stockmanagement.usermanagementsystem.entity.UserAuditLog;
import com.stockmanagement.usermanagementsystem.repository.UserAuditLogRepository;
import com.stockmanagement.usermanagementsystem.service.AuditLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class AuditLogServiceImpl implements AuditLogService {

    @Autowired
    private UserAuditLogRepository auditLogRepository;

    @Override
    public void logActivity(String action, String performedBy, String targetUser, String details) {
        logActivity(action, performedBy, targetUser, details, null);
    }

    @Override
    public void logActivity(String action, String performedBy, String targetUser, String details, String ipAddress) {
        UserAuditLog log = new UserAuditLog();
        log.setAction(action);
        log.setPerformedBy(performedBy);
        log.setTargetUser(targetUser);
        log.setDetails(details);
        log.setIpAddress(ipAddress);
        auditLogRepository.save(log);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserAuditLog> getAuditLogsByTargetUser(String targetUser) {
        return auditLogRepository.findByTargetUserOrderByTimestampDesc(targetUser);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserAuditLog> getAuditLogsByPerformedBy(String performedBy) {
        return auditLogRepository.findByPerformedByOrderByTimestampDesc(performedBy);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserAuditLog> getAuditLogsByAction(String action) {
        return auditLogRepository.findByActionOrderByTimestampDesc(action);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserAuditLog> getAuditLogsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return auditLogRepository.findByTimestampBetween(startDate, endDate);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserAuditLog> getAllAuditLogs(Pageable pageable) {
        return auditLogRepository.findAllByOrderByTimestampDesc(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public long getActionCountSince(String action, LocalDateTime since) {
        return auditLogRepository.countActionsSince(action, since);
    }
}