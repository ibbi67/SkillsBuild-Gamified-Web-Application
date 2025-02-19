"use client";

import { useApi } from "@/hooks/useApi";
import { MeRequest, MeResponse } from "@/types/apiCall";
import { useEffect, useRef } from "react";
import Navbar from "@/components/Navbar";

export default function ProfilePage() {
    const { isLoading, isError, message, fetchData, data } = useApi<
        MeResponse,
        MeRequest
    >("auth/me", { method: "GET" });

    const hasFetched = useRef(false);

    useEffect(() => {
        if (!hasFetched.current) {
            hasFetched.current = true;
            fetchData();
        }
    }, [fetchData]);

    return (
        <div className="flex flex-col">
            <div className="m-4 grow">
                <Navbar />
            </div>
            <div className="m-4 flex max-w-96 grow flex-col gap-4 rounded-lg bg-white p-4 shadow-lg">
                <h1 className="text-center font-bold md:text-2xl">Profile</h1>

                {isLoading ? (
                    <div>Loading...</div>
                ) : isError ? (
                    <div>{message}</div>
                ) : (
                    <div className="flex flex-col gap-2">
                        <div>Id: {data?.id}</div>
                        <div>Username: {data?.username}</div>
                        {data?.roles && data?.roles.length > 0 && (
                            <>
                                <div>Roles:</div>
                                {data?.roles.map((role) => (
                                    <div key={role}>{role}</div>
                                ))}
                            </>
                        )}
                        {data?.authorities && data?.authorities.length > 0 && (
                            <>
                                <div>Authorities:</div>
                                {data?.authorities.map((authority) => (
                                    <div key={authority}>{authority}</div>
                                ))}
                            </>
                        )}
                    </div>
                )}
            </div>
        </div>
    );
}
