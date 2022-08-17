package ru.practicum.shareit.request;

import java.util.Collection;

public interface ItemRequestService {

    ItemRequestDto create(Long userId, ItemRequestDto itemRequestDto);

    Collection<ItemRequestDtoWithAnswers> getAllRequestsOfUser(Long userId);

    Collection<ItemRequestDtoWithAnswers> getAllRequestsOfOtherUsers(Long userId, Integer firstElement,
        Integer numberOfElements);

    ItemRequestDtoWithAnswers getRequestById(Long userId, Long requestId);
}