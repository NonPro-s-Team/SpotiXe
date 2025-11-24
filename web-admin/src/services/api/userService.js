import api from "../api";

/**
 * Fetch all regular users (Email + OTP)
 * Include all users regardless of isActive status (0 or 1)
 * @returns {Promise<Array>} List of regular users
 */
export const getRegularUsers = async () => {
  try {
    // Fetch all users including inactive ones (isActive = 0)
    const response = await api.get("/users?includeDeleted=true");
    // Handle different response structures
    const data = response.data;
    if (Array.isArray(data)) return data;
    if (data?.data && Array.isArray(data.data)) return data.data;
    if (data?.users && Array.isArray(data.users)) return data.users;
    console.warn("Regular users response is not an array:", data);
    return [];
  } catch (error) {
    console.error("Error fetching regular users:", error);
    return []; // Return empty array instead of throwing
  }
};

/**
 * Fetch all Firebase users (Google login)
 * @returns {Promise<Array>} List of Firebase users
 */
export const getFirebaseUsers = async () => {
  try {
    const response = await api.get("/admin/firebase");
    // Handle different response structures
    const data = response.data;
    if (Array.isArray(data)) return data;
    if (data?.data && Array.isArray(data.data)) return data.data;
    if (data?.users && Array.isArray(data.users)) return data.users;
    console.warn("Firebase users response is not an array:", data);
    return [];
  } catch (error) {
    console.error("Error fetching Firebase users:", error);
    return []; // Return empty array instead of throwing
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
    const response = await api.put(`/users/${userId}/disable`);
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
    const response = await api.put(`/users/${userId}/enable`);
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

  // Ensure inputs are arrays
  const safeRegularUsers = Array.isArray(regularUsers) ? regularUsers : [];
  const safeFirebaseUsers = Array.isArray(firebaseUsers) ? firebaseUsers : [];

  // Add regular users (Email + OTP)
  safeRegularUsers.forEach((user) => {
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
  safeFirebaseUsers.forEach((user) => {
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
