import { useQuery } from "@tanstack/react-query";
import { getAllArtists } from "@services/api/artistService";

/**
 * Hook to fetch all artists with React Query
 * @returns {Object} React Query result
 */
export const useArtists = () => {
  return useQuery({
    queryKey: ["artists"],
    queryFn: getAllArtists,
    staleTime: 5 * 60 * 1000, // 5 minutes
    cacheTime: 10 * 60 * 1000, // 10 minutes
  });
};
