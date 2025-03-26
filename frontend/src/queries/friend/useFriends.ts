import axiosInstance from '@/component/axiosInstance';
import { useQuery } from '@tanstack/react-query';
import { AxiosError } from 'axios';

interface FriendsResponse {
    message: string;
    data: {
        username: string;
        firstName?: string;
        lastName?: string;
        email: string;
        avatarLink?: string;
    }[];
}

export const useFriends = () => {
    return useQuery<FriendsResponse, AxiosError<FriendsResponse>>({
        queryKey: ['friends'],
        queryFn: () => axiosInstance.get('/friends').then((res) => res.data),
    });
};