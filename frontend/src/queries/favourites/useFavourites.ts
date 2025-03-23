import { useQuery } from "@tanstack/react-query";
import axiosInstance from "@/component/axiosInstance";
import { Course } from "@/types/databaseTypes";
import { AxiosError } from "axios";

interface FavouritesResponse {
    message: string;
    data: Course[];
}

export const useFavourites = () => {
    return useQuery<FavouritesResponse, AxiosError<FavouritesResponse>>({
        queryFn: () => axiosInstance.get("/favourites").then((res) => res.data),
        queryKey: ["favourites"],
    });
};