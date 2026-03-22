package ru.ssau.s_rest_app.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.ssau.s_rest_app.dto.*;
import ru.ssau.s_rest_app.entity.EventFormat;
import ru.ssau.s_rest_app.entity.EventStatus;
import ru.ssau.s_rest_app.entity.UserStatus;
import ru.ssau.s_rest_app.exception.AppException;
import ru.ssau.s_rest_app.service.AdminService;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    //Дашборд
    @GetMapping("/stats")
    public AdminStatsDto getStats() {
        return adminService.getStats();
    }

    //Пользователи
    @GetMapping("/users")
    public List<AdminUserDto> getUsers(@RequestParam(required = false) String search,
                                       @RequestParam(required = false) String role,
                                       @RequestParam(required = false) String status) {
        return adminService.getUsers(search, role, status);
    }

    @PatchMapping("/users/{id}/role")
    public AdminUserDto updateRole(@PathVariable Long id, @Valid @RequestBody UpdateUserRoleRequest req) throws AppException {
        return adminService.updateUserRole(id, req);
    }

    @PatchMapping("/users/{id}/status")
    public AdminUserDto updateStatus(@PathVariable Long id, @Valid @RequestBody UpdateUserStatusRequest req) throws AppException {
        return adminService.updateUserStatus(id, req);
    }

    //Заявки организаторов
    @GetMapping("/organizer-requests")
    public List<OrganizerRequestAdminDto> getOrganizerRequests() {
        return adminService.getPendingOrganizerRequests();
    }

    @PostMapping("/organizer-requests/{id}/approve")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void approveRequest(@PathVariable Long id) throws AppException {
        adminService.approveOrganizerRequest(id);
    }

    @PostMapping("/organizer-requests/{id}/reject")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void rejectRequest(
            @PathVariable Long id,
            @RequestBody AdminModerationRequest req) throws AppException {
        adminService.rejectOrganizerRequest(id, req);
    }

    //Модерация мероприятий
    @GetMapping("/events")
    public List<AdminEventDto> getEvents(
            @RequestParam(defaultValue = "PENDING") String tab) {
        return adminService.getEventsByTab(tab);
    }

    @PostMapping("/events/{id}/approve")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void approveEvent(
            @PathVariable Long id,
            @RequestBody AdminModerationRequest req) throws AppException {
        adminService.approveEvent(id, req);
    }

    @PostMapping("/events/{id}/reject")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void rejectEvent(
            @PathVariable Long id,
            @RequestBody AdminModerationRequest req) throws AppException {
        adminService.rejectEvent(id, req);
    }

    @PutMapping("/events/{id}")
    public AdminEventDto editEvent(
            @PathVariable Long id,
            @Valid @RequestBody AdminEditEventRequest req) throws AppException {
        return adminService.editEvent(id, req);
    }

    @DeleteMapping("/events/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEvent(@PathVariable Long id) throws AppException {
        adminService.deleteEvent(id);
    }

    @PatchMapping("/events/{id}/status")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changeEventStatus(
            @PathVariable Long id,
            @RequestBody ChangeEventStatusRequest req) throws AppException {
        adminService.changeEventStatus(id, req);
    }

    @PostMapping("/events/{id}/restore")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void restoreEvent(@PathVariable Long id) throws AppException {
        adminService.restoreEvent(id);
    }

    //Справочники: Категории
    @GetMapping("/directories/categories")
    public List<EventCategoryDto> getCategories() {
        return adminService.getAllCategories();
    }

    @PostMapping("/directories/categories")
    @ResponseStatus(HttpStatus.CREATED)
    public EventCategoryDto createCategory(@Valid @RequestBody DirectoryItemRequest req) {
        return adminService.createCategory(req);
    }

    @PutMapping("/directories/categories/{id}")
    public EventCategoryDto updateCategory(@PathVariable Long id, @Valid @RequestBody DirectoryItemRequest req) throws AppException {
        return adminService.updateCategory(id, req);
    }

    @DeleteMapping("/directories/categories/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable Long id) throws AppException {
        adminService.deleteCategory(id);
    }

    //Справочники: Форматы
    @GetMapping("/directories/formats")
    public List<EventFormat> getFormats() {
        return adminService.getAllFormats();
    }

    @PostMapping("/directories/formats")
    @ResponseStatus(HttpStatus.CREATED)
    public EventFormat createFormat(@Valid @RequestBody DirectoryItemRequest req) {
        return adminService.createFormat(req);
    }

    @PutMapping("/directories/formats/{id}")
    public EventFormat updateFormat(@PathVariable Long id, @Valid @RequestBody DirectoryItemRequest req) throws AppException {
        return adminService.updateFormat(id, req);
    }

    @DeleteMapping("/directories/formats/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFormat(@PathVariable Long id) throws AppException {
        adminService.deleteFormat(id);
    }

    //Справочники: Статусы мероприятий
    @GetMapping("/directories/event-statuses")
    public List<EventStatus> getEventStatuses() {
        return adminService.getAllEventStatuses();
    }

    @PostMapping("/directories/event-statuses")
    @ResponseStatus(HttpStatus.CREATED)
    public EventStatus createEventStatus(@Valid @RequestBody DirectoryItemRequest req) {
        return adminService.createEventStatus(req);
    }

    @PutMapping("/directories/event-statuses/{id}")
    public EventStatus updateEventStatus(@PathVariable Long id, @Valid @RequestBody DirectoryItemRequest req) throws AppException {
        return adminService.updateEventStatus(id, req);
    }

    @DeleteMapping("/directories/event-statuses/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEventStatus(@PathVariable Long id) throws AppException {
        adminService.deleteEventStatus(id);
    }

    //Справочники: Статусы пользователей
    @GetMapping("/directories/user-statuses")
    public List<UserStatus> getUserStatuses() {
        return adminService.getAllUserStatuses();
    }

    @PostMapping("/directories/user-statuses")
    @ResponseStatus(HttpStatus.CREATED)
    public UserStatus createUserStatus(@Valid @RequestBody DirectoryItemRequest req) {
        return adminService.createUserStatus(req);
    }

    @DeleteMapping("/directories/user-statuses/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUserStatus(@PathVariable Long id) throws AppException {
        adminService.deleteUserStatus(id);
    }
}
