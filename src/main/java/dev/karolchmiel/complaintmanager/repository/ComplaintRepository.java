package dev.karolchmiel.complaintmanager.repository;

import dev.karolchmiel.complaintmanager.model.Complaint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface ComplaintRepository extends JpaRepository<Complaint, Long> {
    Optional<Complaint> findByProductIdAndComplainant(long productId, String complainant);

    @Transactional
    @Modifying
    @Query("UPDATE Complaint c SET c.content = :content WHERE c.id = :complaintId")
    int updateComplaintContent(@Param("complaintId") long complaintId, @Param("content") String content);
}
