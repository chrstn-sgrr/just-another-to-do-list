<template>
  <div class="todo-list-container">
    <!-- Filters Section -->
    <div class="filters-section" v-if="todos.length > 0">
      <h3 class="filters-title">Filters</h3>
      
      <div class="filters-grid">
        <!-- Completion Filter -->
        <div class="filter-group">
          <label class="filter-label">Status:</label>
          <select v-model="completionFilter" class="filter-select">
            <option value="all">All Tasks</option>
            <option value="active">Active</option>
            <option value="completed">Completed</option>
          </select>
        </div>

        <!-- Priority Filter -->
        <div class="filter-group">
          <label class="filter-label">Priority:</label>
          <select v-model="priorityFilter" class="filter-select">
            <option value="all">All Priorities</option>
            <option value="HIGH">High</option>
            <option value="MEDIUM">Medium</option>
            <option value="LOW">Low</option>
          </select>
        </div>

        <!-- Sort Options -->
        <div class="filter-group">
          <label class="filter-label">Sort by:</label>
          <select v-model="sortBy" class="filter-select">
            <option value="created">Date Created</option>
            <option value="priority">Priority</option>
            <option value="title">Title</option>
          </select>
        </div>

        <!-- Clear Filters -->
        <button @click="clearFilters" class="clear-filters-btn">
          Clear Filters
        </button>
      </div>
    </div>

    <!-- Todo Count -->
    <div class="todo-stats" v-if="todos.length > 0">
      <p class="stats-text">
        Showing {{ filteredTodos.length }} of {{ todos.length }} tasks
        <span v-if="activeCount > 0" class="active-count">
          ({{ activeCount }} active)
        </span>
      </p>
    </div>

    <!-- Todo Items -->
    <div class="todos-container">
      <div v-if="filteredTodos.length === 0 && todos.length > 0" class="no-results">
        <p>No todos match your current filters.</p>
        <button @click="clearFilters" class="clear-filters-btn">
          Show All
        </button>
      </div>

      <div v-else-if="todos.length === 0" class="empty-state">
        <div class="empty-icon">📝</div>
        <h3>No todos yet!</h3>
        <p>Add your first todo above to get started.</p>
      </div>

      <div v-else class="todos-list">
        <TodoItem
          v-for="todo in filteredTodos"
          :key="todo.id"
          :todo="todo"
          @update-todo="handleUpdateTodo"
          @delete-todo="handleDeleteTodo"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import TodoItem from './TodoItem.vue'

// Props
const props = defineProps({
  todos: {
    type: Array,
    required: true
  }
})

// Emits
const emit = defineEmits(['update-todo', 'delete-todo'])

// Reactive data
const completionFilter = ref('all')
const priorityFilter = ref('all')
const sortBy = ref('created')

// Computed properties
const filteredTodos = computed(() => {
  let filtered = [...props.todos]

  // Filter by completion status
  if (completionFilter.value === 'active') {
    filtered = filtered.filter(todo => !todo.completed)
  } else if (completionFilter.value === 'completed') {
    filtered = filtered.filter(todo => todo.completed)
  }

  // Filter by priority
  if (priorityFilter.value !== 'all') {
    filtered = filtered.filter(todo => todo.priority === priorityFilter.value)
  }

  // Sort todos
  return sortTodos(filtered)
})

const activeCount = computed(() => {
  return props.todos.filter(todo => !todo.completed).length
})

// Methods
const sortTodos = (todos) => {
  const sorted = [...todos]

  switch (sortBy.value) {
    case 'priority':
      return sorted.sort((a, b) => {
        const priorityOrder = { 'HIGH': 3, 'MEDIUM': 2, 'LOW': 1 }
        return (priorityOrder[b.priority] || 2) - (priorityOrder[a.priority] || 2)
      })
    
    case 'title':
      return sorted.sort((a, b) => 
        (a.title || '').localeCompare(b.title || '')
      )
    
    case 'created':
    default:
      return sorted.sort((a, b) => 
        new Date(b.createdDate || 0) - new Date(a.createdDate || 0)
      )
  }
}

const clearFilters = () => {
  completionFilter.value = 'all'
  priorityFilter.value = 'all'
  sortBy.value = 'created'
}

const handleUpdateTodo = (updatedTodo) => {
  emit('update-todo', updatedTodo)
}

const handleDeleteTodo = (todoId) => {
  emit('delete-todo', todoId)
}

// Watch for prop changes to reset filters if needed
watch(() => props.todos.length, (newLength) => {
  if (newLength === 0) {
    clearFilters()
  }
})
</script>

<style scoped>
.todo-list-container {
  max-width: 100%;
}

/* Filters Section */
.filters-section {
  background: #f8fafc;
  border: 2px solid #e2e8f0;
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 24px;
}

.filters-title {
  font-size: 18px;
  font-weight: 600;
  color: #1e293b;
  margin: 0 0 16px 0;
  font-family: 'Comic Sans MS', cursive, sans-serif;
}

.filters-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
  gap: 16px;
  align-items: end;
}

.filter-group {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.filter-label {
  font-size: 14px;
  font-weight: 500;
  color: #374151;
  font-family: 'Comic Sans MS', cursive, sans-serif;
}

.filter-select {
  padding: 8px 12px;
  border: 2px solid #d1d5db;
  border-radius: 8px;
  background: white;
  font-size: 14px;
  color: #374151;
  cursor: pointer;
  transition: all 0.2s ease;
}

.filter-select:focus {
  outline: none;
  border-color: #6366f1;
  box-shadow: 0 0 0 3px rgba(99, 102, 241, 0.1);
}

.filter-select:hover {
  border-color: #9ca3af;
}

.clear-filters-btn {
  padding: 8px 16px;
  background: #fee2e2;
  color: #dc2626;
  border: 2px solid #fca5a5;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
  font-family: 'Comic Sans MS', cursive, sans-serif;
}

.clear-filters-btn:hover {
  background: #fecaca;
  transform: translateY(-1px);
}

/* Todo Stats */
.todo-stats {
  margin-bottom: 16px;
}

.stats-text {
  font-size: 14px;
  color: #6b7280;
  margin: 0;
  font-style: italic;
}

.active-count {
  color: #059669;
  font-weight: 500;
}

/* Todos Container */
.todos-container {
  min-height: 200px;
}

.todos-list {
  display: flex;
  flex-direction: column;
}

/* Empty States */
.empty-state, .no-results {
  text-align: center;
  padding: 48px 24px;
  color: #6b7280;
}

.empty-state {
  background: #f9fafb;
  border: 2px dashed #d1d5db;
  border-radius: 12px;
}

.empty-icon {
  font-size: 48px;
  margin-bottom: 16px;
}

.empty-state h3 {
  font-size: 20px;
  color: #374151;
  margin: 0 0 8px 0;
  font-family: 'Comic Sans MS', cursive, sans-serif;
}

.empty-state p {
  font-size: 16px;
  margin: 0;
}

.no-results {
  background: #fef3cd;
  border: 2px solid #f59e0b;
  border-radius: 12px;
}

.no-results p {
  margin: 0 0 16px 0;
  font-size: 16px;
  color: #92400e;
}

/* Responsive Design */
@media (max-width: 768px) {
  .filters-grid {
    grid-template-columns: 1fr;
    gap: 12px;
  }

  .filters-section {
    padding: 16px;
  }

  .empty-state, .no-results {
    padding: 32px 16px;
  }

  .empty-icon {
    font-size: 36px;
  }
}

@media (max-width: 480px) {
  .filter-group {
    gap: 4px;
  }

  .filter-label {
    font-size: 12px;
  }

  .filter-select {
    padding: 6px 10px;
    font-size: 13px;
  }
}
</style>
