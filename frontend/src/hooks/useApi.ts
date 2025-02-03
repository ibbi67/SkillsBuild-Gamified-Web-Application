"use client";

import axios, { AxiosError, AxiosRequestConfig, AxiosResponse } from "axios";
import { useState } from "react";

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
    const [isLoading, setIsLoading] = useState<boolean>(false);
    const [isError, setIsError] = useState<boolean>(false);
    const [message, setMessage] = useState<string>("");
    const [status, setStatus] = useState<number>(200);
    const [data, setData] = useState<ResponseData | null>(null);

    const token = typeof window !== "undefined" ? localStorage.getItem("token") : null;

    const fetchData = async () => {
        setIsLoading(true);
        let response: AxiosResponse<ServerResponse<ResponseData>> | null = null;

        try {
            response = await axios<ServerResponse<ResponseData>>(`http://localhost:8080/${url}`, {
                ...options,
                headers: {
                    ...axios.defaults.headers.common,
                    ...(options.headers || {}),
                    ...(token && { Authorization: `Bearer ${token}` }),
                },
            });
        } catch (error) {
            if (error instanceof AxiosError) {
                setIsError(true);

                if (error.response) {
                    setMessage(error.response.data.message);
                    setStatus(error.response.status);
                } else if (error.request) {
                    setMessage("No response received");
                    setStatus(0);
                } else {
                    setMessage(error.message);
                    setStatus(500);
                }
            } else {
                // Handle other unexpected error types
                setIsError(true);
                setMessage("An unknown error occurred");
                setStatus(500);
            }
        } finally {
            setIsLoading(false);
        }

        console.log(response?.data);

        if (response && !isError) {
            setData(response?.data?.data);
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
