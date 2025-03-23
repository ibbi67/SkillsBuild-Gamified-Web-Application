import { useMutation } from "@tanstack/react-query";
import axiosInstance from "@/component/axiosInstance";
import { AxiosError } from "axios";

interface SignupRequest {
    username: string;
    password: string;
}

interface SignupResponse {
    message: string;
    data: null;
}

export const useSignup = () => {
    return useMutation<SignupResponse, AxiosError<SignupResponse>, SignupRequest>({
        mutationFn: ({ username, password }) =>
            axiosInstance.post("/auth/signup", { username, password }),
        mutationKey: ["signup"],
    });
};
