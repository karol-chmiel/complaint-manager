package dev.karolchmiel.complaintmanager.repository;

import dev.karolchmiel.complaintmanager.model.Complaint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ComplaintRepository extends JpaRepository<Complaint, Long> {
}
