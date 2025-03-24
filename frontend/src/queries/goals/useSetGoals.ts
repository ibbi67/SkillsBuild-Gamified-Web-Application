import { useMutation } from "@tanstack/react-query";
import axiosInstance from "@/component/axiosInstance";
import { AxiosError } from "axios";

interface GoalRequest {
    number: number;
}

interface GoalResponse {
    message: string;
    data: null;
}

export const useLogin = () => {
    return useMutation<GoalResponse, AxiosError<GoalResponse>, GoalRequest>({
        mutationFn: ({ number }) =>
            axiosInstance.post("/goals/edit", { number }),
        mutationKey: ["login"],
    });
};
