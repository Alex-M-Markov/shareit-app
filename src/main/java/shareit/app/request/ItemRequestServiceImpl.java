package shareit.app.request;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import shareit.app.exceptions.RequestNotFoundException;
import shareit.app.exceptions.IllegalElementsInputException;
import shareit.app.exceptions.UserNotFoundException;
import shareit.app.item.Item;
import shareit.app.item.ItemRepository;
import shareit.app.request.ItemRequestDtoWithAnswers.ItemAnswer;
import shareit.app.request.ItemRequestDtoWithAnswers.User;
import shareit.app.user.UserRepository;
import shareit.app.user.UserService;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final ItemRepository itemRepository;


    @Override
    public ItemRequestDto create(Long userId, ItemRequestDto itemRequestDto) {
        log.info("Создается запрос #{} для пользователя {}", itemRequestDto.getId(), userId);
        checkUserExistence(userId);
        ItemRequestDto itemRequestDtoToReturn = ItemRequestMapper.itemRequestToDto(
            itemRequestRepository.save(ItemRequestMapper.dtoToItemRequest(itemRequestDto,
                userRepository.getReferenceById(userId))));
        log.info("Запрос #{} успешно создан", itemRequestDtoToReturn.getId());
        return itemRequestDtoToReturn;
    }

    private void checkUserExistence(Long userId) {
        try {
            userService.getUserById(userId);
        } catch (EntityNotFoundException e) {
            throw new UserNotFoundException(e);
        }
    }

    @Override
    public ItemRequestDtoWithAnswers getRequestById(Long userId, Long requestId) {
        log.info("Получаем запрос #{} пользователя {}", requestId, userId);
        checkUserExistence(userId);
        try {
            ItemRequest itemRequest = itemRequestRepository.getReferenceById(requestId);
            return addAnswer(itemRequest);
        } catch (EntityNotFoundException e) {
            throw new RequestNotFoundException(e);
        }
    }

    private ItemRequestDtoWithAnswers addAnswer(ItemRequest itemRequest) {
        List<ItemRequestDtoWithAnswers.ItemAnswer> listOfAnswerItems = new ArrayList<>();
        List<Item> items = itemRepository.findAllByRequestIdEquals(itemRequest.getId());
        for (Item item : items) {
            ItemRequestDtoWithAnswers.ItemAnswer itemAnswer = new ItemAnswer();
            itemAnswer.setId(item.getId());
            itemAnswer.setName(item.getName());
            itemAnswer.setDescription(item.getDescription());
            itemAnswer.setAvailable(item.getAvailable());
            itemAnswer.setRequestId(itemRequest.getId());
            listOfAnswerItems.add(itemAnswer);
        }
        ItemRequestDtoWithAnswers itemRequestDtoWithAnswers = new ItemRequestDtoWithAnswers(
            itemRequest.getId(), itemRequest.getDescription(), new User(), listOfAnswerItems,
            itemRequest.getCreated());
        itemRequestDtoWithAnswers.getRequestor().setId(itemRequest.getRequestor().getId());
        itemRequestDtoWithAnswers.getRequestor().setName(itemRequest.getRequestor().getName());
        itemRequestDtoWithAnswers.getRequestor().setEmail(itemRequest.getRequestor().getEmail());
        return itemRequestDtoWithAnswers;
    }

    @Override
    public Collection<ItemRequestDtoWithAnswers> getAllRequestsOfUser(Long userId) {
        Boolean includeOwnRecords = true;
        log.info("Получаем все запросы пользователя #{}", userId);
        checkUserExistence(userId);
        Sort sort = Sort.by(Direction.DESC, "created");
        Collection<ItemRequest> allRequestsOfUser = itemRequestRepository.findByRequestorId(userId,
            sort);
        return addAnswers(allRequestsOfUser, userId, includeOwnRecords);
    }

    private Collection<ItemRequestDtoWithAnswers> addAnswers(
        Collection<ItemRequest> allRequestsOfUser, Long userId, Boolean includeOwnRecords) {
        Collection<ItemRequestDtoWithAnswers> itemRequests = new ArrayList<>();
        for (ItemRequest itemRequest : allRequestsOfUser) {
            if (!userId.equals(itemRequest.getRequestor().getId()) || includeOwnRecords) {
                ItemRequestDtoWithAnswers requestDtoWithAnswers = addAnswer(itemRequest);
                itemRequests.add(requestDtoWithAnswers);
            }
        }
        return itemRequests;
    }

    @Override
    public Collection<ItemRequestDtoWithAnswers> getAllRequestsOfOtherUsers(Long userId,
        Integer firstElement,
        Integer numberOfElements) {
        Boolean includeOwnRecords = false;
        log.info("Получаем все запросы пользователя #{}", userId);
        checkUserExistence(userId);
        if (firstElement == null || numberOfElements == null) {
            return new ArrayList<>();
        } else if (firstElement < 0 || numberOfElements <= 0) {
            throw new IllegalElementsInputException(
                "Проверьте количество элементов для отображения");
        } else {
            Sort sort = Sort.by(Direction.DESC, "created");
            Page<ItemRequest> requestsPageable = itemRequestRepository.findAll(
                PageRequest.of(firstElement, numberOfElements, sort));
            Collection<ItemRequest> itemRequests = requestsPageable.getContent();
            return addAnswers(itemRequests, userId, includeOwnRecords);
        }
    }

}