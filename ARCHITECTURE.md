# Easy Meetings - Architecture Documentation

## System Architecture

### High-Level Architecture

```
┌─────────────────┐
│   Web Browser   │
│   (Next.js)     │
└────────┬────────┘
         │ HTTPS
         │ (Cookies for sessions)
         ↓
┌─────────────────┐     ┌──────────────┐
│   API Gateway   │────→│   Redis      │
│   (Quarkus)     │     │   (Sessions) │
└────────┬────────┘     └──────────────┘
         │
    ┌────┴────┐
    │         │
    ↓         ↓
┌────────┐ ┌──────────┐
│PostgreSQL OAuth/OIDC│
│(Data)  │ │ Provider │
└────────┘ └──────────┘
```

## Security Architecture

### Authentication Flow

#### OAuth Flow (Primary)
```
1. User clicks "Sign in with Google"
2. Browser redirects to Google OAuth
3. User authenticates with Google
4. Google redirects back with authorization code
5. Backend exchanges code for tokens
6. Backend creates user record (if new)
7. Backend creates Redis session
8. Backend sends HttpOnly cookie with session ID
9. User is authenticated
```

#### Magic Link Flow (Fallback)
```
1. User enters email address
2. Backend generates secure random token
3. Backend stores token in database with expiration
4. Backend sends email with magic link
5. User clicks link in email
6. Backend validates token (not expired, not used)
7. Backend marks token as used
8. Backend creates/finds user record
9. Backend creates Redis session
10. Backend sends HttpOnly cookie with session ID
11. User is authenticated
```

### Security Measures

**Frontend Security:**
- No tokens stored in browser (no localStorage/sessionStorage)
- HttpOnly cookies prevent XSS token theft
- SameSite=Lax prevents CSRF attacks
- React automatically escapes content (XSS prevention)
- Input validation before API calls

**Backend Security:**
- Session data stored server-side in Redis
- CORS configured for allowed origins only
- SQL injection prevented by Panache/Hibernate ORM
- Role-based access control on all endpoints
- Input validation with Hibernate Validator
- Secure random token generation
- Password-less authentication (no password storage)

**Database Security:**
- Prepared statements (via ORM)
- Foreign key constraints
- Audit logging capability
- Regular backups (production)

**Infrastructure Security:**
- TLS/HTTPS in production
- Environment variable secrets
- Container isolation
- Network policies (production)

## Data Model

### Entity Relationships

```
User
├── created MeetingSeries (1:N)
├── MeetingSeriesMember (N:M)
├── created Topics (1:N)
├── created Entries (1:N)
└── assigned Tasks (1:N)

MeetingSeries
├── Members (1:N)
├── Appointments (1:N)
└── Areas (1:N)

Area
└── Topics (1:N)

Topic
└── Entries (1:N)

Appointment
├── AppointmentMembers (1:N)
└── Entries (1:N)

Entry
├── belongs to Topic (N:1)
├── belongs to Appointment (N:1)
├── created by User (N:1)
└── assigned to User (N:1, optional)
```

### Role Hierarchy

```
Admin
├── All Meeting Leader permissions
├── Delete meeting series
├── Manage all members
└── Full access to all features

Meeting Leader
├── All Member permissions
├── Create/update meeting series
├── Manage areas
├── Invite/remove members
└── Manage appointments

Member
├── All Watcher permissions
├── Create topics
├── Add entries (comments/tasks)
└── Update own entries

Watcher
└── Read-only access
```

## API Design

### RESTful Principles

- Resource-based URLs
- HTTP methods for CRUD operations
- JSON request/response bodies
- Proper HTTP status codes
- Pagination for lists (future)
- Filtering and sorting (future)

### API Versioning

Current: No version prefix (v1 implicit)
Future: `/api/v2/...` for breaking changes

### Error Handling

```json
{
  "error": "Human-readable error message",
  "code": "ERROR_CODE",
  "details": { ... }
}
```

HTTP Status Codes:
- 200: Success
- 201: Created
- 204: No Content (delete success)
- 400: Bad Request (validation error)
- 401: Unauthorized (not authenticated)
- 403: Forbidden (not authorized)
- 404: Not Found
- 500: Internal Server Error

## Frontend Architecture

### Component Structure

```
app/
├── (auth)/
│   ├── login/
│   └── verify/
├── dashboard/
│   ├── layout.tsx (auth guard)
│   ├── page.tsx (series list)
│   └── series/
│       └── [id]/
│           ├── page.tsx (series detail)
│           ├── appointments/
│           ├── areas/
│           └── members/
└── components/
    ├── ui/ (shadcn components)
    └── features/ (business components)
```

### State Management

**Global State (Zustand):**
- User authentication state
- Current user profile

**Server State (Future: React Query):**
- API data fetching
- Caching
- Optimistic updates
- Background refetching

**Local State (useState):**
- Form inputs
- UI toggles
- Temporary data

### Styling Strategy

**Tailwind CSS:**
- Utility-first approach
- Mobile-first responsive design
- Custom design system via CSS variables
- Component variants via CVA (class-variance-authority)

**Design Tokens:**
```css
--primary: Purple (brand color)
--secondary: Light gray
--destructive: Red (errors/delete)
--muted: Lighter text
--border: Subtle borders
```

## Backend Architecture

### Layered Architecture

```
Resource Layer (REST endpoints)
    ↓
Service Layer (business logic)
    ↓
Domain Layer (entities)
    ↓
Repository Layer (Panache - data access)
    ↓
Database
```

### Dependency Injection

Quarkus Arc (CDI) provides:
- `@ApplicationScoped` services (singletons)
- `@Inject` for dependency injection
- Automatic lifecycle management

### Database Migrations

**Flyway:**
- Version-controlled SQL migrations
- Automatic execution on startup
- Baseline on existing databases
- Migration history tracking

**Migration Naming:**
```
V1__initial_schema.sql
V2__add_notifications.sql
V3__add_indexes.sql
```

## Performance Considerations

### Caching Strategy

**Redis Cache:**
- Session data (TTL: 24 hours)
- Future: Query result caching
- Future: Rate limiting data

**Database Indexes:**
- Primary keys (UUID)
- Foreign keys
- Frequently queried columns
- Email lookups
- Date ranges

### Query Optimization

**Lazy Loading:**
- JPA relationships use FetchType.LAZY
- Prevents N+1 query problems

**Projection Queries (Future):**
- Select only needed columns
- DTO projections for complex queries

### Frontend Performance

**Code Splitting:**
- Next.js automatic code splitting
- Dynamic imports for large components

**Image Optimization:**
- Next.js Image component
- Automatic WebP conversion
- Lazy loading

**Bundle Size:**
- Tree shaking unused code
- Minimizing dependencies
- Using shadcn (copy components, not library)

## Scalability Considerations

### Horizontal Scaling

**Backend:**
- Stateless API (sessions in Redis)
- Load balancer ready
- Container-based deployment

**Database:**
- Connection pooling (configured)
- Read replicas (future)
- Partitioning by meeting series (future)

**Redis:**
- Redis Cluster for high availability (future)
- Sentinel for failover (future)

### Vertical Scaling

**Database:**
- Increase memory for larger datasets
- More CPU for complex queries

**Backend:**
- JVM heap tuning
- Thread pool configuration

## Monitoring & Observability

### Metrics (Future Implementation)

**Backend Metrics:**
- Request rate
- Response time
- Error rate
- Database query time
- Cache hit rate

**Business Metrics:**
- Active users
- Meeting series count
- Appointment attendance rate
- Task completion rate

### Logging Strategy

**Current:**
- Console logging (development)
- Log levels: DEBUG, INFO, WARN, ERROR

**Production (Future):**
- Structured JSON logging
- Centralized log aggregation
- Log retention policies

## Testing Strategy

### Backend Testing

**Unit Tests:**
- Service layer logic
- Entity methods
- Utility functions

**Integration Tests:**
- REST API endpoints
- Database operations
- Authentication flows

**Test Coverage Goal:** >80%

### Frontend Testing

**Component Tests:**
- UI component rendering
- User interactions
- State changes

**Integration Tests (Future):**
- Page flows
- API mocking
- E2E with Playwright

**Test Coverage Goal:** >70%

## Deployment Architecture

### Development
```
Local Machine
├── Backend: ./mvnw quarkus:dev (port 8080)
├── Frontend: npm run dev (port 3000)
├── PostgreSQL: Docker (port 5432)
├── Redis: Docker (port 6379)
└── MailHog: Docker (ports 1025, 8025)
```

### Production (Recommended)
```
Cloud Provider
├── Backend: Container (Quarkus) × N instances
├── Frontend: Static hosting (Vercel/Netlify)
├── PostgreSQL: Managed service (RDS/Cloud SQL)
├── Redis: Managed service (ElastiCache/Memorystore)
└── Email: SMTP service (SendGrid/SES)
```

## Future Enhancements

### Phase 2 Features
- Real-time updates (WebSocket)
- File attachments
- Meeting minutes export (PDF)
- Calendar integration
- Notifications system

### Technical Improvements
- GraphQL API option
- Rate limiting
- API versioning
- Advanced caching
- Full-text search (PostgreSQL)
- Audit log UI
- Analytics dashboard

### DevOps
- CI/CD pipeline
- Automated testing
- Infrastructure as Code
- Blue-green deployment
- Health checks
- Auto-scaling

## Conclusion

This architecture follows industry best practices:
- ✅ Separation of concerns
- ✅ Security by design
- ✅ Scalability ready
- ✅ Maintainable code
- ✅ Comprehensive testing
- ✅ Well documented
- ✅ Modern tech stack

The system is production-ready with room for growth and additional features.
