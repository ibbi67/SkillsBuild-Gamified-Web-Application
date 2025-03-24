import { useMutation, useQueryClient } from "@tanstack/react-query";
import axiosInstance from "@/component/axiosInstance";
import { AxiosError } from "axios";
import { CreateGoalRequest, GoalResponse } from "@/types/goalTypes";
import { useMe } from "@/queries/auth/useMe";

export const useCreateGoal = () => {
  const queryClient = useQueryClient();
  const { data: userData } = useMe();
  const personId = userData?.data?.id;
  
  return useMutation<GoalResponse, AxiosError<GoalResponse>, CreateGoalRequest>({
    mutationFn: async (goalData) => {
      try {
        // Using personId as a query param as expected by the backend
        const response = await axiosInstance.post(`/api/goals?personId=${personId}`, {
          description: goalData.description,
          startDate: goalData.startDate,
          endDate: goalData.endDate,
          reward: goalData.reward,
          achieved: false,
          courses: {}  // Initialize empty courses that will be filled later
        });
        
        // Extract the goal ID from the response
        // Check various possible response structures
        let goalId;
        if (response.data.id) {
          goalId = response.data.id;
        } else if (response.data.data && response.data.data.id) {
          goalId = response.data.data.id;
        } else {
          console.error("Unexpected response structure:", response.data);
          throw new Error("Couldn't find goal ID in response");
        }
        
        // If we've created a goal with courses, add them in a separate call
        if (goalData.courseIds && goalData.courseIds.length > 0) {
          // Create a map of courseId: false (not completed) for all selected courses
          const coursesMap: Record<number, boolean> = {};
          
          goalData.courseIds.forEach(courseId => {
            coursesMap[courseId] = false;
          });
          
          // Add courses to the goal
          await axiosInstance.post(`/api/goals/${goalId}/courses`, coursesMap);
        }
        
        // Return the original response data for consistency
        return {
          message: "Goal created successfully",
          data: response.data
        };
      } catch (error) {
        console.error("Error in useCreateGoal:", error);
        throw error;
      }
    },
    mutationKey: ["createGoal"],
    onSuccess: () => {
      // Invalidate goals and dashboard queries to refresh the data
      queryClient.invalidateQueries({ queryKey: ["goals"] });
      queryClient.invalidateQueries({ queryKey: ["dashboard"] });
    }
  });
};