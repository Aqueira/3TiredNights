1.Первое: mvn clean install

2.Второе: docker-compose up --build

Изменены ручки к api:
  Put method -> localhost:8083/user/
  
  Get method userInfo -> localhost:8083/user/
  
  Get method LevelInfo -> localhost:8083/level/

Структура проекта:
```
[User Result Update]
                    |
             [Kafka Producer]
                    |
          Kafka Topic "user-scores"
                    |
    -----------------------------------
    |                 |               |
[Consumer Shard 1] [Consumer Shard 2] ... [Consumer Shard N]
    |                 |               |
[Local Top-20 Cache] [Local Top-20 Cache] ...
    |     |           |      |         |
    |  [Delta Updates]        [Delta Updates]
    |         \               /
    |          \             /
    |           \           /
    |            \         /
    |             \       /
    |              \     /
    |          [Global Top-20 Aggregator]
    |                  |
    |          [In-Memory Global Top-20]
    |                  |
    \----------[API GET Global Top-20]----------
```
Замысел работы:
```
[User Updates] 
      |
      v
   [Shard]
   ┌────────────────────┐
   │ Local Top-20       │
   │ (AtomicReference)  │
   │ Form Delta:        │
   │ added / deleted    │
   └────────────────────┘
      |
      v
[Kafka Topic "third-topic"]
      |
      v
[Global Aggregator]
   ┌────────────────────┐
   │ Global Top-20      │
   │ (AtomicReference)  │
   │ Apply Delta:       │
   │ add / remove users │
   └────────────────────┘
      |
      v
 [API / Services]
```
Ключевые возможные улучшения:
Замена AtomicReference на ConcurrentHashMap:

Более эффективная блокировка на уровне отдельных ключей

Устранение необходимости копировать всю мапу при каждом обновлении

Кэширование сообщений для Kafka при временной недоступности

Минимальное исправление логики шардирования для получения максимальной произовдительности

Чистка кода от ненужной или избыточной писанины
