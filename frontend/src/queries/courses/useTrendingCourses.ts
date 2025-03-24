import { useQuery } from "@tanstack/react-query";
import axiosInstance from "@/component/axiosInstance";
import { Course } from "@/types/databaseTypes";
import { AxiosError } from "axios";

interface TrendingCoursesResponse {
    message: string;
    data: Course[];
}

export const useTrendingCourses = () => {
    return useQuery<TrendingCoursesResponse, AxiosError<TrendingCoursesResponse>>({
        queryFn: () => axiosInstance.get("/courses/trending").then((res) => res.data),
        queryKey: ["trendingCourses"],
    });
};