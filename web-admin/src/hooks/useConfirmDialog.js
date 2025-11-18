import { useState } from "react";

/**
 * Hook to manage ConfirmDialog state
 *
 * Usage:
 * const { isOpen, data, openDialog, closeDialog, confirmDialog } = useConfirmDialog()
 *
 * // Open dialog
 * openDialog({ id: 123, name: 'Song Title' })
 *
 * // In your component
 * <ConfirmDialog
 *   isOpen={isOpen}
 *   onClose={closeDialog}
 *   onConfirm={confirmDialog(handleDelete)}
 *   message={`Bạn có chắc muốn xóa "${data?.name}"?`}
 * />
 */
export function useConfirmDialog() {
  const [isOpen, setIsOpen] = useState(false);
  const [data, setData] = useState(null);

  const openDialog = (itemData = null) => {
    setData(itemData);
    setIsOpen(true);
  };

  const closeDialog = () => {
    setIsOpen(false);
    // Clear data after animation completes
    setTimeout(() => setData(null), 300);
  };

  const confirmDialog = (callback) => async () => {
    if (callback && data) {
      await callback(data);
    }
    closeDialog();
  };

  return {
    isOpen,
    data,
    openDialog,
    closeDialog,
    confirmDialog,
  };
}
