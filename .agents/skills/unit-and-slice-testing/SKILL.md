---
name: unit-and-slice-testing
version: 1.0.0
description: Use when creating or fixing unit tests, slice tests, repository tests, or controller tests with JUnit 5 and Mockito.
---
# Testing Workflow (AlohaRent)
1. **Choose the Fastest Test Slice** (avoid heavy `@SpringBootTest` unless full integration is required):
   - **Service Layer**: Pure unit tests with `@ExtendWith(MockitoExtension.class)`, `@Mock`, and `@InjectMocks`.
   - **Web Layer**: Controller tests with `@WebMvcTest(Controller.class)` and `MockMvc`.
   - **Repository Layer**: Persistence tests with `@DataJpaTest`.
2. **Coverage & Naming**:
   - Follow `methodName_StateUnderTest_ExpectedBehavior` naming.
   - Always test both happy paths and edge/error cases (nulls, invalid dates, overlapping bookings, negative prices, non-existent IDs).
3. **Verify**: Run `./mvnw test -Dtest=TestClassName` and ensure 100% green execution before finishing.
