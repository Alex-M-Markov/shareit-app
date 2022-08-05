package shareit.app.booking;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import javax.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import shareit.app.exceptions.BookingNotFoundException;
import shareit.app.exceptions.IllegalBookingAccess;
import shareit.app.exceptions.IllegalBookingException;
import shareit.app.exceptions.IllegalInputException;
import shareit.app.exceptions.ItemNotFoundException;
import shareit.app.exceptions.UnsupportedStatusException;
import shareit.app.item.ItemService;
import shareit.app.user.UserService;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemService itemService;

    @Override
    public BookingDtoToReturn create(Long userId, BookingDtoIncoming booking) {
        log.info("Создается бронирование #{} для пользователя {}", booking.getId(), userId);
        checkIncomingBooking(booking, userId);
        booking.setStatus(BookingStatus.WAITING);
        BookingDtoToReturn bookingDtoToReturn = BookingMapper.toBookingDto(
            bookingRepository.save(
                BookingMapper.dtoIncomingToBooking(booking, userService.getUserById(userId),
                    itemService.getItemById(booking.getItemId()))));
        log.info("Бронирование #{} успешно создано", bookingDtoToReturn.getId());
        return bookingDtoToReturn;
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
        log.info("Обновляется бронирование #{} для пользователя {}", bookingId, userId);
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
        BookingDtoToReturn bookingDtoToReturn = BookingMapper.toBookingDto(
            bookingRepository.save(booking));
        log.info("Бронирование #{} успешно создано", bookingDtoToReturn.getId());
        return bookingDtoToReturn;
    }

    @Override
    public BookingDtoToReturn getBookingById(Long userId, Long bookingId) {
        log.info("Получаем бронирование #{} пользователя {}", bookingId, userId);
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
        if (bookingState.equals(BookingIncomingStates.UNSUPPORTED_STATUS)) {
            throw new UnsupportedStatusException("Unknown state: UNSUPPORTED_STATUS");
        }
        log.info("Получаем все бронирования пользователя #{} со статусом {}", userId, bookingState);
        Sort sort = Sort.by(Direction.DESC, "start");
        LocalDateTime now = LocalDateTime.now();
        if (userService.getUserById(userId) == null) {
            throw new IllegalInputException("Для этого пользователя бронирований нет");
        }
        switch (bookingState) {
            case ALL:
                return bookingsToDtos(bookingRepository.findByBookerId(userId, sort));
            case CURRENT:
                return bookingsToDtos(
                    bookingRepository.findByBookerIdAndStartIsBeforeAndEndIsAfter(userId, now, now,
                        sort));
            case WAITING:
                return bookingsToDtos(bookingRepository.findByBookerIdAndStatusIs(userId,
                    BookingStatus.WAITING, sort));
            case REJECTED:
                return bookingsToDtos(bookingRepository.findByBookerIdAndStatusIs(userId,
                    BookingStatus.REJECTED, sort));
            case PAST:
                return bookingsToDtos(bookingRepository.findByBookerIdAndEndIsBefore(userId,
                    now, sort));
            case FUTURE:
                return bookingsToDtos(bookingRepository.findByBookerIdAndEndIsAfter(userId,
                    now, sort));
        }
        throw new IllegalBookingAccess("Невозможно получить вещи другого пользователя");
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
        if (bookingState.equals(BookingIncomingStates.UNSUPPORTED_STATUS)) {
            throw new UnsupportedStatusException("Unknown state: UNSUPPORTED_STATUS");
        }
        log.info("Получаем все бронирования вещей пользователя #{} со статусом {}", userId,
            bookingState);
        Sort sort = Sort.by(Direction.DESC, "start");
        LocalDateTime now = LocalDateTime.now();
        if (bookingRepository.findAllByItemOwnerId(userId, sort) == null) {
            throw new ItemNotFoundException();
        }
        if (userService.getUserById(userId) == null) {
            throw new IllegalInputException("Для этого пользователя бронирований нет");
        }
        switch (bookingState) {
            case ALL:
                return bookingsToDtos(bookingRepository.findAllByItemOwnerId(userId, sort));
            case CURRENT:
                return bookingsToDtos(
                    bookingRepository.findAllByItemOwnerIdAndStartIsBeforeAndEndIsAfter(userId,
                        now, now, sort));
            case WAITING:
                return bookingsToDtos(
                    bookingRepository.findAllByItemOwnerIdAndStatusIs(userId,
                        BookingStatus.WAITING, sort));
            case REJECTED:
                return bookingsToDtos(
                    bookingRepository.findAllByItemOwnerIdAndStatusIs(userId,
                        BookingStatus.REJECTED, sort));
            case PAST:
                return bookingsToDtos(
                    bookingRepository.findAllByItemOwnerIdAndEndIsBefore(userId,
                        now, sort));
            case FUTURE:
                return bookingsToDtos(
                    bookingRepository.findAllByItemOwnerIdAndEndIsAfter(userId,
                        now, sort));
        }
        throw new IllegalBookingAccess("Невозможно получить вещи другого пользователя");
    }

}