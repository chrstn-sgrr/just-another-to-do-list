<template>
  <div class="add-todo-form">
    <h2 class="form-title">Add New Todo</h2>
    
    <form @submit.prevent="handleSubmit" class="todo-form">
      <!-- Title Input -->
      <div class="input-group">
        <label for="todo-title" class="input-label">
          What needs to be done?
        </label>
        <input
          id="todo-title"
          v-model="newTodo.title"
          type="text"
          placeholder="Enter your todo..."
          class="title-input"
          :class="{ 'error': titleError }"
          maxlength="500"
          required
          ref="titleInput"
        />
        <div v-if="titleError" class="error-message">
          {{ titleError }}
        </div>
        <div class="char-count">
          {{ newTodo.title.length }}/500
        </div>
      </div>

      <!-- Priority Selection -->
      <div class="input-group">
        <label for="todo-priority" class="input-label">
          Priority Level
        </label>
        <select
          id="todo-priority"
          v-model="newTodo.priority"
          class="priority-select"
        >
          <option value="LOW">🔵 Low Priority</option>
          <option value="MEDIUM">🟡 Medium Priority</option>
          <option value="HIGH">🔴 High Priority</option>
        </select>
      </div>

      <!-- Action Buttons -->
      <div class="form-actions">
        <button
          type="submit"
          class="add-btn"
          :disabled="!canSubmit || isSubmitting"
          :class="{ 'submitting': isSubmitting }"
        >
          <span v-if="isSubmitting" class="loading-spinner">⏳</span>
          <span v-else>➕</span>
          {{ isSubmitting ? 'Adding...' : 'Add Todo' }}
        </button>

        <button
          type="button"
          @click="resetForm"
          class="reset-btn"
          :disabled="isSubmitting"
        >
          Clear
        </button>
      </div>
    </form>

    <!-- Quick Add Presets -->
    <div class="quick-presets">
      <p class="presets-label">Quick Add:</p>
      <div class="preset-buttons">
        <button
          v-for="preset in quickPresets"
          :key="preset.title"
          @click="applyPreset(preset)"
          class="preset-btn"
          :disabled="isSubmitting"
        >
          {{ preset.emoji }} {{ preset.title }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, nextTick, onMounted } from 'vue'

// Emits
const emit = defineEmits(['add-todo'])

// Props (for loading state)
const props = defineProps({
  isLoading: {
    type: Boolean,
    default: false
  }
})

// Template refs
const titleInput = ref(null)

// Reactive data
const newTodo = ref({
  title: '',
  priority: 'MEDIUM'
})

const titleError = ref('')
const isSubmitting = ref(false)

// Quick presets for common todos
const quickPresets = ref([
  { title: 'Buy groceries', priority: 'MEDIUM', emoji: '🛒' },
  { title: 'Call mom', priority: 'HIGH', emoji: '📞' },
  { title: 'Exercise', priority: 'LOW', emoji: '💪' },
  { title: 'Read book', priority: 'LOW', emoji: '📚' }
])

// Computed properties
const canSubmit = computed(() => {
  return newTodo.value.title.trim().length >= 2 && !titleError.value
})

// Validation
const validateTitle = () => {
  const title = newTodo.value.title.trim()
  
  if (title.length === 0) {
    titleError.value = ''
    return true
  }
  
  if (title.length < 2) {
    titleError.value = 'Title must be at least 2 characters long'
    return false
  }
  
  if (title.length > 500) {
    titleError.value = 'Title is too long (max 500 characters)'
    return false
  }
  
  titleError.value = ''
  return true
}

// Watch for title changes to validate
const watchTitle = () => {
  validateTitle()
}

// Methods
const handleSubmit = async () => {
  if (!validateTitle() || !canSubmit.value) {
    return
  }

  try {
    isSubmitting.value = true
    
    const todoData = {
      title: newTodo.value.title.trim(),
      priority: newTodo.value.priority,
      completed: false
    }

    emit('add-todo', todoData)
    
    // Reset form after successful submission
    resetForm()
    
    // Focus back on title input
    await nextTick()
    titleInput.value?.focus()
    
  } catch (error) {
    console.error('Error in form submission:', error)
  } finally {
    isSubmitting.value = false
  }
}

const resetForm = () => {
  newTodo.value = {
    title: '',
    priority: 'MEDIUM'
  }
  titleError.value = ''
}

const applyPreset = (preset) => {
  newTodo.value.title = preset.title
  newTodo.value.priority = preset.priority
  titleInput.value?.focus()
}

// Lifecycle
onMounted(() => {
  titleInput.value?.focus()
})

// Watch title for validation
const unwatchTitle = (() => {
  let timeout
  return () => {
    clearTimeout(timeout)
    timeout = setTimeout(watchTitle, 300) // Debounce validation
  }
})()

// Watch title changes
const watchTitleChanges = () => {
  unwatchTitle()
}
</script>

<style scoped>
.add-todo-form {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 16px;
  padding: 24px;
  margin-bottom: 32px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  color: white;
}

.form-title {
  font-size: 24px;
  font-weight: 700;
  margin: 0 0 20px 0;
  text-align: center;
  font-family: 'Comic Sans MS', cursive, sans-serif;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
}

.todo-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

/* Input Groups */
.input-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.input-label {
  font-size: 14px;
  font-weight: 600;
  color: rgba(255, 255, 255, 0.9);
  font-family: 'Comic Sans MS', cursive, sans-serif;
}

/* Title Input */
.title-input {
  padding: 12px 16px;
  border: 2px solid rgba(255, 255, 255, 0.2);
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.1);
  color: white;
  font-size: 16px;
  transition: all 0.3s ease;
  backdrop-filter: blur(10px);
}

.title-input::placeholder {
  color: rgba(255, 255, 255, 0.6);
}

.title-input:focus {
  outline: none;
  border-color: rgba(255, 255, 255, 0.5);
  background: rgba(255, 255, 255, 0.15);
  box-shadow: 0 0 0 4px rgba(255, 255, 255, 0.1);
}

.title-input.error {
  border-color: #ef4444;
  background: rgba(239, 68, 68, 0.1);
}

.char-count {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.7);
  text-align: right;
}

.error-message {
  font-size: 12px;
  color: #fca5a5;
  font-weight: 500;
}

/* Priority Select */
.priority-select {
  padding: 12px 16px;
  border: 2px solid rgba(255, 255, 255, 0.2);
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.1);
  color: white;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s ease;
  backdrop-filter: blur(10px);
}

.priority-select:focus {
  outline: none;
  border-color: rgba(255, 255, 255, 0.5);
  background: rgba(255, 255, 255, 0.15);
}

.priority-select option {
  background: #374151;
  color: white;
}

/* Form Actions */
.form-actions {
  display: flex;
  gap: 12px;
}

.add-btn {
  flex: 1;
  padding: 14px 24px;
  background: rgba(255, 255, 255, 0.2);
  color: white;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-radius: 12px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  font-family: 'Comic Sans MS', cursive, sans-serif;
}

.add-btn:hover:not(:disabled) {
  background: rgba(255, 255, 255, 0.3);
  transform: translateY(-2px);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.2);
}

.add-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
  transform: none;
}

.add-btn.submitting {
  background: rgba(255, 255, 255, 0.1);
}

.loading-spinner {
  animation: spin 1s linear infinite;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.reset-btn {
  padding: 14px 20px;
  background: transparent;
  color: rgba(255, 255, 255, 0.8);
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-radius: 12px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  font-family: 'Comic Sans MS', cursive, sans-serif;
}

.reset-btn:hover:not(:disabled) {
  background: rgba(255, 255, 255, 0.1);
  color: white;
}

.reset-btn:disabled {
  opacity: 0.3;
  cursor: not-allowed;
}

/* Quick Presets */
.quick-presets {
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid rgba(255, 255, 255, 0.2);
}

.presets-label {
  font-size: 14px;
  font-weight: 600;
  margin: 0 0 12px 0;
  color: rgba(255, 255, 255, 0.9);
  font-family: 'Comic Sans MS', cursive, sans-serif;
}

.preset-buttons {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(120px, 1fr));
  gap: 8px;
}

.preset-btn {
  padding: 8px 12px;
  background: rgba(255, 255, 255, 0.1);
  color: rgba(255, 255, 255, 0.9);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 8px;
  font-size: 12px;
  cursor: pointer;
  transition: all 0.2s ease;
  text-align: center;
}

.preset-btn:hover:not(:disabled) {
  background: rgba(255, 255, 255, 0.2);
  color: white;
  transform: translateY(-1px);
}

.preset-btn:disabled {
  opacity: 0.3;
  cursor: not-allowed;
}

/* Responsive Design */
@media (max-width: 640px) {
  .add-todo-form {
    padding: 20px;
    margin-bottom: 24px;
  }

  .form-title {
    font-size: 20px;
  }

  .form-actions {
    flex-direction: column;
  }

  .preset-buttons {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 480px) {
  .add-todo-form {
    padding: 16px;
  }

  .title-input, .priority-select {
    padding: 10px 12px;
    font-size: 14px;
  }

  .add-btn {
    padding: 12px 20px;
    font-size: 14px;
  }
}
</style>
