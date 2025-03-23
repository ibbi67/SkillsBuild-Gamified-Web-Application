import { useMutation } from "@tanstack/react-query";
import axiosInstance from "@/component/axiosInstance";
import { Enrollment } from "@/types/databaseTypes";
import { AxiosError } from "axios";

interface CreateEnrollmentRequest {
    courseId: number;
}

interface CreateEnrollmentResponse {
    message: string;
    data: Enrollment;
}

export const useCreateEnrollment = () => {
    return useMutation<CreateEnrollmentResponse, AxiosError<CreateEnrollmentResponse>, CreateEnrollmentRequest>({
        mutationFn: ({ courseId }) =>
            axiosInstance.post(`/enrollments/${courseId}`),
        mutationKey: ["createEnrollment"],
    });
};