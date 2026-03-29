// service/EventCategoryService.java
package ru.ssau.s_rest_app.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ssau.s_rest_app.dto.eventCategory.EventCategoryRequestDto;
import ru.ssau.s_rest_app.dto.eventCategory.EventCategoryResponseDto;
import ru.ssau.s_rest_app.entity.EventCategory;
import ru.ssau.s_rest_app.exception.DuplicateEntityException;
import ru.ssau.s_rest_app.exception.EntityNotFoundException;
import ru.ssau.s_rest_app.repository.EventCategoryRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventCategoryService {

    private final EventCategoryRepository repository;

    //Получить все
    @Transactional(readOnly = true)
    public List<EventCategoryResponseDto> getAll() {
        return repository.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    //Получить по id
    @Transactional(readOnly = true)
    public EventCategoryResponseDto getById(Long id) throws EntityNotFoundException {
        return toDto(findOrThrow(id));
    }

    //Создать
    @Transactional
    public EventCategoryResponseDto create(EventCategoryRequestDto dto) throws DuplicateEntityException {
        if (repository.existsByEventCategoryName(dto.getEventCategoryName())) {
            throw new DuplicateEntityException("Категория с названием «" + dto.getEventCategoryName() + "» уже существует"
            );
        }
        EventCategory entity = new EventCategory();
        entity.setEventCategoryName(dto.getEventCategoryName());
        entity.setEventCategoryDescription(dto.getEventCategoryDescription());
        entity.setColorCode(dto.getColorCode());
        return toDto(repository.save(entity));
    }

    //Обновить
    @Transactional
    public EventCategoryResponseDto update(Long id, EventCategoryRequestDto dto) throws DuplicateEntityException, EntityNotFoundException {
        EventCategory entity = findOrThrow(id);
        // Проверяем уникальность только если имя изменилось
        boolean nameChanged = !entity.getEventCategoryName().equalsIgnoreCase(dto.getEventCategoryName());
        if (nameChanged && repository.existsByEventCategoryName(dto.getEventCategoryName())) {
            throw new DuplicateEntityException("Категория с названием «" + dto.getEventCategoryName() + "» уже существует"
            );
        }
        entity.setEventCategoryName(dto.getEventCategoryName());
        entity.setEventCategoryDescription(dto.getEventCategoryDescription());
        entity.setColorCode(dto.getColorCode());
        return toDto(repository.save(entity));
    }

    //Удалить
    @Transactional
    public void delete(Long id) throws EntityNotFoundException {
        findOrThrow(id); // убедимся что существует
        repository.deleteById(id);
    }

    //Вспомогательные
    private EventCategory findOrThrow(Long id) throws EntityNotFoundException {
        return repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Категория с id=" + id + " не найдена"));
    }

    private EventCategoryResponseDto toDto(EventCategory e) {
        return new EventCategoryResponseDto(
                e.getIdEventCategory(),
                e.getEventCategoryName(),
                e.getEventCategoryDescription(),
                e.getColorCode()
        );
    }
}
