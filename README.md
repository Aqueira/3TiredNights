1.Первое: mvn clean install
2.Второе: docker-compose up --build
Изменены ручки к api:
  Put method -> localhost:8083/user/
  Get userInfo -> localhost:8083/user/
  Get LevelInfo -> localhost:8083/user
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
