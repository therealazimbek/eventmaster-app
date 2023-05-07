package com.therealazimbek.spring.eventmasterapp.services;

import com.therealazimbek.spring.eventmasterapp.models.VendorCategory;
import com.therealazimbek.spring.eventmasterapp.repositories.VendorCategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VendorCategoryService {

    private final VendorCategoryRepository repository;

    public VendorCategoryService(VendorCategoryRepository repository) {
        this.repository = repository;
    }

    public List<VendorCategory> findAll() {
        return repository.findAll();
    }
}
