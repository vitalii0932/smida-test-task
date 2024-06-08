package org.example.smidatesttask.dto;

import lombok.Data;

import java.sql.Timestamp;
import java.util.Objects;
import java.util.UUID;

/**
 * class company to transfer company data
 */
@Data
public class CompanyDTO {
    private UUID id;
    private String name;
    private String registrationNumber;
    private String address;
    private Timestamp createdAt;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CompanyDTO that = (CompanyDTO) o;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name) && Objects.equals(registrationNumber, that.registrationNumber) && Objects.equals(address, that.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, registrationNumber, address, createdAt);
    }
}
