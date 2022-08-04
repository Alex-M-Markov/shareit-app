package shareit.app.booking;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByBookerId(Long bookerId, Sort sort);

    List<Booking> findByBookerIdAndEndIsBefore(Long bookerId, LocalDateTime end, Sort sort);

    List<Booking> findByBookerIdAndEndIsAfter(Long bookerId, LocalDateTime end, Sort sort);

    List<Booking> findByBookerIdAndStatusIs(Long bookerId, BookingStatus status, Sort sort);

    List<Booking> findAllByItemOwnerId(Long ownerId, Sort sort);

    List<Booking> findAllByItemOwnerIdAndEndIsBefore(Long ownerId, LocalDateTime end, Sort sort);

    List<Booking> findByBookerIdAndStartIsBeforeAndEndIsAfter(Long ownerId, LocalDateTime start,
        LocalDateTime end, Sort sort);

    List<Booking> findAllByItemOwnerIdAndEndIsAfter(Long ownerId, LocalDateTime end, Sort sort);

    List<Booking> findAllByItemOwnerIdAndStartIsBeforeAndEndIsAfter(Long ownerId,
        LocalDateTime start, LocalDateTime end, Sort sort);

    List<Booking> findAllByItemOwnerIdAndStatusIs(Long ownerId, BookingStatus status, Sort sort);

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