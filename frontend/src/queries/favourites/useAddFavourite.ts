import { useMutation } from "@tanstack/react-query";
import axiosInstance from "@/component/axiosInstance";
import { AxiosError } from "axios";

interface AddFavouriteRequest {
    courseId: number;
}

interface AddFavouriteResponse {
    message: string;
    data: null;
}

export const useAddFavourite = () => {
    return useMutation<AddFavouriteResponse, AxiosError<AddFavouriteResponse>, AddFavouriteRequest>({
        mutationFn: ({ courseId }) =>
            axiosInstance.post(`/favourites/${courseId}`),
        mutationKey: ["addFavourite"],
    });
};