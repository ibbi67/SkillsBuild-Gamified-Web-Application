import { useQuery } from '@tanstack/react-query';
import axiosInstance from '@/component/axiosInstance';
import { Badge } from '@/types/databaseTypes';

export const useGetBadgeById = (id: number) => {
  return useQuery<Badge>({
    queryKey: ['badges', id],
    queryFn: () => axiosInstance.get<{ message: string; data: Badge }>(`/badges/${id}`).then(res => res.data.data)
  });
}; 