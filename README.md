
# Backend for Article web service

Article web service - это веб-сервис, который позволяет пользователям создавать и редактировать статьи,
а также комментировать их. Все статьи и комментарии хранятся в базе данных. Сервис предоставляет REST API,
с помощью которого можно получить список статей, создать новую статью, получить/обновить/удалить статью по её ID,
получить список комментариев к статье, создать комментарий к статье, получить/обновить/удалить комментарий по его ID.

## Требования к реализации
1. Все API должны быть документированы с помощью Swagger.
2. Все API должны быть доступны по HTTP.

## Stack for backend
1. Kotlin (Его знает хорошо только один человек, но он знает)
2. Spring Boot (Стандарт де-факто на рынке)
3. Spring Data JPA (Позволяет работать с БД)
4. PostgreSQL (Стандарт де-факто на рынке(либо MongoDB, но у нас нет необходимости в no-SQL DB))
5. Maven (Для управления зависимостями)
6. Docker (Для разворачивания в контейнере)
7. Swagger (Для документации API)
8. JUnit (Для тестирования)
9. Mockito (Для тестирования)
10. Flyway (Для миграций БД)
11. IntelliJ IDEA (Стандарт де-факто на рынке)
12. GitHub (Для хранения кода)
13. GitHub Actions (Для деплоя)
14. Postman (Для тестирования API)

## Структура backend
1. Article controller - отвечает за управление статьями
2. Article service - отвечает за бизнес-логику статей
3. Auth controller - отвечает за авторизацию
4. Auth service - отвечает за бизнес-логику авторизации
5. User controller - отвечает за управление пользователями
6. User service - отвечает за бизнес-логику пользователей
7. Article analytics controller - отвечает за аналитику статей
8. Article analytics service - отвечает за бизнес-логику аналитики статей
