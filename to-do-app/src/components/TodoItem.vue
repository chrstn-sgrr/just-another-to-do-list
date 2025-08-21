<template>
  <div class="todo-item" :class="{ 'completed': todo.completed }">
    <div class="todo-content">
      <!-- Completion Toggle -->
      <button 
        @click="toggleComplete" 
        class="complete-btn"
        :class="{ 'checked': todo.completed }"
        :aria-label="todo.completed ? 'Mark as incomplete' : 'Mark as complete'"
      >
        <span class="checkmark" v-if="todo.completed">✓</span>
      </button>

      <!-- Todo Title and Details -->
      <div class="todo-details">
        <h3 class="todo-title" :class="{ 'line-through': todo.completed }">
          {{ todo.title }}
        </h3>
        
        <div class="todo-meta">
          <span class="priority-badge" :class="priorityClass">
            {{ todo.priority }}
          </span>
          <span class="created-date">
            {{ formatDate(todo.createdDate) }}
          </span>
        </div>
      </div>

      <!-- Delete Button -->
      <button 
        @click="deleteTodo" 
        class="delete-btn"
        aria-label="Delete todo"
      >
        🗑️
      </button>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

// Props
const props = defineProps({
  todo: {
    type: Object,
    required: true
  }
})

// Emits
const emit = defineEmits(['update-todo', 'delete-todo'])

// Computed properties
const priorityClass = computed(() => {
  return `priority-${props.todo.priority?.toLowerCase() || 'medium'}`
})

// Methods
const toggleComplete = () => {
  const updatedTodo = { ...props.todo, completed: !props.todo.completed }
  emit('update-todo', updatedTodo)
}

const deleteTodo = () => {
  emit('delete-todo', props.todo.id)
}

const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleDateString('en-US', { 
    month: 'short', 
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  })
}
</script>

<style scoped>
.todo-item {
  background: #fefefe;
  border: 2px solid #e5e7eb;
  border-radius: 12px;
  padding: 16px;
  margin-bottom: 12px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
  transition: all 0.2s ease;
  position: relative;
}

.todo-item:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transform: translateY(-1px);
}

.todo-item.completed {
  opacity: 0.7;
  background: #f8f9fa;
}

.todo-content {
  display: flex;
  align-items: flex-start;
  gap: 12px;
}

/* Complete Button */
.complete-btn {
  width: 24px;
  height: 24px;
  border: 2px solid #d1d5db;
  border-radius: 50%;
  background: white;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s ease;
  flex-shrink: 0;
  margin-top: 2px;
}

.complete-btn:hover {
  border-color: #6366f1;
  background: #f0f9ff;
}

.complete-btn.checked {
  background: #6366f1;
  border-color: #6366f1;
  color: white;
}

.checkmark {
  font-size: 14px;
  font-weight: bold;
}

/* Todo Details */
.todo-details {
  flex-grow: 1;
  min-width: 0;
}

.todo-title {
  font-size: 16px;
  font-weight: 500;
  color: #1f2937;
  margin: 0 0 8px 0;
  line-height: 1.4;
  word-wrap: break-word;
  font-family: 'Comic Sans MS', cursive, sans-serif;
}

.todo-title.line-through {
  text-decoration: line-through;
  color: #6b7280;
}

.todo-meta {
  display: flex;
  gap: 12px;
  align-items: center;
  flex-wrap: wrap;
}

/* Priority Badge */
.priority-badge {
  padding: 2px 8px;
  border-radius: 12px;
  font-size: 11px;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  border: 1.5px solid;
}

.priority-high {
  background: #fef2f2;
  color: #dc2626;
  border-color: #fca5a5;
}

.priority-medium {
  background: #fffbeb;
  color: #d97706;
  border-color: #fde68a;
}

.priority-low {
  background: #f0f9ff;
  color: #0284c7;
  border-color: #7dd3fc;
}

/* Created Date */
.created-date {
  font-size: 12px;
  color: #6b7280;
  font-style: italic;
}

/* Delete Button */
.delete-btn {
  background: none;
  border: none;
  cursor: pointer;
  padding: 4px;
  border-radius: 6px;
  transition: all 0.2s ease;
  flex-shrink: 0;
  opacity: 0.7;
}

.delete-btn:hover {
  background: #fef2f2;
  opacity: 1;
  transform: scale(1.1);
}

/* Responsive */
@media (max-width: 640px) {
  .todo-item {
    padding: 12px;
  }
  
  .todo-title {
    font-size: 14px;
  }
  
  .todo-meta {
    gap: 8px;
  }
}
</style>
