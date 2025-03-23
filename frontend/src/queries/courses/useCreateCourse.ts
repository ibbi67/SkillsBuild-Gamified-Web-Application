import { useMutation } from "@tanstack/react-query";
import axiosInstance from "@/component/axiosInstance";
import { Course } from "@/types/databaseTypes";
import { AxiosError } from "axios";

interface CreateCourseRequest {
    title: string;
    description: string;
    link: string;
    estimatedDuration: number;
    difficulty: number;
}

interface CreateCourseResponse {
    message: string;
    data: Course;
}

export const useCreateCourse = () => {
    return useMutation<CreateCourseResponse, AxiosError<CreateCourseResponse>, CreateCourseRequest>({
        mutationFn: ({ title, description, link, estimatedDuration, difficulty }) =>
            axiosInstance.post("/courses", { title, description, link, estimatedDuration, difficulty }),
        mutationKey: ["createCourse"],
    });
};