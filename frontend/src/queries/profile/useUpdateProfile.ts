import { useMutation } from "@tanstack/react-query";
import axiosInstance from "@/component/axiosInstance";
import { AxiosError } from "axios";

interface UpdateProfileResponse {
    message: string;
    data: null;
}

interface UpdateProfileInput {
    username: string;
    password?: string;
    firstName?: string;
    lastName?: string;
    email?: string;
    avatarLink?: string;
}

export const useUpdateProfile = () => {
    return useMutation<UpdateProfileResponse, AxiosError<UpdateProfileResponse>, UpdateProfileInput>({
        mutationFn: (data) => axiosInstance.put("/profile", data).then((res) => res.data),
    });
};
