import { useQuery } from "@tanstack/react-query";
import axiosInstance from "@/component/axiosInstance";
import { AxiosError } from "axios";
import { GoalProgressDTO, GoalProgressResponse } from "@/types/goalTypes";
import { useMe } from "@/queries/auth/useMe";

// Create a type that works with both response formats
type GoalDashboardResponse = GoalProgressResponse | GoalProgressDTO[];

export const useGoalDashboard = () => {
  const { data: user } = useMe();
  const personId = user?.data?.id;
  
  return useQuery<GoalDashboardResponse, AxiosError>({
    queryFn: async () => {
      if (!personId) {
        return [] as GoalProgressDTO[];
      }
      
      const response = await axiosInstance.get(`/api/goals/dashboard/${personId}`);
      return response.data;
    },
    queryKey: ["dashboard", personId],
    enabled: !!personId, // Only run the query if we have a personId
  });
};