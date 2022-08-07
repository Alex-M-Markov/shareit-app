package shareit.app.booking;

import java.time.LocalDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    Page<Booking> findByBookerId(Long bookerId, Pageable pageable);

    Page<Booking> findByBookerIdAndEndIsBefore(Long bookerId, LocalDateTime end, Pageable pageable);

    Page<Booking> findByBookerIdAndEndIsAfter(Long bookerId, LocalDateTime end, Pageable pageable);

    Page<Booking> findByBookerIdAndStatusIs(Long bookerId, BookingStatus status, Pageable pageable);

    Page<Booking> findAllByItemOwnerId(Long ownerId, Pageable pageable);

    Page<Booking> findAllByItemOwnerIdAndEndIsBefore(Long ownerId, LocalDateTime end,
        Pageable pageable);

    Page<Booking> findByBookerIdAndStartIsBeforeAndEndIsAfter(Long ownerId, LocalDateTime start,
        LocalDateTime end, Pageable pageable);

    Page<Booking> findAllByItemOwnerIdAndEndIsAfter(Long ownerId, LocalDateTime end,
        Pageable pageable);

    Page<Booking> findAllByItemOwnerIdAndStartIsBeforeAndEndIsAfter(Long ownerId,
        LocalDateTime start, LocalDateTime end, Pageable pageable);

    Page<Booking> findAllByItemOwnerIdAndStatusIs(Long ownerId, BookingStatus status,
        Pageable pageable);

    @Query("select b from Booking b "
        + "join Item i on i.id = b.item.id "
        + "where b.item.id = ?1 "
        + "and i.owner.id = ?4 "
        + "and b.status = ?3 "
        + "and b.end < ?2 "
        + "order by b.end desc ")
    Booking findFirstByItemIdAndEndIsBeforeAndStatusIs(Long itemId, LocalDateTime current,
        BookingStatus status, Long userId);

    @Query("select b from Booking b "
        + "join Item i on i.id = b.item.id "
        + "where b.item.id = ?1 "
        + "and i.owner.id = ?4 "
        + "and b.status = ?3 "
        + "and b.start > ?2 "
        + "order by b.start asc ")
    Booking findFirstByItemIdAndStartIsAfterAndStatusIs(Long itemId, LocalDateTime current,
        BookingStatus status, Long userId);
}