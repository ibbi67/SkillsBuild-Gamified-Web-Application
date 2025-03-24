import { useMutation, useQueryClient } from "@tanstack/react-query";
import axiosInstance from "@/component/axiosInstance";
import { AxiosError } from "axios";
import { CreateGoalRequest, GoalResponse } from "@/types/goalTypes";

export const useCreateGoal = () => {
  const queryClient = useQueryClient();
  
  return useMutation<GoalResponse, AxiosError<GoalResponse>, CreateGoalRequest>({
    mutationFn: (goalData) => 
      axiosInstance.post("/api/goals", goalData).then(res => res.data),
    mutationKey: ["createGoal"],
    onSuccess: () => {
      // Invalidate goals and dashboard queries to refresh the data
      queryClient.invalidateQueries({ queryKey: ["goals"] });
      queryClient.invalidateQueries({ queryKey: ["dashboard"] });
    }
  });
};