# Документация бэкенда — `ru.ssau.s_rest_app`

Технологический стек: **Spring Boot**, **Spring Security + JWT**, **JPA/Hibernate**, **PostgreSQL**, **Lombok**, **Spring Mail**.

---

## Структура пакетов

```
ru.ssau.s_rest_app/
├── controller/       — REST-контроллеры
├── dto/              — объекты запросов и ответов
├── entity/           — JPA-сущности (таблицы БД)
├── exception/        — иерархия исключений
├── repository/       — Spring Data репозитории
├── security/         — JWT-фильтр, утилиты, конфигурация
└── service/          — бизнес-логика
```

---

## Контроллеры (`controller/`)

| Файл | Базовый URL | Назначение |
|---|---|---|
| `AdminController` | `/api/admin` | Управление пользователями, модерация мероприятий, справочники |
| `AuthController` | `/api/auth` | Регистрация и вход |
| `EventCategoryController` | `/api/categories` | Публичный список категорий |
| `EventController` | `/api/events` | Публичный список мероприятий, запись/отмена записи |
| `EventFormatController` | `/api/formats` | Публичный список форматов |
| `GlobalExceptionHandler` | — | Глобальная обработка ошибок (`@RestControllerAdvice`) |
| `OrganizerEventController` | `/api/organizer/events` | CRUD мероприятий организатора |
| `UserController` | `/api/users` | Профиль, аватар, пароль, категории, ОВЗ, заявка организатора |

### AdminController — эндпоинты

| Метод | URL | Описание |
|---|---|---|
| GET | `/api/admin/stats` | Статистика дашборда |
| GET | `/api/admin/users` | Список пользователей с фильтрами |
| PATCH | `/api/admin/users/{id}/role` | Изменить роль пользователя |
| PATCH | `/api/admin/users/{id}/status` | Изменить статус пользователя |
| GET | `/api/admin/organizer-requests` | Заявки на организатора (PENDING) |
| POST | `/api/admin/organizer-requests/{id}/approve` | Одобрить заявку |
| POST | `/api/admin/organizer-requests/{id}/reject` | Отклонить заявку |
| GET | `/api/admin/events?tab=PENDING` | Мероприятия по вкладке |
| POST | `/api/admin/events/{id}/approve` | Одобрить мероприятие |
| POST | `/api/admin/events/{id}/reject` | Отклонить мероприятие |
| PUT | `/api/admin/events/{id}` | Редактировать и опубликовать |
| DELETE | `/api/admin/events/{id}` | Удалить мероприятие |
| PATCH | `/api/admin/events/{id}/status` | Сменить статус мероприятия |
| POST | `/api/admin/events/{id}/restore` | Возобновить отменённое (→ модерация) |
| GET/POST/PUT/DELETE | `/api/admin/directories/categories` | CRUD категорий |
| GET/POST/PUT/DELETE | `/api/admin/directories/formats` | CRUD форматов |
| GET/POST/PUT/DELETE | `/api/admin/directories/event-statuses` | CRUD статусов мероприятий |
| GET/POST/DELETE | `/api/admin/directories/user-statuses` | CRUD статусов пользователей |

### OrganizerEventController — эндпоинты

| Метод | URL | Описание |
|---|---|---|
| GET | `/api/organizer/events` | Мои мероприятия |
| GET | `/api/organizer/events/{id}` | Одно мероприятие (для формы редактирования) |
| POST | `/api/organizer/events` | Создать мероприятие |
| PUT | `/api/organizer/events/{id}` | Редактировать (→ снова на модерацию) |
| DELETE | `/api/organizer/events/{id}` | Отменить мероприятие |
| POST | `/api/organizer/events/{id}/restore` | Возобновить отменённое (→ модерация) |
| GET | `/api/organizer/events/{id}/participants` | Список участников |

---

## DTO (`dto/`)

| Класс | Направление | Назначение |
|---|---|---|
| `LoginRequest` | ← запрос | Email + пароль для входа |
| `RegisterRequest` | ← запрос | Регистрация нового пользователя |
| `AuthResponse` | → ответ | JWT-токен + данные пользователя |
| `EventCardDto` | → ответ | Карточка мероприятия для списков |
| `EventDetailDto` | → ответ | Детальная страница мероприятия |
| `EventPageResponse` | → ответ | Страница с мероприятиями (пагинация) |
| `EventCategoryDto` | → ответ | Категория с цветом |
| `CreateEventRequest` | ← запрос | Создание мероприятия организатором |
| `UpdateEventRequest` | ← запрос | Редактирование мероприятия |
| `OrganizerEventDto` | → ответ | Мероприятие для страницы организатора |
| `ParticipantDto` | → ответ | Участник мероприятия |
| `AdminEventDto` | → ответ | Мероприятие для панели администратора |
| `AdminUserDto` | → ответ | Пользователь для панели администратора |
| `AdminStatsDto` | → ответ | Статистика дашборда |
| `AdminModerationRequest` | ← запрос | Комментарий при одобрении/отклонении |
| `AdminEditEventRequest` | ← запрос | Редактирование мероприятия администратором |
| `ChangeEventStatusRequest` | ← запрос | Смена статуса мероприятия |
| `OrganizerRequestDto` | → ответ | Заявка на роль организатора (для профиля) |
| `OrganizerRequestAdminDto` | → ответ | Заявка для панели администратора |
| `DirectoryItemRequest` | ← запрос | Создание/редактирование записи справочника |
| `UserProfileResponse` | → ответ | Профиль пользователя |
| `AvatarUpdateRequest` | ← запрос | Смена аватара |
| `ChangePasswordRequest` | ← запрос | Смена пароля |
| `UpdateBirthdayRequest` | ← запрос | Обновление даты рождения |
| `UpdateCategoriesRequest` | ← запрос | Выбор любимых категорий |
| `UpdateDisabilityRequest` | ← запрос | Флаг ОВЗ |
| `UpdateUserRoleRequest` | ← запрос | Смена роли пользователя (admin) |
| `UpdateUserStatusRequest` | ← запрос | Смена статуса пользователя (admin) |

---

## Сущности (`entity/`)

| Класс | Таблица | Описание |
|---|---|---|
| `User` | `users` | Пользователь системы |
| `Role` | `role` | Роль: USER, ORGANIZER, ADMIN |
| `UserStatus` | `user_status` | Статус: ACTIVE, BLOCKED, PENDING |
| `Avatar` | `avatar` | URL аватара пользователя |
| `Event` | `event` | Мероприятие |
| `EventCategory` | `event_category` | Категория мероприятия с цветом |
| `EventFormat` | `event_format` | Формат: ONLINE, OFFLINE, HYBRID |
| `EventStatus` | `event_status` | Статус: PLANNED, ONGOING, COMPLETED, CANCELLED |
| `Place` | `place` | Базовый класс площадки (JOINED inheritance) |
| `PhysicalPlace` | `physical_place` | Офлайн-площадка (адрес, ОВЗ) |
| `OnlinePlace` | `online_place` | Онлайн-площадка (ссылка, примечания) |
| `EventParticipant` | `event_participant` | Запись пользователя на мероприятие |
| `EventParticipantId` | — | Составной ключ EventParticipant |
| `ParticipationStatus` | `participation_status` | Статус записи: REGISTERED, WAITLISTED, CANCELLED |
| `OrganizerRequest` | `organizer_request` | Заявка на роль организатора |
| `RequestStatus` | `request_status` | Статус заявки: PENDING, APPROVED, REJECTED |
| `UserInterest` | `user_interest` | Связь пользователь ↔ категория (любимые) |
| `UserInterestId` | — | Составной ключ UserInterest |
| `CityRoute` | `city_route` | Маршрут по городу (раздел "Новенький") |

---

## Репозитории (`repository/`)

| Интерфейс | Основные методы |
|---|---|
| `EventRepository` | `findPublicEvents` (с JOIN FETCH), `findPendingEvents`, `findApprovedEvents`, `findRejectedEvents`, `findArchiveEvents`, `countPendingModeration` |
| `EventParticipantRepository` | `findByUserIdAndEventId`, `countActiveParticipants`, `findByEventId`, `deleteByEventId` |
| `UserRepository` | `findByEmail`, `findForAdmin` (nativeQuery с фильтрами), `countByRoleName` |
| `OrganizerRequestRepository` | `findByStatusName`, `countPendingRequests`, `findByUserId` |
| `EventCategoryRepository` | стандартные JPA |
| `EventFormatRepository` | стандартные JPA |
| `EventStatusRepository` | `findByEventStatusName` |
| `UserStatusRepository` | `findByUserStatusName` |
| `RoleRepository` | `findByRoleName` |
| `AvatarRepository` | `findByAvatarUrl` |
| `ParticipationStatusRepository` | `findByParticipationStatusName` |
| `PlaceRepository` | стандартные JPA |
| `RequestStatusRepository` | `findByRequestStatusName` |
| `UserInterestRepository` | `findByUserId`, `deleteByUserId` |

---

## Безопасность (`security/`)

| Файл | Назначение |
|---|---|
| `JwtUtils` | Генерация и валидация JWT-токенов |
| `JwtAuthFilter` | Фильтр: читает токен из заголовка, устанавливает аутентификацию |
| `SecurityConfig` | Настройка Spring Security: CORS, публичные/защищённые роуты, BCrypt |
| `UserDetailsServiceImpl` | Загрузка пользователя по email для Spring Security |

### Публичные эндпоинты (без токена)
- `/api/auth/**` — регистрация и вход
- `/api/categories` — список категорий
- `/api/formats` — список форматов
- `/api/events/**` — все мероприятия (список, детали, фильтры)

### Защищённые роуты
- `/api/admin/**` — только `ROLE_ADMIN`
- `/api/organizer/**` — `ROLE_ORGANIZER` или `ROLE_ADMIN`
- Всё остальное — любой авторизованный пользователь

---

## Сервисы (`service/`)

| Класс | Назначение |
|---|---|
| `AuthService` | Регистрация (создание пользователя + опционально заявка), вход (генерация JWT) |
| `AdminService` | Статистика, управление пользователями, модерация, CRUD справочников |
| `EventService` | Публичные выборки мероприятий, запись/отмена записи пользователя |
| `OrganizerEventService` | CRUD мероприятий от имени организатора, возобновление |
| `UserService` | Профиль, аватар, пароль, категории, ОВЗ, заявка на организатора |
| `EmailService` | Отправка писем (одобрение/отклонение мероприятия, заявки) с try-catch |
| `UserDetailsServiceImpl` | Реализация UserDetailsService для Spring Security |

---

## Исключения (`exception/`)

| Класс | HTTP-статус | Сообщение |
|---|---|---|
| `AppException` | динамический | Базовый класс — хранит статус внутри |
| `InvalidCredentialsException` | 401 | Неверный email или пароль |
| `UserAlreadyExistsException` | 409 | Пользователь с таким email уже существует |
| `UserNotFoundException` | 404 | Пользователь не найден |

Все исключения обрабатываются в `GlobalExceptionHandler`.

---

## Известные особенности и важные решения

- **`findPublicEvents` использует `JOIN FETCH`** — обязательно из-за LAZY-загрузки. Без него падает `LazyInitializationException` при обходе коллекции вне транзакции.
- **Статус мероприятия при создании** — `PLANNED`.
- **Фильтр по дате** — `eventDate >= :now` (начало дня), а не `startTime > :now`, чтобы мероприятия текущего дня отображались.
- **`AppException extends Exception`**.
- **`EmailService`** оборачивает отправку в try-catch — ошибки почты не ломают бизнес-логику.
- **`UserRepository.findForAdmin`** использует `nativeQuery = true` для надёжной работы с NULL-параметрами.
- **`Place`** использует `InheritanceType.JOINED` — `PhysicalPlace` и `OnlinePlace` — отдельные таблицы.
