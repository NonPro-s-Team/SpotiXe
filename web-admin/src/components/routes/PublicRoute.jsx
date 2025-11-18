import { Navigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { Loader2 } from 'lucide-react';

/**
 * PublicRoute Component
 * Route cho trang công khai (Login)
 * Redirect về dashboard nếu đã đăng nhập
 */
const PublicRoute = ({ children }) => {
  const { user, loading } = useAuth();

  // Show loading screen while checking authentication
  if (loading) {
    return (
      <div className="min-h-screen bg-bg-primary flex items-center justify-center">
        <div className="text-center">
          <Loader2 className="w-12 h-12 animate-spin text-spotify-green mx-auto mb-4" />
          <p className="text-text-secondary font-medium text-lg">Loading...</p>
        </div>
      </div>
    );
  }

  // If user is authenticated, redirect to dashboard
  if (user) {
    return <Navigate to="/dashboard" replace />;
  }

  // User is not authenticated, render login page
  return children;
};

export default PublicRoute;
