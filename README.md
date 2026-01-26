# Easy Meetings

A comprehensive meeting management application with role-based access control, built with modern best practices in mind.

## Features

- **Authentication**: OAuth (Google) and Magic Link authentication
- **Role-Based Access Control**: Admin, Meeting Leader, Member, and Watcher roles
- **Meeting Series Management**: Create and manage recurring meetings
- **Areas & Topics**: Organize meeting content hierarchically
- **Appointments**: Schedule meetings with planned and attended members
- **Entries**: Add comments and tasks to topics
- **Email Notifications**: Automatic invitation and magic link emails
- **Session Management**: Secure Redis-based session storage
- **OpenAPI Documentation**: Auto-generated API documentation

## Tech Stack

### Backend
- **Framework**: Quarkus 3.x with Kotlin
- **Database**: PostgreSQL with Flyway migrations
- **Cache/Sessions**: Redis
- **Authentication**: OIDC (OAuth) + Custom Magic Link
- **API Documentation**: OpenAPI 3.0 with Swagger UI
- **Email**: Quarkus Mailer
- **Testing**: JUnit 5, REST Assured, Mockito

### Frontend
- **Framework**: Next.js 16 with TypeScript
- **UI Library**: shadcn/ui with Radix UI
- **Styling**: Tailwind CSS
- **State Management**: Zustand
- **Testing**: Jest, React Testing Library

## Prerequisites

- Docker and Docker Compose
- Java 21 (for backend development)
- Node.js 20+ (for frontend development)
- Maven (included via wrapper)

## Quick Start

### 1. Start Infrastructure Services

```bash
# Start PostgreSQL, Redis, and MailHog
docker-compose up -d

# Check services are healthy
docker-compose ps
```

### 2. Configure Environment Variables

```bash
# Copy example environment files
cp .env.example .env

# Edit .env with your OAuth credentials
# Get OAuth credentials from: https://console.cloud.google.com/
```

### 3. Start Backend

```bash
cd backend

# Run in development mode
./mvnw quarkus:dev

# Backend will be available at http://localhost:8080
# Swagger UI at http://localhost:8080/api/swagger-ui
```

### 4. Start Frontend

```bash
cd frontend

# Install dependencies
npm install

# Create .env.local
echo "NEXT_PUBLIC_API_URL=http://localhost:8080" > .env.local

# Run in development mode
npm run dev

# Frontend will be available at http://localhost:3000
```

## Architecture

### Database Schema

The application uses a hierarchical structure:

```
Users
├── Meeting Series (created by users)
    ├── Members (with roles: Admin, Meeting Leader, Member, Watcher)
    ├── Appointments (scheduled meetings)
    │   └── Appointment Members (planned/attended)
    └── Areas (topics organization)
        └── Topics
            └── Entries (comments/tasks)
```

### Authentication Flow

1. **OAuth Flow**: User signs in with Google → Backend validates → Session created
2. **Magic Link Flow**: User requests magic link → Email sent → Token verified → Session created

**Security**: Sessions are stored server-side in Redis, only session ID cookie is sent to browser.

### API Structure

```
/api
├── /auth
│   ├── POST /magic-link          # Request magic link
│   ├── POST /magic-link/verify   # Verify token
│   ├── GET /me                   # Get current user
│   └── GET /callback             # OAuth callback
├── /meeting-series
│   ├── GET /                     # List series
│   ├── POST /                    # Create series
│   ├── GET /:id                  # Get series details
│   ├── PUT /:id                  # Update series
│   ├── DELETE /:id               # Delete series
│   └── /:id/members              # Manage members
└── ... (appointments, areas, topics, entries)
```

## Testing

### Backend Tests

```bash
cd backend

# Run unit tests
./mvnw test

# Run with coverage
./mvnw test jacoco:report

# Coverage report at: target/site/jacoco/index.html
```

### Frontend Tests

```bash
cd frontend

# Run tests
npm test

# Run tests in watch mode
npm run test:watch

# Run with coverage
npm test -- --coverage
```

## Security Best Practices

✅ **Implemented Security Features:**

- No tokens stored in browser (session-only cookies)
- HttpOnly, Secure, SameSite cookie flags
- CORS configuration for API protection
- Role-based access control (RBAC)
- SQL injection prevention (Panache/Hibernate)
- XSS prevention (React auto-escaping)
- CSRF protection via SameSite cookies
- Password-less authentication
- Input validation on both frontend and backend
- Secure session storage in Redis
- OAuth 2.0 / OIDC implementation

## Production Deployment

### Environment Variables

Ensure these are set in production:

```bash
# Backend
OAUTH_CLIENT_ID=your-production-client-id
OAUTH_CLIENT_SECRET=your-production-secret
SESSION_ENCRYPTION_KEY=generate-secure-32-char-string
SMTP_HOST=your-smtp-server
SMTP_PORT=587
SMTP_USERNAME=your-smtp-user
SMTP_PASSWORD=your-smtp-password
APP_BASE_URL=https://yourdomain.com

# Database
DATABASE_URL=postgresql://...
REDIS_URL=redis://...

# Frontend
NEXT_PUBLIC_API_URL=https://api.yourdomain.com
```

### Build for Production

```bash
# Backend
cd backend
./mvnw package -DskipTests
java -jar target/quarkus-app/quarkus-run.jar

# Frontend
cd frontend
npm run build
npm start
```

## API Documentation

Once the backend is running, access:

- **Swagger UI**: http://localhost:8080/api/swagger-ui
- **OpenAPI Spec**: http://localhost:8080/api/openapi

## Email Testing

MailHog provides a web interface to view sent emails:

- **Web UI**: http://localhost:8025

All emails sent in development are captured here.

## Roles & Permissions

| Role | Create Series | Manage Areas | Add Topics | Add Entries | Manage Members |
|------|--------------|--------------|------------|-------------|----------------|
| Admin | ✅ | ✅ | ✅ | ✅ | ✅ |
| Meeting Leader | ✅ | ✅ | ✅ | ✅ | ✅ |
| Member | ❌ | ❌ | ✅ | ✅ | ❌ |
| Watcher | ❌ | ❌ | ❌ | ❌ | ❌ |

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes with tests
4. Submit a pull request

## License

MIT License - see LICENSE file for details

## Support

For issues and questions, please create an issue in the repository.
