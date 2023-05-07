package com.therealazimbek.spring.eventmasterapp.repositories;

import com.therealazimbek.spring.eventmasterapp.models.VendorCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VendorCategoryRepository extends JpaRepository<VendorCategory, Long> {
}
