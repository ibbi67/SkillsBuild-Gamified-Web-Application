import { useQuery } from "@tanstack/react-query";
import axiosInstance from "@/component/axiosInstance";
import { Goal } from "@/types/databaseTypes";
import { AxiosError } from "axios";

interface GoalDashboardResponse {
    message: string;
    data: Goal[];
}

export const useGoalDashboard = () => {
    return useQuery<GoalDashboardResponse, AxiosError<GoalDashboardResponse>>({
        queryFn: () => axiosInstance.get("/goals").then((res) => res.data),
        queryKey: ["goals", "dashboard"],
        select: (data) => {
            // Filter for active goals (current date is between start and end date)
            const now = new Date();
            return {
                ...data,
                data: data.data.filter(goal => {
                    const startDate = new Date(goal.startDate);
                    const endDate = new Date(goal.endDate);
                    return startDate <= now && endDate >= now;
                })
            };
        }
    });
};