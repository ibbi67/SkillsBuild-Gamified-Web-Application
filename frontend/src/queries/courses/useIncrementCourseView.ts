import axiosInstance from "@/component/axiosInstance";
import { useMutation } from "@tanstack/react-query"
import { AxiosError } from "axios";

interface IncrementCourseViewResponse {
    message: string;
    data: null;
}

interface IncrementCourseViewRequest{
    courseId: number;
}

export const useIncrementCourseView = () => {
    return useMutation<IncrementCourseViewResponse, AxiosError<IncrementCourseViewResponse>, IncrementCourseViewRequest>({
        mutationFn: ({courseId}) => axiosInstance.post(`/courses/${courseId}/view`),
        mutationKey: ['incrementCourseView']
    })
}