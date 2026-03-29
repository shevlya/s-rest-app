package ru.ssau.s_rest_app.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ssau.s_rest_app.dto.eventFormat.EventFormatRequestDto;
import ru.ssau.s_rest_app.dto.eventFormat.EventFormatResponseDto;
import ru.ssau.s_rest_app.entity.EventFormat;
import ru.ssau.s_rest_app.exception.DuplicateEntityException;
import ru.ssau.s_rest_app.exception.EntityNotFoundException;
import ru.ssau.s_rest_app.repository.EventFormatRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventFormatService {

    private final EventFormatRepository eventFormatRepository;

    //Получить все
    @Transactional(readOnly = true)
    public List<EventFormatResponseDto> getAll() {
        return eventFormatRepository.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    //Получить по id
    @Transactional(readOnly = true)
    public EventFormatResponseDto getById(Long id) throws EntityNotFoundException {
        return toDto(findOrThrow(id));
    }

    //Создать
    @Transactional
    public EventFormatResponseDto create(EventFormatRequestDto dto) throws DuplicateEntityException {
        if (eventFormatRepository.existsByEventFormatName(dto.getEventFormatName())) {
            throw new DuplicateEntityException("Формат с названием «" + dto.getEventFormatName() + "» уже существует");
        }
        EventFormat entity = new EventFormat();
        entity.setEventFormatName(dto.getEventFormatName());
        entity.setEventFormatDescription(dto.getEventFormatDescription());
        return toDto(eventFormatRepository.save(entity));
    }

    //Обновить
    @Transactional
    public EventFormatResponseDto update(Long id, EventFormatRequestDto dto) throws DuplicateEntityException, EntityNotFoundException {
        EventFormat entity = findOrThrow(id);
        // Проверяем уникальность только если имя изменилось
        boolean nameChanged = !entity.getEventFormatName().equalsIgnoreCase(dto.getEventFormatName());
        if (nameChanged && eventFormatRepository.existsByEventFormatName(dto.getEventFormatName())) {
            throw new DuplicateEntityException("Формат с названием «" + dto.getEventFormatName() + "» уже существует");
        }
        entity.setEventFormatName(dto.getEventFormatName());
        entity.setEventFormatDescription(dto.getEventFormatDescription());
        return toDto(eventFormatRepository.save(entity));
    }

    //Удалить
    @Transactional
    public void delete(Long id) throws EntityNotFoundException {
        findOrThrow(id);
        eventFormatRepository.deleteById(id);
    }

    //Вспомогательные
    private EventFormat findOrThrow(Long id) throws EntityNotFoundException {
        return eventFormatRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Формат с id=" + id + " не найден"));
    }

    private EventFormatResponseDto toDto(EventFormat e) {
        return new EventFormatResponseDto(
                e.getIdEventFormat(),
                e.getEventFormatName(),
                e.getEventFormatDescription()
        );
    }
}