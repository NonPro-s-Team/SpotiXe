/**
 * Token Storage Utility
 * Quản lý JWT token trong localStorage
 */

const TOKEN_KEY = "spotixe_jwt_token";
const USER_DATA_KEY = "spotixe_user_data";

/**
 * Lưu JWT token vào localStorage
 * @param {string} token - JWT token từ backend
 */
export const setToken = (token) => {
  if (token) {
    localStorage.setItem(TOKEN_KEY, token);
  }
};

/**
 * Lấy JWT token từ localStorage
 * @returns {string|null} JWT token hoặc null nếu không có
 */
export const getToken = () => {
  return localStorage.getItem(TOKEN_KEY);
};

/**
 * Xóa JWT token khỏi localStorage
 */
export const removeToken = () => {
  localStorage.removeItem(TOKEN_KEY);
};

/**
 * Lưu user data vào localStorage
 * @param {Object} userData - Thông tin user từ backend
 */
export const setUserData = (userData) => {
  if (userData) {
    localStorage.setItem(USER_DATA_KEY, JSON.stringify(userData));
  }
};

/**
 * Lấy user data từ localStorage
 * @returns {Object|null} User data hoặc null nếu không có
 */
export const getUserData = () => {
  const data = localStorage.getItem(USER_DATA_KEY);
  return data ? JSON.parse(data) : null;
};

/**
 * Xóa user data khỏi localStorage
 */
export const removeUserData = () => {
  localStorage.removeItem(USER_DATA_KEY);
};

/**
 * Xóa tất cả auth data (token + user data)
 */
export const clearAuthData = () => {
  removeToken();
  removeUserData();
};

/**
 * Kiểm tra xem user đã đăng nhập chưa (có token không)
 * @returns {boolean}
 */
export const isAuthenticated = () => {
  return !!getToken();
};
