# Library Book Tracker

A learning project for Spring Data JPA and Spring Security.
You can manage books in a browser or use the REST API in Postman.
The Java application also calls a local test API with a Bearer token.

## Requirements

- Java 21
- Node.js 20 or later
- Postman for API checks
- Internet access for the first Maven run

The project includes the Maven Wrapper. You do not need to install Maven.
Run the commands below in PowerShell from the project folder.

## Demo accounts and database

Start Java with the `demo` profile to create these users:

| Username | Password | Role |
| --- | --- | --- |
| `user` | `user` | USER |
| `admin` | `admin` | ADMIN |

These accounts are only for local practice.
Users come from the database through `AppUserDetailsService`.
The database stores BCrypt password hashes.

H2 keeps data in memory. Books and users are lost when Java stops.
The `demo` profile creates missing users on the next start.
The H2 browser console is disabled.

## Start the project

Use two terminals. Java runs on port `8080`. The partner test API runs on port `9090`.

### Terminal 1: partner test API

```powershell
$env:EXTERNAL_API_TOKEN = "local-demo-token"
$env:DEMO_API_MODE = "ok"
node .\tools\partner-demo\server.cjs
```

Wait for this message:

```text
Partner demo API: http://127.0.0.1:9090
```

This service is a local mock: a small API for testing.
It accepts connections only from the local computer.

### Terminal 2: Java application

```powershell
$env:EXTERNAL_API_TOKEN = "local-demo-token"
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=demo"
```

Use the same token in both processes.
Set the variable in each terminal. A variable in one terminal does not set it in another terminal.
`local-demo-token` is a local example. Never put a real token in Git.

Open [the book page](http://localhost:8080/books) and log in.
Use **Ctrl+C** in each terminal to stop the processes.

### Run Java from IntelliJ IDEA

1. Press **Ctrl+Shift+A** and find **Edit Configurations**.
2. Select `HibernatePracticalApplication`.
3. In **Environment variables**, add `EXTERNAL_API_TOKEN=local-demo-token`.
4. In **Program arguments**, add `--spring.profiles.active=demo`.
5. Click **Apply**, then **OK**. Start Java with the Run button.

If a field is hidden, find it under **Modify options**.
Keep configurations with real secrets local. Do not share them in Git.
Start Node.js in a terminal as shown above.

## Settings

| Environment variable | Used by | Default |
| --- | --- | --- |
| `EXTERNAL_API_TOKEN` | Java and Node.js | Required |
| `EXTERNAL_API_BASE_URL` | Java | `http://127.0.0.1:9090` |
| `DEMO_API_MODE` | Node.js | `ok` |
| `PARTNER_API_PORT` | Node.js | `9090` |

Restart a process after changing its variables.
If you change the partner port, also change `EXTERNAL_API_BASE_URL` for Java.

The HTTP client uses a 2-second connection timeout and a 3-second read timeout.
The read timeout limits how long a read waits for data. It is not a total time limit for the whole request.
The client does not follow redirects.

## Browser pages

| Address | Access |
| --- | --- |
| `/` | Sends a logged-in user to `/books` |
| `/books` | USER and ADMIN can read books |
| `/books/new` | ADMIN can open the create form |
| `/books/{id}/edit` | ADMIN can open the edit form |

ADMIN can create, edit, and delete books. USER can only read the list.
The server checks the role even if someone sends a request without using the buttons.
Forms show errors for empty text fields and a publication year below 1.

Browser login uses a session. POST forms include a CSRF token.
The **Exit** button sends a POST request to log out.

### Check browser CSRF protection

1. Log in as ADMIN and open `/books` with a test book.
2. Open browser developer tools with **F12**.
3. In **Elements**, find the form for the **Delete** button.
4. Remove its hidden input with `name="_csrf"` from the browser page.
5. Click **Delete** without refreshing the page.

The request must return `403`, and the book must stay in the database.
Open `/books` again to get a form with a token.
Do not remove the token from the source templates.

## REST API

Use `http://localhost:8080` as the base address.
In Postman, select **Authorization > Basic Auth** and enter a demo username and password.
The API uses Basic Auth on every request. Browser login does not give access to the API.

- GET requests need an authenticated user.
- All other HTTP methods need ADMIN, including HEAD and OPTIONS.
- API requests do not use the browser session.

| Method | Path | Action |
| --- | --- | --- |
| GET | `/api/books` | List books |
| GET | `/api/books/{id}` | Find one book |
| GET | `/api/books/author/{author}` | Search by author |
| GET | `/api/books/title/{title}` | Search by title |
| GET | `/api/books/available` | List available books |
| GET | `/api/books/count` | Count books |
| GET | `/api/books/after-year/{year}` | List books published after a year |
| POST | `/api/books` | Create a book |
| PUT | `/api/books/{id}` | Update a book |
| DELETE | `/api/books/{id}` | Delete a book |
| GET | `/api/integration/status` | Call the partner test API |

### Create a book

Use POST `/api/books` with Basic Auth `admin` / `admin`.
Select **Body > raw > JSON**:

```json
{
  "title": "Java Basics",
  "author": "Demo Author",
  "genre": "Education",
  "publicationYear": 2026
}
```

The response is `200` with the new book.
The database creates the ID. A new book has `available` set to `true`.

### Update or delete a book

Use an ID from the current GET `/api/books` response.
For PUT `/api/books/{id}`, send the same JSON fields and add `"available": false` or `true`.
For DELETE `/api/books/{id}`, use **Body > none**.

A successful update returns `200` with the book.
A successful delete returns `200` with an empty body.
Missing books return `404`. Invalid book data returns `400`.

### Check API access in Postman

| Request | Account | Expected status |
| --- | --- | --- |
| GET `/api/books` | No Auth | `401` |
| GET `/api/books` | Wrong password | `401` |
| GET `/api/books` | USER or ADMIN | `200` |
| POST, PUT, DELETE for books | USER | `403` |
| POST, PUT, DELETE for valid books | ADMIN | `200` |

After a blocked write request, use GET to check that the data did not change.
After a successful delete, GET for that ID must return `404`.
For No Auth, also remove any manually added `Authorization` header.

## Check the outgoing API call

Send GET `http://localhost:8080/api/integration/status` with Basic Auth `user` / `user`.
Java adds its own Bearer token when it calls the partner API.
It does not forward the user's Basic Auth credentials.

With matching tokens and mode `ok`, the response is `200`:

```json
{
  "status": "ok"
}
```

To test failures, keep Java running and restart only Node.js.
Press **Ctrl+C** before each Node.js restart.

```powershell
$env:EXTERNAL_API_TOKEN = "local-demo-token"
$env:DEMO_API_MODE = "forbidden"
node .\tools\partner-demo\server.cjs
```

Set `DEMO_API_MODE` to `slow` for a 5-second delay, or `ok` for a normal response.
For the wrong-token check, give Node.js a different token and keep Java's token unchanged.
Keep Basic Auth in Postman set to a valid demo account.

| Partner condition | Java status | Response |
| --- | --- | --- |
| Mode `ok`, matching tokens | `200` | `{"status":"ok"}` |
| Wrong partner token | `502` | `External API authentication failed` |
| Mode `forbidden` | `502` | `External API denied access` |
| Partner stopped | `503` | `External API is unavailable` |
| Mode `slow` | `504` | `External API timed out` |
| Other HTTP error | `502` | `External API returned an error` |
| Empty or invalid response | `502` | `Invalid response from external API` |

Errors use this JSON format:

```json
{
  "message": "External API is unavailable"
}
```

An external `401` is a problem with the partner credentials, so Java returns `502`.
A `401` from our API means the caller did not pass Basic Auth.
After failure checks, restart Node.js with matching tokens and mode `ok`. Check that Java returns `200` again.

You can also call the partner directly at `http://127.0.0.1:9090/partner/status`.
For that request, use **Bearer Token** in Postman, not Basic Auth.

## Automated tests

Set a local test token and run:

```powershell
$env:EXTERNAL_API_TOKEN = "local-test-token"
.\mvnw.cmd test
```

After Maven downloads the dependencies, you can use `.\mvnw.cmd -o test` to run offline.
The partner service does not need to run for these tests.
The tests start their own local HTTP servers on free ports.

The tests check:

- The Spring application context starts.
- The HTTP client sends a Bearer token and reads a valid response.
- External `401`, `403`, and `500` responses have safe messages.
- A stopped service returns `503`.
- A timeout before headers or during body reading returns `504`.
- A broken, empty, or invalid body returns `502`.
- The client does not follow redirects.
- Error responses do not expose the test token or private partner details.

Role checks and browser CSRF checks are manual in this project.

## Security notes

- Use demo accounts and HTTP only for local practice. Use HTTPS for real connections with credentials.
- Store real tokens outside source code. Do not log them or commit them to Git.
- CSRF stays enabled for browser forms. It is disabled only in the `/api/**` chain for this local Postman exercise.
- Browsers can send Basic Auth automatically. A stateless API can still have CSRF risks. Review this design before using the API from a browser.
- Book values use Thymeleaf `th:text`, which escapes HTML.

## Common problems

| Problem | What to check |
| --- | --- |
| `Could not resolve placeholder 'EXTERNAL_API_TOKEN'` | Set the token for the Java process, including IDE runs |
| API returns `401` | Check Basic Auth and the active `demo` profile |
| API returns `403` | Use ADMIN for a write request; browser forms also need CSRF |
| A book returns `404` | Use an ID from the current database; H2 is cleared after a restart |
| Integration returns `503` | Start Node.js and check its address and port |
| Integration returns `502` for authentication | Use the same partner token in Java and Node.js |
| Port is already in use | Stop the previous process before starting a new one |
