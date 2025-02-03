"use client";

import { useApi } from "@/hooks/useApi";
import { useState } from "react";
import { LoginRequest } from "@/types/auth";

export default function LoginPage() {
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");

    const { isLoading, isError, message, fetchData, data } = useApi<string, LoginRequest>(
        "auth/login",
        {
            method: "POST",
            data: {
                username,
                password,
            } as LoginRequest,
        },
    );

    if (!isError && data) {
        localStorage.setItem("token", data);
    }

    return (
        <div className="flex justify-center items-center h-screen">
            <div className="flex flex-col p-4 bg-white rounded-lg shadow-lg gap-4 grow max-w-96">
                <h1 className="font-bold text-center md:text-2xl">Login</h1>

                {isError && <div className="text-red-500 text-sm mb-2">{message}</div>}

                <input
                    type="text"
                    placeholder="Username"
                    className="py-1 px-2 md:py-2 border border-gray-300 rounded-lg"
                    onChange={(e) => setUsername(e.target.value)}
                />

                <input
                    type="password"
                    placeholder="Password"
                    className="py-1 px-2 md:py-2 border border-gray-300 rounded-lg"
                    onChange={(e) => setPassword(e.target.value)}
                />

                <button
                    className="bg-blue-500 text-white p-2 rounded-lg disabled:bg-blue-300"
                    onClick={fetchData}
                    disabled={isLoading}
                >
                    {isLoading ? "Logging in..." : "Confirm"}
                </button>
            </div>
        </div>
    );
}
