import api from "../api";

/**
 * Fetch all regular users (Email + OTP)
 * @returns {Promise<Array>} List of regular users
 */
export const getRegularUsers = async () => {
  try {
    const response = await api.get("/users");
    return response.data;
  } catch (error) {
    console.error("Error fetching regular users:", error);
    throw error;
  }
};

/**
 * Fetch all Firebase users (Google login)
 * @returns {Promise<Array>} List of Firebase users
 */
export const getFirebaseUsers = async () => {
  try {
    const response = await api.get("/admin/firebase");
    return response.data;
  } catch (error) {
    console.error("Error fetching Firebase users:", error);
    throw error;
  }
};

/**
 * Disable a Firebase user account
 * @param {string} firebaseUid - Firebase UID
 * @returns {Promise<Object>} Response data
 */
export const disableFirebaseUser = async (firebaseUid) => {
  try {
    const response = await api.post(`/admin/firebase/disable/${firebaseUid}`);
    return response.data;
  } catch (error) {
    console.error("Error disabling Firebase user:", error);
    throw error;
  }
};

/**
 * Enable a Firebase user account
 * @param {string} firebaseUid - Firebase UID
 * @returns {Promise<Object>} Response data
 */
export const enableFirebaseUser = async (firebaseUid) => {
  try {
    const response = await api.post(`/admin/firebase/enable/${firebaseUid}`);
    return response.data;
  } catch (error) {
    console.error("Error enabling Firebase user:", error);
    throw error;
  }
};

/**
 * Disable a regular user account (Email + OTP)
 * @param {string|number} userId - User ID
 * @returns {Promise<Object>} Response data
 */
export const disableRegularUser = async (userId) => {
  try {
    const response = await api.put(`/api/users/${userId}/disable`);
    return response.data;
  } catch (error) {
    console.error("Error disabling regular user:", error);
    throw error;
  }
};

/**
 * Enable a regular user account (Email + OTP)
 * @param {string|number} userId - User ID
 * @returns {Promise<Object>} Response data
 */
export const enableRegularUser = async (userId) => {
  try {
    const response = await api.put(`/api/users/${userId}/enable`);
    return response.data;
  } catch (error) {
    console.error("Error enabling regular user:", error);
    throw error;
  }
};

/**
 * Merge and normalize user data from both sources
 * @param {Array} regularUsers - Users from /api/users
 * @param {Array} firebaseUsers - Users from /admin/firebase
 * @returns {Array} Merged and normalized user list
 */
export const mergeUserData = (regularUsers = [], firebaseUsers = []) => {
  const merged = [];

  // Add regular users (Email + OTP)
  regularUsers.forEach((user) => {
    merged.push({
      id: user.userId || user.id,
      username: user.username || user.name || "N/A",
      email: user.email,
      isActive: user.isActive === 1 || user.isActive === true,
      type: "email",
      logo: "/EmailWithOtp.svg",
      originalData: user,
    });
  });

  // Add Firebase users (Google login)
  firebaseUsers.forEach((user) => {
    merged.push({
      id: user.uid || user.firebaseUid || user.id,
      username:
        user.displayName || user.name || user.email?.split("@")[0] || "N/A",
      email: user.email,
      isActive: !user.disabled && user.disabled !== true,
      type: "firebase",
      logo: "/Firebase.svg",
      firebaseUid: user.uid || user.firebaseUid,
      originalData: user,
    });
  });

  return merged;
};
