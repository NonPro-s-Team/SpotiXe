import axios from "axios";
import toast from "react-hot-toast";
import { getToken, clearAuthData } from "../utils/tokenStorage";

// Base API configuration
const API_BASE_URL =
  import.meta.env.VITE_API_BASE_URL || "http://localhost:3001";

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    "Content-Type": "application/json",
  },
});

// Request interceptor to add JWT token
api.interceptors.request.use(
  async (config) => {
    // Get JWT token from localStorage
    const token = getToken();
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Response interceptor for error handling
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      // Clear auth data and redirect to login on unauthorized
      clearAuthData();
      window.location.href = "/login";
    }

    const message = error.response?.data?.message || "An error occurred";
    toast.error(message);

    return Promise.reject(error);
  }
);

export default api;
