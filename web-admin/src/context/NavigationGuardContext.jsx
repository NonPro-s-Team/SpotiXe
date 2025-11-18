import { createContext, useContext, useState, useCallback } from 'react'
import { useNavigate } from 'react-router-dom'

const NavigationGuardContext = createContext()

export function NavigationGuardProvider({ children }) {
  const [isBlocked, setIsBlocked] = useState(false)
  const [pendingNavigation, setPendingNavigation] = useState(null)
  const [onConfirmCallback, setOnConfirmCallback] = useState(null)
  const navigate = useNavigate()

  const blockNavigation = useCallback((onConfirm) => {
    setIsBlocked(true)
    setOnConfirmCallback(() => onConfirm)
  }, [])

  const unblockNavigation = useCallback(() => {
    setIsBlocked(false)
    setOnConfirmCallback(null)
    setPendingNavigation(null)
  }, [])

  const attemptNavigation = useCallback((path) => {
    if (isBlocked) {
      setPendingNavigation(path)
      return false // Navigation was blocked
    } else {
      navigate(path)
      return true // Navigation was allowed
    }
  }, [isBlocked, navigate])

  const confirmNavigation = useCallback(() => {
    if (onConfirmCallback) {
      onConfirmCallback()
    }
    setIsBlocked(false)
    if (pendingNavigation) {
      navigate(pendingNavigation)
    }
    setPendingNavigation(null)
    setOnConfirmCallback(null)
  }, [onConfirmCallback, pendingNavigation, navigate])

  const cancelNavigation = useCallback(() => {
    setPendingNavigation(null)
  }, [])

  const value = {
    isBlocked,
    blockNavigation,
    unblockNavigation,
    attemptNavigation,
    confirmNavigation,
    cancelNavigation,
    hasPendingNavigation: !!pendingNavigation
  }

  return (
    <NavigationGuardContext.Provider value={value}>
      {children}
    </NavigationGuardContext.Provider>
  )
}

export function useNavigationGuard() {
  const context = useContext(NavigationGuardContext)
  if (!context) {
    throw new Error('useNavigationGuard must be used within NavigationGuardProvider')
  }
  return context
}
