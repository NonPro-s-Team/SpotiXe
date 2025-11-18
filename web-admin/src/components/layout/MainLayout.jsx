import { Outlet } from 'react-router-dom'
import { motion } from 'framer-motion'
import Sidebar from './Sidebar'
import Header from './Header'
import { useSidebarStore } from '@stores/sidebarStore'

export default function MainLayout() {
  const { isCollapsed } = useSidebarStore()

  return (
    <div className="min-h-screen bg-bg-primary">
      <Sidebar />
      <Header />
      
      <motion.main
        animate={{
          marginLeft: isCollapsed ? '80px' : '260px',
        }}
        transition={{ duration: 0.3, ease: 'easeInOut' }}
        className="pt-28 pb-20 md:pb-6 px-4 md:px-6 min-h-screen"
      >
        <div className="max-w-[1920px] mx-auto">
          <Outlet />
        </div>
      </motion.main>
    </div>
  )
}
