# CORS Configuration for Todo API

## 🚨 Important: CORS Setup Required

Your Vue.js frontend (running on `http://localhost:5173`) needs to communicate with your Java API (running on `http://localhost:8080`). Currently, your API doesn't have CORS configured, which will block frontend requests.

## Quick Fix

Add this to your `TodoController.java` class:

```java
@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/todos")
public class TodoController {
    // ... existing code
}
```

## Better Solution (Recommended)

Create a global CORS configuration by adding this class to your `com.example.product_api` package:

```java
package com.example.product_api;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**")
                        .allowedOrigins("http://localhost:5173", "http://localhost:3000")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(true);
            }
        };
    }
}
```

## Testing

1. Start your Java API: `mvn spring-boot:run` (in the `to-do-api` directory)
2. Start your Vue frontend: `npm run dev` (in the `to-do-app` directory)
3. Open `http://localhost:5173` in your browser

The frontend will automatically detect if the API is connected and show a connection status indicator in the header.

## API Endpoints

Your frontend is configured to use these endpoints:

- `GET /api/todos` - Fetch all todos
- `POST /api/todos` - Create new todo
- `PUT /api/todos/{id}` - Update todo
- `DELETE /api/todos/{id}` - Delete todo
- `PATCH /api/todos/{id}/complete` - Toggle completion status
- `GET /api/todos/completed/{status}` - Filter by completion status
