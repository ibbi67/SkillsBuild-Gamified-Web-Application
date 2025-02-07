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

    const fetchData = async () => {
        setIsLoading(true);
        setIsError(false);
        setMessage("");
        setStatus(200);
        setData(null);

        let response: AxiosResponse<ServerResponse<ResponseData>> | null = null;

        try {
            response = await axios<ServerResponse<ResponseData>>(`http://localhost:8080/${url}`, {
                ...options,
                withCredentials: true,
                headers: {
                    ...axios.defaults.headers.common,
                    ...(options.headers || {}),
                    withCredentials: true,
                },
            });
        } catch (error) {
            if (error instanceof AxiosError) {
                setIsError(true);

                if (error.response) {
                    console.log("set message to error.response.data.message");
                    setMessage(error.response.data.message);
                    setStatus(error.response.status);
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

        if (response && !isError) {
            console.log("Setting data to response.data.data");
            setData(response?.data?.data);
            setMessage(response?.data?.message);
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
