import { useMutation } from '@tanstack/react-query';
import axiosInstance from '@/component/axiosInstance';

export const useAwardBadge = (userId: number, badgeId: number) => {
  return useMutation({
    mutationFn: () => axiosInstance.post<{ message: string; data: string }>(`/badges/award/${userId}/${badgeId}`).then(res => res.data.data)
  });
}; 