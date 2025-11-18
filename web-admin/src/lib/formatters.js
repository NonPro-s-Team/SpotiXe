/**
 * Format duration from seconds to MM:SS format
 * @param {number} seconds - Duration in seconds
 * @returns {string} Formatted duration (e.g., "3:45")
 */
export const formatDuration = (seconds) => {
  if (!seconds || seconds < 0) return "0:00";

  const minutes = Math.floor(seconds / 60);
  const remainingSeconds = seconds % 60;

  return `${minutes}:${remainingSeconds.toString().padStart(2, "0")}`;
};

/**
 * Format date to "MMM DD, YYYY" format
 * @param {string|Date} dateString - Date string or Date object
 * @returns {string} Formatted date (e.g., "Nov 06, 2024")
 */
export const formatDate = (dateString) => {
  if (!dateString) return "N/A";

  const date = new Date(dateString);

  if (isNaN(date.getTime())) return "Invalid Date";

  const options = { year: "numeric", month: "short", day: "2-digit" };
  return date.toLocaleDateString("en-US", options);
};

/**
 * Format file size to human-readable format
 * @param {number} bytes - File size in bytes
 * @returns {string} Formatted size (e.g., "2.5 MB")
 */
export const formatFileSize = (bytes) => {
  if (!bytes || bytes === 0) return "0 Bytes";

  const k = 1024;
  const sizes = ["Bytes", "KB", "MB", "GB"];
  const i = Math.floor(Math.log(bytes) / Math.log(k));

  return `${parseFloat((bytes / Math.pow(k, i)).toFixed(2))} ${sizes[i]}`;
};

/**
 * Truncate text with ellipsis
 * @param {string} text - Text to truncate
 * @param {number} maxLength - Maximum length before truncating
 * @returns {string} Truncated text
 */
export const truncateText = (text, maxLength = 50) => {
  if (!text) return "";
  if (text.length <= maxLength) return text;

  return `${text.substring(0, maxLength)}...`;
};

/**
 * Get status color based on isActive flag
 * @param {number|boolean} isActive - Active status (1/0 or true/false)
 * @returns {Object} Color classes for badge
 */
export const getStatusColor = (isActive) => {
  return isActive
    ? {
        bg: "bg-green-500/10",
        text: "text-green-400",
        border: "border-green-500/20",
        label: "Active",
      }
    : {
        bg: "bg-red-500/10",
        text: "text-red-400",
        border: "border-red-500/20",
        label: "Inactive",
      };
};

/**
 * Validate audio file type and size
 * @param {File} file - Audio file
 * @param {number} maxSizeMB - Maximum file size in MB (default: 50)
 * @returns {Object} { valid: boolean, error?: string }
 */
export const validateAudioFile = (file, maxSizeMB = 50) => {
  const validTypes = ["audio/mpeg", "audio/mp3", "audio/wav", "audio/flac"];
  const maxSizeBytes = maxSizeMB * 1024 * 1024;

  if (!validTypes.includes(file.type)) {
    return {
      valid: false,
      error: "Invalid file type. Please upload MP3, WAV, or FLAC files.",
    };
  }

  if (file.size > maxSizeBytes) {
    return {
      valid: false,
      error: `File size exceeds ${maxSizeMB}MB limit.`,
    };
  }

  return { valid: true };
};

/**
 * Validate image file type and size
 * @param {File} file - Image file
 * @param {number} maxSizeMB - Maximum file size in MB (default: 5)
 * @returns {Object} { valid: boolean, error?: string }
 */
export const validateImageFile = (file, maxSizeMB = 5) => {
  const validTypes = ["image/jpeg", "image/jpg", "image/png", "image/webp"];
  const maxSizeBytes = maxSizeMB * 1024 * 1024;

  if (!validTypes.includes(file.type)) {
    return {
      valid: false,
      error: "Invalid file type. Please upload PNG, JPG, or WEBP files.",
    };
  }

  if (file.size > maxSizeBytes) {
    return {
      valid: false,
      error: `File size exceeds ${maxSizeMB}MB limit.`,
    };
  }

  return { valid: true };
};
