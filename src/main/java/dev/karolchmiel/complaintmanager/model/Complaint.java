package dev.karolchmiel.complaintmanager.model;

import com.neovisionaries.i18n.CountryCode;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "COMPLAINT", uniqueConstraints = {
        @UniqueConstraint(name = "UNIQUENESS_CONSTRAINT", columnNames = {"PRODUCT_ID", "COMPLAINANT"})
})
public class Complaint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;

    @Column(name = "PRODUCT_ID", nullable = false)
    private Long productId;

    @Column(name = "CONTENT", nullable = false)
    private String content;

    @Column(name = "CREATION_DATE", nullable = false)
    private LocalDateTime creationDate;

    @Column(name = "COMPLAINANT", nullable = false)
    private String complainant;

    @Column(name = "COMPLAINANT_COUNTRY")
    @Enumerated(EnumType.STRING)
    private CountryCode complainantCountry;

    @Column(name = "COUNT", nullable = false)
    private Integer count;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public String getComplainant() {
        return complainant;
    }

    public void setComplainant(String complainant) {
        this.complainant = complainant;
    }

    public CountryCode getComplainantCountry() {
        return complainantCountry;
    }

    public void setComplainantCountry(CountryCode complainantCountry) {
        this.complainantCountry = complainantCountry;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}
