import { useQuery } from '@tanstack/react-query';
import axiosInstance from '@/component/axiosInstance';
import { Badge } from '@/types/databaseTypes';

export const useGetUserBadges = (userId: number) => {
  return useQuery<Badge[]>({
    queryKey: ['badges', 'user', userId],
    queryFn: () => axiosInstance.get<{ message: string; data: Badge[] }>(`/badges/user/${userId}`).then(res => res.data.data)
  });
}; 