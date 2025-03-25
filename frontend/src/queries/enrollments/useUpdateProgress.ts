import axiosInstance from '@/component/axiosInstance';
import { useMutation } from '@tanstack/react-query';
import axios, { AxiosError } from 'axios';

interface UpdateProgressDTO {
    enrollmentId: number;
    timeSpent: number;
}

interface UpdateProgressResponse {
    message: string;
    data: null;
}

export const useUpdateProgress = () => {
    return useMutation<UpdateProgressResponse, AxiosError<UpdateProgressResponse>, UpdateProgressDTO>({
        mutationFn: ({ enrollmentId, timeSpent }) => axiosInstance.put(`/enrollments/${enrollmentId}/progress`, { timeSpent }),
        mutationKey: ['updateProgress'],
    });
};