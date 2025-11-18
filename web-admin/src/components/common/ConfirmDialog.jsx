import { motion, AnimatePresence } from 'framer-motion'
import { createPortal } from 'react-dom'
import { AlertTriangle, Trash2, X } from 'lucide-react'
import Button from './Button'

/**
 * ConfirmDialog Component - Reusable confirmation dialog
 * 
 * @param {boolean} isOpen - Control dialog visibility
 * @param {function} onClose - Callback when dialog is closed
 * @param {function} onConfirm - Callback when user confirms action
 * @param {string} title - Dialog title (default: "Xác nhận xóa")
 * @param {string|React.Node} message - Dialog message content
 * @param {string} confirmText - Confirm button text (default: "Xóa")
 * @param {string} cancelText - Cancel button text (default: "Hủy")
 * @param {string} variant - Dialog variant: 'danger' | 'warning' | 'info' (default: 'danger')
 * @param {number} count - Number of items to delete (for plural messages)
 * @param {string} itemName - Name of item(s) being deleted (singular)
 * @param {string} itemNamePlural - Name of items being deleted (plural)
 * @param {boolean} isLoading - Show loading state on confirm button
 */
export default function ConfirmDialog({
  isOpen = false,
  onClose,
  onConfirm,
  title,
  message,
  confirmText = 'Xóa',
  cancelText = 'Hủy',
  variant = 'danger',
  count = 1,
  itemName = 'mục',
  itemNamePlural = 'mục',
  isLoading = false,
}) {
  // Auto-generate title if not provided
  const dialogTitle = title || (count > 1 
    ? `Xác nhận xóa ${count} ${itemNamePlural}` 
    : `Xác nhận xóa ${itemName}`)

  // Auto-generate message if not provided
  const dialogMessage = message || (count > 1
    ? `Bạn có chắc chắn muốn xóa ${count} ${itemNamePlural} đã chọn? Hành động này không thể hoàn tác.`
    : `Bạn có chắc chắn muốn xóa ${itemName} này? Hành động này không thể hoàn tác.`)

  // Variant styles
  const variantStyles = {
    danger: {
      icon: Trash2,
      iconBg: 'bg-red-500/10',
      iconColor: 'text-red-500',
      confirmButton: 'danger',
    },
    warning: {
      icon: AlertTriangle,
      iconBg: 'bg-yellow-500/10',
      iconColor: 'text-yellow-500',
      confirmButton: 'warning',
    },
    info: {
      icon: AlertTriangle,
      iconBg: 'bg-blue-500/10',
      iconColor: 'text-blue-500',
      confirmButton: 'primary',
    },
  }

  const currentVariant = variantStyles[variant] || variantStyles.danger
  const Icon = currentVariant.icon

  const handleConfirm = async () => {
    if (onConfirm) {
      await onConfirm()
    }
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
            onClick={onClose}
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
                  <div className={`p-3 rounded-full ${currentVariant.iconBg} flex-shrink-0`}>
                    <Icon className={`w-6 h-6 ${currentVariant.iconColor}`} />
                  </div>

                  {/* Title */}
                  <div className="flex-1 pt-1">
                    <h3 className="text-lg font-semibold text-text-primary">
                      {dialogTitle}
                    </h3>
                  </div>
                </div>

                {/* Close button */}
                <button
                  onClick={onClose}
                  className="p-1 hover:bg-bg-hover rounded-lg transition-colors flex-shrink-0"
                  disabled={isLoading}
                >
                  <X className="w-5 h-5 text-text-tertiary" />
                </button>
              </div>

              {/* Message */}
              <div className="px-6 pb-6">
                <div className="ml-14">
                  {typeof dialogMessage === 'string' ? (
                    <p className="text-text-secondary leading-relaxed">
                      {dialogMessage}
                    </p>
                  ) : (
                    dialogMessage
                  )}
                </div>
              </div>

              {/* Actions */}
              <div className="flex items-center justify-end gap-3 px-6 py-4 bg-bg-secondary border-t border-border">
                <Button
                  variant="secondary"
                  onClick={onClose}
                  disabled={isLoading}
                >
                  {cancelText}
                </Button>
                <Button
                  variant={currentVariant.confirmButton}
                  onClick={handleConfirm}
                  disabled={isLoading}
                  icon={isLoading ? null : Icon}
                >
                  {isLoading ? 'Đang xử lý...' : confirmText}
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
