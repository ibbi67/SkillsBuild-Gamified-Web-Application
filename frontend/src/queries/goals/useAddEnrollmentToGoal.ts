import { useMutation } from "@tanstack/react-query";
import axiosInstance from "@/component/axiosInstance";
import { Goal } from "@/types/databaseTypes";
import { AxiosError } from "axios";

interface AddEnrollmentRequest {
    enrollmentIds: number[];
    // Add any other fields from AddEnrollmentDTO
}

interface AddEnrollmentResponse {
    message: string;
    data: Goal;
}

export const useAddEnrollmentToGoal = (goalId: number) => {
    return useMutation<AddEnrollmentResponse, AxiosError<AddEnrollmentResponse>, AddEnrollmentRequest>({
        mutationFn: (enrollmentData) => 
            axiosInstance.post(`/goals/${goalId}`, enrollmentData).then((res) => res.data),
        mutationKey: ["addEnrollmentToGoal", goalId],
    });
};