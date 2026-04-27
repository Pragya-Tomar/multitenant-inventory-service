# Multitenant Inventory Service

A production-grade **Modular Monolith** REST API built with **Spring Boot 3** and **Java 21** that manages dealers and their vehicles in a multi-tenant environment.

## Architecture

Clean architecture with clear separation of responsibilities:
src/main/java/com/inventory/
├── shared/                        # Cross-cutting concerns
│   ├── context/TenantContext      # Thread-local tenant storage
│   ├── security/TenantFilter      # Enforces X-Tenant-Id header
│   ├── security/SecurityConfig    # Role-based access control
│   └── exception/                 # Global exception handling
└── inventory/                     # Inventory Module
├── dealer/
│   ├── api/                   # Controller (REST layer)
│   ├── application/           # Service (business logic)
│   ├── domain/                # Entity
│   ├── dto/                   # Request/Response shapes
│   └── repository/            # Data access
├── vehicle/
│   ├── api/
│   ├── application/
│   ├── domain/
│   ├── dto/
│   └── repository/
└── admin/
└── api/                   # Admin-only endpoints

## Tech Stack

- Java 21
- Spring Boot 3.2.5
- Spring Security (stateless, header-based)
- Spring Data JPA + Hibernate
- H2 in-memory database
- Maven

## Data Model

**Dealer**
- id (UUID), tenant_id, name, email
- subscriptionType: `BASIC` | `PREMIUM`

**Vehicle**
- id (UUID), tenant_id, dealerId (FK)
- model, price (decimal)
- status: `AVAILABLE` | `SOLD`

## API Endpoints

### Dealers
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/dealers` | Create dealer |
| GET | `/dealers/{id}` | Get dealer by ID |
| GET | `/dealers` | List dealers (pagination + sort) |
| PATCH | `/dealers/{id}` | Update dealer |
| DELETE | `/dealers/{id}` | Delete dealer |

### Vehicles
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/vehicles` | Create vehicle |
| GET | `/vehicles/{id}` | Get vehicle by ID |
| GET | `/vehicles` | List vehicles (filters + pagination) |
| PATCH | `/vehicles/{id}` | Update vehicle |
| DELETE | `/vehicles/{id}` | Delete vehicle |

**Vehicle filters:** `model`, `status`, `priceMin`, `priceMax`, `subscription`

### Admin (GLOBAL_ADMIN only)
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/admin/dealers/countBySubscription` | Count dealers by subscription (GLOBAL) |

## Security

Every request requires two headers:

| Header | Description |
|--------|-------------|
| `X-Tenant-Id` | Tenant identifier — missing = 400 |
| `X-Role` | `USER` or `GLOBAL_ADMIN` |

## Acceptance Checks

| Check | Result |
|-------|--------|
| Missing X-Tenant-Id → 400 | ✅ Enforced by TenantFilter |
| Cross-tenant access → 403 | ✅ Enforced in service layer |
| subscription=PREMIUM is tenant-scoped | ✅ JPA Specification composes tenant + dealer filter |
| Admin endpoint requires GLOBAL_ADMIN | ✅ SecurityConfig + @PreAuthorize |
| Admin count is GLOBAL (all tenants) | ✅ No tenant filter in admin query |

## Running Locally

```bash
mvn spring-boot:run
```

App starts on `http://localhost:8080`

## Example Usage

```bash
# Create a PREMIUM dealer
curl -X POST http://localhost:8080/dealers \
  -H "Content-Type: application/json" \
  -H "X-Tenant-Id: tenant-a" \
  -H "X-Role: USER" \
  -d '{"name":"Elite Motors","email":"elite@motors.com","subscriptionType":"PREMIUM"}'

# Create a vehicle
curl -X POST http://localhost:8080/vehicles \
  -H "Content-Type: application/json" \
  -H "X-Tenant-Id: tenant-a" \
  -H "X-Role: USER" \
  -d '{"dealerId":"<dealer-uuid>","model":"Tesla Model 3","price":45000,"status":"AVAILABLE"}'

# Get PREMIUM dealer vehicles (tenant-scoped)
curl "http://localhost:8080/vehicles?subscription=PREMIUM" \
  -H "X-Tenant-Id: tenant-a" \
  -H "X-Role: USER"

# Admin count (GLOBAL across all tenants)
curl http://localhost:8080/admin/dealers/countBySubscription \
  -H "X-Tenant-Id: any" \
  -H "X-Role: GLOBAL_ADMIN"
```
