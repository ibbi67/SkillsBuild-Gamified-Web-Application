import { useMutation } from "@tanstack/react-query";
import axiosInstance from "@/component/axiosInstance";
import { Goal } from "@/types/databaseTypes";
import { AxiosError } from "axios";

interface UpdateEnrollmentCompletionResponse {
    message: string;
    data: Goal;
}

interface UpdateEnrollmentCompletionParams {
    goalId: number;
    enrollmentId: number;
}

export const useUpdateEnrollmentCompletion = () => {
    return useMutation<
        UpdateEnrollmentCompletionResponse, 
        AxiosError<UpdateEnrollmentCompletionResponse>, 
        UpdateEnrollmentCompletionParams
    >({
        mutationFn: ({ goalId, enrollmentId }) => 
            axiosInstance.put(`/goals/${goalId}/enrollments/${enrollmentId}`).then((res) => res.data),
        mutationKey: ["updateEnrollmentCompletion"],
    });
};