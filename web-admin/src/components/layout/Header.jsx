import { useState, useRef } from 'react'
import { Bell, ChevronDown, LogOut, User, Settings as SettingsIcon } from 'lucide-react'
import { useNavigate } from 'react-router-dom'
import { motion, AnimatePresence } from 'framer-motion'
import { useAuth } from '../../context/AuthContext'
import { useClickOutside } from '@hooks/useClickOutside'
import Avatar from '@components/common/Avatar'
import { cn } from '@utils/helpers'

export default function Header() {
  const [showUserMenu, setShowUserMenu] = useState(false)
  const [showNotifications, setShowNotifications] = useState(false)
  const userMenuRef = useRef(null)
  const notificationsRef = useRef(null)
  
  const { user, logout } = useAuth()
  const navigate = useNavigate()

  useClickOutside(userMenuRef, () => setShowUserMenu(false))
  useClickOutside(notificationsRef, () => setShowNotifications(false))

  const notifications = [
    {
      id: 1,
      title: 'New user registered',
      description: 'John Doe just signed up for Premium',
      time: '5 min ago',
      unread: true,
    },
    {
      id: 2,
      title: 'Track uploaded',
      description: 'Artist "The Weeknd" uploaded a new track',
      time: '15 min ago',
      unread: true,
    },
    {
      id: 3,
      title: 'Payment received',
      description: 'Premium subscription payment of $9.99',
      time: '1 hour ago',
      unread: false,
    },
  ]

  return (
    <header className="fixed top-0 right-0 left-0 md:left-[260px] h-16 bg-bg-primary/95 backdrop-blur-xl border-b border-border z-30">
      <div className="h-full px-4 md:px-6 flex items-center justify-between gap-4">
        {/* Search Bar */}
        <div className="flex-1 max-w-xl">
          <div className="relative">
            {/* <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-text-tertiary" /> */}
            {/* <input
              type="text"
              placeholder="Search tracks, artists, users..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              className="w-full pl-10 pr-4 py-2 bg-bg-secondary border border-border rounded-lg text-text-primary placeholder-text-tertiary focus:border-spotify-green focus:outline-none focus:ring-2 focus:ring-spotify-green/20 transition-all"
            /> */}
          </div>
        </div>

        {/* Right Section */}
        <div className="flex items-center gap-2">
          {/* Theme Toggle */}
          {/* <button
            onClick={toggleTheme}
            className="p-2 rounded-lg hover:bg-bg-hover transition-colors"
            aria-label="Toggle theme"
          >
            {theme === 'dark' ? (
              <Sun className="w-5 h-5 text-text-secondary" />
            ) : (
              <Moon className="w-5 h-5 text-text-secondary" />
            )}
          </button> */}

          {/* Notifications */}
          <div className="relative" ref={notificationsRef}>
            <button
              onClick={() => setShowNotifications(!showNotifications)}
              className="relative p-2 rounded-lg hover:bg-bg-hover transition-colors"
              aria-label="Notifications"
            >
              <Bell className="w-5 h-5 text-text-secondary" />
              {notifications.some(n => n.unread) && (
                <span className="absolute top-1 right-1 w-2 h-2 bg-apple-red rounded-full" />
              )}
            </button>

            <AnimatePresence>
              {showNotifications && (
                <motion.div
                  initial={{ opacity: 0, y: 10 }}
                  animate={{ opacity: 1, y: 0 }}
                  exit={{ opacity: 0, y: 10 }}
                  className="absolute right-0 mt-2 w-80 bg-bg-secondary border border-border rounded-xl shadow-xl overflow-hidden"
                >
                  <div className="p-4 border-b border-border">
                    <h3 className="font-semibold text-text-primary">Notifications</h3>
                  </div>
                  <div className="max-h-96 overflow-y-auto">
                    {notifications.map((notification) => (
                      <div
                        key={notification.id}
                        className={cn(
                          'p-4 border-b border-border hover:bg-bg-hover transition-colors cursor-pointer',
                          notification.unread && 'bg-bg-hover/50'
                        )}
                      >
                        <div className="flex items-start gap-3">
                          <div className={cn(
                            'w-2 h-2 rounded-full mt-2',
                            notification.unread ? 'bg-spotify-green' : 'bg-transparent'
                          )} />
                          <div className="flex-1">
                            <h4 className="font-medium text-text-primary text-sm">
                              {notification.title}
                            </h4>
                            <p className="text-xs text-text-secondary mt-1">
                              {notification.description}
                            </p>
                            <p className="text-xs text-text-tertiary mt-1">
                              {notification.time}
                            </p>
                          </div>
                        </div>
                      </div>
                    ))}
                  </div>
                  <div className="p-3 text-center border-t border-border">
                    <button className="text-sm text-spotify-green hover:underline">
                      View all notifications
                    </button>
                  </div>
                </motion.div>
              )}
            </AnimatePresence>
          </div>

          {/* User Menu */}
          <div className="relative" ref={userMenuRef}>
            <button
              onClick={() => setShowUserMenu(!showUserMenu)}
              className="flex items-center gap-2 p-1.5 pr-3 rounded-lg hover:bg-bg-hover transition-colors"
            >
              <Avatar src={user?.photoURL} name={user?.displayName} size="sm" />
              {/* {console.log(user.photoURL)} */}
              
              <ChevronDown className="w-4 h-4 text-text-secondary" />
            </button>

            <AnimatePresence>
              {showUserMenu && (
                <motion.div
                  initial={{ opacity: 0, y: 10 }}
                  animate={{ opacity: 1, y: 0 }}
                  exit={{ opacity: 0, y: 10 }}
                  className="absolute right-0 mt-2 w-56 bg-bg-secondary border border-border rounded-xl shadow-xl overflow-hidden"
                >
                  <div className="p-3 border-b border-border">
                    <p className="font-medium text-text-primary">{user?.displayName || user?.email}</p>
                    <p className="text-xs text-text-secondary">{user?.email}</p>
                  </div>
                  <div className="py-2">
                    <button
                      data-navigation="true"
                      onClick={() => {
                        navigate('/settings')
                        setShowUserMenu(false)
                      }}
                      className="w-full flex items-center gap-3 px-4 py-2 text-sm text-text-primary hover:bg-bg-hover transition-colors"
                    >
                      <User className="w-4 h-4" />
                      Profile
                    </button>
                    <button
                      data-navigation="true"
                      onClick={() => {
                        navigate('/settings')
                        setShowUserMenu(false)
                      }}
                      className="w-full flex items-center gap-3 px-4 py-2 text-sm text-text-primary hover:bg-bg-hover transition-colors"
                    >
                      <SettingsIcon className="w-4 h-4" />
                      Settings
                    </button>
                  </div>
                  <div className="py-2 border-t border-border">
                    <button
                      onClick={async () => {
                        setShowUserMenu(false)
                        const result = await logout()
                        if (result.success) {
                          navigate('/login')
                        }
                      }}
                      className="w-full flex items-center gap-3 px-4 py-2 text-sm text-apple-red hover:bg-bg-hover transition-colors"
                    >
                      <LogOut className="w-4 h-4" />
                        Logout
                    </button>
                  </div>
                </motion.div>
              )}
            </AnimatePresence>
          </div>
        </div>
      </div>

      {/* Breadcrumbs */}
      {/* {breadcrumbs.length > 0 && (
        <div className="px-4 md:px-6 py-2 border-t border-border bg-bg-secondary/50">
          <div className="flex items-center gap-2 text-sm">
            {breadcrumbs.map((crumb, index) => (
              <div key={crumb.path} className="flex items-center gap-2">
                {index > 0 && <span className="text-text-tertiary">/</span>}
                <button
                  onClick={() => navigate(crumb.path)}
                  className={cn(
                    'hover:text-spotify-green transition-colors',
                    index === breadcrumbs.length - 1
                      ? 'text-text-primary font-medium'
                      : 'text-text-secondary'
                  )}
                >
                  {crumb.name}
                </button>
              </div>
            ))}
          </div>
        </div>
      )} */}
    </header>
  )
}
