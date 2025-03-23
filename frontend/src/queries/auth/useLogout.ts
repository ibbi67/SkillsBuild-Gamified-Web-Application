import { useMutation } from "@tanstack/react-query";
import axiosInstance from "@/component/axiosInstance";
import { AxiosError } from "axios";

interface LogoutResponse {
    message: string;
    data: null;
}

export const useLogout = () => {
    return useMutation<LogoutResponse, AxiosError<LogoutResponse>>({
        mutationFn: () => axiosInstance.post("/auth/logout"),
        mutationKey: ["logout"],
    });
};