import { useState, useEffect, useRef } from 'react'
import { useLocation, useNavigate } from 'react-router-dom'
import { motion, AnimatePresence } from 'framer-motion'
import { createPortal } from 'react-dom'
import { AlertTriangle, X } from 'lucide-react'
import Button from './Button'

/**
 * LeavePageDialog Component - Dialog to confirm leaving page during processing
 * 
 * @param {boolean} isProcessing - Whether there is an ongoing process
 * @param {function} onConfirmLeave - Callback when user confirms leaving
 */
export default function LeavePageDialog({ isProcessing, onConfirmLeave }) {
  const [isOpen, setIsOpen] = useState(false)
  const [pendingNavigation, setPendingNavigation] = useState(null)
  const location = useLocation()
  const navigate = useNavigate()
  const currentPathRef = useRef(location.pathname)

  // Update current path ref
  useEffect(() => {
    if (!isProcessing) {
      currentPathRef.current = location.pathname
    }
  }, [location.pathname, isProcessing])

  // Intercept navigation clicks during processing
  useEffect(() => {
    if (!isProcessing) return

    const handleClick = (e) => {
      // Check if click is on a link or inside a link
      const link = e.target.closest('a[href]')
      if (link) {
        const href = link.getAttribute('href')
        
        // Only intercept internal navigation (starts with /)
        if (href && href.startsWith('/')) {
          // Prevent default navigation
          e.preventDefault()
          e.stopPropagation()
          
          // Show dialog
          setPendingNavigation({ type: 'link', path: href })
          setIsOpen(true)
          return
        }
      }

      // Check if click is on a navigation button (marked with data-navigation="true")
      const navButton = e.target.closest('[data-navigation="true"]')
      if (navButton) {
        // Prevent the click from bubbling to React event handler
        e.preventDefault()
        e.stopPropagation()
        
        // Show confirmation dialog
        // Since we can't extract the path from React onClick handler,
        // we'll navigate to /songs as fallback after confirmation
        setPendingNavigation({ type: 'button', path: null })
        setIsOpen(true)
      }
    }

    // Use capture phase to intercept before React event handlers
    document.addEventListener('click', handleClick, true)

    return () => {
      document.removeEventListener('click', handleClick, true)
    }
  }, [isProcessing])

  // Handle browser navigation (back, forward, refresh, close)
  useEffect(() => {
    if (!isProcessing) return

    const handleBeforeUnload = (e) => {
      e.preventDefault()
      e.returnValue = '' // Required for Chrome
      return '' // Required for other browsers
    }

    window.addEventListener('beforeunload', handleBeforeUnload)

    return () => {
      window.removeEventListener('beforeunload', handleBeforeUnload)
    }
  }, [isProcessing])

  // Handle browser back button
  useEffect(() => {
    if (!isProcessing) return

    const handlePopState = () => {
      // Prevent navigation
      window.history.pushState(null, '', window.location.href)
      
      // Show dialog
      setPendingNavigation('back')
      setIsOpen(true)
    }

    // Push a dummy state to history
    window.history.pushState(null, '', window.location.href)
    window.addEventListener('popstate', handlePopState)

    return () => {
      window.removeEventListener('popstate', handlePopState)
    }
  }, [isProcessing])

  const handleCancel = () => {
    setIsOpen(false)
    setPendingNavigation(null)
  }

  const handleConfirm = () => {
    setIsOpen(false)
    
    // Execute cleanup callback
    if (onConfirmLeave) {
      onConfirmLeave()
    }

    // Handle different navigation types
    if (pendingNavigation) {
      if (pendingNavigation === 'back') {
        // Browser back button
        window.removeEventListener('popstate', () => {})
        window.history.back()
      } else if (pendingNavigation.path) {
        // Link or button click with known path
        setTimeout(() => {
          navigate(pendingNavigation.path)
        }, 100)
      } else {
        // Button click without extractable path - just leave the page
        // User wanted to navigate away, so go to songs list as fallback
        setTimeout(() => {
          navigate('/songs')
        }, 100)
      }
    }
    
    setPendingNavigation(null)
  }

  // Render dialog content
  const dialogContent = (
    <AnimatePresence>
      {isOpen && (
        <>
          {/* Backdrop */}
          <motion.div
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            exit={{ opacity: 0 }}
            transition={{ duration: 0.2 }}
            className="fixed inset-0 bg-black/60 backdrop-blur-sm z-[9998]"
            onClick={handleCancel}
          />

          {/* Dialog */}
          <div className="fixed inset-0 z-[9999] flex items-center justify-center p-4 pointer-events-none">
            <motion.div
              initial={{ opacity: 0, scale: 0.95, y: 20 }}
              animate={{ opacity: 1, scale: 1, y: 0 }}
              exit={{ opacity: 0, scale: 0.95, y: 20 }}
              transition={{ duration: 0.2, type: 'spring', damping: 25, stiffness: 300 }}
              className="bg-bg-card border border-border rounded-xl shadow-2xl max-w-md w-full overflow-hidden pointer-events-auto"
              onClick={(e) => e.stopPropagation()}
            >
              {/* Header */}
              <div className="flex items-start justify-between p-6 pb-4">
                <div className="flex items-start gap-4">
                  {/* Icon */}
                  <div className="p-3 rounded-full bg-yellow-500/10 flex-shrink-0">
                    <AlertTriangle className="w-6 h-6 text-yellow-500" />
                  </div>

                  {/* Title */}
                  <div className="flex-1 pt-1">
                    <h3 className="text-lg font-semibold text-text-primary">
                      Rời khỏi trang?
                    </h3>
                  </div>
                </div>

                {/* Close button */}
                <button
                  onClick={handleCancel}
                  className="p-1 hover:bg-bg-hover rounded-lg transition-colors flex-shrink-0"
                >
                  <X className="w-5 h-5 text-text-tertiary" />
                </button>
              </div>

              {/* Message */}
              <div className="px-6 pb-6">
                <div className="ml-14">
                  <p className="text-text-secondary leading-relaxed mb-3">
                    Bạn đang có tiến trình xử lý chưa hoàn tất. 
                  </p>
                  <p className="text-text-secondary leading-relaxed">
                    Rời khỏi trang sẽ <strong className="text-yellow-500">hủy bỏ tiến trình này</strong> và 
                    các bài hát chưa được xử lý sẽ không được thêm vào hệ thống.
                  </p>
                  <p className="text-text-primary font-medium mt-3">
                    Bạn có chắc chắn muốn rời đi?
                  </p>
                </div>
              </div>

              {/* Actions */}
              <div className="flex items-center justify-end gap-3 px-6 py-4 bg-bg-secondary border-t border-border">
                <Button
                  variant="secondary"
                  onClick={handleCancel}
                >
                  Tiếp tục xử lý
                </Button>
                <Button
                  variant="warning"
                  onClick={handleConfirm}
                  icon={AlertTriangle}
                >
                  Rời khỏi trang
                </Button>
              </div>
            </motion.div>
          </div>
        </>
      )}
    </AnimatePresence>
  )

  // Use React Portal to render outside the current DOM hierarchy
  return createPortal(dialogContent, document.body)
}
