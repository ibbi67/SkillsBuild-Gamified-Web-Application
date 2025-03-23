import { useMutation } from "@tanstack/react-query";
import axiosInstance from "@/component/axiosInstance";
import { AxiosError } from "axios";

interface LoginRequest {
    username: string;
    password: string;
}

interface LoginResponse {
    message: string;
    data: null;
}

export const useLogin = () => {
    return useMutation<LoginResponse, AxiosError<LoginResponse>, LoginRequest>({
        mutationFn: ({ username, password }) =>
            axiosInstance.post("/auth/login", { username, password }),
        mutationKey: ["login"],
    });
};
