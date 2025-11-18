import { useQuery } from "@tanstack/react-query";
import { getAllSongs } from "../services/api/songService";

/**
 * Custom hook for fetching all songs using React Query
 * Implements caching strategy with 5 minutes stale time
 *
 * @returns {Object} Query result with songs data, loading state, error state, and refetch function
 */
export const useSongs = () => {
  return useQuery({
    queryKey: ["songs"],
    queryFn: getAllSongs,
    staleTime: 5 * 60 * 1000, // 5 minutes
    cacheTime: 10 * 60 * 1000, // 10 minutes
    refetchOnMount: false,
    refetchOnWindowFocus: false,
    retry: 2,
    retryDelay: (attemptIndex) => Math.min(1000 * 2 ** attemptIndex, 30000),
  });
};
