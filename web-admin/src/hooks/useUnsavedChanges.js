import { useEffect } from "react";

/**
 * Hook to prevent browser back button navigation
 */
export function useNavigationBlocker(shouldBlock, onConfirmLeave = null) {
  useEffect(() => {
    if (!shouldBlock) return;

    const handlePopState = () => {
      const shouldLeave = window.confirm(
        "Bạn đang có tiến trình xử lý chưa hoàn tất. Rời khỏi trang sẽ hủy tiến trình này. Bạn có chắc chắn muốn rời đi?"
      );

      if (!shouldLeave) {
        // Push state back to prevent navigation
        window.history.pushState(null, "", window.location.href);
      } else if (onConfirmLeave) {
        onConfirmLeave(); // Execute cleanup
      }
    };

    // Push a dummy state to history
    window.history.pushState(null, "", window.location.href);
    window.addEventListener("popstate", handlePopState);

    return () => {
      window.removeEventListener("popstate", handlePopState);
    };
  }, [shouldBlock, onConfirmLeave]);
}

/**
 * Custom hook for handling unsaved changes with dialog
 * Combines beforeunload and navigation blocking
 *
 * @param {boolean} hasUnsavedChanges - Whether there are unsaved changes/ongoing processes
 * @param {function} onConfirmLeave - Callback when user confirms leaving (optional cleanup)
 */
export function useUnsavedChangesWarning(
  hasUnsavedChanges,
  onConfirmLeave = null
) {
  // Prevent browser navigation (refresh, close, back)
  useEffect(() => {
    if (!hasUnsavedChanges) return;

    const handleBeforeUnload = (e) => {
      e.preventDefault();
      e.returnValue =
        "Bạn có thay đổi chưa được lưu. Bạn có chắc chắn muốn rời đi?";
      return e.returnValue;
    };

    window.addEventListener("beforeunload", handleBeforeUnload);

    return () => {
      window.removeEventListener("beforeunload", handleBeforeUnload);
    };
  }, [hasUnsavedChanges]);

  // Prevent browser back button
  useNavigationBlocker(hasUnsavedChanges, onConfirmLeave);
}
