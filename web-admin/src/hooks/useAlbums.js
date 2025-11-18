import { useQuery } from "@tanstack/react-query";
import { getAllAlbums } from "@services/api/albumService";

/**
 * Hook to fetch all albums with React Query
 * @returns {Object} React Query result
 */
export const useAlbums = () => {
  return useQuery({
    queryKey: ["albums"],
    queryFn: getAllAlbums,
    staleTime: 5 * 60 * 1000, // 5 minutes
    cacheTime: 10 * 60 * 1000, // 10 minutes
  });
};
