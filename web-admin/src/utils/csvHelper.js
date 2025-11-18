/**
 * CSV Helper Utilities
 * Functions to handle CSV file operations for bulk song import
 */

/**
 * Download CSV template with predefined headers
 */
export const downloadCSVTemplate = () => {
  const headers = ["AudioFileUrl", "CoverImageUrl"];

  // Sample data rows for demonstration
  const sampleData = [
    ["https://example.com/song1.mp3", "https://example.com/cover1.jpg"],
    ["https://example.com/song2.mp3", "https://example.com/cover2.jpg"],
    ["https://example.com/song3.mp3", "https://example.com/cover3.jpg"],
  ];

  // Create CSV content
  const csvContent = [
    headers.join(","),
    ...sampleData.map((row) => row.map((cell) => `"${cell}"`).join(",")),
  ].join("\n");

  // Create blob and download
  const blob = new Blob(["\uFEFF" + csvContent], {
    type: "text/csv;charset=utf-8;",
  });
  const link = document.createElement("a");
  const url = URL.createObjectURL(blob);

  link.setAttribute("href", url);
  link.setAttribute("download", "song_import_template.csv");
  link.style.visibility = "hidden";

  document.body.appendChild(link);
  link.click();
  document.body.removeChild(link);
};

/**
 * Parse CSV file and return array of song objects
 * @param {File} file - CSV file to parse
 * @returns {Promise<Array>} Array of parsed song objects
 */
export const parseCSVFile = (file) => {
  return new Promise((resolve, reject) => {
    const reader = new FileReader();

    reader.onload = (e) => {
      try {
        const text = e.target.result;
        const lines = text.split("\n").filter((line) => line.trim() !== "");

        if (lines.length < 2) {
          reject(new Error("File CSV không có dữ liệu"));
          return;
        }

        // Parse header
        const headers = parseCSVLine(lines[0]);

        // Validate headers
        const requiredHeaders = ["AudioFileUrl", "CoverImageUrl"];

        const missingHeaders = requiredHeaders.filter(
          (header) => !headers.includes(header)
        );

        if (missingHeaders.length > 0) {
          reject(new Error(`Thiếu các cột: ${missingHeaders.join(", ")}`));
          return;
        }

        // Parse data rows
        const songs = [];
        for (let i = 1; i < lines.length; i++) {
          const values = parseCSVLine(lines[i]);

          if (values.length === headers.length) {
            const song = {};
            headers.forEach((header, index) => {
              song[header] = values[index];
            });

            // Validate required fields
            if (song.AudioFileUrl) {
              songs.push(song);
            }
          }
        }

        resolve(songs);
      } catch (error) {
        reject(error);
      }
    };

    reader.onerror = () => {
      reject(new Error("Không thể đọc file CSV"));
    };

    reader.readAsText(file, "UTF-8");
  });
};

/**
 * Parse a single CSV line, handling quoted values
 * @param {string} line - CSV line to parse
 * @returns {Array<string>} Array of values
 */
const parseCSVLine = (line) => {
  const values = [];
  let current = "";
  let inQuotes = false;

  for (let i = 0; i < line.length; i++) {
    const char = line[i];

    if (char === '"') {
      // Handle escaped quotes ("")
      if (inQuotes && line[i + 1] === '"') {
        current += '"';
        i++;
      } else {
        inQuotes = !inQuotes;
      }
    } else if (char === "," && !inQuotes) {
      values.push(current.trim());
      current = "";
    } else {
      current += char;
    }
  }

  // Add last value
  values.push(current.trim());

  return values;
};

/**
 * Validate song data
 * @param {Object} song - Song object to validate
 * @returns {Object} Validation result { valid: boolean, errors: Array }
 */
export const validateSongData = (song) => {
  const errors = [];

  // Validate URLs
  if (!song.AudioFileUrl || song.AudioFileUrl.trim() === "") {
    errors.push("URL file âm thanh không được để trống");
  } else {
    try {
      new URL(song.AudioFileUrl);
    } catch {
      errors.push("URL file âm thanh không hợp lệ");
    }
  }

  if (song.CoverImageUrl && song.CoverImageUrl.trim() !== "") {
    try {
      new URL(song.CoverImageUrl);
    } catch {
      errors.push("URL ảnh bìa không hợp lệ");
    }
  }

  return {
    valid: errors.length === 0,
    errors,
  };
};

/**
 * Transform CSV data to API format
 * @param {Object} csvRow - Parsed CSV row
 * @returns {Object} Transformed song object
 */
export const transformCSVToSong = (csvRow) => {
  return {
    audioFileUrl: csvRow.AudioFileUrl?.trim() || null,
    coverImageUrl: csvRow.CoverImageUrl?.trim() || null,
  };
};
