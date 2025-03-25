import { useMutation } from "@tanstack/react-query";
import axiosInstance from "@/component/axiosInstance";
import { AxiosError } from "axios";

interface DeleteGoalResponse {
    message: string;
    data: null; // Based on GoalController which returns void/null
}

export const useDeleteGoal = () => {
    return useMutation<DeleteGoalResponse, AxiosError<DeleteGoalResponse>, number>({
        mutationFn: (goalId) => 
            axiosInstance.delete(`/goals/${goalId}`).then((res) => res.data),
        mutationKey: ["deleteGoal"],
    });
};