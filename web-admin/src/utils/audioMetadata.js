/**
 * Audio Metadata Extraction Utilities
 * Extract metadata from audio files (MP3, etc.)
 */

/**
 * Extract metadata from audio file URL
 * @param {string} audioUrl - URL of the audio file
 * @param {AbortSignal} signal - Optional abort signal
 * @returns {Promise<Object>} Extracted metadata
 */
export const extractAudioMetadata = async (audioUrl, signal = null) => {
  try {
    // Check if aborted before starting
    if (signal?.aborted) {
      throw new Error('ABORTED');
    }

    // Create audio element
    const audio = new Audio();
    audio.crossOrigin = "anonymous";

    // Load audio file
    const metadata = await new Promise((resolve, reject) => {
      const timeout = setTimeout(() => {
        reject(new Error("Timeout loading audio file"));
      }, 30000); // 30 seconds timeout

      // Listen for abort signal
      const abortHandler = () => {
        clearTimeout(timeout);
        audio.pause();
        audio.src = '';
        reject(new Error('ABORTED'));
      };

      if (signal) {
        signal.addEventListener('abort', abortHandler);
      }

      audio.addEventListener("loadedmetadata", () => {
        clearTimeout(timeout);
        if (signal) {
          signal.removeEventListener('abort', abortHandler);
        }

        // Basic metadata from audio element
        const basicMetadata = {
          duration: Math.floor(audio.duration) || 0,
          title: null,
          artist: null,
          album: null,
          releaseDate: null,
          genre: null,
        };

        resolve(basicMetadata);
      });

      audio.addEventListener("error", () => {
        clearTimeout(timeout);
        if (signal) {
          signal.removeEventListener('abort', abortHandler);
        }
        reject(new Error("Failed to load audio file"));
      });

      audio.src = audioUrl;
      audio.load();
    });

    // Check abort status again before ID3 extraction
    if (signal?.aborted) {
      throw new Error('ABORTED');
    }

    // Try to extract ID3 tags if available
    try {
      const id3Data = await extractID3Tags(audioUrl, signal);
      return {
        ...metadata,
        ...id3Data,
      };
    } catch (error) {
      // If ID3 extraction fails, return basic metadata
      if (error.message === 'ABORTED') {
        throw error;
      }
      console.warn("ID3 extraction failed, using basic metadata:", error);
      return metadata;
    }
  } catch (error) {
    console.error("Error extracting audio metadata:", error);
    throw error;
  }
};

/**
 * Extract ID3 tags from MP3 file
 * @param {string} audioUrl - URL of the audio file
 * @param {AbortSignal} signal - Optional abort signal
 * @returns {Promise<Object>} ID3 tag data
 */
const extractID3Tags = async (audioUrl, signal = null) => {
  try {
    // Fetch the audio file with abort signal
    const response = await fetch(audioUrl, signal ? { signal } : {});
    if (!response.ok) {
      throw new Error("Failed to fetch audio file");
    }

    const arrayBuffer = await response.arrayBuffer();
    const buffer = new Uint8Array(arrayBuffer);

    // Check for ID3v2 tag
    if (buffer[0] === 0x49 && buffer[1] === 0x44 && buffer[2] === 0x33) {
      const version = buffer[3];
      const flags = buffer[5];

      // Calculate tag size (synchsafe integer)
      const tagSize =
        ((buffer[6] & 0x7f) << 21) |
        ((buffer[7] & 0x7f) << 14) |
        ((buffer[8] & 0x7f) << 7) |
        (buffer[9] & 0x7f);

      const tagData = buffer.slice(10, 10 + tagSize);

      return parseID3v2Frames(tagData, version);
    }

    return {};
  } catch (error) {
    console.error("Error extracting ID3 tags:", error);
    return {};
  }
};

/**
 * Parse ID3v2 frames
 * @param {Uint8Array} data - ID3 tag data
 * @param {number} version - ID3 version
 * @returns {Object} Parsed metadata
 */
const parseID3v2Frames = (data, version) => {
  const metadata = {
    title: null,
    artist: null,
    album: null,
    releaseDate: null,
    genre: null,
  };

  let offset = 0;

  while (offset < data.length - 10) {
    // Frame header size depends on version
    const frameHeaderSize = version >= 3 ? 10 : 6;

    if (offset + frameHeaderSize > data.length) break;

    // Get frame ID
    let frameId;
    if (version >= 3) {
      frameId = String.fromCharCode(
        data[offset],
        data[offset + 1],
        data[offset + 2],
        data[offset + 3]
      );
    } else {
      frameId = String.fromCharCode(
        data[offset],
        data[offset + 1],
        data[offset + 2]
      );
    }

    // Stop if we hit padding
    if (frameId[0] === "\0") break;

    // Get frame size
    let frameSize;
    if (version >= 4) {
      // Synchsafe integer in v2.4
      frameSize =
        ((data[offset + 4] & 0x7f) << 21) |
        ((data[offset + 5] & 0x7f) << 14) |
        ((data[offset + 6] & 0x7f) << 7) |
        (data[offset + 7] & 0x7f);
    } else if (version === 3) {
      // Regular integer in v2.3
      frameSize =
        (data[offset + 4] << 24) |
        (data[offset + 5] << 16) |
        (data[offset + 6] << 8) |
        data[offset + 7];
    } else {
      // v2.2
      frameSize =
        (data[offset + 3] << 16) | (data[offset + 4] << 8) | data[offset + 5];
    }

    if (frameSize === 0 || offset + frameHeaderSize + frameSize > data.length) {
      break;
    }

    // Get frame data
    const frameData = data.slice(
      offset + frameHeaderSize,
      offset + frameHeaderSize + frameSize
    );

    // Parse frame based on ID
    const textValue = decodeTextFrame(frameData);

    // Map frame IDs to metadata fields
    const frameMap = {
      // v2.3/v2.4
      TIT2: "title",
      TPE1: "artist",
      TALB: "album",
      TDRC: "releaseDate",
      TYER: "releaseDate",
      TCON: "genre",
      // v2.2
      TT2: "title",
      TP1: "artist",
      TAL: "album",
      TYE: "releaseDate",
      TCO: "genre",
    };

    if (frameMap[frameId]) {
      // Special handling for artist field - extract all artists
      if (frameMap[frameId] === "artist") {
        const allArtists = extractAllArtists(textValue);
        console.log(
          `[Metadata] Original artist: "${textValue}" -> All artists:`,
          allArtists
        );
        metadata[frameMap[frameId]] = allArtists; // Return as array
      } else {
        metadata[frameMap[frameId]] = textValue;
      }
    }

    offset += frameHeaderSize + frameSize;
  }

  // Log extracted metadata for debugging
  if (metadata.artist) {
    console.log("[Metadata] Artists from ID3:", metadata.artist);
  }

  return metadata;
};

/**
 * Decode text frame
 * @param {Uint8Array} data - Frame data
 * @returns {string} Decoded text
 */
const decodeTextFrame = (data) => {
  if (data.length === 0) return null;

  const encoding = data[0];
  const textData = data.slice(1);

  try {
    let decodedText;
    switch (encoding) {
      case 0: // ISO-8859-1
        decodedText = String.fromCharCode(...textData).replace(/\0/g, "");
        break;
      case 1: // UTF-16 with BOM
      case 2: // UTF-16BE without BOM
        decodedText = new TextDecoder("utf-16")
          .decode(textData)
          .replace(/\0/g, "");
        break;
      case 3: // UTF-8
        decodedText = new TextDecoder("utf-8")
          .decode(textData)
          .replace(/\0/g, "");
        break;
      default:
        decodedText = String.fromCharCode(...textData).replace(/\0/g, "");
    }

    return decodedText ? decodedText.trim() : null;
  } catch (error) {
    console.warn("Error decoding text frame:", error);
    return null;
  }
};

/**
 * Extract all artist names from artist field
 * Splits by common separators and returns array of all artists
 *
 * Examples:
 * - "B Ray / ASTRA" -> ["B Ray", "ASTRA"]
 * - "Erik feat. Min" -> ["Erik", "Min"]
 * - "Sơn Tùng M-TP ft. Snoop Dogg" -> ["Sơn Tùng M-TP", "Snoop Dogg"]
 * - "Mono (feat. OnlyC)" -> ["Mono", "OnlyC"]
 *
 * @param {string} artistString - Full artist string from metadata
 * @returns {Array<string>} Array of all artist names
 */
const extractAllArtists = (artistString) => {
  if (!artistString) return [];

  let workingString = artistString.trim();

  // Remove parentheses but keep the content for parsing
  // "Mono (feat. OnlyC)" -> "Mono feat. OnlyC"
  workingString = workingString.replace(
    /\s*\((feat\.|ft\.|featuring)\s*/gi,
    " $1 "
  );
  workingString = workingString.replace(/\)/g, "");

  // Common separators for featured artists (in priority order)
  const separators = [
    { pattern: /\s+feat\.?\s+/gi, replacement: "||SPLIT||" },
    { pattern: /\s+ft\.?\s+/gi, replacement: "||SPLIT||" },
    { pattern: /\s+featuring\s+/gi, replacement: "||SPLIT||" },
    { pattern: /\s*\/\s*/g, replacement: "||SPLIT||" },
    { pattern: /\s*&\s*/g, replacement: "||SPLIT||" },
    { pattern: /\s*,\s*/g, replacement: "||SPLIT||" },
    { pattern: /\s+and\s+/gi, replacement: "||SPLIT||" },
    { pattern: /\s+with\s+/gi, replacement: "||SPLIT||" },
  ];

  // Apply all separators
  for (const { pattern, replacement } of separators) {
    workingString = workingString.replace(pattern, replacement);
  }

  // Split and clean
  const artists = workingString
    .split("||SPLIT||")
    .map((name) => name.trim())
    .filter(
      (name) =>
        name.length > 0 &&
        !["feat", "ft", "featuring", "with", "and"].includes(name.toLowerCase())
    );

  // Remove duplicates (case-insensitive)
  const uniqueArtists = [];
  const seen = new Set();

  for (const artist of artists) {
    const lowerArtist = artist.toLowerCase();
    if (!seen.has(lowerArtist)) {
      seen.add(lowerArtist);
      uniqueArtists.push(artist);
    }
  }

  return uniqueArtists;
};

/**
 * Format release date from ID3 tag
 * @param {string} dateString - Date string from ID3 tag
 * @returns {string|null} Formatted date (YYYY-MM-DD) or null
 */
export const formatReleaseDate = (dateString) => {
  if (!dateString) return null;

  // Extract year
  const yearMatch = dateString.match(/\d{4}/);
  if (!yearMatch) return null;

  const year = yearMatch[0];

  // Try to extract full date (YYYY-MM-DD)
  const fullDateMatch = dateString.match(/(\d{4})-?(\d{2})-?(\d{2})/);
  if (fullDateMatch) {
    return `${fullDateMatch[1]}-${fullDateMatch[2]}-${fullDateMatch[3]}`;
  }

  // Return year with default month and day
  return `${year}-01-01`;
};
