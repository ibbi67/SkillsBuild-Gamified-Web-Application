import { useMutation, useQueryClient } from "@tanstack/react-query";
import axiosInstance from "@/component/axiosInstance";
import { AxiosError } from "axios";
import { GoalResponse, AddCoursesToGoalRequest } from "@/types/goalTypes";

export const useAddCoursesToGoal = () => {
  const queryClient = useQueryClient();
  
  return useMutation<GoalResponse, AxiosError<GoalResponse>, AddCoursesToGoalRequest>({
    mutationFn: ({ goalId, courses }) => 
      axiosInstance.post(`/api/goals/${goalId}/courses`, courses).then(res => res.data),
    mutationKey: ["addCoursesToGoal"],
    onSuccess: () => {
      // Invalidate goals and dashboard queries to refresh the data
      queryClient.invalidateQueries({ queryKey: ["goals"] });
      queryClient.invalidateQueries({ queryKey: ["dashboard"] });
    }
  });
};