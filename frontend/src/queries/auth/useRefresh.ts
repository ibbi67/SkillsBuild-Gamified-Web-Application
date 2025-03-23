import { useMutation } from "@tanstack/react-query";
import axiosInstance from "@/component/axiosInstance";
import { AxiosError } from "axios";

interface RefreshResponse {
    message: string;
    data: null;
}

export const useRefresh = () => {
    return useMutation<RefreshResponse, AxiosError<RefreshResponse>>({
        mutationFn: () => axiosInstance.post("/auth/refresh"),
        mutationKey: ["refresh"],
    });
};