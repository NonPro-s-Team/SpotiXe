import { useQuery } from "@tanstack/react-query";
import { getAlbumById } from "@services/api/albumService";

/**
 * Hook to fetch album detail by ID
 * @param {number} id - Album ID
 * @returns {Object} React Query result
 */
export const useAlbumDetail = (id) => {
  return useQuery({
    queryKey: ["album", id],
    queryFn: () => getAlbumById(id),
    enabled: !!id,
    staleTime: 5 * 60 * 1000,
  });
};
