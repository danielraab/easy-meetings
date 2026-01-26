# Easy Meetings - Development Setup Guide

## Initial Setup

### 1. Start Infrastructure

```bash
# From the project root
docker-compose up -d

# Verify all services are running
docker-compose ps
```

You should see:
- postgres (port 5432)
- redis (port 6379)
- mailhog (ports 1025, 8025)

### 2. Configure OAuth (Optional but Recommended)

1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Create a new project or select existing
3. Enable Google+ API
4. Create OAuth 2.0 credentials:
   - Application type: Web application
   - Authorized redirect URIs: `http://localhost:8080/api/auth/callback`
5. Copy Client ID and Client Secret

Create `/backend/src/main/resources/application.properties` (if not exists) and update:

```properties
quarkus.oidc.client-id=YOUR_CLIENT_ID
quarkus.oidc.credentials.secret=YOUR_CLIENT_SECRET
```

### 3. Install Backend Dependencies

```bash
cd backend

# Test Maven wrapper works
./mvnw --version

# Compile and download dependencies
./mvnw compile
```

### 4. Run Database Migrations

When you first start the backend, Flyway will automatically run migrations.

```bash
cd backend
./mvnw quarkus:dev
```

Watch for log output showing Flyway migration success.

### 5. Install Frontend Dependencies

```bash
cd frontend

# Install all npm packages
npm install

# Create local environment file
echo "NEXT_PUBLIC_API_URL=http://localhost:8080" > .env.local
```

### 6. Start Development Servers

#### Terminal 1 - Backend
```bash
cd backend
./mvnw quarkus:dev
```

Backend will be available at:
- API: http://localhost:8080
- Swagger UI: http://localhost:8080/api/swagger-ui
- OpenAPI Spec: http://localhost:8080/api/openapi

#### Terminal 2 - Frontend
```bash
cd frontend
npm run dev
```

Frontend will be available at:
- Application: http://localhost:3000

#### Terminal 3 - MailHog (already running in Docker)
View emails at: http://localhost:8025

## Testing the Application

### 1. Test Magic Link Authentication

1. Open http://localhost:3000
2. Click "Send Magic Link"
3. Enter your email address
4. Open MailHog at http://localhost:8025
5. Click the magic link in the email
6. You should be logged in and redirected to the dashboard

### 2. Create a Meeting Series

1. Click "New Meeting Series"
2. Enter a name (e.g., "Weekly Team Sync")
3. Add description (optional)
4. Click Create

### 3. Invite Members

1. Click on a meeting series
2. Click "Invite Member"
3. Enter email and select role
4. Member will receive invitation email in MailHog

## Running Tests

### Backend Tests

```bash
cd backend

# Run all tests
./mvnw test

# Run with coverage report
./mvnw test jacoco:report

# View coverage report
open target/site/jacoco/index.html  # macOS
xdg-open target/site/jacoco/index.html  # Linux
```

### Frontend Tests

```bash
cd frontend

# Run tests once
npm test

# Run in watch mode
npm run test:watch

# Run with coverage
npm test -- --coverage
```

## Common Development Tasks

### Reset Database

```bash
# Stop services
docker-compose down

# Remove volumes
docker-compose down -v

# Start fresh
docker-compose up -d

# Restart backend to run migrations
cd backend
./mvnw quarkus:dev
```

### View Logs

```bash
# All services
docker-compose logs -f

# Specific service
docker-compose logs -f postgres
docker-compose logs -f redis
docker-compose logs -f mailhog
```

### Access Database Directly

```bash
docker exec -it easy-meetings-postgres psql -U easymeeting -d easymeeting
```

Useful SQL commands:
```sql
-- List all tables
\dt

-- View users
SELECT * FROM users;

-- View meeting series
SELECT * FROM meeting_series;

-- Exit
\q
```

### Access Redis Directly

```bash
docker exec -it easy-meetings-redis redis-cli

# Redis commands
KEYS *
GET session:*
```

## Troubleshooting

### Backend won't start

1. Check if PostgreSQL is running: `docker-compose ps`
2. Check if port 8080 is available: `lsof -i :8080`
3. Check backend logs for detailed error messages
4. Verify Java 21 is installed: `java --version`

### Frontend won't start

1. Check if Node.js 20+ is installed: `node --version`
2. Delete node_modules and reinstall: `rm -rf node_modules && npm install`
3. Check if port 3000 is available: `lsof -i :3000`
4. Verify .env.local exists with correct API URL

### Database connection errors

1. Verify PostgreSQL is running: `docker-compose ps postgres`
2. Check connection details in application.properties
3. Try resetting database: `docker-compose down -v && docker-compose up -d`

### Redis connection errors

1. Verify Redis is running: `docker-compose ps redis`
2. Check Redis logs: `docker-compose logs redis`
3. Test connection: `docker exec -it easy-meetings-redis redis-cli ping`

### OAuth not working

1. Verify OAuth credentials are correct in application.properties
2. Check redirect URI matches exactly: `http://localhost:8080/api/auth/callback`
3. Ensure OAuth consent screen is configured in Google Cloud Console
4. Check if you're using http://localhost (not 127.0.0.1)

## Development Tips

### Hot Reload

- **Backend**: Quarkus dev mode supports hot reload for most code changes
- **Frontend**: Next.js automatically reloads on file changes

### API Testing with curl

```bash
# Request magic link
curl -X POST http://localhost:8080/api/auth/magic-link \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com"}'

# List meeting series (requires authentication)
curl -X GET http://localhost:8080/api/meeting-series \
  -H "Cookie: user_session=YOUR_SESSION_ID"
```

### Database Migrations

Add new migrations in `backend/src/main/resources/db/migration/`:

```
V1__initial_schema.sql  (existing)
V2__add_feature.sql     (new)
V3__another_change.sql  (new)
```

Flyway will run them in order automatically.

## IDE Configuration

### IntelliJ IDEA / VS Code

1. Install Kotlin plugin
2. Install Quarkus tools
3. Import backend as Maven project
4. Install ESLint and Prettier for frontend

### Recommended VS Code Extensions

- Kotlin Language
- Quarkus
- ESLint
- Prettier
- Tailwind CSS IntelliSense

## Next Steps

1. Explore the API via Swagger UI
2. Review the database schema in PostgreSQL
3. Customize the frontend theme in `app/globals.css`
4. Add more test coverage
5. Implement additional features

For production deployment, see the main README.md file.
