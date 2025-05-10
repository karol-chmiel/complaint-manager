package dev.karolchmiel.complaintmanager.repository;

import dev.karolchmiel.complaintmanager.model.Complaint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ComplaintRepository extends JpaRepository<Complaint, Long> {
    Optional<Complaint> findByProductIdAndComplainant(long productId, String complainant);
}
