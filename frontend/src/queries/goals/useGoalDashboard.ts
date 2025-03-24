import { useQuery } from "@tanstack/react-query";
import axiosInstance from "@/component/axiosInstance";
import { AxiosError } from "axios";
import { GoalProgressResponse } from "@/types/goalTypes";
import { useMe } from "@/queries/auth/useMe";

export const useGoalDashboard = () => {
  const { data: user } = useMe();
  const personId = user?.data.id;
  
  return useQuery<GoalProgressResponse, AxiosError<GoalProgressResponse>>({
    queryFn: () => 
      axiosInstance.get(`/api/goals/dashboard/${personId}`).then(res => res.data),
    queryKey: ["dashboard", personId],
    enabled: !!personId, // Only run the query if we have a personId
  });
};