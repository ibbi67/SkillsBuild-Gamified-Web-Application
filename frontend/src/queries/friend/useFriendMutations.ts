import axiosInstance from '@/component/axiosInstance';
import { useMutation } from '@tanstack/react-query';
import axios, { AxiosError } from 'axios';

interface MutateFriendResponse {
    message: string;
    data: null
}

interface MutateFriendRequest {
    personId: number;
}

export const useAddFriend = () => {
    return useMutation<MutateFriendResponse, AxiosError<MutateFriendResponse>, MutateFriendRequest>({
        mutationFn: ({ personId }) => axiosInstance.post('/friends', { personId }).then((res) => res.data),
        mutationKey: ['addFriend'],
    });
};

export const useRemoveFriend = () => {
    return useMutation<MutateFriendResponse, AxiosError<MutateFriendResponse>, MutateFriendRequest>({
        mutationFn: ({ personId }) => axiosInstance.delete(`/friends/${personId}`).then((res) => res.data),
        mutationKey: ['addFriend'],
    });
};