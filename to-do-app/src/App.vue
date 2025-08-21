<script setup>
import { ref, onMounted } from 'vue'
import AddTodoForm from './components/AddTodoForm.vue'
import TodoList from './components/TodoList.vue'
import { todoApiService } from './services/todoApiService.js'

// Reactive state
const todos = ref([])
const isLoading = ref(false)
const error = ref('')
const isApiConnected = ref(false)

// Lifecycle - Load todos on component mount
onMounted(async () => {
  await loadTodos()
})

// Methods
const loadTodos = async () => {
  try {
    isLoading.value = true
    error.value = ''
    
    const todosData = await todoApiService.fetchTodos()
    todos.value = todosData
    isApiConnected.value = true
    
  } catch (err) {
    console.error('Failed to load todos:', err)
    error.value = err.message || 'Failed to connect to the API server'
    isApiConnected.value = false
  } finally {
    isLoading.value = false
  }
}

const handleAddTodo = async (newTodoData) => {
  try {
    error.value = ''
    
    const createdTodo = await todoApiService.createTodo(newTodoData)
    todos.value.unshift(createdTodo) // Add to beginning of list
    
  } catch (err) {
    console.error('Failed to create todo:', err)
    error.value = err.message || 'Failed to create todo'
  }
}

const handleUpdateTodo = async (updatedTodo) => {
  try {
    error.value = ''
    
    // Optimistic update - update UI first
    const todoIndex = todos.value.findIndex(todo => todo.id === updatedTodo.id)
    if (todoIndex !== -1) {
      const originalTodo = { ...todos.value[todoIndex] }
      todos.value[todoIndex] = updatedTodo
      
      try {
        // If it's just a completion toggle, use the toggle endpoint
        if (originalTodo.completed !== updatedTodo.completed && 
            originalTodo.title === updatedTodo.title && 
            originalTodo.priority === updatedTodo.priority) {
          await todoApiService.toggleTodoComplete(updatedTodo.id)
        } else {
          // Full update
          await todoApiService.updateTodo(updatedTodo)
        }
      } catch (err) {
        // Revert on failure
        todos.value[todoIndex] = originalTodo
        throw err
      }
    }
    
  } catch (err) {
    console.error('Failed to update todo:', err)
    error.value = err.message || 'Failed to update todo'
  }
}

const handleDeleteTodo = async (todoId) => {
  try {
    error.value = ''
    
    // Optimistic update - remove from UI first
    const todoIndex = todos.value.findIndex(todo => todo.id === todoId)
    if (todoIndex !== -1) {
      const removedTodo = todos.value.splice(todoIndex, 1)[0]
      
      try {
        await todoApiService.deleteTodo(todoId)
      } catch (err) {
        // Revert on failure
        todos.value.splice(todoIndex, 0, removedTodo)
        throw err
      }
    }
    
  } catch (err) {
    console.error('Failed to delete todo:', err)
    error.value = err.message || 'Failed to delete todo'
  }
}

const retryConnection = async () => {
  await loadTodos()
}

const dismissError = () => {
  error.value = ''
}
</script>

<template>
  <div class="app">
    <!-- Header -->
    <header class="app-header">
      <div class="header-content">
        <h1 class="app-title">
          📝 Just Another To-Do List
        </h1>
        <p class="app-subtitle">
          Stay organized, stay productive!
        </p>
        
        <!-- Connection Status -->
        <div class="connection-status" :class="{ 'connected': isApiConnected, 'disconnected': !isApiConnected }">
          <span class="status-indicator"></span>
          {{ isApiConnected ? 'Connected to API' : 'API Disconnected' }}
        </div>
      </div>
    </header>

    <!-- Main Content -->
    <main class="app-main">
      <div class="container">
        
        <!-- Error Display -->
        <div v-if="error" class="error-banner">
          <div class="error-content">
            <span class="error-icon">⚠️</span>
            <div class="error-text">
              <p class="error-message">{{ error }}</p>
              <p class="error-help" v-if="!isApiConnected">
                Make sure your Java API server is running on localhost:8080 and has CORS configured.
              </p>
            </div>
            <div class="error-actions">
              <button @click="retryConnection" class="retry-btn" :disabled="isLoading">
                {{ isLoading ? 'Retrying...' : 'Retry' }}
              </button>
              <button @click="dismissError" class="dismiss-btn">
                ×
              </button>
            </div>
          </div>
        </div>

        <!-- Loading State -->
        <div v-if="isLoading && todos.length === 0" class="loading-state">
          <div class="loading-spinner">⏳</div>
          <p>Loading your todos...</p>
        </div>

        <!-- Main App Content -->
        <div v-else class="app-content">
          <!-- Add Todo Form -->
          <AddTodoForm 
            @add-todo="handleAddTodo"
            :is-loading="isLoading"
          />

          <!-- Todo List -->
          <TodoList
            :todos="todos"
            @update-todo="handleUpdateTodo"
            @delete-todo="handleDeleteTodo"
          />
        </div>
      </div>
    </main>

    <!-- Footer -->
    <footer class="app-footer">
      <p>Built with Vue 3 + Vite | Connected to Java Spring Boot API</p>
    </footer>
  </div>
</template>

<style scoped>
/* Global App Styles */
.app {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  background-attachment: fixed;
  font-family: 'Comic Sans MS', cursive, -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
}

/* Header */
.app-header {
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  border-bottom: 1px solid rgba(255, 255, 255, 0.2);
  padding: 24px 0;
  text-align: center;
  color: white;
}

.header-content {
  max-width: 800px;
  margin: 0 auto;
  padding: 0 20px;
}

.app-title {
  font-size: 2.5rem;
  font-weight: 800;
  margin: 0 0 8px 0;
  text-shadow: 0 2px 10px rgba(0, 0, 0, 0.3);
  background: linear-gradient(45deg, #fff, #f0f9ff);
  background-clip: text;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.app-subtitle {
  font-size: 1.1rem;
  margin: 0 0 16px 0;
  opacity: 0.9;
  font-weight: 500;
}

/* Connection Status */
.connection-status {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 6px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.connection-status.connected {
  background: rgba(34, 197, 94, 0.2);
  border: 1px solid rgba(34, 197, 94, 0.4);
  color: #bbf7d0;
}

.connection-status.disconnected {
  background: rgba(239, 68, 68, 0.2);
  border: 1px solid rgba(239, 68, 68, 0.4);
  color: #fca5a5;
}

.status-indicator {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  animation: pulse 2s infinite;
}

.connected .status-indicator {
  background: #22c55e;
}

.disconnected .status-indicator {
  background: #ef4444;
}

@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.5; }
}

/* Main Content */
.app-main {
  flex: 1;
  padding: 32px 0;
}

.container {
  max-width: 800px;
  margin: 0 auto;
  padding: 0 20px;
}

/* Error Banner */
.error-banner {
  background: rgba(239, 68, 68, 0.1);
  border: 2px solid rgba(239, 68, 68, 0.3);
  border-radius: 12px;
  padding: 16px;
  margin-bottom: 24px;
  backdrop-filter: blur(10px);
}

.error-content {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  color: white;
}

.error-icon {
  font-size: 20px;
  flex-shrink: 0;
}

.error-text {
  flex: 1;
}

.error-message {
  margin: 0 0 4px 0;
  font-weight: 600;
  font-size: 14px;
}

.error-help {
  margin: 0;
  font-size: 12px;
  opacity: 0.9;
  line-height: 1.4;
}

.error-actions {
  display: flex;
  gap: 8px;
  flex-shrink: 0;
}

.retry-btn, .dismiss-btn {
  padding: 6px 12px;
  border: 1px solid rgba(255, 255, 255, 0.3);
  border-radius: 6px;
  background: rgba(255, 255, 255, 0.1);
  color: white;
  font-size: 12px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.retry-btn:hover:not(:disabled) {
  background: rgba(255, 255, 255, 0.2);
}

.retry-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.dismiss-btn {
  width: 28px;
  height: 28px;
  padding: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  font-weight: bold;
}

.dismiss-btn:hover {
  background: rgba(255, 255, 255, 0.2);
}

/* Loading State */
.loading-state {
  text-align: center;
  padding: 80px 20px;
  color: white;
}

.loading-spinner {
  font-size: 48px;
  margin-bottom: 16px;
  animation: spin 1s linear infinite;
}

.loading-state p {
  font-size: 18px;
  margin: 0;
  opacity: 0.9;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

/* App Content */
.app-content {
  animation: fadeIn 0.5s ease-out;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}

/* Footer */
.app-footer {
  background: rgba(0, 0, 0, 0.2);
  color: rgba(255, 255, 255, 0.7);
  text-align: center;
  padding: 16px;
  font-size: 12px;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
}

/* Responsive Design */
@media (max-width: 768px) {
  .app-title {
    font-size: 2rem;
  }
  
  .app-subtitle {
    font-size: 1rem;
  }
  
  .container {
    padding: 0 16px;
  }
  
  .app-main {
    padding: 24px 0;
  }
  
  .error-content {
    flex-direction: column;
    gap: 12px;
  }
  
  .error-actions {
    align-self: flex-start;
  }
}

@media (max-width: 480px) {
  .app-header {
    padding: 20px 0;
  }
  
  .app-title {
    font-size: 1.75rem;
  }
  
  .container {
    padding: 0 12px;
  }
  
  .error-banner {
    padding: 12px;
  }
}
</style>
