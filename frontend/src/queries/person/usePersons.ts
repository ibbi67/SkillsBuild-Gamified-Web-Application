import { useQuery } from "@tanstack/react-query";
import axiosInstance from "@/component/axiosInstance";
import { Person } from "@/types/databaseTypes";
import { AxiosError } from "axios";

interface PersonsResponse {
    message: string;
    data: Person[];
}

export const usePersons = () => {
    return useQuery<PersonsResponse, AxiosError<PersonsResponse>>({
        queryFn: () => axiosInstance.get("/persons").then((res) => res.data),
        queryKey: ["persons"],
    });
};