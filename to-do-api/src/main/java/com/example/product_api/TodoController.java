package com.example.product_api;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/api/todos")
public class TodoController {

    private static final Logger logger = LoggerFactory.getLogger(TodoController.class);
    
    private final List<Todo> todos = new ArrayList<>();
    private final AtomicLong idCounter = new AtomicLong(1);

    // CREATE - Add new todo
    @PostMapping
    public Todo createTodo(@RequestBody Todo todo) {
        todo.setId(idCounter.getAndIncrement());
        todos.add(todo);
        
        logger.info("Created new todo: {}", todo);
        return todo;
    }

    // READ - Get all todos
    @GetMapping
    public List<Todo> getAllTodos() {
        logger.info("Retrieved all todos. Total count: {}", todos.size());
        return todos;
    }

    // READ - Get specific todo by ID
    @GetMapping("/{id}")
    public ResponseEntity<Todo> getTodoById(@PathVariable Long id) {
        Optional<Todo> todo = todos.stream()
                .filter(t -> t.getId().equals(id))
                .findFirst();
        
        if (todo.isPresent()) {
            logger.info("Retrieved todo with ID: {}", id);
            return ResponseEntity.ok(todo.get());
        } else {
            logger.warn("Todo with ID {} not found", id);
            return ResponseEntity.notFound().build();
        }
    }

    // UPDATE - Update existing todo
    @PutMapping("/{id}")
    public ResponseEntity<Todo> updateTodo(@PathVariable Long id, @RequestBody Todo updatedTodo) {
        for (int i = 0; i < todos.size(); i++) {
            Todo todo = todos.get(i);
            if (todo.getId().equals(id)) {
                updatedTodo.setId(id);
                updatedTodo.setCreatedDate(todo.getCreatedDate()); // Keep original creation date
                todos.set(i, updatedTodo);
                
                logger.info("Updated todo with ID: {}", id);
                return ResponseEntity.ok(updatedTodo);
            }
        }
        
        logger.warn("Todo with ID {} not found for update", id);
        return ResponseEntity.notFound().build();
    }

    // DELETE - Remove todo
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTodo(@PathVariable Long id) {
        boolean removed = todos.removeIf(todo -> todo.getId().equals(id));
        
        if (removed) {
            logger.info("Deleted todo with ID: {}", id);
            return ResponseEntity.ok().build();
        } else {
            logger.warn("Todo with ID {} not found for deletion", id);
            return ResponseEntity.notFound().build();
        }
    }

    // PATCH - Mark todo as complete/incomplete
    @PatchMapping("/{id}/complete")
    public ResponseEntity<Todo> toggleComplete(@PathVariable Long id) {
        Optional<Todo> todoOpt = todos.stream()
                .filter(t -> t.getId().equals(id))
                .findFirst();
        
        if (todoOpt.isPresent()) {
            Todo todo = todoOpt.get();
            todo.setCompleted(!todo.getCompleted());
            
            logger.info("Toggled completion status for todo ID: {} to {}", id, todo.getCompleted());
            return ResponseEntity.ok(todo);
        } else {
            logger.warn("Todo with ID {} not found for completion toggle", id);
            return ResponseEntity.notFound().build();
        }
    }

    // BONUS - Get todos by completion status
    @GetMapping("/completed/{status}")
    public List<Todo> getTodosByCompletionStatus(@PathVariable Boolean status) {
        List<Todo> filteredTodos = todos.stream()
                .filter(todo -> todo.getCompleted().equals(status))
                .toList();
        
        logger.info("Retrieved {} todos with completion status: {}", filteredTodos.size(), status);
        return filteredTodos;
    }
}
