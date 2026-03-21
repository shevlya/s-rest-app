package ru.ssau.s_rest_app.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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

    // ── Дашборд ──────────────────────────────────────────────
    @GetMapping("/stats")
    public ResponseEntity<AdminStatsDto> getStats() {
        return ResponseEntity.ok(adminService.getStats());
    }

    // ── Пользователи ─────────────────────────────────────────
    @GetMapping("/users")
    public ResponseEntity<List<AdminUserDto>> getUsers(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String status) {
        return ResponseEntity.ok(adminService.getUsers(search, role, status));
    }

    @PatchMapping("/users/{id}/role")
    public ResponseEntity<AdminUserDto> updateRole(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRoleRequest req) throws AppException {
        return ResponseEntity.ok(adminService.updateUserRole(id, req));
    }

    @PatchMapping("/users/{id}/status")
    public ResponseEntity<AdminUserDto> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserStatusRequest req) throws AppException {
        return ResponseEntity.ok(adminService.updateUserStatus(id, req));
    }

    // ── Заявки организаторов ──────────────────────────────────
    @GetMapping("/organizer-requests")
    public ResponseEntity<List<OrganizerRequestAdminDto>> getOrganizerRequests() {
        return ResponseEntity.ok(adminService.getPendingOrganizerRequests());
    }

    @PostMapping("/organizer-requests/{id}/approve")
    public ResponseEntity<Void> approveRequest(@PathVariable Long id) throws AppException {
        adminService.approveOrganizerRequest(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/organizer-requests/{id}/reject")
    public ResponseEntity<Void> rejectRequest(
            @PathVariable Long id,
            @RequestBody AdminModerationRequest req) throws AppException {
        adminService.rejectOrganizerRequest(id, req);
        return ResponseEntity.noContent().build();
    }

    // ── Модерация мероприятий ─────────────────────────────────
    @GetMapping("/events")
    public ResponseEntity<List<AdminEventDto>> getEvents(
            @RequestParam(defaultValue = "PENDING") String tab) {
        return ResponseEntity.ok(adminService.getEventsByTab(tab));
    }

    @PostMapping("/events/{id}/approve")
    public ResponseEntity<Void> approveEvent(
            @PathVariable Long id,
            @RequestBody AdminModerationRequest req) throws AppException {
        adminService.approveEvent(id, req);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/events/{id}/reject")
    public ResponseEntity<Void> rejectEvent(
            @PathVariable Long id,
            @RequestBody AdminModerationRequest req) throws AppException {
        adminService.rejectEvent(id, req);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/events/{id}")
    public ResponseEntity<AdminEventDto> editEvent(
            @PathVariable Long id,
            @Valid @RequestBody AdminEditEventRequest req) throws AppException {
        return ResponseEntity.ok(adminService.editEvent(id, req));
    }

    @DeleteMapping("/events/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) throws AppException {
        adminService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }

    // ── Справочники ───────────────────────────────────────────
    @GetMapping("/directories/categories")
    public ResponseEntity<List<EventCategoryDto>> getCategories() {
        return ResponseEntity.ok(adminService.getAllCategories());
    }

    @PostMapping("/directories/categories")
    public ResponseEntity<EventCategoryDto> createCategory(
            @Valid @RequestBody DirectoryItemRequest req) {
        return ResponseEntity.ok(adminService.createCategory(req));
    }

    @PutMapping("/directories/categories/{id}")
    public ResponseEntity<EventCategoryDto> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody DirectoryItemRequest req) throws AppException {
        return ResponseEntity.ok(adminService.updateCategory(id, req));
    }

    @DeleteMapping("/directories/categories/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) throws AppException {
        adminService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/directories/formats")
    public ResponseEntity<List<EventFormat>> getFormats() {
        return ResponseEntity.ok(adminService.getAllFormats());
    }

    @PostMapping("/directories/formats")
    public ResponseEntity<EventFormat> createFormat(@Valid @RequestBody DirectoryItemRequest req) {
        return ResponseEntity.ok(adminService.createFormat(req));
    }

    @PutMapping("/directories/formats/{id}")
    public ResponseEntity<EventFormat> updateFormat(
            @PathVariable Long id,
            @Valid @RequestBody DirectoryItemRequest req) throws AppException {
        return ResponseEntity.ok(adminService.updateFormat(id, req));
    }

    @DeleteMapping("/directories/formats/{id}")
    public ResponseEntity<Void> deleteFormat(@PathVariable Long id) throws AppException {
        adminService.deleteFormat(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/directories/event-statuses")
    public ResponseEntity<List<EventStatus>> getEventStatuses() {
        return ResponseEntity.ok(adminService.getAllEventStatuses());
    }

    @PostMapping("/directories/event-statuses")
    public ResponseEntity<EventStatus> createEventStatus(
            @Valid @RequestBody DirectoryItemRequest req) {
        return ResponseEntity.ok(adminService.createEventStatus(req));
    }

    @PutMapping("/directories/event-statuses/{id}")
    public ResponseEntity<EventStatus> updateEventStatus(
            @PathVariable Long id,
            @Valid @RequestBody DirectoryItemRequest req) throws AppException {
        return ResponseEntity.ok(adminService.updateEventStatus(id, req));
    }

    @DeleteMapping("/directories/event-statuses/{id}")
    public ResponseEntity<Void> deleteEventStatus(@PathVariable Long id) throws AppException {
        adminService.deleteEventStatus(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/directories/user-statuses")
    public ResponseEntity<List<UserStatus>> getUserStatuses() {
        return ResponseEntity.ok(adminService.getAllUserStatuses());
    }

    @PostMapping("/directories/user-statuses")
    public ResponseEntity<UserStatus> createUserStatus(
            @Valid @RequestBody DirectoryItemRequest req) {
        return ResponseEntity.ok(adminService.createUserStatus(req));
    }

    @DeleteMapping("/directories/user-statuses/{id}")
    public ResponseEntity<Void> deleteUserStatus(@PathVariable Long id) throws AppException {
        adminService.deleteUserStatus(id);
        return ResponseEntity.noContent().build();
    }

    // Добавить после @DeleteMapping("/events/{id}"):
    @PatchMapping("/events/{id}/status")
    public ResponseEntity<Void> changeEventStatus(@PathVariable Long id, @RequestBody ChangeEventStatusRequest req) throws AppException {
        adminService.changeEventStatus(id, req);
        return ResponseEntity.noContent().build();
    }
}
