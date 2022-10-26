package uz.example.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uz.example.entity.Report;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Me: muhammadqodir
 * Project: Doctor-report-app/IntelliJ IDEA
 * Date:Sun 23/10/22 21:30
 */
public interface ReportRepository extends JpaRepository<Report, Long> {

    List<Report> findAllByOwner_IdOrderByCreatedAtDesc(Long owner_id);

    List<Report> findAllByOwner_IdAndCreatedAtBetweenOrderByCreatedAtDesc(Long owner_id, LocalDateTime createdAt, LocalDateTime createdAt2);

    @Query(value = "select count(id) from Report where owner.id = :owner and doctorFrom = :doctor and createdAt between :from and :to")
    Long findAllCount(@Param("owner") Long owner, @Param("doctor") String fromDoctorName, @Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    @Query(value = "select distinct doctorFrom from Report where owner.id = :owner and createdAt between :from and  :to")
    List<String> findAllDoctors(@Param("owner") Long owner, @Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

}
