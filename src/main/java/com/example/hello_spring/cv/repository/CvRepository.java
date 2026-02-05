package com.example.hello_spring.cv.repository;

import com.example.hello_spring.cv.model.Cv;
import com.example.hello_spring.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CvRepository extends JpaRepository<Cv, Long> {

    // Fetch all CVs belonging to a user
    List<Cv> findByOwner(User owner);

    // Fetch a CV by id + owner (important for security)
    Optional<Cv> findByIdAndOwner(Long id, User owner);
}
