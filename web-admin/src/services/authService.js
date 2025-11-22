/**
 * Backend Authentication Service
 * Xử lý authentication với backend API
 */

import axios from "axios";
import { setToken, setUserData, clearAuthData } from "../utils/tokenStorage";

// Base URL - bỏ /api ở cuối vì endpoint auth không cần /api prefix
const API_BASE_URL = "https://api.spotixe.io.vn";

/**
 * Đăng nhập với backend bằng Firebase token
 * @param {string} firebaseToken - Firebase ID token
 * @returns {Promise<Object>} Response từ backend
 */
export const loginWithBackend = async (firebaseToken) => {
  try {
    // Endpoint: /auth/login (không có /api prefix)
    const endpoint = "/auth/login";
    const fullUrl = `${API_BASE_URL}${endpoint}`;

    console.log("🔐 Login Request:", {
      url: fullUrl,
      baseUrl: API_BASE_URL,
      endpoint: endpoint,
      hasToken: !!firebaseToken,
      tokenPreview: firebaseToken?.substring(0, 20) + "...",
    });

    const response = await axios.post(
      fullUrl,
      {}, // Empty body
      {
        headers: {
          Authorization: `Bearer ${firebaseToken}`,
          "Content-Type": "application/json",
        },
      }
    );

    console.log("✅ Login Response:", response.data);
    const data = response.data;

    // Kiểm tra response format
    if (data.success && data.token) {
      // Lưu JWT token và user data vào localStorage
      setToken(data.token);
      setUserData(data.user);

      return {
        success: true,
        token: data.token,
        user: data.user,
        expiresIn: data.expiresIn,
      };
    } else {
      throw new Error("Invalid response format from backend");
    }
  } catch (error) {
    console.error("❌ Backend login error:", {
      message: error.message,
      status: error.response?.status,
      statusText: error.response?.statusText,
      url: error.config?.url,
      data: error.response?.data,
      headers: error.config?.headers,
    });

    // Clear any existing auth data
    clearAuthData();

    // Parse error message
    let errorMessage = "Failed to authenticate with backend";

    if (error.response) {
      // Backend returned an error response
      console.error("Response Error:", error.response);
      errorMessage =
        error.response.data?.message ||
        error.response.statusText ||
        errorMessage;
    } else if (error.request) {
      // Request was made but no response received
      console.error("Request Error:", error.request);
      errorMessage = "Cannot connect to backend server";
    } else {
      // Something else happened
      console.error("Other Error:", error.message);
      errorMessage = error.message || errorMessage;
    }

    return {
      success: false,
      error: errorMessage,
    };
  }
};

/**
 * Kiểm tra token còn hạn không bằng cách gọi /auth/me
 * @returns {Promise<Object>} { valid: boolean, user?: Object, error?: string }
 */
export const validateToken = async () => {
  try {
    const { getToken } = await import("../utils/tokenStorage");
    const token = getToken();

    if (!token) {
      return { valid: false, error: "No token found" };
    }

    const endpoint = "/auth/me";
    const fullUrl = `${API_BASE_URL}${endpoint}`;

    console.log("🔍 Validating token...");

    const response = await axios.get(fullUrl, {
      headers: {
        Authorization: `Bearer ${token}`,
        "Content-Type": "application/json",
      },
    });

    console.log("✅ Token is valid:", response.data);

    // Token còn hạn - cập nhật user data nếu cần
    if (response.data) {
      setUserData(response.data);
    }

    return {
      valid: true,
      user: response.data,
    };
  } catch (error) {
    console.error("❌ Token validation failed:", {
      status: error.response?.status,
      statusText: error.response?.statusText,
    });

    // Nếu là 401 Unauthorized - token hết hạn hoặc không hợp lệ
    if (error.response?.status === 401) {
      clearAuthData();
      return {
        valid: false,
        error: "Token expired or invalid",
        unauthorized: true,
      };
    }

    // Các lỗi khác (network, server error...)
    return {
      valid: false,
      error: error.message || "Token validation failed",
    };
  }
};

/**
 * Logout - xóa token và user data
 */
export const logoutFromBackend = () => {
  clearAuthData();
};
