import { useMutation } from "@tanstack/react-query";
import axiosInstance from "@/component/axiosInstance";
import { AxiosError } from "axios";

interface RemoveFavouriteRequest {
    courseId: number;
}

interface RemoveFavouriteResponse {
    message: string;
    data: null;
}

export const useRemoveFavourite = () => {
    return useMutation<RemoveFavouriteResponse, AxiosError<RemoveFavouriteResponse>, RemoveFavouriteRequest>({
        mutationFn: ({ courseId }) =>
            axiosInstance.delete(`/favourites/${courseId}`),
        mutationKey: ["removeFavourite"],
    });
};