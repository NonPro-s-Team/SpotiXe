import { createContext, useContext, useState, useEffect } from 'react';
import { signInWithPopup, signInWithEmailAndPassword, signOut, onAuthStateChanged } from 'firebase/auth';
import { auth, googleProvider, analytics } from '../services/firebase';
import { logEvent } from 'firebase/analytics';
import { loginWithBackend, logoutFromBackend, validateToken } from '../services/authService';
import { getUserData, clearAuthData, getToken } from '../utils/tokenStorage';

// Create Auth Context
const AuthContext = createContext();

// Get allowed domain từ environment variable
const ALLOWED_DOMAIN = import.meta.env.VITE_ALLOWED_DOMAIN;

/**
 * Custom hook để sử dụng Auth Context
 * @throws {Error} Nếu sử dụng ngoài AuthProvider
 */
export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};

/**
 * AuthProvider Component
 * Quản lý authentication state và domain verification
 */
export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);
  const [domainAuthorized, setDomainAuthorized] = useState(false);
  const [backendUser, setBackendUser] = useState(null);

  /**
   * Subscribe to auth state changes
   * Tự động kiểm tra domain mỗi khi auth state thay đổi
   */
  useEffect(() => {
    const unsubscribe = onAuthStateChanged(auth, async (currentUser) => {
      if (currentUser) {
        // Kiểm tra email domain
        const isAuthorized = currentUser.email && 
                            currentUser.email.endsWith(ALLOWED_DOMAIN);
        
        if (isAuthorized) {
          // ✅ User được phép - Set user data
          const userData = {
            uid: currentUser.uid,
            email: currentUser.email,
            displayName: currentUser.displayName,
            photoURL: currentUser.photoURL,
          };
          
          setUser(userData);
          setDomainAuthorized(true);

          // Lấy backend user data từ localStorage nếu có
          const storedUser = getUserData();
          if (storedUser) {
            setBackendUser(storedUser);
          }

          // ✅ Kiểm tra JWT token còn hạn không
          const jwtToken = getToken();
          if (jwtToken) {
            console.log('🔍 Checking JWT token validity...');
            const validation = await validateToken();
            
            if (!validation.valid) {
              console.warn('⚠️ JWT token invalid or expired, logging out...');
              // Token hết hạn hoặc không hợp lệ - Logout
              await signOut(auth);
              setUser(null);
              setDomainAuthorized(false);
              setBackendUser(null);
              clearAuthData();
              setLoading(false);
              return;
            }
            
            console.log('✅ JWT token is valid');
            // Cập nhật backend user nếu có data mới từ /auth/me
            if (validation.user) {
              setBackendUser(validation.user);
            }
          }
        } else {
          // ❌ User KHÔNG được phép - Sign out ngay lập tức
          await signOut(auth);
          setUser(null);
          setDomainAuthorized(false);
          setBackendUser(null);
          clearAuthData();
          console.warn('Unauthorized domain access attempt:', currentUser.email);
        }
      } else {
        // User signed out
        setUser(null);
        setDomainAuthorized(false);
        setBackendUser(null);
        clearAuthData();
      }
      setLoading(false);
    });

    // Cleanup subscription
    return () => unsubscribe();
  }, []);

  /**
   * Kiểm tra token định kỳ mỗi 5 phút
   * Tự động logout nếu token hết hạn
   */
  useEffect(() => {
    if (!user || !domainAuthorized) return;

    const checkTokenPeriodically = async () => {
      const jwtToken = getToken();
      if (!jwtToken) {
        console.warn('⚠️ No JWT token found, logging out...');
        await logout();
        return;
      }

      const validation = await validateToken();
      if (!validation.valid) {
        console.warn('⚠️ JWT token expired during session, logging out...');
        await logout();
      }
    };

    // Kiểm tra ngay lập tức
    checkTokenPeriodically();

    // Kiểm tra mỗi 5 phút
    const interval = setInterval(checkTokenPeriodically, 5 * 60 * 1000);

    return () => clearInterval(interval);
  }, [user, domainAuthorized]);

  /**
   * Sign in với Google
   * @returns {Object} { success, user?, error?, unauthorized? }
   */
  const signInWithGoogle = async () => {
    try {
      // Mở Google Sign-in popup
      const result = await signInWithPopup(auth, googleProvider);
      const signedInUser = result.user;
      
      // Kiểm tra domain ngay sau khi sign in
      if (!signedInUser.email || !signedInUser.email.endsWith(ALLOWED_DOMAIN)) {
        // Domain không hợp lệ - Sign out ngay
        await signOut(auth);
        return { 
          success: false, 
          error: `Access restricted to ${ALLOWED_DOMAIN} accounts only.`,
          unauthorized: true 
        };
      }

      // ✅ Đăng nhập với Backend bằng Firebase token
      const firebaseToken = await signedInUser.getIdToken();
      const backendResult = await loginWithBackend(firebaseToken);

      if (!backendResult.success) {
        // Backend authentication failed - Sign out from Firebase
        await signOut(auth);
        return {
          success: false,
          error: backendResult.error || 'Backend authentication failed'
        };
      }

      // Lưu backend user data
      setBackendUser(backendResult.user);
      
      // Log analytics event (optional)
      try {
        if (analytics) {
          logEvent(analytics, 'login', {
            method: 'google',
            domain: ALLOWED_DOMAIN
          });
        }
      } catch (analyticsError) {
        // Analytics error không ảnh hưởng đến login flow
        console.warn('Analytics error:', analyticsError);
      }
      
      return { success: true, user: signedInUser, backendUser: backendResult.user };
    } catch (error) {
      // Xử lý các Firebase errors
      let errorMessage = error.message;
      
      if (error.code === 'auth/popup-closed-by-user') {
        errorMessage = 'Sign-in cancelled. Please try again.';
      } else if (error.code === 'auth/popup-blocked') {
        errorMessage = 'Popup was blocked. Please allow popups for this site.';
      } else if (error.code === 'auth/unauthorized-domain') {
        errorMessage = 'This domain is not authorized. Contact administrator.';
      }
      
      return { success: false, error: errorMessage };
    }
  };

  /**
   * Sign in với Email và Password
   * @param {string} email 
   * @param {string} password 
   * @returns {Object} { success, user?, error?, unauthorized? }
   */
  const signInWithEmail = async (email, password) => {
    try {
      // Kiểm tra domain trước khi sign in
      if (!email || !email.endsWith(ALLOWED_DOMAIN)) {
        return { 
          success: false, 
          error: `Access restricted to ${ALLOWED_DOMAIN} accounts only.`,
          unauthorized: true 
        };
      }

      // Sign in với email và password
      const result = await signInWithEmailAndPassword(auth, email, password);
      const signedInUser = result.user;

      // ✅ Đăng nhập với Backend bằng Firebase token
      const firebaseToken = await signedInUser.getIdToken();
      const backendResult = await loginWithBackend(firebaseToken);

      if (!backendResult.success) {
        // Backend authentication failed - Sign out from Firebase
        await signOut(auth);
        return {
          success: false,
          error: backendResult.error || 'Backend authentication failed'
        };
      }

      // Lưu backend user data
      setBackendUser(backendResult.user);
      
      // Log analytics event (optional)
      try {
        if (analytics) {
          logEvent(analytics, 'login', {
            method: 'email',
            domain: ALLOWED_DOMAIN
          });
        }
      } catch (analyticsError) {
        console.warn('Analytics error:', analyticsError);
      }
      
      return { success: true, user: signedInUser, backendUser: backendResult.user };
    } catch (error) {
      // Xử lý các Firebase errors
      let errorMessage = error.message;
      
      if (error.code === 'auth/invalid-email') {
        errorMessage = 'Invalid email address.';
      } else if (error.code === 'auth/user-disabled') {
        errorMessage = 'This account has been disabled.';
      } else if (error.code === 'auth/user-not-found') {
        errorMessage = 'No account found with this email.';
      } else if (error.code === 'auth/wrong-password') {
        errorMessage = 'Incorrect password.';
      } else if (error.code === 'auth/invalid-credential') {
        errorMessage = 'Invalid email or password.';
      }
      
      return { success: false, error: errorMessage };
    }
  };

  /**
   * Logout user
   * @returns {Object} { success, error? }
   */
  const logout = async () => {
    try {
      // Clear backend auth data
      logoutFromBackend();
      
      // Sign out from Firebase
      await signOut(auth);
      
      // Clear local state
      setBackendUser(null);
      
      return { success: true };
    } catch (error) {
      return { success: false, error: error.message };
    }
  };

  // Context value
  const value = {
    user,                  // Firebase user object or null
    backendUser,          // Backend user object or null
    loading,              // Loading state
    domainAuthorized,     // Domain verification flag
    signInWithGoogle,     // Sign in with Google function
    signInWithEmail,      // Sign in with Email function
    logout,               // Logout function
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};
