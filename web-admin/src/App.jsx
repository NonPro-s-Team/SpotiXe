import { Routes, Route, Navigate } from 'react-router-dom'
import { AuthProvider } from './context/AuthContext'
import MainLayout from '@components/layout/MainLayout'
import PrivateRoute from '@components/routes/PrivateRoute'
import PublicRoute from '@components/routes/PublicRoute'

// Eager loading - Import all pages directly for instant navigation
import Dashboard from '@pages/Dashboard'
import MusicManagement from '@pages/MusicManagement'
import ArtistsManagement from '@pages/ArtistsManagement'
import AlbumsManagement from '@pages/AlbumsManagement'
import UsersManagement from '@pages/UsersManagement'
import Analytics from '@pages/Analytics'
import Playlists from '@pages/Playlists'
import PremiumPlans from '@pages/PremiumPlans'
import Revenue from '@pages/Revenue'
import Reports from '@pages/Reports'
import Settings from '@pages/Settings'
import LoginPage from '@pages/LoginPage'
import NotFound from '@pages/NotFound'

// Song management pages
import AddSong from '@pages/songs/AddSong'
import EditSong from '@pages/songs/EditSong'
import ViewSong from '@pages/songs/ViewSong'
import BulkImportSongs from '@pages/songs/BulkImportSongs'

// Artist management pages
import AddArtist from '@pages/artists/AddArtist'
import EditArtist from '@pages/artists/EditArtist'
import ViewArtist from '@pages/artists/ViewArtist'

// Album management pages
import AddAlbum from '@pages/albums/AddAlbum'
import EditAlbum from '@pages/albums/EditAlbum'
import ViewAlbum from '@pages/albums/ViewAlbum'

function App() {
  return (
    <AuthProvider>
      <Routes>
        {/* Public Route - Login */}
        <Route
          path="/login"
          element={
            <PublicRoute>
              <LoginPage />
            </PublicRoute>
          }
        />
        
        {/* Protected Routes */}
        <Route
          path="/"
          element={
            <PrivateRoute>
              <MainLayout />
            </PrivateRoute>
          }
        >
          <Route index element={<Navigate to="/dashboard" replace />} />
          <Route path="dashboard" element={<Dashboard />} />
          <Route path="songs" element={<MusicManagement />} />
          
          {/* Song routes */}
          <Route path="songs/add" element={<AddSong />} />
          <Route path="songs/bulk-import" element={<BulkImportSongs />} />
          <Route path="songs/:id" element={<ViewSong />} />
          <Route path="songs/:id/edit" element={<EditSong />} />
          
          {/* Artist routes */}
          <Route path="artists" element={<ArtistsManagement />} />
          <Route path="artists/add" element={<AddArtist />} />
          <Route path="artists/:id" element={<ViewArtist />} />
          <Route path="artists/:id/edit" element={<EditArtist />} />
          
          {/* Album routes */}
          <Route path="albums" element={<AlbumsManagement />} />
          <Route path="albums/add" element={<AddAlbum />} />
          <Route path="albums/:id" element={<ViewAlbum />} />
          <Route path="albums/:id/edit" element={<EditAlbum />} />
          
          <Route path="users" element={<UsersManagement />} />
          <Route path="analytics" element={<Analytics />} />
          <Route path="playlists" element={<Playlists />} />
          <Route path="premium" element={<PremiumPlans />} />
          <Route path="revenue" element={<Revenue />} />
          <Route path="reports" element={<Reports />} />
          <Route path="settings" element={<Settings />} />
        </Route>

        {/* 404 Not Found */}
        <Route path="*" element={<NotFound />} />
      </Routes>
    </AuthProvider>
  )
}

export default App
