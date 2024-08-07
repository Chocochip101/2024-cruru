package com.cruru.applyform.domain.repository;

import com.cruru.applyform.domain.ApplyForm;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApplyFormRepository extends JpaRepository<ApplyForm, Long> {

    Optional<ApplyForm> findByDashboardId(long dashboardId);
}