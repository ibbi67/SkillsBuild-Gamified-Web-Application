import { useQuery } from '@tanstack/react-query';
import axiosInstance from '@/component/axiosInstance';
import { Badge } from '@/types/databaseTypes';

export const useGetAllBadges = () => {
  return useQuery<Badge[]>({
    queryKey: ['badges'],
    queryFn: () => axiosInstance.get<{ message: string; data: Badge[] }>('/badges').then(res => res.data.data)
  });
}; 