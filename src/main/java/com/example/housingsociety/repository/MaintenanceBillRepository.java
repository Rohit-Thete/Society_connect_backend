package com.example.housingsociety.repository;

import com.example.housingsociety.entity.MaintenanceBill;
import com.example.housingsociety.entity.MaintenanceBill.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface MaintenanceBillRepository extends JpaRepository<MaintenanceBill, Integer> {

    Optional<MaintenanceBill> findByPeriodYearAndPeriodMonthAndFlatNo(Integer year, Integer month, String flatNo);

    List<MaintenanceBill> findByPeriodYearAndPeriodMonth(Integer year, Integer month);

    List<MaintenanceBill> findByFlatNoOrderByPeriodYearDescPeriodMonthDesc(String flatNo);

    @Query("""
        select b from MaintenanceBill b
        where b.flatNo = :flatNo
          and (b.status = 'PENDING' or b.status = 'PARTIAL')
          and (b.periodYear < :year or (b.periodYear = :year and b.periodMonth < :month))
        """)
    List<MaintenanceBill> findUnpaidBefore(String flatNo, Integer year, Integer month);

    List<MaintenanceBill> findByStatus(Status status);
}
