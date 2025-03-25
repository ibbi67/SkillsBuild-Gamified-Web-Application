import { useMutation } from "@tanstack/react-query";
import axiosInstance from "@/component/axiosInstance";
import { AxiosError } from "axios";

interface CreateGoalRequest {
    startDate: string;
    endDate: string;
    description: string;
    reward: string;
    //achieved to be removed from backend
}

interface CreateGoalResponse {
    message: string;
    data: null; // Based on GoalController which returns void/null
}

export const useCreateGoal = () => {
    return useMutation<CreateGoalResponse, AxiosError<CreateGoalResponse>, CreateGoalRequest>({
        mutationFn: ({ startDate, endDate, description, reward }) => 
            axiosInstance.post("/goals", { startDate, endDate, description, reward }).then((res) => res.data),
        mutationKey: ["createGoal"],
    });
};