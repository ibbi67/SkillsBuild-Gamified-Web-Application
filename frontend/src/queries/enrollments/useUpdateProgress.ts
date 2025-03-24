import { useMutation } from '@tanstack/react-query';
import axios, { AxiosError } from 'axios';

interface UpdateProgressDTO {
    timeSpent: number;
}

interface UpdateProgressResponse {
    message: string;
    data: null;
}

export const useUpdateProgress = () => {
    return useMutation<UpdateProgressResponse, AxiosError<UpdateProgressResponse>, UpdateProgressDTO>({
        mutationFn: ({ timeSpent }) => axios.put(`/enrollments/${timeSpent}/progress`, { timeSpent }),
        mutationKey: ['updateProgress'],
    });
};