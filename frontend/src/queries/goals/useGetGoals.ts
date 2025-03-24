import { useQuery } from "@tanstack/react-query";
import axiosInstance from "@/component/axiosInstance";
import { AxiosError } from "axios";
import { GoalsResponse } from "@/types/goalTypes";
import { useMe } from "@/queries/auth/useMe";

export const useGoals = () => {
  const { data: user } = useMe();
  const personId = user?.data.id;
  
  return useQuery<GoalsResponse, AxiosError<GoalsResponse>>({
    queryFn: () => 
      axiosInstance.get(`/api/goals/${personId}`).then(res => res.data),
    queryKey: ["goals", personId],
    enabled: !!personId, // Only run the query if we have a personId
  });
};