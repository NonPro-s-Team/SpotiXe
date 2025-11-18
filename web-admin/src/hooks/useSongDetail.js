import { useQuery } from "@tanstack/react-query";
import { getSongById } from "../services/api/songService";

/**
 * Custom hook for fetching single song detail using React Query
 *
 * @param {number} id - Song ID
 * @param {Object} options - Additional React Query options
 * @returns {Object} Query result with song data, loading state, error state
 */
export const useSongDetail = (id, options = {}) => {
  return useQuery({
    queryKey: ["song", id],
    queryFn: () => getSongById(id),
    enabled: !!id, // Only run query if ID exists
    staleTime: 5 * 60 * 1000, // 5 minutes
    cacheTime: 10 * 60 * 1000, // 10 minutes
    retry: 2,
    ...options,
  });
};
