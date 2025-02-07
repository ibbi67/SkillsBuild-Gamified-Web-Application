"use client";

import axios, { AxiosError, AxiosRequestConfig, AxiosResponse } from "axios";
import { useState } from "react";
import { useRouter } from "next/navigation";

interface useApiType<T> {
    isLoading: boolean;
    isError: boolean;
    message: string;
    status: number;
    data: T | null;
    fetchData: () => Promise<void>;
}

interface ServerResponse<T> {
    status: number;
    message: string;
    data: T;
}

export const useApi = <ResponseData, RequestData>(
    url: string,
    options: AxiosRequestConfig<RequestData> = {},
): useApiType<ResponseData> => {
    const router = useRouter();
    const [isLoading, setIsLoading] = useState<boolean>(false);
    const [isError, setIsError] = useState<boolean>(false);
    const [message, setMessage] = useState<string>("");
    const [status, setStatus] = useState<number>(200);
    const [data, setData] = useState<ResponseData | null>(null);

    const api = axios.create({
        baseURL: "http://localhost:8080/",
        withCredentials: true,
    });

    api.interceptors.response.use(
        (response) => response,
        async (error) => {
            const originalRequest = error.config;

            if (error.response?.status === 401 && !originalRequest._retry) {
                originalRequest._retry = true;

                try {
                    await api.post("auth/refresh", null, { withCredentials: true });
                    return api(originalRequest);
                } catch (refreshError) {
                    localStorage.removeItem("login");
                    router.push("/auth/login");
                    return Promise.reject(refreshError);
                }
            }
            return Promise.reject(error);
        },
    );

    const fetchData = async () => {
        setIsLoading(true);
        setIsError(false);
        setMessage("");
        setStatus(200);
        setData(null);

        let response: AxiosResponse<ServerResponse<ResponseData>> | null = null;

        try {
            response = await api<ServerResponse<ResponseData>>(url, {
                ...options,
                headers: {
                    ...axios.defaults.headers.common,
                    ...(options.headers || {}),
                },
            });

            setData(response.data.data);
            setMessage(response.data.message);
            setStatus(response.status);
        } catch (error) {
            if (error instanceof AxiosError) {
                setIsError(true);

                if (error.response) {
                    console.log("set message to error.response.data.message");
                    setMessage(error.response.data.message);
                    setStatus(error.response.status);

                    if ((error.response.status === 401, !url.includes("login"))) {
                        localStorage.removeItem("login");
                        router.push("/auth/login");
                    }
                } else if (error.request) {
                    setMessage("No response received (hint: did you start the spring server?)");
                    setStatus(0);
                } else {
                    console.log("set message to error.message");
                    setMessage(error.message);
                    setStatus(500);
                }
            } else {
                setIsError(true);
                setMessage("An unknown error occurred");
                setStatus(500);
            }
        } finally {
            setIsLoading(false);
        }
    };

    return {
        isLoading,
        isError,
        message,
        status,
        data,
        fetchData,
    };
};
