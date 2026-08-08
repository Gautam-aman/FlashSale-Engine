package com.aman.bookingservice.repository;

import com.aman.bookingservice.entity.TicketType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TicketTypeRepository extends JpaRepository<TicketType, Long> {

	@Modifying
	@Query("""
            UPDATE TicketType t
            SET t.availableQuantity = t.availableQuantity - :quantity
            WHERE t.id = :ticketTypeId
            AND t.availableQuantity >= :quantity
            """)
	int reserveInventory(@Param("ticketTypeId") Long ticketTypeId, @Param("quantity") Integer quantity);
}
