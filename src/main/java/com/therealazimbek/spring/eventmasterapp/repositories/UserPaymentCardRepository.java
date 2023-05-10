package com.therealazimbek.spring.eventmasterapp.repositories;

import com.therealazimbek.spring.eventmasterapp.models.UserPaymentCard;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserPaymentCardRepository extends JpaRepository<UserPaymentCard, Long> {
}
