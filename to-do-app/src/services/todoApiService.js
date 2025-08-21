import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api/todos';

// Create axios instance with base configuration
const apiClient = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// API service functions
export const todoApiService = {
  // Fetch all todos
  async fetchTodos() {
    try {
      const response = await apiClient.get('/');
      return response.data;
    } catch (error) {
      console.error('Error fetching todos:', error);
      throw new Error('Failed to fetch todos. Please ensure the API server is running.');
    }
  },

  // Create a new todo
  async createTodo(newTodoData) {
    try {
      const response = await apiClient.post('/', newTodoData);
      return response.data;
    } catch (error) {
      console.error('Error creating todo:', error);
      throw new Error('Failed to create todo. Please try again.');
    }
  },

  // Update an existing todo
  async updateTodo(todo) {
    try {
      const response = await apiClient.put(`/${todo.id}`, todo);
      return response.data;
    } catch (error) {
      console.error('Error updating todo:', error);
      throw new Error('Failed to update todo. Please try again.');
    }
  },

  // Delete a todo
  async deleteTodo(todoId) {
    try {
      await apiClient.delete(`/${todoId}`);
      return true;
    } catch (error) {
      console.error('Error deleting todo:', error);
      throw new Error('Failed to delete todo. Please try again.');
    }
  },

  // Toggle todo completion status
  async toggleTodoComplete(todoId) {
    try {
      const response = await apiClient.patch(`/${todoId}/complete`);
      return response.data;
    } catch (error) {
      console.error('Error toggling todo completion:', error);
      throw new Error('Failed to update todo status. Please try again.');
    }
  },

  // Get todos by completion status (bonus endpoint)
  async getTodosByCompletionStatus(isCompleted) {
    try {
      const response = await apiClient.get(`/completed/${isCompleted}`);
      return response.data;
    } catch (error) {
      console.error('Error fetching todos by status:', error);
      throw new Error('Failed to filter todos. Please try again.');
    }
  },

  // Get a specific todo by ID
  async getTodoById(todoId) {
    try {
      const response = await apiClient.get(`/${todoId}`);
      return response.data;
    } catch (error) {
      console.error('Error fetching todo by ID:', error);
      throw new Error('Failed to fetch todo. Please try again.');
    }
  }
};
