import { motion } from 'framer-motion'
import { NavLink } from 'react-router-dom'
import { useState, useEffect } from 'react'
import {
  Home,
  Music,
  Mic2,
  Users,
  BarChart3,
  ListMusic,
  Crown,
  DollarSign,
  FileText,
  Settings,
  ChevronLeft,
  Clock,
} from 'lucide-react'
import { useSidebarStore } from '@stores/sidebarStore'
import { cn } from '@utils/helpers'

const navigationItems = [
  { name: 'Trang chủ', path: '/dashboard', icon: Home },
  { name: 'Quản lý bài hát', path: '/songs', icon: Music },
  { name: 'Nghệ sĩ', path: '/artists', icon: Mic2 },
  { name: 'Albums', path: '/albums', icon: BarChart3 },
  { name: 'Người dùng', path: '/users', icon: Users },
  { name: 'Danh sách phát', path: '/playlists', icon: ListMusic },
  { name: 'Gói Premium', path: '/premium', icon: Crown },
  { name: 'Doanh thu', path: '/revenue', icon: DollarSign },
  { name: 'Báo cáo', path: '/reports', icon: FileText },
  { name: 'Cài đặt', path: '/settings', icon: Settings },
]

export default function Sidebar() {
  const { isCollapsed } = useSidebarStore()
  const [currentTime, setCurrentTime] = useState(new Date())

  useEffect(() => {
    const timer = setInterval(() => {
      setCurrentTime(new Date())
    }, 1000)

    return () => clearInterval(timer)
  }, [])

  return (
    <>
      {/* Desktop Sidebar */}
      <motion.aside
        initial={false}
        animate={{
          width: isCollapsed ? '80px' : '260px',
        }}
        transition={{ duration: 0.3, ease: 'easeInOut' }}
        className="hidden md:flex flex-col fixed left-0 top-0 h-screen bg-bg-primary border-r border-border z-40"
      >
        {/* Logo */}
        <div className="h-16 flex items-center justify-between px-4 border-b border-border">
          <motion.div
            animate={{
              opacity: isCollapsed ? 0 : 1,
              scale: isCollapsed ? 0.8 : 1,
            }}
            transition={{ duration: 0.2 }}
            className="flex items-center gap-2"
          >
            <img 
              src="/logo.svg" 
              alt="SpotiXe Logo" 
              className="w-8 h-8 rounded-lg"
            />
            <span className="text-xl font-bold text-gradient">SpotiXe</span>
          </motion.div>
          
          {/* <button
            onClick={toggleSidebar}
            className="p-2 rounded-lg hover:bg-bg-hover transition-colors"
          >
            <motion.div
              animate={{ rotate: isCollapsed ? 180 : 0 }}
              transition={{ duration: 0.3 }}
            >
              <ChevronLeft className="w-5 h-5 text-text-secondary" />
            </motion.div>
          </button> */}
        </div>

        {/* Navigation */}
        <nav className="flex-1 overflow-y-auto py-4 px-2">
          <div className="space-y-1">
            {navigationItems.map((item) => (
              <NavLink
                key={item.path}
                to={item.path}
                className={({ isActive }) =>
                  cn(
                    'flex items-center gap-3 px-3 py-2.5 rounded-lg transition-all duration-200 group relative',
                    isActive
                      ? 'bg-bg-hover text-text-primary'
                      : 'text-text-secondary hover:text-text-primary hover:bg-bg-hover'
                  )
                }
              >
                {({ isActive }) => (
                  <>
                    <item.icon
                      className={cn(
                        'w-5 h-5 flex-shrink-0',
                        isActive && 'text-text-primary'
                      )}
                    />
                    <motion.span
                      animate={{
                        opacity: isCollapsed ? 0 : 1,
                        width: isCollapsed ? 0 : 'auto',
                      }}
                      className="font-medium whitespace-nowrap overflow-hidden"
                    >
                      {item.name}
                    </motion.span>
                    
                    {/* Tooltip for collapsed state */}
                    {isCollapsed && (
                      <div className="absolute left-full ml-2 px-3 py-2 bg-bg-secondary border border-border rounded-lg whitespace-nowrap opacity-0 group-hover:opacity-100 pointer-events-none transition-opacity z-50">
                        {item.name}
                      </div>
                    )}
                  </>
                )}
              </NavLink>
            ))}
          </div>
        </nav>

        {/* Date Time Section */}
        <div className="p-4 border-t border-border">
          <motion.div
            animate={{
              justifyContent: isCollapsed ? 'center' : 'flex-start',
            }}
            className="flex items-center gap-3"
          >
            <div className="w-10 h-10 rounded-full bg-green-500/20 flex items-center justify-center">
              <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" className="text-green-500">
                <circle cx="12" cy="12" r="10"/>
                <polyline points="12 6 12 12 16 14"/>
              </svg>
            </div>
            <motion.div
              animate={{
                opacity: isCollapsed ? 0 : 1,
                width: isCollapsed ? 0 : 'auto',
              }}
              className="overflow-hidden"
            >
              <p className="text-sm font-medium text-text-primary">
                {currentTime.toLocaleTimeString('vi-VN', { hour: '2-digit', minute: '2-digit', second: '2-digit' })}
              </p>
              <p className="text-xs text-text-tertiary">
                {currentTime.toLocaleDateString('vi-VN', { weekday: 'short', day: '2-digit', month: '2-digit', year: 'numeric' })}
              </p>
            </motion.div>
          </motion.div>
        </div>
      </motion.aside>

      {/* Mobile Bottom Navigation */}
      <div className="md:hidden fixed bottom-0 left-0 right-0 bg-bg-primary border-t border-border z-40">
        <nav className="flex items-center justify-around py-2">
          {navigationItems.slice(0, 5).map((item) => (
            <NavLink
              key={item.path}
              to={item.path}
              className={({ isActive }) =>
                cn(
                  'flex flex-col items-center gap-1 px-3 py-2 rounded-lg transition-all',
                  isActive
                    ? 'text-spotify-green'
                    : 'text-text-tertiary'
                )
              }
            >
              <item.icon className="w-6 h-6" />
              <span className="text-xs">{item.name.split(' ')[0]}</span>
            </NavLink>
          ))}
        </nav>
      </div>
    </>
  )
}
