package shareit.app.booking;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import javax.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import shareit.app.exceptions.BookingNotFoundException;
import shareit.app.exceptions.IllegalBookingAccess;
import shareit.app.exceptions.IllegalBookingException;
import shareit.app.exceptions.ItemNotFoundException;
import shareit.app.item.ItemService;
import shareit.app.user.UserService;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemService itemService;

    @Override
    public BookingDtoToReturn create(Long userId, BookingDtoIncoming booking) {
        checkIncomingBooking(booking, userId);
        booking.setStatus(BookingStatus.WAITING);
        return BookingMapper.toBookingDto(
            bookingRepository.save(
                BookingMapper.dtoIncomingToBooking(booking, userService.getUserById(userId),
                    itemService.getItemById(booking.getItemId()))));
    }

    private void checkIncomingBooking(BookingDtoIncoming booking, Long userId) {
        if (booking.getStart().isBefore(LocalDateTime.now())) {
            throw new IllegalBookingException(
                "Дата начала бронирования не может быть в прошлом");
        }
        if (booking.getEnd().isBefore(LocalDateTime.now())) {
            throw new IllegalBookingException(
                "Дата окончания бронирования не может быть в прошлом");
        }
        if (booking.getEnd().isBefore(booking.getStart())) {
            throw new IllegalBookingException(
                "Дата окончания бронирования не может раньше его начала");
        }
        if (itemService.getItemById(booking.getItemId()).getAvailable().equals(false)) {
            throw new IllegalBookingException("Эта вещь недоступна для бронирования");
        }
        if (itemService.getAllItemsOfUser(userId).stream()
            .anyMatch(x -> x.getId().equals(booking.getItemId()))) {
            throw new IllegalBookingAccess("Нельзя забронировать собственную вещь");
        }
    }

    @Override
    public BookingDtoToReturn update(Long userId, Long bookingId, Boolean approved) {
        Booking booking = bookingRepository.getReferenceById(bookingId);
        if (!Objects.equals(userId, booking.getItem().getOwner().getId())) {
            throw new IllegalBookingAccess("У Вас нет прав на просмотр этого бронирования");
        }
        if (booking.getStatus().equals(BookingStatus.APPROVED) && approved.equals(true)) {
            throw new IllegalBookingException("Это бронирование уже подтверждено");
        }
        if (approved.equals(true)) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDtoToReturn getBookingById(Long userId, Long bookingId) {
        try {
            Booking booking = bookingRepository.getReferenceById(bookingId);
            if (!Objects.equals(userId, booking.getBooker().getId()) && !Objects.equals(userId,
                booking.getItem().getOwner()
                    .getId())) {
                throw new IllegalBookingAccess("У Вас нет прав на просмотр этого бронирования");
            }
            return BookingMapper.toBookingDto(booking);
        } catch (EntityNotFoundException e) {
            throw new BookingNotFoundException(e);
        }
    }

    @Override
    public Collection<BookingDtoToReturn> getAllBookingsOfUser(Long userId,
        BookingIncomingStates bookingState) {
        Sort sort = Sort.by(Direction.DESC, "start");
        LocalDateTime end = LocalDateTime.now();
        switch (bookingState) {
            case ALL:
                return bookingsToDtos(bookingRepository.findByBooker_Id(userId, sort));
            case CURRENT:
                return bookingsToDtos(bookingRepository.findByBooker_IdAndStatusIs(userId,
                    BookingStatus.APPROVED, sort));
            case WAITING:
                return bookingsToDtos(bookingRepository.findByBooker_IdAndStatusIs(userId,
                    BookingStatus.WAITING, sort));
            case REJECTED:
                return bookingsToDtos(bookingRepository.findByBooker_IdAndStatusIs(userId,
                    BookingStatus.REJECTED, sort));
            case PAST:
                return bookingsToDtos(bookingRepository.findByBooker_IdAndEndIsBefore(userId,
                    end, sort));
            case FUTURE:
                return bookingsToDtos(bookingRepository.findByBooker_IdAndEndIsAfter(userId,
                    end, sort));
        }
        return new ArrayList<>();
    }

    private Collection<BookingDtoToReturn> bookingsToDtos(Collection<Booking> bookings) {
        Collection<BookingDtoToReturn> bookingDtos = new ArrayList<>();
        for (Booking booking : bookings) {
            bookingDtos.add(BookingMapper.toBookingDto(booking));
        }
        return bookingDtos;
    }

    @Override
    public Collection<BookingDtoToReturn> getAllBookingsOfUserItems(Long userId,
        BookingIncomingStates bookingState) {
        Sort sort = Sort.by(Direction.DESC, "start");
        LocalDateTime end = LocalDateTime.now();
        if (bookingRepository.findAllByItem_Owner_Id(userId, sort) == null) {
            throw new ItemNotFoundException();
        }
        switch (bookingState) {
            case ALL:
                return bookingsToDtos(bookingRepository.findAllByItem_Owner_Id(userId, sort));
            case CURRENT:
                return bookingsToDtos(bookingRepository.findAllByItem_Owner_IdAndStatusIs(userId,
                    BookingStatus.APPROVED, sort));
            case WAITING:
                return bookingsToDtos(bookingRepository.findAllByItem_Owner_IdAndStatusIs(userId,
                    BookingStatus.WAITING, sort));
            case REJECTED:
                return bookingsToDtos(bookingRepository.findAllByItem_Owner_IdAndStatusIs(userId,
                    BookingStatus.REJECTED, sort));
            case PAST:
                return bookingsToDtos(bookingRepository.findAllByItem_Owner_IdAndEndIsBefore(userId,
                    end, sort));
            case FUTURE:
                return bookingsToDtos(bookingRepository.findAllByItem_Owner_IdAndEndIsAfter(userId,
                    end, sort));
        }
        return new ArrayList<>();
    }

}