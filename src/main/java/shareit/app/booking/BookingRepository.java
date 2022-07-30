package shareit.app.booking;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByBooker_Id(Long bookerId, Sort sort);

    List<Booking> findByBooker_IdAndEndIsBefore(Long bookerId, LocalDateTime end, Sort sort);

    List<Booking> findByBooker_IdAndEndIsAfter(Long bookerId, LocalDateTime end, Sort sort);

    List<Booking> findByBooker_IdAndStatusIs(Long bookerId, BookingStatus status, Sort sort);

    List<Booking> findAllByItem_Owner_Id(Long ownerId, Sort sort);

    List<Booking> findAllByItem_Owner_IdAndEndIsBefore(Long ownerId, LocalDateTime end, Sort sort);

    List<Booking> findAllByItem_Owner_IdAndEndIsAfter(Long ownerId, LocalDateTime end, Sort sort);

    List<Booking> findAllByItem_Owner_IdAndStatusIs(Long ownerId, BookingStatus status, Sort sort);

    @Query("select b from Booking b "
        + "join Item i on i.id = b.item.id "
        + "where b.item.id = ?1 "
        + "and i.owner.id = ?4 "
        + "and b.status = ?3 "
        + "and b.end < ?2 "
        + "order by b.end desc ")
    Booking findFirstByItem_IdAndEndIsBeforeAndStatusIs(Long itemId, LocalDateTime current,
        BookingStatus status, Long userId);

    @Query("select b from Booking b "
        + "join Item i on i.id = b.item.id "
        + "where b.item.id = ?1 "
        + "and i.owner.id = ?4 "
        + "and b.status = ?3 "
        + "and b.start > ?2 "
        + "order by b.start asc ")
    Booking findFirstByItem_IdAndStartIsAfterAndStatusIs(Long itemId, LocalDateTime current,
        BookingStatus status, Long userId);
}