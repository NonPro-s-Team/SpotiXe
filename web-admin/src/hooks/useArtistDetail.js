import { useQuery } from "@tanstack/react-query";
import { getArtistById } from "@services/api/artistService";

/**
 * Hook to fetch artist detail by ID
 * @param {number} id - Artist ID
 * @returns {Object} React Query result
 */
export const useArtistDetail = (id) => {
  return useQuery({
    queryKey: ["artist", id],
    queryFn: () => getArtistById(id),
    enabled: !!id,
    staleTime: 5 * 60 * 1000,
  });
};
