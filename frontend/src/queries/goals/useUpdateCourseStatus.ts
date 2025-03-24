import { useMutation, useQueryClient } from "@tanstack/react-query";
import axiosInstance from "@/component/axiosInstance";
import { AxiosError } from "axios";
import { GoalResponse, UpdateCourseStatusRequest } from "@/types/goalTypes";

export const useUpdateCourseStatus = () => {
  const queryClient = useQueryClient();
  
  return useMutation<GoalResponse, AxiosError<GoalResponse>, UpdateCourseStatusRequest>({
    mutationFn: ({ goalId, courseId, completed }) => 
      axiosInstance.put(`/api/goals/${goalId}/courses/${courseId}?completed=${completed}`).then(res => res.data),
    mutationKey: ["updateCourseStatus"],
    onSuccess: () => {
      // Invalidate goals and dashboard queries to refresh the data
      queryClient.invalidateQueries({ queryKey: ["goals"] });
      queryClient.invalidateQueries({ queryKey: ["dashboard"] });
    }
  });
};